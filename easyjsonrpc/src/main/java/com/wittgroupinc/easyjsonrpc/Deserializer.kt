package com.wittgroupinc.easyjsonrpc

import com.google.gson.JsonElement
import java.lang.reflect.Type

interface Deserializer<T> {
    fun deserialize(type: Type, result: JsonElement): T
    fun serialize(data: JsonRpcRequest): String
}
