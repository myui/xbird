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
package xqts.expressions.operators.arithexpr.durationarith.timesubtractdtd;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class TimeSubtractDTDTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[50]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public TimeSubtractDTDTest() {
    	super(TimeSubtractDTDTest.class.getName());
        this.xqts = new XQTSTestBase(TimeSubtractDTDTest.class.getName(), TARGET_XQTS_VERSION);
    }

    public void testOpSubtractDayTimeDurationFromTime2args1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime2args2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime2args3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime2args4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime2args5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testOpSubtractDayTimeDurationFromTime17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testKTimeSubtractDTD1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testKTimeSubtractDTD2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testKTimeSubtractDTD3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testKTimeSubtractDTD4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testKTimeSubtractDTD5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

}