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
 * This interface represents a sequence of items obtained as a result of
 * evaluation XQuery expressions. The result sequence is tied to the
 * XQconnection object on which the expression was evaluated. This sequence can
 * be obtained by performing an executeQuery on the expression object. It
 * represents a cursor-like interface. The XQResultSequence object is dependent
 * on the connection and the expression from which it was created and is only
 * valid for the duration of those objects. Thus, if any one of those objects is
 * closed, this XQResultSequence object will be implicitly closed and it can no
 * longer be used. Similarly re-executing the expression also implicitly closes
 * the associated result sequences. An XQJ driver is not required to provide
 * finalizer methods for the connection and other objects. Hence it is strongly
 * recommended that users call close method explicitly to free any resources. It
 * is also recommended that they do so under a final block to ensure that the
 * object is closed even when there are exceptions. Not closing this object
 * implicitly or explicitly might result in serious memory leaks. When the
 * XQResultSequence is closed any XQResultItem objects obtained from it are also
 * implicitly closed. Example - XQPreparedExpression expr =
 * conn.prepareExpression("for $i .."); XQResultSequence result =
 * expr.executeQuery(); // create the ItemTypes for string and integer
 * XQItemType strType = conn.createAtomicType(XQItemType.XQBASETYPE_STRING);
 * XQItemType intType = conn.createAtomicType(XQItemType.XQBASETYPE_INT); //
 * posititioned before the first item while (result.next()) { XQItemType type =
 * result.getItemType(); // If string, then get the string value out if
 * (type.equals(strType)) String str = result.getAtomicValue(); else if
 * (type.equals(intType)) // if it is an integer.. int intval = result.getInt();
 * ... } result.close(); // explicitly close the result sequence
 */
public interface XQResultSequence extends XQSequence {
    /**
     * Gets the XQuery connection associated with this result sequence
     */
    XQConnection getConnection() throws XQException;

}
