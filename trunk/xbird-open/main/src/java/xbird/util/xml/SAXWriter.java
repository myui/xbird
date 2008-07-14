/*
 * @(#)$Id: SAXWriter.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.xml;

import java.io.*;
import java.nio.charset.Charset;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import xbird.util.io.FastBufferedWriter;
import xbird.util.resource.Encodings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SAXWriter extends DefaultHandler implements LexicalHandler {

    public static final String DEFAULT_ENCODING = "UTF-8";

    // controls
    private final Writer _writer;
    private final String _encoding;
    private int _depth = -1;
    private boolean _inCData = false;
    private boolean _isXML11 = false;
    private boolean _inElement = false;
    private boolean _startDocumentDone = false;

    // properties
    private boolean _enforceFlashOnEachEvent = false;
    private boolean _prettyPrint = false;
    private boolean _writesXMLDecl = false;
    private String _indent = "  ";
    private String _version = "1.0";

    /**
     * Note that "file.encoding" system property is written to XML encoding.
     */
    public SAXWriter(Writer writer) {
        this(writer, Encodings.getFileEncodingIANA());
    }

    public SAXWriter(Writer writer, String defaultEncoding) {
        super();
        if(defaultEncoding == null) {
            throw new IllegalArgumentException();
        }
        this._writer = writer;
        this._encoding = defaultEncoding;
        if(writer instanceof OutputStreamWriter) {
            final String enc = ((OutputStreamWriter) writer).getEncoding();
            if(!defaultEncoding.equals(enc)) {
                throw new IllegalStateException("Invalid encoding '" + defaultEncoding
                        + "' is specified for '" + enc + '\'');
            }
        }
    }

    public SAXWriter(OutputStream os) {
        this(os, DEFAULT_ENCODING);
    }

    public SAXWriter(OutputStream os, String encoding) {
        this(new FastBufferedWriter(new OutputStreamWriter(os, Charset.forName(encoding)), 4096), encoding);
    }

    public void setVersion(String ver) {
        this._version = ver;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this._prettyPrint = prettyPrint;
    }

    public void setXMLDeclaration(boolean decl) {
        this._writesXMLDecl = decl;
    }

    public Writer getWriter() {
        return _writer;
    }

    //--------------------------------------------
    // ContentHandler interfaces

    @Override
    public void startDocument() throws SAXException {
        if(_startDocumentDone) {
            return;
        } else {
            this._startDocumentDone = true;
        }
        if("1.1".equals(_version)) {
            _isXML11 = true;
        }
        if(!_writesXMLDecl) {
            return;
        }
        try {
            final Writer writer = _writer;
            writer.write("<?xml version='" + _version + "'");
            if(_encoding != null) {
                writer.write(" encoding='" + _encoding + "'?>\n");
            } else {
                writer.write("?>\n");
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        flush();
    }

    @Override
    public void startElement(String uri, String local, String raw, Attributes attrs)
            throws SAXException {
        if(_writesXMLDecl && !_startDocumentDone) {
            startDocument();
        }
        _depth++;
        try {
            indent();
            final Writer writer = _writer;
            writer.write('<');
            writer.write(raw);
            final boolean isXML11 = _isXML11;
            final int limit = attrs.getLength();
            for(int i = 0; i < limit; i++) {
                writer.write(' ');
                writer.write(attrs.getQName(i));
                writer.write("=\"");
                normalizeAndPrint(writer, attrs.getValue(i), true, isXML11);
                writer.write("\"");
            }
            writer.write('>');
        } catch (IOException e) {
            throw new SAXException(e);
        }
        this._inElement = true;
    }

    @Override
    public void endElement(String uri, String local, String raw) throws SAXException {
        try {
            indent();
            final Writer writer = _writer;
            writer.write("</");
            writer.write(raw);
            writer.write('>');
        } catch (IOException e) {
            throw new SAXException(e);
        }
        _depth--;
        if(_enforceFlashOnEachEvent) {
            flush();
        }
        this._inElement = false;
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if(_writesXMLDecl && !_startDocumentDone) {
            startDocument();
        }
        try {
            final Writer writer = _writer;
            if(_inCData) {
                writer.write(ch, start, length);
            } else {
                if(_inElement) {
                    normalizeAndPrint(writer, ch, start, length, false, _isXML11);
                } else {
                    writer.write(ch, start, length);
                }
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
        if(_enforceFlashOnEachEvent) {
            flush();
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    // NOP
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if(_writesXMLDecl && !_startDocumentDone) {
            startDocument();
        }
        try {
            indent();
            final Writer writer = _writer;
            writer.write("<?");
            writer.write(target);
            if(data != null && data.length() > 0) {
                writer.write(' ');
                writer.write(data);
            }
            writer.write("?>");
        } catch (IOException e) {
            throw new SAXException(e);
        }
        if(_enforceFlashOnEachEvent) {
            flush();
        }
    }

    //--------------------------------------------
    // LexicalHandler interfaces

    public void startCDATA() throws SAXException {
        if(_writesXMLDecl && !_startDocumentDone) {
            startDocument();
        }
        try {
            indent();
            _writer.write("<![CDATA[");
        } catch (IOException e) {
            throw new SAXException(e);
        }
        _inCData = true;
    }

    public void endCDATA() throws SAXException {
        try {
            _writer.write("]]>");
        } catch (IOException e) {
            throw new SAXException(e);
        }
        _inCData = false;
        if(_enforceFlashOnEachEvent) {
            flush();
        }
    }

    public void comment(char ch[], int start, int length) throws SAXException {
        if(_writesXMLDecl && !_startDocumentDone) {
            startDocument();
        }
        try {
            indent();
            final Writer writer = _writer;
            writer.write("<!--");
            final int limit = start + length;
            int i = start;
            for(; i + 3 < limit; i += 4) {
                writer.write(ch[i]);
                writer.write(ch[i + 1]);
                writer.write(ch[i + 2]);
                writer.write(ch[i + 3]);
            }
            for(; i < limit; i++) {
                writer.write(ch[i]);
            }
            writer.write("-->");
        } catch (IOException e) {
            throw new SAXException(e);
        }
        if(_enforceFlashOnEachEvent) {
            flush();
        }
    }

    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {}

    public void endDTD() throws SAXException {}

    public void startEntity(String arg0) throws SAXException {}

    public void endEntity(String arg0) throws SAXException {}

    //--------------------------------------------
    // helpers

    public void flush() {
        try {
            _writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException("flash failed.", e);
        }
    }

    private void indent() throws IOException {
        if(_prettyPrint) {
            final Writer writer = _writer;
            final String indent = _indent;
            final int limit = _depth;
            for(int i = 0; i < limit; i++) {
                writer.write(indent);
            }
        }
    }

    private static void normalizeAndPrint(final Writer writer, final char[] ch, final int offset, final int length, final boolean isAttValue, final boolean isXML11)
            throws IOException {
        int i = offset;
        final int last = offset + length;
        final int limit = last - 7;
        for(; i < limit; i += 8) {
            normalizeAndPrint(writer, ch[i], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 1], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 2], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 3], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 4], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 5], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 6], isAttValue, isXML11);
            normalizeAndPrint(writer, ch[i + 7], isAttValue, isXML11);
        }
        for(; i < last; i++) {
            normalizeAndPrint(writer, ch[i], isAttValue, isXML11);
        }
    }

    private static void normalizeAndPrint(final Writer writer, final String s, final boolean isAttValue, final boolean isXML11)
            throws IOException {
        if(s == null) {
            return;
        }
        final int last = s.length();
        final int limit = last - 3;
        int i = 0;
        for(; i < limit; i += 4) {
            normalizeAndPrint(writer, s.charAt(i), isAttValue, isXML11);
            normalizeAndPrint(writer, s.charAt(i + 1), isAttValue, isXML11);
            normalizeAndPrint(writer, s.charAt(i + 2), isAttValue, isXML11);
            normalizeAndPrint(writer, s.charAt(i + 3), isAttValue, isXML11);
        }
        for(; i < last; i++) {
            normalizeAndPrint(writer, s.charAt(i), isAttValue, isXML11);
        }
    }

    private static void normalizeAndPrint(final Writer writer, final char c, final boolean isAttValue, final boolean isXML11)
            throws IOException {
        switch(c) {
            case '<':
                writer.write("&lt;");
                break;
            case '>':
                writer.write("&gt;");
                break;
            case '&':
                writer.write("&amp;");
                break;
            case '"':
                if(isAttValue) {
                    writer.write("&quot;");
                } else {
                    writer.write('\"');
                }
                break;
            /*
             case '\r': {
             writer.write( "&#xD;");
             break;
             }
             */
            default:
                // In XML 1.1, control chars in the ranges [#x1-#x1F, #x7F-#x9F] must be escaped.
                //
                // Escape space characters that would be normalized to #x20 in attribute values
                // when the document is reparsed.
                //
                // Escape NEL (0x85) and LSEP (0x2028) that appear in content 
                // if the document is XML 1.1, since they would be normalized to LF 
                // when the document is reparsed.
                if(isXML11
                        && ((c >= 0x01 && c <= 0x1F && c != 0x09 && c != 0x0A)
                                || (c >= 0x7F && c <= 0x9F) || c == 0x2028) || isAttValue
                        && (c == 0x09 || c == 0x0A)) {
                    writer.write("&#x");
                    writer.write(Integer.toHexString(c).toUpperCase());
                    writer.write(';');
                } else {
                    writer.write(c);
                }
        }
    }

}