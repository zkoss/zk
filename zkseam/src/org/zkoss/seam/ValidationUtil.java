/* ValidationUtil.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
    Jul 29, 2007 11:04:28 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.core.Expressions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.Binding;
import org.zkoss.zkplus.databind.BindingSaveEvent;
/**
 * This class helps developers to do validation when Data Binding Events is sent by Seam's rule   
 * @author Dennis.Chen
 */
public class ValidationUtil{

    static private String WRONG_VALUE_KEY = "zk.seam.ValidationUtil_WV";
    
    /**
     * Validate input value when onBindingSave, the validation result will be stored in current Execution,
     * and will be throw out when afterValidate();
     * @param event 
     * @throws WrongValueException
     */
    static public void validate(BindingSaveEvent event) throws WrongValueException{
        Component comp = event.getTarget();
        Object value = event.getValue();
        Binding binding = event.getBinding();
        
        String jsfexpress = "#{"+binding.getExpression()+"}";
        InvalidValue[] ivs;
        try {
             ivs = Expressions.instance().validate(jsfexpress, value);
        } catch (Exception e) {
             throw new WrongValueException("model validation failed:"
                                    + e.getMessage(), null);
        }
        if (ivs!=null) {
            Execution exec = Executions.getCurrent();
            List wvList = (List)exec.getAttribute(WRONG_VALUE_KEY);
            if(wvList==null){
                wvList = (List)Collections.synchronizedList(new LinkedList());
                exec.setAttribute(WRONG_VALUE_KEY, wvList);
            }
            for(int i=0;i<ivs.length;i++){
                wvList.add(new WrongValueException(comp,ivs[i].getMessage()));
            }
        }
    }
    
    /**
     * Validate input when onBindingValidate,
     * if there are any WrongValueExecption was crated when validate(),
     * then a WrongValueException will be throw out. 
     * @param event
     * @throws WrongValueException
     */
    static  public void afterValidate(Event event) throws WrongValueException{
        Execution exec = Executions.getCurrent();
        List wvList = (List)exec.getAttribute(WRONG_VALUE_KEY);
        if(wvList==null) return;
        exec.removeAttribute(WRONG_VALUE_KEY);
        if(wvList.size()>0) throw (WrongValueException)wvList.get(0);
    }
}
