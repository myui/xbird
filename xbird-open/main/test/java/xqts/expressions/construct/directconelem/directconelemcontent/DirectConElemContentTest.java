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
package xqts.expressions.construct.directconelem.directconelemcontent;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class DirectConElemContentTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[103]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public DirectConElemContentTest() {
    	super(DirectConElemContentTest.class.getName());
        this.xqts = new XQTSTestBase(DirectConElemContentTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testConstrContInvalid1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContInvalid2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContInvalid3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContInvalid4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEol1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEol2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEol3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEol4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEntref1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEntref2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEntref3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEntref4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEntref5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContCharref1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContCharref2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContCdata1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContText1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContText2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNested1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNested2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNested3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNested4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEnclexpr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEnclexpr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEnclexpr3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEnclexpr4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContEnclexpr5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNested5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNodeid1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNodeid2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNodeid3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNodeid4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNodeid5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContConstrmod8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContNsmode6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContUripres1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAdjtext1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAdjtext2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAdjtext3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAdjtext4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContDoc1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContBaseuri1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContBaseuri2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContBaseuri3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContParent1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContAttr8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContString1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContData1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContDocument1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testConstrContDocument2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2DirectConElemContent13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

}