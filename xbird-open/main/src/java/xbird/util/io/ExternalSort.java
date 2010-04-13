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
package xbird.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xbird.util.codec.VariableByteCodec;
import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ExternalSort {

    @Nonnull
    private final RecordReader reader;
    @Nonnull
    private final RecordWriter writer;
    @Nonnull
    private final Comparator<byte[]> comparator;

    private int inputFileBufSize = 8192;
    private int outputBufSize = 4 * 1024 * 1024;
    private boolean deleteInputFiles = true;
    private int maxMergeOnce = -1;

    public ExternalSort(Comparator<byte[]> comparator) {
        this.reader = new DefaultRecordReader();
        this.writer = new DefaultRecordWriter();
        this.comparator = comparator;
    }

    public ExternalSort(@Nonnull RecordReader reader, @Nonnull RecordWriter writer, @Nonnull Comparator<byte[]> comparator) {
        this.reader = reader;
        this.writer = writer;
        this.comparator = comparator;
    }

    public void setInputFileBufSize(int inputFileBufSize) {
        this.inputFileBufSize = inputFileBufSize;
    }

    public void setOutputBufSize(int outputBufSize) {
        this.outputBufSize = outputBufSize;
    }

    public void setDeleteInputFiles(boolean deleteInputFiles) {
        this.deleteInputFiles = deleteInputFiles;
    }

    public void setMaxMergeOnce(int maxMergeOnce) {
        if(maxMergeOnce < 2 && maxMergeOnce != -1) {
            throw new IllegalArgumentException("greather than or equals to 2 or -1 is allowed: "
                    + maxMergeOnce);
        }
        this.maxMergeOnce = maxMergeOnce;
    }

    public void merge(final File inputDir, final File outputFile) throws IOException {
        if(maxMergeOnce == -1) {
            mergeAllAtOnce(inputDir, inputFileBufSize, outputFile, outputBufSize, reader, writer, comparator, deleteInputFiles);
        } else {
            mergeAll(inputDir, inputFileBufSize, outputFile, outputBufSize, reader, writer, comparator, deleteInputFiles, maxMergeOnce);
        }
    }

    public static void mergeAll(final File inputDir, final int inputFileBufSize, final File outputFile, final int outputBufSize, final RecordReader recReader, final RecordWriter recWriter, final Comparator<byte[]> recComparator, boolean deleteInputFiles, final int maxMergeOnce)
            throws IOException {
        if(!inputDir.isDirectory()) {
            throw new IllegalArgumentException("inputDir is not directory: "
                    + inputDir.getAbsolutePath());
        }
        boolean deleteInputs = deleteInputFiles;
        List<File> fileList = FileUtils.listFiles(inputDir, false);
        while(true) {
            final int totalFiles = fileList.size();
            if(totalFiles <= maxMergeOnce) {
                File[] files = ArrayUtils.toArray(fileList, File[].class);
                mergeAllAtOnce(files, inputFileBufSize, outputFile, outputBufSize, recReader, recWriter, recComparator, deleteInputs);
                return;
            }
            final File[] files = new File[maxMergeOnce];
            int from = 0;
            boolean loop = true;
            while(loop) {
                final int to;
                if(from < totalFiles) {
                    to = from + maxMergeOnce;
                } else {
                    to = totalFiles - 1;
                    loop = false;
                }
                fileList = fileList.subList(from, to);
                fileList.toArray(files);
                File tmpOutFile = File.createTempFile(outputFile.getName(), ".sorted", inputDir);
                mergeAllAtOnce(files, inputFileBufSize, tmpOutFile, outputBufSize, recReader, recWriter, recComparator, deleteInputs);
            }
            deleteInputs = true;
            fileList = FileUtils.listFiles(inputDir, ".sorted", false);
        }
    }

    public static void mergeAllAtOnce(final File inputDir, final int inputFileBufSize, final File outputFile, final int outputBufSize, final RecordReader recReader, final RecordWriter recWriter, final Comparator<byte[]> recComparator, final boolean deleteInputFiles)
            throws IOException {
        if(!inputDir.isDirectory()) {
            throw new IllegalArgumentException("inputDir is not directory: "
                    + inputDir.getAbsolutePath());
        }
        List<File> files = FileUtils.listFiles(inputDir, false);
        File[] filesAry = ArrayUtils.toArray(files, File[].class);
        mergeAllAtOnce(filesAry, inputFileBufSize, outputFile, outputBufSize, recReader, recWriter, recComparator, deleteInputFiles);
    }

    public static void mergeAllAtOnce(final File[] sortedFiles, final int inputFileBufSize, final File outputFile, final int outputBufSize, final RecordReader recReader, final RecordWriter recWriter, final Comparator<byte[]> recComparator, final boolean deleteInputFiles)
            throws IOException {
        if(sortedFiles.length == 0) {
            throw new IllegalArgumentException("No files to be sorted");
        }

        FileOutputStream fos = new FileOutputStream(outputFile);
        final FastBufferedOutputStream out = new FastBufferedOutputStream(fos, outputBufSize);

        final int numFiles = sortedFiles.length;
        final InputStream[] in = new InputStream[numFiles];
        for(int i = 0; i < numFiles; i++) {
            File file = sortedFiles[i];
            if(file == null) {
                in[i] = null;
            } else {
                FileInputStream fis = new FileInputStream(file);
                FastBufferedInputStream bis = new FastBufferedInputStream(fis, inputFileBufSize);
                in[i] = bis;
            }
        }

        final byte[][] records = new byte[numFiles][];
        byte[] minRecord = null;
        while(true) {
            for(int i = 0; i < numFiles; i++) {
                final InputStream is = in[i];
                if(records[i] == null && is != null) {
                    final byte[] rec = recReader.readRecord(is);
                    if(rec == null) {
                        IOUtils.closeQuietly(is);
                        in[i] = null;
                        if(deleteInputFiles) {
                            if(!sortedFiles[i].delete()) {
                                throw new IllegalArgumentException("Cannot delete file: "
                                        + sortedFiles[i].getAbsolutePath());
                            }
                        }
                        continue;
                    }
                    if(minRecord == null) {
                        minRecord = rec;
                    } else if(recComparator.compare(rec, minRecord) < 0) {
                        minRecord = rec;
                    } else {
                        records[i] = rec;
                    }
                }
            }
            if(minRecord == null) {
                break;
            }
            recWriter.writeRecord(minRecord, out);
        }
        out.flush();
        out.close();
    }

    public interface RecordReader {

        @Nullable
        byte[] readRecord(InputStream in) throws IOException;

    }

    public interface RecordWriter {

        void writeRecord(@Nonnull byte[] record, @Nonnull OutputStream out) throws IOException;

    }

    public static final class DefaultRecordReader implements RecordReader {

        public DefaultRecordReader() {}

        public byte[] readRecord(InputStream in) throws IOException {
            int size = VariableByteCodec.decodeInt(in);
            byte[] b = new byte[size];
            IOUtils.readFully(in, b);
            return b;
        }
    }

    public static final class DefaultRecordWriter implements RecordWriter {

        public DefaultRecordWriter() {}

        public void writeRecord(byte[] record, OutputStream out) throws IOException {
            VariableByteCodec.encodeInt(record.length, out);
            out.write(record);
        }
    }

    public static final class DefaultRecordComparator implements Comparator<byte[]> {

        final int numFields;
        final FastByteArrayInputStream lbuf;
        final FastByteArrayInputStream rbuf;

        public DefaultRecordComparator(int numFields) {
            this.numFields = numFields;
            this.lbuf = new FastByteArrayInputStream();
            this.rbuf = new FastByteArrayInputStream();
        }

        public int compare(final byte[] lhs, final byte[] rhs) {
            lbuf.init(lhs);
            rbuf.init(rhs);

            for(int i = 0; i < numFields; i++) {
                int lsize = VariableByteCodec.decodeInt(lbuf);
                byte[] lb = new byte[lsize];
                lbuf.read(lb, 0, lsize);

                int rsize = VariableByteCodec.decodeInt(rbuf);
                byte[] rb = new byte[rsize];
                rbuf.read(rb, 0, rsize);

                final int cmp = ArrayUtils.compareTo(lb, rb);
                if(cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }

    }

}
