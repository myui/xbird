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
package xqts.expressions.exprseqtypes.seqexprcastable;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class SeqExprCastableTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[132]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public SeqExprCastableTest() {
    	super(SeqExprCastableTest.class.getName());
        this.xqts = new XQTSTestBase(SeqExprCastableTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testCastableAs001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs021() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs022() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs023() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs024() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs025() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs026() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs027() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs028() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs029() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs030() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs031() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs032() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs033() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs034() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs035() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs036() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs037() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs038() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs039() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs040() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs041() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs042() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs043() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs044() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs045() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs046() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs047() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs048() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs049() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs050() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs051() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs052() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs053() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs054() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs055() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs056() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs057() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs058() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs059() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs060() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs061() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs062() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs063() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs064() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs065() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs066() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs067() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs068() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs069() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs070() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs071() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs072() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs073() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs074() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs075() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs076() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs077() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs078() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs079() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs080() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs081() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs082() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs083() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs084() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs089() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs090() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs091() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs092() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs093() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs094() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs095() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs096() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs097() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs098() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs099() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs100() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs101() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs102() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs103() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs104() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs105() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs106() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs107() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs108() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs109() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs110() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs111() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs112() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs113() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs114() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs115() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs116() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs117() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs118() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs119() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs120() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs121() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs122() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs123() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs124() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs125() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs126() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs127() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs128() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs129() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs130() throws Exception {
        xqts.invokeTest(TEST_PATH + "[130]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs131() throws Exception {
        xqts.invokeTest(TEST_PATH + "[131]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs132() throws Exception {
        xqts.invokeTest(TEST_PATH + "[132]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs133() throws Exception {
        xqts.invokeTest(TEST_PATH + "[133]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs134() throws Exception {
        xqts.invokeTest(TEST_PATH + "[134]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs135() throws Exception {
        xqts.invokeTest(TEST_PATH + "[135]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs136() throws Exception {
        xqts.invokeTest(TEST_PATH + "[136]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs137() throws Exception {
        xqts.invokeTest(TEST_PATH + "[137]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs138() throws Exception {
        xqts.invokeTest(TEST_PATH + "[138]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs139() throws Exception {
        xqts.invokeTest(TEST_PATH + "[139]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs140() throws Exception {
        xqts.invokeTest(TEST_PATH + "[140]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs141() throws Exception {
        xqts.invokeTest(TEST_PATH + "[141]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs142() throws Exception {
        xqts.invokeTest(TEST_PATH + "[142]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs143() throws Exception {
        xqts.invokeTest(TEST_PATH + "[143]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs144() throws Exception {
        xqts.invokeTest(TEST_PATH + "[144]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs145() throws Exception {
        xqts.invokeTest(TEST_PATH + "[145]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs146() throws Exception {
        xqts.invokeTest(TEST_PATH + "[146]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs147() throws Exception {
        xqts.invokeTest(TEST_PATH + "[147]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs148() throws Exception {
        xqts.invokeTest(TEST_PATH + "[148]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs149() throws Exception {
        xqts.invokeTest(TEST_PATH + "[149]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs150() throws Exception {
        xqts.invokeTest(TEST_PATH + "[150]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs151() throws Exception {
        xqts.invokeTest(TEST_PATH + "[151]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs152() throws Exception {
        xqts.invokeTest(TEST_PATH + "[152]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs153() throws Exception {
        xqts.invokeTest(TEST_PATH + "[153]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs154() throws Exception {
        xqts.invokeTest(TEST_PATH + "[154]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs155() throws Exception {
        xqts.invokeTest(TEST_PATH + "[155]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs156() throws Exception {
        xqts.invokeTest(TEST_PATH + "[156]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs157() throws Exception {
        xqts.invokeTest(TEST_PATH + "[157]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs158() throws Exception {
        xqts.invokeTest(TEST_PATH + "[158]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs159() throws Exception {
        xqts.invokeTest(TEST_PATH + "[159]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs160() throws Exception {
        xqts.invokeTest(TEST_PATH + "[160]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs161() throws Exception {
        xqts.invokeTest(TEST_PATH + "[161]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs162() throws Exception {
        xqts.invokeTest(TEST_PATH + "[162]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs163() throws Exception {
        xqts.invokeTest(TEST_PATH + "[163]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs164() throws Exception {
        xqts.invokeTest(TEST_PATH + "[164]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs165() throws Exception {
        xqts.invokeTest(TEST_PATH + "[165]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs166() throws Exception {
        xqts.invokeTest(TEST_PATH + "[166]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs167() throws Exception {
        xqts.invokeTest(TEST_PATH + "[167]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs168() throws Exception {
        xqts.invokeTest(TEST_PATH + "[168]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs169() throws Exception {
        xqts.invokeTest(TEST_PATH + "[169]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs170() throws Exception {
        xqts.invokeTest(TEST_PATH + "[170]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs171() throws Exception {
        xqts.invokeTest(TEST_PATH + "[171]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs172() throws Exception {
        xqts.invokeTest(TEST_PATH + "[172]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs173() throws Exception {
        xqts.invokeTest(TEST_PATH + "[173]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs174() throws Exception {
        xqts.invokeTest(TEST_PATH + "[174]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs175() throws Exception {
        xqts.invokeTest(TEST_PATH + "[175]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs176() throws Exception {
        xqts.invokeTest(TEST_PATH + "[176]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs177() throws Exception {
        xqts.invokeTest(TEST_PATH + "[177]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs178() throws Exception {
        xqts.invokeTest(TEST_PATH + "[178]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs179() throws Exception {
        xqts.invokeTest(TEST_PATH + "[179]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs180() throws Exception {
        xqts.invokeTest(TEST_PATH + "[180]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs181() throws Exception {
        xqts.invokeTest(TEST_PATH + "[181]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs182() throws Exception {
        xqts.invokeTest(TEST_PATH + "[182]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs183() throws Exception {
        xqts.invokeTest(TEST_PATH + "[183]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs184() throws Exception {
        xqts.invokeTest(TEST_PATH + "[184]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs185() throws Exception {
        xqts.invokeTest(TEST_PATH + "[185]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs186() throws Exception {
        xqts.invokeTest(TEST_PATH + "[186]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs187() throws Exception {
        xqts.invokeTest(TEST_PATH + "[187]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs188() throws Exception {
        xqts.invokeTest(TEST_PATH + "[188]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs189() throws Exception {
        xqts.invokeTest(TEST_PATH + "[189]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs190() throws Exception {
        xqts.invokeTest(TEST_PATH + "[190]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs191() throws Exception {
        xqts.invokeTest(TEST_PATH + "[191]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs192() throws Exception {
        xqts.invokeTest(TEST_PATH + "[192]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs193() throws Exception {
        xqts.invokeTest(TEST_PATH + "[193]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs194() throws Exception {
        xqts.invokeTest(TEST_PATH + "[194]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs195() throws Exception {
        xqts.invokeTest(TEST_PATH + "[195]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs196() throws Exception {
        xqts.invokeTest(TEST_PATH + "[196]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs197() throws Exception {
        xqts.invokeTest(TEST_PATH + "[197]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs198() throws Exception {
        xqts.invokeTest(TEST_PATH + "[198]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs199() throws Exception {
        xqts.invokeTest(TEST_PATH + "[199]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs200() throws Exception {
        xqts.invokeTest(TEST_PATH + "[200]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs201() throws Exception {
        xqts.invokeTest(TEST_PATH + "[201]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs202() throws Exception {
        xqts.invokeTest(TEST_PATH + "[202]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs203() throws Exception {
        xqts.invokeTest(TEST_PATH + "[203]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs204() throws Exception {
        xqts.invokeTest(TEST_PATH + "[204]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs205() throws Exception {
        xqts.invokeTest(TEST_PATH + "[205]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs206() throws Exception {
        xqts.invokeTest(TEST_PATH + "[206]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs207() throws Exception {
        xqts.invokeTest(TEST_PATH + "[207]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs208() throws Exception {
        xqts.invokeTest(TEST_PATH + "[208]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs209() throws Exception {
        xqts.invokeTest(TEST_PATH + "[209]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs210() throws Exception {
        xqts.invokeTest(TEST_PATH + "[210]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs211() throws Exception {
        xqts.invokeTest(TEST_PATH + "[211]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs212() throws Exception {
        xqts.invokeTest(TEST_PATH + "[212]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs213() throws Exception {
        xqts.invokeTest(TEST_PATH + "[213]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs214() throws Exception {
        xqts.invokeTest(TEST_PATH + "[214]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs215() throws Exception {
        xqts.invokeTest(TEST_PATH + "[215]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs216() throws Exception {
        xqts.invokeTest(TEST_PATH + "[216]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs217() throws Exception {
        xqts.invokeTest(TEST_PATH + "[217]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs218() throws Exception {
        xqts.invokeTest(TEST_PATH + "[218]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs219() throws Exception {
        xqts.invokeTest(TEST_PATH + "[219]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs220() throws Exception {
        xqts.invokeTest(TEST_PATH + "[220]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs221() throws Exception {
        xqts.invokeTest(TEST_PATH + "[221]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs222() throws Exception {
        xqts.invokeTest(TEST_PATH + "[222]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs223() throws Exception {
        xqts.invokeTest(TEST_PATH + "[223]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs224() throws Exception {
        xqts.invokeTest(TEST_PATH + "[224]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs225() throws Exception {
        xqts.invokeTest(TEST_PATH + "[225]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs226() throws Exception {
        xqts.invokeTest(TEST_PATH + "[226]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs227() throws Exception {
        xqts.invokeTest(TEST_PATH + "[227]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs228() throws Exception {
        xqts.invokeTest(TEST_PATH + "[228]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs229() throws Exception {
        xqts.invokeTest(TEST_PATH + "[229]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs230() throws Exception {
        xqts.invokeTest(TEST_PATH + "[230]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs231() throws Exception {
        xqts.invokeTest(TEST_PATH + "[231]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs232() throws Exception {
        xqts.invokeTest(TEST_PATH + "[232]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs233() throws Exception {
        xqts.invokeTest(TEST_PATH + "[233]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs234() throws Exception {
        xqts.invokeTest(TEST_PATH + "[234]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs235() throws Exception {
        xqts.invokeTest(TEST_PATH + "[235]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs236() throws Exception {
        xqts.invokeTest(TEST_PATH + "[236]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs237() throws Exception {
        xqts.invokeTest(TEST_PATH + "[237]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs238() throws Exception {
        xqts.invokeTest(TEST_PATH + "[238]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs239() throws Exception {
        xqts.invokeTest(TEST_PATH + "[239]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs240() throws Exception {
        xqts.invokeTest(TEST_PATH + "[240]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs241() throws Exception {
        xqts.invokeTest(TEST_PATH + "[241]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs242() throws Exception {
        xqts.invokeTest(TEST_PATH + "[242]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs243() throws Exception {
        xqts.invokeTest(TEST_PATH + "[243]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs244() throws Exception {
        xqts.invokeTest(TEST_PATH + "[244]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs245() throws Exception {
        xqts.invokeTest(TEST_PATH + "[245]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs246() throws Exception {
        xqts.invokeTest(TEST_PATH + "[246]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs247() throws Exception {
        xqts.invokeTest(TEST_PATH + "[247]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs248() throws Exception {
        xqts.invokeTest(TEST_PATH + "[248]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs249() throws Exception {
        xqts.invokeTest(TEST_PATH + "[249]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs250() throws Exception {
        xqts.invokeTest(TEST_PATH + "[250]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs251() throws Exception {
        xqts.invokeTest(TEST_PATH + "[251]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs252() throws Exception {
        xqts.invokeTest(TEST_PATH + "[252]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs253() throws Exception {
        xqts.invokeTest(TEST_PATH + "[253]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs254() throws Exception {
        xqts.invokeTest(TEST_PATH + "[254]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs255() throws Exception {
        xqts.invokeTest(TEST_PATH + "[255]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs256() throws Exception {
        xqts.invokeTest(TEST_PATH + "[256]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs257() throws Exception {
        xqts.invokeTest(TEST_PATH + "[257]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs258() throws Exception {
        xqts.invokeTest(TEST_PATH + "[258]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs259() throws Exception {
        xqts.invokeTest(TEST_PATH + "[259]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs260() throws Exception {
        xqts.invokeTest(TEST_PATH + "[260]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs261() throws Exception {
        xqts.invokeTest(TEST_PATH + "[261]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs262() throws Exception {
        xqts.invokeTest(TEST_PATH + "[262]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs263() throws Exception {
        xqts.invokeTest(TEST_PATH + "[263]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs264() throws Exception {
        xqts.invokeTest(TEST_PATH + "[264]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs265() throws Exception {
        xqts.invokeTest(TEST_PATH + "[265]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs266() throws Exception {
        xqts.invokeTest(TEST_PATH + "[266]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs267() throws Exception {
        xqts.invokeTest(TEST_PATH + "[267]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs268() throws Exception {
        xqts.invokeTest(TEST_PATH + "[268]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs269() throws Exception {
        xqts.invokeTest(TEST_PATH + "[269]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs270() throws Exception {
        xqts.invokeTest(TEST_PATH + "[270]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs271() throws Exception {
        xqts.invokeTest(TEST_PATH + "[271]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs272() throws Exception {
        xqts.invokeTest(TEST_PATH + "[272]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs273() throws Exception {
        xqts.invokeTest(TEST_PATH + "[273]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs274() throws Exception {
        xqts.invokeTest(TEST_PATH + "[274]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs275() throws Exception {
        xqts.invokeTest(TEST_PATH + "[275]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs276() throws Exception {
        xqts.invokeTest(TEST_PATH + "[276]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs277() throws Exception {
        xqts.invokeTest(TEST_PATH + "[277]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs278() throws Exception {
        xqts.invokeTest(TEST_PATH + "[278]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs279() throws Exception {
        xqts.invokeTest(TEST_PATH + "[279]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs280() throws Exception {
        xqts.invokeTest(TEST_PATH + "[280]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs281() throws Exception {
        xqts.invokeTest(TEST_PATH + "[281]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs282() throws Exception {
        xqts.invokeTest(TEST_PATH + "[282]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs283() throws Exception {
        xqts.invokeTest(TEST_PATH + "[283]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs284() throws Exception {
        xqts.invokeTest(TEST_PATH + "[284]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs285() throws Exception {
        xqts.invokeTest(TEST_PATH + "[285]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs286() throws Exception {
        xqts.invokeTest(TEST_PATH + "[286]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs287() throws Exception {
        xqts.invokeTest(TEST_PATH + "[287]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs288() throws Exception {
        xqts.invokeTest(TEST_PATH + "[288]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs289() throws Exception {
        xqts.invokeTest(TEST_PATH + "[289]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs290() throws Exception {
        xqts.invokeTest(TEST_PATH + "[290]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs291() throws Exception {
        xqts.invokeTest(TEST_PATH + "[291]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs292() throws Exception {
        xqts.invokeTest(TEST_PATH + "[292]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs293() throws Exception {
        xqts.invokeTest(TEST_PATH + "[293]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs294() throws Exception {
        xqts.invokeTest(TEST_PATH + "[294]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs295() throws Exception {
        xqts.invokeTest(TEST_PATH + "[295]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs296() throws Exception {
        xqts.invokeTest(TEST_PATH + "[296]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs297() throws Exception {
        xqts.invokeTest(TEST_PATH + "[297]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs298() throws Exception {
        xqts.invokeTest(TEST_PATH + "[298]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs299() throws Exception {
        xqts.invokeTest(TEST_PATH + "[299]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs300() throws Exception {
        xqts.invokeTest(TEST_PATH + "[300]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs301() throws Exception {
        xqts.invokeTest(TEST_PATH + "[301]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs302() throws Exception {
        xqts.invokeTest(TEST_PATH + "[302]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs303() throws Exception {
        xqts.invokeTest(TEST_PATH + "[303]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs304() throws Exception {
        xqts.invokeTest(TEST_PATH + "[304]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs305() throws Exception {
        xqts.invokeTest(TEST_PATH + "[305]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs306() throws Exception {
        xqts.invokeTest(TEST_PATH + "[306]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs307() throws Exception {
        xqts.invokeTest(TEST_PATH + "[307]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs308() throws Exception {
        xqts.invokeTest(TEST_PATH + "[308]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs309() throws Exception {
        xqts.invokeTest(TEST_PATH + "[309]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs310() throws Exception {
        xqts.invokeTest(TEST_PATH + "[310]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs311() throws Exception {
        xqts.invokeTest(TEST_PATH + "[311]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs312() throws Exception {
        xqts.invokeTest(TEST_PATH + "[312]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs313() throws Exception {
        xqts.invokeTest(TEST_PATH + "[313]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs314() throws Exception {
        xqts.invokeTest(TEST_PATH + "[314]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs315() throws Exception {
        xqts.invokeTest(TEST_PATH + "[315]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs316() throws Exception {
        xqts.invokeTest(TEST_PATH + "[316]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs317() throws Exception {
        xqts.invokeTest(TEST_PATH + "[317]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs318() throws Exception {
        xqts.invokeTest(TEST_PATH + "[318]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs319() throws Exception {
        xqts.invokeTest(TEST_PATH + "[319]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs320() throws Exception {
        xqts.invokeTest(TEST_PATH + "[320]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs321() throws Exception {
        xqts.invokeTest(TEST_PATH + "[321]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs322() throws Exception {
        xqts.invokeTest(TEST_PATH + "[322]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs323() throws Exception {
        xqts.invokeTest(TEST_PATH + "[323]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs324() throws Exception {
        xqts.invokeTest(TEST_PATH + "[324]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs325() throws Exception {
        xqts.invokeTest(TEST_PATH + "[325]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs326() throws Exception {
        xqts.invokeTest(TEST_PATH + "[326]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs327() throws Exception {
        xqts.invokeTest(TEST_PATH + "[327]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs328() throws Exception {
        xqts.invokeTest(TEST_PATH + "[328]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs329() throws Exception {
        xqts.invokeTest(TEST_PATH + "[329]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs330() throws Exception {
        xqts.invokeTest(TEST_PATH + "[330]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs331() throws Exception {
        xqts.invokeTest(TEST_PATH + "[331]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs332() throws Exception {
        xqts.invokeTest(TEST_PATH + "[332]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs333() throws Exception {
        xqts.invokeTest(TEST_PATH + "[333]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs334() throws Exception {
        xqts.invokeTest(TEST_PATH + "[334]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs335() throws Exception {
        xqts.invokeTest(TEST_PATH + "[335]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs336() throws Exception {
        xqts.invokeTest(TEST_PATH + "[336]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs337() throws Exception {
        xqts.invokeTest(TEST_PATH + "[337]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs338() throws Exception {
        xqts.invokeTest(TEST_PATH + "[338]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs339() throws Exception {
        xqts.invokeTest(TEST_PATH + "[339]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs340() throws Exception {
        xqts.invokeTest(TEST_PATH + "[340]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs341() throws Exception {
        xqts.invokeTest(TEST_PATH + "[341]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs342() throws Exception {
        xqts.invokeTest(TEST_PATH + "[342]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs343() throws Exception {
        xqts.invokeTest(TEST_PATH + "[343]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs344() throws Exception {
        xqts.invokeTest(TEST_PATH + "[344]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs345() throws Exception {
        xqts.invokeTest(TEST_PATH + "[345]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs346() throws Exception {
        xqts.invokeTest(TEST_PATH + "[346]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs347() throws Exception {
        xqts.invokeTest(TEST_PATH + "[347]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs348() throws Exception {
        xqts.invokeTest(TEST_PATH + "[348]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs349() throws Exception {
        xqts.invokeTest(TEST_PATH + "[349]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs350() throws Exception {
        xqts.invokeTest(TEST_PATH + "[350]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs351() throws Exception {
        xqts.invokeTest(TEST_PATH + "[351]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs352() throws Exception {
        xqts.invokeTest(TEST_PATH + "[352]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs353() throws Exception {
        xqts.invokeTest(TEST_PATH + "[353]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs354() throws Exception {
        xqts.invokeTest(TEST_PATH + "[354]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs355() throws Exception {
        xqts.invokeTest(TEST_PATH + "[355]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs356() throws Exception {
        xqts.invokeTest(TEST_PATH + "[356]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs357() throws Exception {
        xqts.invokeTest(TEST_PATH + "[357]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs358() throws Exception {
        xqts.invokeTest(TEST_PATH + "[358]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs359() throws Exception {
        xqts.invokeTest(TEST_PATH + "[359]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs360() throws Exception {
        xqts.invokeTest(TEST_PATH + "[360]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs361() throws Exception {
        xqts.invokeTest(TEST_PATH + "[361]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs362() throws Exception {
        xqts.invokeTest(TEST_PATH + "[362]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs363() throws Exception {
        xqts.invokeTest(TEST_PATH + "[363]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs364() throws Exception {
        xqts.invokeTest(TEST_PATH + "[364]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs365() throws Exception {
        xqts.invokeTest(TEST_PATH + "[365]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs366() throws Exception {
        xqts.invokeTest(TEST_PATH + "[366]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs367() throws Exception {
        xqts.invokeTest(TEST_PATH + "[367]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs368() throws Exception {
        xqts.invokeTest(TEST_PATH + "[368]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs369() throws Exception {
        xqts.invokeTest(TEST_PATH + "[369]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs370() throws Exception {
        xqts.invokeTest(TEST_PATH + "[370]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs371() throws Exception {
        xqts.invokeTest(TEST_PATH + "[371]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs372() throws Exception {
        xqts.invokeTest(TEST_PATH + "[372]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs373() throws Exception {
        xqts.invokeTest(TEST_PATH + "[373]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs374() throws Exception {
        xqts.invokeTest(TEST_PATH + "[374]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs375() throws Exception {
        xqts.invokeTest(TEST_PATH + "[375]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs376() throws Exception {
        xqts.invokeTest(TEST_PATH + "[376]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs377() throws Exception {
        xqts.invokeTest(TEST_PATH + "[377]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs378() throws Exception {
        xqts.invokeTest(TEST_PATH + "[378]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs379() throws Exception {
        xqts.invokeTest(TEST_PATH + "[379]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs380() throws Exception {
        xqts.invokeTest(TEST_PATH + "[380]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs381() throws Exception {
        xqts.invokeTest(TEST_PATH + "[381]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs382() throws Exception {
        xqts.invokeTest(TEST_PATH + "[382]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs383() throws Exception {
        xqts.invokeTest(TEST_PATH + "[383]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs384() throws Exception {
        xqts.invokeTest(TEST_PATH + "[384]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs385() throws Exception {
        xqts.invokeTest(TEST_PATH + "[385]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs386() throws Exception {
        xqts.invokeTest(TEST_PATH + "[386]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs387() throws Exception {
        xqts.invokeTest(TEST_PATH + "[387]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs388() throws Exception {
        xqts.invokeTest(TEST_PATH + "[388]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs389() throws Exception {
        xqts.invokeTest(TEST_PATH + "[389]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs390() throws Exception {
        xqts.invokeTest(TEST_PATH + "[390]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs391() throws Exception {
        xqts.invokeTest(TEST_PATH + "[391]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs392() throws Exception {
        xqts.invokeTest(TEST_PATH + "[392]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs393() throws Exception {
        xqts.invokeTest(TEST_PATH + "[393]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs394() throws Exception {
        xqts.invokeTest(TEST_PATH + "[394]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs395() throws Exception {
        xqts.invokeTest(TEST_PATH + "[395]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs396() throws Exception {
        xqts.invokeTest(TEST_PATH + "[396]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs397() throws Exception {
        xqts.invokeTest(TEST_PATH + "[397]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs398() throws Exception {
        xqts.invokeTest(TEST_PATH + "[398]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs399() throws Exception {
        xqts.invokeTest(TEST_PATH + "[399]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs400() throws Exception {
        xqts.invokeTest(TEST_PATH + "[400]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs401() throws Exception {
        xqts.invokeTest(TEST_PATH + "[401]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs402() throws Exception {
        xqts.invokeTest(TEST_PATH + "[402]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs403() throws Exception {
        xqts.invokeTest(TEST_PATH + "[403]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs404() throws Exception {
        xqts.invokeTest(TEST_PATH + "[404]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs405() throws Exception {
        xqts.invokeTest(TEST_PATH + "[405]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs406() throws Exception {
        xqts.invokeTest(TEST_PATH + "[406]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs407() throws Exception {
        xqts.invokeTest(TEST_PATH + "[407]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs408() throws Exception {
        xqts.invokeTest(TEST_PATH + "[408]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs409() throws Exception {
        xqts.invokeTest(TEST_PATH + "[409]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs410() throws Exception {
        xqts.invokeTest(TEST_PATH + "[410]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs411() throws Exception {
        xqts.invokeTest(TEST_PATH + "[411]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs412() throws Exception {
        xqts.invokeTest(TEST_PATH + "[412]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs413() throws Exception {
        xqts.invokeTest(TEST_PATH + "[413]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs414() throws Exception {
        xqts.invokeTest(TEST_PATH + "[414]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs415() throws Exception {
        xqts.invokeTest(TEST_PATH + "[415]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs416() throws Exception {
        xqts.invokeTest(TEST_PATH + "[416]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs417() throws Exception {
        xqts.invokeTest(TEST_PATH + "[417]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs418() throws Exception {
        xqts.invokeTest(TEST_PATH + "[418]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs419() throws Exception {
        xqts.invokeTest(TEST_PATH + "[419]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs420() throws Exception {
        xqts.invokeTest(TEST_PATH + "[420]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs421() throws Exception {
        xqts.invokeTest(TEST_PATH + "[421]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs422() throws Exception {
        xqts.invokeTest(TEST_PATH + "[422]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs423() throws Exception {
        xqts.invokeTest(TEST_PATH + "[423]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs424() throws Exception {
        xqts.invokeTest(TEST_PATH + "[424]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs425() throws Exception {
        xqts.invokeTest(TEST_PATH + "[425]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs426() throws Exception {
        xqts.invokeTest(TEST_PATH + "[426]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs427() throws Exception {
        xqts.invokeTest(TEST_PATH + "[427]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs428() throws Exception {
        xqts.invokeTest(TEST_PATH + "[428]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs429() throws Exception {
        xqts.invokeTest(TEST_PATH + "[429]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs430() throws Exception {
        xqts.invokeTest(TEST_PATH + "[430]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs431() throws Exception {
        xqts.invokeTest(TEST_PATH + "[431]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs432() throws Exception {
        xqts.invokeTest(TEST_PATH + "[432]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs433() throws Exception {
        xqts.invokeTest(TEST_PATH + "[433]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs434() throws Exception {
        xqts.invokeTest(TEST_PATH + "[434]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs435() throws Exception {
        xqts.invokeTest(TEST_PATH + "[435]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs436() throws Exception {
        xqts.invokeTest(TEST_PATH + "[436]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs437() throws Exception {
        xqts.invokeTest(TEST_PATH + "[437]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs438() throws Exception {
        xqts.invokeTest(TEST_PATH + "[438]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs439() throws Exception {
        xqts.invokeTest(TEST_PATH + "[439]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs440() throws Exception {
        xqts.invokeTest(TEST_PATH + "[440]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs441() throws Exception {
        xqts.invokeTest(TEST_PATH + "[441]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs442() throws Exception {
        xqts.invokeTest(TEST_PATH + "[442]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs443() throws Exception {
        xqts.invokeTest(TEST_PATH + "[443]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs444() throws Exception {
        xqts.invokeTest(TEST_PATH + "[444]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs445() throws Exception {
        xqts.invokeTest(TEST_PATH + "[445]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs446() throws Exception {
        xqts.invokeTest(TEST_PATH + "[446]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs447() throws Exception {
        xqts.invokeTest(TEST_PATH + "[447]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs448() throws Exception {
        xqts.invokeTest(TEST_PATH + "[448]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs449() throws Exception {
        xqts.invokeTest(TEST_PATH + "[449]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs450() throws Exception {
        xqts.invokeTest(TEST_PATH + "[450]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs451() throws Exception {
        xqts.invokeTest(TEST_PATH + "[451]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs452() throws Exception {
        xqts.invokeTest(TEST_PATH + "[452]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs453() throws Exception {
        xqts.invokeTest(TEST_PATH + "[453]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs454() throws Exception {
        xqts.invokeTest(TEST_PATH + "[454]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs455() throws Exception {
        xqts.invokeTest(TEST_PATH + "[455]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs456() throws Exception {
        xqts.invokeTest(TEST_PATH + "[456]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs457() throws Exception {
        xqts.invokeTest(TEST_PATH + "[457]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs458() throws Exception {
        xqts.invokeTest(TEST_PATH + "[458]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs459() throws Exception {
        xqts.invokeTest(TEST_PATH + "[459]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs460() throws Exception {
        xqts.invokeTest(TEST_PATH + "[460]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs461() throws Exception {
        xqts.invokeTest(TEST_PATH + "[461]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs462() throws Exception {
        xqts.invokeTest(TEST_PATH + "[462]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs463() throws Exception {
        xqts.invokeTest(TEST_PATH + "[463]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs464() throws Exception {
        xqts.invokeTest(TEST_PATH + "[464]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs465() throws Exception {
        xqts.invokeTest(TEST_PATH + "[465]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs466() throws Exception {
        xqts.invokeTest(TEST_PATH + "[466]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs467() throws Exception {
        xqts.invokeTest(TEST_PATH + "[467]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs468() throws Exception {
        xqts.invokeTest(TEST_PATH + "[468]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs469() throws Exception {
        xqts.invokeTest(TEST_PATH + "[469]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs470() throws Exception {
        xqts.invokeTest(TEST_PATH + "[470]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs471() throws Exception {
        xqts.invokeTest(TEST_PATH + "[471]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs472() throws Exception {
        xqts.invokeTest(TEST_PATH + "[472]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs473() throws Exception {
        xqts.invokeTest(TEST_PATH + "[473]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs474() throws Exception {
        xqts.invokeTest(TEST_PATH + "[474]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs475() throws Exception {
        xqts.invokeTest(TEST_PATH + "[475]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs476() throws Exception {
        xqts.invokeTest(TEST_PATH + "[476]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs477() throws Exception {
        xqts.invokeTest(TEST_PATH + "[477]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs478() throws Exception {
        xqts.invokeTest(TEST_PATH + "[478]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs479() throws Exception {
        xqts.invokeTest(TEST_PATH + "[479]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs480() throws Exception {
        xqts.invokeTest(TEST_PATH + "[480]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs481() throws Exception {
        xqts.invokeTest(TEST_PATH + "[481]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs482() throws Exception {
        xqts.invokeTest(TEST_PATH + "[482]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs483() throws Exception {
        xqts.invokeTest(TEST_PATH + "[483]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs484() throws Exception {
        xqts.invokeTest(TEST_PATH + "[484]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs485() throws Exception {
        xqts.invokeTest(TEST_PATH + "[485]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs486() throws Exception {
        xqts.invokeTest(TEST_PATH + "[486]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs487() throws Exception {
        xqts.invokeTest(TEST_PATH + "[487]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs488() throws Exception {
        xqts.invokeTest(TEST_PATH + "[488]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs489() throws Exception {
        xqts.invokeTest(TEST_PATH + "[489]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs490() throws Exception {
        xqts.invokeTest(TEST_PATH + "[490]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs491() throws Exception {
        xqts.invokeTest(TEST_PATH + "[491]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs492() throws Exception {
        xqts.invokeTest(TEST_PATH + "[492]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs493() throws Exception {
        xqts.invokeTest(TEST_PATH + "[493]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs494() throws Exception {
        xqts.invokeTest(TEST_PATH + "[494]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs495() throws Exception {
        xqts.invokeTest(TEST_PATH + "[495]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs496() throws Exception {
        xqts.invokeTest(TEST_PATH + "[496]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs497() throws Exception {
        xqts.invokeTest(TEST_PATH + "[497]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs498() throws Exception {
        xqts.invokeTest(TEST_PATH + "[498]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs499() throws Exception {
        xqts.invokeTest(TEST_PATH + "[499]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs500() throws Exception {
        xqts.invokeTest(TEST_PATH + "[500]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs501() throws Exception {
        xqts.invokeTest(TEST_PATH + "[501]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs502() throws Exception {
        xqts.invokeTest(TEST_PATH + "[502]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs503() throws Exception {
        xqts.invokeTest(TEST_PATH + "[503]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs504() throws Exception {
        xqts.invokeTest(TEST_PATH + "[504]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs505() throws Exception {
        xqts.invokeTest(TEST_PATH + "[505]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs506() throws Exception {
        xqts.invokeTest(TEST_PATH + "[506]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs507() throws Exception {
        xqts.invokeTest(TEST_PATH + "[507]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs508() throws Exception {
        xqts.invokeTest(TEST_PATH + "[508]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs509() throws Exception {
        xqts.invokeTest(TEST_PATH + "[509]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs510() throws Exception {
        xqts.invokeTest(TEST_PATH + "[510]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs511() throws Exception {
        xqts.invokeTest(TEST_PATH + "[511]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs512() throws Exception {
        xqts.invokeTest(TEST_PATH + "[512]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs513() throws Exception {
        xqts.invokeTest(TEST_PATH + "[513]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs514() throws Exception {
        xqts.invokeTest(TEST_PATH + "[514]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs515() throws Exception {
        xqts.invokeTest(TEST_PATH + "[515]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs516() throws Exception {
        xqts.invokeTest(TEST_PATH + "[516]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs517() throws Exception {
        xqts.invokeTest(TEST_PATH + "[517]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs518() throws Exception {
        xqts.invokeTest(TEST_PATH + "[518]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs519() throws Exception {
        xqts.invokeTest(TEST_PATH + "[519]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs520() throws Exception {
        xqts.invokeTest(TEST_PATH + "[520]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs521() throws Exception {
        xqts.invokeTest(TEST_PATH + "[521]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs522() throws Exception {
        xqts.invokeTest(TEST_PATH + "[522]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs523() throws Exception {
        xqts.invokeTest(TEST_PATH + "[523]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs524() throws Exception {
        xqts.invokeTest(TEST_PATH + "[524]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs525() throws Exception {
        xqts.invokeTest(TEST_PATH + "[525]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs526() throws Exception {
        xqts.invokeTest(TEST_PATH + "[526]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs527() throws Exception {
        xqts.invokeTest(TEST_PATH + "[527]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs528() throws Exception {
        xqts.invokeTest(TEST_PATH + "[528]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs529() throws Exception {
        xqts.invokeTest(TEST_PATH + "[529]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs530() throws Exception {
        xqts.invokeTest(TEST_PATH + "[530]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs531() throws Exception {
        xqts.invokeTest(TEST_PATH + "[531]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs532() throws Exception {
        xqts.invokeTest(TEST_PATH + "[532]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs533() throws Exception {
        xqts.invokeTest(TEST_PATH + "[533]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs534() throws Exception {
        xqts.invokeTest(TEST_PATH + "[534]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs535() throws Exception {
        xqts.invokeTest(TEST_PATH + "[535]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs536() throws Exception {
        xqts.invokeTest(TEST_PATH + "[536]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs537() throws Exception {
        xqts.invokeTest(TEST_PATH + "[537]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs538() throws Exception {
        xqts.invokeTest(TEST_PATH + "[538]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs539() throws Exception {
        xqts.invokeTest(TEST_PATH + "[539]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs540() throws Exception {
        xqts.invokeTest(TEST_PATH + "[540]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs541() throws Exception {
        xqts.invokeTest(TEST_PATH + "[541]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs542() throws Exception {
        xqts.invokeTest(TEST_PATH + "[542]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs543() throws Exception {
        xqts.invokeTest(TEST_PATH + "[543]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs544() throws Exception {
        xqts.invokeTest(TEST_PATH + "[544]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs545() throws Exception {
        xqts.invokeTest(TEST_PATH + "[545]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs546() throws Exception {
        xqts.invokeTest(TEST_PATH + "[546]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs547() throws Exception {
        xqts.invokeTest(TEST_PATH + "[547]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs548() throws Exception {
        xqts.invokeTest(TEST_PATH + "[548]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs549() throws Exception {
        xqts.invokeTest(TEST_PATH + "[549]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs550() throws Exception {
        xqts.invokeTest(TEST_PATH + "[550]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs551() throws Exception {
        xqts.invokeTest(TEST_PATH + "[551]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs552() throws Exception {
        xqts.invokeTest(TEST_PATH + "[552]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs553() throws Exception {
        xqts.invokeTest(TEST_PATH + "[553]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs554() throws Exception {
        xqts.invokeTest(TEST_PATH + "[554]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs555() throws Exception {
        xqts.invokeTest(TEST_PATH + "[555]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs556() throws Exception {
        xqts.invokeTest(TEST_PATH + "[556]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs557() throws Exception {
        xqts.invokeTest(TEST_PATH + "[557]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs558() throws Exception {
        xqts.invokeTest(TEST_PATH + "[558]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs559() throws Exception {
        xqts.invokeTest(TEST_PATH + "[559]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs560() throws Exception {
        xqts.invokeTest(TEST_PATH + "[560]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs561() throws Exception {
        xqts.invokeTest(TEST_PATH + "[561]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs562() throws Exception {
        xqts.invokeTest(TEST_PATH + "[562]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs563() throws Exception {
        xqts.invokeTest(TEST_PATH + "[563]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs564() throws Exception {
        xqts.invokeTest(TEST_PATH + "[564]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs565() throws Exception {
        xqts.invokeTest(TEST_PATH + "[565]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs566() throws Exception {
        xqts.invokeTest(TEST_PATH + "[566]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs567() throws Exception {
        xqts.invokeTest(TEST_PATH + "[567]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs568() throws Exception {
        xqts.invokeTest(TEST_PATH + "[568]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs569() throws Exception {
        xqts.invokeTest(TEST_PATH + "[569]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs570() throws Exception {
        xqts.invokeTest(TEST_PATH + "[570]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs571() throws Exception {
        xqts.invokeTest(TEST_PATH + "[571]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs572() throws Exception {
        xqts.invokeTest(TEST_PATH + "[572]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs573() throws Exception {
        xqts.invokeTest(TEST_PATH + "[573]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs574() throws Exception {
        xqts.invokeTest(TEST_PATH + "[574]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs575() throws Exception {
        xqts.invokeTest(TEST_PATH + "[575]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs576() throws Exception {
        xqts.invokeTest(TEST_PATH + "[576]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs577() throws Exception {
        xqts.invokeTest(TEST_PATH + "[577]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs578() throws Exception {
        xqts.invokeTest(TEST_PATH + "[578]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs579() throws Exception {
        xqts.invokeTest(TEST_PATH + "[579]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs580() throws Exception {
        xqts.invokeTest(TEST_PATH + "[580]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs581() throws Exception {
        xqts.invokeTest(TEST_PATH + "[581]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs582() throws Exception {
        xqts.invokeTest(TEST_PATH + "[582]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs583() throws Exception {
        xqts.invokeTest(TEST_PATH + "[583]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs584() throws Exception {
        xqts.invokeTest(TEST_PATH + "[584]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs585() throws Exception {
        xqts.invokeTest(TEST_PATH + "[585]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs586() throws Exception {
        xqts.invokeTest(TEST_PATH + "[586]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs587() throws Exception {
        xqts.invokeTest(TEST_PATH + "[587]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs588() throws Exception {
        xqts.invokeTest(TEST_PATH + "[588]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs589() throws Exception {
        xqts.invokeTest(TEST_PATH + "[589]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs590() throws Exception {
        xqts.invokeTest(TEST_PATH + "[590]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs591() throws Exception {
        xqts.invokeTest(TEST_PATH + "[591]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs592() throws Exception {
        xqts.invokeTest(TEST_PATH + "[592]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs593() throws Exception {
        xqts.invokeTest(TEST_PATH + "[593]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs594() throws Exception {
        xqts.invokeTest(TEST_PATH + "[594]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs595() throws Exception {
        xqts.invokeTest(TEST_PATH + "[595]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs596() throws Exception {
        xqts.invokeTest(TEST_PATH + "[596]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs597() throws Exception {
        xqts.invokeTest(TEST_PATH + "[597]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs598() throws Exception {
        xqts.invokeTest(TEST_PATH + "[598]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs599() throws Exception {
        xqts.invokeTest(TEST_PATH + "[599]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs600() throws Exception {
        xqts.invokeTest(TEST_PATH + "[600]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs601() throws Exception {
        xqts.invokeTest(TEST_PATH + "[601]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs602() throws Exception {
        xqts.invokeTest(TEST_PATH + "[602]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs603() throws Exception {
        xqts.invokeTest(TEST_PATH + "[603]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs604() throws Exception {
        xqts.invokeTest(TEST_PATH + "[604]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs605() throws Exception {
        xqts.invokeTest(TEST_PATH + "[605]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs606() throws Exception {
        xqts.invokeTest(TEST_PATH + "[606]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs607() throws Exception {
        xqts.invokeTest(TEST_PATH + "[607]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs608() throws Exception {
        xqts.invokeTest(TEST_PATH + "[608]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs609() throws Exception {
        xqts.invokeTest(TEST_PATH + "[609]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs610() throws Exception {
        xqts.invokeTest(TEST_PATH + "[610]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs611() throws Exception {
        xqts.invokeTest(TEST_PATH + "[611]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs612() throws Exception {
        xqts.invokeTest(TEST_PATH + "[612]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs613() throws Exception {
        xqts.invokeTest(TEST_PATH + "[613]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs614() throws Exception {
        xqts.invokeTest(TEST_PATH + "[614]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs615() throws Exception {
        xqts.invokeTest(TEST_PATH + "[615]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs616() throws Exception {
        xqts.invokeTest(TEST_PATH + "[616]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs617() throws Exception {
        xqts.invokeTest(TEST_PATH + "[617]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs618() throws Exception {
        xqts.invokeTest(TEST_PATH + "[618]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs619() throws Exception {
        xqts.invokeTest(TEST_PATH + "[619]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs620() throws Exception {
        xqts.invokeTest(TEST_PATH + "[620]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs621() throws Exception {
        xqts.invokeTest(TEST_PATH + "[621]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs622() throws Exception {
        xqts.invokeTest(TEST_PATH + "[622]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs623() throws Exception {
        xqts.invokeTest(TEST_PATH + "[623]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs624() throws Exception {
        xqts.invokeTest(TEST_PATH + "[624]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs625() throws Exception {
        xqts.invokeTest(TEST_PATH + "[625]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs626() throws Exception {
        xqts.invokeTest(TEST_PATH + "[626]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs627() throws Exception {
        xqts.invokeTest(TEST_PATH + "[627]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs628() throws Exception {
        xqts.invokeTest(TEST_PATH + "[628]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs629() throws Exception {
        xqts.invokeTest(TEST_PATH + "[629]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs630() throws Exception {
        xqts.invokeTest(TEST_PATH + "[630]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs631() throws Exception {
        xqts.invokeTest(TEST_PATH + "[631]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs632() throws Exception {
        xqts.invokeTest(TEST_PATH + "[632]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs633() throws Exception {
        xqts.invokeTest(TEST_PATH + "[633]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs634() throws Exception {
        xqts.invokeTest(TEST_PATH + "[634]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs635() throws Exception {
        xqts.invokeTest(TEST_PATH + "[635]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs636() throws Exception {
        xqts.invokeTest(TEST_PATH + "[636]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs637() throws Exception {
        xqts.invokeTest(TEST_PATH + "[637]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs638() throws Exception {
        xqts.invokeTest(TEST_PATH + "[638]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs639() throws Exception {
        xqts.invokeTest(TEST_PATH + "[639]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs640() throws Exception {
        xqts.invokeTest(TEST_PATH + "[640]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs641() throws Exception {
        xqts.invokeTest(TEST_PATH + "[641]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs642() throws Exception {
        xqts.invokeTest(TEST_PATH + "[642]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs643() throws Exception {
        xqts.invokeTest(TEST_PATH + "[643]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs644() throws Exception {
        xqts.invokeTest(TEST_PATH + "[644]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs645() throws Exception {
        xqts.invokeTest(TEST_PATH + "[645]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs646() throws Exception {
        xqts.invokeTest(TEST_PATH + "[646]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs647() throws Exception {
        xqts.invokeTest(TEST_PATH + "[647]");
    }

    @org.junit.Test(timeout=300000)
    public void testCastableAs648() throws Exception {
        xqts.invokeTest(TEST_PATH + "[648]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[649]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[650]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[651]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[652]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[653]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[654]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[655]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[656]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[657]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[658]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[659]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[660]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[661]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[662]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[663]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[664]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[665]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[666]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[667]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[668]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[669]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[670]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[671]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[672]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[673]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[674]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[675]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[676]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[677]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[678]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[679]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[680]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[681]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[682]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[683]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[684]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[685]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqExprCastable38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[686]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqExprCastable1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[687]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqExprCastable2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[688]");
    }

}