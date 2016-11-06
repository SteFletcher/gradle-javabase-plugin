/**
 * Created by stefletcher on 29/08/2016.
 */
package com.stefletcher.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.kt3k.gradle.plugin.CoverallsPlugin
import org.sonarqube.gradle.SonarQubePlugin
import org.sonarqube.gradle.SonarQubeTask

class JavaBasePlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        println project.name


        def travisFile = new File(project.projectDir.absolutePath+'/.travis.yml')
        if(!travisFile.exists()) {
            travisFile << "language: groovy\n" +
                    "\n" +
                    "jdk:\n" +
                    "  - oraclejdk8\n" +
                    "\n" +
                    "script:\n" +
                    "  - ./gradlew clean build test\n" +
                    "\n" +
                    "before_install:\n" +
                    "  - chmod +x gradlew\n" +
                    "\n" +
                    "after_success:\n" +
                    "- \"./gradlew jacocoTestReport coveralls --debug\""
        }

        project.getPluginManager().apply(JavaPlugin.class)
        project.getPluginManager().apply(GroovyPlugin.class)
        project.getPluginManager().apply(JacocoPlugin.class)
        project.getPluginManager().apply(SonarQubePlugin.class)
        project.getPluginManager().apply(CoverallsPlugin.class)

        this.setupTesting(project)

        project.afterEvaluate {
            project.tasks.withType(SonarQubeTask) {
                it.doLast {
                    def group = it.project.group
                    def sonarProjectName = it.properties.get("sonar.projectName")
                    def sonarProjectkey = it.properties.get("sonar.projectKey")
                    def sonarBranch = it.properties.get("sonar.branch")

                }
            }
        }

    }

    void setupTesting(Project project){

        project.tasks.withType(Test){
            testLogging.exceptionFormat = 'full'
        }

        project.sourceSets {
            integTest {
                groovy.srcDir project.file('src/integTest/groovy')
                resources.srcDir project.file('src/integTest/resources')
                compileClasspath = project.sourceSets.main.output + project.configurations.testRuntime
                runtimeClasspath = output + compileClasspath
            }
        }

        project.task('integTest', type: Test) {
            group = 'verification'
            description = "Runs the tests in the 'integTest' sourceset."
            testClassesDir = project.sourceSets.integTest.output.classesDir
            classpath = project.sourceSets.integTest.runtimeClasspath
            reports.html.destination = project.file("${project.buildDir}/reports/integ")
        }

        project.jacocoTestReport {
            reports {
                xml.enabled = true
            }
        }

        project.task('jacocoIntegTestReport', type: JacocoReport) {
            sourceSets project.sourceSets.main
            executionData project.integTest
            reports {
                xml.enabled true
                csv.enabled false
                html.destination "${project.buildDir}/reports/jacocoInteg"
            }
        }

        project.task('jacocoCombinedTestReport', type: JacocoReport) {
            sourceSets project.sourceSets.main
            executionData project.test, project.integTest
            reports {
                xml.enabled true
                csv.enabled false
                html.destination "${project.buildDir}/reports/jacocoCombined"
            }
        }

        project.tasks.withType(JacocoReport) {
            group = 'verification'
        }

        project.integTest.mustRunAfter project.test

        //Generate Jacoco Reports after each test task.
        project.test.finalizedBy project.jacocoTestReport
        project.integTest.finalizedBy project.jacocoIntegTestReport
        project.integTest.finalizedBy project.jacocoCombinedTestReport
    }
}
