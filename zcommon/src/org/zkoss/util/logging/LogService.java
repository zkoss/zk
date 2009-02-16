/* LogService.java

{{IS_NOTE

	Purpose: The MBean-able log service
	Description: 
	History:
	 2001/6/4, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.logging;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.*;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.mesg.MCommon;
import org.zkoss.io.FileWatchdog;
import org.zkoss.io.Files;

/**
 * The log service which is used to monitor i3-log.conf.
 * To initialize it, invoke {@link #init}. Note: {@link Log} could work
 * without {@link LogService}.
 *
 * <p>Design consideration: the configure and configureAndWatch
 * methods could be declared as static, but we don't.
 * Reason: easilier to extend (also the performance gained by
 * the static method is neglectable comparing its complexity).
 *
 * <p>Implementation Note:
 * LogService cannot be a component (because ComponentManager depedns
 * on iDom, which depends on log). Thus, we use Singleton instead.
 *
 * @author tomyeh
 */
public class LogService {
	private static final Log log = Log.lookup(LogService.class);

	/** One log service per the root logger.
	 * <p>Note: Tomcat has one root logger per Web application.
	 */
	private static final Map _svcs = new HashMap(5);
	/** The service name. */
	private static final String SERVICE_NAME = "logging";

	/** The name of the root logger that this logging service is monitoring.
	 * Note: we cannot use Logger because Tomcat has one root per Web app
	 */
	protected final String _root;

	private FileWatchdog _logwdog;
	private File _logfn;

	/** Returns whether the logging service is started, i.e., whether
	 * {@link #init} is invoked.
	 */
	public static final boolean isInited(String rootnm) {
		synchronized (_svcs) {
			return _svcs.containsKey(rootnm);
		}
	}

	/** Initializes the logging service.
	 * If the log service already started, an error message is logged.
	 *
	 * <p>Note: it also enables the hierarchy support of loggers by
	 * calling {@link Log#setHierarchy} with true.
	 *
	 * @param rootnm the name of the root logger. The logging service
	 * registered handlers at the specified logger.
	 * @param cls the implementation to start. If null, {@link LogService}
	 * is used.
	 */
	public static final LogService init(String rootnm, Class cls) {
		if (rootnm == null)
			throw new IllegalArgumentException("null");

		Log.setHierarchy(true); //turn on the hierarchy

		final Logger root = Logger.getLogger(rootnm);
			//Tomcat has one root per Web app, while Logger.global is shared
			//Thus we have to use getLogger to verify whether it is installed

		synchronized (_svcs) {
			LogService svc = (LogService)_svcs.get(root);
			if (svc != null) {
				log.warning("Already started: "+rootnm);
			} else {
				try {
					svc = (LogService)Classes.newInstance(
						cls != null ? cls: LogService.class,
						new Class[] {String.class}, new Object[] {rootnm});
				} catch (Exception ex) {
					throw SystemException.Aide.wrap(ex);
				}
				_svcs.put(root, svc);
			}
			return svc;
		}
	}
	/** Stops the logging service
	 */
	public static final void stop(String rootnm) {
		if (rootnm == null)
			throw new IllegalArgumentException("null");

		final Logger root = Logger.getLogger(rootnm);
			//Tomcat has one root per Web app, while Logger.global is shared
			//Thus we have to use getLogger to verify whether it is installed

		final LogService svc;
		synchronized (_svcs) {
			svc = (LogService)_svcs.get(root);
			if (svc == null) return;
		}
		svc.stop();
	}

	/** Constructor.
	 * Don't call this method directly. Rather, use {@link #init} to
	 * start the service.
	 */
	public LogService(String root) {
		if (root == null)
			throw new IllegalArgumentException("null");
		_root = root;
			//Note: we don't store Logger because Tomcat has one root
			//per Web app, so we have to getLogger dynamically

		//monitor i3-log.conf
		try {
			_logfn = new File(Files.getConfigDirectory(), "i3-log.conf");
			if (_logfn.exists()) log.info("Monitor "+_logfn);
			else log.info("File not found: "+_logfn);
			_logwdog = configureAndWatch(_logfn, D.ON ? 10000: 360000);//millisec
		} catch (Exception ex) {
			log.warning(ex);
		}

		log.debug(MCommon.SERVICE_INIT_OK, SERVICE_NAME);
			//1. don't put it in constructor because log is not ready until start
			//2. use debug because LogService is also used by client
	}
	/** Stops the service. You rarely need to do so. This service monitors
	 * whether any preference is changed, and automatically reflects
	 * the new changes.
	 */
	synchronized public void stop() {
		System.out.println("Stopping "+SERVICE_NAME+"...");

		final Logger root = Logger.getLogger(_root);
		synchronized (_svcs) {
			final Object o = _svcs.remove(root);
			if (o != this) {
				_svcs.put(root, this); //resotre
				throw new IllegalStateException("LogService has beeing stopped");
			}
		}

		if (_logwdog != null) _logwdog.cease();
	}

	/**
	 * Configures based the properties.
	 *
	 * <p>The key is a logger name and the value is the level.
	 * A special level, INHERIT or NULL, to denote resetting the level
	 * to be the same as the logger's parent.
	 *
	 * @param props the properties
	 */
	public final void configure(Properties props) {
		for (final Iterator it = props.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String key = (String)me.getKey();
			String val = (String)me.getValue();
			if (val != null) val = val.trim();

			final Level level = Log.getLevel(val);
			if (level != null || val.equalsIgnoreCase("NULL")
			|| val.equalsIgnoreCase("INHERIT")) {
				Logger.getLogger(key).setLevel(level);
			} else {
				log.warning("Illegal log level, "+val+", for "+key);
			}
		}
	}
	/**
	 * Configures based the properties stored in a file.
	 * <p>The key is a logger name and the value is the level.
	 *
	 * @param file the file
	 */
	public final void configure(File file)
	throws FileNotFoundException, IOException {
		log.info(MCommon.FILE_OPENING, file);
		final Properties props = new Properties();
		final FileInputStream is = new FileInputStream(file);
		try {
			props.load(is);
		} finally {
			try {is.close();} catch (Throwable ex) {}
		}
		configure(props);
	}
	/**
	 * Configures based the properties stored in a file.
	 * <p>The key is a logger name and the value is the level.
	 *
	 * @param filename the filename
	 */
	public final void configure(String filename)
	throws FileNotFoundException, IOException {
		configure(new File(filename));
	}

	public class WatchdogCallback implements FileWatchdog.Callback {
		private WatchdogCallback() {
		}

		public final void onModified(File file) {
			try {
				configure(file);
			} catch (Exception ex) {
				log.warning(MCommon.FILE_READ_FAILED, file, ex);
			}
		}
	}
	/**
	 * Periodically checks whether a file is modified, and configures 
	 * based the properties stored in the file, if any modifications.
	 *
	 * <p>The key is a logger name and the value is the level.
	 *
	 * @param file the file
	 * @param delay the delay in milliseconds to wait between each check.
	 */
	public final FileWatchdog configureAndWatch(File file, long delay) {
		FileWatchdog wg = new FileWatchdog(file, delay, new WatchdogCallback());
		wg.start();
		return wg;
	}
}
