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
package xbird.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import xbird.config.Settings;
import xbird.server.Server;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SysTray implements ActionListener {

    private static final String TITLE = "Xbird SysTray";

    private static final String CMD_START_SERVER = "Start";
    private static final String CMD_SHUTDOWN_SERVER = "Shutdown";
    private static final String CMD_ABOUT = "About...";
    private static final String CMD_EXIT = "Exit";

    private static final int NORMAL_DUKE = 0;
    private static final int DUKE_UP = 1;
    private static final ImageIcon[] _icons = {
            new ImageIcon(SysTray.class.getResource("icons/snoozeDuke.gif")),
            new ImageIcon(SysTray.class.getResource("icons/jws-dukeonly.gif")) };

    private final TrayIcon _trayIcon;
    private Server _server = null;

    public SysTray() {
        SystemTray tray = SystemTray.getDefaultSystemTray();
        TrayIcon icon = new TrayIcon(_icons[NORMAL_DUKE], TITLE, createMenu());
        icon.setIconAutoSize(true);
        tray.addTrayIcon(icon);
        this._trayIcon = icon;
    }

    public void draw(String[] args) {}

    private JPopupMenu createMenu() {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem itemStart = new JMenuItem(CMD_START_SERVER);
        itemStart.addActionListener(this);
        popup.add(itemStart);
        JMenuItem itemShutdown = new JMenuItem(CMD_SHUTDOWN_SERVER);
        itemShutdown.addActionListener(this);
        popup.add(itemShutdown);
        popup.addSeparator();
        JMenuItem itemAbout = new JMenuItem(CMD_ABOUT);
        itemAbout.addActionListener(this);
        popup.add(itemAbout);
        popup.addSeparator();
        JMenuItem itemExit = new JMenuItem(CMD_EXIT);
        itemExit.addActionListener(this);
        popup.add(itemExit);
        return popup;
    }

    private void startServer() {
        if(_server != null) {
            _trayIcon.displayMessage("WARN", "Server is already started", TrayIcon.WARNING_MESSAGE_TYPE);
            return;
        }
        final Server server = new Server();
        server.start();
        this._server = server;
        _trayIcon.setIcon(_icons[DUKE_UP]);
    }

    private void shutdownServer() {
        if(_server == null) {
            return;
        }
        _server.shutdown(false);
        this._server = null;
        _trayIcon.setIcon(_icons[NORMAL_DUKE]);
    }

    public void actionPerformed(ActionEvent e) {
        final String cmd = e.getActionCommand();
        if(cmd == CMD_START_SERVER) {
            startServer();
        } else if(cmd == CMD_SHUTDOWN_SERVER) {
            shutdownServer();
        } else if(cmd == CMD_ABOUT) {
            _trayIcon.displayMessage("INFO", "SysTray of Xbird ver " + Settings.XBIRD_VERSION, TrayIcon.INFO_MESSAGE_TYPE);
        } else if(cmd == CMD_EXIT) {
            shutdownServer();
            System.exit(0);
        } else {
            _trayIcon.displayMessage("ERROR", "Invalid action: " + cmd, TrayIcon.ERROR_MESSAGE_TYPE);
        }
    }

    public static void main(String[] args) {
        new SysTray().draw(args);
    }
}
