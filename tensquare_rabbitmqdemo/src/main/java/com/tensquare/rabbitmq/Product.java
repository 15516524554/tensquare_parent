package com.tensquare.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class Product {
  @Autowired private RabbitTemplate template;

  /*  public void send() throws IOException, TimeoutException {
    // 创建消息
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT); // 设置消息持久化
    messageProperties.setMessageId(UUID.randomUUID().toString()); // 唯一id
    messageProperties.getHeaders().put("desc", "信息描述");
    Message message = new Message("第一次测试rabbitmq".getBytes(), messageProperties);
    template.convertAndSend(
        "exchange_name",
        "routingKey.abcd",
        message,
        new MessagePostProcessor() {
          @Override
          public Message postProcessMessage(Message message) throws AmqpException {
            System.out.println("-------添加额外的配置-------");
            message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
            message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
            return message;
          }
        });
    System.out.println("发送成功");
  }*/
  final RabbitTemplate.ConfirmCallback confirmCallback =
      new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
          System.err.println("correlationData:" + correlationData);
          System.out.println("ack:" + ack);
          System.err.println("cause:" + cause);
          if (!ack) {
            System.err.println("没有确认，进行异常处理,消息唯一id为：" + correlationData.getId());
          }
        }
      };
  final RabbitTemplate.ReturnCallback returnCallback =
      new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(
            Message message, int replyCode, String replyText, String exchange, String routingKey) {
          System.err.println("return message:" + message);
          System.err.println("return replyCode:" + replyCode);
          System.err.println("return replyText:" + replyText);
          System.err.println("return exchange:" + exchange);
          System.err.println("return exchange:" + exchange);
        }
      };

  public final void send2(Object message, Map<String, Object> argument) throws IOException {
    MessageHeaders messageHeaders = new MessageHeaders(argument);
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setContentType("utf-8");
    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
    Message message1 = new Message("你好".getBytes(), messageProperties);
    org.springframework.messaging.Message mess =
        MessageBuilder.createMessage(message1, messageHeaders);
    // 发布消息
    template.setConfirmCallback(confirmCallback);
    template.setReturnCallback(returnCallback);
    // 最后一个参数设置全局唯一id
    template.convertAndSend(
        "test_exchange", "test.send", mess, new CorrelationData(UUID.randomUUID().toString()));
  }
}
