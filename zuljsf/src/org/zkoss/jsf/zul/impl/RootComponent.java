/* RootComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2007 5:56:49 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zul.Label;

/**
 * A skeletal class to implement the root ZK Component.
 * Currently, only the page component ({@link org.zkoss.jsf.zul.Page})
 * extends from this class.
 * 
 * @author Dennis.Chen
 */
public class RootComponent extends AbstractComponent{
	private static final Log log = Log.lookup(RootComponent.class);
	private LanguageDefinition _langdef;
	private org.zkoss.zk.ui.Page _page;
	private String _lang = "Java";
	private ComponentInfo _componentInfo;
	private boolean _nested;
	
	
	// these two field must follow the value of ZkFuns
	/** Denotes whether style sheets are generated for this request. */
	private static final String ATTR_LANG_CSS_GENED
		= "javax.zkoss.zk.lang.css.generated";
	/** Denotes whether JavaScripts are generated for this request. */
	private static final String ATTR_LANG_JS_GENED
		= "javax.zkoss.zk.lang.js.generated";
	
	/**
	 * protected Constructor. Construct a RootTag with
	 * LanguageDefinition =  "xul/html".
	 */
	protected RootComponent() {
		_langdef = LanguageDefinition.lookup("xul/html");
	}

	/** Adds a child ZUL Component.
	 */
	/*package*/ void addChildZULComponent(LeafComponent child) {
		child.getZULComponent().setPage(_page);
	}
	/** Returns the default scripting language.
	 */
	public String getZScriptLanguage() {
		return _lang;
	}
	/**
	 * Sets the default scripting language in this RootComponent.
	 *
	 * <p>Default: Java.
	 *
	 * @param lang the name of the scripting language, such as
	 * Java, Ruby and Groovy.
	 */
	public void setZScriptLanguage(String lang) {
		_lang = lang != null ? lang: "Java";
	}
	/** 
	 * Sets the default scripting language in this RootComponent.
	 * It is the same as {@link #setZScriptLanguage} (used to simplify
	 * the typing in JSF page).
	 */
	public void setZscriptLanguage(String lang) {
		setZScriptLanguage(lang);
	}
	
	/**
	 * Get ComponentInfo for current Component Tree.<br/>
	 * it check a existed instance and return it, if not, a new instance will be created and return.</br>
	 */
	protected ComponentInfo getComponentInfo(){
		if(_componentInfo==null){
			_componentInfo = new ComponentInfo();
		}
		return _componentInfo;
	}

	/** Initializes the page.
	 * It is called after the ZUL Page is created, and
	 * before any ZUL Component is created.
	 *
	 * <p>Default: does nothing
	 *
	 * @param exec the execution.
	 * Note: when this method is called, the execution is not activated.
	 * For example, Executions.getCurrent() returns null.
	 * @param page the page
	 */
	protected void init(Execution exec, org.zkoss.zk.ui.Page page) {
	}
	

	/**
	 * A Richlet class to handle build ZUL Component Tree.
	 * @author Dennis.Chen
	 *
	 */
	private class MyRichlet implements Richlet {
		FacesContext fctx;
		
		public MyRichlet(FacesContext fctx){
			this.fctx = fctx;
		}
		public void init(RichletConfig config) {
		}
		public void destroy() {
		}
		public void service(org.zkoss.zk.ui.Page page) {
			Initiators inits   = 
				(Initiators) getFacesContext().getExternalContext().getRequestMap().get(Initiators.class.getName());
			if(inits!=null)inits.doInit(page);
			try {
				//load children
				ComponentInfo ci = getComponentInfo();
				List children = ci.getChildrenInfo(RootComponent.this);
				if(children!=null){
					StringWriter writer = new StringWriter();
					writer.write(getBodyContent());
					for (Iterator kids = children.iterator(); kids.hasNext(); ){
						AbstractComponent kid = (AbstractComponent) kids.next();
			            kid.loadZULTree(page,writer);
			        }
					if(inits!=null)inits.doAfterCompose(page);
					Utils.adjustChildren(
							page, RootComponent.this, ci.getChildrenInfo(RootComponent.this), writer.toString()/*new String(bos.toString("UTF-8"))*/);
					
					
					//a bug? if last child of page is inline, then Messagebox.show will cause error.
					//so, add a unvisible label to work around this.
					Label junk = new Label();
					junk.setVisible(false);
					
					//java.lang.UnsupportedOperationException
					//at java.util.Collections$UnmodifiableCollection.add(Collections.java:1018)
					//page.getRoots().add(junk);
					
					junk.setPage(page);
					
					
				}else{
					//bug #1832862 Content disappear in JSFComponent
					Utils.adjustChildren(
						page, RootComponent.this, new ArrayList(), getBodyContent());
				}
				setBodyContent(null);//clear it;
			} catch (Exception ex) {
				log.realCauseBriefly(ex); 
				if(inits!=null)inits.doCatch(ex);
				throw UiException.Aide.wrap(ex);
			}finally{
				if(inits!=null)inits.doFinally();
			}
		}
		public LanguageDefinition getLanguageDefinition() {
			return _langdef;
		}
	}
	/**
	 * RootComponent was supposed to handle all children's ZScript.
	 * @param parent  The owner of zscript segment. 
	 * @param zs A ZScript object.
	 * @throws IOException  
	 */
	public void processZScript(Component parent, ZScript zs) throws IOException
	{
		if (zs.getLanguage() == null){
			zs.setLanguage(_page.getZScriptLanguage());
		}
		if(zs.isDeferred()){
			((PageCtrl)_page).addDeferredZScript(parent, zs);
		}else{
			final Map backup = new HashMap();
			final Namespace ns = parent != null ?
				Namespaces.beforeInterpret(backup, parent, false):
				Namespaces.beforeInterpret(backup, _page, false);
			try {
				_page.interpret(zs.getLanguage(),zs.getContent(_page,parent), ns);
			} finally {
				Namespaces.afterInterpret(backup, ns, false);
			}	
		}
	}


	public void encodeBegin(FacesContext context) throws IOException {
		final ExternalContext exc = context.getExternalContext();
		final HttpServletRequest request =
			(HttpServletRequest)exc.getRequest();
		
		final AbstractComponent ac =
			(AbstractComponent)findAncestorWithClass(this, AbstractComponent.class);
		
		if(ac!=null){
			_nested = true;
		}else{
			_nested = false;
			//skip the other zul to generate js or css which is not controlled by this page,
			//such as <jsp:include page="other.zul"/> in a page.
			WebManager.setRequestLocal(request, ATTR_LANG_JS_GENED, Boolean.TRUE);
			WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, Boolean.TRUE);
		}
	}
	
	/**
	 * Override Method, 
	 * When encodeEnd in RootComponent, all it's children ZULJSF Component has encoded,
	 * then we start initial the ZK environment, and initial ZUL component by calling {@link AbstractComponent#loadZULTree} of each children under RootComponent.
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		
		//for providing page initial and render
		final WebApp _wapp;
		final WebAppCtrl _wappc;
		final Execution _exec;
		final Richlet _richlet;
		
		

		final ExternalContext exc = context.getExternalContext();
		final ServletContext svlctx = (ServletContext)exc.getContext();
		final HttpServletRequest request =
			(HttpServletRequest)exc.getRequest();
		final HttpServletResponse response =
			(HttpServletResponse)exc.getResponse();
		
		final WebManager webman = WebManager.getWebManager(svlctx);
		final Session sess = WebManager.getSession(svlctx, request);
		
		if(!_nested){
			WebManager.setRequestLocal(request, ATTR_LANG_JS_GENED, null);
			WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, null);
		}
		

		//TODO check push
		//RequestContexts.push(pgctx);
		SessionsCtrl.setCurrent(sess);
		try {
			_wapp = sess.getWebApp();
			_wappc = (WebAppCtrl)_wapp;

			//final Desktop desktop = webman.getDesktop(sess, request, null, true);
			final Desktop desktop = webman.getDesktop(sess, request,response, null, true);
			final RequestInfo ri = new RequestInfoImpl(
				_wapp, sess, desktop, request,
				PageDefinitions.getLocator(_wapp, null));
			((SessionCtrl)sess).notifyClientRequest(true);

			final UiFactory uf = _wappc.getUiFactory();
			_richlet = new MyRichlet(context);
			_page = uf.newPage(ri, _richlet, null);
			if(_lang!=null)_page.setZScriptLanguage(_lang);

			_exec = new ExecutionImpl(
				svlctx, request, response, desktop, _page);
			_exec.setAttribute(
				PageCtrl.ATTR_REDRAW_BY_INCLUDE, Boolean.TRUE);
				//Always use include; not forward
			
			init(_exec, _page); //initialize the page
			_wappc.getUiEngine().execNewPage(_exec, _richlet, _page, context.getResponseWriter());
		} finally {
			SessionsCtrl.setCurrent(null);
			//RequestContexts.pop();
		}
	}
	
	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = _lang;
		return (values);
	}
	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_lang = ((String) values[1]);
	}
	
	
	
	
}
