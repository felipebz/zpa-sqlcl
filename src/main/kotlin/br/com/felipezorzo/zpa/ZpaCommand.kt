package br.com.felipezorzo.zpa

import oracle.dbtools.extension.SQLCLService
import oracle.dbtools.raptor.newscriptrunner.*

import java.sql.Connection

class ZpaCommand : CommandListener(), IHelp, SQLCLService {

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
            // handle cmd.sql
        }
    }

    companion object {
        private const val ZPA_ENABLED = "ZPA.ENABLED"
    }

}
