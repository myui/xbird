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
package xbird.storage.io;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.junit.Test;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class VarSegmentsBFileTest extends TestCase {

    public VarSegmentsBFileTest(String name) {
        super(name);
    }

    @Test
    public void testWrite() throws IOException {
        File tmpFile = new File("testWrite.tmp");
        tmpFile.deleteOnExit();
        if(tmpFile.exists()) {
            tmpFile.delete();
        }
        System.out.println("tmpFile: " + tmpFile.getAbsolutePath());
        VarSegmentsBFile segment = new VarSegmentsBFile(tmpFile);
        byte[] b0 = new byte[] { 2, 2, 23 };
        segment.write(0, b0);
        byte[] b1 = new byte[] { 2, 1, 24 };
        segment.write(1, b1);
        byte[] b3 = new byte[] { 2, 43, 1 };
        segment.write(3, b3);
        byte[] b2 = new byte[] { 1, 3, 3 };
        segment.write(2, b2);
        byte[] b4 = new byte[] { 6, 1 };
        segment.write(4, b4);
        byte[] b5 = new byte[] { 9, 3, 4 };
        segment.write(5, b5);
        segment.flush(true);
        VarSegmentsBFile readSegment = new VarSegmentsBFile(tmpFile);
        ArrayAssert.assertEquals(b0, readSegment.read(0));
        ArrayAssert.assertEquals(b0, readSegment.read(0));
        ArrayAssert.assertEquals(b1, readSegment.read(1));
        ArrayAssert.assertEquals(b3, readSegment.read(3));
        ArrayAssert.assertEquals(b2, readSegment.read(2));
        ArrayAssert.assertEquals(b4, readSegment.read(4));
        ArrayAssert.assertEquals(b5, readSegment.read(5));
        tmpFile.delete();
    }

    @Test
    public void testWriteUpdate() throws IOException {
        File tmpFile = new File("testWriteUpdate.tmp");
        tmpFile.deleteOnExit();
        if(tmpFile.exists()) {
            assertTrue("delete file failed: " + tmpFile.getAbsolutePath(), tmpFile.delete());
        }
        System.out.println("tmpFile: " + tmpFile.getAbsolutePath());
        VarSegmentsBFile segment = new VarSegmentsBFile(tmpFile);
        byte[] b0 = new byte[] { 2, 2, 23 };
        segment.write(0, b0);
        byte[] b1 = new byte[] { 2, 1, 24 };
        segment.write(1, b1);
        byte[] b3 = new byte[] { 2, 43, 1 };
        segment.write(3, b3);
        byte[] b2 = new byte[] { 1, 3, 3 };
        segment.write(2, b2);
        segment.write(2, b3);
        byte[] b4 = new byte[] { 6, 1 };
        segment.write(4, b4);
        byte[] b5 = new byte[] { 9, 3, 4 };
        segment.write(5, b5);
        segment.write(5, b4);
        segment.flush(true);
        VarSegmentsBFile readSegment = new VarSegmentsBFile(tmpFile);
        ArrayAssert.assertEquals(b0, readSegment.read(0));
        ArrayAssert.assertEquals(b0, readSegment.read(0));
        ArrayAssert.assertEquals(b1, readSegment.read(1));
        ArrayAssert.assertEquals(b3, readSegment.read(3));
        ArrayAssert.assertEquals(b3, readSegment.read(2));
        ArrayAssert.assertEquals(b4, readSegment.read(4));
        readSegment.write(4, b3);
        ArrayAssert.assertEquals(b3, readSegment.read(4));
        ArrayAssert.assertEquals(b4, readSegment.read(5));
        tmpFile.delete();
    }

}
