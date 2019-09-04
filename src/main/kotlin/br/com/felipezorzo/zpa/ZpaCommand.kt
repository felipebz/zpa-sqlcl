package br.com.felipezorzo.zpa

import oracle.dbtools.extension.SQLCLService
import oracle.dbtools.raptor.newscriptrunner.*
import org.sonar.plsqlopen.CustomAnnotationBasedRulesDefinition
import org.sonar.plsqlopen.checks.CheckList
import org.sonar.plsqlopen.checks.ParsingErrorCheck
import org.sonar.plsqlopen.rules.ActiveRules
import org.sonar.plsqlopen.rules.Repository
import org.sonar.plsqlopen.rules.RuleMetadataLoader
import org.sonar.plsqlopen.rules.ZpaChecks
import org.sonar.plsqlopen.squid.AstScanner
import org.sonar.plugins.plsqlopen.api.checks.PlSqlCheck
import org.sonar.plugins.plsqlopen.api.checks.PlSqlVisitor
import java.nio.charset.StandardCharsets

import java.sql.Connection

class ZpaCommand : CommandListener(), IHelp, SQLCLService {

    private val scanner: AstScanner by lazy {
        val repository = Repository("zpa")
        val ruleMetadataLoader = RuleMetadataLoader()

        // don't register an issue for parsing errors
        val checkList = CheckList.checks.filterNot { it == ParsingErrorCheck::class.java }

        CustomAnnotationBasedRulesDefinition.load(repository, "zpa", checkList, ruleMetadataLoader)

        val activeRules = ActiveRules().addRepository(repository)

        val checks = ZpaChecks<PlSqlVisitor>(activeRules, "zpa", ruleMetadataLoader)
                .addAnnotatedChecks(CheckList.checks)

        AstScanner(checks.all(), null, true, StandardCharsets.UTF_8)
    }

    override fun getCommandListener() = ZpaCommand::class.java

    override fun getCommand() = "zpa"

    override fun getHelp() = "execute static analysis with ZPA"

    override fun isSqlPlus() = false

    override fun handleEvent(conn: Connection?, ctx: ScriptRunnerContext, cmd: ISQLCommand): Boolean {
        if (matches(command, cmd.sql)) {
            val parts = cmd.sql.split("\\s".toRegex()).toTypedArray()

            if (parts[1].equals("on", ignoreCase = true)) {
                ctx.putProperty(ZPA_ENABLED, java.lang.Boolean.TRUE)
                ctx.write("Static analysis enabled\n")
            } else if (parts[1].equals("off", ignoreCase = true)) {
                ctx.putProperty(ZPA_ENABLED, java.lang.Boolean.FALSE)
                ctx.write("Static analysis disabled\n")
            }
            return true
        }
        return false
    }

    override fun beginEvent(connection: Connection?, scriptRunnerContext: ScriptRunnerContext, isqlCommand: ISQLCommand) {

    }

    override fun endEvent(conn: Connection?, ctx: ScriptRunnerContext, cmd: ISQLCommand) {
        if (ctx.getProperty(ZPA_ENABLED) != null && ctx.getProperty(ZPA_ENABLED) as Boolean) {
            val result = scanner.scanFile(InputFile(cmd.sql))

            val issues = result.executedChecks
                    .flatMap { (it as PlSqlCheck).issues() }
                    .map { it.primaryLocation() }
                    .sortedWith(compareBy({ it.startLine() }, { it.startLineOffset() }))

            for (issue in issues) {
                ctx.write("Line ${issue.startLine()}: ${issue.message()}\n")
            }
        }
    }

    companion object {
        private const val ZPA_ENABLED = "ZPA.ENABLED"
    }

}
