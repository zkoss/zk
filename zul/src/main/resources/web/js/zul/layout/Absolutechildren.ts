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
 * attribute or calling {@link setX} and {@link setY} methods.
 *
 * @defaultValue {@link getZclass}: z-absolutechildren.
 *
 * @author ashish
 * @since 6.0.0
 */
@zk.WrapClass('zul.layout.Absolutechildren')
export class Absolutechildren extends zul.Widget {
    /** @internal */
    _x = 0;
    /** @internal */
    _y = 0;

    /**
     * Sets current "x" position within parent container component.
     * @defaultValue `0`
     * @param x - the x position
     */
    setX(x: number, opts?: Record<string, boolean>): this {
        const o = this._x;
        this._x = x;

        if (o !== x || opts?.force) {
			if (this.desktop) {
				this._rePositionX();
			}
		}

        return this;
    }

    /**
     * @returns the current "x" position within parent container component
     */
    getX(): number | undefined {
        return this._x;
    }

    /**
     * Sets current "y" position within parent container component.
     * @defaultValue `0`
     * @param y - the y position
     */
    setY(y: number, opts?: Record<string, boolean>): this {
        const o = this._y;
        this._y = y;

        if (o !== y || opts?.force) {
			if (this.desktop) {
				this._rePositionY();
			}
		}

        return this;
    }

    /**
     * @returns the current "y" position within parent container component
     */
    getY(): number | undefined {
        return this._y;
    }

    /** @internal */
    _rePositionBoth(): void {
		this._rePositionX();
		this._rePositionY();
	}

    /** @internal */
    _rePositionX(): void {
		jq(this.$n()).css('left', this._x);
	}

    /** @internal */
    _rePositionY(): void {
		jq(this.$n()).css('top', this._y);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._rePositionBoth();
	}
}