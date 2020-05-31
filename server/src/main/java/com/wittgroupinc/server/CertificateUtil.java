package com.wittgroupinc.server;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


public class CertificateUtil {
    private static String FILE_NAME = "certificate"; // pem file name without extention
    private static String KEY_FILE_NAME = "key"; // filename for the key file


    public static SSLContext getContext(Context ctx) {
        SSLContext context;
        String password = "TEMP_PASSWORD";
        try {
            context = SSLContext.getInstance( "TLS" );

            InputStream certStream = ctx.getResources().openRawResource(
                    ctx.getResources().getIdentifier(FILE_NAME,
                            "raw", ctx.getPackageName()));

            InputStream keyStream = ctx.getResources().openRawResource(
                    ctx.getResources().getIdentifier(KEY_FILE_NAME,
                            "raw", ctx.getPackageName()));

            byte[] certi = getBytesFromStream( certStream );
            byte[] keyi = getBytesFromStream( keyStream );
            byte[] certBytes = parseDERFromPEM( certi, "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----" );
            byte[] keyBytes = parseDERFromPEM( keyi, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----" );

            X509Certificate cert = generateCertificateFromDER( certBytes );
            RSAPrivateKey key = generatePrivateKeyFromDER( keyBytes );

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load( null );
            keystore.setCertificateEntry( "cert-alias", cert );
            keystore.setKeyEntry( "key-alias", key, password.toCharArray(), new Certificate[]{ cert } );

            KeyManagerFactory kmf = KeyManagerFactory.getInstance( "X509" );
            kmf.init( keystore, password.toCharArray() );
            KeyManager[] km = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keystore);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            context.init(km, trustManagers, null);

        } catch ( Exception e ) {
            context = null;
        }
        return context;
    }

    private static byte[] parseDERFromPEM( byte[] pem, String beginDelimiter, String endDelimiter ) {
        String data = new String( pem );
        String[] tokens = data.split( beginDelimiter );
        tokens = tokens[1].split( endDelimiter );
        return Base64.decode( tokens[0], Base64.DEFAULT );
    }

    private static RSAPrivateKey generatePrivateKeyFromDER( byte[] keyBytes ) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( keyBytes );

        KeyFactory factory = KeyFactory.getInstance( "RSA" );

        return ( RSAPrivateKey ) factory.generatePrivate( spec );
    }

    private static X509Certificate generateCertificateFromDER( byte[] certBytes ) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance( "X.509" );

        return ( X509Certificate ) factory.generateCertificate( new ByteArrayInputStream( certBytes ) );
    }

    private static byte[] getBytes( File file ) {
        byte[] bytesArray = new byte[( int ) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream( file );
            fis.read( bytesArray ); //read file into bytes[]
            fis.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    private static byte[] getBytesFromStream(InputStream strm) throws IOException {
        final int bufLen = 4096;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = strm.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) strm.close();
            else try {
                strm.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
