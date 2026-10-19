import { test, expect } from '@playwright/test';

// Render smoke test: every preview page must return HTTP 200 and compose without
// error. xmllint only proves a .zul is well-formed XML — it does NOT catch ZK
// semantic errors such as an unsupported mold or an attribute with no setter,
// which surface only at compose time as an HTTP 500.
//
// Every listed page shares the `.z-p-8` page wrapper, which we assert is visible.
// (`preview.zul` is intentionally excluded — it's a human-only SPA listing page
// whose live-reload script keeps the network busy so `networkidle` never settles.)
//
// Requires the preview app running on ${PREVIEW_URL}
//   cd zkpreview && ./start.sh

const PAGES = [
  '/a.zul',
  '/absolutelayout.zul',
  '/anchorlayout.zul',
  '/anchornav.zul',
  '/area.zul',
  '/audio.zul',
  '/avatar.zul',
  '/badge.zul',
  '/bandbox.zul',
  '/barcode.zul',
  '/barcodescanner.zul',
  '/biglistbox.zul',
  '/borderlayout.zul',
  '/breadcrumb.zul',
  '/button.zul',
  '/calendar.zul',
  '/camera.zul',
  '/captcha.zul',
  '/caption.zul',
  '/cardlayout.zul',
  '/carousel.zul',
  '/cascader.zul',
  '/checkbox.zul',
  '/chip.zul',
  '/chosenbox.zul',
  '/coachmark.zul',
  '/codeeditor.zul',
  '/colorbox.zul',
  '/columnlayout.zul',
  '/combobox.zul',
  '/combobutton.zul',
  '/confirmpopup.zul',
  '/cropper.zul',
  '/datebox.zul',
  // daterangebox: the "Invalid" State-Matrix cell renders UNSTYLED until ZK-6133
  // (https://zkoss.atlassian.net/browse/ZK-6133) is fixed — daterangebox drops the user
  // `sclass`, so `.z-daterangebox-invalid` can't be applied declaratively. Any future test
  // that asserts the daterangebox invalid state will only pass once ZK-6133 lands.
  '/daterangebox.zul',
  '/decimalbox.zul',
  '/dnd.zul',
  '/doublebox.zul',
  '/doublespinner.zul',
  '/drawer.zul',
  '/dropupload.zul',
  '/errorbox.zul',
  '/runtime-error.zul',
  '/fileupload.zul',
  '/fisheyebar.zul',
  '/goldenlayout.zul',
  '/grid-detail.zul',
  '/grid-grouping.zul',
  '/grid-header.zul',
  '/grid-livegrouping.zul',
  '/grid-paging.zul',
  '/grid.zul',
  '/groupbox.zul',
  '/hlayout.zul',
  '/html.zul',
  '/iframe.zul',
  '/imagemap.zul',
  '/inputgroup.zul',
  '/inputs.zul',
  '/intbox.zul',
  '/label.zul',
  '/linelayout.zul',
  '/listbox-grouping.zul',
  '/listbox-header.zul',
  '/listbox.zul',
  '/loading.zul',
  '/loadingbar.zul',
  '/longbox.zul',
  '/menubar.zul',
  '/messagebox.zul',
  '/multislider.zul',
  '/navbar.zul',
  '/notification.zul',
  '/organigram.zul',
  '/overview.zul',
  '/paging.zul',
  '/panel.zul',
  '/pdfviewer.zul',
  '/popup.zul',
  '/portallayout.zul',
  '/progressmeter.zul',
  '/radiogroup.zul',
  '/rangeslider.zul',
  '/rating.zul',
  '/responsive-grid.zul',
  '/rowlayout.zul',
  '/scrollbar.zul',
  '/scrollview.zul',
  '/searchbox.zul',
  '/selectbox.zul',
  '/separator.zul',
  '/signature.zul',
  '/skeleton.zul',
  '/slider.zul',
  '/space.zul',
  '/spinner.zul',
  '/splitlayout.zul',
  '/splitter.zul',
  '/stepbar.zul',
  '/tabbox-misc.zul',
  '/tabbox.zul',
  '/tablelayout.zul',
  '/tbeditor.zul',
  '/textbox.zul',
  '/timebox.zul',
  '/timepicker.zul',
  '/toast.zul',
  '/toolbar.zul',
  '/tree-header.zul',
  '/tree.zul',
  '/utility/grid-layout.zul',
  '/utility/interactions.zul',
  '/utility/opacity.zul',
  '/utility/print.zul',
  '/utility/visibility.zul',
  '/utility/zindex.zul',
  '/video.zul',
  '/vlayout.zul',
  '/window.zul',
];

test.describe('render smoke — all preview pages', () => {
  for (const path of PAGES) {
    test(`renders ${path}`, async ({ page }) => {
      const resp = await page.goto(path, { waitUntil: 'networkidle' });

      expect(resp, `no response for ${path}`).toBeTruthy();
      expect(
        resp!.status(),
        `${path} returned HTTP ${resp!.status()} — page failed to compile`
      ).toBe(200);

      await expect(
        page.locator('.z-p-8').first(),
        `${path} did not render the .z-p-8 page wrapper`
      ).toBeVisible();
    });
  }
});
