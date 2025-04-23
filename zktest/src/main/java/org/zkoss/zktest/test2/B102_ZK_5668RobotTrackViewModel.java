/* B102_ZK_5668RobotTrackViewModel.java

	Purpose:
		
	Description:
		
	History:
		11:12 AM 2025/4/22, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;


/**
 * @author jumperchen
 */
public class B102_ZK_5668RobotTrackViewModel {
	private static final String RUNNING = "running";
	public static final String INTERVAL = "interval";
	public static final String UPDATE_INTERVAL = "updateInterval";

	private double x;
	private AtomicBoolean running = new AtomicBoolean(false);
	private int interval = 500;

	@AfterCompose
	public void enableServerPush(@ContextParam(ContextType.DESKTOP)Desktop desktop){
		desktop.enableServerPush(true);
	}

	@Command
	@NotifyChange(RUNNING)
	public void start(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		updatePosition();
		this.running.set(true);
		startBackgroundThread(desktop);
	}

	@Command
	@NotifyChange(RUNNING)
	public void stop(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		this.running.set(false);
	}

	@GlobalCommand(UPDATE_INTERVAL)
	@NotifyChange(INTERVAL)
	public void updateInterval(@BindingParam("interval") int interval) {
		this.interval = Math.min(Math.max(interval, 200), 2000);
	}

	private void startBackgroundThread(Desktop desktop) {
		new Thread(() -> {
			while (running.get()) {
				try {
					activated(desktop, () -> {
						this.updatePosition();
					});
					Thread.sleep(interval);
				} catch(Exception e) {
					//leave background thread, no error handling in this Demo
					return;
				}
			}
		}).start();
	}

	private void activated(Desktop desktop, Runnable task) {
		try {
			Executions.activate(desktop);
			task.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Executions.deactivate(desktop);
		}
	}

	private void updatePosition() {
		double now = (double) System.nanoTime() / TimeUnit.SECONDS.toNanos(1);
		x = Math.cos(now / Math.PI / 2.0) * 40 + 50;
		BindUtils.postNotifyChange(null, null, this, "x");
	}

	public boolean getRunning() {
		return running.get();
	}

	public double getX() {
		return x;
	}
}
