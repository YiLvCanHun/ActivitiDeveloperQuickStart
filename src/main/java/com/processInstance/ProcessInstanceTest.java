package com.processInstance;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Created by peng.zhang
 * Time: 2018/5/28-14:14.
 */
public class ProcessInstanceTest {

    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    /**
     * 通过 zip 压缩包方式部署流程
     */
    @Test
    public void deploymentProcessDefinition_zip() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloWorld.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment() //创建部署对象
                .name("processInstanceTest") //声明流程的名称
                .addZipInputStream(zipInputStream)
                .deploy(); //完成部署

        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称: " + deployment.getName());
        System.out.println("部署时间: " + deployment.getDeploymentTime());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance(){
        String processDefinitionKey = "activitiDemo";
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("执行对象ID：" + processInstance.getId()); //执行对象ID
        System.out.println("流程实例ID：" + processInstance.getProcessInstanceId()); //流程实例ID
        System.out.println("流程定义ID: " + processInstance.getProcessDefinitionId()); //activitiDemo:4:10004
    }

}
