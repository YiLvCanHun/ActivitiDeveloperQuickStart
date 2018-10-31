package com.processDefinition;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;


/**
 * Created by peng.zhang
 * Time: 2018/5/25-14:34.
 */
public class ProcessDefinitionTest {

    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    /**
     * 部署单个流程
     */
    @Test
    public void deploymentProcessDefinition_classpath() {
        //获取流程定义和部署对象相关的Service
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment() //创建部署对象
                .name("流程定义") //声明流程的名称
                .addClasspathResource("diagrams/helloWorld.bpmn") //加载资源文件，一次只能加载一个文件
                .addClasspathResource("diagrams/helloWorld.png")
                .deploy(); //完成部署

        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称: " + deployment.getName());
        System.out.println("部署时间: " + deployment.getDeploymentTime());
    }

    /**
     * 通过 zip 压缩包方式部署流程
     */
    @Test
    public void deploymentProcessDefinition_zip() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloWorld.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //获取流程定义和部署对象相关的Service
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment() //创建部署对象
                .name("流程定义") //声明流程的名称
                .addZipInputStream(zipInputStream)
                .deploy(); //完成部署

        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称: " + deployment.getName());
        System.out.println("部署时间: " + deployment.getDeploymentTime());
    }

    /**
     * 查询流程定义
     */
    @Test
    public void findProcessDefinition() {
        List<ProcessDefinition> list =
                processEngine.getRepositoryService() // 与流程定义和部署相关的 service
                .createProcessDefinitionQuery() // 创建一个流程定义的查询

                 /*指定查询条件 where 条件*/
//                .deploymentId(deploymentId)  使用部署对象的ID 查询
//                .processDefinitionId(processDefinitionId)  使用流程定义ID 查询
//                .processDefinitionKey(processDefinitionKey)  使用流程定义的 KEY 查询
//                .processDefinitionNameLike(processDefinitionNameLike)  使用流程定义的名称模糊查询

                 /*排序*/
                .orderByProcessDefinitionVersion().asc() // 按照版本的升序排序
//                .orderByProcessDefinitionName().desc()  // 按照流程定义的名称降序排列

                 /*返回的结果集*/
                .list();  //返回一个集合列表,封装流程定义
//                .singleResult();  返回唯一结果集
//                .count(); 返回结果集数量
//                .listPage(firstResult, maxResults); 分页查询

        /*
         * 查询最新版本的流程定义
         * key:流程定义的 key
         * value:流程定义的对象
         * key 值相同的情况下,后一次的值将替换前一次的值
         */
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        if (list != null && list.size() > 0) {
            for (ProcessDefinition definition : list) {
                map.put(definition.getKey(), definition);
            }
        }

        List<ProcessDefinition> pdList = new ArrayList<>(map.values());
        if (pdList.size() > 0) {
            for (ProcessDefinition pd : pdList) {
                System.out.println("流程定义ID: " + pd.getId());  // key + version + 随机数
                System.out.println("流程定义名称: " + pd.getName()); // bpmn文件中的 name 属性
                System.out.println("流程定义的Key: " + pd.getKey()); // bpmn文件中的 id 属性
                System.out.println("流程定义的版本: " + pd.getVersion()); // key 相同,版本升级,默认为1
                System.out.println("资源名称bpmn文件: " + pd.getResourceName());
                System.out.println("资源名称png文件: " + pd.getDiagramResourceName());
                System.out.println("部署对象ID: " + pd.getDeploymentId());
                System.out.println("##################################################");
            }
        }
    }

    /**
     * 删除流程定义,使用部署 ID 完成删除
     */
    @Test
    public void deleteProcessDefinition(){
        /*
         * 不带级联的删除(默认为 false)
         *      只能删除没有启动的流程,如果流程启动,就会抛出异常
         */
//        processEngine.getRepositoryService()
//                .deleteDeployment("2501");

        /*
         * 级联删除
         *      不管流程是否启动,都可以删除,加上 true
         */
        processEngine.getRepositoryService()
                .deleteDeployment("2501",true);

        System.out.println("删除成功");
    }

    /**
     * 查看流程图
     */
    @Test
    public void viewPicture() throws IOException {
        /*将生成图片放到文件夹下*/
        String deploymentId = "2501";
//        获取图片资源名称
        List<String> nameList = processEngine.getRepositoryService()
                .getDeploymentResourceNames(deploymentId);
        String resourceName = "";
//        从文件名称中过滤出带有 .png 的文件并赋值给 resourceName
        if (nameList != null && nameList.size() > 0) {
            for (String s : nameList) {
                if (s.indexOf(".png") > 0)
                    resourceName = s;
            }
        }

//        获取图片输入流
        InputStream ins = processEngine.getRepositoryService()
                .getResourceAsStream(deploymentId, resourceName);
//        将图片生成到D盘目录下
        File file = new File("D:/" + resourceName);
        OutputStream outs = new FileOutputStream(file);
//        将输入流的图片写到D盘下
        FileCopyUtils.copy(ins, outs);
    }
}
