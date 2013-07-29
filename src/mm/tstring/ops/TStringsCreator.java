package mm.tstring.ops;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import mm.tstring.config.TStringConfig;
import mm.tstring.objects.TString;
import mm.tstring.util.TStringGlobals;
import mm.tstring.util.Util;

/**
 * @author m!m
 * 
 */
public class TStringsCreator extends TStringOperation
{
    private class ParseCallable implements Callable<Void>
    {
        private File file;

        public ParseCallable(File file)
        {
            super();
            this.file = file;
        }

        @Override
        public Void call() throws Exception
        {
            if (!TStringsCreator.this.isUnparsedFile(this.file))
            {
                System.out.println("Parsing file: " + this.file.getPath());
                try
                {
                    String content = Util.readFile(this.file);

                    Matcher contentMatcher = TStringGlobals.tstringPattern
                            .matcher(content);

                    while (contentMatcher.find())
                    {
                        String value = contentMatcher.group(1);
                        int index;

                        try
                        {
                            index = Integer.parseInt(contentMatcher.group(2));
                        }
                        catch (NumberFormatException e)
                        {
                            TStringsCreator.logger.log(Level.SEVERE,
                                    "Malformatted index element: '"
                                            + contentMatcher.group(2) + "'.");
                            continue;
                        }

                        Util.addTString(value, index);
                    }
                }
                catch (IOException e)
                {
                    System.err.println("Exception while reading file "
                            + this.file.getPath());
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private static class UpdateCallable implements Callable<Void>
    {
        private File file;

        public UpdateCallable(File file)
        {
            super();
            this.file = file;
        }

        @Override
        public Void call() throws Exception
        {
            if (!this.file.getName().equals("tstrings.tbl"))
            {
                System.out.println("Updating file: "
                        + this.file.getAbsolutePath());
                try
                {
                    String contentStr = Util.readFile(this.file);
                    StringBuilder content = new StringBuilder(contentStr);

                    Matcher contentMatcher = TStringGlobals.tstringPattern
                            .matcher(contentStr);

                    int num = 0;
                    while (contentMatcher.find())
                    {
                        String value = contentMatcher.group(1);
                        int index = Util.getIndex(value);

                        if (index < 0)
                        {
                            TStringsCreator.logger
                                    .warning("Couldn't find index for value '"
                                            + value + "'! Using -1 instead.");
                        }

                        content.replace(contentMatcher.start(),
                                contentMatcher.end(),
                                String.format("XSTR(\"%s\", %d)", value, index));

                        num++;

                        int i = contentMatcher.start();
                        contentMatcher.reset(content.toString());
                        contentMatcher.region(i + 1, content.length());
                    }

                    if (num > 0)
                    {
                        Util.write(content.toString(), this.file);
                    }
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            return null;
        }
    }

    private static final String[] ignoredFiles = { "tstrings.tbl",
            "strings.tbl"                     };

    private static final Logger   logger       = Logger.getLogger(TStringsCreator.class
                                                       .getName());

    public TStringsCreator(File root, File backup)
    {
        super(root, backup);
    }

    private boolean backupFiles(File[] files)
    {
        if (!this.backup.exists() && !this.backup.mkdirs())
        {
            TStringsCreator.logger.severe("Could not create '"
                    + this.backup.getAbsolutePath()
                    + "'! Aborting operation...");
            return false;
        }

        for (File file : files)
        {
            System.out.println("Backup in progress: " + file.getPath());
            Util.copyfile(
                    file.getAbsolutePath(),
                    this.backup.getAbsolutePath() + "/"
                            + file.getParentFile().getName() + "/"
                            + file.getName());
        }

        return true;
    }

    private boolean isUnparsedFile(File file)
    {
        for (String ignored : TStringsCreator.ignoredFiles)
        {
            if (file.getName().toLowerCase().equals(ignored))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a new tstrings.tbl overwriting the former one.
     * 
     * @param files
     *            The files where the TStrings will be extracted
     */
    @Override
    public boolean makeTString(File[] files)
    {
        if (!TStringConfig.isNoBackup())
        {
            if (!this.backupFiles(files))
            {
                return false;
            }
        }
        this.parseFiles(files);
        this.updateIndexes();
        Util.optimizeTable();
        this.updateFiles(files);
        this.updateTStringsTable();

        return true;
    }

    private void parseFiles(File[] files)
    {
        ExecutorService exec = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        for (File file : files)
        {
            exec.submit(new ParseCallable(file));
        }

        exec.shutdown();

        while (true)
        {
            try
            {
                if (exec.awaitTermination(1, TimeUnit.MINUTES))
                {
                    break;
                }
            }
            catch (InterruptedException e)
            {
            }
        }

        Collections.sort(TStringGlobals.tstrings);
    }

    private void updateFiles(File[] files)
    {
        ExecutorService exec = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        for (File file : files)
        {
            exec.submit(new UpdateCallable(file));
        }

        exec.shutdown();

        while (true)
        {
            try
            {
                if (exec.awaitTermination(1, TimeUnit.MINUTES))
                {
                    break;
                }
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    private void updateIndexes()
    {
        Collections.sort(TStringGlobals.tstrings);
        for (TString string : TStringGlobals.tstrings)
        {
            if (string.getIndex() < 0)
            {
                int nextIndex = Util.getNextFreeTStringsIndex();
                string.setIndex(nextIndex);
            }
        }
        Collections.sort(TStringGlobals.tstrings);
    }

    private void updateTStringsTable()
    {
        System.out.println("Generating tstrings.tbl");
        StringBuilder content = new StringBuilder("#default\n");
        for (TString string : TStringGlobals.tstrings)
        {
            content.append(string.getIndex());
            content.append(", \"");
            content.append(string.getValue());
            content.append("\"\n\n");
        }
        content.append("#end");
        File tstrings = new File(this.root.getAbsolutePath()
                + "/data/tables/tstrings.tbl");
        if (tstrings.exists() && !TStringConfig.isNoBackup())
        {
            Util.copyfile(tstrings.getAbsolutePath(), TStringConfig.getBackupDir()
                    + "/tstrings.tbl");
        }
        try
        {
            System.out.println("Generating done. Now writng tstring.tbl");
            Util.write(content.toString(), tstrings);
            System.out.println("Writing completed. Have a nice day...");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
