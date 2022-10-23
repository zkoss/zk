// **************************************************************************
// Code Generator: DO NOT modify the content directly
// **************************************************************************

/* ActionType.java

	Purpose:

	Description:

	History:
		2:21 PM 2021/10/6, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.util.ActionHandler0;
import org.zkoss.zephyr.util.ActionHandler1;
import org.zkoss.zephyr.util.ActionHandler2;
import org.zkoss.zephyr.util.ActionHandler3;
import org.zkoss.zephyr.util.ActionHandler4;
import org.zkoss.zephyr.util.ActionHandler5;
import org.zkoss.zephyr.util.ActionHandler6;
import org.zkoss.zephyr.util.ActionHandler7;
import org.zkoss.zephyr.util.ActionHandler8;
import org.zkoss.zephyr.util.ActionHandler9;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zk.ui.event.Events;

/**
 * Action type util interface to wrap a Lambda function or functional interface
 * into a specific {@link Action}, such as {@link #onClick(OnClick0)}.
 *
 * For example,
 * <pre><code>
 * ILabel.of("my label").withAction(onClick((){@code ->} System.out.println("clicked")));
 * </code></pre>
 * @author jumperchen
 */
public interface ActionType {

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage0 extends ActionHandler0 {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_ACROSS_PAGE)
	interface OnAcrossPage9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnAcrossPage0 onAcrossPage(OnAcrossPage0 handler) {
		return handler;
	}

	static <A> OnAcrossPage1<A> onAcrossPage(OnAcrossPage1<A> handler) {
		return handler;
	}

	static <A, B> OnAcrossPage2<A, B> onAcrossPage(OnAcrossPage2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnAcrossPage3<A, B, C> onAcrossPage(OnAcrossPage3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnAcrossPage4<A, B, C, D> onAcrossPage(OnAcrossPage4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnAcrossPage5<A, B, C, D, E> onAcrossPage(OnAcrossPage5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnAcrossPage6<A, B, C, D, E, F> onAcrossPage(OnAcrossPage6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnAcrossPage7<A, B, C, D, E, F, G> onAcrossPage(OnAcrossPage7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnAcrossPage8<A, B, C, D, E, F, G, H> onAcrossPage(OnAcrossPage8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnAcrossPage9<A, B, C, D, E, F, G, H, I> onAcrossPage(OnAcrossPage9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender0 extends ActionHandler0 {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender1<A> extends ActionHandler1<A> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = ZulEvents.ON_AFTER_RENDER)
	interface OnAfterRender9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnAfterRender0 onAfterRender(OnAfterRender0 handler) {
		return handler;
	}

	static <A> OnAfterRender1<A> onAfterRender(OnAfterRender1<A> handler) {
		return handler;
	}

	static <A, B> OnAfterRender2<A, B> onAfterRender(OnAfterRender2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnAfterRender3<A, B, C> onAfterRender(OnAfterRender3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnAfterRender4<A, B, C, D> onAfterRender(OnAfterRender4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnAfterRender5<A, B, C, D, E> onAfterRender(OnAfterRender5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnAfterRender6<A, B, C, D, E, F> onAfterRender(OnAfterRender6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnAfterRender7<A, B, C, D, E, F, G> onAfterRender(OnAfterRender7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnAfterRender8<A, B, C, D, E, F, G, H> onAfterRender(OnAfterRender8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnAfterRender9<A, B, C, D, E, F, G, H, I> onAfterRender(OnAfterRender9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize0 extends ActionHandler0 {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_AFTER_SIZE)
	interface OnAfterSize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnAfterSize0 onAfterSize(OnAfterSize0 handler) {
		return handler;
	}

	static <A> OnAfterSize1<A> onAfterSize(OnAfterSize1<A> handler) {
		return handler;
	}

	static <A, B> OnAfterSize2<A, B> onAfterSize(OnAfterSize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnAfterSize3<A, B, C> onAfterSize(OnAfterSize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnAfterSize4<A, B, C, D> onAfterSize(OnAfterSize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnAfterSize5<A, B, C, D, E> onAfterSize(OnAfterSize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnAfterSize6<A, B, C, D, E, F> onAfterSize(OnAfterSize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnAfterSize7<A, B, C, D, E, F, G> onAfterSize(OnAfterSize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnAfterSize8<A, B, C, D, E, F, G, H> onAfterSize(OnAfterSize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnAfterSize9<A, B, C, D, E, F, G, H, I> onAfterSize(OnAfterSize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos0 extends ActionHandler0 {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_ANCHOR_POS)
	interface OnAnchorPos9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnAnchorPos0 onAnchorPos(OnAnchorPos0 handler) {
		return handler;
	}

	static <A> OnAnchorPos1<A> onAnchorPos(OnAnchorPos1<A> handler) {
		return handler;
	}

	static <A, B> OnAnchorPos2<A, B> onAnchorPos(OnAnchorPos2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnAnchorPos3<A, B, C> onAnchorPos(OnAnchorPos3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnAnchorPos4<A, B, C, D> onAnchorPos(OnAnchorPos4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnAnchorPos5<A, B, C, D, E> onAnchorPos(OnAnchorPos5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnAnchorPos6<A, B, C, D, E, F> onAnchorPos(OnAnchorPos6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnAnchorPos7<A, B, C, D, E, F, G> onAnchorPos(OnAnchorPos7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnAnchorPos8<A, B, C, D, E, F, G, H> onAnchorPos(OnAnchorPos8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnAnchorPos9<A, B, C, D, E, F, G, H, I> onAnchorPos(OnAnchorPos9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_BLUR)
	interface OnBlur0 extends ActionHandler0 {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_BLUR)
	interface OnBlur9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnBlur0 onBlur(OnBlur0 handler) {
		return handler;
	}

	static <A> OnBlur1<A> onBlur(OnBlur1<A> handler) {
		return handler;
	}

	static <A, B> OnBlur2<A, B> onBlur(OnBlur2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnBlur3<A, B, C> onBlur(OnBlur3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnBlur4<A, B, C, D> onBlur(OnBlur4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnBlur5<A, B, C, D, E> onBlur(OnBlur5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnBlur6<A, B, C, D, E, F> onBlur(OnBlur6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnBlur7<A, B, C, D, E, F, G> onBlur(OnBlur7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnBlur8<A, B, C, D, E, F, G, H> onBlur(OnBlur8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnBlur9<A, B, C, D, E, F, G, H, I> onBlur(OnBlur9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_BOOKMARK_CHANGE)
	interface OnBookmarkChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnBookmarkChange0 onBookmarkChange(OnBookmarkChange0 handler) {
		return handler;
	}

	static <A> OnBookmarkChange1<A> onBookmarkChange(OnBookmarkChange1<A> handler) {
		return handler;
	}

	static <A, B> OnBookmarkChange2<A, B> onBookmarkChange(OnBookmarkChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnBookmarkChange3<A, B, C> onBookmarkChange(OnBookmarkChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnBookmarkChange4<A, B, C, D> onBookmarkChange(OnBookmarkChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnBookmarkChange5<A, B, C, D, E> onBookmarkChange(OnBookmarkChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnBookmarkChange6<A, B, C, D, E, F> onBookmarkChange(OnBookmarkChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnBookmarkChange7<A, B, C, D, E, F, G> onBookmarkChange(OnBookmarkChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnBookmarkChange8<A, B, C, D, E, F, G, H> onBookmarkChange(OnBookmarkChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnBookmarkChange9<A, B, C, D, E, F, G, H, I> onBookmarkChange(OnBookmarkChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel0 extends ActionHandler0 {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CANCEL)
	interface OnCancel9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnCancel0 onCancel(OnCancel0 handler) {
		return handler;
	}

	static <A> OnCancel1<A> onCancel(OnCancel1<A> handler) {
		return handler;
	}

	static <A, B> OnCancel2<A, B> onCancel(OnCancel2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnCancel3<A, B, C> onCancel(OnCancel3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnCancel4<A, B, C, D> onCancel(OnCancel4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnCancel5<A, B, C, D, E> onCancel(OnCancel5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnCancel6<A, B, C, D, E, F> onCancel(OnCancel6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnCancel7<A, B, C, D, E, F, G> onCancel(OnCancel7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnCancel8<A, B, C, D, E, F, G, H> onCancel(OnCancel8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnCancel9<A, B, C, D, E, F, G, H, I> onCancel(OnCancel9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CHANGE)
	interface OnChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CHANGE)
	interface OnChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnChange0 onChange(OnChange0 handler) {
		return handler;
	}

	static <A> OnChange1<A> onChange(OnChange1<A> handler) {
		return handler;
	}

	static <A, B> OnChange2<A, B> onChange(OnChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnChange3<A, B, C> onChange(OnChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnChange4<A, B, C, D> onChange(OnChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnChange5<A, B, C, D, E> onChange(OnChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnChange6<A, B, C, D, E, F> onChange(OnChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnChange7<A, B, C, D, E, F, G> onChange(OnChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnChange8<A, B, C, D, E, F, G, H> onChange(OnChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnChange9<A, B, C, D, E, F, G, H, I> onChange(OnChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging0 extends ActionHandler0 {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CHANGING)
	interface OnChanging9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnChanging0 onChanging(OnChanging0 handler) {
		return handler;
	}

	static <A> OnChanging1<A> onChanging(OnChanging1<A> handler) {
		return handler;
	}

	static <A, B> OnChanging2<A, B> onChanging(OnChanging2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnChanging3<A, B, C> onChanging(OnChanging3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnChanging4<A, B, C, D> onChanging(OnChanging4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnChanging5<A, B, C, D, E> onChanging(OnChanging5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnChanging6<A, B, C, D, E, F> onChanging(OnChanging6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnChanging7<A, B, C, D, E, F, G> onChanging(OnChanging7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnChanging8<A, B, C, D, E, F, G, H> onChanging(OnChanging8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnChanging9<A, B, C, D, E, F, G, H, I> onChanging(OnChanging9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CHECK)
	interface OnCheck0 extends ActionHandler0 {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CHECK)
	interface OnCheck9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnCheck0 onCheck(OnCheck0 handler) {
		return handler;
	}

	static <A> OnCheck1<A> onCheck(OnCheck1<A> handler) {
		return handler;
	}

	static <A, B> OnCheck2<A, B> onCheck(OnCheck2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnCheck3<A, B, C> onCheck(OnCheck3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnCheck4<A, B, C, D> onCheck(OnCheck4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnCheck5<A, B, C, D, E> onCheck(OnCheck5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnCheck6<A, B, C, D, E, F> onCheck(OnCheck6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnCheck7<A, B, C, D, E, F, G> onCheck(OnCheck7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnCheck8<A, B, C, D, E, F, G, H> onCheck(OnCheck8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnCheck9<A, B, C, D, E, F, G, H, I> onCheck(OnCheck9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll0 extends ActionHandler0 {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CHECK_SELECT_ALL)
	interface OnCheckSelectAll9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnCheckSelectAll0 onCheckSelectAll(OnCheckSelectAll0 handler) {
		return handler;
	}

	static <A> OnCheckSelectAll1<A> onCheckSelectAll(OnCheckSelectAll1<A> handler) {
		return handler;
	}

	static <A, B> OnCheckSelectAll2<A, B> onCheckSelectAll(OnCheckSelectAll2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnCheckSelectAll3<A, B, C> onCheckSelectAll(OnCheckSelectAll3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnCheckSelectAll4<A, B, C, D> onCheckSelectAll(OnCheckSelectAll4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnCheckSelectAll5<A, B, C, D, E> onCheckSelectAll(OnCheckSelectAll5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnCheckSelectAll6<A, B, C, D, E, F> onCheckSelectAll(OnCheckSelectAll6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnCheckSelectAll7<A, B, C, D, E, F, G> onCheckSelectAll(OnCheckSelectAll7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnCheckSelectAll8<A, B, C, D, E, F, G, H> onCheckSelectAll(OnCheckSelectAll8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnCheckSelectAll9<A, B, C, D, E, F, G, H, I> onCheckSelectAll(OnCheckSelectAll9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CLICK)
	interface OnClick0 extends ActionHandler0 {}

	@Action(type = Events.ON_CLICK)
	interface OnClick1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CLICK)
	interface OnClick9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnClick0 onClick(OnClick0 handler) {
		return handler;
	}

	static <A> OnClick1<A> onClick(OnClick1<A> handler) {
		return handler;
	}

	static <A, B> OnClick2<A, B> onClick(OnClick2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnClick3<A, B, C> onClick(OnClick3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnClick4<A, B, C, D> onClick(OnClick4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnClick5<A, B, C, D, E> onClick(OnClick5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnClick6<A, B, C, D, E, F> onClick(OnClick6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnClick7<A, B, C, D, E, F, G> onClick(OnClick7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnClick8<A, B, C, D, E, F, G, H> onClick(OnClick8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnClick9<A, B, C, D, E, F, G, H, I> onClick(OnClick9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo0 extends ActionHandler0 {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CLIENT_INFO)
	interface OnClientInfo9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnClientInfo0 onClientInfo(OnClientInfo0 handler) {
		return handler;
	}

	static <A> OnClientInfo1<A> onClientInfo(OnClientInfo1<A> handler) {
		return handler;
	}

	static <A, B> OnClientInfo2<A, B> onClientInfo(OnClientInfo2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnClientInfo3<A, B, C> onClientInfo(OnClientInfo3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnClientInfo4<A, B, C, D> onClientInfo(OnClientInfo4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnClientInfo5<A, B, C, D, E> onClientInfo(OnClientInfo5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnClientInfo6<A, B, C, D, E, F> onClientInfo(OnClientInfo6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnClientInfo7<A, B, C, D, E, F, G> onClientInfo(OnClientInfo7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnClientInfo8<A, B, C, D, E, F, G, H> onClientInfo(OnClientInfo8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnClientInfo9<A, B, C, D, E, F, G, H, I> onClientInfo(OnClientInfo9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CLOSE)
	interface OnClose0 extends ActionHandler0 {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CLOSE)
	interface OnClose9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnClose0 onClose(OnClose0 handler) {
		return handler;
	}

	static <A> OnClose1<A> onClose(OnClose1<A> handler) {
		return handler;
	}

	static <A, B> OnClose2<A, B> onClose(OnClose2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnClose3<A, B, C> onClose(OnClose3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnClose4<A, B, C, D> onClose(OnClose4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnClose5<A, B, C, D, E> onClose(OnClose5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnClose6<A, B, C, D, E, F> onClose(OnClose6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnClose7<A, B, C, D, E, F, G> onClose(OnClose7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnClose8<A, B, C, D, E, F, G, H> onClose(OnClose8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnClose9<A, B, C, D, E, F, G, H, I> onClose(OnClose9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize0 extends ActionHandler0 {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize1<A> extends ActionHandler1<A> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = ZulEvents.ON_COL_SIZE)
	interface OnColSize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnColSize0 onColSize(OnColSize0 handler) {
		return handler;
	}

	static <A> OnColSize1<A> onColSize(OnColSize1<A> handler) {
		return handler;
	}

	static <A, B> OnColSize2<A, B> onColSize(OnColSize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnColSize3<A, B, C> onColSize(OnColSize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnColSize4<A, B, C, D> onColSize(OnColSize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnColSize5<A, B, C, D, E> onColSize(OnColSize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnColSize6<A, B, C, D, E, F> onColSize(OnColSize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnColSize7<A, B, C, D, E, F, G> onColSize(OnColSize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnColSize8<A, B, C, D, E, F, G, H> onColSize(OnColSize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnColSize9<A, B, C, D, E, F, G, H, I> onColSize(OnColSize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CREATE)
	interface OnCreate0 extends ActionHandler0 {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CREATE)
	interface OnCreate9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnCreate0 onCreate(OnCreate0 handler) {
		return handler;
	}

	static <A> OnCreate1<A> onCreate(OnCreate1<A> handler) {
		return handler;
	}

	static <A, B> OnCreate2<A, B> onCreate(OnCreate2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnCreate3<A, B, C> onCreate(OnCreate3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnCreate4<A, B, C, D> onCreate(OnCreate4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnCreate5<A, B, C, D, E> onCreate(OnCreate5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnCreate6<A, B, C, D, E, F> onCreate(OnCreate6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnCreate7<A, B, C, D, E, F, G> onCreate(OnCreate7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnCreate8<A, B, C, D, E, F, G, H> onCreate(OnCreate8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnCreate9<A, B, C, D, E, F, G, H, I> onCreate(OnCreate9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey0 extends ActionHandler0 {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_CTRL_KEY)
	interface OnCtrlKey9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnCtrlKey0 onCtrlKey(OnCtrlKey0 handler) {
		return handler;
	}

	static <A> OnCtrlKey1<A> onCtrlKey(OnCtrlKey1<A> handler) {
		return handler;
	}

	static <A, B> OnCtrlKey2<A, B> onCtrlKey(OnCtrlKey2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnCtrlKey3<A, B, C> onCtrlKey(OnCtrlKey3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnCtrlKey4<A, B, C, D> onCtrlKey(OnCtrlKey4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnCtrlKey5<A, B, C, D, E> onCtrlKey(OnCtrlKey5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnCtrlKey6<A, B, C, D, E, F> onCtrlKey(OnCtrlKey6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnCtrlKey7<A, B, C, D, E, F, G> onCtrlKey(OnCtrlKey7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnCtrlKey8<A, B, C, D, E, F, G, H> onCtrlKey(OnCtrlKey8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnCtrlKey9<A, B, C, D, E, F, G, H, I> onCtrlKey(OnCtrlKey9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading0 extends ActionHandler0 {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_DATA_LOADING)
	interface OnDataLoading9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnDataLoading0 onDataLoading(OnDataLoading0 handler) {
		return handler;
	}

	static <A> OnDataLoading1<A> onDataLoading(OnDataLoading1<A> handler) {
		return handler;
	}

	static <A, B> OnDataLoading2<A, B> onDataLoading(OnDataLoading2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnDataLoading3<A, B, C> onDataLoading(OnDataLoading3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnDataLoading4<A, B, C, D> onDataLoading(OnDataLoading4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnDataLoading5<A, B, C, D, E> onDataLoading(OnDataLoading5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnDataLoading6<A, B, C, D, E, F> onDataLoading(OnDataLoading6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnDataLoading7<A, B, C, D, E, F, G> onDataLoading(OnDataLoading7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnDataLoading8<A, B, C, D, E, F, G, H> onDataLoading(OnDataLoading8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnDataLoading9<A, B, C, D, E, F, G, H, I> onDataLoading(OnDataLoading9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation0 extends ActionHandler0 {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_DEFERRED_EVALUATION)
	interface OnDeferredEvaluation9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnDeferredEvaluation0 onDeferredEvaluation(OnDeferredEvaluation0 handler) {
		return handler;
	}

	static <A> OnDeferredEvaluation1<A> onDeferredEvaluation(OnDeferredEvaluation1<A> handler) {
		return handler;
	}

	static <A, B> OnDeferredEvaluation2<A, B> onDeferredEvaluation(OnDeferredEvaluation2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnDeferredEvaluation3<A, B, C> onDeferredEvaluation(OnDeferredEvaluation3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnDeferredEvaluation4<A, B, C, D> onDeferredEvaluation(OnDeferredEvaluation4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnDeferredEvaluation5<A, B, C, D, E> onDeferredEvaluation(OnDeferredEvaluation5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnDeferredEvaluation6<A, B, C, D, E, F> onDeferredEvaluation(OnDeferredEvaluation6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnDeferredEvaluation7<A, B, C, D, E, F, G> onDeferredEvaluation(OnDeferredEvaluation7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnDeferredEvaluation8<A, B, C, D, E, F, G, H> onDeferredEvaluation(OnDeferredEvaluation8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnDeferredEvaluation9<A, B, C, D, E, F, G, H, I> onDeferredEvaluation(OnDeferredEvaluation9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle0 extends ActionHandler0 {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_DESKTOP_RECYCLE)
	interface OnDesktopRecycle9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnDesktopRecycle0 onDesktopRecycle(OnDesktopRecycle0 handler) {
		return handler;
	}

	static <A> OnDesktopRecycle1<A> onDesktopRecycle(OnDesktopRecycle1<A> handler) {
		return handler;
	}

	static <A, B> OnDesktopRecycle2<A, B> onDesktopRecycle(OnDesktopRecycle2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnDesktopRecycle3<A, B, C> onDesktopRecycle(OnDesktopRecycle3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnDesktopRecycle4<A, B, C, D> onDesktopRecycle(OnDesktopRecycle4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnDesktopRecycle5<A, B, C, D, E> onDesktopRecycle(OnDesktopRecycle5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnDesktopRecycle6<A, B, C, D, E, F> onDesktopRecycle(OnDesktopRecycle6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnDesktopRecycle7<A, B, C, D, E, F, G> onDesktopRecycle(OnDesktopRecycle7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnDesktopRecycle8<A, B, C, D, E, F, G, H> onDesktopRecycle(OnDesktopRecycle8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnDesktopRecycle9<A, B, C, D, E, F, G, H, I> onDesktopRecycle(OnDesktopRecycle9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick0 extends ActionHandler0 {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_DOUBLE_CLICK)
	interface OnDoubleClick9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnDoubleClick0 onDoubleClick(OnDoubleClick0 handler) {
		return handler;
	}

	static <A> OnDoubleClick1<A> onDoubleClick(OnDoubleClick1<A> handler) {
		return handler;
	}

	static <A, B> OnDoubleClick2<A, B> onDoubleClick(OnDoubleClick2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnDoubleClick3<A, B, C> onDoubleClick(OnDoubleClick3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnDoubleClick4<A, B, C, D> onDoubleClick(OnDoubleClick4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnDoubleClick5<A, B, C, D, E> onDoubleClick(OnDoubleClick5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnDoubleClick6<A, B, C, D, E, F> onDoubleClick(OnDoubleClick6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnDoubleClick7<A, B, C, D, E, F, G> onDoubleClick(OnDoubleClick7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnDoubleClick8<A, B, C, D, E, F, G, H> onDoubleClick(OnDoubleClick8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnDoubleClick9<A, B, C, D, E, F, G, H, I> onDoubleClick(OnDoubleClick9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_DROP)
	interface OnDrop0 extends ActionHandler0 {}

	@Action(type = Events.ON_DROP)
	interface OnDrop1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_DROP)
	interface OnDrop9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnDrop0 onDrop(OnDrop0 handler) {
		return handler;
	}

	static <A> OnDrop1<A> onDrop(OnDrop1<A> handler) {
		return handler;
	}

	static <A, B> OnDrop2<A, B> onDrop(OnDrop2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnDrop3<A, B, C> onDrop(OnDrop3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnDrop4<A, B, C, D> onDrop(OnDrop4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnDrop5<A, B, C, D, E> onDrop(OnDrop5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnDrop6<A, B, C, D, E, F> onDrop(OnDrop6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnDrop7<A, B, C, D, E, F, G> onDrop(OnDrop7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnDrop8<A, B, C, D, E, F, G, H> onDrop(OnDrop8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnDrop9<A, B, C, D, E, F, G, H, I> onDrop(OnDrop9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_ERROR)
	interface OnError0 extends ActionHandler0 {}

	@Action(type = Events.ON_ERROR)
	interface OnError1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_ERROR)
	interface OnError2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_ERROR)
	interface OnError3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_ERROR)
	interface OnError4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_ERROR)
	interface OnError5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_ERROR)
	interface OnError6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_ERROR)
	interface OnError7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_ERROR)
	interface OnError8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_ERROR)
	interface OnError9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnError0 onError(OnError0 handler) {
		return handler;
	}

	static <A> OnError1<A> onError(OnError1<A> handler) {
		return handler;
	}

	static <A, B> OnError2<A, B> onError(OnError2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnError3<A, B, C> onError(OnError3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnError4<A, B, C, D> onError(OnError4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnError5<A, B, C, D, E> onError(OnError5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnError6<A, B, C, D, E, F> onError(OnError6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnError7<A, B, C, D, E, F, G> onError(OnError7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnError8<A, B, C, D, E, F, G, H> onError(OnError8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnError9<A, B, C, D, E, F, G, H, I> onError(OnError9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus0 extends ActionHandler0 {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_FOCUS)
	interface OnFocus9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnFocus0 onFocus(OnFocus0 handler) {
		return handler;
	}

	static <A> OnFocus1<A> onFocus(OnFocus1<A> handler) {
		return handler;
	}

	static <A, B> OnFocus2<A, B> onFocus(OnFocus2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnFocus3<A, B, C> onFocus(OnFocus3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnFocus4<A, B, C, D> onFocus(OnFocus4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnFocus5<A, B, C, D, E> onFocus(OnFocus5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnFocus6<A, B, C, D, E, F> onFocus(OnFocus6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnFocus7<A, B, C, D, E, F, G> onFocus(OnFocus7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnFocus8<A, B, C, D, E, F, G, H> onFocus(OnFocus8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnFocus9<A, B, C, D, E, F, G, H, I> onFocus(OnFocus9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill0 extends ActionHandler0 {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_FULFILL)
	interface OnFulfill9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnFulfill0 onFulfill(OnFulfill0 handler) {
		return handler;
	}

	static <A> OnFulfill1<A> onFulfill(OnFulfill1<A> handler) {
		return handler;
	}

	static <A, B> OnFulfill2<A, B> onFulfill(OnFulfill2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnFulfill3<A, B, C> onFulfill(OnFulfill3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnFulfill4<A, B, C, D> onFulfill(OnFulfill4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnFulfill5<A, B, C, D, E> onFulfill(OnFulfill5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnFulfill6<A, B, C, D, E, F> onFulfill(OnFulfill6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnFulfill7<A, B, C, D, E, F, G> onFulfill(OnFulfill7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnFulfill8<A, B, C, D, E, F, G, H> onFulfill(OnFulfill8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnFulfill9<A, B, C, D, E, F, G, H, I> onFulfill(OnFulfill9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_GROUP)
	interface OnGroup0 extends ActionHandler0 {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_GROUP)
	interface OnGroup9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnGroup0 onGroup(OnGroup0 handler) {
		return handler;
	}

	static <A> OnGroup1<A> onGroup(OnGroup1<A> handler) {
		return handler;
	}

	static <A, B> OnGroup2<A, B> onGroup(OnGroup2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnGroup3<A, B, C> onGroup(OnGroup3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnGroup4<A, B, C, D> onGroup(OnGroup4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnGroup5<A, B, C, D, E> onGroup(OnGroup5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnGroup6<A, B, C, D, E, F> onGroup(OnGroup6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnGroup7<A, B, C, D, E, F, G> onGroup(OnGroup7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnGroup8<A, B, C, D, E, F, G, H> onGroup(OnGroup8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnGroup9<A, B, C, D, E, F, G, H, I> onGroup(OnGroup9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState0 extends ActionHandler0 {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_HISTORY_POP_STATE)
	interface OnHistoryPopState9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnHistoryPopState0 onHistoryPopState(OnHistoryPopState0 handler) {
		return handler;
	}

	static <A> OnHistoryPopState1<A> onHistoryPopState(OnHistoryPopState1<A> handler) {
		return handler;
	}

	static <A, B> OnHistoryPopState2<A, B> onHistoryPopState(OnHistoryPopState2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnHistoryPopState3<A, B, C> onHistoryPopState(OnHistoryPopState3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnHistoryPopState4<A, B, C, D> onHistoryPopState(OnHistoryPopState4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnHistoryPopState5<A, B, C, D, E> onHistoryPopState(OnHistoryPopState5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnHistoryPopState6<A, B, C, D, E, F> onHistoryPopState(OnHistoryPopState6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnHistoryPopState7<A, B, C, D, E, F, G> onHistoryPopState(OnHistoryPopState7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnHistoryPopState8<A, B, C, D, E, F, G, H> onHistoryPopState(OnHistoryPopState8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnHistoryPopState9<A, B, C, D, E, F, G, H, I> onHistoryPopState(OnHistoryPopState9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth0 extends ActionHandler0 {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_INNER_WIDTH)
	interface OnInnerWidth9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnInnerWidth0 onInnerWidth(OnInnerWidth0 handler) {
		return handler;
	}

	static <A> OnInnerWidth1<A> onInnerWidth(OnInnerWidth1<A> handler) {
		return handler;
	}

	static <A, B> OnInnerWidth2<A, B> onInnerWidth(OnInnerWidth2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnInnerWidth3<A, B, C> onInnerWidth(OnInnerWidth3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnInnerWidth4<A, B, C, D> onInnerWidth(OnInnerWidth4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnInnerWidth5<A, B, C, D, E> onInnerWidth(OnInnerWidth5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnInnerWidth6<A, B, C, D, E, F> onInnerWidth(OnInnerWidth6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnInnerWidth7<A, B, C, D, E, F, G> onInnerWidth(OnInnerWidth7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnInnerWidth8<A, B, C, D, E, F, G, H> onInnerWidth(OnInnerWidth8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnInnerWidth9<A, B, C, D, E, F, G, H, I> onInnerWidth(OnInnerWidth9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize0 extends ActionHandler0 {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MAXIMIZE)
	interface OnMaximize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnMaximize0 onMaximize(OnMaximize0 handler) {
		return handler;
	}

	static <A> OnMaximize1<A> onMaximize(OnMaximize1<A> handler) {
		return handler;
	}

	static <A, B> OnMaximize2<A, B> onMaximize(OnMaximize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnMaximize3<A, B, C> onMaximize(OnMaximize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnMaximize4<A, B, C, D> onMaximize(OnMaximize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnMaximize5<A, B, C, D, E> onMaximize(OnMaximize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnMaximize6<A, B, C, D, E, F> onMaximize(OnMaximize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnMaximize7<A, B, C, D, E, F, G> onMaximize(OnMaximize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnMaximize8<A, B, C, D, E, F, G, H> onMaximize(OnMaximize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnMaximize9<A, B, C, D, E, F, G, H, I> onMaximize(OnMaximize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize0 extends ActionHandler0 {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MINIMIZE)
	interface OnMinimize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnMinimize0 onMinimize(OnMinimize0 handler) {
		return handler;
	}

	static <A> OnMinimize1<A> onMinimize(OnMinimize1<A> handler) {
		return handler;
	}

	static <A, B> OnMinimize2<A, B> onMinimize(OnMinimize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnMinimize3<A, B, C> onMinimize(OnMinimize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnMinimize4<A, B, C, D> onMinimize(OnMinimize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnMinimize5<A, B, C, D, E> onMinimize(OnMinimize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnMinimize6<A, B, C, D, E, F> onMinimize(OnMinimize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnMinimize7<A, B, C, D, E, F, G> onMinimize(OnMinimize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnMinimize8<A, B, C, D, E, F, G, H> onMinimize(OnMinimize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnMinimize9<A, B, C, D, E, F, G, H, I> onMinimize(OnMinimize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MODAL)
	interface OnModal0 extends ActionHandler0 {}

	@Action(type = Events.ON_MODAL)
	interface OnModal1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MODAL)
	interface OnModal9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnModal0 onModal(OnModal0 handler) {
		return handler;
	}

	static <A> OnModal1<A> onModal(OnModal1<A> handler) {
		return handler;
	}

	static <A, B> OnModal2<A, B> onModal(OnModal2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnModal3<A, B, C> onModal(OnModal3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnModal4<A, B, C, D> onModal(OnModal4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnModal5<A, B, C, D, E> onModal(OnModal5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnModal6<A, B, C, D, E, F> onModal(OnModal6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnModal7<A, B, C, D, E, F, G> onModal(OnModal7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnModal8<A, B, C, D, E, F, G, H> onModal(OnModal8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnModal9<A, B, C, D, E, F, G, H, I> onModal(OnModal9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut0 extends ActionHandler0 {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MOUSE_OUT)
	interface OnMouseOut9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnMouseOut0 onMouseOut(OnMouseOut0 handler) {
		return handler;
	}

	static <A> OnMouseOut1<A> onMouseOut(OnMouseOut1<A> handler) {
		return handler;
	}

	static <A, B> OnMouseOut2<A, B> onMouseOut(OnMouseOut2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnMouseOut3<A, B, C> onMouseOut(OnMouseOut3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnMouseOut4<A, B, C, D> onMouseOut(OnMouseOut4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnMouseOut5<A, B, C, D, E> onMouseOut(OnMouseOut5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnMouseOut6<A, B, C, D, E, F> onMouseOut(OnMouseOut6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnMouseOut7<A, B, C, D, E, F, G> onMouseOut(OnMouseOut7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnMouseOut8<A, B, C, D, E, F, G, H> onMouseOut(OnMouseOut8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnMouseOut9<A, B, C, D, E, F, G, H, I> onMouseOut(OnMouseOut9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver0 extends ActionHandler0 {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MOUSE_OVER)
	interface OnMouseOver9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnMouseOver0 onMouseOver(OnMouseOver0 handler) {
		return handler;
	}

	static <A> OnMouseOver1<A> onMouseOver(OnMouseOver1<A> handler) {
		return handler;
	}

	static <A, B> OnMouseOver2<A, B> onMouseOver(OnMouseOver2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnMouseOver3<A, B, C> onMouseOver(OnMouseOver3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnMouseOver4<A, B, C, D> onMouseOver(OnMouseOver4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnMouseOver5<A, B, C, D, E> onMouseOver(OnMouseOver5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnMouseOver6<A, B, C, D, E, F> onMouseOver(OnMouseOver6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnMouseOver7<A, B, C, D, E, F, G> onMouseOver(OnMouseOver7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnMouseOver8<A, B, C, D, E, F, G, H> onMouseOver(OnMouseOver8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnMouseOver9<A, B, C, D, E, F, G, H, I> onMouseOver(OnMouseOver9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_MOVE)
	interface OnMove0 extends ActionHandler0 {}

	@Action(type = Events.ON_MOVE)
	interface OnMove1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_MOVE)
	interface OnMove9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnMove0 onMove(OnMove0 handler) {
		return handler;
	}

	static <A> OnMove1<A> onMove(OnMove1<A> handler) {
		return handler;
	}

	static <A, B> OnMove2<A, B> onMove(OnMove2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnMove3<A, B, C> onMove(OnMove3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnMove4<A, B, C, D> onMove(OnMove4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnMove5<A, B, C, D, E> onMove(OnMove5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnMove6<A, B, C, D, E, F> onMove(OnMove6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnMove7<A, B, C, D, E, F, G> onMove(OnMove7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnMove8<A, B, C, D, E, F, G, H> onMove(OnMove8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnMove9<A, B, C, D, E, F, G, H, I> onMove(OnMove9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify0 extends ActionHandler0 {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_NOTIFY)
	interface OnNotify9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnNotify0 onNotify(OnNotify0 handler) {
		return handler;
	}

	static <A> OnNotify1<A> onNotify(OnNotify1<A> handler) {
		return handler;
	}

	static <A, B> OnNotify2<A, B> onNotify(OnNotify2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnNotify3<A, B, C> onNotify(OnNotify3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnNotify4<A, B, C, D> onNotify(OnNotify4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnNotify5<A, B, C, D, E> onNotify(OnNotify5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnNotify6<A, B, C, D, E, F> onNotify(OnNotify6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnNotify7<A, B, C, D, E, F, G> onNotify(OnNotify7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnNotify8<A, B, C, D, E, F, G, H> onNotify(OnNotify8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnNotify9<A, B, C, D, E, F, G, H, I> onNotify(OnNotify9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_OK)
	interface OnOK0 extends ActionHandler0 {}

	@Action(type = Events.ON_OK)
	interface OnOK1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_OK)
	interface OnOK2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_OK)
	interface OnOK3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_OK)
	interface OnOK4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_OK)
	interface OnOK5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_OK)
	interface OnOK6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_OK)
	interface OnOK7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_OK)
	interface OnOK8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_OK)
	interface OnOK9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnOK0 onOK(OnOK0 handler) {
		return handler;
	}

	static <A> OnOK1<A> onOK(OnOK1<A> handler) {
		return handler;
	}

	static <A, B> OnOK2<A, B> onOK(OnOK2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnOK3<A, B, C> onOK(OnOK3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnOK4<A, B, C, D> onOK(OnOK4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnOK5<A, B, C, D, E> onOK(OnOK5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnOK6<A, B, C, D, E, F> onOK(OnOK6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnOK7<A, B, C, D, E, F, G> onOK(OnOK7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnOK8<A, B, C, D, E, F, G, H> onOK(OnOK8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnOK9<A, B, C, D, E, F, G, H, I> onOK(OnOK9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_OPEN)
	interface OnOpen0 extends ActionHandler0 {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_OPEN)
	interface OnOpen9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnOpen0 onOpen(OnOpen0 handler) {
		return handler;
	}

	static <A> OnOpen1<A> onOpen(OnOpen1<A> handler) {
		return handler;
	}

	static <A, B> OnOpen2<A, B> onOpen(OnOpen2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnOpen3<A, B, C> onOpen(OnOpen3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnOpen4<A, B, C, D> onOpen(OnOpen4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnOpen5<A, B, C, D, E> onOpen(OnOpen5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnOpen6<A, B, C, D, E, F> onOpen(OnOpen6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnOpen7<A, B, C, D, E, F, G> onOpen(OnOpen7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnOpen8<A, B, C, D, E, F, G, H> onOpen(OnOpen8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnOpen9<A, B, C, D, E, F, G, H, I> onOpen(OnOpen9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize0 extends ActionHandler0 {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize1<A> extends ActionHandler1<A> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = ZulEvents.ON_PAGE_SIZE)
	interface OnPageSize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnPageSize0 onPageSize(OnPageSize0 handler) {
		return handler;
	}

	static <A> OnPageSize1<A> onPageSize(OnPageSize1<A> handler) {
		return handler;
	}

	static <A, B> OnPageSize2<A, B> onPageSize(OnPageSize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnPageSize3<A, B, C> onPageSize(OnPageSize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnPageSize4<A, B, C, D> onPageSize(OnPageSize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnPageSize5<A, B, C, D, E> onPageSize(OnPageSize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnPageSize6<A, B, C, D, E, F> onPageSize(OnPageSize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnPageSize7<A, B, C, D, E, F, G> onPageSize(OnPageSize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnPageSize8<A, B, C, D, E, F, G, H> onPageSize(OnPageSize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnPageSize9<A, B, C, D, E, F, G, H, I> onPageSize(OnPageSize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging0 extends ActionHandler0 {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging1<A> extends ActionHandler1<A> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = ZulEvents.ON_PAGING)
	interface OnPaging9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnPaging0 onPaging(OnPaging0 handler) {
		return handler;
	}

	static <A> OnPaging1<A> onPaging(OnPaging1<A> handler) {
		return handler;
	}

	static <A, B> OnPaging2<A, B> onPaging(OnPaging2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnPaging3<A, B, C> onPaging(OnPaging3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnPaging4<A, B, C, D> onPaging(OnPaging4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnPaging5<A, B, C, D, E> onPaging(OnPaging5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnPaging6<A, B, C, D, E, F> onPaging(OnPaging6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnPaging7<A, B, C, D, E, F, G> onPaging(OnPaging7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnPaging8<A, B, C, D, E, F, G, H> onPaging(OnPaging8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnPaging9<A, B, C, D, E, F, G, H, I> onPaging(OnPaging9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback0 extends ActionHandler0 {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_PIGGYBACK)
	interface OnPiggyback9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnPiggyback0 onPiggyback(OnPiggyback0 handler) {
		return handler;
	}

	static <A> OnPiggyback1<A> onPiggyback(OnPiggyback1<A> handler) {
		return handler;
	}

	static <A, B> OnPiggyback2<A, B> onPiggyback(OnPiggyback2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnPiggyback3<A, B, C> onPiggyback(OnPiggyback3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnPiggyback4<A, B, C, D> onPiggyback(OnPiggyback4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnPiggyback5<A, B, C, D, E> onPiggyback(OnPiggyback5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnPiggyback6<A, B, C, D, E, F> onPiggyback(OnPiggyback6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnPiggyback7<A, B, C, D, E, F, G> onPiggyback(OnPiggyback7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnPiggyback8<A, B, C, D, E, F, G, H> onPiggyback(OnPiggyback8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnPiggyback9<A, B, C, D, E, F, G, H, I> onPiggyback(OnPiggyback9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_RENDER)
	interface OnRender0 extends ActionHandler0 {}

	@Action(type = Events.ON_RENDER)
	interface OnRender1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_RENDER)
	interface OnRender9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnRender0 onRender(OnRender0 handler) {
		return handler;
	}

	static <A> OnRender1<A> onRender(OnRender1<A> handler) {
		return handler;
	}

	static <A, B> OnRender2<A, B> onRender(OnRender2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnRender3<A, B, C> onRender(OnRender3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnRender4<A, B, C, D> onRender(OnRender4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnRender5<A, B, C, D, E> onRender(OnRender5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnRender6<A, B, C, D, E, F> onRender(OnRender6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnRender7<A, B, C, D, E, F, G> onRender(OnRender7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnRender8<A, B, C, D, E, F, G, H> onRender(OnRender8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnRender9<A, B, C, D, E, F, G, H, I> onRender(OnRender9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick0 extends ActionHandler0 {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_RIGHT_CLICK)
	interface OnRightClick9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnRightClick0 onRightClick(OnRightClick0 handler) {
		return handler;
	}

	static <A> OnRightClick1<A> onRightClick(OnRightClick1<A> handler) {
		return handler;
	}

	static <A, B> OnRightClick2<A, B> onRightClick(OnRightClick2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnRightClick3<A, B, C> onRightClick(OnRightClick3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnRightClick4<A, B, C, D> onRightClick(OnRightClick4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnRightClick5<A, B, C, D, E> onRightClick(OnRightClick5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnRightClick6<A, B, C, D, E, F> onRightClick(OnRightClick6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnRightClick7<A, B, C, D, E, F, G> onRightClick(OnRightClick7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnRightClick8<A, B, C, D, E, F, G, H> onRightClick(OnRightClick8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnRightClick9<A, B, C, D, E, F, G, H, I> onRightClick(OnRightClick9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError0 extends ActionHandler0 {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SCRIPT_ERROR)
	interface OnScriptError9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnScriptError0 onScriptError(OnScriptError0 handler) {
		return handler;
	}

	static <A> OnScriptError1<A> onScriptError(OnScriptError1<A> handler) {
		return handler;
	}

	static <A, B> OnScriptError2<A, B> onScriptError(OnScriptError2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnScriptError3<A, B, C> onScriptError(OnScriptError3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnScriptError4<A, B, C, D> onScriptError(OnScriptError4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnScriptError5<A, B, C, D, E> onScriptError(OnScriptError5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnScriptError6<A, B, C, D, E, F> onScriptError(OnScriptError6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnScriptError7<A, B, C, D, E, F, G> onScriptError(OnScriptError7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnScriptError8<A, B, C, D, E, F, G, H> onScriptError(OnScriptError8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnScriptError9<A, B, C, D, E, F, G, H, I> onScriptError(OnScriptError9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll0 extends ActionHandler0 {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SCROLL)
	interface OnScroll9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnScroll0 onScroll(OnScroll0 handler) {
		return handler;
	}

	static <A> OnScroll1<A> onScroll(OnScroll1<A> handler) {
		return handler;
	}

	static <A, B> OnScroll2<A, B> onScroll(OnScroll2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnScroll3<A, B, C> onScroll(OnScroll3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnScroll4<A, B, C, D> onScroll(OnScroll4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnScroll5<A, B, C, D, E> onScroll(OnScroll5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnScroll6<A, B, C, D, E, F> onScroll(OnScroll6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnScroll7<A, B, C, D, E, F, G> onScroll(OnScroll7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnScroll8<A, B, C, D, E, F, G, H> onScroll(OnScroll8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnScroll9<A, B, C, D, E, F, G, H, I> onScroll(OnScroll9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling0 extends ActionHandler0 {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SCROLLING)
	interface OnScrolling9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnScrolling0 onScrolling(OnScrolling0 handler) {
		return handler;
	}

	static <A> OnScrolling1<A> onScrolling(OnScrolling1<A> handler) {
		return handler;
	}

	static <A, B> OnScrolling2<A, B> onScrolling(OnScrolling2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnScrolling3<A, B, C> onScrolling(OnScrolling3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnScrolling4<A, B, C, D> onScrolling(OnScrolling4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnScrolling5<A, B, C, D, E> onScrolling(OnScrolling5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnScrolling6<A, B, C, D, E, F> onScrolling(OnScrolling6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnScrolling7<A, B, C, D, E, F, G> onScrolling(OnScrolling7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnScrolling8<A, B, C, D, E, F, G, H> onScrolling(OnScrolling8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnScrolling9<A, B, C, D, E, F, G, H, I> onScrolling(OnScrolling9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos0 extends ActionHandler0 {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SCROLL_POS)
	interface OnScrollPos9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnScrollPos0 onScrollPos(OnScrollPos0 handler) {
		return handler;
	}

	static <A> OnScrollPos1<A> onScrollPos(OnScrollPos1<A> handler) {
		return handler;
	}

	static <A, B> OnScrollPos2<A, B> onScrollPos(OnScrollPos2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnScrollPos3<A, B, C> onScrollPos(OnScrollPos3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnScrollPos4<A, B, C, D> onScrollPos(OnScrollPos4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnScrollPos5<A, B, C, D, E> onScrollPos(OnScrollPos5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnScrollPos6<A, B, C, D, E, F> onScrollPos(OnScrollPos6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnScrollPos7<A, B, C, D, E, F, G> onScrollPos(OnScrollPos7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnScrollPos8<A, B, C, D, E, F, G, H> onScrollPos(OnScrollPos8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnScrollPos9<A, B, C, D, E, F, G, H, I> onScrollPos(OnScrollPos9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch0 extends ActionHandler0 {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SEARCH)
	interface OnSearch9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSearch0 onSearch(OnSearch0 handler) {
		return handler;
	}

	static <A> OnSearch1<A> onSearch(OnSearch1<A> handler) {
		return handler;
	}

	static <A, B> OnSearch2<A, B> onSearch(OnSearch2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSearch3<A, B, C> onSearch(OnSearch3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSearch4<A, B, C, D> onSearch(OnSearch4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSearch5<A, B, C, D, E> onSearch(OnSearch5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSearch6<A, B, C, D, E, F> onSearch(OnSearch6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSearch7<A, B, C, D, E, F, G> onSearch(OnSearch7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSearch8<A, B, C, D, E, F, G, H> onSearch(OnSearch8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSearch9<A, B, C, D, E, F, G, H, I> onSearch(OnSearch9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching0 extends ActionHandler0 {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SEARCHING)
	interface OnSearching9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSearching0 onSearching(OnSearching0 handler) {
		return handler;
	}

	static <A> OnSearching1<A> onSearching(OnSearching1<A> handler) {
		return handler;
	}

	static <A, B> OnSearching2<A, B> onSearching(OnSearching2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSearching3<A, B, C> onSearching(OnSearching3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSearching4<A, B, C, D> onSearching(OnSearching4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSearching5<A, B, C, D, E> onSearching(OnSearching5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSearching6<A, B, C, D, E, F> onSearching(OnSearching6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSearching7<A, B, C, D, E, F, G> onSearching(OnSearching7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSearching8<A, B, C, D, E, F, G, H> onSearching(OnSearching8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSearching9<A, B, C, D, E, F, G, H, I> onSearching(OnSearching9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SELECT)
	interface OnSelect0 extends ActionHandler0 {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SELECT)
	interface OnSelect9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSelect0 onSelect(OnSelect0 handler) {
		return handler;
	}

	static <A> OnSelect1<A> onSelect(OnSelect1<A> handler) {
		return handler;
	}

	static <A, B> OnSelect2<A, B> onSelect(OnSelect2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSelect3<A, B, C> onSelect(OnSelect3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSelect4<A, B, C, D> onSelect(OnSelect4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSelect5<A, B, C, D, E> onSelect(OnSelect5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSelect6<A, B, C, D, E, F> onSelect(OnSelect6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSelect7<A, B, C, D, E, F, G> onSelect(OnSelect7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSelect8<A, B, C, D, E, F, G, H> onSelect(OnSelect8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSelect9<A, B, C, D, E, F, G, H, I> onSelect(OnSelect9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection0 extends ActionHandler0 {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SELECTION)
	interface OnSelection9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSelection0 onSelection(OnSelection0 handler) {
		return handler;
	}

	static <A> OnSelection1<A> onSelection(OnSelection1<A> handler) {
		return handler;
	}

	static <A, B> OnSelection2<A, B> onSelection(OnSelection2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSelection3<A, B, C> onSelection(OnSelection3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSelection4<A, B, C, D> onSelection(OnSelection4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSelection5<A, B, C, D, E> onSelection(OnSelection5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSelection6<A, B, C, D, E, F> onSelection(OnSelection6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSelection7<A, B, C, D, E, F, G> onSelection(OnSelection7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSelection8<A, B, C, D, E, F, G, H> onSelection(OnSelection8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSelection9<A, B, C, D, E, F, G, H, I> onSelection(OnSelection9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SIZE)
	interface OnSize0 extends ActionHandler0 {}

	@Action(type = Events.ON_SIZE)
	interface OnSize1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SIZE)
	interface OnSize9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSize0 onSize(OnSize0 handler) {
		return handler;
	}

	static <A> OnSize1<A> onSize(OnSize1<A> handler) {
		return handler;
	}

	static <A, B> OnSize2<A, B> onSize(OnSize2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSize3<A, B, C> onSize(OnSize3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSize4<A, B, C, D> onSize(OnSize4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSize5<A, B, C, D, E> onSize(OnSize5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSize6<A, B, C, D, E, F> onSize(OnSize6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSize7<A, B, C, D, E, F, G> onSize(OnSize7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSize8<A, B, C, D, E, F, G, H> onSize(OnSize8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSize9<A, B, C, D, E, F, G, H, I> onSize(OnSize9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide0 extends ActionHandler0 {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SLIDE)
	interface OnSlide9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSlide0 onSlide(OnSlide0 handler) {
		return handler;
	}

	static <A> OnSlide1<A> onSlide(OnSlide1<A> handler) {
		return handler;
	}

	static <A, B> OnSlide2<A, B> onSlide(OnSlide2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSlide3<A, B, C> onSlide(OnSlide3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSlide4<A, B, C, D> onSlide(OnSlide4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSlide5<A, B, C, D, E> onSlide(OnSlide5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSlide6<A, B, C, D, E, F> onSlide(OnSlide6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSlide7<A, B, C, D, E, F, G> onSlide(OnSlide7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSlide8<A, B, C, D, E, F, G, H> onSlide(OnSlide8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSlide9<A, B, C, D, E, F, G, H, I> onSlide(OnSlide9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SORT)
	interface OnSort0 extends ActionHandler0 {}

	@Action(type = Events.ON_SORT)
	interface OnSort1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SORT)
	interface OnSort2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SORT)
	interface OnSort3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SORT)
	interface OnSort4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SORT)
	interface OnSort5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SORT)
	interface OnSort6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SORT)
	interface OnSort7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SORT)
	interface OnSort8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SORT)
	interface OnSort9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSort0 onSort(OnSort0 handler) {
		return handler;
	}

	static <A> OnSort1<A> onSort(OnSort1<A> handler) {
		return handler;
	}

	static <A, B> OnSort2<A, B> onSort(OnSort2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSort3<A, B, C> onSort(OnSort3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSort4<A, B, C, D> onSort(OnSort4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSort5<A, B, C, D, E> onSort(OnSort5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSort6<A, B, C, D, E, F> onSort(OnSort6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSort7<A, B, C, D, E, F, G> onSort(OnSort7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSort8<A, B, C, D, E, F, G, H> onSort(OnSort8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSort9<A, B, C, D, E, F, G, H, I> onSort(OnSort9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_STATE_CHANGE)
	interface OnStateChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnStateChange0 onStateChange(OnStateChange0 handler) {
		return handler;
	}

	static <A> OnStateChange1<A> onStateChange(OnStateChange1<A> handler) {
		return handler;
	}

	static <A, B> OnStateChange2<A, B> onStateChange(OnStateChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnStateChange3<A, B, C> onStateChange(OnStateChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnStateChange4<A, B, C, D> onStateChange(OnStateChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnStateChange5<A, B, C, D, E> onStateChange(OnStateChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnStateChange6<A, B, C, D, E, F> onStateChange(OnStateChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnStateChange7<A, B, C, D, E, F, G> onStateChange(OnStateChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnStateChange8<A, B, C, D, E, F, G, H> onStateChange(OnStateChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnStateChange9<A, B, C, D, E, F, G, H, I> onStateChange(OnStateChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_STUB)
	interface OnStub0 extends ActionHandler0 {}

	@Action(type = Events.ON_STUB)
	interface OnStub1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_STUB)
	interface OnStub2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_STUB)
	interface OnStub3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_STUB)
	interface OnStub4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_STUB)
	interface OnStub5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_STUB)
	interface OnStub6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_STUB)
	interface OnStub7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_STUB)
	interface OnStub8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_STUB)
	interface OnStub9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnStub0 onStub(OnStub0 handler) {
		return handler;
	}

	static <A> OnStub1<A> onStub(OnStub1<A> handler) {
		return handler;
	}

	static <A, B> OnStub2<A, B> onStub(OnStub2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnStub3<A, B, C> onStub(OnStub3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnStub4<A, B, C, D> onStub(OnStub4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnStub5<A, B, C, D, E> onStub(OnStub5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnStub6<A, B, C, D, E, F> onStub(OnStub6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnStub7<A, B, C, D, E, F, G> onStub(OnStub7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnStub8<A, B, C, D, E, F, G, H> onStub(OnStub8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnStub9<A, B, C, D, E, F, G, H, I> onStub(OnStub9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe0 extends ActionHandler0 {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_SWIPE)
	interface OnSwipe9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnSwipe0 onSwipe(OnSwipe0 handler) {
		return handler;
	}

	static <A> OnSwipe1<A> onSwipe(OnSwipe1<A> handler) {
		return handler;
	}

	static <A, B> OnSwipe2<A, B> onSwipe(OnSwipe2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnSwipe3<A, B, C> onSwipe(OnSwipe3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnSwipe4<A, B, C, D> onSwipe(OnSwipe4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnSwipe5<A, B, C, D, E> onSwipe(OnSwipe5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnSwipe6<A, B, C, D, E, F> onSwipe(OnSwipe6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnSwipe7<A, B, C, D, E, F, G> onSwipe(OnSwipe7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnSwipe8<A, B, C, D, E, F, G, H> onSwipe(OnSwipe8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnSwipe9<A, B, C, D, E, F, G, H, I> onSwipe(OnSwipe9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick0 extends ActionHandler0 {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_TARGET_CLICK)
	interface OnTargetClick9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnTargetClick0 onTargetClick(OnTargetClick0 handler) {
		return handler;
	}

	static <A> OnTargetClick1<A> onTargetClick(OnTargetClick1<A> handler) {
		return handler;
	}

	static <A, B> OnTargetClick2<A, B> onTargetClick(OnTargetClick2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnTargetClick3<A, B, C> onTargetClick(OnTargetClick3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnTargetClick4<A, B, C, D> onTargetClick(OnTargetClick4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnTargetClick5<A, B, C, D, E> onTargetClick(OnTargetClick5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnTargetClick6<A, B, C, D, E, F> onTargetClick(OnTargetClick6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnTargetClick7<A, B, C, D, E, F, G> onTargetClick(OnTargetClick7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnTargetClick8<A, B, C, D, E, F, G, H> onTargetClick(OnTargetClick8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnTargetClick9<A, B, C, D, E, F, G, H, I> onTargetClick(OnTargetClick9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_TIMER)
	interface OnTimer0 extends ActionHandler0 {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_TIMER)
	interface OnTimer9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnTimer0 onTimer(OnTimer0 handler) {
		return handler;
	}

	static <A> OnTimer1<A> onTimer(OnTimer1<A> handler) {
		return handler;
	}

	static <A, B> OnTimer2<A, B> onTimer(OnTimer2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnTimer3<A, B, C> onTimer(OnTimer3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnTimer4<A, B, C, D> onTimer(OnTimer4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnTimer5<A, B, C, D, E> onTimer(OnTimer5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnTimer6<A, B, C, D, E, F> onTimer(OnTimer6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnTimer7<A, B, C, D, E, F, G> onTimer(OnTimer7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnTimer8<A, B, C, D, E, F, G, H> onTimer(OnTimer8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnTimer9<A, B, C, D, E, F, G, H, I> onTimer(OnTimer9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_TIME_ZONE_CHANGE)
	interface OnTimeZoneChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnTimeZoneChange0 onTimeZoneChange(OnTimeZoneChange0 handler) {
		return handler;
	}

	static <A> OnTimeZoneChange1<A> onTimeZoneChange(OnTimeZoneChange1<A> handler) {
		return handler;
	}

	static <A, B> OnTimeZoneChange2<A, B> onTimeZoneChange(OnTimeZoneChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnTimeZoneChange3<A, B, C> onTimeZoneChange(OnTimeZoneChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnTimeZoneChange4<A, B, C, D> onTimeZoneChange(OnTimeZoneChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnTimeZoneChange5<A, B, C, D, E> onTimeZoneChange(OnTimeZoneChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnTimeZoneChange6<A, B, C, D, E, F> onTimeZoneChange(OnTimeZoneChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnTimeZoneChange7<A, B, C, D, E, F, G> onTimeZoneChange(OnTimeZoneChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnTimeZoneChange8<A, B, C, D, E, F, G, H> onTimeZoneChange(OnTimeZoneChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnTimeZoneChange9<A, B, C, D, E, F, G, H, I> onTimeZoneChange(OnTimeZoneChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad0 extends ActionHandler0 {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_TOP_PAD)
	interface OnTopPad9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnTopPad0 onTopPad(OnTopPad0 handler) {
		return handler;
	}

	static <A> OnTopPad1<A> onTopPad(OnTopPad1<A> handler) {
		return handler;
	}

	static <A, B> OnTopPad2<A, B> onTopPad(OnTopPad2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnTopPad3<A, B, C> onTopPad(OnTopPad3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnTopPad4<A, B, C, D> onTopPad(OnTopPad4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnTopPad5<A, B, C, D, E> onTopPad(OnTopPad5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnTopPad6<A, B, C, D, E, F> onTopPad(OnTopPad6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnTopPad7<A, B, C, D, E, F, G> onTopPad(OnTopPad7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnTopPad8<A, B, C, D, E, F, G, H> onTopPad(OnTopPad8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnTopPad9<A, B, C, D, E, F, G, H, I> onTopPad(OnTopPad9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup0 extends ActionHandler0 {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_UNGROUP)
	interface OnUngroup9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnUngroup0 onUngroup(OnUngroup0 handler) {
		return handler;
	}

	static <A> OnUngroup1<A> onUngroup(OnUngroup1<A> handler) {
		return handler;
	}

	static <A, B> OnUngroup2<A, B> onUngroup(OnUngroup2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnUngroup3<A, B, C> onUngroup(OnUngroup3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnUngroup4<A, B, C, D> onUngroup(OnUngroup4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnUngroup5<A, B, C, D, E> onUngroup(OnUngroup5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnUngroup6<A, B, C, D, E, F> onUngroup(OnUngroup6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnUngroup7<A, B, C, D, E, F, G> onUngroup(OnUngroup7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnUngroup8<A, B, C, D, E, F, G, H> onUngroup(OnUngroup8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnUngroup9<A, B, C, D, E, F, G, H, I> onUngroup(OnUngroup9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload0 extends ActionHandler0 {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_UPLOAD)
	interface OnUpload9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnUpload0 onUpload(OnUpload0 handler) {
		return handler;
	}

	static <A> OnUpload1<A> onUpload(OnUpload1<A> handler) {
		return handler;
	}

	static <A, B> OnUpload2<A, B> onUpload(OnUpload2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnUpload3<A, B, C> onUpload(OnUpload3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnUpload4<A, B, C, D> onUpload(OnUpload4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnUpload5<A, B, C, D, E> onUpload(OnUpload5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnUpload6<A, B, C, D, E, F> onUpload(OnUpload6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnUpload7<A, B, C, D, E, F, G> onUpload(OnUpload7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnUpload8<A, B, C, D, E, F, G, H> onUpload(OnUpload8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnUpload9<A, B, C, D, E, F, G, H, I> onUpload(OnUpload9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_URI_CHANGE)
	interface OnURIChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnURIChange0 onURIChange(OnURIChange0 handler) {
		return handler;
	}

	static <A> OnURIChange1<A> onURIChange(OnURIChange1<A> handler) {
		return handler;
	}

	static <A, B> OnURIChange2<A, B> onURIChange(OnURIChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnURIChange3<A, B, C> onURIChange(OnURIChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnURIChange4<A, B, C, D> onURIChange(OnURIChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnURIChange5<A, B, C, D, E> onURIChange(OnURIChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnURIChange6<A, B, C, D, E, F> onURIChange(OnURIChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnURIChange7<A, B, C, D, E, F, G> onURIChange(OnURIChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnURIChange8<A, B, C, D, E, F, G, H> onURIChange(OnURIChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnURIChange9<A, B, C, D, E, F, G, H, I> onURIChange(OnURIChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_USER)
	interface OnUser0 extends ActionHandler0 {}

	@Action(type = Events.ON_USER)
	interface OnUser1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_USER)
	interface OnUser2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_USER)
	interface OnUser3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_USER)
	interface OnUser4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_USER)
	interface OnUser5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_USER)
	interface OnUser6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_USER)
	interface OnUser7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_USER)
	interface OnUser8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_USER)
	interface OnUser9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnUser0 onUser(OnUser0 handler) {
		return handler;
	}

	static <A> OnUser1<A> onUser(OnUser1<A> handler) {
		return handler;
	}

	static <A, B> OnUser2<A, B> onUser(OnUser2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnUser3<A, B, C> onUser(OnUser3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnUser4<A, B, C, D> onUser(OnUser4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnUser5<A, B, C, D, E> onUser(OnUser5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnUser6<A, B, C, D, E, F> onUser(OnUser6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnUser7<A, B, C, D, E, F, G> onUser(OnUser7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnUser8<A, B, C, D, E, F, G, H> onUser(OnUser8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnUser9<A, B, C, D, E, F, G, H, I> onUser(OnUser9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange0 extends ActionHandler0 {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_VISIBILITY_CHANGE)
	interface OnVisibilityChange9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnVisibilityChange0 onVisibilityChange(OnVisibilityChange0 handler) {
		return handler;
	}

	static <A> OnVisibilityChange1<A> onVisibilityChange(OnVisibilityChange1<A> handler) {
		return handler;
	}

	static <A, B> OnVisibilityChange2<A, B> onVisibilityChange(OnVisibilityChange2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnVisibilityChange3<A, B, C> onVisibilityChange(OnVisibilityChange3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnVisibilityChange4<A, B, C, D> onVisibilityChange(OnVisibilityChange4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnVisibilityChange5<A, B, C, D, E> onVisibilityChange(OnVisibilityChange5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnVisibilityChange6<A, B, C, D, E, F> onVisibilityChange(OnVisibilityChange6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnVisibilityChange7<A, B, C, D, E, F, G> onVisibilityChange(OnVisibilityChange7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnVisibilityChange8<A, B, C, D, E, F, G, H> onVisibilityChange(OnVisibilityChange8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnVisibilityChange9<A, B, C, D, E, F, G, H, I> onVisibilityChange(OnVisibilityChange9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick0 extends ActionHandler0 {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_WEEK_CLICK)
	interface OnWeekClick9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnWeekClick0 onWeekClick(OnWeekClick0 handler) {
		return handler;
	}

	static <A> OnWeekClick1<A> onWeekClick(OnWeekClick1<A> handler) {
		return handler;
	}

	static <A, B> OnWeekClick2<A, B> onWeekClick(OnWeekClick2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnWeekClick3<A, B, C> onWeekClick(OnWeekClick3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnWeekClick4<A, B, C, D> onWeekClick(OnWeekClick4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnWeekClick5<A, B, C, D, E> onWeekClick(OnWeekClick5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnWeekClick6<A, B, C, D, E, F> onWeekClick(OnWeekClick6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnWeekClick7<A, B, C, D, E, F, G> onWeekClick(OnWeekClick7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnWeekClick8<A, B, C, D, E, F, G, H> onWeekClick(OnWeekClick8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnWeekClick9<A, B, C, D, E, F, G, H, I> onWeekClick(OnWeekClick9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex0 extends ActionHandler0 {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex1<A> extends ActionHandler1<A> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex2<A, B> extends ActionHandler2<A, B> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex3<A, B, C> extends ActionHandler3<A, B, C> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex4<A, B, C, D> extends ActionHandler4<A, B, C, D> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex5<A, B, C, D, E> extends ActionHandler5<A, B, C, D, E> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex6<A, B, C, D, E, F> extends
			ActionHandler6<A, B, C, D, E, F> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex7<A, B, C, D, E, F, G> extends
			ActionHandler7<A, B, C, D, E, F, G> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex8<A, B, C, D, E, F, G, H> extends
			ActionHandler8<A, B, C, D, E, F, G, H> {}

	@Action(type = Events.ON_Z_INDEX)
	interface OnZIndex9<A, B, C, D, E, F, G, H, I> extends
			ActionHandler9<A, B, C, D, E, F, G, H, I> {}

	static OnZIndex0 onZIndex(OnZIndex0 handler) {
		return handler;
	}

	static <A> OnZIndex1<A> onZIndex(OnZIndex1<A> handler) {
		return handler;
	}

	static <A, B> OnZIndex2<A, B> onZIndex(OnZIndex2<A, B> handler) {
		return handler;
	}

	static <A, B, C> OnZIndex3<A, B, C> onZIndex(OnZIndex3<A, B, C> handler) {
		return handler;
	}

	static <A, B, C, D> OnZIndex4<A, B, C, D> onZIndex(OnZIndex4<A, B, C, D> handler) {
		return handler;
	}

	static <A, B, C, D, E> OnZIndex5<A, B, C, D, E> onZIndex(OnZIndex5<A, B, C, D, E> handler) {
		return handler;
	}

	static <A, B, C, D, E, F> OnZIndex6<A, B, C, D, E, F> onZIndex(OnZIndex6<A, B, C, D, E, F> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G> OnZIndex7<A, B, C, D, E, F, G> onZIndex(OnZIndex7<A, B, C, D, E, F, G> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H> OnZIndex8<A, B, C, D, E, F, G, H> onZIndex(OnZIndex8<A, B, C, D, E, F, G, H> handler) {
		return handler;
	}

	static <A, B, C, D, E, F, G, H, I> OnZIndex9<A, B, C, D, E, F, G, H, I> onZIndex(OnZIndex9<A, B, C, D, E, F, G, H, I> handler) {
		return handler;
	}

}