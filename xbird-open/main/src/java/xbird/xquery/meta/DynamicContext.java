/*
 * @(#)$Id: DynamicContext.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.ObjectStreamException;
import java.util.*;

import xbird.config.Settings;
import xbird.xquery.DynamicError;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.FilterExpr.Filtered;
import xbird.xquery.func.context.Last;
import xbird.xquery.misc.DocumentManager;
import xbird.xquery.misc.ModuleManager;

/**
 * dynamic context.
 * <DIV lang="en">
 * Dynamic Context Components<br/>
 * <ul>
 * <li><u>Context item (scope: dynamic)</u></li><br/>
 * changes during evaluation of path expressions and predicates
 * <li><u>Context position (scope: dynamic)</u></li><br/>
 * dynamic; changes during evaluation of path expressions and predicates
 * <li><u>Context size (scope: dynamic)</u></li><br/>
 * changes during evaluation of path expressions and predicates
 * <li><u>Variable values (scope: dynamic)</u></li><br/>
 * for-expressions and quantified expressions can bind new variables
 * <li><u>Current date and time (scope: global)</u></li><br/>
 * must be initialized by implementation
 * <li><u>Implicit timezone (scope: global)</u></li><br/>
 * must be initialized by implementation
 * <li><u>Available documents (scope: global)</u></li><br/>
 * must be initialized by implementation
 * <li><u>Available collections (scope: global)</u></li><br/>
 * must be initialized by implementation
 * <li><u>Default collection (scope: global)</u></li><br/>
 * overwriteable by implementation
 * </ul>
 * For each component, "global" indicates that the value of the component
 * remains constant throughout evaluation of the XPath expression, 
 * whereas "dynamic" indicates that the value of the component 
 * can be modified by the evaluation of subexpressions.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#eval_context
 * @link http://www.w3.org/TR/xpath20/#id-xp-evaluation-context-components
 */
public class DynamicContext implements XQueryContext {
    private static final long serialVersionUID = -5588614518227145610L;

    private static final boolean ENV_PROFILING = Boolean.parseBoolean(Settings.get("xbird.profiling"));
    private static final Profiler profiler = ENV_PROFILING ? new Profiler() : null;

    public static final DynamicContext DUMMY = new DynamicContext(new StaticContext());
    public static final DynamicContext PROBE = new DynamicContext(null);

    private StaticContext staticContext;

    //---------------------------
    // dynamic scope components

    private transient IFocus focus = null;
    private transient DocumentManager documents = new DocumentManager();
    private transient Stack<Sequence<? extends Item>> tracSequenceStack = new Stack<Sequence<? extends Item>>();

    private transient XQExpression _queryExpr;
    
    //---------------------------
    // glabal scope components

    private GregorianCalendar _currentDateTime;

    //---------------------------

    private DynamicContext() {
        this.documents = new DocumentManager();
    }

    public DynamicContext(StaticContext statEnv) {
        this(statEnv, new GregorianCalendar());
    }

    protected DynamicContext(StaticContext statEnv, GregorianCalendar currentDateTime) {
        this.staticContext = statEnv;
        this._currentDateTime = new GregorianCalendar();
    }

    //--------------------------------------------
    // Getters/Setters

    public void setStaticContext(StaticContext staticEnv) {
        this.staticContext = staticEnv;
    }

    public StaticContext getStaticContext() {
        return staticContext;
    }

    public ModuleManager getModuleManager() {
        return staticContext.getModuleManager();
    }

    public DocumentManager getDocumentManager() {
        return documents;
    }

    public void setFocus(IFocus focus) {
        this.focus = focus;
    }

    public IFocus getFocus() {
        return focus;
    }

    public void setImpliciteTimeZone(TimeZone tz) {
        _currentDateTime.setTimeZone(tz);
    }

    public Profiler getProfiler() {
        return profiler;
    }

    //--------------------------------------------
    // Dynamic scope components

    public Item contextItem() {
        if(focus == null) {
            return null;
        }
        return focus.getContextItem();
    }

    public int contextPosition() throws DynamicError {
        if(focus == null) {
            throw new DynamicError("err:XPDY0002", "ContentPosition is not set");
        }
        return focus.getContextPosition();
    }

    //--------------------------------------------
    // Global scope components

    /**
     * @link http://www.w3.org/TR/xpath20/#dt-timezone
     */
    public TimeZone implicitTimezone() {
        return _currentDateTime.getTimeZone();
    }

    public GregorianCalendar currentDateTime() {
        return _currentDateTime;
    }

    //--------------------------------------------
    // extension TODO REVIEWME workaround for fn:last, FilterExpr

    /**
     * @see Filtered#next(IFocus)
     */
    public final void pushSequence(final Sequence<? extends Item> seq) {
        if(seq == null) {
            throw new IllegalArgumentException();
        }
        tracSequenceStack.push(seq);
    }

    /**
     * @see Filtered#next(IFocus)
     */
    public final Sequence<? extends Item> popSequence() {
        return tracSequenceStack.pop();
    }

    /**
     * @see Last#eval(Sequence, xbird.xquery.dm.value.sequence.ValueSequence, DynamicContext)
     */
    public final Sequence<? extends Item> peekSequence() {
        return tracSequenceStack.peek();
    }
    
    public final void setQueryExpression(XQExpression expr) {
        this._queryExpr = expr;
    }
    
    public final XQExpression getQueryExpression() {
        return _queryExpr;
    }

    protected Object readResolve() throws ObjectStreamException {
        this.documents = new DocumentManager();
        this.tracSequenceStack = new Stack<Sequence<? extends Item>>();
        return this;
    }
}
