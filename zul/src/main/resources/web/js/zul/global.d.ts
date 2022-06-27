export * as box from './box';
export * as db from './db';
// export * as fud from './fud';
// export * as grid from './grid';
export declare namespace grid {
    export import Row = zul.Widget; // zk/domtouch
}
export * as inp from './inp';
// export * as lang from './lang';
// export * as layout from './layout';
export * as med from './med';
export * as menu from './menu';
export * as mesh from './mesh';
// export * as sel from './sel';
export declare namespace sel {
    export import ItemWidget = zul.Widget; // zk/dom
    export import Listitem = zul.sel.ItemWidget; // zk/domtouch
    export import Treerow = zul.Widget; // zk/domtouch
}
// export * as tab from './tab';
// export * as utl from './utl';
export * as wgt from './wgt';
// export * as wnd from './wnd';
export declare namespace wnd { // zk/flex
    export import Panelchildren = zul.ContainerWidget;
}
export * from '.';
export as namespace zul;

declare global {
	interface HTMLElement {
		_lastsz?: null | {
			height: number;
			width: number;
		};
	}
    const msgzul: Record<
	| 'DATE_REQUIRED'
	| 'EMPTY_NOT_ALLOWED'
	| 'FIRST' // zul/mesh/Paging
	| 'GRID_ASC' // zul/mesh/ColumnMenuWidget
	| 'GRID_DESC' // zul/mesh/ColumnMenuWidget
	| 'GRID_GROUP' // zul/mesh/ColumnMenuWidget
	| 'GRID_UNGROUP' // zul/mesh/ColumnMenuWidget
	| 'ILLEGAL_VALUE'
	| 'INTEGER_REQUIRED' // zul/inp/Intbox
	| 'LAST' // zul/inp/Intbox
	| 'NEXT' // zul/inp/Intbox
	| 'NO_AUDIO_SUPPORT'
	| 'NO_FUTURE_PAST_TODAY'
	| 'NO_FUTURE_PAST'
	| 'NO_FUTURE_TODAY'
	| 'NO_FUTURE'
	| 'NO_NEGATIVE_ZERO'
	| 'NO_NEGATIVE'
	| 'NO_PAST_TODAY'
	| 'NO_PAST'
	| 'NO_POSITIVE_NEGATIVE_ZERO'
	| 'NO_POSITIVE_NEGATIVE'
	| 'NO_POSITIVE_ZERO'
	| 'NO_POSITIVE'
	| 'NO_TODAY'
	| 'NO_ZERO'
	| 'NUMBER_REQUIRED' // zul/inp/Decimalbox
	| 'OK' // zul/dom
	| 'OUT_OF_RANGE_SEPARATOR' // zul/inp/SimpleLocalTimeConstraint
	| 'OUT_OF_RANGE'
	| 'PANEL_EXPAND' // zul/inp/ComboWidget
	| 'PREV' // zul/inp/Intbox
	| 'UNKNOWN_TYPE'
	| 'UPLOAD_ERROR_EXCEED_MAXSIZE' // zul/Upload
	| 'VALUE_NOT_MATCHED' // zul/inp/Combobox
	, string>;
}