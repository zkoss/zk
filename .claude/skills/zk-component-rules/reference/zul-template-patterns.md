# ZUL template patterns

`<apply templateURI="...">` is ZK 10's shadow element for including a template into a page. Used heavily in preview pages and theme harnesses.

## How `<apply templateURI>` works

```xml
<apply templateURI="~./pv/matrix.zul"
       title="States"
       cols="${['Default','Disabled','Readonly']}"
       rowsTemplate="my-rows">
    <template name="my-rows">
        <div sclass="pv-row">...</div>
    </template>
</apply>
```

- The referenced ZUL file is loaded and rendered as a shadow element.
- Attributes on `<apply>` (`title`, `cols`, `rowsTemplate`) become `arg.*` EL expressions inside the template.
- `<template name="...">` blocks defined inside the `<apply>` block are scoped to it — they do not leak to sibling `<apply>` blocks.

## Content partial pattern

To reuse the body of a component preview across multiple pages (individual page + overview page), extract the matrix sections into `pv/{component}-content.zul`:

```xml
<!-- pv/textbox-content.zul -->
<zk>
<apply templateURI="~./pv/matrix.zul" title="States" ...>
    <template name="textbox-rows">...</template>
</apply>
</zk>
```

Both the standalone page and the overview page apply this partial:

```xml
<!-- textbox.zul AND inputs.zul -->
<apply templateURI="~./pv/textbox-content.zul"/>
```

### Rules for content partials

1. **Root element**: wrap in `<zk>` for XML validity. ZK's `<zk>` renders as nothing (transparent container).
2. **No `<?page?>` directive**: partials are not pages.
3. **No `<h:link>` or `<div sclass="z-p-8">` wrapper**: those belong to the wrapping page.
4. **No `<zscript>` for data**: keep `<zscript>` in the wrapping page. Shadow elements have their own composition scope — `<zscript>` inside an `<apply>` template behaves unpredictably across ZK versions. Define `${sampleDate}` etc. once at the top of each page that consumes the partial.
5. **Template names**: scoped to their `<apply>` block. Name collisions across partials are safe.

## Template name resolution

Inside `pv/matrix.zul`, `${arg.rowsTemplate}` resolves to the string `"my-rows"`, and `<apply template="${arg.rowsTemplate}"/>` finds the `<template name="my-rows">` defined in the calling `<apply>` block. This is how `matrix.zul` stays generic while letting callers inject their rows.

## `arg.*` EL context

Attributes set on the calling `<apply>` are available as `arg.<name>` inside the template. Examples:

- `cols="${[...]}"` → `${arg.cols}`
- `rowsTemplate="x"` → `${arg.rowsTemplate}`
- `colsSuffix="-wide"` → `${arg.colsSuffix}` (empty string when not provided, NOT `null` — but interpolation works either way)

## Sibling matrix template (`pv/matrix.zul`)

A shared template used by all preview pages. It renders:
- A section title
- A column-header row
- Rows via the caller-supplied `rowsTemplate`

Calling pages opt into wider columns by passing `colsSuffix="-wide"` (which appends to the CSS class `pv-cols-{N}{-wide}`).

## Preview page best practices

- One section per matrix (States, Multiline, Variants, etc.).
- Use the matrix template for grid-shaped state tables.
- Use freeform `<div sclass="z-mb-6">` blocks for variants that don't fit a grid (e.g. comboitem variants, button colors).
- Centralise data setup (sample dates, models) in the wrapping page's `<zscript>`, not in partials.
