package mm.tstring;

import mm.tstring.objects.FileTString;

import java.util.Collection;

public interface ITstringParser
{
    public Collection<FileTString> parseStrings(IFile file);
}
