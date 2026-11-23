package org.zkoss.zktest.test2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

public class B65_ZK_1913 {
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

	@Command
	public void toggltServerPush()
			throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);
		desktop.enableServerPush(false);
	}
}
