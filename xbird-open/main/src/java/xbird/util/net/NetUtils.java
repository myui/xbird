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
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NetUtils {

    private static final String BIND_NIC;
    static {
        BIND_NIC = Settings.getThroughSystemProperty("xbird.net.bind_interface");
    }

    private NetUtils() {}

    /**
     * Get local address without loopback address.
     * 
     * @throws 
     */
    public static InetAddress getLocalHost() {
        final InetAddress addr = getLocalHost(false);
        if(addr == null) {
            throw new IllegalStateException("No valid IP address for this host found");
        }
        return addr;
    }

    /**
     * @return null if no valid address found.
     */
    public static InetAddress getLocalHost(boolean allowLoopbackAddr) {
        if(BIND_NIC != null) {
            final NetworkInterface nic;
            try {
                nic = NetworkInterface.getByName(BIND_NIC);
            } catch (SocketException e) {
                throw new IllegalStateException("Error while getting NetworkInterface: " + BIND_NIC, e);
            }
            if(nic == null) {
                final StringBuilder buf = new StringBuilder(128);
                buf.append("{ ");
                try {
                    Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
                    boolean hasMore = nics.hasMoreElements();
                    while(hasMore) {
                        NetworkInterface n = nics.nextElement();
                        String nicName = n.getName();
                        buf.append(nicName);
                        if((hasMore = nics.hasMoreElements()) == true) {
                            buf.append(',');
                        }
                    }
                } catch (SocketException se) {
                    ;
                }
                buf.append(" }");
                throw new IllegalArgumentException("NIC '" + BIND_NIC + "' not found in " + buf);
            }
            final Enumeration<InetAddress> nicAddrs = nic.getInetAddresses();
            while(nicAddrs.hasMoreElements()) {
                final InetAddress nicAddr = nicAddrs.nextElement();
                if(!nicAddr.isLoopbackAddress()/* && !nicAddr.isLinkLocalAddress() */) {
                    return nicAddr;
                }
            }
            return null;
        }
        InetAddress localHost = null;
        try {
            InetAddress probeAddr = InetAddress.getLocalHost();
            if(allowLoopbackAddr) {
                localHost = probeAddr;
            }
            if(probeAddr.isLoopbackAddress()/* || probeAddr.isLinkLocalAddress() */) {
                final Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
                nicLoop: while(nics.hasMoreElements()) {
                    NetworkInterface nic = nics.nextElement();
                    if(nic.isLoopback()) {
                        continue;
                    }
                    final Enumeration<InetAddress> nicAddrs = nic.getInetAddresses();
                    while(nicAddrs.hasMoreElements()) {
                        InetAddress nicAddr = nicAddrs.nextElement();
                        if(!nicAddr.isLoopbackAddress()/* && !nicAddr.isLinkLocalAddress() */) {
                            localHost = nicAddr;
                            if(nic.isVirtual()) {
                                continue nicLoop; // try to find IP-address of non-virtual NIC
                            } else {
                                break nicLoop;
                            }
                        }
                    }
                }
            } else {
                localHost = probeAddr;
            }
        } catch (UnknownHostException ue) {
            throw new IllegalStateException(ue);
        } catch (SocketException se) {
            throw new IllegalStateException(se);
        }
        return localHost;
    }

    public static String getLocalHostName() {
        return getLocalHost().getHostName();
    }

    public static String getLocalHostAddress() {
        return getLocalHost().getHostAddress();
    }

    /**
     * @link http://www.ietf.org/rfc/rfc2396.txt
     */
    public static String getLocalHostAddressAsUrlString() {
        final InetAddress addr = getLocalHost();
        final String hostaddr = addr.getHostAddress();
        if(isIpV6Address(addr)) {
            // hostaddr = hostaddr.replaceAll("%", "%25")
            String v6addr = '[' + hostaddr + ']';
            return v6addr;
        }
        return hostaddr;
    }

    public static boolean isIpV6Address(final InetAddress addr) {
        return addr instanceof Inet6Address;
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

    public static void shutdownAndCloseQuietly(final Socket socket) {
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            ;
        }
        try {
            socket.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void shutdownOutputQuietly(final Socket sock) {
        try {
            sock.shutdownOutput();
        } catch (IOException e) {
            ;
        }
    }
}
