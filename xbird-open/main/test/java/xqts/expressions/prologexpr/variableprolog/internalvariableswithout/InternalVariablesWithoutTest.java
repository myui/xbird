/*
 * @(#)$Id: XQTSTest.template 946 2006-09-14 03:31:56Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xqts.expressions.prologexpr.variableprolog.internalvariableswithout;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class InternalVariablesWithoutTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[146]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public InternalVariablesWithoutTest() {
    	super(InternalVariablesWithoutTest.class.getName());
        this.xqts = new XQTSTestBase(InternalVariablesWithoutTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testVarDecl001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testVarDecl062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testVardeclerr() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2InternalVariablesWithout13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

}