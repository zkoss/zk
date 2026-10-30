#!/usr/bin/env bash
# Captures iceblue (default ZK theme) baseline screenshots for seed components.
#
# Prerequisites: an IceBlue preview host. It exists only in the separate
# zkThemeTemplate repository (port 8082); zk does not serve one. Start that host
# and point $ICEBLUE_URL at it before running this script.
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
BASE_URL="${ICEBLUE_URL:-http://localhost:8082}"
OUTPUT_DIR="$PROJECT_ROOT/doc/contracts/baselines"

echo "Base URL: $BASE_URL"
echo "Output:   $OUTPUT_DIR"
echo "Components: ${COMPONENTS[*]}"

node "$SCRIPT_DIR/capture-iceblue.js" "$BASE_URL" "$OUTPUT_DIR" "${COMPONENTS[@]}"
