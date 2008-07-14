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
package xqts.expressions.logicexpr;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class LogicExprTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[100]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public LogicExprTest() {
    	super(LogicExprTest.class.getName());
        this.xqts = new XQTSTestBase(LogicExprTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd063() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd064() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd065() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd066() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd067() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd068() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd069() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd070() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd071() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd072() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd073() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd074() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd075() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd076() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd077() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd078() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd079() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd080() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd081() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd082() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd083() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd084() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd089() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd090() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd091() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd092() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd093() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd094() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd095() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd096() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd097() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd098() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd099() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[130]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[131]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[132]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[133]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[134]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[135]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[136]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[137]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[138]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[139]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[140]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[141]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[142]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[143]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[144]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[145]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd146() throws Exception {
        xqts.invokeTest(TEST_PATH + "[146]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd147() throws Exception {
        xqts.invokeTest(TEST_PATH + "[147]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[148]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[149]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[150]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[151]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[152]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[153]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[154]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[155]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[156]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[157]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[158]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalAnd159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[159]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[160]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[161]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[162]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[163]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[164]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[165]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[166]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[167]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[168]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[169]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[170]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[171]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[172]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[173]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[174]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[175]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[176]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[177]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[178]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[179]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[180]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[181]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[182]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[183]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[184]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[185]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[186]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[187]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[188]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[189]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[190]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[191]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[192]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[193]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[194]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[195]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[196]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[197]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[198]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[199]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[200]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[201]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[202]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[203]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[204]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[205]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[206]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[207]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[208]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[209]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[210]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[211]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[212]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[213]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[214]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[215]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[216]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[217]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[218]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[219]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[220]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[221]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr063() throws Exception {
        xqts.invokeTest(TEST_PATH + "[222]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr064() throws Exception {
        xqts.invokeTest(TEST_PATH + "[223]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr065() throws Exception {
        xqts.invokeTest(TEST_PATH + "[224]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr066() throws Exception {
        xqts.invokeTest(TEST_PATH + "[225]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr067() throws Exception {
        xqts.invokeTest(TEST_PATH + "[226]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr068() throws Exception {
        xqts.invokeTest(TEST_PATH + "[227]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr069() throws Exception {
        xqts.invokeTest(TEST_PATH + "[228]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr070() throws Exception {
        xqts.invokeTest(TEST_PATH + "[229]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr071() throws Exception {
        xqts.invokeTest(TEST_PATH + "[230]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr072() throws Exception {
        xqts.invokeTest(TEST_PATH + "[231]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr073() throws Exception {
        xqts.invokeTest(TEST_PATH + "[232]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr074() throws Exception {
        xqts.invokeTest(TEST_PATH + "[233]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr075() throws Exception {
        xqts.invokeTest(TEST_PATH + "[234]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr076() throws Exception {
        xqts.invokeTest(TEST_PATH + "[235]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr077() throws Exception {
        xqts.invokeTest(TEST_PATH + "[236]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr078() throws Exception {
        xqts.invokeTest(TEST_PATH + "[237]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr079() throws Exception {
        xqts.invokeTest(TEST_PATH + "[238]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr080() throws Exception {
        xqts.invokeTest(TEST_PATH + "[239]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr081() throws Exception {
        xqts.invokeTest(TEST_PATH + "[240]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr082() throws Exception {
        xqts.invokeTest(TEST_PATH + "[241]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr083() throws Exception {
        xqts.invokeTest(TEST_PATH + "[242]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr084() throws Exception {
        xqts.invokeTest(TEST_PATH + "[243]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[244]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[245]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[246]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[247]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr089() throws Exception {
        xqts.invokeTest(TEST_PATH + "[248]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr090() throws Exception {
        xqts.invokeTest(TEST_PATH + "[249]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr091() throws Exception {
        xqts.invokeTest(TEST_PATH + "[250]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr092() throws Exception {
        xqts.invokeTest(TEST_PATH + "[251]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr093() throws Exception {
        xqts.invokeTest(TEST_PATH + "[252]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr094() throws Exception {
        xqts.invokeTest(TEST_PATH + "[253]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr095() throws Exception {
        xqts.invokeTest(TEST_PATH + "[254]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr096() throws Exception {
        xqts.invokeTest(TEST_PATH + "[255]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr097() throws Exception {
        xqts.invokeTest(TEST_PATH + "[256]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr098() throws Exception {
        xqts.invokeTest(TEST_PATH + "[257]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr099() throws Exception {
        xqts.invokeTest(TEST_PATH + "[258]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[259]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[260]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[261]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[262]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[263]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[264]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[265]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[266]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[267]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[268]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[269]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[270]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[271]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[272]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[273]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[274]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[275]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[276]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[277]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[278]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[279]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[280]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[281]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[282]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[283]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[284]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[285]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[286]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[287]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[288]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[289]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[290]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[291]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[292]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[293]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[294]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[295]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[296]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[297]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[298]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[299]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[300]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[301]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[302]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[303]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[304]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr146() throws Exception {
        xqts.invokeTest(TEST_PATH + "[305]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr147() throws Exception {
        xqts.invokeTest(TEST_PATH + "[306]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[307]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[308]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[309]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[310]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[311]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[312]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[313]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[314]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[315]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[316]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[317]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpLogicalOr159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[318]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[319]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[320]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[321]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[322]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[323]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[324]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[325]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[326]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[327]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[328]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[329]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[330]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[331]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[332]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[333]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[334]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[335]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[336]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[337]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[338]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[339]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[340]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[341]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[342]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[343]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[344]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[345]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[346]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[347]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[348]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[349]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[350]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[351]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[352]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[353]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[354]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[355]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[356]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr39() throws Exception {
        xqts.invokeTest(TEST_PATH + "[357]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr40() throws Exception {
        xqts.invokeTest(TEST_PATH + "[358]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr41() throws Exception {
        xqts.invokeTest(TEST_PATH + "[359]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr42() throws Exception {
        xqts.invokeTest(TEST_PATH + "[360]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr43() throws Exception {
        xqts.invokeTest(TEST_PATH + "[361]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr44() throws Exception {
        xqts.invokeTest(TEST_PATH + "[362]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr45() throws Exception {
        xqts.invokeTest(TEST_PATH + "[363]");
    }

    @org.junit.Test(timeout=300000)
    public void testKLogicExpr46() throws Exception {
        xqts.invokeTest(TEST_PATH + "[364]");
    }

}