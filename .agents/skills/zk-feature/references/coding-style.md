<!-- Canonical source: zk-bug/references/coding-style.md — keep both copies in sync -->

# Coding Style Reference

## General Rules (All Languages)

- Disallow empty blocks (use comments if block must be empty)
- Maximum line length: 100 characters (excluding comments)

---

## Java

- No star imports (`import java.util.*` is forbidden)
- One top-level class per file
- No line wrap for declarations
- Empty blocks must contain a comment
- Curly braces: consistent opening/closing placement
- Whitespace around operators and keywords
- One statement per line
- Array brackets as part of type: `String[] args` not `String args[]`
- Long literals use uppercase `L`: `100L`
- Modifier order: `public/protected/private`
- Empty line separator between class members
- Dot, comma, ellipsis, method ref wrapping rules apply
- Parameter and member names: `lowerCamelCase`
- No finalizer
- No extra spaces around generic symbols
- Import order must be consistent
- Operators: break lines **after** operators, not before
- Annotations on their own line
- Method names: `lowerCamelCase` with a verb
- Empty catch block must specify a valid variable name
- No redundant imports

---

## JavaScript

### Disallowed
- `with`, `eval`
- Mixed spaces and tabs
- Multiple spaces
- Newline before block statements
- Operator before line break
- Space before semicolon
- Space before postfix unary operators
- Space after object keys
- Spaces in call expression
- Trailing comma
- `alert`, `confirm`, `prompt`
- `console` (except in `gulpfile.js`)
- `debugger`

### Required
- Space before block statements
- Spaces in anonymous/named function expressions
- Semicolons
- Space after/before binary operators
- Space before keywords: `else`, `catch`
- Space before object values
- Spaces in conditional expression
- CRLF line breaks
- Single quotes (except to avoid escaping)
- Capitalized constructors

### Variables & Functions
- No duplicate keys, args, or case labels
- No empty character classes
- No reassignment of catch exceptions
- No extra boolean casts or semicolons
- No function reassignment
- No undeclared variables (`no-undef`)
- No unused variables

### Best Practices
- Use `isNaN()` for NaN checks
- Valid `typeof` comparisons only
- No fallthrough in switch
- No global reassignment
- No octal literals
- No useless catch, escape, or `with`
- No delete on variables
- No eval or implied eval

### Formatting
- Brace style: one true brace style
- Consistent dot location for property chains
- Key spacing around colon in object literals
- No multiple empty lines
- No trailing spaces
- Single quotes
- No Unicode BOM
- `one var` per scope

### ZK-Specific ESLint Rules
- `zk/noMixedHtml` — no mixed HTML
- `zk/noLocationHrefAssign` — no direct `location.href` assignment

---

## TypeScript

- No dynamic delete (`@typescript-eslint/no-dynamic-delete`)
- No implied eval
- Explicit function return types required
- No `any` (`@typescript-eslint/no-explicit-any`)
- Prefer `interface` over `type` for object definitions
- Use `as` for type assertions (not angle brackets)
- Consistent member delimiter style in interfaces
- Interface names: PascalCase
- SDL rules enabled (`@microsoft/eslint-plugin-sdl`)

---

## Node.js (`gulpfile.js`)

- `console` statements allowed
- ECMAScript version: 2020
- Variables may be declared separately