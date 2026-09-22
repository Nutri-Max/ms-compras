package com.nutrimax.mscompras.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "nutrimax.exchange";
    public static final String ROUTING_KEY = "order.confirmed";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Cola para MS-Productos (descontar stock)
    @Bean
    public Queue colaProductos() {
        return new Queue("productos.order-confirmed", true);
    }

    @Bean
    public Binding bindingProductos(Queue colaProductos, TopicExchange exchange) {
        return BindingBuilder.bind(colaProductos).to(exchange).with(ROUTING_KEY);
    }

    // Cola para MS-Notificaciones (enviar confirmacion)
    @Bean
    public Queue colaNotificaciones() {
        return new Queue("notificaciones.order-confirmed", true);
    }

    @Bean
    public Binding bindingNotificaciones(Queue colaNotificaciones, TopicExchange exchange) {
        return BindingBuilder.bind(colaNotificaciones).to(exchange).with(ROUTING_KEY);
    }

    // Cola para MS-Analitica (registrar venta)
    @Bean
    public Queue colaAnalitica() {
        return new Queue("analitica.order-confirmed", true);
    }

    @Bean
    public Binding bindingAnalitica(Queue colaAnalitica, TopicExchange exchange) {
        return BindingBuilder.bind(colaAnalitica).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        org.springframework.amqp.rabbit.core.RabbitTemplate template = new org.springframework.amqp.rabbit.core.RabbitTemplate(
                connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}