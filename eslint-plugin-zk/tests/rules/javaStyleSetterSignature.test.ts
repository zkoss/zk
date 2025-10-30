import { javaStyleSetterSignature } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function setterReturnThis(line: number, column: number) {
	return {
		messageId: 'setterReturnThis' as const,
		line,
		column,
	};
}

function setterSuggestiveParameter(line: number, column: number) {
	return {
		messageId: 'setterSuggestiveParameter' as const,
		line,
		column,
	};
}

ruleTester.run('javaStyleSetterSignature', javaStyleSetterSignature, {
	valid: [`
class A {
  set open(v: boolean) {}
  set(v: string): void {}
  set_(v: number): void {}
  static set(v: string): void {}
  static set_(v: number): void {}
  static setEnglishName(v: string): void {}
}
`, `
let a = {
  set open(v: boolean) {}
}
`,
	],
	invalid: [
		{
			code: `
class Test {
  setName(name: string): void {
    if (this._name != name) {
      console.log('name = ' + name);
    }
    this._name = name;
  }
  setOpen(open) {
    this._open = open;
  }
  setDisabled(disabled): this {}
}
abstract class Test2 {
  abstract setAutodisabled(autodisabled: boolean | undefined)
}
`,
			output: `
class Test {
  setName(name: string): this {
    if (this._name != name) {
      console.log('name = ' + name);
    }
    this._name = name;
  }
  setOpen(open): this {
    this._open = open;
  }
  setDisabled(disabled): this {}
}
abstract class Test2 {
  abstract setAutodisabled(autodisabled: boolean | undefined): this
}
`,
			errors: [
				setterReturnThis(3, 24),
				setterReturnThis(9, 3),
				setterReturnThis(15, 3),
			],
		},
		{
			code: `
class Test {
  setName(val: string): this {
    if (this._name != val) {
      console.log('name = ' + val);
    }
    this._name = val;
  }
  setOpen(v): this {
    this._open = v;
  }
  setDisabled(disabled): this {}
  setFlexSizeH_(flexSizeH: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {}
}
abstract class Test2 {
  abstract setAutodisabled(v: boolean | undefined): this
}
`,
			output: `
class Test {
  setName(name: string): this {
    if (this._name != name) {
      console.log('name = ' + name);
    }
    this._name = name;
  }
  setOpen(open): this {
    this._open = open;
  }
  setDisabled(disabled): this {}
  setFlexSizeH_(flexSizeH: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {}
}
abstract class Test2 {
  abstract setAutodisabled(autodisabled: boolean | undefined): this
}
`,
			errors: [
				setterSuggestiveParameter(3, 11),
				setterSuggestiveParameter(9, 11),
				setterSuggestiveParameter(16, 28),
			],
		}
	]
});