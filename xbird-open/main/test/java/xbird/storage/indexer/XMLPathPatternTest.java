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
package xbird.storage.indexer;

import junit.framework.TestCase;

import org.junit.Assert;

import xbird.util.xml.NamespaceBinder;
import xbird.xquery.meta.StaticContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XMLPathPatternTest extends TestCase {

    public XMLPathPatternTest(String name) {
        super(name);
    }
    
    public void testSimpleMatch1() {
        match("/aaa/bbb/ccc", "/aaa/bbb/ccc", true);
        match("/aaa/ccc/ccc", "/aaa/ccc/ccc", true);
        match("/aaa/ccc/ccc", "/bbb/aaa/ccc/cccc", false);
    }
    
    public void testDescMatch1() {
        match("/aaa//ccc", "/aaa/bbb/ccc", true);
        match("/aaa//ccc", "/aaa/ccc/ccc", true);
        match("//ccc/cccc", "/bbb/aaa/ccc/cccc", true);
        match("//cccc", "/bbb/aaa/ccc/cccc", true);
        match("/bbb//ccc/aaa/cccc", "/bbb/aaa/ccc/cccc", false);
    }

    public void testWildcardMatch1() {
        match("/*//ccc", "/aaa/bbb/ccc", true);
        match("/aaa/*/*/ccc", "/aaa/ccc/ccc", false);
        match("/aaa/*/ccc", "/aaa/ccc/ccc", true);
        match("/aaa/ccc/*", "/aaa/ccc/ccc", true);
        match("//*/cccc", "/bbb/aaa/ccc/cccc", true);
        match("//aaa/*", "/bbb/aaa/ccc/cccc", false);
    }
    
    private void match(String ptnStr1, String ptnStr2, boolean expected) {
        StaticContext statEnv = new StaticContext();
        NamespaceBinder namespaces = statEnv.getStaticalyKnownNamespaces();
        XMLPathPattern ptn1 = new XMLPathPattern(ptnStr1, namespaces);
        XMLPathPattern ptn2 = new XMLPathPattern(ptnStr2, namespaces);
        Assert.assertSame(ptn1 + " <-> " + ptn2, expected, ptn1.match(ptn2));
    }

}
