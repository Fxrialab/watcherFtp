package fxrialab.utils.watcherFtp.domains;

public class Config
{
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getLocalFolder()
    {
        return localFolder;
    }

    public void setLocalFolder(String localFolder)
    {
        this.localFolder = localFolder;
    }

    public String getRemoteFolder()
    {
        return remoteFolder;
    }

    public void setRemoteFolder(String remoteFolder)
    {
        this.remoteFolder = remoteFolder;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    private String name;
    private String host;
    private int    port;
    private String localFolder;
    private String remoteFolder;
    private String user;
    private String password;

}
