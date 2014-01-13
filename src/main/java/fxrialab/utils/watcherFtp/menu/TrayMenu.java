package fxrialab.utils.watcherFtp.menu;

import fxrialab.utils.watcherFtp.operations.EventDispatcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrayMenu extends EventDispatcher
{
    private SystemTray tray;
    private TrayMenu trayMenu;

    public TrayMenu()
    {
        super();
        trayMenu = this;
        tray = SystemTray.getSystemTray();
        if(tray != null)
        {
            //popup menu
            PopupMenu menu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ExitActionHandler());

            menu.add(exitItem);

             TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("src/main/resources/sftp.png"),"Watcher FTP Menu",menu);
            icon.setPopupMenu(menu);
            try{
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * listeners
     */

    private class ExitActionHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            trayMenu.removeEventListener("exit", this);

            dispatchEvent(new ActionEvent(this, hashCode(), "exit"));
        }
    }
}

