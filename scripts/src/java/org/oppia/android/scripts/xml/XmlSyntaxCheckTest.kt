package org.oppia.android.scripts.xml

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.oppia.android.scripts.common.XML_SYNTAX_CHECK_FAILED_OUTPUT_INDICATOR
import org.oppia.android.scripts.common.XML_SYNTAX_CHECK_PASSED_OUTPUT_INDICATOR
import org.oppia.android.testing.assertThrows
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/** Tests for [XmlSyntaxCheck]. */
class XmlSyntaxCheckTest {
  private val outContent: ByteArrayOutputStream = ByteArrayOutputStream()
  private val originalOut: PrintStream = System.out

  @Rule
  @JvmField
  var tempFolder = TemporaryFolder()

  @Before
  fun setUpTests() {
    tempFolder.newFolder("testfiles")
    System.setOut(PrintStream(outContent))
  }

  @After
  fun restoreStreams() {
    System.setOut(originalOut)
  }

  @Test
  fun testXmlSyntax_validXml_xmlSyntaxIsCorrect() {
    val validXml =
      """
      <?xml version="1.0" encoding="utf-8"?>
      <shape xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="rectangle">
        <solid android:color="#3333334D" />
        <size android:height="1dp" />
      </shape>
      """.trimIndent()

    val tempFile = tempFolder.newFile("testfiles/TestFile.xml")
    tempFile.writeText(validXml)

    runScript()

    assertThat(outContent.toString().trim()).isEqualTo(XML_SYNTAX_CHECK_PASSED_OUTPUT_INDICATOR)
  }

  @Test
  fun testXmlSyntax_invalidOpeningTag_xmlSyntaxIsIncorrect() {
    val invalidXml =
      """
      <?xml version="1.0" encoding="utf-8"?>
      <shape xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="rectangle">
        <solid android:color="#3333334D" />
        <size android:height="1dp" />
      </shapes>
      """.trimIndent()
    val tempFile = tempFolder.newFile("testfiles/TestFile.xml")
    tempFile.writeText(invalidXml)

    val exception = assertThrows(Exception::class) {
      runScript()
    }

    assertThat(exception).hasMessageThat().contains(XML_SYNTAX_CHECK_FAILED_OUTPUT_INDICATOR)
    assertThat(outContent.toString().trim()).isEqualTo(
      """
      ${retrieveTestFilesDirectoryPath()}/TestFile.xml:6:8: The end-tag for element type "shape" must end with a '>' delimiter.
      """.trimIndent()
    )
  }

  @Test
  fun testXmlSyntax_multipleFilesHavingInvalidXml_xmlSyntaxIsIncorrect() {
    val invalidXmlForFile1 =
      """
      <?xml version="1.0" encoding="utf-8"?>
      <shape xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="rectangle">
        <<solid android:color="#3333334D" />
        <size android:height="1dp" />
      </shape>
      """.trimIndent()
    val invalidXmlForFile2 =
      """
      <?xml version="1.0" encoding="utf-8"?>
      <shape xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="rectangle">
        <solid android:color="#3333334D" />
        <size android:height="1dp" />
      </shapes>
      """.trimIndent()
    val tempFile1 = tempFolder.newFile("testfiles/TestFile1.xml")
    val tempFile2 = tempFolder.newFile("testfiles/TestFile2.xml")
    tempFile1.writeText(invalidXmlForFile1)
    tempFile2.writeText(invalidXmlForFile2)

    val exception = assertThrows(Exception::class) {
      runScript()
    }

    assertThat(exception).hasMessageThat().contains(XML_SYNTAX_CHECK_FAILED_OUTPUT_INDICATOR)
    assertThat(outContent.toString().trim()).isEqualTo(
      """
      ${retrieveTestFilesDirectoryPath()}/TestFile2.xml:6:8: The end-tag for element type "shape" must end with a '>' delimiter.
      ${retrieveTestFilesDirectoryPath()}/TestFile1.xml:4:4: The content of elements must consist of well-formed character data or markup.
      """.trimIndent()
    )
  }

  /** Retrieves the absolute path of testfiles directory. */
  private fun retrieveTestFilesDirectoryPath(): String {
    return "${tempFolder.root}/testfiles"
  }

  /** Helper function which executes the main method of the script. */
  private fun runScript() {
    main(retrieveTestFilesDirectoryPath())
  }
}
