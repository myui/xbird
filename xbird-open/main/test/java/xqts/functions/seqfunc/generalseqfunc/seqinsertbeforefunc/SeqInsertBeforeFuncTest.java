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
package xqts.functions.seqfunc.generalseqfunc.seqinsertbeforefunc;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class SeqInsertBeforeFuncTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[238]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public SeqInsertBeforeFuncTest() {
    	super(SeqInsertBeforeFuncTest.class.getName());
        this.xqts = new XQTSTestBase(SeqInsertBeforeFuncTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs001() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs002() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs003() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs004() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs005() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs006() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs007() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs008() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs009() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs010() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs011() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs012() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs013() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs014() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs015() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs016() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs017() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs018() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs019() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testFnInsertBeforeMixArgs020() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc5() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc6() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc7() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc8() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc9() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc10() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc11() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc12() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc13() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc14() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc15() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc16() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc17() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc18() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc19() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc20() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testKSeqInsertBeforeFunc21() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

}