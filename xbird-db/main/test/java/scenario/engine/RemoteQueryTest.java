/*
 * @(#)$Id: RemoteQueryTest.java 3619 2008-03-26 07:23:03Z yui $
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
package scenario.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import net.sf.saxon.trans.XPathException;

import org.junit.Test;
import org.xml.sax.SAXException;

import xbird.util.datetime.StopWatch;
import xbird.util.string.StringUtils;
import xbird.util.system.SystemUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.Sequence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public class RemoteQueryTest extends TestCase {

    private static final String DEFAUL_DOC_BASE = "C:/workspace/xbird/examples/crest";
    private static final String DOC_BASE = System.getProperty("docbase", DEFAUL_DOC_BASE);

    private static String executeQuery(File queryFile) throws XQueryException, IOException {
        System.gc();
        int gcBefore = SystemUtils.countGC();
        long free = SystemUtils.getHeapFreeMemory();
        StringBuilder stdbuf = new StringBuilder(256);
        stdbuf.append(" - free(init): " + StringUtils.displayBytesSize(free));
        StopWatch sw = new StopWatch("[Xbird] " + queryFile);
        XQueryProcessor processor = new XQueryProcessor();
        XQueryModule mod = processor.parse(new FileInputStream(queryFile), new File(DOC_BASE).toURI());
        processor.compile(mod);
        System.err.println(mod.getExpression().toString());
        Sequence result = processor.execute(mod);
        StringWriter res_sw = new StringWriter();
        Serializer ser = new SAXSerializer(new SAXWriter(res_sw), res_sw);
        ser.emit(result);
        String swresult = sw.toString();
        long used = SystemUtils.getHeapUsedMemory();
        stdbuf.append(", used(before GC): " + StringUtils.displayBytesSize(used));
        System.gc();
        used = SystemUtils.getHeapUsedMemory();
        stdbuf.append(", used(after GC): " + StringUtils.displayBytesSize(used));
        int gcCalled = SystemUtils.countGC() - gcBefore;
        stdbuf.append(", total gc count: " + gcCalled);
        System.out.println(swresult);
        System.out.println(stdbuf.toString());
        return res_sw.toString();
    }

    @Test
    public void xtestScenario02() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException, URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File rfile = new File(baseDir, "scenario02.xq");
        executeQuery(rfile);
    }

    @Test
    public void xtestScenario02Compare() throws FileNotFoundException, XPathException,
            SAXException, IOException, ParserConfigurationException, XQueryException,
            URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File lfile = new File(baseDir, "scenario02_local.xq");
        File rfile = new File(baseDir, "scenario02.xq");
        assertEquals(executeQuery(lfile), executeQuery(rfile));
    }

    @Test
    public void xtestScenario03Compare() throws FileNotFoundException, XPathException,
            SAXException, IOException, ParserConfigurationException, XQueryException,
            URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File lfile = new File(baseDir, "scenario03_local.xq");
        File rfile = new File(baseDir, "scenario03.xq");
        assertEquals(executeQuery(lfile), executeQuery(rfile));
    }

    @Test
    public void xtestScenario03colCompare() throws FileNotFoundException, XPathException,
            SAXException, IOException, ParserConfigurationException, XQueryException,
            URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File lfile = new File(baseDir, "scenario03_local.xq");
        File rfile = new File(baseDir, "scenario03col.xq");
        assertEquals(executeQuery(lfile), executeQuery(rfile));
    }

    @Test
    public void testScenario02col() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException, URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File rfile = new File(baseDir, "scenario02col.xq");
        executeQuery(rfile);
    }

    @Test
    public void xtestScenario03col() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException, URISyntaxException {
        File baseDir = new File(DOC_BASE);
        if(!baseDir.exists()) {
            throw new IllegalStateException("Illegal base dir: " + DOC_BASE);
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalStateException();
        }
        File rfile = new File(baseDir, "scenario03col.xq");
        executeQuery(rfile);
    }
}
