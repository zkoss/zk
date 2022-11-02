/* Applet.ts

	Purpose:

	Description:

	History:
		Wed Mar 25 17:11:55     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The multimedia widgets, such as applet and audio.
 */
//zk.$package('zul.med');

/**
 * A generic applet component.
 *
 * <p>
 * Non XUL extension.
 * <p>Note: {@link setVisible} with false cannot work in IE. (Browser's limitation)
 */
@zk.WrapClass('zul.med.Applet')
export class Applet extends zul.Widget<HTMLAppletElement> {
	/** @internal */
	_code?: string;
	/** @internal */
	_codebase?: string;
	/** @internal */
	_archive?: string;
	/** @internal */
	_mayscript?: boolean;
	/** @internal */
	_params: Partial<HTMLAppletElement>;
	/** @internal */
	_align?: string;
	/** @internal */
	_hspace?: string;
	/** @internal */
	_vspace?: string;

	constructor() {
		super(); // FIXME: params?
		this._params = {};
		// NOTE: Prior to TS migration, super is called after _params is initialized
		// above, but it seems to not matter, as nowhere does super nor do zkcml
		// overrides use _params during initialization.
	}

	/**
	 * @returns the applet class to run.
	 */
	getCode(): string | undefined {
		return this._code;
	}

	/**
	 * Sets the applet class to run.
	 */
	setCode(code: string, opts?: Record<string, boolean>): this {
		const o = this._code;
		this._code = code;

		if (o !== code || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns a relative base URL for applets specified in {@link setCode} (URL).
	 */
	getCodebase(): string | undefined {
		return this._codebase;
	}

	/**
	 * Sets a relative base URL for applets specified in {@link setCode} (URL).
	 */
	setCodebase(codebase: string, opts?: Record<string, boolean>): this {
		const o = this._codebase;
		this._codebase = codebase;

		if (o !== codebase || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the location of an archive file (URL).
	 * @since 5.0.3
	 */
	getArchive(): string | undefined {
		return this._archive;
	}

	/**
	 * Sets the location of an archive file (URL).
	 * @since 5.0.3
	 */
	setArchive(archive: string, opts?: Record<string, boolean>): this {
		const o = this._archive;
		this._archive = archive;

		if (o !== archive || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether the applet is allowed to access the scripting object.
	 * @since 5.0.3
	 */
	isMayscript(): boolean {
		return !!this._mayscript;
	}

	/**
	 * Sets whether the applet is allowed to access the scripting object.
	 * @since 5.0.3
	 */
	setMayscript(mayscript: boolean, opts?: Record<string, boolean>): this {
		const o = this._mayscript;
		this._mayscript = mayscript;

		if (o !== mayscript || opts?.force) {
			const n = this.$n();
			if (n)
				n.mayscript = mayscript; // Use of non-standard `mayscript` attribute
		}

		return this;
	}

	/**
	 * @returns the alignment of an applet according to surrounding elements.
	 * @since 5.0.3
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/**
	 * Sets the alignment of an applet according to surrounding elements.
	 * @since 5.0.3
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.$n();
			if (n)
				n.align = align;
		}

		return this;
	}

	/**
	 * @returns the horizontal spacing around an applet.
	 * @since 5.0.3
	 */
	getHspace(): string | undefined {
		return this._hspace;
	}

	/**
	 * Sets the horizontal spacing around an applet.
	 * @since 5.0.3
	 */
	setHspace(hspace: string, opts?: Record<string, boolean>): this {
		const o = this._hspace;
		this._hspace = hspace;

		if (o !== hspace || opts?.force) {
			var n = this.$n();
			if (n)
				n.hspace = hspace as unknown as number;
		}

		return this;
	}

	/**
	 * @returns the vertical spacing around an applet.
	 * @since 5.0.3
	 */
	getVspace(): string | undefined {
		return this._vspace;
	}

	/**
	 * Sets the vertical spacing around an applet.
	 * @since 5.0.3
	 */
	setVspace(vspace: string, opts?: Record<string, boolean>): this {
		const o = this._vspace;
		this._vspace = vspace;

		if (o !== vspace || opts?.force) {
			var n = this.$n();
			if (n)
				n.vspace = vspace as unknown as number;
		}

		return this;
	}

	/**
	 * Invokes the function of the applet running at the client.
	 */
	invoke(): void {
		// Empty on purpose. To be overriden in the following static initialization block.
	}
	static {
		Applet.prototype.invoke = zk.ie ? function (this: Applet) {
			const n = this.$n(),
				len = arguments.length;
			if (n && len >= 1) {
				const fn = arguments[0] as string;
				try {
					// eslint-disable-next-line @typescript-eslint/ban-types
					const func = n[fn] as Function;
					if (len === 1)
						func();
					else if (len === 2)
						func(arguments[1]);
					else {
						const args = [...arguments as unknown as []];
						args.shift();
						func(args);
					}
				} catch (e) {
					zk.error('Failed to invoke applet\'s method: ' + fn + '\n' + (e as Error).message);
				}
			}
		} : function (this: Applet) {
			const n = this.$n(),
				len = arguments.length;
			if (n && len >= 1) {
				const fn = arguments[0] as string,
					// eslint-disable-next-line @typescript-eslint/ban-types
					func = n[fn] as Function | undefined;
				if (!func) {
					zk.error('Method not found: ' + fn);
					return;
				}
				try {
					const args: unknown[] = [],
						arrayArg: unknown[] = [];
					if (arguments.length < 3) {
						if (arguments[1])
							args.push(arguments[1]);
					} else {
						for (var j = 1; j < len;)
							arrayArg.push(arguments[j++]);
						args.push(arrayArg);
					}
					func.call(n, ...args);
				} catch (e) {
					zk.error('Failed to invoke applet\'s method: ' + fn + '\n' + (e as Error).message);
				}
			}
		};
	}

	/**
	 * @returns the value of the specified field.
	 */
	getField(name: string): string | undefined {
		var n = this.$n();
		return n ? n[name] as string | undefined : undefined;
	}

	/**
	 * Sets the value of the specified filed.
	 */
	setField(field: string, value: string): this {
		const n = this.$n();
		if (n) {
			try {
				n[field] = value;
			} catch (e) {
				zk.error('Failed to set applet\'s field: ' + field + '\n' + (e as Error).message);
			}
		}
		return this;
	}

	/**
	 * Sets the param. Notice that it is meaningful only if it is called
	 * before redraw. For example, `setParam('attr1', 'value1')`
	 * gives a `param` tag under `applet` tag with name `attr1`, value `value1`.
	 * {@label SEPARATE_PARAMS}
	 */
	setParam(param: string, val: string): this
	/**
	 * @see {@link Applet.(setParam:SEPARATE_PARAMS)}
	 */
	setParam(param: [param: string, val: string]): this
	setParam(param: string | [string, string], val?: string): this {
		if (arguments.length == 1) {
			val = param[1];
			param = param[0];
		}
		if (val != null) this._params[param as string] = val;
		else delete this._params[param as string];
		return this;
	}

	/**
	 * Sets the params map. It should only be called before redraw.
	 * @param params - A map of param pairs, as applet parameters. For example,
	 * `{attr1:'value1', attr2:'value2'}` gives two `param`
	 * tags under `applet` tag with names `attr1, attr2`,
	 * values `value1, value2` respectively.
	 * @since 5.0.4
	 */
	setParams(params: Partial<HTMLAppletElement>): this {
		this._params = params;
		return this;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
				+ ' code="' + (this._code || '') + '"'
				+ zUtl.appendAttr('codebase', this._codebase)
				+ zUtl.appendAttr('archive', this._archive)
				+ zUtl.appendAttr('align', this._align)
				+ zUtl.appendAttr('hspace', this._hspace)
				+ zUtl.appendAttr('vspace', this._vspace)
				+ zUtl.appendAttr('mayscript', this._mayscript);
	}

	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		return super.domStyle_(no)
			+ 'visibility:visible;'; //bug 2815049
	}

	/** @internal */
	_outParamHtml(out: string[]): void {
		var params = this._params;
		for (var nm in params)
			out.push('<param name="', zUtl.encodeXML(nm), '" value="', zUtl.encodeXML(params[nm] as string), '"/>');
	}
}