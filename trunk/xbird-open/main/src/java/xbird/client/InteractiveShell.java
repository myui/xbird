/*
 * @(#)$Id: InteractiveShell.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import xbird.engine.XQEngine;
import xbird.engine.XQEngineClient;
import xbird.engine.Request.ReturnType;
import xbird.engine.request.QueryRequest;
import xbird.server.ServiceException;
import xbird.server.services.PerfmonService;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FastBufferedWriter;
import xbird.util.io.IOUtils;
import xbird.util.lang.PrintUtils;
import xbird.util.resource.ResourceUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
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
public final class InteractiveShell {

    @Option(name = "-o", usage = "output to this file", metaVar = "OUTPUT")
    private File _out = null;

    @Option(name = "-q", usage = "execute query in the specified file", metaVar = "FILE")
    private File _queryFile = null;

    @Option(name = "-pp", usage = "enable XML pretty printting (default=false)")
    private boolean _prettyPrint = false;

    @Option(name = "-baseuri", usage = "specify implicit baseURI", metaVar = "URI")
    private String _baseUri = null;

    @Option(name = "-timeout", usage = "specify timeout in seconds", metaVar = "TIME(seconds)")
    private int _timeout = -1;

    @Option(name = "-t", usage = "enable timing (default=false)")
    private boolean _timing = false;

    @Option(name = "-tms", usage = "enable timing in mill seconds (default=false)")
    private boolean _timing_in_msec = false;

    @Option(name = "-perfmon", usage = "enable performance monitoring (default=false)")
    private boolean _perfmon = false;

    @Option(name = "-encoding", usage = "encoding of result XML document (default=UTF-8)", metaVar = "ENCODING")
    private String _encoding = "UTF-8";

    @Option(name = "-debug", usage = "Run in debug mode (default=false)")
    private boolean _debug = false;

    @Option(name = "-pull", usage = "Execute in Pull mode (default=false)")
    private boolean _pull = false;

    @Option(name = "-wrap", usage = "Wrap result with dummy root node (default=false)")
    private boolean _wrap = false;

    @Option(name = "-ep", usage = "Remote Endpoint", metaVar = "URI (e.g., //localhost:1099/xbird/srv-01)")
    private String _remoteEndpoint = null;

    @Option(name = "-help", usage = "Show help (default=false)")
    private boolean _showHelp = false;

    private CmdLineParser _parser;

    public InteractiveShell() {}

    private void batchExec() throws XQueryException {
        assert (_queryFile != null);
        String path = _queryFile.getPath();
        if(path.startsWith("file:\\")) { // work around for stylus studio
            path = path.substring(6);
            _queryFile = new File(path);
        }
        if(_baseUri == null) {
            _baseUri = ResourceUtils.toURIString(_queryFile.getParentFile());
        }
        final Reader fis;
        try {
            fis = new FileReader(_queryFile);
        } catch (FileNotFoundException e) {
            throw new DynamicError("Illegal Query file: " + path, e);
        }
        execute(fis, _timeout);
    }

    private void interactiveExec() {
        System.out.println("[xbird interactive shell]\n");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder buf = new StringBuilder(255);
        while(true) {
            System.out.print("> ");
            final String line;
            try {
                line = input.readLine();
            } catch (IOException e) {
                throw new IllegalStateException("stdin read error!", e);
            }
            if(line == null) { // end of stream
                break;
            } else if(line.equals("quit;") || line.equals("exit;")) {
                System.out.println("Bye!");
                System.exit(0);
            } else if(line.equals("help;")) {
                showHelp();
                continue;
            }
            int llength = line.length();
            if(llength == 0) {
                continue;
            } else {
                char last = line.charAt(llength - 1);
                if(last == '\\') { // read through
                    buf.append(line.substring(0, llength - 2));
                    continue;
                } else if(last == ';') {
                    buf.append(line);
                    continue;
                }
                buf.append(line);
                String query = buf.toString();
                buf.setLength(0);
                try {
                    execute(new StringReader(query), _timeout);
                } catch (Exception e) {//avoid
                    String msg = e.getMessage();
                    if(msg != null) {
                        System.err.println(e.getMessage());
                    } else {
                        System.err.println("execution failed!");
                    }
                    StackTraceElement[] st = e.getStackTrace();
                    System.err.println(st[1]);
                }
            }
        }
    }

    private void execute(Reader reader, int timeout) throws XQueryException {
        final StopWatch sw = new StopWatch("elapsed time");
        if(_perfmon) {
            try {
                new PerfmonService().start();
            } catch (ServiceException e) {
                System.err.println(PrintUtils.prettyPrintStackTrace(e));
            }
        }
        if(timeout > 0) {
            TimerTask cancel = new TimerTask() {
                public void run() {
                    System.err.println("Execution Timeout: " + sw.toString());
                    System.exit(1);
                }
            };
            Timer timer = new Timer();
            timer.schedule(cancel, timeout * 1000);
            try {
                execute(reader);
            } catch (XQueryException e) {
                timer.cancel();
                throw e;
            }
            timer.cancel();
        } else {
            execute(reader);
        }
        if(_timing) {
            if(_timing_in_msec) {
                System.out.println('\n' + sw.elapsed() + " msec");
            } else {
                System.out.println();
                System.out.println(sw);
            }
        }
    }

    private URI getBaseUri() {
        final URI baseuri;
        if(_baseUri != null) {
            baseuri = ResourceUtils.buildURI(_baseUri);
        } else {
            if(_queryFile != null) {
                baseuri = _queryFile.toURI();
            } else {
                String userdir = System.getProperty("user.dir");
                File file = new File(userdir);
                baseuri = file.toURI();
            }
        }
        assert (baseuri != null);
        return baseuri;
    }

    private void execute(Reader reader) throws XQueryException {
        final Writer writer;
        if(_out != null) {
            try {
                writer = new FastBufferedWriter(new OutputStreamWriter(new FileOutputStream(_out), _encoding), 8192);
            } catch (IOException e) {
                throw new DynamicError("Illegal Output file: " + e.getMessage());
            }
        } else {
            writer = new FastBufferedWriter(new OutputStreamWriter(System.out, Charset.forName(_encoding)), 4096);
        }

        if(_remoteEndpoint != null) {
            try {
                executeAt(reader, writer, _remoteEndpoint);
            } catch (IOException e) {
                throw new XQueryException("Caused an IO error", e);
            }
        } else {
            XQueryModule xqmod = new XQueryModule();
            XQueryProcessor proc = new XQueryProcessor(xqmod);
            XQueryModule module = proc.parse(reader, getBaseUri());
            if(_pull) {
                executeWithPullMode(proc, module, writer);
            } else {
                executeWithPushMode(proc, module, writer);
            }
        }
        try {
            writer.flush();
        } catch (IOException ignorable) {
            // avoid this error
            String msg = ignorable.getMessage();
            if(msg != null) {
                System.err.println(msg);
            }
        }
    }

    private SAXWriter prepareSAXWriter(Writer writer) {
        final SAXWriter saxwr = new SAXWriter(writer, _encoding);
        if(_prettyPrint) {
            saxwr.setPrettyPrint(true);
        }
        if(_wrap) {
            saxwr.setXMLDeclaration(false);
            try {
                writer.write("<root>\n");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return saxwr;
    }

    private void executeAt(Reader reader, Writer writer, String remoteEndpoint)
            throws XQueryException, IOException {
        XQEngine engine = new XQEngineClient(remoteEndpoint);
        String query = IOUtils.toString(reader);
        QueryRequest request = new QueryRequest(query, ReturnType.ASYNC_REMOTE_SEQUENCE);
        final Sequence<Item> resultSeq;
        try {
            resultSeq = (Sequence<Item>) engine.execute(request);
        } catch (RemoteException e) {
            throw new XQueryException("failed to execute a query", e);
        }

        SAXWriter saxwr = prepareSAXWriter(writer);
        Serializer ser = new SAXSerializer(saxwr, writer);
        ser.emit(resultSeq);
        if(_wrap) {
            try {
                writer.write("\n</root>\n");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void executeWithPushMode(XQueryProcessor proc, XQueryModule module, Writer writer)
            throws XQueryException {
        SAXWriter saxwr = prepareSAXWriter(writer);
        Serializer ser = new SAXSerializer(saxwr, writer);
        proc.execute(module, ser);
        if(_wrap) {
            try {
                writer.write("\n</root>\n");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void executeWithPullMode(XQueryProcessor proc, XQueryModule module, Writer writer)
            throws XQueryException {
        SAXWriter saxwr = prepareSAXWriter(writer);
        Serializer ser = new SAXSerializer(saxwr, writer);
        Sequence<? extends Item> result = proc.execute(module);
        if(_timing) {
            final StopWatch sw = new StopWatch("print time");
            ser.emit(result);
            if(_timing_in_msec) {
                System.out.println('\n' + sw.elapsed() + " msec");
            } else {
                System.out.println();
                System.out.println(sw);
            }
        } else {
            ser.emit(result);
        }
        if(_wrap) {
            try {
                writer.write("\n</root>\n");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new InteractiveShell().run(args);
        } catch (Throwable e) {
            PrintUtils.prettyPrintStackTrace(e, System.err);
            System.exit(1);
        }
        System.exit(0);
    }

    public void run(String[] args) throws XQueryException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            showHelp();
            return;
        }
        this._parser = parser;
        if(_showHelp) {
            showHelp();
            return;
        }
        if(_queryFile != null) {
            batchExec();
        } else {
            interactiveExec();
        }
    }

    private void showHelp() {
        assert (_parser != null);
        System.err.println("[Usage] \n $ java " + getClass().getSimpleName()
                + _parser.printExample(ExampleMode.ALL));
        _parser.printUsage(System.err);
    }

}
