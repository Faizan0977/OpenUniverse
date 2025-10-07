# Command-line instruction for decrypting an encrypted text.

This field stores a shell command that can be executed to decrypt a given
encrypted string using various encryption tools such as GPG, OpenSSL, or the
UNIX password manager (pass). The command should be structured in a way that it
can be run directly in a terminal.

**Placeholder Variables:**

- `SECRET_TEXT`: The encrypted string that needs to be decrypted.
- `MySecretPassword`: The secret passphrase or key used for decryption.

Examples of command-line encryption and decryption:

## GPG (GNU Privacy Guard)

Encrypt a message using GPG (Symmetric Encryption with AES-256):

```bash
echo -n "Hello, World!" | gpg --symmetric --cipher-algo AES256 --passphrase "MySecretPassword" --batch --yes --quiet --output - 2>/dev/null | base64 | tr -d '\n'
```

<br>

Decrypt the message using GPG:

```bash
base64 -d <<< "SECRET_TEXT" | gpg --decrypt --passphrase "MySecretPassword" --batch --yes --quiet 2>/dev/null
```

## OpenSSL

Encrypt a message using OpenSSL (AES-256-CBC):

```bash
echo -n "Hello, World!" | openssl enc -aes-256-cbc -salt -base64 -pass pass:"MySecretPassword" 2>/dev/null | tr -d '\n'
```

<br>

Decrypt the message using OpenSSL:

```bash
echo "SECRET_TEXT" | openssl enc -aes-256-cbc -d -base64 -pass pass:"MySecretPassword" 2>/dev/null
```

## PASS (Standard UNIX Password Manager)

Retrieve a stored password using pass:

```bash
pass "SECRET_TEXT" 2>/dev/null | tr -d '\n'
```
