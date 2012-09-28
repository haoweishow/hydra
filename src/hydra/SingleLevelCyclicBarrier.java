package hydra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Demonstrator how to run multithreaded calculations
 *  from a master thread that executes calculator threads
 *  that all read some common list in un-synchronized fashions,
 *  then hold a long lock to write to a hash map in synchronized fashion.
 *  Master thread uses cyclic barrier (num of threads + 1 for itself)
 *  to wait for all calculators to complete.
 *
 * @author Edmon Begoli
 */
public class SingleLevelCyclicBarrier  {

	static ArrayList<Long> factor1 = new ArrayList<Long>();
	static ArrayList<Long> factor2 = new ArrayList<Long>();
	static HashMap<String, Long> results = new HashMap<String, Long>();
	//cyclic barrier for common completion
	CyclicBarrier barrier;
	int numOfThreads;

        //set up lists and results hash map
	static{
		for ( int i = 0; i < 1000; i++ ){
			factor1.add( new Long( (int)(Math.random() * 100000 )) );
			factor2.add( new Long( (int)(Math.random() * 1000 )) );
			results.put( "result-" + i, new Long( 0 ) );
		}

	}//end static

        /** Set up number of threads and barrier size (num of threads + 1 for parent)
	 * @param numOfThreads
	 */
	public SingleLevelCyclicBarrier( int numOfThreads ){
		this.numOfThreads = numOfThreads;
	       	this.barrier = new CyclicBarrier( numOfThreads + 1 );
        }//end constructor

	/** Inner class - a runnable calculator thread
	 */
        class CalculatorThread implements Runnable{

	   int id = 0;
	   @Override
	   public void run() {
		System.out.println( id + " starting at " + java.util.Calendar.getInstance().getTime() );
		Random random = new Random();
		int randomIndex = random.nextInt( 1000 );
		long looping = 10000000;
		long value = factor1.get( randomIndex ).longValue() * factor1.get( randomIndex ).longValue();
		//just some code to keep the thread and CPU busy
		for ( long i = 0; i < (randomIndex * looping ); i++ ){
                      long result = randomIndex * random.nextInt( 1000 );
		}//end for

		System.out.println( id + " thread process finished at " +
			                  java.util.Calendar.getInstance().getTime() +
					  " for " + randomIndex + "*10^6" );


		//synchronize on the hashmap results
		// keep lock as short as possible
		synchronized ( results ){

		     results.put( "result-" +randomIndex, new Long( value ) );

		     System.out.println( id + " result of calculation[" + value 
			                    +"] put at "
					    + java.util.Calendar.getInstance().getTime() );

		}
		//decrement the barrier indicating that this thread is done and waiting for others
		try {
			System.out.println( id + " at barrier position " + barrier.getNumberWaiting() + 
			      " waiting. " );
			barrier.await(); //thread is done and waiting for others to finish
		} catch (InterruptedException ex) {
				Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE,
				      "child " + id + "failed ", ex);
		} catch (BrokenBarrierException ex) {
				Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE, 
				      "child " + id + "failed on await", ex);
		}
	   }//end method
	}//end inner class

	/** Inner class - a runnable compression thread that exhibits CPU oriented task
	 */
    class CompressionThread implements Runnable{
	   int id = 0;
	   String source;

	   @Override
	   public void run() {
		System.out.println( id + " starting at " + java.util.Calendar.getInstance().getTime() );
		Zip.zipFile( source , id + ".zip" );
		System.out.println( id + " compression thread finished at " +
			                  java.util.Calendar.getInstance().getTime() +
					  " for " + id + ".zip"  );
		//decrement the barrier indicating that this thread is done and waiting for others
		try {
			System.out.println( id + " at barrier position " + barrier.getNumberWaiting() +
			      " waiting. " );
			barrier.await();
		} catch (InterruptedException ex) {
				Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE, 
				      "child " + id + "failed ", ex);
		} catch (BrokenBarrierException ex) {
				Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE, 
				      "child " + id + "failed on await", ex);
		}
	   }//end method
	}//end inner class

	/** Method runs other calculator threads
	 * @throws InterruptedException
	 * @throws BrokenBarrierException
	 */
	public void runCalculatorThreads() throws InterruptedException, BrokenBarrierException{
		for ( int i = 0; i < numOfThreads; i++ ){
		      CalculatorThread calculator = new SingleLevelCyclicBarrier.CalculatorThread();
		      calculator.id = i; //name the thread
		      new Thread( calculator ).start();
		}
		//decrement the barrier and wait for others to complete
		System.out.println( "Barrier in parent at " + barrier.getNumberWaiting() );
		barrier.await();
	}//end method

	/** Method runs other threads
	 * @throws InterruptedException
	 * @throws BrokenBarrierException
	 */
	public void runCompressionThreads() throws InterruptedException, BrokenBarrierException{
		for ( int i = 0; i < numOfThreads; i++ ){
		      CompressionThread compression = new SingleLevelCyclicBarrier.CompressionThread();
		      compression.id = i; //name the thread
		      //TODO: change how is this file loaded. It should be from the classpath
              // just keep in mind that compression thread will then need a file object
		      compression.source = "/sample.pdf";
		      new Thread( compression ).start();
	        }//end for
		//decrement the barrier and wait for others to complete
		System.out.println( "Barrier in parent at " + barrier.getNumberWaiting() );
		barrier.await();
	}//end method

	/** Runs the demo
	 * @param args
	 * @throws Exception
	 */
	public static void main( String args[] ){
	       try{
		       long start_time = System.currentTimeMillis();
		       int numberOfThreads = Integer.valueOf( args[0] );
		       SingleLevelCyclicBarrier optimizerDemo = new SingleLevelCyclicBarrier( numberOfThreads );
		       optimizerDemo.runCompressionThreads();
               optimizerDemo.runCalculatorThreads();
		       long end_time = System.currentTimeMillis();
		       System.out.println( "Process of " + numberOfThreads +  
			     " threads completed in " + (end_time - start_time)/1000 + " sec."  );
		       //System.out.println( "Threaded operations completed in " +
		       //optimizerDemo.results + " barrier count at " + optimizerDemo.barrier.getNumberWaiting()  );
	       }catch( Exception e ){
		       Logger.getLogger(SingleLevelCyclicBarrier.class.getName()).log(Level.SEVERE, "Main failed ", e);
	       }
	}//end main
}//end class
