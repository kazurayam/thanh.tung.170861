import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.testobject.ConditionType

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.nio.file.Path
import java.nio.file.Paths
import com.kms.katalon.core.configuration.RunConfiguration
import groovy.lang.GroovyShell
import groovy.lang.Binding

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