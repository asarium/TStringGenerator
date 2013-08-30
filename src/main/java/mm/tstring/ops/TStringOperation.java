package mm.tstring.ops;

import java.io.File;

public abstract class TStringOperation
{
    protected File backup = null;

    protected File root = null;

    public TStringOperation(File root, File backup)
    {
        this.root = root;
        this.backup = backup;
    }

    public abstract boolean makeTString(File[] input);
}
