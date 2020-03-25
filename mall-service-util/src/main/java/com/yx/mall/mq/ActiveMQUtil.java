package com.yx.mall.mq;

import lombok.extern.log4j.Log4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.*;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/27/13:11
 */
@Log4j
public class ActiveMQUtil {
    PooledConnectionFactory pooledConnectionFactory = null;

    public ConnectionFactory init(String brokerUrl){
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        pooledConnectionFactory = new PooledConnectionFactory(factory);
        pooledConnectionFactory.setReconnectOnException(true);
        pooledConnectionFactory.setMaxConnections(5);
        pooledConnectionFactory.setExpiryTimeout(10000);
        return this.pooledConnectionFactory;
    }

    public ConnectionFactory getConnectionFacotry(){
        return pooledConnectionFactory;
    }


    /**
        * @description: 发送持久化MQ
        * @author:  YX
        * @date:    2019/12/27 16:05
        * @param: queueName
        * @param: mapMessage
        * @return: void
        * @throws: 
        */
    public void sendTransactedMQ(String queueName,Message message){
        try{
            this.sendMQ(queueName,message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
        * @description: 延迟MQ
        * @author:  YX
        * @date:    2019/12/29 15:41
        * @param: queueName
        * @param: mapMessage
        * @param: ms 毫秒
        * @return: void
        * @throws:
        */
    public void sendDelayMQ(String queueName,Message message,int ms){
        log.debug("发送延迟MQ，消息队列：" +queueName + ",message:" + message);
        try{
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,ms);
            this.sendMQ(queueName,message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendMQ(String queueName,Message message)throws Exception{
        log.debug("发送持久化MQ，消息队列：" +queueName + ",message:" + message);
        Connection connection = null;
        Session session = null;
        try{
            connection = this.getConnectionFacotry().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue paymentSuccessQueue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(paymentSuccessQueue);
            producer.send(message);
            session.commit();
        }catch(Exception e){
            try{
                if(session != null){
                    session.rollback();
                }
            }catch(Exception e1){
                throw e1;
            }
            throw e;
        }finally{
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e1){
                throw e1;
            }
        }
    }

}
