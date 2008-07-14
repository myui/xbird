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
package xqts.expressions.operators.compexpr.valcomp.durationdatetimeop.durationeq;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class DurationEQTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[61]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public DurationEQTest() {
    	super(DurationEQTest.class.getName());
        this.xqts = new XQTSTestBase(DurationEQTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDurationEqual27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpYearMonthDurationEqual2args20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpDayTimeDurationEqual2args20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testDistinctDurationEqual1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testDistinctDurationEqual2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ22() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ23() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ24() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ25() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ26() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ27() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ28() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ29() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ30() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ31() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ32() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ33() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ34() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ35() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ36() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ37() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ38() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ39() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ40() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ41() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ42() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ43() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ44() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ45() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ46() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ47() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ48() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ49() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ50() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ51() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ52() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ53() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ54() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ55() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ56() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ57() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ58() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ59() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testKDurationEQ60() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

}