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
    private static class TStringComparator implements Comparator<TString>
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

    private Map<IFile, Set<FileTString>> fileTStringsMapping;

    private Set<TString> sortedTStrings;

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

        fileTStringsMapping = new HashMap<IFile, Set<FileTString>>();

        for (IFile file : fileProvider.getFiles())
        {
            logger.info("Parsing file: " + file.getName());
            Collection<FileTString> fileTStrings = parser.parseStrings(file);

            fileTStringsMapping.put(file, new TreeSet<FileTString>(fileTStrings));
        }

        sortedTStrings = buildTStringsSet();
    }

    @Override
    public Collection<TString> getTStrings()
    {
        return sortedTStrings;
    }

    @Override
    public void writeStrings(IFileProvider fileProvider)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<TString> buildTStringsSet()
    {
        // We use a special comparator to make two TStrings equal when their contents match
        NavigableSet<TString> tstringSet = new TreeSet<TString>(new TStringComparator());

        for (Map.Entry<IFile, Set<FileTString>> entry : fileTStringsMapping.entrySet())
        {
            for (FileTString fileTString : entry.getValue())
            {
                // Add a clean TString instance here
                TString string = new TString(fileTString.getValue(), fileTString.getIndex());
                if (!tstringSet.add(string))
                {
                    // The set already contains a string with the same string value
                    SortedSet<TString> headSet = tstringSet.headSet(string, true);

                    // Check if the indexes match
                    if (headSet.last().getIndex() != string.getIndex())
                    {
                        // We have an index mismatch with the same content
                        logger.warning(String.format(
                                "String \"%s\" has mismatching indexes! Found indexes %d and %d. Keeping first",
                                string.getValue(), headSet.last().getIndex(), string.getIndex()));
                    }
                }
            }

        }


        return tstringSet;
    }
}
