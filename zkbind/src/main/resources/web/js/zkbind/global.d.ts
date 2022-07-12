export as namespace zkbind;
export * from '.';

declare global {
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent#parameters
	// According to the link above, `encodeURIComponent` accepts anything as argument.
	// We override typescript's built-in declaration which only accepts `string | number | boolean`.
	function encodeURIComponent(uriComponent: unknown): string;
}