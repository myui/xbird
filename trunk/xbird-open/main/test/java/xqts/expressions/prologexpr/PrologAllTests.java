package xqts.expressions.prologexpr;

import junit.framework.Test;
import junit.framework.TestSuite;
import xqts.expressions.prologexpr.baseuriprolog.BaseURIPrologTest;
import xqts.expressions.prologexpr.boundaryspaceprolog.BoundarySpacePrologTest;
import xqts.expressions.prologexpr.collationprolog.CollationPrologTest;
import xqts.expressions.prologexpr.constructionprolog.ConstructionPrologTest;
import xqts.expressions.prologexpr.copynamespacesprolog.CopyNamespacesPrologTest;
import xqts.expressions.prologexpr.defaultnamespaceprolog.DefaultNamespacePrologTest;
import xqts.expressions.prologexpr.defaultorderingprolog.DefaultOrderingPrologTest;
import xqts.expressions.prologexpr.emptyorderprolog.EmptyOrderPrologTest;
import xqts.expressions.prologexpr.functiondeclaration.FunctionPrologTest;
import xqts.expressions.prologexpr.namespaceprolog.NamespacePrologTest;
import xqts.expressions.prologexpr.optiondeclarationprolog.OptionDeclarationPrologTest;
import xqts.expressions.prologexpr.variableprolog.externalvariableswith.ExternalVariablesWithTest;
import xqts.expressions.prologexpr.variableprolog.externalvariableswithout.ExternalVariablesWithoutTest;
import xqts.expressions.prologexpr.variableprolog.internalvariableswith.InternalVariablesWithTest;
import xqts.expressions.prologexpr.variableprolog.internalvariableswithout.InternalVariablesWithoutTest;
import xqts.expressions.prologexpr.versionprolog.VersionPrologTest;

public class PrologAllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for xqts.expressions.prologexpr.baseuriprolog");
        //$JUnit-BEGIN$
        suite.addTestSuite(BaseURIPrologTest.class);
        suite.addTestSuite(BoundarySpacePrologTest.class);
        suite.addTestSuite(CollationPrologTest.class);
        suite.addTestSuite(ConstructionPrologTest.class);
        suite.addTestSuite(CopyNamespacesPrologTest.class);
        suite.addTestSuite(DefaultNamespacePrologTest.class);
        suite.addTestSuite(DefaultOrderingPrologTest.class);
        suite.addTestSuite(EmptyOrderPrologTest.class);
        suite.addTestSuite(FunctionPrologTest.class);
        suite.addTestSuite(NamespacePrologTest.class);
        suite.addTestSuite(OptionDeclarationPrologTest.class);
        suite.addTestSuite(ExternalVariablesWithTest.class);
        suite.addTestSuite(ExternalVariablesWithoutTest.class);
        suite.addTestSuite(InternalVariablesWithTest.class);
        suite.addTestSuite(InternalVariablesWithoutTest.class);
        suite.addTestSuite(VersionPrologTest.class);
        //$JUnit-END$
        return suite;
    }

}
