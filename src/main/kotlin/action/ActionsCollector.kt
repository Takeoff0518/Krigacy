package top.mrxiaom.kritor.adapter.onebot.action

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import top.mrxiaom.kritor.adapter.onebot.action.actions.*
import top.mrxiaom.kritor.adapter.onebot.connection.ChannelWrapper
import top.mrxiaom.kritor.adapter.onebot.connection.IAdapter
import top.mrxiaom.kritor.adapter.onebot.utils.buildJsonObject
import top.mrxiaom.kritor.adapter.onebot.utils.putJsonObject

object ActionsCollector {
    fun IAdapter.addActionListeners() {
        for (action in listOf(
            GetLoginInfo, SetQQProfile, GetVersionInfo
        )) {
            val anno = action.findAnnotation<Action>() ?: continue
            addActionListener(action, *anno.value)
        }
    }

    private inline fun <reified T : Annotation> Any.findAnnotation(): T? {
        return this::class.java.annotations.firstNotNullOfOrNull { it as? T }
    }
}

annotation class Action(
    vararg val value: String
)

interface IAction {
    suspend fun IAdapter.execute(wrap: ChannelWrapper, data: JsonObject, echo: JsonElement)
    suspend fun IAdapter.ok(echo: JsonElement, block: MutableMap<String, Any>.() -> Unit = {}) {
        pushActionResponse(echo, 0, null, block)
    }
    suspend fun IAdapter.failed(echo: JsonElement, retCode: Int, message: String, block: MutableMap<String, Any>.() -> Unit = {}) {
        pushActionResponse(echo, retCode, message, block)
    }
}
suspend fun IAdapter.pushActionResponse(echo: JsonElement, retCode: Int, message: String? = null, block: MutableMap<String, Any>.() -> Unit = {}) {
    push(buildJsonObject {
        put("status", if (retCode == 0) "ok" else "failed")
        put("retcode", retCode)
        if (message != null) put("msg", message)
        putJsonObject("data", block)
        put("echo", echo)
    }.toString())
}

suspend fun IAction.execute(adapter: IAdapter, type: String, channel: ChannelWrapper, data: JsonObject, echo: JsonElement) = runCatching {
    adapter.execute(channel, data, echo)
}.onFailure {
    adapter.logger.error("执行 $type 时出现错误", it)
    adapter.pushActionResponse(echo, 1404, it.toString())
}
