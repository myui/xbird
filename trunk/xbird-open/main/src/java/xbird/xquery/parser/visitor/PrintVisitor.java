/*
 * @(#)$Id: PrintVisitor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.parser.visitor;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import xbird.xquery.Pragma;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.cond.QuantifiedExpr;
import xbird.xquery.expr.constructor.AttributeConstructor;
import xbird.xquery.expr.constructor.CommentConstructor;
import xbird.xquery.expr.constructor.DocConstructor;
import xbird.xquery.expr.constructor.ElementConstructor;
import xbird.xquery.expr.constructor.NamespaceConstructor;
import xbird.xquery.expr.constructor.PIConstructor;
import xbird.xquery.expr.constructor.TextConstructor;
import xbird.xquery.expr.constructor.TextContent;
import xbird.xquery.expr.decorative.ExtensionExpr;
import xbird.xquery.expr.decorative.ParenthesizedExpr;
import xbird.xquery.expr.decorative.UnorderedExpr;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.dyna.ValidateOp;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.flwr.Binding;
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.flwr.OrderSpec;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.func.FunctionCall;
import xbird.xquery.expr.logical.AndExpr;
import xbird.xquery.expr.logical.OrExpr;
import xbird.xquery.expr.math.AdditiveExpr;
import xbird.xquery.expr.math.MultiplicativeExpr;
import xbird.xquery.expr.math.NegativeExpr;
import xbird.xquery.expr.opt.ExpressionProxy;
import xbird.xquery.expr.opt.PathIndexAccessExpr;
import xbird.xquery.expr.opt.PathVariable;
import xbird.xquery.expr.opt.ShippedVariable;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.expr.opt.Join.PromoteJoinExpression;
import xbird.xquery.expr.path.FilterExpr;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.expr.path.PathExpr;
import xbird.xquery.expr.path.FilterExpr.Instruction;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.expr.seq.SequenceOp;
import xbird.xquery.expr.types.CaseClause;
import xbird.xquery.expr.types.CastExpr;
import xbird.xquery.expr.types.CastableExpr;
import xbird.xquery.expr.types.InstanceofOp;
import xbird.xquery.expr.types.TreatExpr;
import xbird.xquery.expr.types.TypeswitchExpr;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PrintVisitor extends AbstractXQueryParserVisitor {

    private final StringBuilder buf = new StringBuilder(1024 * 4);
    private int indent = 0;

    public PrintVisitor() {
        super();
    }

    public String getResult() {
        // replace to simple expression.
        final String toDel = "/descendant-or-self::node()/";
        for(;;) {
            int head = buf.indexOf(toDel);
            if(head == -1) {
                break;
            }
            int rep_end = head + toDel.length();
            buf.replace(head, rep_end, "//");
        }
        return buf.toString();
    }

    public void init() {
        this.indent = 0;
        buf.delete(0, buf.length());
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public XQExpression visit(AdditiveExpr op, XQueryContext ctxt) throws XQueryException {
        op.getLeftOperand().visit(this, ctxt);
        lineFeed();
        if(op.isPlus()) {
            buf.append('+');
        } else {
            buf.append('-');
        }
        lineFeed();
        op.getRightOperand().visit(this, ctxt);
        return op;
    }

    @Override
    public XQExpression visit(AndExpr expr, XQueryContext ctxt) throws XQueryException {
        List exprs = expr.getExpressions();
        for(int i = 0; i < exprs.size(); i++) {
            if(i != 0) {
                buf.append(" and");
                lineFeed();
            }
            XQExpression e = (XQExpression) exprs.get(i);
            e.visit(this, ctxt);
        }
        return expr;
    }

    @Override
    public XQExpression visit(AttributeConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        header("AttributeConstructor");
        final QualifiedName name = constructor.getName();
        if(name != null) {
            attrFeed("name", name.toString());
        } else {
            attrFeed("name");
            indentln();
            XQExpression nameExpr = constructor.getNameExpr();
            nameExpr.visit(this, ctxt);
            indent--;
        }

        attrFeed("value");
        indent++;
        final List values = constructor.getValueExprs();
        for(int i = 0; i < values.size(); i++) {
            XQExpression v = (XQExpression) values.get(i);
            lineFeed();
            v.visit(this, ctxt);
        }
        indent--;

        return constructor;
    }

    @Override
    public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
        buf.append(step.toString());
        //super.visit(step, ctxt);
        return step;
    }

    @Override
    public XQExpression visit(BindingVariable variable, XQueryContext ctxt) throws XQueryException {
        XQExpression expr = variable.getValue();
        assert (expr != null);
        expr.visit(this, ctxt);
        return variable;
    }

    @Override
    public XQExpression visit(CaseClause clause, XQueryContext ctxt) throws XQueryException {
        buf.append("case ");
        Variable v = clause.getVariable();
        if(v != null) {
            final String vname = v.getName();
            if(vname != null) {
                buf.append('$');
                buf.append(vname);
            }
            buf.append(" as ");
        }
        buf.append(clause.getType().toString());
        buf.append(" return ");
        indentln();
        clause.getReturnExpr().visit(this, ctxt);
        indent--;
        return clause;
    }

    @Override
    public XQExpression visit(CastableExpr expr, XQueryContext ctxt) throws XQueryException {
        header("CastableExpr");
        indentln();
        expr.getExpression().visit(this, ctxt);
        Type t = expr.getType();
        if(t != null) {
            buf.append(" castable as ");
            buf.append(t.toString());
        }
        indent--;
        return expr;
    }

    @Override
    public XQExpression visit(CastExpr expr, XQueryContext ctxt) throws XQueryException {
        header("CastExpr");
        indentln();
        expr.getExpression().visit(this, ctxt);
        Type t = expr.getType();
        if(t != null) {
            buf.append(" cast as ");
            buf.append(t.toString());
        }
        indent--;
        return expr;
    }

    @Override
    public XQExpression visit(CommentConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        header("CommentConstructor");

        final String content = constructor.getContent();
        if(content != null) {
            attrFeed("content", content);
        } else {
            attrFeed("content");
            final XQExpression contentExpr = constructor.getContentExpr();
            indentln();
            contentExpr.visit(this, ctxt);
            indent--;
        }

        return constructor;
    }

    @Override
    public XQExpression visit(ComparisonOp comp, XQueryContext ctxt) throws XQueryException {
        comp.getLeftOperand().visit(this, ctxt);
        buf.append(' ' + comp.getOperator() + ' ');
        comp.getRightOperand().visit(this, ctxt);
        return comp;
    }

    @Override
    public XQExpression visit(CompositePath fragment, XQueryContext ctxt) throws XQueryException {
        XQExpression filter = fragment.getFilterExpr();
        if(!(filter instanceof PathVariable)) {
            XQExpression src = fragment.getSourceExpr();
            src.visit(this, ctxt);
            buf.append('/');
        }
        filter.visit(this, ctxt);
        return fragment;
    }

    @Override
    public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append('.');
        return expr;
    }

    @Override
    public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt) throws XQueryException {
        this.visit((FunctionCall) call, ctxt);
        return call;
    }

    @Override
    public XQExpression visit(DocConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        header("DocConstructor");

        final XQExpression expr = constructor.getExpr();
        indentln();
        expr.visit(this, ctxt);
        indent--;

        return constructor;
    }

    @Override
    public XQExpression visit(ElementConstructor constructor, XQueryContext ctxt)
            throws XQueryException {

        final String name = constructor.getElemName();
        if(name != null) {
            buf.append('<' + name + '>');
        } else {
            header("ElementConstructor");
            attrFeed("name");
            final XQExpression nameExpr = constructor.getNameExpr();
            indentln();
            nameExpr.visit(this, ctxt);
            indent--;
        }

        List atts = constructor.getAttributes();
        final int attlen = atts.size();
        if(attlen > 0) {
            attrFeed("attributes");
            indent++;
            for(int i = 0; i < attlen; i++) {
                XQExpression a = (XQExpression) atts.get(i);
                lineFeed();
                a.visit(this, ctxt);
            }
            indent--;
        }

        List values = constructor.getContents();
        if(values.size() > 0) {
            attrFeed("value");
            indent++;
            for(int i = 0; i < values.size(); i++) {
                XQExpression v = (XQExpression) values.get(i);
                if(!(v instanceof TextContent)) {
                    lineFeed();
                }
                v.visit(this, ctxt);
            }
            indent--;
        }

        if(name != null) {
            lineFeed();
            buf.append("</" + name + '>');
        }

        return constructor;
    }

    @Override
    public XQExpression visit(ExpressionProxy proxy, XQueryContext ctxt) throws XQueryException {
        buf.append("PROXY|");
        XQExpression replaced = proxy.getReplacedExpr();
        if(replaced != null) {
            buf.append(replaced.toString());
        } else {
            XQExpression orig = proxy.getOriginalExpr();
            orig.visit(this, ctxt);
        }
        buf.append('|');
        return proxy;
    }

    @Override
    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException {
        final List pragmas = expr.getPragmas();
        for(int i = 0; i < pragmas.size(); i++) {
            Pragma p = (Pragma) pragmas.get(i);
            buf.append(p.toString());
        }

        final XQExpression e = expr.getExpr();
        if(e != null) {
            indentln();
            e.visit(this, ctxt);
            indent--;
        }
        return expr;
    }

    @Override
    public XQExpression visit(ExternalVariable var, XQueryContext ctxt) throws XQueryException {
        final String name = var.getName();
        buf.append("declare variable $" + name);
        final Type type = var.getType();
        if(type != null) {
            buf.append(" as ");
            buf.append(type);
        }
        buf.append(" external;");
        return var;
    }

    @Override
    public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException {
        expr.getSourceExpr().visit(this, ctxt);
        List predicates = expr.getPredicates();
        for(int i = 0; i < predicates.size(); i++) {
            XQExpression p = (XQExpression) predicates.get(i);
            buf.append('[');
            p.visit(this, ctxt);
            buf.append(']');
        }
        return expr;
    }

    @Override
    public XQExpression visit(FLWRExpr expr, XQueryContext ctxt) throws XQueryException {
        // for, let
        List causes = expr.getClauses();
        int prev_type = -1;
        for(int i = 0; i < causes.size(); i++) {
            Binding cause = (Binding) causes.get(i);
            Variable var = cause.getVariable();
            final int type = cause.getExpressionType();
            if(i != 0) {
                if(prev_type == type) {
                    buf.append(", ");
                }
                lineFeed();
            }
            switch(type) {
                case Binding.FOR_CLAUSE:
                    if(prev_type != type) {
                        buf.append("for $"); // fist appearence.
                    } else {
                        buf.append("    $");
                    }
                    String varname = var.getName();
                    if(varname != null) {
                        buf.append(varname);
                        Variable posVar = ((ForClause) cause).getPositionVariable();
                        if(posVar != null) {
                            buf.append(" at $");
                            buf.append(posVar.getName());
                        }
                    } else {
                        assert (var instanceof BindingVariable || var instanceof PathVariable);
                    }
                    buf.append(" in ");
                    //indentln();
                    XQExpression inExpr = ((ForClause) cause).getInExpr();
                    inExpr.visit(this, ctxt);
                    //indent--;
                    break;
                case Binding.LET_CLAUSE:
                    if(prev_type != type) {
                        buf.append("let $"); // fist appearence.
                    } else {
                        buf.append("    $");
                    }
                    buf.append(var.getName());
                    buf.append(" := ");
                    //indentln();
                    var.getValue().visit(this, ctxt);
                    //indent--;
                    break;
                default:
                    throw new IllegalStateException();
            }
            prev_type = type;
        }

        XQExpression filteredRet = expr.getFilteredReturnExpr();

        // where
        if(filteredRet == null) {
            XQExpression filter = expr.getWhereExpr();
            if(filter != null) {
                lineFeed();
                buf.append("where ");
                indentln();
                filter.visit(this, ctxt);
                indent--;
            }
        }

        // order by
        List orders = expr.getOrderSpecs();
        for(int i = 0; i < orders.size(); i++) {
            if(i == 0) {
                lineFeed();
                if(expr.isStableOrdering()) {
                    buf.append("stable ");
                }
                buf.append("order by");
            } else {
                buf.append(',');
            }
            indentln();
            OrderSpec spec = (OrderSpec) orders.get(i);
            spec.getKeyExpr().visit(this, ctxt);
            indent--;
            if(spec.isDescending()) {
                buf.append(" descending");
            }
            if(spec.isEmptyGreatest()) {
                buf.append(" empty greatest");
            }
            URI collation = spec.getCollation();
            if(collation != null) {
                buf.append(" collation ");
                buf.append(collation.toString());
            }
        }

        // return
        lineFeed();
        buf.append("return ");
        indentln();
        XQExpression ret = (filteredRet == null) ? expr.getReturnExpr() : filteredRet;
        ret.visit(this, ctxt);
        indent--;

        return expr;
    }

    @Override
    public XQExpression visit(FunctionCall call, XQueryContext ctxt) throws XQueryException {
        QualifiedName fname = call.getFuncName();
        final String prefix = fname.getPrefix();
        if(prefix != null && prefix.length() > 0) {
            buf.append(prefix);
            buf.append(':');
        }
        buf.append(fname.getLocalPart());
        buf.append('(');
        List params = call.getParams();
        for(int i = 0; i < params.size(); i++) {
            if(i != 0) {
                buf.append(", ");
            }
            XQExpression p = (XQExpression) params.get(i);
            p.visit(this, ctxt);
        }
        buf.append(')');
        return call;
    }

    @Override
    public XQExpression visit(IfExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append("if (");
        expr.getCondExpr().visit(this, ctxt);
        buf.append(") then");
        indentln();
        expr.getThenExpr().visit(this, ctxt);
        indent--;
        lineFeed();
        buf.append("else ");
        indentln();
        expr.getElseExpr().visit(this, ctxt);
        indent--;

        return expr;
    }

    @Override
    public XQExpression visit(InstanceofOp op, XQueryContext ctxt) throws XQueryException {
        header("InstanceofOp");
        indentln();
        op.getExpression().visit(this, ctxt);
        Type t = op.getType();
        if(t != null) {
            buf.append(" instance of ");
            buf.append(t.toString());
        }
        indent--;
        return op;
    }

    @Override
    public XQExpression visit(Instruction instruction, XQueryContext ctxt) throws XQueryException {
        buf.append(instruction.toString());
        return instruction;
    }

    @Override
    public XQExpression visit(LiteralExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append(expr.toString());
        return expr;
    }

    @Override
    public XQExpression visit(MultiplicativeExpr op, XQueryContext ctxt) throws XQueryException {
        op.getLeftOperand().visit(this, ctxt);
        buf.append(' ' + op.getOperator() + ' ');
        op.getRightOperand().visit(this, ctxt);
        return op;
    }

    @Override
    public XQExpression visit(NamespaceConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        header("NamespaceConstructor");
        attrFeed(constructor.getPrefix(), constructor.getValue());
        return constructor;
    }

    @Override
    public XQExpression visit(NegativeExpr op, XQueryContext ctxt) throws XQueryException {
        buf.append('-');
        op.getExpression().visit(this, ctxt);
        return op;
    }

    @Override
    public XQExpression visit(NodeTest nt, XQueryContext ctxt) throws XQueryException {
        buf.append(nt.toString());
        return nt;
    }

    @Override
    public XQExpression visit(OrExpr expr, XQueryContext ctxt) throws XQueryException {
        List exprs = expr.getExpressions();
        for(int i = 0; i < exprs.size(); i++) {
            if(i != 0) {
                buf.append(" or");
                lineFeed();
            }
            XQExpression e = (XQExpression) exprs.get(i);
            e.visit(this, ctxt);
        }
        return expr;
    }

    @Override
    public XQExpression visit(ParenthesizedExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append('(');
        indentln();
        expr.getExpr().visit(this, ctxt);
        indent--;
        lineFeed();
        buf.append(')');
        return expr;
    }

    @Override
    public XQExpression visit(PathExpr path, XQueryContext ctxt) throws XQueryException {
        final List<XQExpression> steps = path.getSteps();
        final int steplen = steps.size();
        for(int i = 0; i < steplen; i++) {
            if(i != 0) {
                buf.append('/');
            }
            XQExpression expr = steps.get(i);
            expr.visit(this, ctxt);
        }
        /*
         if(steplen > 0) {
         XQExpression lastExpr = steps.get(steplen - 1);
         lastExpr.visit(this, ctxt);
         }
         */
        return path;
    }

    @Override
    public XQExpression visit(PathIndexAccessExpr variable, XQueryContext ctxt)
            throws XQueryException {
        buf.append(variable.toString());
        return variable;
    }

    @Override
    public XQExpression visit(PIConstructor constructor, XQueryContext ctxt) throws XQueryException {
        header("PIConstructor");

        final String target = constructor.getTarget();
        final String content = constructor.getContent();
        if(target != null) {
            if(content == null) {
                attrFeed("target", target);
                attrFeed("content");
                indentln();
                constructor.getContentExpr().visit(this, ctxt);
                indent--;
            } else {
                attrFeed(target, content);
            }
        } else {
            attrFeed("target");
            indentln();
            constructor.getTargetExpr().visit(this, ctxt);
            indent--;

            if(content == null) {
                attrFeed("content");
                indentln();
                constructor.getContentExpr().visit(this, ctxt);
                indent--;
            } else {
                attrFeed("content", content);
            }
        }
        return constructor;
    }

    @Override
    public XQExpression visit(PreEvaluatedVariable variable, XQueryContext ctxt)
            throws XQueryException {
        buf.append(variable.toString());
        return variable;
    }

    @Override
    public XQExpression visit(PromoteJoinExpression expr, XQueryContext ctxt)
            throws XQueryException {
        buf.append(expr.toString());
        return expr;
    }

    @Override
    public XQExpression visit(QuantifiedExpr expr, XQueryContext ctxt) throws XQueryException {
        if(expr.isEvery()) {
            buf.append("every ");
        } else {
            buf.append("some ");
        }
        BindingVariable binding = expr.getBinding();
        assert (binding != null);
        buf.append('$');
        buf.append(binding.getName());

        buf.append(" in ");
        indentln();
        XQExpression inExpr = binding.getValue();
        inExpr.visit(this, ctxt);
        indent--;

        lineFeed();
        buf.append("satisfies");
        indentln();
        expr.getCondExpr().visit(this, ctxt);
        indent--;

        return expr;
    }

    @Override
    public XQExpression visit(SequenceExpression expr, XQueryContext ctxt) throws XQueryException {
        List exprs = expr.getExpressions();
        buf.append('(');
        indentln();
        for(int i = 0; i < exprs.size(); i++) {
            if(i != 0) {
                buf.append(", ");
                lineFeed();
            }
            XQExpression e = (XQExpression) exprs.get(i);
            e.visit(this, ctxt);
        }
        indent--;
        lineFeed();
        buf.append(')');
        return expr;
    }

    @Override
    public XQExpression visit(SequenceOp op, XQueryContext ctxt) throws XQueryException {
        op.getLeftOperand().visit(this, ctxt);
        buf.append(' ' + op.getOperator() + ' ');
        op.getRightOperand().visit(this, ctxt);
        return op;
    }

    @Override
    public XQExpression visit(TextConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        header("TextConstructor");

        final XQExpression content = constructor.getContent();
        indentln();
        content.visit(this, ctxt);
        indent--;

        return constructor;
    }

    @Override
    public XQExpression visit(TreatExpr expr, XQueryContext ctxt) throws XQueryException {
        header("TreatExpr");
        indentln();
        expr.getExpression().visit(this, ctxt);
        Type t = expr.getType();
        if(t != null) {
            buf.append(" treat as ");
            buf.append(t.toString());
        }
        indent--;
        return expr;
    }

    @Override
    public XQExpression visit(TypeswitchExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append("switch( ");
        expr.getOperandExpr().visit(this, ctxt);
        buf.append(")\n{");
        indentln();
        List cases = expr.getCaseClauses();
        for(int i = 0; i < cases.size(); i++) {
            if(i != 0) {
                lineFeed();
            }
            CaseClause c = (CaseClause) cases.get(i);
            c.visit(this, ctxt);
        }
        lineFeed();
        buf.append("default:");
        XQExpression dc = expr.getDefaultClause();
        if(dc != null) {
            indentln();
            dc.visit(this, ctxt);
            indent--;
        }
        indent--;
        lineFeed();
        buf.append('}');
        return expr;
    }

    @Override
    public XQExpression visit(UnorderedExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append("unordered {");
        indentln();
        expr.getExpr().visit(this, ctxt);
        indent--;
        buf.append("\n}");
        return expr;
    }

    @Override
    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException {
        buf.append("declare function ");
        final QualifiedName funcName = function.getName();
        final String prefix = funcName.getPrefix();
        if(prefix != null && prefix.length() > 0) {
            buf.append(prefix);
            buf.append(':');
        }
        buf.append(funcName.getLocalPart());
        buf.append('(');
        List params = function.getParameters();
        for(int i = 0; i < params.size(); i++) {
            if(i != 0) {
                buf.append(", ");
            }
            VarRef p = (VarRef) params.get(i);
            buf.append(p.toString());
        }
        buf.append(") ");
        Type returnType = function.getReturnType();
        if(returnType != null) {
            buf.append("as ");
            buf.append(returnType.toString());
        }
        buf.append(" {");
        indentln();
        function.getBodyExpression().visit(this, ctxt);
        indent--;
        lineFeed();
        buf.append('}');

        return null;
    }

    @Override
    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException {
        buf.append("validate ");
        if(op.isStrict() == false) {
            buf.append("lax ");
        }
        buf.append('{');
        indentln();
        op.getExpression().visit(this, ctxt);
        indent--;
        lineFeed();
        buf.append('}');
        return op;
    }

    @Override
    public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
        String name = variable.getName();
        buf.append("$" + name);
        Type type = variable.getType();
        if(type != null) {
            buf.append(" as ");
            buf.append(type);
        }
        XQExpression value = variable.getValue();
        buf.append(" := ");
        if(value != null) { // may be null with funcall
            value.visit(this, ctxt);
        }
        return variable;
    }

    @Override
    public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
        Variable var = ref.getValue();
        if(var instanceof PathVariable) {
            visit((PathVariable) var, ctxt);
        } else if(var instanceof ParametricVariable) {
            XQExpression expr = ((ParametricVariable) var).getValue();
            if(expr == null) {
                buf.append(var);
            } else {
                visit(expr, ctxt);
            }
        } else if(var instanceof ThreadedVariable) {
            ThreadedVariable threadVar = (ThreadedVariable) var;
            buf.append(threadVar);
            buf.append(" := ");
            indentln();
            XQExpression expr = threadVar.getValue();
            assert (expr != null);
            visit(expr, ctxt);
            indent--;
        } else if(var instanceof ShippedVariable) {
            buf.append(var.getValue());
        } else {
            buf.append(var);
        }
        return ref;
    }

    @Override
    public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
        buf.append("execute at ");
        buf.append(expr.getRemoteEndpoint());
        buf.append(" {");
        indentln();
        XQExpression body = expr.getBodyExpression();
        body.visit(this, ctxt);
        indent--;
        lineFeed();
        buf.append(" }");
        return expr;
    }

    @Override
    public XQExpression visit(XQueryModule module, XQueryContext ctxt) throws XQueryException {
        header("XQueryModule");

        attrFeed("version", module.getVersion());
        attrFeed("encoding", module.getEncoding());
        attrFeed("location", module.getLocation());
        attrFeed("namespace", module.getNamespace());

        final Iterator<Variable> v_itor = module.getGlobalVariables().iterator();
        if(v_itor.hasNext()) {
            attrFeed("variable decls");
            indent++;
            do {
                lineFeed();
                Variable v = v_itor.next();
                v.visit(this, ctxt);
            } while(v_itor.hasNext());
            indent--;
        }

        final Iterator<UserFunction> f_itor = module.getLocalFunctions().iterator();
        if(f_itor.hasNext()) {
            attrFeed("function decls");
            indent++;
            do {
                lineFeed();
                UserFunction f = f_itor.next();
                f.visit(this, ctxt);
            } while(f_itor.hasNext());
            indent--;
        }

        final XQExpression body = module.getExpression();
        if(body != null) {
            attrFeed("expression body");
            indentln();
            body.visit(this, ctxt);
            indent--;
        }

        return null;
    }

    private void attrFeed(String name) {
        lineFeed();
        buf.append(" - ").append(name).append(" : ");
    }

    private void attrFeed(String name, Object attr) {
        if(attr != null) {
            attrFeed(name);
            buf.append(attr.toString());
        }
    }

    private void header(String title) {
        buf.append(title);
    }

    private void indentln() {
        indent++;
        lineFeed();
    }

    private void lineFeed() {
        buf.append('\n');
        for(int i = 0; i < indent; i++) {
            buf.append("   ");
        }
    }

}
