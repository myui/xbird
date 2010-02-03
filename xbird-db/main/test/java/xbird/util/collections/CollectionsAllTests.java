package xbird.util.collections;

import xbird.util.collections.ints.Int2IntHashTest;
import xbird.util.collections.ints.Int2IntLRUMapTest;
import xbird.util.collections.ints.IntHashTest;
import xbird.util.collections.ints.IntLRUMapTest;
import xbird.util.collections.longs.Long2LongHashTest;
import xbird.util.collections.longs.LongHashTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CollectionsAllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for xbird.util.collections");
        //$JUnit-BEGIN$
        suite.addTestSuite(LongHashTest.class);
        suite.addTestSuite(Long2LongHashTest.class);
        suite.addTestSuite(Int2IntLRUMapTest.class);
        suite.addTestSuite(Int2IntHashTest.class);
        suite.addTestSuite(IntHashTest.class);
        suite.addTestSuite(LRUPerformanceTest.class);
        suite.addTestSuite(IntLRUMapTest.class);
        //$JUnit-END$
        return suite;
    }

}
