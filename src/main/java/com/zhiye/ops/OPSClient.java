/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.zhiye.ops;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * An UDP client taht just send thousands of small messages to a UdpServer. 
 * 
 * This class is used for performance test purposes. It does nothing at all, but send a message
 * repetitly to a server.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class OPSClient extends IoHandlerAdapter {
    /** The connector */
    private IoConnector connector;

    /** The session */
    private static IoSession session;

    private boolean received = false;
   
	private static ObjectMapper objectMapper = null;

    /**
     * Create the UdpClient's instance
     */
    public OPSClient(String ip,int port) {
		objectMapper = new ObjectMapper();
		/*try {
			//jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
        connector = new NioSocketConnector();

        connector.setHandler(this);
		connector.setConnectTimeoutMillis(10*1000);
		
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        ConnectFuture connFuture = connector.connect(new InetSocketAddress(ip,port));

        connFuture.awaitUninterruptibly();

        session = connFuture.getSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        received = true;
        Map map = objectMapper.readValue(message.toString(), Map.class);
        System.out.println("服务端返回数据："+message.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    /**
     * The main method : instanciates a client, and send N messages. We sleep 
     * between each K messages sent, to avoid the server saturation.
     * @param args The arguments
     * @throws Exception If something went wrong
     */
    public static void main(String[] args) throws Exception {
    	String name = "1505939ud9.iask.in";   ///通过域名取得IP
    	
    	OPSClient client = new OPSClient(name, 24521);  

        long t0 = System.currentTimeMillis();
        
		short[] sht=new short[4];
		sht[0]=1;
		sht[1]=2;
		sht[2]=3;
		sht[3]=4;
		
        ModbusSend ms = new ModbusSend();
        ms.setReadOrWrit("rCs");
        ms.setSlaveId(1);
        ms.setStart(40);
        ms.setLen(1);
        ms.setShrtArray(sht);


		String content = sendString(ms);
		
		session.write(content);
        long t1 = System.currentTimeMillis();

        System.out.println("Sent messages delay : " + (t1 - t0));

        Thread.sleep(200);

        client.connector.dispose(true);
    }
    
	/**
	 *   发送数据
	 * @param redWCode  rCs 批量读线圈上的内容 ;wCs 批量写数据到线圈;rHRs 读保持寄存器上的内容 ;wHRs 批量写数据到保持寄存器;rIRs 读输入寄存器 ;rDI 读离散输入状态
	 * @param slaveId  从站地址
	 * @param start  开始地址
	 * @param len  数据长度
	 * @param values  传输数据内容
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	private static  String sendString(ModbusSend ms) throws JsonGenerationException, JsonMappingException, IOException{

	    System.out.println("发送给服务端的内容: " + objectMapper.writeValueAsString(ms));
		return objectMapper.writeValueAsString(ms);
	}
}
