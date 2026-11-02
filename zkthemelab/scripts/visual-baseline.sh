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
# Pass the same Playwright args to both (e.g. --project=gallery -g "button"). The preview app
# must be running and serving the CSS you want to capture (see verification.md).
# capture refuses to run while CSS in zk, or in ../zkcml when present, has uncommitted or untracked
# changes, so the baseline is the unedited CSS. compare never writes a missing baseline: it fails.
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
        dirty=""
        for repo in "$ROOT" "$ROOT/../zkcml"; do
            [ -d "$repo" ] || continue
            dirty="$dirty$(git -C "$repo" status --porcelain --untracked-files=all -- '*.css' '*.css.dsp' | sed "s|^|  $repo: |")"
        done
        if [ -n "$dirty" ]; then
            echo "visual-baseline: CSS has uncommitted changes; capture the baseline before editing CSS" >&2
            echo "$dirty" >&2
            exit 2
        fi
        mkdir -p "$SNAP_DIR"
        (cd "$MODULE" && npx playwright test --config "$CONFIG" --update-snapshots=all "$@")
        rc=$?
        if [ $rc -eq 0 ]; then
            printf 'commit %s\ncaptured %s\nargs %s\n' "$(git -C "$ROOT" rev-parse --short HEAD)" \
                "$(date '+%Y-%m-%d %H:%M')" "$*" > "$STAMP"
        else
            rm -f "$STAMP"
        fi
        exit $rc
        ;;
    compare)
        if [ ! -f "$STAMP" ]; then
            echo "visual-baseline: no local baseline; run 'capture' before editing CSS" >&2
            exit 2
        fi
        sed 's/^/baseline: /' "$STAMP"
        if [ "$(sed -n 's/^args //p' "$STAMP")" != "$*" ]; then
            echo "visual-baseline: compare args differ from the captured ones; pages outside the capture fail as missing" >&2
        fi
        (cd "$MODULE" && npx playwright test --config "$CONFIG" --update-snapshots=none "$@")
        ;;
    *)
        echo "usage: $0 capture|compare [playwright args...]" >&2
        exit 2
        ;;
esac
