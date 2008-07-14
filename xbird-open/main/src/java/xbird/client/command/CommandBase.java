/*
 * @(#)$Id: CommandBase.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.client.command;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class CommandBase implements Command {

    protected final Session session;

    public CommandBase(Session session) {
        this.session = session;
    }

    protected void throwException(String msg) throws CommandFailedException {
        throw new CommandFailedException(msg + "\n" + usage());
    }

    protected void rethrowException(Exception cause) throws CommandFailedException {
        this.rethrowException(cause.getMessage(), cause);
    }

    protected void rethrowException(String msg, Exception cause) throws CommandFailedException {
        throw new CommandFailedException(msg + "\n" + usage(), cause);
    }

    protected static String constructHelp(String desc, String usage) {
        return constructHelp(desc, usage, null);
    }

    protected static String constructHelp(String desc, String usage, String help) {
        StringBuilder buf = new StringBuilder(256);
        buf.append("------------------------------------------\n");
        buf.append("Description: ").append(desc).append('\n');
        buf.append("Usage: ").append(usage).append('\n');
        if(help != null) {
            buf.append("Help: ").append(help).append('\n');
        }
        buf.append("------------------------------------------");
        return buf.toString();
    }

}
