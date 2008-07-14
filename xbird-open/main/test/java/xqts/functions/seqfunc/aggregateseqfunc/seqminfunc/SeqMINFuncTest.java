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
package xqts.functions.seqfunc.aggregateseqfunc.seqminfunc;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class SeqMINFuncTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[250]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public SeqMINFuncTest() {
    	super(SeqMINFuncTest.class.getName());
        this.xqts = new XQTSTestBase(SeqMINFuncTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testFnMinint1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht1args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht1args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht1args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinint2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinintg2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindec2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMindbl2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinflt2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinlng2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinusht2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnint2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinpint2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinulng2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnpi2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinnni2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMinsht2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMin1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMin2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnMin3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[130]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[131]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[132]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[133]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[134]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[135]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[136]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[137]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[138]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[139]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[140]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[141]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[142]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[143]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[144]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[145]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc39() throws Exception {
        xqts.invokeTest(TEST_PATH + "[146]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc40() throws Exception {
        xqts.invokeTest(TEST_PATH + "[147]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc41() throws Exception {
        xqts.invokeTest(TEST_PATH + "[148]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc42() throws Exception {
        xqts.invokeTest(TEST_PATH + "[149]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc43() throws Exception {
        xqts.invokeTest(TEST_PATH + "[150]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc44() throws Exception {
        xqts.invokeTest(TEST_PATH + "[151]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc45() throws Exception {
        xqts.invokeTest(TEST_PATH + "[152]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc46() throws Exception {
        xqts.invokeTest(TEST_PATH + "[153]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc47() throws Exception {
        xqts.invokeTest(TEST_PATH + "[154]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc48() throws Exception {
        xqts.invokeTest(TEST_PATH + "[155]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc49() throws Exception {
        xqts.invokeTest(TEST_PATH + "[156]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc50() throws Exception {
        xqts.invokeTest(TEST_PATH + "[157]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqMINFunc51() throws Exception {
        xqts.invokeTest(TEST_PATH + "[158]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[159]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[160]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[161]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[162]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[163]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[164]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[165]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[166]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[167]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[168]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[169]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[170]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[171]");
    }

    @org.junit.Test(timeout=300000)
    public void testK2SeqMINFunc14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[172]");
    }

}