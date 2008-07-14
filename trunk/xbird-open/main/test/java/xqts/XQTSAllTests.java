/*
 * @(#)$Id: XQTSAllTests.java 3619 2008-03-26 07:23:03Z yui $
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
package xqts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import xbird.util.lang.ClassResolver;

/**
 * -Dxqts.report_on -Duser.timezone=America/New_York
 */
public class XQTSAllTests {

    static {
        System.setProperty("xqts.report_on", "");
        System.setProperty("user.timezone", "America/New_York");
    }

    private static final String DEFAULT_TEST_RESOURCE_DIR = "main/test/resources/xqts";
    private static final String TEST_RESOURCE_DIR = XQTSTestBase.XQTS_PROP.getProperty("test_resource_dir", DEFAULT_TEST_RESOURCE_DIR);
    private static final String[] IGNORE_LIST = { "schemaimport", "schemavalidation",
            "trivialembedding", "statictyping", "optionalfeatureerrors" };

    public static void main(String[] args) {
        System.setProperty("xqts.report_on", "");
        System.setProperty("xqts.disable_printing", "");
        System.setProperty("user.timezone", "America/New_York");
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test-suite for XQTS version " + XQTSTestBase.xqtsVersion);
        final List<Class<? extends TestCase>> clazzList;
        try {
            clazzList = testCases();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //$JUnit-BEGIN$        
        for(Class<? extends TestCase> clazz : clazzList) {
            suite.addTestSuite(clazz);
        }
        //$JUnit-END$
        return suite;
    }

    public static List<Class<? extends TestCase>> testCases() throws IOException,
            ClassNotFoundException {
        File resourceDir = new File(TEST_RESOURCE_DIR);
        assert (resourceDir.exists()) : resourceDir.getAbsolutePath();
        File testCasesFile = new File(resourceDir, "TestCase.list");
        if(!testCasesFile.exists()) {
            return Collections.emptyList();
        }
        List<Class<? extends TestCase>> testCases = new ArrayList<Class<? extends TestCase>>(512);
        BufferedReader reader = new BufferedReader(new FileReader(testCasesFile));
        String line = null;
        while((line = reader.readLine()) != null) {
            String clazzName = line.trim();
            boolean ignore = false;
            for(String list : IGNORE_LIST) {
                if(clazzName.contains(list)) {
                    ignore = true;
                    break;
                }
            }
            if(!ignore) {
                Class<? extends TestCase> clazz = (Class<? extends TestCase>) ClassResolver.get(clazzName);
                testCases.add(clazz);
            }
        }
        return testCases;
    }
}