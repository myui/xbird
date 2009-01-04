/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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
package xbird;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xbird.util.concurrent.ExecutorUtils;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class MultithreadedTestSuite extends TestSuite {

    private final int nthreads;

    public MultithreadedTestSuite(String name, int nthreads) {
        super(name);
        this.nthreads = nthreads;
    }

    @Override
    public void run(final TestResult result) {
        final ExecutorService exec = Executors.newFixedThreadPool(nthreads);
        final Enumeration<Test> tests = tests();
        while(tests.hasMoreElements()) {
            final Test test = tests.nextElement();
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        test.run(result);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        exec.shutdown();
        ExecutorUtils.shutdownAndAwaitTermination(exec);
    }

}
