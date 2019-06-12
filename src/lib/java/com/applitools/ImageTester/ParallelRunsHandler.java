package com.applitools.ImageTester;
import java.util.LinkedList;
import java.util.Queue;

public class ParallelRunsHandler {
	private final Queue<Runnable> queue;
	private final Object syncObject = new Object();
	private final static int MAX_THREADS = 500;

	public ParallelRunsHandler(Queue<Runnable> queue) {
		this.queue = queue;
	}

	public ParallelRunsHandler() {
		this(new LinkedList<Runnable>());
	}

	public void addRunnable(Runnable runnable){
		queue.add(runnable);
	}

	public void run(int numberOfThreads) throws InterruptedException {
		
		LinkedList<Thread> threads = new LinkedList<>();

		if ((numberOfThreads > 0) && (numberOfThreads < MAX_THREADS)) {
			
			int optimalNumberOfThreads = Math.min(numberOfThreads, queue.size());

			for (int i = 0; i < optimalNumberOfThreads; i++) {
				Thread thread = new Thread(new ThreadPoolRunnable());
				threads.add(thread);
				thread.start();
			}
		}
		else
		{
			throw new IllegalArgumentException("Number of threads must be between 1 to " + MAX_THREADS);
		}
		
		for (Thread thread : threads)
		{
			thread.join();
		}
	}

	private class ThreadPoolRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				Runnable runnable = null;
				synchronized (syncObject) {
					if (queue.isEmpty()) {
						return;
					}

					runnable = queue.poll();
				}

				try {
					runnable.run();
				} catch (Exception e) {

				}
			}
		}
	}


}
