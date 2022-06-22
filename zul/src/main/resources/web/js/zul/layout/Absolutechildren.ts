/* Absolutechildren.ts

	Purpose:

	Description:

	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * <p>A container component that can contain any other ZK component and can only
 * be contained as direct child of Absolutelayout component. It can be absolutely
 * positioned within Absolutelayout component by either setting "x" and "y"
 * attribute or calling {@link #setX(int)} and {@link #setY(int)} methods.
 *
 * <p>Default {@link #getZclass}: z-absolutechildren.
 *
 * @author ashish
 * @since 6.0.0
 */
export class Absolutechildren extends zul.Widget {
    private _x = 0;
    private _y = 0;

    /**
     * Sets current "x" position within parent container component.
     * <p>Default: 0
     * @param int x the x position
     */
    public setX(x: number, opts?: Record<string, boolean>): this {
        const o = this._x;
        this._x = x;

        if (o !== x || (opts && opts.force)) {
			if (this.desktop) {
				this._rePositionX();
			}
		}

        return this;
    }

    /**
     * Returns the current "x" position within parent container component
     * @return int
     */
    public getX(): number | null {
        return this._x;
    }

    /**
     * Sets current "y" position within parent container component.
     * <p>Default: 0
     * @param int y the y position
     */
    public setY(y: number, opts?: Record<string, boolean>): this {
        const o = this._y;
        this._y = y;

        if (o !== y || (opts && opts.force)) {
			if (this.desktop) {
				this._rePositionY();
			}
		}

        return this;
    }

    /**
     * Returns the current "y" position within parent container component
     * @return int
     */
    public getY(): number | null {
        return this._y;
    }

    private _rePositionBoth(): void {
		this._rePositionX();
		this._rePositionY();
	}

    private _rePositionX(): void {
		jq(this.$n()!).css('left', this._x);
	}

    private _rePositionY(): void {
		jq(this.$n()!).css('top', this._y);
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._rePositionBoth();
	}
}
zul.layout.Absolutechildren = zk.regClass(Absolutechildren);
