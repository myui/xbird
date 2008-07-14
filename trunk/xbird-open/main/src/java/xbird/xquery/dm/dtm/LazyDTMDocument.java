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
package xbird.xquery.dm.dtm;

import java.io.IOException;

import xbird.storage.DbCollection;
import xbird.util.io.IOUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.Item;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.Profiler;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LazyDTMDocument extends DTMDocument {
    private static final long serialVersionUID = 4796528791022117887L;

    private final String _fileName;
    private/* final */DbCollection _col;
    private final DynamicContext _dynEnv;

    public LazyDTMDocument() {// for serialization
        this._fileName = null;
        this._col = null;
        this._dynEnv = null;
    }

    public LazyDTMDocument(final String fileName, final DbCollection col, final DynamicContext dynEnv) {
        if(fileName == null) {
            throw new IllegalArgumentException();
        }
        if(col == null) {
            throw new IllegalArgumentException();
        }
        this._id = 0;
        this._fileName = fileName;
        this._col = col;
        this._dynEnv = dynEnv;
    }

    @Override
    public IDocumentTable documentTable() {
        return getDataModel().getDocumentTable();
    }

    @Override
    public DocumentTableModel getDataModel() {
        return preload();
    }

    /**
     * @see DocumentTableLoader#load(DbCollection, String, DynamicContext)
     */
    public void reclaim() {
        this._model = null;
        IOUtils.closeQuietly(_store);
        this._store = null;
    }

    @Override
    public Focus<Item> iterator() {
        preload();
        return new Focus<Item>(this, _dynEnv);
    }

    public DocumentTableModel preload() {
        if(_model == null) {
            final IDocumentTable doctbl;
            try {
                doctbl = DocumentTableLoader.load(_col, _fileName, _dynEnv);
            } catch (IOException e) {
                throw new XQRTException("failed loading document: " + _fileName, e);
            }
            Profiler profiler = _dynEnv.getProfiler();
            if(profiler != null) {
                PagingProfile profile = doctbl.getPagingProfile();
                if(profile != null) {
                    profile.setProfiler(profiler);
                }
            }
            DocumentTableModel model = new DocumentTableModel(doctbl, true);
            setInternalTable(model);
            this._model = model;
            return model;
        } else {
            return _model;
        }
    }
}
