export * from './numfmt';
export * from './msgfmt';
export default {};

// export { Date, Calendar } from '@zul/db/datefmt';
// The following should have the same effect as the line above except that
// nothing in `@zul/db/datefmt` is actually imported through this file. We merely
// want to "declare" the following values/types through this file.
export declare var Date: typeof import('@zul/db/datefmt').Date;
export declare var Calendar: typeof import('@zul/db/datefmt').Calendar;
export type Calendar = import('@zul/db/datefmt').Calendar;