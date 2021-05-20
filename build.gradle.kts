// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.foundation.buildsrc.*

buildscript {//这里早于buildSrc，所以无法使用
    repositories {
        maven { setUrl("http://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        maven { setUrl("https://maven.aliyun.com/repository/google/") }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter/") }
        maven { setUrl("https://maven.aliyun.com/repository/central/") }
        maven { setUrl("https://jitpack.io") }
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { setUrl("http://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        maven { setUrl("https://maven.aliyun.com/repository/google/") }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter/") }
        maven { setUrl("https://maven.aliyun.com/repository/central/") }
        maven { setUrl("https://jitpack.io") }
        maven {
            setUrl(Publish.Maven.codingArtifactsRepoUrl)
            credentials {
                username = Publish.Maven.repositoryUserName
                password = Publish.Maven.repositoryPassword
            }
        }
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}