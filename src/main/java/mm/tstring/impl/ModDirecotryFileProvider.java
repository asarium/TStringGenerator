package mm.tstring.impl;

import mm.tstring.IFile;
import mm.tstring.IFileProvider;
import mm.tstring.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModDirecotryFileProvider implements IFileProvider
{
    private static final String[] fileExtensions = {"fs2, fc2", "tbl", "tbm"};

    private static final String[] searchPaths = {"/data/tables", "/data/missions"};

    private File modRootDirecotry;

    public ModDirecotryFileProvider(File modRootDirecotry)
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
    public Iterable<IFile> getFiles()
    {
        if (!modRootDirecotry.isDirectory())
        {
            return Collections.emptyList();
        }

        List<IFile> files = new ArrayList<IFile>();

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
                            files.add(new DefaultFile(f));
                            break;
                        }
                    }
                }
            }
        }

        return files;
    }
}
