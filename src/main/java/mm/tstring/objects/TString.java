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

    private boolean editable = true;

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

    /**
     * Constructs a TString object with the aditional option to specify if it is
     * editable.
     *
     * @param value    String valie
     * @param index    The index
     * @param editable Sets this object to be editable or not.
     */
    public TString(String value, int index, boolean editable)
    {
        this(value, index);
        this.setEditable(editable);
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
        if (this.index != other.index)
        {
            return false;
        }
        return true;
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

    public boolean isEditable()
    {
        return this.editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
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
