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
package xbird.util.lang;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UnsafeUtils {

    private static final Unsafe unsafe;
    static {
        unsafe = _getUnsafe();
    }

    private UnsafeUtils() {}

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    private static Unsafe _getUnsafe() {
        // Not on bootclasspath
        if(UnsafeUtils.class.getClassLoader() == null) {
            return Unsafe.getUnsafe();
        }
        try {
            final Field fld = Unsafe.class.getDeclaredField("theUnsafe");
            fld.setAccessible(true);
            return (Unsafe) fld.get(UnsafeUtils.class);
        } catch (Exception e) {
            return null;
        }
    }
}
