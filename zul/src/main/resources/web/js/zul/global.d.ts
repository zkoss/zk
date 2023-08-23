export * as box from './box';
export * as db from './db';
export * as fud from './fud';
export * as grid from './grid';
export * as inp from './inp';
export * as layout from './layout';
export * as med from './med';
export * as menu from './menu';
export * as mesh from './mesh';
export * as sel from './sel';
export * as tab from './tab';
export * as utl from './utl';
export * as wgt from './wgt';
export * as wnd from './wnd';
export * from '.';
export as namespace zul;

declare global {
	interface HTMLElement {
		/** @internal */
		_width?: string; // zul.tab.Tabs
		/** @internal */
		_lastsz?: { // zul/mesh
			width: number;
			height: number;
		};
		/** @internal */
		_lastSize?: { // zul/layout
			width: number;
			height: number;
		};
	}

	// HTMLMediaElement has the same properties just in different captilization
	interface HTMLEmbedElement { // zul/med/Flash
		movie: string;
		wmode: string;
		bgcolor: string;
		quality: string;
		autoplay: boolean | '';
		loop: boolean | '';
	}

    const msgzul: Record<
	| 'DATE_REQUIRED'
	| 'EMPTY_NOT_ALLOWED'
	| 'FIRST' // zul/mesh/Paging
	| 'GRID_ASC' // zul/mesh/ColumnMenuWidget
	| 'GRID_DESC' // zul/mesh/ColumnMenuWidget
	| 'GRID_GROUP' // zul/mesh/ColumnMenuWidget
	| 'GRID_OTHER' // zul/sel/Listheader
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
	| 'CANCEL' // touch/timebox-touch
	| 'OUT_OF_RANGE_SEPARATOR' // zul/inp/SimpleLocalTimeConstraint
	| 'OUT_OF_RANGE'
	| 'PANEL_COLLAPSE' // zul/wnd/Panel
	| 'PANEL_CLOSE' // zul/wgt/ButtonRenderer
	| 'PANEL_EXPAND' // zul/inp/ComboWidget
	| 'PANEL_MAXIMIZE' // zul/wnd/Panel
	| 'PANEL_MINIMIZE' // zul/wgt/ButtonRenderer
	| 'PANEL_RESTORE' // zul/wnd/Panel
	| 'PREV' // zul/inp/Intbox
	| 'UNKNOWN_TYPE'
	| 'UPLOAD_CANCEL' // zul/fud/FileuploadDig
	| 'UPLOAD_ERROR_EXCEED_MAXSIZE' // zul/Upload
	| 'VALUE_NOT_MATCHED' // zul/inp/Combobox
	| 'WS_HOME' // zul/WScroll
	| 'WS_PREV' // zul/WScroll
	| 'WS_NEXT' // zul/WScroll
	| 'WS_END' // zul/WScroll
	| 'CURRENT_DESCRIPTION' // zul/mesh-a11y
	| 'DETAILED_DESCRIPTION' // zul/mesh-a11y
	| 'ITEMWIDGET_SELECTED' // zul/sel-a11y
	| 'LISTBOX_SELECT_ALL' // zul/sel-a11y
	| 'NOTIFICATION_INFO' // zul/wgt-a11y
	| 'NOTIFICATION_WARNING' // zul/wgt-a11y
	| 'NOTIFICATION_ERROR' // zul/wgt-a11y
	| 'COLORBOX_PALETTE_BUTTON_LABEL' // zkex/inp-a11y
	| 'COLORBOX_PICKER_BUTTON_LABEL' // zkex/inp-a11y
	| 'COLORBOX_CURRENT_COLOR' // zkex/inp-a11y
	| 'COLORBOX_COLOR_PALETTE' // zkex/inp-a11y
	| 'COLORBOX_COLOR_PICKER' // zkex/inp-a11y
	| 'COLORBOX_COLOR_R' // zkex/inp-a11y
	| 'COLORBOX_COLOR_G' // zkex/inp-a11y
	| 'COLORBOX_COLOR_B' // zkex/inp-a11y
	| 'COLORBOX_HUE' // zkex/inp-a11y
	| 'COLORBOX_SATURATION' // zkex/inp-a11y
	| 'COLORBOX_VALUE' // zkex/inp-a11y
	| 'COLORBOX_COLOR_HEX' // zkex/inp-a11y
	| 'COLORBOX_COLOR_SPECTRUM_BAR' // zkex/inp-a11y
	| 'COLORBOX_SELECT_COLOR_BUTTON' // zkex/inp-a11y
	| 'COLORBOX_COLOR_FIELD' // zkex/inp-a11y
	| 'CHOSENBOX_CREATE_MESSAGE' // zkmax/inp-a11y
	| 'CHOSENBOX_SELECTION' // zkmax/inp-a11y
	, string>;
}