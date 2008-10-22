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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

}
