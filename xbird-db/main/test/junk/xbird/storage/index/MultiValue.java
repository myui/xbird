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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.util.io.IOUtils;
import xbird.util.lang.PrintUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MultiValue extends Value {
    private static final long serialVersionUID = -6886417349002289178L;
    private static final Log LOG = LogFactory.getLog(MultiValue.class);

    private transient Value[] values;

    public MultiValue() {}

    public MultiValue(Value... values) {
        super(merge(values));
        this.values = values;
    }

    private MultiValue(byte[] b, Value[] values) {
        super(b);
        this.values = values;
    }

    public Value[] getValues() {
        return values;
    }

    private static byte[] merge(Value[] values) {
        final FastByteArrayOutputStream bos = new FastByteArrayOutputStream(2048);
        final int size = values.length;
        try {
            IOUtils.writeInt(size, bos);
            for(Value v : values) {
                IOUtils.writeInt(v.getLength(), bos);
                v.writeTo(bos);
            }
        } catch (IOException e) {
            LOG.error(PrintUtils.prettyPrintStackTrace(e));
            throw new IllegalStateException(e);
        }
        return bos.toByteArray();
    }

    public static MultiValue readFrom(byte[] b) throws IOException {
        final FastByteArrayInputStream bis = new FastByteArrayInputStream(b);
        final int size = IOUtils.readInt(bis);
        final Value[] valueAry = new Value[size];
        for(int i = 0; i < size; i++) {
            int len = IOUtils.readInt(bis);
            byte[] v = new byte[len];
            bis.read(v, 0, len);
            valueAry[i] = new Value(v, 0, len);
        }
        return new MultiValue(b, valueAry);
    }

}
