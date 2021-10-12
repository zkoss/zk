/** B70_ZK_2754_Composer1.java.

	Purpose:
		
	Description:
		
	History:
		4:03:57 PM Jun 9, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 *
 */
@SuppressWarnings("serial")
public class B70_ZK_2754_Composer1 extends SelectorComposer<Component> {

    private EventQueue<Event> queue1;
    private EventQueue<Event> queue2;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        queue1 = EventQueues.lookup(B70_ZK_2754_Composer.EQ_NAME_1, B70_ZK_2754_Composer.QUEUE_SCOPE, false);
        queue2 = EventQueues.lookup(B70_ZK_2754_Composer.EQ_NAME_2, B70_ZK_2754_Composer.QUEUE_SCOPE, false);
    }
    @Listen("onClick = #p1")
    public void doPublish1() {
        queue1.publish(new Event("to queue1"));
    }
    
    @Listen("onClick = #p2")
    public void doPublish2() {
        queue2.publish(new Event("to queue2"));
    }
    
    @Listen("onClick = #both")
    public void doPublishBoth() {
        queue1.publish(new Event("to queue1"));
        queue2.publish(new Event("to queue2"));     
    }
}
