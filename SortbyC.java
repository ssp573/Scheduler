import java.util.Comparator;


public class SortbyC implements Comparator<Process>{

	@Override
	public int compare(Process o1, Process o2) {
		// TODO Auto-generated method stub
		return o1.getC()-o2.getC();
	}

}
