/*
 * @(#)$Id: ThreadedVariable.java 3881 2008-05-27 23:04:16Z yui $
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
package xbird.xquery.expr.opt;

import java.io.*;

import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.ILock;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ThreadedVariable extends Variable implements Externalizable, Runnable, Evaluable {
    private static final long serialVersionUID = -1978535551819873596L;

    private DynamicContext _dynEnv = null;
    private transient XQueryException _catchedException = null;

    private/* final */transient ILock _lock = new AtomicBackoffLock(true);

    private transient boolean _exported = false;

    public ThreadedVariable(XQExpression value) {
        super(null, value);
        this._type = value.getType();
    }

    public ThreadedVariable() {}// for Externalizable

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this._lock = new AtomicBackoffLock(true);
        this._exported = true;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    public void setDynamicContext(DynamicContext dynEnv) {
        this._dynEnv = dynEnv;
    }

    public void lock() {
        assert (!_lock.isLocked());
        _lock.lock();
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_result != null) {
            return _result;
        }
        final ILock lock = _lock;
        if(_exported && !lock.isLocked()) {
            throw new IllegalStateException("ThreadVariable#" + getName()
                    + " should not called by this thread: " + Thread.currentThread().getName());
        }
        lock.lock();
        try {
            if(_catchedException != null) {
                throw _catchedException;
            }
            final Sequence result = _result;
            if(result == null) {
                throw new XQueryException("Result of ThreadedVariable#" + getName()
                        + " was illegally null");
            }
            return result;
        } finally {
            lock.unlock();
            this._dynEnv = null;
            //this._result = null;
        }
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> seq = this.eval(contextSeq, dynEnv);
        handler.emit(seq);
    }

    public void run() {
        if(_result == null) {
            if(_value == null) {
                throw new IllegalStateException("The value of variable " + this.toString()
                        + " is not defined");
            }
            if(_dynEnv == null) {
                throw new IllegalStateException("DynamicContext is not binded");
            }
            final Sequence<? extends Item> result;
            try {
                result = _value.eval(ValueSequence.EMPTY_SEQUENCE, _dynEnv);
                this._result = result;
            } catch (XQueryException e) {
                this._catchedException = e;
            } finally {
                _lock.unlock();
            }
        }
    }
}
