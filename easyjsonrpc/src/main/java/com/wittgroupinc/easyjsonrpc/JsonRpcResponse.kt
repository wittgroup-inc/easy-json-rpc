package com.wittgroupinc.easyjsonrpc

import com.google.gson.JsonElement

data class JsonRpcResponse(
    val id: Long,
    val result: JsonElement,
    val error: JsonRpcError
)
