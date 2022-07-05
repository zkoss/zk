// Everything exported from this file must either be a "declared value" or a "type".
// They must be bundled in `zul/db` not `zul/inp`. The effect of this should be like
// the following commented piece of code except that values are "declared" not
// "defined". Typescript doesn't yet have syntax to "re-export as declaration", so we
// 1. export classes as types,
// 2. export classes as declared values whose types are "types of that in point 1".
// The current solution is very fragile as it repeats the same name 4 times each, which
// must be carefully checked.
/*
export {
	TimeHandler,
	HourInDayHandler,
	HourInDayHandler2,
	HourHandler,
	HourHandler2,
	MinuteHandler,
	SecondHandler,
	AMPMHandler,
} from '../db/Timebox';
*/

export declare var TimeHandler: typeof import('../db/Timebox').TimeHandler;
export declare var HourInDayHandler: typeof import('../db/Timebox').HourInDayHandler;
export declare var HourInDayHandler2: typeof import('../db/Timebox').HourInDayHandler2;
export declare var HourHandler: typeof import('../db/Timebox').HourHandler;
export declare var HourHandler2: typeof import('../db/Timebox').HourHandler2;
export declare var MinuteHandler: typeof import('../db/Timebox').MinuteHandler;
export declare var SecondHandler: typeof import('../db/Timebox').SecondHandler;
export declare var AMPMHandler: typeof import('../db/Timebox').AMPMHandler;

export type TimeHandler = import('../db/Timebox').TimeHandler;
export type HourInDayHandler = import('../db/Timebox').HourInDayHandler;
export type HourInDayHandler2 = import('../db/Timebox').HourInDayHandler2;
export type HourHandler = import('../db/Timebox').HourHandler;
export type HourHandler2 = import('../db/Timebox').HourHandler2;
export type MinuteHandler = import('../db/Timebox').MinuteHandler;
export type SecondHandler = import('../db/Timebox').SecondHandler;
export type AMPMHandler = import('../db/Timebox').AMPMHandler;