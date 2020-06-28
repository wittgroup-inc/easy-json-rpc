package com.wittgroupinc.easyjsonrpc.serializer

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.wittgroupinc.easyjsonrpc.models.JsonRpcRequest
import java.lang.reflect.Type

class SerializerImpl<T> :
    Serializer<T> {
    private val gson = Gson()
    override fun serialize(data: JsonRpcRequest): String = gson.toJson(data)
    override fun deserialize(type: Type, result: JsonElement): T {
        return gson.fromJson<T>(result, type)
    }
}
