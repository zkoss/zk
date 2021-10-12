package org.zkoss.zktest.bind.issue;

import org.zkoss.zul.Listitem;

public class B02224DebugListitem extends Listitem {
	private static final long serialVersionUID = 1L;

	static int count = 0;
	static long lastTime = System.currentTimeMillis();

	public B02224DebugListitem() {
		super();
		logCount();
	}

	public <T> B02224DebugListitem(String label, T value) {
		super(label, value);
		logCount();
	}

	public B02224DebugListitem(String label) {
		super(label);
		logCount();
	}

//	@Override
//	public void onPageAttached(Page newpage, Page oldpage) {
//		super.onPageAttached(newpage, oldpage);
//		logTimeDiff("attach ");
//	}
//
//	@Override
//	public void onPageDetached(Page page) {
//		System.out.println(count + " ");
//		logTimeDiff("detach ");
//	}
//	
//	private void logTimeDiff(String message) {
//		long time = System.currentTimeMillis();
//		System.out.println(message + (time - lastTime));
//		lastTime = time;
//	}
	
	private void logCount() {
//		System.out.println("B02224DebugListitem > " +count);
		count++;
	}

	

}