/** ZhtmlVM.java.

	Purpose:
		
	Description:
		
	History:
		2:10:02 PM Feb 24, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

/**
 * @author jameschu
 *
 */
public class ZhtmlVM {
	private String templateURI;

	public String getTemplateURI() {
		return templateURI;
	}

	@Init
	public void init() {
		templateURI = "../include/zhtmlApply1.zhtml";
	}
	

	@NotifyChange("templateURI")
	@Command
	public void changeTemplate() {
		this.templateURI = "../include/zhtmlApply2.zhtml";
	}
	
}
