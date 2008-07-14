/*
 * @(#)$Id: Focus.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.meta;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import xbird.util.iterator.CloseableIterator;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.expr.Terminatable;

/**
 * Focus of current execution context.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#eval_context
 */
public class Focus<E extends Item> implements Serializable, IFocus<E> {
    private static final long serialVersionUID = 4695870262448622687L;

    private final Sequence<E> _src;

    // --------------------------------------------

    private final Iterator<? extends Item> _baseFocus;
    private E _seeked = null;
    private LinkedList<E> _pendings = null; // created on demand
    private boolean _reachedEnd = false;
    private int _position = 0;
    private int _last = -1;

    private transient final List<Terminatable> _terminationHooks = new LinkedList<Terminatable>();

    //--------------------------------------------
    // Formal Semantics variables    

    private E _citem = null;
    private int _cpos = 0;

    //--------------------------------------------

    public Focus(final Sequence<E> src, final DynamicContext dynEnv) {
        this(src, null, dynEnv);
    }

    public Focus(final Sequence<E> src, final Iterator<? extends Item> base, final DynamicContext dynEnv) {
        this._src = src;
        this._baseFocus = base;
        dynEnv.setFocus(this);
    }

    public <T extends Item> Iterator<T> getBaseFocus() {
        return (Iterator<T>) _baseFocus;
    }

    public boolean hasNext() {
        if(_seeked != null) {
            return true;
        }
        final E nextItem = emurateNext();
        if(nextItem != null) {
            this._seeked = nextItem;
            return true;
        } else {
            return false;
        }
    }

    private final E emurateNext() {
        if(_seeked != null) {
            throw new IllegalStateException();
        }
        final E curItem = _citem;
        final boolean hasmore;
        try {
            hasmore = _src.next(this); // this call may replace this._item!
        } catch (XQueryException e) {
            throw new XQRTException(e);
        }
        if(!hasmore) {
            return null;
        }
        if(_seeked != null && _citem != null) {
            if(_pendings == null) {
                this._pendings = new LinkedList<E>();
            }
            _pendings.offer(_seeked);
        }
        final E seeked = _citem;
        this._citem = curItem; // replace back to original current item.
        return seeked;
    }

    public final E next() {
        final E pendingItem = _seeked;
        if(pendingItem != null) {
            this._citem = pendingItem;
            this._seeked = null;
            ++_cpos;
            return pendingItem;
        }
        final boolean hasmore;
        try {
            hasmore = _src.next(this); // this call may replace this._item!
        } catch (XQueryException e) {
            throw new XQRTException(e);
        }
        if(!hasmore) {
            throw new NoSuchElementException();
        }
        ++_cpos;
        return _citem;
    }

    public void remove() {
        throw new UnsupportedOperationException("Focus#remove() is not supported.");
    }

    //--------------------------------------------
    // Formal Semantics interfaces    

    public E getContextItem() {
        return _citem;
    }

    public E setContextItem(E item) {
        if(item == null) {
            throw new IllegalArgumentException();
        }
        if(item instanceof SingleCollection) {
            final IFocus<Item> focus = item.iterator();
            item = (E) focus.next();
        }
        final E prev = _citem;
        this._citem = item;
        return prev;
    }

    public int getContextPosition() {
        return _cpos;
    }

    public void setContextPosition(int pos) {
        this._cpos = pos;
    }

    public void offerItem(E item) {
        if(_seeked == null) {
            this._seeked = item;
        } else {
            if(_pendings == null) {
                this._pendings = new LinkedList<E>();
            }
            _pendings.offer(item);
        }
    }

    public E pollItem() {
        if(_seeked != null) {
            final E ret = _seeked;
            this._seeked = null;
            return ret;
        } else if(_pendings != null) {
            return _pendings.poll();
        }
        return null;
    }

    public boolean reachedEnd() {
        return _reachedEnd;
    }

    public void setReachedEnd(boolean end) {
        this._reachedEnd = end;
    }

    public int getPosition() {
        return _position;
    }

    public int incrPosition() {
        return ++_position;
    }

    public int getLast() {
        return _last;
    }

    public void setLast(int last) {
        this._last = last;
    }

    public final Focus<E> iterator() {
        return this;
    }

    public void close() throws IOException {
        if(!_terminationHooks.isEmpty()) {
            for(Terminatable t : _terminationHooks) {
                t.close();
            }
        }
        final Iterator<? extends Item> base = _baseFocus;
        if(base instanceof CloseableIterator) {
            ((CloseableIterator) base).close();
        }
    }

    public final void closeQuietly() {
        try {
            close();
        } catch (IOException e) {
            ;
        }
    }

    public void addTerminationHook(Terminatable hooked) {
        _terminationHooks.add(hooked);
    }

    @Override
    public String toString() {
        if(_citem == null) {
            return "ContextItem is not set.";
        }
        final StringWriter sw = new StringWriter();
        final SAXWriter writer = new SAXWriter(sw);
        writer.setXMLDeclaration(false);
        final SAXSerializer ser = new SAXSerializer(writer, sw);
        try {
            ser.emit(_citem);
        } catch (XQueryException e) {
            throw new XQRTException("eval failed!: " + e.getMessage());
        }
        return sw.toString();
    }

}
