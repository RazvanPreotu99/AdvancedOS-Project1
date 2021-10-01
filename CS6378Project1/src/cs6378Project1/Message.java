package cs6378Project1;

import java.io.Serializable;

//Message needs to be serializable in order to send it using sockets
public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	//ID of the node sending the message
	NodeID source;
	
	//Payload of the message
	byte[] data;
	
	//Constructor
	public Message(NodeID source, byte[] data)
	{
		this.source = source;
		this.data = data;
	}
}

