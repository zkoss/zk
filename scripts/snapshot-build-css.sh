#!/bin/bash
#
# Snapshot every output of scripts/build-css.js, so a change to the build can be proven
# byte-identical (a refactor) or limited to the intended files (a feature or fix).
#
# Usage:
#   bash scripts/snapshot-build-css.sh <new-dir> # once before the change, once after
#   diff -r <before-dir> <after-dir>             # a refactor must print nothing
#
# What one snapshot holds:
#   - out/<module>-min, out/<module>-dev: the four modules (zul, zkmax, zkex, za11y), each built
#     minified and with --dev, written through --out so the codegen dirs are not touched;
#   - out/zul-emit + emit/: zul built with --emit-docs, and the two side files it writes into the
#     repo (each repo file is put back as it was, or removed if the run created it);
#   - log-*.txt: stdout/stderr and exit code of every run, plus the two usage errors (exit 2);
#   - manifest.txt: a sha256 of every output file.
#
# Keep this in step with build-css.js: a new flag, module, output or side file there needs a
# matching run or copy here, or the snapshot stops covering it.
#
# Needs node_modules in this repo and the zkcml checkout at ../zkcml (the paths build-css.js uses).
set -u

ZK=$(cd "$(dirname "$0")/.." && pwd)
SNAP=${1:-}
if [ -z "$SNAP" ]; then echo "Usage: $0 <new-dir>"; exit 2; fi
if [ -e "$SNAP" ]; then echo "Snapshot dir already exists: $SNAP"; exit 1; fi
mkdir -p "$SNAP"
SNAP=$(cd "$SNAP" && pwd)
cd "$ZK" || exit 1

# Run build-css.js with the given args, logging output and exit code to $SNAP/log-$1.txt.
run() {
    local label=$1; shift
    local log="$SNAP/log-$label.txt"
    node scripts/build-css.js "$@" > "$log" 2>&1
    echo "exit=$?" >> "$log"
}

for mod in zul zkmax zkex za11y; do
    run "$mod-min" --module "$mod" --out "$SNAP/out/$mod-min"
    run "$mod-dev" --module "$mod" --out "$SNAP/out/$mod-dev" --dev
done

# --emit-docs writes these two files into the repo: save them, run, copy the result out, restore.
DOCS="doc/spec/icon-index.md zkthemelab/src/main/webapp/icons-lucide.zul"
mkdir -p "$SNAP/saved" "$SNAP/emit"
for f in $DOCS; do
    if [ -e "$f" ]; then cp "$f" "$SNAP/saved/$(basename "$f")"; fi
done
run zul-emit --module zul --out "$SNAP/out/zul-emit" --emit-docs
for f in $DOCS; do
    b=$(basename "$f")
    if [ -e "$f" ]; then cp "$f" "$SNAP/emit/$b"; fi
    if [ -e "$SNAP/saved/$b" ]; then cp "$SNAP/saved/$b" "$f"; else rm -f "$f"; fi
done
rm -r "$SNAP/saved"

run usage-none
run usage-bogus --module bogus

(cd "$SNAP/out" && find . -type f | LC_ALL=C sort | while read -r f; do shasum -a 256 "$f"; done) > "$SNAP/manifest.txt"

echo "Snapshot: $SNAP ($(wc -l < "$SNAP/manifest.txt" | tr -d ' ') output files)"
for log in "$SNAP"/log-*.txt; do
    echo "  $(basename "$log" .txt): $(tail -1 "$log")"
done
