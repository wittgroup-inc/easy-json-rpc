package com.wittgroupinc.server

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake


class MainActivity : AppCompatActivity(), ServerCallback {

    private lateinit var server: Server

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        server = Server(8887, this)
        // Need to comment this line if CA certificate does not work.
        // receiver.setWebSocketFactory(DefaultSSLWebSocketServerFactory(CertificateUtil.getContext(this)))
        start_button.setOnClickListener { startServer() }
        stop_button.setOnClickListener { stopServer() }
    }

    private fun startServer() {
        try {
            server.start()
            println("ChatServer started on port:  ${server.port}")
            start_button.visibility = View.GONE
            stop_button.visibility = View.VISIBLE
        } catch (e: IllegalStateException) {
            Log.e("MainActivity", e.message)
        }
    }


    private fun stopServer() {
        server.stop(1000)
        start_button.visibility = View.VISIBLE
        stop_button.visibility = View.GONE
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        showMessage("${resolveHostName(conn)} : $message")
        conn?.send("{\"id\": 1,\"result\": {\"userId\": 12,\"userName\": \"Pawan\"}")
    }


    override fun onStart(port: String) = showMessage("Server Started")

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) =
        showMessage("${resolveHostName(conn)} has left the room!")

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) =
        showMessage("${resolveHostName(conn)} entered the room!")

    private fun showMessage(message: String) =
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }

    private fun resolveHostName(conn: WebSocket?) =
        "${conn?.resourceDescriptor?.length?.let { conn.resourceDescriptor?.substring(1, it) }}"

    private fun resolveHost(conn: WebSocket?) =
        "${conn?.remoteSocketAddress?.address?.hostAddress}"

}
