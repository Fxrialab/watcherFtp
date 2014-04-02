package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.EventDispatcher;
import fxrialab.utils.watcherFtp.domains.FolderChangeEvent;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.file.StandardWatchEventKinds.*;

public class RemoteDir extends EventDispatcher
{
    private static Logger log = LoggerFactory.getLogger(RemoteDir.class);

    private String remotePath;
    private Queue<FolderChangeEvent> changes;

    private String sshHost;
    private int sshPort;
    private String sshUser;
    private String sshPwd;
    private SSHClient client;

    public RemoteDir(String sshHost,int sshPort,String sshUser,String sshPwd, String remotePath)
    {
        super();
        this.remotePath = remotePath;
        this.sshHost    = sshHost;
        this.sshPort    = sshPort;
        this.sshPwd     = sshPwd;
        this.sshUser    = sshUser;
        this.changes   = new ConcurrentLinkedQueue<FolderChangeEvent>();

    }

    protected void processChange()
    {
        if(!changes.isEmpty())
        {
            FolderChangeEvent ev = changes.remove();
            String sourceFolder = (ev.getSource()).toString();
            String changePath   = ev.getChange().toString();
            changePath = changePath.replaceAll("\\\\","/");
            sourceFolder = sourceFolder.replaceAll("\\\\","/");
            try
            {
                if (client== null || !client.isConnected())
                    client = FtpHostManager.connectSftp(this.sshHost, this.sshPort, this.sshUser, this.sshPwd);
                File source = Paths.get(sourceFolder + "/" +changePath).toFile();

                if(ev.getActionCommand()== ENTRY_DELETE.name() )
                {
                    Session session = client.startSession();
                    String cmd =  "rm -rf " + remotePath + "/" + changePath;
                    session.exec(cmd);
                    SystemTray.getSystemTray().getTrayIcons()[0].displayMessage("Removing ", changePath, TrayIcon.MessageType.INFO);
                    log.info(cmd);
                    session.close();
                }
                else if(ev.getActionCommand()==ENTRY_CREATE.name())
                {
                    SFTPClient ftp = client.newSFTPClient();
                    ftp.put(sourceFolder + "/" + changePath, remotePath + "/" + changePath);
                    SystemTray.getSystemTray().getTrayIcons()[0].displayMessage("Uploading ", changePath, TrayIcon.MessageType.INFO);
                    log.info("created folder " + remotePath + "/" + changePath);
                    ftp.close();
                }
                else if(source.exists() && source.isFile()  && ev.getActionCommand()==ENTRY_MODIFY.name())
                {
                    SFTPClient ftp = client.newSFTPClient();
                    ftp.put(sourceFolder + "/" + changePath, remotePath + "/" + changePath);
                    SystemTray.getSystemTray().getTrayIcons()[0].displayMessage("Uploading ", changePath, TrayIcon.MessageType.INFO);
                    log.info("updated " + remotePath + "/" + changePath);
                    ftp.close();
                }
            } catch (IOException e)
            {
                log.error("NIO error:" ,e);
                try
                {
                    client.close();
                } catch (IOException e1)
                {
                    log.error("disconnect error:", e1);
                }
                finally
                {
                    client = null;
                }
            }
            try
            {
                Thread.sleep(5);
            } catch (InterruptedException e)
            {
                log.error("Thread error:",e);
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
