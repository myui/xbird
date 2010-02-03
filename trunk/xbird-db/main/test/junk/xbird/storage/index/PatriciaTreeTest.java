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
package xbird.storage.index;

import junit.framework.TestCase;

import org.junit.Assert;

import xbird.util.lang.ObjectUtils;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PatriciaTreeTest extends TestCase {

    public void testInsertFind() {
        final PatriciaTrie trie = new PatriciaTrie(4096);

        final byte[] b1 = StringUtils.getBytes("mississippi");
        final byte[] b2 = StringUtils.getBytes("missi");
        final byte[] b3 = StringUtils.getBytes("mississi");
        final byte[] b4 = StringUtils.getBytes("mi");
        final byte[] b5 = StringUtils.getBytes("massissi");
        final byte[] b6 = StringUtils.getBytes("mifdfffff");
        final byte[] b7 = StringUtils.getBytes("amifdfffff");
        final Value v1 = new Value(b1);
        final Value v2 = new Value(b2);
        final Value v3 = new Value(b3);
        final Value v4 = new Value(b4);
        final Value v5 = new Value(b5);
        final Value v6 = new Value(b6);
        final Value v7 = new Value(b7);

        trie.insert(b1, v1);
        trie.insert(b2, v2);
        trie.insert(b3, v3);
        trie.insert(b4, v4);
        trie.insert(b5, v5);
        trie.insert(b6, v6);
        trie.insert(b7, v7);

        Assert.assertEquals(v1, trie.find(b1));
        Assert.assertEquals(v2, trie.find(b2));
        Assert.assertEquals(v3, trie.find(b3));
        Assert.assertEquals(v4, trie.find(b4));
        Assert.assertEquals(v5, trie.find(b5));
        Assert.assertEquals(v6, trie.find(b6));
        Assert.assertEquals(v7, trie.find(b7));

        //System.out.println(trie);
    }

    public void testInsertDelete() {
        final PatriciaTrie trie = new PatriciaTrie(4096);

        final byte[] b1 = StringUtils.getBytes("mississippi");
        final byte[] b2 = StringUtils.getBytes("missi");
        final byte[] b3 = StringUtils.getBytes("mississi");
        final byte[] b4 = StringUtils.getBytes("mi");
        final byte[] b5 = StringUtils.getBytes("massissi");
        final byte[] b6 = StringUtils.getBytes("mifdfffff");
        final byte[] b7 = StringUtils.getBytes("amifdfffff");
        final Value v1 = new Value(b1);
        final Value v2 = new Value(b2);
        final Value v3 = new Value(b3);
        final Value v4 = new Value(b4);
        final Value v5 = new Value(b5);
        final Value v6 = new Value(b6);
        final Value v7 = new Value(b7);

        trie.insert(b1, v1);
        trie.insert(b2, v2);
        trie.insert(b3, v3);
        trie.insert(b4, v4);
        trie.insert(b5, v5);
        trie.insert(b6, v6);
        trie.insert(b7, v7);

        Assert.assertEquals(v1, trie.find(b1));
        Assert.assertEquals(true, trie.delete(b1, v1));
        Assert.assertEquals(false, trie.delete(b1, v1));
        Assert.assertEquals(null, trie.find(b1));
        trie.insert(b1, v1);
        Assert.assertEquals(v1, trie.find(b1));
        Assert.assertEquals(true, trie.delete(b1, v1));
        
        Assert.assertEquals(true, trie.delete(b2, v2));
        Assert.assertEquals(true, trie.delete(b3, v3));
        Assert.assertEquals(true, trie.delete(b4, v4));
        Assert.assertEquals(true, trie.delete(b5, v5));
        Assert.assertEquals(v6, trie.find(b6));
        Assert.assertEquals(true, trie.delete(b7, v7));
        Assert.assertEquals(null, trie.find(b7));
        Assert.assertEquals(false, trie.delete(b6, v1));
        Assert.assertEquals(true, trie.delete(b6, v6));
        Assert.assertEquals(false, trie.delete(b6, v6));
        
        Assert.assertEquals(null, trie.find(b2));
        Assert.assertEquals(null, trie.find(b3));
        Assert.assertEquals(null, trie.find(b4));
        Assert.assertEquals(null, trie.find(b5));
        Assert.assertEquals(null, trie.find(b6));
        Assert.assertEquals(null, trie.find(b7));

        //System.out.println(trie);
    }
    
    public void testCopy() {
        final PatriciaTrie trie = new PatriciaTrie(4096);

        final byte[] b1 = StringUtils.getBytes("mississippi");
        final byte[] b2 = StringUtils.getBytes("missi");
        final byte[] b3 = StringUtils.getBytes("mississi");
        final byte[] b4 = StringUtils.getBytes("mi");
        final byte[] b5 = StringUtils.getBytes("massissi");
        final byte[] b6 = StringUtils.getBytes("mifdfffff");
        final byte[] b7 = StringUtils.getBytes("amifdfffff");
        final Value v1 = new Value(b1);
        final Value v2 = new Value(b2);
        final Value v3 = new Value(b3);
        final Value v4 = new Value(b4);
        final Value v5 = new Value(b5);
        final Value v6 = new Value(b6);
        final Value v7 = new Value(b7);

        trie.insert(b1, v1);
        trie.insert(b2, v2);
        trie.insert(b3, v3);
        trie.insert(b4, v4);
        trie.insert(b5, v5);
        trie.insert(b6, v6);
        trie.insert(b7, v7);
        
        System.out.println(trie);
        
        final PatriciaTrie trie2 = ObjectUtils.deepCopy(trie);

        System.out.println(trie2);
        
        Assert.assertEquals(v1, trie2.find(b1));
        Assert.assertEquals(v2, trie2.find(b2));
        Assert.assertEquals(v3, trie2.find(b3));
        Assert.assertEquals(v4, trie2.find(b4));
        Assert.assertEquals(v5, trie2.find(b5));
        Assert.assertEquals(v6, trie2.find(b6));
        Assert.assertEquals(v7, trie2.find(b7));        
    }
    
    public void testSplit() {
        final PatriciaTrie trie = new PatriciaTrie(4096);
        final byte[] b1 = StringUtils.getBytes("mississippi");
        final byte[] b2 = StringUtils.getBytes("missi");
        final byte[] b3 = StringUtils.getBytes("mississi");
        final byte[] b4 = StringUtils.getBytes("mi");
        final byte[] b5 = StringUtils.getBytes("massissi");
        final byte[] b6 = StringUtils.getBytes("mifdfffff");
        final byte[] b7 = StringUtils.getBytes("amifdfffff");
        final Value v1 = new Value(b1);
        final Value v2 = new Value(b2);
        final Value v3 = new Value(b3);
        final Value v4 = new Value(b4);
        final Value v5 = new Value(b5);
        final Value v6 = new Value(b6);
        final Value v7 = new Value(b7);
        trie.insert(b1, v1);
        trie.insert(b2, v2);
        trie.insert(b3, v3);
        trie.insert(b4, v4);
        trie.insert(b5, v5);
        trie.insert(b6, v6);
        trie.insert(b7, v7);
        
        trie.needSplit();
        trie.split();
    }
}
