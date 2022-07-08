/* Imagemap.ts

	Purpose:

	Description:

	History:
		Thu Mar 26 15:54:00     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An image map.
 */
@zk.WrapClass('zul.wgt.Imagemap')
export class Imagemap extends zul.wgt.Image {
	public static _doneURI?: string;
	public static _stamp?: number;

	public override getWidth(): string | null | undefined {
		return this._width;
	}

	public override setWidth(v: string, opts?: Record<string, boolean>): this {
		const o = this._width;
		this._width = v;

		if (o !== v || (opts && opts.force)) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.width = v;
		}

		return this;
	}

	public override getHeight(): string | null | undefined {
		return this._height;
	}

	public override setHeight(v: string, opts?: Record<string, boolean>): this {
		const o = this._height;
		this._height = v;

		if (o !== v || (opts && opts.force)) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.height = v;
		}

		return this;
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		if (!jq('#zk_hfr_')[0])
			jq.newFrame('zk_hfr_', null,
				zk.webkit ? 'position:absolute;top:-1000px;left:-1000px;width:0;height:0;display:inline'
					: null/*invisible*/);
			//creates a hidden frame. However, in safari, we cannot use invisible frame
			//otherwise, safari will open a new window
	}

	public override getImageNode(): HTMLImageElement | null | undefined {
		return this.$n('real');
	}

	public override getCaveNode(): HTMLElement | null | undefined {
		return this.$n('map');
	}

	protected override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.desktop && this.firstChild == this.lastChild) //first child
			this._fixchd(true);
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop && !this.firstChild) //remove last
			this._fixchd(false);
	}

	private _fixchd(bArea: boolean): void {
		var mapid = this.uuid + '-map',
			img = this.getImageNode()!;
		img.useMap = bArea ? '#' + mapid : '';
		img.isMap = !bArea;
	}

	protected override contentAttrs_(): string {
		var attr = super.contentAttrs_(),
			w = this._width,
			h = this._height;
		if (w || h) { // B50-ZK-478
			attr += ' style="';
			if (w)
				attr += 'width:' + w + ';';
			if (h)
				attr += 'height:' + h + ';';
			attr += '"';
		}
		return attr + (this.firstChild ? ' usemap="#' + this.uuid + '-map"' :
			' ismap="ismap"');
	}

	//@Override
	public override fromPageCoord(x: number, y: number): zk.Offset {
		//2997402: Imagemap rightclick/doubleclick wrong coordinates
		var ofs = zk(this.getImageNode()).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	}

	public _doneURI(): string {
		var Imagemap = zul.wgt.Imagemap,
			url = Imagemap._doneURI;
		return url ? url :
			Imagemap._doneURI = zk.IMAGEMAP_DONE_URI ? zk.IMAGEMAP_DONE_URI :
				zk.ajaxURI('/web/zul/html/imagemap-done.html', {desktop: this.desktop, au: true});
	}

	/** Called by imagemap-done.html. */
	public static onclick(href: string): void {
		if (zul.wgt.Imagemap._toofast()) return;

		var j = href.indexOf('?');
		if (j < 0) return;

		var k = href.indexOf('?', ++j);
		if (k < 0) return;

		var id = href.substring(j, k),
			wgt = zk.Widget.$(id);
		if (!wgt) return; //component might be removed

		j = href.indexOf(',', ++k);
		if (j < 0) return;

		wgt.fire('onClick', {
			x: zk.parseInt(href.substring(k, j)),
			y: zk.parseInt(href.substring(j + 1))
		}, {ctl: true});
	}

	public static _toofast(): boolean {
		if (zk.gecko) { //bug 1510374
			var Imagemap = zul.wgt.Imagemap,
				now = jq.now();
			if (Imagemap._stamp && now - Imagemap._stamp < 800)
				return true;
			Imagemap._stamp = now;
		}
		return false;
	}
}