/** F80_ZK_3157_Composer.java.

	Purpose:
		
	Description:
		
	History:
 		Wed Apr 6 14:14:32 CST 2016, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jameschu
 *
 */
@SuppressWarnings("serial")
public class F80_ZK_3157_Composer extends SelectorComposer<Component> {

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }
    
    @Listen("onClick = #set1")
    public void doSet1() {
		Library.setProperty("3157", "set1");
		Clients.log(Library.getProperties("3157").toString());
	}
	@Listen("onClick = #add1")
	public void doAdd1() {
		Library.addProperty("3157", "add1");
		Clients.log(Library.getProperties("3157").toString());
	}
	@Listen("onClick = #set2")
	public void doSet2() {
		List<String> properties = new LinkedList<String>();
		properties.add("set2-01");
		properties.add("set2-02");
		Library.setProperties("3157", properties);
		Clients.log(Library.getProperties("3157").toString());
	}
	@Listen("onClick = #add2")
	public void doAdd2() {
		List<String> properties = new LinkedList<String>();
		properties.add("add2-01");
		properties.add("add2-02");
		Library.addProperties("3157", properties);
		Clients.log(Library.getProperties("3157").toString());
	}
}
