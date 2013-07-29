package mm.tstring.config;

import mm.tstring.config.TStringConfig.Mode;

import com.beust.jcommander.IStringConverter;

public class ModeConverter implements IStringConverter<Mode>
{
    @Override
    public Mode convert(String value)
    {
        return Mode.valueOf(value.toUpperCase());
    }
}
