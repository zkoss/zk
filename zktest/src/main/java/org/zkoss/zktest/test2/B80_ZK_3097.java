/* B80_ZK_3097.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 12:17:57 CST 2016, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zhtml.Div;
import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @author wenning
 */
public class B80_ZK_3097 extends SelectorComposer {

    @Wire
    Div div;

    byte[] _bytes;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        ByteArrayOutputStream oaos1 = new ByteArrayOutputStream();
        ObjectOutputStream oOut = new ObjectOutputStream(oaos1);
        oOut.writeObject(div);
        oOut.close();
        oaos1.close();
        _bytes = oaos1.toByteArray();

        ByteArrayInputStream oaos2 = new ByteArrayInputStream(_bytes);
        ObjectInputStream oIn = new ObjectInputStream(oaos2);
        AbstractTag abt = (Div) oIn.readObject();
        oIn.close();
        oaos2.close();
    }

}
