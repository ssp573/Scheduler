
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Scheduler {
	public static ArrayList<Process> Processes;
	static boolean verbose=false;
	static int counter=0;
	static ArrayList<Integer> Numbers=new ArrayList<Integer>();
	public static void main(String[] args) throws IOException{
		File f;
		Scanner sc;
		String filename="";
		if (args.length>0){
		if (args[0].equals("--verbose")){
			verbose=true;
			filename=args[1];
		}
		else{
			filename=args[0];
			verbose=false;
			}
		f=new File(filename);
		sc=new Scanner(f);
		Processes= getProcesses(sc);
		sc.close();}
		else{
			Scanner s=new Scanner(System.in);
			Processes= getProcess(s);
		}
		File file= new File("random-numbers");
		Scanner ransc = new Scanner(file);	
		while (ransc.hasNext()){
			int next=ransc.nextInt();
			Numbers.add(next);
		}
		ransc.close();
		FCFS(Processes);
		counter=0;
		f=new File(filename);
		sc=new Scanner(f);
		Processes= getProcesses(sc);
		RR(Processes,2);
		counter=0;
		f=new File(filename);
		sc=new Scanner(f);
		Processes= getProcesses(sc);
		Uniprogrammed(Processes);
		counter=0;
		f=new File(filename);
		sc=new Scanner(f);
		Processes= getProcesses(sc);
		SRTN(Processes);
	}
	
public static int randomOS(int seed) throws NumberFormatException, IOException{
		int random=Numbers.get(counter)%seed+1;
		counter++;
		return random;
	}

public static ArrayList<Process> getProcesses(Scanner sc) throws IOException{
	ArrayList<Process> Processes=new ArrayList<>();
	int no_of_processes= sc.nextInt();
	for (int i =0;i<no_of_processes ;i++){
		Process p=new Process(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt());
		Processes.add(p);
	}
	System.out.print("\nThe original input was:\t"+Processes.size());
	for (Process p:Processes){
		System.out.print("\t"+p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+ "\t");
		}
	System.out.println("");
	Collections.sort(Processes,new SortbyArrival());
	System.out.print("The (sorted) input is:\t"+Processes.size());
	for (Process p:Processes){
		System.out.print("\t"+p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+"\t");
		}
	return Processes;
}

public static ArrayList<Process> getProcess(Scanner s){
			ArrayList<Process> Processes=new ArrayList<Process>();
			int no_of_processes=s.nextInt();
			while (no_of_processes>0){
				int a= s.nextInt();
				int b= s.nextInt();
				int c= s.nextInt();
				int io=s.nextInt();
				Process p=new Process(a,b,c,io);
				Processes.add(p);
				no_of_processes--;
		}
		System.out.print("\nThe original input was:\t"+Processes.size());
		for (Process p:Processes){
			System.out.print("\t"+p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+ "\t");
			}
		System.out.println("");
		Collections.sort(Processes,new SortbyArrival());
		System.out.print("The (sorted) input is:\t"+Processes.size());
		for (Process p:Processes){
			System.out.print("\t"+p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+"\t");
			}
		return Processes;
	}
	
	
	public static void FCFS(ArrayList<Process> Processes) throws NumberFormatException, IOException{
			int total_time_left= get_total_time(Processes);
			int curr_time=0;
			float finish_time, io_total=0,cpu_total = 0,avg_ta_time=0,avg_wait_time=0;
			Process running=null;
			ArrayList<Process> ReadyQ= new ArrayList<Process>();
			ArrayList<Process> BlockedQ= new ArrayList<Process>();
			ArrayList<Process> Unstarted= new ArrayList<Process>();
			for (Process p: Processes){
				Unstarted.add(p);
			}
			if(verbose){
				System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
			}
			while (total_time_left>0){
				if (verbose){
					String[] states=new String[Processes.size()];
					int[] number=new int[Processes.size()];
					for (int i=0;i<Processes.size();i++){
						if (Processes.get(i).ready){
							states[i]="ready";
							number[i]=0;
						}
						else if (Processes.get(i).running){
							states[i]="running";
							number[i]=Processes.get(i).getCPU_Burst();
						}
						else if (Processes.get(i).blocked){
							states[i]="blocked";
							number[i]=Processes.get(i).getIO_Burst();
						}
						else if (Processes.get(i).terminated){
							states[i]="terminated";
							number[i]=0;
						}
						else{
							states[i]="unstarted";
							number[i]=0;
						}
					}
					System.out.println("");
					System.out.print("\nBefore Cycle "+curr_time+" :\t");
					int i=0;
					while (i<Processes.size()){
						System.out.print(states[i]+" "+number[i]+"\t\t");
						i++;
					}
				}
				if (running!=null){
					cpu_total=cpu_total+1;
					int burst=running.getCPU_Burst();
					int cpu_time_left=running.getC();
					if(burst>cpu_time_left){
						burst=cpu_time_left;
					}
					running.setCPU_Burst(burst-1);
					running.setC(cpu_time_left-1);
					total_time_left--;
					if(burst<2){
						if (running.getC()>0){
							running.setIO_Burst(randomOS(running.getIO()));
							running.blocked=true;
							running.running=false;
							running.ready=false;
						}
						else{
							running.terminated=true;
							running.running=false;
							running.blocked=false;
							running.ready=false;
							running.setFinish(curr_time);
						}
						running=null;
					}
				}
				
				if (ReadyQ.size()>0){
					Process temp=null;
					for (Process p:ReadyQ){
						if (running==null){
							p.setCPU_Burst(randomOS(p.getB()));
							p.ready=false;
							p.blocked=false;
							p.running=true;
							running=p;
							temp=p;
						}
					}
					ReadyQ.remove(temp);
				}
				
				if (BlockedQ.size()>0){
					io_total=io_total+1;
					ArrayList<Process> temp= new ArrayList<>();
					for(Process p : BlockedQ){
						int io_left=p.getIO_Burst();
						p.io_time++;
						p.setIO_Burst(io_left-1);
						if (io_left<2){
							if (running==null){
								p.setCPU_Burst(randomOS(p.getB()));
								p.running=true;
								p.blocked=false;
								p.ready=false;
								running=p;
							}
							else{
								p.ready=true;
								p.blocked=false;
								p.running=false;		
								p.setIO_Burst(-1);
							}
							temp.add(p);
						}
					}
					BlockedQ.removeAll(temp);
				}
				
				if (Unstarted.size()>0){
					ArrayList<Process> temp= new ArrayList<>();
					for(Process p : Unstarted){
						if (p.getA()<=curr_time){
							if (running==null){
								p.setCPU_Burst(randomOS(p.getB()));
								p.running=true;
								p.blocked=false;
								p.ready=false;
								running=p;
							}
							else{
								p.ready=true;
								p.blocked=false;
								p.running=false;
								p.setIO_Burst(-1);
							}
							temp.add(p);
						}
					}
					Unstarted.removeAll(temp);
				}
				
				for (Process p:Processes){
					if (p.ready){
						if (!ReadyQ.contains(p)){
							ReadyQ.add(p);
						}	
					}
					else if (p.blocked){
						if (!BlockedQ.contains(p)){
							BlockedQ.add(p);
						}
					}
				}
				if (BlockedQ.size()>1){
					Collections.sort(BlockedQ, new SortbyArrival());
				}
				curr_time++;
			}
			finish_time=curr_time-1;
			System.out.println("\n\nThe scheduling algorithm used was First Come First Served.\n");
			int no_of_processes=Processes.size();
			for(int i=0;i<no_of_processes;i++){
				int ta_time=(Processes.get(i).getFinish()-Processes.get(i).getA());
				int wa_time=((Processes.get(i).getFinish()-Processes.get(i).getA())-Processes.get(i).io_time-Processes.get(i).getCPU_time_left());
				avg_ta_time=avg_ta_time+ta_time;
				avg_wait_time=avg_wait_time+wa_time;
				System.out.println("\nProcess "+i+"\n\t(A,B,C,IO) = "+"("+Processes.get(i).getA()+","+Processes.get(i).getB()+","+Processes.get(i).getCPU_time_left()+","+Processes.get(i).getIO()+")");
				System.out.println("\tFinishing Time: "+(Processes.get(i).getFinish())+"\n\tTurnaround Time: "+ta_time);
				System.out.println("\tI/O Time: "+Processes.get(i).io_time+"\n\tWaiting Time: "+wa_time);
			}
			System.out.println("\nSummary Data:\n"+"\tFinishing time: "+finish_time+"\n\tCPU Utilization: "+cpu_total/finish_time+"\n\tI/O Utilization: "+io_total/finish_time+"\n\tThroughput: "+Processes.size()*100/finish_time+" processes per hundred cycles\n\t"+"Average Turnaround Time: "+avg_ta_time/Processes.size()+"\n\tAverage Waiting time: "+avg_wait_time/Processes.size());
			System.out.println("======================================================================================================");
	}
	
	public static int SortbyArrival(Process a,Process b){
		return (a.getA()-b.getA());
	}
	
	public static void RR(ArrayList<Process> Processes, int quantum) throws NumberFormatException, IOException{
		int total_time_left= get_total_time(Processes);
		int curr_time=0;
		float finish_time, io_total=0,cpu_total = 0,avg_ta_time=0,avg_wait_time=0;
		Process running=null;
		ArrayList<Process> ReadyQ= new ArrayList<Process>();
		ArrayList<Process> BlockedQ= new ArrayList<Process>();
		ArrayList<Process> Unstarted= new ArrayList<Process>();
		for (Process p: Processes){
			Unstarted.add(p);
		}
		if(verbose){
			System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
		}
		while (total_time_left>0){
			boolean MoveToReady=false;
			if (verbose){
				String[] states=new String[Processes.size()];
				int[] number=new int[Processes.size()];
				for (int i=0;i<Processes.size();i++){
					if (Processes.get(i).ready){
						states[i]="ready";
						number[i]=Processes.get(i).curr_burst;
					}
					else if (Processes.get(i).running){
						states[i]="running";
						number[i]=Processes.get(i).curr_burst;
					}
					else if (Processes.get(i).blocked){
						states[i]="blocked";
						number[i]=Processes.get(i).getIO_Burst();
					}
					else if (Processes.get(i).terminated){
						states[i]="terminated";
						number[i]=0;
					}
					else{
						states[i]="unstarted";
						number[i]=0;
					}
				}
				System.out.println("");
				System.out.print("\nBefore Cycle "+curr_time+" :\t");
				int i=0;
				while (i<Processes.size()){
					System.out.print(states[i]+" "+number[i]+"\t\t");
					i++;
				}
			}
			int count=0;
			for (Process p:Processes){
				if (p.blocked && p.getIO_Burst()>1)
					count++;
				else if (p.terminated)
					count++;
			}
			if (count==Processes.size()-1){
				MoveToReady=true;
			}
			if (running!=null){
				cpu_total++;
				int burst=running.curr_burst;
				int cpu_time_left=running.getC();
				if(burst>cpu_time_left){
					burst=cpu_time_left;
				}
				running.curr_burst--;
				running.setC(cpu_time_left-1);
				total_time_left--;
				if(burst<2 && running.getCPU_Burst()!=0){
					if (running.getC()>0){
						running.preempted=true;
						running.blocked=false;
						running.running=false;
						running.ready=true;
						if (MoveToReady){
							ReadyQ.add(running);
						}
					}
					else{
						running.terminated=true;
						running.running=false;
						running.blocked=false;
						running.ready=false;
						running.setFinish(curr_time);
					}
					running=null;
				}
				else if(burst<2 && running.getCPU_Burst()==0){
					if (running.getC()>0){
						running.setIO_Burst(randomOS(running.getIO()));
						running.blocked=true;
						running.running=false;
						running.ready=false;
					}
					else{
						running.terminated=true;
						running.running=false;
						running.blocked=false;
						running.ready=false;
						running.setFinish(curr_time);
					}
					running=null;
				}
			}
			
			if (ReadyQ.size()>0){
				Process temp=null;
				for (Process p:ReadyQ){
					if (running==null){
						int burst_time;
						if (p.preempted){
							burst_time=p.getCPU_Burst();
						}else{
							burst_time=randomOS(p.getB());
							if (burst_time>p.getC()){
								burst_time=p.getC();
							}
						}
						if (burst_time>quantum){
							p.curr_burst=quantum;
							p.setCPU_Burst(burst_time-quantum);
						}else{
							p.curr_burst=burst_time;
							p.preempted=false;
							p.setCPU_Burst(0);
							}
						p.ready=false;
						p.blocked=false;
						p.running=true;
						running=p;
						temp=p;
					}
				}
				ReadyQ.remove(temp);
			}
			
			if (BlockedQ.size()>0){
				io_total++;
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p : BlockedQ){
					int io_left=p.getIO_Burst();
					p.io_time++;
					p.setIO_Burst(io_left-1);
					if (io_left<2){
						if (running==null){
							int burst_time=randomOS(p.getB());
							if (burst_time>quantum){
								p.curr_burst=quantum;
								p.setCPU_Burst(burst_time-quantum);
							}
							else{
								p.curr_burst=burst_time;
								p.setCPU_Burst(0);
							}
							p.running=true;
							p.blocked=false;
							p.ready=false;
							running=p;
						}
						else{
							p.ready=true;
							p.blocked=false;
							p.running=false;		
							p.setIO_Burst(-1);
						}
						temp.add(p);
					}
				}
				BlockedQ.removeAll(temp);
			}
			
			if (Unstarted.size()>0){
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p : Unstarted){
					if (p.getA()<=curr_time){
						if (running==null){
							int burst_time=randomOS(p.getB());
							if (burst_time>quantum){
								p.curr_burst=quantum;
								p.setCPU_Burst(burst_time-quantum);
							}
							else{
								p.curr_burst=burst_time;
								p.setCPU_Burst(0);
							}
							p.running=true;
							p.blocked=false;
							p.ready=false;
							running=p;
						}
						else{
							p.ready=true;
							p.blocked=false;
							p.running=false;
							p.setIO_Burst(-1);
						}
						temp.add(p);
					}
				}
				Unstarted.removeAll(temp);
			}
			
			for (Process p:Processes){
				if (p.ready){
					if (!ReadyQ.contains(p)){
						ReadyQ.add(p);
					}	
				}
				else if (p.blocked){
					if (!BlockedQ.contains(p)){
						BlockedQ.add(p);
					}
				}
			}
			if (BlockedQ.size()>1){
				Collections.sort(BlockedQ, new SortbyArrival());
			}
			curr_time++;
		}
		finish_time=curr_time-1;
		System.out.println("\n\nThe scheduling algorithm used was Round Robin\n");
		int no_of_processes=Processes.size();
		for(int i=0;i<no_of_processes;i++){
			int ta_time=(Processes.get(i).getFinish()-Processes.get(i).getA());
			int wa_time=((Processes.get(i).getFinish()-Processes.get(i).getA())-Processes.get(i).io_time-Processes.get(i).getCPU_time_left());
			avg_ta_time=avg_ta_time+ta_time;
			avg_wait_time=avg_wait_time+wa_time;
			System.out.println("\n\nProcess "+i+"\n\t(A,B,C,IO) = "+"("+Processes.get(i).getA()+","+Processes.get(i).getB()+","+Processes.get(i).getCPU_time_left()+","+Processes.get(i).getIO()+")");
			System.out.println("\tFinishing Time: "+(Processes.get(i).getFinish())+"\n\tTurnaround Time: "+ta_time);
			System.out.println("\tI/O Time: "+Processes.get(i).io_time+"\n\tWaiting Time: "+wa_time);
		}
		System.out.println("\nSummary Data:\n"+"\tFinishing time: "+finish_time+"\n\tCPU Utilization: "+cpu_total/finish_time+"\n\tI/O Utilization: "+io_total/finish_time+"\n\tThroughput: "+Processes.size()*100/finish_time+" processes per hundred cycles\n\t"+"Average Turnaround Time: "+avg_ta_time/Processes.size()+"\n\tAverage Waiting time: "+avg_wait_time/Processes.size());
		System.out.println("=====================================================================================================");
	}
	
	public static boolean AllTerminated(ArrayList<Process> Processes){
		int count=0;
		for (Process p:Processes){
			if (p.terminated){
				count++;
			}
		}
		if (count==Processes.size()){
			return true;
		}
		else
			return false;
		
	}
	
	public static void Uniprogrammed(ArrayList<Process> Processes) throws NumberFormatException, IOException{
		int curr_time=0;
		float finish_time, io_total=0,cpu_total = 0,avg_ta_time=0,avg_wait_time=0;
		Process running=null;
		ArrayList<Process> ReadyQ= new ArrayList<Process>();
		ArrayList<Process> BlockedQ= new ArrayList<Process>();
		ArrayList<Process> Unstarted= new ArrayList<Process>();
		for (Process p: Processes){
			Unstarted.add(p);
		}
		if(verbose){
			System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
		}
		while (!AllTerminated(Processes)){
			if (verbose){
				String[] states=new String[Processes.size()];
				int[] number=new int[Processes.size()];
				for (int i=0;i<Processes.size();i++){
					if (Processes.get(i).ready){
						states[i]="ready";
						number[i]=0;
					}
					else if (Processes.get(i).running){
						states[i]="running";
						number[i]=Processes.get(i).getCPU_Burst();
					}
					else if (Processes.get(i).blocked){
						states[i]="blocked";
						number[i]=Processes.get(i).getIO_Burst();
					}
					else if (Processes.get(i).terminated){
						states[i]="terminated";
						number[i]=0;
					}
					else{
						states[i]="unstarted";
						number[i]=0;
					}
				}
				System.out.println("");
				System.out.print("\nBefore Cycle "+curr_time+" :\t");
				int i=0;
				while (i<Processes.size()){
					System.out.print(states[i]+" "+number[i]+"\t\t");
					i++;
				}
			}
			if (running!=null && !running.blocked){
				cpu_total++;
				int burst=running.getCPU_Burst();
				int cpu_time_left=running.getC();
				if(burst>cpu_time_left){
					burst=cpu_time_left;
				}
					running.setCPU_Burst(burst-1);
					running.setC(cpu_time_left-1);
				if(burst<2){
					if (running.getC()>0){
						running.setIO_Burst(randomOS(running.getIO()));
						running.blocked=true;
						running.running=false;
						running.ready=false;
					}else{
						running.terminated=true;
						running.running=false;
						running.blocked=false;
						running.ready=false;
						running.setFinish(curr_time);
						running=null;
					}
				}
			}
			
			if (ReadyQ.size()>0){
				Process temp=null;
				for (Process p: ReadyQ){
					if (running==null){
						p.setCPU_Burst(randomOS(p.getB()));
						p.ready=false;
						p.blocked=false;
						p.running=true;
						running=p;
						temp=p;
					}
				}
				ReadyQ.remove(temp);
			}

			if (BlockedQ.size()>0){
				io_total++;
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p : BlockedQ){
					int io_left=p.getIO_Burst();
					p.io_time++;
					p.setIO_Burst(io_left-1);
					if (io_left<2){
						p.setCPU_Burst(randomOS(p.getB()));
						p.running=true;
						p.blocked=false;
						p.ready=false;
						running=p;
						temp.add(p);
					}
				}
				BlockedQ.removeAll(temp);
			}

			if (Unstarted.size()>0){
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p:Unstarted){
					if  (p.getA()<=curr_time){
						if(running==null){
							p.setCPU_Burst(randomOS(p.getB()));
							p.running=true;
							p.blocked=false;
							p.ready=false;
							running=p;
						}else{
							p.ready=true;
							p.blocked=false;
							p.running=false;
							p.setIO_Burst(-1);
						}
						temp.add(p);
					}
				}
				Unstarted.removeAll(temp);
			}
			for (Process p:Processes){
				if (p.ready){
					if (!ReadyQ.contains(p)){
						ReadyQ.add(p);
					}	
				}
				else if (p.blocked){
					if (!BlockedQ.contains(p)){
						BlockedQ.add(p);
					}
				}
			}
			if (BlockedQ.size()>1){
				Collections.sort(BlockedQ, new SortbyArrival());
			}
			curr_time++;
			
		}
		finish_time=curr_time-1;
		System.out.println("\n\nThe scheduling algorithm used was Uniprogrammed\n");
		int no_of_processes=Processes.size();
		for(int i=0;i<no_of_processes;i++){
			int ta_time=(Processes.get(i).getFinish()-Processes.get(i).getA());
			int wa_time=((Processes.get(i).getFinish()-Processes.get(i).getA())-Processes.get(i).io_time-Processes.get(i).getCPU_time_left());
			avg_ta_time=avg_ta_time+ta_time;
			avg_wait_time=avg_wait_time+wa_time;
			System.out.println("\nProcess "+i+"\n\t(A,B,C,IO) = "+"("+Processes.get(i).getA()+","+Processes.get(i).getB()+","+Processes.get(i).getCPU_time_left()+","+Processes.get(i).getIO()+")");
			System.out.println("\tFinishing Time: "+(Processes.get(i).getFinish())+"\n\tTurnaround Time: "+ta_time);
			System.out.println("\tI/O Time: "+Processes.get(i).io_time+"\n\tWaiting Time: "+wa_time);
		}
		System.out.println("\nSummary Data:\n"+"\tFinishing time: "+finish_time+"\n\tCPU Utilization: "+cpu_total/finish_time+"\n\tI/O Utilization: "+io_total/finish_time+"\n\tThroughput: "+Processes.size()*100/finish_time+" processes per hundred cycles\n\t"+"Average Turnaround Time: "+avg_ta_time/Processes.size()+"\n\tAverage Waiting time: "+avg_wait_time/Processes.size());	
		System.out.println("=====================================================================================================");
	}
	
	public static int SortbyIndex(Process a, Process b){
		return Processes.indexOf(a)-Processes.indexOf(b);
	}
	
	public static void SRTN(ArrayList<Process> Processes) throws NumberFormatException, IOException{
		int curr_time=0;
		float finish_time, io_total=0,cpu_total = 0,avg_ta_time=0,avg_wait_time=0;
		Process running=null;
		ArrayList<Process> ReadyQ= new ArrayList<Process>();
		ArrayList<Process> BlockedQ= new ArrayList<Process>();
		ArrayList<Process> Unstarted= new ArrayList<Process>();
		for (Process p: Processes){
			Unstarted.add(p);
		}
		if(verbose){
			System.out.println("\n\nThis detailed printout gives the state and remaining burst for each process");
		}
		while (!AllTerminated(Processes)){
			if (verbose){
				String[] states=new String[Processes.size()];
				int[] number=new int[Processes.size()];
				for (int i=0;i<Processes.size();i++){
					if (Processes.get(i).ready){
						states[i]="ready";
						number[i]=Processes.get(i).getCPU_Burst();
					}
					else if (Processes.get(i).running){
						states[i]="running";
						number[i]=Processes.get(i).getCPU_Burst();
					}
					else if (Processes.get(i).blocked){
						states[i]="blocked";
						number[i]=Processes.get(i).getIO_Burst();
					}
					else if (Processes.get(i).terminated){
						states[i]="terminated";
						number[i]=0;
					}
					else{
						states[i]="unstarted";
						number[i]=0;
					}
				}
				System.out.println("");
				System.out.print("\nBefore Cycle "+curr_time+" :\t");
				int i=0;
				while (i<Processes.size()){
					System.out.print(states[i]+" "+number[i]+"\t\t");
					i++;
				}
			}
			if (running!=null){
				cpu_total++;
				int burst=running.getCPU_Burst();
				int cpu_time_left=running.getC();
				running.setCPU_Burst(burst-1);
				running.setC(cpu_time_left-1);
				if (burst<2){
					if(running.getC()>0){
						running.setIO_Burst(randomOS(running.getIO()));
						running.blocked=true;
						running.running=false;
						running.ready=false;
					}else{
						running.terminated=true;
						running.blocked=false;
						running.running=false;
						running.ready=false;
						running.setFinish(curr_time);
					}
					running=null;
				}
			}
			
			if (Unstarted.size()>0){
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p: Unstarted){
					if (p.getA()<=curr_time){
						p.blocked=false;
						p.ready=true;
						p.running=false;
						ReadyQ.add(p);
						temp.add(p);
					}
				}
				Unstarted.removeAll(temp);
				}
			
			if (BlockedQ.size()>0){
				io_total++;
				ArrayList<Process> temp= new ArrayList<>();
				for(Process p : BlockedQ){
					int io_left=p.getIO_Burst();
					p.io_time++;
					p.setIO_Burst(io_left-1);
					if (io_left<2){
						p.running=false;
						p.blocked=false;
						p.ready=true;
						ReadyQ.add(p);
						temp.add(p);
					}
				}
				BlockedQ.removeAll(temp);
			}
			
			if (ReadyQ.size()>0){
				Collections.sort(ReadyQ,new SortbyIndex());
				Collections.sort(ReadyQ, new SortbyArrival());
				Process least_time = Collections.min(ReadyQ, new SortbyC());
				if (running==null || least_time.getC()<running.getC()){
					if (running!=null){
						running.ready=true;
						running.blocked=false;
						running.running=false;
					}
					least_time.running=true;
					least_time.blocked=false;
					least_time.ready=false;
					running=least_time;
					ReadyQ.remove(least_time);
					if (least_time.getCPU_Burst()==0){
						least_time.setCPU_Burst(randomOS(least_time.getB()));
					}
					if (least_time.getCPU_Burst()>least_time.getC()){
						least_time.setCPU_Burst(least_time.getC());
					}
				}
			}
			for (Process p:Processes){
				if (p.ready){
					if (!ReadyQ.contains(p)){
						ReadyQ.add(p);
					}	
				}
				else if (p.blocked){
					if (!BlockedQ.contains(p)){
						BlockedQ.add(p);
					}
				}
			}
			curr_time++;

		}
		finish_time=curr_time-1;
		System.out.println("\n\nThe scheduling algorithm used was Preemptive Shortest Job First\n");
		int no_of_processes=Processes.size();
		for(int i=0;i<no_of_processes;i++){
			int ta_time=(Processes.get(i).getFinish()-Processes.get(i).getA());
			int wa_time=((Processes.get(i).getFinish()-Processes.get(i).getA())-Processes.get(i).io_time-Processes.get(i).getCPU_time_left());
			avg_ta_time=avg_ta_time+ta_time;
			avg_wait_time=avg_wait_time+wa_time;
			System.out.println("\nProcess "+i+"\n\t(A,B,C,IO) = "+"("+Processes.get(i).getA()+","+Processes.get(i).getB()+","+Processes.get(i).getCPU_time_left()+","+Processes.get(i).getIO()+")");
			System.out.println("\tFinishing Time: "+(Processes.get(i).getFinish())+"\n\tTurnaround Time: "+ta_time);
			System.out.println("\tI/O Time: "+Processes.get(i).io_time+"\n\tWaiting Time: "+wa_time);
		}
		System.out.println("\nSummary Data:\n"+"\tFinishing time: "+finish_time+"\n\tCPU Utilization: "+cpu_total/finish_time+"\n\tI/O Utilization: "+io_total/finish_time+"\n\tThroughput: "+Processes.size()*100/finish_time+" processes per hundred cycles\n\t"+"Average Turnaround Time: "+avg_ta_time/Processes.size()+"\n\tAverage Waiting time: "+avg_wait_time/Processes.size());	
		System.out.println("==============================================================================================================================");
	}
	
	public static int get_total_time(ArrayList<Process> Processes){
		int total=0;
		for (Process p:Processes){
			total=total+p.getC();
		}
		return total;
	} 
	

	public static boolean isReady(Process p,int curr_time){
		if (curr_time<p.getA() || p.blockedFor!=0){
			return false;
		}
		else{
			return true;
		}
	}
	
}