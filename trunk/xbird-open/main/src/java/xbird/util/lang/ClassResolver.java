/*
 * @(#)$Id: ClassResolver.java 3619 2008-03-26 07:23:03Z yui $
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

import java.lang.reflect.Array;
import java.util.Map;

import xbird.util.collections.SoftHashMap;

/**
 * ClassResolver provides a class caching feature replaces the use of 
 * <code>Class.forName</code> to reduce the cost of dynamic class loading.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja">
 * </DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ClassResolver {

    private static final Map<String, Class> classPool = new SoftHashMap<String, Class>();

    private ClassResolver() {}

    public static Class<?> get(String name) throws ClassNotFoundException {
        Class<?> c = classPool.get(name);
        if(c == null) {
            c = Class.forName(name);
            classPool.put(name, c);
        }
        return c;
    }

    public static void put(Class<?> c) {
        classPool.put(c.getName(), c);
    }

    public static <T extends Class> T getWrapperClass(Class clazz) {
        if(!clazz.isPrimitive()) {
            throw new IllegalStateException("clazz is not primitive type: " + clazz.getSimpleName());
        }
        Object array = Array.newInstance(clazz, 1);
        Object wrapper = Array.get(array, 0);
        return (T) wrapper.getClass();
    }

    public static boolean isPresent(String className) {
        return isPresent(className, getDefaultClassLoader());
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        assert (classLoader != null);
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you absolutely need a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     * 
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl == null) {
            cl = ClassResolver.class.getClassLoader();
        }
        return cl;
    }

}
