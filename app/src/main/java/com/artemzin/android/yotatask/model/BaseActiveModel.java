package com.artemzin.android.yotatask.model;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;

import com.artemzin.android.yotatask.Loggi;
import com.artemzin.android.yotatask.YotaTaskApp;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public abstract class BaseActiveModel {

    private static final Executor EXECUTOR_SERVICE = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger mThreadsCounter = new AtomicInteger(0);
        private final int mThreadPriority = 3;

        @Override
        public Thread newThread(@NonNull Runnable r) {
            int threadId = mThreadsCounter.getAndIncrement();
            Thread thread = new Thread(r, "ActiveModelThread#" + threadId);
            thread.setPriority(mThreadPriority);
            return thread;
        }
    });

    private static final Handler RETRY_TASK_EXECUTION_HANDLER;
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    static {
        HandlerThread handlerThread = new HandlerThread("RETRY_TASK_EXECUTION_HANDLER_THREAD", Process.THREAD_PRIORITY_LOWEST);
        handlerThread.start();
        RETRY_TASK_EXECUTION_HANDLER = new Handler(handlerThread.getLooper());
    }

    private final Context mContext;

    private Queue<WeakReference<Task>> mActiveModelTasks = new ConcurrentLinkedQueue<WeakReference<Task>>();

    public BaseActiveModel(@NonNull Context context) {
        mContext = context.getApplicationContext();

        if (shouldInject()) {
            YotaTaskApp.get(context).inject(this);
        }
    }

    /**
     * Provides easy ability for dependency injection in constructor
     * @return true if you want to use dependency injection
     */
    protected boolean shouldInject() {
        return false;
    }

    /**
     * Gets context for this active model
     *
     * @return application context instance
     */
    @NonNull public Context getContext() {
        return mContext;
    }

    /**
     * Gets default executor for background tasks
     *
     * @return executor for background tasks
     */
    @SuppressWarnings("UnusedDeclaration") protected static Executor getExecutor() {
        return EXECUTOR_SERVICE;
    }

    protected void executeTask(@NonNull final Task task) {
        mActiveModelTasks.add(new WeakReference<Task>(task));

        try {
            task.run(EXECUTOR_SERVICE);
        } catch (RejectedExecutionException e) {
            Loggi.w(((Object) this).getClass().getSimpleName(), "executeTask", e);
            RETRY_TASK_EXECUTION_HANDLER.postDelayed(new Runnable() {
                @Override public void run() {
                    executeTask(task);
                }
            }, (RANDOM.nextInt(25) + 1) * 100); // delay from 100ms to 2400 ms
        }
    }

    /**
     * Cancels all running background tasks in this active model
     */
    public void cancelAllRunningTasks() {
        int count = 0;

        Iterator<WeakReference<Task>> tasksWeakReferences = mActiveModelTasks.iterator();

        while (tasksWeakReferences.hasNext()) {
            WeakReference<Task> taskWeakReference = tasksWeakReferences.next();
            Task task = taskWeakReference.get();

            if (task != null) {
                task.cancel();
                count++;
            }

            tasksWeakReferences.remove();
        }

        Loggi.i(getClass().getName(), "cancelAllRunningTasks, cancelled " + count + " tasks");
    }

    /**
     * Releases resources of this Active Model, cancels running tasks
     */
    public void release() {
        cancelAllRunningTasks();
    }
}
