package mm.tstring.config;

import com.beust.jcommander.IStringConverter;
import mm.tstring.config.TStringConfig.Mode;

public class ModeConverter implements IStringConverter<Mode>
{
    @Override
    public Mode convert(String value)
    {
        return Mode.valueOf(value.toUpperCase());
    }
}
