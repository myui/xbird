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
package xbird.util.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ClassUtils {

    private ClassUtils() {}

    @Nonnull
    public static String getSimpleClassName(@Nullable Object obj) {
        if(obj == null) {
            return "null";
        }
        return getSimpleClassName(obj.getClass());
    }

    @Nonnull
    public static String getSimpleClassName(@Nonnull Class<?> cls) {
        String className = cls.getName();
        int idx = className.lastIndexOf(".");
        return idx == -1 ? className : className.substring(idx + 1);
    }

    @Nonnull
    public static String getRelativeClassFilePath(@Nonnull String className) {
        return className.replace('.', '/') + ".class";
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    public static File getClassFile(@Nonnull Class<?> clazz) {
        String className = clazz.getName();
        String path = getRelativeClassFilePath(className);
        URL url = clazz.getResource('/' + path);
        String absolutePath = url.getFile();
        String decoded = URLDecoder.decode(absolutePath);
        return new File(decoded);
    }

    public static byte[] getClassAsBytes(@Nonnull Class<?> clazz) throws IOException {
        InputStream is = getClassAsStream(clazz);
        return IOUtils.getBytes(is);
    }

    public static InputStream getClassAsStream(@Nonnull Class<?> clazz) throws IOException {
        String className = clazz.getName();
        String path = getRelativeClassFilePath(className);
        URL url = clazz.getResource('/' + path);
        return url.openStream();
    }

    public static long getLastModified(@Nonnull Class<?> clazz) {
        final File file = getClassFile(clazz);
        if(file.exists()) {
            return file.lastModified();
        }
        return -1L;
    }

}
