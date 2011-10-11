/**
 *
 */
package org.zkoss.zstl
import org.zkoss.ztl.ZKClientTestCase
import org.zkoss.ztl.util.ConfigHelper
import org.zkoss.ztl.Widget
import scala.collection.JavaConversions._

/**
 * ZTL for Scala to test
 * @author jumperchen
 */
class ZTL4ScalaTestCase extends ZKClientTestCase {
  var ch = ConfigHelper.getInstance()
  target = ch.getServer() + ch.getContextPath() + "/" + ch.getAction()
  browsers = getBrowsers(ch.getBrowser())
  _timeout = ch.getTimeout().toInt
  caseID = getClass().getSimpleName()

  var _engine: Widget = null;
  def runZTL(zscript: scala.xml.Elem, executor: () => Unit) = {
    for (browser <- browsers) {
      try {
        start(browser);
        windowFocus();
        windowMaximize();
        _engine = new Widget(new StringBuffer("zk.Desktop._dt"))

        runZscript(
          zscript toString () replaceAll ("\n", " "))

        waitResponse();

        executor();
      } finally {
        stop();
      }
    }
  }
  def engine(): Widget = _engine;
}