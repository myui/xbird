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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter;
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

    public XBirdCollectionStrategyTest() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File colDir = new File(tmpDir, COLLECTION_NAME);

        if(colDir.exists()) {
            colDir.delete();
        }
        colDir.mkdir();
        System.out.println("collection directory: " + colDir.getAbsolutePath());
    }

    @Test
    public void addingElements() {
        XBirdCollectionStrategy<String, Object> strategy = new XBirdCollectionStrategy<String, Object>(COLLECTION_NAME);

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
    }

    @XStreamAlias("message")
    public class RendezvousMessage {

        private Author author;

        @XStreamOmitField
        private int messageType = -1;

        @XStreamImplicit(itemFieldName = "part")
        private List<String> content;

        @XStreamConverter(GregorianCalendarConverter.class)
        private Calendar created = new GregorianCalendar();

        public RendezvousMessage(int messageType, String... content) {
            this.author = new Author("anonymous");
            this.messageType = messageType;
            this.content = Arrays.asList(content);
        }

        @Override
        public int hashCode() {
            return content.hashCode() ^ author.hashCode() + messageType;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof RendezvousMessage)) {
                return false;
            }
            RendezvousMessage other = (RendezvousMessage) obj;
            if(author.equals(other.author) && content.equals(other.content)
                    && created.equals(other.created)) {
                return true;
            }
            return false;
        }

    }

    public class Author {
        private final String name;

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
}
