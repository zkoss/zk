/*
*Ref.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/5/24 ¤U¤È 2:57:00, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;
/**
 * 
 * an easy ref to object
 * @author Ian Tsai
 * @date 2007/5/24
 */
public class Ref<T>
{
    public T ref;
    /**
     * 
     * @param object
     */
    public Ref(T object)
    {
        ref = object;
    }
    
    /**
     * Defult Constructor
     */
    public Ref(){}
    /**
     * 
     * @return
     */
    public T get()
    {
        return ref;
    }
    
    /**
     * 
     * @return
     */
    public void set(T object)
    {
        ref = object;
    }
    
}//end of class
