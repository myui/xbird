/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.xquery.dm.coder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Assert;

import xbird.util.datetime.StopWatch;
import xbird.util.lang.ObjectUtils;
import xbird.xquery.*;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQEventEncoderTest extends TestCase {

    private static final String queryFile = "C:/Users/myui/workspace/xbird-db/examples/xqueryd/xmark/local/xpathmark05.xq";

    public void testNormalSequence() throws FileNotFoundException, XQueryException {
        System.out.println(" ============ Materialized ============ ");
        final Sequence<? extends Item> result = doQuery(queryFile);
        final ValueSequence materialized = new ValueSequence(result.materialize(), null);

        StopWatch sw01 = new StopWatch("Sequence encoding time");
        byte[] encoded0 = ObjectUtils.toBytes(materialized);
        System.err.println(sw01);

        System.err.println("encoded size: " + encoded0.length + " bytes");

        StopWatch sw02 = new StopWatch("Sequence decoding time");
        ObjectUtils.readObjectQuietly(encoded0);
        System.err.println(sw02);
    }

    public void testCoding() throws FileNotFoundException, XQueryException {
        System.out.println(" ============ Bulk Mode ============ ");
        invokeTest(false);
    }

    public void testCodingPiped() throws FileNotFoundException, XQueryException {
        System.out.println(" ============ Piped Mode ============ ");
        invokeTest(true);
    }

    public void invokeTest(boolean piped) throws FileNotFoundException, XQueryException {
        final Sequence<? extends Item> result = doQuery(queryFile);

        MarshalledSequence serResult = new MarshalledSequence(result, DynamicContext.DUMMY);        
        serResult.setPiped(piped);

        StopWatch sw1 = new StopWatch("SerializedSequence encoding time");
        byte[] encoded = ObjectUtils.toBytes(serResult);
        System.err.println(sw1);

        System.err.println("encoded size: " + encoded.length + " bytes");

        StopWatch sw2 = new StopWatch("SerializedSequence decoding time");
        Sequence<Item> decodedSeq1 = ObjectUtils.readObjectQuietly(encoded);
        System.err.println(sw2);

        StopWatch sw3 = new StopWatch("IncrementalDecodedSequnece encoding time");
        byte[] encoded2 = ObjectUtils.toBytes(decodedSeq1);
        System.err.println(sw3);

        StopWatch sw4 = new StopWatch("IncrementalDecodedSequnece decoding time");
        Sequence<Item> decodedSeq2 = ObjectUtils.readObjectQuietly(encoded2);
        System.err.println(sw4);

        MarshalledSequence reaccessed2 = (MarshalledSequence) decodedSeq2;
        reaccessed2.setReaccessable(true);
        
        // partial decode
        Iterator<Item> itor2 = decodedSeq2.iterator();
        for(int i = 0; i < 75; i++) {
            itor2.next();
        }

        StopWatch sw5 = new StopWatch("IncrementalDecodedSequnece (partially decoded: 100 items)  encoding time");
        byte[] encoded3 = ObjectUtils.toBytes(decodedSeq2);
        System.err.println(sw5);

        StopWatch sw6 = new StopWatch("IncrementalDecodedSequnece (partially decoded: 100 items) decoding time");
        Sequence<Item> decodedSeq3 = ObjectUtils.readObjectQuietly(encoded3);
        MarshalledSequence reaccessed3 = (MarshalledSequence) decodedSeq3;
        reaccessed3.setReaccessable(true);
        System.err.println(sw6);

        // full decode
        int count1 = 0;
        for(Item it : decodedSeq3) {
            count1++;
        }

        StopWatch sw7 = new StopWatch("!IncrementalDecodedSequnece (full decoded)  encoding time");
        byte[] encoded4 = ObjectUtils.toBytes(decodedSeq3);
        System.err.println(sw7);

        StopWatch sw8 = new StopWatch("!IncrementalDecodedSequnece (full decoded) decoding time");
        Sequence<Item> decodedSeq4 = ObjectUtils.readObjectQuietly(encoded4);
        System.err.println(sw8);

        int count2 = 0;
        for(Item it : decodedSeq4) {
            count2++;
        }
        System.err.println("Total decoded items: " + count2);
        Assert.assertEquals(count1, count2);

        byte[] encoded5 = ObjectUtils.toBytes(reaccessed2);
    }

    private static Sequence<? extends Item> doQuery(String queryFile) throws FileNotFoundException,
            XQueryException {
        XQueryModule xqmod = new XQueryModule();
        XQueryProcessor proc = new XQueryProcessor(xqmod);
        XQueryModule module = proc.parse(new FileInputStream(queryFile));
        return proc.execute(module);
    }
}
