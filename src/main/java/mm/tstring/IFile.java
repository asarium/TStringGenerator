package mm.tstring;

import java.io.IOException;

/**
 * Abstraction around a file which can be read and written to.
 */
public interface IFile
{
    /**
     * Gets the contents of the file as a string.
     *
     * @return The contents of the file
     * @throws IOException When a error happens while reading the file
     */
    public String getContent() throws IOException;

    /**
     * Gets the name of the underlying file instance, the format is not specified
     *
     * @return The name
     */
    public String getName();

    /**
     * Writes the given string to the file.
     *
     * @param content The content that should be written, may not be <code>null</code>
     * @throws IOException              When an error happens while writing tot he file
     * @throws IllegalArgumentException When <code>content</code> ist <code>null</code>
     */
    public void writeContent(String content) throws IOException;
}
