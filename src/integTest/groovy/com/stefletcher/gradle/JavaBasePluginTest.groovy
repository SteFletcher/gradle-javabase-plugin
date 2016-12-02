package com.stefletcher.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * Created by stefletcher on 29/08/2016.
 */
public class JavaBasePluginTest extends Specification{
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()


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
    @Unroll
    def "gradle compatiblility tests"() {
        when:
            println "Building with version $gradleVersion"
            def result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments('build')
                    .withPluginClasspath()
                    .withGradleDistribution(versionToURI(gradleVersion))
                    .withDebug(true)
                    .forwardOutput()
                    .build()
        then:
            result.task(":build").outcome == SUCCESS
        where:
            gradleVersion << ['2.8', '2.9', '2.10','2.11','2.12', '2.13','2.14', '3.0', '3.1', '3.2', '3.2.1']
    }

    URI versionToURI(String version) {
        "https://services.gradle.org/distributions/gradle-${version}-all.zip".toURI()
    }

}