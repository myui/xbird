/*
 * @(#)$Id:IStringChunk.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.IOException;

import xbird.storage.DbCollection;
import xbird.util.resource.PropertyMap;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public interface IStringChunk extends Externalizable, Closeable {

    static final String STRING_CHUNK_FILE_SUFFIX = ".strc";
    static final String KEY_STRPOOL_WRITTEN = "sp_written";
    static final String KEY_CHUNK_WRITTEN = "cc_written";
    static final String KEY_CHUNK_POINTER = "cc_pointer";

    public int getAndIncrementReferenceCount();

    public long store(char[] ch, int start, int length);

    public long store(String s);

    public void get(long addr, StringBuilder sb);

    public String getString(long addr);

    public long getBufferAddress(long addr);

    public void flush(DbCollection coll, String docName, PropertyMap properties) throws IOException;

}