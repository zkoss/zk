# Utility Class Naming

Normative rules for naming Marble's `z-*` utility classes. The review this page was derived from
is kept locally under `tasks/` and is not in the repository; its durable content (the rules, the
text-size scale, and the rename verification method) is carried in this page. The review's
evidence and the alternatives it compared are not kept in the repository. Follow this page when
adding a class or a whole category.

Marble ships **423 authorable utility classes**. They are Marble's public vocabulary from ZK
11.0 onward, and `sclass` is a plain string with no compiler behind it — once released, a rename
is a breaking change in customer markup that no deprecation cycle can cheaply cover. Get the
name right the first time.

---

## 1. The house style

**Bootstrap 5 for the property vocabulary. Tailwind only where its form is both shorter *and*
less ambiguous.**

That is the rule the existing set already follows, and most past defects were places where it was
applied inconsistently rather than places where it was wrong.

| Marble follows Bootstrap | Marble follows Tailwind |
|---|---|
| `z-d-flex`, `z-d-none` (display) | `z-justify-center` (not `justify-content-center`) |
| `z-m-1`, `z-px-2`, `z-mx-auto` (spacing) | `z-items-center` (not `align-items-center`) |
| `z-position-absolute`, `z-top-0`, `z-float-start` | `z-grow-0`, `z-shrink-0` (not `flex-grow-0`) |
| `z-fw-bold`, `z-fst-italic`, `z-lh-sm` | `z-gap-x-4`, `z-gap-y-2` (not `col-gap`/`row-gap`) |
| `z-border-top`, `z-rounded-lg`, `z-w-100` | `z-flex-col`, `z-grid-cols-3`, `z-text-xs` |
| `z-hstack`, `z-vstack`, `z-visually-hidden` | `z-min-w-0`, `z-flex-1` |

Marble deliberately diverges from both for `z-elevation-0…3`: Bootstrap and Tailwind say
`shadow-*`, but Marble is an MD3 theme, the tokens are `--zk-elevation-*`, and consistency with
the token layer directly beneath four classes beats cross-framework familiarity.

---

## 2. The seven rules

### R1 — Keep the hyphen between property and value

`z-m-1`, never `z-m1`. Bootstrap and Tailwind both write `m-1`; Marble's name is exactly that
plus the mandatory `z-` prefix, and that recognition is the most valuable property the spacing
scale has. The frameworks that dropped the hyphen (Basscss, Tachyons) are unmaintained.

It also would not stay consistent — `z-m1` is fine but `z-mauto` is not — and the generated
manifest parses names as `prefix-property-value`.

### R2 — One rule, one name. No aliases.

Two spellings for one behaviour means two things to document, two to complete, and two to keep
in sync. `z-sr-only` and `z-text-muted` were removed for exactly this.

The same reasoning renamed `z-fw-regular` to **`z-fw-normal`**. The CSS keyword is `normal`,
Bootstrap writes `fw-normal`, and the sibling `z-fst-normal` already uses `normal`. One family
should not say `regular` and `normal` for the same idea. `z-fw-regular` is no longer a valid name.

### R3 — A trailing `-xs/-sm/-md/-lg/-xl` always means a size

Breakpoints go in the **infix** slot: `z-d-md-none`, `z-cq-lg-flex` — matching Bootstrap's
`d-md-none` and sitting where print already sits (`z-d-print-none`).

This is not cosmetic. While breakpoints were a suffix, the same four tokens meant two unrelated
things and nothing in the name said which:

```
z-d-block-md     display:block at >= 768px     (a breakpoint)
z-hstack-md      gap: 16px, at every width     (a size)
z-grid-fill-md   min column width 200px        (a size)
```

It also deadlocked the vocabulary: `z-hstack-md` was taken, so "hstack at md and up" had no name
available. With breakpoints in the infix slot, the suffix is free and unambiguous.

### R4 — Logical properties, never physical

`start`/`end`, never `left`/`right`: `z-ms-2`, `z-border-start`, `z-float-end`, `z-text-start`.
Marble is RTL-capable and the physical names silently break it.

### R5 — A utility never takes a bare noun that could name a ZK component

Every ZK widget renders `z-<widgetname>`. A utility named like a widget will one day collide with
the real thing, and because the two definitions sit in different cascade layers, one silently
overrides the other rather than failing loudly.

`z-card` was renamed to `z-paper` (MUI's name for the elevated-surface primitive) for this
reason: a Card component is a normal thing for an MD3-aligned framework to add. `z-paper`,
`z-hstack`, `z-container` and `z-clearfix` are safe because no framework would name a widget
after them.

Utilities and components share the `z-` prefix deliberately — a separate `zu-` prefix was
considered and rejected, because it costs the Bootstrap/Tailwind recognition that makes `z-m-1`
readable at a glance. The shared namespace is managed by this rule instead.

### R6 — Prefer the accurate name over the short one when they conflict

`z-visually-hidden` beats `z-sr-only`: the clip-rect rule leaves content in the accessibility
tree, so it is still announced, and "sr-only" understates who can reach it. `z-hidden` is worse
still — it collides with `display:none`, which Marble already spells `z-d-none`, and would claim
a removal that never happened.

`z-bg-surface-container-low` beats `z-bg-surface-low` for the same reason: the short form read
as a variant of `z-bg-surface` when it actually maps to `--zk-color-surface-container-low`.

Length is not the enemy. A name that describes a different behaviour from the one it implements
is.

### R7 — A size scale is monotone, with no borrowed English words

`xs sm md lg xl 2xl 3xl …`, each step guessable from its neighbours. The font-size scale
previously carried both `md` (13px) and `base` (14px); `base` means "the default size" in
Tailwind while Marble's default body was the 13px step, so the obvious reach gave the wrong
size silently. The scale now has no `base` step. Its current px values, as listed in the header
comment of `zul/src/main/resources/web/zul/css/utility/_typography.css`:

| class | px | MD3 typescale role(s) |
|---|---|---|
| `z-text-xs` | 11 | label-small |
| `z-text-sm` | 12 | body-small, label-medium |
| `z-text-md` | 13 | body-medium (default body) |
| `z-text-lg` | 14 | title-small, label-large |
| `z-text-xl` | 16 | title-medium, body-large |
| `z-text-2xl` | 22 | title-large |
| `z-text-3xl` | 24 | headline-small |
| `z-text-4xl` | 28 | headline-medium |
| `z-text-5xl` | 32 | headline-large |
| `z-text-6xl` | 36 | display-small |
| `z-text-7xl` | 45 | display-medium |
| `z-text-8xl` | 57 | display-large |

---

## 3. Changing an existing name

Renames are mechanical but the verification is not. The method below is the part that matters
every time; the original review it came from is kept locally under `tasks/`, not in the repository.

- **Anchor the sweep** on `(?<![\w-])name(?![\w-])`. `sclass` is space-separated, and an
  unanchored match rewrites `z-text-xl` inside `z-text-2xl` — and `z-cardlayout` inside `z-card`.
- **Sweep every file type**, verified by a repo-wide grep with no extension filter. Baselines
  live in `.json` (`zkpreview/doc/focus-ring-known-clips.json` keys on a string embedding a whole
  class list), and generated files need their *generator* changed too.
- **Ask whether the change is a rename or a shift.** A rename removes the old name, so a missed
  call site becomes an orphan and the coverage guard fires. A *shift within a closed set* — the
  font-size renumber — leaves the old name valid with a new meaning, and no orphan guard can see
  it. Those need the two-phase sweep through temporaries plus the font-size equivalence oracle
  (`zkpreview/src/test/playwright/font-size-equivalence.spec.ts`, project `font-size`).
- **Screenshots are not the gate.** `screenshot.spec.ts` compares state crops at
  `maxDiffPixels: 20`; a one-step font change on a short label fits inside that floor.

### 3.1 Sweep mechanics (every rename)

- Within a single pass, replace the longest name first, or route through temporaries (3.2).
- Exclude `../zkcml` entirely, and `zkpreview/build/`, because build output regenerates.
- Exclude this page from the sweep. It records old → new pairs, and a sweep would rewrite the
  "before" column.
- **A removal is not a rename.** Rewriting the call sites of a deleted class to its survivor is
  correct in component markup but wrong in a gallery page, where it can produce two identical
  demo rows or a caption naming the same class twice. After any removal, grep the gallery pages
  for duplicated demo labels.
- Generated files need their **generator** changed too, or the next run reinstates the old name.
  For example, `scripts/build-css.js` emits `icons-lucide.zul`, and its template held
  `z-align-center`.
- If a file also holds someone else's uncommitted changes, sweep it for correctness but stage only
  your own hunks.
- Screenshot specs of the "visual review" kind (for example `forced-colors-gallery.spec.ts`)
  rewrite their baseline PNGs on every run. Revert those images before staging.

### 3.2 Shifts within a closed set (for example the text-size renumber)

A shift keeps the old name and gives it a new value, so the orphan check cannot see a missed call
site. Apply it as follows:

1. **Two-phase sweep through temporaries.** Phase 1 moves every old name to a temporary name keyed
   by its current px value. Phase 2 moves each temporary to the final name for that px value.
   Each token is rewritten exactly once, so a chained sweep cannot walk a class up the scale
   several times.

   ```
   phase 1:  z-text-{xs,sm,md,base,lg,xl,2xl…7xl}  →  z-tmptext-{11,12,13,14,16,22,24,28,32,36,45,57}
   phase 2:  z-tmptext-{px}                        →  the final name for that px value
   ```

2. **Gate 1 (after phase 1).** Run `grep -rE 'z-text-(xs|sm|md|base|lg|xl|[2-7]xl)\b'` over
   `**/*.zul`, `**/*.ts`, `**/*.java` and `doc/**/*.md`. It must return zero hits. Any hit is a
   missed call site and is named and located before phase 2.
3. **Gate 2 (computed font-size equivalence).** Before any edit, capture the baseline with
   `UPDATE_FONT_BASELINE=1 npx playwright test --project=font-size`. The spec
   `zkpreview/src/test/playwright/font-size-equivalence.spec.ts` records a computed font-size
   histogram per preview page into `zkpreview/doc/font-size-baseline.json`. After both phases,
   run the same project without the env var. It must show an empty diff. A non-empty diff names
   the page and the buckets.
4. **Gate 3 (manifest purity).** Dump the manifest before the edit, apply the intended rename map
   to it mechanically, and diff the result against the manifest generated after the edit. It must
   be identical. This shows that no declaration, `var()` reference or media prelude changed.

Gates 1 to 3 apply to a shift. Gate 4 applies to every rename: the manifest class count matches the
expected number, every manifest entry resolves in the built `norm.css.dsp`, and the full Playwright
suite is clean. If failures already existed before the change, the failing set must be the same
before and after the change.

---

## 4. Where the classes live

| File | Category |
|---|---|
| `zul/src/main/resources/web/zul/css/utility/_spacing.css` | margin / padding, 4px scale |
| `…/_layout.css` | display, responsive, container queries, flex, grid, gap, sizing, overflow, position |
| `…/_typography.css` | font size, weight, style, line height, alignment, transform |
| `…/_colors.css` | text and background colour roles |
| `…/_borders.css` | border and corner radius |
| `…/_elevation.css` | elevation levels |
| `…/_stack.css` | `z-hstack` / `z-vstack` |
| `…/_surface.css` | `z-paper` |
| `…/_print.css` | `z-d-print-*` and the print reset |

The catalogue is generated at build time to
`zul/codegen/resources/web/zul/css/utility-classes.json` and ships inside `zul.jar`. See
`doc/utility-class-discovery.md`.

---

## 5. Related

- The original review this page was derived from — kept locally under `tasks/`, not in the
  repository. Its durable content is carried in this page; its evidence is not kept in the
  repository.
- `doc/spec/spacing-policy.md` — the 4px scale the spacing classes expose
- `doc/spec/responsive-design.md` — the Bootstrap breakpoint values used by R3
