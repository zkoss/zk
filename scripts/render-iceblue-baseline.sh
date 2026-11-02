#!/usr/bin/env bash
# Captures iceblue (default ZK theme) baseline screenshots for seed components.
#
# Prerequisites: a running zkthemelab preview (see the Ports section of
# .claude/skills/marble-theme/reference/verification.md). The capture sets the
# zktheme=iceblue cookie, so zkthemelab renders the page in IceBlue; it fails a
# component whose page did not load the IceBlue stylesheet.
#
# SEED below is the first batch of components, whose baselines are already in
# doc/contracts/baselines/. Name other components as arguments to capture them.
#
# Usage:
#   ./scripts/render-iceblue-baseline.sh                        # all 5 seed components
#   ./scripts/render-iceblue-baseline.sh stepbar signature      # specific components
#
# Output: doc/contracts/baselines/<comp>-iceblue.png

set -euo pipefail

SEED=(stepbar signature tbeditor organigram pdfviewer)
COMPONENTS=("${@:-${SEED[@]}}")
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
# @playwright/test lives under zkthemelab/, not at the zk root.
export NODE_PATH="$PROJECT_ROOT/zkthemelab/node_modules${NODE_PATH:+:$NODE_PATH}"
BASE_URL="${PREVIEW_URL:-http://127.0.0.1:8085}"
OUTPUT_DIR="$PROJECT_ROOT/doc/contracts/baselines"

echo "Base URL: $BASE_URL"
echo "Output:   $OUTPUT_DIR"
echo "Components: ${COMPONENTS[*]}"

node "$SCRIPT_DIR/capture-iceblue.js" "$BASE_URL" "$OUTPUT_DIR" "${COMPONENTS[@]}"
