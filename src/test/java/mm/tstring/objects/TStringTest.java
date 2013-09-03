package mm.tstring.objects;

import org.junit.Assert;
import org.junit.Test;

public class TStringTest
{
    @Test(expected = UnsupportedOperationException.class)
    public void testSetIndex() throws Exception
    {
        {
            TString string = new TString("Test", 1234);

            string.setIndex(4321);

            Assert.assertEquals(4321, string.getIndex());
        }
        {
            TString string = new TString("Test", 1234, true);

            string.setIndex(4321);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetValue() throws Exception
    {
        {
            TString string = new TString("Test", 1234);

            string.setValue("TestEdit");

            Assert.assertEquals("TestEdit", string.getValue());
        }
        {
            TString string = new TString("Test", 1234, true);

            string.setValue("TestEdit");
        }
    }
}
