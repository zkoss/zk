/* RootTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 19:07:04     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp.impl;

import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.jsp.Jsps;
import org.zkoss.web.el.ELContexts;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.ExecutionImpl;

/**
 * A skeletal class to implement the root ZK tag.
 * Currently, only the page tag ({@link org.zkoss.zul.jsp.PageTag})
 * extends from this class.
 *
 * <p>The derive may override {@link #init} to initialize the
 * page.
 *
 * @author tomyeh
 */
abstract public class RootTag extends AbstractTag {
	private static final Log log = Log.lookup(RootTag.class);
	private LanguageDefinition _langdef;
	private Page _page;
	private String _lang = "Java";

	/**
	 * protected Constractor. Constract a RootTag with
	 * LanguageDefinition =  "xul/html".
	 *
	 */
	protected RootTag() {
		_langdef = LanguageDefinition.lookup("xul/html");
	}

	/** Adds a child tag.
	 */
	/*package*/ void addChildTag(LeafTag child) {
		child.getComponent().setPage(_page);
	}
	/** Returns the default scripting language.
	 */
	public String getZScriptLanguage() {
		return _lang;
	}
	/**
	 * Sets the defult scripting language in this Root tag.
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
	 * Sets the defult scripting language in this Root tag.
	 * It is the same as {@link #setZScriptLanguage} (used to simplify
	 * the typing in JSP page).
	 */
	public void setZscriptLanguage(String lang) {
		setZScriptLanguage(lang);
	}

	//Derived to override//
	/** Initializes the page.
	 * It is called after the page is created, and
	 * before any component is created.
	 *
	 * <p>Default: does nothing
	 *
	 * @param exec the execution.
	 * Note: when this method is called, the execution is not activated.
	 * For example, Executions.getCurrent() returns null.
	 * @param page the page
	 */
	protected void init(Execution exec, Page page) {
	}
	

	//super//
	/** To process this root tag.
	 * The deriving class rarely need to override this method.
	 */
	public void doTag() throws JspException, IOException {
		if (!isEffective())
			return; //nothing to do

		final AbstractTag pt =
			(AbstractTag)findAncestorWithClass(this, AbstractTag.class);
		if ((pt instanceof RootTag) || (pt instanceof BranchTag))
			throw new JspTagException("<page> can be placed inside of "+pt);

		final JspContext jspctx = getJspContext();
		final PageContext pgctx = Jsps.getPageContext(jspctx);
		final ServletContext svlctx = pgctx.getServletContext();
		final HttpServletRequest request =
			(HttpServletRequest)pgctx.getRequest();
		final HttpServletResponse response =
			(HttpServletResponse)pgctx.getResponse();

		final WebManager webman = WebManager.getWebManager(svlctx);
		final Session sess = WebManager.getSession(svlctx, request);

		ELContexts.push(pgctx);
			//Optiional but enable JSP page use DPS's TLD files
			//If we don't push, everying works fine except JSP page
			//that uses ZK JSP tags cannot use xxx.dsp.tld
		SessionsCtrl.setCurrent(sess);
		try {
			final WebApp wapp = sess.getWebApp();
			final WebAppCtrl wappc = (WebAppCtrl)wapp;

			final Desktop desktop = webman.getDesktop(sess, request, null, true);
			final RequestInfo ri = new RequestInfoImpl(
				wapp, sess, desktop, request,
				PageDefinitions.getLocator(wapp, null));
			((SessionCtrl)sess).notifyClientRequest(null);

			final UiFactory uf = wappc.getUiFactory();
			final Richlet richlet = new MyRichlet();
			_page = uf.newPage(ri, richlet, null);
			if(_lang!=null)_page.setZScriptLanguage(_lang);

			final Execution exec = new ExecutionImpl(
				svlctx, request, response, desktop, _page);
			exec.setAttribute(
				PageCtrl.ATTR_REDRAW_BY_INCLUDE, Boolean.TRUE);
				//Always use include; not forward
			
			init(exec, _page); //initialize the page

			wappc.getUiEngine().execNewPage(exec, richlet, _page, jspctx.getOut());
		} finally {
			SessionsCtrl.setCurrent(null);
			ELContexts.pop();
		}
	}

	
	private class MyRichlet implements Richlet {
		public void init(RichletConfig config) {
		}
		public void destroy() {
		}
		public void service(Page page) {
			try {
				final StringWriter out = new StringWriter();
				getJspBody().invoke(out);
				Utils.adjustChildren(
					page, null, page.getRoots(), out.toString());
			} catch (Exception ex) {
				log.realCauseBriefly(ex); //Apache Jasper Compiler eats ex
				throw UiException.Aide.wrap(ex);
			}
		}
		public LanguageDefinition getLanguageDefinition() {
			return _langdef;
		}
	}
	/**
	 * Root tag was supposed to handle all children's ZScript.
	 * @param com  The owner of zscript segment. 
	 * @param zs A ZScript object.
	 */
	public void addDefferdZscript(Component com, ZScript zs)
	{
		if (zs.getLanguage() == null)
			zs.setLanguage(
					_page.getZScriptLanguage());
		((PageCtrl)_page).addDeferredZScript(com, zs);
	}


}
