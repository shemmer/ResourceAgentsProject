package resourceAgent;

import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public enum Resource {
	ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE;

	int capacity = 6;
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	private static final List<Resource> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	public static Resource randomResource()  {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
	public static List<Resource> allValuesAsList(){
		return VALUES;
	}
}
