import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peng.zhang
 * Time: 2018/5/24-15:11.
 */
public class ProcessImportTest {

    /**
     * 流程引擎（核心对象），默认加载类路径下命名为 activiti.cfg.xml
     */
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

//    不使用 xml 文件的方式
//    private ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//            .setJdbcUrl("jdbc:mysql://localhost:3306/test")
//            .setJdbcUsername("root")
//            .setJdbcPassword("root")
//            .setJdbcDriver("com.mysql.jdbc.Driver")
//            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//    private ProcessEngine processEngine = cfg.buildProcessEngine();

    @Test
    public void deploymentProcessDefinition() {
        //获取流程定义和部署对象相关的Service
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment() //创建部署对象
                .name("activitiDemo") //声明流程的名称
                .addClasspathResource("diagrams/helloWorld.bpmn") //加载资源文件，一次只能加载一个文件
                .addClasspathResource("diagrams/helloWorld.png")
                .deploy(); //完成部署

        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署时间: " + deployment.getDeploymentTime());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        System.out.println(
                "Found process definition ["
                        + processDefinition.getName() + "] with id ["
                        + processDefinition.getId() + "]");
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {

        String processDefinitionKey = "activitiDemo";//流程定义的key,也就是bpmn中存在的ID

        ProcessInstance pi = processEngine.getRuntimeService()//管理流程实例和执行对象，也就是表示正在执行的操作
                .startProcessInstanceByKey(processDefinitionKey);////按照流程定义的key启动流程实例

        System.out.println("流程实例ID：" + pi.getId());//流程实例ID：101
        System.out.println("流程实例ID：" + pi.getProcessInstanceId());//流程实例ID：101
        System.out.println("流程定义ID:" + pi.getProcessDefinitionId());//myMyHelloWorld:1:4
    }

    /**
     * 查看当前任务办理人的个人任务
     */
    @Test
    public void findPersonalTaskList() {
        String assignee = "经理";//当前任务办理人
        List<Task> tasks = processEngine.getTaskService()//与任务相关的Service
                .createTaskQuery()//创建一个任务查询对象
                .taskAssignee(assignee)
                .list();
        if (tasks != null && tasks.size() > 0) {
            for (Task task : tasks) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + task.getCreateTime());
                System.out.println("流程实例ID:" + task.getProcessInstanceId());
                System.out.println("#####################################");
            }
        }
    }

    /**
     * 完成流程实例,根据 taskId
     */
    @Test
    public void completePersonalTask() {
        String taskId = "20002";
        TaskService taskService = processEngine.getTaskService();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "提交申请");
        map.put("taskUser", "员工");
        taskService.complete(taskId, map);
        System.out.println("完成任务，任务ID：" + taskId);
    }

    /**
     * 查询流程状态(判断流程正在执行,还是已经结束)
     */
    @Test
    public void isProcessEnd(){
        String processInstanceId = "10004";
        ProcessInstance pi = processEngine.getRuntimeService() // 表示正在执行的流程实例和流程对象
                .createProcessInstanceQuery() // 创建流程实例查询
                .processInstanceId(processInstanceId) // 使用流程实例ID查询
                .singleResult();
    }
}
