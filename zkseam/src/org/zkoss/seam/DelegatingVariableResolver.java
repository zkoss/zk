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
package org.zkoss.seam;

import org.jboss.seam.jsf.ListDataModel;
import org.zkoss.zk.scripting.VariableResolver;
/**
 * A Variable Resolver to find variable from Seam's context.
 * 
 * <br/>Configuration in zul file:<br/>
 * &lt;?variable-resolver class=&quot;org.zkforge.seam.DelegatingVariableResolver&quot;?&gt;
 * 
 * @author Dennis.Chen
 *
 */
public class DelegatingVariableResolver implements VariableResolver {
    
    public DelegatingVariableResolver(){
    }

    /**
     * Get bean from Seam's context,<br/>
     * If bean is a ListDataModel, then a {@link ListDataModelWrapper} which contains original bean will be return.
     * This is for delegating JSF' DataModel to ZK's DataModel.
     * 
     * @param name name of component
     * @return bean of context.
     */
    public Object getVariable(String name) {
        Object obj = ContextUtil.getBean(name);
        
        if(obj instanceof ListDataModel){
            return new ListDataModelWrapper((ListDataModel)obj);
        }
        return obj;
    }
    


}
