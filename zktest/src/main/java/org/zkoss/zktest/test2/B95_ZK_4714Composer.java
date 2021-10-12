package org.zkoss.zktest.test2;

import java.util.function.Function;

import org.zkoss.lang.Threads;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;

public class B95_ZK_4714Composer implements Composer {

	private Desktop desktop;
	private LanguageDefinition zul;
	private Page page;
	private Component container;

	private long startTime = System.currentTimeMillis();

	@Override
	public void doAfterCompose(Component container) throws Exception {
		int baseDelay = 2000;

		ClassWebResource cwr = WebManager.getWebManager(WebApps.getCurrent()).getClassWebResource();
		cwr.removeExtendlet("wpd");
		cwr.addExtendlet("wpd", new B95_ZK_4714WpdExtendlet("/wgt4714.test", baseDelay));

		this.container = container;
		zul = LanguageDefinition.getByExtension("zul");
		desktop = container.getDesktop();
		page = container.getPage();
		desktop.enableServerPush(true);

		new Thread(() -> {
			Component test01 = doActivated(this::addComponent, "test01");
			Component test02 = doActivated(this::addComponent, "test02");
			Component test03 = doActivated(this::addComponent, "test03");
			Threads.sleep(baseDelay);
			Component test04 = doActivated(this::addComponent, "test04");
			Threads.sleep(baseDelay / 2);
			doActivated(this::invokeSetStyle, test01);
			doActivated(this::invokeSetStyle, test02);
			doActivated(this::invokeSetStyle, test03);
			doActivated(this::invokeSetStyle, test04);
		}).start();
	}

	private Component addComponent(String name) {
		System.out.println(elapsedTime() + "ms - add component: " + name);
		Component test = zul.getComponentDefinition(name).newInstance(page, null);
		this.container.appendChild(test);
		return test;
	}

	private Void invokeSetStyle(Component component) {
		System.out.println(elapsedTime() + "ms - set style on: " + component.getDefinition().getName());
		((HtmlBasedComponent) component).setStyle("font-weight:700;");
		System.out.println(" component desktop: " + component.getDesktop() != null);
		Clients.response(new AuInvoke(component, "setStyle", "font-weight: 700;color: rgb(255, 0, 0);"));
		return null;
	}

	private <T, R> R doActivated(Function<T, R> function, T param) {
		try {
			Executions.activate(desktop);
			return function.apply(param);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Executions.deactivate(desktop);
		}
		throw new IllegalStateException("doActivated didn't return as expected");
	}

	long elapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
}
