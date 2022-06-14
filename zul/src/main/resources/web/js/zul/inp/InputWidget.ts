/* InputWidget.ts

	Purpose:

	Description:

	History:
		Sat Dec 13 23:30:28     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The input related widgets, such as textbox and combobox.
 */
//zk.$package('zul.inp');

interface InputValidation {
	value?: unknown;
	rawValue?: unknown;
	server?: boolean;
	error?: string;
}

var _keyIgnorable = zk.opera ? function (code: number) {
		return code == 32 || code > 46; //DEL
	} : function (code: number) {
		return code >= 32;
	},
	_fixInput = zk.ie ? function (wgt: InputWidget) { //ZK-426; ZK-3237: IE 11 also have this problem
		setTimeout(function () { //we have to delay since zk.currentFocus might not be ready
			if (wgt == zk.currentFocus)
				window.zjq.fixInput(wgt.getInputNode()!);
		}, 0);
	} : zk.$void,
	windowX = 0,
	windowY = 0;

/** @class zul.inp.RoundUtl
 * The RoundUtl used to adjust the display of the rounded input.
 * @since 5.0.7
 */
export let RoundUtl = {
	// @since 7.0.0
	buttonVisible(wgt: InputWidget, v: boolean): void {
		var n = wgt.$n('btn');
		if (n) {
			var fnm = v ? 'removeClass' : 'addClass';
			// eslint-disable-next-line @typescript-eslint/no-unsafe-call
			jq(n)[fnm](wgt.$s('disabled'));
			// eslint-disable-next-line @typescript-eslint/no-unsafe-call
			jq(wgt.getInputNode()!)[fnm](wgt.$s('input-full'));
		}
	},
	// @since 7.0.0
	doFocus_(wgt: InputWidget): void {
		if (wgt._inplace) {
			if (wgt._inplaceTimerId != null) {
				clearTimeout(wgt._inplaceTimerId);
				wgt._inplaceTimerId = null;
			}
			wgt.onSize();
		}
	},
	doBlur_(wgt: InputWidget): void {
		if (wgt._inplace) {
			var n = wgt.$n()!;
			if (wgt._inplaceTimerId != null) {
				clearTimeout(wgt._inplaceTimerId);
				wgt._inplaceTimerId = null;
			}
			wgt._inplaceTimerId = setTimeout(function () {
				if (wgt.desktop) jq(wgt.$n()!).addClass(wgt.getInplaceCSS());
			}, wgt._inplaceTimeout);
			wgt.onSize();
			// should not clear node width if hflex is true
			if (!wgt.getHflex())
				n.style.width = wgt.getWidth() || '';
		}
	}
};
zul.inp.RoundUtl = RoundUtl;

/**
 * A skeletal implementation for a input widget.
 *
 * <p>The delay to send the onChanging event is controlled by
 * {@link #onChangingDelay}, which is default to 350.
 * To change it, you can specify the following in a ZUL file.
 * <pre><code>
&lt;?script content="zk.afterLoad('zul.inp',function(){zul.inp.InputWidget.onChangingDelay=1000;})"?&gt;
</code></pre>
 */
export class InputWidget extends zul.Widget {
	private _maxlength = 0;
	private _cols = 0;
	//_tabindex: 0,
	protected _type = 'text';
	private _placeholder: string | null = null;
	private _inputAttributes: Record<string, string> | null = null;
	private _lastinputAttributes: Record<string, string> | null = null;
	public _inplaceTimerId: number | null = null;
	public _inplaceTimeout = 150;
	private _inplaceIgnore = false;

	private _name?: string;
	public _cst?: zul.inp.SimpleConstraint | null;
	private _reVald?: boolean;
	protected valueEnter_?: string | null;
	protected valueSel_?: string | null;
	private _lastChg?: string;
	private _errbox?: zul.inp.Errorbox | null;
	public __ebox?: zul.inp.Errorbox;
	private _tidChg?: number | null;
	private _multiline?: boolean;
	private _disabled?: boolean;
	private _readonly?: boolean;
	protected _value?: string;
	private _errmsg?: string | null;
	private _defRawVal?: string;
	private _lastKeyDown?: number | null;
	private _tabbable?: boolean;
	private _instant?: boolean;
	public _errorboxSclass?: string;
	public _errorboxIconSclass?: string;
	public _inplace?: boolean;

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * @return String
	 */
	public getName(): string | undefined {
		return this._name;
	}

	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param String name the name of this component.
	 */
	public setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp) //check if bind
				inp.name = name;
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	public setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp) { //check if bind
				inp.disabled = disabled;
				var fnm = disabled ? 'addClass' : 'removeClass';
				// eslint-disable-next-line @typescript-eslint/no-unsafe-call
				jq(this.$n()!)[fnm](this.$s('disabled'));
			}
		}

		return this;
	}

	/** Returns whether it is readonly.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isReadonly(): boolean | undefined {
		return this._readonly;
	}

	/** Sets whether it is readonly.
	 * @param boolean readonly
	 */
	public setReadonly(readonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._readonly;
		this._readonly = readonly;

		if (o !== readonly || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp) {
				_fixInput(this);

				var fnm = readonly ? 'addClass' : 'removeClass';

				inp.readOnly = readonly;
				// eslint-disable-next-line @typescript-eslint/no-unsafe-call
				jq(this.$n()!)[fnm](this.$s('readonly')); //Merge breeze
			}
		}

		return this;
	}

	/** Returns the cols.
	 * <p>Default: 0 (non-positive means the same as browser's default).
	 * @return int
	 */
	public getCols(): number {
		return this._cols;
	}

	/** Sets the cols.
	 * @param int cols
	 */
	public setCols(cols: number, opts?: Record<string, boolean>): this {
		const o = this._cols;
		this._cols = cols;

		if (o !== cols || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp) {
				if (cols != 0) {
					interface ZULInputElement extends HTMLInputElement {
						cols: number;
					}
					if (this.isMultiline()) (inp as ZULInputElement).cols = cols;
					else inp.size = cols;
				} else {
					if (this.isMultiline()) inp.removeAttribute('cols');
					else inp.removeAttribute('size');
				}
			}
		}

		return this;
	}

	/** Returns the maxlength.
	 * <p>Default: 0 (non-postive means unlimited).
	 * @return int
	 */
	public getMaxlength(): number {
		return this._maxlength;
	}

	/** Sets the maxlength.
	 * @param int maxlength
	 */
	public setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp && !this.isMultiline())
				inp.maxLength = maxlength;
		}

		return this;
	}

	/** Returns the tab order of this component.
	 * <p>Default: 0 (means the same as browser's default).
	 * @return int
	 */
	public override getTabindex(): number | null | undefined {
		return this._tabindex;
	}

	/** Sets the tab order of this component.
	 * @param int tabindex
	 */
	public override setTabindex(tabindex: number, opts?: Record<string, boolean>): this {
		const o = this._tabindex;
		this._tabindex = tabindex;

		if (o !== tabindex || (opts && opts.force)) {
			var inp = this.getInputNode();
			if (inp) {
				if (tabindex == null)
					inp.removeAttribute('tabindex');
				else
					inp.tabIndex = tabindex;
			}
		}

		return this;
	}

	/** Returns whether enable the inplace-editing.
	 * <p>default: false.
	 * @return boolean
	 */
	public isInplace(): boolean | undefined {
		return this._inplace;
	}

	/** Sets to enable the inplace-editing function that the look and feel is
	 * like a label.
	 * @param boolean inplace
	 */
	public setInplace(inplace: boolean, opts?: Record<string, boolean>): this {
		const o = this._inplace;
		this._inplace = inplace;

		if (o !== inplace || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the placeholder text
	 * @since 6.5.0
	 * @return String placeholder
	 */
	public getPlaceholder(): string | null {
		return this._placeholder;
	}

	/**
	 * Sets the placeholder text that is displayed when input is empty.
	 * Only works for browsers supporting HTML5.
	 * @since 6.5.0
	 * @param String placeholder
	 */
	public setPlaceholder(placeholder: string, opts?: Record<string, boolean>): this {
		const o = this._placeholder;
		this._placeholder = placeholder;

		if (o !== placeholder || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the additional attributes which is set by setinputAttributes(inputAttributes).
	 * @since 8.6.1
	 * @return Map inputAttributes
	 */
	public getInputAttributes(): Record<string, string> | null {
		return this._inputAttributes;
	}

	/**
	 * Sets some additional attributes to the input html tag in the component.
	 * this will only reset the additional attributes that are set by this method.
	 * @since 8.6.1
	 * @param Map inputAttributes
	 */
	public setInputAttributes(inputAttributes: Record<string, string>, opts?: Record<string, boolean>): this {
		const o = this._inputAttributes;
		this._inputAttributes = inputAttributes;

		if (o !== inputAttributes || (opts && opts.force)) {
			if (this.desktop) {
				var inpNode = this.getInputNode()!;
				for (var key in this._lastinputAttributes) {
					inpNode.removeAttribute(key);
				}
				for (var key in this._inputAttributes) {
					var val = this._inputAttributes[key];
					inpNode.setAttribute(key, val);
				}
			}
			this._lastinputAttributes = inputAttributes;
		}

		return this;
	}

	/** Returns whether to send onChange event as soon as user types in the
	 * input.
	 * <p>Default: false.
	 * @return boolean
	 * @since 6.0.0
	 */
	public isInstant(): boolean | undefined {
		return this._instant;
	}

	/**
	 * Sets whether to send onChange event as soon as user types in the
	 * input.
	 * @param boolean instant
	 * @since 6.0.0
	 */
	public setInstant(instant: boolean): this {
		this._instant = instant;
		return this;
	}

	/** Returns the custom style class name applied to the errorbox, if any.
	 * <p>Default: null.
	 * @return String
	 * @since 8.0.1
	 */
	public getErrorboxSclass(): string | undefined {
		return this._errorboxSclass;
	}

	/**
	 * Sets the custom style class name to be applied to the errorbox.
	 * @param String errorboxSclass
	 * @since 8.0.1
	 */
	public setErrorboxSclass(errorboxSclass: string): this {
		this._errorboxSclass = errorboxSclass;
		return this;
	}

	/** Returns the custom style icon class name applied to the errorbox, if any.
	 * <p>Default: null.
	 * @return String
	 * @since 8.0.1
	 */
	public getErrorboxIconSclass(): string | undefined {
		return this._errorboxIconSclass;
	}

	/**
	 * Sets the custom style icon class name to be applied to the errorbox.
	 * @param String errorboxIconSclass
	 * @since 8.0.1
	 */
	public setErrorboxIconSclass(errorboxIconSclass: string): this {
		this._errorboxIconSclass = errorboxIconSclass;
		return this;
	}

	/** Returns the CSS style of inplace if inplace is not null
	 * @return String
	 */
	public getInplaceCSS(): string {
		return this._inplace ? this.$s('inplace') : '';
	}

	/** Selects the whole text in this input.
	 * @param int start the starting index of the selection range
	 * @param int end the ending index of the selection range (excluding).
	 * 		In other words, the text between start and (end-1) is selected.
	 */
	public select(start: number, end: number): void {
		// bug ZK-1695: need to focus input and set selection range in Firefox
		var inpNode = this.getInputNode();
		if (inpNode) { // ZK-4538: can't be focused anyway unless rendered
			if (zk.currentFocus != inpNode as unknown)
				this.focus_();

			if (start == null && end == null)
				inpNode.select();
			else
				zk(inpNode).setSelectionRange(start, end);
		}
	}

	/** Returns the type.
	 * <p>Default: text.
	 * @return String
	 */
	public getType(): string {
		return this._type;
	}

	/** Returns whether it is multiline.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isMultiline(): boolean {
		return false;
	}

	/**
	 * Returns whether is in rounded mold or not.
	 * <p>Default: false, only combo component are true (@since 7.0.0)
	 * @return boolean
	 */
	public inRoundedMold(): boolean {
		return true;
	}

	/** Returns the text representing the value in the given format,
	 * or an empty etring if value is null
	 * @return String
	 * @since 5.0.5
	 */
	public getText(): string {
		return this.coerceToString_(this.getValue());
	}

	/** Sets the text representing the value in the given format.
	 * @param String txt the text
	 * @since 5.0.5
	 */
	public setText(txt: string): void {
		this.setValue(this.coerceFromString_(txt));
	}

	/** Returns the value in the String format.
	 * @return String
	 */
	public getValue(): string | undefined {
		return this._value;
	}

	/** Sets the value in the String format(assumes no locale issue).
	 * <p>Notice that the invocation of {@link #getValue} won't fire
	 * the onChange event. To fire it, you have to invoke {@link #fireOnChange}
	 * explicitly.
	 * @param Object value the value.
	 * @param boolean fromServer whether it is called from the server.
	 * The error message will be cleared if true
	 */
	public setValue(value: unknown, fromServer?: boolean): void {
		var vi: InputValidation | undefined;
		// for zephyr to treat as "value" attribute from "_value" at client side
		if (typeof value == 'number' || typeof value == 'string')
			value = this.unmarshall_(value);
		if (fromServer)
			this.clearErrorMessage(this._cst != null);
		else {
			vi = this._validate(value);
			value = vi.value;
		}

		InputWidget._clearOnChanging(this);

		//Note: for performance reason, we don't send value back if
		//the validation shall be done at server, i.e., if (vi.server)
		if ((!vi || !vi.error) && (fromServer || !this._equalValue(this._value, value))) {
			this._value = value as string;
			var inp = this.getInputNode();
			if (inp) //check if bind
				this._defRawVal = this._lastChg = inp.value = value = this.coerceToString_(value as string);
		}
	}

	//value object set from server(smartUpdate, renderProperites)
	public set_value(value: void, fromServer?: boolean): void {
		this.setValue(this.unmarshall_(value), fromServer);
	}

	/** Returns the input node of this widget
	 * @return DOMElement
	 */
	// eslint-disable-next-line @typescript-eslint/ban-ts-comment
	// @ts-ignore
	public override getInputNode(): HTMLInputElement | null | undefined {
		return (this.$n('real') || this.$n()) as HTMLInputElement | null | undefined;
	}

	public override getTextNode(): HTMLInputElement | null | undefined {
		return this.getInputNode();
	}

	public override domAttrs_(no?: Partial<zk.DomAttrsOptions>): string {
		var attr = super.domAttrs_(no);
		if (!no || !no.text)
			attr += this.textAttrs_();
		return attr;
	}

	/** Attributes for the text control.
	 * Called automatically by [[#domAttrs_]] unless {text:true}
	 * is specified
	 * @return String
	 */
	protected textAttrs_(): string {
		var html = '', v;
		if (this.isMultiline()) {
			v = this._cols;
			if (v > 0) html += ' cols="' + v + '"';
		} else {
			html += ' value="' + this._areaText() + '"';
			html += ' type="' + this.getType() + '"';
			v = this._cols;
			if (v > 0) html += ' size="' + v + '"';
			v = this._maxlength;
			if (v > 0) html += ' maxlength="' + v + '"';
		}
		v = this._tabindex;
		if (v != undefined) html += ' tabindex="' + v + '"';
		v = this._name;
		if (v) html += ' name="' + v + '"';
		if (this._disabled) html += ' disabled="disabled"';
		if (this._readonly) html += ' readonly="readonly"';
		if (this._placeholder) html += ' placeholder="' + zUtl.encodeXML(this._placeholder) + '"';
		if (this._inputAttributes) {
			for (var key in this._inputAttributes) {
				var val = this._inputAttributes[key];
				html += (' ' + key + '="' + val + '"');
			}
		}

		var s = jq.filterTextStyle(this.domStyle_({width: true, height: true, top: true, left: true}));
		if (s) html += ' style="' + s + '"';

		return html;
	}

	protected _onChanging(timeout: number): void {
		InputWidget._onChanging.call(this, timeout);
	}

	private _areaText(): string {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	}

	/** Sets the constraint.
	 * <p>Default: null (means no constraint all all).
	 * @param String cst
	 */
	public setConstraint(cst: string | null): void {
		if (typeof cst == 'string' && cst.charAt(0) != '['/*by server*/)
			this._cst = new zul.inp.SimpleConstraint(cst);
		else
			this._cst = cst as null;
		if (this._cst)
			this._reVald = true; //revalidate required
	}

	/** Returns the constraint, or null if no constraint at all.
	 * @return zul.inp.SimpleConstraint
	 */
	public getConstraint(): zul.inp.SimpleConstraint | null | undefined {
		return this._cst;
	}

	protected override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		var inp = this.getInputNode()!;
		this._lastChg = inp.value;
		if (evt.domTarget!.tagName) { //Bug 2111900
			if (this._inplace) {
				jq(this.$n()!).removeClass(this.getInplaceCSS());
				if (this._inplaceTimerId != null) {
					clearTimeout(this._inplaceTimerId);
					this._inplaceTimerId = null;
				}
			}

			// Bug #2280308
			if (this._errbox) {
				var self = this, cstp = self._cst && self._cst._pos;
				setTimeout(function () {
					if (self._errbox)
						self._errbox.open(self, null, cstp || self._errbox._defaultPos,
								{dodgeRef: !cstp}); // Bug 3251564
				});
			}
		}
	}

	protected override doBlur_(evt: zk.Event): void {
		InputWidget._stopOnChanging(this, true);
		if (!zk.alerting && this.shallUpdate_(zk.currentFocus!)) {
			this.updateChange_();
			super.doBlur_(evt);
		}
		if (this._inplace) {
			InputWidget._clearInplaceTimeout(this);
			if (!this._inplaceIgnore) {
				var self = this;
				self._inplaceTimerId = setTimeout(function () {
					if (self.desktop) jq(self.$n()!).addClass(self.getInplaceCSS());
				}, self._inplaceTimeout);
			}
		}

		//B65-ZK-1285: scroll window object back when virtual keyboard closed on ipad
		if (zk.ios && jq(this.$n()!).data('fixscrollposition')) { //only scroll back when data-fixScrollPosition attribute is applied
			var x = window.pageXOffset,
				y = window.pageYOffset;

			if (x != windowX || y != windowY)
				window.scrollTo(windowX, windowY);
		}
		this._lastKeyDown = null;
	}

	protected _doTouch(evt: zk.Event): boolean | undefined {
		if (!zk.ios) {
			return false;
		}
		//B65-ZK-1285: get window offset information before virtual keyboard opened on ipad
		windowX = window.pageXOffset;
		windowY = window.pageYOffset;
	}

	protected _doSelect(evt: zk.Event): void { //domListen_
		if (this.isListen('onSelection')) {
			var inp = this.getInputNode()!,
				sr = zk(inp).getSelectionRange(),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e)});
		}
	}

	protected _doMouseOver(): void {
		if (this._disabled)
			return;
		jq(this.getInputNode()!).addClass(this.$s('hover'));
	}

	protected _doMouseOut(): void {
		if (this._disabled)
			return;
		jq(this.getInputNode()!).removeClass(this.$s('hover'));
	}

	/** Returns shall be update or not
	 * @param zk.Widget focus
	 */
	protected shallUpdate_(focus: zk.Widget): boolean {
		return !focus || !zUtl.isAncestor(this, focus);
	}

	/** Returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 *
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 *
	 * <p>
	 * If the error message is set, we say this input is in the error mode.
	 * @return String
	 * @deprecated use getErrorMessage() instead.
	 */
	public getErrorMesssage(): string | null | undefined {
		return this.getErrorMessage();
	}

	/** Returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 *
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 *
	 * <p>
	 * If the error message is set, we say this input is in the error mode.
	 * @return String
	 */
	public getErrorMessage(): string | null | undefined {
		return this._errmsg;
	}

	/** Marks this widget's value is wrong and show the error message.
	 * <p>It is usually called by {@link zk.AuCmd0#wrongValue} (from the sever)
	 * @param String msg the error message
	 */
	public setErrorMessage(msg: string): void {
		this.clearErrorMessage(true, true);
		this._markError(msg, null, true);
	}

	/** Clears the error message and the error status.
	 * <p>It is also called by {@link zk.AuCmd0#clearWrongValue} (from the server).
	 * @param boolean revalidate whether to re-validate the value next time
	 * onblur occurs
	 * @param boolean remainError whether the input widget remains in the
	 * error status, if any, after the invocation.
	 */
	public clearErrorMessage(revalidate: boolean, remainError?: boolean): void {
		var w = this._errbox;
		if (w) {
			this._errbox = null;
			w.destroy();
		}
		if (!remainError) {
			this._errmsg = null;
			jq(this.getInputNode()!).removeClass(this.$s('invalid'));

		}
		if (revalidate)
			this._reVald = true; //revalidate required
	}

	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString_} and {@link #coerceFromString_}
	 * to convert between a string and your targeting type.
	 *
	 * <p>Moreover, when {@link zul.inp.Textbox} is called, it calls this method
	 * with value = null. Derives shall handle this case properly.
	 *
	 * @param String value the string to coerce from
	 * @return String
	 */
	protected coerceFromString_(value: string | null | undefined): string | null | undefined {
		return value;
	}

	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString_} and {@link #coerceFromString_}
	 * to convert between a string and your targeting type.
	 * @param Object value the value that will be coerced to a string
	 * @return String
	 */
	protected coerceToString_(value: unknown): string {
		return (value as string | null | undefined) || '';
	}

	private _markError(msg: string, val: string | null, noOnError?: boolean): void {
		this._errmsg = msg;

		if (this.desktop) { //err not visible if not attached //B85-ZK-3321
			jq(this.getInputNode()!).addClass(this.$s('invalid'));
			
			interface CustomConstraint extends zul.inp.SimpleConstraint {
				showCustomError?: (inp: InputWidget, msg: string) => boolean;
			}
			var cst: CustomConstraint | null | undefined = this._cst,
				errbox: CustomConstraint['showCustomError'] | boolean;
			if (cst as unknown != '[c') {
				if (cst && (errbox = cst.showCustomError))
					errbox = errbox.call(cst, this, msg);

				if (!errbox) this._errbox = this.showError_(msg);
			}

			if (!noOnError)
				this.fire('onError', {value: val, message: msg});
		}
	}

	/** Make the {@link zul.inp.SimpleConstraint} calls the validate for val,
	 * if {@link zul.inp.SimpleConstraint} is exist
	 * @param Object val a String, a number, or a date,the number or name of flag,
	 * such as 'no positive", 0x0001.
	 */
	protected validate_(val: unknown): string | boolean | undefined {
		var cst: zul.inp.SimpleConstraint | null | undefined;
		if (cst = this._cst) {
			if (typeof cst == 'string') return false; //by server
			var msg = cst.validate(this, val);
			if (!msg && cst.serverValidate) return false; //client + server
			return msg as string;
		}
	}

	private _validate(value: unknown): InputValidation {
		zul.inp.validating = true;
		try {
			var val = value,
				msg: string | boolean | undefined;
			if (typeof val == 'string' || val == null) {
				val = this.coerceFromString_(val as string);
				if (val && ((msg = (val as {error: string}).error) || (val as {server: boolean}).server)) {
					this.clearErrorMessage(true);
					if ((val as {server: boolean}).server || this._cst as unknown == '[c') { //CustomConstraint
						this._reVald = false;
						return {rawValue: value || '', server: true}; //let server to validate it
					}
					this._markError(msg, val as string);
					return val as {server?: boolean; error?: string};
				}
			}

			//unlike server, validation occurs only if attached
			if (!this.desktop) this._errmsg = null;
			else {
				var em = this._errmsg;
				this.clearErrorMessage(true);
				msg = this.validate_(val);
				if (msg === false) {
					this._reVald = false;
					return {value: val, server: true}; //let server to validate it
				}
				if (msg) {
					this._markError(msg as string, val as string);
					return {error: msg as string};
				}
				this._reVald = false;
				if (em)
					this._sendClearingErrorEvent(val);
			}
			return {value: val};
		} finally {
			zul.inp.validating = false;
		}
	}

	private _sendClearingErrorEvent(val: unknown): void {
		// ZK-4453 for easier overriding this behavior
		this.fire('onError', {value: val});
	}

	public _shallIgnore(evt: zk.Event, keys: string): boolean | undefined {
		// ZK-1736 add metakey on mac
		if (zk.mac && evt.metaKey)
			return;
		else {
			var code = (zk.ie < 11 || zk.opera) ? evt.keyCode : evt.charCode as number;
			if (!evt.altKey && !evt.ctrlKey && _keyIgnorable(code) && keys.indexOf(String.fromCharCode(code)) < 0) {
				evt.stop();
				return true;
			}
		}
	}

	/** Create a {@link zul.inp.Errorbox} widget, and show the error message
	 * @param String msg the error message
	 * @see zul.inp.Errorbox#show
	 */
	protected showError_(msg: string): zul.inp.Errorbox {
		var eb = new zul.inp.Errorbox(this, msg);
		eb.show();
		return eb;
	}

	public onShow(): void {
		if (this.__ebox) {
			this.setFloating_(true);
			this.__ebox.show();
		}
	}

	private _equalValue(a: unknown, b: unknown): boolean {
		return a == b || this.marshall_(a) == this.marshall_(b);
	}

	protected marshall_<T>(val: T): T {
		return val;
	}

	protected unmarshall_<T>(val: T): T {
		return val;
	}

	/** Updates the change to server by firing onChange if necessary.
	 * @return boolean
	 */
	protected updateChange_(): boolean {
		if (zul.inp.validating) return false; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.getInputNode()!,
			value = inp.value;
		if (!this._reVald && value == this._defRawVal /* ZK-658 */)
			return false; //not changed

		var wasErr = this._errmsg,
			vi = this._validate(value);
		if (!vi.error || vi.server) {
			var upd, data: InputValidation | undefined;
			if (vi.rawValue != null) { //coerce failed
				data = {rawValue: vi.rawValue};
			} else if (!vi.error) {
				/*
				 * ZK-1220: with instant="true", inp.value = value will occur position error when change position.
				 * Datebox, Timebox and FormatWidget which assign format can't avoid this issue.
				 * Because they will change the "value" all the time.
				 */
				value = this.coerceToString_(vi.value);
				if (inp.value !== value) {
					inp.value = value;
				}
				this._reVald = false;

				//reason to use this._defRawVal rather than this._value is
				//to save the trouble of coerceToString issue
				upd = wasErr || !this._equalValue(vi.value, this._value);
				if (upd) {
					this._value = vi.value as string; //vi - not coerced
					this._defRawVal = value;
				}
			}
			if (upd || vi.server)
				this.fire('onChange',
					InputWidget._onChangeData(this,
						data != null ? data as Record<string, unknown> : {value: this.marshall_(vi.value)}),
					vi.server ? {toServer: true} : null, 90);
		}
		return true;
	}

	/** Fires the onChange event.
	 * If the widget is created at the server, the event will be sent
	 * to the server too.
	 * @param Map opts [optional] the options. Refer to {@link zk.Event#opts}
	 * @since 5.0.5
	 */
	public fireOnChange(opts?: Partial<zk.EventOptions>): void {
		this.fire('onChange',
			InputWidget._onChangeData(this, {value: this.marshall_(this.getValue())}), opts);
	}

	private _resetForm(): void {
		var inp = this.getInputNode()!;
		if (inp.value != inp.defaultValue) { //test if it will be reset
			var wgt = this;
			setTimeout(function () {wgt.updateChange_();}, 0);
				//value not reset yet so wait a moment
		}
	}

	//super//
	public override focus_(timeout?: number): boolean {
		// ZK-2020: should give timeout for ie11
		if (zk.ie11_ && !timeout)
			timeout = 0;
		zk(this.getInputNode()).focus(timeout);
		return true;
	}

	protected override domClass_(no?: Partial<zk.DomClassOptions>): string {
		var sc = super.domClass_(no);
		if ((!no || !no.zclass) && this._disabled)
			sc += ' ' + this.$s('disabled');

		if ((!no || !no.input) && this._inplace)
			sc += ' ' + this.getInplaceCSS();

		// Merge breeze
		if ((!no || !no.zclass) && this._readonly)
			sc += ' ' + this.$s('readonly');

		return sc;
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n: HTMLInputElement | HTMLFormElement | null = this.getInputNode()!;

		this._lastChg = this._defRawVal = n.value;

		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_')
			.domListen_(n, 'onSelect')
			.domListen_(n, 'onMouseOver')
			.domListen_(n, 'onMouseOut');
		//prevent unexpected onInput bug in IE10 and IE11, see https://connect.microsoft.com/IE/feedback/details/816137
		if (zk.ie10_ || zk.ie11_) {
			var self = this;
			setTimeout(function () {
				self.domListen_(self.getInputNode()!, 'onInput', 'doInput_');
			}, 100);
		} else {
			this.domListen_(n, 'onInput', 'doInput_');
		}

		if (zk.ios)
			this.domListen_(n, 'onTouchStart', '_doTouch');

		if (n = n.form)
			jq(n).on('reset', this.proxy(this._resetForm));
		zWatch.listen({onShow: this});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onShow: this});
		InputWidget._stopOnChanging(this);
		this.clearErrorMessage(true);

		var n: HTMLInputElement | HTMLFormElement | null = this.getInputNode()!;
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_')
			.domUnlisten_(n, 'onSelect')
			.domUnlisten_(n, 'onMouseOver')
			.domUnlisten_(n, 'onMouseOut')
			.domUnlisten_(n, 'onInput', 'doInput_');

		if (zk.ios)
			this.domUnlisten_(n, 'onTouchStart', '_doTouch');

		if (n = n.form)
			jq(n).off('reset', this.proxy(this._resetForm));

		super.unbind_(skipper, after, keepRod);
	}

	protected doInput_(evt: zk.Event): void {
		//ZK-2757, fire onChange when native drag'n' drop in different browsers
		var wgt = this;
		//in IE, current focus changes after onInput event
		setTimeout(function () {
			if (wgt && !zk.chrome && !wgt._lastKeyDown && zk.currentFocus != wgt)
				wgt.doBlur_(evt); //fire onBlur again
		}, 10);
		// ZK-4938: fire an onChanging event when users enter a predictive text
		this._updateValue();
	}

	protected override resetSize_(orient: zk.FlexOrient): void {
		var n: HTMLInputElement | null | undefined;
		if (this.$n() != (n = this.getInputNode()))
			n!.style[orient == 'w' ? 'width' : 'height'] = '';
		super.resetSize_(orient);
	}

	protected override doKeyDown_(evt: zk.Event): void {
		var keyCode = evt.keyCode;
		this._lastKeyDown = keyCode;
		if (this._readonly && keyCode == 8 && evt.target == this) {
			evt.stop(); // Bug #2916146
			return;
		}

		if (keyCode == 9 && !evt.altKey && !evt.ctrlKey && !evt.shiftKey
		&& this._tabbable) {
			var inp = this.getInputNode()!,
				$inp = zk(inp),
				sr = $inp.getSelectionRange(),
				val: string | number = inp.value;
			val = val.substring(0, sr[0]) + '\t' + val.substring(sr[1]);
			inp.value = val;

			val = sr[0] + 1;
			$inp.setSelectionRange(val, val);

			evt.stop();
			return;
		}

		InputWidget._stopOnChanging(this); // wait for onInput

		super.doKeyDown_(evt);
	}

	private _updateValue(): void {
		//Support maxlength for Textarea
		if (this.isMultiline()) {
			var maxlen = this._maxlength;
			if (maxlen > 0) {
				var inp = this.getInputNode()!,
					val = inp.value;
				if (val != this._defRawVal && val.length > maxlen) {
					inp.value = val.substring(0, maxlen);
				}
			}
		}
		this._startOnChanging();
	}

	private _startOnChanging(): void {
		if (this.isListen('onChanging') || this._instant)
			InputWidget._startOnChanging(this);
	}

	protected override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean | undefined {
		if (!simulated && this._inplace) {
			if (!this._multiline && evt.keyCode == 13) {
				var $inp = jq(this.getInputNode()!), inc = this.getInplaceCSS();
				if ($inp.toggleClass(inc).hasClass(inc))
					$inp.zk.setSelectionRange(0, $inp[0].value.length);
			} else
				jq(this.getInputNode()!).removeClass(this.getInplaceCSS());
		}
		if (evt.keyCode != 13 || !this.isMultiline())
			return super.afterKeyDown_(evt);
	}

	protected override beforeCtrlKeys_(evt: zk.Event): void {
		this.updateChange_();
	}

	public override shallIgnoreClick_(evt: zk.Event): boolean | undefined {
		return this.isDisabled();
	}

	/**
	 * Inserts the text at the current cursor position.
	 * It would trigger focus and change event.
	 *
	 * @param String text the text to be inserted
	 * @since 8.5.1
	 */
	public setInsertedText(text: string): void {
		if (text) {
			var inp = this.getInputNode();
			if (inp) {
				var zkinp = zk(inp);
				// IE/Edge would get caretPos as 0 if not getting focus first
				if (zk.currentFocus != inp as unknown)
					zkinp.focus();
				var caretPos = zkinp.getSelectionRange()[0],
					txt = this.getText(),
					before = txt.substring(0, caretPos),
					after = txt.substring(caretPos);
				caretPos += text.length;
				this.setText(before + text + after);
				this.select(caretPos, caretPos);
				this.fireOnChange();
			}
		}
	}

	/** The delay for sending the onChanging event (unit: milliseconds).
	 * The onChanging event will be sent after the specified delay once
	 * the user pressed a keystroke (and changed the value).
	 * <p>Default: 350
	 * @type int
	 * @since 5.0.1
	 */
	public static onChangingDelay = 350;

	/** Whether to send at least one the onChanging event if it is listened
	 * and the content is ever changed.
	 * <p>Default: true
	 * @type boolean
	 * @since 5.0.1
	 */
	public static onChangingForced = true;

	// for errorbox, datebox, combowidget
	public static _isInView(wgt: Pick<InputWidget, 'getInputNode'>): boolean {
		var n = wgt.getInputNode();
		return zk(n).isRealScrollIntoView(true);
	}

	private static _onChanging(this: InputWidget, timeout: number): void {
		//Note: "this" is available here
		if (this.desktop) {
			var inp = this.getInputNode()!,
				val = this.valueEnter_ || inp.value;
			if (this._lastChg != val) {
				this._lastChg = val;
				var valsel = this.valueSel_;
				this.valueSel_ = null;
				if (this.isListen('onChanging'))
					this.fire('onChanging', zul.inp.InputWidget._onChangeData(this, {value: val}, valsel == val), //pass inp.value directly
						{ignorable: 1 as unknown as boolean, rtags: {onChanging: 1}}, timeout || 5);
				if (this._instant)
					this.updateChange_();
			}
		}
	}

	private static _onChangeData(wgt: InputWidget, inf: Record<string, unknown>, selbk?: boolean): Record<string, unknown> {
		inf.start = zk(wgt.getInputNode()).getSelectionRange()[0];
		if (selbk) inf.bySelectBack = true;
		return inf;
	}

	private static _startOnChanging(wgt: InputWidget): void {
		InputWidget._stopOnChanging(wgt);
		wgt._tidChg = setTimeout(
			wgt.proxy(InputWidget._onChanging), InputWidget.onChangingDelay);
	}

	private static _stopOnChanging(wgt: InputWidget, onBlur?: boolean): void {
		if (wgt._tidChg) {
			clearTimeout(wgt._tidChg);
			wgt._tidChg = null;
		}
		if (onBlur) {
			if ((InputWidget.onChangingForced
				&& wgt.isListen('onChanging')) || wgt._instant) {
				InputWidget._onChanging.call(wgt, -1); //force
			}
			InputWidget._clearOnChanging(wgt);
		}
	}

	private static _clearOnChanging(wgt: InputWidget): void {
		wgt.valueEnter_ = wgt.valueSel_ = null;
	}

	private static _clearInplaceTimeout(widget: InputWidget): void {
		if (widget._inplaceTimerId) {
			clearTimeout(widget._inplaceTimerId);
			widget._inplaceTimerId = null;
		}
	}
}
zul.inp.InputWidget = zk.regClass(InputWidget);

/** @class zul.inp.InputCtrl
 * @import zk.Widget
 * @import jq.Event
 * @import zk.Draggable
 * The extra control for the InputWidget.
 * It is designed to be overriden
 * @since 6.5.0
 */
export let InputCtrl = {
	/**
	 * Returns whether to preserve the focus state.
	 * @param zk.Widget wgt a widget
	 * @return boolean
	 */
	isPreservedFocus(wgt: zk.Widget): boolean {
		return true;
	},
	/**
	 * Returns whether to preserve the mousemove state.
	 * @param zk.Widget wgt a widget
	 * @return boolean
	 */
	isPreservedMouseMove(wgt: zk.Widget): boolean {
		return true;
	},
	/**
	 * Returns whether to ignore the dragdrop for errorbox
	 * @param zk.Draggable dg the drag object
	 * @param Offset pointer
	 * @param jq.Event evt
	 * @return boolean
	 */
	isIgnoredDragForErrorbox(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var c = dg.control!.$n('c');
		return evt.domTarget == c && jq(c!).hasClass('z-errbox-close-over');
	}
};
zul.inp.InputCtrl = InputCtrl;