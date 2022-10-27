/* UiAgentCtrl.java

	Purpose:

	Description:

	History:
		2:39 PM 2021/10/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.util.Collection;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IMediaBase;
import org.zkoss.util.media.Media;
import org.zkoss.zk.au.AuResponse;

/**
 * An addition interface to {@link UiAgent}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public class UiAgentCtrl {
	/**
	 * Deactivate the given UiAgent
	 * @param uiAgent
	 */
	public static void deactivate(UiAgent uiAgent) {
		if (uiAgent instanceof UiAgentImpl) {
			((UiAgentImpl) uiAgent)._exec = null;
		}
	}

	/** An utilities to create an array of JavaScript objects
	 * ({@link JavaScriptValue}) that can be used
	 * to mount the specified widget at the clients.
	 */
	public static <I extends IComponent> Collection<JavaScriptValue> redraw(Locator locator, Collection<I> children) {
		return UiAgentImpl.redraw(locator, children);
	}

	/** A special smart update to update a value in Object.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, Object value) {
		smartUpdate(locator, attr, value, false);
	}
	/** A special smart update to update a value in Object.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, Object value, boolean append) {
		if (value instanceof Media) {
			UiAgentImpl.smartUpdate(locator, attr, new JavaScriptValue(
					IMediaBase.JSObjectUrl.generate((Media) value)), append);
		} else {
			UiAgentImpl.smartUpdate(locator, attr, value, append);
		}
	}
	/** A special smart update to update a value in int.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, int value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in long.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, long value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in byte.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, byte value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in char.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, char value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in boolean.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, boolean value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in float.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, float value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}
	/** A special smart update to update a value in double.
	 * <p>It is the same as {@link #smartUpdate(Locator, String, Object)}.
	 */
	public static <I extends IComponent> void smartUpdate(Locator locator, String attr, double value) {
		UiAgentImpl.smartUpdate(locator, attr, value);
	}

	/** Adds a response with the given priority.
	 * The higher priority, the earlier the update is executed.
	 * @param key could be anything. If null, the response is appended.
	 * If not null, the second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 */
	public static <I extends IComponent> void response(String key, AuResponse response, int priority) {
		UiAgentImpl.response(key, response, priority);
	}

	/** Adds a response which will be sent to client at the end
	 * of the execution.
	 * @param key could be anything. If null, the response is appended.
	 * If not null, the second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 */
	public static <I extends IComponent> void response(String key, AuResponse response) {
		UiAgentImpl.response(key, response, 0);
	}

	/** Adds a response directly.
	 * In other words, it is the same as <code>addResponse(resposne.getOverrideKey(), response)</code>
	 */
	public static <I extends IComponent> void response(AuResponse response) {
		response(response.getOverrideKey(), response);
	}
}
