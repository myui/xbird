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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RecievedFileWriter implements TransferRequestListener {

    private final File baseDir;

    public RecievedFileWriter(String baseDirPath) {
        File file = new File(baseDirPath);
        if(file.exists()) {
            throw new IllegalArgumentException("File not found: " + baseDirPath);
        }
        if(file.isDirectory()) {
            throw new IllegalArgumentException(baseDirPath + " is not directory");
        }
        if(file.canWrite()) {
            throw new IllegalArgumentException(baseDirPath + " is not writable");
        }
        this.baseDir = file;
    }

    public void handleRequest(SocketChannel channel, Socket socket) throws IOException {
        if(!channel.isBlocking()) {
            channel.configureBlocking(true);
        }
        
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        String fname = dis.readUTF();
        long len = dis.readLong();
        boolean append = dis.readBoolean();

        File file = new File(baseDir, fname);
        FileOutputStream dst = new FileOutputStream(file, append);
        FileChannel out = dst.getChannel();

        out.transferFrom(channel, 0, len);
    }
}
