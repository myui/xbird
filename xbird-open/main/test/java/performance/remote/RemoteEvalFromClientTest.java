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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.engine.XQEngine;
import xbird.engine.XQEngineClient;
import xbird.engine.Request.ReturnType;
import xbird.engine.request.QueryRequest;
import xbird.util.io.IOUtils;
import xbird.util.io.NoopWriter;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RemoteEvalFromClientTest {

    @DataProvider(name = "scenario01")
    public Object[][] getSettings() {
        return new Object[][] { { "//localhost:1099/xbird/srv-01",
                "/root/.eclipse/xbird-db/examples/grid/map01.xq" } };
    }

    @Test(dataProvider = "scenario01", invocationCount = 1, threadPoolSize = 1)
    public void scenario01(String remoteEndpoint, String fileName) throws FileNotFoundException,
            IOException, XQueryException {
        XQEngine engine = new XQEngineClient(remoteEndpoint);
        String query = IOUtils.toString(new FileInputStream(fileName));
        QueryRequest request = new QueryRequest(query, ReturnType.ASYNC_REMOTE_SEQUENCE);
        Sequence<Item> resultSeq = (Sequence<Item>) engine.execute(request);
        Writer writer = new NoopWriter();
        SAXWriter saxwr = new SAXWriter(writer, "UTF-8");
        Serializer ser = new SAXSerializer(saxwr, writer);
        ser.emit(resultSeq);
    }

    public static void main(String[] args) {
        TestListenerAdapter adapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { RemoteEvalFromClientTest.class });
        testng.addListener(adapter);
        testng.run();
    }
}
