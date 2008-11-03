/*
 * @(#)$Id$
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
package xbird.xquery.dm.value.sequence;

import java.util.ArrayList;
import java.util.List;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.meta.DynamicContext;

import junit.framework.TestCase;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IncrEvalSequenceTest extends TestCase {

    public void testCompute1() throws InterruptedException {
        final int seqsize = 10000;
        final int stocksize = seqsize + 1;
        
        final List<Item> items = new ArrayList<Item>(seqsize);      
        for(int i= 0; i<seqsize; i++) {
            items.add(new XInteger(i));
        }
        final ValueSequence entitySeq = new ValueSequence(items, DynamicContext.DUMMY);
        final IncrEvalSequence incrSeq = new IncrEvalSequence(entitySeq, stocksize, DynamicContext.DUMMY);
        incrSeq.compute();
        
        final MarshalledSequence marSeq = new MarshalledSequence(incrSeq, DynamicContext.DUMMY);
        marSeq.setRedirectable(false);
        
        ObjectUtils.toBytes(marSeq);
    }
    
    public void testCompute2() throws InterruptedException {
        final int seqsize = 10000;
        final int stocksize = 256 + 1;
        
        final List<Item> items = new ArrayList<Item>(seqsize);      
        for(int i= 0; i<seqsize; i++) {
            items.add(new XInteger(i));
        }
        final ValueSequence entitySeq = new ValueSequence(items, DynamicContext.DUMMY);
        final IncrEvalSequence incrSeq = new IncrEvalSequence(entitySeq, stocksize, DynamicContext.DUMMY);
        
        Thread th = new Thread(incrSeq);
        th.start();
        
        Thread.sleep(3000);
        
        final MarshalledSequence marSeq = new MarshalledSequence(incrSeq, DynamicContext.DUMMY);
        marSeq.setRedirectable(false);
        
        ObjectUtils.toBytes(marSeq);
    }
    
    public void testCompute2NoWait() throws InterruptedException {
        final int seqsize = 10000;
        final int stocksize = 256 + 1;
        
        final List<Item> items = new ArrayList<Item>(seqsize);      
        for(int i= 0; i<seqsize; i++) {
            items.add(new XInteger(i));
        }
        final ValueSequence entitySeq = new ValueSequence(items, DynamicContext.DUMMY);
        final IncrEvalSequence incrSeq = new IncrEvalSequence(entitySeq, stocksize, DynamicContext.DUMMY);
       
        final MarshalledSequence marSeq = new MarshalledSequence(incrSeq, DynamicContext.DUMMY);
        marSeq.setRedirectable(false);
        
        Thread th = new Thread(incrSeq);
        th.start();

        ObjectUtils.toBytes(marSeq);
    }
    
    public void testCompute2NoWait2() throws InterruptedException {
        final int seqsize = 10000;
        final int stocksize = 32 + 1;
        
        final List<Item> items = new ArrayList<Item>(seqsize);      
        for(int i= 0; i<seqsize; i++) {
            items.add(new XInteger(i));
        }
        final ValueSequence entitySeq = new ValueSequence(items, DynamicContext.DUMMY);
        final IncrEvalSequence incrSeq = new IncrEvalSequence(entitySeq, stocksize, DynamicContext.DUMMY);
       
        final MarshalledSequence marSeq = new MarshalledSequence(incrSeq, DynamicContext.DUMMY);
        marSeq.setRedirectable(false);
        
        Thread th = new Thread(incrSeq);
        th.start();

        ObjectUtils.toBytes(marSeq);
    }


}
