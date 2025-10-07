# OpenUniverse build guide

## Prepare keystore

### Step 1: Generate a private key with OpenSSL
First, create a new private key.
This key is used to sign your JAR file and should be kept secure.
For strong security, a 2048-bit RSA key is standard.

```bash
openssl genrsa -out private.key 2048
```

### Step 2: Create a self-signed certificate
Create a self-signed X.509 certificate using the private key. You will be asked for information about the entity the certificate is for. 

```bash
openssl req -new -x509 -key private.key -out certificate.crt -days 365 -subj "/CN=MyCompany/O=MyCompany/L=City/C=US"
```

### Step 3: Combine the key and certificate into a PKCS#12 keystore
Combine the private key and certificate into a PKCS#12 file for use as a keystore. You'll need to set an alias and a password for the keystore. 

```bash
openssl pkcs12 -export -in certificate.crt -inkey private.key -out keystore.p12 -name "signing_alias" -passout pass:your_password
```

## Building executable with Maven

This section describes how to build your Java application using [Apache Maven](https://maven.apache.org/), prior to packaging it into an AppImage.


### Install Maven (if not already installed)

On Debian/Ubuntu-based systems:

```bash
sudo apt update
sudo apt install maven
```

On Fedora:

```bash
sudo dnf install maven
```

To verify installation:

```bash
mvn -v
```

### Build the Project

Navigate to the root of your Maven project (where the `pom.xml` file is located), then run:

```bash
cd OpenUniverse
mvn clean
```

This will:

* Clean any previous builds
* Compile the project
* Run tests (if defined)
* Package the output into a JAR file

The final JAR will typically be located in the `target/` directory:

```
target/ou.jar
```

You can now include this JAR in your `AppDir` under `usr/bin/` or another suitable location.

### Build Without Running Tests (Optional)

If you want to skip tests during packaging:

```bash
mvn clean install -DskipTests
```

### References

* [Introduction to the POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html)
* [Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)


## Create, sign and verify AppImage (Optional)

This guide demonstrates how to create an AppImage, generate and use a GPG key to sign it, and validate the embedded signature.

### What is AppImage?

[AppImage](https://appimage.org/) is a format for packaging Linux applications into a single, self-contained executable. It works on most major distributions without installation, root permissions, or dependency conflicts.

AppImages are ideal for distributing portable applications with a native experience.

### Prepare Your AppDir Structure

An AppImage is built from a directory known as an **AppDir**. This directory must have a specific structure:

```
OpenUniverse.AppDir/
├── AppRun       # Launcher script or binary (entry point)
├── ou.desktop   # Desktop entry file (defines name, icon, exec)
├── ou.png       # Icon file (PNG format)
├── ou           # Executable
└── jre          # Optional JRE directory

```

### Minimum Required Files:

* `AppRun`: The executable or script to launch the app. Must be executable.
* `ou.desktop`: Standard desktop entry file (must match the AppImage name).
* `ou.png`: Icon used for desktop integration.

> See full spec: [https://docs.appimage.org/reference/appdir.html](https://docs.appimage.org/reference/appdir.html)

### Create the AppImage

Once your `OpenUniverse.AppDir` is prepared, package it:

```bash
ARCH=x86_64 ./appimagetool-x86_64.AppImage OpenUniverse.AppDir ou-linux-x86_64
```

This will create `ou-linux-x86_64` AppImage executable.

### Generate a GPG Key in Batch Mode

Generate a secure GPG key pair using the following command:

```bash
gpg --batch --passphrase 'YOUR_STRONG_PASSWORD' --pinentry-mode loopback --gen-key <(echo -e 'Key-Type: RSA\nKey-Length: 4096\nSubkey-Type: RSA\nSubkey-Length: 4096\nName-Real: YourName\nName-Email: your-mail@example.com\nExpire-Date: 0\n%commit')
```

> ⚠️ Replace `'YOUR_STRONG_PASSWORD'` with a secure passphrase.<br>
> ⚠️ Replace `YourName` with your name.<br>
> ⚠️ Replace `your-mail@example.com` with your email address.

### List Your Secret Keys

To find your signing key's fingerprint:

```bash
gpg --list-secret-keys
```

Look for the fingerprint (40-character hex) and copy it for use in the next step.

### Sign Your AppImage

Sign the AppImage using the generated GPG key:

```bash
ARCH=x86_64 ./appimagetool-x86_64.AppImage ou-linux-x86_64 --sign --sign-key YOUR_40_CHARACTER_HEX_FINGERPRINT
```

> Replace the key fingerprint with your actual key's fingerprint.

### Display the Embedded Signature

Inspect the embedded GPG signature:

```bash
./ou-linux-x86_64 --appimage-signature
```

### Validate the Signature

To validate the AppImage signature, download the validation tool:

```bash
wget https://github.com/AppImageCommunity/AppImageUpdate/releases/download/continuous/validate-x86_64.AppImage
chmod +x validate-x86_64.AppImage
```
Continuous build Pre-release
Build log: https://github.com/AppImageCommunity/AppImageUpdate/actions/runs/15079240494

Validators for other platforms:

https://github.com/AppImageCommunity/AppImageUpdate/releases/download/continuous/validate-aarch64.AppImage
https://github.com/AppImageCommunity/AppImageUpdate/releases/download/continuous/validate-armhf.AppImage
https://github.com/AppImageCommunity/AppImageUpdate/releases/download/continuous/validate-i686.AppImage
https://github.com/AppImageCommunity/AppImageUpdate/releases/download/continuous/validate-x86_64.AppImage


```bash
./validate-x86_64.AppImage ou-linux-x86_64
```

A successful validation confirms the AppImage was signed by a trusted key and hasn't been modified.

### References

* [AppImage Official Site](https://appimage.org/)
* [AppDir Specification](https://docs.appimage.org/reference/appdir.html)
* [appimagetool Usage](https://docs.appimage.org/reference/appimagetool.html)
* [AppImage Signing](https://docs.appimage.org/packaging-guide/optional/signatures.html#signing-appimages)
* [AppImage Reading the signature](https://docs.appimage.org/packaging-guide/optional/signatures.html#reading-the-signature)
* [AppImage Validating the signature](https://docs.appimage.org/packaging-guide/optional/signatures.html#validating-the-signature)
* [GPG Key Management](https://gnupg.org/documentation/)
