# Batch 17 + ZK-core repro: issue comments (POSTED 2026-10-13, screenshots under `screenshots/batch17/` in the tracker repo)

Local copies of the screenshots are not kept in this repo.

## Part A: fixed in the theme (batch 17)

## #32

# Root cause

With `frozenCols`, the biglistbox renders the frozen pane and the scrollable pane next to each other in the header and in the body. The old theme floated the panes left; Marble lacked those rules, so the scrollable pane wrapped under the frozen one and the header turned into two rows.

# Solution

The panes are floated again, as in the old theme. The frozen and scrollable headers now share one row (vertical offset 0, before 39px), and so do the bodies. Horizontal scrolling still moves only the scrollable columns. A biglistbox without frozen columns is pixel-identical to before.

# Result

![frozenCols 3](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/32-frozen3.png?raw=true)

## #20

# Root cause

ZK marks day 1 of each month panel with the same `selected` class as a real selection (it uses it internally for keyboard navigation). Marble paints that class as a filled circle, so the 1st of each month looked selected.

# Solution

Inside the daterangebox popup, a day carrying that class but not belonging to the range (start, end or in-range) now looks like any other day, including the hover tint. Range start and end, in-range days, and the selected day in a standalone calendar or datebox are unchanged.

# Result

![range 10 to 15](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/20-seeded.png?raw=true)

## #50

# Root cause

In a horizontal navbar, an open dropdown had no vertical position, so it was centred on its own trigger and covered it. Clicking the trigger again hit the dropdown, so the menu could not be closed.

# Solution

The dropdown now opens directly below its trigger (top edge = trigger bottom edge), left-aligned with it. Clicking the trigger again closes it. Dropdown size, vertical navbars and collapsed navbars are unchanged.

Note: clicking outside or pressing Escape does not close an expanded horizontal navbar dropdown in either theme. That is existing ZK behaviour, not part of this fix.

# Result

Open:

![open](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/50-dropdown-open.png?raw=true)

After clicking "Get Started" again:

![closed](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/50-dropdown-closed.png?raw=true)

## #75

# Root cause

For the left and top positions, Marble reserved room for the arrow with a bottom or right margin, but ZK only takes the top and left margins into account when it positions a popup. The 8px arrow therefore landed on the button.

# Solution

The arrow room is now reserved on the side ZK takes into account. Left and top popups keep an 8px gap to the button (before 0), the same as right and bottom. Arrow and popup sizes are unchanged.

On the preview page, the Left button sits too close to the window edge for a 283px popup, so that popup is pushed against the edge. That is the page layout, not the component.

# Result

Left:

![left](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/75-left.png?raw=true)

Top:

![top](https://github.com/hawkchen/marble-issue/blob/main/screenshots/batch17/75-top.png?raw=true)

## Part B: ZK behaviour (reproduced in both themes)

## #9

# Root cause

Reproduced in both themes. Clicking a parent (Japan) and then closing the popup leaves the parent stored as the cascader's selection on the client, while the server receives nothing. That is why the field is empty but shows the clear "x", and why reopening shows Japan selected.

# Solution

This is a ZK widget bug, not a theme issue. Filed as [ZK-6193](https://zkoss.atlassian.net/browse/ZK-6193).

## #44

# Root cause

Reproduced in both themes. When the page sits in a scrolling container (as in the preview app), anchornav scrolls the wrong element, so nothing moves.

# Solution

This is a ZK widget bug, already tracked as [ZK-5941](https://zkoss.atlassian.net/browse/ZK-5941). I added the root cause and a reproducer there.

## #21

# Root cause

The daterangebox has no Save/Apply button by design: a time change is committed as soon as both dates are picked, and while you edit the time. In my tests the time was saved in every case.

# Solution

No change for the missing button. While checking, I found a related bug: Cancel and Escape also commit an unsaved time change. Filed as [ZK-6194](https://zkoss.atlassian.net/browse/ZK-6194).

## #28

# Root cause

Same in both themes. The searchbox keeps the search text when it closes. This is documented ZK behaviour; an application clears it with `setSearchText("")`.

# Solution

No change. If clearing on reopen should become the default, that is a feature request for ZK. Tell me and I will file it.

## #56

# Root cause

Same in both themes. A window in popup mode without a `position` opens where it sits in the page, which on this preview page is below the visible area. The theme sets no position.

# Solution

No theme change. The fix belongs on the preview page: give the window `position="center"`. It is queued with the other preview-page issues.

## #14

# Root cause

Same in both themes. The chosenbox shows a "no result" message only when the page sets `noResultsText`; by default the row is empty. With `creatable` set, the "create" message replaces it, as documented.

# Solution

No theme change. The preview page should set `noResultsText` so the empty state is visible; it is queued with the other preview-page issues. If an empty state should appear by default, that is a ZK feature request.

## #58

# Root cause

Same in both themes. Inside the flyout, the restore arrow is hidden on purpose by ZK. The collapsed bar keeps its own arrow, and clicking it docks the region again, so the region can always be restored.

# Solution

No change. ZK-5687 (redesign of the borderlayout flyout) already covers how the flyout should look.
