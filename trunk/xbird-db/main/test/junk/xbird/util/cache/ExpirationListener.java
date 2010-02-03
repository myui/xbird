/*
 * @(#)$Id$
 *
 * Copyright (c) 2005-2008 Makoto YUI and Project XBird
 * All rights reserved.
 * 
 * This file is part of XBird and is distributed under the terms of
 * the Common Public License v1.0.
 * 
 * Contributors:
 *     o The Apache MINA Project - initial implementation
 *     o Makoto YUI - ported from Apache MINA project
 */
/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package xbird.util.cache;

/**
 * A listener for expired object events.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 589932 $, $Date: 2007-10-30 10:50:39 +0900 (Tue, 30 Oct 2007) $
 * TODO Make this a inner interface of ExpiringMap
 */
public interface ExpirationListener<E> {
    void expired(E expiredObject);
}
