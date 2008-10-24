/*
 * @(#)$Id: CommandInvoker.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.*;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.util.datetime.StopWatch;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CommandInvoker {
    private static final Log LOG = LogFactory.getLog(CommandInvoker.class);

    @Option(name = "-col", usage = "base collection", metaVar = "Filespec")
    private String colpath = null;

    @Argument
    private List<String> arguments = new ArrayList<String>();

    private Session session;
    private final List<Command> commands = new ArrayList<Command>(8);

    public CommandInvoker(Session session) {
        setSession(session);
    }

    public void setSession(Session newSession) {
        this.session = newSession;
        registerCommands(newSession);
    }

    private void registerCommands(Session session) {
        commands.add(new ImportDocument(session));
        // TODO add more commands
    }

    public boolean executeCommand(String[] args) throws CommandException {
        final Command cmd = matchedCommand(args);
        return cmd.process(args);
    }

    private Command matchedCommand(String[] args) throws CommandNotFound {
        for(Command cmd : commands) {
            if(cmd.match(args)) {
                return cmd;
            }
        }
        throw new CommandNotFound("Illegal argument: " + Arrays.toString(args));
    }

    private static final class CommandNotFound extends CommandException {
        private static final long serialVersionUID = -8453943462408305317L;

        public CommandNotFound(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        Session session = new Session();
        CommandInvoker invoker = new CommandInvoker(session);
        invoker.run(args);
    }

    private void run(String[] args) {
        prepArgs(args);
        if(colpath != null) {
            final DbCollection col;
            try {
                col = DbCollection.getRootCollection().createCollection(colpath);
            } catch (DbException e) {
                throw new IllegalStateException("create collection failed: " + colpath, e);
            }
            session.setContextCollection(col);
        }
        String[] cmdArgs = arguments.toArray(new String[arguments.size()]);
        StopWatch sw = new StopWatch();
        boolean success = false;
        try {
            success = executeCommand(cmdArgs);
        } catch (CommandException e) {
            LOG.error("command failed: " + Arrays.toString(cmdArgs), e);
        }
        if(success) {
            LOG.info("Command is successfully done.\nelasped " + sw);
        } else {
            LOG.error("Command failed: " + Arrays.toString(args) + "\nelasped " + sw);
        }
    }

    private void prepArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            LOG.error(e.getMessage());
            return;
        }
    }

}
