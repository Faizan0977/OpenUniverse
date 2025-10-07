package org.ou.common.utils;

import org.bouncycastle.openpgp.PGPSignature;

public class CommitSignInfo {

    public byte[] signedData;
    public PGPSignature pgpSignature;
    public String issuerKeyIdHex;
    public boolean issuerKeyIdTrusted;
}
