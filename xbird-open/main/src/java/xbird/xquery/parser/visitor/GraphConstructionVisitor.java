/*
 * @(#)$Id: GraphConstructionVisitor.java 3619 2008-03-26 07:23:03Z yui $
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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.ext.JGraphModelAdapter;
import org._3pq.jgrapht.graph.DefaultDirectedGraph;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

import xbird.util.collections.ObjectStack;
import xbird.util.gui.JGraphUtils;
import xbird.util.io.IOUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.cond.QuantifiedExpr;
import xbird.xquery.expr.constructor.*;
import xbird.xquery.expr.decorative.*;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.dyna.ValidateOp;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.flwr.*;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.func.FunctionCall;
import xbird.xquery.expr.logical.AndExpr;
import xbird.xquery.expr.logical.OrExpr;
import xbird.xquery.expr.math.*;
import xbird.xquery.expr.opt.DistinctSortExpr;
import xbird.xquery.expr.opt.PathIndexAccessExpr;
import xbird.xquery.expr.opt.Join.PromoteJoinExpression;
import xbird.xquery.expr.path.*;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.expr.seq.*;
import xbird.xquery.expr.types.*;
import xbird.xquery.expr.var.*;
import xbird.xquery.expr.var.BindingVariable.PositionalVariable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParser;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class GraphConstructionVisitor extends AbstractXQueryParserVisitor {

    // -------------------------------------
    // constants

    private static final String APPLT_TITLE = "XQuery Query Tree";
    private static final String DEFAULT_BG_COLOR = "#FAFBFF";
    private static final Dimension DEFAULT_SIZE = new Dimension(640, 480);
    private static final Color RED_NODE_COLOR = Color.decode("#CC3300");
    private static final Color BLUE_NODE_COLOR = Color.decode("#3366CC");
    private static final Color GREEN_NODE_COLOR = Color.decode("#006633");

    // -------------------------------------
    // instance variables

    private int counter = 1;
    private JFrame frame;
    private DirectedGraph graph;
    private JGraph jgraph;
    private final ObjectStack parentStack = new ObjectStack();
    private final Map<String, XQExpression> sourceExprMap = new IdentityHashMap<String, XQExpression>();
    private final StaticContext statEnv;
    private final Set<QualifiedName> visitedVarNames = new HashSet<QualifiedName>();

    // -------------------------------------

    public GraphConstructionVisitor(StaticContext statEnv) {
        // create a JGraphT graph
        this.graph = new DefaultDirectedGraph();
        this.statEnv = statEnv;
    }

    private void reset() {
        this.graph = new DefaultDirectedGraph();
        parentStack.clear();
        sourceExprMap.clear();
        visitedVarNames.clear();
    }

    public void showInFrame() {
        JGraph jgraph = createJGraph();
        //jgraph.setPreferredSize(DEFAULT_SIZE);
        jgraph.setBackground(Color.decode(DEFAULT_BG_COLOR));
        // Show in Frame
        this.frame = new JFrame(APPLT_TITLE);
        // add menu bar
        MyMenuChooser menuBar = new MyMenuChooser(frame);
        frame.setJMenuBar(menuBar);
        // append graph panel
        JScrollPane panel = new JScrollPane(jgraph);
        panel.setPreferredSize(DEFAULT_SIZE);
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public XQExpression visit(AdditiveExpr op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " #" + counter + " AdditiveExpr";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(AndExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " AndExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(AttributeConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " AttributeConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " AxisStep";
        addChildNode(node, step);
        parentStack.push(node);
        super.visit(step, ctxt);
        parentStack.pop();
        return step;
    }

    public XQExpression visit(BindingVariable variable, XQueryContext ctxt) throws XQueryException {
        if(!(variable instanceof PositionalVariable)) {
            XQExpression expr = variable.getValue();
            assert (expr != null);
            expr.visit(this, ctxt);
        }
        return variable;
    }

    public XQExpression visit(BuiltInFunction function, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " BuiltInFunction";
        addChildNode(node, null);
        parentStack.push(node);
        super.visit(function, ctxt);
        parentStack.pop();
        return null;
    }

    public XQExpression visit(CaseClause clause, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " CaseClause";
        addChildNode(node, clause);
        parentStack.push(node);
        super.visit(clause, ctxt);
        parentStack.pop();
        return clause;
    }

    public XQExpression visit(CastableExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " CastableExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(CastExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " CastExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(CommentConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " CommentConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(ComparisonOp comp, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ComparisonOp";
        addChildNode(node, comp);
        parentStack.push(node);
        super.visit(comp, ctxt);
        parentStack.pop();
        return comp;
    }

    public XQExpression visit(CompositePath fragment, XQueryContext ctxt) throws XQueryException {
        String node = "#" + counter + " CompositePath";
        addChildNode(node, fragment);
        parentStack.push(node);
        XQExpression srcExpr = fragment.getSourceExpr();
        srcExpr.visit(this, ctxt);
        parentStack.pop();
        return fragment;
    }

    public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ContextItemExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " DirectFunctionCall";
        addChildNode(node, call);
        parentStack.push(node);
        super.visit(call, ctxt);
        parentStack.pop();
        return call;
    }

    public XQExpression visit(DistinctSortExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " DistinctSortExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(DocConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " DocConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(ElementConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " ElementConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ExtensionExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(ExternalVariable var, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ExternalVariable";
        addChildNode(node, var);
        parentStack.push(node);
        super.visit(var, ctxt);
        parentStack.pop();
        return var;
    }

    public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " FilterExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(FLWRExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " FLWRExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ForClause";
        addChildNode(node, clause);
        parentStack.push(node);
        super.visit(clause, ctxt);
        parentStack.pop();
        return clause;
    }

    public XQExpression visit(FunctionCall call, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " FunctionCall";
        addChildNode(node, call);
        parentStack.push(node);
        super.visit(call, ctxt);
        parentStack.pop();
        return call;
    }

    public XQExpression visit(IfExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " IfExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(InstanceofOp op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " InstanceofOp";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " LetClause";
        addChildNode(node, clause);
        parentStack.push(node);
        super.visit(clause, ctxt);
        parentStack.pop();
        return clause;
    }

    public XQExpression visit(LiteralExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " LiteralExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(MultiplicativeExpr op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " MultiplicativeExpr";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(NamespaceConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " NamespaceConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(NegativeExpr op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " NegativeExpr";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(NodeTest test, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " NodeTest";
        addChildNode(node, test);
        parentStack.push(node);
        super.visit(test, ctxt);
        parentStack.pop();
        return test;
    }

    public XQExpression visit(OrderSpec spec, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " OrderSpec";
        addChildNode(node, spec);
        parentStack.push(node);
        super.visit(spec, ctxt);
        parentStack.pop();
        return spec;
    }

    public XQExpression visit(OrExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " OrExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(PromoteJoinExpression expr, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " PromoteJoinExpression";
        addChildNode(node, expr);
        return expr;
    }

    public XQExpression visit(ParenthesizedExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ParenthesizedExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    @Override
    public XQExpression visit(PathIndexAccessExpr variable, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " PathIndexAccessExpr";
        addChildNode(node, variable);
        parentStack.push(node);
        super.visit(variable, ctxt);
        parentStack.pop();
        return variable;
    }

    public XQExpression visit(PathExpr path, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " PathExpr";
        addChildNode(node, path);
        parentStack.push(node);
        super.visit(path, ctxt);
        parentStack.pop();
        return path;
    }

    public XQExpression visit(PIConstructor constructor, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " PIConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(QuantifiedExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " QuantifiedExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(SequenceExpression expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " SequenceExpression";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(SequenceOp op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " SequenceOp";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(TextConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String node = "#" + counter + " TextConstructor";
        addChildNode(node, constructor);
        parentStack.push(node);
        super.visit(constructor, ctxt);
        parentStack.pop();
        return constructor;
    }

    public XQExpression visit(TreatExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " TreatExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(TypeswitchExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " TypeswitchExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(UnionOp op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " UnionOp";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(UnorderedExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " UnorderedExpr";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " UserFunction";
        addChildNode(node, null);
        parentStack.push(node);
        super.visit(function, ctxt);
        parentStack.pop();
        return null;
    }

    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " ValidateOp";
        addChildNode(node, op);
        parentStack.push(node);
        super.visit(op, ctxt);
        parentStack.pop();
        return op;
    }

    public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
        final QualifiedName varname = variable.getVarName();
        if(!visitedVarNames.contains(varname)) {
            visitedVarNames.add(varname);
            final String node = "#" + counter + " Variable";
            addRootNode(node, variable);
            parentStack.push(node);
            super.visit(variable, ctxt);
            parentStack.pop();
        }
        return variable;
    }

    public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " VarRef";
        addChildNode(node, ref);
        parentStack.push(node);
        Variable var = ref.getValue();
        if(!(var instanceof BindingVariable)) {
            super.visit(var, ctxt);
        }
        parentStack.pop();
        return ref;
    }

    @Override
    public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " XQueryD";
        addChildNode(node, expr);
        parentStack.push(node);
        super.visit(expr, ctxt);
        parentStack.pop();
        return expr;
    }

    public XQExpression visit(XQueryModule module, XQueryContext ctxt) throws XQueryException {
        final String node = "#" + counter + " XQueryModule";
        addChildNode(node, module.getExpression());
        parentStack.push(node);
        super.visit(module, ctxt);
        parentStack.pop();
        return null;
    }

    private void addChildNode(final String node, XQExpression expr) {
        counter++;
        graph.addVertex(node);
        sourceExprMap.put(node, expr);
        if(parentStack.peek() != null) {
            graph.addEdge(parentStack.peek(), node);
        }
    }

    private void addRootNode(final String node, XQExpression expr) {
        counter++;
        graph.addVertex(node);
        sourceExprMap.put(node, expr);
        assert (!parentStack.isEmpty());
        graph.addEdge(parentStack.get(0), node);
    }

    private JGraph createJGraph() {
        // create a visualization using JGraph, via an adapter
        JGraphModelAdapter m_jgAdapter = new JGraphModelAdapter(graph, JGraphModelAdapter.createDefaultVertexAttributes(), createDefaultEdgeAttributes());
        this.jgraph = new JGraph(m_jgAdapter);
        // layout setting
        JGraphUtils.applyOrderedTreeLayout(graph, m_jgAdapter, jgraph);
        // vertex color setting
        Iterator vertexIter = graph.vertexSet().iterator();
        while(vertexIter.hasNext()) {
            String vertex = (String) vertexIter.next();
            if(vertex.indexOf("QuantifiedExpr") > 0 || vertex.indexOf("FLWRExpr") > 0
                    || vertex.indexOf("PromoteJoinExpression") > 0) {
                DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
                AttributeMap attr = cell.getAttributes();
                GraphConstants.setBackground(attr, RED_NODE_COLOR);
                AttributeMap cellAttr = new AttributeMap();
                cellAttr.put(cell, attr);
                m_jgAdapter.edit(cellAttr, null, null, null);
            } else if(vertex.indexOf("DistinctSortExpr") > 0) {
                DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
                AttributeMap attr = cell.getAttributes();
                GraphConstants.setBackground(attr, GREEN_NODE_COLOR);
                AttributeMap cellAttr = new AttributeMap();
                cellAttr.put(cell, attr);
                m_jgAdapter.edit(cellAttr, null, null, null);
            } else if(vertex.indexOf("PathIndexAccessExpr") > 0) {
                DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
                AttributeMap attr = cell.getAttributes();
                GraphConstants.setBackground(attr, BLUE_NODE_COLOR);
                AttributeMap cellAttr = new AttributeMap();
                cellAttr.put(cell, attr);
                m_jgAdapter.edit(cellAttr, null, null, null);
            }
        }
        // mouseClicked event handling
        jgraph.addMouseListener(new MyMouseListener());
        return jgraph;
    }

    private static AttributeMap createDefaultEdgeAttributes() {
        AttributeMap map = new AttributeMap();

        GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
        GraphConstants.setEndFill(map, true);
        GraphConstants.setEndSize(map, 10);

        GraphConstants.setForeground(map, Color.decode(DEFAULT_BG_COLOR));
        GraphConstants.setFont(map, GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN, 0));
        GraphConstants.setLineColor(map, Color.decode("#7AA1E6"));

        return map;
    }

    private class MyMenuChooser extends JMenuBar implements ActionListener, PropertyChangeListener {
        private final JFrame _frame;
        private final JTextArea text;
        private final JFileChooser fc;

        public MyMenuChooser(JFrame frame) {
            this._frame = frame;
            JMenu menuf = new JMenu("file");
            add(menuf);

            JMenuItem menu1 = new JMenuItem("open");
            menu1.addActionListener(this);
            menuf.add(menu1);

            text = new JTextArea();
            text.setEditable(false);

            String baseuri = StaticContext.SYSTEM_BASE_URI;
            String dirpath = baseuri.substring(baseuri.indexOf('/') + 1, baseuri.lastIndexOf('/'));
            fc = new JFileChooser(dirpath);
            // file chooser
            fc.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if(f.getName().endsWith(".xq")) {
                        fc.setCurrentDirectory(f.getParentFile());
                        return true;
                    } else {
                        return false;
                    }
                }

                public String getDescription() {
                    return "XQuery file(*.xq)";
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand() == "open") {

                // preview window                
                JScrollPane scroll = new JScrollPane(new JScrollPane(text));
                scroll.setPreferredSize(new Dimension(500, 0));
                fc.setAccessory(scroll);
                fc.addPropertyChangeListener(this);
                final int fd = fc.showOpenDialog(_frame);
                if(fd == JFileChooser.APPROVE_OPTION) {
                    reset();
                    frame.getContentPane().removeAll();
                    try {
                        File file = fc.getSelectedFile();
                        System.err.println(file.getName());
                        System.out.println(IOUtils.toString(new FileInputStream(file)));
                        System.out.println();
                        FileReader fr = new FileReader(file);
                        XQueryParser t = new XQueryParser(fr);
                        XQueryModule m = t.parse();
                        fr.close();
                        StaticContext sc = t.getStaticContext();
                        sc.setSystemBaseURI(statEnv.getSystemBaseURI());
                        m.staticAnalysis(sc);
                        m.visit(GraphConstructionVisitor.this, sc);
                        // create panel
                        final JGraph jgraph = createJGraph();
                        jgraph.setBackground(Color.decode(DEFAULT_BG_COLOR));
                        final JScrollPane panel = new JScrollPane(jgraph);
                        panel.setPreferredSize(DEFAULT_SIZE);
                        _frame.getContentPane().add(panel);
                        _frame.pack();
                    } catch (Exception err) {
                        System.err.println(err.getMessage());
                    }
                }
            }
            _frame.repaint();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Object evSource = evt.getSource();
            if(evSource instanceof JFileChooser) {
                JFileChooser chooser = (JFileChooser) evSource;
                File file = chooser.getSelectedFile();
                if(file == null) {
                    return;
                }
                try {
                    String contents = IOUtils.toString(new FileInputStream(file));
                    text.setText(contents);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            _frame.repaint();
        }
    }

    private class MyMouseListener extends MouseAdapter {

        public void mouseClicked(MouseEvent ev) {
            assert (jgraph != null && frame != null);
            if(ev.getClickCount() == 2) {
                Object cell = jgraph.getSelectionCell();
                if(cell instanceof DefaultGraphCell) {
                    DefaultGraphCell selectedCell = (DefaultGraphCell) cell;
                    if(selectedCell != null) {
                        String selected = (String) selectedCell.getUserObject();
                        XQExpression expr = sourceExprMap.get(selected);
                        JOptionPane.showMessageDialog(frame, (expr == null) ? selected
                                : expr.toString());
                    }
                }
                super.mouseClicked(ev);
            } else if(SwingUtilities.isRightMouseButton(ev)) {
                DefaultGraphCell selectedCell = (DefaultGraphCell) jgraph.getSelectionCell();
                if(selectedCell != null) {
                    // Create PopupMenu for the Cell
                    JPopupMenu menu = createPopupMenu(selectedCell);
                    // Display PopupMenu
                    menu.show(frame, ev.getX(), ev.getY());
                    menu.setVisible(true);
                }
            }
        }

        private JPopupMenu createPopupMenu(final DefaultGraphCell cell) {
            assert (cell != null);
            final JPopupMenu menu = new JPopupMenu();
            menu.add(new AbstractAction("Show InferredType") {
                public void actionPerformed(ActionEvent e) {
                    String selected = (String) cell.getUserObject();
                    XQExpression expr = sourceExprMap.get(selected);
                    Type type = expr.getType();
                    JOptionPane.showMessageDialog(frame, (type == null) ? "nil" : type);
                }
            });
            menu.add(new AbstractAction("Eval Expression") {
                public void actionPerformed(ActionEvent e) {
                    String selected = (String) cell.getUserObject();
                    XQExpression expr = sourceExprMap.get(selected);
                    DynamicContext dynEnv = new DynamicContext(statEnv);
                    StringWriter sw = new StringWriter();
                    SAXSerializer ser = new SAXSerializer(new SAXWriter(sw), sw);
                    try {
                        expr.evalAsEvents(ser, ValueSequence.EMPTY_SEQUENCE, dynEnv);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "eval failed!: " + ex.getMessage());
                        ex.printStackTrace();
                        return;
                    }
                    JOptionPane.showMessageDialog(frame, sw.toString());
                }
            });
            menu.add(new AbstractAction("Eval Expression (pull)") {
                public void actionPerformed(ActionEvent e) {
                    String selected = (String) cell.getUserObject();
                    XQExpression expr = sourceExprMap.get(selected);
                    DynamicContext dynEnv = new DynamicContext(statEnv);
                    final Sequence result;
                    try {
                        result = expr.eval(ValueSequence.EMPTY_SEQUENCE, dynEnv);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "eval failed!: " + ex.getMessage());
                        ex.printStackTrace();
                        return;
                    }
                    JOptionPane.showMessageDialog(frame, result.toString());
                }
            });
            menu.add(new AbstractAction("Print Tree") {
                public void actionPerformed(ActionEvent e) {
                    String selected = (String) cell.getUserObject();
                    XQExpression expr = sourceExprMap.get(selected);
                    String s = expr.toString();
                    JOptionPane.showMessageDialog(frame, (expr == null) ? selected : s);
                    System.out.println(s);
                    System.out.println();
                }
            });
            return menu;
        }

    }

}
