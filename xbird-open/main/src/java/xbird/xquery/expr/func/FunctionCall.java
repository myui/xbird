/*
 * @(#)$Id: FunctionCall.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.func;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.meta.*;
import xbird.xquery.misc.DocumentManager;
import xbird.xquery.misc.ModuleManager;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-function-calls
 * @link http://www.w3.org/TR/xquery-semantics/#jd_map_function_argument
 */
public abstract class FunctionCall extends AbstractXQExpression implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected/* final */QualifiedName _funcName; // not null
    protected/* final */List<XQExpression> _params; // immutable, not null

    public FunctionCall(QualifiedName funcName) {
        this(funcName, Collections.<XQExpression> emptyList());
    }

    public FunctionCall(QualifiedName funcName, List<XQExpression> params) {
        if(funcName == null) {
            throw new IllegalArgumentException();
        }
        if(params == null) {
            throw new IllegalArgumentException();
        }
        this._funcName = funcName;
        this._params = params;
    }

    public FunctionCall() {
        super();
    } // for Externalizable

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._type = (Type) in.readObject();
        this._funcName = QualifiedName.readFrom(in);
        final int numParams = in.readInt();
        final ArrayList<XQExpression> params = new ArrayList<XQExpression>(numParams);
        for(int i = 0; i < numParams; i++) {
            XQExpression e = (XQExpression) in.readObject();
            params.add(e);
        }
        this._params = params;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_type);
        _funcName.writeExternal(out);
        final List<XQExpression> params = _params;
        final int numParams = params.size();
        out.writeInt(numParams);
        for(XQExpression e : params) {
            out.writeObject(e);
        }
    }

    public QualifiedName getFuncName() {
        return this._funcName;
    }

    public List<XQExpression> getParams() {
        return this._params;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            final List<XQExpression> params = _params;
            final int size = params.size();
            for(int i = 0; i < size; i++) {
                final XQExpression p = params.get(i);
                params.set(i, p.staticAnalysis(statEnv));
            }
        }
        return this;
    }

    public static final class FunctionCallContext extends DynamicContext {
        private static final long serialVersionUID = 5980215207608149731L;
        private static final Log LOG = LogFactory.getLog("xbird.funcall.stackframe");

        private final DynamicContext _dynEnv;
        private final List<Sequence> _slots;
        private final int _stackFrameDepth;

        public FunctionCallContext(DynamicContext env) {
            super(env.getStaticContext(), env.currentDateTime());
            this._dynEnv = env;
            this._slots = new ArrayList<Sequence>(12);
            if(env instanceof FunctionCallContext) {
                this._stackFrameDepth = ((FunctionCallContext) env).getStackFrameDepth() + 1;
            } else {
                this._stackFrameDepth = 0;
            }
        }

        public int getStackFrameDepth() {
            return _stackFrameDepth;
        }

        public int getSlotSize() {
            return _slots.size();
        }

        public int storeLocal(BindingVariable variable, Sequence result) {
            int slotSize = _slots.size();
            int slot = variable.getSlot();
            if(slot == BindingVariable.SLOT_UNSET) {
                slot = slotSize;
                variable.setSlot(slot);
                _slots.add(slot, result);
                if(LOG.isDebugEnabled()) {
                    LOG.debug("StackFrame[" + _stackFrameDepth + "] Store $" + variable.getName()
                            + " value to the slot(" + slot + ')');
                    if(LOG.isTraceEnabled()) {
                        LOG.trace(result);
                    }
                }
            } else {
                int lastslot = slotSize - 1;
                if(slot > lastslot) {
                    _slots.add(slot, result);
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("StackFrame[" + _stackFrameDepth + "] Store $"
                                + variable.getName() + " value to the slot(" + slot + ')');
                    }
                    if(LOG.isTraceEnabled()) {
                        LOG.trace(result);
                    }
                } else {
                    _slots.set(slot, result);
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("StackFrame[" + _stackFrameDepth + "] Set $" + variable.getName()
                                + " value on the slot(" + slot + ')');
                    }
                    if(LOG.isTraceEnabled()) {
                        LOG.trace(result);
                    }
                }
            }
            return slot;
        }

        public Sequence loadLocal(BindingVariable variable, int slot) {
            assert (slot != BindingVariable.SLOT_UNSET);
            int slotlen = _slots.size();
            if(slot >= slotlen) {
                throw new IllegalStateException("StackFrame[" + _stackFrameDepth
                        + "] Illegal slot number(" + slot + ") for $" + variable.getName());
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("StackFrame[" + _stackFrameDepth + "] Load $" + variable.getName()
                        + " from the slot(" + slot + ')');
            }
            Sequence res = _slots.get(slot);
            if(LOG.isTraceEnabled()) {
                LOG.trace(res);
            }
            return res;
        }

        @Override
        public Item contextItem() {
            return _dynEnv.contextItem();
        }

        @Override
        public int contextPosition() throws DynamicError {
            return _dynEnv.contextPosition();
        }

        @Override
        public DocumentManager getDocumentManager() {
            return _dynEnv.getDocumentManager();
        }

        @Override
        public IFocus getFocus() {
            return _dynEnv.getFocus();
        }

        @Override
        public ModuleManager getModuleManager() {
            return _dynEnv.getModuleManager();
        }

        @Override
        public TimeZone implicitTimezone() {
            return _dynEnv.implicitTimezone();
        }

        @Override
        public void setFocus(IFocus focus) {
            _dynEnv.setFocus(focus);
        }

        @Override
        public void setImpliciteTimeZone(TimeZone tz) {
            _dynEnv.setImpliciteTimeZone(tz);
        }

        @Override
        public void setStaticContext(StaticContext staticEnv) {
            _dynEnv.setStaticContext(staticEnv);
        }
    }
}
