/* EventQueue.java

	Purpose:
		
	Description:
		
	History:
		Fri May  2 15:35:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * An event queue.
 * An event queue is a many-to-many 'channel' to publish events and to subscribe
 * listeners.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface EventQueue {
	/** Publishes an event the queue.
	 *
	 * <p>If this is a desktop-level event queue, this method must be called
	 * within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * <p>On the other hand, if this is an application-level event queue,
	 * it is OK to be called without the current execution.
	 *
	 * @exception IllegalStateException if this method is called
	 * not within an activated execution (such as a working thread),
	 * and this is a desktop-level event queue.
	 */
	public void publish(Event event);
	/** Subscribes a listener to this queue.
	 * It is the same as <code>subscribe(listener, false)</code>
	 * ({@link #subscribe(EventListener,boolean)}. In other words,
	 * it subscribes a synchronous listener.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * <p>Note: if this is an application-level event queue, the listener
	 * shall not access the component associated with the event
	 * {@link Event#getTarget}.
	 *
	 * <p>An event listener can be subscribed multiple times, and
	 * it will be invoked multiple times if an event is published.
	 *
	 * <p>Even if this is an application-level or session-level event queue,
	 * the listener is subscribed for the current desktop only.
	 * If you want to use the same listener for multiple desktops,
	 * you have to subscribe them separately when the corresponding
	 * execution is available.
	 */
	public void subscribe(EventListener listener);
	/** Subscribes a synchronous or asynchronous listener to this event queue.
	 * A synchronous listener works the same as a normal event listener
	 * (listeners registered to a component ({@link org.zkoss.zk.ui.Component#addEventListener}).
	 * It is executed one-by-one (no two even listener will be executed at the same time)
	 * and under an execution (i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} never null).
	 * In additions, it is allowed to manipulate the components belonging
	 * to the current execution.
	 * <p>On the other hand, an asynchronous listener is executed asynchronously.
	 * It can <i>not</i> access the components belonging to any desktop.
	 * There is no current execution ({@link org.zkoss.zk.ui.Executions#getCurrent} is null}.
	 * However, it is useful to make the application more responsive when
	 * executing a long operation. A typical use is to execute the long operation
	 * in an asynchronous listener, and then all other events can be processed
	 * concurrently. For example,
	 * <pre><code>
&lt;window title="long operation" border="true"&gt;
  &lt;zscript&gt;
  void print(String msg) {
    new Label(msg).setParent(inf);
  }
  &lt;/zscript&gt;
  &lt;button label="async long op"&gt;
    &lt;attribute name="onClick"&gt;&lt;![CDATA[
   if (EventQueues.exists("longop")) {
     print("It is busy. Please wait");
     return; //busy
   }

   EventQueue eq = EventQueues.lookup("longop"); //create a queue
   String result;

   //subscribe async listener to handle long operation
   eq.subscribe(new EventListener() {
     public void onEvent(Event evt) {
       if ("doLongOp".equals(evt.getName())) {
         org.zkoss.lang.Threads.sleep(3000); //simulate a long operation
         result = "done"; //store the result
         eq.publish(new Event("endLongOp")); //notify it is done
       }
     }
   }, true); //asynchronous

   //subscribe a normal listener to show the resul to the browser
   eq.subscribe(new EventListener() {
     public void onEvent(Event evt) {
       if ("endLongOp".equals(evt.getName())) {
   	     print(result); //show the result to the browser
   	     EventQueues.remove("longop");
   	   }
   	 }
   }); //synchronous

   print("Wait for 3 seconds");
   eq.publish(new Event("doLongOp")); //kick off the long operation
    ]]&gt;&lt;/attribute&gt;
  &lt;/button&gt;
  &lt;vbox id="inf"/&gt;
&lt;/window&gt;
</code></pre>
	 * <p>The asynchornous event listener requires Server Push which
	 * is available in ZK PE or EE, or you have to configure your own
	 * implementation.
	 * <p>If you want to show a busy message to cover a portion of the desktop,
	 * use {@link org.zkoss.zk.ui.util.Clients#showBusy(org.zkoss.zk.ui.Component,String,boolean)}
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 * <p>An event listener can be subscribed multiple times, and
	 * it will be invoked multiple times if an event is published.
	 *
	 * <p>Even if this is an application-level or session-level event queue,
	 * the listener is subscribed for the current desktop only.
	 * If you want to use the same listener for multiple desktops,
	 * you have to subscribe them separately when the corresponding
	 * execution is available.
	 */
	public void subscribe(EventListener listner, boolean async);
	/** Unsubscribes a listener from the queue.
	 *
	 * <p>Note: this method must be called within an activated exection,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.
	 *
	 * <p>Notice that this method only unsubscribes the listener
	 * subscribed for this desktop. It doesn't check the listeners
	 * for other desktops even if this is an application-level or
	 * session-level event queue.
	 *
	 * @return true if the listener was subscribed.
	 */
	public boolean unsubscribe(EventListener listener);

	/** Returns if an event listener is subscribed.
	 * <p>Notice that this method only checks the listeners
	 * subscribed for this desktop. It doesn't check the listeners
	 * for other desktops even if this is an application-level or
	 * session-level event queue.
	 */
	public boolean isSubscribed(EventListener listener);
	/** Closes the event queue.
	 * After closed, application cannot access any of its method.
	 * <p>Don't call this method directly. It is called only internally.
	 * Rather, use {@link EventQueues#remove} instead.
	 */
	public void close();
}
