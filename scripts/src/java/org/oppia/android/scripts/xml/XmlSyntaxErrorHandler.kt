package org.oppia.android.scripts.xml

import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException

/**
 * Class for custom error handling of the parse exceptions thrown by the parser. It collects all
 * the syntax errors thrown by the parser, which can then later be retrieved.
 */
class XmlSyntaxErrorHandler : ErrorHandler {
  private val syntaxErrorList = mutableListOf<SAXParseException>()

  override fun warning(e: SAXParseException) {
    syntaxErrorList.add(e)
  }

  override fun error(e: SAXParseException) {
    syntaxErrorList.add(e)
  }

  override fun fatalError(e: SAXParseException) {
    syntaxErrorList.add(e)
  }

  /**
   * Retrieves all the errors collected by the handler.
   *
   * @return List<SAXParseException> a list of all the errors collected by the error handler
   */
  fun retrieveErrorList(): List<SAXParseException> {
    return syntaxErrorList
  }
}
