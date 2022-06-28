/* Anchorchildren.ts

	Purpose:

	Description:

	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The children of Anchorlayout. <br>
 * Can accept any ZK component as child.
 *
 * <p>Default {@link #getZclass}: z-anchorchildren.
 * @author peterkuo
 * @since 6.0.0
 */
export class Anchorchildren extends zul.Widget {
    private _anchor?: string;

    /**
     * Sets the width, height relative to parent, anchorlayout.
     * It can use % or number.
     * Accept one argument, or two argument separated by space.
     * The first argument is for width, and second for height.
     * For example, "50% 50%" means the anchorchildren width and height is 50%
     * of {@link Anchorlayout}.
     * "-30 20%" means the width is 20px less than parent, and height is 20% of parent.
     * "50%" means the width is 50% of parent, and the height is no assumed.
     * @param String anchor
     */
    public setAnchor(anchor: string, opts?: Record<string, boolean>): this {
        const o = this._anchor;
        this._anchor = anchor;

        if (o !== anchor || (opts && opts.force)) {
			if (this.desktop)
				this.onSize();
		}

        return this;
    }

    /**
     * Returns the anchor setting.
     * @return String
     */
    public getAnchor(): string | undefined {
        return this._anchor;
    }

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this});
		super.unbind_(skipper, after, keepRod);
	}

    public override onSize(): void {
		//calculate the height and width in pixel based on _anchor
		var n = this.$n_(),
			parentn = this.parent!.$n_(),
			parentwidth = jq(parentn).width()!,
			parentheight = jq(parentn).height()!,
			arr = this._anchor ? this._anchor.split(' ', 2) : [],
			anchorWidth = arr[0],
			anchorHeight = arr[1];

		if (anchorWidth) {
			if (anchorWidth.indexOf('%') > 0) {
				n.style.width = anchorWidth;
			} else {
				n.style.width = jq.px0(parentwidth + zk.parseInt(anchorWidth));
			}
		}

		if (anchorHeight) {
			if (anchorHeight.indexOf('%') > 0) {
				n.style.height = anchorHeight;
			} else {
				n.style.height = jq.px0(parentheight + zk.parseInt(anchorHeight));
			}
		}
	}
}
zul.layout.Anchorchildren = zk.regClass(Anchorchildren);
