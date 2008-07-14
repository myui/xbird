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

import javax.xml.namespace.QName;

/**
 * XQConstants class provides constants that can be used in the XQJ API.
 */
public final class XQConstants {
    /**
     * The constant indicating the binding mode deferred, refer to
     * XQDynamicContext for more information.
     */
    public static final int BINDING_MODE_DEFERRED = 1;

    /**
     * The constant indicating the binding mode immediate, refer to
     * XQDynamicContext for more information.
     */
    public static final int BINDING_MODE_IMMEDIATE = 0;

    /**
     * The constant indicating the the boundary-space policy for expression
     * evaluation is to preserve white spaces
     */
    public static final int BOUNDARY_SPACE_PRESERVE = 1;

    /**
     * The constant indicating the the boundary-space policy for expression
     * evaluation is to strip white spaces
     */
    public static final int BOUNDARY_SPACE_STRIP = 2;

    /**
     * The constant indicating that the type of a constructed element node is
     * xs:anyType, and all attribute and element nodes copied during node
     * construction retain their original types.
     */
    public static final int CONSTRUCTION_MODE_PRESERVE = 1;

    /**
     * The constant indicating that the type of a constructed element node is
     * xs:untyped; all element nodes copied during node construction receive the
     * type xs:untyped, and all attribute nodes copied during node construction
     * receive the type xs:untypedAtomic.
     */
    public static final int CONSTRUCTION_MODE_STRIP = 2;

    /**
     * Defines the QName for the context item. This is used to bind values to
     * the context item via the bind methods of XQDynamicContext.
     */
    public static final javax.xml.namespace.QName CONTEXT_ITEM = new QName("http://xqj.jcp.org", "context-item", "xqj");

    /**
     * The constant indicating that the inherit mode should be used in namespace
     * binding assignement when an existing element node is copied by an element
     * constructor, as described in
     */
    public static final int COPY_NAMESPACES_MODE_INHERIT = 1;

    /**
     * The constant indicating that the no-inherit mode should be used in
     * namespace binding assignement when an existing element node is copied by
     * an element constructor, as described in
     */
    public static final int COPY_NAMESPACES_MODE_NO_INHERIT = 2;

    /**
     * The constant indicating that the no-preserve mode should be used in
     * namespace binding assignement when an existing element node is copied by
     * an element constructor, as described in
     */
    public static final int COPY_NAMESPACES_MODE_NO_PRESERVE = 2;

    /**
     * The constant indicating that the preserve mode should be used in
     * namespace binding assignement when an existing element node is copied by
     * an element constructor, as described in
     */
    public static final int COPY_NAMESPACES_MODE_PRESERVE = 1;

    /**
     * The constant indicating that ordering of empty sequences and NaN values
     * as keys in an order by clause in a FLWOR expression is "greatest". See
     * for details.
     */
    public static final int DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_GREATEST = 1;

    /**
     * The constant indicating that ordering of empty sequences and NaN values
     * as keys in an order by clause in a FLWOR expression is "least". See for
     * details.
     */
    public static final int DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_LEAST = 2;

    /**
     * The constant indicating that the result sequences must be closed when the
     * commit on the connection is called.
     */
    public static final int HOLDTYPE_CLOSE_CURSORS_AT_COMMIT = 2;

    /**
     * The constant indicating that the result sequences must be preserved when
     * the commit on the connection is called.
     */
    public static final int HOLDTYPE_HOLD_CURSORS_OVER_COMMIT = 1;

    /**
     * The constant indicating that the expression language used in
     * XQConnection.prepareExpression and XQExpression.execute is XQuery (any
     * version).
     */
    public static final int LANGTYPE_XQUERY = 1;

    /**
     * The constant indicating that the expression language used in
     * XQConnection.prepareExpression and XQExpression.execute is XQueryX.
     */
    public static final int LANGTYPE_XQUERYX = 2;

    /**
     * The constant indicating that ordered results are to be returned by
     * certain path expressions, union, intersect, and except expressions, and
     * FLWOR expressions that have no order by clause.
     */
    public static final int ORDERING_MODE_ORDERED = 1;

    /**
     * The constant indicating that unordered results are to be returned by
     * certain path expressions, union, intersect, and except expressions, and
     * FLWOR expressions that have no order by clause.
     */
    public static final int ORDERING_MODE_UNORDERED = 2;

    /**
     * The constant indicating that the result sequence can only be scrolled
     * forward.
     */
    public static final int SCROLLTYPE_FORWARD_ONLY = 1;

    /**
     * The constant indicating that the result sequence can be scrolled forward
     * or backward and is insensitive to any updates done on the underlying
     * objects
     */
    public static final int SCROLLTYPE_SCROLLABLE = 2;

}
