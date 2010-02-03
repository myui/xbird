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
package xbird.util.io;

import java.io.*;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294974
 */
public class FastBufferedInputStreamTest extends TestCase {

    public void testAll() throws IOException {
        test("/teradisk/data/xmark10/xmark10.xml.strc");
    }

    private void test(String fileName) throws IOException {
        final int filesize = (int) (new File(fileName).length());
        System.err.println("filesize: " + filesize);
        final FastBufferedInputStream bis = new FastBufferedInputStream(new FileInputStream(fileName));
        final FileInputStream test = new FileInputStream(fileName);
        final Random random = new Random();
        final byte buf1[] = new byte[32768];
        final byte buf2[] = new byte[32768];

        while(true) {
            switch(random.nextInt(4)) {
                case 0: {
                    System.err.println("read()");
                    int a1 = bis.read();
                    int a2 = test.read();
                    Assert.assertTrue("Buffered read() returned " + a1 + " instead of " + a2, a1 == a2);
                    if(a1 == -1) {
                        return;
                    }
                    break;
                }
                case 1: {
                    int off = random.nextInt(buf1.length);
                    int len = random.nextInt(buf1.length - off + 1);
                    int a1 = bis.read(buf1, off, len);
                    int a2 = test.read(buf2, off, len);
                    System.err.println("read(b, " + off + ", " + len + ")");

                    Assert.assertTrue("Buffered read(b, " + off + ", " + len + ") returned " + a1
                            + " instead of " + a2, a1 == a2);

                    for(int i = off; i < off + len; i++) {
                        Assert.assertTrue("Buffered read(b, " + off + ", " + len
                                + ") has a mismatch at position " + i, buf1[i] == buf2[i]);
                    }
                    break;
                }
                case 2: {
                    System.err.println("available()");
                    Assert.assertTrue("available() returned " + bis.available() + " instead of "
                            + test.available(), bis.available() == test.available());
                    break;
                }
                case 3: {
                    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294974
                    int pos = random.nextInt(filesize);
                    int a1 = (int) bis.skip(pos);
                    int a2 = (int) test.skip(pos);
                    System.err.println("skip(" + pos + ")");
                    Assert.assertTrue("skip() returned " + a1 + " instead of " + a2, a1 == a2);
                    break;
                }
            }
        }
    }
}
