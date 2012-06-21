/* Anchorchildren.js

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The children of Anchorlayout. <br> 
 * Can accept any ZK component as child.
 * 
 * <p>Default {@link #getZclass}: z-anchorchildren.
 * @author peterkuo
 * @since 6.0.0
 */
zul.layout.Anchorchildren = zk.$extends(zul.Widget, {
	_anchor: null,
	$define: {
		/**
		 * Sets the width, height relative to parent, anchorlayout.
		 * It can use % or number.
		 * Accept one argument, or two argument separated by space.
		 * The first argument is for width, and second for height.
		 * For example, "50% 50%" means the anchorchildren width and height is 50%
		 * of {@link Anchorlayout}.
		 * "-30 20%" means the width is 20px less than parent, and height is 20% of parent.
		 * "50%" means the width is 50% of parent, and the height is no assumed. 
		 * @param String anchor
		 */
		/**
		 * Returns the anchor setting.
		 * @return String
		 */
		anchor: function () {
			if (this.desktop)
				this.onSize();
		}
	},
	bind_: function () {
		this.$supers(zul.layout.Anchorchildren, 'bind_', arguments);
		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this.$supers(zul.layout.Anchorchildren, 'unbind_', arguments);
	},	
	onSize: function () {
		if (!this.isRealVisible()) 
			return;
		
		//calculate the height and width in pixel based on _anchor		
		var n = this.$n(),
			parentn = this.parent.$n(),
			parentwidth = jq(parentn).width(),
			parentheight = jq(parentn).height(),
			arr = this._anchor ? this._anchor.split(" ",2) : [],
			anchorWidth = arr[0],
			anchorHeight = arr[1];
		
		if (anchorWidth) {
			if (anchorWidth.indexOf("%") > 0) {
				n.style.width = jq.px0(Math.floor(parentwidth * zk.parseInt(anchorWidth) / 100));
			} else {
				n.style.width = jq.px0(parentwidth + zk.parseInt(anchorWidth));
			}
		}
		
		if (anchorHeight) {
			if (anchorHeight.indexOf("%") > 0) {
				n.style.height = jq.px0(Math.floor(parentheight * zk.parseInt(anchorHeight) / 100));
			} else {
				n.style.height = jq.px0(parentheight + zk.parseInt(anchorHeight));
			}		
		}
	}
});