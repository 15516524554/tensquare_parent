package com.example.tensquare_rabbitmqconsumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
// queues 指定 routing  key

/** 直接模式就是生产者直接把消息发给队列 */
@Component
public class Consumer {
  @Autowired private AmqpTemplate template;

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "test_spring_queue", durable = "true"),
              exchange = @Exchange(value = "test_exchange", type = "topic"),
              key = "test.#" // 会接收所有routingKey为这个开头的消息,*匹配一个单词
              ))
  @RabbitHandler()
  public void receive(Message message, Channel channel) throws IOException {
    System.err.println("----------------------------");
    Set<Map.Entry<String, Object>> entrySet = message.getHeaders().entrySet();
    for (Map.Entry<String, Object> map : entrySet) {
      if (map != null) {
        System.out.println("key:" + map.getKey() + ";---value:" + map.getValue());
      }
    }
    System.out.println("消费端收到的消息：" + message.getPayload());
    Long deliveryLog = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG); // 获取唯一性id
    // 接收消息
    if (deliveryLog != null) {
      channel.basicAck(deliveryLog, false); // 手动签收
    }
  }
}
