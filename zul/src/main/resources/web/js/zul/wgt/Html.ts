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
 * The browser native content is specified by {@link setContent}.
 *
 * <p>Notice that {@link Html} generates HTML SPAN to enclose
 * the embedded HTML tags. Thus, you can specify the style
 * ({@link getStyle}), tooltip {@link getTooltip} and so on.
 */
@zk.WrapClass('zul.wgt.Html')
export class Html extends zul.Widget<HTMLHtmlElement> {
	/** @internal */
	_content = '';

	/**
	 * @returns the embedded content (i.e., HTML tags).
	 * @defaultValue empty ("").
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	getContent(): string {
		return this._content;
	}

	/**
	 * Sets the embedded content (i.e., HTML tags).
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || opts?.force) {
			var n = this.$n();
			// Allow HTML and Script tag here according to Component reference guide.
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = /*safe*/ content || '';
		}

		return this;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (Array.isArray(this._content)) //zk().detachChildren() is used
			for (var ctn = this._content, n = this.$n_(), j = 0; j < ctn.length; ++j)
				n.appendChild(ctn[j]);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (Array.isArray(this._content)) //zk().detachChildren() is used
			for (var n = this.$n_(); n.firstChild;)
				n.removeChild(n.firstChild);
		super.unbind_(skipper, after, keepRod);
	}
}