package mm.tstring.objects;

import mm.tstring.util.Util;

import java.util.logging.Logger;

/**
 * Class to represent an entry in tstrings.tbl
 *
 * @author m!m
 */
public class TString implements Comparable<TString>
{

    private static final Logger logger = Logger.getLogger(TString.class.getName());

    private boolean immutable = false;

    private int index = -1;

    private String value = null;

    /**
     * The constructor constructs a <code>TString</code>-object with the
     * specified index and value.
     *
     * @param value The <code>String</code> value of this TString
     * @param index The index.
     */
    public TString(String value, int index)
    {
        this.setValue(value);
        this.setIndex(index);
    }

    public TString(String value, int index, boolean immutable)
    {
        this.index = index;
        this.value = value;
        this.immutable = immutable;
    }

    @Override
    public int compareTo(TString o)
    {
        if (o == null)
        {
            return 1;
        }

        if (this.getIndex() < o.getIndex())
        {
            return -1;
        }

        if (this.getIndex() == o.getIndex())
        {
            return Util.compare(this.getValue(), o.getValue());
        }

        if (this.getIndex() > o.getIndex())
        {
            return 1;
        }

        return 0;
    }

    /**
     * Gets the value of this object
     *
     * @return The value
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Sets the new value of this object
     *
     * @param value The new <code>String</code> value.
     */
    public void setValue(String value)
    {
        if (isImmutable())
        {
            throw new UnsupportedOperationException("Object is immutable");
        }

        this.value = value;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    /**
     * Gets the index of this object
     *
     * @return The index
     */
    public int getIndex()
    {
        return this.index;
    }

    /**
     * Sets the new index.
     *
     * @param index The new index
     * @throws IllegalArgumentException When the index is < -1
     */
    public void setIndex(int index)
    {
        if (isImmutable())
        {
            throw new UnsupportedOperationException("Object is immutable");
        }

        if (index >= -1)
        {
            this.index = index;
        }
        else
        {
            TString.logger.warning("Found illegal value '" + index + "'. Setting it to -1.");
            this.index = -1;
        }
    }

    @Override
    public int hashCode()
    {
        int result = index;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof TString))
        {
            return false;
        }

        TString tString = (TString) o;

        if (index != tString.index)
        {
            return false;
        }

        return !(value != null ? !value.equals(tString.value) : tString.value != null);
    }

    @Override
    public String toString()
    {
        return String.format("XSTR(\"%s\", %d)", value == null ? "<nothing>" : value, index);
    }
}
