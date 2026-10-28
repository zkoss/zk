#!/usr/bin/env bash
# Compute a contract's `js-source-hash` from the ZK jars ACTUALLY ON THE CLASSPATH.
#
# In zk the checkout IS the widget being styled — the jars in ~/.m2 are simply built
# from it, not an independent reference. So the hash is taken directly over the widget
# source files under the checkout's four widget-source roots (zul, zk, zkmax, zkex)
# rather than recomputed from a jar on the classpath.
#
# Usage:
#   scripts/js-source-hash.sh zul/code/Codeeditor.ts zul/code/mold/codeeditor.js
#
# Arguments are the contract's `js-source-files:` entries verbatim: paths relative to
# `web/js/` inside the jar. Files are concatenated in the order given (so the contract's
# list order is part of the hash) and sha256'd — the same shape as the old recipe, only
# reading from the resolved artefact.
#
# stdout: the bare hash (safe to capture)
# stderr: provenance — which jar each file came from
#
# Exit codes (mirrors check-css-dsp.js so callers can treat them the same way):
#   0  hash computed
#   2  cannot verify on this machine (a `zkmax/` or `zkex/` entry while `../zkcml` is absent) —
#      callers should TOLERATE this, never treat it as drift
#   1  a declared file is in none of the jars — a real contract error

set -euo pipefail

if [ "$#" -eq 0 ]; then
  echo "usage: $0 <path-under-web/js> [more...]" >&2
  exit 1
fi

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Every checkout root that can hold widget JS under web/js/. A widget lives in exactly
# one of them, so search them all rather than making the caller name the module.
roots=(
  "zul/src/main/resources/web/js"
  "zk/src/main/resources/web/js"
  "../zkcml/zkmax/src/main/resources/web/js"
  "../zkcml/zkex/src/main/resources/web/js"
)

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

concat="$work/concat"
: > "$concat"

for rel in "$@"; do
  found=""
  for root in "${roots[@]}"; do
    if [ -f "$repo_root/$root/$rel" ]; then
      cat "$repo_root/$root/$rel" >> "$concat"
      echo "  ${rel}  <-  ${root}" >&2
      found="1"
      break
    fi
  done
  if [ -z "$found" ]; then
    if [[ "$rel" == zkmax/* || "$rel" == zkex/* ]] && [ ! -d "$repo_root/../zkcml" ]; then
      echo "js-source-hash: '${rel}' needs ../zkcml, which is not checked out — cannot verify" >&2
      exit 2
    fi
    echo "js-source-hash: '${rel}' is under none of the 4 widget-source roots" >&2
    exit 1
  fi
done

shasum -a 256 "$concat" | cut -d' ' -f1
