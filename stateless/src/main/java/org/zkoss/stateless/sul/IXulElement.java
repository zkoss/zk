/*IXulElement.java

	Purpose:
		
	Description:
		
	History:
		3:27 PM 2021/9/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import javax.annotation.Nullable;

import org.zkoss.stateless.action.data.KeyData;
import org.zkoss.stateless.action.data.OpenData;
import org.zkoss.zul.Popup;

/**
 * Immutable {@link org.zkoss.zul.impl.XulElement} interface.
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onCtrlKey</td>
 *          <td><strong>ActionData</strong>: {@link KeyData}
 *          <br> Represents an action triggered by the user presses the {@link #getCtrlKeys() ctrlKeys} on a component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Keystroke Handling</h3>
 * <p> To handle the control keys, you have to specify the keystrokes you want to
 * handle with {@link #withCtrlKeys(String)}. Then, if any child component gains
 * the focus and the user presses a keystroke matches the combination,
 * the {@code onCtrlKey} action will be triggered to the component with
 * an instance of {@link KeyData}.
 *
 * Like {@code ENTER} and {@code ESC}, you could specify the listener and
 * the {@code ctrlKeys} attribute in one of the ancestors.
 * ZK will search the component having the focus, its parent,
 * its parent's parent and so on to find if any of them specifies the {@code ctrlKeys}
 * attribute that matches the keystroke.
 *
 * <br><br>
 * For example,
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/keystrokeHandling")
 * public IComponent keystrokeHandling() {
 *     return IVlayout.of(ITextbox.DEFAULT, IDatebox.DEFAULT)
 *         .withCtrlKeys("@c^a#f10^#f3").withAction(this::doKeystrokeHandling);
 * }
 *
 * {@literal @}{@code Action}(type = Events.ON_CTRL_KEY)
 * public void doKeystrokeHandling(KeyData data) {
 *     Clients.log(data.getKeyCode());
 * }</code>
 * </pre>
 * As shown, you could use {@link KeyData#getKeyCode()} to know which key was pressed.
 * </p>
 * @author jumperchen
 */
public interface IXulElement<I extends IXulElement> extends IHtmlBasedComponent<I> {

	/**
	 * Returns what keystrokes to intercept when a user presses the {@code key}
	 * on the component.
	 * <p>Default: {@code null}.</p>
	 */
	@Nullable
	String getCtrlKeys();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code ctrlKeys}.
	 *
	 * <p>Sets what keystrokes to intercept.</p>
	 *
	 * <p>The {@code string} could be a combination of the following:
	 * <table>
	 * <tbody>
	 * <tr>
	 * <th><center>Key</center>
	 * </th>
	 * <th><center>Syntax</center>
	 * </th>
	 * <th><center>Description</center>
	 * </th>
	 * </tr>
	 * <tr>
	 * <td>Control
	 * </td>
	 * <td><center>{@code ^[?]}</center>
	 * </td>
	 * <td>{@code [?]} can be <b>a~z, 0~9, #[?]</b>,
	 * <p>e.g. {@code ^k} represents {@code Ctrl+k}
	 * </p>
	 * </td></tr>
	 * <tr>
	 * <td>Alt
	 * </td>
	 * <td><center>{@code @[?]}</center>
	 * </td>
	 * <td>{@code [?]} can be <b>a~z, 0~9, #[?]</b>,
	 * <p>e.g. {@code @k} represents {@code Alt+k}
	 * </p>
	 * </td></tr>
	 * <tr>
	 * <td>Shift
	 * </td>
	 * <td><center>{@code $[?]}</center>
	 * </td>
	 * <td>{@code [?]} can be <b>#[?]</b>. <b>Note:</b> $a ~ $z are not supported.
	 * <p>e.g. {@code $#down} represents {@code Shift+↓}
	 * </p>
	 * </td></tr>
	 * <tr>
	 * <td>Mac command(⌘)
	 * </td>
	 * <td><center>{@code %[?]}</center>
	 * </td>
	 * <td>
	 * <p>{@code [?]} can be <b> a~z, 0~9, #[?]</b>.
	 * e.g. {@code %k} represents {@code command+k}
	 * </p>
	 * </td></tr>
	 * <tr>
	 * <td><b>Navigation key</b>
	 * </td>
	 * <td><center>{@code #[?]}</center>
	 * </td>
	 * <td>the supported value of {@code [?]} are listed below:
	 * </td></tr>
	 * <tr>
	 * <td>Home
	 * </td>
	 * <td><center>{@code #home}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>End
	 * </td>
	 * <td><center>{@code #end}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>Insert
	 * </td>
	 * <td><center>{@code #ins}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>Delete
	 * </td>
	 * <td><center>{@code #del}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>←
	 * </td>
	 * <td><center>{@code #left}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>→
	 * </td>
	 * <td><center>{@code #right}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>↑
	 * </td>
	 * <td><center>{@code #up}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>↓
	 * </td>
	 * <td><center>{@code #down}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>PgUp
	 * </td>
	 * <td><center>{@code #pgup}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>PgDn
	 * </td>
	 * <td><center>{@code #pgdn}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>Backspace
	 * </td>
	 * <td><center>{@code #bak}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>function key (F1, F2,... F12)
	 * </td>
	 * <td><center>{@code #f1, #f2, ... #f12}</center>
	 * </td>
	 * <td>
	 * </td></tr>
	 * <tr>
	 * <td>Tab
	 * </td>
	 * <td><center>{@code #tab}</center>
	 * </td>
	 * <td>
	 * </td></tr></tbody></table>
	 *
	 * <p>For example,
	 * <dl>
	 * <dt>{@code ^a^d@c#f10#left#right}</dt>
	 * <dd>It means you want to intercept Ctrl+A, Ctrl+D, Alt+C, F10,
	 * Left and Right.</dd>
	 * <dt>{@code ^#left}</dt>
	 * <dd>It means Ctrl+Left.</dd>
	 * <dt>{@code ^#f1}</dt>
	 * <dd>It means Ctrl+F1.</dd>
	 * <dt>{@code @#f3}</dt>
	 * <dd>It means Alt+F3.</dd>
	 * </dl>
	 *
	 * <p><b>Note:</b> it doesn't support Ctrl+Alt, Shift+Ctrl, Shift+Alt or Shift+Ctrl+Alt.</p>
	 * @param ctrlKeys The expression of the keystroke combination to intercept with.
	 * @return A modified copy of the {@code this} object
	 */
	I withCtrlKeys(@Nullable String ctrlKeys);

	/**
	 * Returns the {@code ID} of the {@link IPopup popup} that should appear
	 * when the user right-clicks on the element (a.k.a., context menu).
	 *
	 * <p>Default: {@code null} (no context menu).
	 */
	@Nullable
	String getContext();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code context}.
	 *
	 * <p>Sets the {@code ID} of the {@link IPopup popup} that should appear
	 * when the user right-clicks on the element (a.k.a., context menu).
	 *
	 * <p>An {@code onOpen} action is triggered to the context menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on {@link OpenData#getReference()}) by listening to the {@code onOpen}
	 * action.
	 *
	 * <p> The context menu can be shown by a position
	 * or the location of {@code x} and {@code y}, you can specify the following format:
	 * <br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * {@code .withContext("id, start_before")}
	 * </pre>
	 * <p> The context menu can also be shown on customized location of {@code x}
	 * and {@code y} by adding parentheses {@code "()"}, for example,
	 * <pre>
	 * {@code .withContext("id, x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)")}
	 * </pre>
	 *
	 * <p> Possible values for the position attribute are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the popup appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the popup appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the popup appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the popup appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the popup appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the popup at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * <b>Note:</b> The position will be ignored if coordinates are set.
	 * @param context The {@code ID} of {@link IPopup}
	 * @return A modified copy of the {@code this} object
	 */
	I withContext(@Nullable String context);

	/**
	 * Returns the {@code ID} of the {@link IPopup popup} that should appear
	 * when the user clicks on the component.
	 *
	 * <p>Default: {@code null} (no popup).</p>
	 */
	@Nullable
	String getPopup();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code popup}.
	 *
	 * <p>Sets the {@code ID} of the {@link Popup popup} that should appear
	 * when the user clicks on the component.
	 *
	 * <p>An {@code onOpen} action is triggered to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on {@link OpenData#getReference()}) by listening to the
	 * {@code onOpen} action.
	 *
	 * <p> The popup can be shown by a position
	 * or the location of {@code x} and {@code y}, you can specify the following format:</br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * {@code .withPopup("id, start_before")}
	 * </pre>
	 * <p> The popup can also be shown on customized location of {@code x} and {@code y}
	 * by adding parentheses {@code "()"}, for example,
	 * <pre>
	 * {@code .withPopup("id, x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)")}
	 * </pre>
	 *
	 * <p> Possible values for the position attribute are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the popup appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the popup appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the popup appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the popup appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the popup appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the popup at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * <b>Note:</b> The position will be ignored if coordinates are set.
	 * @param popup The {@code ID} of {@link IPopup}
	 * @return A modified copy of the {@code this} object
	 */
	I withPopup(@Nullable String popup);

	/** Returns the ID of the popup ({@link IPopup}) that should be used
	 * as a tooltip window when the mouse hovers over the component for a moment.
	 * The tooltip will automatically disappear when the mouse is moved away.
	 *
	 * <p>Default: {@code null} (no tooltip).</p>
	 */
	@Nullable
	String getTooltip();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code tooltip}.
	 *
	 * <p>Sets the {@code ID} of the {@link Popup popup} that should be used
	 * as a tooltip window when the mouse hovers over the component for a moment.
	 *
	 * <p>An {@code onOpen} action is triggered to the tooltip if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on {@link OpenData#getReference()}) by listening to the
	 * {@code onOpen} action.
	 *
	 * <p> The tooltip can be shown by a position
	 * or the location of {@code x} and {@code y}, and can be specified
	 * with a delay time (in millisecond), you can specify the following format:
	 * <br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start, delay=500</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * {@code .withTooltip("id, start_before")}
	 * </pre>
	 * <p> The tooltip can also be shown on customized location of {@code x} and {@code y}
	 * by adding parentheses {@code "()"}, for example,
	 * <pre>
	 * {@code .withTooltip("id, x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)")}
	 * </pre>
	 *
	 * <p> Possible values for the position attribute are:
	 * <ul>
	 * 	<li><b>before_start</b><br/> the popup appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the popup appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the popup appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the popup appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the popup appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the popup appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the popup appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the popup appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the popup appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the popup appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the popup appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the popup appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the popup overlaps the anchor, with anchor and popup aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the popup appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the popup appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the popup at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * <b>Note:</b> The position will be ignored if coordinates are set.
	 * @param tooltip The {@code ID} of {@link IPopup}
	 * @return A modified copy of the {@code this} object
	 */
	I withTooltip(@Nullable String tooltip);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	// same as XulElement#renderProperties(ContentRenderer)
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		render(renderer, "popup", getPopup());
		render(renderer, "context", getContext());
		// ZK-816
		render(renderer, "tooltip", getTooltip());
		render(renderer, "ctrlKeys", getCtrlKeys());
	}
}