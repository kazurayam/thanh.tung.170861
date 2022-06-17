import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

String htmlText = """<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Sample HTML</title>
</head>
<body>
  <p>Message d'accueil</p>
</body>
</html>
"""

Path htmlFile = Paths.get(RunConfiguration.getProjectDir()).resolve("sample.html")
htmlFile.toFile().text = htmlText

WebUI.openBrowser('')
WebUI.navigateToUrl(htmlFile.toUri().toURL().toExternalForm())

String key = "Message d'accueil"

/*
TestObject tObjFragileAgainstApos = makeTestObject(''' //p[text()='${value}'] ''', ['value':key])
boolean b1 = WebUI.verifyElementPresent(tObjFragileAgainstApos, 3, FailureHandling.CONTINUE_ON_FAILURE)
 */

TestObject tObjRobustAgainstApos = makeTestObjectRobustAgainstApos(''' //p[text()="${value}"] ''', ['value':key])
boolean b2 = WebUI.verifyElementPresent(tObjRobustAgainstApos, 3, FailureHandling.CONTINUE_ON_FAILURE)

WebUI.closeBrowser()

/**
 * 
 * @param parameterisedXPath e.g., //*[contains(text(),"${value}")]
 * @param parameters e.g., ["value": "Message"]
 * @return a TestObject instance
 */
TestObject makeTestObjectRobustAgainstApos(String parameterisedXPath, Map<String, String> parameters) {
	Binding binding = new Binding(parameters)
	GroovyShell groovyShell = new GroovyShell(binding)
	String interpolatedXPath = (String)groovyShell.evaluate(' \" ' + parameterisedXPath.replace('\"', '\\"') + ' \" ')
	println "interpolatedXPath=" + interpolatedXPath
	TestObject tObj = new TestObject(interpolatedXPath)
	tObj.addProperty('xpath', ConditionType.EQUALS, interpolatedXPath)
	return tObj
}