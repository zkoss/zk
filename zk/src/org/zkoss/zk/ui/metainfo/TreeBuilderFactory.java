/** TreeBuilderFactory.java.

	Purpose:
		
	Description:
		
	History:
		4:49:47 PM Sep 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.UiException;

/**
 * A tree builder factory to load a parser dynamically
 * @author jumperchen
 * @since 8.0.0
 */
public class TreeBuilderFactory {
	/**
	 * Creates a tree builder depended on its file extension.
	 * <p> If the file extension is missing in the {@link LanguageDefinition}, and the class of 
	 * {@link XmlTreeBuilder} is assumed.
	 */
	public static TreeBuilder makeBuilder(String extension) {
		LanguageDefinition ldf = LanguageDefinition.getByExtension(extension);
		String treeBuilderClass = ldf.getTreeBuilderClass();
		if (treeBuilderClass == null || "org.zkoss.zk.ui.metainfo.XulTreeBuilder".equals(treeBuilderClass))
			return new XmlTreeBuilder();
		try {
			return (TreeBuilder) Classes.newInstance(Classes.forNameByThread(treeBuilderClass), null);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
