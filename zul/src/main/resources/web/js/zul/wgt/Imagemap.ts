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
	static _doneURI?: string;
	static _stamp?: number;

	override getWidth(): string | undefined {
		return this._width;
	}

	override setWidth(width: string, opts?: Record<string, boolean>): this {
		const o = this._width;
		this._width = width;

		if (o !== width || opts?.force) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.width = width;
		}

		return this;
	}

	override getHeight(): string | undefined {
		return this._height;
	}

	override setHeight(height: string, opts?: Record<string, boolean>): this {
		const o = this._height;
		this._height = height;

		if (o !== height || opts?.force) { // B50-ZK-478
			var n = this.getImageNode();
			if (n)
				n.style.height = height;
		}

		return this;
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		if (!jq('#zk_hfr_')[0])
			jq.newFrame('zk_hfr_', undefined,
				zk.webkit ? 'position:absolute;top:-1000px;left:-1000px;width:0;height:0;display:inline'
					: undefined/*invisible*/);
			//creates a hidden frame. However, in safari, we cannot use invisible frame
			//otherwise, safari will open a new window
	}

	override getImageNode(): HTMLImageElement | undefined {
		return this.$n('real');
	}

	override getCaveNode(): HTMLElement | undefined {
		return this.$n('map');
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.desktop && this.firstChild == this.lastChild) //first child
			this._fixchd(true);
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop && !this.firstChild) //remove last
			this._fixchd(false);
	}

	_fixchd(bArea: boolean): void {
		var mapid = this.uuid + '-map',
			img = this.getImageNode()!;
		img.useMap = bArea ? '#' + mapid : '';
		img.isMap = !bArea;
	}

	override contentAttrs_(): string {
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
	override fromPageCoord(x: number, y: number): zk.Offset {
		//2997402: Imagemap rightclick/doubleclick wrong coordinates
		var ofs = zk(this.getImageNode()).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	}

	_doneURI(): string {
		var Imagemap = zul.wgt.Imagemap,
			url = Imagemap._doneURI;
		return url ? url :
			Imagemap._doneURI = zk.IMAGEMAP_DONE_URI ? zk.IMAGEMAP_DONE_URI :
				zk.ajaxURI('/web/zul/html/imagemap-done.html', {desktop: this.desktop, au: true});
	}

	/** Called by imagemap-done.html. */
	static onclick(href: string): void {
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

	static _toofast(): boolean {
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