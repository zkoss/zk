#!/usr/bin/env bash
# check-doc-tokens.sh — every `--zk-*` name a Marble doc mentions must still exist.
#
# Catches the drift a token removal or rename leaves behind: a skill reference,
# a spec page or a token-file comment that still names a token no CSS defines.
#
# Scanned:  .claude/skills/marble-theme/**/*.md, doc/**/*.md and doc/**/*.html,
#           and zul/src/main/resources/web/zul/css/tokens/*.css (comments included).
# Defined:  every `--zk-x:` declaration in the CE theme CSS and, when the zkcml
#           checkout is found, in its zkmax / zkex / za11y CSS; also every name a
#           widget's TS/JS reads with `var(--zk-x, <fallback>)` (a hook the theme
#           leaves undefined, such as the codeeditor token colours).
# Skipped:  wildcards (`--zk-color-*`, `--zk-spacing-{…}`, `--zk-foo-<n>`), and the
#           names in check-doc-tokens.allow (IceBlue names, placeholders, flags).
#
# It cannot see a token referred to by a nickname or a value ("data-row 52");
# .claude/rules/doc-conventions.md forbids that, so the full name is always greppable.
#
# Usage:
#   bash check-doc-tokens.sh [--zkcml <dir>]
#     --zkcml <dir>   the EE checkout (default: $ZKCML, else ../zkcml beside this repo)
#
# Exit status: 0 when every name resolves, 1 when any does not (each printed as
# file:line name), 2 on a usage error.

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(git -C "$SCRIPT_DIR" rev-parse --show-toplevel)" || exit 2
ZKCML="${ZKCML:-$PROJECT_ROOT/../zkcml}"

while [ $# -gt 0 ]; do
    case "$1" in
        --zkcml)   ZKCML="$2"; shift 2 ;;
        -h|--help) sed -n '2,22p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'; exit 0 ;;
        *) echo "Unknown option: $1" >&2; exit 2 ;;
    esac
done

# The real grep binary; see audit-css.sh for why ripgrep is not used.
GREP="$(command -v ggrep || command -v grep)"

CSS_ROOTS="$PROJECT_ROOT/zul/src/main/resources/web"
JS_ROOTS="$PROJECT_ROOT/zul/src/main/resources/web/js"
if [ -d "$ZKCML" ]; then
    for m in zkmax zkex za11y; do
        [ -d "$ZKCML/$m/src/main/resources/web" ] || continue
        CSS_ROOTS="$CSS_ROOTS $ZKCML/$m/src/main/resources/web"
        [ -d "$ZKCML/$m/src/main/resources/web/js" ] && JS_ROOTS="$JS_ROOTS $ZKCML/$m/src/main/resources/web/js"
    done
else
    echo "warning: zkcml not found at $ZKCML; EE/PE tokens count as undefined" >&2
fi

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

# shellcheck disable=SC2086
"$GREP" -rhoE --include='*.css' -e '--zk-[a-zA-Z0-9-]+[[:space:]]*:' $CSS_ROOTS \
    | sed 's/[[:space:]]*:$//' | sort -u > "$TMP/defined"
# shellcheck disable=SC2086
"$GREP" -rhoE --include='*.ts' --include='*.js' -e 'var\(--zk-[a-zA-Z0-9-]+' $JS_ROOTS \
    | sed 's/^var(//' | sort -u >> "$TMP/defined"
"$GREP" -vE '^[[:space:]]*(#|$)' "$SCRIPT_DIR/check-doc-tokens.allow" \
    | sed 's/[[:space:]].*//' >> "$TMP/defined"

status=0
while IFS= read -r hit; do
    file="${hit%%:*}"; rest="${hit#*:}"; line="${rest%%:*}"; name="${rest#*:}"
    case "$name" in *-|*'*'|*'{'|*'<') continue ;; esac   # wildcard
    if ! "$GREP" -qxF -- "$name" "$TMP/defined"; then
        echo "${file#"$PROJECT_ROOT"/}:$line $name"
        status=1
    fi
done < <(
    { find "$PROJECT_ROOT/.claude/skills/marble-theme" -name '*.md'
      find "$PROJECT_ROOT/doc" \( -name '*.md' -o -name '*.html' \)
      find "$PROJECT_ROOT/zul/src/main/resources/web/zul/css/tokens" -name '*.css'; } \
    | while IFS= read -r f; do
        "$GREP" -noE -- '--zk-[a-zA-Z0-9-]*[a-zA-Z0-9][-*{<]?' "$f" | sed "s|^|$f:|"
    done | sort -u
)

exit $status
