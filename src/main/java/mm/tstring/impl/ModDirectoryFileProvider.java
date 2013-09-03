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

    private File modRootDirecotry;

    public ModDirectoryFileProvider(File modRootDirecotry)
    {
        if (modRootDirecotry == null)
        {
            throw new IllegalArgumentException("modRootDirecotry");
        }
        if (!modRootDirecotry.isDirectory())
        {
            throw new IllegalArgumentException("root directory not actually a directory");
        }

        this.modRootDirecotry = modRootDirecotry;
    }

    @Override
    public boolean backupFiles()
    {
        URI rootURI = modRootDirecotry.toURI();

        for (File f : getFilesystemFiles())
        {
            URI relativize = rootURI.relativize(f.toURI());

            URI backupURI = rootURI.resolve("/backup").resolve(relativize);

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
        if (!modRootDirecotry.isDirectory())
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
        File table = new File(modRootDirecotry, "data/tables/tstrings.tbl");

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
            File directory = new File(modRootDirecotry, searchDir);

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
