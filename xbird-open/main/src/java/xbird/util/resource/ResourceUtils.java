/*
 * @(#)$Id: ResourceUtils.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.File;
import java.net.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ResourceUtils {

    private ResourceUtils() {}

    public static String toURIString(File file) {
        final URI uri = file.toURI();
        final boolean isfile = file.isFile();
        final String uristr = uri.toString();
        if (isfile) {
            return uristr;
        } else {
            return uristr.endsWith("/") ? uristr : uristr + '/';
        }
    }

    public static String toURLString(File file) {
        final URL url;
        try {
            url = file.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could't convert to URL: " + file.getName());
        }
        final boolean isfile = file.isFile();
        final String urlstr = url.toExternalForm();
        if (isfile) {
            return urlstr;
        } else {
            return urlstr.endsWith("/") ? urlstr : urlstr + '/';
        }
    }

    public static URI buildURI(String spec) {
        assert (spec != null);
        final boolean isFile = spec.startsWith("file:");
        if (isFile) {
            //final File file = new File(spec.substring(5));
            //return file.toURI();
            return URI.create(spec);
        } else {
            try {
                return new URI(spec);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Illegal URI form: " + spec, e);
            }
        }
    }

    public static URL buildURL(String spec) {
        assert (spec != null);
        final boolean isFile = spec.startsWith("file:");
        try {
            if (isFile) {
                final File file = new File(spec.substring(5));
                return adjustTrailingSlash(file.toURL(), file.isDirectory());
            }
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could't build URL: " + spec);
        }
    }

    private static URL adjustTrailingSlash(URL url, boolean trailingSlash)
            throws MalformedURLException {
        String file = url.getFile();
        if (trailingSlash == (file.endsWith("/"))) {
            return url;
        }
        file = trailingSlash ? file + "/" : file.substring(0, file.length() - 1);
        return new URL(url.getProtocol(), url.getHost(), file);
    }
}
