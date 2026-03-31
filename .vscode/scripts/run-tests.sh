#!/bin/bash
# Run multiple ZK test classes sequentially, stop on first failure.
# Usage: run-tests.sh <workspace-folder> <test-list>

WS="$1"
RAW="$2"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

if [ -z "$(echo "$RAW" | tr -d '[:space:]')" ]; then
    echo "No tests provided"
    exit 1
fi

ensure_docker(){
    if docker info >/dev/null 2>&1; then return 0; fi
    if ! command -v docker >/dev/null 2>&1; then echo "[ERROR] docker not found"; return 1; fi
    echo "[INFO] Starting Docker Desktop..."
    command -v open >/dev/null 2>&1 && open -ga Docker >/dev/null 2>&1
    for i in $(seq 1 60); do
        docker info >/dev/null 2>&1 && echo "[INFO] Docker ready." && return 0
        sleep 2
    done
    echo "[ERROR] Docker unavailable"; return 1
}

# Parse: split on whitespace, strip trailing method()/method name, dedup
CLASSES=$(printf '%s\n' $RAW \
    | sed 's/(.*)$//' \
    | sed 's/\.[a-z][a-zA-Z0-9_]*$//' \
    | grep -v '^$' \
    | awk '!seen[$0]++')

if [ -z "$CLASSES" ]; then
    echo "No valid tests found"
    exit 1
fi

# Build indexed arrays and validate all files upfront
CLS_ARR=()
TASK_ARR=()
NEEDS_DOCKER=0
while IFS= read -r cls; do
    rf="zktest/src/test/java/$(echo "$cls" | tr '.' '/').java"
    if [ ! -f "${WS}/${rf}" ]; then
        echo "[ERROR] File not found: ${rf}"
        exit 1
    fi
    if grep -Eq '@ForkJVMTestOnly|@Tag\(\s*"ForkJVMTestOnly"' "${WS}/${rf}"; then
        TASK_ARR+=("testGroupForkJVMTestOnly")
        NEEDS_DOCKER=1
    else
        TASK_ARR+=("test")
    fi
    CLS_ARR+=("$cls")
done <<< "$CLASSES"

TOTAL=${#CLS_ARR[@]}

if [ $NEEDS_DOCKER -eq 1 ]; then
    ensure_docker || exit 1
fi

echo -e "${BOLD}========================================${NC}"
echo -e "${BOLD}  Found $TOTAL test class(es) to run${NC}"
echo -e "${BOLD}========================================${NC}"

PASSED=0
for idx in $(seq 0 $((TOTAL - 1))); do
    cls="${CLS_ARR[$idx]}"
    task="${TASK_ARR[$idx]}"
    num=$((idx + 1))

    echo ""
    echo -e "${BOLD}----------------------------------------${NC}"
    echo -e "  ${BOLD}[$num/$TOTAL]${NC} $cls"
    echo -e "${BOLD}----------------------------------------${NC}"

    if ! (cd "${WS}/zktest" && ./gradlew "${task}" --tests "${cls}" \
        -PmaxParallelForks=1 --console=plain --no-daemon); then
        echo ""
        echo -e "${RED}${BOLD}========================================${NC}"
        echo -e "${RED}${BOLD}  FAILED at [$num/$TOTAL]: $cls${NC}"
        echo -e "${RED}${BOLD}  Passed: $PASSED / $TOTAL${NC}"
        echo -e "${RED}${BOLD}========================================${NC}"
        exit 1
    fi

    PASSED=$((PASSED + 1))
    echo -e "  ${GREEN}${BOLD}[$num/$TOTAL] PASS${NC} $cls"
done

echo ""
echo -e "${GREEN}${BOLD}========================================${NC}"
echo -e "${GREEN}${BOLD}  All tests passed! ($TOTAL/$TOTAL)${NC}"
echo -e "${GREEN}${BOLD}========================================${NC}"
