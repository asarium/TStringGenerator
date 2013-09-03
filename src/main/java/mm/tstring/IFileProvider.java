package mm.tstring;

public interface IFileProvider
{
    public boolean backupFiles();

    public Iterable<IFile> getFiles();

    public IFile getTStringTable(boolean create);
}
