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
 * {@link setWidth} and {@link setHeight} are read-only.
 *
 * @defaultValue {@link getZclass}: z-panelchildren.
 */
@zk.WrapClass('zul.wnd.Panelchildren')
export class Panelchildren extends zul.ContainerWidget {
	override parent?: zul.wnd.Panel;

	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel.setHeight} instead.
	 */
	override setHeight(height?: string): this {
		// Empty on purpose. Shoudn't do anything.
		return this;
	}

	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel.setWidth} instead.
	 */
	override setWidth(width?: string): this { // readonly
		// Empty on purpose. Shoudn't do anything.
		return this;
	}

	// super
	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		const out: string[] = [],
			scls = super.domClass_(no);
		if (scls)
			out.push(scls);
		if (!no?.zclass) {
			const zcls = this.getZclass(),
				parent = this.parent!;
			if (!parent.getTitle() && !parent.caption)
				out.push(zcls + '-noheader');
			if (!parent._bordered())
				out.push(zcls + '-noborder');
		}
		return out.join(' ');
	}

	/** @internal */
	override updateDomStyle_(): void {
		super.updateDomStyle_();
		if (this.desktop)
			zUtl.fireSized(this.parent!);
	}
}