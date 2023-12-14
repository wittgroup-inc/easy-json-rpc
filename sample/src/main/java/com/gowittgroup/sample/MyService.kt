package com.gowittgroup.sample

import com.gowittgroup.easyjsonrpc.annotations.JsonRpc
import com.gowittgroup.easyjsonrpc.annotations.JsonRpcParam
import io.reactivex.Single


interface MyService {
    @JsonRpc("myMethod")
    fun myMethod(
        @JsonRpcParam("param1") a: Int,
        @JsonRpcParam("param2") b: String,
        @JsonRpcParam("param3") c: List<Int> = emptyList()
    ): Single<User>
}
