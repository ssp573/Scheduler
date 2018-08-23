import java.util.Comparator;


public class SortbyIndex implements Comparator<Process>{

	@Override
	public int compare(Process o1, Process o2) {
		// TODO Auto-generated method stub
		return Scheduler.Processes.indexOf(o1)-Scheduler.Processes.indexOf(o2);
	}

}
