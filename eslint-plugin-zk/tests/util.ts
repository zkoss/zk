import { ESLintUtils } from '@typescript-eslint/utils';

// import { parser, parserOptions } from '../.eslintrc.json' as const;
// FIXME: Awaiting strongly-typed JSON module imports, which allows the line of code above:
// See https://github.com/microsoft/TypeScript/issues/32063
const parser = '@typescript-eslint/parser';
const parserOptions = { project: './tsconfig.json' };

export const typedRuleTester = new ESLintUtils.RuleTester({ parser, parserOptions });
export const untypedRuleTester = new ESLintUtils.RuleTester({ parser });
export import noFormat = ESLintUtils.noFormat;