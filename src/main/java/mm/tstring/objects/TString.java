package mm.tstring.objects;

import java.util.logging.Logger;

/**
 * Class to represent an entry in tstrings.tbl
 *
 * @author m!m
 */
public class TString implements Comparable<TString>
{

    private static final Logger logger = Logger.getLogger(TString.class.getName());

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
            return 0;
        }

        if (this.getIndex() > o.getIndex())
        {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        TString other = (TString) obj;

        return this.index == other.index;
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
     * Gets the value of this object
     *
     * @return The value
     */
    public String getValue()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.index;
        return result;
    }

    /**
     * Sets the new index.
     *
     * @param index The new index
     * @throws IllegalArgumentException When the index is < -1
     */
    public void setIndex(int index)
    {
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

    /**
     * Sets the new value of this object
     *
     * @param value The new <code>String</code> value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}
