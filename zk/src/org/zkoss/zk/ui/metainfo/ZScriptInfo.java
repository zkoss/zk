/* ZScriptInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 11:23:01 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.net.URL;

import org.zkoss.util.resource.Locator;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ConditionImpl;

/**
 * Represents a zscript element.
 * @author tomyeh
 * @since 6.0.0
 */
public class ZScriptInfo extends ConditionLeafInfo {
	private final ZScript _zs;
	private boolean _deferred;

	/** Creates a {@link ZScriptInfo} with the content directly.
	 *
	 * @param zslang the scripting language. If null, it is the same as
	 * {@link Page#getZScriptLanguage}.
	 * @param content the zscript content
	 */
	public ZScriptInfo(NodeInfo parent, String zslang, String content, ConditionImpl cond) {
		super(parent, cond);
		_zs = new ZScript(zslang, content);
	}
	/** Create a {@link ZScriptInfo} with an URL that is used to load the content.
	 *
	 * @param url the URL to load the content of zscript.
	 */
	public ZScriptInfo(NodeInfo parent, String zslang, URL url, ConditionImpl cond) {
		super(parent, cond);
		_zs = new ZScript(zslang, url);
	}
	/** Constructs a {@link ZScriptInfo} with an URL, which might contain an EL
	 * expression.
	 *
	 * @param url the URL. It may contain XEL expressions.
	 * @param locator the locator used to locate the zscript file
	 */
	public ZScriptInfo(NodeInfo parent, String zslang, String url, Locator locator, ConditionImpl cond) {
		super(parent, cond);
		_zs = new ZScript(parent.getEvaluatorRef(), zslang, url, locator);
	}

	/** Returns the {@link ZScript} object hold in this info.
	 */
	public ZScript getZScript() {
		_zs.setEvaluatorRef(_evalr); //in case that this info has been moved
		return _zs;
	}
	/** Returns the scripting language, or null if the default scripting language
	 * is preferred.
	 */
	public String getLanguage() {
		return _zs.getLanguage();
	}
	/** Sets the scripting language.
	 *
	 * @param zslang the scripting language. If null, the default scripting
	 * language is assume.
	 */
	public void setLanguage(String zslang) {
		_zs.setLanguage(zslang);
	}

	/** Returns the raw content.
	 * It is the content specified in the contructor
	 * ({@link #ZScriptInfo(NodeInfo, String, String, ConditionImpl)}.
	 * If URL is specified in the contructor, null is returned.
	 *
	 * <p>On the other hand, {@link #getContent} will load the content
	 * automatically if URL is specified.
	 *
	 * @return the raw content specified in the contructor, or null
	 * if URL is specified instead.
	 */
	public String getRawContent() {
		return _zs.getRawContent();
	}
	/** Returns the content of zscript.
	 * If URL is specified, this method loads the content from it.
	 * If URL is an EL expression, it will be evaluated first.
	 *
	 * <p>Note: before evaluating the returned script, you have to invoke
	 * {@link #isEffective(Component)} or {@link #isEffective(Page)} first.
	 *
	 * @param page the page when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScriptInfo(NodeInfo, String, String, Locator, ConditionImpl)}.
	 * @param comp the component when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScriptInfo(NodeInfo, String, String, Locator, ConditionImpl)}.
	 * @exception org.zkoss.zk.ui.UiException if faied to load the content
	 */
	public String getContent(Page page, Component comp) {
		_zs.setEvaluatorRef(_evalr); //in case that this info has been moved
		return _zs.getContent(page, comp);
	}

	/** Returns whether the evaluation of the zscript shall be deferred.
	 */
	public boolean isDeferred() {
		return _deferred;
	}
	/** Sets whether the evaluation of the zscript shall be deferred.
	 * <p>Default: false.
	 */
	public void setDeferred(boolean deferred) {
		_deferred = deferred;
	}

	//Object//
	public String toString() {
		return "[zscript: " + _zs + ']';
	}
}
