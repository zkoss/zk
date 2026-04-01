// Ambient declarations for packages that only use `exports` field (no `main`/`types` top-level).
// TypeScript 4.9.5 with moduleResolution "node" cannot resolve them.

declare module '@typescript-eslint/rule-tester' {
	import type { RuleTesterConfig } from '@typescript-eslint/utils/dist/ts-eslint/RuleTester';
	import type { RuleModule } from '@typescript-eslint/utils/dist/ts-eslint/Rule';

	interface RunTests<Options extends readonly unknown[]> {
		readonly valid: readonly (string | { code: string; options?: Options; name?: string })[];
		readonly invalid: readonly {
			code: string;
			options?: Options;
			errors: readonly { messageId: string; line?: number; column?: number; data?: Record<string, unknown> }[];
			output?: string | null;
			name?: string;
		}[];
	}

	export class RuleTester {
		constructor(config: Record<string, unknown>);
		run<Options extends readonly unknown[]>(
			name: string,
			rule: RuleModule<string, Options>,
			tests: RunTests<Options>
		): void;
	}
}

declare module '@typescript-eslint/parser' {
	const parser: Record<string, unknown>;
	export = parser;
}
