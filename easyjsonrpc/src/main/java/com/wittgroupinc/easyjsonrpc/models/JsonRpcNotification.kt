package com.wittgroupinc.easyjsonrpc.models

data class JsonRpcNotification(
    val method: String,
    val params: Map<String, Any?> = emptyMap(),
    val jsonrpc: String = "2.0"
)
