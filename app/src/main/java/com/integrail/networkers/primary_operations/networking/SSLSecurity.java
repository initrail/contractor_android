package com.integrail.networkers.primary_operations.networking;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Integrail on 7/1/2016.
 */
public class SSLSecurity {
    public SSLContext setContext(InputStream input){
        Certificate ca= null;
        SSLContext context = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try {
                ca = cf.generateCertificate(input);
            } finally {
                input.close();
            }
            String keyStore = KeyStore.getDefaultType();
            KeyStore key = KeyStore.getInstance(keyStore);
            key.load(null, null);
            key.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(key);
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return context;
    }
}
