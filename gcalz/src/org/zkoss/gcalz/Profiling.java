/*
*Profiling.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/19 ¤U¤È 5:09:13, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.util.Date;

/**
 * @author Ian Tsai
 * @date 2007/7/19
 */
public class Profiling
{
    private String prefix;
    private long befor;
    private long after;
    private boolean inAction;
    
    /**
     * 
     *
     */
    public void end()
    {
        after = new Date().getTime();
        //System.out.println("<- \""+ prefix +"()\" Spend:"+((after-befor)/1000)+" sec.");
        inAction = false;
    }
    /**
     * 
     * @param pre
     * @return
     */
    public void start(String pre)
    {
        if(inAction)end();
        befor = new Date().getTime();
        //System.out.println("-> \""+ (prefix=pre) +"()\" Start Times:"+(befor/1000)+" sec.");
        inAction = true;
    }
    

}//end of class
