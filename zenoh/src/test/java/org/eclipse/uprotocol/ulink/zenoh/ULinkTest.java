package org.eclipse.uprotocol.ulink.zenoh;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ULinkTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ULinkTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ULinkTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testULink()
    {
        assertTrue( true );
    }
}
