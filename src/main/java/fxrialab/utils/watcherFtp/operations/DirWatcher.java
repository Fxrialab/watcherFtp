package fxrialab.utils.watcherFtp.operations;

import fxrialab.utils.EventDispatcher;
import fxrialab.utils.watcherFtp.domains.FolderChangeEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirWatcher extends EventDispatcher
{
    protected WatchService watcher;
    private Path targetFolder;
    private Map<WatchKey,Path> keys;
    private boolean beingDisposed = false;

    public DirWatcher(Path folder) throws IOException
    {
        super();
        this.targetFolder = folder;
        watcher = FileSystems.getDefault().newWatchService();

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

    public void unregister(Path p) throws IOException
    {
        if(!keys.containsKey(p.toString()))
        {
            WatchKey k = null;
            Iterator<WatchKey> iterator = keys.keySet().iterator();
            while(iterator.hasNext())
            {
                k = iterator.next();
                if(keys.get(k) == p)
                {
                    break;
                }
            }
            if(k != null)
                keys.remove(k);
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

    public void unwatchDirRecursive(Path p) throws IOException
    {
        Files.walkFileTree(p, new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                unregister(dir);
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
                else if(kind == ENTRY_DELETE)
                {
                    if(Files.isDirectory(child))
                    {
                        try
                        {
                            unwatchDirRecursive(child);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                dispatchEvent(new FolderChangeEvent(targetFolder,kind.name(),targetFolder.relativize(child)));
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

    public void start()
    {
        try
        {
            watchDirRecursive(targetFolder);
            processEvents();
        } catch (IOException e)
        {
            LoggerFactory.getLogger(DirWatcher.class).error("Start watcher error:",e);
        }
    }
}
