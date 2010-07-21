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
package xbird.util.xfer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xbird.util.io.FastByteArrayOutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RunnableFileSender implements Runnable {

    @Nullable
    private final File file;
    private/* final */long pos;
    private/* final */long count;
    @Nullable
    private final FastByteArrayOutputStream fileData;
    @Nullable
    private final String fileName;

    @Nullable
    private final String writeDirPath;
    @Nonnull
    private final InetAddress dstAddr;
    private final int dstPort;
    private final boolean append;
    private final boolean sync;

    @Nullable
    private TransferClientHandler handler = null;

    public RunnableFileSender(@Nonnull File file, @Nullable String writeDirPath, @Nonnull InetAddress dstAddr, int dstPort, boolean append, boolean sync) {
        this(file, 0L, -1L, writeDirPath, dstAddr, dstPort, append, sync);
    }

    public RunnableFileSender(@Nonnull File file, long pos, long count, @Nullable String writeDirPath, @Nonnull InetAddress dstAddr, int dstPort, boolean append, boolean sync) {
        this.file = file;
        this.pos = pos;
        this.count = count;
        this.fileData = null;
        this.fileName = null;
        this.writeDirPath = writeDirPath;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
        this.append = append;
        this.sync = sync;
    }

    public RunnableFileSender(@Nonnull FastByteArrayOutputStream fileData, @Nonnull String fileName, @Nullable String writeDirPath, @Nonnull InetAddress dstAddr, int dstPort, boolean append, boolean sync) {
        this.file = null;
        this.fileData = fileData;
        this.fileName = fileName;
        this.writeDirPath = writeDirPath;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
        this.append = append;
        this.sync = sync;
    }

    public void setTransferHandler(TransferClientHandler handler) {
        this.handler = handler;
    }

    public void run() {
        if(file == null) {
            try {
                TransferUtils.send(fileData, fileName, writeDirPath, dstAddr, dstPort, append, sync, handler);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to send file data '" + fileName + "' to "
                        + dstAddr + ':' + dstPort, e);
            }
        } else {
            try {
                TransferUtils.sendfile(file, pos, count, null, dstAddr, dstPort, append, sync, handler);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to send file '" + file.getAbsolutePath()
                        + "' to " + dstAddr + ':' + dstPort, e);
            }
        }
    }

}
