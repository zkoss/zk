package org.zkoss.zktest.zats;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.zkoss.zats.mimic.DefaultZatsEnvironment;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.ZatsEnvironment;

public abstract class ZATSTestCase {
	private static ThreadLocal<ZatsEnvironment> env = new ThreadLocal<>();
	
	@BeforeClass
	public static void init() {
		ZatsEnvironment zatsEnvironment = new DefaultZatsEnvironment("./src/archive/WEB-INF", "/zktest");
		zatsEnvironment.init("./src/archive/");
		env.set(zatsEnvironment);
	}
	
	@AfterClass
	public static void end() {
		ZatsEnvironment zatsEnvironment = env.get();
		if (zatsEnvironment != null)
			zatsEnvironment.destroy();
		env.set(null);
	}
	
	@After
	public void after() {
		ZatsEnvironment zatsEnvironment = env.get();
		if (zatsEnvironment != null)
			zatsEnvironment.cleanup();
	}
	
	public DesktopAgent connect() {
		return connect("");
	}
	
	public DesktopAgent connect(String location){
		ZatsEnvironment zatsEnvironment = env.get();
		if (location == null || location.length() <= 0) {
			String filePath = getFileLocation();
			try {
				return zatsEnvironment.newClient().connect(filePath);
			} catch (RuntimeException e) {
				try {
					if (e.getCause() instanceof FileNotFoundException)
						return zatsEnvironment.newClient().connect(filePath.replace("_", "-"));
					else
						throw e;
				} catch (RuntimeException e2) {
					if (e2.getCause() instanceof FileNotFoundException)
						return zatsEnvironment.newClient().connect(filePath.replace("-", "_"));
					else
						throw e2;
				}
			}
		} else
			return zatsEnvironment.newClient().connect(location);
	}

	protected String getFileLocation() {
		String className = this.getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/");
		int lastTest = className.lastIndexOf("Test");
		return className.substring(0, lastTest) + getFileExtension();
	}

	protected String getFileExtension() {
		return ".zul";
	}
}
