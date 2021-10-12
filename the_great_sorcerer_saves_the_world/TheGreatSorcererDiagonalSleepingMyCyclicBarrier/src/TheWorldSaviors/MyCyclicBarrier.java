package TheWorldSaviors;

/**
 * This is my custom Cyclic Barrier. It has a constructor which receives the number of parties and it also has the
 * method await used to make the threads wait until all of them arrive to the synchronization point.<br>
 * @author Anghel Florina-Catalina
 *
 */
public class MyCyclicBarrier {
	int initialParties; //total parties
    int partiesAwait; //parties yet to arrive
    Runnable cyclicBarrrierEvent;
    
    /**
     * New CyclicBarrier is created where parties number of thread wait for each
     * other to reach common barrier point, when all threads have reached common
     * barrier point, parties number of waiting threads are released and
     * barrierAction (event) is triggered.
     * @param parties int - the number of threads that need to reach the barrier before they are all allowed to continue
     */
    public MyCyclicBarrier(int parties) {
           initialParties=parties;
           partiesAwait=parties;   
    }
 
    /**
     * If the current thread is not the last to arrive(i.e. call await() method) then
     * it waits until one of the following things happens:
     * <ul>
     * <li>The last thread to call arrive(i,.e. call await() method)</li>
     * <li>Some other thread interrupts the current thread</li>
     * <li>Some other thread interrupts one of the other waiting threads</li>
     * <li>Some other thread times out while waiting for barrier</li>
     * <li>Some other thread invokes reset() method on this cyclicBarrier</li>
     * </ul>
     */
    public synchronized void await() throws InterruptedException {
           //decrements awaiting parties by 1.
           partiesAwait--;
           
           //If the current thread is not the last to arrive, thread will wait.
           if(partiesAwait>0){
                  this.wait();
           }else{
        	   /*If the current thread is last to arrive, notify all waiting threads, and
           		launch event*/
                  /* All parties have arrive, make partiesAwait equal to initialParties,
                    so that CyclicBarrier could become cyclic. */
                  partiesAwait=initialParties;
                  
                  notifyAll(); //notify all waiting threads
           }
    }
}
