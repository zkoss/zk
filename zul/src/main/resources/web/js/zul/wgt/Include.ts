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
	/** @internal */
	_enclosingTag = 'div';
	/** @internal */
	_comment?: boolean;
	/** @internal */
	_childjs?: (() => void);
	/** @internal */
	_xcnt?: HTMLElement[] | string;

	constructor() {
		super(); // FIXME: params?
		this._fellows = {};
		// NOTE: Prior to TS migration, super is called after `_fellows` is initialized.
		// In this case, it's safe to initialize `_fellows` after calling super, as
		// the none in the chain of super constructors will access `_fellows`.
	}

	/**
	 * @returns whether to generate the included content inside the HTML comment.
	 * @defaultValue `false`.
	 */
	isComment(): boolean {
		return !!this._comment;
	}

	/**
	 * Sets whether to generate the included content inside the HTML comment.
	 */
	setComment(comment: boolean): this {
		this._comment = comment;
		return this;
	}

	/**
	 * @returns the enclosing tag
	 * @since 7.0.4
	 */
	getEnclosingTag(): string {
		return this._enclosingTag;
	}

	/**
	 * Sets the enclosing tag
	 * @since 7.0.4
	 */
	setEnclosingTag(enclosingTag: string): this {
		this._enclosingTag = enclosingTag;
		return this;
	}

	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		var styleHTML = super.domStyle_(no);
		if (!this.previousSibling && !this.nextSibling) {
		//if it is only child, the default is 100%
			if ((!no || !no.width) && !this.getWidth())
				styleHTML += 'width:100%;';
			if ((!no || !no.height) && !this.getHeight())
				styleHTML += 'height:100%;';
		}
		return styleHTML;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		const ctn = this._childjs;
		if (ctn) {
			ctn();
			this._childjs = this._xcnt = undefined;
				//only once since the content has been created as child widgets
		}

		const xcnt = this._xcnt;
		if (Array.isArray(xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n_(), j = 0; j < xcnt.length; ++j)
				n.appendChild(xcnt[j]);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (Array.isArray(this._xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n_(); n.firstChild;)
				n.removeChild(n.firstChild);
		super.unbind_(skipper, after, keepRod);
	}
}