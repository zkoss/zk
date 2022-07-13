/* Content.ts

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:33:37 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
@zk.WrapClass('zhtml.Content')
export class Content extends zhtml.Widget {
	_content?: string;
	prolog?: string;
	epilog?: string;

	getContent(): string | undefined {
		return this._content;
	}

	setContent(v: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = v;

		if (o !== v || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	override redraw(out: string[]): void {
		var s = this.prolog;
		if (s) out.push(s);

		// B65-ZK-1836
		out.push((this._content || '').replace(/<\/(?=script>)/ig, '<\\/'));

		s = this.epilog;
		if (s) out.push(s);
	}
}