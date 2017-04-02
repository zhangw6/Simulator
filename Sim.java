/**
 * The driver of the simulation 
 */

public class Sim {
    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {
        
        Belt belt = new Belt();
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        BeltMover mover = new BeltMover(belt);
        Sensor sensor = new Sensor(belt);
        Arm arm = new Arm(belt);
        Inspector ins = new Inspector(belt);

        consumer.start();
        producer.start();
        mover.start();
        sensor.start();
        arm.start();
        ins.start();

        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive() &&
               sensor.isAlive() &&
               arm.isAlive() && ins.isAlive())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }

        // interrupt other threads
        consumer.interrupt();
        producer.interrupt();
        mover.interrupt();
        sensor.interrupt();
        arm.interrupt();
        ins.interrupt();

        System.out.println("Sim terminating");
        System.out.println(BicycleHandlingThread.getTerminateException());
        System.exit(0);
    }
}
