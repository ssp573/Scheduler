public class Process {
	private int A;
	private int B;
	private int C;
	private int IO;
	private int cpu_time_left;
	private int finish;
	public int blockedFor;
	public boolean terminated;
	public int io_time;
	public boolean ready;
	public boolean running;
	public boolean blocked;
	private int cpu_burst;
	private int io_burst;
	public boolean preempted;
	public int curr_burst;
	public Process(int a,int b, int c, int io){
		A=a;
		B=b;
		C=c;
		IO=io;
		cpu_time_left=c;
		finish=0;
		blocked=false;
		ready=false;
		running=false;
		blockedFor=0;
		terminated=false;
		io_time=0;
		preempted=false;
	}
	public int getCPU_time_left(){
		return cpu_time_left;
	}
	public int getFinish(){
		return finish;
	}
	public int getCPU_Burst(){
		return cpu_burst;
	}
	public int getIO_Burst(){
		return io_burst;
	}
	public int getA(){
		return A;
	}
	public int getB(){
		return B;
	}
	public int getC(){
		return C;
	}
	public int getIO(){
		return IO;
	}
	public boolean getBlocked(){
		return blocked;
	}
	public void setA(int a){
		A=a;
	}
	public void setCPU_time_left(int cpu){
		cpu_time_left=cpu;
	}
	public void setB(int b){
		B=b;
	}
	public void setC(int c){
		C=c;
	}
	public void setIO(int io){
		IO=io;
	}
	public void setFinish(int f){
		finish=f;
	}
	public void setCPU_Burst(int burst){
		cpu_burst=burst;
	}
	public void setIO_Burst(int burst){
		io_burst=burst;
	}

}
