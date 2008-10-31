/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NetUtils {

    private NetUtils() {}

    public static InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getLocalHostName() {
        return getLocalHost().getHostName();
    }

    public static String getLocalHostAddress() {
        return getLocalHost().getHostAddress();
    }

    public static String getHostNameWithoutDomain(final InetAddress addr) {
        final String hostName = addr.getHostName();
        final int pos = hostName.indexOf('.');
        if(pos == -1) {
            return hostName;
        } else {
            return hostName.substring(0, pos);
        }
    }

    public static int getAvailablePort() {
        try {
            ServerSocket s = new ServerSocket(0);
            s.setReuseAddress(true);
            s.close();
            return s.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to find an available port", e);
        }
    }

    public static int getAvialablePort(final int basePort) {
        if(basePort == 0) {
            return getAvailablePort();
        }
        if(basePort < 0 || basePort > 65535) {
            throw new IllegalArgumentException("Illegal port number: " + basePort);
        }
        for(int i = basePort; i <= 65535; i++) {
            if(isPortAvailable(i)) {
                return i;
            }
        }
        throw new NoSuchElementException("Could not find available port greater than or equals to "
                + basePort);
    }

    public static boolean isPortAvailable(final int port) {
        ServerSocket s = null;
        try {
            s = new ServerSocket(port);
            s.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }

    public static URI toURI(final URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void closeQuietly(final Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void closeQuietly(final SocketChannel channel) {
        final Socket socket = channel.socket();
        try {
            socket.close();
        } catch (IOException e) {
            ;
        }
        try {
            channel.close();
        } catch (IOException e) {
            ;
        }

    }
}
