package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.IFileProvider;
import mm.tstring.util.Util;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ModDirectoryFileProvider implements IFileProvider
{
    private static final String[] fileExtensions = {"fs2, fc2", "tbl", "tbm"};

    private static final String[] searchPaths = {"/data/tables", "/data/missions"};

    private File modRootDirectory;

    public ModDirectoryFileProvider(File modRootDirectory)
    {
        if (modRootDirectory == null)
        {
            throw new IllegalArgumentException("modRootDirectory");
        }
        if (!modRootDirectory.isDirectory())
        {
            throw new IllegalArgumentException("root directory not actually a directory");
        }

        this.modRootDirectory = modRootDirectory;
    }

    @Override
    public boolean backupFiles()
    {
        URI rootURI = modRootDirectory.toURI();

        for (File f : getFilesystemFiles())
        {
            URI relativize = rootURI.relativize(f.toURI());

            URI backupURI = URI.create(rootURI.toString() +"backup/" +relativize.getPath());

            File backupFile = new File(backupURI);

            if (!Util.copyFile(f, backupFile))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterable<IFile> getFiles()
    {
        if (!modRootDirectory.isDirectory())
        {
            return Collections.emptyList();
        }

        List<IFile> files = new ArrayList<IFile>();

        for (File f : getFilesystemFiles())
        {
            files.add(new DefaultFile(f));
        }

        return files;
    }

    @Override
    public IFile getTStringTable()
    {
        // the table should be located there
        File table = new File(modRootDirectory, "data/tables/tstrings.tbl");

        if (table.isFile())
        {
            return new DefaultFile(table);
        }
        else
        {
            return null;
        }
    }

    private Collection<File> getFilesystemFiles()
    {
        List<File> files = new ArrayList<File>();

        for (String searchDir : searchPaths)
        {
            File directory = new File(modRootDirectory, searchDir);

            if (directory.isDirectory())
            {
                for (File f : directory.listFiles())
                {
                    String ext = Util.getExtension(f.getName());

                    for (String extension : fileExtensions)
                    {
                        if (extension.equalsIgnoreCase(ext))
                        {
                            files.add(f);
                            break;
                        }
                    }
                }
            }
        }

        return files;
    }
}
