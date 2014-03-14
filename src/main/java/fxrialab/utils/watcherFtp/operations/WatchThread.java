package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.watcherFtp.domains.Config;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class WatchThread implements Runnable
{
    private Config cfg;
    public WatchThread(Config cfg)
    {
        this.cfg = cfg;
    }

    @Override
    public void run()
    {
        DirWatcher local;
        try
        {
            local = new DirWatcher(Paths.get(cfg.getLocalFolder()));
            RemoteDir remote = new RemoteDir(cfg.getHost(),cfg.getPort(),
                    cfg.getUser(),cfg.getPassword(),
                    cfg.getRemoteFolder());
            LoggerFactory.getLogger("WatchThread").info("start watching folder " + cfg.getLocalFolder());
            remote.listen(local);
            local.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
