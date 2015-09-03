/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The UNBESCAPE team (http://www.unbescape.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.zkoss.lang;

/**
 * <p>
 *   Types of escape operations to be performed on JavaScript text:
 * </p>
 *
 * <ul>
 *     <li><tt><strong>SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA</strong></tt>: Use
 *         Single Escape Chars whenever possible (depending on the specified
 *         {@link org.unbescape.javascript.JavaScriptEscapeLevel}). For escaped characters that do
 *         not have an associated SEC, default to using <tt>&#92;xFF</tt> Hexadecimal Escapes
 *         if possible (characters &lt;= <tt>U+00FF</tt>), then default to <tt>&#92;uFFFF</tt>
 *         Hexadecimal Escapes. This type of escape <u>produces the smallest escaped string
 *         possible</u>.</li>
 *     <li><tt><strong>SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA</strong></tt>: Use
 *         Single Escape Chars whenever possible (depending on the specified
 *         {@link org.unbescape.javascript.JavaScriptEscapeLevel}). For escaped characters that do
 *         not have an associated SEC, default to using <tt>&#92;uFFFF</tt> Hexadecimal Escapes.</li>
 *     <li><tt><strong>XHEXA_DEFAULT_TO_UHEXA</strong></tt>: Replace escaped characters with
 *          <tt>&#92;xFF</tt> Hexadecimal Escapes if possible (characters &lt;= <tt>U+00FF</tt>),
 *          default to <tt>&#92;uFFFF</tt> Hexadecimal Escapes.</li>
 *     <li><tt><strong>UHEXA</strong></tt>: Replace escaped characters with
 *         <tt>&#92;uFFFF</tt> Hexadecimal Escapes.</li>
 * </ul>
 *
 * <p>
 *   For further information, see the <em>Glossary</em> and the <em>References</em> sections at the
 *   documentation for the {@link org.unbescape.javascript.JavaScriptEscape} class.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
/**package*/ enum JavaScriptEscapeType {

    /**
     * Use Single Escape Chars if possible, default to &#92;xFF hexadecimal escapes
     * if possible (characters &lt;= U+FF), then default to &#92;uFFFF hexadecimal escapes.
     */
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA(true, true),

    /**
     * Use Single Escape Chars if possible, default to &#92;uFFFF hexadecimal escapes.
     */
    SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA(true, false),

    /**
     * Use &#92;xFF hexadecimal escapes if possible (characters &lt;= U+FF), default
     * to &#92;uFFFF hexadecimal escapes.
     */
    XHEXA_DEFAULT_TO_UHEXA(false, true),

    /**
     * Always use &#92;uFFFF hexadecimal escapes.
     */
    UHEXA(false, false);


    private final boolean useSECs;
    private final boolean useXHexa;

    JavaScriptEscapeType(final boolean useSECs, final boolean useXHexa) {
        this.useSECs = useSECs;
        this.useXHexa = useXHexa;
    }

    boolean getUseSECs() {
        return this.useSECs;
    }

    boolean getUseXHexa() {
        return this.useXHexa;
    }

}

