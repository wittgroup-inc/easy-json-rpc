package com.wittgroupinc.easyjsonrpc

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.reflect.Type

class MyDeserializer<T> : Deserializer<T> {
    private val gson = Gson()
    override fun serialize(data: JsonRpcRequest): String = gson.toJson(data)
    override fun deserialize(type: Type, result: JsonElement): T {
        return gson.fromJson<T>(result, type)
    }


}
