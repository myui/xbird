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
package xqts.expressions.operators.arithexpr.numericopr.numericmultiply;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class NumericMultiplyTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[22]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public NumericMultiplyTest() {
    	super(NumericMultiplyTest.class.getName());
        this.xqts = new XQTSTestBase(NumericMultiplyTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydec2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydec2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydec2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydec2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydec2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydbl2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydbl2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydbl2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydbl2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplydbl2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyflt2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyflt2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyflt2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyflt2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyflt2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyusht2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyusht2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyusht2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyusht2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyusht2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplypint2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplypint2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplypint2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplypint2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplypint2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyulng2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyulng2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyulng2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyulng2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplyulng2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynpi2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynpi2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynni2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynni2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynni2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynni2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplynni2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpNumericMultiplymix2args9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testKNumericMultiply33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

}