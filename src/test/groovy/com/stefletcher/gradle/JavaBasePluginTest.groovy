package com.stefletcher.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by stefletcher on 29/08/2016.
 */
public class JavaBasePluginTest extends Specification{
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()


    File buildFile
    def setupSepc() {
        def testFolder = new File(testProjectDir.absolutePath+'/src/test/groovy')
        def buildFolder = new File(testProjectDir.absolutePath+'/src/main/groovy')
    }


    def setup() {

        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << '''

            plugins {
                id 'com.stefletcher.gradle-javabase-plugin'
            }
            sonarqube {
              properties {
                property "sonar.projectName", "My Project Name"
                property "sonar.projectKey", "org.sonarqube:java-gradle-simple"
              }
            }
        '''
    }
    def "should apply plugin without error"() {
        given:
            def a
        when:
            def project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('test')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
            a = 1
        then:
            a == 1
    }

    def "should run sonarqube task without error"() {
        given:
        def a
        when:
        def project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('sonarqube')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
        a = 1
        then:
        a == 1
    }

}