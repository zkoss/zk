/* Script.ts

	Purpose:

	Description:

	History:
		Thu Dec 11 15:39:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag.
 * @import zul.wgt.Label
 */
@zk.WrapClass('zul.utl.Script')
export class Script extends zk.Widget {
	private _src?: string;
    private _content?: string;
	private _charset?: string;
	private _srcrun?: boolean;
	// eslint-disable-next-line @typescript-eslint/ban-types
	private _fn?: Function;
	private packages?: string;

    /** Returns the content of the script element.
     * By content we mean the JavaScript codes that will be enclosed
     * by the HTML SCRIPT element.
     *
     * <p>Default: null.
     * @return String
     */
    public getContent(): string | undefined {
        return this._content;
    }

    /** Sets the content of the script element.
     * By content we mean the JavaScript codes that will be enclosed
     * by the HTML SCRIPT element.
     * @param String content
     */
    public setContent(cnt: string, opts?: Record<string, boolean>): this {
        const o = this._content;
        this._content = cnt;

        if (o !== cnt || (opts && opts.force)) {
			if (cnt) {
				this._fn = typeof cnt == 'function' ? cnt : new Function(cnt);
				if (this.desktop) //check parent since no this.$n()
					this._exec();
			} else
				delete this._fn;
		}

        return this;
    }

    /** Returns the URI of the source that contains the script codes.
     * <p>Default: null.
     * @return String
     */
    public getSrc(): string | undefined {
        return this._src;
    }

    /** Sets the URI of the source that contains the script codes.
     *
     * <p>You either add the script codes directly with the {@link Label}
     * children, or
     * set the URI to load the script codes with {@link #setSrc}.
     * But, not both.
     *
     * @param String src the URI of the source that contains the script codes
     */
    public setSrc(src: string, opts?: Record<string, boolean>): this {
        const o = this._src;
        this._src = src;

        if (o !== src || (opts && opts.force)) {
			if (src) {
				this._srcrun = false;
				if (this.desktop)
					this._exec();
			}
		}

        return this;
    }

    /** Returns the character enconding of the source.
     * It is used with {@link #getSrc}.
     *
     * <p>Default: null.
     * @return String
     */
    public getCharset(): string | undefined {
        return this._charset;
    }

    /** Sets the character encoding of the source.
     * It is used with {@link #setSrc}.
     * @param String charset
     */
    public setCharset(charset: string): this {
        this._charset = charset;
        return this;
    }

    private _exec(): void {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		delete this.packages; //only once
		zk.load(pkgs);

		if (zk.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	}

    private _exec0(): void {
		var wgt = this, fn = this._fn;
		if (fn) {
			delete this._fn; //run only once
			zk.afterMount(function () {fn!.apply(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			var e = document.createElement('script');
			e.id = this.uuid;
			e.type = 'text/javascript';
			e.charset = this._charset ?? 'UTF-8';
			e.src = this._src;
			var n = this.$n(),
				nextSib: ChildNode | null = null;
			if (n) {
				nextSib = n.nextSibling;
				jq(n).remove();
			}

			if (nextSib) //use jq here would load this script twice in IE8/9
				nextSib.parentNode!.insertBefore(e, nextSib);
			else
				document.body.appendChild(e);
		}
		//update node
		this._node = jq(this.uuid, zk)[0];
		this._nodeSolved = true;
	}

	public override ignoreFlexSize_(attr: string): boolean {
		// ZK-2248: ignore widget dimension in vflex/hflex calculation
		return true;
	}

    //super//
	public override redraw(out: Array<string>, skipper?: zk.Skipper | null): void {
		// empty
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._visible = false; //Bug ZK-1516: no DOM element widget should always return false.
		this._exec();
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this._node!).remove(); // ZK-4043: the script DOM is appended in body, a manual remove is needed.
		super.unbind_(skipper, after, keepRod);
	}
}
