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
import org.sonarqube.gradle.SonarQubePlugin
import org.sonarqube.gradle.SonarQubeTask

class JavaBasePlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        println "hello world"
        println project.name
        project.getPluginManager().apply(JavaPlugin.class)
        project.getPluginManager().apply(GroovyPlugin.class)
        project.getPluginManager().apply(JacocoPlugin.class)
        project.getPluginManager().apply(SonarQubePlugin.class)

        project.tasks.withType(Test){
            testLogging.exceptionFormat = 'full'
        }

        project.afterEvaluate {
            project.tasks.withType(SonarQubeTask) {
                it.doLast {
                    def group = it.project.group
                    def sonarProjectName = it.properties.get("sonar.projectName")
                    def sonarProjectkey = it.properties.get("sonar.projectKey")
                    println "$group:$sonarProjectName"
                }
            }
        }

    }
}
