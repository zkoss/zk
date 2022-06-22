export * from './numfmt';
export * from './msgfmt';
export type Calendar = import('@zul/db/datefmt').Calendar;
export let Calendar: typeof import('@zul/db/datefmt').Calendar;
export declare let Date: typeof import('@zul/db/datefmt').FmtDate;
export default {};