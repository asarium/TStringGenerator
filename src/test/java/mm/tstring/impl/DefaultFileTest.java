package mm.tstring.impl;

import mm.tstring.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;

public class DefaultFileTest
{

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File testFile;

    @Before
    public void setUp() throws Exception
    {
        testFile = folder.newFile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNoFile() throws Exception
    {
        new DefaultFile(folder.newFolder());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNull() throws Exception
    {
        new DefaultFile(null);
    }

    @Test
    public void testGetContent() throws Exception
    {
        String testData = generateTestData();

        Writer writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(testFile));

            writer.write(testData.toCharArray());
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }

        DefaultFile file = new DefaultFile(testFile);

        Assert.assertEquals(testData, file.getContent());
    }

    private String generateTestData()
    {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < rand.nextInt(15); i++)
        {
            int length = rand.nextInt(30);
            char[] buffer = new char[length];

            for (int j = 0; j < length; j++)
            {
                // Returns a ASCII printable character
                int character = 32 + rand.nextInt(94);

                buffer[j] = (char) character;
            }

            builder.append(buffer).append('\n');
        }

        return builder.toString();
    }

    @Test
    public void testGetName() throws Exception
    {
        DefaultFile file = new DefaultFile(testFile);

        Assert.assertEquals(testFile.getAbsolutePath(), file.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteContent() throws Exception
    {
        DefaultFile file = new DefaultFile(testFile);

        String testData = generateTestData();

        file.writeContent(testData);

        Assert.assertEquals(testData, Util.readFile(testFile));

        // Should throw exception
        file.writeContent(null);
    }
}
