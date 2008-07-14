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

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TimeoutSocketProdiver {
    private static final boolean DISABLE_COSTOM_SERV_SOCKET = true; //TODO
    private static final int TIMEOUT = Integer.parseInt(Settings.get("xbird.remote.socketTimeout", "0"));

    // not intended to be singleton
    private static TimeoutClientSocketFactory _clientf;
    private static TimeoutServerSocketFactory _serverf;

    private TimeoutSocketProdiver() {}

    public static TimeoutClientSocketFactory createClientSocketFactory() {
        if(TIMEOUT == 0) {
            return null;
        }
        TimeoutClientSocketFactory cf = _clientf;
        if(cf == null) {
            cf = new TimeoutClientSocketFactory(TIMEOUT);
            _clientf = cf;
        }
        return cf;
    }

    public static TimeoutClientSocketFactory createClientSocketFactory(int timeout) {
        if(timeout == TIMEOUT) {
            if(timeout == 0) {
                return null;
            }
            return createClientSocketFactory();
        }
        return new TimeoutClientSocketFactory(timeout);
    }

    /**
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6332603
     */
    public static TimeoutServerSocketFactory createServerSocketFactory() {
        if(DISABLE_COSTOM_SERV_SOCKET) {
            return null; // workaround for the JDK bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6332603
        }
        TimeoutServerSocketFactory sf = _serverf;
        if(sf == null) {
            sf = new TimeoutServerSocketFactory(TIMEOUT);
            _serverf = sf;
        }
        return sf;
    }

    /**
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6332603
     */
    @Deprecated
    public static TimeoutServerSocketFactory createServerSocketFactory(int timeout) {
        if(timeout == TIMEOUT) {
            return createServerSocketFactory();
        }
        return new TimeoutServerSocketFactory(timeout);
    }
}
