/*
*Ref.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/5/24 PM 2:57:00, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;
/**
 * 
 * an easy ref to object
 * @author Ian Tsai
 */
public class Ref
{
    public Object ref;
    /**
     * 
     * @param object the object this ref pointed
     */
    public Ref(Object object)
    {
        ref = object;
    }
    
    /**
     * Defult Constructor
     */
    public Ref(){}
    /**
     * 
     * @return the object this ref pointed
     */
    public Object get()
    {
        return ref;
    }
    
    /**
     * 
     * @param object the pointed Object
     */
    public void set(Object object)
    {
        ref = object;
    }
    
}//end of class
