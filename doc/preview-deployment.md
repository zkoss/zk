# Deploying the preview app for design review

How to put the Marble preview app on a shared Tomcat so a designer (or anyone
without a dev setup) can browse the theme in a normal browser.

| | |
|---|---|
| Build | `./scripts/build-preview-war.sh` → `target/marble.war` |
| Deploy | `./scripts/deploy-preview.sh` (passwordless — after a one-time `--setup-key`) |
| Result | `http://<host>:<port>/marble/` → redirects to the use-case browser |

## Why a WAR and not the Spring Boot app

Locally the preview app runs as a Spring Boot main class
(`zk.example.ThemePreviewApp`), whose only real jobs are mapping `/**.zul` to a
view name and serving root-level CSS. A servlet container does both natively, so
the WAR contains **no Spring at all** — just the ZK jars, the theme jar and the
preview view-models. `ThemePreviewApp` and the iceblue launcher are deliberately
excluded from the WAR.

## What the WAR looks like

```
marble.war
├── index.html              generated: redirects / to usecase/index.zul
├── *.zul, usecase/, pv/, utility/     the preview pages, served by DHtmlLayoutServlet
└── WEB-INF/
    ├── web.xml             generated: zkLoader (*.zul) + auEngine (/zkau/*)
    ├── zk.xml              generated: org.zkoss.theme.preferred = marble
    ├── classes/
    │   ├── web/**          the SAME preview tree again (see below)
    │   └── zk/example/*    view-models and composers used by the pages
    └── lib/*.jar           ZK jars + marble-<version>.jar
```

The preview tree is staged **twice on purpose**:

- **war root** — `DHtmlLayoutServlet` serves these as `/button.zul`,
  `/usecase/index.zul`, …
- **`WEB-INF/classes/web/`** — everything the pages pull with ZK's `~./` prefix
  (`img/`, `media/`, `usecase/usecase.css`, macro/template ZULs, and the SPA's
  own `~./<page>.zul` navigation) resolves through `ClassWebResource`, which
  reads the **classpath**, not the war root. Drop this copy and the sidebar
  navigation 404s.

Two things the build does on the way out:

- **strips the live-reload `<script>`** from `preview.zul` / `usecase/index.zul`
  (it points at `localhost:50000`, a dev-only server).
- **asserts the theme actually landed**: the staged theme jar must contain the
  compiled `*.css.dsp` files, and `norm.css.dsp` must be comment-free (i.e. the
  minified packaged build, not a dev/watch build). A WAR that lost its CSS still
  answers HTTP 200 on every page and merely looks unstyled — an expensive thing
  to discover on the review server.

### `-Dexec.skip=true` during the build

The build runs `mvn package -Dexec.skip=true` and calls `npm run build:css` +
`scripts/check-icon-coverage.sh` + `scripts/stamp-commit.sh` itself. Reason: the
pom's `watch-css` exec-execution is bound to `process-resources` and is `async`,
so a plain `mvn package` starts `npm run watch`, which rebuilds the theme CSS in
**dev** mode (unminified) on top of the packaged output and leaves a file watcher
behind.

### The build marker in the sidebar

`scripts/stamp-commit.sh` writes the HEAD commit date + short hash to
`target/test-classes/zk/example/commit-stamp.txt`, which the staging step rsyncs
into `WEB-INF/classes/`. `UseCaseVM.getCommitStamp()` reads it back and the
use-case sidebar shows it next to the "Marble" wordmark, so a reviewer's
screenshot can be pinned to a commit. It has to be captured at build time
because the WAR ships without a `.git` directory; with no git repo at all the
script writes nothing and the sidebar simply shows no date.

## Servlet flavour: javax vs jakarta

ZK ships the same release in two servlet flavours under different version
strings (see also the note in `pom.xml`):

| Flavour | `zk.version` | Container |
|---|---|---|
| javax (default for the WAR) | `10.4.0.FL.20260713-Eval` | Tomcat 9 / Servlet 4.0 |
| jakarta (default for dev)   | `10.4.0-jakarta.FL.20260713-Eval` | Tomcat 10+ / Servlet 5+ |

The WAR defaults to **javax**, because the review Tomcat is 9.0.83. Nothing else
changes between the two: neither the theme classes nor the preview view-models
touch a servlet type, so the same sources compile and run against either.

```bash
ZK_VERSION=10.4.0-jakarta.FL.20260713-Eval ./scripts/build-preview-war.sh   # Tomcat 10/11
```

The generated `web.xml` is written for the javax flavour (Servlet 4.0 schema);
switch its namespace/version too if you move to a jakarta container.

## Deploying

```bash
./scripts/deploy-preview.sh --setup-key    # ONCE per machine (see below)
./scripts/deploy-preview.sh                # build + backup + upload + verify + screenshots
./scripts/deploy-preview.sh --no-build     # upload target/marble.war as it is
./scripts/deploy-preview.sh --verify       # only verify + screenshot
```

Target defaults live at the top of the script and are all overridable:

```bash
HOST=zktest@10.1.3.241 PORT=8093 \
  BASE=/home/zktest/servers/support3-apache-tomcat-9.0.83/webapps \
  APP=marble ./scripts/deploy-preview.sh
```

Deploy `APP=marble-rc2` to park a second build next to the first.

### Passwordless login (`--setup-key`)

The deploy is unattended: it sleeps 30s waiting for autoDeploy and then drives
headless Chrome, so a hidden password prompt would just hang it. Login therefore
uses SSH key auth, and the whole run authenticates **once** — every later step
(each `ssh`, the `scp`, the log tail) rides the same `ControlMaster` socket.

Run this once per machine:

```bash
./scripts/deploy-preview.sh --setup-key
```

It uses `~/.ssh/<account>` — `~/.ssh/zktest` for the default host — and appends
the public half to the server's `authorized_keys` via `ssh-copy-id`, generating
the key (ed25519, no passphrase) only if it does not exist yet. The key belongs
to the **server account**, not to this theme, so the same one serves any app
deployed to that Tomcat. **This is the only step that ever asks for the server
password** — it is not in this repo, it is in the internal server notes. Normal
runs then use `BatchMode=yes`, so a missing key fails in under a second with
instructions instead of prompting.

Optional — make even the setup step silent by putting the password in the macOS
Keychain first (`-w` with no value prompts, so it stays out of your shell
history):

```bash
security add-generic-password -a zktest -s zktest-ssh -w -U
```

The script picks that item up through `SSH_ASKPASS`, and also falls back to it if
key auth ever stops working (server rebuilt, `authorized_keys` wiped).

Three things worth knowing:

- The key has **no passphrase**, because nothing can answer one in an unattended
  run. The key file is therefore the credential — as sensitive as the password,
  and it grants shell access as `zktest`. Delete its line from the server's
  `authorized_keys` to revoke.
- `KEY=~/.ssh/other-key ./scripts/deploy-preview.sh` overrides which key is used,
  the same way `HOST`/`PORT`/`BASE`/`APP` do.
- `~/.ssh/zktest` is not one of the filenames ssh offers by default, so scripts
  that just run `ssh zktest@<host>` (e.g. the kkapp deploy script this one was
  modelled on) still get a password prompt. Three lines in `~/.ssh/config` fix
  that for all of them at once, without touching their code:

  ```
  Host 10.1.3.241
      IdentityFile ~/.ssh/zktest
      IdentitiesOnly yes
  ```

After uploading, the script waits 30s for Tomcat's autoDeploy, then:

1. `GET /marble/` plus `usecase/index.zul`, `button.zul`, `grid.zul` — all must be 200.
2. greps the served `zul/css/zk.wcs` bundle for `--zk-color-primary`, so an
   unstyled deploy fails loudly instead of looking fine.
3. screenshots those pages into `tasks/screenshots/deploy/` (git-ignored).
4. tails `catalina.out` for `SEVERE`/`Exception`.

## Verified

Both scripts were validated against a local **Tomcat 9.0.83** with the javax ZK
build before first use: `/marble/`, `usecase/index.zul`, `button.zul`,
`grid.zul` all returned 200, `catalina.out` was clean, the reset stylesheet was
injected ahead of `zk.wcs` by `MarbleThemeProvider`, the served bundle was 1.39 MB
and contained the `--zk-*` tokens, and the rendered screenshots matched the local
dev app.

## Telling the designer what to do with it

Send her `http://<host>:<port>/marble/` and nothing else — the sidebar reaches
every page, and the **Report this page** button at the top of it files a
pre-filled issue for whatever she is looking at. See
[design-review-feedback.md](design-review-feedback.md).

## Known limits

- **`preview.zul`** (the all-components page) lists its sections by scanning the
  classpath `web` directory as a real `File` (`ZulListVM`), so it only works
  while Tomcat keeps `unpackWARs="true"` (the default). The use-case SPA does not
  depend on this.
- The WAR is ~33 MB, mostly the `media/` demo assets, which are staged twice.
- ZK **Eval** jars are used, same as in dev; EE components render but the build
  is not licensed for production use.
