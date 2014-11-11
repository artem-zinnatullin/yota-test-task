package com.artemzin.android.yotatask.model;

import android.os.Handler;
import android.os.Looper;

import com.artemzin.android.yotatask.Loggi;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Alternative variant of AsyncTask
 *
 * @param <Result> type of the processing result
 */
public abstract class Task<Result> {

    /**
     * Listener for the tasks
     * <p/>
     * <p>NOTICE: if listener's impl throw exception in one of its methods, it won't fail the task execution!</p>
     *
     * @param <Result> type of the task work result
     */
    public abstract static class Listener<Result> {
        /**
         * Called in UI thread right before executing the task
         */
        public void onPreExecute() { }

        /**
         * Called in UI thread if exception had occurred while executing the task
         *
         * @param e exception
         */
        public abstract void onExceptionOccurred(Throwable e);

        /**
         * Called in background thread after the task finished data processing
         * <p/>
         * <p>NOTICE: if your impl will throw exception in this method, onDataProcessed() won't be called</p>
         *
         * @param result of the processing
         */
        public void onDataProcessedBackground(Result result) { }

        /**
         * Called in UI thread when the task finished data processing
         *
         * @param result of the processing
         */
        public abstract void onDataProcessed(Result result);
    }

    public static final int STATE_NOT_LAUNCHED = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_CANCELLED = 3;

    private static final Handler UI_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    private AtomicInteger mState = new AtomicInteger(STATE_NOT_LAUNCHED);
    private final Listener<Result> mListener;

    /**
     * Creates new task without listener
     */
    public Task() {
        mListener = null;
    }

    /**
     * Creates new task
     *
     * @param listener for callbacks, can be null, if you want to just run some code
     */
    public Task(Listener<Result> listener) {
        mListener = listener;
    }

    private String getLogTag() {
        return ((Object) this).getClass().getName();
    }

    /**
     * Gets current state of the task
     *
     * @return one of #STATE_NOT_LAUNCHED..#STATE_FINISHED
     */
    public int getState() {
        return mState.get();
    }

    /**
     * Runs this task with notifying listener if required
     *
     * @param executor to run the task on it
     */
    public final void run(final Executor executor) {
        if (mState.get() == STATE_CANCELLED) {
            return;
        }

        if (mState.get() != STATE_NOT_LAUNCHED) {
            throw new IllegalStateException("Task was already launched!");
        }

        if (executor == null) {
            throw new IllegalArgumentException("Executor can not be null");
        }

        if (isCancelled()) {
            return;
        }

        mState.set(STATE_RUNNING);

        if (isCancelled()) {
            return;
        }

        notifyListenerAboutOnPreExecute();

        if (isCancelled()) {
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (isCancelled()) {
                    return;
                }

                final Result result;

                try {
                    result = doWork();
                } catch (Throwable e) {
                    if (isCancelled()) {
                        return;
                    }

                    notifyListenerAboutOnExceptionOccurred(e);
                    return;
                }

                mState.set(STATE_FINISHED);

                if (isCancelled()) {
                    return;
                }


                if (!notifyListenerAboutDataProcessedBackground(result)) {
                    return;
                }

                UI_THREAD_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isCancelled()) {
                            return;
                        }

                        notifyListenerAboutDataProcessed(result);
                    }
                });
            }
        });
    }

    /**
     * Cancels execution of the task
     */
    public final void cancel() {
        mState.set(STATE_CANCELLED);
    }

    /**
     * Your implementation can check result of this method in a loop for example in the doWork() and stop execution if result if true
     *
     * @return true if task was cancelled, false otherwise
     */
    protected final boolean isCancelled() {
        return mState.get() == STATE_CANCELLED;
    }

    /**
     * Override this method and do some work, bitch! (Breaking Bad)
     *
     * @return typed result of the processing, can be null if task's listener is ready for this
     * @throws java.lang.Throwable that can occur in execution process
     */
    protected abstract Result doWork() throws Throwable;

    private void notifyListenerAboutOnPreExecute() {
        if (mListener != null) {
            UI_THREAD_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mListener.onPreExecute();
                    } catch (Exception e) {
                        Loggi.e(getLogTag(), "notifyListenerAboutOnPreExecute", e);
                    }
                }
            });
        }
    }

    private void notifyListenerAboutOnExceptionOccurred(final Throwable e) {
        Loggi.e(getLogTag(), "onExceptionOccurred", e);

        if (mListener != null) {
            UI_THREAD_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mListener.onExceptionOccurred(e);
                    } catch (Exception e) {
                        Loggi.e(getLogTag(), "notifyListenerAboutOnExceptionOccurred", e);
                    }
                }
            });
        }
    }

    private boolean notifyListenerAboutDataProcessedBackground(Result result) {
        if (mListener != null) {
            try {
                mListener.onDataProcessedBackground(result);
                return true;
            } catch (Exception e) {
                Loggi.e(getLogTag(), "notifyListenerAboutDataProcessedBackground", e);
                return false;
            }
        }

        return false;
    }

    private void notifyListenerAboutDataProcessed(final Result result) {
        if (mListener != null) {
            UI_THREAD_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mListener.onDataProcessed(result);
                    } catch (Exception e) {
                        Loggi.e(getLogTag(), "notifyListenerAboutDataProcessed", e);
                    }
                }
            });
        }
    }
}
