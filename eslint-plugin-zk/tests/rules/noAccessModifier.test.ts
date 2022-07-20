import { noAccessModifier } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function omitAccessModifierError(line: number, column: number) {
	return {
		messageId: 'omitAccessModifier' as const,
		line,
		column,
	};
}

ruleTester.run('noAccessModifier', noAccessModifier, {
	valid: [`
class Test {
  constructor(foo: string) {}
  /*blablabla*/ get foo(): string {}
  readonly a?: boolean;
}`,
	],
	invalid: [
		{
			code: `
class Test {
  public constructor(private foo: string) {}
  protected /*blablabla*/ get foo(): string {}
  private readonly a?: boolean;
}`,
			output: `
class Test {
  constructor(foo: string) {}
  /*blablabla*/ get foo(): string {}
  readonly a?: boolean;
}`,
			errors: [
				omitAccessModifierError(3, 3),
				omitAccessModifierError(3, 22),
				omitAccessModifierError(4, 3),
				omitAccessModifierError(5, 3),
			],
		}
	]
});