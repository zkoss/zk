/* index.ts

	Purpose:

	Description:

	History:
		12:22 PM 2022/1/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type Callable = (...arg: any[]) => unknown;

export type StringFieldValue = string | null | undefined;

export type BooleanFieldValue = boolean | null | undefined;

export type NumberFieldValue = number | null | undefined;

export type DOMFieldValue = HTMLElement | null | undefined;

export type Offset = [number, number];

export function cast<T>(o: unknown): T {
	return o as T;
}