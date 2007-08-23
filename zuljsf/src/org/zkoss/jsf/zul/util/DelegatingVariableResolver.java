/* DelegatingVariableResolver.java
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
package org.zkoss.jsf.zul.util;

import org.zkoss.zk.scripting.VariableResolver;
/**
 * A Variable Resolver to find variable from JSF context.
 * 
 * <br/>Configuration in zul file:<br/>
 * &lt;?variable-resolver class=&quot;org.zkoss.jsf.zul.util.DelegatingVariableResolver&quot;?&gt;
 * 
 * @author Dennis.Chen
 *
 */
public class DelegatingVariableResolver implements VariableResolver {
    
    public DelegatingVariableResolver(){
    }

    /**
     * Get bean from JSF context,<br/>
     * 
     * @param name bean name
     * @return bean get from JSF context 
     */
    public Object getVariable(String name) {
        Object obj = ContextUtil.getBean(name);
        
        return obj;
    }
    


}
