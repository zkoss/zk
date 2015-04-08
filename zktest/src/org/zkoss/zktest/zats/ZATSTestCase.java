package org.zkoss.zktest.zats;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.zkoss.zats.mimic.DefaultZatsEnvironment;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.ZatsEnvironment;

public abstract class ZATSTestCase {
	protected static ZatsEnvironment env;
	
	@BeforeClass
	public static void init() {
		env = new DefaultZatsEnvironment("./src/archive/WEB-INF", "/zktest");
		env.init("./src/archive/");
	}
	
	@AfterClass
	public static void end() {
		env.destroy();
	}
	
	@After
	public void after() {
		env.cleanup();
	}
	
	public DesktopAgent connect() {
		return connect("");
	}
	
	public DesktopAgent connect(String location){
		if (location == null || location.length() <= 0) {
			String filePath = getFileLocation();
			try {
				return env.newClient().connect(filePath);
			} catch (Exception e) {
				try {
					return env.newClient().connect(filePath.replace("_", "-"));
				} catch (Exception e2) {
					return env.newClient().connect(filePath.replace("-", "_"));
				}
			}
		} else
			return env.newClient().connect(location);
	}

	protected String getFileLocation() {
		String className = this.getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/");
		int lastTest = className.lastIndexOf("Test");
		return className.substring(0, lastTest) + ".zul";
	}
}
