/*
 * @(#)$Id: XQueryException.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.misc.ErrorCodes;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQueryException extends Exception {
    private static final long serialVersionUID = 2883870112606336562L;

    protected String error;

    public XQueryException(String errCode) {
        super();
        this.error = errCode;
    }

    public XQueryException(String errCode, String msg) {
        super(msg);
        this.error = errCode;
    }

    public XQueryException(String errCode, Throwable cause) {
        super(cause);
        this.error = errCode;
    }

    public XQueryException(String errCode, String msg, Throwable cause) {
        super(msg, cause);
        this.error = errCode;
    }

    public XQueryException(XQRTException cause) {
        this(cause.getErrCode(), cause.getMessage(), cause);
    }

    @Override
    public String getMessage() {
        return ErrorCodes.getMessage(error) + ' ' + super.getMessage();
    }

    public String getErrCode() {
        return error;
    }

    public void setErrCode(String code) {
        this.error = code;
    }

}
