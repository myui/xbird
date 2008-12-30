/*
 * @(#)$Id: DMProcessingInstruction.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.xml.XMLUtils;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#ProcessingInstructionNode
 */
public class DMProcessingInstruction extends DMNode {
    private static final long serialVersionUID = 6723015424684439431L;

    private final String _target;

    public DMProcessingInstruction() {
        super();
        this._target = null;
    }

    public DMProcessingInstruction(String target, String content) {
        super();
        this._target = target;
        setContent(content);
    }

    protected DMProcessingInstruction(int id, String target, String content) {
        super(id);
        this._target = target;
        setContent(content);
    }

    public String getTarget() {
        return _target;
    }

    //--------------------------------------------
    // DM interfaces

    public byte nodeKind() {
        return NodeKind.PROCESSING_INSTRUCTION;
    }

    @Override
    public QualifiedName nodeName() {
        return QNameTable.instantiate(XMLUtils.NULL_NS_URI, _target);
    }

}
