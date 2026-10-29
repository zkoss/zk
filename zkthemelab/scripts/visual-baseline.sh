#!/usr/bin/env bash
#
# Local screenshot baselines for the Marble Playwright suite.
#
# The baselines are not in the repository: screenshots differ between machines (OS, fonts,
# GPU), so a baseline is only meaningful on the machine that compares against it. Capture
# them yourself BEFORE you edit CSS, then compare after the edit:
#
#   bash zkthemelab/scripts/visual-baseline.sh capture [playwright args...]
#   ... edit CSS, rebuild, reload the preview app ...
#   bash zkthemelab/scripts/visual-baseline.sh compare [playwright args...]
#
# Pass the same Playwright args to both (e.g. --project=desktop -g "button"). The preview app
# must be running and serving the CSS you want to capture (see verification.md).
# capture refuses to run while CSS has uncommitted changes, so the baseline is the unedited CSS.
# Exit: 2 = usage error or a refused capture; otherwise Playwright's exit code.

set -uo pipefail

MODULE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ROOT="$(git -C "$MODULE" rev-parse --show-toplevel)" || exit 2
SNAP_DIR="$MODULE/doc/screenshots"
STAMP="$SNAP_DIR/.baseline"
CONFIG="src/test/playwright/playwright.config.ts"

mode="${1:-}"
[ $# -gt 0 ] && shift

case "$mode" in
    capture)
        if ! git -C "$ROOT" diff --quiet HEAD -- '*.css' '*.css.dsp'; then
            echo "visual-baseline: CSS has uncommitted changes; capture the baseline before editing CSS" >&2
            git -C "$ROOT" diff --name-only HEAD -- '*.css' '*.css.dsp' | sed 's/^/  /' >&2
            exit 2
        fi
        mkdir -p "$SNAP_DIR"
        (cd "$MODULE" && npx playwright test --config "$CONFIG" --update-snapshots=all "$@")
        rc=$?
        printf 'commit %s\ncaptured %s\nargs %s\n' "$(git -C "$ROOT" rev-parse --short HEAD)" \
            "$(date '+%Y-%m-%d %H:%M')" "$*" > "$STAMP"
        exit $rc
        ;;
    compare)
        if [ ! -f "$STAMP" ]; then
            echo "visual-baseline: no local baseline; run 'capture' before editing CSS" >&2
            exit 2
        fi
        sed 's/^/baseline: /' "$STAMP"
        (cd "$MODULE" && npx playwright test --config "$CONFIG" "$@")
        ;;
    *)
        echo "usage: $0 capture|compare [playwright args...]" >&2
        exit 2
        ;;
esac
