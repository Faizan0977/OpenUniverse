/*
 * The MIT License
 * Copyright © 2025 OpenUniverse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ou.common.utils;

import java.util.List;

import org.ou.common.constants.IMessageDigestAlg;

import com.eternitywall.ots.DetachedTimestampFile;
import com.eternitywall.ots.Hash;
import com.eternitywall.ots.OpenTimestamps;
import com.eternitywall.ots.op.OpSHA256;

public class OtsUtils {
    public static byte[] stamp(byte[] bs, List<String> calendarsUrls, Integer quorum) throws Exception {
        byte[] digest = CommonUtils.getMessageDigest(IMessageDigestAlg.SHA256).digest(bs);
        DetachedTimestampFile detached = DetachedTimestampFile.from(new Hash(digest, OpSHA256._TAG));
        OpenTimestamps.stamp(detached, calendarsUrls, quorum);
        return detached.serialize();
    }
}
