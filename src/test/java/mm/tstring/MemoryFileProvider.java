package mm.tstring;

/**
 * Created with IntelliJ IDEA.
 * User: Marius
 * Date: 02.09.13
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class MemoryFileProvider implements IFileProvider
{
    private Iterable<IFile> files;

    private IFile tstringTable;

    public MemoryFileProvider(IFile tstringTable, Iterable<IFile> files)
    {
        this.tstringTable = tstringTable;
        this.files = files;
    }

    @Override
    public boolean backupFiles()
    {
        return true;
    }

    @Override
    public Iterable<IFile> getFiles()
    {
        return files;
    }

    @Override
    public IFile getTStringTable()
    {
        return tstringTable;
    }
}
