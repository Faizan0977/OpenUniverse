package org.ou.common.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HexFormat;

public class SignatureUtils {

    /**
     * Signs the given data using a private key from the keystore.
     *
     * @param data The byte array to sign.
     * @param keystoreFile Path to the keystore file.
     * @param keystoreType Type of the keystore (JKS, PKCS12, etc.).
     * @param keystorePassword Password for the keystore.
     * @param keyAlias Alias of the private key inside the keystore.
     * @param keyPassword Password for the private key.
     * @param signatureAlgorithm Signature algorithm (e.g., "SHA256withRSA").
     * @return The signed byte array.
     * @throws Exception If signing fails.
     */
    public static String sign(byte[] data, PrivateKey privateKey, String signatureAlgorithm) throws Exception {
        if (privateKey == null) {
            throw new KeyStoreException("Private key not found");
        }
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initSign(privateKey);
        signature.update(data);
        //return CommonUtils.bytesToHex(signature.sign());
        return HexFormat.of().formatHex(signature.sign());
    }

    /**
     * Loads a private key from a keystore.
     */
    public static PrivateKey getPrivateKey(//
            String keystoreFile, //
            String keystoreType, //
            String keystorePassword, //
            String keyAlias, //
            String keyPassword //
    ) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }
        return ((PrivateKeyEntry) keyStore.getEntry(keyAlias, new KeyStore.PasswordProtection(keyPassword.toCharArray()))).getPrivateKey();
    }
}
