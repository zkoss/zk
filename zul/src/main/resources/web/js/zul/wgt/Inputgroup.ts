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
@zk.WrapClass('zul.wgt.Inputgroup')
export class Inputgroup extends zul.Widget {
	_vertical = false;

	/**
	 * Returns whether it is a vertical orientation.
	 * <p>Default: false
	 *
	 * @return boolean whether it is a vertical orientation
	 */
	isVertical(): boolean {
		return this._vertical;
	}

	/**
	 * Sets whether it is a vertical orientation.
	 * @param boolean vertical whether it is a vertical orientation
	 */
	setVertical(vertical: boolean, opts?: Record<string, boolean>): this {
		const o = this._vertical;
		this._vertical = vertical;

		if (o !== vertical || opts?.force) {
			if (this.desktop) {
				jq(this.$n()).toggleClass(this.$s('vertical'), vertical);
			}
		}

		return this;
	}

	// treat this as setVertical(boolean) for zephyr
	setOrient(orient: string): this {
		return this.setVertical(orient == 'vertical');
	}

	override domClass_(no?: zk.DomClassOptions): string {
		const classes = super.domClass_(no);
		return classes + (this._vertical ? ' ' + this.$s('vertical') : '');
	}

	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		if (before)
			jq(before.$n('chdex') || before.$n()!).before(
				this.encloseChildHTML_({ child: child })!);
		else
			jq(this.getCaveNode()).append(
				this.encloseChildHTML_({ child: child })!);

		child.bind(desktop);
	}

	override removeChildHTML_(child: zk.Widget, ignoreDom?: boolean): void {
		super.removeChildHTML_(child, ignoreDom);
		jq(child.uuid + '-chdex', zk).remove();
	}

	encloseChildHTML_(opts: { child: zk.Widget; out?: string[] }): string | undefined {
		const out = opts.out || new zk.Buffer(),
			w = opts.child;
		if (!(w instanceof zul.wgt.Button) && !(w instanceof zul.wgt.Toolbarbutton)
			&& (!zul.inp || !(w instanceof zul.inp.InputWidget))) {
			out.push('<div id="', w.uuid, '-chdex" class="', this.$s('text'), '">');
			w.redraw(out);
			out.push('</div>');
		} else {
			w.redraw(out);
		}
		if (!opts.out) return out.join('');
	}
}