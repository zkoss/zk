/* Command.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 18 00:07:15     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

/** Represents a command of a request ({@link AuRequest}).
 * Each command is unique no matter {@link #getId} is the same or not.
 * All commands must be declared as static members of {@link AuRequest}
 * such that they will be initialized sequentially at begining.
 * Also, if a command will generate an event, it shall register the
 * event in its constructor.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
abstract public class Command {
	private final String _id;
	private final int _flags;

	/** Whether to skip this command if previous commands ever caused errors.
	 *In other words, it decides whether to skip this command when
	 * {@link #process(AuRequest,boolean)} is called with errorEver = true;
	 * This argument is used to control the behavior of
	 * {@link #process(AuRequest,boolean)}.
	 *
	 * <p>Part of the returned value of {@link #getFlags}.
	 */
	public static final int SKIP_IF_EVER_ERROR = 0x0001;
	/** Whether this command is ignorable. 
	 * In other words, it is ignored if there is any following command is
	 * recieved.
	 *
	 * <p>Part of the returned value of {@link #getFlags}.
	 */
	public static final int IGNORABLE = 0x0002;
	/** Whether this command belongs to the control group.
	 * A new control-group command is dropped if there is already
	 * another control-group command is in the queue.
	 */
	public static final int CTRL_GROUP = 0x0004;
	/** Whether to drop the existent command (in the queue) if it is the same
	 * as the new arrival one.
	 * By the same we mean both command's ID and target are the same.
	 */
	public static final int IGNORE_OLD_EQUIV = 0x0008;
	/** Whether to drop the existent command (in the queue) if it is the same
	 * as the immediate following one.
	 * By the same we mean both command's ID and target are the same.
	 */
	public static final int IGNORE_IMMEDIATE_OLD_EQUIV = 0x0010;

	/**
	 * @param flags a combination of {@link #SKIP_IF_EVER_ERROR},
	 * {@link #IGNORABLE} and others
	 */
	protected Command(String id, int flags) {
		_id = id;
		_flags = flags;

		AuRequest.addCommand(this);
	}
	/** Returns the attributes of this command, a combination of
	 * {@link #SKIP_IF_EVER_ERROR}, {@link #IGNORABLE} and others.
	 */
	public final int getFlags() {
		return _flags;
	}

	/** Returns ID of this command. */
	public final String getId() {
		return _id;
	}
	public final String toString() {
		return _id;
	}
	public final boolean equals(Object o) { //prevent override
		return this == o;
	}

	/** Derived must override this method to process this command.
	 */
	abstract protected void process(AuRequest request);
	/** Called to process the specified request.
	 * You don't override this method directly.
	 * Rather, override {@link #process(AuRequest)}.
	 *
	 * @param everError whether any error ever occured before
	 * processing this command.
	 * Notice that multiple commands might be processed in one
	 * execution.
	 */
	public final void process(AuRequest request, boolean everError) {
		if (!everError || !((_flags & SKIP_IF_EVER_ERROR) == 0))
			process(request);
	}
}
