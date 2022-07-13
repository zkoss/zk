/* Include.ts

	Purpose:

	Description:

	History:
		Tue Oct 14 15:23:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An include widget
 */
@zk.WrapClass('zul.wgt.Include')
export class Include extends zul.Widget {
	//F70-ZK-2455: a way to change enclosing tag
	_enclosingTag = 'div';
	_comment?: boolean;
	_childjs?: (() => void) | null;
	_xcnt?: HTMLElement[] | string | null;

	constructor() {
		super(); // FIXME: params?
		this._fellows = {};
		// NOTE: Prior to TS migration, super is called after `_fellows` is initialized.
		// In this case, it's safe to initialize `_fellows` after calling super, as
		// the none in the chain of super constructors will access `_fellows`.
	}

	/** Returns whether to generate the included content inside
	 * the HTML comment.
	 * <p>Default: false.
	 *
	 * @return boolean
	 */
	isComment(): boolean | undefined {
		return this._comment;
	}

	/** Sets  whether to generate the included content inside
	 * the HTML comment.
	 * @param boolean comment
	 */
	setComment(comment: boolean): this {
		this._comment = comment;
		return this;
	}

	/**
	 * Returns the enclosing tag
	 * @return String
	 * @since 7.0.4
	 */
	getEnclosingTag(): string {
		return this._enclosingTag;
	}

	/**
	 * Sets the enclosing tag
	 * @param String enclosing tag
	 * @since 7.0.4
	 */
	setEnclosingTag(tag: string): void {
		this._enclosingTag = tag;
	}

	//super//
	override domStyle_(no?: zk.DomStyleOptions): string {
		var style = super.domStyle_(no);
		if (!this.previousSibling && !this.nextSibling) {
		//if it is only child, the default is 100%
			if ((!no || !no.width) && !this.getWidth())
				style += 'width:100%;';
			if ((!no || !no.height) && !this.getHeight())
				style += 'height:100%;';
		}
		return style;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		const ctn = this._childjs;
		if (ctn) {
			ctn();
			this._childjs = this._xcnt = null;
				//only once since the content has been created as child widgets
		}

		const xcnt = this._xcnt;
		if (jq.isArray(xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n_(), j = 0; j < xcnt.length; ++j)
				n.appendChild(xcnt[j]);
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		if (jq.isArray(this._xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n_(); n.firstChild;)
				n.removeChild(n.firstChild);
		super.unbind_(skipper, after, keepRod);
	}
}