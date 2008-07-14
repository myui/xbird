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
package xbird.xquery.dm.labeling;

import xbird.util.codec.UTF8Codec;
import xbird.util.collections.IntStack;
import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RevPathCoder implements Cloneable {

    public static final int SLASH = 1;
    public static final int SEPARATOR = 2;
    public static final int ATTRIBUTE = 3;
    public static final int IDENTIFIER_OFFSET = 4;
    private static final int ID_SYSTEM_LIMIT = Integer.MAX_VALUE - IDENTIFIER_OFFSET;

    // controls
    public static final int PERCENT = 0;
    public static final byte PERCENT_CODE = 0x0;

    private final IntStack stack;

    public RevPathCoder() {
        this(new IntStack(12));
    }

    public RevPathCoder(IntStack stack) {
        this.stack = stack;
    }

    public void slash() {
        stack.push(SLASH);
    }

    public void separator() {
        stack.push(SEPARATOR);
    }

    public void separatorSlash() {
        stack.push(SEPARATOR);
        stack.push(SLASH);
    }

    public void separatorPercentSlash() {
        stack.push(SEPARATOR);
        stack.push(PERCENT);
        stack.push(SLASH);
    }

    public void attribute() {
        stack.push(ATTRIBUTE);
    }

    public void identifer(int id) {
        if(id > ID_SYSTEM_LIMIT || id < 0) {
            throw new IllegalArgumentException("Illegal Identifier: " + id);
        }
        stack.push(id + IDENTIFIER_OFFSET);
    }

    public void addAll(final int[] ids) {
        for(int id : ids) {
            stack.push(id);
        }
    }

    public void popUntilNextSeparator() {
        while(!stack.isEmpty()) {
            int poped = stack.pop();
            if(poped == SEPARATOR) {
                break;
            }
        }
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public byte[] encode() {
        int[] revpath = stack.toArray();
        ArrayUtils.reverse(revpath);
        byte[] b = UTF8Codec.encode(revpath);
        return b;
    }

    @Override
    public String toString() {
        return stack.toString();
    }

    @Override
    public RevPathCoder clone() {
        RevPathCoder cloned = new RevPathCoder(stack.clone());
        return cloned;
    }
}
