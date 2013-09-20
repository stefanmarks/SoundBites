package com.illposed.osc;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import processing.core.PVector;

/**
 * Class for managing parameters that can be changed via OSC.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public class OSCParameter<Type> implements OSCListener
{
    public OSCParameter(String address, Type initialValue)
    {
        this.address = address;
        this.value   = initialValue;
        this.changed = true;
        this.listeners = new LinkedList<OSCParameterListener<Type>>();
    }

    public void registerWithPort(OSCPortIn port)
    {
        port.addListener(address, this);
    }
    
    public void registerListener(OSCParameterListener<Type> listener)
    {
        listeners.add(listener);
    }

    public OSCMessage prepareMessage()
    {
        OSCMessage message = new OSCMessage(address);
        if ( value instanceof PVector )
        {
            PVector vec = (PVector) value;
            message.addArgument(vec.x);
            message.addArgument(vec.y);
            message.addArgument(vec.z);
        }
        else if ( value instanceof Enum )
        {
            Enum e = (Enum) value;
            message.addArgument(e.name());
        }
        else
        {
            message.addArgument(value);
        }
        return message;
    }
    
    public Type get()
    {
        return value;
    }
    
    public void set(Type newValue)
    {
        if ( value instanceof PVector )
        {
            ((PVector) value).set((PVector) newValue);
        }
        else
        {
            value = newValue;
        }
        changed = true;
        notifyListeners();
    }

    public boolean isChanged()
    {
        return changed;
    }
    
    public void resetChanged()
    {
        changed = false;
    }
    
    @Override
    public void acceptMessage(Date time, OSCMessage message)
    {
        Object[] params = message.getArguments();
        if ( (value instanceof PVector) && (params.length > 2) )
        {
            ((PVector) value).set((Float) params[0], 
                                  (Float) params[1], 
                                  (Float) params[2]);
        }
        else if ( (value instanceof Enum) && (params.length > 0) )
        {
            String name    = (String) params[0];
            Object enums[] = value.getClass().getEnumConstants();
            for ( Object e : enums )
            {
                if ( ((Enum) e).name().equals(name) )
                {
                    value = (Type) e;
                    break;
                }
            }
        }
        else if ( params.length > 0 )
        {
            value = (Type) params[0];
        }
        else
        {
            System.err.println("Received invalid OSC packet '" + message.getAddress() + "'.");
        }
        changed = true;
        notifyListeners();
    }
    
    private void notifyListeners()
    {
        for ( OSCParameterListener<Type> listener : listeners )
        {
            listener.valueChanged(this);
        }
    }
    
    private String                           address;
    private Type                             value;
    private boolean                          changed;
    private List<OSCParameterListener<Type>> listeners;
}
