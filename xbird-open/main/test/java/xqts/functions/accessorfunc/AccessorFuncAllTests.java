package xqts.functions.accessorfunc;

import junit.framework.Test;
import junit.framework.TestSuite;
import xqts.functions.accessorfunc.baseurifunc.BaseURIFuncTest;
import xqts.functions.accessorfunc.datafunc.DataFuncTest;
import xqts.functions.accessorfunc.documenturifunc.DocumentURIFuncTest;
import xqts.functions.accessorfunc.nilledfunc.NilledFuncTest;
import xqts.functions.accessorfunc.nodenamefunc.NodeNameFuncTest;
import xqts.functions.accessorfunc.staticbaseurifunc.StaticBaseURIFuncTest;
import xqts.functions.accessorfunc.stringfunc.StringFuncTest;

public class AccessorFuncAllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for xqts.functions.accessorfunc");
        //$JUnit-BEGIN$
        suite.addTestSuite(BaseURIFuncTest.class);
        suite.addTestSuite(DataFuncTest.class);
        suite.addTestSuite(DocumentURIFuncTest.class);
        suite.addTestSuite(NilledFuncTest.class);
        suite.addTestSuite(NodeNameFuncTest.class);
        suite.addTestSuite(StaticBaseURIFuncTest.class);
        suite.addTestSuite(StringFuncTest.class);
        //$JUnit-END$
        return suite;
    }

}
