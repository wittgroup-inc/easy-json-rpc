package com.wittgroupinc.easyjsonrpc

import java.lang.RuntimeException

class JsonRpcCallException(code: Int, message: String) : RuntimeException(

) {
    override val message: String?
        get() = super.message
}
