package fxrialab.utils.watcherFtp.operations;

import com.jcraft.jsch.Channel;
import fxrialab.utils.watcherFtp.domains.FolderChangeEvent;

import static java.nio.file.StandardWatchEventKinds.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RemoteDir extends EventDispatcher
{
    private Channel connector;
    private String remotePath;
    private Queue<FolderChangeEvent> changes;

    public RemoteDir(Channel connector, String remotePath)
    {
        super();
        this.connector = connector;
        this.remotePath = remotePath;
        this.changes   = new ConcurrentLinkedQueue<FolderChangeEvent>();
    }

    protected void processChange()
    {
        if(!changes.isEmpty())
        {
            FolderChangeEvent ev = changes.remove();
            processChange();
        }
    }

    public void setLocalDirWatcher(DirWatcher watcher)
    {
        watcher.addEventListener(ENTRY_CREATE.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
            }
        });
        watcher.addEventListener(ENTRY_DELETE.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
            }
        });
        watcher.addEventListener(ENTRY_MODIFY.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
            }
        });
    }

}
