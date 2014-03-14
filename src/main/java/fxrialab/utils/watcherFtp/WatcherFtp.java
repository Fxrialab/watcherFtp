package fxrialab.utils.watcherFtp;

import fxrialab.utils.watcherFtp.domains.Config;
import fxrialab.utils.watcherFtp.menu.TrayMenu;
import fxrialab.utils.watcherFtp.operations.ConfigManager;
import fxrialab.utils.watcherFtp.operations.DirWatcher;
import fxrialab.utils.watcherFtp.operations.RemoteDir;
import fxrialab.utils.watcherFtp.operations.WatchThread;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class WatcherFtp implements Runnable {
    public static void main( String[] args )
    {
        WatcherFtp app = new WatcherFtp();
        EventQueue.invokeLater(app);
    }

    private TrayMenu menu;
    private boolean stopPolling=false;

    @Override
    public void run() {
        menu = new TrayMenu();
        menu.addEventListener("exit",new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopPolling = true;
                System.exit(0);
            }
        });
        ConfigManager configManager = new ConfigManager();
        Config[] configs = configManager.listAllConfigs();
        Executor exe = Executors.newCachedThreadPool();
        for(Config cfg:configs)
        {
            menu.addLabel(cfg.getLocalFolder() + " ==> " + cfg.getRemoteFolder());
            exe.execute(new WatchThread(cfg));
        }
    }
}
