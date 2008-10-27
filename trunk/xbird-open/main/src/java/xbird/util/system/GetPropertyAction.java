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
package xbird.util.system;

import java.security.PrivilegedAction;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GetPropertyAction implements PrivilegedAction<String> {

    private final String name;
    private final String defaultValue;

    public GetPropertyAction(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public GetPropertyAction(String name) {
        this(name, null);
    }

    public String run() {
        return System.getProperty(name, defaultValue);
    }

}