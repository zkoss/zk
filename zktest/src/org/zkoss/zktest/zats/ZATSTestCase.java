package org.zkoss.zktest.zats;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;

public abstract class ZATSTestCase {
	
	@BeforeClass
	public static void init() {
		Zats.init("./src/archive/bind/issue");
	}
	
	@AfterClass
	public static void end() {
		Zats.end();
	}
	
	@After
	public void after() {
		Zats.cleanup();
	}
	
	public DesktopAgent connect() {
		return connect("");
	}
	
	public DesktopAgent connect(String location){
		if (location == null || location.length() <= 0)
			return Zats.newClient().connect(getFileLocation());
		else
			return Zats.newClient().connect(location);
	}

	private String getFileLocation() {
		String className = this.getClass().getName().replaceAll("org.zkoss.zktest.zats.","/");
		int lastTest = className.lastIndexOf("Test");
		return className.substring(0, lastTest) + ".zul";
	}

}
