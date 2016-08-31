package com.stefletcher.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification;

import java.io.File;

/**
 * Created by stefletcher on 29/08/2016.
 */
public class JavaBasePluginTest extends Specification{
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()


    File buildFile

    def setup() {

        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << '''

            plugins {
                id 'com.stefletcher.gradle-javabase-plugin'
            }
        '''
    }
    def "should apply plugin without error"() {
        given:
            def a
        when:
            def project = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('clean', 'build')
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
            a = 1
        then:
            a == 1
    }

}