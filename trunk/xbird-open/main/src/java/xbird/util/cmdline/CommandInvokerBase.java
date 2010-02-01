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
package xbird.util.cmdline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import xbird.util.datetime.StopWatch;
import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class CommandInvokerBase {

    protected final Map<String, String> options = new HashMap<String, String>(8);
    private final List<Command> commands = new ArrayList<Command>(4);

    public CommandInvokerBase(Command... cmds) {
        registerCommands(cmds);
    }

    public void registerCommands(Command... cmds) {
        for(Command cmd : cmds) {
            commands.add(cmd);
        }
    }

    public String getOption(String name) {
        return options.get(name);
    }

    public boolean executeCommand(String[] args) throws CommandException {
        final Command cmd = matchedCommand(args);
        final Map<String, Option<?>> opts = cmd.listOptions();
        for(Map.Entry<String, String> e : options.entrySet()) {
            String name = e.getKey();
            Option<?> opt = opts.get(name);
            if(opt != null) {
                String value = e.getValue();
                opt.setValue(value);
            }
        }
        for(Option<?> opt : opts.values()) {
            if(!opt.isValid()) {
                final StringBuilder msg = new StringBuilder(128);
                msg.append("Required command line option '");
                msg.append(opt.getName());
                msg.append("' is not specified");
                String help = opt.getHelp();
                if(help != null) {
                    msg.append(":\n -- ");
                    msg.append(help);
                }
                throw new CommandException(msg.toString());
            }
        }
        return cmd.process(args);
    }

    @Nonnull
    private Command matchedCommand(String[] args) throws CommandException {
        for(Command cmd : commands) {
            if(cmd.match(args)) {
                return cmd;
            }
        }
        throw new CommandException("Illegal argument: " + Arrays.toString(args));
    }

    protected void run(String[] args) {
        final StopWatch sw = new StopWatch();
        boolean success = false;
        try {
            String[] cmds = parseOptions(args);
            success = executeCommand(cmds);
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
        if(success) {
            System.out.println("Command is successfully done.\nelasped " + sw);
        } else {
            System.err.println("Command failed: " + Arrays.toString(args) + "\nelasped " + sw);
        }
    }

    private String[] parseOptions(String args[]) throws CommandException {
        final int arglen = args.length;
        if(arglen == 0) {
            return args;
        }
        int i = 0;
        for(; i < arglen; i += 2) {
            String arg = args[i];
            if(arg.startsWith("-")) {
                String optkey = arg.substring(1);
                if(i >= arglen) {
                    throw new CommandException("Illegal command: " + Arrays.toString(args));
                }
                String optvalue = args[i + 1];
                options.put(optkey.toLowerCase(), optvalue);
            } else {
                break;
            }
        }
        return ArrayUtils.copyOfRange(args, i, arglen);
    }
}
