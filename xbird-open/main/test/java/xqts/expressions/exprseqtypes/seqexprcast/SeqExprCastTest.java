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
package xqts.expressions.exprseqtypes.seqexprcast;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class SeqExprCastTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[131]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public SeqExprCastTest() {
    	super(SeqExprCastTest.class.getName());
        this.xqts = new XQTSTestBase(SeqExprCastTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testCasthc1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc39() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc40() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc41() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc42() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testCasthc43() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs063() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs064() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs065() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs066() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs067() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs068() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs069() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs070() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs071() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs072() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs073() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs074() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs075() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs076() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs077() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs078() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs079() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs080() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs081() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs082() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs083() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs084() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[130]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[131]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs089() throws Exception {
        xqts.invokeTest(TEST_PATH + "[132]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs090() throws Exception {
        xqts.invokeTest(TEST_PATH + "[133]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs091() throws Exception {
        xqts.invokeTest(TEST_PATH + "[134]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs092() throws Exception {
        xqts.invokeTest(TEST_PATH + "[135]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs093() throws Exception {
        xqts.invokeTest(TEST_PATH + "[136]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs094() throws Exception {
        xqts.invokeTest(TEST_PATH + "[137]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs095() throws Exception {
        xqts.invokeTest(TEST_PATH + "[138]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs096() throws Exception {
        xqts.invokeTest(TEST_PATH + "[139]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs097() throws Exception {
        xqts.invokeTest(TEST_PATH + "[140]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs098() throws Exception {
        xqts.invokeTest(TEST_PATH + "[141]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs099() throws Exception {
        xqts.invokeTest(TEST_PATH + "[142]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[143]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[144]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[145]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[146]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[147]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[148]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[149]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[150]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[151]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[152]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[153]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[154]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[155]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[156]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[157]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[158]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[159]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[160]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[161]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[162]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[163]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[164]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[165]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[166]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[167]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[168]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[169]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[170]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[171]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[172]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[173]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[174]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[175]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[176]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[177]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[178]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[179]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[180]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[181]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[182]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[183]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[184]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[185]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[186]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[187]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[188]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs146() throws Exception {
        xqts.invokeTest(TEST_PATH + "[189]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs147() throws Exception {
        xqts.invokeTest(TEST_PATH + "[190]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[191]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[192]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[193]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[194]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[195]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[196]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[197]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[198]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[199]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[200]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[201]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[202]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs160() throws Exception {
        xqts.invokeTest(TEST_PATH + "[203]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs161() throws Exception {
        xqts.invokeTest(TEST_PATH + "[204]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs162() throws Exception {
        xqts.invokeTest(TEST_PATH + "[205]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs163() throws Exception {
        xqts.invokeTest(TEST_PATH + "[206]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs164() throws Exception {
        xqts.invokeTest(TEST_PATH + "[207]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs165() throws Exception {
        xqts.invokeTest(TEST_PATH + "[208]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs166() throws Exception {
        xqts.invokeTest(TEST_PATH + "[209]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs167() throws Exception {
        xqts.invokeTest(TEST_PATH + "[210]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs168() throws Exception {
        xqts.invokeTest(TEST_PATH + "[211]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs169() throws Exception {
        xqts.invokeTest(TEST_PATH + "[212]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs170() throws Exception {
        xqts.invokeTest(TEST_PATH + "[213]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs171() throws Exception {
        xqts.invokeTest(TEST_PATH + "[214]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs172() throws Exception {
        xqts.invokeTest(TEST_PATH + "[215]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs173() throws Exception {
        xqts.invokeTest(TEST_PATH + "[216]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs174() throws Exception {
        xqts.invokeTest(TEST_PATH + "[217]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs175() throws Exception {
        xqts.invokeTest(TEST_PATH + "[218]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs176() throws Exception {
        xqts.invokeTest(TEST_PATH + "[219]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs177() throws Exception {
        xqts.invokeTest(TEST_PATH + "[220]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs178() throws Exception {
        xqts.invokeTest(TEST_PATH + "[221]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs179() throws Exception {
        xqts.invokeTest(TEST_PATH + "[222]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs180() throws Exception {
        xqts.invokeTest(TEST_PATH + "[223]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs181() throws Exception {
        xqts.invokeTest(TEST_PATH + "[224]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs182() throws Exception {
        xqts.invokeTest(TEST_PATH + "[225]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs183() throws Exception {
        xqts.invokeTest(TEST_PATH + "[226]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs184() throws Exception {
        xqts.invokeTest(TEST_PATH + "[227]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs185() throws Exception {
        xqts.invokeTest(TEST_PATH + "[228]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs186() throws Exception {
        xqts.invokeTest(TEST_PATH + "[229]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs187() throws Exception {
        xqts.invokeTest(TEST_PATH + "[230]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs188() throws Exception {
        xqts.invokeTest(TEST_PATH + "[231]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs189() throws Exception {
        xqts.invokeTest(TEST_PATH + "[232]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs190() throws Exception {
        xqts.invokeTest(TEST_PATH + "[233]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs191() throws Exception {
        xqts.invokeTest(TEST_PATH + "[234]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs192() throws Exception {
        xqts.invokeTest(TEST_PATH + "[235]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs193() throws Exception {
        xqts.invokeTest(TEST_PATH + "[236]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs194() throws Exception {
        xqts.invokeTest(TEST_PATH + "[237]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs195() throws Exception {
        xqts.invokeTest(TEST_PATH + "[238]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs196() throws Exception {
        xqts.invokeTest(TEST_PATH + "[239]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs197() throws Exception {
        xqts.invokeTest(TEST_PATH + "[240]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs198() throws Exception {
        xqts.invokeTest(TEST_PATH + "[241]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs199() throws Exception {
        xqts.invokeTest(TEST_PATH + "[242]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs200() throws Exception {
        xqts.invokeTest(TEST_PATH + "[243]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs201() throws Exception {
        xqts.invokeTest(TEST_PATH + "[244]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs202() throws Exception {
        xqts.invokeTest(TEST_PATH + "[245]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs203() throws Exception {
        xqts.invokeTest(TEST_PATH + "[246]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs204() throws Exception {
        xqts.invokeTest(TEST_PATH + "[247]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs205() throws Exception {
        xqts.invokeTest(TEST_PATH + "[248]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs206() throws Exception {
        xqts.invokeTest(TEST_PATH + "[249]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs207() throws Exception {
        xqts.invokeTest(TEST_PATH + "[250]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs208() throws Exception {
        xqts.invokeTest(TEST_PATH + "[251]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs209() throws Exception {
        xqts.invokeTest(TEST_PATH + "[252]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs210() throws Exception {
        xqts.invokeTest(TEST_PATH + "[253]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs211() throws Exception {
        xqts.invokeTest(TEST_PATH + "[254]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs212() throws Exception {
        xqts.invokeTest(TEST_PATH + "[255]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs213() throws Exception {
        xqts.invokeTest(TEST_PATH + "[256]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs214() throws Exception {
        xqts.invokeTest(TEST_PATH + "[257]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs215() throws Exception {
        xqts.invokeTest(TEST_PATH + "[258]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs216() throws Exception {
        xqts.invokeTest(TEST_PATH + "[259]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs217() throws Exception {
        xqts.invokeTest(TEST_PATH + "[260]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs218() throws Exception {
        xqts.invokeTest(TEST_PATH + "[261]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs219() throws Exception {
        xqts.invokeTest(TEST_PATH + "[262]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs220() throws Exception {
        xqts.invokeTest(TEST_PATH + "[263]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs221() throws Exception {
        xqts.invokeTest(TEST_PATH + "[264]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs222() throws Exception {
        xqts.invokeTest(TEST_PATH + "[265]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs223() throws Exception {
        xqts.invokeTest(TEST_PATH + "[266]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs224() throws Exception {
        xqts.invokeTest(TEST_PATH + "[267]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs225() throws Exception {
        xqts.invokeTest(TEST_PATH + "[268]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs226() throws Exception {
        xqts.invokeTest(TEST_PATH + "[269]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs227() throws Exception {
        xqts.invokeTest(TEST_PATH + "[270]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs228() throws Exception {
        xqts.invokeTest(TEST_PATH + "[271]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs229() throws Exception {
        xqts.invokeTest(TEST_PATH + "[272]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs230() throws Exception {
        xqts.invokeTest(TEST_PATH + "[273]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs231() throws Exception {
        xqts.invokeTest(TEST_PATH + "[274]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs232() throws Exception {
        xqts.invokeTest(TEST_PATH + "[275]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs233() throws Exception {
        xqts.invokeTest(TEST_PATH + "[276]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs234() throws Exception {
        xqts.invokeTest(TEST_PATH + "[277]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs235() throws Exception {
        xqts.invokeTest(TEST_PATH + "[278]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs236() throws Exception {
        xqts.invokeTest(TEST_PATH + "[279]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs237() throws Exception {
        xqts.invokeTest(TEST_PATH + "[280]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs238() throws Exception {
        xqts.invokeTest(TEST_PATH + "[281]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs239() throws Exception {
        xqts.invokeTest(TEST_PATH + "[282]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs240() throws Exception {
        xqts.invokeTest(TEST_PATH + "[283]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs241() throws Exception {
        xqts.invokeTest(TEST_PATH + "[284]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs242() throws Exception {
        xqts.invokeTest(TEST_PATH + "[285]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs243() throws Exception {
        xqts.invokeTest(TEST_PATH + "[286]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs244() throws Exception {
        xqts.invokeTest(TEST_PATH + "[287]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs245() throws Exception {
        xqts.invokeTest(TEST_PATH + "[288]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs246() throws Exception {
        xqts.invokeTest(TEST_PATH + "[289]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs247() throws Exception {
        xqts.invokeTest(TEST_PATH + "[290]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs248() throws Exception {
        xqts.invokeTest(TEST_PATH + "[291]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs249() throws Exception {
        xqts.invokeTest(TEST_PATH + "[292]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs250() throws Exception {
        xqts.invokeTest(TEST_PATH + "[293]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs251() throws Exception {
        xqts.invokeTest(TEST_PATH + "[294]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs252() throws Exception {
        xqts.invokeTest(TEST_PATH + "[295]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs253() throws Exception {
        xqts.invokeTest(TEST_PATH + "[296]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs254() throws Exception {
        xqts.invokeTest(TEST_PATH + "[297]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs255() throws Exception {
        xqts.invokeTest(TEST_PATH + "[298]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs256() throws Exception {
        xqts.invokeTest(TEST_PATH + "[299]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs257() throws Exception {
        xqts.invokeTest(TEST_PATH + "[300]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs258() throws Exception {
        xqts.invokeTest(TEST_PATH + "[301]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs259() throws Exception {
        xqts.invokeTest(TEST_PATH + "[302]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs260() throws Exception {
        xqts.invokeTest(TEST_PATH + "[303]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs261() throws Exception {
        xqts.invokeTest(TEST_PATH + "[304]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs262() throws Exception {
        xqts.invokeTest(TEST_PATH + "[305]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs263() throws Exception {
        xqts.invokeTest(TEST_PATH + "[306]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs264() throws Exception {
        xqts.invokeTest(TEST_PATH + "[307]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs265() throws Exception {
        xqts.invokeTest(TEST_PATH + "[308]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs266() throws Exception {
        xqts.invokeTest(TEST_PATH + "[309]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs267() throws Exception {
        xqts.invokeTest(TEST_PATH + "[310]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs268() throws Exception {
        xqts.invokeTest(TEST_PATH + "[311]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs269() throws Exception {
        xqts.invokeTest(TEST_PATH + "[312]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs270() throws Exception {
        xqts.invokeTest(TEST_PATH + "[313]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs271() throws Exception {
        xqts.invokeTest(TEST_PATH + "[314]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs272() throws Exception {
        xqts.invokeTest(TEST_PATH + "[315]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs273() throws Exception {
        xqts.invokeTest(TEST_PATH + "[316]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs274() throws Exception {
        xqts.invokeTest(TEST_PATH + "[317]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs275() throws Exception {
        xqts.invokeTest(TEST_PATH + "[318]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs276() throws Exception {
        xqts.invokeTest(TEST_PATH + "[319]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs277() throws Exception {
        xqts.invokeTest(TEST_PATH + "[320]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs278() throws Exception {
        xqts.invokeTest(TEST_PATH + "[321]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs279() throws Exception {
        xqts.invokeTest(TEST_PATH + "[322]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs280() throws Exception {
        xqts.invokeTest(TEST_PATH + "[323]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs281() throws Exception {
        xqts.invokeTest(TEST_PATH + "[324]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs282() throws Exception {
        xqts.invokeTest(TEST_PATH + "[325]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs283() throws Exception {
        xqts.invokeTest(TEST_PATH + "[326]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs284() throws Exception {
        xqts.invokeTest(TEST_PATH + "[327]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs285() throws Exception {
        xqts.invokeTest(TEST_PATH + "[328]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs286() throws Exception {
        xqts.invokeTest(TEST_PATH + "[329]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs287() throws Exception {
        xqts.invokeTest(TEST_PATH + "[330]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs288() throws Exception {
        xqts.invokeTest(TEST_PATH + "[331]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs289() throws Exception {
        xqts.invokeTest(TEST_PATH + "[332]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs290() throws Exception {
        xqts.invokeTest(TEST_PATH + "[333]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs291() throws Exception {
        xqts.invokeTest(TEST_PATH + "[334]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs292() throws Exception {
        xqts.invokeTest(TEST_PATH + "[335]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs293() throws Exception {
        xqts.invokeTest(TEST_PATH + "[336]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs294() throws Exception {
        xqts.invokeTest(TEST_PATH + "[337]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs295() throws Exception {
        xqts.invokeTest(TEST_PATH + "[338]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs296() throws Exception {
        xqts.invokeTest(TEST_PATH + "[339]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs297() throws Exception {
        xqts.invokeTest(TEST_PATH + "[340]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs298() throws Exception {
        xqts.invokeTest(TEST_PATH + "[341]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs299() throws Exception {
        xqts.invokeTest(TEST_PATH + "[342]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs300() throws Exception {
        xqts.invokeTest(TEST_PATH + "[343]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs301() throws Exception {
        xqts.invokeTest(TEST_PATH + "[344]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs302() throws Exception {
        xqts.invokeTest(TEST_PATH + "[345]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs303() throws Exception {
        xqts.invokeTest(TEST_PATH + "[346]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs304() throws Exception {
        xqts.invokeTest(TEST_PATH + "[347]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs305() throws Exception {
        xqts.invokeTest(TEST_PATH + "[348]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs306() throws Exception {
        xqts.invokeTest(TEST_PATH + "[349]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs307() throws Exception {
        xqts.invokeTest(TEST_PATH + "[350]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs308() throws Exception {
        xqts.invokeTest(TEST_PATH + "[351]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs309() throws Exception {
        xqts.invokeTest(TEST_PATH + "[352]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs310() throws Exception {
        xqts.invokeTest(TEST_PATH + "[353]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs311() throws Exception {
        xqts.invokeTest(TEST_PATH + "[354]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs312() throws Exception {
        xqts.invokeTest(TEST_PATH + "[355]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs313() throws Exception {
        xqts.invokeTest(TEST_PATH + "[356]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs314() throws Exception {
        xqts.invokeTest(TEST_PATH + "[357]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs315() throws Exception {
        xqts.invokeTest(TEST_PATH + "[358]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs316() throws Exception {
        xqts.invokeTest(TEST_PATH + "[359]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs317() throws Exception {
        xqts.invokeTest(TEST_PATH + "[360]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs318() throws Exception {
        xqts.invokeTest(TEST_PATH + "[361]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs319() throws Exception {
        xqts.invokeTest(TEST_PATH + "[362]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs320() throws Exception {
        xqts.invokeTest(TEST_PATH + "[363]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs321() throws Exception {
        xqts.invokeTest(TEST_PATH + "[364]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs322() throws Exception {
        xqts.invokeTest(TEST_PATH + "[365]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs323() throws Exception {
        xqts.invokeTest(TEST_PATH + "[366]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs324() throws Exception {
        xqts.invokeTest(TEST_PATH + "[367]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs325() throws Exception {
        xqts.invokeTest(TEST_PATH + "[368]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs326() throws Exception {
        xqts.invokeTest(TEST_PATH + "[369]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs327() throws Exception {
        xqts.invokeTest(TEST_PATH + "[370]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs328() throws Exception {
        xqts.invokeTest(TEST_PATH + "[371]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs329() throws Exception {
        xqts.invokeTest(TEST_PATH + "[372]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs330() throws Exception {
        xqts.invokeTest(TEST_PATH + "[373]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs331() throws Exception {
        xqts.invokeTest(TEST_PATH + "[374]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs332() throws Exception {
        xqts.invokeTest(TEST_PATH + "[375]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs333() throws Exception {
        xqts.invokeTest(TEST_PATH + "[376]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs334() throws Exception {
        xqts.invokeTest(TEST_PATH + "[377]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs335() throws Exception {
        xqts.invokeTest(TEST_PATH + "[378]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs336() throws Exception {
        xqts.invokeTest(TEST_PATH + "[379]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs337() throws Exception {
        xqts.invokeTest(TEST_PATH + "[380]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs338() throws Exception {
        xqts.invokeTest(TEST_PATH + "[381]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs339() throws Exception {
        xqts.invokeTest(TEST_PATH + "[382]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs340() throws Exception {
        xqts.invokeTest(TEST_PATH + "[383]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs341() throws Exception {
        xqts.invokeTest(TEST_PATH + "[384]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs342() throws Exception {
        xqts.invokeTest(TEST_PATH + "[385]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs343() throws Exception {
        xqts.invokeTest(TEST_PATH + "[386]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs344() throws Exception {
        xqts.invokeTest(TEST_PATH + "[387]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs345() throws Exception {
        xqts.invokeTest(TEST_PATH + "[388]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs346() throws Exception {
        xqts.invokeTest(TEST_PATH + "[389]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs347() throws Exception {
        xqts.invokeTest(TEST_PATH + "[390]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs348() throws Exception {
        xqts.invokeTest(TEST_PATH + "[391]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs349() throws Exception {
        xqts.invokeTest(TEST_PATH + "[392]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs350() throws Exception {
        xqts.invokeTest(TEST_PATH + "[393]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs351() throws Exception {
        xqts.invokeTest(TEST_PATH + "[394]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs352() throws Exception {
        xqts.invokeTest(TEST_PATH + "[395]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs353() throws Exception {
        xqts.invokeTest(TEST_PATH + "[396]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs354() throws Exception {
        xqts.invokeTest(TEST_PATH + "[397]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs355() throws Exception {
        xqts.invokeTest(TEST_PATH + "[398]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs356() throws Exception {
        xqts.invokeTest(TEST_PATH + "[399]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs357() throws Exception {
        xqts.invokeTest(TEST_PATH + "[400]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs358() throws Exception {
        xqts.invokeTest(TEST_PATH + "[401]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs359() throws Exception {
        xqts.invokeTest(TEST_PATH + "[402]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs360() throws Exception {
        xqts.invokeTest(TEST_PATH + "[403]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs361() throws Exception {
        xqts.invokeTest(TEST_PATH + "[404]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs362() throws Exception {
        xqts.invokeTest(TEST_PATH + "[405]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs363() throws Exception {
        xqts.invokeTest(TEST_PATH + "[406]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs364() throws Exception {
        xqts.invokeTest(TEST_PATH + "[407]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs365() throws Exception {
        xqts.invokeTest(TEST_PATH + "[408]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs366() throws Exception {
        xqts.invokeTest(TEST_PATH + "[409]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs367() throws Exception {
        xqts.invokeTest(TEST_PATH + "[410]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs368() throws Exception {
        xqts.invokeTest(TEST_PATH + "[411]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs369() throws Exception {
        xqts.invokeTest(TEST_PATH + "[412]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs370() throws Exception {
        xqts.invokeTest(TEST_PATH + "[413]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs371() throws Exception {
        xqts.invokeTest(TEST_PATH + "[414]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs372() throws Exception {
        xqts.invokeTest(TEST_PATH + "[415]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs373() throws Exception {
        xqts.invokeTest(TEST_PATH + "[416]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs374() throws Exception {
        xqts.invokeTest(TEST_PATH + "[417]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs375() throws Exception {
        xqts.invokeTest(TEST_PATH + "[418]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs376() throws Exception {
        xqts.invokeTest(TEST_PATH + "[419]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs377() throws Exception {
        xqts.invokeTest(TEST_PATH + "[420]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs378() throws Exception {
        xqts.invokeTest(TEST_PATH + "[421]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs379() throws Exception {
        xqts.invokeTest(TEST_PATH + "[422]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs380() throws Exception {
        xqts.invokeTest(TEST_PATH + "[423]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs381() throws Exception {
        xqts.invokeTest(TEST_PATH + "[424]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs382() throws Exception {
        xqts.invokeTest(TEST_PATH + "[425]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs383() throws Exception {
        xqts.invokeTest(TEST_PATH + "[426]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs384() throws Exception {
        xqts.invokeTest(TEST_PATH + "[427]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs385() throws Exception {
        xqts.invokeTest(TEST_PATH + "[428]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs386() throws Exception {
        xqts.invokeTest(TEST_PATH + "[429]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs387() throws Exception {
        xqts.invokeTest(TEST_PATH + "[430]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs388() throws Exception {
        xqts.invokeTest(TEST_PATH + "[431]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs389() throws Exception {
        xqts.invokeTest(TEST_PATH + "[432]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs390() throws Exception {
        xqts.invokeTest(TEST_PATH + "[433]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs391() throws Exception {
        xqts.invokeTest(TEST_PATH + "[434]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs392() throws Exception {
        xqts.invokeTest(TEST_PATH + "[435]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs393() throws Exception {
        xqts.invokeTest(TEST_PATH + "[436]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs394() throws Exception {
        xqts.invokeTest(TEST_PATH + "[437]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs395() throws Exception {
        xqts.invokeTest(TEST_PATH + "[438]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs396() throws Exception {
        xqts.invokeTest(TEST_PATH + "[439]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs397() throws Exception {
        xqts.invokeTest(TEST_PATH + "[440]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs398() throws Exception {
        xqts.invokeTest(TEST_PATH + "[441]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs399() throws Exception {
        xqts.invokeTest(TEST_PATH + "[442]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs400() throws Exception {
        xqts.invokeTest(TEST_PATH + "[443]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs401() throws Exception {
        xqts.invokeTest(TEST_PATH + "[444]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs402() throws Exception {
        xqts.invokeTest(TEST_PATH + "[445]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs403() throws Exception {
        xqts.invokeTest(TEST_PATH + "[446]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs404() throws Exception {
        xqts.invokeTest(TEST_PATH + "[447]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs405() throws Exception {
        xqts.invokeTest(TEST_PATH + "[448]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs406() throws Exception {
        xqts.invokeTest(TEST_PATH + "[449]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs407() throws Exception {
        xqts.invokeTest(TEST_PATH + "[450]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs408() throws Exception {
        xqts.invokeTest(TEST_PATH + "[451]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs409() throws Exception {
        xqts.invokeTest(TEST_PATH + "[452]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs410() throws Exception {
        xqts.invokeTest(TEST_PATH + "[453]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs411() throws Exception {
        xqts.invokeTest(TEST_PATH + "[454]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs412() throws Exception {
        xqts.invokeTest(TEST_PATH + "[455]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs413() throws Exception {
        xqts.invokeTest(TEST_PATH + "[456]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs414() throws Exception {
        xqts.invokeTest(TEST_PATH + "[457]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs415() throws Exception {
        xqts.invokeTest(TEST_PATH + "[458]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs416() throws Exception {
        xqts.invokeTest(TEST_PATH + "[459]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs417() throws Exception {
        xqts.invokeTest(TEST_PATH + "[460]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs418() throws Exception {
        xqts.invokeTest(TEST_PATH + "[461]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs419() throws Exception {
        xqts.invokeTest(TEST_PATH + "[462]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs420() throws Exception {
        xqts.invokeTest(TEST_PATH + "[463]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs421() throws Exception {
        xqts.invokeTest(TEST_PATH + "[464]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs422() throws Exception {
        xqts.invokeTest(TEST_PATH + "[465]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs423() throws Exception {
        xqts.invokeTest(TEST_PATH + "[466]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs424() throws Exception {
        xqts.invokeTest(TEST_PATH + "[467]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs425() throws Exception {
        xqts.invokeTest(TEST_PATH + "[468]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs426() throws Exception {
        xqts.invokeTest(TEST_PATH + "[469]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs427() throws Exception {
        xqts.invokeTest(TEST_PATH + "[470]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs428() throws Exception {
        xqts.invokeTest(TEST_PATH + "[471]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs429() throws Exception {
        xqts.invokeTest(TEST_PATH + "[472]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs430() throws Exception {
        xqts.invokeTest(TEST_PATH + "[473]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs431() throws Exception {
        xqts.invokeTest(TEST_PATH + "[474]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs432() throws Exception {
        xqts.invokeTest(TEST_PATH + "[475]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs433() throws Exception {
        xqts.invokeTest(TEST_PATH + "[476]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs434() throws Exception {
        xqts.invokeTest(TEST_PATH + "[477]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs435() throws Exception {
        xqts.invokeTest(TEST_PATH + "[478]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs436() throws Exception {
        xqts.invokeTest(TEST_PATH + "[479]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs437() throws Exception {
        xqts.invokeTest(TEST_PATH + "[480]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs438() throws Exception {
        xqts.invokeTest(TEST_PATH + "[481]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs439() throws Exception {
        xqts.invokeTest(TEST_PATH + "[482]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs440() throws Exception {
        xqts.invokeTest(TEST_PATH + "[483]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs441() throws Exception {
        xqts.invokeTest(TEST_PATH + "[484]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs442() throws Exception {
        xqts.invokeTest(TEST_PATH + "[485]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs443() throws Exception {
        xqts.invokeTest(TEST_PATH + "[486]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs444() throws Exception {
        xqts.invokeTest(TEST_PATH + "[487]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs445() throws Exception {
        xqts.invokeTest(TEST_PATH + "[488]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs446() throws Exception {
        xqts.invokeTest(TEST_PATH + "[489]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs447() throws Exception {
        xqts.invokeTest(TEST_PATH + "[490]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs448() throws Exception {
        xqts.invokeTest(TEST_PATH + "[491]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs449() throws Exception {
        xqts.invokeTest(TEST_PATH + "[492]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs450() throws Exception {
        xqts.invokeTest(TEST_PATH + "[493]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs451() throws Exception {
        xqts.invokeTest(TEST_PATH + "[494]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs452() throws Exception {
        xqts.invokeTest(TEST_PATH + "[495]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs453() throws Exception {
        xqts.invokeTest(TEST_PATH + "[496]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs454() throws Exception {
        xqts.invokeTest(TEST_PATH + "[497]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs455() throws Exception {
        xqts.invokeTest(TEST_PATH + "[498]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs456() throws Exception {
        xqts.invokeTest(TEST_PATH + "[499]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs457() throws Exception {
        xqts.invokeTest(TEST_PATH + "[500]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs458() throws Exception {
        xqts.invokeTest(TEST_PATH + "[501]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs459() throws Exception {
        xqts.invokeTest(TEST_PATH + "[502]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs460() throws Exception {
        xqts.invokeTest(TEST_PATH + "[503]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs461() throws Exception {
        xqts.invokeTest(TEST_PATH + "[504]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs462() throws Exception {
        xqts.invokeTest(TEST_PATH + "[505]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs463() throws Exception {
        xqts.invokeTest(TEST_PATH + "[506]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs464() throws Exception {
        xqts.invokeTest(TEST_PATH + "[507]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs465() throws Exception {
        xqts.invokeTest(TEST_PATH + "[508]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs466() throws Exception {
        xqts.invokeTest(TEST_PATH + "[509]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs467() throws Exception {
        xqts.invokeTest(TEST_PATH + "[510]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs468() throws Exception {
        xqts.invokeTest(TEST_PATH + "[511]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs469() throws Exception {
        xqts.invokeTest(TEST_PATH + "[512]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs470() throws Exception {
        xqts.invokeTest(TEST_PATH + "[513]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs471() throws Exception {
        xqts.invokeTest(TEST_PATH + "[514]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs472() throws Exception {
        xqts.invokeTest(TEST_PATH + "[515]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs473() throws Exception {
        xqts.invokeTest(TEST_PATH + "[516]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs474() throws Exception {
        xqts.invokeTest(TEST_PATH + "[517]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs475() throws Exception {
        xqts.invokeTest(TEST_PATH + "[518]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs476() throws Exception {
        xqts.invokeTest(TEST_PATH + "[519]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs477() throws Exception {
        xqts.invokeTest(TEST_PATH + "[520]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs478() throws Exception {
        xqts.invokeTest(TEST_PATH + "[521]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs479() throws Exception {
        xqts.invokeTest(TEST_PATH + "[522]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs480() throws Exception {
        xqts.invokeTest(TEST_PATH + "[523]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs481() throws Exception {
        xqts.invokeTest(TEST_PATH + "[524]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs482() throws Exception {
        xqts.invokeTest(TEST_PATH + "[525]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs483() throws Exception {
        xqts.invokeTest(TEST_PATH + "[526]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs484() throws Exception {
        xqts.invokeTest(TEST_PATH + "[527]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs485() throws Exception {
        xqts.invokeTest(TEST_PATH + "[528]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs486() throws Exception {
        xqts.invokeTest(TEST_PATH + "[529]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs487() throws Exception {
        xqts.invokeTest(TEST_PATH + "[530]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs488() throws Exception {
        xqts.invokeTest(TEST_PATH + "[531]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs489() throws Exception {
        xqts.invokeTest(TEST_PATH + "[532]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs490() throws Exception {
        xqts.invokeTest(TEST_PATH + "[533]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs491() throws Exception {
        xqts.invokeTest(TEST_PATH + "[534]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs492() throws Exception {
        xqts.invokeTest(TEST_PATH + "[535]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs493() throws Exception {
        xqts.invokeTest(TEST_PATH + "[536]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs494() throws Exception {
        xqts.invokeTest(TEST_PATH + "[537]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs495() throws Exception {
        xqts.invokeTest(TEST_PATH + "[538]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs496() throws Exception {
        xqts.invokeTest(TEST_PATH + "[539]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs497() throws Exception {
        xqts.invokeTest(TEST_PATH + "[540]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs498() throws Exception {
        xqts.invokeTest(TEST_PATH + "[541]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs499() throws Exception {
        xqts.invokeTest(TEST_PATH + "[542]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs500() throws Exception {
        xqts.invokeTest(TEST_PATH + "[543]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs501() throws Exception {
        xqts.invokeTest(TEST_PATH + "[544]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs502() throws Exception {
        xqts.invokeTest(TEST_PATH + "[545]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs503() throws Exception {
        xqts.invokeTest(TEST_PATH + "[546]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs504() throws Exception {
        xqts.invokeTest(TEST_PATH + "[547]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs505() throws Exception {
        xqts.invokeTest(TEST_PATH + "[548]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs506() throws Exception {
        xqts.invokeTest(TEST_PATH + "[549]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs507() throws Exception {
        xqts.invokeTest(TEST_PATH + "[550]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs508() throws Exception {
        xqts.invokeTest(TEST_PATH + "[551]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs509() throws Exception {
        xqts.invokeTest(TEST_PATH + "[552]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs510() throws Exception {
        xqts.invokeTest(TEST_PATH + "[553]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs511() throws Exception {
        xqts.invokeTest(TEST_PATH + "[554]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs512() throws Exception {
        xqts.invokeTest(TEST_PATH + "[555]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs513() throws Exception {
        xqts.invokeTest(TEST_PATH + "[556]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs514() throws Exception {
        xqts.invokeTest(TEST_PATH + "[557]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs515() throws Exception {
        xqts.invokeTest(TEST_PATH + "[558]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs516() throws Exception {
        xqts.invokeTest(TEST_PATH + "[559]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs517() throws Exception {
        xqts.invokeTest(TEST_PATH + "[560]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs518() throws Exception {
        xqts.invokeTest(TEST_PATH + "[561]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs519() throws Exception {
        xqts.invokeTest(TEST_PATH + "[562]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs520() throws Exception {
        xqts.invokeTest(TEST_PATH + "[563]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs521() throws Exception {
        xqts.invokeTest(TEST_PATH + "[564]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs522() throws Exception {
        xqts.invokeTest(TEST_PATH + "[565]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs523() throws Exception {
        xqts.invokeTest(TEST_PATH + "[566]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs524() throws Exception {
        xqts.invokeTest(TEST_PATH + "[567]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs525() throws Exception {
        xqts.invokeTest(TEST_PATH + "[568]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs526() throws Exception {
        xqts.invokeTest(TEST_PATH + "[569]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs527() throws Exception {
        xqts.invokeTest(TEST_PATH + "[570]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs528() throws Exception {
        xqts.invokeTest(TEST_PATH + "[571]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs529() throws Exception {
        xqts.invokeTest(TEST_PATH + "[572]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs530() throws Exception {
        xqts.invokeTest(TEST_PATH + "[573]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs531() throws Exception {
        xqts.invokeTest(TEST_PATH + "[574]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs532() throws Exception {
        xqts.invokeTest(TEST_PATH + "[575]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs533() throws Exception {
        xqts.invokeTest(TEST_PATH + "[576]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs534() throws Exception {
        xqts.invokeTest(TEST_PATH + "[577]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs535() throws Exception {
        xqts.invokeTest(TEST_PATH + "[578]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs536() throws Exception {
        xqts.invokeTest(TEST_PATH + "[579]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs537() throws Exception {
        xqts.invokeTest(TEST_PATH + "[580]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs538() throws Exception {
        xqts.invokeTest(TEST_PATH + "[581]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs539() throws Exception {
        xqts.invokeTest(TEST_PATH + "[582]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs540() throws Exception {
        xqts.invokeTest(TEST_PATH + "[583]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs541() throws Exception {
        xqts.invokeTest(TEST_PATH + "[584]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs542() throws Exception {
        xqts.invokeTest(TEST_PATH + "[585]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs543() throws Exception {
        xqts.invokeTest(TEST_PATH + "[586]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs544() throws Exception {
        xqts.invokeTest(TEST_PATH + "[587]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs545() throws Exception {
        xqts.invokeTest(TEST_PATH + "[588]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs546() throws Exception {
        xqts.invokeTest(TEST_PATH + "[589]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs547() throws Exception {
        xqts.invokeTest(TEST_PATH + "[590]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs548() throws Exception {
        xqts.invokeTest(TEST_PATH + "[591]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs549() throws Exception {
        xqts.invokeTest(TEST_PATH + "[592]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs550() throws Exception {
        xqts.invokeTest(TEST_PATH + "[593]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs551() throws Exception {
        xqts.invokeTest(TEST_PATH + "[594]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs552() throws Exception {
        xqts.invokeTest(TEST_PATH + "[595]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs553() throws Exception {
        xqts.invokeTest(TEST_PATH + "[596]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs554() throws Exception {
        xqts.invokeTest(TEST_PATH + "[597]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs555() throws Exception {
        xqts.invokeTest(TEST_PATH + "[598]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs556() throws Exception {
        xqts.invokeTest(TEST_PATH + "[599]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs557() throws Exception {
        xqts.invokeTest(TEST_PATH + "[600]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs558() throws Exception {
        xqts.invokeTest(TEST_PATH + "[601]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs559() throws Exception {
        xqts.invokeTest(TEST_PATH + "[602]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs560() throws Exception {
        xqts.invokeTest(TEST_PATH + "[603]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs561() throws Exception {
        xqts.invokeTest(TEST_PATH + "[604]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs562() throws Exception {
        xqts.invokeTest(TEST_PATH + "[605]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs563() throws Exception {
        xqts.invokeTest(TEST_PATH + "[606]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs564() throws Exception {
        xqts.invokeTest(TEST_PATH + "[607]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs565() throws Exception {
        xqts.invokeTest(TEST_PATH + "[608]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs566() throws Exception {
        xqts.invokeTest(TEST_PATH + "[609]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs567() throws Exception {
        xqts.invokeTest(TEST_PATH + "[610]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs568() throws Exception {
        xqts.invokeTest(TEST_PATH + "[611]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs569() throws Exception {
        xqts.invokeTest(TEST_PATH + "[612]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs570() throws Exception {
        xqts.invokeTest(TEST_PATH + "[613]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs571() throws Exception {
        xqts.invokeTest(TEST_PATH + "[614]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs572() throws Exception {
        xqts.invokeTest(TEST_PATH + "[615]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs573() throws Exception {
        xqts.invokeTest(TEST_PATH + "[616]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs574() throws Exception {
        xqts.invokeTest(TEST_PATH + "[617]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs575() throws Exception {
        xqts.invokeTest(TEST_PATH + "[618]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs576() throws Exception {
        xqts.invokeTest(TEST_PATH + "[619]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs577() throws Exception {
        xqts.invokeTest(TEST_PATH + "[620]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs578() throws Exception {
        xqts.invokeTest(TEST_PATH + "[621]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs579() throws Exception {
        xqts.invokeTest(TEST_PATH + "[622]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs580() throws Exception {
        xqts.invokeTest(TEST_PATH + "[623]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs581() throws Exception {
        xqts.invokeTest(TEST_PATH + "[624]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs582() throws Exception {
        xqts.invokeTest(TEST_PATH + "[625]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs583() throws Exception {
        xqts.invokeTest(TEST_PATH + "[626]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs584() throws Exception {
        xqts.invokeTest(TEST_PATH + "[627]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs585() throws Exception {
        xqts.invokeTest(TEST_PATH + "[628]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs586() throws Exception {
        xqts.invokeTest(TEST_PATH + "[629]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs587() throws Exception {
        xqts.invokeTest(TEST_PATH + "[630]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs588() throws Exception {
        xqts.invokeTest(TEST_PATH + "[631]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs589() throws Exception {
        xqts.invokeTest(TEST_PATH + "[632]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs590() throws Exception {
        xqts.invokeTest(TEST_PATH + "[633]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs591() throws Exception {
        xqts.invokeTest(TEST_PATH + "[634]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs592() throws Exception {
        xqts.invokeTest(TEST_PATH + "[635]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs593() throws Exception {
        xqts.invokeTest(TEST_PATH + "[636]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs594() throws Exception {
        xqts.invokeTest(TEST_PATH + "[637]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs595() throws Exception {
        xqts.invokeTest(TEST_PATH + "[638]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs596() throws Exception {
        xqts.invokeTest(TEST_PATH + "[639]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs597() throws Exception {
        xqts.invokeTest(TEST_PATH + "[640]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs598() throws Exception {
        xqts.invokeTest(TEST_PATH + "[641]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs599() throws Exception {
        xqts.invokeTest(TEST_PATH + "[642]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs600() throws Exception {
        xqts.invokeTest(TEST_PATH + "[643]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs601() throws Exception {
        xqts.invokeTest(TEST_PATH + "[644]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs602() throws Exception {
        xqts.invokeTest(TEST_PATH + "[645]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs603() throws Exception {
        xqts.invokeTest(TEST_PATH + "[646]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs604() throws Exception {
        xqts.invokeTest(TEST_PATH + "[647]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs605() throws Exception {
        xqts.invokeTest(TEST_PATH + "[648]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs606() throws Exception {
        xqts.invokeTest(TEST_PATH + "[649]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs607() throws Exception {
        xqts.invokeTest(TEST_PATH + "[650]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs608() throws Exception {
        xqts.invokeTest(TEST_PATH + "[651]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs609() throws Exception {
        xqts.invokeTest(TEST_PATH + "[652]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs610() throws Exception {
        xqts.invokeTest(TEST_PATH + "[653]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs611() throws Exception {
        xqts.invokeTest(TEST_PATH + "[654]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs612() throws Exception {
        xqts.invokeTest(TEST_PATH + "[655]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs613() throws Exception {
        xqts.invokeTest(TEST_PATH + "[656]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs614() throws Exception {
        xqts.invokeTest(TEST_PATH + "[657]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs615() throws Exception {
        xqts.invokeTest(TEST_PATH + "[658]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs616() throws Exception {
        xqts.invokeTest(TEST_PATH + "[659]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs617() throws Exception {
        xqts.invokeTest(TEST_PATH + "[660]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs618() throws Exception {
        xqts.invokeTest(TEST_PATH + "[661]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs619() throws Exception {
        xqts.invokeTest(TEST_PATH + "[662]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs620() throws Exception {
        xqts.invokeTest(TEST_PATH + "[663]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs621() throws Exception {
        xqts.invokeTest(TEST_PATH + "[664]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs622() throws Exception {
        xqts.invokeTest(TEST_PATH + "[665]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs623() throws Exception {
        xqts.invokeTest(TEST_PATH + "[666]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs624() throws Exception {
        xqts.invokeTest(TEST_PATH + "[667]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs625() throws Exception {
        xqts.invokeTest(TEST_PATH + "[668]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs626() throws Exception {
        xqts.invokeTest(TEST_PATH + "[669]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs627() throws Exception {
        xqts.invokeTest(TEST_PATH + "[670]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs628() throws Exception {
        xqts.invokeTest(TEST_PATH + "[671]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs629() throws Exception {
        xqts.invokeTest(TEST_PATH + "[672]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs630() throws Exception {
        xqts.invokeTest(TEST_PATH + "[673]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs631() throws Exception {
        xqts.invokeTest(TEST_PATH + "[674]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs632() throws Exception {
        xqts.invokeTest(TEST_PATH + "[675]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs633() throws Exception {
        xqts.invokeTest(TEST_PATH + "[676]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs634() throws Exception {
        xqts.invokeTest(TEST_PATH + "[677]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs635() throws Exception {
        xqts.invokeTest(TEST_PATH + "[678]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs636() throws Exception {
        xqts.invokeTest(TEST_PATH + "[679]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs637() throws Exception {
        xqts.invokeTest(TEST_PATH + "[680]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs638() throws Exception {
        xqts.invokeTest(TEST_PATH + "[681]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs639() throws Exception {
        xqts.invokeTest(TEST_PATH + "[682]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs640() throws Exception {
        xqts.invokeTest(TEST_PATH + "[683]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs641() throws Exception {
        xqts.invokeTest(TEST_PATH + "[684]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs642() throws Exception {
        xqts.invokeTest(TEST_PATH + "[685]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs643() throws Exception {
        xqts.invokeTest(TEST_PATH + "[686]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs644() throws Exception {
        xqts.invokeTest(TEST_PATH + "[687]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs645() throws Exception {
        xqts.invokeTest(TEST_PATH + "[688]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs646() throws Exception {
        xqts.invokeTest(TEST_PATH + "[689]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs647() throws Exception {
        xqts.invokeTest(TEST_PATH + "[690]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs648() throws Exception {
        xqts.invokeTest(TEST_PATH + "[691]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs649() throws Exception {
        xqts.invokeTest(TEST_PATH + "[692]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs650() throws Exception {
        xqts.invokeTest(TEST_PATH + "[693]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs651() throws Exception {
        xqts.invokeTest(TEST_PATH + "[694]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs652() throws Exception {
        xqts.invokeTest(TEST_PATH + "[695]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs653() throws Exception {
        xqts.invokeTest(TEST_PATH + "[696]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs654() throws Exception {
        xqts.invokeTest(TEST_PATH + "[697]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs655() throws Exception {
        xqts.invokeTest(TEST_PATH + "[698]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs656() throws Exception {
        xqts.invokeTest(TEST_PATH + "[699]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs657() throws Exception {
        xqts.invokeTest(TEST_PATH + "[700]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs658() throws Exception {
        xqts.invokeTest(TEST_PATH + "[701]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs659() throws Exception {
        xqts.invokeTest(TEST_PATH + "[702]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs660() throws Exception {
        xqts.invokeTest(TEST_PATH + "[703]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs661() throws Exception {
        xqts.invokeTest(TEST_PATH + "[704]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs662() throws Exception {
        xqts.invokeTest(TEST_PATH + "[705]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs663() throws Exception {
        xqts.invokeTest(TEST_PATH + "[706]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs664() throws Exception {
        xqts.invokeTest(TEST_PATH + "[707]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs665() throws Exception {
        xqts.invokeTest(TEST_PATH + "[708]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs666() throws Exception {
        xqts.invokeTest(TEST_PATH + "[709]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs667() throws Exception {
        xqts.invokeTest(TEST_PATH + "[710]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs668() throws Exception {
        xqts.invokeTest(TEST_PATH + "[711]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs669() throws Exception {
        xqts.invokeTest(TEST_PATH + "[712]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs670() throws Exception {
        xqts.invokeTest(TEST_PATH + "[713]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs671() throws Exception {
        xqts.invokeTest(TEST_PATH + "[714]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastAs672() throws Exception {
        xqts.invokeTest(TEST_PATH + "[715]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastFOCA0001_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[716]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastFOCA0003_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[717]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[718]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[719]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[720]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[721]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[722]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[723]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[724]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[725]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[726]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[727]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[728]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[729]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[730]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[731]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[732]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[733]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[734]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[735]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[736]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[737]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[738]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[739]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[740]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[741]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[742]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[743]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[744]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[745]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[746]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[747]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[748]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[749]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[750]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[751]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[752]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[753]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[754]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[755]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast39() throws Exception {
        xqts.invokeTest(TEST_PATH + "[756]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast40() throws Exception {
        xqts.invokeTest(TEST_PATH + "[757]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast41() throws Exception {
        xqts.invokeTest(TEST_PATH + "[758]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast42() throws Exception {
        xqts.invokeTest(TEST_PATH + "[759]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast43() throws Exception {
        xqts.invokeTest(TEST_PATH + "[760]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast44() throws Exception {
        xqts.invokeTest(TEST_PATH + "[761]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast45() throws Exception {
        xqts.invokeTest(TEST_PATH + "[762]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast46() throws Exception {
        xqts.invokeTest(TEST_PATH + "[763]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast47() throws Exception {
        xqts.invokeTest(TEST_PATH + "[764]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast48() throws Exception {
        xqts.invokeTest(TEST_PATH + "[765]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast49() throws Exception {
        xqts.invokeTest(TEST_PATH + "[766]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast50() throws Exception {
        xqts.invokeTest(TEST_PATH + "[767]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast51() throws Exception {
        xqts.invokeTest(TEST_PATH + "[768]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast52() throws Exception {
        xqts.invokeTest(TEST_PATH + "[769]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast53() throws Exception {
        xqts.invokeTest(TEST_PATH + "[770]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast54() throws Exception {
        xqts.invokeTest(TEST_PATH + "[771]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast55() throws Exception {
        xqts.invokeTest(TEST_PATH + "[772]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast56() throws Exception {
        xqts.invokeTest(TEST_PATH + "[773]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast57() throws Exception {
        xqts.invokeTest(TEST_PATH + "[774]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast58() throws Exception {
        xqts.invokeTest(TEST_PATH + "[775]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast59() throws Exception {
        xqts.invokeTest(TEST_PATH + "[776]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast60() throws Exception {
        xqts.invokeTest(TEST_PATH + "[777]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast61() throws Exception {
        xqts.invokeTest(TEST_PATH + "[778]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast62() throws Exception {
        xqts.invokeTest(TEST_PATH + "[779]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast63() throws Exception {
        xqts.invokeTest(TEST_PATH + "[780]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast64() throws Exception {
        xqts.invokeTest(TEST_PATH + "[781]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast65() throws Exception {
        xqts.invokeTest(TEST_PATH + "[782]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast66() throws Exception {
        xqts.invokeTest(TEST_PATH + "[783]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast67() throws Exception {
        xqts.invokeTest(TEST_PATH + "[784]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast68() throws Exception {
        xqts.invokeTest(TEST_PATH + "[785]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast69() throws Exception {
        xqts.invokeTest(TEST_PATH + "[786]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast70() throws Exception {
        xqts.invokeTest(TEST_PATH + "[787]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast71() throws Exception {
        xqts.invokeTest(TEST_PATH + "[788]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast72() throws Exception {
        xqts.invokeTest(TEST_PATH + "[789]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast73() throws Exception {
        xqts.invokeTest(TEST_PATH + "[790]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast74() throws Exception {
        xqts.invokeTest(TEST_PATH + "[791]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast75() throws Exception {
        xqts.invokeTest(TEST_PATH + "[792]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast76() throws Exception {
        xqts.invokeTest(TEST_PATH + "[793]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast77() throws Exception {
        xqts.invokeTest(TEST_PATH + "[794]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast78() throws Exception {
        xqts.invokeTest(TEST_PATH + "[795]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast79() throws Exception {
        xqts.invokeTest(TEST_PATH + "[796]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast80() throws Exception {
        xqts.invokeTest(TEST_PATH + "[797]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast81() throws Exception {
        xqts.invokeTest(TEST_PATH + "[798]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast82() throws Exception {
        xqts.invokeTest(TEST_PATH + "[799]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast83() throws Exception {
        xqts.invokeTest(TEST_PATH + "[800]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast84() throws Exception {
        xqts.invokeTest(TEST_PATH + "[801]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast85() throws Exception {
        xqts.invokeTest(TEST_PATH + "[802]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast86() throws Exception {
        xqts.invokeTest(TEST_PATH + "[803]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast87() throws Exception {
        xqts.invokeTest(TEST_PATH + "[804]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast88() throws Exception {
        xqts.invokeTest(TEST_PATH + "[805]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast89() throws Exception {
        xqts.invokeTest(TEST_PATH + "[806]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast90() throws Exception {
        xqts.invokeTest(TEST_PATH + "[807]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast91() throws Exception {
        xqts.invokeTest(TEST_PATH + "[808]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast92() throws Exception {
        xqts.invokeTest(TEST_PATH + "[809]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast93() throws Exception {
        xqts.invokeTest(TEST_PATH + "[810]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast94() throws Exception {
        xqts.invokeTest(TEST_PATH + "[811]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast95() throws Exception {
        xqts.invokeTest(TEST_PATH + "[812]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast96() throws Exception {
        xqts.invokeTest(TEST_PATH + "[813]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast97() throws Exception {
        xqts.invokeTest(TEST_PATH + "[814]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast98() throws Exception {
        xqts.invokeTest(TEST_PATH + "[815]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast99() throws Exception {
        xqts.invokeTest(TEST_PATH + "[816]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[817]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[818]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[819]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[820]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[821]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[822]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[823]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[824]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[825]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[826]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[827]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[828]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[829]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[830]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[831]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[832]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[833]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[834]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[835]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[836]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[837]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[838]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[839]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[840]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[841]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[842]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[843]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[844]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[845]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[846]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[847]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[848]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[849]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[850]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[851]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[852]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[853]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[854]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[855]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[856]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[857]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[858]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[859]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[860]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[861]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[862]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[863]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[864]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[865]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[866]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[867]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[868]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[869]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[870]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[871]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[872]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[873]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[874]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast160() throws Exception {
        xqts.invokeTest(TEST_PATH + "[875]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast161() throws Exception {
        xqts.invokeTest(TEST_PATH + "[876]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast162() throws Exception {
        xqts.invokeTest(TEST_PATH + "[877]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast163() throws Exception {
        xqts.invokeTest(TEST_PATH + "[878]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast164() throws Exception {
        xqts.invokeTest(TEST_PATH + "[879]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast165() throws Exception {
        xqts.invokeTest(TEST_PATH + "[880]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast166() throws Exception {
        xqts.invokeTest(TEST_PATH + "[881]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast167() throws Exception {
        xqts.invokeTest(TEST_PATH + "[882]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast168() throws Exception {
        xqts.invokeTest(TEST_PATH + "[883]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast169() throws Exception {
        xqts.invokeTest(TEST_PATH + "[884]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast170() throws Exception {
        xqts.invokeTest(TEST_PATH + "[885]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast171() throws Exception {
        xqts.invokeTest(TEST_PATH + "[886]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast172() throws Exception {
        xqts.invokeTest(TEST_PATH + "[887]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast173() throws Exception {
        xqts.invokeTest(TEST_PATH + "[888]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast174() throws Exception {
        xqts.invokeTest(TEST_PATH + "[889]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast175() throws Exception {
        xqts.invokeTest(TEST_PATH + "[890]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast176() throws Exception {
        xqts.invokeTest(TEST_PATH + "[891]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast177() throws Exception {
        xqts.invokeTest(TEST_PATH + "[892]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast178() throws Exception {
        xqts.invokeTest(TEST_PATH + "[893]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast179() throws Exception {
        xqts.invokeTest(TEST_PATH + "[894]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast180() throws Exception {
        xqts.invokeTest(TEST_PATH + "[895]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast181() throws Exception {
        xqts.invokeTest(TEST_PATH + "[896]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast182() throws Exception {
        xqts.invokeTest(TEST_PATH + "[897]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast183() throws Exception {
        xqts.invokeTest(TEST_PATH + "[898]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast184() throws Exception {
        xqts.invokeTest(TEST_PATH + "[899]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast185() throws Exception {
        xqts.invokeTest(TEST_PATH + "[900]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast186() throws Exception {
        xqts.invokeTest(TEST_PATH + "[901]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast187() throws Exception {
        xqts.invokeTest(TEST_PATH + "[902]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast188() throws Exception {
        xqts.invokeTest(TEST_PATH + "[903]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast189() throws Exception {
        xqts.invokeTest(TEST_PATH + "[904]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast190() throws Exception {
        xqts.invokeTest(TEST_PATH + "[905]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast191() throws Exception {
        xqts.invokeTest(TEST_PATH + "[906]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast192() throws Exception {
        xqts.invokeTest(TEST_PATH + "[907]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast193() throws Exception {
        xqts.invokeTest(TEST_PATH + "[908]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast194() throws Exception {
        xqts.invokeTest(TEST_PATH + "[909]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast195() throws Exception {
        xqts.invokeTest(TEST_PATH + "[910]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast196() throws Exception {
        xqts.invokeTest(TEST_PATH + "[911]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast197() throws Exception {
        xqts.invokeTest(TEST_PATH + "[912]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast198() throws Exception {
        xqts.invokeTest(TEST_PATH + "[913]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast199() throws Exception {
        xqts.invokeTest(TEST_PATH + "[914]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast200() throws Exception {
        xqts.invokeTest(TEST_PATH + "[915]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast201() throws Exception {
        xqts.invokeTest(TEST_PATH + "[916]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast202() throws Exception {
        xqts.invokeTest(TEST_PATH + "[917]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast203() throws Exception {
        xqts.invokeTest(TEST_PATH + "[918]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast204() throws Exception {
        xqts.invokeTest(TEST_PATH + "[919]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast205() throws Exception {
        xqts.invokeTest(TEST_PATH + "[920]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast206() throws Exception {
        xqts.invokeTest(TEST_PATH + "[921]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast207() throws Exception {
        xqts.invokeTest(TEST_PATH + "[922]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast208() throws Exception {
        xqts.invokeTest(TEST_PATH + "[923]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast209() throws Exception {
        xqts.invokeTest(TEST_PATH + "[924]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast210() throws Exception {
        xqts.invokeTest(TEST_PATH + "[925]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast211() throws Exception {
        xqts.invokeTest(TEST_PATH + "[926]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast212() throws Exception {
        xqts.invokeTest(TEST_PATH + "[927]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast215() throws Exception {
        xqts.invokeTest(TEST_PATH + "[928]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast216() throws Exception {
        xqts.invokeTest(TEST_PATH + "[929]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast217() throws Exception {
        xqts.invokeTest(TEST_PATH + "[930]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast218() throws Exception {
        xqts.invokeTest(TEST_PATH + "[931]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast219() throws Exception {
        xqts.invokeTest(TEST_PATH + "[932]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast220() throws Exception {
        xqts.invokeTest(TEST_PATH + "[933]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast221() throws Exception {
        xqts.invokeTest(TEST_PATH + "[934]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast222() throws Exception {
        xqts.invokeTest(TEST_PATH + "[935]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast223() throws Exception {
        xqts.invokeTest(TEST_PATH + "[936]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast224() throws Exception {
        xqts.invokeTest(TEST_PATH + "[937]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast225() throws Exception {
        xqts.invokeTest(TEST_PATH + "[938]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast226() throws Exception {
        xqts.invokeTest(TEST_PATH + "[939]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast227() throws Exception {
        xqts.invokeTest(TEST_PATH + "[940]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast228() throws Exception {
        xqts.invokeTest(TEST_PATH + "[941]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast229() throws Exception {
        xqts.invokeTest(TEST_PATH + "[942]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast230() throws Exception {
        xqts.invokeTest(TEST_PATH + "[943]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast231() throws Exception {
        xqts.invokeTest(TEST_PATH + "[944]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast232() throws Exception {
        xqts.invokeTest(TEST_PATH + "[945]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast233() throws Exception {
        xqts.invokeTest(TEST_PATH + "[946]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast234() throws Exception {
        xqts.invokeTest(TEST_PATH + "[947]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast235() throws Exception {
        xqts.invokeTest(TEST_PATH + "[948]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast236() throws Exception {
        xqts.invokeTest(TEST_PATH + "[949]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast237() throws Exception {
        xqts.invokeTest(TEST_PATH + "[950]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast238() throws Exception {
        xqts.invokeTest(TEST_PATH + "[951]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast239() throws Exception {
        xqts.invokeTest(TEST_PATH + "[952]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast240() throws Exception {
        xqts.invokeTest(TEST_PATH + "[953]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast241() throws Exception {
        xqts.invokeTest(TEST_PATH + "[954]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast242() throws Exception {
        xqts.invokeTest(TEST_PATH + "[955]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast243() throws Exception {
        xqts.invokeTest(TEST_PATH + "[956]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast244() throws Exception {
        xqts.invokeTest(TEST_PATH + "[957]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast245() throws Exception {
        xqts.invokeTest(TEST_PATH + "[958]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast246() throws Exception {
        xqts.invokeTest(TEST_PATH + "[959]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast247() throws Exception {
        xqts.invokeTest(TEST_PATH + "[960]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast248() throws Exception {
        xqts.invokeTest(TEST_PATH + "[961]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast249() throws Exception {
        xqts.invokeTest(TEST_PATH + "[962]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast250() throws Exception {
        xqts.invokeTest(TEST_PATH + "[963]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast251() throws Exception {
        xqts.invokeTest(TEST_PATH + "[964]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast252() throws Exception {
        xqts.invokeTest(TEST_PATH + "[965]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast253() throws Exception {
        xqts.invokeTest(TEST_PATH + "[966]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast254() throws Exception {
        xqts.invokeTest(TEST_PATH + "[967]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast255() throws Exception {
        xqts.invokeTest(TEST_PATH + "[968]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast256() throws Exception {
        xqts.invokeTest(TEST_PATH + "[969]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast257() throws Exception {
        xqts.invokeTest(TEST_PATH + "[970]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast258() throws Exception {
        xqts.invokeTest(TEST_PATH + "[971]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast259() throws Exception {
        xqts.invokeTest(TEST_PATH + "[972]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast260() throws Exception {
        xqts.invokeTest(TEST_PATH + "[973]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast261() throws Exception {
        xqts.invokeTest(TEST_PATH + "[974]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast262() throws Exception {
        xqts.invokeTest(TEST_PATH + "[975]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast263() throws Exception {
        xqts.invokeTest(TEST_PATH + "[976]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast264() throws Exception {
        xqts.invokeTest(TEST_PATH + "[977]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast265() throws Exception {
        xqts.invokeTest(TEST_PATH + "[978]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast266() throws Exception {
        xqts.invokeTest(TEST_PATH + "[979]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast267() throws Exception {
        xqts.invokeTest(TEST_PATH + "[980]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast268() throws Exception {
        xqts.invokeTest(TEST_PATH + "[981]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast269() throws Exception {
        xqts.invokeTest(TEST_PATH + "[982]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast270() throws Exception {
        xqts.invokeTest(TEST_PATH + "[983]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast271() throws Exception {
        xqts.invokeTest(TEST_PATH + "[984]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast272() throws Exception {
        xqts.invokeTest(TEST_PATH + "[985]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast273() throws Exception {
        xqts.invokeTest(TEST_PATH + "[986]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast274() throws Exception {
        xqts.invokeTest(TEST_PATH + "[987]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast275() throws Exception {
        xqts.invokeTest(TEST_PATH + "[988]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast276() throws Exception {
        xqts.invokeTest(TEST_PATH + "[989]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast277() throws Exception {
        xqts.invokeTest(TEST_PATH + "[990]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast278() throws Exception {
        xqts.invokeTest(TEST_PATH + "[991]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast279() throws Exception {
        xqts.invokeTest(TEST_PATH + "[992]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast280() throws Exception {
        xqts.invokeTest(TEST_PATH + "[993]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast281() throws Exception {
        xqts.invokeTest(TEST_PATH + "[994]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast282() throws Exception {
        xqts.invokeTest(TEST_PATH + "[995]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast283() throws Exception {
        xqts.invokeTest(TEST_PATH + "[996]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast284() throws Exception {
        xqts.invokeTest(TEST_PATH + "[997]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast285() throws Exception {
        xqts.invokeTest(TEST_PATH + "[998]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast286() throws Exception {
        xqts.invokeTest(TEST_PATH + "[999]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast287() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1000]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast288() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1001]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast289() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1002]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast290() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1003]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast291() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1004]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast292() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1005]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast293() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1006]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast294() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1007]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast295() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1008]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast296() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1009]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast297() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1010]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast298() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1011]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast299() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1012]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast300() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1013]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast301() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1014]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast302() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1015]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast303() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1016]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast304() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1017]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast305() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1018]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast306() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1019]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast307() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1020]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast308() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1021]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast309() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1022]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast310() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1023]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast311() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1024]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast312() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1025]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast313() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1026]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast314() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1027]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast315() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1028]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast316() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1029]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast317() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1030]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast318() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1031]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast319() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1032]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast320() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1033]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast321() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1034]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast322() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1035]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast323() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1036]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast324() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1037]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast325() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1038]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast326() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1039]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast327() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1040]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast328() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1041]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast329() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1042]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast330() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1043]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast331() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1044]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast332() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1045]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast333() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1046]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast334() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1047]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast335() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1048]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast336() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1049]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast337() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1050]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast338() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1051]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast339() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1052]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast340() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1053]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast341() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1054]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast342() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1055]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast343() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1056]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast344() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1057]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast345() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1058]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast346() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1059]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast347() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1060]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast348() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1061]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast349() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1062]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast350() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1063]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast351() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1064]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast352() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1065]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast353() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1066]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast354() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1067]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast355() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1068]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast356() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1069]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast357() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1070]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast358() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1071]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast359() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1072]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast360() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1073]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast361() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1074]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast362() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1075]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast363() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1076]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast364() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1077]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast365() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1078]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast366() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1079]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast367() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1080]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast368() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1081]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast369() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1082]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast370() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1083]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast371() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1084]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast372() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1085]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast373() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1086]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast374() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1087]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast375() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1088]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast376() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1089]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast377() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1090]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast378() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1091]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast379() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1092]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast380() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1093]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast381() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1094]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast382() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1095]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast383() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1096]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast384() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1097]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast385() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1098]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast386() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1099]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast387() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1100]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast388() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1101]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast389() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1102]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast390() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1103]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast391() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1104]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast392() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1105]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast393() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1106]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast394() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1107]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast395() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1108]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast396() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1109]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast397() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1110]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast398() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1111]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast399() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1112]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast400() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1113]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast401() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1114]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast402() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1115]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast403() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1116]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast404() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1117]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast405() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1118]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast406() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1119]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast407() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1120]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast408() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1121]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast409() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1122]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast410() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1123]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast411() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1124]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast415() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1125]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast416() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1126]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast417() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1127]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast418() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1128]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast419() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1129]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast420() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1130]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast421() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1131]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast422() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1132]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast423() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1133]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast424() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1134]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast425() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1135]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast426() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1136]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast427() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1137]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast428() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1138]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast429() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1139]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast430() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1140]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast431() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1141]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast432() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1142]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast433() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1143]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast434() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1144]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast435() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1145]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast436() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1146]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast437() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1147]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast438() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1148]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast439() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1149]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast440() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1150]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast441() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1151]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast442() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1152]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast443() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1153]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast444() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1154]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast445() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1155]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast446() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1156]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast447() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1157]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast448() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1158]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast449() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1159]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast450() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1160]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast451() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1161]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast452() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1162]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast453() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1163]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast454() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1164]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast455() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1165]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast456() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1166]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast457() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1167]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast458() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1168]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast459() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1169]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast460() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1170]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast461() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1171]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast462() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1172]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast463() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1173]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast464() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1174]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast465() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1175]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast466() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1176]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast467() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1177]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast468() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1178]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast469() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1179]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast470() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1180]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast471() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1181]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast472() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1182]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast473() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1183]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast474() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1184]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast475() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1185]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast476() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1186]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast477() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1187]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast478() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1188]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast479() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1189]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast480() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1190]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast481() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1191]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast482() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1192]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast483() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1193]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast484() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1194]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast485() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1195]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast486() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1196]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast487() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1197]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast488() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1198]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast489() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1199]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast490() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1200]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast491() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1201]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast492() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1202]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast493() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1203]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast494() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1204]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast495() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1205]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast496() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1206]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast497() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1207]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast498() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1208]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast499() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1209]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast500() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1210]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast501() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1211]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast502() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1212]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast503() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1213]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast504() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1214]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast505() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1215]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast506() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1216]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast507() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1217]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast508() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1218]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast509() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1219]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast510() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1220]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast511() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1221]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast512() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1222]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast513() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1223]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast514() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1224]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast515() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1225]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast516() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1226]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast517() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1227]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast518() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1228]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast519() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1229]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast520() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1230]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast521() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1231]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast522() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1232]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast523() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1233]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast524() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1234]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast525() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1235]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast526() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1236]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast527() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1237]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast528() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1238]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast529() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1239]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast530() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1240]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast531() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1241]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast532() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1242]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast533() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1243]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast534() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1244]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast535() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1245]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast536() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1246]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast537() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1247]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast538() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1248]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast539() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1249]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast540() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1250]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast541() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1251]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast542() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1252]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast543() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1253]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast544() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1254]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast545() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1255]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast546() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1256]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast547() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1257]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast548() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1258]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast549() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1259]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast550() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1260]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast551() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1261]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast552() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1262]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast553() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1263]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast554() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1264]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast555() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1265]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast556() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1266]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast557() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1267]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast558() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1268]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast559() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1269]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast560() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1270]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast561() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1271]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast562() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1272]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast563() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1273]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast564() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1274]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast565() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1275]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast566() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1276]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast567() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1277]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast568() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1278]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast569() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1279]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast570() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1280]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast571() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1281]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast572() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1282]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast573() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1283]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast574() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1284]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast575() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1285]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast576() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1286]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast577() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1287]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast578() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1288]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast579() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1289]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast580() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1290]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast581() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1291]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast582() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1292]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast583() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1293]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast584() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1294]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast585() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1295]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast586() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1296]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast587() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1297]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast588() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1298]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast589() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1299]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast590() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1300]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast591() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1301]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast592() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1302]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast593() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1303]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast594() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1304]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast595() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1305]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast596() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1306]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast597() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1307]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast598() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1308]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast599() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1309]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast600() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1310]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast601() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1311]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast602() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1312]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast603() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1313]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast604() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1314]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast605() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1315]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast606() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1316]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast607() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1317]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast608() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1318]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast609() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1319]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast610() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1320]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast611() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1321]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast612() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1322]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast613() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1323]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast614() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1324]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast615() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1325]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast616() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1326]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast617() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1327]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast618() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1328]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast619() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1329]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast620() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1330]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast621() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1331]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast622() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1332]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast623() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1333]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast624() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1334]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast625() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1335]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast626() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1336]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast627() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1337]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast628() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1338]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast629() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1339]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast630() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1340]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast631() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1341]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast632() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1342]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast633() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1343]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast634() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1344]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast635() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1345]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast636() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1346]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast637() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1347]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast638() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1348]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast639() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1349]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast640() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1350]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast641() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1351]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast642() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1352]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast643() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1353]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast644() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1354]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast645() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1355]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast646() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1356]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast647() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1357]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast648() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1358]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast649() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1359]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast650() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1360]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast651() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1361]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast652() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1362]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast653() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1363]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast654() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1364]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast655() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1365]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast656() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1366]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast657() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1367]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast658() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1368]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast659() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1369]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast660() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1370]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast661() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1371]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast662() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1372]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast663() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1373]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast664() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1374]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast665() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1375]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast666() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1376]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast667() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1377]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast668() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1378]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast669() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1379]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast670() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1380]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast671() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1381]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast672() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1382]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast673() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1383]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast674() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1384]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast675() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1385]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast676() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1386]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast677() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1387]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast678() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1388]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast679() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1389]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast680() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1390]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast681() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1391]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast682() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1392]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast683() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1393]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast684() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1394]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast685() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1395]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast686() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1396]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast687() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1397]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast688() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1398]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast689() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1399]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast690() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1400]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast691() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1401]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast692() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1402]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast693() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1403]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast694() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1404]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast695() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1405]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast696() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1406]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast697() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1407]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast698() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1408]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast699() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1409]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast700() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1410]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast701() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1411]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast702() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1412]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast703() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1413]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast704() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1414]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast705() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1415]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast706() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1416]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast707() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1417]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast708() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1418]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast709() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1419]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast710() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1420]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast711() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1421]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast712() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1422]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast713() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1423]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast714() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1424]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast715() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1425]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast716() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1426]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast717() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1427]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast718() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1428]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast719() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1429]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast720() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1430]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast721() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1431]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast722() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1432]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast723() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1433]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast724() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1434]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast725() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1435]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast726() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1436]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast727() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1437]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast728() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1438]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast729() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1439]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast730() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1440]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast731() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1441]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast732() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1442]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast733() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1443]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast734() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1444]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast735() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1445]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast736() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1446]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast737() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1447]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast738() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1448]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast739() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1449]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast740() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1450]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast741() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1451]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast742() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1452]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast743() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1453]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast744() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1454]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast745() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1455]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast746() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1456]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast747() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1457]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast748() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1458]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast749() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1459]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast750() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1460]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast751() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1461]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast752() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1462]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast753() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1463]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast754() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1464]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast755() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1465]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast756() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1466]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast757() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1467]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast758() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1468]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast759() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1469]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast760() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1470]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast761() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1471]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast762() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1472]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast763() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1473]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast764() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1474]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast765() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1475]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast766() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1476]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast767() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1477]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast768() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1478]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast769() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1479]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast770() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1480]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast771() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1481]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast772() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1482]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast773() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1483]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast774() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1484]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast775() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1485]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast776() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1486]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast777() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1487]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast778() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1488]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast779() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1489]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast780() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1490]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast781() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1491]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast782() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1492]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast783() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1493]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast784() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1494]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast785() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1495]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast786() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1496]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast787() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1497]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast788() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1498]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast789() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1499]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast790() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1500]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast791() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1501]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast792() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1502]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast793() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1503]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast794() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1504]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast795() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1505]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast796() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1506]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast797() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1507]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast798() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1508]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast799() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1509]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast800() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1510]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast801() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1511]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast802() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1512]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast803() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1513]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast804() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1514]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast805() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1515]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast806() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1516]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast807() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1517]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast808() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1518]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast809() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1519]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast810() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1520]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast811() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1521]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast812() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1522]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast813() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1523]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast814() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1524]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast815() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1525]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast816() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1526]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast817() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1527]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast818() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1528]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast819() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1529]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast820() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1530]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast821() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1531]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast822() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1532]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast823() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1533]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast824() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1534]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast825() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1535]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast826() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1536]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast827() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1537]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast828() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1538]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast829() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1539]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast830() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1540]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast831() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1541]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast832() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1542]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast833() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1543]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast834() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1544]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast835() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1545]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast836() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1546]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast837() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1547]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast838() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1548]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast839() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1549]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast840() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1550]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast841() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1551]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast842() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1552]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast843() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1553]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast844() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1554]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast845() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1555]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast846() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1556]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast847() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1557]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast848() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1558]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast849() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1559]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast850() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1560]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast851() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1561]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast852() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1562]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast853() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1563]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast854() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1564]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast855() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1565]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast856() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1566]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast857() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1567]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast858() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1568]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast859() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1569]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast860() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1570]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast861() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1571]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast862() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1572]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast863() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1573]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast864() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1574]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast865() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1575]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast866() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1576]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast867() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1577]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast868() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1578]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast869() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1579]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast870() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1580]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast871() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1581]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast872() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1582]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast873() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1583]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast874() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1584]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast875() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1585]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast876() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1586]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast877() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1587]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast878() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1588]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast879() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1589]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast880() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1590]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast881() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1591]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast882() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1592]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast883() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1593]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast884() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1594]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast885() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1595]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast886() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1596]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast887() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1597]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast888() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1598]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast889() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1599]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast890() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1600]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast891() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1601]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast892() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1602]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast893() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1603]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast894() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1604]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast895() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1605]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast896() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1606]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast897() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1607]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast898() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1608]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast899() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1609]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast900() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1610]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast901() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1611]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast902() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1612]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast903() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1613]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast904() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1614]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast905() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1615]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast906() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1616]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast907() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1617]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast908() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1618]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast909() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1619]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast910() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1620]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast911() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1621]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast912() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1622]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast913() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1623]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast914() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1624]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast915() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1625]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast916() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1626]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast917() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1627]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast918() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1628]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast919() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1629]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast920() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1630]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast921() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1631]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast922() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1632]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast923() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1633]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast924() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1634]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast925() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1635]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast926() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1636]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast927() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1637]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast928() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1638]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast929() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1639]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast930() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1640]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast931() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1641]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast932() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1642]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast933() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1643]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast934() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1644]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast935() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1645]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast936() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1646]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast937() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1647]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast938() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1648]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast939() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1649]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast940() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1650]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast941() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1651]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast942() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1652]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast943() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1653]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast944() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1654]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast945() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1655]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast946() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1656]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast947() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1657]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast948() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1658]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast949() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1659]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast950() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1660]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast951() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1661]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast952() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1662]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast953() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1663]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast954() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1664]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast955() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1665]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast956() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1666]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast957() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1667]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast958() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1668]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast959() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1669]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast960() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1670]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast961() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1671]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast962() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1672]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast963() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1673]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast964() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1674]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast965() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1675]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast966() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1676]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast967() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1677]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast968() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1678]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast969() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1679]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast970() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1680]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast971() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1681]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast972() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1682]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast973() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1683]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast974() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1684]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast975() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1685]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast976() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1686]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast977() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1687]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast978() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1688]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast979() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1689]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast980() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1690]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast981() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1691]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast982() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1692]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast983() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1693]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast984() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1694]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast985() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1695]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast986() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1696]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast987() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1697]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast988() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1698]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast989() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1699]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast990() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1700]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast991() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1701]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast992() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1702]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast993() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1703]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast994() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1704]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast995() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1705]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast996() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1706]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast997() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1707]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast998() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1708]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast999() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1709]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1000() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1710]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1711]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1712]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1713]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1714]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1715]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1716]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1717]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1718]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1719]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1720]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1721]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1722]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1723]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1724]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1725]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1726]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1727]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1728]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1729]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1730]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1731]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1732]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1733]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1734]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1735]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1736]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1737]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1738]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1739]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1740]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1741]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1742]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1743]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1744]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1745]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1746]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1747]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1748]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1749]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1750]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1751]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1752]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1753]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1754]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1755]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1756]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1757]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1758]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1759]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1760]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1761]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1762]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1763]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1764]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1765]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1766]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1767]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1768]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1769]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1770]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1771]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1772]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1063() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1773]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1064() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1774]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1065() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1775]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1066() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1776]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1067() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1777]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1068() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1778]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1069() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1779]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1070() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1780]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1071() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1781]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1072() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1782]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1073() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1783]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1074() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1784]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1075() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1785]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1076() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1786]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1077() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1787]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1078() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1788]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1079() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1789]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1080() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1790]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1081() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1791]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1082() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1792]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1083() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1793]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1084() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1794]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1795]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1796]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1797]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1798]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1089() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1799]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1090() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1800]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1091() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1801]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1092() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1802]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1093() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1803]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1094() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1804]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1095() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1805]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1096() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1806]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1097() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1807]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1098() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1808]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1099() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1809]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1810]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1811]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1812]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1813]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1814]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1815]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1816]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1817]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1818]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1819]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1820]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1821]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1822]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1823]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1824]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1825]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1826]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1827]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1828]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1829]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1830]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1831]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1832]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1833]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1834]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1835]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1836]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1837]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1838]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1839]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1840]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1841]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1842]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1843]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1844]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1845]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1846]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1847]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1848]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1849]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1850]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1851]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1852]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1853]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1854]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1855]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1146() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1856]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1147() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1857]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1858]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1859]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1860]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1861]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1862]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1863]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1864]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1865]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1866]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1867]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1868]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1869]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1160() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1870]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1161() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1871]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1162() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1872]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1163() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1873]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1164() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1874]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1165() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1875]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1166() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1876]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1167() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1877]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1168() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1878]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1169() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1879]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1170() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1880]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1171() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1881]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1172() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1882]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1173() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1883]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1174() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1884]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1175() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1885]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1176() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1886]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1177() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1887]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1178() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1888]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1179() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1889]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1180() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1890]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1181() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1891]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1182() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1892]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1183() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1893]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1184() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1894]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1185() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1895]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1186() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1896]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1187() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1897]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1188() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1898]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1189() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1899]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1190() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1900]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1191() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1901]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1192() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1902]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1193() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1903]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1194() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1904]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1195() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1905]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1196() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1906]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1197() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1907]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1198() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1908]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1199() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1909]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1200() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1910]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1201() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1911]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1202() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1912]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1203() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1913]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1204() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1914]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1205() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1915]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1206() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1916]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1207() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1917]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1208() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1918]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1209() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1919]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1210() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1920]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1211() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1921]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1212() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1922]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1213() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1923]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1214() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1924]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1215() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1925]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1216() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1926]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1217() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1927]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1218() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1928]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1219() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1929]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1220() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1930]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1221() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1931]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1222() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1932]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1223() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1933]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1224() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1934]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1225() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1935]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1226() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1936]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1227() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1937]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1228() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1938]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1229() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1939]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1230() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1940]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1231() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1941]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1232() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1942]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1233() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1943]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1234() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1944]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1235() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1945]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1236() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1946]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1237() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1947]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1238() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1948]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1239() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1949]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1240() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1950]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1241() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1951]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1242() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1952]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1243() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1953]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1244() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1954]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1245() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1955]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1246() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1956]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1247() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1957]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1248() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1958]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1249() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1959]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1250() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1960]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1251() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1961]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1252() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1962]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1253() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1963]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1254() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1964]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1255() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1965]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1256() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1966]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1257() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1967]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1258() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1968]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1259() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1969]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1260() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1970]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1261() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1971]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1262() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1972]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1263() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1973]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1264() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1974]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1265() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1975]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1266() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1976]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1267() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1977]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1268() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1978]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1269() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1979]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1270() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1980]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1271() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1981]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1272() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1982]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1273() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1983]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1274() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1984]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1275() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1985]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1276() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1986]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1277() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1987]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1278() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1988]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1279() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1989]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1280() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1990]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1281() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1991]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1282() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1992]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1283() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1993]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1284() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1994]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1285() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1995]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1286() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1996]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1287() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1997]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1288() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1998]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1289() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1999]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1290() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2000]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1291() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2001]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1292() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2002]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1293() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2003]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1294() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2004]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1295() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2005]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1296() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2006]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1297() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2007]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1298() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2008]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1299() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2009]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1300() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2010]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1301() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2011]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1302() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2012]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1303() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2013]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1304() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2014]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1305() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2015]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1306() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2016]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1307() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2017]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1308() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2018]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1309() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2019]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1310() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2020]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1311() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2021]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1312() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2022]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1313() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2023]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1314() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2024]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1315() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2025]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1316() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2026]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1317() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2027]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1318() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2028]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1319() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2029]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1320() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2030]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1321() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2031]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1322() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2032]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1323() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2033]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1324() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2034]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1325() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2035]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1326() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2036]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1327() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2037]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1328() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2038]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1329() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2039]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1330() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2040]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1331() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2041]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1332() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2042]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1333() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2043]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1334() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2044]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1335() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2045]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1336() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2046]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1337() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2047]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1338() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2048]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1339() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2049]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1340() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2050]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1341() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2051]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1342() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2052]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1343() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2053]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1344() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2054]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1345() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2055]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1346() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2056]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1347() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2057]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1348() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2058]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1349() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2059]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1350() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2060]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1351() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2061]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1352() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2062]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1353() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2063]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1354() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2064]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1355() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2065]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1356() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2066]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1357() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2067]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1358() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2068]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1359() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2069]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1360() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2070]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1361() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2071]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1362() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2072]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1363() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2073]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1364() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2074]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1365() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2075]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1366() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2076]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1367() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2077]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1368() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2078]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1369() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2079]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1370() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2080]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1371() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2081]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1372() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2082]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1373() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2083]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1374() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2084]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1375() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2085]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1376() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2086]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1377() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2087]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1378() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2088]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1379() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2089]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1380() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2090]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1381() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2091]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1382() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2092]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1383() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2093]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1384() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2094]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1385() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2095]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1386() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2096]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1387() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2097]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1388() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2098]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1389() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2099]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1390() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2100]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1391() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2101]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1392() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2102]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1393() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2103]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1394() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2104]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1395() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2105]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1396() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2106]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1397() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2107]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1398() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2108]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1399() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2109]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1400() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2110]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1401() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2111]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1402() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2112]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1403() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2113]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1404() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2114]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1405() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2115]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1406() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2116]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1407() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2117]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1408() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2118]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1409() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2119]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1410() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2120]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1411() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2121]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1412() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2122]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1413() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2123]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1414() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2124]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1415() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2125]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1416() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2126]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1417() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2127]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1418() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2128]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1419() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2129]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1420() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2130]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1421() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2131]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1422() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2132]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1423() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2133]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1424() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2134]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1425() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2135]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1426() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2136]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1427() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2137]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1428() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2138]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1429() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2139]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1430() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2140]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1431() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2141]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1432() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2142]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1433() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2143]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1434() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2144]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1435() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2145]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1436() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2146]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1437() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2147]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1438() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2148]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1439() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2149]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1440() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2150]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1441() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2151]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1442() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2152]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1443() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2153]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1444() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2154]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1445() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2155]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1446() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2156]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1447() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2157]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1448() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2158]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1449() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2159]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1450() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2160]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1451() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2161]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1452() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2162]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1453() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2163]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1454() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2164]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1455() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2165]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1456() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2166]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1457() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2167]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1458() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2168]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1459() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2169]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1460() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2170]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1461() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2171]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1462() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2172]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1463() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2173]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1464() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2174]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1465() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2175]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1466() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2176]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1467() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2177]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1468() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2178]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1469() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2179]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1470() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2180]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1471() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2181]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1472() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2182]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1473() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2183]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1474() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2184]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1475() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2185]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1476() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2186]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1477() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2187]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1478() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2188]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1479() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2189]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1480() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2190]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1481() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2191]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1482() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2192]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1483() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2193]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1484() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2194]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1485() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2195]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1486() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2196]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1487() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2197]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1488() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2198]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1489() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2199]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1490() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2200]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1491() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2201]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1492() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2202]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1493() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2203]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1494() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2204]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCast1495() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2205]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqExprCast1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2206]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqExprCast2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2207]");
    }

}