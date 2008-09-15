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
package xbird.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AnnotationUtils {

    private AnnotationUtils() {}

    public static <T extends Annotation> T getAnnotation(Class<?> cls, Class<T> annCls) {
        for(Class<?> c = cls; !c.equals(Object.class); c = c.getSuperclass()) {
            T ann = c.getAnnotation(annCls);
            if(ann != null) {
                return ann;
            }
        }
        return null;
    }

    public static void injectFieldResource(@Nullable Object target, @Nonnull Class<? extends Annotation> annCls, @Nonnull ResourceInjector injector)
            throws IllegalArgumentException, IllegalAccessException {
        if(target == null) {
            return;
        }
        for(Class<?> cls = target.getClass(); cls.equals(Object.class) == false; cls = cls.getSuperclass()) {
            for(Field f : cls.getDeclaredFields()) {
                if(f.getName().startsWith("this$")) {// for anonymous class
                    f.setAccessible(true);
                    injectFieldResource(f.get(target), annCls, injector); // recursion
                } else {
                    Annotation ann = f.getAnnotation(annCls);
                    if(ann != null) {
                        injector.inject(f, target, ann);
                    }
                }
            }
        }
    }
}
