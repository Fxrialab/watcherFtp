package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.EventDispatcher;
import fxrialab.utils.watcherFtp.domains.FolderChangeEvent;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.file.StandardWatchEventKinds.*;

public class RemoteDir extends EventDispatcher
{
    private String remotePath;
    private Queue<FolderChangeEvent> changes;

    private String sshHost;
    private int sshPort;
    private String sshUser;
    private String sshPwd;
    private SSHClient client;

    public RemoteDir(String sshHost,int sshPort,String sshUser,String sshPwd, String remotePath) throws IOException
    {
        super();
        this.remotePath = remotePath;
        this.sshHost    = sshHost;
        this.sshPort    = sshPort;
        this.sshPwd     = sshPwd;
        this.sshUser    = sshUser;

        client = FtpHostManager.connectSftp(this.sshHost,this.sshPort,this.sshUser,this.sshPwd);
        this.changes   = new ConcurrentLinkedQueue<FolderChangeEvent>();

    }

    protected void processChange()
    {
        if(!changes.isEmpty())
        {
            FolderChangeEvent ev = changes.remove();
            String sourceFolder = (ev.getSource()).toString();
            String changePath   = ev.getChange().toString();
            System.out.println("change :" + changePath);
            try
            {
                if(!client.isConnected())
                    client = FtpHostManager.connectSftp(this.sshHost,this.sshPort,this.sshUser,this.sshPwd);
                SFTPClient ftp = client.newSFTPClient();
                ftp.put(sourceFolder + "/" + changePath,remotePath + "/" + changePath);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                Thread.sleep(5);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            processChange();
        }
    }

    public void listen(DirWatcher watcher)
    {
        watcher.addEventListener(ENTRY_CREATE.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
                processChange();
            }
        });
        watcher.addEventListener(ENTRY_DELETE.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
                processChange();
            }
        });
        watcher.addEventListener(ENTRY_MODIFY.name(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changes.add((FolderChangeEvent)e);
                processChange();
            }
        });
    }

}
