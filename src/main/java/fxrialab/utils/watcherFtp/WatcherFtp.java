package fxrialab.utils.watcherFtp;

import fxrialab.utils.watcherFtp.domains.Config;
import fxrialab.utils.watcherFtp.menu.TrayMenu;
import fxrialab.utils.watcherFtp.operations.ConfigManager;
import fxrialab.utils.watcherFtp.operations.DirWatcher;
import fxrialab.utils.watcherFtp.operations.RemoteDir;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;

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
        for(Config cfg:configs)
        {
            try
            {
                DirWatcher local = new DirWatcher(Paths.get(cfg.getLocalFolder()));
                RemoteDir remote = new RemoteDir(cfg.getHost(),cfg.getPort(),
                                                 cfg.getUser(),cfg.getPassword(),
                                                 cfg.getRemoteFolder());
                remote.listen(local);

                menu.addLabel(cfg.getLocalFolder() + "->" + cfg.getRemoteFolder());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
//        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
//        exe.scheduleAtFixedRate(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                while (!stopPolling)
//                {
//                    FtpHostManager.keepServersAlive();
//                    try
//                    {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        },0,200, TimeUnit.MILLISECONDS);
    }
}
