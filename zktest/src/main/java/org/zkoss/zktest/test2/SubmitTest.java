package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;

public class SubmitTest {
	private static final String ZK664KEY = "ZK-664";

	public static int getCount() {
		Session session = Executions.getCurrent().getSession();
		Integer count = (Integer) session.getAttribute(ZK664KEY);
		if (count == null) count = 0;
		count++;
		session.setAttribute(ZK664KEY, count);
		return count;
	}

	public static void resetCount() {
		Executions.getCurrent().getSession().removeAttribute(ZK664KEY);
	}
}
