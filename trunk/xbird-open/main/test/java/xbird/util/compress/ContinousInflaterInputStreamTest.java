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
package xbird.util.compress;

import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import junit.framework.Assert;
import junit.framework.TestCase;
import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ContinousInflaterInputStreamTest extends TestCase {

    public void testContinuous() throws IOException {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream(8192);
        DeflaterOutputStream def1 = new DeflaterOutputStream(out, new Deflater(Deflater.DEFAULT_COMPRESSION, false), 100);
        IOUtils.writeString("1", def1);
        IOUtils.writeInt(2, def1);
        def1.close();
        DeflaterOutputStream def2 = new DeflaterOutputStream(out, new Deflater(Deflater.DEFAULT_COMPRESSION, false), 100);
        IOUtils.writeString("3", def2);
        IOUtils.writeString("4", def2);
        def2.close();
        DeflaterOutputStream def3 = new DeflaterOutputStream(out, new Deflater(Deflater.DEFAULT_COMPRESSION, false), 100);
        IOUtils.writeString("5", def3);
        IOUtils.writeString("6", def3);
        def3.close();

        byte[] b = out.toByteArray();
        FastByteArrayInputStream in = new FastByteArrayInputStream(b);
        ContinousInflaterInputStream inf1 = new ContinousInflaterInputStream(in, new Inflater(false), 8192);
        Assert.assertEquals("1", IOUtils.readString(inf1));
        Assert.assertEquals(2, IOUtils.readInt(inf1));
        Assert.assertEquals("3", IOUtils.readString(inf1));
        Assert.assertEquals("4", IOUtils.readString(inf1));
        Assert.assertEquals("5", IOUtils.readString(inf1));
        Assert.assertEquals("6", IOUtils.readString(inf1));

        Assert.assertEquals(1, inf1.available());
        Assert.assertEquals(-1, inf1.read());
        Assert.assertEquals(0, inf1.available());
    }

    public void testNonContinuous() throws IOException {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream(8192);
        DeflaterOutputStream def1 = new DeflaterOutputStream(out, new Deflater(Deflater.DEFAULT_COMPRESSION, false), 100);
        IOUtils.writeString("1", def1);
        IOUtils.writeInt(2, def1);
        IOUtils.writeString("3", def1);
        IOUtils.writeString("4", def1);
        IOUtils.writeString("5", def1);
        IOUtils.writeString("6", def1);
        def1.close();

        byte[] b = out.toByteArray();
        FastByteArrayInputStream in = new FastByteArrayInputStream(b);
        InflaterInputStream inf1 = new InflaterInputStream(in, new Inflater(false), 8192);
        Assert.assertEquals("1", IOUtils.readString(inf1));
        Assert.assertEquals(2, IOUtils.readInt(inf1));
        Assert.assertEquals("3", IOUtils.readString(inf1));
        Assert.assertEquals("4", IOUtils.readString(inf1));
        Assert.assertEquals("5", IOUtils.readString(inf1));
        Assert.assertEquals("6", IOUtils.readString(inf1));

        Assert.assertEquals(1, inf1.available());
        Assert.assertEquals(-1, inf1.read());
        Assert.assertEquals(0, inf1.available());
    }

}
