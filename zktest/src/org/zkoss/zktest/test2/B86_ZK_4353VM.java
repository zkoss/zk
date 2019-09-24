/* B86_ZK_4353VM.java

		Purpose:
		
		Description:
		
		History:
				Mon Aug 05 17:44:42 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.WebApp;

import java.io.File;
import java.io.IOException;

public class B86_ZK_4353VM {
	private AImage image;
	
	@Init
	public void init(@ContextParam(ContextType.APPLICATION) WebApp webapp) throws IOException {
		image = new AImage(new File(webapp.getRealPath("test2/img/B86-ZK-4353-zklogo3.png")));
	}
	
	public AImage getImage() {
		return image;
	}
}
