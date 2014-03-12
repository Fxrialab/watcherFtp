package fxrialab.utils.watcherFtp.menu;

import fxrialab.utils.EventDispatcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrayMenu extends EventDispatcher
{
    private SystemTray tray;
    private TrayMenu trayMenu;
    private PopupMenu popup;
    public TrayMenu()
    {
        super();
        trayMenu = this;
        tray = SystemTray.getSystemTray();
        if(tray != null)
        {
            //popup menu
            popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ExitActionHandler());
            popup.add(exitItem);

             TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("icon.png"),"Watcher FTP Menu",popup);
            icon.setPopupMenu(popup);
            try{
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void addLabel(String label)
    {
        MenuItem item = new MenuItem(label);
        item.setEnabled(false);
        popup.insert(item,0);
    }

    /**
     * listeners
     */

    private class ExitActionHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            MenuItem source = (MenuItem)e.getSource();
            source.removeActionListener(this);

            trayMenu.dispatchEvent(new ActionEvent(trayMenu, hashCode(), "exit"));
        }
    }
}

