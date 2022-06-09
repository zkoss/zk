/* Inputgroup.ts

		Purpose:

		Description:

		History:
				Thu Mar 07 16:51:36 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

*/
/**
 * An inputgroup.
 *
 * Inspired by Bootstrap’s Input group and Button group.
 * By prepending or appending some components to the input component,
 * you can merge them like a new form-input component.
 *
 * <h3>Accepted child components</h3>
 * <ul>
 *     <li>Label</li>
 *     <li>InputElement</li>
 *     <li>LabelImageElement</li>
 * </ul>
 *
 * <p>Default {@link #getZclass}: z-inputgroup.
 *
 * @since 9.0.0
 * @author charlesqiu, rudyhuang
 */
export class Inputgroup extends zul.Widget {
	private _vertical = false;

	/**
	 * Returns whether it is a vertical orientation.
	 * <p>Default: false
	 *
	 * @return boolean whether it is a vertical orientation
	 */
	public isVertical(): boolean {
		return this._vertical;
	}

	/**
	 * Sets whether it is a vertical orientation.
	 * @param boolean vertical whether it is a vertical orientation
	 */
	public setVertical(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._vertical;
		this._vertical = v;

		if (o !== v || (opts && opts.force)) {
			if (this.desktop) {
				jq(this.$n()!).toggleClass(this.$s('vertical'), v);
			}
		}

		return this;
	}

	protected override domClass_(no?: Partial<zk.DomClassOptions>): string {
		let classes = this.domClass_(no);
		return classes + (this._vertical ? ' ' + this.$s('vertical') : '');
	}

	protected override insertChildHTML_(child: zk.Widget, before?: zk.Widget | null, desktop?: zk.Desktop | null): void {
		if (before)
			jq(before.$n('chdex') || before.$n()!).before(
				this.encloseChildHTML_({child: child})!);
		else
			jq(this.getCaveNode()).append(
				this.encloseChildHTML_({child: child})!);

		child.bind(desktop);
	}

	public override removeChildHTML_(child: zk.Widget, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		jq(child.uuid + '-chdex', zk).remove();
	}

	protected encloseChildHTML_(opts: {child: zk.Widget; out?: string[]}): string | undefined {
		let out = opts.out || new zk.Buffer(),
			w = opts.child;
		if (!w.$instanceof(zul.wgt.Button) && !w.$instanceof(zul.wgt.Toolbarbutton)
				&& (!zul.inp || !w.$instanceof(zul.inp.InputWidget))) {
			out.push('<div id="', w.uuid, '-chdex" class="', this.$s('text'), '">');
			w.redraw(out);
			out.push('</div>');
		} else {
			w.redraw(out);
		}
		if (!opts.out) return out.join('');
	}
}
zul.wgt.Inputgroup = zk.regClass(Inputgroup);