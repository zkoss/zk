/* Drawer.ts

	Purpose:

	Description:

	History:
		Fri Aug 30 11:19:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(() => {
/**
 * A drawer.
 * <p>Only support browsers that support Flex and CSS Transitions. (IE10+, Edge, Chrome, Firefox, Safari)
 *
 * @author rudyhuang
 * @since 9.0.0
 */
zul.wgt.Drawer = zk.$extends(zul.Widget, {
    _title: '',
    _position: 'right',
    _mask: true,
    _visible: false,
    _closable: false,
    _autodrop: false,
    _threshold: 30,

    $define: {
        /**
         * Returns the title of this drawer. {@code null} means no title.
         * <p>Default: null.
         * @return String
         */
        /**
         * Sets the title of this drawer. {@code null} means no title.
         * @param String title the title.
         */
        title(v: string) {
            if (this.desktop) {
                jq(this.$n('title')).html(zUtl.encodeXML(v));
                jq(this.$n('header')).toggle(!!v);
            }
        },
        /**
         * Returns the position of this drawer. Valid values are "left", "right", "top" and "bottom".
         * <p>Default: right.
         * @return String
         */
        /**
         * Sets the position of this drawer. Valid values are "left", "right", "top" and "bottom".
         * @param String position the position of this drawer.
         */
        position: null,
        /**
         * Returns whether it is masked when opened.
         * <p>Default: true.
         * @return boolean
         */
        /**
         * Sets whether it is masked when opened.
         * @param boolean mask mask enabled?
         */
        mask(v: boolean) {
            if (this.desktop)
                jq(this.$n('mask')).toggleClass(this.$s('mask-enabled'), v);
        },
        /**
         * Returns whether it is closeable by user (a button).
         * <p>Default: false.
         * @return boolean
         */
        /**
         * Sets whether it is closeable by user (a button).
         * @param boolean closable closable enabled?
         */
        closable(v: boolean) {
            if (this.desktop)
                jq(this.$n('close')).toggle(v);
        },
    },

    bind_() {
        this.$supers(zul.wgt.Drawer, 'bind_', arguments);
        this.domListen_(this.$n('close'), 'onClick', 'close')
            .domListen_(this.$n('mask'), 'onClick', 'close');
        this._overrideAnimationSpeed();
        zk(this.$n()).makeVParent();
        this.setFloating_(true);
    },
    unbind_() {
        this._unregisterAutodropHandler();
        this.setFloating_(false);
        zk(this.$n()).undoVParent();
        this.domUnlisten_(this.$n('mask'), 'onClick', 'close')
            .domUnlisten_(this.$n('close'), 'onClick', 'close');
        this.$supers(zul.wgt.Drawer, 'unbind_', arguments);
    },

    _overrideAnimationSpeed() {
        const DEFAULT_ANIMATION_SPEED_MILLIS = this.$class._defaultAnimationSpeedMillis;
        let real = this.$n('real'),
            animationSpeed = zk(real).getAnimationSpeed(DEFAULT_ANIMATION_SPEED_MILLIS);
        if (animationSpeed != DEFAULT_ANIMATION_SPEED_MILLIS) {
            let finalSpeed: number;
            if (animationSpeed == 'slow' || animationSpeed == 'fast')
                finalSpeed = jq.fx.speeds[animationSpeed];
            else if (typeof animationSpeed == 'number')
                finalSpeed = animationSpeed;
            else
                finalSpeed = parseInt(animationSpeed);
            real.style.transitionDuration = `${finalSpeed}ms`;
        }
    },
    _unregisterAutodropHandler() {
        if (this._autodropHandler != null) {
            document.removeEventListener('mousemove', this._autodropHandler);
            this._autodropHandler = null;
        }
    },
    _dimension(): string {
        if (this.$class._isLeftRight(this._position))
            return this._width ? ` style="width: ${this._width}"` : '';
        else
            return this._height ? ` style="height: ${this._height}"` : '';
    },

    domClass_() {
        let classes: string = this.$supers('domClass_', arguments);
        classes += ' ' + this.$s(this._position);
        if (this._visible) classes += ' ' + this.$s('open');
        return classes;
    },

    setVisible(visible: boolean) {
        if (this.isVisible() != visible) {
            let args = arguments,
                real = this.$n('real'),
                animationSpeed = zk(real).getAnimationSpeed(this.$class._defaultAnimationSpeedMillis);
            if (!visible && animationSpeed != 0) { // No transitionend event if animationSpeed = 0
                jq(real).one('transitionend', () => this.$supers('setVisible', args));
            } else {
                this.$supers('setVisible', args);
            }

            let n = this.$n();
            if (n) {
                jq(n).toggleClass(this.$s('open'), visible);
                this.fire('onOpen', {open: visible});
            }
        }
    },

    setHeight(height: string) {
        if (this._height != height) {
            this._height = height;
            if (!this.$class._isLeftRight(this._position)) {
                let real = this.$n('real');
                if (real) {
                    real.style.height = height;
                }
            }
        }
    },
    setWidth(width: string) {
        if (this._width != width) {
            this._width = width;
            if (this.$class._isLeftRight(this._position)) {
                let real = this.$n('real');
                if (real) {
                    real.style.width = width;
                }
            }
        }
    },
    setPosition(position: string) {
        if (this._position != position) {
            if (this.desktop) {
                jq(this.$n())
                    .removeClass(this.$s(this._position))
                    .addClass(this.$s(position));
                let isLeftRightOld = this.$class._isLeftRight(this._position),
                    isLeftRightNew = this.$class._isLeftRight(position);
                if (isLeftRightOld != isLeftRightNew) {
                    let real = this.$n('real');
                    real.style[isLeftRightOld ? 'width' : 'height'] = '';
                    real.style[isLeftRightNew ? 'width' : 'height'] = isLeftRightNew ? this._width : this._height;
                }
            }
            this._position = position;
        }
    },
    /**
     * Sets whether it is opened automatically when the mouse cursor is near the page edge.
     * @param boolean autodrop autodrop enabled?
     */
    setAutodrop(autodrop: boolean) {
        if (!zk.mobile) {
            let _autodrop = this._autodropHandler != null;
            if (autodrop != _autodrop) {
                if (autodrop) {
                    this._autodropHandler = this.$class._throttle(this._doAutodrop.bind(this), 200);
                    document.addEventListener('mousemove', this._autodropHandler);
                } else {
                    this._unregisterAutodropHandler();
                }
            }
        }
    },
    /**
     * Returns whether it is opened automatically when the mouse cursor is near the page edge.
     * <p>Default: false.
     * @return boolean
     */
    isAutodrop(): boolean {
        return this._autodropHandler != null;
    },
    _doAutodrop(evt: MouseEvent) {
        if (!this._visible) {
            let isLeftRight = this.$class._isLeftRight(this._position),
                mousePos = isLeftRight ? evt.clientX : evt.clientY,
                doc = document.documentElement,
                clientPos = isLeftRight ? doc.clientWidth : doc.clientHeight,
                shouldDrop = (this._position == 'left' || this._position == 'top')
                    ? mousePos <= this._threshold
                    : mousePos >= clientPos - this._threshold;
            if (shouldDrop) this.open();
        }
    },
    /**
     * Opens the drawer.
     */
    open() {
        this.setVisible(true);
    },
    /**
     * Closes the drawer.
     */
    close() {
        this.setVisible(false);
    },
}, {
    _defaultAnimationSpeedMillis: 200, // should be the same in CSS
    _isLeftRight(position: string): boolean {
        return position == 'left' || position == 'right';
    },

    // copied from underscore.js, MIT license
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _throttle<T, A extends any[], R>(func: (this: T, ...args: A) => R, wait: number): (this: T, ...args: A) => R {
        var timeout: number | null, context, args, result: R;
        var previous = 0;
        var later = function (): void {
            previous = Date.now();
            timeout = null;
            result = func.apply(context, args);
            if (!timeout) context = args = null;
        };

        return function () {
            var now = Date.now();
            var remaining = wait - (now - previous);
            context = this;
            args = arguments;
            if (remaining <= 0 || remaining > wait) {
                if (timeout) {
                    clearTimeout(timeout);
                    timeout = null;
                }
                previous = now;
                result = func.apply(context, args);
                if (!timeout) context = args = null;
            } else if (!timeout) {
                timeout = setTimeout(later, remaining);
            }
            return result;
        };
    },
});
})();
