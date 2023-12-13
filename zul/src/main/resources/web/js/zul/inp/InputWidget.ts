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

export interface CoerceFromStringResult {
	server?: boolean;
	error?: string;
}
export interface InputValidationResult<ValueType> extends CoerceFromStringResult {
	value?: ValueType;
	rawValue?: string;
}

var _keyIgnorable = zk.opera ? function (code: number) {
		return code == 32 || code > 46; //DEL
	} : function (code: number) {
		return code >= 32;
	},
	windowX = 0,
	windowY = 0;

/** @class zul.inp.RoundUtl
 * The RoundUtl used to adjust the display of the rounded input.
 * @since 5.0.7
 */
export var RoundUtl = {
	// @since 7.0.0
	buttonVisible<T>(wgt: InputWidget<T>, v: boolean): void {
		var n = wgt.$n('btn');
		if (n) {
			var fnm = v ? 'removeClass' as const : 'addClass' as const;
			jq(n)[fnm](wgt.$s('disabled'));
			jq(wgt.getInputNode())[fnm](wgt.$s('input-full'));
		}
	},
	// @since 7.0.0
	/** @internal */
	doFocus_<T>(wgt: InputWidget<T>): void {
		if (wgt._inplace) {
			if (wgt._inplaceTimerId != null) {
				clearTimeout(wgt._inplaceTimerId);
				wgt._inplaceTimerId = undefined;
			}
			wgt.onSize();
		}
	},
	/** @internal */
	doBlur_<T>(wgt: InputWidget<T>): void {
		if (wgt._inplace) {
			var n = wgt.$n_();
			if (wgt._inplaceTimerId != null) {
				clearTimeout(wgt._inplaceTimerId);
				wgt._inplaceTimerId = undefined;
			}
			wgt._inplaceTimerId = setTimeout(function () {
				if (wgt.desktop) jq(wgt.$n()).addClass(wgt.getInplaceCSS());
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
 * {@link onChangingDelay}, which is default to 350.
 * To change it, you can specify the following in a ZUL file.
 * ```html
 * <script content="zk.afterLoad('zul.inp',function(){zul.inp.InputWidget.onChangingDelay=1000;})"?>
 * ```
 */
@zk.WrapClass('zul.inp.InputWidget')
export class InputWidget<ValueType = unknown> extends zul.Widget<HTMLInputElement> {
	/** @internal */
	_maxlength = 0;
	/** @internal */
	_cols = 0;
	//_tabindex: 0,
	/** @internal */
	_type = 'text';
	/** @internal */
	_placeholder?: string;
	/** @internal */
	_inputAttributes?: Record<string, string>;
	/** @internal */
	_lastinputAttributes?: Record<string, string>;
	/** @internal */
	_inplaceTimerId?: number;
	/** @internal */
	_inplaceTimeout = 150;
	/** @internal */
	_inplaceIgnore = false;

	/** @internal */
	_name?: string;
	/** @internal */
	_cst?: zul.inp.SimpleConstraint | string;
	/** @internal */
	_reVald?: boolean;
	/** @internal */
	valueEnter_?: string;
	/** @internal */
	valueSel_?: string;
	/** @internal */
	_lastChg?: string;
	/** @internal */
	_errbox?: zul.inp.Errorbox;
	/** @internal */
	__ebox?: zul.inp.Errorbox;
	/** @internal */
	_tidChg?: number;
	/** @internal */
	_multiline?: boolean;
	/** @internal */
	_disabled = false;
	/** @internal */
	_readonly = false;
	/** @internal */
	_value?: ValueType;
	/** @internal */
	_errmsg?: string;
	/** @internal */
	_defRawVal?: string;
	/** @internal */
	_lastKeyDown?: number;
	/** @internal */
	_tabbable?: boolean;
	/** @internal */
	_instant?: boolean;
	/** @internal */
	_errorboxSclass?: string;
	/** @internal */
	_errorboxIconSclass?: string;
	/** @internal */
	_inplace?: boolean;

	/**
	 * @returns the name of this component.
	 * @defaultValue `null`.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	getName(): string | undefined {
		return this._name;
	}

	/**
	 * Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name - the name of this component.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			var inp = this.getInputNode();
			if (inp) //check if bind
				inp.name = name;
		}

		return this;
	}

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			var inp = this.getInputNode();
			if (inp) { //check if bind
				inp.disabled = disabled;
				var fnm = disabled ? 'addClass' as const : 'removeClass' as const;
				jq(this.$n())[fnm](this.$s('disabled'));
			}
		}

		return this;
	}

	/**
	 * @returns whether it is readonly.
	 * @defaultValue `false`.
	 */
	isReadonly(): boolean {
		return this._readonly;
	}

	/**
	 * Sets whether it is readonly.
	 */
	setReadonly(readonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._readonly;
		this._readonly = readonly;

		if (o !== readonly || opts?.force) {
			var inp = this.getInputNode();
			if (inp) {
				var fnm = readonly ? 'addClass' as const : 'removeClass' as const;

				inp.readOnly = readonly;
				jq(this.$n())[fnm](this.$s('readonly')); //Merge breeze
			}
		}

		return this;
	}

	/**
	 * @returns the cols.
	 * @defaultValue `0` (non-positive means the same as browser's default).
	 */
	getCols(): number {
		return this._cols;
	}

	/**
	 * Sets the cols.
	 */
	setCols(cols: number, opts?: Record<string, boolean>): this {
		const o = this._cols;
		this._cols = cols;

		if (o !== cols || opts?.force) {
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

	/**
	 * @returns the maxlength.
	 * @defaultValue `0` (non-postive means unlimited).
	 */
	getMaxlength(): number {
		return this._maxlength;
	}

	/**
	 * Sets the maxlength.
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || opts?.force) {
			var inp = this.getInputNode();
			if (inp && !this.isMultiline())
				inp.maxLength = maxlength;
		}

		return this;
	}

	/**
	 * @returns the tab order of this component.
	 * @defaultValue `0` (means the same as browser's default).
	 */
	override getTabindex(): number | undefined {
		return this._tabindex;
	}

	/**
	 * Sets the tab order of this component.
	 */
	override setTabindex(tabindex: number, opts?: Record<string, boolean>): this {
		const o = this._tabindex;
		this._tabindex = tabindex;

		if (o !== tabindex || opts?.force) {
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

	/**
	 * @returns whether enable the inplace-editing.
	 * @defaultValue `false`.
	 */
	isInplace(): boolean {
		return !!this._inplace;
	}

	/**
	 * Sets to enable the inplace-editing function that the look and feel is
	 * like a label.
	 */
	setInplace(inplace: boolean, opts?: Record<string, boolean>): this {
		const o = this._inplace;
		this._inplace = inplace;

		if (o !== inplace || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the placeholder text
	 * @since 6.5.0
	 */
	getPlaceholder(): string | undefined {
		return this._placeholder;
	}

	/**
	 * Sets the placeholder text that is displayed when input is empty.
	 * Only works for browsers supporting HTML5.
	 * @since 6.5.0
	 */
	setPlaceholder(placeholder: string, opts?: Record<string, boolean>): this {
		const o = this._placeholder;
		this._placeholder = placeholder;

		if (o !== placeholder || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the additional attributes which is set by setinputAttributes(inputAttributes).
	 * @since 8.6.1
	 */
	getInputAttributes(): Record<string, string> | undefined {
		return this._inputAttributes;
	}

	/**
	 * Sets some additional attributes to the input html tag in the component.
	 * this will only reset the additional attributes that are set by this method.
	 * @since 8.6.1
	 */
	setInputAttributes(inputAttributes: Record<string, string>, opts?: Record<string, boolean>): this {
		const o = this._inputAttributes;
		this._inputAttributes = inputAttributes;

		if (o !== inputAttributes || opts?.force) {
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

	/**
	 * @returns whether to send onChange event as soon as user types in the
	 * input.
	 * @defaultValue `false`.
	 * @since 6.0.0
	 */
	isInstant(): boolean {
		return !!this._instant;
	}

	/**
	 * Sets whether to send onChange event as soon as user types in the input.
	 * @since 6.0.0
	 */
	setInstant(instant: boolean): this {
		this._instant = instant;
		return this;
	}

	/**
	 * @returns the custom style class name applied to the errorbox, if any.
	 * @defaultValue `null`.
	 * @since 8.0.1
	 */
	getErrorboxSclass(): string | undefined {
		return this._errorboxSclass;
	}

	/**
	 * Sets the custom style class name to be applied to the errorbox.
	 * @since 8.0.1
	 */
	setErrorboxSclass(errorboxSclass: string): this {
		this._errorboxSclass = errorboxSclass;
		return this;
	}

	/**
	 * @returns the custom style icon class name applied to the errorbox, if any.
	 * @defaultValue `null`.
	 * @since 8.0.1
	 */
	getErrorboxIconSclass(): string | undefined {
		return this._errorboxIconSclass;
	}

	/**
	 * Sets the custom style icon class name to be applied to the errorbox.
	 * @since 8.0.1
	 */
	setErrorboxIconSclass(errorboxIconSclass: string): this {
		this._errorboxIconSclass = errorboxIconSclass;
		return this;
	}

	/**
	 * @returns the CSS style of inplace if inplace is not null
	 */
	getInplaceCSS(): string {
		return this._inplace ? this.$s('inplace') : '';
	}

	/**
	 * Selects the whole text in this input.
	 * @param start - the starting index of the selection range
	 * @param end - the ending index of the selection range (excluding).
	 * 		In other words, the text between start and (end-1) is selected.
	 */
	select(start: number, end: number): void {
		// bug ZK-1695: need to focus input and set selection range in Firefox
		var inpNode = this.getInputNode();
		if (inpNode) { // ZK-4538: can't be focused anyway unless rendered
			if (zk.currentFocus != inpNode as unknown) // FIXME: comparing a zk.Widget with a HTMLElement?
				this.focus_();

			if (start == null && end == null)
				inpNode.select();
			else
				zk(inpNode).setSelectionRange(start, end);
		}
	}

	/**
	 * @returns the type.
	 * @defaultValue text.
	 */
	getType(): string {
		return this._type;
	}

	/**
	 * @returns whether it is multiline.
	 * @defaultValue `false`.
	 */
	isMultiline(): boolean {
		return false;
	}

	/**
	 * @returns whether is in rounded mold or not.
	 * @defaultValue `false`, only combo component are true
	 * @since 7.0.0
	 */
	inRoundedMold(): boolean {
		return true;
	}

	/**
	 * @returns the text representing the value in the given format,
	 * or an empty etring if value is null
	 * @since 5.0.5
	 */
	getText(): string {
		return this.coerceToString_(this.getValue());
	}

	/**
	 * Sets the text representing the value in the given format.
	 * @param text - the text
	 * @since 5.0.5
	 */
	setText(text: string): this {
		return this.setValue(this.coerceFromString_(text) as ValueType);
	}

	/**
	 * @returns the value in the String format.
	 */
	getValue(): ValueType | undefined {
		return this._value;
	}

	/**
	 * Sets the value in the String format(assumes no locale issue).
	 * <p>Notice that the invocation of {@link getValue} won't fire
	 * the onChange event. To fire it, you have to invoke {@link fireOnChange}
	 * explicitly.
	 * @param value - the value.
	 * @param fromServer - whether it is called from the server.
	 * The error message will be cleared if true
	 */
	setValue(value: ValueType | number | string, fromServer?: boolean): this {
		var vi: InputValidationResult<ValueType> | undefined;
		// for stateless to treat as "value" attribute from "_value" at client side
		if (typeof value == 'number' || typeof value == 'string')
			value = this.unmarshall_(value);
		if (fromServer)
			this.clearErrorMessage(this._cst != null);
		else {
			vi = this._validate(value as ValueType);
			value = vi.value!;
		}

		InputWidget._clearOnChanging(this);

		//Note: for performance reason, we don't send value back if
		//the validation shall be done at server, i.e., if (vi.server)
		if ((!vi || !vi.error) && (fromServer || !this._equalValue(this._value, value as ValueType))) {
			this._value = value as ValueType;
			var inp = this.getInputNode();
			if (inp) //check if bind
				this._defRawVal = this._lastChg = inp.value = value = this.coerceToString_(value as ValueType);
		}
		return this;
	}

	//value object set from server(smartUpdate, renderProperites)
	// eslint-disable-next-line zk/javaStyleSetterSignature
	set_value(value: string | number, fromServer?: boolean): void {
		this.setValue(this.unmarshall_(value), fromServer);
	}

	/**
	 * @returns the input node of this widget
	 */
	override getInputNode(): HTMLInputElement | undefined {
		return this.$n('real') ?? this.$n();
	}

	override getTextNode(): HTMLInputElement | undefined {
		return this.getInputNode();
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if (!no || !no.text)
			attr += this.textAttrs_();
		return attr;
	}

	/**
	 * Attributes for the text control.
	 * Called automatically by {@link domAttrs_} unless `{text:true}` is specified
	 * @returns String
	 * @internal
	 */
	textAttrs_(): string {
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

	/** @internal */
	_onChanging(timeout?: number): void {
		InputWidget._onChanging.call(this, timeout);
	}

	/** @internal */
	_areaText(): string {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	}

	/**
	 * Sets the constraint.
	 * @defaultValue `null` (means no constraint all all).
	 */
	setConstraint(constraint: zul.inp.SimpleConstraint | string | undefined): this {
		if (typeof constraint == 'string' && !constraint.startsWith('[')/*by server*/)
			this._cst = new zul.inp.SimpleConstraint(constraint);
		else
			this._cst = constraint as zul.inp.SimpleConstraint | undefined;
		if (this._cst)
			this._reVald = true; //revalidate required
		return this;
	}

	/**
	 * @returns the constraint, or null if no constraint at all.
	 */
	getConstraint(): zul.inp.SimpleConstraint | string | undefined {
		return this._cst;
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		var inp = this.getInputNode()!;
		this._lastChg = inp.value;
		if (evt.domTarget.tagName) { //Bug 2111900
			if (this._inplace) {
				jq(this.$n()).removeClass(this.getInplaceCSS());
				if (this._inplaceTimerId != null) {
					clearTimeout(this._inplaceTimerId);
					this._inplaceTimerId = undefined;
				}
			}

			// Bug #2280308
			if (this._errbox) {
				var self = this, cstp = self._cst && (self._cst as zul.inp.SimpleConstraint)._pos;
				setTimeout(function () {
					if (self._errbox)
						self._errbox.open(self, undefined, cstp || self._errbox._defaultPos,
								{dodgeRef: !cstp}); // Bug 3251564
				});
			}
		}
	}

	/** @internal */
	override doBlur_(evt: zk.Event): void {
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
					if (self.desktop) jq(self.$n()).addClass(self.getInplaceCSS());
				}, self._inplaceTimeout);
			}
		}

		//B65-ZK-1285: scroll window object back when virtual keyboard closed on ipad
		if (zk.ios && jq(this.$n()).data('fixscrollposition')) { //only scroll back when data-fixScrollPosition attribute is applied
			var x = window.pageXOffset,
				y = window.pageYOffset;

			if (x != windowX || y != windowY)
				window.scrollTo(windowX, windowY);
		}
		this._lastKeyDown = undefined;
	}

	/** @internal */
	_doTouch(evt: zk.Event): boolean {
		if (!zk.ios) {
			return false;
		}
		//B65-ZK-1285: get window offset information before virtual keyboard opened on ipad
		windowX = window.pageXOffset;
		windowY = window.pageYOffset;
		return false;
	}

	/** @internal */
	_doSelect(evt: zk.Event): void { //domListen_
		if (this.isListen('onSelection')) {
			var inp = this.getInputNode()!,
				sr = zk(inp).getSelectionRange(),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e)});
		}
	}

	/** @internal */
	_doMouseOver(): void {
		if (this._disabled)
			return;
		jq(this.getInputNode()).addClass(this.$s('hover'));
	}

	/** @internal */
	_doMouseOut(): void {
		if (this._disabled)
			return;
		jq(this.getInputNode()).removeClass(this.$s('hover'));
	}

	/**
	 * @returns shall be update or not
	 * @internal
	 */
	shallUpdate_(focus: zk.Widget): boolean {
		return !focus || !zUtl.isAncestor(this, focus);
	}

	/**
	 * @returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 *
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 *
	 * <p>
	 * If the error message is set, we say this input is in the error mode.
	 * @deprecated use getErrorMessage() instead.
	 */
	getErrorMesssage(): string | undefined {
		return this.getErrorMessage();
	}

	/**
	 * @returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 *
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 *
	 * <p>
	 * If the error message is set, we say this input is in the error mode.
	 */
	getErrorMessage(): string | undefined {
		return this._errmsg;
	}

	/**
	 * Marks this widget's value is wrong and show the error message.
	 * <p>It is usually called by {@link zk.AuCmd0#wrongValue} (from the sever)
	 * @param msg - the error message
	 */
	setErrorMessage(errorMessage: string): this {
		this.clearErrorMessage(true, true);
		this._markError(errorMessage, undefined, true);
		return this;
	}

	/**
	 * Clears the error message and the error status.
	 * <p>It is also called by {@link zk.AuCmd0#clearWrongValue} (from the server).
	 * @param revalidate - whether to re-validate the value next time
	 * onblur occurs
	 * @param remainError - whether the input widget remains in the
	 * error status, if any, after the invocation.
	 */
	clearErrorMessage(revalidate: boolean, remainError?: boolean): void {
		var w = this._errbox;
		if (w) {
			this._errbox = undefined;
			w.destroy();
		}
		if (!remainError) {
			this._errmsg = undefined;
			jq(this.getInputNode()).removeClass(this.$s('invalid'));

		}
		if (revalidate)
			this._reVald = true; //revalidate required
	}

	/**
	 * Coerces the value passed to {@link setValue}.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link coerceToString_} and {@link coerceFromString_}
	 * to convert between a string and your targeting type.
	 *
	 * <p>Moreover, when {@link zul.inp.Textbox} is called, it calls this method
	 * with value = null. Derives shall handle this case properly.
	 *
	 * @param value - the string to coerce from
	 * @returns String
	 * @internal
	 */
	coerceFromString_(value: string | undefined): zul.inp.CoerceFromStringResult | ValueType | undefined {
		return value as unknown as ValueType;
	}

	/**
	 * Coerces the value passed to {@link setValue}.
	 *
	 * @defaultValue convert null to an empty string.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link coerceToString_} and {@link coerceFromString_}
	 * to convert between a string and your targeting type.
	 * @param value - the value that will be coerced to a string
	 * @returns String
	 * @internal
	 */
	coerceToString_(value: ValueType | undefined): string {
		return (value as unknown as string | undefined) || '';
	}

	/** @internal */
	_markError(msg: string, val?: string, noOnError?: boolean): void {
		this._errmsg = msg;

		if (this.desktop) { //err not visible if not attached //B85-ZK-3321
			jq(this.getInputNode()).addClass(this.$s('invalid'));
			
			interface CustomConstraint extends zul.inp.SimpleConstraint {
				showCustomError?: (inp: InputWidget<ValueType>, msg: string) => boolean;
			}
			var cst = this._cst,
				errbox: CustomConstraint['showCustomError'] | boolean;
			if (cst != '[c') {
				if (cst && (errbox = (cst as CustomConstraint).showCustomError))
					errbox = errbox.call(cst, this, msg);

				if (!errbox) this._errbox = this.showError_(msg);
			}

			if (!noOnError)
				this.fire('onError', {value: val, message: msg});
		}
	}

	/**
	 * Make the {@link zul.inp.SimpleConstraint} calls the validate for val,
	 * if {@link zul.inp.SimpleConstraint} is exist
	 * @param val - a String, a number, or a date,the number or name of flag,
	 * such as 'no positive", 0x0001.
	 * @internal
	 */
	validate_(val: unknown): string | boolean | undefined {
		var cst: zul.inp.SimpleConstraint | string | undefined;
		if (cst = this._cst) {
			if (typeof cst == 'string') return false; //by server
			var msg = cst.validate(this, val);
			if (!msg && cst.serverValidate) return false; //client + server
			return msg as string;
		}
	}

	/** @internal */
	_validate(value: string | ValueType | undefined): InputValidationResult<ValueType> {
		zul.inp.validating = true;
		try {
			var val: typeof value | CoerceFromStringResult = value,
				msg: string | boolean | undefined;
			if (typeof val == 'string' || val == null) {
				val = this.coerceFromString_(val as string);
				if (val && ((msg = (val as CoerceFromStringResult).error) || (val as CoerceFromStringResult).server)) {
					this.clearErrorMessage(true);
					if ((val as CoerceFromStringResult).server || this._cst as unknown == '[c') { //CustomConstraint
						this._reVald = false;
						return {rawValue: (value as string) || '', server: true}; //let server to validate it
					}
					this._markError(msg!, val as string);
					return val;
				}
			}

			//unlike server, validation occurs only if attached
			if (!this.desktop) this._errmsg = undefined;
			else {
				var em = this._errmsg;
				this.clearErrorMessage(true);
				msg = this.validate_(val);
				if (msg === false) {
					this._reVald = false;
					return {value: val as ValueType, server: true}; //let server to validate it
				}
				if (msg) {
					this._markError(msg as string, val as string);
					return {error: msg as string};
				}
				this._reVald = false;
				if (em)
					this._sendClearingErrorEvent(val);
			}
			return {value: val as ValueType};
		} finally {
			zul.inp.validating = false;
		}
	}

	/** @internal */
	_sendClearingErrorEvent(val: unknown): void {
		// ZK-4453 for easier overriding this behavior
		this.fire('onError', {value: val});
	}

	/** @internal */
	_shallIgnore(evt: zk.Event, keys: string): boolean {
		// ZK-1736 add metakey on mac
		if (zk.mac && evt.metaKey)
			return false;
		else {
			var code = zk.opera ? evt.keyCode : evt.charCode!;
			if (!evt.altKey && !evt.ctrlKey && _keyIgnorable(code) && !keys.includes(String.fromCharCode(code))) {
				evt.stop();
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a {@link zul.inp.Errorbox} widget, and show the error message
	 * @param msg - the error message
	 * @see zul.inp.Errorbox#show
	 * @internal
	 */
	showError_(msg: string): zul.inp.Errorbox {
		var eb = new zul.inp.Errorbox(this, msg);
		eb.show();
		return eb;
	}

	onShow(): void {
		if (this.__ebox) {
			this.setFloating_(true);
			this.__ebox.show();
		}
	}

	/** @internal */
	_equalValue(a: ValueType | undefined, b: ValueType | undefined): boolean {
		return a == b || this.marshall_(a) == this.marshall_(b);
	}

	/** @internal */
	marshall_(val: ValueType | undefined): string | undefined {
		return val as never;
	}

	/** @internal */
	unmarshall_(val: string | number): ValueType | '' | 0 {
		return val as never;
	}

	/**
	 * Updates the change to server by firing onChange if necessary.
	 * @returns boolean
	 * @internal
	 */
	updateChange_(): boolean {
		if (zul.inp.validating) return false; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.getInputNode()!,
			value = inp.value;
		if (!this._reVald && value == this._defRawVal /* ZK-658 */)
			return false; //not changed

		var wasErr = this._errmsg,
			vi = this._validate(value);
		if (!vi.error || vi.server) {
			var upd, data: InputValidationResult<ValueType> | undefined;
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
					this._value = vi.value; //vi - not coerced
					this._defRawVal = value;
				}
			}
			if (upd || vi.server)
				this.fire('onChange',
					InputWidget._onChangeData(this,
						data != null ? data as Record<string, unknown> : {value: this.marshall_(vi.value)}),
					vi.server ? {toServer: true} : undefined, 90);
		}
		return true;
	}

	/**
	 * Fires the onChange event.
	 * If the widget is created at the server, the event will be sent
	 * to the server too.
	 * @param opts - the options. Refer to {@link zk.Event#opts}
	 * @since 5.0.5
	 */
	fireOnChange(opts?: zk.EventOptions): void {
		this.fire('onChange',
			InputWidget._onChangeData(this, {value: this.marshall_(this.getValue())}), opts);
	}

	/** @internal */
	_resetForm(): void {
		var inp = this.getInputNode()!;
		if (inp.value != inp.defaultValue) { //test if it will be reset
			var wgt = this;
			setTimeout(function () {wgt.updateChange_();}, 0);
				//value not reset yet so wait a moment
		}
	}
	
	/** @internal */
	override focus_(timeout?: number): boolean {
		zk(this.getInputNode()).focus(timeout);
		return true;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
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

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n: HTMLInputElement | HTMLFormElement = this.getInputNode()!;

		this._lastChg = this._defRawVal = n.value;

		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_')
			.domListen_(n, 'onSelect')
			.domListen_(n, 'onMouseOver')
			.domListen_(n, 'onMouseOut')
			.domListen_(n, 'onInput', 'doInput_');

		if (zk.ios)
			this.domListen_(n, 'onTouchStart', '_doTouch');

		if (n.form)
			jq(n.form).on('reset', this.proxy(this._resetForm));
		zWatch.listen({onShow: this});
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onShow: this});
		InputWidget._stopOnChanging(this);
		this.clearErrorMessage(true);

		var n: HTMLInputElement | HTMLFormElement = this.getInputNode()!;
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_')
			.domUnlisten_(n, 'onSelect')
			.domUnlisten_(n, 'onMouseOver')
			.domUnlisten_(n, 'onMouseOut')
			.domUnlisten_(n, 'onInput', 'doInput_');

		if (zk.ios)
			this.domUnlisten_(n, 'onTouchStart', '_doTouch');

		if (n.form)
			jq(n.form).off('reset', this.proxy(this._resetForm));

		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	doInput_(evt: zk.Event): void {
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

	/** @internal */
	override resetSize_(orient: zk.FlexOrient): void {
		var n: HTMLInputElement | undefined;
		if (this.$n() != (n = this.getInputNode()))
			n!.style[orient == 'w' ? 'width' : 'height'] = '';
		super.resetSize_(orient);
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
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

	/** @internal */
	_updateValue(): void {
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

	/** @internal */
	_startOnChanging(): void {
		if (this.isListen('onChanging') || this._instant)
			InputWidget._startOnChanging(this);
	}

	/** @internal */
	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace) {
			if (!this._multiline && evt.keyCode == 13) {
				var $inp = jq(this.getInputNode()), inc = this.getInplaceCSS();
				if ($inp.toggleClass(inc).hasClass(inc))
					$inp.zk.setSelectionRange(0, $inp[0].value.length);
			} else
				jq(this.getInputNode()).removeClass(this.getInplaceCSS());
		}
		if (evt.keyCode != 13 || !this.isMultiline())
			return super.afterKeyDown_(evt);
		return false;
	}

	/** @internal */
	override beforeCtrlKeys_(evt: zk.Event): void {
		this.updateChange_();
	}

	/** @internal */
	override shallIgnoreClick_(evt: zk.Event): boolean {
		return this.isDisabled();
	}

	/**
	 * Inserts the text at the current cursor position.
	 * It would trigger focus and change event.
	 *
	 * @param insertedText - the text to be inserted
	 * @since 8.5.1
	 */
	setInsertedText(insertedText: string): this {
		if (insertedText) {
			var inp = this.getInputNode();
			if (inp) {
				var zkinp = zk(inp);
				// IE/Edge would get caretPos as 0 if not getting focus first
				if (zk.currentFocus != inp as unknown) // FIXME: comparing a zk.Widget with a HTMLElement?
					zkinp.focus();
				var caretPos = zkinp.getSelectionRange()[0],
					txt = this.getText(),
					before = txt.substring(0, caretPos),
					after = txt.substring(caretPos);
				caretPos += insertedText.length;
				this.setText(before + insertedText + after);
				this.select(caretPos, caretPos);
				this.fireOnChange();
			}
		}
		return this;
	}

	/** The delay for sending the onChanging event (unit: milliseconds).
	 * The onChanging event will be sent after the specified delay once
	 * the user pressed a keystroke (and changed the value).
	 * @defaultValue `350`
	 * @since 5.0.1
	 */
	static onChangingDelay = 350;

	/** Whether to send at least one the onChanging event if it is listened
	 * and the content is ever changed.
	 * @defaultValue `true`
	 * @since 5.0.1
	 */
	static onChangingForced = true;

	// for errorbox, datebox, combowidget
	/** @internal */
	static _isInView<T>(wgt: Pick<InputWidget<T>, 'getInputNode'>): boolean {
		var n = wgt.getInputNode();
		return zk(n).isRealScrollIntoView(true);
	}

	/** @internal */
	static _onChanging<T>(this: InputWidget<T>, timeout?: number): void {
		//Note: "this" is available here
		if (this.desktop) {
			var inp = this.getInputNode()!,
				val = this.valueEnter_ || inp.value;
			if (this._lastChg != val) {
				this._lastChg = val;
				var valsel = this.valueSel_;
				this.valueSel_ = undefined;
				if (this.isListen('onChanging'))
					this.fire('onChanging', zul.inp.InputWidget._onChangeData(this, {value: val}, valsel == val), //pass inp.value directly
						{ignorable: true, rtags: {onChanging: 1}}, timeout || 5);
				if (this._instant)
					this.updateChange_();
			}
		}
	}

	/** @internal */
	static _onChangeData<T>(wgt: InputWidget<T>, inf: Record<string, unknown>, selbk?: boolean): Record<string, unknown> {
		inf.start = zk(wgt.getInputNode()).getSelectionRange()[0];
		if (selbk) inf.bySelectBack = true;
		return inf;
	}

	/** @internal */
	static _startOnChanging<T>(wgt: InputWidget<T>): void {
		InputWidget._stopOnChanging(wgt);
		wgt._tidChg = setTimeout(
			wgt.proxy(InputWidget._onChanging), InputWidget.onChangingDelay);
	}

	/** @internal */
	static _stopOnChanging<T>(wgt: InputWidget<T>, onBlur?: boolean): void {
		if (wgt._tidChg) {
			clearTimeout(wgt._tidChg);
			wgt._tidChg = undefined;
		}
		if (onBlur) {
			if ((InputWidget.onChangingForced
				&& wgt.isListen('onChanging')) || wgt._instant) {
				InputWidget._onChanging.call(wgt, -1); //force
			}
			InputWidget._clearOnChanging(wgt);
		}
	}

	/** @internal */
	static _clearOnChanging<T>(wgt: InputWidget<T>): void {
		wgt.valueEnter_ = wgt.valueSel_ = undefined;
	}

	/** @internal */
	static _clearInplaceTimeout<T>(widget: InputWidget<T>): void {
		if (widget._inplaceTimerId) {
			clearTimeout(widget._inplaceTimerId);
			widget._inplaceTimerId = undefined;
		}
	}
}

/** @class zul.inp.InputCtrl
 * @import zk.Widget
 * @import jq.Event
 * @import zk.Draggable
 * The extra control for the InputWidget.
 * It is designed to be overriden
 * @since 6.5.0
 */
export var InputCtrl = {
	/**
	 * @returns whether to preserve the focus state.
	 * @param wgt - a widget
	 */
	isPreservedFocus(wgt: zk.Widget): boolean {
		return true;
	},
	/**
	 * @returns whether to preserve the mousemove state.
	 * @param wgt - a widget
	 */
	isPreservedMouseMove(wgt: zk.Widget): boolean {
		return true;
	},
	/**
	 * @returns whether to ignore the dragdrop for errorbox
	 * @param dg - the drag object
	 */
	isIgnoredDragForErrorbox(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var c = (dg.control as zk.Widget).$n('c');
		return evt.domTarget == c && jq(c).hasClass('z-errbox-close-over');
	}
};
zul.inp.InputCtrl = InputCtrl;