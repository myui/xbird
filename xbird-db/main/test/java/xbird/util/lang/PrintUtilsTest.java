/*
 * @(#)$Id: PrintUtilsTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.lang;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import xbird.util.lang.PrintUtils;

public class PrintUtilsTest extends TestCase {

    @Test
    public void testGetOneLineMessage() {
        String src = "fdfdsfsf\nwddsds";
        Throwable th = new Exception(src);
        String expected = Exception.class.getName() + ": fdfdsfsf";
        Assert.assertEquals(expected, PrintUtils.getOneLineMessage(th));
    }

    @Test
    public void testGetOneLineMessage2() {
        String src = "fdfdsfsfnwddsds";
        Throwable th = new Exception(src);
        String expected = Exception.class.getName() + ": " + src;
        Assert.assertEquals(expected, PrintUtils.getOneLineMessage(th));
    }

}
