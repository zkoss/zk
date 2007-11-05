/*
*XmlOutputFns.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 ¤U¤È 6:10:33, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.xml;

import java.io.IOException;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Executions;

/**
 * @author Ian Tsai
 * @date 2007/7/11
 */
public class XmlOutputFns
{
    public static final String XML_MIME_TYPE = "application/xml; charset=UTF-8";
    public static final String XML_OUTPUT_ID = XmlOutputFns.class.toString();
    /**
     * 
     *
     */
    public static String output()
    {
        ServletFns.getCurrentResponse().setContentType(XML_MIME_TYPE);
        String out = 
            (String) ServletFns.getCurrentRequest().getAttribute(XML_OUTPUT_ID);
        return out==null?"":out;
    }
    
    /**
     * 
     * @param xmlContent
     * @throws IOException 
     */
    public static void doForward( String xmlContent) throws IOException
    {
        Executions.getCurrent().setAttribute(XmlOutputFns.XML_OUTPUT_ID, xmlContent);        
        Executions.forward("~./zul/xmlout/xmloutput.dsp"); 
    }
    
}//end of class
