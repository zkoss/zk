#!/usr/bin/env bash
# check-icon-coverage.sh — enforces the icon-naming policy in `doc/spec/icon-policy.md`.
#
# Rule 2 (preview/example content): every `z-icon-{name}` referenced in
#   zkthemelab/src/main/webapp/web/**/*.zul MUST render in both Marble and IceBlue:
#   (a) it is a served Marble icon class — one that build-css.js actually emits, i.e.
#   the union of:
#     - a Lucide name (`node_modules/lucide-static/icons/{name}.svg` exists),
#     - a `FA_TO_LUCIDE` alias key (the FA-style names ZK widget JS emits, e.g.
#       `caret-down`, `angle-up`, `cogs`, `times` — build emits a `.z-icon-{fa}` rule),
#     - a `CUSTOM_ICONS` key (bare glyphs ZK emits with no Lucide equivalent, e.g.
#       `exclamation`),
#     - `fw` (the no-glyph width modifier).
#   The FA + custom keys are read straight from `scripts/build-css.js` so the valid
#   set stays in lock-step with what the build serves (single source of truth); and
#   (b) IceBlue renders it with a glyph: it is listed in `scripts/iceblue-icon-names.txt`
#   (`fw` is exempt). A name that only one theme draws shows a blank icon in the other,
#   which is easy to misread as a theme defect.
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
#   0 — clean (every preview icon renders in both themes)
#   1 — at least one preview ZUL uses a name one of the themes does not render; details on stderr
#
# Usage:
#   ./scripts/check-icon-coverage.sh

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LUCIDE_DIR="$PROJECT_ROOT/node_modules/lucide-static/icons"
ICEBLUE_FILE="$SCRIPT_DIR/iceblue-icon-names.txt"
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

# Keep only the names IceBlue also renders (fw has no glyph in either theme).
BOTH_FILE=$(mktemp)
trap 'rm -f "$VALID_FILE" "$BOTH_FILE"' EXIT
{ /usr/bin/grep -v '^#' "$ICEBLUE_FILE"; echo "fw"; } | sort -u | comm -12 "$VALID_FILE" - > "$BOTH_FILE"

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
                    printf "FAIL: %s:%d — z-icon-%s does not render in both Marble and IceBlue\n", rel, FNR, name > "/dev/stderr"
                }
            }
        }
        END {
            if (bad) {
                print "" > "/dev/stderr"
                printf "FAILED — %d of %d icon references use a name one of the themes does not render.\n", bad, total > "/dev/stderr"
                print "Fix by using a name that is both served by Marble (doc/spec/icon-index.md) and listed in scripts/iceblue-icon-names.txt." > "/dev/stderr"
                exit 1
            }
            printf "OK — %d icon references checked, all render in both Marble and IceBlue.\n", total
        }
    ' "$BOTH_FILE"
