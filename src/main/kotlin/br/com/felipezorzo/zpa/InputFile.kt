package br.com.felipezorzo.zpa

import org.sonar.plugins.plsqlopen.api.PlSqlFile

class InputFile(private val contents: String) : PlSqlFile {
    override fun contents(): String = contents
    override fun fileName() = ""
    override fun type() = PlSqlFile.Type.MAIN
}