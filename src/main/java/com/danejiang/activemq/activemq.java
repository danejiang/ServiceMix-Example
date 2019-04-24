package com.danejiang.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import com.danejiang.service.mainService;

import java.util.logging.Logger;

public class activemq {
    private static final transient Logger logger = Logger.getLogger("activemq");

    private static Connection rConnection= null;
    private static Session rSession = null;
    private static MessageConsumer rMessageConsumer = null;

    public static boolean send(String topicType,String topicName, String topicMessage) {
        try {
            // 创建连接工厂
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");

            // 创建JMS连接实例，并启动连接
            Connection connection = factory.createConnection("smx", "smx");
            connection.start();

            // 创建Session对象，不开启事务
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建主题和生成者：按消息类型分别处理
            MessageProducer producer = null;
            if(topicType.toLowerCase().equals("queue")){
                // 创建主题
                Queue queue = session.createQueue(topicName);

                // 创建生成者
                producer = session.createProducer(queue);
            }else if(topicType.toLowerCase().equals("topic")){
                // 创建主题
                Topic topic = session.createTopic(topicName);

                // 创建生成者
                producer = session.createProducer(topic);
            }else{
                logger.info("Send MQ Message error:not set topic type.");
                return false;
            }

            // 设置消息不需持久化。默认消息需要持久化
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // 创建文本消息  或者其他格式的信息
            TextMessage message = session.createTextMessage(topicMessage);

            // 发送消息。non-persistent 默认异步发送；persistent 默认同步发送
            producer.send(message);

            // 关闭会话和连接
            producer.close();
            session.close();
            connection.close();

            logger.info("Send MQ Message:" + topicName);
            return true;
        } catch (Exception e) {
            logger.info("Send MQ Message error:" + e.toString());
            return false;
        }
    }

    public static boolean receive(String topicType, String topicName) {
        try {
            // 创建连接工厂
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");

            // 创建JMS连接实例，并启动连接
            rConnection = connectionFactory.createConnection("smx", "smx");
            rConnection.start();

            // 创建Session对象，不开启事务
            rSession = rConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建主题和消费者：按消息类型分别处理
            if (topicType.toLowerCase().equals("queue")) {
                // 创建主题
                Queue queue = rSession.createQueue(topicName);

                // 创建消费者
                rMessageConsumer = rSession.createConsumer(queue);
            } else if (topicType.toLowerCase().equals("topic")) {
                // 创建主题
                Topic topic = rSession.createTopic(topicName);

                // 创建消费者
                rMessageConsumer = rSession.createConsumer(topic);
            } else {
                logger.info("Start MQ Message error:not set topic type.");
                return false;
            }

            // 异步消费
            rMessageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage mess = (TextMessage) message;
                    try {
                        //消息处理
                        logger.info("Receive MQ Message:" + topicName+",Result:"+ mainService.doMQ(topicName, mess.getText()));
                    } catch (JMSException e) {
                        logger.info("Receive MQ Message error:" + e.toString());
                    }
                }
            });

            logger.info("Started receive MQ Message:" + topicName);
            return true;
        } catch (Exception e) {
            logger.info("Start receive MQ Message error:" + e.toString());
            return false;
        }
    }

    public static void stop() {
        try {
            // 关闭会话和连接
            if (rMessageConsumer != null) rMessageConsumer.close();
            if (rSession != null) rSession.close();
            if (rConnection != null) rConnection.close();

            logger.info("Stoped MQ Message.");
        } catch (Exception e) {
            logger.info("Stop MQ Message error:" + e.toString());
        }
    }
}
