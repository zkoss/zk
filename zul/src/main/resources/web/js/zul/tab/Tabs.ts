/* Tabs.ts

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:43 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/**
 * A collection of tabs ({@link Tab}).
 *
 * <p>Default {@link #getZclass}: z-tabs.
 */
@zk.WrapClass('zul.tab.Tabs')
export class Tabs extends zul.Widget {
	override parent!: zul.tab.Tabbox | undefined;

	_tabsScrollLeft = 0;
	_tabsScrollTop = 0;
	_shallCheck?: boolean;
	_doingScroll?: Record<string, number>;

	/** Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	getTabbox(): zul.tab.Tabbox | undefined {
		return this.parent;
	}

	//@Override
	override getWidth(): string | undefined {
		var wd = this._width;
		if (!wd) {
			var tabbox = this.getTabbox();
			if (tabbox && tabbox.isVertical())
				return '50px';
		}
		return wd;
	}

	override onSize(): void {
		this._fixWidth(true); //ZK-2810: set height to tabbox when onSize (maybe setHeight or setWidth)

		// Bug Z35-tabbox-004.zul, we need to check again.
		this._scrollcheck('init');
	}

	beforeSize(): void {
		var tabbox = this.getTabbox()!,
			width = tabbox.getWidth(),
			style = this.$n_().style;
		if ((!width || width.endsWith('%') || width == 'auto') && !tabbox.inAccordionMold() && !tabbox.isVertical()) {
			this.$n_('cave').style.width = '';
			if (style.width) {
				style.width = '';
				if (!tabbox.isTabscroll())
					tabbox.$n_().style.width = '';
			}
		}
	}

	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		var last = child.previousSibling,
			out;
		if (before)
			jq(before).before(out = child.redrawHTML_());
		else if (last)
			jq(last).after(out = child.redrawHTML_());
		else {
			var edge = this.$n('edge');
			if (edge)
				jq(edge).before(out = child.redrawHTML_());
			else
				jq(this.getCaveNode()!).append(out = child.redrawHTML_());
		}
		// ZK-5009, if out is empty, ignore for bind()
		if (out) {
			child.bind(desktop);
		}
	}

	//bug #3014664
	override setVflex(v: boolean | string | undefined): this { //vflex ignored for Tabs
		if (v != 'min') v = false;
		return super.setVflex(v);
	}

	//bug #3014664
	override setHflex(v: boolean | string | undefined): this { //hflex ignored for Tabs
		if (v != 'min') v = false;
		return super.setHflex(v);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this, onResponse: this, beforeSize: this});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this, onResponse: this, beforeSize: this});
		super.unbind_(skipper, after, keepRod);
	}

	_scrollcheck(way: string, tb?: zul.tab.Tab): void {
		this._shallCheck = false;
		var tabbox = this.getTabbox()!;
		if (!this.desktop
			|| (tabbox && (!tabbox.isRealVisible() || !tabbox.isTabscroll())))
			return;

		var tabs = this.$n(),
			tbx = tabbox.$n();

		if (!tabs || !tbx)
			return;	// tabbox is delete , no need to check scroll

		if (tabbox.isVertical()) { //vertical
			var tabsOffsetHeight = tabs.offsetHeight,
				tabsScrollTop = tabs.scrollTop,
				childHeight = 0;

			jq(this.$n_('cave')).children().each(function () {
				childHeight += this.offsetHeight;
			});

			if (tabbox._scrolling) { //already in scrolling status
				var btnsize = this._getArrowSize();
				if (tabs.offsetHeight <= btnsize) return;

				var sel = tabbox.getSelectedTab(),
					node = tb ? tb.$n() : (sel ? sel.$n() : undefined),
					nodeOffsetTop = node ? node.offsetTop : 0,
					nodeOffsetHeight = node ? node.offsetHeight : 0;

				if (childHeight <= tabsOffsetHeight + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false);
					tabs.style.height = jq.px0(tbx.offsetHeight - 2);
					tabs.scrollTop = 0;
				}
				switch (way) {
				case 'end':
					var d = childHeight - tabsOffsetHeight - tabsScrollTop;
					this._doScroll(d >= 0 ? 'down' : 'up', d >= 0 ? d : Math.abs(d));
					break;
				case 'init':
				case 'vsel':
					if (nodeOffsetTop < tabsScrollTop) {
						this._doScroll('up', tabsScrollTop - nodeOffsetTop);
					} else if (nodeOffsetTop + nodeOffsetHeight > tabsScrollTop + tabsOffsetHeight) {
						this._doScroll('down', nodeOffsetTop + nodeOffsetHeight - tabsScrollTop - tabsOffsetHeight);
					}
					break;
				}
			} else { // not enough tab to scroll
				if (childHeight - tabsOffsetHeight > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var btnsize = this._getArrowSize(),
						temp = tbx.offsetHeight - btnsize;
					tabs.style.height = temp > 0 ? temp + 'px' : '';
					if (way == 'end') {
						var d = childHeight - tabsOffsetHeight - tabsScrollTop + 2;
						if (d >= 0)
							this._doScroll(this.uuid, d);
					}
				} else {
					this._showbutton(false);
				}
			}
		} else if (!tabbox.inAccordionMold()) {
			var cave = this.$n_('cave'),
				sel = tabbox.getSelectedTab(),
				node = tb ? tb.$n() : (sel ? sel.$n() : undefined),
				nodeOffsetLeft = node ? node.offsetLeft : 0,
				nodeOffsetWidth = node ? node.offsetWidth : 0,
				tabsOffsetWidth = tabs.offsetWidth,
				tabsScrollLeft = this._tabsScrollLeft,
				childWidth = 0,
				toolbar = tabbox.toolbar,
				toolbarWidth = 0;

			jq(cave).children().each(function () {
				childWidth += this.offsetWidth;
			});

			if (toolbar && toolbar.desktop)
				toolbarWidth = toolbar.$n_().offsetWidth;

			if (tabbox._scrolling) { //already in scrolling status
				var btnsize = this._getArrowSize();
				tabbox.$n_('right').style.right = toolbarWidth + 'px';

				if (childWidth <= tabsOffsetWidth + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false);
					tabs.style.width = jq.px0(tbx.offsetWidth - toolbarWidth);
					this._fixTabsScrollLeft(0);
				}
				// scroll to specific position
				switch (way) {
				case 'end':
					var d = childWidth - tabsOffsetWidth - tabsScrollLeft;
					this._doScroll(d >= 0 ? 'right' : 'left', d >= 0 ? d : Math.abs(d));
					break;
				case 'init':
				case 'sel':
					if (nodeOffsetLeft == tabsScrollLeft) // nothing to do
						break;

					if (nodeOffsetLeft < tabsScrollLeft) {
						this._doScroll('left', tabsScrollLeft - nodeOffsetLeft);
					} else if (nodeOffsetLeft + nodeOffsetWidth > tabsScrollLeft + tabsOffsetWidth) {
						this._doScroll('right', nodeOffsetLeft + nodeOffsetWidth - tabsScrollLeft - tabsOffsetWidth);
					}
					break;
				}
			} else { // not enough tab to scroll
				if (childWidth - tabsOffsetWidth > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var btnsize = this._getArrowSize(),
						temp = tbx.offsetWidth - toolbarWidth - btnsize;//coz show button then getsize again
					tabs.style.width = temp > 0 ? temp + 'px' : '';
					tabbox.$n_('right').style.right = toolbarWidth + 'px';

					if (way == 'sel') {
						if (nodeOffsetLeft < tabsScrollLeft) {
							this._doScroll('left', tabsScrollLeft - nodeOffsetLeft);
						} else if (nodeOffsetLeft + nodeOffsetWidth > tabsScrollLeft + tabsOffsetWidth) {
							this._doScroll('right', nodeOffsetLeft + nodeOffsetWidth - tabsScrollLeft - tabsOffsetWidth);
						}
					}
				} else {
					this._showbutton(false);
				}
			}
		}
	}

	_doScroll(to: string, move: number): void {
		if (!this._doingScroll)
			this._doingScroll = {};
		if (move <= 0 || this._doingScroll[to])
			return;
		var step: number,
			self = this,
			tabs = this.$n();

		this._doingScroll[to] = move;
		//the tab bigger , the scroll speed faster
		step = move <= 60 ? 5 : (5 * (zk.parseInt(move / 60) + 1));
		//Use to scroll
		var goscroll = function (tabs, to: string, step: number): void {
			switch (to) {
			case 'right':
				self._fixTabsScrollLeft(self._tabsScrollLeft + step);
				break;
			case 'left':
				self._fixTabsScrollLeft(self._tabsScrollLeft - step);
				break;
			case 'up':
				self._fixTabsScrollTop(self._tabsScrollTop - step);
				break;
			default:
				self._fixTabsScrollTop(self._tabsScrollTop + step);
			}
			var tabsScrollLeft = self._tabsScrollLeft,
				tabsScrollTop = self._tabsScrollTop;
			self._fixTabsScrollLeft(tabsScrollLeft <= 0 ? 0 : tabsScrollLeft);
			self._fixTabsScrollTop(tabsScrollTop <= 0 ? 0 : tabsScrollTop);
		},
		run = setInterval(function () {
			if (!move || !self.desktop) {
				delete self._doingScroll![to];
				clearInterval(run);
				return;
			} else {
				//high speed scroll, need break
				move < step ? goscroll(tabs, to, move) : goscroll(tabs, to, step);
				move -= step;
				move = move < 0 ? 0 : move;
			}
		}, 10);
	}

	_getArrowSize(): number {
		var tabbox = this.getTabbox()!,
			isVer = tabbox.isVertical(),
			btnA = isVer ? tabbox.$n('up') : tabbox.$n('left'),
			btnB = isVer ? tabbox.$n('down') : tabbox.$n('right'),
			size = 0;
		if (btnA && btnB) {
			size = isVer ? btnA.offsetHeight + btnB.offsetHeight : btnA.offsetWidth + btnB.offsetWidth;
		}
		return size;
	}

	_showbutton(show: boolean): void {
		var tabbox = this.getTabbox()!;
		if (tabbox.isTabscroll()) {
			var cls = tabbox.$s('scroll');
			jq(tabbox).removeClass(cls);
			if (show) {
				// ZK-1959: the height of arrow should not change when the tabbox add tab
				if (!tabbox.isVertical() && !tabbox.inAccordionMold()) {
					var tb = tabbox.toolbar;
					tabbox.$n_('left').style.height = tabbox.$n_('right').style.height = '';
					if (tb)
						tb.$n_().style.height = '';
				}

				jq(tabbox).addClass(cls);
			}
		}
	}

	_fixWidth(toSel: boolean): void {
		var tabs = this.$n_(),
			tabbox = this.getTabbox()!,
			tbx = tabbox.$n_(),
			btnsize = tabbox._scrolling ? this._getArrowSize() : 0;
		this._fixHgh(toSel); //ZK-2810: don't set height to tabbox when deselect
		if (tabbox.isVertical()) {
			//LI in IE doesn't have width...
			if (tabs.style.width) {
				tabs._width = tabs.style.width;
			} else {
				//vertical tabs have default width 50px
				tabs.style.width = tabs._width ?? '50px';
			}
		} else if (!tabbox.inAccordionMold()) {
			if (tbx.offsetWidth < btnsize)
				return;
			if (tabbox.isTabscroll()) {
				var toolbar: zul.wgt.Toolbar | HTMLElement | undefined = tabbox.toolbar;
				if (toolbar)
					toolbar = toolbar.$n();
				if (!tbx.style.width) {
					tbx.style.width = '100%';
					if (tabbox._scrolling)
						tabs.style.width = jq.px0(zk(tbx).contentWidth() - (toolbar ? toolbar.offsetWidth : 0) - btnsize);
					else
						tabs.style.width = jq.px0(zk(tbx).contentWidth() - (toolbar ? toolbar.offsetWidth : 0));
				} else {
					if (tabbox._scrolling)
						tabs.style.width = jq.px0(zk(tbx).contentWidth() - (toolbar ? toolbar.offsetWidth : 0) - btnsize);
					else
						tabs.style.width = jq.px0(zk(tbx).contentWidth() - (toolbar ? toolbar.offsetWidth : 0));
				}
				if (toolbar && tabbox._scrolling)
					tabbox.$n_('right').style.right = toolbar.offsetWidth + 'px';

			} else {
				if (!tbx.style.width) {
					if (tbx.offsetWidth) {
						tbx.style.width = jq.px0(tbx.offsetWidth);
						tabs.style.width = jq.px0(zk(tbx).contentWidth() - zk(tabs).marginWidth());
					}
				} else {
					tabs.style.width = jq.px0(zk(tbx).contentWidth() - zk(tabs).marginWidth());
				}
			}
		}
	}

	_fixHgh(toSel: boolean): void {
		if (this.getTabbox()!._scrolling) return;
		var tabbox = this.getTabbox()!;
		//fix tabpanels's height if tabbox's height is specified
		//Ignore accordion since its height is controlled by each tabpanel
		if (tabbox.isVertical()) {
			const tabs = this.$n_(),
				tbx = tabbox.$n_(),
				u = tabbox.$n('up'),
				d = tabbox.$n('down'),
				cave = this.$n_('cave'),
				allTab = jq(cave).children();

			if (!tabbox.getHeight() && (!tabbox._vflex || tabbox._vflex == 'min')) { // B50-ZK-473: vflex 1
				if (!toSel) { //ZK-2810: clear height of tabbox when deselect
					jq(tbx).css('height', '');
				} else {
					var tabsHgh = allTab.length * allTab[0].offsetHeight, // default height
						seldPanel = tabbox.getSelectedPanel(),
						panelsHgh = seldPanel && seldPanel.getPanelContentHeight_() || 0,  //B60-ZK-965
					// NOTE: seldPanel.getPanelContentHeight_() could return NaN; the return value must
					// be validated before use.
					realHgh = Math.max(tabsHgh, panelsHgh);
					tbx.style.height = jq.px0(realHgh + zk(tbx).padBorderHeight());
				}
			}
			tabs.style.height = jq.px0(zk(tbx).contentHeight() - zk(tabs).marginHeight());

			if (u && d) {
				u.style.width = d.style.width = tabs.style.width;
			}
		} else {
			const r = tabbox.$n('right'),
				l = tabbox.$n('left'),
				tb = tabbox.toolbar?.$n(),
				tabs = this.$n(),
				hgh = jq.px0(tabs ? tabs.offsetHeight : 0);
			if (r && l) {
				r.style.height = l.style.height = hgh;
			}
			if (tb) {
				tb.style.height = hgh;
			}
			if (tabs)
				tabs.style.height = '';
		}
	}

	onResponse(): void {
		if (this._shallCheck) {
			this._scrollcheck('init');
		}
	}

	override onChildRemoved_(child: zk.Widget): void {
		var p = this.parent;
		if (p && child == p._selTab) {
			p._selTab = undefined;
		}
		if (this.desktop)
			this._shallCheck = true;
		super.onChildRemoved_(child);
	}

	override onChildAdded_(child: zk.Widget): void {
		if (this.desktop)
			this._shallCheck = true;
		super.onChildAdded_(child);
	}

	override onChildVisible_(child: zk.Widget): void {
		if (this.desktop) {
			var tabbox = this.getTabbox()!;
			if (tabbox.inAccordionMold() && tabbox.getHeight()) {
				tabbox.syncSize();
			}
		}
		super.onChildVisible_(child);
	}

	override ignoreFlexSize_(attr: zk.FlexOrient): boolean {
		var p = this.getTabbox()!;
		return (p.isVertical() && 'h' == attr)
			|| (p.isHorizontal() && 'w' == attr);
	}

	_fixTabsScrollLeft(scrollLeft: number): void {
		this.$n_().scrollLeft = this._tabsScrollLeft = scrollLeft;
	}

	_fixTabsScrollTop(scrollTop: number): void {
		this.$n_().scrollTop = this._tabsScrollTop = scrollTop;
	}
}