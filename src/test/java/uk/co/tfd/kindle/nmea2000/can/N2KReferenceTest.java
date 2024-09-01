package uk.co.tfd.kindle.nmea2000.can;

import org.junit.Assert;
import org.junit.Test;

public class N2KReferenceTest {

    @Test
    public void quickTest() {
        N2KReference.TimeSource ref = N2KReference.TimeSource.lookup( 5);
        Assert.assertEquals("LocalCrystalClock", ref.name);
        Assert.assertEquals(5, ref.id);
    }
}
