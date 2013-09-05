/* Iframe.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 19 11:47:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The utility widgets, such as iframe and script.
 */
//zk.$package('zul.utl');

/**
 * Includes an inline frame.
 *
 * <p>Unlike HTML iframe, this component doesn't have the frameborder
 * property. Rather, use the CSS style to customize the border (like
 * any other components).
 */
zul.utl.Iframe = zk.$extends(zul.Widget, {
	_scrolling: 'auto',

	$define: {
		src: function (v) {
			var n = this.$n();
			if (n) n.src = v || '';
		},
		/**
		 * Return the scroll bars.
		 * <p>Defalut: "auto"
		 * @return String
		 * @deprecated as of release 7.0.0, use CSS instead.
		 */
		/**
		 * Define scroll bars
		 * @param String scrolling "true", "false", "yes" or "no" or "auto", "auto" by default
		 * If null, "auto" is assumed.
		 * @deprecated as of release 7.0.0, use CSS instead.
		 */
		scrolling: function (v) {
			if (!v) this._scrolling = v = 'auto';
			var n = this.$n();
			if (n) {
				if (zk.gecko || zk.opera)
					n.scrolling = 'true' === v ? 'yes': 'false' === v ? 'no': v;	
				else 
					this.rerender();
			}
		},
		/** Returns the alignment.
		 * <p>Default: null (use browser default).
		 * @return String
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		/** Sets the alignment: one of top, middle, bottom, left, right and
		 * center.
		 * @param String align
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		align: function (v) {
			var n = this.$n();
			if (n) n.align = v || '';
		},
		/** Returns the frame name.
		 * <p>Default: null (use browser default).
		 * @return String
		 */
		/** Sets the frame name.
		 * @param String name
		 */
		name: function (v) {
			var n = this.$n();
			if (n) n.name = v || '';
		},
		/** Returns whether to automatically hide this component if
		 * a popup or dropdown is overlapped with it.
		 *
		 * <p>Default: false.
		 *
		 * <p>If an iframe contains PDF or other non-HTML resource,
		 * it is possible that it obscues the popup that shall be shown
		 * above it. To resolve this, you have to specify autohide="true"
		 * to this component, and specify the following in the page:
		 * <pre><code>&lt;script content="zk.useStack='auto';"?>
		 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Stackup_and_Shadow">Stackup and Shadow</a>
		 * for more information.
		 * @return boolean
		 */
		/** Sets whether to automatically hide this component if
		 * a popup or dropdown is overlapped with it.
		 * @param boolean autohide
		 */
		autohide: function (v) {
			var n = this.$n();
			if (n) jq(n).attr('z_autohide', v);
		}
	},
	//super//
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.utl.Iframe, 'bind_', arguments);
		if (this._src) {
			var self = this;
			after.push(function () {self.$n().src = self._src;});
		}
	},
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments)
				+ ' src="' + zjq.src0 + '" frameborder="0"',
		v = this._scrolling;
		if ('auto' != v)
			attr += ' scrolling="' + ('true' === v ? 'yes': 'false' === v ? 'no': v) + '"';	
		if (v = this._align) 
			attr += ' align="' + v + '"';
		if (v = this._name) 
			attr += ' name="' + v + '"';
		if (v = this._autohide) 
			attr += ' z_autohide="' + v + '"';
		return attr;
	}
});
