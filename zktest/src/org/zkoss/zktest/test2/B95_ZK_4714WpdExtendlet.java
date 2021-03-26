package org.zkoss.zktest.test2;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.http.WpdExtendlet;

public class B95_ZK_4714WpdExtendlet extends WpdExtendlet {
	private final String path;
	private final int delay;

	public B95_ZK_4714WpdExtendlet(String path, int delay) {
		this.path = path;
		this.delay = delay;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
		if (path.endsWith("wpd") && path.contains(this.path)) {
			System.out.println("delay : " + path);
			Threads.sleep(delay);
			System.out.println("loaded: " + path);
		}
		super.service(request, response, path);
	}
}
