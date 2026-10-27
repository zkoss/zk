#!/usr/bin/env bash
#
# Start the Marble preview app (zkthemelab).
# Page inventory and URL forms: see README.md in this directory.
#
#   Usage:  ./start.sh [port]      # port defaults to 8085
#
# Three things that will bite you if you deviate from this script:
#
# 1. `appRun`, never `appStart`.
#    Under gretty 3.1.1 on Gradle 8.10 the `appStart` client never returns. `appRun` blocks
#    waiting for a keypress on stdin and treats EOF as that keypress, so stdin must stay open:
#    run this from an interactive terminal. `./start.sh < /dev/null`, or a CI runner with no
#    TTY, will start the server and immediately shut it down again.
#    To stop it: press any key in this terminal (closing stdin has the same effect).
#    `./gradlew appStop` does NOT work against an appRun server: it fails with
#    java.net.ConnectException: Connection refused. Under appRun only the statusPort in
#    build/gretty_ports.properties listens -- the servicePort that appStop dials never
#    opens. If you lose the terminal, kill the process instead.
#
# 2. Browse to 127.0.0.1, not localhost.
#    Chrome resolves `localhost` to IPv6 ::1 while gretty binds IPv4, so `localhost` hangs or
#    refuses the connection. The URL echoed below is the one that works.
#
# 3. The first start takes minutes, and there is no live reload.
#    settings.gradle makes this a composite build over the ../../zk and ../../zkcml sources, so
#    the first run compiles the framework itself; a warm restart serves in about 10 s.
#    Nothing watches the CSS. After editing Marble CSS, rebuild and restart:
#        (cd .. && ./gradlew :zul:compileMarbleCss)     # or the matching zkcml task for EE
#    The live-reload <script> tags still present in a few preview pages are inert.
#
set -euo pipefail

PORT="${1:-8085}"

cd "$(dirname "$0")"

echo "Marble preview -> http://127.0.0.1:${PORT}/   (welcome page: index.zul, the link index)"
echo "Press any key in this terminal to stop the server."
echo

exec ./gradlew appRun -PhttpPort="${PORT}" --console=plain
