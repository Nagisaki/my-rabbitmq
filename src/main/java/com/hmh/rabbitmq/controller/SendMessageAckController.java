package com.hmh.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 测试 消息确认
 *
 * 先从总体的情况分析，推送消息存在四种情况：
 * ①消息推送到server，但是在server里找不到交换机
 * ②消息推送到server，找到交换机了，但是没找到队列
 * ③消息推送到sever，交换机和队列啥都没找到
 * ④消息推送成功
 * @Author : JCccc
 * @CreateTime : 2019/9/3
 * @Description :
 **/
@RestController
public class SendMessageAckController {

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    /**
     * 1.消息推送到server，但是在server里找不到交换机
     * @return
     */
    @GetMapping("/TestMessageAck")
    public String TestMessageAck() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRouting", map);
        return "ok";
    }

    /**
     * 2.消息推送到server，找到交换机了，但是没找到队列
     * @return
     */
    @GetMapping("/TestMessageAck2")
    public String TestMessageAck2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: lonelyDirectExchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("lonelyDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

    /**
     * 3.消息推送到sever，交换机和队列啥都没找到
     * 这种情况其实一看就觉得跟1很像，没错 ，3和1情况回调是一致的，所以不做结果说明了。
     *   结论： 这种情况触发的是 ConfirmCallback 回调函数。
     */


    /**
     * 消息推送成功
     */
    @GetMapping("/TestMessageAck3")
    public String sendDirectMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "消息推送成功";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        return "ok";
    }
}