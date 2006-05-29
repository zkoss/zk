/* PageDefinitions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 12:34:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.net.URL;

import com.potix.lang.D;
import com.potix.util.resource.Locator;
import com.potix.idom.Document;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.UiException;

/**
 * Utilities to retrieve page definitions.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/29 04:28:08 $
 */
public class PageDefinitions {
	/** Returns the page definition of the specified raw content; never null.
	 *
	 * @param extension the default extension if the content doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(Locator locator,
	String content, String extension) {
		try {
			return getPageDefinitionDirectly(locator,
				new StringReader(content), extension);
		} catch (IOException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Returns the page definition of the raw content from the specified
	 * reader; never null.
	 *
	 * @param extension the default extension if the content (of reader) doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(Locator locator,
	Reader reader, String extension) throws IOException {
		try {
			return new Parser(locator).parse(reader, extension);
		} catch (IOException ex) {
			throw (IOException)ex;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Returns the page definition of the specified raw content in DOM;
	 * never null.
	 *
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If doc doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(Locator locator,
	Document doc, String extension) {
		try {
			return new Parser(locator).parse(doc, extension);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
