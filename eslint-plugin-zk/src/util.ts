import { ESLintUtils } from '@typescript-eslint/utils';
import { homepage } from '../package.json';

interface ZkPluginDocs {
	recommended: string;
}

const rulesURL = new URL('/rules', homepage).href;
export const createRule = ESLintUtils.RuleCreator<ZkPluginDocs>(name => `${rulesURL}/${name}`);
export import getParserServices = ESLintUtils.getParserServices;