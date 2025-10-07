#!/bin/sh

#########################################
# ⚠️ WARNING! NOT FOR PRODUCTION USAGE! #
#########################################

# Quick and dirty script to create a self-signed Root CA and a code signing certificate.

# This script creates a self-signed Root CA and a code signing certificate,
# then exports them into a PKCS12 keystore for use with jarsigner or similar tools.
# Requires OpenSSL to be installed and available in PATH.

set -e

# Output directory for generated files
# In this example, we use an absolute path in the user's home directory.
# Make sure the path is correct for your environment.

OUTDIR="$1"
mkdir -p "$OUTDIR"

# Keystore related parameters
# Adjust these as needed
# ⚠️ Make sure to keep your keystore and passwords secure!
# ⚠️ Never commit your keystore or passwords to version control!

KEYSTORE="$OUTDIR/keystore.p12"
STOREPASS="your_password"
ALIAS="signing_alias"
KEYPASS="your_password"

SUBJ_ROOT_CA="/C=US/ST=State/L=City/O=MyCompany/CN=MyCompany Root CA"
SUBJ="/C=US/ST=State/L=City/O=MyCompany/CN=MyCompany Code Signing"


# Generate private key for Root CA (unencrypted, since it's internal)
openssl genrsa -out "$OUTDIR/rootCA.key" 4096

# Create self-signed Root CA certificate (valid 10 years)
openssl req -x509 -new -nodes -key "$OUTDIR/rootCA.key" -sha256 -days 3650 \
  -out "$OUTDIR/rootCA.crt" \
  -subj "$SUBJ_ROOT_CA"

# Generate encrypted private key for code signing
openssl genrsa -aes256 -passout pass:"$KEYPASS" -out "$OUTDIR/codesign.key" 2048

# Create CSR for code signing cert
openssl req -new -key "$OUTDIR/codesign.key" -out "$OUTDIR/codesign.csr" \
  -subj "$SUBJ" -passin pass:"$KEYPASS"

# Config file for code signing extensions
cat > "$OUTDIR/codesign.ext" <<EOF
basicConstraints=CA:FALSE
keyUsage = digitalSignature
extendedKeyUsage = codeSigning
EOF

# Sign CSR with Root CA (valid ~2 years)
openssl x509 -req -in "$OUTDIR/codesign.csr" \
  -CA "$OUTDIR/rootCA.crt" -CAkey "$OUTDIR/rootCA.key" -CAcreateserial \
  -out "$OUTDIR/codesign.crt" -days 825 -sha256 -extfile "$OUTDIR/codesign.ext"

# Export into PKCS#12 keystore (separate KEYPASS + STOREPASS)
openssl pkcs12 -export \
  -in "$OUTDIR/codesign.crt" \
  -inkey "$OUTDIR/codesign.key" \
  -certfile "$OUTDIR/rootCA.crt" \
  -out "$KEYSTORE" \
  -name "$ALIAS" \
  -passout pass:"$STOREPASS" \
  -passin pass:"$KEYPASS"

echo "Certificates and keystore generated in $OUTDIR"
echo "Keystore file: $KEYSTORE"

# ⚠️ Note: This script is for demonstration purposes only and should not be used in production environments.
# In production, use a trusted CA to issue your code signing certificates.
# Also, protect your private keys and passwords appropriately.
# Make sure to adjust the subject details and passwords in keystore.properties before running.

# EOF