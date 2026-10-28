# ZK Source Code Reference

This document explains how to navigate the ZK source code to understand component DOM structures.

---

## ZK Source Location

```
/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/
```

---

## Directory Structure

```
zul/
├── box/           # Box, Hbox, Vbox
├── db/            # Calendar (used by Datebox)
├── grid/          # Grid, Row, Column
├── inp/           # Input components (Textbox, Combobox, etc.)
├── layout/        # BorderLayout, AbsoluteLayout, etc.
├── menu/          # Menu, Menubar, Menuitem
├── mesh/          # Shared mesh components (Paging, Auxhead, Frozen)
├── sel/           # Selection components (Listbox, Tree)
├── tab/           # Tabbox, Tab, Tabpanel
├── wgt/           # Widgets (Button, Checkbox, etc.)
└── wnd/           # Window, Panel
```

---

## Key File Types

### 1. Mold Files (`mold/*.js`)

**Purpose**: HTML template for rendering the component

**Location**: `{category}/mold/{component}.js`

**Example** (`wgt/mold/button.js`):
```javascript
function button$mold$(out) {
    out.push('<button type="', zUtl.encodeXML(this._type), '"', this.domAttrs_());
    if (this._disabled) out.push(' disabled="disabled"');
    out.push('>', this.domContent_(), '</button>');
}
```

**What to look for**:
- HTML tag structure
- CSS class names (from `this.$s('classname')`)
- Attributes and conditions
- Child elements

---

### 2. Component Files (`*.ts`)

**Purpose**: Component logic, properties, and methods

**Location**: `{category}/{Component}.ts`

**Example** (`wgt/Button.ts`):
```typescript
@zk.WrapClass('zul.wgt.Button')
export class Button extends zul.LabelImageWidget<HTMLButtonElement> {
    _orient = 'horizontal';
    _dir = 'normal';
    _type = 'button';
    _disabled?: boolean;

    override domContent_(): string {
        // Returns inner HTML content
    }
}
```

**What to look for**:
- Properties (prefixed with `_`)
- `domContent_()` method for inner content
- `domAttrs_()` for attributes
- State handling methods
- `$s('classname')` calls for CSS class names

---

### 3. LESS Files (`less/*.less`)

**Purpose**: Existing styles (for reference only)

**Location**: `{category}/less/{component}.less`

**Example** (`wgt/less/button.less`):
```less
.z-button {
    // Base styles
    &-image {
        // Image styles
    }
    &:hover { }
    &:focus { }
    &:active { }
    &[disabled] { }
}
```

**What to look for**:
- CSS class naming patterns
- State selectors
- Sub-element naming (using `&-` prefix)

---

## Common Patterns

### CSS Class Naming

ZK uses `this.$s('name')` to generate scoped class names:

```typescript
this.$s('button')     // Returns: "z-button"
this.$s('image')      // Returns: "z-button-image"
this.$s('selected')   // Returns: "z-button-selected"
```

### DOM Content

Components often have a `domContent_()` method that returns the inner HTML:

```typescript
override domContent_(): string {
    var label = zUtl.encodeXML(this.getLabel()),
        img = this.getImage(),
        iconSclass = this.domIcon_();
    // ... build and return HTML string
}
```

### DOM Attributes

The `domAttrs_()` method returns common attributes:

```typescript
// Typically includes: id, class, style, etc.
out.push('<div', this.domAttrs_(), '>');
```

---

## Component Categories

### Widgets (`wgt/`)

| Component | Files |
|-----------|-------|
| Button | `Button.ts`, `mold/button.js` |
| Checkbox | `Checkbox.ts`, `mold/checkbox.js` |
| Radio | `Radio.ts` (extends Checkbox) |
| Toolbarbutton | `Toolbarbutton.ts`, `mold/toolbarbutton.js` |
| Combobutton | `Combobutton.ts`, `mold/combobutton.js` |
| Groupbox | `Groupbox.ts`, `mold/groupbox.js` |
| Popup | `Popup.ts`, `mold/popup.js` |
| Separator | `Separator.ts`, `mold/separator.js` |
| Progressmeter | `Progressmeter.ts`, `mold/progressmeter.js` |

### Inputs (`inp/`)

| Component | Files |
|-----------|-------|
| Textbox | `Textbox.ts`, `mold/textbox.js` |
| Intbox | `Intbox.ts` (extends Textbox) |
| Decimalbox | `Decimalbox.ts` (extends Textbox) |
| Combobox | `Combobox.ts`, `mold/combo.js` |
| Bandbox | `Bandbox.ts`, `mold/combo.js` |
| Datebox | `Datebox.ts`, `mold/combo.js` |
| Timebox | `Timebox.ts`, `mold/combo.js` |
| Spinner | `Spinner.ts`, `mold/spinner.js` |
| Slider | `Slider.ts`, `mold/slider.js` |

### Selection (`sel/`)

| Component | Files |
|-----------|-------|
| Listbox | `Listbox.ts`, `mold/listbox.js` |
| Listitem | `Listitem.ts`, `mold/listitem.js` |
| Listheader | `Listheader.ts`, `mold/listheader.js` |
| Tree | `Tree.ts`, `mold/tree.js` |
| Treeitem | `Treeitem.ts`, `mold/treeitem.js` |

### Grid (`grid/`)

| Component | Files |
|-----------|-------|
| Grid | `Grid.ts`, `mold/grid.js` |
| Row | `Row.ts`, `mold/row.js` |
| Column | `Column.ts`, `mold/column.js` |

### Tabs (`tab/`)

| Component | Files |
|-----------|-------|
| Tabbox | `Tabbox.ts`, `mold/tabbox.js` |
| Tabs | `Tabs.ts`, `mold/tabs.js` |
| Tab | `Tab.ts`, `mold/tab.js` |
| Tabpanel | `Tabpanel.ts`, `mold/tabpanel.js` |

### Menu (`menu/`)

| Component | Files |
|-----------|-------|
| Menubar | `Menubar.ts`, `mold/menubar.js` |
| Menu | `Menu.ts`, `mold/menu.js` |
| Menuitem | `Menuitem.ts`, `mold/menuitem.js` |
| Menupopup | `Menupopup.ts`, `mold/menupopup.js` |

### Window (`wnd/`)

| Component | Files |
|-----------|-------|
| Window | `Window.ts`, `mold/window.js` |
| Panel | `Panel.ts`, `mold/panel.js` |

### Layout (`layout/`)

| Component | Files |
|-----------|-------|
| Borderlayout | `Borderlayout.ts`, `mold/borderlayout.js` |
| North/South/East/West/Center | `LayoutRegion.ts` |

---

## Research Workflow

### Step 1: Find the component category

```bash
# Search for component file
find /Users/hawk/Documents/workspace/ZK10/zk/zul -name "*Button*" -type f
```

### Step 2: Read the mold file

```bash
cat /Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/wgt/mold/button.js
```

### Step 3: Read the component TS file

Look for:
- Properties
- `domContent_()` method
- State handling
- CSS class generation

### Step 4: Check existing LESS for class names

```bash
cat /Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/wgt/less/button.less
```

### Step 5: Document in `03-component-dom-structures.md`

Add the DOM structure, properties, states, and CSS classes.

---

## Tips

1. **Inheritance**: Many components extend base classes. Check parent classes for shared behavior.

2. **Molds**: Some components have multiple molds. Check for different rendering modes.

3. **Sub-components**: Complex components (Listbox, Grid) have many sub-components. Research all related files.

4. **CSS Class Convention**: ZK uses `.z-{component}` as base class, `.z-{component}-{element}` for children.

5. **States**: Common state patterns:
   - `[disabled]` - Disabled attribute
   - `.z-{component}-selected` - Selected state
   - `.z-{component}-hover` - Hover (sometimes via JS)
   - `.z-{component}-focus` - Focus (sometimes via JS)
