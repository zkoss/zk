/* F100_ZK_4988VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 11 14:34:15 CST 2024, Created by rebeccalai

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

public class F100_ZK_4988VM {
	private static final String RUNNING = "running";
	public static final String POSITION = "position";
	private AtomicBoolean running = new AtomicBoolean(false);
	private Coord position = new Coord(50, 50);

	public boolean getRunning() {
		return running.get();
	}

	public Coord getPosition() {
		return position;
	}

	public class Coord {
		private double x;
		private double y;

		public Coord(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}

	@Command
	@NotifyChange(RUNNING)
	public void start(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		updatePosition();
		this.running.set(true);
		desktop.enableServerPush(true);
		startBackgroundThread(desktop);
	}

	@Command
	@NotifyChange(RUNNING)
	public void stop(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		this.running.set(false);
	}

	private void startBackgroundThread(Desktop desktop) {
		new Thread(() -> {
			while (running.get()) {
				try {
					activated(desktop, this::updatePosition);
					Thread.sleep(500);
				} catch (Exception e) {
					throw new RuntimeException(e);
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
		this.position = new Coord(Math.cos(now / Math.PI / 2.0) * 40 + 50, Math.sin(now / Math.PI / 2.0) * 40 + 50);
		BindUtils.postNotifyChange(null, null, this, POSITION);
	}
}
