/*
 * "Report this page" -> a prefilled issue on the private design-review tracker.
 * Deep link only: no server round trip, no storage, nothing to keep in sync but
 * this one URL. See doc/design-review-feedback.md.
 *
 * Loaded by usecase/index.zul, whose sidebar button calls marbleReportIssue()
 * through a client-side w:onClick handler - so this has to define a global.
 */
var MARBLE_TRACKER = 'https://github.com/hawkchen/marble-issue/issues/new';

function marbleReportIssue() {
    // The SPA keeps the page it shows in the bookmark, so the hash IS the page id.
    // Empty hash = the landing page (UseCaseVM.DEFAULT_PAGE).
    var page = (location.hash || '').replace(/^#/, '') || 'usecase/inventory-table';
    var body = [
        '## What looks wrong', '',
        '<!-- describe it here, and drag a screenshot straight into this box -->', '',
        '## What it should look like', '', '',
        '## How much does it matter', '',
        'Blocker / Should fix / Polish / Idea  (delete the rest)', '',
        '---', '',
        '| | |', '|---|---|',
        '| Page | `' + page + '` |',
        '| URL | ' + location.href + ' |',
        '| Viewport | ' + window.innerWidth + ' x ' + window.innerHeight
            + ' @' + (window.devicePixelRatio || 1) + 'x |',
        '| Density | ' + (document.documentElement.getAttribute('data-density') || 'comfortable') + ' |',
        '| Browser | ' + navigator.userAgent + ' |'
    ].join('\n');
    window.open(MARBLE_TRACKER
        + '?labels=design-feedback'
        + '&title=' + encodeURIComponent('[design] ' + page)
        + '&body=' + encodeURIComponent(body), '_blank', 'noopener');
}
