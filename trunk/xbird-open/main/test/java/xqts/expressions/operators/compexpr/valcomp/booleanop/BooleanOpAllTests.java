package xqts.expressions.operators.compexpr.valcomp.booleanop;

import junit.framework.Test;
import junit.framework.TestSuite;
import xqts.expressions.operators.compexpr.valcomp.booleanop.booleanequal.BooleanEqualTest;
import xqts.expressions.operators.compexpr.valcomp.booleanop.booleangt.BooleanGTTest;
import xqts.expressions.operators.compexpr.valcomp.booleanop.booleanlt.BooleanLTTest;

public class BooleanOpAllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for xqts.expressions.operators.compexpr.valcomp.booleanop.booleanequal");
        //$JUnit-BEGIN$
        suite.addTestSuite(BooleanEqualTest.class);
        suite.addTestSuite(BooleanLTTest.class);
        suite.addTestSuite(BooleanGTTest.class);
        //$JUnit-END$
        return suite;
    }

}
