package fxrialab.utils.watcherFtp.operations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class EventDispatcher
{
    private HashMap<String,ActionListener> listeners;

    public EventDispatcher()
    {
        this.listeners = new HashMap<String, ActionListener>(10);
    }

    public void addEventListener(String event, ActionListener l)
    {
        ActionListener eventListener;
        if(!listeners.containsKey(event))
        {
            listeners.put(event, null);
        }
        eventListener = listeners.get(event);
        listeners.put(event, AWTEventMulticaster.add(l, eventListener));
    }

    public void removeEventListener(String event, ActionListener l)
    {
        ActionListener eventListener;
        if(!listeners.containsKey(event))
        {
            listeners.put(event, null);
        }
        eventListener = listeners.get(event);
        listeners.put(event, AWTEventMulticaster.remove(l, eventListener));
    }

    public void dispatchEvent(ActionEvent ev)
    {
        if(listeners.containsKey(ev.getActionCommand()))
        {
            ActionListener eventListener = listeners.get(ev.getActionCommand());
            eventListener.actionPerformed(ev);
        }
    }
}
