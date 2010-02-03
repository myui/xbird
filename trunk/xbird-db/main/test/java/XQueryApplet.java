/*
 * @(#)$Id: XQueryApplet.java 3619 2008-03-26 07:23:03Z yui $
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
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;

import xbird.xquery.XQueryModule;
import xbird.xquery.parser.*;
import xbird.xquery.parser.visitor.PrintVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQueryApplet extends Applet {
    boolean isStandalone = false;

    Button button1 = new Button();

    TextArea textArea1 = new TextArea("", 100, 30,
            TextArea.SCROLLBARS_VERTICAL_ONLY);

    BorderLayout borderLayout1 = new BorderLayout();

    TextArea textArea2 = new TextArea("", 100, 30,
            TextArea.SCROLLBARS_VERTICAL_ONLY);

    /**Get a parameter value*/
    public String getParameter(String key, String def) {
        return isStandalone ? System.getProperty(key, def)
                : (getParameter(key) != null ? getParameter(key) : def);
    }

    /**Construct the applet*/
    public XQueryApplet() {
    }

    /**Initialize the applet*/
    public void init() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**Component initialization*/
    private void jbInit() throws Exception {
        button1.setLabel("Parse");
        button1.addActionListener(new ParseBAL(this));
        textArea1.setRows(6);
        textArea1.setText("");
        this.setLayout(borderLayout1);
        textArea2.setText("Abstract Syntax Tree will appear here");
        textArea2.setRows(15);
        this.add(button1, BorderLayout.NORTH);
        this.add(textArea1, BorderLayout.CENTER);
        this.add(textArea2, BorderLayout.SOUTH);
        this.doLayout();
    }

    /**Start the applet*/
    public void start() {
    }

    /**Stop the applet*/
    public void stop() {
    }

    /**Destroy the applet*/
    public void destroy() {
    }

    /**Get Applet information*/
    public String getAppletInfo() {
        return "Applet Information";
    }

    /**Get parameter info*/
    public String[][] getParameterInfo() {
        return null;
    }

    void button1_action(ActionEvent evt) {
        String expr = textArea1.getText();
        XQueryParser parser = new XQueryParser(
                new ByteArrayInputStream(expr.getBytes()));
        parser.disable_tracing();

        PrintVisitor dumper;
        try {
            XQueryModule module = parser.parse();
            dumper = new PrintVisitor();
            module.visit(dumper, null);
            textArea2.setText(dumper.getResult());
        } catch (Exception e) {
            textArea2.setText(e.getMessage());
        }
    }

    private static class ParseBAL implements java.awt.event.ActionListener {
        XQueryApplet _myApplet;

        public ParseBAL(XQueryApplet applet) {
            _myApplet = applet;
        }

        public void actionPerformed(ActionEvent e) {
            _myApplet.button1_action(e);
        }
    }
}
