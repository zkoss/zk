module.exports = {
  "root": true,
  "parser": "@typescript-eslint/parser",
  "plugins": [
    "@typescript-eslint"
  ],
  "parserOptions": {
    "ecmaVersion": 5,
    "sourceType": "script",
    "ecmaFeatures": {}
  },
  "env": {
    "browser": true
  },
  "overrides": [
    {
      "files": [
        "*.ts"
      ],
      "parserOptions": {
        "tsconfigRootDir": __dirname,
        "project": "./tsconfig.json"
      },
      "rules": {
        // Recommended
        "for-direction": "error",
        "getter-return": "error",
        "no-async-promise-executor": "error",
        "no-compare-neg-zero": "error",
        "no-console": "error",
        "no-constant-condition": [
          "error",
          {
            "checkLoops": false
          }
        ],
        "no-control-regex": "error",
        "no-debugger": "error",
        "no-dupe-args": "error",
        "no-dupe-else-if": "error",
        "no-dupe-keys": "error",
        "no-duplicate-case": "error",
        "no-empty": [
          "error",
          {
            "allowEmptyCatch": true
          }
        ],
        "no-empty-character-class": "error",
        "no-ex-assign": "error",
        "no-extra-boolean-cast": "error",
        "no-extra-semi": "error",
        "no-func-assign": "error",
        "no-import-assign": "error",
        "no-inner-declarations": "error",
        "no-invalid-regexp": "error",
        "no-irregular-whitespace": "error",
        "no-misleading-character-class": "error",
        "no-obj-calls": "error",
        "no-prototype-builtins": "error",
        "no-regex-spaces": "error",
        "no-setter-return": "error",
        "no-sparse-arrays": "error",
        "no-unexpected-multiline": "error",
        "no-unreachable": "error",
        "no-unsafe-finally": "error",
        "no-unsafe-negation": "error",
        "use-isnan": "error",
        "valid-typeof": "error",
        "no-alert": "error",
        "no-case-declarations": "error",
        "no-empty-pattern": "error",
        "no-fallthrough": "error",
        "no-global-assign": "error",
        "no-octal": "error",
        "no-self-assign": "error",
        "no-unused-labels": "error",
        "no-useless-catch": "error",
        "no-useless-escape": "error",
        "no-with": "error",
        "no-delete-var": "error",
        "no-shadow-restricted-names": "error",
        // https://typescript-eslint.io/docs/linting/troubleshooting/#i-get-errors-from-the-no-undef-rule-about-global-variables-not-being-defined-even-though-there-are-no-typescript-errors
        // https://github.com/typescript-eslint/typescript-eslint/issues/1856
        // https: //github.com/typescript-eslint/typescript-eslint/issues/2477#issuecomment-686892459
        // Just rely on tsc for no-undef, eslint is lacking in scope analysis.
        "no-undef": "off", // no-undef": ["error", {"typeof": false}],
        // https://github.com/typescript-eslint/typescript-eslint/blob/main/packages/eslint-plugin/docs/rules/no-unused-vars.md#how-to-use
        // Prefer tsc 
        "no-unused-vars": "off", // "no-unused-vars": ["error", {"vars": "local", "args": "none" /*TODO*/}],
        "no-eval": "error",
        "no-implied-eval": "error",
        "no-mixed-spaces-and-tabs": [
          "error",
          "smart-tabs"
        ],
        "no-multiple-empty-lines": "error",
        "no-trailing-spaces": [
          "error",
          {
            "skipBlankLines": true
          }
        ],
        "no-multi-spaces": [
          "error",
          {
            "ignoreEOLComments": true
          }
        ],
        "brace-style": [
          "error",
          "1tbs",
          {
            "allowSingleLine": true
          }
        ],
        "dot-location": [
          "error",
          "property"
        ],
        //"operator-linebreak": ["error", "after", { "overrides": {"+": "before", "-": "before", "*": "before", "/": "before", ".": "before"} }],
        "semi-spacing": "error",
        "semi-style": "error",
        "space-unary-ops": "error",
        "key-spacing": "error",
        "func-call-spacing": "error",
        "one-var": [
          "error",
          "consecutive"
        ],
        "space-before-function-paren": [
          "error",
          {
            "named": "ignore"
          }
        ],
        "space-in-parens": "error",
        "semi": "error",
        "space-infix-ops": "error",
        "keyword-spacing": "error",
        "space-before-blocks": "error",
        "switch-colon-spacing": "error",
        "comma-spacing": "error",
        "comma-style": "error",
        "quotes": [
          "error",
          "single",
          {
            "avoidEscape": true
          }
        ],
        "unicode-bom": "error",
        "@typescript-eslint/adjacent-overload-signatures": "error",
        "@typescript-eslint/ban-types": [
          "error",
          {
            "types": {
              "Function": false
            }
          }
        ],
        "@typescript-eslint/ban-ts-comment": "error",
        "@typescript-eslint/consistent-type-assertions": [
          "error",
          {
            "assertionStyle": "as",
            "objectLiteralTypeAssertions": "never"
          }
        ],
        "@typescript-eslint/consistent-type-definitions": [
          "error",
          "interface"
        ],
        "@typescript-eslint/explicit-function-return-type": [
          "error",
          {
            "allowExpressions": true,
            "allowTypedFunctionExpressions": true,
            "allowHigherOrderFunctions": true
          }
        ],
        "@typescript-eslint/member-delimiter-style": "error",
        "@typescript-eslint/naming-convention": [
          "error",
          {
            "selector": "interface",
            "format": [
              "PascalCase"
            ],
            "custom": {
              "regex": "^I[A-Z]",
              "match": false
            }
          }
        ],
        "@typescript-eslint/no-empty-interface": [
          "error",
          {
            "allowSingleExtends": true
          }
        ],
        "@typescript-eslint/no-explicit-any": "error",
        "@typescript-eslint/no-inferrable-types": "error",
        "@typescript-eslint/no-misused-new": "off",
        "@typescript-eslint/no-namespace": "off",
        "@typescript-eslint/no-parameter-properties": "error",
        "@typescript-eslint/no-require-imports": "error",
        "@typescript-eslint/triple-slash-reference": [
          "error",
          {
            "path": "never",
            "types": "never",
            "lib": "never"
          }
        ],
        "@typescript-eslint/no-var-requires": "error",
        "@typescript-eslint/type-annotation-spacing": "error"
        // "@typescript-eslint/explicit-module-boundary-types": "error" // TODO
        // "import/prefer-default-export": "off",
        // "@typescript-eslint/explicit-function-return-type": "error",
      }
    }
  ]
}