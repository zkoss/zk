/* Panelchildren.ts

	Purpose:

	Description:

	History:
		Mon Jan 12 18:31:03     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Panelchildren is used for {@link zul.wnd.Panel} component to manage each
 * child who will be shown in the body of Panel.
 * Note that the size of Panelchildren is automatically calculated by Panel so both
 * {@link #setWidth(String)} and {@link #setHeight(String)} are read-only.
 *
 * <p>Default {@link #getZclass}: z-panelchildren.
 */
@zk.WrapClass('zul.wnd.Panelchildren')
export class Panelchildren extends zul.ContainerWidget {
	public override parent!: zul.wnd.Panel;

	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel#setHeight(String)} instead.
	 * @param String height
	 */
	public override setHeight(height: string | null): void {
		// Empty on purpose. Shoudn't do anything.
	}

	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel#setWidth(String)} instead.
	 * @param String width
	 */
	public override setWidth(width: string | null): void { // readonly
		// Empty on purpose. Shoudn't do anything.
	}

	// super
	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			var zcls = this.getZclass(),
				added = !this.parent.getTitle() && !this.parent.caption ?
				zcls + '-noheader' : '';
			if (added) scls += (scls ? ' ' : '') + added;
			added = this.parent._bordered() ? '' : zcls + '-noborder';
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	}

	protected override updateDomStyle_(): void {
		super.updateDomStyle_();
		if (this.desktop)
			zUtl.fireSized(this.parent);
	}
}