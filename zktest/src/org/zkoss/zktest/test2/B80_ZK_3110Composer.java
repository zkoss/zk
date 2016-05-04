/* B80_ZK_3110Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu, Apr 28, 2016  6:20:45 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3110Composer extends SelectorComposer<Div>{
	
	@Wire
	private Div tagList;
	private ListModelList<String> tagsModel = new ListModelList<>();
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		tagsModel.add("ajax");
		tagsModel.add("new");
		tagsModel.add("framework");
		
		CollectionTemplate tagItems = new CollectionTemplate(true);
		tagItems.setTemplateResolver(new CollectionTemplateResolver<String>() {
			@Override
			public Template resolve(String arg0) {
				return tagList.getTemplate(arg0.contains("new") ? "tag1" : "tag2");
			}
		});
		tagItems.setModel(tagsModel);
		tagItems.apply(tagList);
	}
	
}