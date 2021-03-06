
public class Inspector extends BicycleHandlingThread {

	 // the belt to which the producer puts the bicycles
	    protected Belt belt;
	
	    /**
	     * Create a new producer to feed a given belt
	     */
	    Inspector(Belt belt) {
	        super();
	        this.belt = belt;
	    }
	
	    /**
	     * The thread's main method. 
	     * Continually tries to place bicycles on the belt at random intervals.
	     */
	    public void run() {
	        while (!isInterrupted()) {
	            try {
	                // put a new bicycle on the belt
	                belt.inspectBicycle();
	                sleep(50);
	
	           
	            } catch (InterruptedException e) {
	                this.interrupt();
	            }
	        }
	        System.out.println("Inspector terminated");
	    }

	
	
	
	
	
}
