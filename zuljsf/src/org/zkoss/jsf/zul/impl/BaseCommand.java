/* BaseCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Some code of this file is refer to Apache License Version 2.0
the license file is http://www.apache.org/licenses/LICENSE-2.0 
 
Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.util.Map;

import javax.faces.component.ActionSource;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.zkoss.jsf.zul.impl.BranchComponent;

/**
 * The Base implementation of ActionSource. 
 * 
 * @author Dennis.Chen
 * 
 */
public abstract class BaseCommand extends BranchComponent implements ActionSource {

	
	//=== code reference from my faces ====
	private MethodBinding _action = null;

	private MethodBinding _actionListener = null;

	public void setAction(MethodBinding action) {
		_action = action;
	}

	public MethodBinding getAction() {
		return _action;
	}

	public void setActionListener(MethodBinding actionListener) {
		_actionListener = actionListener;
	}

	public MethodBinding getActionListener() {
		return _actionListener;
	}

	public void addActionListener(ActionListener listener) {
		addFacesListener(listener);
	}

	public ActionListener[] getActionListeners() {
		return (ActionListener[]) getFacesListeners(ActionListener.class);
	}

	public void removeActionListener(ActionListener listener) {
		removeFacesListener(listener);
	}

	public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);

		if (event instanceof ActionEvent) {
			FacesContext context = getFacesContext();

			MethodBinding actionListenerBinding = getActionListener();
			if (actionListenerBinding != null) {
				try {
					actionListenerBinding.invoke(context, new Object[] { event });
				} catch (EvaluationException e) {
					Throwable cause = e.getCause();
					if (cause != null && cause instanceof AbortProcessingException) {
						throw (AbortProcessingException) cause;
					} else {
						throw e;
					}
				}
			}

			ActionListener defaultActionListener = context.getApplication().getActionListener();
			if (defaultActionListener != null) {
				defaultActionListener.processAction((ActionEvent) event);
			}
		}
	}

	public void queueEvent(FacesEvent event) {
		if (event != null && this == event.getSource() && event instanceof ActionEvent) {
			if (isImmediate()) {
				event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
			} else {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			}
		}
		super.queueEvent(event);
	}
	
	private static final boolean DEFAULT_IMMEDIATE = false;

    private Boolean _immediate = null;

    public void setImmediate(boolean immediate)
    {
        _immediate = Boolean.valueOf(immediate);
    }

    public boolean isImmediate()
    {
        if (_immediate != null) return _immediate.booleanValue();
        ValueBinding vb = getValueBinding("immediate");
        Boolean v = vb != null ? (Boolean)vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_IMMEDIATE;
    }

    public Object saveState(FacesContext context)
    {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = saveAttachedState(context, _action);
        values[2] = saveAttachedState(context, _actionListener);
        values[3] = _immediate;
        return values;
    }

    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _action = (MethodBinding)restoreAttachedState(context, values[1]);
        _actionListener = (MethodBinding)restoreAttachedState(context, values[2]);
        _immediate = (Boolean)values[3];
    }
    
    
    //============ ZK implementation==================
    
    private static final String PARM_POSTFIX = "_cmd";
    
    protected String getJSSubmitMethodName(FacesContext context){
    	String scriptId = getClientId(context).replaceAll(":","_");
    	String submitmethod="submit_"+scriptId;
    	return submitmethod;
    }

	protected String getJSSubmitScript(FacesContext context){
		UIForm form = getUIForm();
    	String scriptId = getClientId(context).replaceAll(":","_");
    	String formScriptId = form.getClientId(context).replaceAll(":","_");
    	
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("function "+getJSSubmitMethodName(context)+"(){");
		sb.append("var cid = '"+scriptId+PARM_POSTFIX+"';");
		sb.append("var form = document.getElementById('"+formScriptId+"');");
		sb.append("var el = document.createElement('input');");
		sb.append("el.id = cid;");
		sb.append("el.name = cid;");
		sb.append("el.type = 'hidden';");
		sb.append("el.value = 't';");
		sb.append("form.appendChild(el);");
		sb.append("form.submit();");
		sb.append("}");
		sb.append("</script>\n");
		
		return sb.toString();
	}

    /**
     * check that is there any submitting info in request.
     */
	private boolean isSubmitted(FacesContext facesContext) {
        String scriptId = getClientId(facesContext).replaceAll(":","_");
        Map paramMap = facesContext.getExternalContext().getRequestParameterMap();
        return paramMap.containsKey(scriptId+PARM_POSTFIX);
    }
	
	/**
	 * @return return true if there are any action or actionListener register in this component.
	 */
	protected boolean hasListener(){
		MethodBinding action = getAction();
		MethodBinding listener = getActionListener();
		ActionListener[] als = getActionListeners();
		if(action!=null ||listener!=null || (als!=null && als.length>0)){
			return true;
		}
		return false;
	}
    
	/**
	 * Override Method, decode and queue event
	 */
    public void decode(FacesContext facesContext) {
        //super.decode must not be called, because value is handled here
    	//there is no reset type in zk component,
        if (isSubmitted(facesContext)) {
            queueEvent(new ActionEvent(this));
        }
    }
    
}
