package com.ashi.sslserverPinning.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
public class ServerPinningService {

    @Value("${app.pinning.cert-path}")
    private Resource certResource;

    private String expectedSha256Pin;
    private String certificateSha256Hex;

    @PostConstruct
    public void init() throws Exception {
        try (InputStream inputStream = certResource.getInputStream()) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(cert.getEncoded());

            expectedSha256Pin = "sha256/" + Base64.getEncoder().encodeToString(digest);
            certificateSha256Hex = toHex(digest);
        }
    }

    public String getExpectedSha256Pin() {
        return expectedSha256Pin;
    }

    public String getCertificateSha256Hex() {
        return certificateSha256Hex;
    }

    public boolean isPinValid(String providedPin) {
        if (providedPin == null || providedPin.isBlank()) {
            return false;
        }
        return expectedSha256Pin.equals(providedPin.trim());
    }

    private String toHex(byte[] digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}

