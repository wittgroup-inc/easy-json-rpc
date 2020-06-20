package com.wittgroupinc.easyjsonrpc.serializer

import com.google.gson.JsonElement
import com.wittgroupinc.easyjsonrpc.models.JsonRpcRequest
import java.lang.reflect.Type

interface Serializer<T> {
    fun deserialize(type: Type, result: JsonElement): T
    fun serialize(data: JsonRpcRequest): String
}
