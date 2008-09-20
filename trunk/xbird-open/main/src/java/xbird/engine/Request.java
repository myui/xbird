/*
 * @(#)$Id: Request.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import xbird.util.lang.ObjectUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class Request implements Externalizable {

    public static final short NORMAL_PRIORITY = 0;
    public static final short MAX_PRIORITY = Short.MAX_VALUE;
    public static final short MIN_PRIORITY = Short.MIN_VALUE;
    /** wait() equals to wait(0) actually, so NO_TIMEOUT is 0. */
    public static final long NO_TIMEOUT = 0;

    /** implicit return type */
    protected/* final */ReturnType _retType;
    protected String _id;
    /** by the default, (synchronous) response */
    protected ReplyPattern _replyPattern = ReplyPattern.RESPONSE;
    protected ResultHandler _resultHandler = null;

    // auxiliary properties
    protected short _priority = NORMAL_PRIORITY;
    protected long _timeout = NO_TIMEOUT;

    // latency calculation stuff
    private long _invokedTimestamp = -1L;

    public Request() {//Externalizable
        this(ReturnType.AUTO);
    }

    protected Request(ReturnType retType) {
        if(retType == null) {
            throw new IllegalArgumentException();
        }
        this._retType = retType;
        this._id = getSignature() + "@" + UUID.randomUUID().toString();
    }

    protected Request(String id) {
        this(id, ReturnType.AUTO);
    }

    protected Request(String id, ReturnType retType) {
        if(id == null) {
            throw new IllegalArgumentException();
        }
        if(retType == null) {
            throw new IllegalArgumentException();
        }
        this._retType = retType;
        this._id = id;
    }

    public abstract Signature getSignature();

    public ReturnType getReturnType() {
        return _retType;
    }

    public ReplyPattern getReplyPattern() {
        return _replyPattern;
    }

    public void setReplyPattern(ReplyPattern pattern) {
        this._replyPattern = pattern;
    }

    public ResultHandler getResultHandler() {
        return _resultHandler;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this._resultHandler = resultHandler;
    }

    public String getIdentifier() {
        return _id;
    }

    public void setIdentifier(String id) {
        this._id = id;
    }

    public short getPriority() {
        return _priority;
    }

    public void setPriority(short priority) {
        this._priority = priority;
    }

    public long getTimeout() {
        return _timeout;
    }

    public void setTimeout(long timeout) {
        if(timeout < 0) {
            throw new IllegalArgumentException("Illegal timeout: " + timeout);
        }
        this._timeout = timeout;
    }

    public void setInvoked(long timestamp) {
        this._invokedTimestamp = timestamp;
    }

    public long getInvoked() {
        return _invokedTimestamp;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    // ---------------------------------------------------

    public enum Signature {

        COMMAND("Command"), QUERY("Query"), PREPARED_QUERY("PreparedQuery"), COMPILE("Compile"), GRID_QTASK(
                "GridQueryTask");

        final String name;

        Signature(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ReturnType {
        AUTO, STRING, BOOLEAN, /* not recommended */SEQUENCE, MARSHALLED_SEQUENCE, REMOTE_SEQUENCE, ASYNC_REMOTE_SEQUENCE, THROTTLED_ASYNC_REMOTE_SEQUENCE, REDIRECTABLE_MARSHALLED_SEQUENCE, REMOTE_PADED_SEQUENCE;

        public boolean isRemoteSequnece() {
            return this == ASYNC_REMOTE_SEQUENCE || this == REMOTE_SEQUENCE
                    || this == THROTTLED_ASYNC_REMOTE_SEQUENCE || this == REMOTE_PADED_SEQUENCE;
        }

        public static ReturnType readFrom(final ObjectInput in) throws IOException {
            final int type = in.readByte();
            switch(type) {
                case 0:
                    return AUTO;
                case 1:
                    return STRING;
                case 2:
                    return BOOLEAN;
                case 3:
                    return SEQUENCE;
                case 4:
                    return REMOTE_SEQUENCE;
                case 5:
                    return ASYNC_REMOTE_SEQUENCE;
                case 6:
                    return THROTTLED_ASYNC_REMOTE_SEQUENCE;
                case 7:
                    return MARSHALLED_SEQUENCE;
                case 8:
                    return REDIRECTABLE_MARSHALLED_SEQUENCE;
                case 9:
                    return REMOTE_PADED_SEQUENCE;
                default:
                    throw new IllegalStateException("Unexpected type: " + type);
            }
        }

        public void writeTo(final ObjectOutput out) throws IOException {
            switch(this) {
                case AUTO:
                    out.writeByte(0);
                    break;
                case STRING:
                    out.writeByte(1);
                    break;
                case BOOLEAN:
                    out.writeByte(2);
                    break;
                case SEQUENCE:
                    out.writeByte(3);
                    break;
                case REMOTE_SEQUENCE:
                    out.writeByte(4);
                    break;
                case ASYNC_REMOTE_SEQUENCE:
                    out.writeByte(5);
                    break;
                case THROTTLED_ASYNC_REMOTE_SEQUENCE:
                    out.writeByte(6);
                    break;
                case MARSHALLED_SEQUENCE:
                    out.writeByte(7);
                    break;
                case REDIRECTABLE_MARSHALLED_SEQUENCE:
                    out.writeByte(8);
                    break;
                case REMOTE_PADED_SEQUENCE:
                    out.writeByte(9);
                    break;
                default:
                    throw new IllegalStateException("Unexpected: " + this);
            }
        }
    }

    public static final ReturnType resolveReturnType(final String type) {
        if("AUTO".equalsIgnoreCase(type)) {
            return ReturnType.AUTO;
        } else if("THROTTLED_ASYNC_REMOTE_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.THROTTLED_ASYNC_REMOTE_SEQUENCE;
        } else if("MARSHALLED_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.MARSHALLED_SEQUENCE;
        } else if("REDIRECTABLE_MARSHALLED_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.REDIRECTABLE_MARSHALLED_SEQUENCE;
        } else if("REMOTE_PAGED_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.REMOTE_PADED_SEQUENCE;
        } else if("ASYNC_REMOTE_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.ASYNC_REMOTE_SEQUENCE;
        } else if("REMOTE_SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.REMOTE_SEQUENCE;
        } else if("SEQUENCE".equalsIgnoreCase(type)) {
            return ReturnType.SEQUENCE;
        } else if("STRING".equalsIgnoreCase(type)) {
            return ReturnType.STRING;
        } else if("BOOLEAN".equalsIgnoreCase(type)) {
            return ReturnType.BOOLEAN;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    /** 
     * The default reply pattern is (synchronous) Response.
     * (Asynchronous) Callback reply pattern is not recommended, since
     * a client takes an RMI-connection until callback is invocated
     * and callback does not work if client is behind a firewall (includes Windows Firewall).
     * Note that Poll reply pattern spends server's cache-resource until 
     * poll-request is invocated. Pending responses are flushed to disk 
     * when the cache is overflowed.
     * The cache size on main-memory depends on the ehcache configuration.
     * 
     * @link http://java.sun.com/developer/onlineTraining/rmi/RMI.html#FirewallIssues
     * @link http://java.sun.com/j2se/1.5.0/docs/guide/rmi/spec/rmi-arch6.html
     */
    public enum ReplyPattern {
        RESPONSE, CALLBACK, POLL;

        public static ReplyPattern readFrom(final ObjectInput in) throws IOException {
            final int ptn = in.readByte();
            switch(ptn) {
                case 1:
                    return RESPONSE;
                case 2:
                    return CALLBACK;
                case 3:
                    return POLL;
                default:
                    throw new IllegalStateException("Illegal pattern: " + ptn);
            }
        }

        public void writeTo(final ObjectOutput out) throws IOException {
            switch(this) {
                case RESPONSE:
                    out.writeByte(1);
                    break;
                case CALLBACK:
                    out.writeByte(2);
                    break;
                case POLL:
                    out.writeByte(3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected: " + this);
            }
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._retType = ReturnType.readFrom(in);
        this._id = ObjectUtils.readString(in);
        final int auxiliary = in.readByte();
        if(auxiliary != 0) {
            if((auxiliary & 1) == 1) {
                this._replyPattern = ReplyPattern.readFrom(in);
            }
            if((auxiliary & 2) == 2) {
                this._resultHandler = (ResultHandler) in.readObject();
            }
            if((auxiliary & 4) == 4) {
                this._priority = in.readShort();
            }
            if((auxiliary & 8) == 8) {
                this._timeout = in.readLong();
            }
        }
        this._invokedTimestamp = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        _retType.writeTo(out); // #1
        assert (_id != null);
        ObjectUtils.writeString(out, _id); // #2
        int auxiliary = 0;
        final ReplyPattern replyPattern = _replyPattern;
        if(replyPattern != ReplyPattern.RESPONSE) {
            auxiliary |= 1;
        }
        final ResultHandler handler = _resultHandler;
        if(handler != null) {
            auxiliary |= 2;
        }
        final short priority = _priority;
        if(priority != NORMAL_PRIORITY) {
            auxiliary |= 4;
        }
        final long timeout = _timeout;
        if(timeout != 0) {
            auxiliary |= 8;
        }
        out.writeByte(auxiliary); // #3 controls
        if(auxiliary != 0) {
            if(replyPattern != ReplyPattern.RESPONSE) {
                replyPattern.writeTo(out);
            }
            if(handler != null) {
                out.writeObject(handler);
            }
            if(priority != NORMAL_PRIORITY) {
                out.writeShort(priority);
            }
            if(timeout != 0) {
                out.writeLong(timeout);
            }
        }
        out.writeLong(_invokedTimestamp);
    }

}
