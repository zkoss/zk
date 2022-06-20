import './datefmt'; // Side-effect-only import to register zk.fmt members
import './Timebox'; // Side-effect-only import to register zul.inp members
export { Timebox } from './Timebox'; // Expose Timebox via zul.db
export * from './Calendar';
export * from './Datebox';

// Unformater is used by zul.db.Timebox and zul.db.Datebox. Its signature is found at
// https://blog.zkoss.org/2011/11/06/zk-6-0-new-feature-highlight-part-3-dateboxtimebox-input-shortcut/
export type Unformater = (val?: string | null) => DateImpl | null;