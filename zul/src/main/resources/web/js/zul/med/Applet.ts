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
 * <p>Note: {@link #setVisible} with false cannot work in IE. (Browser's limitation)
 */
@zk.WrapClass('zul.med.Applet')
export class Applet extends zul.Widget<HTMLAppletElement> {
	private _code?: string;
	private _codebase?: string;
	private _archive?: string;
	private _mayscript?: boolean;
	private _params: Partial<HTMLAppletElement>;
	private _align?: string;
	private _hspace?: string;
	private _vspace?: string;

	public constructor() {
		super(); // FIXME: params?
		this._params = {};
		// NOTE: Prior to TS migration, super is called after _params is initialized
		// above, but it seems to not matter, as nowhere does super nor do zkcml
		// overrides use _params during initialization.
	}

	/** Return the applet class to run.
	 * @return String
	 */
	public getCode(): string | undefined {
		return this._code;
	}

	/** Sets the applet class to run.
	 * @param String code
	 */
	public setCode(code: string, opts?: Record<string, boolean>): this {
		const o = this._code;
		this._code = code;

		if (o !== code || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Return a relative base URL for applets specified in {@link #setCode} (URL).
	 * @return String
	 */
	public getCodebase(): string | undefined {
		return this._codebase;
	}

	/** Sets a relative base URL for applets specified in {@link #setCode} (URL).
	 * @param String codebase
	 */
	public setCodebase(codebase: string, opts?: Record<string, boolean>): this {
		const o = this._codebase;
		this._codebase = codebase;

		if (o !== codebase || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the location of an archive file (URL).
	 * @return String
	 * @since 5.0.3
	 */
	public getArchive(): string | undefined {
		return this._archive;
	}

	/** Sets the location of an archive file (URL).
	 * @param String archive
	 * @since 5.0.3
	 */
	public setArchive(archive: string, opts?: Record<string, boolean>): this {
		const o = this._archive;
		this._archive = archive;

		if (o !== archive || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether the applet is allowed to access the scripting object.
	 * @return boolean
	 * @since 5.0.3
	 */
	public isMayscript(): boolean | undefined {
		return this._mayscript;
	}

	/** Sets whether the applet is allowed to access the scripting object.
	 * @param boolean mayscript
	 * @since 5.0.3
	 */
	public setMayscript(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._mayscript;
		this._mayscript = v;

		if (o !== v || (opts && opts.force)) {
			const n = this.$n();
			if (n)
				n.mayscript = v; // Use of non-standard `mayscript` attribute
		}

		return this;
	}

	/** Returns the alignment of an applet according to surrounding elements.
	 * @return String
	 * @since 5.0.3
	 */
	public getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the alignment of an applet according to surrounding elements.
	 * @param String align
	 * @since 5.0.3
	 */
	public setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.align = v;
		}

		return this;
	}

	/** Returns the horizontal spacing around an applet.
	 * @return String
	 * @since 5.0.3
	 */
	public getHspace(): string | undefined {
		return this._hspace;
	}

	/** Sets the horizontal spacing around an applet.
	 * @param String hspace
	 * @since 5.0.3
	 */
	public setHspace(v: string, opts?: Record<string, boolean>): this {
		const o = this._hspace;
		this._hspace = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.hspace = v as unknown as number;
		}

		return this;
	}

	/** Returns the vertical spacing around an applet.
	 * @return String
	 * @since 5.0.3
	 */
	public getVspace(): string | undefined {
		return this._vspace;
	}

	/** Sets the vertical spacing around an applet.
	 * @param String vspace
	 * @since 5.0.3
	 */
	public setVspace(v: string, opts?: Record<string, boolean>): this {
		const o = this._vspace;
		this._vspace = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.vspace = v as unknown as number;
		}

		return this;
	}

	/** Invokes the function of the applet running at the client.
	 */
	public invoke(): void {
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
						var args = Array.apply(null, arguments as unknown as unknown[]);
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
					var args: unknown[] = [],
						arrayArg: unknown[] = [];
					if (arguments.length < 3) {
						if (arguments[1])
							args.push(arguments[1]);
					} else {
						for (var j = 1; j < len;)
							arrayArg.push(arguments[j++]);
						args.push(arrayArg);
					}
					func.apply(n, args);
				} catch (e) {
					zk.error('Failed to invoke applet\'s method: ' + fn + '\n' + (e as Error).message);
				}
			}
		};
	}

	/** Returns the value of the specified field.
	 * @param String name
	 * @return String
	 */
	public getField(name: string): string | null | undefined {
		var n = this.$n();
		return n ? n[name] as string | undefined : null;
	}

	/** Sets the value of the specified filed.
	 * @param String name
	 * @param String value
	 */
	public setField(name: string, value: string): void {
		var n = this.$n();
		if (n)
			try {
				n[name] = value;
			} catch (e) {
				zk.error('Failed to set applet\'s field: ' + name + '\n' + (e as Error).message);
			}
	}

	/** Sets the param. Notice that it is meaningful only if it is called
	 * before redraw. For example, <code>setParam('attr1', 'value1')</code>
	 * gives a <code>param</code> tag under <code>applet</code> tag with name
	 * <code>attr1</code>, value <code>value1</code>.
	 * There are two format:
	 * setParam(nm, val)
	 * and
	 * setParam([nm, val])
	 * @param String nm
	 * @param String val
	 */
	public setParam(nm: string, val: string): void {
		if (arguments.length == 1) {
			val = nm[1];
			nm = nm[0];
		}
		if (val != null) this._params[nm] = val;
		else delete this._params[nm];
	}

	/** Sets the params map. It should only be called before redraw.
	 * @param Map m A map of param pairs, as applet parameters. For example,
	 * <code>{attr1:'value1', attr2:'value2'}</code> gives two <code>param</code>
	 * tags under <code>applet</code> tag with names <code>attr1, attr2</code>,
	 * values <code>value1, value2</code> respectively.
	 * @since 5.0.4
	 */
	public setParams(m: Partial<HTMLAppletElement>): void {
		this._params = m;
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
				+ ' code="' + (this._code || '') + '"'
				+ zUtl.appendAttr('codebase', this._codebase)
				+ zUtl.appendAttr('archive', this._archive)
				+ zUtl.appendAttr('align', this._align)
				+ zUtl.appendAttr('hspace', this._hspace)
				+ zUtl.appendAttr('vspace', this._vspace)
				+ zUtl.appendAttr('mayscript', this._mayscript);
	}

	protected override domStyle_(no?: zk.DomStyleOptions): string {
		return super.domStyle_(no)
			+ 'visibility:visible;'; //bug 2815049
	}

	public _outParamHtml(out: string[]): void {
		var params = this._params;
		for (var nm in params)
			out.push('<param name="', zUtl.encodeXML(nm), '" value="', zUtl.encodeXML(params[nm] as string), '"/>');
	}
}