
public class Arm extends BicycleHandlingThread {

	 // the belt to which the producer puts the bicycles
	    protected Belt belt;
	
	    /**
	     * Create a new producer to feed a given belt
	     */
	    Arm(Belt belt) {
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
	                belt.getTagged();
	                belt.dropTagged();
	                belt.getInspectedBicycle();
	                belt.dropInspected();
	                belt.reput();
	       	        sleep(1000);

	           
	            } catch (InterruptedException e) {
	                this.interrupt();
	            }
	        }
	        System.out.println("Arm terminated");
	    }

	
	
	
	
	
}
