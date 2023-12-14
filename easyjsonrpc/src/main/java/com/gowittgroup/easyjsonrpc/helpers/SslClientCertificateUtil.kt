package com.gowittgroup.easyjsonrpc.helpers

import android.content.Context
import com.gowittgroup.easyjsonrpc.Constants
import com.gowittgroup.easyjsonrpc.R
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.*

class SslClientCertificateUtil() {
    private val TAG = SslClientCertificateUtil::class.java.simpleName
    private val PASSWORD = "password"

    internal inner class KeyAndTrustManagers(
            val keyManagers: Array<KeyManager>,
            val trustManagers: Array<TrustManager>
    )

    private fun readCertificate(context: Context): InputStream {
        try {
            val caInput = context.applicationContext.resources.openRawResource(
                R.raw.certificate
            )
            return caInput
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    fun socketFactory(context: Context): SSLSocketFactory {
        val sslContext = SSLContext.getInstance(Constants.TSL_PROTOCOL)
        sslContext.init(
                trustManagerForCertificates(readCertificate(context)).keyManagers,
                trustManagerForCertificates(readCertificate(context)).trustManagers,
                null
        )
        return sslContext.socketFactory
    }

    fun sslTrustManager(context: Context): X509TrustManager {
        val keyAndTrustManagers = trustManagerForCertificates(readCertificate(context))
        return keyAndTrustManagers.trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun trustManagerForCertificates(`in`: InputStream): KeyAndTrustManagers {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)
        require(!certificates.isEmpty()) { "expected non-empty set of trusted certificates" }
        val password = PASSWORD.toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        var index = 0
        for (certificate in certificates) {
            val certificateAlias = Integer.toString(index++)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }
        val keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
        )
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(keyStore)
        return KeyAndTrustManagers(
                keyManagerFactory.keyManagers,
                trustManagerFactory.trustManagers
        )
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `in`: InputStream? = null // By convention, 'null' creates an empty key1 store.
            keyStore.load(`in`, password)
            return keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }
}
