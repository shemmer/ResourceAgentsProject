package offer;

import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public enum Resource {
	//TODO Change names
	ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE, DUMMY, DUMMY_MAX, ID;

	private long timeStamp;
	private static final List<Resource> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	public static Resource randomResource()  {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
	public static List<Resource> allValuesAsList(){
		return VALUES;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
