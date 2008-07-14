/*
 * @(#)$Id: XBirdError.java 3754 2008-04-14 23:48:21Z yui $
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
package xbird;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XBirdError extends Error {
    private static final long serialVersionUID = -6104820043956987537L;

    public XBirdError() {
        super();
    }

    public XBirdError(String message) {
        super(message);
    }

    public XBirdError(Throwable cause) {
        super(cause);
    }

    public XBirdError(String message, Throwable cause) {
        super(message, cause);
    }

}
