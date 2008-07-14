/*
 * @(#)$Id: Encodings.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link org.apache.xerces.util.EncodingMap
 */
public final class Encodings {

    private static final Log LOG = LogFactory.getLog(Encodings.class);

    private static final Properties JAVA2IANA = new Properties();

    static {
        try {
            JAVA2IANA.load(Encodings.class.getResourceAsStream("java2iana.properties"));
        } catch (IOException e) {
            throw new IllegalStateException("file `java2iana.properties` not find.", e);
        }
    }

    private Encodings() {}

    public static String getIANAEncoding(String javaEncoding) {
        return JAVA2IANA.getProperty(javaEncoding);
    }

    public static String getFileEncodingIANA() {
        String javaEnc = System.getProperty("file.encoding");
        assert (javaEnc != null);
        String ianaEnc = JAVA2IANA.getProperty(javaEnc);
        if(ianaEnc != null) {
            return ianaEnc;
        } else {
            if(!JAVA2IANA.containsValue(javaEnc)) {
                LOG.warn("IANA encoding is not found for `" + javaEnc
                        + "`, so use the system-default UTF-8 encoding");
                return "UTF-8";
            }
            return javaEnc;
        }
    }
}
