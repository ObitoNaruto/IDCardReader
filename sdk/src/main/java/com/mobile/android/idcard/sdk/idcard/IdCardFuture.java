package com.mobile.android.idcard.sdk.idcard;

import android.os.OperationCanceledException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public interface IdCardFuture<V> {

    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();

    boolean isDone();

    V getResult() throws OperationCanceledException, IOException, IdCardReaderException;

    V getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException, IdCardReaderException;
}
