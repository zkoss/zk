/**
 * 
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2747TimerController extends SelectorComposer<Window> {
	private static final long serialVersionUID = -1952983479970324134L;
	@Wire
	private Timer timer;

	/**
	 * Creates a timer object, which detach the own component after 5sec.
	 */
	@Override
	public void doAfterCompose(final Window comp) throws Exception {
		super.doAfterCompose(comp);

		timer.setDelay(5000);
		timer.setRepeats(false);
		timer.addEventListener(Events.ON_TIMER, new EventListener<Event>() {
			public void onEvent(Event event) {
				comp.detach();
			}
		});
	}
}
