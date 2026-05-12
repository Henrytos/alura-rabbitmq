package br.com.alurafood.avaliacao.avaliacao.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvaliacaoAMQPConfiguration {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(
                connectionFactory);

        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    Queue filaDetalhesAvaliacao() {
        return QueueBuilder.nonDurable("pagamentos.avaliacao-pedido")
                .deadLetterExchange("pagamento.dlx")
                .build();
    }

    @Bean
    FanoutExchange fanoutExchangePagamento() {
        return ExchangeBuilder
                .fanoutExchange("pagamento.ex")
                .build();
    }

    @Bean
    Queue criarDlqFilaAvaliacao() {
        return QueueBuilder.nonDurable("pagamentos.avaliacao-pedido-dlq").build();
    }

    @Bean
    FanoutExchange fanoutDLXExchangePagamento() {
        return ExchangeBuilder
                .fanoutExchange("pagamento.dlx")
                .build();
    }

    @Bean
    Binding bindPagamentoAvaliacao() {
        return BindingBuilder.bind(filaDetalhesAvaliacao())
                .to(fanoutExchangePagamento());
    }

    @Bean
    Binding bindPagamentoDlq() {
        return BindingBuilder.bind(criarDlqFilaAvaliacao())
                .to(fanoutDLXExchangePagamento());
    }
}
