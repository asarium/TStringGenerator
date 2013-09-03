package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.IFileProvider;
import mm.tstring.ITStringManager;
import mm.tstring.ITstringParser;
import mm.tstring.config.TStringConfig;
import mm.tstring.objects.FileTString;
import mm.tstring.objects.TString;
import mm.tstring.util.Util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultTStringManager implements ITStringManager
{
    private static class TStringIndexComparator implements Comparator<TString>
    {

        @Override
        public int compare(TString o1, TString o2)
        {
            if (o1 == o2)
            {
                return 0;
            }
            else if (o1 == null)
            {
                return -1;
            }
            else if (o2 == null)
            {
                return 1;
            }

            return Integer.compare(o1.getIndex(), o2.getIndex());
        }
    }

    private static class TStringValueComparator implements Comparator<TString>
    {

        @Override
        public int compare(TString o1, TString o2)
        {
            if (o1 == o2)
            {
                return 0;
            }
            else if (o1 == null)
            {
                return -1;
            }
            else if (o2 == null)
            {
                return 1;
            }

            return Util.compare(o1.getValue(), o2.getValue());
        }
    }

    private static final Logger logger = Logger.getLogger(DefaultTStringManager.class.getName());

    private static final Pattern tstringTableEntryPattern = Pattern.compile("(\\d*)\\s*,\\s*\"([^\"]*)\"");

    private NavigableSet<TString> indexSortedTStrings;

    private boolean updateExisitingTable;

    private NavigableSet<TString> valueSortedTStrings;

    public DefaultTStringManager(TStringConfig.Mode mode)
    {
        updateExisitingTable = mode == TStringConfig.Mode.UPDATE;
    }

    @Override
    public void collectTStrings(ITstringParser parser, IFileProvider fileProvider)
    {
        if (parser == null)
        {
            throw new IllegalArgumentException("parser");
        }
        if (fileProvider == null)
        {
            throw new IllegalArgumentException("fileProvider");
        }

        buildTStringsSets(parseTStrings(parser, fileProvider), fileProvider);

        fillGaps();
    }

    /**
     * Searches through indexSortedTStrings and fills index gaps
     */
    private void fillGaps()
    {
        // A new set needs to be filled in as entries may change their position
        NavigableSet<TString> newSet = new TreeSet<TString>(new TStringIndexComparator());

        int lastIndex = -1;
        for (TString string : indexSortedTStrings)
        {
            if (string.isImmutable())
            {
                // We can't change the string so simply add it but don't change lastIndex
                newSet.add(string);
            }
            else
            {
                int diff = string.getIndex() - lastIndex;

                if (diff > 1)
                {
                    // We have a gap
                    int index = lastIndex;

                    // Increment index until we find the right spot
                    do
                    {
                        index++;
                        string.setIndex(index);
                    } while (!newSet.add(string));
                }
                else
                {
                    newSet.add(string);
                }

                lastIndex = string.getIndex();
            }
        }

        indexSortedTStrings = newSet;
    }

    private Collection<FileTString> parseTStrings(ITstringParser parser, IFileProvider fileProvider)
    {
        Collection<FileTString> fileStrings = new ArrayList<FileTString>();

        for (IFile file : fileProvider.getFiles())
        {
            logger.info("Parsing file: " + file.getName());
            Collection<FileTString> fileTStrings = parser.parseStrings(file);

            fileStrings.addAll(fileTStrings);
        }

        return fileStrings;
    }

    @Override
    public Collection<TString> getTStrings()
    {
        return indexSortedTStrings;
    }

    @Override
    public void writeStrings(ITstringParser parser, IFileProvider fileProvider)
    {
        if (!fileProvider.backupFiles())
        {
            logger.warning("Failed to create backups, no files will be changed.");
            return;
        }

        Map<IFile, Collection<FileTString>> tstringMapping = new HashMap<IFile, Collection<FileTString>>();

        for (IFile file : fileProvider.getFiles())
        {
            tstringMapping.put(file, parser.parseStrings(file));
        }

        replaceContents(tstringMapping);

        IFile tstringsTable = fileProvider.getTStringTable(true);

        if (tstringsTable != null)
        {
            writeTable(tstringsTable);
        }
        else
        {
            logger.info("Failed to create tstring.tbl!");
        }
    }

    private void replaceContents(Map<IFile, Collection<FileTString>> tstringMapping)
    {
        for (Map.Entry<IFile, Collection<FileTString>> entry : tstringMapping.entrySet())
        {
            logger.info("Replacing strings in \"" + entry.getKey().getName() + "\".");

            try
            {
                StringBuilder contentBuilder = new StringBuilder(entry.getKey().getContent());

                long currentOffset = 0;

                for (FileTString tstring : entry.getValue())
                {
                    NavigableSet<TString> headSet = valueSortedTStrings.headSet(tstring, true);

                    TString last = headSet.last();

                    if (!last.getValue().equals(tstring.getValue()))
                    {
                        logger.warning(
                                "TString with content '" + tstring.getValue() + "' and index " + tstring.getIndex() +
                                        " wasn't found in parsed TString list!");
                    }
                    else
                    {
                        String newContent = String.format("XSTR(\"%s\", %d)", last.getValue(), last.getIndex());

                        int begin = (int) (tstring.getOffset() + currentOffset);
                        int end = (int) (begin + tstring.getLength());

                        // Recompute offset
                        currentOffset = currentOffset + newContent.length() - tstring.getLength();

                        contentBuilder.replace(begin, end, newContent);
                    }
                }

                entry.getKey().writeContent(contentBuilder.toString());
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Failed to update contents of file '" + entry.getKey().getName() + "'.", e);
            }
        }
    }

    private void writeTable(IFile tStringTable)
    {
        PrintStream stream = null;
        try
        {
            stream = new PrintStream(tStringTable.openOutputStream(), true);

            stream.println("#default");
            for (TString string : indexSortedTStrings)
            {
                stream.printf("%d, \"%s\"%n%n", string.getIndex(), string.getValue());
            }
            stream.println("#end");
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error while writing TStrings table!", e);
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }

    private void buildTStringsSets(Collection<FileTString> fileStrings, IFileProvider fileProvider)
    {
        // We use a special comparator to make two TStrings equal when their contents match
        valueSortedTStrings = new TreeSet<TString>(new TStringValueComparator());
        indexSortedTStrings = new TreeSet<TString>(new TStringIndexComparator());

        if (updateExisitingTable)
        {
            readExisitingTable(fileProvider.getTStringTable(false));
        }

        for (FileTString fileTString : fileStrings)
        {
            // Add a clean TString instance here
            addTString(new TString(fileTString.getValue(), fileTString.getIndex()));
        }
    }

    private void addTString(TString string)
    {
        if (string.isImmutable())
        {
            throw new IllegalArgumentException("TString may not be immutable!");
        }

        if (string.getIndex() < 0)
        {
            string.setIndex(findNextFreeIndex());
        }

        // We can do this as the two sets use special comparators
        boolean valueContained = valueSortedTStrings.contains(string);
        boolean indexContained = indexSortedTStrings.contains(string);

        if (!valueContained && !indexContained)
        {
            // This is a completely new string
            valueSortedTStrings.add(string);
            indexSortedTStrings.add(string);
        }
        else if (valueContained && !indexContained)
        {
            NavigableSet<TString> stringHeadSet = valueSortedTStrings.headSet(string, true);
            logger.warning(String.format("Found duplicate string '%s' with different index %d and %d. Keeping first...",
                    string.getValue(), stringHeadSet.last().getIndex(), string.getIndex()));
        }
        else if (!valueContained) // indexContained is always true here
        {
            logger.warning(String.format("Found duplicate index usage of %d. Fixing...", string.getIndex()));

            // Set new index
            string.setIndex(findNextFreeIndex());

            // Now add it
            valueSortedTStrings.add(string);
            indexSortedTStrings.add(string);
        }
        else
        {
            // both already contained, simply check for errors
            NavigableSet<TString> valueHeadSet = valueSortedTStrings.headSet(string, true);
            NavigableSet<TString> indexHeadSet = indexSortedTStrings.headSet(string, true);

            TString sameValue = valueHeadSet.last();
            TString sameIndex = indexHeadSet.last();

            if (!sameValue.equals(sameIndex))
            {
                logger.warning(String.format("Found mismatching TStrings!%n" +
                        "String 1: XSTR(\"%s\", %d)%n" +
                        "String 2: XSTR(\"%s\", %d)", sameIndex.getValue(), sameIndex.getIndex(), sameValue.getValue(),
                        sameValue.getIndex()));
            }
        }
    }

    /**
     * Searches {@link #indexSortedTStrings} for a free index by using a sequential search.
     *
     * @return The next free index
     */
    private int findNextFreeIndex()
    {
        int lastIndex = -1;
        for (TString current : indexSortedTStrings)
        {
            int diff = current.getIndex() - lastIndex;

            if (diff > 1)
            {
                return lastIndex + 1;
            }

            lastIndex = current.getIndex();
        }

        return lastIndex + 1;
    }

    private void readExisitingTable(IFile tStringTable)
    {
        if (tStringTable == null)
        {
            logger.warning("No tstrings.tbl file found, going to create mode...");
            updateExisitingTable = false;
        }
        else
        {
            try
            {
                String content = tStringTable.getContent();

                Matcher matcher = tstringTableEntryPattern.matcher(content);

                while (matcher.find())
                {
                    int index = Integer.parseInt(matcher.group(1));
                    String contentString = matcher.group(2);

                    TString string = new TString(contentString, index, true);
                    valueSortedTStrings.add(string);
                    indexSortedTStrings.add(string);
                }
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Failed to read tstrings contents, going to create mode.", e);
            }
        }
    }
}
