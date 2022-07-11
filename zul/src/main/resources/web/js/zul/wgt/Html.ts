/* Html.ts

	Purpose:

	Description:

	History:
		Sun Nov 23 20:35:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A component used to embed the browser native content (i.e., HTML tags)
 * into the output sent to the browser.
 * The browser native content is specified by {@link #setContent}.
 *
 * <p>Notice that {@link Html} generates HTML SPAN to enclose
 * the embedded HTML tags. Thus, you can specify the style
 * ({@link #getStyle}), tooltip {@link #getTooltip} and so on.
 */
@zk.WrapClass('zul.wgt.Html')
export class Html extends zul.Widget<HTMLHtmlElement> {
	private _content = '';

	/** Returns the embedded content (i.e., HTML tags).
	 * <p>Default: empty ("").
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 * @return String
	 */
	public getContent(): string {
		return this._content;
	}

	/** Sets the embedded content (i.e., HTML tags).
	 * @param String content
	 */
	public setContent(v: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.innerHTML = v || '';
		}

		return this;
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (jq.isArray(this._content)) //zk().detachChildren() is used
			for (var ctn = this._content, n = this.$n_(), j = 0; j < ctn.length; ++j)
				n.appendChild(ctn[j]);
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		if (jq.isArray(this._content)) //zk().detachChildren() is used
			for (var n = this.$n_(); n.firstChild;)
				n.removeChild(n.firstChild);
		super.unbind_(skipper, after, keepRod);
	}
}