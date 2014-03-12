package fxrialab.utils.watcherFtp.operations;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FtpHostManager
{
    private static Map<String,SSHClient> sessionMap = new HashMap<String, SSHClient>(10);

    public static SSHClient connectSftp(String host, int port,String user,String password) throws IOException
    {
        String key = user + "@"+host + ":" + port;
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        System.out.println("connecting to "+host);
        ssh.connect(host, port);
        ssh.authPassword(user, password);
        ssh.startSession();

        sessionMap.put(key,ssh);

        return sessionMap.get(key);
    }
}
