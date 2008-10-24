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
package xbird.xquery.expr.opt;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.remote.RemoteSequence;
import xbird.util.datetime.StopWatch;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.coder.XQEventDecoder;
import xbird.xquery.dm.coder.XQEventEncoder;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ShippedVariable extends Variable implements Externalizable {
    private static final long serialVersionUID = 7003466767355357342L;
    private static final Log LOG = LogFactory.getLog(ShippedVariable.class);

    private int _identifier = -1;
    private transient boolean _local = true;

    public ShippedVariable(Variable var, int identifier) {
        super(var.getVarName(), var);
        if(_varName == null) {
            throw new IllegalArgumentException();
        }
        if(identifier < 0) {
            throw new IllegalArgumentException("Illegal identifier: " + identifier);
        }
        this._identifier = identifier;
        this._result = var.getResult();
        this._type = var.getType();
    }

    public ShippedVariable(QualifiedName varName, Sequence result) {
        super(varName);
        if(varName == null) {
            throw new IllegalArgumentException();
        }
        if(result == null) {
            throw new IllegalArgumentException();
        }
        this._result = result;
    }

    public ShippedVariable() {// for Externalizable
        super(null);
    }

    public void setLocal(boolean local) {
        this._local = local;
    }

    public int getIdentifier() {
        return _identifier;
    }

    @Override
    public Type getType() {
        final Sequence result = _result;
        if(result == null) {
            return _type;
        } else {
            return _result.getType();
        }
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        Sequence result = _result;
        if(_local) {
            final XQExpression valueExpr = _value;
            if(valueExpr == null) {
                throw new IllegalStateException();
            }
            result = valueExpr.eval(contextSeq, dynEnv);
            this._result = result;
        }
        return result;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_result != null) {
            handler.emit(_result);
        } else {
            _value.evalAsEvents(handler, contextSeq, dynEnv); //TODO REVIEWME required?
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_type);
        out.writeObject(_varName);
        out.writeInt(_identifier);
        final Sequence result = _result;
        if(result == null) {
            out.writeBoolean(false);
            final XQExpression value = _value;
            if(value == null) {
                throw new IllegalStateException();
            }
            out.writeObject(value);
        } else {
            out.writeBoolean(true);
            if(result instanceof RemoteSequence) {
                out.writeBoolean(true);
                RemoteSequence remote = (RemoteSequence) result;
                remote.writeExternal(out);
            } else {
                out.writeBoolean(false);
                final StopWatch sw = new StopWatch("Elapsed time for encoding `$" + getName()
                        + '\'');
                final XQEventEncoder encoder = new XQEventEncoder(out);
                try {
                    encoder.emit(result);
                } catch (XQueryException xqe) {
                    throw new IllegalStateException("failed encoding `$" + getName() + '\'', xqe);
                } catch (Throwable e) {
                    LOG.fatal(e);
                    throw new IllegalStateException("failed encoding `$" + getName() + '\'', e);
                }
                if(LOG.isInfoEnabled()) {
                    LOG.info(sw);
                }
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._local = false;
        this._type = (Type) in.readObject();
        this._varName = (QualifiedName) in.readObject();
        this._identifier = in.readInt();

        final boolean hasResult = in.readBoolean();
        if(hasResult) {
            final boolean isRemoteSeq = in.readBoolean();
            if(isRemoteSeq) {
                this._result = RemoteSequence.readFrom(in);
            } else {
                final StopWatch sw = new StopWatch("Elapsed time for decoding `$" + getName()
                        + '\'');
                final XQEventDecoder decoder = new XQEventDecoder(in);
                try {
                    this._result = decoder.decode();
                } catch (XQueryException xqe) {
                    throw new IllegalStateException("failed decoding `$" + getName() + '\'', xqe);
                } catch (Throwable e) {
                    throw new IllegalStateException("failed decoding `$" + getName() + '\'', e);
                }
                if(LOG.isInfoEnabled()) {
                    LOG.info(sw);
                }
            }
        } else {
            this._value = (XQExpression) in.readObject();
        }
    }

    public static ShippedVariable readFrom(ObjectInput in) throws IOException,
            ClassNotFoundException {
        final ShippedVariable var = new ShippedVariable();
        var.readExternal(in);
        return var;
    }

}
