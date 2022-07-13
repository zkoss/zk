module.exports = {
	'root': true,
	'env': {
	  'browser': true
	},
	'overrides': [
    {
      'files': [
        '*.js'
      ],
      'globals': {
        '$eval': 'readonly',
        'JQuery': 'readonly',
        'jq': 'writable',
        'JQ': 'writable',
        'zjq': 'writable',
        'zUtl': 'writable',
        'zAu': 'writable',
        'zWs': 'writable',
        'zWatch': 'writable',
        'zFlex': 'writable',
        'zKeys': 'writable',
        'msgzk': 'writable',
        'msgzul': 'writable',
        'Dates': 'readonly',
        'DateImpl': 'readonly',
        'WeakMap': 'readonly',
        // packages
        'zk': 'writable',
        'zhtml': 'writable',
        'zkbind': 'writable',
        'zul': 'writable',
        'zkex': 'writable',
        'zkmax': 'writable',
        'za11y': 'writable',
        // mount.js exports
        'zkdt': 'readonly',
        'zkx': 'readonly',
        'zkx_': 'readonly',
        'zkac': 'readonly',
        'zkmx': 'readonly',
        'zkmb': 'readonly',
        'zkme': 'readonly',
        'zkdh': 'readonly',
        // duplicate function declaration reserved keyword
        '_zkf': 'writable',
        'Moment': 'readonly'
      },
      'rules': {
        // Recommended
        'for-direction': 'error',
        'getter-return': 'error',
        'no-async-promise-executor': 'error',
        'no-compare-neg-zero': 'error',
        'no-console': 'error',
        'no-constant-condition': ['error', { 'checkLoops': false }],
        'no-control-regex': 'error',
        'no-debugger': 'error',
        'no-dupe-args': 'error',
        'no-dupe-else-if': 'error',
        'no-dupe-keys': 'error',
        'no-duplicate-case': 'error',
        'no-empty': ['error', { 'allowEmptyCatch': true }],
        'no-empty-character-class': 'error',
        'no-ex-assign': 'error',
        'no-extra-boolean-cast': 'error',
        'no-extra-semi': 'error',
        'no-func-assign': 'error',
        'no-import-assign': 'error',
        'no-inner-declarations': 'error',
        'no-invalid-regexp': 'error',
        'no-irregular-whitespace': 'error',
        'no-misleading-character-class': 'error',
        'no-obj-calls': 'error',
        'no-prototype-builtins': 'error',
        'no-regex-spaces': 'error',
        'no-setter-return': 'error',
        'no-sparse-arrays': 'error',
        'no-unexpected-multiline': 'error',
        'no-unreachable': 'error',
        'no-unsafe-finally': 'error',
        'no-unsafe-negation': 'error',
        'use-isnan': 'error',
        'valid-typeof': 'error',

        'no-alert': 'error',
        'no-case-declarations': 'error',
        'no-empty-pattern': 'error',
        'no-fallthrough': 'error',
        'no-global-assign': 'error',
        'no-octal': 'error',
        'no-self-assign': 'error',
        'no-unused-labels': 'error',
        'no-useless-catch': 'error',
        'no-useless-escape': 'error',
        'no-with': 'error',
        'no-delete-var': 'error',
        'no-shadow-restricted-names': 'error',
        'no-undef': ['error', { 'typeof': false }],
        'no-unused-vars': ['error', { 'vars': 'local', 'args': 'none' }],
        'no-eval': 'error',
        'no-implied-eval': 'error',
        'no-mixed-spaces-and-tabs': ['error', 'smart-tabs'],
        'no-multiple-empty-lines': 'error',
        'no-trailing-spaces': ['error', { 'skipBlankLines': true }],
        'no-multi-spaces': ['error', { 'ignoreEOLComments': true }],
        'brace-style': ['error', '1tbs', { 'allowSingleLine': true }],
        'dot-location': ['error', 'property'],
        //'operator-linebreak': ['error', 'after', { 'overrides': {'+': 'before', '-': 'before', '*': 'before', '/': 'before', '.': 'before'} }],
        'semi-spacing': 'error',
        'semi-style': 'error',
        'space-unary-ops': 'error',
        'key-spacing': 'error',
        'func-call-spacing': 'error',
        'one-var': ['error', 'consecutive'],
        'space-before-function-paren': ['error', { 'named': 'ignore' } ],
        'space-in-parens': 'error',
        'semi': 'error',
        'space-infix-ops': 'error',
        'keyword-spacing': 'error',
        'space-before-blocks': 'error',
        'switch-colon-spacing': 'error',
        'comma-spacing': 'error',
        'comma-style': 'error',
        'quotes': ['error', 'single', { 'avoidEscape': true }],
        'unicode-bom': 'error'
      }
    },
    {
      'files': [
        '*.ts'
      ],
      'parser': '@typescript-eslint/parser',
      'plugins': [
        '@typescript-eslint'
      ],
      'parserOptions': {
        'ecmaVersion': 5,
        'sourceType': 'script',
        'ecmaFeatures': {},
        'project': './tsconfig.json'
      },
      'extends': [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:@typescript-eslint/recommended-requiring-type-checking',
        'plugin:@typescript-eslint/strict'
      ],
      'rules': { // Recommended
        // Possible Problems
        'no-cond-assign': 'warn',
        'no-constant-condition': [
          'error',
          {
            'checkLoops': false
          }
        ],
        // Suggestions
        'no-alert': 'error',
        'no-console': 'error',
        'no-empty': [
          'error',
          {
            'allowEmptyCatch': true
          }
        ],
        'no-empty-function': 'error',
        'no-eval': 'warn',
        'no-implied-eval': 'warn',
        'no-var': 'off',
        'prefer-const': 'warn',
        'prefer-rest-params': 'warn',
        'prefer-spread': 'warn',
        // Layout & Formatting
        'brace-style': [
          'error',
          '1tbs',
          {
            'allowSingleLine': true
          }
        ],
        'dot-location': [
          'error',
          'property'
        ],
        'no-mixed-spaces-and-tabs': [
          'error',
          'smart-tabs'
        ],
        'no-multi-spaces': [
          'error',
          {
            'ignoreEOLComments': true
          }
        ],
        'no-multiple-empty-lines': 'error',
        'no-trailing-spaces': [
          'error',
          {
            'skipBlankLines': true
          }
        ],
        'semi-spacing': 'error',
        'semi-style': 'error',
        'space-unary-ops': 'error',
        'key-spacing': 'error',
        'func-call-spacing': 'error',
        'one-var': [
          'error',
          'consecutive'
        ],
        'space-before-function-paren': [
          'error',
          {
            'named': 'ignore'
          }
        ],
        'space-in-parens': 'error',
        'semi': 'error',
        'space-infix-ops': 'error',
        'keyword-spacing': 'error',
        'space-before-blocks': 'error',
        'switch-colon-spacing': 'error',
        'comma-spacing': 'error',
        'comma-style': 'error',
        'quotes': [
          'error',
          'single',
          {
            'avoidEscape': true
          }
        ],
        'unicode-bom': 'error',
        // typescript
        'no-undef': 'off',
        'no-unused-vars': 'off',
        '@typescript-eslint/no-unused-vars': 'off',
        '@typescript-eslint/ban-types': [
          'error',
          {
            'types': {
              'Function': {
                'message': 'Prefer `zk.Callable`',
                'fixWith': 'zk.Callable'
              }
            }
          }
        ],
        '@typescript-eslint/consistent-type-assertions': [
          'warn',
          {
            'assertionStyle': 'as',
            'objectLiteralTypeAssertions': 'never'
          }
        ],
        '@typescript-eslint/consistent-type-definitions': [
          'error',
          'interface'
        ],
        '@typescript-eslint/explicit-function-return-type': [
          'error',
          {
            'allowExpressions': true,
            'allowTypedFunctionExpressions': true,
            'allowHigherOrderFunctions': true
          }
        ],
        '@typescript-eslint/explicit-module-boundary-types': 'error',
        '@typescript-eslint/member-delimiter-style': 'error',
        '@typescript-eslint/naming-convention': [
          'error',
          {
            'selector': 'interface',
            'format': [
              'PascalCase'
            ],
            'custom': {
              'regex': '^I[A-Z]',
              'match': false
            }
          }
        ],
        '@typescript-eslint/no-empty-interface': [
          'error',
          {
            'allowSingleExtends': true
          }
        ],
        '@typescript-eslint/no-explicit-any': 'error',
        '@typescript-eslint/no-implied-eval': 'warn',
        '@typescript-eslint/no-namespace': [
          'error',
          {
            'allowDeclarations': true,
            'allowDefinitionFiles': true
          }
        ],
        '@typescript-eslint/no-non-null-asserted-nullish-coalescing': 'off',
        '@typescript-eslint/no-non-null-asserted-optional-chain': 'off',
        '@typescript-eslint/no-non-null-assertion': 'off',
        '@typescript-eslint/no-parameter-properties': 'error', // deprecated
        '@typescript-eslint/no-require-imports': 'error',
        '@typescript-eslint/no-this-alias': 'warn',
        '@typescript-eslint/restrict-plus-operands': 'warn',
        '@typescript-eslint/triple-slash-reference': [
          'error',
          {
            'path': 'never',
            'types': 'never',
            'lib': 'never'
          }
        ],
        '@typescript-eslint/type-annotation-spacing': 'error',
        '@typescript-eslint/unbound-method': 'off'
      }
    }
  ]
};