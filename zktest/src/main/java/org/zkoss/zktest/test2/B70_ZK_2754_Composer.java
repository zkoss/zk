/** B70_ZK_2754_Composer.java.

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
public class B70_ZK_2754_Composer extends SelectorComposer<Component> {

    // public static final String EQ = EventQueues.SESSION;
    public static final String QUEUE_SCOPE = EventQueues.GROUP;
    public static final String EQ_NAME_1 = "eq1";
    public static final String EQ_NAME_2 = "eq2";
    
    private EventQueue<Event> queue1;
    private EventQueue<Event> queue2;

    @Wire
    private Label label1;

    @Wire
    private Label label2;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        queue1 = EventQueues.lookup(EQ_NAME_1, QUEUE_SCOPE, true);
        queue1.subscribe( 
            new EventListener<Event>() {
                public void onEvent(Event ev) {
                    label1.setValue(label1.getValue() + "[" + ev.getName() + "]");
                }
            });
        queue2 = EventQueues.lookup(EQ_NAME_2, QUEUE_SCOPE, true);
        queue2.subscribe( 
            new EventListener<Event>() {
                public void onEvent(Event ev) {
                    label2.setValue(label2.getValue() + "[" + ev.getName() + "]");
                }
            });
    }
    
    @Listen("onClick = #publish1")
    public void doPublish1() {
        queue1.publish(new Event("to queue1"));
    }
    
    @Listen("onClick = #publish2")
    public void doPublish2() {
        queue2.publish(new Event("to queue2"));
    }
}
