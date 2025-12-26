:root {
    /* Font Sizes */
    /*--baseFontSize: 16px;*/
    --fontSizeXLarge: round(up, calc(var(--baseFontSize) * 1.25), 1px);
    --fontSizeLarge: round(down, calc(var(--baseFontSize) * 1.125), 1px);
    --fontSizeMedium: round(up, var(--baseFontSize), 1px);
    --fontSizeSmall: round(down, calc(var(--baseFontSize) * 0.875), 1px);
    --fontSizeXSmall: round(up, calc(var(--baseFontSize) * 0.75), 1px);

    /* Component Basic Coloring */
    /* Font color */
    --textColorDefault: rgba(0, 0, 0, 0.9);
    --textColorLight: rgba(0, 0, 0, 0.57);
    --textColorLighter: rgba(0, 0, 0, 0.34);
    --textColorDefault3: #FFFFFF;
    --textColorActive: #0093F9;

    --colorPrimary: #0093F9;
    --colorPrimaryDark: #0064ED;
    --colorPrimaryLight: hsl(from var(--colorPrimary) h s calc(l + 25));
    --colorPrimaryLighter: hsl(from var(--colorPrimary) h s calc(l + 45)); /* List hover Bg */
    --colorAccent: #FFA516;
    --colorAccent2: #FF4051;
    --colorAccent3: #261429; /* Tooltip Bg */
    --colorBackground1: #F9FCFF; /* Window Bg, Panel Bg */
    --colorBackground3: #FFFFRFF; /* Container Bg */
    --colorGreyDark: #A8A8A8;
    --colorGreyLight: #D9D9D9; /* Btn disabled Bg */
    --colorGreyLighter: #F2F2F2; /* Field disabled Bg */
}

.z-rangeslider {
    --rangesliderButtonColor: var(--colorPrimary);
    --rangesliderDisabledButtonColor: hsl(from var(--rangesliderButtonColor) h s calc(l + 30));
}

.z-multislider {
    --multisliderButtonColor: var(--colorPrimary);
    --multisliderButtonColor2: var(--colorPrimaryLight);
    --multisliderButtonHoverColor2: hsl(from var(--multisliderButtonColor2) h s calc(l + 15));
    --multisliderButtonActiveColor2: hsl(from var(--multisliderButtonColor2) h s calc(l - 20));
    --multisliderDisabledButtonColor: hsl(from var(--multisliderButtonColor) h s calc(l + 30));
    --multisliderDisabledButtonColor2: hsl(from var(--multisliderButtonColor) h s calc(l + 20));
}