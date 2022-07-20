module.exports = {
	"root": true,
    "env": {
        "es2021": true,
        "node": true
    },
    "extends": [
        "eslint:recommended",
        "plugin:@typescript-eslint/recommended",
		"plugin:@typescript-eslint/recommended-requiring-type-checking",
		"plugin:@typescript-eslint/strict"
    ],
    "parser": "@typescript-eslint/parser",
    "parserOptions": {
        "ecmaVersion": "latest",
        "sourceType": "module",
		"tsconfigRootDir": __dirname,
		"project": "./tsconfig.json"
    },
    "plugins": [
        "@typescript-eslint"
    ],
    "rules": {
        "indent": [
            "error",
            "tab"
        ],
        "linebreak-style": [
            "error",
            "unix"
        ],
        "quotes": [
            "error",
            "single"
        ],
        "semi": [
            "error",
            "always"
        ]
    },
	"overrides": [
		{
			"files": [
				"*.ts"
			],
			"rules": {
				// Let TS handle `undef` and `unused-vars`.
				"no-undef": "off",
				"no-unused-vars": "off",
				"@typescript-eslint/no-unused-vars": "off"
			}
		}
	]
}
