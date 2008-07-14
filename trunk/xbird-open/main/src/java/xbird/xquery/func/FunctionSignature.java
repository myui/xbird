/*
 * @(#)$Id: FunctionSignature.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func;

import java.io.*;

import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FunctionSignature implements Externalizable {
    private static final long serialVersionUID = 6978276272237889970L;
    private static final Type[] EMPTY_TYPES = new Type[] {};

    private/* final */QualifiedName _name;
    private/* final */Type[] _argumentTypes;
    private/* final */int _arity;

    public FunctionSignature(QualifiedName name) {
        this(name, EMPTY_TYPES);
    }

    public FunctionSignature(QualifiedName name, Type[] argumentTypes) {
        if(name == null) {
            throw new IllegalArgumentException();
        }
        this._name = name;
        this._argumentTypes = argumentTypes;
        this._arity = argumentTypes.length;
    }

    public FunctionSignature(QualifiedName name, int arity) {
        if(name == null) {
            throw new IllegalArgumentException();
        }
        this._name = name;
        this._arity = arity;
        this._argumentTypes = null;
    }

    public FunctionSignature() {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._name = QualifiedName.readFrom(in);
        final int arglen = in.readInt();
        if(arglen == -1) {
            this._argumentTypes = null;
        } else {
            final Type[] argumentTypes = new Type[arglen];
            for(int i = 0; i < arglen; i++) {
                Type t = (Type) in.readObject();
                argumentTypes[i] = t;
            }
        }
        this._arity = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        _name.writeExternal(out);
        final Type[] argumentTypes = _argumentTypes;
        if(argumentTypes == null) {
            out.writeInt(-1);
        } else {
            final int arglen = argumentTypes.length;
            out.writeInt(arglen);
            for(int i = 0; i < arglen; i++) {
                out.writeObject(argumentTypes[i]);
            }
        }
        out.writeInt(_arity);
    }

    public static FunctionSignature readFrom(ObjectInput in) throws IOException,
            ClassNotFoundException {
        final FunctionSignature sign = new FunctionSignature();
        sign.readExternal(in);
        return sign;
    }

    public QualifiedName getName() {
        return _name;
    }

    public Type[] getArgumentTypes() {
        return _argumentTypes;
    }

    public Type getArgumentType(int index) {
        if(index < 0 || index > _argumentTypes.length) {
            throw new IndexOutOfBoundsException("Invalid index for " + getName() + ".. " + _arity);
        }
        return _argumentTypes[index];
    }

    public int getArity() {
        return _arity;
    }

    public boolean equals(Object arg) {
        if(arg instanceof FunctionSignature) {
            return equals((FunctionSignature) arg);
        } else {
            return false;
        }
    }

    public boolean equals(FunctionSignature arg) {
        return this._arity == arg._arity && this._name.equals(arg._name);
    }

    public int hashCode() {
        return _name.hashCode() + _arity;
    }

}