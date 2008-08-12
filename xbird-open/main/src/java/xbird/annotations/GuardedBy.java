/*
 * @(#)$Id$
 *
 * Copyright (c) 2005 Brian Goetz and Tim Peierls
 * Released under the Creative Commons Attribution License
 *   (http://creativecommons.org/licenses/by/2.5)
 * Official home: http://www.jcip.net
 *
 * Any republication or derived work distributed in source code form
 * must include this copyright and license notice.
 */
package xbird.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <DIV lang="en">
 * Adopted from Java Concurrency in Practice, and changed the retention policy to SOURCE.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.jcip.net
 */
@Documented
@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface GuardedBy {
    String value();
}
