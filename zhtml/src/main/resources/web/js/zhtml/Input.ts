/* Input.ts

	Purpose:
		
	Description:
		
	History:
		Fri Aug 21 15:06:06 CST 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
@zk.WrapClass('zhtml.Input')
export class Input extends zhtml.Widget {
	value?: string;
	start?: number;

	/** @internal */
	_doChange(evt: zk.Event): void {
		var n = this.$n();
		if (n) {
			var val = n.value;
			if (val != this._defValue) {
				this._defValue = val;
				this.fire('onChange', this._onChangeData(val));
			}
		}
	}

	/** @internal */
	_onChangeData(val: string, selbak?: boolean): {value: string; start: number; marshal(): [string?, boolean?, number?]} {
		return {value: val,
			start: zk(this.$n()).getSelectionRange()[0],
			marshal: this._onChangeMarshal};
	}

	/** @internal */
	_onChangeMarshal(): [string?, boolean?, number?] {
		return [this.value, false, this.start];
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var n: HTMLInputElement | undefined;

		if (this.isListen('onChange', {any: true}) && (n = this.$n())) {
			this._defValue = n.value;
			this.domListen_(n, 'onChange');
		}
		if (this.isListen('onCheck', {any: true}) && (n = this.$n())) {
			this._defChecked = n.checked;
			this.domListen_(n, 'onCheck');
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._defValue !== undefined)
			this.domUnlisten_(this.$n_(), 'onChange');
		super.unbind_(skipper, after, keepRod);
	}
}