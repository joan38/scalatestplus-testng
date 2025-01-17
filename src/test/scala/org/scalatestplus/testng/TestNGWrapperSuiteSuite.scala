/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.scalatest._
import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.events.Ordinal
import org.scalatestplus.testng.SharedHelpers.EventRecordingReporter

package org.scalatestplus.testng {

  class TestNGWrapperSuiteSuite extends FunSuite {
  
    val XML_SUITES_PROPERTY = "xml_suites"
      
    val legacySuiteXml = 
      <suite name="Simple Suite">
        <test verbose="10" name="org.scalatestplus.testng.test" annotations="JDK">
          <classes>
            <class name="org.scalatestplus.testng.testpackage.LegacySuite"/>
          </classes>
        </test>
      </suite>
      
    test("wrapper suite properly notifies reporter when tests start, and pass") {
    
      val xmlSuiteFile = this.createSuite( legacySuiteXml )

      val reporter = new EventRecordingReporter
      
      val status = new StatefulStatus
      (new TestNGWrapperSuite(List(xmlSuiteFile))).runTestNG(reporter, new Tracker, status)
      status.setCompleted()

      assert(reporter.testSucceededEventsReceived.length == 1)
    }

    val legacySuiteWithThreeTestsXml = 
      <suite name="Simple Suite">
        <test verbose="10" name="org.scalatestplus.testng.test" annotations="JDK">
          <classes>
            <class name="org.scalatestplus.testng.testpackage.LegacySuite"/>
            <class name="org.scalatestplus.testng.testpackage.LegacySuiteWithTwoTests"/>
          </classes>
        </test>
      </suite>
    
    test("wrapper suite should be notified for all tests") {
      
      val xmlSuiteFile = this.createSuite(legacySuiteWithThreeTestsXml)

      val reporter = new EventRecordingReporter
      
      val status = new StatefulStatus()
      (new TestNGWrapperSuite(List(xmlSuiteFile))).runTestNG(reporter, new Tracker, status)
      status.setCompleted()

      assert(reporter.testSucceededEventsReceived.length == 3)
    }
    
    def createSuite(suiteNode: scala.xml.Elem) : String = {
      val tmp = File.createTempFile("testng", "wrapper")
      FileUtils.writeStringToFile(tmp, suiteNode.toString)
      tmp.getAbsolutePath
    }
  }

  package testpackage {
    import org.testng.annotations._
  
    class LegacySuite extends TestNGSuite {
      @Test def legacyTestThatPasses(): Unit = {}
    }
    class LegacySuiteWithTwoTests extends TestNGSuite {
      @Test def anotherLegacyTestThatPasses(): Unit = {}
      @Test def anotherLegacyTestThatPasses2(): Unit = {}
    }
  }
}
