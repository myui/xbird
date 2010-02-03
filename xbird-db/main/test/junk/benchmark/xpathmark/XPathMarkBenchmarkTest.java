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
package benchmark.xpathmark;

import java.io.*;
import java.lang.reflect.Method;

import junit.extensions.RepeatedTest;
import junit.framework.*;
import xbird.util.io.IOUtils;

import com.clarkware.junitperf.TimedTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XPathMarkBenchmarkTest extends TestCase {

    private static final long SECONDS = 1000;

    // nessesary
    private static final String BENCHMARK_DIR = System.getProperty("benchmark.dir");
    private static final String EXEC_CMD = System.getProperty("benchmark.exec_cmd");
    private static final String BENCHMARK_OUTDIR = System.getProperty("benchmark.outdir");

    // optional
    private static final long TIMEOUT = Long.getLong("benchmark.timeout", 600) * SECONDS;
    private static final int ITERATIONS = Integer.getInteger("benchmark.iterations", 2);

    // constants
    private static final String SF1_TARGET = "xmark1.xml";

    private final File benchmarkDir;
    private final File outDir;

    public XPathMarkBenchmarkTest(String fName) {
        super(fName);
        if(BENCHMARK_DIR == null || EXEC_CMD == null || BENCHMARK_OUTDIR == null) {
            throw new IllegalStateException("Please set VM variable '-Dbenchmark.dir', '-Dbenchmark.exec_cmd', and '-benchmark.outdir'");
        }
        File dir = new File(BENCHMARK_DIR);
        if(!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("benchmark base directory '" + dir + "' does not exist");
        }
        File outDir = new File(BENCHMARK_OUTDIR);
        if(!outDir.exists() || !outDir.isDirectory()) {
            throw new IllegalStateException("output directory '" + outDir + "' does not exist");
        }
        this.benchmarkDir = dir;
        this.outDir = outDir;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        Method[] methods = XPathMarkBenchmarkTest.class.getMethods();
        for(Method m : methods) {
            String fname = m.getName();
            if(fname.startsWith("test")) {
                Test testCase = new XPathMarkBenchmarkTest(fname);
                Test repeatedTest = new RepeatedTest(testCase, ITERATIONS);
                Test timedTest = new TimedTest(repeatedTest, TIMEOUT, true);
                suite.addTest(timedTest);
            }
        }
        return suite;
    }

    private void invokeTest(String queryfile, String target) {

        try {
            String query = IOUtils.toString(XPathMarkBenchmarkTest.class.getResourceAsStream(queryfile)).trim();
            String args = EXEC_CMD + " \"" + query + "\" \"" + target + '"';
            System.out.println("Invoke command: \n " + args);

            Process proc = Runtime.getRuntime().exec(args, null, benchmarkDir);

            InputStream is = proc.getInputStream();
            File outFile = new File(outDir, queryfile + ".result");
            IOUtils.copy(is, new FileOutputStream(outFile));
            is.close();
            
            int ret = proc.waitFor();
            if(ret != 0) {
                System.out.println("process exited with value : " + ret);
            }            
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        } catch (InterruptedException irre) {
            throw new IllegalStateException(irre);
        }
    }

    public void testQ1_sf1() {
        invokeTest("q1.xpath", SF1_TARGET);
    }

    public void testQ2_sf1() {
        invokeTest("q2.xpath", SF1_TARGET);
    }

    public void testQ4_sf1() {
        invokeTest("q4.xpath", SF1_TARGET);
    }

    public void testQ5_sf1() {
        invokeTest("q5.xpath", SF1_TARGET);
    }

    public void testQ6_sf1() {
        invokeTest("q6.xpath", SF1_TARGET);
    }

    public void testQ7_sf1() {
        invokeTest("q7.xpath", SF1_TARGET);
    }

    public void testQ14_sf1() {
        invokeTest("q14.xpath", SF1_TARGET);
    }

    public void testQ15_sf1() {
        invokeTest("q15.xpath", SF1_TARGET);
    }

    public void testQ16_sf1() {
        invokeTest("q16.xpath", SF1_TARGET);
    }

    public void testQ17_sf1() {
        invokeTest("q17.xpath", SF1_TARGET);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
