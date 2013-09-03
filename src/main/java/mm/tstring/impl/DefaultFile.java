package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.util.Util;

import java.io.*;

public class DefaultFile implements IFile
{
    private File file;

    public DefaultFile(File file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("file");
        }
        if (!file.isFile())
        {
            throw new IllegalArgumentException("file is not a file!");
        }

        this.file = file;
    }

    @Override
    public int hashCode()
    {
        return file != null ? file.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultFile))
        {
            return false;
        }

        DefaultFile that = (DefaultFile) o;

        if (file != null ? !file.equals(that.file) : that.file != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public OutputStream openOutputStream() throws IOException
    {
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    @Override
    public String getContent() throws IOException
    {
        return Util.readFile(file);
    }

    @Override
    public String getName()
    {
        try
        {
            return file.getCanonicalPath();
        }
        catch (IOException e)
        {
            return file.getAbsolutePath();
        }
    }

    @Override
    public void writeContent(String content) throws IOException
    {
        if (content == null)
        {
            throw new IllegalArgumentException("content");
        }

        Util.write(content, file);
    }
}
