package fxrialab.utils.watcherFtp.domains;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

public class FolderChangeEvent extends ActionEvent
{
    public Path getChange()
    {
        return change;
    }

    public void setChange(Path change)
    {
        this.change = change;
    }

    private Path change;
    public FolderChangeEvent(Object source, String command, Path change)
    {
        super(source, (int)Math.random(), command);
        this.change = change;
    }
}
