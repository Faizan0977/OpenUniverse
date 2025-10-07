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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ou.common.constants.IConstants;
import org.ou.common.constants.IDocConst;
import org.ou.common.constants.IDocSpec;
import org.ou.common.constants.IDocTypes;
import org.ou.common.constants.IMessageDigestAlg;
import org.ou.common.constants.IMsg;
import org.ou.common.utils.CommonUtils;
import org.ou.common.utils.GitUtils;
import org.ou.to.AbstractTo;
import org.ou.to.CalendarDocTo;
import org.ou.to.CommandTo;
import org.ou.to.EventDocTo;
import org.ou.to.EventPublisherDocTo;
import org.ou.to.ExportSettings;
import org.ou.to.HeadTo;
import org.ou.to.JobDocTo;
import org.ou.to.ProcessorDocTo;
import org.ou.to.RootDocTo;
import org.ou.to.SchedulerDocTo;
import org.ou.to.SystemDocTo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * IndexerFileProcessor class.</p>
 *
 *
 * @since 1.0.21
 */
public class IndexerFileProcessor implements IFileProcessor {

    public Path rootPath;
    private final Map<String, Object> defaultPropertiesMap;

    /**
     * <p>
     * Constructor for IndexerFileProcessor.</p>
     *
     * @param rootPath a {@link java.nio.file.Path} object
     * @param defaultPropertiesMap a {@link java.util.Map} object
     */
    public IndexerFileProcessor(Path rootPath, Map<String, Object> defaultPropertiesMap) {
        this.rootPath = rootPath;
        this.defaultPropertiesMap = defaultPropertiesMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String /* doc key */, AbstractTo> process(Path filePath) throws Exception {
        String docFile = rootPath.relativize(filePath).toString();
        ObjectMapper ompr = CommonUtils.getObjectMapperByFileExt(docFile);
        if (ompr == null) {
            return new HashMap<>(0);
        }
        byte[] docBs = Files.readAllBytes(filePath);
        String docBlobSha1 = GitUtils.computeGitBlobSHA1(docBs);
        //String docSha1 = GitUtils.computeSHA1(docBs);
        String docSha1 = CommonUtils.digest(IMessageDigestAlg.SHA1, docBs);

        Object object = ompr.readValue(docBs, Object.class);
        List<Map<String, Object>> listOfMaps = CommonUtils.toList(object);
        Map<String /* doc id */, AbstractTo> abstractTos = new HashMap<>(listOfMaps.size());
        for (int i = 0; i < listOfMaps.size(); i++) {
            Map<String, Object> map = listOfMaps.get(i);
            HeadTo headTo = new HeadTo();
            headTo.docFile = docFile;
            headTo.docBlobSha1 = docBlobSha1;
            headTo.docSha1 = docSha1;
            headTo.indexInFile = i;
            AbstractTo abstractTo = parseDoc(headTo, map);
            if (abstractTo != null) {
                String docKey = DocKeyUtils.createDocKey(headTo.docType, headTo.name);
                if (IDocTypes.DOC_TYPE_ROOT.equals(headTo.docType) && !IConstants.ROOT_DOC_NAME.equals(headTo.name)) {
                    continue;
                }
                if (abstractTos.containsKey(docKey)) {
                    CommonUtils.exitWithMsg(IMsg.DUPLICATED_KEY_IN_FILE, headTo.docFile, headTo.indexInFile);
                    return null;
                }
                abstractTos.put(docKey, abstractTo);
            }
        }
        return abstractTos;
    }

    private AbstractTo parseDoc(HeadTo headTo, Map<String, Object> map) throws Exception {
        Map<String, Object> propertiesMap = (Map) map.get("properties");
        if (propertiesMap == null) {
            propertiesMap = new HashMap<>();
        }
        Map<String, Object> tmpPropertiesMap = new HashMap<>(defaultPropertiesMap);
        tmpPropertiesMap.putAll(propertiesMap);
        propertiesMap.putAll(tmpPropertiesMap);

        Map<String, Object> headMap = (Map) map.get("head");
        if (headMap == null) {
            return null;
        }
        
        String docSpec = (String) headMap.get(IDocConst.HEAD_DOC_SPEC);
        if (docSpec == null) {
            return null;
        }
        if (!IDocSpec.ALLOWED_DOC_SPECS.contains(docSpec)) {
            return null;
        }
        headTo.docSpec = docSpec;

        String docType = (String) headMap.get(IDocConst.HEAD_DOC_TYPE);
        if (docType == null) {
            return null;
        }
        headTo.docType = docType;
        headTo.name = (String) headMap.get(IDocConst.HEAD_DOC_NAME);
        if (headTo.name == null) {
            return null;
        }

        headTo.disabled = (Boolean) headMap.get("disabled");
        if (headTo.disabled == null) {
            headTo.disabled = false;
        }
        if (headTo.disabled) {
            return null;
        }

        headTo.description = (String) headMap.get("description");
        headTo.tags = (Collection<String>) headMap.get("tags");
        headTo.attr = (Map) headMap.get("attr");

        List<Map<String, Object>> validatorCommandMaps = (List) headMap.get("validator_сommands");
        if (validatorCommandMaps != null) {
            headTo.validatorCommands = new ArrayList<>();
            for (Map<String, Object> validatorCommandMap : validatorCommandMaps) {
                CommandTo commandTo = new CommandTo();
                fillCommandSettings(commandTo, validatorCommandMap);
                headTo.validatorCommands.add(commandTo);
            }
        }

        switch (docType) {
            case IDocTypes.DOC_TYPE_ROOT:
                RootDocTo rootTo = new RootDocTo();
                rootTo.headTo = headTo;
                rootTo.properties.putAll(propertiesMap);
                rootTo.jobs = (String) map.get("jobs");
                rootTo.triggers = (String) map.get("triggers");
                rootTo.signSettingsMap = (Map) map.get("sign");
                rootTo.timestampSettingsMap = (Map) map.get("timestamp");
                rootTo.otsSettingsMap = (Map) map.get("ots");
                rootTo.ntpServersList = (List) map.get("ntp_servers");
                rootTo.stunServersList = (List) map.get("stun_servers");
                Collection<Map<String, Object>> dmqCommandsListOfMaps = (Collection) map.get("export_dmqs");
                if (dmqCommandsListOfMaps != null) {
                    for (Map<String, Object> dmqCommandMap : dmqCommandsListOfMaps) {
                        CommandTo dmqCommandTo = new CommandTo();
                        fillCommandSettings(dmqCommandTo, dmqCommandMap);
                        rootTo.exportDmqCommandTos.add(dmqCommandTo);
                    }
                }
                Collection<Map<String, Object>> exportTargetsListOfMaps = (Collection) map.get("export_targets");
                if (exportTargetsListOfMaps != null) {
                    rootTo.exportTargets = parseExportTargetsJson(exportTargetsListOfMaps);
                }
                String hashAlgorithm = (String) map.get("hash_algorithm");
                if (hashAlgorithm != null) {
                    rootTo.hashAlgorithm = hashAlgorithm;
                }
                Map<String, Object> decryptCommandMap = (Map) map.get("decrypt_command");
                if (decryptCommandMap != null) {
                    if (rootTo.decryptCommandTo == null) {
                        rootTo.decryptCommandTo = new CommandTo();
                        fillCommandSettings(rootTo.decryptCommandTo, decryptCommandMap);
                    }
                }
                return rootTo;
            case IDocTypes.DOC_TYPE_EVENT:
                EventDocTo eventTo = new EventDocTo();
                eventTo.headTo = headTo;
                Boolean processable = (Boolean) map.get("processable");
                if (processable != null) {
                    eventTo.processable = processable;
                }
                eventTo.properties.putAll(propertiesMap);
                return eventTo;
            case IDocTypes.DOC_TYPE_JOB:
                JobDocTo jobTo = new JobDocTo();
                jobTo.headTo = headTo;
                jobTo.properties.putAll(propertiesMap);
                jobTo.processors = (String) map.get("processors");
                jobTo.systems = (String) map.get("systems");
                return jobTo;
            case IDocTypes.DOC_TYPE_SYSTEM:
                SystemDocTo systemTo = new SystemDocTo();
                systemTo.headTo = headTo;
                systemTo.properties.putAll(propertiesMap);
                systemTo.systemDefJson = map.get("system_def");
                return systemTo;
            case IDocTypes.DOC_TYPE_EVENT_PROCESSOR:
                ProcessorDocTo processorTo = new ProcessorDocTo();
                processorTo.headTo = headTo;
                processorTo.properties.putAll(propertiesMap);
                Map<String, Object> processorCommandMap = (Map) map.get("command");
                fillCommandSettings(processorTo.commandTo, processorCommandMap);
                return processorTo;
            case IDocTypes.DOC_TYPE_TRIGGERS_EVENT_PUBLISHER:
                EventPublisherDocTo eventPublisherDocTo = new EventPublisherDocTo();
                eventPublisherDocTo.headTo = headTo;
                eventPublisherDocTo.properties.putAll(propertiesMap);
                Map<String, Object> triggerCommandMap = (Map) map.get("command");
                fillCommandSettings(eventPublisherDocTo.commandTo, triggerCommandMap);
                return eventPublisherDocTo;
            case IDocTypes.DOC_TYPE_TRIGGERS_EVENT_CALENDAR:
                CalendarDocTo calendarTo = new CalendarDocTo();
                calendarTo.headTo = headTo;
                calendarTo.properties.putAll(propertiesMap);
                Object calendarObj = map.get("calendars_list");
                calendarTo.calendarList = CommonUtils.toList(calendarObj);
                return calendarTo;
            case IDocTypes.DOC_TYPE_TRIGGERS_EVENT_SCHEDULER:
                SchedulerDocTo schedulerTo = new SchedulerDocTo();
                schedulerTo.headTo = headTo;
                schedulerTo.properties.putAll(propertiesMap);
                Object schedulerObj = map.get("schedulers_list");
                schedulerTo.schedulerList = CommonUtils.toList(schedulerObj);
                return schedulerTo;
            default:
                throw new IllegalArgumentException(docType);
        }
    }

    private static Collection<ExportSettings> parseExportTargetsJson(Collection<Map<String, Object>> exportTargetsListOfMaps) throws Exception {
        Collection<ExportSettings> list = new ArrayList<>();
        for (Map<String, Object> exportTarget : exportTargetsListOfMaps) {
            String id = (String) exportTarget.get("id");
            if (id == null) {
                continue; // id required!
            }
            Boolean disabled = (Boolean) exportTarget.get("disabled");
            if (disabled != null && disabled) {
                continue;
            }

            ExportSettings exportSettings = new ExportSettings();
            fillExportSettings(exportSettings, exportTarget);
            list.add(exportSettings);
        }
        return list;
    }

    private static void fillCommandSettings(CommandTo commandTo, Map<String, Object> map) {
        Integer instancesCount = (Integer) map.get("instances_count");
        if (instancesCount != null) {
            commandTo.instancesCount = instancesCount;
        }

        commandTo.cmd = (String) map.get("cmd");
        commandTo.workingDir = (String) map.get("working_dir");
        commandTo.errorLogFile = (String) map.get("error_log_file");
        commandTo.envVars = (Map) map.get("env_vars");
        if (commandTo.envVars == null) {
            commandTo.envVars = Collections.EMPTY_MAP;
        }
        commandTo.args = (List) map.get("args");
        if (commandTo.args == null) {
            commandTo.args = Collections.EMPTY_LIST;
        }
    }

    private static void fillExportSettings(ExportSettings exportSettings, Map<String, Object> exportTarget) {
        exportSettings.id = (String) exportTarget.get("id");
        exportSettings.storeAsArray = (Boolean) exportTarget.get("store_as_array");
        if (exportSettings.storeAsArray == null) {
            exportSettings.storeAsArray = false;
        }
        exportSettings.enableCompression = (Boolean) exportTarget.get("enable_compression");
        if (exportSettings.enableCompression == null) {
            exportSettings.enableCompression = false;
        }
        exportSettings.compresionLevel = (Integer) exportTarget.get("compresion_level");
        //
        Map<String, Object> exportCommandMap = (Map) exportTarget.get("command");
        fillCommandSettings(exportSettings.commandTo, exportCommandMap);
    }
}
