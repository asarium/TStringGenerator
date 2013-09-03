package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.IFileProvider;
import mm.tstring.ITStringManager;
import mm.tstring.ITstringParser;
import mm.tstring.objects.FileTString;
import mm.tstring.objects.TString;
import mm.tstring.util.Util;

import java.util.*;
import java.util.logging.Logger;

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

            int strResult = Util.compare(o1.getValue(), o2.getValue());

            if (strResult == 0)
            {
                return 0;
            }
            else
            {
                return o1.compareTo(o2);
            }
        }
    }

    private static final Logger logger = Logger.getLogger(DefaultTStringManager.class.getName());

    private NavigableSet<TString> indexSortedTStrings;

    private NavigableSet<TString> valueSortedTStrings;

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

        buildTStringsSets(parseTStrings(parser, fileProvider));
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
    public void writeStrings(IFileProvider fileProvider)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void buildTStringsSets(Collection<FileTString> fileStrings)
    {
        // We use a special comparator to make two TStrings equal when their contents match
        valueSortedTStrings = new TreeSet<TString>(new TStringValueComparator());
        indexSortedTStrings = new TreeSet<TString>(new TStringIndexComparator());

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
}
