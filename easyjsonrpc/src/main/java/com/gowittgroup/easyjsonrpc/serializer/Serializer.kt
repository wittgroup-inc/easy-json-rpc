package com.gowittgroup.easyjsonrpc.serializer

import com.google.gson.JsonElement
import com.gowittgroup.easyjsonrpc.models.JsonRpcRequest
import java.lang.reflect.Type

interface Serializer<T> {
    fun deserialize(type: Type, result: JsonElement): T
    fun serialize(data: JsonRpcRequest): String
}
