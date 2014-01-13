package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.watcherFtp.domains.Config;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Stream;

public class ConfigManager
{
    public Config[] listAllConfigs()
    {
        ArrayList<Config> configs = new ArrayList<Config>(2);

        Path configPath = FileSystems.getDefault().getPath("configs");
        if(Files.isDirectory(configPath))
        {

            try
            {
                DirectoryStream<Path> dir = Files.newDirectoryStream(configPath);

                for(Path p: dir)
                {
                    BufferedReader reader = null;
                    try
                    {
                        reader = Files.newBufferedReader(p);
                        Properties props = new Properties();
                        props.load(reader);
                        Config cfg = new Config();
                        cfg.setHost(props.getProperty("host"));
                        cfg.setLocalFolder(props.getProperty("localDir"));
                        cfg.setName(props.getProperty(p.normalize().toString()));
                        cfg.setPassword(props.getProperty("password"));
                        cfg.setPort(Integer.parseInt(props.getProperty("port")));
                        cfg.setRemoteFolder(props.getProperty("remoteDir"));
                        cfg.setUser(props.getProperty("user"));

                        configs.add(cfg);
                        reader.close();
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        return configs.toArray(new Config[configs.size()]);
    }
}
