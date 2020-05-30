package com.wittgroupinc.sample

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.wittgroupinc.easyjsonrpc.Deserializer
import com.wittgroupinc.easyjsonrpc.JsonRpcRequest
import java.lang.reflect.Type

class MyDeserializer<T> : Deserializer<User> {
    private val gson = Gson()
    override fun serialize(data: JsonRpcRequest): String = gson.toJson(data)
    override fun deserialize(type: Type, result: JsonElement): User =
        gson.fromJson<User>(result, type)


}
