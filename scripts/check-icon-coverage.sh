#!/usr/bin/env bash
# check-icon-coverage.sh — enforces the icon-naming policy in `doc/spec/icon-policy.md`.
#
# Rule 2 (preview/example content): every `z-icon-{name}` referenced in
#   zkthemelab/src/main/webapp/web/**/*.zul MUST resolve to a real served icon class —
#   i.e. one that build-css.js actually emits. That is the union of:
#     - a Lucide name (`node_modules/lucide-static/icons/{name}.svg` exists),
#     - a `FA_TO_LUCIDE` alias key (the FA-style names ZK widget JS emits, e.g.
#       `caret-down`, `angle-up`, `cogs`, `times` — build emits a `.z-icon-{fa}` rule),
#     - a `CUSTOM_ICONS` key (bare glyphs ZK emits with no Lucide equivalent, e.g.
#       `exclamation`),
#     - `fw` (the no-glyph width modifier).
#   Both kinds — Lucide names AND ZK's built-in FA/custom class names — are allowed,
#   because both render. Only invented / misspelled names (no served rule) fail.
#   The FA + custom keys are read straight from `scripts/build-css.js` so the valid
#   set stays in lock-step with what the build serves (single source of truth).
#
# Rule 1 (ZK widget-emitted names) is NOT enforced here — it lives in the
# zk-theme-evaluator agent prompt and is checked manually when bumping ZK.
#
# Excluded files (icon catalogs — scanning them is pointless and was the dominant
# performance cost):
#   - icons-lucide.zul: auto-generated from lucide-static.
#   - utility/icons.zul: the "All Lucide Icons" catalog page (also hosts the
#     FA-compat alias demo). Excluded wholesale because the entire file is a catalog.
#
# Exit codes:
#   0 — clean (every preview icon resolves to a served icon class)
#   1 — at least one preview ZUL uses an unknown icon name; details on stderr
#
# Usage:
#   ./scripts/check-icon-coverage.sh

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LUCIDE_DIR="$PROJECT_ROOT/node_modules/lucide-static/icons"
ZUL_DIR="$PROJECT_ROOT/zkthemelab/src/main/webapp/web"

if [ ! -d "$LUCIDE_DIR" ]; then
    echo "ERROR: $LUCIDE_DIR not found. Run 'npm install' first." >&2
    exit 2
fi

# Build the set of valid Lucide names (basename without .svg).
VALID_FILE=$(mktemp)
trap 'rm -f "$VALID_FILE"' EXIT
ls -1 "$LUCIDE_DIR" | sed 's/\.svg$//' | sort -u > "$VALID_FILE"

# z-icon-fw is a width modifier (no glyph) — explicitly allowed.
echo "fw" >> "$VALID_FILE"

# ZK widget JS emits FA-style class names (e.g. z-icon-caret-down) and a few bare
# custom-glyph names (e.g. z-icon-exclamation). build-css.js generates real
# .z-icon-{name} rules for all of them via the FA_TO_LUCIDE and CUSTOM_ICONS maps,
# so they are genuine served classes and are valid in authored ZULs too. Pull their
# keys straight from those map definitions so this stays in lock-step with the build.
BUILD_CSS="$PROJECT_ROOT/scripts/build-css.js"
sed -n -e "/const FA_TO_LUCIDE = {/,/^};/p" -e "/const CUSTOM_ICONS = {/,/^};/p" "$BUILD_CSS" \
    | /usr/bin/grep -oE "^[[:space:]]*'[a-z0-9-]+':" | tr -d " ':" >> "$VALID_FILE"

sort -u -o "$VALID_FILE" "$VALID_FILE"

# Scan every preview ZUL except the icon catalogs. Stream the list NUL-delimited
# into a SINGLE awk process (first arg = valid-names file, rest = ZULs): a
# per-line shell loop spawning grep/sed was ~30k subprocess spawns (~2 min);
# one awk pass is sub-second. xargs -0 keeps this portable to bash 3.2 (no
# mapfile) and safe for paths with spaces. We accept z-icon-<name> tokens
# wherever they appear (iconSclass, sclass, class, plain text); FILENAME/FNR
# give the location with no path parsing.
find "$ZUL_DIR" -name '*.zul' ! -name 'icons-lucide.zul' ! -name 'icons.zul' -print0 \
    | sort -z \
    | xargs -0 awk -v root="$PROJECT_ROOT/" '
        NR == FNR { valid[$0] = 1; next }
        {
            line = $0
            while (match(line, /z-icon-[a-z0-9-]+/)) {
                name = substr(line, RSTART + 7, RLENGTH - 7)   # strip "z-icon-"
                line = substr(line, RSTART + RLENGTH)
                total++
                if (!(name in valid)) {
                    bad++
                    rel = FILENAME; sub(root, "", rel)
                    printf "FAIL: %s:%d — z-icon-%s does not resolve to a served icon class (no Lucide name or ZK FA/custom alias)\n", rel, FNR, name > "/dev/stderr"
                }
            }
        }
        END {
            if (bad) {
                print "" > "/dev/stderr"
                printf "FAILED — %d of %d icon references use unknown icon names.\n", bad, total > "/dev/stderr"
                print "Fix by using a real Lucide name or a known ZK FA/custom alias (see doc/spec/icon-index.md)." > "/dev/stderr"
                exit 1
            }
            printf "OK — %d icon references checked, all resolve to served icon classes.\n", total
        }
    ' "$VALID_FILE"
