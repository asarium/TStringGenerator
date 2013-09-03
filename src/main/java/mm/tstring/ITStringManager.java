package mm.tstring;

import mm.tstring.objects.TString;

import java.util.Collection;

public interface ITStringManager
{
    public void collectTStrings(ITstringParser parser, IFileProvider fileProvider);

    public Collection<TString> getTStrings();

    public void writeStrings(ITstringParser parser, IFileProvider fileProvider);
}
