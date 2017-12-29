package com.mobile.android.idcard.sdk.idcard;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.OperationCanceledException;
import android.util.Log;

import com.mobile.android.idcard.sdk.handler.IHandler;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class IdCardManager {
    private static final String TAG = IdCardManager.class.getSimpleName();
    private Context mContext;
    private final Handler mMainHandler;
    private IIdCardService mService;
    private static volatile IdCardManager sInstance = null;

    public static IdCardManager getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (IdCardManager.class) {
                if (sInstance == null) {
                    sInstance = new IdCardManager(context);
                }
            }
        }
        return sInstance;
    }

    private IdCardManager(Context context) {
        mContext = context;
        mService = new IdCardServiceImpl();
        mMainHandler = new Handler(mContext.getMainLooper());
    }

    public IdCardFuture<Bundle> readIdCard(final Bundle bundle, final Activity activity, IIdCardManagerCallback<Bundle> callback, Handler handler, final IHandler deviceHandler) {

        return new IdCardTask(activity, handler, callback) {
            @Override
            public void doWork() {
                mService.readIdCard(mResponse, bundle, deviceHandler);
            }
        }.start();
    }

    private void ensureNotOnMainThread() {
        final Looper looper = Looper.myLooper();
        if (looper != null && looper == mContext.getMainLooper()) {
            final IllegalStateException exception = new IllegalStateException(
                    "calling this from your main thread can lead to deadlock");
            Log.e(TAG, "calling this from your main thread can lead to deadlock and/or ANRs",
                    exception);
            if (mContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.FROYO) {
                throw exception;
            }
        }
    }

    private void postToHandler(Handler handler, final IIdCardManagerCallback<Bundle> callback, final IdCardFuture<Bundle> future) {
        handler = handler == null ? mMainHandler : handler;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.run(future);
            }
        });
    }

    private abstract class IdCardTask extends FutureTask<Bundle> implements IdCardFuture<Bundle> {

        final IIdCardManagerResponse mResponse;
        final Handler mHandler;
        final IIdCardManagerCallback<Bundle> mCallback;
        final Activity mActivity;

        public IdCardTask(Activity activity, Handler handler, IIdCardManagerCallback<Bundle> callback) {
            super(new Callable<Bundle>() {
                @Override
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            mHandler = handler;
            mCallback = callback;
            mActivity = activity;
            mResponse = new Response();
        }

        public final IdCardFuture<Bundle> start() {
            doWork();
            return this;
        }

        @Override
        protected void set(Bundle bundle) {
            if (bundle == null) {
                Log.e(TAG, "the bundle must not be null", new Exception());
            }
            super.set(bundle);
        }

        public abstract void doWork();

        private Bundle internalGetResult(Long timeout, TimeUnit unit)
                throws OperationCanceledException, IOException, IdCardReaderException {
            if (!isDone()) {
                ensureNotOnMainThread();
            }
            try {
                if (timeout == null) {
                    return get();
                } else {
                    return get(timeout, unit);
                }
            } catch (CancellationException e) {
                throw new OperationCanceledException();
            } catch (TimeoutException e) {
                // fall through and cancel
            } catch (InterruptedException e) {
                // fall through and cancel
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                } else if (cause instanceof IdCardReaderException) {
                    throw (IdCardReaderException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException(cause);
                }
            } finally {
                cancel(true /* interrupt if running */);
            }
            throw new OperationCanceledException();
        }

        @Override
        public Bundle getResult()
                throws OperationCanceledException, IOException, IdCardReaderException {
            return internalGetResult(null, null);
        }

        @Override
        public Bundle getResult(long timeout, TimeUnit unit)
                throws OperationCanceledException, IOException, IdCardReaderException {
            return internalGetResult(timeout, unit);
        }

        @Override
        protected void done() {
            if (mCallback != null) {
                postToHandler(mHandler, mCallback, this);
            }
        }

        private class Response implements IIdCardManagerResponse {
            @Override
            public void onResult(Bundle bundle) {
                set(bundle);
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                setException(new IdCardReaderException("errorCode:" + errorCode + ", msg:" + errorMsg));
            }
        }
    }
}
