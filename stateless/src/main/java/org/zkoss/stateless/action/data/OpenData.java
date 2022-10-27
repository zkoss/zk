/* OpenData.java

	Purpose:

	Description:

	History:
		11:12 AM 2021/11/2, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.Self;
import org.zkoss.stateless.sul.IBandbox;
import org.zkoss.stateless.sul.ICombobox;
import org.zkoss.stateless.sul.ITreeitem;
import org.zkoss.zk.ui.event.Events;

/**
 * Represents an action data cause by user's opening or closing
 * something at the client.
 *
 * <p><b>Note:</b> it is a bit confusing but {@link Events#ON_CLOSE} is sent when
 * user clicks a close button. It is a request to ask the server
 * to close a window, a tab or others. If the server ignores the action,
 * nothing will happen at the client.
 *
 * <p>On the other hand, {@link Events#ON_OPEN} (with {@link OpenData}) is
 * a notification. It is sent to notify the server that the client has
 * opened or closed something.
 * And, the server cannot prevent the client from opening or closing.
 *
 * @author jumperchen
 */
public class OpenData implements ActionData {

	private boolean open;
	private List<Integer> referencePath;
	private String reference;
	private Object value;

	/**
	 * Returns whether it causes open.
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Returns the reference path if any.
	 * This method is used for {@link ITreeitem} to open or close a treeitem to
	 * get its tree path.
	 *
	 * @return
	 */
	public int[] getReferencePath() {
		return Optional.ofNullable(referencePath)
				.orElse(Collections.emptyList()).stream().mapToInt(i -> i)
				.toArray();
	}

	/** Returns the reference that causes {@link Self}
	 * to be opened.
	 *
	 * <p>It is {@code null}, if the open event is not caused by opening
	 * a context menu, a tooltip or a popup.
	 * <br>
	 * <br>
	 * <b>Note:</b> the {@code onOpen} action is also sent when closing the context menu
	 * (tooltip and popup), and this method returns null in this case.
	 * Thus, it is better to test {@link #isOpen()} or {@link #getReference()}
	 * before accessing the returned value.
	 *
	 * <code>if (event.isOpen()) doSome(event.getReference());</code>
	 */
	public Locator getReference() {
		return reference == null ? null : Locator.of(reference);
	}

	/**
	 * Returns the value of the target component,
	 * when the onOpen event is sent, or null if not applicable.
	 *
	 * <p><b>Note:</b> {@link ICombobox}, {@link IBandbox} and other {@code combo-type}
	 * input don't send the {@code onChange} action when the dropdown is opened
	 * ({@code onOpen}).
	 * Thus, if you want to do something depends on the value,
	 * use the value returned by this method.
	 * Furthermore, for {@link ICombobox} and {@link IBandbox}, the returned value is
	 * a non-null String instance.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @hidden for Javadoc
	 */
	@Override
	public String toString() {
		return "OpenData{" + "open=" + open + ", referencePath=" + referencePath
				+ ", reference='" + reference + '\'' + ", value=" + value + '}';
	}
}
