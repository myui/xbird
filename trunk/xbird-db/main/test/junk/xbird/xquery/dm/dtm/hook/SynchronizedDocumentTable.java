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
package xbird.xquery.dm.dtm.hooked;

import java.util.concurrent.atomic.AtomicInteger;

import xbird.xquery.dm.dtm.IDocumentTable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class SynchronizedDocumentTable extends DocumentTableDelegate {

    private final AtomicInteger _activeCount = new AtomicInteger(1);

    public SynchronizedDocumentTable(IDocumentTable doc) {
        super(doc);
    }

    public int incrActiveCount() {
        return _activeCount.getAndIncrement();
    }

    public int decrActiveCount() {
        return _activeCount.getAndDecrement();
    }

    @Override
    public long dataAt(long at) {
        if(_activeCount.get() == 0) {
            return super.dataAt(at);
        } else {
            synchronized(this) {
                return super.dataAt(at);
            }
        }
    }

    @Override
    public void getText(long at, StringBuilder sb) {
        if(_activeCount.get() == 0) {
            super.getText(at, sb);
        } else {
            synchronized(this) {
                super.getText(at, sb);
            }
        }
    }

    @Override
    public String getText(long at) {
        if(_activeCount.get() == 0) {
            return super.getText(at);
        } else {
            synchronized(this) {
                return super.getText(at);
            }
        }
    }
    
}
