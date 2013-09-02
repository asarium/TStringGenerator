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

    public MemoryFileProvider(Iterable<IFile> files)
    {
        this.files = files;
    }

    @Override
    public Iterable<IFile> getFiles()
    {
        return files;
    }
}
