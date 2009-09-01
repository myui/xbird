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
package xbird.ext.xstream;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import xbird.storage.DbException;
import xbird.util.io.FileUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.dtm.DocumentTableLoader;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ProxyNodeSequence;
import xbird.xquery.meta.DynamicContext;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.persistence.XmlArrayList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XBirdCollectionStrategyTest {

    private static final String COLLECTION_NAME = "xstreamTest";
    private static final File colDir = new File(System.getProperty("java.io.tmpdir")
            + File.separatorChar + "xbird", COLLECTION_NAME);

    public XBirdCollectionStrategyTest() throws IOException {
        if(colDir.exists()) {
            FileUtils.cleanDirectory(colDir);
        } else {
            colDir.mkdir();
        }
        System.out.println("collection directory: " + colDir.getAbsolutePath());
    }

    @After
    public void tearDown() throws IOException {
        DocumentTableLoader.clean();
        FileUtils.cleanDirectory(colDir);
    }

    @Test
    public void addingAuthors1() {
        XStream xstream = XBirdCollectionStrategy.getAnnotationProcessableXStreamInstance();
        XBirdCollectionStrategy<String, Object> strategy = new XBirdCollectionStrategy<String, Object>(COLLECTION_NAME, xstream);

        //xstream.processAnnotations(Author.class);

        List<Author> list = new XmlArrayList(strategy);

        // adds four authors
        list.add(new Author("joe walnes"));
        list.add(new Author("joerg schaible"));
        list.add(new Author("mauro talevi"));
        list.add(new Author("guilherme silveira"));

        // adding an extra author
        Author mistake = new Author("mama");
        list.add(mistake);

        Assert.assertEquals(5, list.size());

        List<Author> list2 = new XmlArrayList(strategy);
        Iterator<Author> itor = list2.iterator();
        while(itor.hasNext()) {
            Author author = itor.next();
            if(author.getName().equals("mama")) {
                System.out.println("Removing mama...");
                itor.remove();
            } else {
                System.out.println("Keeping " + author.getName());
            }
        }

        Assert.assertEquals(4, list.size());
        list2.clear();
    }

    //@Test
    public void addingRendezvousMessages1() throws DbException, XQueryException {
        XStream xstream = new XStream();
        XBirdCollectionStrategy<String, Object> strategy = new XBirdCollectionStrategy<String, Object>(COLLECTION_NAME, xstream);
        xstream.processAnnotations(RendezvousMessage.class);

        RendezvousMessage msg1 = new RendezvousMessage(15, Arrays.asList(new Author("anonymous"), new Author("makoto")));
        System.out.println(xstream.toXML(msg1));
        System.out.println();

        RendezvousMessage msg2 = new RendezvousMessage(15, Arrays.asList(new Author("anonymous")), "firstPart", "secondPart");
        System.out.println(xstream.toXML(msg2));
        System.out.println();

        List<RendezvousMessage> list = new XmlArrayList(strategy);
        list.add(msg1);
        list.add(msg2);
        Assert.assertEquals(2, list.size());

        String query1 = "fn:collection('/" + COLLECTION_NAME + "/1.xml')//author[1]";
        XQueryProcessor proc = new XQueryProcessor();
        XQueryModule compiled1 = proc.parse(query1);
        StringWriter sw = new StringWriter();
        SAXWriter handler = new SAXWriter(sw);
        SAXSerializer ser = new SAXSerializer(handler);

        proc.execute(compiled1, ser);
        handler.flush();
        String result1 = sw.toString();

        System.err.println(result1);

        Author author1 = (Author) xstream.fromXML(result1);
        Assert.assertEquals("anonymous", author1.getName());
    }

    //@Test
    public void addingRendezvousMessages2() throws DbException, XQueryException {
        XStream xstream = new XStream();
        XBirdCollectionStrategy<String, Object> strategy = new XBirdCollectionStrategy<String, Object>(COLLECTION_NAME, xstream);
        xstream.processAnnotations(RendezvousMessage.class);
        xstream.processAnnotations(Author.class);

        List<Author> author1 = Arrays.asList(new Author("anonymous"));
        RendezvousMessage msg1 = new RendezvousMessage(15, author1);
        System.out.println(xstream.toXML(msg1));
        System.out.println();

        List<Author> author2 = Arrays.asList(new Author("makoto"), new Author("leo"), new Author("grun"));
        RendezvousMessage msg2 = new RendezvousMessage(15, author2, "firstPart", "secondPart");
        System.out.println(xstream.toXML(msg2));
        System.out.println();

        List<RendezvousMessage> list = new XmlArrayList(strategy);
        list.add(msg1);
        list.add(msg2);
        Assert.assertEquals(2, list.size());

        String query1 = "fn:collection('/" + COLLECTION_NAME + "/.*.xml')//author";
        XQueryProcessor proc = new XQueryProcessor();
        XQueryModule compiled1 = proc.parse(query1);
        Sequence<? extends Item> items = proc.execute(compiled1);
        INodeSequence<DTMElement> nodes = ProxyNodeSequence.wrap(items, DynamicContext.DUMMY);

        for(DTMElement node : nodes) {
            Object unmarshalled = xstream.unmarshal(new DTMReader(node));
            Author author = (Author) unmarshalled;
            System.out.println("author: " + author.getName());
        }
    }

    @XStreamAlias("message")
    public static class RendezvousMessage {

        @XStreamImplicit(itemFieldName = "author")
        private List<Author> authors;

        @XStreamOmitField
        private int messageType = -1;

        //@XStreamImplicit(itemFieldName = "part")
        private List<String> content;

        @XStreamConverter(SingleValueCalendarConverter.class)
        private Calendar created = new GregorianCalendar();

        public RendezvousMessage(int messageType, List<Author> authors, String... content) {
            this.authors = authors;
            this.messageType = messageType;
            this.content = Arrays.asList(content);
        }

        @Override
        public int hashCode() {
            return content.hashCode() ^ authors.hashCode() + messageType;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof RendezvousMessage)) {
                return false;
            }
            RendezvousMessage other = (RendezvousMessage) obj;
            if(authors.equals(other.authors) && content.equals(other.content)
                    && created.equals(other.created)) {
                return true;
            }
            return false;
        }

    }

    @XStreamAlias("author")
    public static class Author {
        private String name;

        public Author(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Author)) {
                return false;
            }
            return name.equals(((Author) obj).name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static final class SingleValueCalendarConverter implements Converter {

        public SingleValueCalendarConverter() {
            super();
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Calendar calendar = (Calendar) source;
            writer.setValue(String.valueOf(calendar.getTime().getTime()));
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date(Long.parseLong(reader.getValue())));
            return calendar;
        }

        public boolean canConvert(Class type) {
            return type.equals(GregorianCalendar.class);
        }
    }

}
