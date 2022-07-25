/* Submit.ts

	Purpose:

	Description:

	History:
		Wed Aug 24 16:18:18 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
/**
 * A Submit Button.
 *
 */
@zk.WrapClass('zul.fud.Submit')
export class Submit extends zul.wgt.Button {
	override nextSibling!: zk.Widget & { setDisabled(disabled: boolean) };
	_tmp?: number;

	override getZclass(): string { // keep the button's zclass
		return this._zclass == null ? 'z-button' : this._zclass;
	}

	submit(): void {
		const f = this.$f('fileupload')!,
			t = (): boolean => {
				if (zul.Upload.isFinish(f)) {
					this.$o<zul.fud.FileuploadDlg>()!.submit();
					clearInterval(this._tmp);
					this._tmp = undefined;
					return true;
				}
				return false;
			};
		if (t()) return;
		this._tmp = setInterval(t, 800);
		this.setDisabled(true);
		this.nextSibling.setDisabled(true);
		if (zk.ie < 11)
			this.$f('btns')!.rerender();
	}

	revert(): void {
		clearInterval(this._tmp);
		this._tmp = undefined;
		this.setDisabled(false);
		this.nextSibling.setDisabled(false);
		if (zk.ie < 11)
			this.$f('btns')!.rerender();
	}
}