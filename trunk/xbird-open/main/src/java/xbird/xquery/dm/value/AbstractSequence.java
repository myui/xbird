/*
 * @(#)$Id: AbstractSequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import xbird.util.struct.ThreeLogic;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.meta.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractSequence<E extends Item> implements Sequence<E> {
    private static final long serialVersionUID = 1L;

    protected final DynamicContext _dynEnv;

    public AbstractSequence(DynamicContext dynEnv) {
        this._dynEnv = dynEnv;
    }

    /** -1(nil), 0(empty), 1(not empty) */
    private ThreeLogic isEmptyFlag = ThreeLogic.NIL;

    public boolean isEmpty() {
        if(isEmptyFlag != ThreeLogic.NIL) {
            return isEmptyFlag == ThreeLogic.TRUE;
        }
        final boolean empty = isEmpty(iterator());//FIXME
        isEmptyFlag = (empty ? ThreeLogic.TRUE : ThreeLogic.FALSE);
        return empty;
    }

    private boolean isEmpty(IFocus<E> focus) {
        return (focus.getContextPosition() == 0) ? !focus.hasNext() : true;
    }

    public IFocus<E> iterator() {
        return new Focus<E>(this, _dynEnv);
    }

    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        return (Sequence<? extends Item>) AtomizedSequence.wrap(this, dynEnv);
    }

    public List<E> materialize() {
        final List<E> vs = new ArrayList<E>(512);
        final IFocus<E> itor = iterator();
        for(E it : itor) {
            vs.add(it);
        }
        itor.closeQuietly();
        return vs;
    }

    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        final SAXSerializer ser = new SAXSerializer(new SAXWriter(sw), sw);
        try {
            ser.emit(this);
        } catch (XQueryException e) {
            return "failed at " + e.getStackTrace()[1] + "!\n" + e.getMessage();
        }
        return sw.toString();
    }

}
