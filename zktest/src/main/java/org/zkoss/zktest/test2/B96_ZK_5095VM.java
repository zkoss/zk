package org.zkoss.zktest.test2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;

public class B96_ZK_5095VM implements Serializable {

	public B96_ZK_5095VM() {
		//print debugging info of NotSerializableException
		System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
	}

	@Command
	public void write(@ContextParam(ContextType.COMPONENT) Component component) throws IOException {
		FileOutputStream f = new FileOutputStream(new File("myObjects.txt"));
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(Sessions.getCurrent());
		o.writeObject(component.getAttribute("$composer", true));
		o.close();
	}
}
