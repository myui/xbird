/*
 * @(#)$Id: PipedDeepCopy.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;

/**
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects
 * in a memory efficient way. Objects are serialized in the calling thread and
 * de-serialized in another thread.
 * <DIV lang="en">
 * Error checking is fairly minimal in this implementation. If an object is
 * encountered that cannot be serialized (or that references an object
 * that cannot be serialized) an error is printed to System.err and
 * null is returned. Depending on your specific application, it might
 * make more sense to have copy(...) re-throw the exception.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://javatechniques.com/public/java/docs/basics/low-memory-deep-copy.html
 * @deprecated
 */
public final class PipedDeepCopy {

    /**
     * Flag object used internally to indicate that deserialization failed.
     */
    private static final Object ERROR = new Object();

    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     */
    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Make a connected pair of piped streams
            FastPipedInputStream in = new FastPipedInputStream();
            FastPipedOutputStream pos = new FastPipedOutputStream(in);

            // Make a deserializer thread (see inner class below)
            Deserializer des = new Deserializer(in);
            des.start();

            // Write the object to the pipe
            ObjectOutputStream out = new ObjectOutputStream(pos);
            out.writeObject(orig);

            // Wait for the object to be deserialized
            obj = des.getDeserializedObject();

            // See if something went wrong
            if(obj == ERROR) {
                obj = null;
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        return obj;
    }

    /**
     * Thread subclass that handles deserializing from a PipedInputStream.
     */
    private static final class Deserializer extends Thread {
        /**
         * Object that we are deserializing
         */
        private Object obj = null;

        /**
         * Lock that we block on while deserialization is happening
         */
        private Object lock = null;

        /**
         * InputStream that the object is deserialized from.
         */
        private FastPipedInputStream in = null;

        public Deserializer(FastPipedInputStream pin) throws IOException {
            this.lock = new Object();
            this.in = pin;
        }

        public void run() {
            Object o = null;
            try {
                ObjectInputStream oin = new ObjectInputStream(in);
                o = oin.readObject();
            } catch (IOException e) {
                // This should never happen. If it does we make sure
                // that a the object is set to a flag that indicates
                // deserialization was not possible.
                throw new IllegalStateException(e);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException(cnfe);
            }
            synchronized(lock) {
                if(o == null) {
                    obj = ERROR;
                } else {
                    obj = o;
                }
                lock.notifyAll();
            }
        }

        /**
         * Returns the deserialized object. This method will block until
         * the object is actually available.
         */
        public Object getDeserializedObject() {
            // Wait for the object to show up
            try {
                synchronized(lock) {
                    while(obj == null) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException ie) {
                // If we are interrupted we just return null
            }
            return obj;
        }
    }
}
