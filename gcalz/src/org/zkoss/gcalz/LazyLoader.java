/*
*EventListBox.java
*
*   Purpose:
*       
*   Description:
*       
*   History:
*     2007/7/19 PM 2:17:28, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;
/**
 * @author Ian Tsai
 * @date 2007/7/19
 */
public class LazyLoader
{
    private Ref threadRef =  new Ref();
    /**
     * A generic Function  to do lazyloading works.<br>
     *  
     * @param invoker An Ref object, you should override get() method to 
     * implement those havy works need to do.
     * @param sleepWait busy waiting parameter, in any test round if still need 
     * to wait, how long to sleep? 
     * @param testRound busy waiting parameter, how many test round to test.
     * @return A Ref to get back the working result, you can use Ref.ref ==null 
     * to know if answer is back, or just invoke ref.get() to wait answer. 
     */
    public Ref doLoad(final Ref invoker, final int sleepWait, final int testRound)
    {
        final Ref ref = new Ref(){
            public Ref innerThread = threadRef;
            public Object get()
            {
                for(int i=testRound;super.get()==null&&i>0&&
                	((Thread)innerThread.get()).isAlive();i--)
                {
                	try
                    {
                        Thread.sleep(sleepWait);
                    } 
                    catch (InterruptedException e){ e.printStackTrace();}
                }
                return super.get();
            }
        };//end of class
        threadRef.set( new Thread(new Runnable(){
            public void run()
            {
                Object ans = null;
                ans = invoker.get();
                ref.set(ans);
            }
        }));//end of class
        ((Thread)threadRef.get()).start();
        return ref;
    }
    /**
     * get the thread that
     * @return
     */
    public Ref getLoadingThreadRef()
    {
        return threadRef;
    }
    
}//end of class
