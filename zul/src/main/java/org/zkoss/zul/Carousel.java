/* Carousel.java

	Purpose:

	Description:

	History:
		Wed Apr 22 2026, Created by yuehfeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A carousel (slideshow) container. Children must be {@link Carouselitem}.
 *
 * <p>Default {@link #getZclass}: "z-carousel".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Carousel extends XulElement {
	private static final long serialVersionUID = 6938934209815550099L;

	static {
		addClientEvent(Carousel.class, Events.ON_SELECT, CE_IMPORTANT | CE_DUPLICATE_IGNORE);
		// onChanging is sent before the active index actually changes — useful
		// for lazy-loading the next slide's image, or for prefetch. Posted as an
		// InputEvent: getValue() is the index being moved to (a String per the
		// InputEvent contract — parse with Integer.parseInt), getPreviousValue()
		// is the index being left (an Integer). Not cancelable: returning early
		// or throwing from the handler does not abort the change.
		addClientEvent(Carousel.class, Events.ON_CHANGING, CE_DUPLICATE_IGNORE);
	}

	private int _activeIndex;
	private boolean _autoplay;
	private int _interval = 5000;
	private boolean _showArrows = true;
	private boolean _showIndicators = true;
	private boolean _loop = true;
	private boolean _pause = true;
	private boolean _keyboard = true;
	private String _orient = "horizontal";
	private String _effect = "slide";

	public Carousel() {
	}

	/** Returns the index of the currently active slide.
	 * <p>Matches what {@link #renderProperties} actually sends to the client:
	 * when the stored {@code _activeIndex} exceeds the current child count
	 * (e.g. ZUL set <code>activeIndex='5'</code> before adding children),
	 * the value is clamped to {@code count - 1} for both render and getter
	 * so callers never observe an index that doesn't correspond to a real
	 * slide.
	 */
	public int getActiveIndex() {
		final int count = getChildren().size();
		if (count > 0 && _activeIndex >= count)
			return count - 1;
		return _activeIndex;
	}

	/** Sets the index of the currently active slide.
	 * @param activeIndex the zero-based index of the slide to activate; must be
	 *     in {@code [0, slideCount-1]}. Setting a different value pushes the new
	 *     value to the client via {@code smartUpdate}.
	 * @throws WrongValueException if {@code activeIndex} is negative, or is not
	 *     less than the current number of slides.
	 */
	public void setActiveIndex(int activeIndex) throws WrongValueException {
		if (activeIndex < 0)
			throw new WrongValueException("activeIndex cannot be negative: " + activeIndex);
		final int count = getChildren().size();
		if (count > 0 && activeIndex >= count)
			throw new WrongValueException(
					"activeIndex must be less than the number of slides ("
							+ count + "): " + activeIndex);
		if (_activeIndex != activeIndex) {
			_activeIndex = activeIndex;
			smartUpdate("activeIndex", _activeIndex);
		}
	}

	/** Returns whether the carousel advances through its slides automatically.
	 * <p>Default: false.
	 * @since 10.4.0
	 */
	public boolean isAutoplay() {
		return _autoplay;
	}

	/** Sets whether the carousel advances through its slides automatically.
	 * @param autoplay true to start advancing slides automatically (using the
	 *     {@link #getInterval interval}), false to leave navigation manual.
	 */
	public void setAutoplay(boolean autoplay) {
		if (_autoplay != autoplay) {
			_autoplay = autoplay;
			smartUpdate("autoplay", _autoplay);
		}
	}

	/** Returns the autoplay interval in milliseconds.
	 * <p>Default: 5000.
	 * @since 10.4.0
	 */
	public int getInterval() {
		return _interval;
	}

	/** Sets the autoplay interval in milliseconds.
	 * @throws WrongValueException if {@code interval} is less than 500ms — the
	 *     animation cannot reliably finish (and screen readers cannot
	 *     announce the slide change) inside a tighter window.
	 */
	public void setInterval(int interval) throws WrongValueException {
		if (interval < 500)
			throw new WrongValueException("interval must be >= 500ms: " + interval);
		if (_interval != interval) {
			_interval = interval;
			smartUpdate("interval", _interval);
		}
	}

	/** Returns whether the previous/next navigation arrows are shown.
	 * <p>Default: true.
	 * @since 10.4.0
	 */
	public boolean isShowArrows() {
		return _showArrows;
	}

	/** Sets whether the previous/next navigation arrows are shown.
	 * @param showArrows true to display the navigation arrows, false to hide them.
	 */
	public void setShowArrows(boolean showArrows) {
		if (_showArrows != showArrows) {
			_showArrows = showArrows;
			smartUpdate("showArrows", _showArrows);
		}
	}

	/** Returns whether the slide indicators (the position dots) are shown.
	 * <p>Default: true.
	 * @since 10.4.0
	 */
	public boolean isShowIndicators() {
		return _showIndicators;
	}

	/** Sets whether the slide indicators (the position dots) are shown.
	 * @param showIndicators true to display the indicators, false to hide them.
	 */
	public void setShowIndicators(boolean showIndicators) {
		if (_showIndicators != showIndicators) {
			_showIndicators = showIndicators;
			smartUpdate("showIndicators", _showIndicators);
		}
	}

	/** Returns whether the carousel wraps around from the last slide back to the
	 * first (and vice versa).
	 * <p>Default: true.
	 * @since 10.4.0
	 */
	public boolean isLoop() {
		return _loop;
	}

	/** Sets whether the carousel wraps around from the last slide back to the
	 * first (and vice versa).
	 * @param loop true to wrap around continuously, false to stop at the ends.
	 */
	public void setLoop(boolean loop) {
		if (_loop != loop) {
			_loop = loop;
			smartUpdate("loop", _loop);
		}
	}

	/** Whether autoplay pauses while the cursor hovers over the carousel.
	 * Mirrors Bootstrap's <code>data-bs-pause="hover"</code> behavior.
	 * <p>Default: true.
	 * @since 10.4.0
	 */
	public boolean isPause() {
		return _pause;
	}

	/** Sets whether autoplay pauses while the cursor hovers over the carousel.
	 * @param pause true to pause autoplay on hover, false to keep advancing.
	 */
	public void setPause(boolean pause) {
		if (_pause != pause) {
			_pause = pause;
			smartUpdate("pause", _pause);
		}
	}

	/** Whether the carousel reacts to left/right arrow keys when focused.
	 * Mirrors Bootstrap's <code>keyboard</code> option.
	 * <p>Default: true.
	 * @since 10.4.0
	 */
	public boolean isKeyboard() {
		return _keyboard;
	}

	/** Sets whether the carousel reacts to left/right arrow keys when focused.
	 * @param keyboard true to enable arrow-key navigation, false to disable it.
	 */
	public void setKeyboard(boolean keyboard) {
		if (_keyboard != keyboard) {
			_keyboard = keyboard;
			smartUpdate("keyboard", _keyboard);
		}
	}

	/** Returns the orientation along which the slides advance.
	 * <p>Default: "horizontal".
	 * @since 10.4.0
	 */
	public String getOrient() {
		return _orient;
	}

	/** Sets the orientation along which the slides advance.
	 * @param orient one of "horizontal" or "vertical"; a null value is treated
	 *     as "horizontal".
	 * @throws WrongValueException if {@code orient} is neither "horizontal" nor
	 *     "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		orient = Utils.checkEnum(orient, "horizontal", "orient must be horizontal or vertical: ", "horizontal", "vertical");
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	/** Returns the transition effect used when moving between slides.
	 * <p>Default: "slide".
	 * @since 10.4.0
	 */
	public String getEffect() {
		return _effect;
	}

	/** Sets the transition effect used when moving between slides.
	 * @param effect one of "slide", "fade" or "none"; a null value is treated
	 *     as "slide".
	 * @throws WrongValueException if {@code effect} is none of "slide", "fade"
	 *     or "none".
	 */
	public void setEffect(String effect) throws WrongValueException {
		effect = Utils.checkEnum(effect, "slide", "effect must be slide/fade/none: ", "slide", "fade", "none");
		if (!Objects.equals(_effect, effect)) {
			_effect = effect;
			smartUpdate("effect", _effect);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-carousel" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		// Compute a clamped effective index without mutating _activeIndex so
		// render stays side-effect-free — `getActiveIndex()` always reflects
		// exactly what the developer set, even if it's currently out of
		// range. The clamp matters only for ZUL pages where setActiveIndex(N)
		// appears before the N-th carouselitem in the file; the setter and
		// onChildRemoved handle the other paths.
		final int count = getChildren().size();
		final int effectiveIdx = (count > 0 && _activeIndex >= count) ? count - 1 : _activeIndex;
		super.renderProperties(renderer);
		render(renderer, "activeIndex", effectiveIdx);
		render(renderer, "autoplay", _autoplay);
		if (_interval != 5000)
			render(renderer, "interval", _interval);
		if (!_showArrows)
			renderer.render("showArrows", false);
		if (!_showIndicators)
			renderer.render("showIndicators", false);
		if (!_loop)
			renderer.render("loop", false);
		if (!_pause)
			renderer.render("pause", false);
		if (!_keyboard)
			renderer.render("keyboard", false);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (!"slide".equals(_effect))
			render(renderer, "effect", _effect);
	}

	//-- Component --//
	@Override
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Carouselitem))
			throw new UiException("Unsupported child for carousel: " + child);
		super.beforeChildAdded(child, refChild);
	}

	@Override
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		// Deliberately do NOT clamp `_activeIndex` here. ZUL evaluates
		// `activeIndex="N"` before attaching children, and children arrive
		// one at a time — re-clamping per add would force the value down on
		// the first attach (e.g. `activeIndex=1` with two slides clamps to
		// 0 when only the first slide has arrived) and never come back up.
		// `getActiveIndex()` and `renderProperties` already clamp lazily at
		// read time against the current child count, so the rendered state
		// stays correct without mutating the stored value mid-add.
		//
		// Force a re-render so the client widget tears down + re-installs
		// its head/tail clones around the new child set. Without this, a
		// dynamic appendChild after bind_ lands AFTER the head clone in DOM
		// order, breaking the loop-mode layout. The cost (one invalidate
		// per add) is acceptable for carousels — programmatically adding
		// a slide is expected to be a coarse-grained operation.
		invalidate();
	}

	@Override
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		// Keep _activeIndex within [0, size-1] when a slide is detached.
		// Without this, removing the active or any earlier slide leaves
		// _activeIndex pointing past the end and getSelectedItem() returns
		// null while items still exist.
		final int count = getChildren().size();
		if (count == 0) {
			if (_activeIndex != 0) {
				_activeIndex = 0;
				smartUpdate("activeIndex", _activeIndex);
			}
		} else if (_activeIndex >= count) {
			_activeIndex = count - 1;
			smartUpdate("activeIndex", _activeIndex);
		}
		// Same reasoning as onChildAdded — invalidate so the client
		// rebuilds clones around the new child set.
		invalidate();
	}

	@Override
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		// Honor the framework's everError suppression for our matched commands
		// too — without this short-circuit the validated Events.postEvent calls
		// below would still fire inside an already-errored request chain,
		// running late listeners against potentially-inconsistent state.
		// Unmatched commands fall through to super, which has its own
		// everError handling.
		if (everError && (cmd.equals(Events.ON_SELECT) || cmd.equals(Events.ON_CHANGING)))
			return;
		if (cmd.equals(Events.ON_SELECT)) {
			final Map<String, Object> data = request.getData();
			// Guard against malformed AU payload (missing/non-integer index)
			// and out-of-range values — the client may race the server's
			// onChildRemoved. Only post onSelect when the index is genuinely
			// usable; the alternatives are both worse:
			//  - Falling through to super would dispatch the raw map, which
			//    may carry attacker-controlled extra keys.
			//  - Clamping to [0, count-1] would fire a listener targeting a
			//    different slide than the user thinks they clicked.
			// Listeners that need to know about the dropped click should
			// reconcile via onChildRemoved / the next valid onSelect.
			final Object idxObj = data.get("index");
			// Accept any Number — JSON parsers may decode small ints as
			// Integer / Long / Double depending on size and configuration.
			// Stripping to Integer-only would silently drop legitimate
			// payloads from non-default parsers.
			if (idxObj instanceof Number) {
				final int idx = ((Number) idxObj).intValue();
				final int count = getChildren().size();
				if (idx >= 0 && idx < count) {
					_activeIndex = idx;
					// Post a SelectEvent so listeners with the conventional
					// `onSelect(SelectEvent)` signature can call
					// evt.getReference() / evt.getSelectedItems() and receive
					// a typed Carouselitem instead of digging into the raw
					// data map. The data map is still populated (with only
					// the validated "index" key) so existing bindings such as
					// `event.data.index` / `event.getData().get("index")`
					// continue to resolve — rebuilding the map locally also
					// strips any attacker-controlled extra keys that
					// request.getData() may carry.
					final Carouselitem item = (Carouselitem) getChildren().get(idx);
					Events.postEvent(new org.zkoss.zk.ui.event.SelectEvent<Carouselitem, Object>(
							Events.ON_SELECT, this,
							java.util.Collections.singleton(item),
							null, null, null, null, null,
							item,
							java.util.Collections.singletonMap("index", idx),
							0));
				}
			}
		} else if (cmd.equals(Events.ON_CHANGING)) {
			final Map<String, Object> data = request.getData();
			final Object fromObj = data.get("fromIndex");
			final Object toObj = data.get("toIndex");
			if (fromObj instanceof Number && toObj instanceof Number) {
				final int fromIdx = ((Number) fromObj).intValue();
				final int toIdx = ((Number) toObj).intValue();
				// Mirror the ON_SELECT range-check so listeners never see
				// out-of-range indices from a hostile or racing client.
				final int count = getChildren().size();
				if (fromIdx >= 0 && fromIdx < count
						&& toIdx >= 0 && toIdx < count) {
					// Post an InputEvent so listeners read the change through
					// the conventional onChanging contract: getValue() is the
					// index being moved to, getPreviousValue() the index being
					// left. Rebuilding the payload locally also drops any
					// attacker-controlled extra keys request.getData() carries.
					Events.postEvent(new InputEvent(Events.ON_CHANGING, this,
							String.valueOf(toIdx), fromIdx));
				}
			}
		} else {
			super.service(request, everError);
		}
	}

	/** Returns the currently active item, or null when there are no slides.
	 * <p>The lookup uses the same clamped index as {@link #getActiveIndex} so
	 * the two getters stay in agreement when {@code _activeIndex} was set
	 * out of range before any child was attached.
	 */
	public Carouselitem getSelectedItem() {
		java.util.List<?> children = getChildren();
		int idx = getActiveIndex();
		if (idx >= 0 && idx < children.size())
			return (Carouselitem) children.get(idx);
		return null;
	}
}
