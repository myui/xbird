/*
 * @(#)$Id: ParseApplet.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.parser;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import xbird.xquery.XQueryModule;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.parser.visitor.GraphConstructionVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ParseApplet extends TestCase {

    private static final boolean enableStaticAnalysis = true;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ParseApplet.class);
    }

    public ParseApplet(String testName) {
        super(testName);
    }

    public void testParse() throws Exception {
        //parse("D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/pos02.xq");
        //parse("C:/Software/XQTS_current_rev1.28/Queries/XQuery/FullAxis/preceding-siblingAxis/preceding-sibling-20.xq");
        //parse("D:/workspace/xbird/examples/xqueryd/xqd01local.xq");
        //parse("/root/.eclipse/xbird/examples/crest/tmp/scenario01d.xq");
        parse("C:/Documents and Settings/myui/workspace/xbird-db/examples/crest/scenario01_local.xq");
        //parse("D:/workspace/xbird/examples/misc/simple_fib.xq");
        //parse("D:/workspace/xbird/examples/misc/sudoku.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/yahoo_weather.xq");
        //parse("D:/Software/XQTS_0_8_6/Queries/XQuery/UseCase/UseCaseNS/ns-queries-results-q7.xq");
        //parse("D:/Software/XQTS_0_8_6/Queries/XQuery/UseCase/UseCasePARTS/parts-queries-results-q1.xq");
        //parse("D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/local_q9.xq");
        //parse("D:/workspace/xbird/examples/crest/scenario02_local.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/indexof01.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/let_order.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/namespace_let.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/usecase_1.1.9.4_q4.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/absolute_path.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/atomize01.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/following.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/every01.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/fndata.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/namespace_let02.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/kafka.xq");
        //parse("D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/q8_r02.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/doc_encode01.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/user4.xq");
        //parse("C:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/user.xq");
        //parse("D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/endless3.xq");
        //parse("D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/for_norm02.xq");
    }    

    private static void parse(String path) throws Exception {
        System.out.println("Reading from: " + path);
        XQueryParser t = new XQueryParser(new FileInputStream(path));
        t.disable_tracing();
        final XQueryModule m;
        try {
            m = t.parse();
        } catch (Throwable e) {
            throw new Exception("Exception caused while parsing: " + path, e);
        }
        StaticContext sc = t.getStaticContext();
        sc.setSystemBaseURI(new File(path).toURI());
        if (enableStaticAnalysis) {
            m.staticAnalysis(sc);
        }
        GraphConstructionVisitor visualizer = new GraphConstructionVisitor(sc);
        m.visit(visualizer, sc);
        visualizer.showInFrame();
    }

}
