package fxrialab.utils.watcherFtp.operations;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FtpHostManager
{
    private static Map<String,SFTPClient> sessionMap = new HashMap<String, SFTPClient>(10);

    public static SFTPClient connectSftp(String host, int port,String user,String password) throws IOException
    {
        String key = user + "@"+host + ":" + port;
        if(!sessionMap.containsKey(key))
        {
            SSHClient ssh = new SSHClient();
            ssh.setTimeout(10000);
            ssh.loadKnownHosts();

            SFTPClient client = ssh.newSFTPClient();

            sessionMap.put(key,client);
        }

        return sessionMap.get(key);
    }
}
