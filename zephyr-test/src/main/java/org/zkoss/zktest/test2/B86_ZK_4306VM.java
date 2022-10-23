/* B86_ZK_4306VM.java

	Purpose:
		
	Description:
		
	History:
		Fri May 31 10:17:28 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.http.SourceMapManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author jameschu
 */
public class B86_ZK_4306VM {
	byte[] _bytes;
	@Command
	public void doSerialize() throws Exception {
		doSerialize0();
		doDeserialize0();
	}
	public void doSerialize0() throws Exception{
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(new SourceMapManager(null, null, null));
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0() throws Exception{
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);
		oos.close();
		oaos.close();
	}
}
