package com.gowittgroup.server

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class Server(address: InetSocketAddress?) : WebSocketServer(address) {
    private var callback: ServerCallback? = null

    constructor(port: Int, callback: ServerCallback) : this(InetSocketAddress(port)) {
        this.callback = callback
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        this.callback?.onOpen(conn, handshake)
        conn?.send("Hello from the server") //This method sends a message to the new client
        broadcast("new connection:  ${handshake?.resourceDescriptor}") //This method sends a message to all clients connected
        println("${conn?.remoteSocketAddress?.address?.hostAddress}  entered the room!")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        this.callback?.onClose(conn, code, reason, remote)
        broadcast("$conn has left the room!")
        println("$conn has left the room!")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        this.callback?.onMessage(conn, message)
        println("$conn: $message")
    }

    override fun onStart() {
        this.callback?.onStart("")
        println("Server started!")
        connectionLostTimeout = 0
        connectionLostTimeout = 100
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        ex?.printStackTrace()
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

}
