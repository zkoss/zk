import { preferNativeInstanceof } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function useInstanceofOperator(line: number, column: number) {
	return {
		messageId: 'useInstanceofOperator' as const,
		line,
		column,
	};
}

ruleTester.run('preferNativeInstanceof', preferNativeInstanceof, {
	valid: [`
isInstanceof()
`, `
isInstanceof(a)
`, `
isInstanceof(a, b)
`, `
isInstanceof(a, b, c)
`, `
A.isInstanceof()
`, `
A.isInstanceof(a, b)
`, `
A.isInstanceof(a, b, c)
`, `
$instanceof()
`, `
$instanceof(A, B)`
	],
	invalid: [
		{
			code: `
!zkmax.nav.Anchornav.isInstance(this._wgt) &&
this.$instanceof(zul.wgt.A) &&
!this._wgt.$instanceof(zul.wgt.A, zul.wgt.Button, zkex.inp.Color)
`,
			output: `
!(this._wgt instanceof zkmax.nav.Anchornav) &&
(this instanceof zul.wgt.A) &&
!(this._wgt instanceof zul.wgt.A || this._wgt instanceof zul.wgt.Button || this._wgt instanceof zkex.inp.Color)
`,
			errors: [
				useInstanceofOperator(2, 22),
				useInstanceofOperator(3, 6),
				useInstanceofOperator(4, 12),
			],
		}
	]
});