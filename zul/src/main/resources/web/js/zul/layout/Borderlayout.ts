/* Borderlayout.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The layout widgets, such as borderlayout.
 */
//zk.$package('zul.layout');
interface Ambit {
	north(ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void;
	south(ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void;
	east(ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void;
	west(ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void;
}
var _ambit: Ambit = {
	'north': function (ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void {
		ambit.w = width - ambit.w;
		center.y = ambit.ts!;
		center.h -= ambit.ts!;
	},
	'south': function (ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void {
		ambit.w = width - ambit.w;
		ambit.y = height - ambit.y;
		center.h -= ambit.ts!;
	},
	'east': function (ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void {
		ambit.y += center.y;
		ambit.h = center.h - ambit.h;
		ambit.x = width - ambit.x;
		center.w -= ambit.ts!;
	},
	'west': function (ambit: zul.layout.LayoutRegionAmbit, center: zul.layout.LayoutRegionAmbit, width: number, height: number): void {
		ambit.y += center.y;
		ambit.h = center.h - ambit.h;
		center.x += ambit.ts!;
		center.w -= ambit.ts!;
	}
};

function _getRegionSize(wgt?: zul.layout.LayoutRegion, hor?: boolean, ext?: boolean): number {
	if (!wgt)
		return 0;
	var n = wgt.$n_('real')!,
		sz = hor ? 'offsetWidth' as const : 'offsetHeight' as const,
		sum = n[sz];
	if (ext) {
		var cn = wgt.$n('colled'),
			sn = wgt.$n('split');
		if (cn)
			sum += cn[sz];
		if (sn)
			sum += sn[sz];
	}
	return sum;
}

/**
 * A border layout is a layout container for arranging and resizing
 * child components to fit in five regions: north, south, east, west, and center.
 * Each region may
 * contain no more than one component, and is identified by a corresponding
 * constant: <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
 * <code>WEST</code>, and <code>CENTER</code>.
 *
 * <p>Default {@link #getZclass}: z-borderlayout.
 *
 */
@zk.WrapClass('zul.layout.Borderlayout')
export class Borderlayout extends zul.Widget {
	north?: zul.layout.North;
	south?: zul.layout.South;
	center?: zul.layout.Center;
	west?: zul.layout.West;
	east?: zul.layout.East;
	_shallResize?: boolean;
	_isOnSize?: boolean;
	_animationDisabled = false;

	setResize(): this {
		this.resize();
		return this;
	}

	/**
	 * Returns whether disable animation effects
	 * <p>Default: false.
	 * @since 5.0.8
	 */
	isAnimationDisabled(): boolean {
		return this._animationDisabled;
	}

	/**
	 * Sets to disable animation effects.
	 * @since 5.0.8
	 */
	setAnimationDisabled(animationDisabled: boolean): this {
		if (this._animationDisabled != animationDisabled) {
			this._animationDisabled = animationDisabled;
		}
		return this;
	}

	//-- super --//
	override onChildAdded_(child: zul.layout.LayoutRegion): void {
		super.onChildAdded_(child);
		switch (child.getPosition()) {
			case Borderlayout.NORTH:
				this.north = child as zul.layout.North;
				break;
			case Borderlayout.SOUTH:
				this.south = child as zul.layout.South;
				break;
			case Borderlayout.CENTER:
				this.center = child as zul.layout.Center;
				break;
			case Borderlayout.WEST:
				this.west = child as zul.layout.West;
				break;
			case Borderlayout.EAST:
				this.east = child as zul.layout.East;
				break;
		}
		this._shallResize = true;
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.north)
			delete this.north;
		else if (child == this.south)
			delete this.south;
		else if (child == this.center)
			delete this.center;
		else if (child == this.west)
			delete this.west;
		else if (child == this.east)
			delete this.east;
		if (!this.childReplacing_)
			this._shallResize = true;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this, onCommandReady: this});
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this, onCommandReady: this});
		super.unbind_(skipper, after, keepRod);
	}

	onCommandReady(): void {
		if (this._shallResize)
			this.resize();
	}

	override beforeMinFlex_(o: string): number {
		// B50-ZK-309
		var east = this.east,
			west = this.west,
			north = this.north,
			south = this.south,
			center = this.center;
		return o == 'w' ?
			Math.max(
				_getRegionSize(north, true), _getRegionSize(south, true),
				_getRegionSize(east, true, true) + _getRegionSize(west, true, true)
				+ _getRegionSize(center, true)) :
			_getRegionSize(north, false, true)
			+ _getRegionSize(south, false, true)
			+ Math.max(
				_getRegionSize(east), _getRegionSize(west),
				_getRegionSize(center));
	}

	//@Override, region with vflex/hflex, must wait flex resolved then do resize
	override afterChildrenFlex_(): void {
		//region's min vflex/hflex resolved and try the border resize
		//@see #_resize
		if (this._isOnSize)
			this._resize(true);
	}

	/**
	 * Re-sizes this layout component.
	 */
	resize(): void {
		if (this.desktop)
			this._resize();
	}

	_resize(isOnSize?: boolean): void {
		this._shallResize = false;
		this._isOnSize = isOnSize;
		if (!zk(this.$n()).isRealVisible()) return; //ZK-2686: incorrect borderlayout resizing to 0px in tabbox

		//make sure all regions size is resolved
		var rs: ['north', 'south', 'west', 'east'] = ['north', 'south', 'west', 'east'], k = rs.length;
		for (var region: zul.layout.LayoutRegion | undefined, j = 0; j < k; ++j) {
			region = this[rs[j]] as zul.layout.LayoutRegion | undefined;
			if (region && zk(region.$n()).isVisible()
				&& ((region._nvflex && region._vflexsz === undefined)
					|| (region._nhflex && region._hflexsz === undefined)))
				return;	//region size unknown, border cannot _resize() now,
						   //return and keep this._isOnSize true
						   //onSize event will be fired to region later, and region will
						   //call back to _resize() via afterChildrenFlex_() when it resolve
						   //itself the vflex and hflex
		}

		var el = this.$n_(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			center = {
				x: 0,
				y: 0,
				w: width,
				h: height
			};

		// fixed Opera 10.5+ bug
		if (zk.opera && !height && (!el.style.height || el.style.height == '100%')) {
			var parent = el.parentElement!;
			center.h = height = parent.offsetHeight;
		}

		for (var region: zul.layout.LayoutRegion | undefined, ambit: zul.layout.LayoutRegionAmbit, j = 0; j < k; ++j) {
			region = this[rs[j]] as zul.layout.LayoutRegion | undefined;
			if (region && zk(region.$n()).isVisible()) {
				ambit = region._ambit();
				_ambit[rs[j]]!(ambit, center, width, height);
				this._resizeWgt(region, ambit); //might recursive back
				var directionToCalculate: 'width' | 'height' = region._isVertical() ? 'width' : 'height';
				jq(region.$n('title')!).width((jq(region.$n('colled')!)[directionToCalculate]() as number) - ((jq(region.$n('btned')!)[directionToCalculate]()) as number));
			}
		}
		if (this.center && zk(this.center.$n()).isVisible()) {
			var mars = this.center.getCurrentMargins_();
			center.x += mars.left;
			center.y += mars.top;
			center.w -= mars.width;
			center.h -= mars.height;
			this._resizeWgt(this.center, center); //might recursive back
		}
		this._isOnSize = false; // reset
	}

	_resizeWgt(wgt: zul.layout.LayoutRegion, ambit: zul.layout.LayoutRegionAmbit, ignoreSplit?: boolean): void {
		if (wgt._open) {
			if (!ignoreSplit && wgt.$n('split')) {
				wgt._fixSplit();
				ambit = wgt._reszSplt(ambit);
			}
			zk.copy(wgt.$n_('real').style, {
				left: jq.px(ambit.x),
				top: jq.px(ambit.y)
			});
			this._resizeBody(wgt, ambit);
		} else {
			wgt.$n_('split').style.display = 'none';
			var colled = wgt.$n('colled');
			if (colled) {
				zk.copy(colled.style, {
					left: jq.px(ambit.x),
					top: jq.px(ambit.y),
					width: jq.px0(ambit.w),
					height: jq.px0(ambit.h)
				});
			}
			//Bug ZK-1406: resize body after onSize if it is visible but is not open
			var real = wgt.$n('real');
			if (real && zk(real).isVisible()) {
				var isSouch = wgt instanceof zul.layout.South,
					isEast = wgt instanceof zul.layout.East;
				if (isSouch || wgt instanceof zul.layout.North) {
					ambit.h = real.offsetHeight;
					if (isSouch)
						real.style.top = jq.px(ambit.y - ambit.h);
				}
				if (isEast || wgt instanceof zul.layout.West) {
					ambit.w = real.offsetWidth;
					if (isEast)
						real.style.left = jq.px(ambit.x - ambit.w);
				}
				this._resizeBody(wgt, ambit);
			}
		}
	}

	_resizeBody(wgt: zul.layout.LayoutRegion, ambit: zul.layout.LayoutRegionAmbit): void {
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var el = wgt.$n_('real');
		if (!this._ignoreResize(el, ambit.w, ambit.h)) {
			var fchild = wgt.isFlex() && wgt.getFirstChild(),
				bodyEl = fchild ? wgt.getFirstChild()!.$n_() : wgt.$n_('cave'),
				bs = bodyEl.style,
				$el = zk(el);

			el.style.width = jq.px0(ambit.w);
			var contentWidth = $el.contentWidth();
			//ZK-2750, prevent parent region grows up infinitely
			if ((wgt instanceof zul.layout.West || wgt instanceof zul.layout.East) && !wgt._width && !wgt._hflex)
				contentWidth++; // B50-ZK-641: text wrap in IE
			bs.width = jq.px0(contentWidth);

			el.style.height = jq.px0(ambit.h);
			if (wgt.$n('cap'))
				ambit.h = Math.max(0, ambit.h - wgt.$n_('cap').offsetHeight);

			// Bug: B50-3201762: Borderlayout flex has issue with listbox hflex in IE 6
			if (fchild) { // B50-ZK-198: always need cave height
				const cv = wgt.$n('cave');
				if (cv)
					cv.style.height = jq.px0(ambit.h - $el.padBorderHeight());
			}
			bs.height = jq.px0(ambit.h - $el.padBorderHeight());
			if (wgt._nativebar && wgt.isAutoscroll()) {
				bs.overflow = 'auto';
				bs.position = 'relative';
			}
			if (!this._isOnSize)
				zUtl.fireSized(wgt);
		}
	}

	_ignoreResize(el: HTMLElement, w: number, h: number): boolean {
		if (el._lastSize && el._lastSize.width == w && el._lastSize.height == h) {
			return true;
		}

		// store fot next time to check
		el._lastSize = {width: w, height: h};
		return false;
	}

	//zWatch//
	override onSize(): void {
		this._resize(true);
	}

	/**
	 * The north layout constraint (top of container).
	 * @type String
	 */
	static NORTH = 'north' as const;

	/**
	 * The south layout constraint (bottom of container).
	 * @type String
	 */
	static SOUTH = 'south' as const;

	/**
	 * The east layout constraint (right side of container).
	 * @type String
	 */
	static EAST = 'east' as const;

	/**
	 * The west layout constraint (left side of container).
	 * @type String
	 */
	static WEST = 'west' as const;

	/**
	 * The center layout constraint (middle of container).
	 * @type String
	 */
	static CENTER = 'center' as const;
}
