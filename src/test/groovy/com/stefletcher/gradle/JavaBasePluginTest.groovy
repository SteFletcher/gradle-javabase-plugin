package com.stefletcher.gradle

import org.gradle.internal.logging.StandardOutputCapture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

/**
 * Created by stefletcher on 29/08/2016.
 */
public class JavaBasePluginTest extends Specification{
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Rule
    final OutputCapture console = new OutputCapture();

    File buildFile
    File settingsFile
    def setupSepc() {
        def testFolder = new File(testProjectDir.absolutePath+'/src/test/groovy')
        def buildFolder = new File(testProjectDir.absolutePath+'/src/main/groovy')
    }


    def setup() {
        settingsFile = testProjectDir.newFile('settings.gradle')
        settingsFile << '''rootProject.name="java-base-component"'''

        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << '''

            plugins {
                id 'com.stefletcher.gradle-javabase-plugin'
            }
            group 'com.example'
            sonarqube {
              properties {
                property "sonar.projectName", "My Project Name"

              }
            }
        '''

    }
    def "should apply plugin with error when server unavailable"() {
        given:
        when:
            BuildResult project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('sonarqube')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
        then:
            thrown Exception
            console.toString().contains("server [http://localhost:9000] can not be reached")
    }

    def "should apply plugins and run build task without error"() {
        given:
        when:
            def project = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments('build')
                    .withPluginClasspath()
                    .withDebug(true)
                    .forwardOutput()
                    .build()
        then:
            project.task(":build").getOutcome() == TaskOutcome.SUCCESS
    }

    def "travis file created"() {
        given:
        when:
        def project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('build')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
        File travisFile = new File(testProjectDir.root.getAbsolutePath()+'/travis.yml')
        then:
        travisFile.exists()
    }

    def "should run integTest task without error"() {
        given:
        when:
        def project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('integTest')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
        then:
        project.task(":integTest").getOutcome() == TaskOutcome.UP_TO_DATE
    }

}