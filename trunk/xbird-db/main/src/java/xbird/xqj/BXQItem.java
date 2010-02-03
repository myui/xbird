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
package xbird.xqj;

import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.Properties;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.*;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.adapter.StreamReaderAdapter;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class BXQItem implements XQItem {

    protected Item item_;
    protected boolean _closed = false;

    public BXQItem(Item item) {
        this.item_ = item;
    }

    public void close() throws XQException {
        this._closed = true;
        this.item_ = null;
    }

    public boolean isClosed() {
        return _closed;
    }

    public String getAtomicValue() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof AtomicValue) {
            return item.stringValue();
        }
        throw new XQException("The current item must be an atomic value, but was " + item, "err:XQJxxxx");
    }

    public boolean getBoolean() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof BooleanValue) {
            return ((BooleanValue) item).booleanValue();
        }
        throw new XQException("The current item must be an atomic value of type xs:boolean or a subtype, but was "
                + item, "err:XQJxxxx");
    }

    public byte getByte() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof XNumber) {
            final Type type = item.getType();
            if(type instanceof DecimalType) {
                XNumber num = (XNumber) item;
                final long lv = num.asLong();
                if(!ByteType.inRange(lv)) {
                    throw new XQException("The value must be in the value space of byte: " + lv, "err:XQJxxxx");
                }
                return (byte) lv;
            }
        }
        throw new XQException("Conversion to a byte failed for " + item, "err:XQJxxxx");
    }

    public double getDouble() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof XDouble) {
            return ((XDouble) item).asDouble();
        }
        throw new XQException("Conversion to a double failed for " + item, "err:XQJxxxx");
    }

    public float getFloat() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof XFloat) {
            return ((XFloat) item).asFloat();
        }
        throw new XQException("Conversion to a float failed for " + item, "err:XQJxxxx");
    }

    public int getInt() throws XQException {
        ensureNotClosed();
        final Item item = item_;
        if(item instanceof XNumber) {
            final Type type = item.getType();
            if(type instanceof DecimalType) {
                XNumber num = (XNumber) item;
                final long lv = num.asLong();
                if(!IntType.inRange(lv)) {
                    throw new XQException("The value must be in the value space of int: " + lv, "err:XQJxxxx");
                }
                return (byte) lv;
            }
        }
        throw new XQException("Conversion to int failed for " + item, "err:XQJxxxx");
    }

    public XMLStreamReader getItemAsStream() throws XQException {
        return new StreamReaderAdapter(item_);
    }

    public String getItemAsString(Properties props) throws XQException {
        return item_.toString();
    }

    public XQItemType getItemType() throws XQException {
        final Type type = item_.getType();
        return new BXQItemType(type);
    }

    public long getLong() throws XQException {
        final Type type = item_.getType();
        if(type instanceof DecimalType) {
            final DecimalType dt = (DecimalType) type;
            final String sv = item_.stringValue();
            try {
                dt.createInstance(sv, LongType.LONG, DynamicContext.DUMMY);
            } catch (XQueryException e) {
                final XQException xqe = new XQException(e.getMessage());
                xqe.initCause(e);
                throw xqe;
            }
        }
        throw new XQException("convertion to long failed: " + item_);
    }

    public Node getNode() throws XQException {
        return null;
    }

    public URI getNodeUri() throws XQException {
        return null;
    }

    public Object getObject() throws XQException {
        final Item item = item_;
        if(item instanceof AtomicValue) {
            final AtomicValue atomic = (AtomicValue) item;
            try {
                return atomic.toJavaObject();
            } catch (XQueryException e) {
                final XQException xqe = new XQException(e.getMessage());
                xqe.initCause(e);
                throw xqe;
            }
        } else if(item instanceof XQNode) {
            return getNode();
        }
        throw new XQException("Unexpected item class: " + item.getClass().getName());
    }

    public short getShort() throws XQException {
        final Type type = item_.getType();
        if(type instanceof DecimalType) {
            final DecimalType dt = (DecimalType) type;
            final String sv = item_.stringValue();
            try {
                dt.createInstance(sv, ShortType.SHORT, DynamicContext.DUMMY);
            } catch (XQueryException e) {
                final XQException xqe = new XQException(e.getMessage());
                xqe.initCause(e);
                throw xqe;
            }
        }
        throw new XQException("convertion to short failed: " + item_);
    }

    public boolean instanceOf(XQItemType type) throws XQException {
        return false;
    }

    public void writeItem(OutputStream os, Properties props) throws XQException {
        final SAXWriter saxhdlr = new SAXWriter(os);
        writeItemToSAX(saxhdlr);
    }

    public void writeItem(Writer ow, Properties props) throws XQException {
        final SAXWriter saxhdlr = new SAXWriter(ow);
        writeItemToSAX(saxhdlr);
    }

    public void writeItemToResult(Result result) throws XQException {
        if(result instanceof DOMResult) {
            final DOMResult domResult = (DOMResult) result;
            final Node rootNode = domResult.getNode();
            final Node newNode = getNode();
            if(rootNode != null) {
                rootNode.appendChild(newNode); // REVIEWME
            } else {
                domResult.setNode(newNode);
            }
        } else if(result instanceof SAXResult) {
            ContentHandler handler = ((SAXResult) result).getHandler();
            writeItemToSAX(handler);
        } else if(result instanceof StreamResult) {
            StreamResult streamResult = (StreamResult) result;
            final Writer writer = streamResult.getWriter();
            if(writer == null) {
                OutputStream os = streamResult.getOutputStream();
                writeItem(os, new Properties());
            } else {
                writeItem(writer, new Properties());
            }
        } else {
            throw new XQException("Unsupport Result class: " + result.getClass().getName());
        }
    }

    public void writeItemToSAX(ContentHandler saxhdlr) throws XQException {
        final SAXSerializer ser = new SAXSerializer(saxhdlr);
        try {
            ser.emit(item_);
        } catch (XQueryException e) {
            final XQException xqe = new XQException(e.getMessage());
            xqe.initCause(e);
            throw xqe;
        }
    }

    private void ensureNotClosed() throws XQException {
        if(_closed) {
            throw new XQException("The underlying item is already closed", "err:XQJxxxx");
        }
    }

}
