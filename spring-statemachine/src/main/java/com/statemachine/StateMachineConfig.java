package com.statemachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.RepositoryStateMachinePersist;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "missionStateMachineFactory")
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter {
//    private final StateAction stateAction;
//    private final TransitionGuard transitionGuard;
//    private final TransitionAction transitionAction;

//    @Override
//    public void configure(StateMachineConfigurationConfigurer configurationConfigure) throws Exception {
//        configurationConfigure
//                .withConfiguration()
//                .autoStartup(true)
//                .listener(
//                        new StateMachineListenerAdapter() {
//                            @Override
//                            public void stateChanged(State from, State to) {
//                                String fromState = (from == null ? "null" : from.getId().name());
//                                String toState = to.getId().name();
//                                log.info("State changed from {} to {}", fromState, toState);
//                            }
//                        });
//    }
//
//    @Override
//    public void configure(StateMachineStateConfigurer stateConfigurer) throws Exception {
//        stateConfigurer
//                .withStates()
//                .initial(DeliveryStateType.IDLE)
//                .state(DeliveryStateType.IDLE, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.GOING_TO_LOADING_POINT, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.WAITING_FOR_LOADING, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.LOADING, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.GOING_TO_UNLOADING_POINT, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.WAITING_FOR_UNLOADING, stateAction.publishChangedEvent())
//                .state(DeliveryStateType.UNLOADING, stateAction.publishChangedEvent());
//    }
//
//    @Override
//    public void configure(StateMachineTransitionConfigurer transitionConfigurer) throws Exception {
//        transitionConfigurer
//                .withExternal()
//                .source(DeliveryStateType.IDLE)
//                .target(DeliveryStateType.GOING_TO_LOADING_POINT)
//                .event(MissionWorkerEventType.DISPATCHED)
//                .guard(transitionGuard.justPass())
//                .action(transitionAction.moveToPickupPoint())
//                .and()
//                .withExternal()
//                .source(DeliveryStateType.GOING_TO_LOADING_POINT)
//                .target(DeliveryStateType.WAITING_FOR_LOADING)
//                .event(MissionWorkerEventType.ARRIVED)
//                .guard(transitionGuard.justPass())
//                .action(transitionAction.doNothing())
//                .and()
//                .withExternal()
//                .source(DeliveryStateType.WAITING_FOR_LOADING)
//                .target(DeliveryStateType.LOADING)
//                .event(MissionWorkerEventType.OPENED_LID)
//                .guard(transitionGuard.justPass())
//                .action(transitionAction.setLidOpened())
//                .and()
//                .withInternal()
//                .source(DeliveryStateType.LOADING)
//                .event(MissionWorkerEventType.CLOSED_LID)
//                .action(transitionAction.setLidClosed())
//                .and()
//                .withInternal()
//                .source(DeliveryStateType.LOADING)
//                .event(MissionWorkerEventType.OPENED_LID)
//                .action(transitionAction.setLidOpened())
//                .and()
//                .withExternal()
//                .source(DeliveryStateType.LOADING)
//                .target(DeliveryStateType.GOING_TO_UNLOADING_POINT)
//                .event(MissionWorkerEventType.COMPLETED_LOADING)
//                .guard(transitionGuard.isLidClosed()) // Allow transition only if the lid is closed.
//                .action(transitionAction.moveToDropPoint());
//        // ...
//    }
//    // ...
//
//    @Bean
//    public StateMachinePersist stateMachinePersist(RedisConnectionFactory connectionFactory) {
//        RedisStateMachineContextRepository repository =
//                new RedisStateMachineContextRepository(connectionFactory);
//        return new RepositoryStateMachinePersist(repository);
//    }
//    @Bean
//    public RedisStateMachinePersister redisStateMachinePersister(
//            StateMachinePersist stateMachinePersist) {
//        return new RedisStateMachinePersister(stateMachinePersist);
//    }
}
