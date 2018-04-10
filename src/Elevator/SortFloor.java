package Elevator;

/**
 * This class is used to sort array of floors by ascending or descending.
 * @author Chenxiao Cui
 * @version March 23, 2018
 */
public class SortFloor {	

	/**
	 * Sort floors by ascending
	 * @param param
	 * @return param int[]
	 */
	public static int[] ascSort(int[] param) {
		if( param == null || param.length < 2){
			new NullPointerException();
		}
		
		int in, out;
		int temp = 0;

		for (out = param.length; out > 0; out--) {
			for (in = 0; in < out - 1; in++) {
				if (param[in] > param[in + 1]) {
					temp = param[in];
					param[in] = param[in + 1];
					param[in + 1] = temp;
				}
			}
		}
		return param;
	}


	/**
	 * Sort floors by descending
	 * @param param
	 * @return param int[]
	 */
	public static int[] descSort(int[] param) {
		if( param == null || param.length < 2){
			new NullPointerException();
		}
		
		int in, out;
		int temp = 0;
		for (out = 0; out < param.length; out++) {
			for (in = param.length - 1; in > out; in--) {
				if (param[in] > param[in - 1]) {
					temp = param[in];
					param[in] = param[in - 1];
					param[in - 1] = temp;
				}
			}
		}
		return param;
	}
}
