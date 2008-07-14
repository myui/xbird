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
package xbird.client.command;

import java.util.Arrays;

import javax.xml.namespace.NamespaceContext;

import xbird.storage.DbCollection;
import xbird.util.lang.ArrayUtils;
import xbird.util.xml.NamespaceBinder;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CreateIndex extends CommandBase {

    private static final String[] COMMAND = new String[] { "create", null, "index" };
    private static final String[] IDXTYPE = new String[] { "value", "path", "fulltext" };
    private static final int VALUE_IDX = 0, PATH_IDX = 1, FULLTEXT_IDX = 2;
    private static final String PATTERN_CMD = "on", NAMESPACE_CMD = "nsmap", DATATYPE_CMD = "as";

    public CreateIndex(Session session) {
        super(session);
    }

    public boolean match(String[] args) {
        if(args.length < COMMAND.length) {
            return false;
        }
        if(!(COMMAND[0].equals(args[0]) & COMMAND[2].equals(args[2]))) {
            return false;
        }
        if(!ArrayUtils.contains(IDXTYPE, args[1])) {
            return false;
        }
        return true;
    }

    public boolean process(String[] args) throws CommandFailedException {
        final DbCollection col = session.getContextCollection();
        if(col == null) {
            throwException("Context collection is not set");
        }
        final int type = ArrayUtils.indexOf(IDXTYPE, args[1]);
        assert (type != ArrayUtils.INDEX_NOT_FOUND);
        final boolean status;
        switch(type) {
            case PATH_IDX: {
                final String[] result = parsePattern(args, PATTERN_CMD, NAMESPACE_CMD);
                status = createPathIndex(col, result[0], result[1]);
                break;
            }
            case VALUE_IDX: {
                final String[] result = parsePattern(args, DATATYPE_CMD, PATTERN_CMD, NAMESPACE_CMD);
                status = createValueIndex(col, result[0], result[1], result[2]);
                break;
            }
            case FULLTEXT_IDX: {
                final String[] result = parsePattern(args, PATTERN_CMD, NAMESPACE_CMD);
                status = createFullTextIndex(col, result[0], result[1]);
                break;
            }
            default:
                throw new IllegalStateException("Illegal type: " + type);
        }
        return status;
    }

    private static boolean createPathIndex(final DbCollection col, final String pattern, final String nsmap) {

        return true;
    }

    private static boolean createValueIndex(final DbCollection col, final String dataType, final String pattern, final String nsmap) {

        return true;
    }

    private static boolean createFullTextIndex(final DbCollection col, final String pattern, final String nsmap) {

        return true;
    }

    private static NamespaceContext toNamespaceContexts(final String nsmap) {
        final NamespaceBinder namespaces = new NamespaceBinder();
        if(nsmap != null) {
            final String[] entries = nsmap.split(",");
            for(String entry : entries) {
                String[] items = entry.split("=");
                if(items.length != 2) {
                    throw new IllegalArgumentException("Illegal nsmap description: " + nsmap);
                }
                namespaces.declarePrefix(items[0], items[1]);
            }
        }
        return namespaces;
    }

    private static String[] parsePattern(final String[] args, final String... pattern) {
        final int ptnlen = pattern.length;
        final String[] result = new String[ptnlen];
        int ri = 0;
        final int arglen = args.length;
        for(int i = COMMAND.length; i + 1 < arglen; i += 2) {
            final String s = args[i];
            final int pi = ArrayUtils.indexOf(pattern, s, ri);
            if(pi == ArrayUtils.INDEX_NOT_FOUND) {
                continue;
            }
            result[pi] = args[i + 1];
            ri = pi + 1;
            if(ri >= ptnlen) {
                break;
            }
        }
        return result;
    }

    public String usage() {
        return constructHelp("Create an index for the specified collection", "create 'indexType' index [as 'dataType'] [on 'pathExp'] [nsmap 'prefix=namespaceUri(,prefix=namespaceUri)*']");
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/,=http://naist.jp".split(" "), DATATYPE_CMD, PATTERN_CMD, NAMESPACE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), DATATYPE_CMD, PATTERN_CMD, NAMESPACE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), DATATYPE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), PATTERN_CMD)));
        System.out.println(Arrays.toString(parsePattern("create path index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), PATTERN_CMD, NAMESPACE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), NAMESPACE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index on /aaa/bbb/ccc nsmap xbird=http://xbird.org/".split(" "), PATTERN_CMD, NAMESPACE_CMD)));
        System.out.println(Arrays.toString(parsePattern("create value index nsmap xbird=http://xbird.org/".split(" "), PATTERN_CMD, NAMESPACE_CMD)));
        System.out.println(Arrays.toString("=aaaa".split("=")));
    }

}
