package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.util.Util;

import java.io.File;
import java.io.IOException;

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
