package mm.tstring;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryFile implements IFile
{
    private String content;

    private String name;

    public MemoryFile(String name, String content)
    {
        this.content = content;
        this.name = name;
    }

    @Override
    public OutputStream openOutputStream() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContent() throws IOException
    {
        return content;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void writeContent(String content) throws IOException
    {
        if (content == null)
        {
            throw new IllegalArgumentException("content");
        }

        this.content = content;
    }
}
