package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class F70_ZK_2076 extends SelectorComposer<Window> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Wire
    Textbox t1;
    @Wire
    Textbox t2;
     
    @Listen("onClick=#b1")
    public void b1() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new Event("b1"));
    }
    
    @Listen("onClick=#b2")
    public void b2() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new Event("b2", null, new Object[]{2, "test"}));
    }
    
    @Listen("onClick=#b3")
    public void b3() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new Event("b3", null, new Object[]{3, "test"}));
    }
    
    @Listen("onClick=#b4")
    public void b4() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new Event("b4", null, new Object[]{4, "test"}));
    }
    
    @Listen("onClick=#b5")
    public void b5() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new Event("b5", null, new Object[]{5, "test"}));
    }
    
    @Listen("onClick=#b6")
    public void b6() {
    	EventQueues.lookup("test", EventQueues.DESKTOP, true).publish(new CustomEvent("b6", null, "test6"));
    }
    
    @Listen("onClick=#clean")
    public void clean() {
        t1.setValue("");
        t2.setValue("");
    }
    
    @Subscribe("test")
    public void b1Test() {
    	t1.setValue("test1");
    }
    
    @Subscribe(value = "test", scope=EventQueues.DESKTOP, eventName="b2")
    public void b2Test(Event event) {
    	Object[] objs = (Object[]) event.getData();
    	t2.setValue(objs[1] + "" + objs[0]);
    }
    
    @Subscribe(value = "test", eventName="b3")
    public void b3Test(int i, String s) {
    	t2.setValue(s + i);
    }
    
    @Subscribe(value = "test", eventName="b4")
    public void b4Test(Event event, int i, String s) {
    	Object[] objs = (Object[]) event.getData();
    	t2.setValue(s + objs[0]);
    }
    
    @Subscribe(value = "test", eventName="fake")
    public void b5Test() {
    	t2.setValue("test5");
    }
    
    @Subscribe(value = "test", eventName="b6")
    public void b6Test(CustomEvent event) {
    	t2.setValue(event.getData().toString());
    }
    
    public class CustomEvent extends Event {
		public CustomEvent(String name, Component target, Object data) {
			super(name, target, data);
		}
	}
}
