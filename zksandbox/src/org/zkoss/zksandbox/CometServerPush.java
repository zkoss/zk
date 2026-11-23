/* CometServerPush.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 20, 2008 11:31:54 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zksandbox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

/**
 * @author jumperchen
 * 
 */
public class CometServerPush {

	public static void start(Component info, Component listbox)
			throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			desktop.enableServerPush(true);
			new WorkingThread(info, listbox).start();
		}
	}

	public static void stop() throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);
		} else {
			Messagebox.show("Already stopped");
		}
	}

	private static class WorkingThread extends Thread {
		private static Color colors[] = { Color.blue, Color.cyan, Color.green,
				Color.magenta, Color.orange, Color.pink, Color.red,
				Color.yellow, Color.lightGray, Color.white };

		private Ellipse2D.Float[] ellipses;

		private double esize[];

		private float estroke[];

		private double maxSize;

		private final Desktop _desktop;

		private final Component _info;

		private final Listbox _listbox;

		private int _width = 150;

		private int _height = 150;

		private WorkingThread(Component info, Component listbox) {
			_desktop = info.getDesktop();
			_info = info;
			_listbox = (Listbox) listbox;
		}

		public void run() {
			try {
				while (true) {
					if (_info.getDesktop() == null
							|| !_desktop.isServerPushEnabled()) {
						_desktop.enableServerPush(false);
						return;
					}
					Executions.activate(_desktop);
					try {
						((Image) _info).setContent(paint());
					} finally {
						Executions.deactivate(_desktop);
					}
					Threads.sleep(1000);
				}
			} catch (DesktopUnavailableException ex) {
				System.out.println("The server push thread interrupted");
			} catch (InterruptedException e) {
				System.out.println("The server push thread interrupted");
			}
		}

		public void init(int size) {
			if (size == 60)
				size = 8;
			else if (size == 40)
				size = 12;

			// an array of type Ellipse2D.Float
			ellipses = new Ellipse2D.Float[size];

			// a double array initialized to the length of the ellipses array
			esize = new double[ellipses.length];

			// a float array initialized to the length of the ellipses array
			estroke = new float[ellipses.length];
		}

		public void getRandomXY(int i, double size, int w, int h) {
			esize[i] = size;
			estroke[i] = 1.0f;
			double x = Math.random() * (w - (maxSize / 2));
			double y = Math.random() * (h - (maxSize / 2));
			ellipses[i].setFrame(x, y, size, size);
		}

		public BufferedImage paint() {
			int size;
			try {
				size = Integer.parseInt(_listbox.getSelectedItem().getLabel());
			} catch (NumberFormatException e) {
				size = 20;
			}

			init(size);
			// fills the ellipses array with Ellipse2D.Float objects
			for (int i = 0; i < ellipses.length; i++) {
				ellipses[i] = new Ellipse2D.Float();
				// gets location for each ellipse with the given random size
				getRandomXY(i, size * Math.random(), _width, _height);
			}

			BufferedImage bi = new BufferedImage(_width, _height,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D g2 = bi.createGraphics();
			g2.setBackground(Color.BLACK);
			g2.clearRect(0, 0, _width, _height);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// sets the color and stroke size and draws each ellipse
			for (int i = 0; i < ellipses.length; i++) {
				g2.setColor(colors[i % colors.length]);
				g2.setStroke(new BasicStroke(estroke[i]));
				g2.draw(ellipses[i]);
			}

			g2.dispose();
			return bi;
		}
	}
}
