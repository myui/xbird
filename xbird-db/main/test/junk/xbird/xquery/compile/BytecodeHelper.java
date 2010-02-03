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
package xbird.xquery.compile;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BytecodeHelper {

    private BytecodeHelper() {}

    public static String getInternalName(final Class c) {
        return c.getName().replace('.', '/');
    }

    public static String getDescriptor(final Class c) {
        StringBuilder buf = new StringBuilder(128);
        getDescriptor(buf, c);
        return buf.toString();
    }

    private static void getDescriptor(final StringBuilder buf, final Class c) {
        Class d = c;
        while(true) {
            if(d.isPrimitive()) {
                char car;
                if(d == Integer.TYPE) {
                    car = 'I';
                } else if(d == Void.TYPE) {
                    car = 'V';
                } else if(d == Boolean.TYPE) {
                    car = 'Z';
                } else if(d == Byte.TYPE) {
                    car = 'B';
                } else if(d == Character.TYPE) {
                    car = 'C';
                } else if(d == Short.TYPE) {
                    car = 'S';
                } else if(d == Double.TYPE) {
                    car = 'D';
                } else if(d == Float.TYPE) {
                    car = 'F';
                } else /* if (d == Long.TYPE) */{
                    car = 'J';
                }
                buf.append(car);
                return;
            } else if(d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for(int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }

}
