package model;

import java.util.ArrayList;

public class ObservableList<T> extends ArrayList<T>{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean remove(Object o) {
		Boolean flag = super.remove(o);
		MainRoutine.checkDownloader();
		return flag;
	}
}