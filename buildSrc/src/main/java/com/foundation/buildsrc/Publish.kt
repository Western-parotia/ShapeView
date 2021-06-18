package com.foundation.buildsrc

import java.io.File
import java.util.*

private const val VERSION = "1.0.0"
private const val SNAPSHOT = false

/**
 * 如果空则为4级包名
 */
private const val ARTIFACT_ID = ""

object Publish {
    object Version {
        var versionName = VERSION
            private set
            get() = when (SNAPSHOT) {
                true -> "$field-SNAPSHOT"
                false -> field
            }
        const val versionCode = 1

        private fun getTimestamp(): String {
            return java.text.SimpleDateFormat(
                "yyyy-MM-dd-hh-mm-ss",
                java.util.Locale.CHINA
            )
                .format(java.util.Date(System.currentTimeMillis()))
        }

        fun getVersionTimestamp(): String {
            return "$versionName-${getTimestamp()}"
        }
    }

    object Maven {
        val codingArtifactsRepoUrl = "https://mijukeji-maven.pkg.coding.net/repository/jileiku/base_maven/"
        val repositoryUserName: String
        val repositoryPassword: String

        init {
            val localProperties = Properties()
            var lp = File("local.properties")
            if (!lp.exists()) lp = File("../local.properties")//“/”win和mac都支持
            if (!lp.exists()) throw RuntimeException("没有找到local.properties")
            localProperties.load(lp.inputStream())
            val name = localProperties.getProperty("repositoryUserName")
            val password = localProperties.getProperty("repositoryPassword")
            if (name == null || password == null) {
                throw RuntimeException("请在local.properties添加私有仓库的用户名（repositoryUserName）和密码（repositoryPassword）")
            }
            repositoryUserName = name
            repositoryPassword = password
        }

        /**
         * 获取模块3级包名，如：com.foundation.widget
         */
        fun getThreePackage(projectDir: File): String {
            val st = getFourPackage(projectDir)
            return st.substring(0, st.lastIndexOf("."))
        }

        /**
         * 获取模块4级包名，如：com.foundation.widget.shape
         */
        fun getFourPackage(projectDir: File): String {
            try {
                val javaFile = File(projectDir, "src\\main\\java")
                if (javaFile.exists()) {
                    val child = javaFile.listFiles()[0].listFiles()[0].listFiles()[0].listFiles()[0]
                    //先删掉前段路径，然后转为.
                    return child.absolutePath.substring(javaFile.absolutePath.length + 1)
                        .replace("/", ".")
                        .replace("\\", ".")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            throw  RuntimeException("没有找到第四级包名")
        }

        /**
         * 上传库时的名字（如果配置为空则取四级包名名字）
         */
        fun getArtifactId(projectDir: File): String {
            val id = ARTIFACT_ID
            if (id.isNotEmpty()) {
                return id;
            }
            try {
                val javaFile = File(projectDir, "src\\main\\java")
                if (javaFile.exists()) {
                    val name = javaFile.listFiles()[0].listFiles()[0].listFiles()[0].listFiles()[0].name
                    //第四级的名字，首字母大写
                    return name[0].toUpperCase() + name.substring(1, name.length)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            throw  RuntimeException("没有找到第四级包名")
        }

    }
}