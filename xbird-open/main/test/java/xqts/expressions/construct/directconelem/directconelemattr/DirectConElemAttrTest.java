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
package xqts.expressions.construct.directconelem.directconelemattr;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class DirectConElemAttrTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[101]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public DirectConElemAttrTest() {
    	super(DirectConElemAttrTest.class.getName());
        this.xqts = new XQTSTestBase(DirectConElemAttrTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrSyntax10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrParent1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNsdecl1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNsdecl2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNspre1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNspre2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNsprein1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrNsprein2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrDistnames1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrDistnames2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrDistnames3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrDistnames4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrContent1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrContent2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrContent3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrContent4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrContent5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrWs1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrWs2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrWs3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrWs4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrWs5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEntref1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEntref2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrCharref1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrEnclexpr7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrId1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrAttrId2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemAttr8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

}