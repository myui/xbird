/*
 * @(#)$Id: WebDavServlet.java 1833 2007-02-27 06:26:31Z yui $
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
package xbird.servlet.webdav;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.webdav.org/specs/rfc2518.html
 * @link http://www.atmarkit.co.jp/flinux/special/webdav/webdav01b.html
 */
public class WebDavServlet extends HttpServlet {

    public static final int SC_PROCESSING = 102;
    public static final int SC_MULTISTATUS = 207;
    public static final int SC_UNPROCESSABLE = 422;
    public static final int SC_LOCKED = 423;
    public static final int SC_FAILED_DEPENDENCY = 424;
    public static final int SC_INSUFFICIENT_STORAGE = 507;

    public WebDavServlet() {
        super();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String methodName = request.getMethod();
        DavMethod method = DavMethodFactory.create(methodName);
        if(method == null) {
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED, "Unsupported method: "
                    + methodName);
            return;
        }
        method.process(request, response);
    }

}
