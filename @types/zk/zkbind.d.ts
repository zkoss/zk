/* zkbind.d.ts

	Purpose:
		Type definitions for ZK
	Description:
		
	History:
		Fri Feb 19 12:39:06 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

declare namespace zk {
	interface ZKBind {
		$(n: string | HTMLElement | zk.Event | JQuery.Event, opts?: Partial<BinderOptions>): Binder | null;
		Binder: BinderStatic;
	}

	interface BinderStatic {
		new (wgt: zk.Widget, target: any): Binder;
		postCommand(dom: HTMLElement, command: string, args?: Record<string, unknown> | null,
			opts?: Partial<zk.EventOptions> | null, timeout?: number): void;
		postGlobalCommand(dom: HTMLElement, command: string, args?: Record<string, unknown> | null,
			opts?: Partial<zk.EventOptions> | null, timeout?: number): void;
	}

	interface BinderOptions {
		exact: boolean;
		strict: boolean;
		child: boolean;
	}

	interface Binder extends zk.Object {
		after(cmd: string, fn: (args?: Record<string, unknown>) => void): this;
		unAfter(cmd: string, fn: (args?: Record<string, unknown>) => void): this;
		destroy(): void;
		command(cmd: string, args?: Record<string, unknown> | null, opts?: Partial<zk.EventOptions> | null, timeout?: number): this;
		globalCommand(cmd: string, args?: Record<string, unknown> | null, opts?: Partial<zk.EventOptions> | null, timeout?: number): this;
		upload(cmd: string, file: File): void;
	}
}

declare var zkbind: zk.ZKBind;