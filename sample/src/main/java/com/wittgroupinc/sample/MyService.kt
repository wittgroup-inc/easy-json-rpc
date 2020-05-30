package com.wittgroupinc.sample

import com.wittgroupinc.easyjsonrpc.JsonRpc
import com.wittgroupinc.easyjsonrpc.JsonRpcParam
import io.reactivex.Single


interface MyService<User> {
    @JsonRpc("myMethod")
    fun myMethod(
        @JsonRpcParam("param1") a: Int,
        @JsonRpcParam("param2") b: String,
        @JsonRpcParam("param3") c: List<Int> = emptyList()
    ): Single<User>
}
