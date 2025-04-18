package lk.badagini.core.ordermangement.config

import lk.badagini.core.ordermangement.domain.OrderStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListener
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State

@Configuration
@EnableStateMachineFactory
class StateMachineConfig : StateMachineConfigurerAdapter<OrderStatus, String>() {

    override fun configure(config: StateMachineConfigurationConfigurer<OrderStatus, String>) {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(listener())
    }

    override fun configure(states: StateMachineStateConfigurer<OrderStatus, String>) {
        states
            .withStates()
            .initial(OrderStatus.NEW)
            .states(OrderStatus.values().toSet())
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<OrderStatus, String>) {
        transitions
            .withExternal()
            .source(OrderStatus.NEW).target(OrderStatus.CONFIRMED)
            .event("CONFIRM")
            .and()
            .withExternal()
            .source(OrderStatus.NEW).target(OrderStatus.REJECTED)
            .event("REJECT")
            .and()
            .withExternal()
            .source(OrderStatus.NEW).target(OrderStatus.CANCELLED)
            .event("CANCEL")
            .and()
            .withExternal()
            .source(OrderStatus.NEW).target(OrderStatus.FAILED_PAYMENT)
            .event("PAYMENT_FAILED")
            .and()
            .withExternal()
            .source(OrderStatus.CONFIRMED).target(OrderStatus.PREPARING)
            .event("START_PREPARING")
            .and()
            .withExternal()
            .source(OrderStatus.CONFIRMED).target(OrderStatus.CANCELLED)
            .event("CANCEL")
            .and()
            .withExternal()
            .source(OrderStatus.PREPARING).target(OrderStatus.READY_FOR_PICKUP)
            .event("READY")
            .and()
            .withExternal()
            .source(OrderStatus.PREPARING).target(OrderStatus.CANCELLED)
            .event("CANCEL")
            .and()
            .withExternal()
            .source(OrderStatus.READY_FOR_PICKUP).target(OrderStatus.PICKED_UP)
            .event("PICKUP")
            .and()
            .withExternal()
            .source(OrderStatus.PICKED_UP).target(OrderStatus.DELIVERED)
            .event("DELIVER")
            .and()
            .withExternal()
            .source(OrderStatus.PICKED_UP).target(OrderStatus.FAILED_DELIVERY)
            .event("DELIVERY_FAILED")
            .and()
            .withExternal()
            .source(OrderStatus.FAILED_DELIVERY).target(OrderStatus.RETURNED_TO_STORE)
            .event("RETURN")
    }

    @Bean
    fun listener(): StateMachineListener<OrderStatus, String> {
        return object : StateMachineListenerAdapter<OrderStatus, String>() {
            override fun stateChanged(from: State<OrderStatus, String>?, to: State<OrderStatus, String>?) {
                if (from?.id != null && to?.id != null) {
                    println("State changed from ${from.id} to ${to.id}")
                }
            }
        }
    }
} 