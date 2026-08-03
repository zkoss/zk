#!/bin/bash
# Run the currently open test file in VS Code.
# Usage: run-current-test.sh <workspace-folder> <relative-file>

WS="$1"
RF="$2"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

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

if [[ "$RF" != zktest/src/test/java/*.java ]]; then
    echo "Current file is not under zktest/src/test/java"
    exit 1
fi

CLS="${RF#zktest/src/test/java/}"
CLS="${CLS%.java}"
CLS="${CLS//\//.}"

if grep -Eq '@ForkJVMTestOnly|@Tag\(\s*"ForkJVMTestOnly"' "${WS}/${RF}"; then
    TASK=testGroupForkJVMTestOnly
else
    TASK=test
fi

if [ "$TASK" = "testGroupForkJVMTestOnly" ]; then
    ensure_docker || exit 1
fi

echo -e "${BOLD}Running ${TASK} for ${CLS}${NC}"
if cd "${WS}/zktest" && ./gradlew "${TASK}" --tests "${CLS}" -PmaxParallelForks=1 --console=plain --no-daemon; then
    echo -e "\n${GREEN}${BOLD}PASSED${NC} ${CLS}"
else
    echo -e "\n${RED}${BOLD}FAILED${NC} ${CLS}"
    exit 1
fi
