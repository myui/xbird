/*
 * @(#)$Id$
 *
 * Copyright 2008 Makoto YUI
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
 *     Makoto YUI - reversely generated from XQJ javadoc using codavaj
 */
package javax.xml.xquery;

/**
 * This interface represents an item in the XDM.
 */
public interface XQItem extends XQItemAccessor {
    /**
     * Close the item and release all the resources associated with this item.
     * No method other than the isClosed or close method may be called once the
     * item is closed. Calling close on an XQItem object that is already closed
     * has no effect.
     */
    void close() throws XQException;

    /**
     * Checks if the item is closed.
     */
    boolean isClosed();

}
