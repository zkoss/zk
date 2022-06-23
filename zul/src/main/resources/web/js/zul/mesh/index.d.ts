export * from './MeshWidget.js'; // jsdoc="true"
export * from './HeadWidget.js'; // jsdoc="true"
// export * from './ColumnMenuWidget.js'; // jsdoc="true"
// export * from './HeaderWidget.js'; // jsdoc="true"
export declare class HeaderWidget extends zul.LabelImageWidget {
	public parent: zul.mesh.HeadWidget | null;
	public nextSibling: zul.mesh.HeaderWidget | null;
	public _hflexWidth?: number;
	public _nhflexbak?: boolean;
	public static _faker: string[];
	public _origWd: string | null;
	public getContentWidth_(): number

	public setDisabled(disabled: boolean, opts?: Record<string, boolean>): this;
	public isDisabled(): boolean | undefined;
}
// export * from './SortWidget.js'; // jsdoc="true"
// export * from './FooterWidget.js'; // jsdoc="true"
export * from './Paging';
// export * from './Auxhead';
export declare class Auxhead extends zul.mesh.HeadWidget {}
// export * from './Auxheader';
export import Auxheader = zul.mesh.HeaderWidget; // zk/flex
export * from './Frozen';