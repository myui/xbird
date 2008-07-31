/*
 * @(#)$Id: DynamicError.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DynamicError extends XQueryException {
    private static final long serialVersionUID = 9092059221835493454L;

    public DynamicError(String errCode, String msg, Throwable cause) {
        super(errCode, msg, cause);
    }

    public DynamicError(String errCode, String msg) {
        super(errCode, msg);
    }

    public DynamicError(String errCode, Throwable cause) {
        super(errCode, cause);
    }

    public DynamicError(String errCode) {
        super(errCode);
    }

    public DynamicError(XQRTException cause) {
        super(cause);
    }

}
