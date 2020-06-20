package com.wittgroupinc.easyjsonrpc.models

data class JsonRpcRequest(
    val id: Long,
    val method: String,
    val params: Map<String, Any?> = emptyMap()
)
