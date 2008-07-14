package xqts.fullaxis;

import junit.framework.Test;
import junit.framework.TestSuite;
import xqts.fullaxis.ancestor_or_selfaxis.AncestorOrSelfAxisTest;
import xqts.fullaxis.ancestoraxis.AncestorAxisTest;
import xqts.fullaxis.following_siblingaxis.FollowingSiblingAxisTest;
import xqts.fullaxis.followingaxis.FollowingAxisTest;
import xqts.fullaxis.preceding_siblingaxis.PrecedingSiblingAxisTest;
import xqts.fullaxis.precedingaxis.PrecedingAxisTest;

public class FullAxisAllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for xqts.fullaxis");
        //$JUnit-BEGIN$
        suite.addTestSuite(AncestorOrSelfAxisTest.class);
        suite.addTestSuite(AncestorAxisTest.class);
        suite.addTestSuite(FollowingSiblingAxisTest.class);
        suite.addTestSuite(FollowingAxisTest.class);
        suite.addTestSuite(PrecedingSiblingAxisTest.class);
        suite.addTestSuite(PrecedingAxisTest.class);
        //$JUnit-END$
        return suite;
    }

}
