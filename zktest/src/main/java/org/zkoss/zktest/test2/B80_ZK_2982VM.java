/* B80_ZK_2982VM.java

	Purpose:
		
	Description:
		
	History:
		2:29 PM 11/23/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2982VM implements Serializable {
	@Command
	public void serialize(@ContextParam(ContextType.VIEW) Div div)
			throws IOException, ClassNotFoundException {
		Page pg = div.getPage();
		((ComponentCtrl)div).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(div);
		oos.close();
		oaos.close();
		byte[] bytes = oaos.toByteArray();


		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);

		Div newdiv = (Div) ois.readObject();
		div.detach();
		bais.close();
		ois.close();
		newdiv.setPage(pg);
		((Label)newdiv.getFirstChild()).setValue("Serialized!!");
		((ComponentCtrl)newdiv).sessionDidActivate(newdiv.getPage());//simulate
	}

	@Command
	public void doEcho(@BindingParam("msg") String msg) {
		Clients.log("chicked " + msg);
	}
}
