package com.gowittgroup.server

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gowittgroup.server.databinding.ActivityMainBinding
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.DefaultSSLWebSocketServerFactory


class MainActivity : AppCompatActivity(), ServerCallback {

    private lateinit var server: Server
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        server = Server(8887, this)
        // Need to comment this line if CA certificate1 does not work.
        server.setWebSocketFactory(DefaultSSLWebSocketServerFactory(CertificateUtil.getContext(this)))
        binding.startButton.setOnClickListener { startServer() }
        binding.stopButton.setOnClickListener { stopServer() }
    }

    private fun startServer() {
        try {
            server.start()
            println("ChatServer started on port:  ${server.port}")
            binding.startButton.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
        } catch (e: IllegalStateException) {
            e.message?.let { Log.e("MainActivity", it) }
        }
    }


    private fun stopServer() {
        server.stop(1000)
        binding.startButton.visibility = View.VISIBLE
        binding.stopButton.visibility = View.GONE
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        showMessage("${resolveHostName(conn)} : $message")
        conn?.send("{\"id\":1,\"result\":{\"userId\":12,\"userName\":\"Pawan\"},\"jsonrpc\":\"2.0\"}")
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
