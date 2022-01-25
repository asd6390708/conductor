/*
 * Copyright 2022 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.core.dal;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.utils.ExternalPayloadStorage.Operation;
import com.netflix.conductor.common.utils.ExternalPayloadStorage.PayloadType;
import com.netflix.conductor.core.utils.ExternalPayloadStorageUtils;
import com.netflix.conductor.model.TaskModel;
import com.netflix.conductor.model.WorkflowModel;
import com.netflix.conductor.metrics.Monitors;

@Component
public class ModelMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelMapper.class);

    private final ExternalPayloadStorageUtils externalPayloadStorageUtils;

    public ModelMapper(ExternalPayloadStorageUtils externalPayloadStorageUtils) {
        this.externalPayloadStorageUtils = externalPayloadStorageUtils;
    }

    /**
     * Fetch the fully formed workflow domain object with complete payloads
     *
     * @param workflowModel the workflow domain object from the datastore
     * @return the workflow domain object {@link WorkflowModel} with payloads from external storage
     */
    public WorkflowModel getWorkflowModel(WorkflowModel workflowModel) {
        populateWorkflowAndTaskPayloadData(workflowModel);
        return workflowModel;
    }

    public WorkflowModel getLeanCopy(WorkflowModel workflowModel) {
        WorkflowModel leanWorkflowModel = workflowModel.copy();
        externalizeWorkflowData(leanWorkflowModel);
        return leanWorkflowModel;
    }

    public Workflow getWorkflow(WorkflowModel workflowModel) {
        externalizeWorkflowData(workflowModel);

        Workflow workflow = new Workflow();
        workflow.setStatus(WorkflowModel.Status.getWorkflowStatusDTO(workflowModel.getStatus()));
        workflow.setEndTime(workflowModel.getEndTime());
        workflow.setWorkflowId(workflowModel.getWorkflowId());
        workflow.setParentWorkflowId(workflowModel.getParentWorkflowId());
        workflow.setParentWorkflowTaskId(workflowModel.getParentWorkflowTaskId());
        workflow.setInput(workflowModel.getInput());
        workflow.setOutput(workflowModel.getOutput());
        workflow.setCorrelationId(workflow.getCorrelationId());
        workflow.setReRunFromWorkflowId(workflowModel.getReRunFromWorkflowId());
        workflow.setReasonForIncompletion(workflowModel.getReasonForIncompletion());
        workflow.setEvent(workflowModel.getEvent());
        workflow.setTaskToDomain(workflowModel.getTaskToDomain());
        workflow.setFailedReferenceTaskNames(workflowModel.getFailedReferenceTaskNames());
        workflow.setWorkflowDefinition(workflowModel.getWorkflowDefinition());
        workflow.setExternalInputPayloadStoragePath(
                workflowModel.getExternalInputPayloadStoragePath());
        workflow.setExternalOutputPayloadStoragePath(
                workflowModel.getExternalOutputPayloadStoragePath());
        workflow.setPriority(workflowModel.getPriority());
        workflow.setVariables(workflowModel.getVariables());
        workflow.setLastRetriedTime(workflowModel.getLastRetriedTime());
        workflow.setOwnerApp(workflowModel.getOwnerApp());
        workflow.setCreateTime(workflowModel.getCreatedTime());
        workflow.setUpdateTime(workflowModel.getUpdatedTime());
        workflow.setCreatedBy(workflowModel.getCreatedBy());
        workflow.setUpdatedBy(workflowModel.getUpdatedBy());

        workflow.setTasks(
                workflowModel.getTasks().stream().map(this::getTask).collect(Collectors.toList()));

        return workflow;
    }

    public TaskModel getTaskModel(TaskModel taskModel) {
        populateTaskData(taskModel);
        return taskModel;
    }

    public TaskModel getLeanCopy(TaskModel taskModel) {
        TaskModel leanTaskModel = taskModel.copy();
        externalizeTaskData(leanTaskModel);
        return leanTaskModel;
    }

    public Task getTask(TaskModel taskModel) {
        externalizeTaskData(taskModel);

        Task task = new Task();
        task.setTaskType(taskModel.getTaskType());
        task.setStatus(TaskModel.Status.getTaskStatusDTO(taskModel.getStatus()));
        task.setInputData(taskModel.getInputData());
        task.setReferenceTaskName(taskModel.getReferenceTaskName());
        task.setRetryCount(taskModel.getRetryCount());
        task.setSeq(taskModel.getSeq());
        task.setCorrelationId(taskModel.getCorrelationId());
        task.setPollCount(taskModel.getPollCount());
        task.setTaskDefName(taskModel.getTaskDefName());
        task.setScheduledTime(taskModel.getScheduledTime());
        task.setStartTime(taskModel.getStartTime());
        task.setEndTime(taskModel.getEndTime());
        task.setUpdateTime(taskModel.getUpdateTime());
        task.setStartDelayInSeconds(taskModel.getStartDelayInSeconds());
        task.setRetriedTaskId(taskModel.getRetriedTaskId());
        task.setRetried(taskModel.isRetried());
        task.setExecuted(taskModel.isExecuted());
        task.setCallbackFromWorker(taskModel.isCallbackFromWorker());
        task.setResponseTimeoutSeconds(taskModel.getResponseTimeoutSeconds());
        task.setWorkflowInstanceId(taskModel.getWorkflowInstanceId());
        task.setWorkflowType(taskModel.getWorkflowType());
        task.setTaskId(taskModel.getTaskId());
        task.setReasonForIncompletion(taskModel.getReasonForIncompletion());
        task.setCallbackAfterSeconds(taskModel.getCallbackAfterSeconds());
        task.setWorkerId(taskModel.getWorkerId());
        task.setOutputData(taskModel.getOutputData());
        task.setWorkflowTask(taskModel.getWorkflowTask());
        task.setDomain(taskModel.getDomain());
        task.setInputMessage(taskModel.getInputMessage());
        task.setOutputMessage(taskModel.getOutputMessage());
        task.setRateLimitPerFrequency(taskModel.getRateLimitPerFrequency());
        task.setRateLimitFrequencyInSeconds(taskModel.getRateLimitFrequencyInSeconds());
        task.setExternalInputPayloadStoragePath(taskModel.getExternalInputPayloadStoragePath());
        task.setExternalOutputPayloadStoragePath(taskModel.getExternalOutputPayloadStoragePath());
        task.setWorkflowPriority(taskModel.getWorkflowPriority());
        task.setExecutionNameSpace(taskModel.getExecutionNameSpace());
        task.setIsolationGroupId(taskModel.getIsolationGroupId());
        task.setIteration(taskModel.getIteration());
        task.setSubWorkflowId(taskModel.getSubWorkflowId());
        task.setSubworkflowChanged(taskModel.isSubworkflowChanged());
        return task;
    }

    /**
     * Populates the workflow input data and the tasks input/output data if stored in external
     * payload storage.
     *
     * @param workflowModel the workflowModel for which the payload data needs to be populated from
     *     external storage (if applicable)
     */
    private void populateWorkflowAndTaskPayloadData(WorkflowModel workflowModel) {
        if (StringUtils.isNotBlank(workflowModel.getExternalInputPayloadStoragePath())) {
            Map<String, Object> workflowInputParams =
                    externalPayloadStorageUtils.downloadPayload(
                            workflowModel.getExternalInputPayloadStoragePath());
            Monitors.recordExternalPayloadStorageUsage(
                    workflowModel.getWorkflowName(),
                    Operation.READ.toString(),
                    PayloadType.WORKFLOW_INPUT.toString());
            workflowModel.setInput(workflowInputParams);
            workflowModel.setExternalInputPayloadStoragePath(null);
        }

        workflowModel.getTasks().forEach(this::populateTaskData);
    }

    private void populateTaskData(TaskModel taskModel) {
        if (StringUtils.isNotBlank(taskModel.getExternalOutputPayloadStoragePath())) {
            taskModel.setOutputData(
                    externalPayloadStorageUtils.downloadPayload(
                            taskModel.getExternalOutputPayloadStoragePath()));
            Monitors.recordExternalPayloadStorageUsage(
                    taskModel.getTaskDefName(),
                    Operation.READ.toString(),
                    PayloadType.TASK_OUTPUT.toString());
            taskModel.setExternalOutputPayloadStoragePath(null);
        }

        if (StringUtils.isNotBlank(taskModel.getExternalInputPayloadStoragePath())) {
            taskModel.setInputData(
                    externalPayloadStorageUtils.downloadPayload(
                            taskModel.getExternalInputPayloadStoragePath()));
            Monitors.recordExternalPayloadStorageUsage(
                    taskModel.getTaskDefName(),
                    Operation.READ.toString(),
                    PayloadType.TASK_INPUT.toString());
            taskModel.setExternalInputPayloadStoragePath(null);
        }
    }

    private void externalizeTaskData(TaskModel taskModel) {
        externalPayloadStorageUtils.verifyAndUpload(taskModel, PayloadType.TASK_INPUT);
        externalPayloadStorageUtils.verifyAndUpload(taskModel, PayloadType.TASK_OUTPUT);
    }

    private void externalizeWorkflowData(WorkflowModel workflowModel) {
        externalPayloadStorageUtils.verifyAndUpload(workflowModel, PayloadType.WORKFLOW_INPUT);
        externalPayloadStorageUtils.verifyAndUpload(workflowModel, PayloadType.WORKFLOW_OUTPUT);
    }
}
