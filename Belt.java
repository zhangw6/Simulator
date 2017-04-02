/**
 * The bicycle quality control belt
 */
public class Belt {

	// the items in the belt segments

	protected Bicycle[] segment;

	// the length of this belt
	protected int beltLength = 5;

	// to help format output trace
	final private static String indentation = "                  ";
	protected boolean tagged = false;
	protected boolean held = false;
	protected Bicycle tempbike, tempbike2, tempbike3;
	protected Bicycle insBike;
	protected Bicycle inspectedBike;

	/**
	 * Create a new, empty belt, initialised as empty
	 */
	public Belt() {
		segment = new Bicycle[beltLength];
		for (int i = 0; i < segment.length; i++) {
			segment[i] = null;
		}
	}

	/**
	 * Put a bicycle on the belt.
	 * 
	 * @param bicycle
	 *            the bicycle to put onto the belt.
	 * @param index
	 *            the place to put the bicycle
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void put(Bicycle bicycle, int index) throws InterruptedException {

		// while there is another bicycle in the way, block this thread
		while (segment[index] != null) {
			wait();
		}

		// insert the element at the specified location
		segment[index] = bicycle;

		// make a note of the event in output trace
		System.out.println(bicycle + " arrived");

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * Take a bicycle off the end of the belt
	 * 
	 * @return the removed bicycle
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized Bicycle getEndBelt() throws InterruptedException {

		Bicycle bicycle;

		// while there is no bicycle at the end of the belt, block this thread
		while (segment[segment.length - 1] == null) {
			wait();
		}

		// get the next item
		bicycle = segment[segment.length - 1];
		segment[segment.length - 1] = null;
		if (!bicycle.defective) {
			// make a note of the event in output trace
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " departed");
		} else {
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " recycled");
		}
		// notify any waiting threads that the belt has changed
		notifyAll();
		return bicycle;
	}

	/**
	 * Move the belt along one segment
	 * 
	 * @throws OverloadException
	 *             if there is a bicycle at position beltLength.
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void move() throws InterruptedException, OverloadException {
		// if there is something at the end of the belt,
		// or the belt is empty, do not move the belt
		while (isEmpty() || segment[segment.length - 1] != null) {
			wait();
		}

		// double check that a bicycle cannot fall of the end
		if (segment[segment.length - 1] != null) {
			String message = "Bicycle fell off end of " + " belt";
			throw new OverloadException(message);
		}

		// move the elements along, making position 0 null
		for (int i = segment.length - 1; i > 0; i--) {
			if (this.segment[i - 1] != null) {
				System.out.println(indentation + this.segment[i - 1] + " [ s" + (i) + " -> s" + (i + 1) + " ]");
			}
			segment[i] = segment[i - 1];
		}
		segment[0] = null;
		// System.out.println(indentation + this);

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * @return the maximum size of this belt
	 */
	public int length() {
		return beltLength;
	}

	/**
	 * Peek at what is at a specified segment
	 * 
	 * @param index
	 *            the index at which to peek
	 * @return the bicycle in the segment (or null if the segment is empty)
	 */
	public Bicycle peek(int index) {
		Bicycle result = null;
		if (index >= 0 && index < beltLength) {
			result = segment[index];
		}
		return result;
	}

	/**
	 * Check whether the belt is currently empty
	 * 
	 * @return true if the belt is currently empty, otherwise false
	 */
	private boolean isEmpty() {
		for (int i = 0; i < segment.length; i++) {
			if (segment[i] != null) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return java.util.Arrays.toString(segment);
	}

	/*
	 * @return the final position on the belt
	 */
	public int getEndPos() {
		return beltLength - 1;
	}

	public synchronized void senseBicycle() throws InterruptedException {

		Bicycle bicycle;

		// while there is no bicycle at the end of the belt, block this thread
		while (segment[2] == null) {
			tagged = false;
			tempbike = null;
			wait();
		}

		// get the next item
		bicycle = segment[2];
		tempbike3 = segment[2];
		if (bicycle.tagged) {
			segment[2] = null;
			tagged = true;
			tempbike = bicycle;
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " sensed result: tagged");

		} else {
			tagged = false;
			tempbike = null;
			// make a note of the event in output trace
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " sensed result: not tagged");

			// notify any waiting threads that the belt has changed
			notifyAll();
		}

	}

	/**
	 * get the tagged bike from belt via arm
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized Bicycle getTagged() throws InterruptedException {

		// while there is no bicycle at the end of the belt, block this thread
		while (tempbike == null) {
			wait();
		}
		held = true;
		// get the next item

		// make a note of the event in output trace
		System.out.print(indentation + indentation);
		System.out.println(tempbike + " heldByArm");

		// notify any waiting threads that the belt has changed
		notifyAll();
		return tempbike;
	}

	public synchronized Bicycle dropTagged() throws InterruptedException {

		// while there is no bicycle at the end of the belt, block this thread
		while (held == false && tempbike == null) {
			wait();
		}
		held = false;
		// get the next item

		// make a note of the event in output trace
		System.out.print(indentation + indentation);
		System.out.println(tempbike + " dropByArm");
		// notify any waiting threads that the belt has changed
		notifyAll();
		insBike = tempbike;
		tempbike = null;
		return insBike;
	}

	/**
	 * get the inspected bike from belt
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized void inspectBicycle() throws InterruptedException {

		Bicycle bicycle;

		// while there is no bicycle at the end of the belt, block this thread
		while (insBike == null) {
			wait();
		}

		bicycle = insBike;
		insBike = null;
		// get the next item
		if (bicycle.defective) {
			bicycle.tagged = true;
			inspectedBike = bicycle;
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " inspected and defective, so we keep tags");

		} else {
			bicycle.tagged = false;
			inspectedBike = bicycle;
			// make a note of the event in output trace
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " inspected and not defective, so we remove tags");

			// notify any waiting threads that the belt has changed
			notifyAll();
		}

	}

	/**
	 * get the inspected bike from belt
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized void getInspectedBicycle() throws InterruptedException {

		// while there is no bicycle at the end of the belt, block this thread
		while (inspectedBike == null) {
			wait();
		}
		held = true;

		System.out.print(indentation + indentation);
		System.out.println(inspectedBike + " get inspected bike by arm");

		notifyAll();

	}

	/**
	 * drop inspected bike by arm
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized Bicycle dropInspected() throws InterruptedException {

		// while there is no bicycle at the end of the belt, block this thread
		while (inspectedBike == null) {
			wait();
		}

		held = false;

		System.out.print(indentation + indentation);
		System.out.println(inspectedBike + " drop inspected bike By Arm");

		// notify any waiting threads that the belt has changed
		notifyAll();
		tempbike3 = inspectedBike;

		inspectedBike = null;

		return tempbike3;
	}

	/**
	 * re-put the bike on slot 3
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized void reput() throws InterruptedException {

		// while there is another bicycle in the way, block this thread
		while (segment[2] == null || segment[3] != null || tempbike3 == null) {
			wait();
		}

		// insert the element at the specified location
		segment[3] = tempbike3;

		// make a note of the event in output trace

		System.out.println(tempbike3 + " get reput");

		tempbike3 = null;

		// notify any waiting threads that the belt has changed
		notifyAll();

	}

}
