package com.gowittgroup.easyjsonrpc.models

import com.google.gson.JsonElement

data class JsonRpcResponse(
    val id: Long,
    val result: JsonElement,
    val error: JsonRpcError,
    val jsonrpc: String = "2.0"
)
