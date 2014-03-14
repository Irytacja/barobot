package com.barobot.isp;


import com.barobot.common.interfaces.SerialInputListener;
import com.barobot.parser.Queue;
import com.barobot.parser.message.AsyncMessage;
import com.barobot.parser.output.AsyncDevice;
import com.barobot.parser.output.Mainboard;

public class Hardware {
	private WindowsSerialPort serial	= null;
	private Queue q						= new Queue();
	public String comPort				= "com1";
	static int mainboardSource			= 0;

	public Hardware(String comPort) {
		init();
		this.comPort = comPort;
		serial = new WindowsSerialPort( comPort, IspSettings.fullspeed );
	}
	public void init() {
		Mainboard mb	= new Mainboard();
	//	AsyncDevice c	= new Console();
	//	AsyncDevice u	= new MainScreen();
		/*
			mb.addGlobalRegex("^E.+", new GlobalMatch(){
				public boolean run( String in ) {
					Parser.log(Level.INFO, "Mainboard init: " + in);
					return true;
				}});
*/
		mainboardSource = Queue.registerSource( mb );
		Queue.enableDevice( mainboardSource );
	}

	public void send(String command, final String retcmd) {
		System.out.println("\t>>>add 2 send: " + command +" / "+retcmd);
		q.add( mainboardSource, command, retcmd );
	}
	public void send(String command) {
		q.add( command + "\n", false );
	}
	public void connect() {
		if(serial.isConnected()){
			return;
		}
		boolean res = serial.open();
		if(!res ){
			System.exit(-1);
		}
		try {
			Queue.getDevice( mainboardSource ).registerSender( serial );
			SerialInputListener listener = new SerialInputListener() {
				public void onRunError(Exception e) {
					// TODO Auto-generated method stub
				}
				public void onNewData(byte[] data) {
					String in = new String(data);
					q.read( mainboardSource, in );
				}
				public boolean isEnabled() {
					return true;
				}
			};
			serial.addOnReceive( listener);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	void setProgrammerSpeed(){
		serial.setBaud(IspSettings.programmspeed);
	}
	public void closeOnReady() {
		q.add( new AsyncMessage( true ){		// na koncu zamknij
			public void run(AsyncDevice dev) {
				close();
			}
		});
	}
	protected void close() {
		serial.close();
		if(q != null){
			q.clear( mainboardSource );
			Queue.disableDevice( mainboardSource );
		}
	}
	public Queue getQueue() {
		return q;
	}
}
