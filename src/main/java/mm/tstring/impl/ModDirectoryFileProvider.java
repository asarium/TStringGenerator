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
    private static final String[] fileExtensions = {"fs2", "fc2", "tbl", "tbm"};

    private static final String[] searchPaths = {"/data/tables", "/data/missions"};

    private File backupDir;

    private File modRootDirectory;

    public ModDirectoryFileProvider(File modRootDirectory, File backupDir)
    {
        if (modRootDirectory == null)
        {
            throw new IllegalArgumentException("modRootDirectory");
        }
        if (!modRootDirectory.isDirectory())
        {
            throw new IllegalArgumentException("root directory not actually a directory");
        }

        if (backupDir == null)
        {
            throw new IllegalArgumentException("backupDir");
        }
        if (!backupDir.isDirectory())
        {
            throw new IllegalArgumentException("backup directory not actually a directory");
        }

        this.backupDir = backupDir;
        this.modRootDirectory = modRootDirectory;
    }

    @Override
    public boolean backupFiles()
    {
        URI rootURI = modRootDirectory.toURI();

        for (File f : getFilesystemFiles())
        {
            URI relativize = rootURI.relativize(f.toURI());

            File backupFile = new File(backupDir, relativize.getPath());

            if (!Util.copyFile(f, backupFile))
            {
                return false;
            }
        }

        return true;
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
    public IFile getTStringTable(boolean create)
    {
        // the table should be located there
        File table = new File(modRootDirectory, "data/tables/tstrings.tbl");

        if (table.isFile())
        {
            return new DefaultFile(table);
        }
        else
        {
            if (create)
            {
                if (!table.exists())
                {
                    if (!Util.createFile(table))
                    {
                        return null;
                    }
                }
                return new DefaultFile(table);
            }
            else
            {
                return null;
            }
        }
    }
}
