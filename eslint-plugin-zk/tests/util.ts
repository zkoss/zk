import { RuleTester } from '@typescript-eslint/rule-tester';
import * as tsParser from '@typescript-eslint/parser';
import * as path from 'path';

const parserOptions = { project: './tsconfig.json', tsconfigRootDir: path.resolve(__dirname, '..') };

export const typedRuleTester = new RuleTester({ languageOptions: { parser: tsParser, parserOptions } });
export const untypedRuleTester = new RuleTester({ languageOptions: { parser: tsParser } });
export function noFormat(strings: TemplateStringsArray, ...keys: string[]): string {
	return strings.reduce((result, str, i) => result + str + (keys[i] || ''), '');
}