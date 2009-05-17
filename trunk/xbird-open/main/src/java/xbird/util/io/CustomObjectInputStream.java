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
package xbird.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405@gmail.com)
 */
public final class CustomObjectInputStream extends ObjectInputStream {

    @Nonnull
    private final ClassLoader cl;
    private final boolean initialize;

    public CustomObjectInputStream(InputStream in, @Nonnull ClassLoader cl) throws IOException {
        this(in, cl, true);
    }

    public CustomObjectInputStream(InputStream in, @Nonnull ClassLoader cl, boolean initialize)
            throws IOException {
        super(in);
        this.cl = cl;
        this.initialize = initialize;
        enableResolveObject(true);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {
        String clsName = desc.getName();
        return Class.forName(clsName, initialize, cl);
    }

}
