package core.commands.dev

import core.commands.parser.ArgType
import core.commands.parser.Executable
import core.user.User

class Parse : Executable<Unit>(
    "devp", """
    Print verbose log of parser  
    開発用 / For development
""".trimIndent()
) {
    val cmd by argument(ArgType.Executable, "cmd")
    val bypassArgs by argument(ArgType.String, "args").vararg(true)
    override suspend fun execute(user: User, rawArgs: List<String>) {
        cmd.verbose(bypassArgs)
    }
}

class Status : Executable<Unit>(
    "stat", """
    Print JRE Status  
    開発用 / For development
""".trimIndent()
) {
    override suspend fun execute(user: User, rawArgs: List<String>) {

        out.println("""
           EseLinux MemoryInfo
           Free : ${"%,6d MB".format((Runtime.getRuntime().freeMemory()/(1024*1024)))}
           Total: ${"%,6d MB".format((Runtime.getRuntime().totalMemory()/(1024*1024)))}
           Max  : ${"%,6d MB".format((Runtime.getRuntime().maxMemory()/(1024*1024)))}
        """.trimIndent())

    }
}