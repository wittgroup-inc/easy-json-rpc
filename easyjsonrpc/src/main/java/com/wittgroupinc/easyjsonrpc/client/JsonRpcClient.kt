package com.wittgroupinc.easyjsonrpc.client

import com.google.gson.JsonElement
import com.wittgroupinc.easyjsonrpc.models.JsonRpcRequest
import io.reactivex.Single

interface JsonRpcClient<R> {

    fun <R> call(request: JsonRpcRequest, responseParser: (JsonElement) -> R): Single<R>
}
