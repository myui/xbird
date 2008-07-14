/*
 * @(#)$Id: NamespaceBinder.java 3621 2008-03-26 07:29:06Z yui $
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

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import xbird.util.iterator.EmptyIterator;

/**
 * The class manages contexts of XML namespace processing.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class NamespaceBinder implements NamespaceContext, Serializable {
    private static final long serialVersionUID = 3592735755238731324L;

    private static final int DEFAULT_CONTEXT_SIZE = 32;

    private Context contexts[];
    private Context currentContext;
    private int contextPos;

    public NamespaceBinder() {
        reset();
    }

    public String getNamespaceURI(String prefix) {
        return currentContext.getNamespaceURI(prefix);
    }

    public String getPrefix(String nsuri) {
        return currentContext.getPrefix(nsuri);
    }

    public Iterator<String> getPrefixes(String nsuri) {
        if(nsuri == null) {
            throw new IllegalArgumentException();
        }
        Iterator<String> i = currentContext.getPrefixes();
        if(!i.hasNext()) {
            return EmptyIterator.<String> emptyIterator();
        }
        final List<String> list = new LinkedList<String>();
        do {
            String prefix = i.next();
            if(nsuri.equals(getNamespaceURI(prefix))) {
                list.add(prefix);
            }
        } while(i.hasNext());
        return list.iterator();
    }

    /**
     * Revert to the previous Namespace context.
     */
    public void popContext() {
        contextPos--;
        if(contextPos < 0) {
            throw new EmptyStackException();
        }
        currentContext = contexts[contextPos];
    }

    /**
     * Start a new Namespace context.
     */
    public void pushContext() {
        int max = contexts.length;
        contextPos++;

        // Extend the array if necessary
        if(contextPos >= max) {
            Context newContexts[] = new Context[max * 2];
            System.arraycopy(contexts, 0, newContexts, 0, max);
            contexts = newContexts;
        }

        // Allocate the context if necessary.
        currentContext = contexts[contextPos];
        if(currentContext == null) {
            contexts[contextPos] = currentContext = new Context();
        }

        // Set the parent, if any.
        if(contextPos > 0) {
            currentContext.setParent(contexts[contextPos - 1]);
        }
    }

    /**
     * Reset this Namespace support object for reuse.
     */
    public final void reset() {
        contexts = new Context[DEFAULT_CONTEXT_SIZE];
        contextPos = 0;
        contexts[contextPos] = currentContext = new Context();
        currentContext.declarePrefix(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    }
    
    public final void clean() {
        this.contexts = null;
        this.currentContext = null;
    }

    /**
     * Declare a Namespace prefix.
     */
    public boolean declarePrefix(String prefix, String uri) {
        if(XMLConstants.XML_NS_PREFIX.equals(prefix) || XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return false;
        } else {
            currentContext.declarePrefix(prefix, uri);
            return true;
        }
    }

    public void declarePrefixs(final Map<String, String> nsmap) {
        for(Entry<String, String> e : nsmap.entrySet()) {
            declarePrefix(e.getKey(), e.getValue());
        }
    }

    /**
     * Return an enumeration of all prefixes currently declared.
     */
    public Iterator<String> getAllPrefixes() {
        return currentContext.getPrefixes();
    }

    public String[] getAllPrefixesAsArray() {
        final Set<String> prefixes = currentContext.getPrefixesAsArray();
        final int size = prefixes.size();
        return prefixes.toArray(new String[size]);
    }

    public int getNamespaceCount() {
        return currentContext.getNamespaceCount();
    }

    /**
     * Returns an NamespaceURI value of the specified index.
     */
    public String getNamespaceURI(int index) {
        return getNamespaceURI(getPrefix(index));
    }

    /**
     * Returns an namespace prefix of the specified index.
     */
    public String getPrefix(int index) {
        return currentContext.getPrefix(index);
    }

    public String toString() {
        return currentContext.toString();
    }

    /**
     * Internal class for a single Namespace context.
     */
    protected static final class Context implements Serializable {
        private static final long serialVersionUID = 1L;

        private Context _parent = null;

        private final Map<String, String> namespaces = new HashMap<String, String>();

        private final List<String> insertOrder = new ArrayList<String>();

        Context() {}

        void setParent(Context context) {
            this._parent = context;
            // inherits namespaces from the given parent context 
            // to the current context.
            this.namespaces.putAll(context.getNamespaces());
            this.insertOrder.addAll(context.insertOrder);
        }

        int getNamespaceCount() {
            return this.namespaces.size() - (_parent == null ? 0 : _parent.getNamespaces().size());
        }

        String getNamespaceURI(String prefix) {
            return namespaces.get(prefix);
        }

        String getPrefix(int index) {
            // take a thought of parent's namespaces.
            final int i = (_parent == null ? 0 : _parent.getNamespaces().size()) + index;
            return insertOrder.get(i);
        }

        String getPrefix(String nsuri) {
            Iterator entries = namespaces.entrySet().iterator();
            while(entries.hasNext()) {
                Map.Entry e = (Entry) entries.next();
                if(nsuri.equals(e.getValue())) {
                    return (String) e.getKey();
                }
            }
            return null;
        }

        Iterator<String> getPrefixes() {
            return namespaces.keySet().iterator();
        }

        Set<String> getPrefixesAsArray() {
            return namespaces.keySet();
        }

        void declarePrefix(String prefix, String nsuri) {
            if(nsuri == null) {
                throw new IllegalArgumentException("NamespaceURI must be specified");
            }
            assert (prefix != null);
            String old = namespaces.put(prefix, nsuri);
            if(old == null) {
                insertOrder.add(prefix);
            }
        }

        private Map<String, String> getNamespaces() {
            return namespaces;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder(255);
            buf.append(namespaces.toString());
            if(_parent != null) {
                buf.append('\n');
                buf.append(_parent.toString());
            }
            return buf.toString();
        }
    }

}