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
package xbird.xquery.misc;

import static xbird.xquery.misc.IStringChunk.STRING_CHUNK_FILE_SUFFIX;

import java.io.File;
import java.io.IOException;

import xbird.storage.DbCollection;
import xbird.storage.io.VarSegments;
import xbird.storage.io.VarSegments.DescriptorType;
import xbird.util.resource.PropertyMap;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StringChunkLoader {

    private StringChunkLoader() {}

    public static IStringChunk load(final DbCollection coll) throws IOException {
        PropertyMap props = coll.getCollectionProperties();
        File chunkFile = getChunkFile(coll);
        VarSegments paged = new VarSegments(chunkFile, DescriptorType.hash);
        IStringChunk sc = new PagedStringChunk2(paged, props);
        return sc;
    }

    private static File getChunkFile(final DbCollection coll) {
        String dir = coll.getAbsolutePath();
        String colName = coll.getCollectionName();
        File chunkFile = new File(dir, colName + STRING_CHUNK_FILE_SUFFIX);
        return chunkFile;
    }
}
