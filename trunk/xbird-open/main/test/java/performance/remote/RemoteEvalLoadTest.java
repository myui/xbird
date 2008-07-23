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
package performance.remote;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.client.InteractiveShell;
import xbird.util.StopWatch;
import xbird.util.concurrent.ExecutorUtils;
import xbird.util.io.FileUtils;
import xbird.util.lang.ArrayUtils;
import xbird.xquery.XQueryException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RemoteEvalLoadTest {

    //@DataProvider(name = "queries")
    public Object[][] getQueries1() {
        return new Object[][] { { "/root/.eclipse/xbird/examples/xqueryd/xmark/q11.xq", "/dev/null" } };
    }

    //@DataProvider(name = "queries")
    public Object[][] getQueries2_unix() {
        return new Object[][] { { "/root/.eclipse/xbird/examples/crest/tmp/scenario01d4.xq",
                "/dev/null" } };
    }

    //@DataProvider(name = "queries")
    public Object[][] getQueries2_windows() {
        return new Object[][] { { "D:/workspace/xbird/examples/crest/tmp/scenario01d.xq", "NUL" } };
    }

    //@DataProvider(name = "queries")
    public Object[][] getQueries3() {
        return new Object[][] { { "/root/.eclipse/xbird/examples/xqueryd/xmark/bdq/q06.xq",
                "/dev/null" } };
    }

    //@DataProvider(name = "queries")
    public Object[][] getQueries4() {
        return new Object[][] { {
                "/home/yui/workspace/xbird/examples/xqueryd/xmark/bdq/xpathmark05.xq", "/dev/null" } };
    }

    //@DataProvider(name = "queries")
    public Object[][] getQueries5() {
        return new Object[][] { { "/root/.eclipse/xbird/examples/xbench/dcmd/q03.xq", "/dev/null" } };
    }

    //@DataProvider(name = "xmark20")
    public Object[][] getQueries20() {
        return new Object[][] { { "/root/.eclipse/xbird/examples/xbench/dcmd/q03.xq", "/dev/null" } };
    }

    //@Test(dataProvider = "queries", invocationCount = 10, threadPoolSize = 5)
    public void scenario_itr10_pool5(String queryFile, String outFile) throws XQueryException {
        String[] args = new String[] { "-t", "-q", queryFile, "-o", outFile };
        new InteractiveShell().run(args);
    }

    //@Test(dataProvider = "queries", invocationCount = 50, threadPoolSize = 25)
    public void scenario_itr50_pool25(String queryFile, String outFile) throws XQueryException {
        String[] args = new String[] { "-t", "-q", queryFile, "-o", outFile };
        new InteractiveShell().run(args);
    }

    //@Test(dataProvider = "queries", invocationCount = 100, threadPoolSize = 30)
    public void scenario_itr50_pool10(String queryFile, String outFile) throws XQueryException {
        String[] args = new String[] { "-t", "-q", queryFile, "-o", outFile };
        new InteractiveShell().run(args);
    }

    //@Test(dataProvider = "queries", invocationCount = 2, threadPoolSize = 2)
    public void scenario_itr1_pool1(String queryFile, String outFile) throws XQueryException {
        String[] args = new String[] { "-t", "-q", queryFile, "-o", outFile };
        new InteractiveShell().run(args);
    }

    //@Test(dataProvider = "queries", invocationCount = 180, threadPoolSize = 60)
    public void scenario_itr100_pool30(String queryFile, String outFile) throws XQueryException {
        String[] args = new String[] { "-t", "-q", queryFile, "-o", outFile };
        new InteractiveShell().run(args);
    }

    public static void main(String[] args) {
        TestListenerAdapter adapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { RemoteEvalLoadTest.class });
        testng.addListener(adapter);
        testng.run();
    }

    @Test(dataProvider = "rand_xmark_remote_10set")
    public void scenario_invoke_random20_xmark_queries(final String[] tests) {
        ExecutorService exec = Executors.newFixedThreadPool(64);
        final int len = tests.length;
        final AtomicInteger counter = new AtomicInteger(0);
        try {
            for(int i = 0; i < len; i++) {
                exec.execute(new Runnable() {
                    public void run() {
                        int nth = counter.getAndIncrement();
                        String testfilename = tests[nth];
                        String test = FileUtils.basename(testfilename) + "#" + nth;
                        //String[] args = new String[] { "-q", testfilename, "-o", OUTPUT_DEST + test };
                        String[] args = new String[] { "-q", testfilename, "-o", "/dev/null" };
                        StopWatch sw = new StopWatch("elapsed time of " + test);
                        try {
                            new InteractiveShell().run(args);
                        } catch (XQueryException e) {
                            Assert.fail("failed " + test, e);
                        }
                        System.out.println(sw);
                    }
                });
            }
        } finally {
            ExecutorUtils.shutdownAndAwaitTermination(exec);
        }
    }

    @DataProvider(name = "rand_xmark_remote_10set")
    public Object[][] getRandXMarkRemoteEval() {
        return new Object[][] { { generateXMarkTest(10) } };
    }

    private static String[] generateXMarkTest(int nset) {
        final int size = 17 * nset;
        final String[] tests = new String[size];
        int counter = 0;
        for(int j = 0; j < nset; j++) {
            for(int i = 1; i <= 20; i++) {
                if(i != 10 && i != 11 & i != 12) {
                    tests[counter++] = "/home/yui/workspace/xbird/examples/xqueryd/xmark/q" + i + ".xq";
                }
            }
        }
        ArrayUtils.shuffle(tests, 154543252525L);
        return tests;
    }

    @DataProvider(name = "rand_xmark_local_10set")
    public Object[][] getRandXMarkLocal() {
        return new Object[][] { { generateXMarkTestLocal(10) } };
    }

    private static String[] generateXMarkTestLocal(int nset) {
        final int size = 17 * nset;
        final String[] tests = new String[size];
        int counter = 0;
        for(int j = 0; j < nset; j++) {
            for(int i = 1; i <= 20; i++) {
                if(i != 10 && i != 11 & i != 12) {
                    tests[counter++] = "/net/takayama/export/db/makoto-y/workspace/xbird/examples/xmark/col/q"
                            + i + ".xq";
                }
            }
        }
        ArrayUtils.shuffle(tests, 154543252525L);
        return tests;
    }

}
