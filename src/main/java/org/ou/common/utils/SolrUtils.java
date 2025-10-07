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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;

/**
 * <p>
 * SolrUtils class.</p>
 *

 * @since 1.0.21
 */
public class SolrUtils {

    /**
     * <p>
     * createSolrHome.</p>
     *
     * @param solrHome a {@link java.nio.file.Path} object
     * @param coreName a {@link java.lang.String} object
     * @param solrConfigXmlRes a {@link java.lang.String} object
     * @param solrSchemaXmlRes a {@link java.lang.String} object
     * @throws java.lang.Exception if any.
     */
    public static void createSolrHome(Path solrHome, String coreName, String solrConfigXmlRes, String solrSchemaXmlRes) throws Exception {
        Path solrCorePath = solrHome.resolve(coreName);
        Files.createDirectories(solrCorePath);
        Path confPath = solrHome.resolve("conf");
        Files.createDirectories(confPath);
        try (InputStream is = SolrUtils.class.getClassLoader().getResourceAsStream(solrConfigXmlRes)) {
            Files.copy(is, solrHome.resolve("conf/solrconfig.xml"), StandardCopyOption.REPLACE_EXISTING);
        }
        try (InputStream is = SolrUtils.class.getClassLoader().getResourceAsStream(solrSchemaXmlRes)) {
            Files.copy(is, solrHome.resolve("conf/schema.xml"), StandardCopyOption.REPLACE_EXISTING);
        }
        try (SolrClient sc = startSolrClient(solrHome, coreName)) {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName(coreName);
            createRequest.setConfigSet(confPath.toString());
            sc.request(createRequest);
        }
    }

    /**
     * <p>
     * startSolrClient.</p>
     *
     * @param solrHome a {@link java.nio.file.Path} object
     * @param coreName a {@link java.lang.String} object
     * @return a {@link org.apache.solr.client.solrj.SolrClient} object
     */
    public static SolrClient startSolrClient(Path solrHome, String coreName) {
        NodeConfig config = new NodeConfig.NodeConfigBuilder(null, solrHome).build();
        return new EmbeddedSolrServer(config, coreName);
    }
}
