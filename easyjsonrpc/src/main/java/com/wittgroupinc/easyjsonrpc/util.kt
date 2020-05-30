package com.wittgroupinc.easyjsonrpc

import android.util.Log
import io.reactivex.Single
import java.lang.reflect.*
import java.util.concurrent.atomic.AtomicLong

fun <T, B> createJsonRpcService(
    service: Class<T>,
    client: JsonRpcClient<B>,
    resultDeserializer: Deserializer<B>,
    logger: (String) -> Unit = {}
): T {

    val classLoader = service.classLoader
    val interfaces = arrayOf<Class<*>>(service)
    val invocationHandler = createInvocationHandler(service, client, resultDeserializer, logger)

    @Suppress("UNCHECKED_CAST")
    return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler) as T
}

private fun <T, B> createInvocationHandler(
    service: Class<T>,
    client: JsonRpcClient<B>,
    resultDeserializer: Deserializer<B>,
    logger: (String) -> Unit
): InvocationHandler {
    return object : InvocationHandler {

        val requestId = AtomicLong(0)

        @Throws(Throwable::class)
        override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any {
            val methodAnnotation =
                method.getAnnotation(JsonRpc::class.java) ?: return method.invoke(this, args)

            if (!method.returnsSingle) {
                error("Only io.reactivex.Single<T> is supported as return type")
            }

            val id = requestId.incrementAndGet()
            val methodName = methodAnnotation.value
            val parameters = method.jsonRpcParameters(args, service)

            val request = JsonRpcRequest(id, methodName, parameters)
            val returnType = method.resultGenericTypeArgument

            logger("JsonRPC: Calling: $request")
            return client.call(request) { result ->
                logger("JsonRPC: Parsing $returnType from result=$result")
                resultDeserializer.deserialize(returnType, result)
            }
        }
    }
}

private fun Method.jsonRpcParameters(args: Array<Any?>?, service: Class<*>): Map<String, Any?> {
    return parameterAnnotations
        .map { it?.firstOrNull { JsonRpcParam::class.java.isInstance(it) } }
        .mapIndexed { i, a ->
            when (a) {
                is JsonRpcParam -> a.value
                else -> error(
                    "Argument #$i of ${service.name}#$name()" +
                            " must be annotated with @${JsonRpcParam::class.java.simpleName}"
                )
            }
        }
        .mapIndexed { i, name -> name to args?.get(i) }
        .associate { it }
}

private val Method.returnsSingle: Boolean
    get() = returnType.canonicalName == Single::class.java.canonicalName

private val Method.resultGenericTypeArgument: Type
    @Suppress("CAST_NEVER_SUCCEEDS")
    get() = (this.genericReturnType as ParameterizedType).actualTypeArguments.first()

private class NullJsonRpcCallResultException : Exception()

fun <T> create(service: Class<T>): T {
    val socket = MyWebSocket()
    val deserializer = MyDeserializer<T>()
    val jsonRpcClient: JsonRpcClientImpl<T> =
        JsonRpcClientImpl(socket, deserializer, 100L, Logger())
    return createJsonRpcService(service, jsonRpcClient, deserializer, logger)
}

private val logger = fun(msg: String) {
    Log.d("tag", msg)
}
