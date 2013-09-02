package mm.tstring.impl;

import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Marius
 * Date: 02.09.13
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTStringManagerTest
{
    @Test
    public void testWriteTable() throws Exception
    {

    }

    @Test
    public void testReplaceFileContents() throws Exception
    {

    }

    @Test
    public void testGetTStrings() throws Exception
    {

    }

    @Test
    public void testCollectTStrings() throws Exception
    {
        DefaultTStringManager manager = new DefaultTStringManager();
        DefaultTStringParser parser = new DefaultTStringParser();
        ModDirecotryFileProvider provider = new ModDirecotryFileProvider(new File("G:\\Programme\\FreeSpace\\Diaspora"));

        manager.collectTStrings(parser, provider);
    }
}
