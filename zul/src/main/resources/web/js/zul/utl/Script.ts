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
	/** @internal */
	_src?: string;
	/** @internal */
	_content?: string;
	/** @internal */
	_charset?: string;
	/** @internal */
	_srcrun?: boolean;
	/** @internal */
		// eslint-disable-next-line @typescript-eslint/ban-types
	_fn?: Function;
	packages?: string;

	/**
	 * @returns the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * @defaultValue `null`.
	 */
	getContent(): string | undefined {
		return this._content;
	}

	/**
	 * Sets the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || opts?.force) {
			if (content) {
				// eslint-disable-next-line no-new-func
				this._fn = typeof content == 'function' ? content : new Function(content);
				if (this.desktop) //check parent since no this.$n()
					this._exec();
			} else
				delete this._fn;
		}

		return this;
	}

	/**
	 * @returns the URI of the source that contains the script codes.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the URI of the source that contains the script codes.
	 *
	 * <p>You either add the script codes directly with the {@link Label}
	 * children, or
	 * set the URI to load the script codes with {@link setSrc}.
	 * But, not both.
	 *
	 * @param src - the URI of the source that contains the script codes
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			if (src) {
				this._srcrun = false;
				if (this.desktop)
					this._exec();
			}
		}

		return this;
	}

	/**
	 * @returns the character enconding of the source.
	 * It is used with {@link getSrc}.
	 *
	 * @defaultValue `null`.
	 */
	getCharset(): string | undefined {
		return this._charset;
	}

	/**
	 * Sets the character encoding of the source.
	 * It is used with {@link setSrc}.
	 */
	setCharset(charset: string): this {
		this._charset = charset;
		return this;
	}

	/** @internal */
	_exec(): void {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		delete this.packages; //only once
		zk.load(pkgs);

		if (zk.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	}

	/** @internal */
	_exec0(): void {
		var wgt = this, fn = this._fn;
		if (fn) {
			delete this._fn; //run only once
			zk.afterMount(function () {
				fn!.call(wgt);
			});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			var e = document.createElement('script');
			e.id = this.uuid;
			e.type = 'text/javascript';
			e.charset = this._charset ?? 'UTF-8';
			e.src = this._src;
			var n = this.$n(),
				// eslint-disable-next-line zk/noNull
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

	/** @internal */
	override ignoreFlexSize_(attr: string): boolean {
		// ZK-2248: ignore widget dimension in vflex/hflex calculation
		return true;
	}

	override redraw(out: string[], skipper?: zk.Skipper): void {
		// empty
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._visible = false; //Bug ZK-1516: no DOM element widget should always return false.
		this._exec();
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this._node).remove(); // ZK-4043: the script DOM is appended in body, a manual remove is needed.
		super.unbind_(skipper, after, keepRod);
	}
}