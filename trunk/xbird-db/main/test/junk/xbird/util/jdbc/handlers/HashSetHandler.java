/*
 * @(#)$Id: HashSetHandler.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import xbird.util.jdbc.ResultSetHandler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class HashSetHandler<E> extends ArrayListHandler<E> {

    public HashSetHandler() {
        super();
    }

    public Set<E> handle(ResultSet rs) throws SQLException {        
        Collection<E> list = (Collection<E>) super.handle(rs);
        Set<E> ret = new HashSet<E>(list.size());
        ret.addAll(list);
        return ret;
    }

    public static ResultSetHandler getInstance() {
        return Singleton.instance;
    }
    
    /** Lazy instantiation. */
    protected static final class Singleton {
        static final HashSetHandler instance = new HashSetHandler();
    }
    
}
