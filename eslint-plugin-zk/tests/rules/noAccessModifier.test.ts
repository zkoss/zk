import { noAccessModifier } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function omitAccessModifier(line: number, column: number) {
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
				omitAccessModifier(3, 3),
				omitAccessModifier(3, 22),
				omitAccessModifier(4, 3),
				omitAccessModifier(5, 3),
			],
		}
	]
});