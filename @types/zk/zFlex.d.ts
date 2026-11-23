/* zFlex.d.ts

	Purpose:
		
	Description:
		
	History:
		Tue Mar 02 12:24:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
declare namespace zk {
	type FlexOrient = 'w' | 'h';

	interface FlexInfo {
		isFlexRow: boolean;
		flexContainerChildren: HTMLElement[];
		childrenWidgets: zk.Widget[];
	}

	interface ZFlex {
		applyCSSFlex(): void;
		beforeSize(ctl, opts, cleanup: boolean): void;
		beforeSizeClearCachedSize(ctl, opts, cleanup: boolean): void;
		beforeSizeForRead(): void;
		clearCSSFlex(wgt: zk.Widget, o: FlexOrient, clearAllSiblings?: boolean): void;
		fixFlex(wgt: zk.Widget): void;
		fixMinFlex(wgt: zk.Widget, wgtn: HTMLElement, o: string): number;
		getFlexInfo(wgt: zk.Widget): FlexInfo;
		onFitSize(): void;
		onSize(): void;
	}
}

declare var zFlex: zk.ZFlex;