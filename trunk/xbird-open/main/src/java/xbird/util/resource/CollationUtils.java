/*
 * @(#)$Id: CollationUtils.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.resource;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.*;
import java.util.Locale;

import xbird.util.io.IOUtils;
import xbird.xquery.XQueryConstants;
import xbird.xquery.meta.StaticContext;

/**
 * Collator construction utility.
 * <DIV lang="en">
 * <pre>
 * Resolving <code>Collator</code> strategy is as following:
 * 
 *  (1) If arg is null or "http://www.w3.org/2005/04/xpath-functions/collation/codepoint",
 *     assumed to use default collation, and return null.
 *  
 *  (2) If arg is start with "http:://xbird.metabrick.org/collation?",
 *     assumed to use using system collation.
 *  
 *  (3) Else, try to read arg URI and constructs <code>RuleBasedCollator</code>
 *     with the respond content as collation rule.
 *     
 *  (4) If fail to construct a collation in the step (1) to (3), return null. 
 * 
 * System collation form:   <i>SYSTEM_COLLATION?GETS_ARGS</i>
 *
 *  [1] <i>SYSTEM_COLLATION</i> = http:://xbird.metabrick.org/collation?
 *   
 *      - Precisely saying, the value defined in {@link CollationUtils#SYSTEM_COLLATION}.
 *
 *  [2] <i>GETS_ARGS</i> := <i>LOCALE</i>("#"<i>COLOPTS</i> )*
 *  
 *  [3] <i>LOCALE</i> := <i>LANG</i>"-"<i>COUNTRY</i>"-"<i>VARIANT</i>
 *  
 *      - LOCALE(LANG, COUNTRY, VARIANT) constructs <code>java.util.Locale</code>.
 *  
 *  [4] <i>COLOPTS</i> := <i>STLENGTH</i> | <i>DECOMPOSITION</i>
 *  
 *  [5] <i>STLENGTH</i> := "primary" | "secondary" | "tertiary"
 *  
 *  [6] <i>DECOMPOSITION</i> :=  "canonical" | "NFD" | "full" | "NFKD"
 *  
 *      - "canonical" and "NFD" means <a href="http://www.unicode.org/unicode/reports/tr15/#Canonical_Composition_Examples">CANONICAL_DECOMPOSITION</a>.
 *      - "full" and "NFKD" means <a href="http://www.unicode.org/unicode/reports/tr15/#Compatibility_Composition_Examples">FULL_DECOMPOSITION</a>.
 * 
 *  Cautions:
 *      - parameter is overrided within last specified.
 * </pre>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see Locale
 * @see Collator
 * @see RuleBasedCollator
 * @see XQueryConstants#UNICODE_CODEPOINT_COLLATION
 * @see #SYSTEM_COLLATION
 */
public final class CollationUtils {

    public static final String SYSTEM_COLLATION = "http:://xbird/collation?";

    private CollationUtils() {}

    public static Collator resolve(URI uri, StaticContext statEnv) {
        return resolve(uri.toString(), statEnv);
    }

    public static Collator resolve(String uri, StaticContext statEnv) {
        if(uri == null) {
            uri = statEnv.getDefaultCollation();
        }
        if(uri == null || uri.equals(XQueryConstants.UNICODE_CODEPOINT_COLLATION)) {
            return null;
        }
        int pos = uri.indexOf(SYSTEM_COLLATION);
        if(pos > 0) {
            String getsArg = uri.substring(SYSTEM_COLLATION.length());
            final int sharppos = getsArg.indexOf('#');
            String qloc = (sharppos > 0) ? getsArg.substring(0, sharppos - 1) : getsArg;
            String args[] = qloc.split("-");
            final Locale loc;
            final int loclen = args.length;
            switch(loclen) {
                case 1:
                    loc = new Locale(args[0]);
                    break;
                case 2:
                    loc = new Locale(args[0], args[1]);
                    break;
                case 3:
                    loc = new Locale(args[0], args[1], args[2]);
                    break;
                default:
                    throw new IllegalArgumentException("invalid collation uri: " + uri);
            }
            final Collator col = Collator.getInstance(loc);
            if(sharppos > 0) {
                if(getsArg.indexOf("#primary") > 0) {
                    col.setStrength(Collator.PRIMARY);
                } else if(getsArg.indexOf("#secondary") > 0) {
                    col.setStrength(Collator.SECONDARY);
                } else if(getsArg.indexOf("#tertiary") > 0) {
                    col.setStrength(Collator.TERTIARY);
                } else if(getsArg.indexOf("#identical") > 0) {
                    col.setStrength(Collator.IDENTICAL);
                } else if(getsArg.indexOf("#canonical") > 0 || getsArg.indexOf("#NFD") > 0) {
                    col.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
                } else if(getsArg.indexOf("#full") > 0 || getsArg.indexOf("#NFKD") > 0) {
                    col.setDecomposition(Collator.FULL_DECOMPOSITION);
                }
            }
            return col;
        } else {
            final Collator col;
            try {
                URI baseuri = statEnv.getBaseURI();
                final URI targetUri;
                if(baseuri == null) {
                    targetUri = URI.create(uri);
                } else {
                    targetUri = baseuri.resolve(uri);
                }
                URL targetUrl = targetUri.toURL();
                InputStream is = targetUrl.openStream();
                String rules = IOUtils.toString(is);
                col = new RuleBasedCollator(rules);
            } catch (Exception e) {
                return null;
            }
            return col;
        }
    }

    public static int indexOf(final String src, final String pattern, final RuleBasedCollator collator) {
        if(collator == null) {
            return src.indexOf(pattern);
        } else {
            return indexOf(src, pattern, collator, false);
        }
    }

    public static boolean endsWith(final String src, final String pattern, final RuleBasedCollator collator) {
        if(collator == null) {
            return src.endsWith(pattern);
        } else {
            return indexOf(src, pattern, collator, true) != -1;
        }
    }

    private static int indexOf(final String src, final String pattern, final RuleBasedCollator collator, boolean endsWith) {
        assert (collator != null);
        CollationElementIterator srcItor = collator.getCollationElementIterator(src);
        CollationElementIterator patternItor = collator.getCollationElementIterator(pattern);
        final int ptnlen = patternItor.getOffset();
        int seeked = 0;
        boolean more = true;
        int srccp, ptncp = -1;
        for(int i = 0; (srccp = srcItor.next()) != CollationElementIterator.NULLORDER; i++) {
            if(more) {
                ptncp = patternItor.next();
                if(ptncp == CollationElementIterator.NULLORDER) {
                    if(endsWith) {
                        if(srcItor.next() != CollationElementIterator.NULLORDER) {
                            return -1;
                        }
                    }
                    return i - 1;
                }
            }
            if(srccp != ptncp) {
                more = false;
                seeked = 0;
            } else {
                if(seeked == ptnlen) {
                    if(endsWith) {
                        if(srcItor.next() != CollationElementIterator.NULLORDER) {
                            return -1;
                        }
                    }
                    return i;
                }
                more = true;
                ++seeked;
            }
        }
        return -1;
    }

}
