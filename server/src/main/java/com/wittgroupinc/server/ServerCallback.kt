package com.wittgroupinc.server

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

interface ServerCallback {
    fun onMessage(conn: WebSocket?, message: String?)
    fun onStart(port: String)
    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean)
    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?)
}
