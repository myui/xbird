/*
 * @(#)$Id: ServerConstants.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server;

import java.io.Serializable;

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ServerConstants implements Serializable {

    public static final String RMI_PROTOCOL_IIOP = "iiop";

    public static final String RMI_PROTOCOL_JRMP = "jrmp";

    public static final String RMI_PROTOCOL_JRMP_SSL = "jrmp-ssl";

    public static final String SERV_NAME_PREFIX = "xbird.rmi.serv.name";

    public static final String REPOS_NAME_PREFIX = "xbird.rmi.repository.name";

    public static final String QP_NAME_PREFIX = "xbird.rmi.qp.name";

    public static final String RMI_PROTOCOL_KEY = "xbird.rmi.protocol";

    public static final String RMI_PROTOCOL = Settings.get(RMI_PROTOCOL_KEY, RMI_PROTOCOL_JRMP);

    public static final String GLOBAL_REGISTORY = Settings.get("xbird.rmi.registry.global");

    private ServerConstants() {}

    public static enum ReturnType {
        Sequence, String, JoinTable, OutputStream, DeflaterOutputStream
    }

}
