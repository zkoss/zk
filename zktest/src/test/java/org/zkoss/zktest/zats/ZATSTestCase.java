package org.zkoss.zktest.zats;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import org.zkoss.zats.mimic.DefaultZatsEnvironment;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.ZatsEnvironment;

public abstract class ZATSTestCase {
	protected static ZatsEnvironment env;
	
	@BeforeAll
	public static void init() {
		env = new DefaultZatsEnvironment("./src/main/webapp/WEB-INF", "/zktest");
		env.init("./src/main/webapp");
	}
	
	@AfterAll
	public static void end() {
		env.destroy();
	}
	
	@AfterEach
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
			} catch (RuntimeException e) {
				try {
					if (e.getCause() instanceof FileNotFoundException)
						return env.newClient().connect(filePath.replace("_", "-"));
					else
						throw e;
				} catch (RuntimeException e2) {
					if (e2.getCause() instanceof FileNotFoundException)
						return env.newClient().connect(filePath.replace("-", "_"));
					else
						throw e2;
				}
			}
		} else
			return env.newClient().connect(location);
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
