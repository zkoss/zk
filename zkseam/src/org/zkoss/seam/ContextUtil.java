/* ContextUtil.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
    Jul 25, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Context;
/**
 * This class helps developers to do thing with Seam's context
 * @author Dennis.Chen
 */
public class ContextUtil {

    /**
     * Get bean form Seam's Context by name.
     * @param name name of component
     * @return bean of name
     */
    static public Object getBean(String name){
        return Component.getInstance(name);
    }
    
    
    /**
     * Update bean to Seam's context by name.<br/>
     * This method will force getting a previous bean in context or by Seam's Component.getInstance().
     * (This is becase if there is nothing in context, then there is nothing to update into context.
     * This also make sure Seam will inject updated bean in context into other beans.)
     * @param name name of component
     * @param bean new instance or updated instance of component.
     */
    static public void updateToContext(String name,Object bean){
        //get From context or crate or nothing.
        Object obj = Component.getInstance(name);
        if(obj==null) return;
        putToContext(name,bean);
    }
    
    /**
     * Put bean into Seam's context ,<br/> 
     * the scope is depends on component's scope.
     * Note that, if there is no component found by name, the scope will be ScopeType.CONVERSATION.
     * @param name name of component
     * @param bean new instance or updated instance of component.
     */
    static public void putToContext(String name,Object bean){
        Component comp = Component.forName(name);
        ScopeType scope;
        if(comp!=null){
            scope = comp.getScope();
        }else{
            scope = ScopeType.CONVERSATION;
        }
        putToContext(name,bean,scope);
        
    }
    
    /**
     * Put bean into Seam's context 
     * @param name name of component
     * @param bean new instance or updated instance of component.
     */
    static public void putToContext(String name,Object bean,ScopeType scope){
        if(ScopeType.STATELESS.equals(scope)) return;
        
        if(scope.isContextActive()){
            Context context = scope.getContext();
            context.set(name,bean);
        }
        
    }
}
