/* FileWatchdog.java


	Purpose: 
	Description: 
	History:
	 2001/7/25, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.File;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.Threads;
import org.zkoss.util.logging.Log;

/**
 * File modification monitor thread. It monitors a gving file in a lowest
 * priority thread. Then, if it detects any modification, invokes
 * a giving interface. Note: Callback.onModified is at least called once.
 *
 * <p>Caller has to implement the FileWatchdog.Callback interface. Then,<br>
 * new FileWatchdog(filename, 30000, callback).start();
 *
 * <p>This class is extended from Thread, so you could manipulate it as
 * a normal thread, such as interrupt, suspend, and change priority.
 *
 * <p>The watchdog executes at the lowest priority, so it won't affect
 * much of the system performance.
 *
 * @author tomyeh
 */
public class FileWatchdog extends Thread {
	private static final Log log = Log.lookup(FileWatchdog.class);

	/** The interface to implement when using a FileWatchdog. */
	public interface Callback {
		/** Callbacks when the file is modified.
		 *
		 * @param file the file being monitored
		 */
		public void onModified(File file) throws Exception;
	}

	private File _file;
	private Callback _callback;
	private long _delay;
	private long _lastModified = 0;
	private boolean _warnedAlready = false;
	private boolean _ceased = false;

	/** Constructor.
	 * @param file the file
	 * @param delay the delay in milliseconds to wait between each check
	 * @param callback the callback
	 */
	public FileWatchdog(File file, long delay, Callback callback) {
		setup(file, delay, callback);
	}
	/** Constructor.
	 * @param filename the filename
	 * @param delay the delay in milliseconds to wait between each check
	 * @param callback the callback
	 */
	public FileWatchdog(String filename, long delay, Callback callback) {
		if (filename == null)
			throw new IllegalArgumentException("null");
		setup(new File(filename), delay, callback);
	}

	private void setup(File file, long delay, Callback callback) {
		if (file == null || callback == null)
			throw new IllegalArgumentException("null");

		_file = file;
		_delay = delay;
		_callback = callback;

		Threads.setPriority(this, Thread.MIN_PRIORITY);
		Threads.setDaemon(this, true);
	}

	//-- Thread --//
	public void start() {
		_ceased = false;
		super.start();
	}
	/** Stops the thread. */
	public void cease() {
		_ceased = true;
	}
	public void run() {
		do {
			try {
				if (_file.exists()) {
					long l = _file.lastModified();
					if (l > _lastModified) {
						if (!_ceased && log.debugable())
							log.debug(_file + " modified: " + l + " vs " + _lastModified);

						_lastModified = l;
						_warnedAlready = false;
						if (!_ceased)
							_callback.onModified(_file);
					}
				} else if (!_warnedAlready) {
					if (!_ceased) log.debug(MCommon.FILE_NOT_FOUND, _file.getPath());
					_warnedAlready = true;
				}
			}catch(Exception ex) {
				if (!_ceased)
					log.warning(MCommon.FILE_READ_FAILED, _file, ex);
				return; //no point in continuing
			}

			try {
				if (!_ceased)
					sleep(_delay);
			}catch(InterruptedException ex) {
				System.out.println("The file watchdog is interrupted");
				break; //interrupted
			}
		} while (!_ceased);

		System.out.println(Thread.currentThread().getName()+": the file watchdog stops");
		//Don't use log because LogService depends on it
	}
}
