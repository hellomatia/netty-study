package com.statemachine;


import org.junit.Test;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

public class SateMachineTest {
//
//    @Test
//    public void testStatemachine_goingThroughNormalFlow() throws Exception {
//        // ...
//        StateMachineTestPlan plan =
//                StateMachineTestPlanBuilder.builder()
//                        .stateMachine(testStateMachine)
//                        // Check initial state.
//                        .step()
//                        .expectState(DeliveryStateType.IDLE)
//                        .and()
//                        // Check transition.
//                        .step()
//                        .sendEvent(MissionWorkerEventType.DISPATCHED)
//                        .expectState(DeliveryStateType.GOING_TO_LOADING_POINT)
//                        .expectVariable(MissionStateVariable.VARIABLE_KEY, missionStateVariable)
//                        .and()
//                        // Check transition.
//                        .step()
//                        .sendEvent(MissionWorkerEventType.ARRIVED)
//                        .expectState(DeliveryStateType.WAITING_FOR_LOADING)
//                        .expectVariable(MissionStateVariable.VARIABLE_KEY, missionStateVariable)
//                        .and()
//                        // Check transition.
//                        .step()
//                        .sendEvent(MissionWorkerEventType.OPENED_LID)
//                        .expectState(DeliveryStateType.LOADING)
//                        .expectVariable(MissionStateVariable.VARIABLE_KEY, missionStateVariable)
//                        .and()
//                        // ...
//                        // Check transition.
//                        .step()
//                        .sendEvent(MissionWorkerEventType.COMPLETED_UNLOADING)
//                        .expectState(DeliveryStateType.IDLE)
//                        .expectVariable(MissionStateVariable.VARIABLE_KEY, missionStateVariable)
//                        .and()
//                        .build();
//        // Test execution.
//        plan.test();
//    }
}
