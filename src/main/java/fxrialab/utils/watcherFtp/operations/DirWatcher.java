package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.watcherFtp.domains.FolderChangeEvent;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirWatcher extends EventDispatcher
{
    protected static WatchService watcher;
    private Path targetFolder;
    private WatchKey wkey;
    private Map<WatchKey,Path> keys;
    private boolean beingDisposed = false;

    public DirWatcher(Path folder) throws IOException
    {
        super();
        this.targetFolder = folder;
        if(watcher == null)
        {
            watcher = FileSystems.getDefault().newWatchService();
        }

        keys = new HashMap<WatchKey, Path>(20);
    }

    public void dispose()
    {
        beingDisposed = true;
        try
        {
            watcher.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void register(Path p) throws IOException
    {
        if(!keys.containsKey(p.toString()))
        {
            WatchKey k = p.register(watcher, ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
            keys.put(k,p);
        }
    }

    public void watchDirRecursive(Path p) throws IOException
    {
        Files.walkFileTree(p, new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                register(dir);
                return super.preVisitDirectory(dir, attrs);
            }
        });
    }

    protected void processEvents()
    {
        while (!beingDisposed)
        {
            WatchKey key;
            try
            {
                key = watcher.take();
            } catch (InterruptedException e)
            {
                return;
            }

            Path dir = keys.get(key);
            if(dir == null)
            {
                System.err.println("watch key is not recognized");
                continue;
            }

            for(WatchEvent<?> event: key.pollEvents())
            {
                WatchEvent.Kind kind = event.kind();
                if(kind == OVERFLOW)
                {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>)(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                if(kind == ENTRY_CREATE)
                {
                    if(Files.isDirectory(child))
                    {
                        try
                        {
                            watchDirRecursive(child);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                dispatchEvent(new FolderChangeEvent(this,kind.name(),targetFolder.relativize(child)));
            }

            boolean  valid = key.reset();
            if(!valid)
            {
                keys.remove(key);
                if(keys.isEmpty())
                {
                    dispose();
                }
            }
        }
    }
}
