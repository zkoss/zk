/* Widget.ts

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 11:03:40     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
@zk.WrapClass('zhtml.Widget')
export class Widget extends zk.Widget<HTMLInputElement> {
	override rawId = true;
	/** The class name (`zhtml.Widget`) */
	override className = 'zhtml.Widget';
	/** The widget name (`zhtml`) */
	override widgetName = 'zhtml';
	/** @internal */
	_defChecked?: boolean;
	/** @internal */
	_defValue?: string;

	setDynamicProperty(dynamicProperty: [string, string]): this {
		var n = this.$n();
		if (n) {
			var nm = dynamicProperty[0], val = dynamicProperty[1];
			switch (nm) {
			case 'checked':
				n.checked = this._defChecked = val != null;
				break;
			case 'value':
				n.value = this._defValue = val;
				break;
			case 'style':
				zk(n).clearStyles().jq.css(jq.parseStyle(val));
				break;
			case 'class':
				n.className = val;
				break;
			case 'disabled':
			case 'readOnly':
				n[nm] = 'true' == val;
				break;
			default:
				n[nm] = val;
			}
		}
		return this;
	}

	/** @internal */
	override doClick_(wevt: zk.Event): void {
		var n = this.$n();
		if (n) {
			if (n.tagName != 'INPUT')
				super.doClick_(wevt);
			else if (!n.disabled) {
				if (n.type == 'checkbox' || n.type == 'radio')
					this._doCheck();
					//continue to fire onClick_ for backward compatibility
				this.fireX(wevt); //no propagation
			}
		}
	}

	/** @internal */
	_doCheck(): void {
		var n = this.$n();
		if (n) {
			var val = n.checked;
			if (val != this._defChecked) { //changed
				this._defChecked = val;
				this.fire('onCheck', val);
			}
		}
	}

	override redraw(out: string[]): void {
		return zk.Native.$redraw.call(this, out);
	}
}