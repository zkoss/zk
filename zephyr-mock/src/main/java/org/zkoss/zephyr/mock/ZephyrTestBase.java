/* ZephyrTestBase.java

	Purpose:

	Description:

	History:
		2:11 PM 2021/10/1, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.zkoss.lang.Classes;
import org.zkoss.util.resource.Locator;
import org.zkoss.zephyr.ui.IStubComponent;
import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.impl.VolatilePage;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.WebAppsCtrl;

/**
 * A base class for Junit to run with Zephyr immutable component.
 * @author jumperchen
 */
public class ZephyrTestBase {
	public static int INIT_BUFFER_SIZE = 1024 * 8;

	private boolean disableForBenchmarkMode = true;

	private static final PageDefinition pageDefinition = new PageDefinition(
			LanguageDefinition.getByExtension("zul"), new Locator() {
		public String getDirectory() {
			return null;
		}

		public URL getResource(String s) {
			return null;
		}

		public InputStream getResourceAsStream(String s) {
			return null;
		}
	});

	public ZephyrTestBase() {
	}

	public ZephyrTestBase(boolean disableForBenchmarkMode) {
		this.disableForBenchmarkMode = disableForBenchmarkMode;
	}

	@BeforeEach
	public void beforeEach() {
		DummyExec dummyExec = new DummyExec();
		WebApp webapp = dummyExec.getDesktop().getWebApp();
		ExecutionsCtrl.setCurrent(dummyExec);
		WebAppsCtrl.setCurrent(webapp);
		VolatilePage volatilePage = new VolatilePage(pageDefinition);
		volatilePage.preInit();
		dummyExec.setCurrentPage(volatilePage);
		// simulated for ZK EE
		try {
			Class<?> runTimeClass = Classes.forNameByThread("org.zkoss.zkex.rt.Runtime");
			runTimeClass.getMethod("init", WebApp.class, Boolean.TYPE).invoke(runTimeClass, webapp, true);
		} catch (Throwable e) {
			// ignore
		}
	}

	@AfterEach
	public void afterEach() {
		// reset
		ExecutionsCtrl.setCurrent(null);
		WebAppsCtrl.setCurrent(null);
	}

	public String richlet(Supplier<? extends IComponent> supplier) {
		StringWriter stringWriter = new StringWriter(INIT_BUFFER_SIZE);
		try {
			IStubComponent.of(supplier.get()).redraw(stringWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trimUuidPart(stringWriter.toString(), disableForBenchmarkMode);
	}

	public String composer(Supplier<? extends Component> supplier) {
		StringWriter stringWriter = new StringWriter(INIT_BUFFER_SIZE);
		try {
			IStubComponent.of(
							(IComponent) Immutables.proxyIComponent(supplier.get()))
					.redraw(stringWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trimUuidPart(stringWriter.toString(), disableForBenchmarkMode);
	}

	public String thenComposer(Supplier<? extends Component> supplier, Function<? extends IComponent, IComponent> andThen) {
		StringWriter stringWriter = new StringWriter(INIT_BUFFER_SIZE);
		try {
			IStubComponent.of(
					andThen.apply(Immutables.proxyIComponent(supplier.get()))).redraw(stringWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trimUuidPart(stringWriter.toString(), disableForBenchmarkMode);
	}

	public String zul(Supplier<? extends AbstractComponent> supplier) {
		StringWriter stringWriter = new StringWriter(INIT_BUFFER_SIZE);
		try {
			AbstractComponent zcmp = supplier.get();

			// ignore for benchmark
			if (disableForBenchmarkMode) {
				zcmp.setPage((((ExecutionCtrl) Executions.getCurrent()).getCurrentPage()));
			}
			zcmp.redraw(stringWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trimUuidPart(stringWriter.toString(), disableForBenchmarkMode);
	}

	private static String trimUuidPart(String output, boolean disableForBenchmarkMode) {
		if (disableForBenchmarkMode)
			return output.replaceAll("'z__[^\']*'", "")
					.replaceAll(",z\\$[^}|,]+", "") // for z$rod
				.replaceAll(",\\$+[\\w]+:[\\w]+", "")
				.replaceAll("\\$+[\\w]+:[\\w]+,?", "")
				.replaceAll(",'vertical'", "")
				.replaceAll(",'trendy'", "")
				.replaceAll(",cssflex:false", "")
				.replaceAll(",\\{cssflex:false}", ",{}")
				.replaceAll("_zephyr:true", "") // for IMeshElement
				.replaceAll("\\{,", "{") // for IMeshElement
				.replaceAll("[,]?_loaded:[^}|,]+", "") // for ITreeitem
				.replaceAll("[,]?_index:[^}|,]+", "") // for ITreeitem
				.replaceAll(",selectedIndex:[^}]?", ""); // for IRadiogroup
		return output;
	}
}
