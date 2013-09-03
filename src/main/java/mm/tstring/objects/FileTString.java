package mm.tstring.objects;

public class FileTString extends TString
{
    private final long length;

    private final long offset;

    /**
     * The constructor constructs a <code>TString</code>-object with the
     * specified index and value.
     *
     * @param value  The <code>String</code> value of this TString
     * @param index  The index.
     * @param offset The offset in the file
     * @param length The length of the whole XSTR part
     */
    public FileTString(String value, int index, long offset, long length)
    {
        super(value, index);
        this.offset = offset;
        this.length = length;
    }

    public long getLength()
    {
        return length;
    }

    public long getOffset()
    {
        return offset;
    }
}
