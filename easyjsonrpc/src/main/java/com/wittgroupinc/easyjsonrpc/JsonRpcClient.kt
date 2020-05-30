package com.wittgroupinc.easyjsonrpc

import com.google.gson.JsonElement
import io.reactivex.Single

interface JsonRpcClient<R> {

    fun <R> call(request: JsonRpcRequest, responseParser: (JsonElement) -> R): Single<R>
}
