

public abstract class Thread2 extends Thread {
	protected volatile boolean running;
	public Thread2(){
		super();
		running=true;
	}
	
	public void pause(){
		running=false;
	}
	
	public void go(){
		running=true;
	}
}
