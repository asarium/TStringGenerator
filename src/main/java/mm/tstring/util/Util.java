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

    public static boolean createFile(File file)
    {
        if (!file.exists())
        {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            {
                logger.warning("Failed to create directory '" + file.getParent() + "'!");
                return false;
            }

            try
            {
                if (file.createNewFile())
                {
                    logger.fine("Created file '" + file.getAbsolutePath() + "'.");
                }

                return true;
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Failed to create file '" + file.getAbsolutePath() + "'!", e);
                return false;
            }
        }
        else if (!file.isFile())
        {
            return false;
        }
        else
        {
            return true;
        }
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
