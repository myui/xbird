/*
 * @(#)$Id$
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
package xbird.engine.request;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import xbird.engine.Request;
import xbird.xquery.ext.grid.QueryTask;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GridQueryTaskRequest extends Request {

    private QueryTask queryTask;

    public GridQueryTaskRequest() {}// for Externalizable

    public GridQueryTaskRequest(QueryTask queryTask) {
        super(ReturnType.AUTO);
        this.queryTask = queryTask;
    }

    @Override
    public Signature getSignature() {
        return Signature.GRID_QTASK;
    }

    public QueryTask getTask() {
        return queryTask;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.queryTask = (QueryTask) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(queryTask);
    }
}
