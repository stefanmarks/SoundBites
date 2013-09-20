package com.illposed.osc;

/**
 * Listener interface for parameter changes.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public interface OSCParameterListener<Type> 
{
    void valueChanged(OSCParameter<Type> param);
}
