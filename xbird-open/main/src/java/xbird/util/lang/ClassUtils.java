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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xbird.util.io.FileUtils;
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
        File file = getClassFile(clazz);
        String path = file.getPath();
        final int idx = path.lastIndexOf('!');
        if(idx != -1) {
            String jarFilePath = path.substring(0, idx);
            if(jarFilePath.startsWith("file:\\")) {// workaround for windows
                jarFilePath = jarFilePath.substring(6);
            }
            file = new File(jarFilePath);
        }
        if(file.exists()) {
            return file.lastModified();
        }
        return -1L;
    }

    public static Map<String, File> getInnerClassFiles(@Nonnull Class<?> clazz, boolean includeSelf) {
        final Map<String, File> m = new LinkedHashMap<String, File>(8);
        final File file = getClassFile(clazz);
        if(includeSelf) {
            String clsName = clazz.getName();
            m.put(clsName, file);
        }
        File directory = file.getParentFile();
        String simpleName = clazz.getSimpleName();
        final List<File> list = FileUtils.listFiles(directory, new String[] { simpleName }, new String[] { ".class" }, false);
        for(File f : list) {
            String fname = f.getName();
            int idx = fname.lastIndexOf('.');
            String clsName = fname.substring(0, idx);
            m.put(clsName, f);
        }
        return m;
    }
}
