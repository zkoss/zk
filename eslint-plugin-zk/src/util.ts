import { ESLintUtils } from '@typescript-eslint/utils';
import { homepage } from '../package.json';

const rulesURL = new URL('/rules', homepage).href;
export const createRule = ESLintUtils.RuleCreator(name => `${rulesURL}/${name}`);