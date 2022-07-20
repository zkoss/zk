/* Tab.ts

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:51 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
// ZK-886, called by unbind_ and rerender
// this._oldId used in tab.js
// this.$n() will be cleared during rerender
// but LinkedPanel.firstChild will not,
// the condition LinkedPanel.firstChild != this.$n()
// will get the wrong result
// delete it later for the invalidate() case
function _logId(wgt: zul.tab.Tab): void {
	if (!wgt._oldId) {
		wgt._oldId = wgt.uuid;
		setTimeout(function () {
			delete wgt._oldId;
		}, 0);
	}
}
/**
 * A tab.
 * <p>
 * Default {@link #getZclass}: z-tab.
 */
@zk.WrapClass('zul.tab.Tab')
export class Tab extends zul.LabelImageWidget implements zul.LabelImageWidgetWithDisable {
	override parent!: zul.tab.Tabs | null;

	_oldId?: string;
	_closable?: boolean;
	_disabled?: boolean;

	constructor() {
		super(); // FIXME: params?
		this.listen({onClose: this}, -1000);
	}

	/**
	 * Returns whether this tab is closable. If closable, a button is displayed
	 * and the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isClosable(): boolean | undefined {
		return this._closable;
	}

	/**
	 * Sets whether this tab is closable. If closable, a button is displayed and
	 * the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 * @param boolean closable
	 */
	setClosable(closable: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = closable;

		if (o !== closable || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	override getImage(): string | undefined {
		return this._image;
	}

	override setImage(v: string, opts?: Record<string, boolean>): this {
		const o = this._image;
		this._image = v;

		if (o !== v || (opts && opts.force)) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether this tab is disabled.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/**
	 * Sets whether this tab is disabled. If a tab is disabled, then it cann't
	 * be selected or closed by user, but it still can be controlled by server
	 * side program.
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether this tab is selected.
	 * @return boolean
	 */
	isSelected(): boolean {
		const tabbox = this.getTabbox();
		return tabbox?.getSelectedTab() == this;
	}

	/**
	 * Sets whether this tab is selected.
	 * @param boolean selected
	 */
	setSelected(selected: boolean, fromServer?: boolean): void {
		var tabbox = this.getTabbox();
		if (tabbox && selected) {
			tabbox.setSelectedTab(this, fromServer);
		}
	}

	/**
	 * Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	getTabbox(): zul.tab.Tabbox | null {
		return this.parent ? this.parent.parent : null;
	}

	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 * @return int
	 */
	getIndex(): number {
		return this.getChildIndex();
	}

	/**
	 * Returns the panel associated with this tab.
	 * @return zul.tab.Tabpanel
	 */
	getLinkedPanel(): zul.tab.Tabpanel | undefined {
		return this.getTabbox()?.getTabpanels()?.getChildAt<zul.tab.Tabpanel>(this.getIndex());
	}

	_doCloseClick(evt: zk.Event): void {
		if (!this._disabled) {
			this.fire('onClose');
			evt.stop();
		}
	}

	_sel(toSel: boolean, notify: boolean): void {
		var tabbox = this.getTabbox();

		/* ZK-1441
			* If tabbox is animating (end-user click different tabs quickly), ignore this action.
			*/
		if (!tabbox || tabbox._animating) return;

		var panel = this.getLinkedPanel(),
			inAccordion = tabbox.inAccordionMold();

		if (toSel) {
			const ps = tabbox.tabpanels;
			if (ps) {
				if (ps._selPnl && ps._selPnl != panel) ps._selPnl._sel(false, inAccordion);
				ps._selPnl = panel; //stored in tabpanels
			}
			tabbox._selTab = this;
		}

		if (!this.desktop) return;

		if (toSel)
			jq(this).addClass(this.$s('selected'));
		else
			jq(this).removeClass(this.$s('selected'));

		if (panel && panel.isVisible()) //Bug ZK-1618: not show tabpanel if visible is false
			panel._sel(toSel, true);

		if (!inAccordion) {
			var tabs = this.parent;
			if (tabs) tabs._fixWidth(toSel); //ZK-2810: don't set height to tabbox when deselect
		}

		if (toSel) {
			if (tabbox.isVertical())
				this.parent?._scrollcheck('vsel', this);
			else if (!tabbox.inAccordionMold())
				this.parent?._scrollcheck('sel', this);
		}

		if (notify)
			this.fire('onSelect', {items: [this], reference: this});
	}

	override setHeight(height: string | null): void {
		super.setHeight(height);
		if (this.desktop) {
			this._calcHgh();
			zUtl.fireSized(this.parent!);
		}
	}

	override setWidth(width: string | null): void {
		super.setWidth(width);
		if (this.desktop)
			zUtl.fireSized(this.parent!);
	}

	_calcHgh(): void {
		var tabbox = this.getTabbox()!;

		if (!tabbox.isVertical()) {
			var r = tabbox.$n('right'),
				l = tabbox.$n('left'),
				tb = tabbox.toolbar?.$n(),
				tabs = tabbox.tabs!.$n(),
				hgh = jq.px0(tabs ? tabs.offsetHeight : 0);

			if (r && l) {
				r.style.height = l.style.height = hgh;
			}
			if (tb) {
				tb.style.height = hgh;
			}
		}
	}

	//protected
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (this._disabled) return;
		/* ZK-1441
		 * If tabbox is animating (end-user click different tabs quickly), ignore this action.
		 */
		if (this.getTabbox()?._animating) return;
		this.setSelected(true);
		super.doClick_(evt, popupOnly);
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			if (this.isDisabled()) scls += ' ' + this.$s('disabled');
			if (this.isSelected()) scls += ' ' + this.$s('selected');
		}
		return scls;
	}

	override domContent_(): string {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage(),
			iconSclass = this.domIcon_();

		if (!label) label = '&nbsp;';
		if (!img && !iconSclass) return label;
		if (!img) {
			img = iconSclass;
		} else
			img = '<img src="' + img + '" class="' + this.$s('image') + '" alt="" aria-hidden="true"/>'
			+ (iconSclass ? ' ' + iconSclass : '');
		return label ? img + ' ' + label : img;
	}

	//bug #3014664
	override setVflex(v: boolean | string | null | undefined): void { //vflex ignored for Tab
		if (v != 'min') v = false;
		super.setVflex(v);
	}

	//bug #3014664
	override setHflex(v: boolean | string | null | undefined): void { //hflex ignored for Tab
		if (v != 'min') v = false;
		super.setHflex(v);
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var closebtn = this.isClosable() ? this.$n('cls') : null;
		if (closebtn) {
			this.domListen_(closebtn, 'onClick', '_doCloseClick');
		}
		if (this.getHeight())
			this._calcHgh();

		//ZK-3016 make sure parent always do scrollCheck on child bind
		this.parent!._shallCheck = true;
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var closebtn = this.$n('cls');
		// ZK-886
		_logId(this);
		if (closebtn) {
			this.domUnlisten_(closebtn, 'onClick', '_doCloseClick');
		}
		super.unbind_(skipper, after, keepRod);
	}

	//event handler//
	onClose(): void {
		if (this.getTabbox()!.inAccordionMold()) {
			this.getTabbox()!._syncSize();
		}
	}

	override deferRedrawHTML_(out: string[]): void {
		const tag = this.getTabbox()!.inAccordionMold() ? 'div' : 'li';
		out.push('<', tag, this.domAttrs_({domClass: true}), ' class="z-renderdefer"></', tag, '>');
	}

	override rerender(skipper?: number | zk.Skipper | null): void {
		// ZK-886
		if (this.desktop)
			_logId(this);
		super.rerender(skipper);
	}

	contentRenderer_(out: string[]): void {
		out.push('<span id="', this.uuid, '-cnt" class="', this.$s('text'), '">', this.domContent_(), '</span>');
	}
}
/** @class zul.tab.TabRenderer
 * The renderer used to render a Tab.
 * It is designed to be overriden
 * @since 5.0.5
 */
export let TabRenderer = {
	/** Check the Tab whether to render the frame
	 */
	isFrameRequired(): boolean {
		return false;
	}
};
zul.tab.TabRenderer = TabRenderer;