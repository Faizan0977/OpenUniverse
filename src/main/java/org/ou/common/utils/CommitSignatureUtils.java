package org.ou.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.KeyIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.util.encoders.Hex;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class CommitSignatureUtils {

    private static final String BC_PROVIDER = "BC";

    static {
        if (Security.getProvider(BC_PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Checks if the repository is configured to sign commits by default.
     *
     * @param repo the JGit repository
     * @return true if commit.gpgsign is set to true, false otherwise
     */
    public static boolean isRepoSigningCommits(Repository repo) {
        StoredConfig config = repo.getConfig();
        return config.getBoolean("commit", null, "gpgsign", false);
    }

    /**
     * Exports all public keys from the system GnuPG keybox as raw OpenPGP
     * bytes.
     *
     * @return byte[] containing the exported keyring (binary OpenPGP format)
     * @throws IOException if gpg fails or I/O error
     * @throws InterruptedException if process is interrupted
     */
    public static byte[] exportPublicKeys() throws IOException, InterruptedException {
        List<String> command = Arrays.asList("gpg", "--batch", "--yes", "--export");

        ProcessBuilder pb = new ProcessBuilder(command);
        //pb.redirectErrorStream(true);

        Process process = pb.start();

        try (InputStream in = process.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            in.transferTo(out);

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("gpg export failed with exit code " + exitCode);
            }

            return out.toByteArray();
        }
    }

    /**
     * Returns the raw GPG signature block of a commit (if present).
     *
     * @param repo JGit repository
     * @param commitRef commit id or ref (e.g. "HEAD")
     * @return byte[] containing the raw GPG signature block, or null if not
     * signed
     */
    public static CommitSignInfo getCommitSignInfo(Repository repo, String commitRef) throws IOException {
        ObjectId commitId = repo.resolve(commitRef);
        if (commitId == null) {
            throw new IllegalArgumentException("Commit " + commitRef + " not found");
        }

        try (RevWalk walk = new RevWalk(repo)) {
            RevCommit commit = walk.parseCommit(commitId);

            CommitSignInfo commitSignInfo = new CommitSignInfo();
            commitSignInfo.signedData = extractSignedData(commit.getRawBuffer());
            byte[] rawGpgSignature = commit.getRawGpgSignature();

            // Decode ASCII-armored signature
            //InputStream in = PGPUtil.getDecoderStream(new ByteArrayInputStream(rawGpgSignature));
            try (InputStream in = new ArmoredInputStream(new ByteArrayInputStream(rawGpgSignature))) {
                PGPObjectFactory pgpFact = new PGPObjectFactory(in, new JcaKeyFingerprintCalculator());
                Object obj = pgpFact.nextObject();
                PGPSignatureList sigList = (obj instanceof PGPSignatureList)
                        ? (PGPSignatureList) obj
                        : ((PGPSignatureList) ((PGPObjectFactory) obj).nextObject());

                if (!sigList.isEmpty()) {
                    commitSignInfo.pgpSignature = sigList.get(0);
                    commitSignInfo.issuerKeyIdHex = issuerIdHex(commitSignInfo.pgpSignature);
                    commitSignInfo.issuerKeyIdTrusted = isTrustedStrict(commitSignInfo.issuerKeyIdHex);
                }
            }
            return commitSignInfo;
        }
    }

    /**
     * Verifies a commit's GPG signature against the given pubring file.
     *
     * @param repository JGit repository
     * @param commitRef commit hash or ref (e.g. "HEAD")
     * @param pubringPath path to GPG public keyring file (e.g. exported
     * pubring.gpg)
     * @return true if commit is signed and valid, false otherwise
     */
    public static boolean verifyCommitSignature(CommitSignInfo commitSignInfo, byte[] keyring) {
        try (InputStream in = new ByteArrayInputStream(keyring)) {

            // Load pubring
            PGPPublicKeyRingCollection pubRing = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream(in),
                    new JcaKeyFingerprintCalculator()
            );

            PGPSignature pgpSignature = commitSignInfo.pgpSignature;

            // Find public key
            PGPPublicKey key = pubRing.getPublicKey(pgpSignature.getKeyID());
            if (key == null) {
                System.err.println("No public key found for keyID: " + Long.toHexString(pgpSignature.getKeyID()));
                return false;
            }

            // Init verifier
            pgpSignature.init(new JcaPGPContentVerifierBuilderProvider().setProvider(BC_PROVIDER), key);

            // Update with commit’s signed data
            pgpSignature.update(commitSignInfo.signedData);

            // Verify
            boolean verified = pgpSignature.verify();
            return verified;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the canonical commit bytes for signature verification. This
     * strips the 'gpgsig' block entirely, preserving Git's signed content.
     *
     * @param commit JGit commit
     * @return byte[] canonical commit data
     */
    private static byte[] extractSignedData(byte[] raw) {
        String text = new String(raw, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();

        boolean skippingSig = false;

        for (String line : text.split("\n", -1)) {
            if (line.startsWith("gpgsig ")) {
                skippingSig = true; // start skipping signature
                continue;
            }
            if (skippingSig) {
                if (line.startsWith(" ")) {
                    continue; // skip indented signature lines
                } else {
                    skippingSig = false; // end of signature block
                }
            }
            sb.append(line).append("\n"); // keep canonical LF endings
        }
        return sb.toString().substring(0, sb.length() - 1).getBytes(StandardCharsets.UTF_8);
    }

    private static String issuerIdHex(PGPSignature sig) {
        List<KeyIdentifier> ids = sig.getHashedKeyIdentifiers();
        if (ids.isEmpty()) {
            ids = sig.getUnhashedKeyIdentifiers();
        }
        if (!ids.isEmpty()) {
            KeyIdentifier kid = ids.get(0);
            byte[] fp = kid.getFingerprint();          // preferred: full fingerprint
            if (fp != null) {
                return Hex.toHexString(fp);
            }
            long id = kid.getKeyId();                  // fallback: 64-bit key-id
            return String.format("%016x", id);
        }
        return String.format("%016x", sig.getKeyID());
    }

    private  enum KeyStatus {
        VALID_TRUSTED,
        VALID_UNTRUSTED,
        REVOKED,
        EXPIRED,
        DISABLED,
        NOT_FOUND,
        ERROR
    }

    /**
     * Checks the trust level of a key in the local GPG keyring.
     * Strict policy: only u or f are trusted.
     *
     * @param fingerprint hex fingerprint (no spaces)
     * @return KeyStatus
     */
    private static KeyStatus checkKeyStatus(String fingerprint) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "gpg", "--batch", "--with-colons", "--list-keys", fingerprint
            );
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("pub:")) {
                        String[] parts = line.split(":");
                        if (parts.length < 2) continue;

                        String validity = parts[1]; // second field = trust/validity

                        switch (validity) {
                            case "r": return KeyStatus.REVOKED;
                            case "e": return KeyStatus.EXPIRED;
                            case "d": return KeyStatus.DISABLED;
                            case "u": // ultimately trusted
                            case "f": // fully trusted
                                return KeyStatus.VALID_TRUSTED;
                            default: // n, m, q, or anything else
                                return KeyStatus.VALID_UNTRUSTED;
                        }
                    }
                }
            }

            int exit = p.waitFor();
            if (exit != 0) return KeyStatus.NOT_FOUND;

        } catch (Exception e) {
            e.printStackTrace();
            return KeyStatus.ERROR;
        }
        return KeyStatus.NOT_FOUND;
    }

    // Convenience method
    public static boolean isTrustedStrict(String fingerprint) {
        return checkKeyStatus(fingerprint) == KeyStatus.VALID_TRUSTED;
    }

}
/*
# Generate key if you don’t have one
gpg --full-generate-key

# Tell Git which key to use
git config user.signingkey <your-key-id (full 40-character fingerprint)>

# Enable commit signing (optional, per-user)
git config commit.gpgsign true

# Sign a commit explicitly
git commit -S -m "My signed commit"

# Verification:
git log --show-signature
git show --show-signature HEAD

# Show commit with signature block
git cat-file -p HEAD
git cat-file -p <commit-sha>

# Issuer key ID
git cat-file commit <sha> | gpg --list-packets

# Verify commit signature against your pubring
git verify-commit <commit>

# list all your keys
gpg --list-secret-keys --keyid-format=long

# In modern GnuPG, the pubring.kbx format is default — you’d need to export (in ASCII armor) your key first:
gpg --export --armor <your-key-id> > pubring.gpg

# Show the key ID used to sign the commit
git cat-file commit HEAD \
  | sed -n '/^gpgsig /,/^$/p' \
  | sed 's/^gpgsig //; s/^ //' \
  | gpg --list-packets


===========
tree e1ba8590864b9dbf7b68c57ee2304f9347d8a6d2
parent 33186bb6d90b03897b8f462e8f42d6378c436d1d
author Your Name <you@example.com> 1757366164 +0300
committer Your Name <you@example.com> 1757366164 +0300
gpgsig -----BEGIN PGP SIGNATURE-----
 
 iQGzBAABCgAdFiEEjJAKWj+1NWaEvKECyjLYEdye+nQFAmi/R5QACgkQyjLYEdye
 +nRFwAwAgiWo8e9zb5ndfEa7blTOrSGKgNj/BZW7r1Ar6POADm6IZc+WwzKDyy+N
 SP2t28ZebkazKRD09eDC+NurpiIRozk9XmLvAwbTN2z51BWlp0nhDg4XbhqiqWrm
 te4C7DWataCB/pEPljAqd3vsGxucSx7jA/1qBfEGTdU4i6jEbmTj9bMAqkpezoOV
 jVN/oTYEgP6XJaESJsp61kix5QLR9GkXw6LJTrHziSXNJcB2tC5R8r3+iiJg4K7O
 htMFTXErOQ6R0Q1AtIwVxNO+36rORxxoPkCBYbb074uiuj7TuOJeDDsnaMyB/jQi
 fg3DqrH7LR4trfY5f+vR7iSLfy0JSiF/qXf9nGnio2PXSVCqwhcB9obZGgqAc8/x
 cE7kXXU+qx6HX+Mm+GcXBPZmL/FTDBjp86rBEoLjLEzDWz6P1BBJeSHN4rHOIpSY
 6m6T3AS73+B96UCy+vnpHgACGKNgs7hM2VySbDTw4UxU63uJfj7oVrP4N57QrQ07
 MKWcl1SQ
 =CKVb
 -----END PGP SIGNATURE-----

My signed commit
 */
