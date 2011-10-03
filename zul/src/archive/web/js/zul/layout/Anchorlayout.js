/* Anchorlayout.js

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An anchorlayout lays out a container which can resize 
 * it's children base on its width and height<br>
 * 
 * <p>Default {@link #getZclass}: z-anchorlayout.
 * 
 * @author peterkuo
 * @since 6.0.0
 */
zul.layout.Anchorlayout = zk.$extends(zul.Widget, {
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-anchorlayout";
	},
	onSize: function () {
		if (this.isRealVisible() && this.nChildren) {
			var cmp = this.$n(),
				cave, total;
			
			if ((cave = this.$n("cave")) && zk.ie6_)
				cave.style.width = "0px";
				
			total = zk(cmp).revisedWidth(cmp.offsetWidth);
			
			cave.style.width = jq.px0(total);
			
			if (cmp.style.height) {
				if (zk.ie6_)
					cave.style.height = "0px";
				cave.style.height = zk(cmp).revisedHeight(cmp.offsetHeight, true) + "px";
			}
			
			for (var wgt = this.firstChild; wgt; wgt = wgt.nextSibling) {
				var wdh = wgt.getWidth();
				if (wdh && wdh.endsWith('px'))
					total -= (zk.parseInt(wdh) + zk(wgt).padBorderWidth());
			}
			
			total = Math.max(0, total);
			
			for (var wgt = this.firstChild; wgt; wgt = wgt.nextSibling) {
				var wdh = wgt.getWidth();
				if (wdh && wdh.endsWith('%'))
					wgt.$n().style.width = (total ? Math.max(0, Math.floor(zk.parseInt(wdh) / 100 * total)
												- zk(wgt).padBorderWidth()) : 0) + "px";
			}
		}
	},
	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.desktop)
			zUtl.fireSized(this);
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop)
			zUtl.fireSized(this);
	},
	bind_: function () {//after compose
		this.$supers(zul.layout.Anchorlayout, 'bind_', arguments); 
		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this.$supers(zul.layout.Anchorlayout, 'unbind_', arguments);
	}
});