package mm.tstring.util;

import mm.tstring.config.TStringConfig;
import mm.tstring.config.TStringConfig.Mode;
import mm.tstring.objects.TString;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class declaring utility functions.
 *
 * @author m!m
 */
public class Util
{
    private static final Logger logger = Logger.getLogger(Util.class.getName());

    private static final List<Integer> occupiedIndexes = new ArrayList<Integer>();

    /**
     * Overloaded method. Referes to {@link #addTString(TString)}.
     *
     * @param value Value of the TString
     * @param index Index of the TString
     */
    public synchronized static void addTString(String value, int index)
    {
        Util.addTString(new TString(value, index));
    }

    /**
     * Adds the specified <code>TString</code> to
     * {@link TStringGlobals#tstrings} but only if the value of the string isn't
     * already in the list.
     *
     * @param string The TString to add
     */
    public synchronized static void addTString(TString string)
    {
        for (TString tstring : TStringGlobals.tstrings)
        {
            if (tstring.getValue().equals(string.getValue()))
            {
                return;
            }
        }

        if (!Util.occupiedIndexes.contains(string.getIndex()) && string.getIndex() > 0)
        {
            TStringGlobals.tstrings.add(string);
            Util.occupiedIndexes.add(string.getIndex());
        }
        else
        {
            while (Util.occupiedIndexes.contains(string.getIndex()))
            {
                string.setIndex(string.getIndex() + 1);
            }
            TStringGlobals.tstrings.add(string);
            Util.occupiedIndexes.add(string.getIndex());
        }

        Collections.sort(TStringGlobals.tstrings);
    }

    private static void askSwitch()
    {
        System.out.print("An existing TStrings table has been found. Do you want to switch to the update mode?(y/n)");
        String input = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        while (input == null)
        {
            try
            {
                input = stdIn.readLine();
            }
            catch (IOException e)
            {
            }
            if (input != null)
            {
                input = input.toLowerCase();
                if (input.equals("y"))
                {
                    TStringConfig.setMode(Mode.UPDATE);
                    System.out.println("Mode has been switched to 'update'");
                }
                else
                {
                    System.out.println("Staying with 'create'");
                }
            }
        }
    }

    public static <T extends Comparable<T>> int compare(T o1, T o2)
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
        else
        {
            return o1.compareTo(o2);
        }
    }

    /**
     * Copies <code>srFile</code> to <code>dtFile</code>
     *
     * @param srcFile The source file to be copied.
     * @param dstFile The destination
     */
    public static boolean copyFile(File srcFile, File dstFile)
    {
        if (!srcFile.isFile())
        {
            logger.warning("'" + srcFile.getAbsolutePath() + "' is no file!");
            return false;
        }

        if (!dstFile.exists())
        {
            if (!dstFile.getParentFile().exists() && !dstFile.getParentFile().mkdirs())
            {
                logger.warning("Failed to create directory '" + dstFile.getParent() + "'!");
                return false;
            }

            try
            {
                if (dstFile.createNewFile())
                {
                    logger.fine("Created file '" + dstFile.getAbsolutePath() + "'.");
                }
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Failed to create file '" + dstFile.getAbsolutePath() + "'!", e);
                return false;
            }
        }
        else if (!dstFile.isFile())
        {
            logger.warning("Destination file '" + dstFile.getAbsolutePath() + "' already exists but is no file!");
            return false;
        }

        FileChannel source = null;
        FileChannel destination = null;
        try
        {
            source = new FileInputStream(srcFile).getChannel();
            destination = new FileOutputStream(dstFile).getChannel();

            destination.transferFrom(source, 0, source.size());
        }
        catch (FileNotFoundException e)
        {
            // This should not happen
            logger.log(Level.SEVERE, "Failed to find file!", e);
            return false;
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Failed to copy file!", e);
            return false;
        }
        finally
        {
            if (source != null)
            {
                try
                {
                    source.close();
                }
                catch (IOException e)
                {
                    logger.log(Level.SEVERE,
                            "Failed to close file channel from file '" + srcFile.getAbsolutePath() + "'!", e);
                }
            }

            if (destination != null)
            {
                try
                {
                    destination.close();
                }
                catch (IOException e)
                {
                    logger.log(Level.SEVERE,
                            "Failed to close file channel from file '" + dstFile.getAbsolutePath() + "'!", e);
                }
            }
        }

        return true;
    }

    public static String getExtension(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name");
        }

        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Return the index matching the specified String or -1 if not found.
     *
     * @param search The String to search for
     * @return The index of the TString object
     */
    public static int getIndex(String search)
    {
        for (TString string : TStringGlobals.tstrings)
        {
            if (string.getValue().equals(search))
            {
                return string.getIndex();
            }
        }
        return -1;
    }

    /**
     * Searches for unoccupied indexes and returns the first it can find.
     *
     * @return The first free index
     */
    public static int getNextFreeTStringsIndex()
    {
        Collections.sort(Util.occupiedIndexes);
        int last = -1;
        for (Integer integ : Util.occupiedIndexes)
        {
            if (integ - last > 0)
            {
                return last + 1;
            }
            else
            {
                last = last + 1;
            }
        }
        return Util.occupiedIndexes.get(Util.occupiedIndexes.size() - 1) + 1;
    }

    /**
     * Filters the files that are to be parsed out of the directories specified
     * by {@link TStringGlobals#searchDirs}
     *
     * @return The files to be parsed.
     */
    public static File[] getParseFiles()
    {
        List<File> parseFiles = new ArrayList<File>();
        boolean tStringFound = false;
        for (String search : TStringGlobals.searchDirs)
        {
            File searchDir = new File(TStringConfig.getRootDir().getAbsolutePath() + search);
            if (searchDir.exists())
            {
                File[] searchFiles = searchDir.listFiles(new FilenameFilter()
                {

                    @Override
                    public boolean accept(File dir, String name)
                    {
                        return Util.isTranslateFile(name);
                    }
                });
                for (File searchFile : searchFiles)
                {
                    if (searchFile.getName().equals("tstrings.tbl"))
                    {
                        tStringFound = true;
                        if (TStringConfig.getMode() == Mode.CREATE)
                        {
                            // Util.askSwitch();
                        }
                    }
                    parseFiles.add(searchFile);
                }
            }
            else
            {
                System.err.println("The directory '" + searchDir.getAbsolutePath() + "' does not exist.");
            }
        }
        if (TStringConfig.getMode() == Mode.UPDATE && !tStringFound)
        {
            System.out.println(
                    "'update' mode was specified but there was no TStrings table found. Switching back to 'create'");
            TStringConfig.setMode(Mode.CREATE);
        }
        File[] parseArray = new File[parseFiles.size()];
        for (int i = 0; i < parseArray.length; i++)
        {
            parseArray[i] = parseFiles.get(i);
        }
        return parseArray;
    }

    public static boolean isTranslateFile(String name)
    {
        final String[] exts = {".tbl", ".tbm", ".fs2", ".fc2"};

        for (String ext : exts)
        {
            if (name.endsWith(ext))
            {
                return true;
            }
        }

        return false;
    }

    public static void optimizeTable()
    {
        Collections.sort(TStringGlobals.tstrings);
        System.out.println("Optimizing table...");
        for (int i = 0; i < TStringGlobals.tstrings.size(); i++)
        {
            TString current = TStringGlobals.tstrings.get(i);
            if (i == 0 && current.getIndex() > 0)
            {
                current.setIndex(0);
            }
            if (i > 0)
            {
                if (current.getIndex() - TStringGlobals.tstrings.get(i - 1).getIndex() > 1)
                {
                    current.setIndex(TStringGlobals.tstrings.get(i - 1).getIndex() + 1);
                }
            }
        }
    }

    /**
     * Reads the contents of a file
     *
     * @param file The file this function reads from.
     * @return The contents or null on error.
     * @throws IOException If the file cannot be read or something similar
     */
    public static String readFile(File file) throws IOException
    {
        if (file.exists())
        {
            if (file.canRead())
            {
                StringBuilder content = new StringBuilder();

                BufferedReader input = null;
                try
                {
                    input = new BufferedReader(new FileReader(file));
                    char[] buffer = new char[1024];
                    int n;

                    while ((n = input.read(buffer)) != -1)
                    {
                        content.append(buffer, 0, n);
                    }
                }
                finally
                {
                    if (input != null)
                    {
                        input.close();
                    }
                }

                return content.toString();
            }
            else
            {
                throw new IOException("File " + file.getPath() + " is not readable.");
            }
        }
        else
        {
            throw new FileNotFoundException("File " + file.getPath() + " does not exist.");
        }
    }

    /**
     * Writes content to a file
     *
     * @param content The content to be written
     * @param file    The file that is beeing written
     * @throws IOException If something goes wrong
     */
    public static void write(String content, File file) throws IOException
    {
        if (!file.exists())
        {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            {
                Util.logger.severe("Couldn't create directory '" + file.getParent() + "'!");
                return;
            }
            if (file.createNewFile())
            {
                Util.logger.fine("Created file '" + file.getAbsolutePath() + "'.");
            }
        }
        FileWriter writer = null;
        BufferedWriter output = null;

        try
        {
            writer = new FileWriter(file);
            output = new BufferedWriter(writer);

            output.write(content);
        }
        finally
        {
            if (output != null)
            {
                output.close();
            }
            if (writer != null)
            {
                writer.close();
            }
        }
    }
}
