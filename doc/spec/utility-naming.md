# Utility Class Naming

Normative rules for naming Marble's `z-*` utility classes. Derived from the full review in
`doc/utility-class-naming-review.md`, which carries the evidence and the rejected alternatives;
this page is the short form to follow when adding a class or a whole category.

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
size silently.

---

## 3. Changing an existing name

Renames are mechanical but the verification is not. `doc/utility-class-naming-review.md` §6
carries the full method; the parts that matter every time:

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

- `doc/utility-class-naming-review.md` — the review this page condenses; evidence, rejected
  options, and the verification method in full
- `doc/spec/spacing-policy.md` — the 4px scale the spacing classes expose
- `doc/spec/responsive-design.md` — the Bootstrap breakpoint values used by R3
