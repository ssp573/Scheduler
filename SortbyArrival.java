import java.util.Comparator;


public class SortbyArrival implements Comparator<Process>{

	@Override
	public int compare(Process o1, Process o2) {
		// TODO Auto-generated method stub
		return o1.getA()-o2.getA();
	}

}
