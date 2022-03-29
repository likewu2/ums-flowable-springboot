package com.flowable.flowableboot;

import static org.awaitility.Awaitility.await;

import com.flowable.flowableboot.model.Person;
import com.flowable.flowableboot.repository.PersonRepository;
import com.flowable.flowableboot.service.FlowableService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FlowableTriggerableCustomServiceTaskApplicationTests {
  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private FlowableService flowableService;

  @Test
  @Deployment(resources = "/processes/one-task-process.bpmn20.xml")
  void testTriggereableCustomerServiceTask(){

    // Pull a person from personRepository
    Person person = personRepository.findByUsername("PEDROSORTO");

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("person", person);

    // Start a new process instance
    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("multiTaskProcess", variables);

    // Check if triggerable custom service task was reached
    await().atMost(30L, TimeUnit.SECONDS).until(
        () -> this.runtimeService.createExecutionQuery()
        .activityId("service1")
        .processInstanceId(processInstance.getProcessInstanceId())
        .singleResult() != null
    );

//    //    Check the value of the count var before the trigger
//    HistoricVariableInstance historicVariableInstance = this.historyService.createHistoricVariableInstanceQuery()
//        .processInstanceId(processInstance.getProcessInstanceId())
//        .variableName("count")
//        .singleResult();
//
//    //    print the value of count as it was modified in the execute method
//    System.out.println(String.format("Before trigger: %s", historicVariableInstance.getValue()));

//    //    Get the execution ID of the triggerable service task
//    Execution execution = this.runtimeService.createExecutionQuery()
//        .processInstanceId(processInstance.getProcessInstanceId())
//        .activityId("service1")
//        .singleResult();
//
//    //    Trigger the service task
//    this.runtimeService.trigger(execution.getId());

//    //    Get the value of the variable 'count' after the trigger
//    historicVariableInstance = this.historyService.createHistoricVariableInstanceQuery()
//        .processInstanceId(processInstance.getProcessInstanceId())
//        .variableName("count")
//        .singleResult();
//
//    //    Value of count as it was modified in the trigger method
//    System.out.println(String.format("After trigger: %s", historicVariableInstance.getValue()));

    //    Check if the user task was reached
    await().atMost(30L, TimeUnit.SECONDS).until(
        () -> this.runtimeService.createExecutionQuery()
        .activityId("statusTask")
        .processInstanceId(processInstance.getProcessInstanceId())
        .singleResult() != null
    );

//    // Retrieve the specific task by passing in the task name and processInstanceID
//    Task userTask = this.flowableService.retrieveTask(processInstance, "1st task");
//    this.flowableService.completeTask(userTask);
//
////    Check to see process has ended
//    await().atMost(30L, TimeUnit.SECONDS).until(
//        () -> this.historyService.createHistoricProcessInstanceQuery()
//        .processInstanceId(processInstance.getProcessInstanceId())
//        .finished()
//        .singleResult() != null
//    );
  }


}
