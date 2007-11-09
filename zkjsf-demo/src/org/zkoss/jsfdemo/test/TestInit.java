/* TestInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 7, 2007 6:06:18 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import java.util.Iterator;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author Dennis.Chen
 *
 */
public class TestInit implements Initiator{
	
	Window ww ;
	Label arg0;
	Label arg1;
	Label afco;
	Label doca;
	Label fina;
	
	String str1;
	String str2;

	public void doAfterCompose(Page page) throws Exception {
		Iterator iter = page.getRoots().iterator();
		for(;iter.hasNext();){
			Object obj = iter.next();
			if(obj instanceof Window && ((Window)obj).getId().equals("ww")){
				ww = (Window)obj;
			}
		}
		arg0 = (Label)ww.getFellow("arg0");
		arg1 = (Label)ww.getFellow("arg1");
		afco = (Label)ww.getFellow("afco");
		doca = (Label)ww.getFellow("doca");
		fina = (Label)ww.getFellow("fina");
		
		arg0.setValue(str1);
		arg1.setValue(str2);
		
		afco.setValue("Checked");
		
	}

	public boolean doCatch(Throwable arg0) throws Exception {
		doca.setValue("Checked");
		return false;
	}

	public void doFinally() throws Exception {
		fina.setValue("Checked");
	}

	public void doInit(Page page, Object[] args) throws Exception {
		str1 = (String)args[0];
		str2 = (String)args[1];
	}

}
