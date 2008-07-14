/*
 * @(#)$Id: DMComment.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.node;

import xbird.xquery.dm.NodeKind;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#CommentNode
 */
public class DMComment extends DMNode {
    private static final long serialVersionUID = -3971811312599153844L;

    public DMComment() {//for serialization
        super();
    }
    
    public DMComment(String content) {
        super();
        setContent(content);
    }
    
    protected DMComment(int id, String content) {
        super(id);
        setContent(content);
    }

    //--------------------------------------------
    // DM interfaces
    
    public byte nodeKind() {
        return NodeKind.COMMENT;
    }

}
