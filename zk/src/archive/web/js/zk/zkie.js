/* zkie.js

	Purpose:
		Patch for IE
	Description:
		Like domie.js, this file patches IE issues, but it is included
		after all other modules
	History:
		Thu Jan  5 10:20:52 TST 2012, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
(function() {
//Note: we have to depend on client-side's zk.css3, since,
//if Browser Mode=9 and Document Mode=8, it means non-CSS3 but server can't detect it
if (!zk.css3) {
	var _defShadowOpts = {left: 4, right: 4, top: 3, bottom: 3},
		_shadowEnding = zk.ie6_ ? '" class="z-shadow"></div>':
		'" class="z-shadow"><div class="z-shadow-tl"><div class="z-shadow-tr"></div></div>'
		+'<div class="z-shadow-cl"><div class="z-shadow-cr"><div class="z-shadow-cm">&#160;</div></div></div>'
		+'<div class="z-shadow-bl"><div class="z-shadow-br"></div></div></div>';

	/** The shadow effect.
	 * @disable(zkgwt)
	 */
	zk.eff.Shadow = zk.$extends(zk.Object, {
		/** Constructor of the Shadow object.
		 * <p>Notice that you have to invoke {@link #destroy},
		 * if the shadow is no longer used. 
		 * @param DOMElement element the element to associate the shadow
		 * @param Map opts [optional] the options. Alowed options:
		 * <ul>
		 * <li>left: The margin at left. Default: 4.</li>
		 * <li>right: The margin at right. Default: 4.</li>
		 * <li>top: the margin at top. Default: 3.</li>
		 * <li>bottom: the margin at bottom. Default: 3.</li>
		 * <li>stackup: whether to create a stackup (see jqzk#makeStackup)</li> 
		 * </ul>
		 */
		$init: function (element, opts) {
			opts = this.opts = zk.eff._skuOpts(zk.$default(opts, _defShadowOpts));
			if (zk.ie6_) {
				opts.left -= 1;
				opts.right -= 8;
				opts.top -= 2;
				opts.bottom -= 6;
			}

			this.node = element;
			var sdwid = element.id + "-sdw";
			jq(element).before('<div id="' + sdwid + _shadowEnding);
			this.shadow = jq(sdwid, zk)[0];
		},
		/** Destroys the shadow. You cannot use this object any more. 
		 */
		destroy: function () {
			jq(this.shadow).remove();
			jq(this.stackup).remove();
			this.node = this.shadow = this.stackup = null;
		},
		/** Hides the shadow, no matter the associated element is visible or not.
		 * <p>Notice this method is rarely used. Rather, {@link #sync} is more convenient to use. 
		 */
		hide: function(){
			jq(this.shadow).hide();
			jq(this.stackup).hide();
		},
		/** Synchronizes the visual states of the element with shadow. The visual states include the visibility, location, dimensions and z-index. When the associated element is changed, you have to invoke this method to synchronize the visual states. 
		 */
		sync: function () {
			var node = this.node, $node = jq(node),
				shadow = this.shadow;
			if (!node || !$node.zk.isVisible(true)) {
				this.hide();
				return false;
			}

			for (var c = shadow;;) {
				if (!(c = c.nextSibling) || c.tagName) {
					if (c != node)
						node.parentNode.insertBefore(shadow, node);
					break;
				}
			}

			var opts = this.opts,
				l = node.offsetLeft, t = node.offsetTop,
				w = node.offsetWidth, h = node.offsetHeight,
				wd = Math.max(0, w - opts.left + opts.right),
				hgh = Math.max(0, h - opts.top + opts.bottom),
				st = shadow.style;
			st.left = jq.px(l + opts.left);
			st.top = jq.px(t + opts.top);
			st.width = jq.px0(wd);
			st.zIndex = zk.parseInt($node.css("zIndex"));
			st.display = "block";
			if (zk.ie6_) st.height = jq.px0(hgh);
			else {
				var cns = shadow.childNodes;
				cns[1].style.height = jq.px0(hgh - cns[0].offsetHeight - cns[2].offsetHeight);
			}

			var stackup = this.stackup;
			if(opts.stackup) {
				if(!stackup)
					stackup = this.stackup =
						jq.newStackup(node, node.id + '-sdwstk', shadow);

				st = stackup.style;
				st.left = jq.px(l);
				st.top = jq.px(t);
				st.width = jq.px0(w);
				st.height = jq.px0(h);
				st.zIndex = shadow.style.zIndex;
				st.display = "block";
			}
			return true;
		},
		/** Returns the lowest level of elements of this shadow. By lowest level, we mean the element is displayed at the botton in the z order.
		 * The bottom element is the stackup element if #$init is called with the stackup option. Otherwise, it is the shadow element. 
		 * @return DOMElement
		 */
		getBottomElement: function () {
			return this.stackup || this.shadow;
		}
	});
}
})();