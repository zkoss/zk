# fileupload

`Fileupload` (`<fileupload>`, `zul.wgt.Fileupload`, Java `org.zkoss.zul.Fileupload`) — a button-style trigger that opens an upload dialog.

## Button subclass

The component registry (`zul/src/main/resources/metainfo/zk/lang.xml`) declares `<fileupload>` with `<extends>button</extends>` and no `<css-uri>`. The component keeps the `z-button` zclass, so the trigger is styled by the button CSS (`css/button.css.dsp`). The `fud/fileupload.css` file, bundled by `scripts/build-css.js`, styles the file rows of the upload dialog, not the button.

## No `multiple` property

Verified from source: `Fileupload.java` defines no `multiple` property or setter. Its Javadoc mentions multiple-file upload only in the dialog methods.

## Multi-file upload

Multi-file upload is set through the `upload` option string on the button, for example `upload="true,multiple=true"`. The option string is documented in `zk/src/main/java/org/zkoss/zk/ui/ext/Uploadable.java`. The client parses the `multiple=` option in `zul/src/main/resources/web/js/zul/Upload.ts`.

## DOM

The rendered DOM and state classes are not described in this file.
