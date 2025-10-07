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
package org.ou.indexer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.eclipse.jgit.ignore.IgnoreNode;
import org.ou.common.constants.IMsg;
import org.ou.common.utils.CommonUtils;
import org.ou.to.AbstractTo;
import org.ou.to.HeadTo;

/**
 * <p>
 * DirectoryTreeProcessor class.</p>
 *
 *
 * @since 1.0.21
 */
public class DirectoryTreeProcessor {

    private static class IgnoreNodeEntry {

        final Path baseDir;
        final IgnoreNode node;

        IgnoreNodeEntry(Path baseDir, IgnoreNode node) {
            this.baseDir = baseDir;
            this.node = node;
        }
    }

    /**
     * <p>
     * processDocs.</p>
     *
     * @param solrClient a {@link org.apache.solr.client.solrj.SolrClient}
     * object
     * @param rootDir a {@link java.nio.file.Path} object
     * @param defaultPropertiesMap a {@link java.util.Map} object
     * @return a {@link java.util.Map} object
     * @throws java.lang.Exception if any.
     */
    public static Map<String /* doc key */, AbstractTo> processDocs(SolrClient solrClient, Path rootDir, Map<String, Object> defaultPropertiesMap) throws Exception {
        IFileProcessor fileProcessor = new IndexerFileProcessor(rootDir, defaultPropertiesMap);
        Map<String /* doc key */, AbstractTo> abstractTos = new HashMap<>();
        try {
            processDirectoryTree(rootDir, new ArrayList<IgnoreNodeEntry>(), fileProcessor, abstractTos, defaultPropertiesMap);
        } catch (Exception e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }
        for (AbstractTo abstractTo : abstractTos.values()) {
            SolrInputDocument sid = convert(abstractTo);
            solrClient.add(sid);
        }
        solrClient.commit();
        return abstractTos;
    }

    private static void processDirectoryTree(
            Path filePath,
            List<IgnoreNodeEntry> ignoreStack, // stores node + its base dir
            IFileProcessor fileProcessor,
            Map<String /* doc key */, AbstractTo> abstractTos,
            Map<String, Object> propertiesMap) throws Exception {

        boolean isDir = Files.isDirectory(filePath);

        // check ignore rules from child to parent
        for (int i = ignoreStack.size() - 1; i >= 0; i--) {
            IgnoreNodeEntry entry = ignoreStack.get(i);
            Path rel = entry.baseDir.relativize(filePath).normalize();
            String relStr = rel.toString().replace(File.separator, "/");
            IgnoreNode.MatchResult r = entry.node.isIgnored(relStr, isDir);
            if (r == IgnoreNode.MatchResult.IGNORED) {
                return;
            }
            if (r == IgnoreNode.MatchResult.NOT_IGNORED) {
                break;
            }
        }

        if (isDir) {
            // load this directory's .ouignore if exists
            Path ouignoreFile = filePath.resolve(".ouignore");
            IgnoreNodeEntry newEntry = null;
            if (Files.isRegularFile(ouignoreFile)) {
                IgnoreNode node = new IgnoreNode();
                try (InputStream in = Files.newInputStream(ouignoreFile)) {
                    node.parse(in);
                }
                newEntry = new IgnoreNodeEntry(filePath, node);
                ignoreStack.add(newEntry);
            }

            try (var stream = Files.list(filePath)) {
                stream.forEach(entry -> {
                    try {
                        processDirectoryTree(entry, ignoreStack, fileProcessor, abstractTos, propertiesMap);
                    } catch (Exception e) {
                        System.err.println("Error reading entry: " + e.getMessage());
                    }
                });
            }

            if (newEntry != null) {
                ignoreStack.remove(ignoreStack.size() - 1);
            }

        } else {
            // process file
            Map<String, AbstractTo> parsed = fileProcessor.process(filePath);
            for (var entry : parsed.entrySet()) {
                String docKey = entry.getKey();
                AbstractTo abstractTo = entry.getValue();
                if (abstractTos.containsKey(docKey)) {
                    CommonUtils.exitWithMsg(IMsg.DUPLICATED_KEY_IN_DOC, abstractTo.headTo.docFile);
                    return;
                }
                abstractTos.put(docKey, abstractTo);
            }
        }
    }

    private static SolrInputDocument convert(AbstractTo abstractTo) throws Exception {
        SolrInputDocument solrDoc = new SolrInputDocument();

        HeadTo headTo = abstractTo.headTo;
        String docKey = DocKeyUtils.createDocKey(headTo.docType, headTo.name);

        solrDoc.setField("doc_key", docKey);
        solrDoc.setField("doc_type", headTo.docType);
        solrDoc.setField("doc_file", headTo.docFile);

        solrDoc.setField("name", headTo.name);
        if (headTo.description != null) {
            solrDoc.setField("description", headTo.description);
        }
        if (headTo.tags != null) {
            solrDoc.setField("tags", headTo.tags);
        }
        if (headTo.attr != null) {
            for (Map.Entry<String, Object> entry : headTo.attr.entrySet()) {
                String key = "attr." + entry.getKey();
                Object value = entry.getValue();
                solrDoc.setField(key, value);
            }
        }
        return solrDoc;
    }

}
