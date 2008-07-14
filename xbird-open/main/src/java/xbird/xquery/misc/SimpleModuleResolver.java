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
package xbird.xquery.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SimpleModuleResolver implements ModuleResolver {

    private final Map<String, String> _rule;

    public SimpleModuleResolver() {
        this(new HashMap<String, String>());
    }

    public SimpleModuleResolver(Map<String, String> rule) {
        this._rule = rule;
    }

    public void addMappingRule(String logicalLocation, String physicalLocation) {
        _rule.put(logicalLocation, physicalLocation);
    }

    public String resolveLocation(String logicalLocation) {
        final String resolved = _rule.get(logicalLocation);
        return (resolved == null) ? logicalLocation : resolved;
    }

}
