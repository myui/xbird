/*
 * @(#)$Id: SysTray.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.tools;

import javax.swing.JOptionPane;

import xbird.config.Settings;
import xbird.server.Server;

import snoozesoft.systray4j.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SysTray implements SysTrayMenuListener {

    private static final String TITLE = "Xbird SysTray";

    private static final String CMD_START_SERVER = "Start";
    private static final String CMD_SHUTDOWN_SERVER = "Shutdown";
    private static final String CMD_ABOUT = "About...";
    private static final String CMD_EXIT = "Exit";

    private static final int NORMAL_DUKE = 0;
    private static final int DUKE_UP = 1;
    private static final ImageIcon[] _icons = {
            new ImageIcon(SysTray.class.getResource("icons/duke.ico")),
            new ImageIcon(SysTray.class.getResource("icons/duke_up.ico")) };

    private final SysTrayMenu _menu;

    private Server _server = null;

    private SysTray() {
        for(SysTrayMenuIcon icon : _icons) {
            icon.addSysTrayMenuListener(this);
        }
        this._menu = new SysTrayMenu(_icons[NORMAL_DUKE], TITLE);
    }

    public void iconLeftClicked(SysTrayMenuEvent e) {}

    public void iconLeftDoubleClicked(SysTrayMenuEvent e) {}

    public void menuItemSelected(SysTrayMenuEvent e) {
        final String cmd = e.getActionCommand();
        if(cmd == CMD_START_SERVER) {
            startServer();
        } else if(cmd == CMD_SHUTDOWN_SERVER) {
            shutdownServer();
        } else if(cmd == CMD_ABOUT) {
            JOptionPane.showMessageDialog(null, "SysTray of Xbird ver " + Settings.XBIRD_VERSION);
        } else if(cmd == CMD_EXIT) {
            shutdownServer();
            System.exit(0);
        } else {
            System.err.println("Invalid action: " + cmd);
        }
    }

    private void startServer() {
        if(_server != null) {
            JOptionPane.showMessageDialog(null, "Server is already started");
            return;
        }
        Server server = new Server();
        server.start();
        this._server = server;
        _menu.setIcon(_icons[DUKE_UP]);
    }

    private void shutdownServer() {
        if(_server == null) {
            return;
        }
        _server.shutdown(false);
        this._server = null;
        _menu.setIcon(_icons[NORMAL_DUKE]);
    }

    private void run(String[] args) {
        createMenu();
    }

    private void createMenu() {
        SysTrayMenuItem itemExit = new SysTrayMenuItem(CMD_EXIT, CMD_EXIT);
        itemExit.addSysTrayMenuListener(this);
        _menu.addItem(itemExit);
        _menu.addSeparator();
        SysTrayMenuItem itemAbout = new SysTrayMenuItem(CMD_ABOUT, CMD_ABOUT);
        itemAbout.addSysTrayMenuListener(this);
        _menu.addItem(itemAbout);
        _menu.addSeparator();
        SysTrayMenuItem itemShutdownAll = new SysTrayMenuItem(CMD_SHUTDOWN_SERVER, CMD_SHUTDOWN_SERVER);
        itemShutdownAll.addSysTrayMenuListener(this);
        _menu.addItem(itemShutdownAll);
        SysTrayMenuItem itemStartAll = new SysTrayMenuItem(CMD_START_SERVER, CMD_START_SERVER);
        itemStartAll.addSysTrayMenuListener(this);
        _menu.addItem(itemStartAll);
    }

    public static void main(String[] args) {
        new SysTray().run(args);
    }

}
