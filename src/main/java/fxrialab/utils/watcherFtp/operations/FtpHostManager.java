package fxrialab.utils.watcherFtp.operations;

import com.jcraft.jsch.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FtpHostManager
{
    private static JSch jsch;
    private static Properties commonProps;
    private static Map<String,Channel> sessionMap;
    static {
        jsch = new JSch();
        commonProps = new Properties();
        commonProps.put("StrictHostKeyChecking","No");
        sessionMap = Collections.synchronizedMap(new HashMap<String, Channel>(10));
    }
    public static Channel connectSftp(String host, int port,String user,String password) throws JSchException
    {
        String key = user + "@"+host + ":" + port;
        if(!sessionMap.containsKey(key))
        {
            Session session = jsch.getSession(user,host,port);
            session.setConfig(commonProps);
            session.setPassword(password);

            session.connect();
            session.setServerAliveInterval(100);
            Channel channel = session.openChannel("sftp");
            sessionMap.put(key,channel);
        }

        return sessionMap.get(key);
    }

    public static void keepServersAlive()
    {
        for(Channel channel : sessionMap.values())
        {
            try
            {
                channel.getSession().sendKeepAliveMsg();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
