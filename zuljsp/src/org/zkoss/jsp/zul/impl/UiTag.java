/**
 * 
 */
package org.zkoss.jsp.zul.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.zkoss.jsp.zul.ComponentDefinitionTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;

/**
 * {@link UiTag} is used to represent the dynamic tagging feature that ZK support but Jsp spec does not.<br>
 * The purpose of {@link UiTag} is to declare a component instance that user defined in language add-on or current Jsp page.<br>
 * About how to declare a custom component definition in Jsp page, please take a look at: {@link ComponentDefinitionTag}<br>
 * <br>
 * For example:<br>
 * <code>
 * &lt;z:ui tag="mywindow" title="A demo window"/>
 * </code><br>
 * It same as the declaration below in ZK's zul page:<br>
 * <code>
 * &lt;mywindow  title="A demo window"/>
 * </code>
 * @author Ian Tsai
 * 
 */
public class UiTag extends BranchTag {

	
	private String _tag;
	private ComponentDefinition _compDef;
	private Component[] _comps;
	
	protected String getJspTagName(){
		throw new UnsupportedOperationException(
				"this method: Component newComponent(Class) is not supported in:"+this.getClass());
	}
	/**
	 * 
	 * @return the tag that this {@link UiTag} use.
	 */
	public String getTag() {
		return _tag;
	}
	/**
	 * Set the information of component definition about what component this {@link UiTag} should create  
	 * while component creation phase.
	 * @param tag .
	 */
	public void setTag(String tag) {
		this._tag = tag;
		
	}

	/* */
	void initComponent() throws JspException  {
		if(_roottag==null)
			throw new IllegalStateException("Must be nested inside the page tag: "+this);	
		
		Page page = this._roottag.getPage();
		_compDef = page.getComponentDefinition(_tag, true);
		if(_compDef==null)
			throw new JspException("can't find this Component's definition:"+_tag);

		composeHandle = new ComposerHandler(_attrMap.remove("apply"));
		try {//TODO: use-class initial works...
			//add composer to intercept creation...
			//TODO: composerExt.doBeforeCompose(page, parentComponent, compInfo); 
			Object useClass = _compDef.getImplementationClass();
			
			if (_compDef.isInlineMacro()) {// the tag holds multiple components.
				final Map props = new HashMap();
				Component parent = this._parenttag.getComponent();
				props.put("includer", parent);
				
				_compDef.evalProperties(props, page, parent);
				props.putAll(_attrMap);
				if(this.getUse()!=null)props.put("use", this.getUse());
				_comps = parent.getDesktop().getExecution().
					createComponents(_compDef.getMacroURI(), props);
			}
			else {// the tag hold only one component. 
				String clazzName = null;
				
				if(super.getUse()!=null)
					clazzName = getUse();
				else if(useClass instanceof String)
					clazzName = (String)useClass;
				else
					clazzName = ((Class)useClass).getName();

				_comps = new Component []{_comp=_compDef.newInstance(page, clazzName)};
				
				composeHandle.doBeforeComposeChildren(_comp);
				_comp.getDefinition().applyProperties(_comp);
				
			}
		} catch (Exception e) {
			if(!_compDef.isInlineMacro())
				composeHandle.doCatch(e);
			throw new JspException(e);
		}
		finally
		{
			if(!_compDef.isInlineMacro())
				composeHandle.doFinally();
		}
		//append this tag to parent or root.
		if (_parenttag != null)_parenttag.addChildTag(this);
		else _roottag.addChildTag(this);

	}
	
	/*package */ void afterComposeComponent() throws JspException{
		if(_compDef.isInlineMacro()){// the comps are already initialized by ZK.  
			if ( _comps==null)
				throw new JspTagException("newComponent() returns null");
			_parenttag.addChildTag(this);
		}
		else
			super.afterComposeComponent();
	}

	public boolean isInline() {
		return _compDef.isInlineMacro();
	}

	public Component[] getComponents() {
		return _comps;
	}
	

}//end of class...
