/* Style.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan 14 15:28:14     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The style component used to specify CSS styles for the owner desktop.
 * <p>
 * Note: a style component can appear anywhere in a ZUML page, but it affects
 * all components in the same desktop.
 * <p>
 * Note: if the src and content properties are both set, the later one overrides
 * the previous one.
 */
zul.utl.Style = zk.$extends(zk.Widget, {
	$define: {
    	/**
		 * Returns the URI of an external style sheet.
		 * <p>
		 * Default: null.
		 * 
		 * @return String
		 */
		/**
		 * Sets the URI of an external style sheet.
		 * <p>Calling this method implies setContent(null).
		 * @param String src the URI of an external style sheet
		 */
		src: function () {
			this._content = null;
			this.rerender(0);
		},
		/**
		 * Returns the content of this style tag.
		 * @return String
		 * @since 5.0.8
		 */
		/**
		 * Sets the content of this style tag.
		 * <p>Calling this method implies setSrc(null).
		 * @param String content the content of this style tag.
		 */
		content: function () {
			this._src = null;
			this.rerender(0);
		},
		/**
		 * Returns the media dependencies for this style sheet.
		 * <p>Default: null
		 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
		 * @return String
		 * @since 5.0.3
		 */
		/**
		 * Sets the media dependencies for this style sheet.
		 * @param String media the media of this style sheet.
		 * @since 5.0.3
		 */
		media: function (v) {
			var n = this.$n();
			if (n) n.media = v;
		}
	}
});
if (zk.ie6_)
	zul.utl.Style.prototype.bind_ = function () {
		this.$supers(zul.utl.Style, 'bind_', arguments);

		//test2/Z5-style.zul: we have to re-assign href (setOuter might work well)
		if (this._src) {
			var self = this;
			setTimeout(function () {
				var n = self.$n();
				if (n) n.href = self._src;
			});
		}
	};
