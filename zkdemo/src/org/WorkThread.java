package org;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;

public class WorkThread extends Thread {
	private final Desktop _desktop;

	private final Label _info;

	private int _cnt;

	private boolean _ceased;

	public WorkThread(Label info) {
		_desktop = info.getDesktop();
		_info = info;
	}

	public void run() {
		try {
			while (!_ceased) {
				Threads.sleep(2000); // Update each two seconds
				Executions.activate(_desktop); // get full control of desktop
				try {
					_info.setValue(Integer.toString(++_cnt));
				} catch (RuntimeException ex) {
					throw ex;
				} catch (Error ex) {
					throw ex;
				} finally {
					Executions.deactivate(_desktop); // release full control
														// of desktop
				}
			}
		} catch (InterruptedException ex) {
		}
	}

	public void setDone() {
		_ceased = true;
	}
}
