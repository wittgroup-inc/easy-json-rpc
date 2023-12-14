package com.gowittgroup.easyjsonrpc.client

import com.google.gson.JsonElement
import com.gowittgroup.easyjsonrpc.models.JsonRpcRequest
import io.reactivex.Single

interface JsonRpcClient<R> {
    fun <R> call(request: JsonRpcRequest, responseParser: (JsonElement) -> R): Single<R>
}
