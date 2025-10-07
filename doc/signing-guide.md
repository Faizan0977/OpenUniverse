# Signing Guide: Keystore from `OpenSSL` (Recommended) or `JDK keytool`

This tutorial explains how to generate a signing key and prepare the required parameters for a Java-based signature system. You can generate the keystore using either **OpenSSL (recommended)** or the JDK's built-in `keytool`.

## Option 1: Using `OpenSSL` (Recommended)

### Step 1: Generate RSA Private Key

```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:4096
```

### Step 2: Create Self-Signed Certificate

```bash
openssl req -new -x509 -key private_key.pem -out cert.pem -days 365 \
  -subj "/CN=My Signing Key"
```

### Step 3: Convert to PKCS12 Keystore

```bash
openssl pkcs12 -export -in cert.pem -inkey private_key.pem -out sign_keystore.p12 -name my_signing_key -passout pass:my_store_password
```

> In PKCS#12 format, key password and store password are usually the same.

### Parameters to Fill

| Parameter                  | Example Value       |
| -------------------------- | ------------------- |
| `sign_key_store_file`      | `sign_keystore.p12` |
| `sign_key_store_type`      | `PKCS12`            |
| `sign_key_store_password`  | `my_store_password` |
| `sign_key_alias`           | `my_signing_key`    |
| `sign_key_password`        | `my_store_password` |
| `sign_signature_algorithm` | `SHA256withRSA`     |

## Notes

* The `sign_signature_algorithm` should match the key type:

  * For RSA: `SHA256withRSA`
  * For EC: `SHA256withECDSA`
* `.p12` (PKCS12) format is interoperable and recommended for new systems.
* In PKCS#12 format, the key and store password are typically required to be the same.
* Keep your private key and credentials secure.

## Option 2: Using `keytool` (JDK)

### Step 1: Generate Keystore and Key

```bash
keytool -genkeypair \
  -alias my_signing_key \
  -keyalg RSA \
  -keysize 4096 \
  -sigalg SHA256withRSA \
  -keystore sign_keystore.p12 \
  -storetype PKCS12 \
  -storepass my_store_password \
  -keypass my_key_password \
  -dname "CN=My Signing Key, OU=Dev, O=MyCompany, L=City, S=State, C=US"
```

### Parameters to Fill

| Parameter                  | Example Value       |
| -------------------------- | ------------------- |
| `sign_key_store_file`      | `sign_keystore.p12` |
| `sign_key_store_type`      | `PKCS12`            |
| `sign_key_store_password`  | `my_store_password` |
| `sign_key_alias`           | `my_signing_key`    |
| `sign_key_password`        | `my_key_password`   |
| `sign_signature_algorithm` | `SHA256withRSA`     |

## Notes

* The `sign_signature_algorithm` should match the key type:

  * For RSA: `SHA256withRSA`
  * For EC: `SHA256withECDSA`
* `.p12` (PKCS12) format is interoperable and recommended for new systems.
* In PKCS#12 format, the key and store password are typically required to be the same.
* Keep your private key and credentials secure.
