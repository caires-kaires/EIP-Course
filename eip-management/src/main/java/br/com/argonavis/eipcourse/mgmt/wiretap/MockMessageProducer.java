package br.com.argonavis.eipcourse.mgmt.wiretap;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MockMessageProducer {

	private Connection con;
	private Session session;
	private MessageProducer producer;
	
	MockMessageProducer(Connection con, Destination destination) throws JMSException {
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(destination);
	}
	
	public void send() throws JMSException {
		List<String> dataList = MockData.getMockData();
		for(String data : dataList) {
			TextMessage message = session.createTextMessage(data);
			message.setStringProperty("Tipo", "xml");
			producer.send(message);
		}
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
			con = factory.createConnection();
			Destination queue = (Destination) ctx.lookup("inbound");
			
			MockMessageProducer producer = new MockMessageProducer(con, queue);
			System.out.println("Will send messages...");
			producer.send();
			System.out.println("Done.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
