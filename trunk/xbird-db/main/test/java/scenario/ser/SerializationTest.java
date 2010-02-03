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
package scenario.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.junit.Assert;

import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.util.io.NoHeaderObjectOutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SerializationTest extends TestCase {

    public void test1() throws IOException, ClassNotFoundException {
        FastByteArrayOutputStream f = new FastByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(f);
        Student student1 = new Student("Chris", "Giroir", "111111", "elkfjwe", "wifjew");
        out.writeObject(student1);
        out.writeInt(5);
        out.close();

        out = new NoHeaderObjectOutputStream(f);
        Student student2 = new Student("Chris2", "Giroir2", "elkfjwe", "111111");
        out.writeObject(student2);
        out.close();

        final byte[] b = f.toByteArray();

        FastByteArrayInputStream in = new FastByteArrayInputStream(b);
        ObjectInputStream oin = new ObjectInputStream(in);

        Assert.assertEquals(student1, oin.readObject());
        Assert.assertEquals(5, oin.readInt());
        Assert.assertEquals(student2, oin.readObject());

        oin.close();
    }

    public void test2() throws IOException, ClassNotFoundException {
        FastByteArrayOutputStream f = new FastByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(f);
        Student student1 = new Student("Chris", "Giroir", "111111", "elkfjwe", "wifjew");
        out.writeObject(student1);
        out.writeInt(5);

        FastByteArrayOutputStream out2 = new FastByteArrayOutputStream();
        ObjectOutputStream oos2 = new ObjectOutputStream(out2);
        Student student2 = new Student("Chris2", "Giroir2", "elkfjwe", "111111");
        Student student3 = new Student("dsfsfsd", "sasa", "DSsd", "sds");
        oos2.writeObject(student2);
        oos2.writeObject(student3);

        byte[] b2 = out2.toByteArray();
        out.writeInt(b2.length);
        out.write(b2);

        FastByteArrayInputStream bis3 = new FastByteArrayInputStream(b2);
        ObjectInputStream ois3 = new ObjectInputStream(bis3);
        Assert.assertEquals(student2, ois3.readObject());
        Assert.assertEquals(student3, ois3.readObject());

        out.close();

        final byte[] b = f.toByteArray();
        FastByteArrayInputStream in = new FastByteArrayInputStream(b);
        ObjectInputStream oin = new ObjectInputStream(in);

        Assert.assertEquals(student1, oin.readObject());
        Assert.assertEquals(5, oin.readInt());

        int size3 = oin.readInt();
        byte[] b3 = new byte[size3];
        oin.read(b3);
        ArrayAssert.assertEquals(b2, b3);

        FastByteArrayInputStream bis4 = new FastByteArrayInputStream(b3);
        ObjectInputStream ois4 = new ObjectInputStream(bis4);
        Assert.assertEquals(student2, ois4.readObject());
        Assert.assertEquals(student3, ois4.readObject());

        oin.close();
    }

    public void testPlaceHolder1() throws IOException, ClassNotFoundException {
        FastByteArrayOutputStream f = new FastByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(f);
        PlaceHolder holder1 = new PlaceHolder();
        out.writeObject(holder1);
        out.flush();

        FastByteArrayInputStream bis = new FastByteArrayInputStream(f.getInternalArray(), 0, f.size());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Assert.assertEquals(holder1, ois.readObject());
    }

    private static class PlaceHolder implements Serializable {
        private static final long serialVersionUID = -5540955490390152329L;

        Student student1;

        transient Student student2;
        transient Student student3;
        transient byte[] ser23;

        PlaceHolder() throws IOException {
            this.student1 = new Student("Chris", "Giroir", "111111", "elkfjwe", "wifjew");
            this.ser23 = ser();
        }

        private byte[] ser() throws IOException {
            student2 = new Student("Chris2", "Giroir2", "elkfjwe", "111111");
            student3 = new Student("dsfsfsd", "sasa", "DSsd", "sds");
            FastByteArrayOutputStream out2 = new FastByteArrayOutputStream();
            ObjectOutputStream oos2 = new ObjectOutputStream(out2);
            oos2.writeObject(student2);
            oos2.writeObject(student3);
            return out2.toByteArray();
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            // Write out and any hidden stuff
            s.defaultWriteObject();

            s.writeInt(ser23.length);
            s.write(ser23);
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            // Read in any hidden stuff
            s.defaultReadObject();

            int size = s.readInt();
            byte[] b = new byte[size];
            s.readFully(b, 0, size);
            this.ser23 = b;

            FastByteArrayInputStream bis3 = new FastByteArrayInputStream(b);
            ObjectInputStream ois3 = new ObjectInputStream(bis3);
            this.student2 = (Student) ois3.readObject();
            this.student3 = (Student) ois3.readObject();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof PlaceHolder) {
                PlaceHolder other = (PlaceHolder) obj;
                if(!student1.equals(other.student1)) {
                    return false;
                }
                if(!student2.equals(other.student2)) {
                    return false;
                }
                if(!student3.equals(other.student3)) {
                    return false;
                }
                return Arrays.equals(ser23, other.ser23);
            }
            return false;
        }

    }

    private static class Student implements Serializable {
        final String[] names;

        Student(String... names) {
            this.names = names;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Student) {
                Student other = (Student) obj;
                return Arrays.equals(names, other.names);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(names);
        }

    }
}
