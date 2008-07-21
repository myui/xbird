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
package xqts.expressions.pathexpr.pathexpressions;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class PathExpressionsTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[9]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public PathExpressionsTest() {
    	super(PathExpressionsTest.class.getName());
        this.xqts = new XQTSTestBase(PathExpressionsTest.class.getName(), TARGET_XQTS_VERSION);
    }

    public void testPathExprErr2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

}