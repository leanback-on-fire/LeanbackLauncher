package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class Task<TResult>
{
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull Activity paramActivity, @NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
  }
  
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
  }
  
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull Executor paramExecutor, @NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
  }
  
  @NonNull
  public abstract Task<TResult> addOnFailureListener(@NonNull Activity paramActivity, @NonNull OnFailureListener paramOnFailureListener);
  
  @NonNull
  public abstract Task<TResult> addOnFailureListener(@NonNull OnFailureListener paramOnFailureListener);
  
  @NonNull
  public abstract Task<TResult> addOnFailureListener(@NonNull Executor paramExecutor, @NonNull OnFailureListener paramOnFailureListener);
  
  @NonNull
  public abstract Task<TResult> addOnSuccessListener(@NonNull Activity paramActivity, @NonNull OnSuccessListener<? super TResult> paramOnSuccessListener);
  
  @NonNull
  public abstract Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> paramOnSuccessListener);
  
  @NonNull
  public abstract Task<TResult> addOnSuccessListener(@NonNull Executor paramExecutor, @NonNull OnSuccessListener<? super TResult> paramOnSuccessListener);
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> paramContinuation)
  {
    throw new UnsupportedOperationException("continueWith is not implemented");
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, TContinuationResult> paramContinuation)
  {
    throw new UnsupportedOperationException("continueWith is not implemented");
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    throw new UnsupportedOperationException("continueWithTask is not implemented");
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    throw new UnsupportedOperationException("continueWithTask is not implemented");
  }
  
  @Nullable
  public abstract Exception getException();
  
  public abstract TResult getResult();
  
  public abstract <X extends Throwable> TResult getResult(@NonNull Class<X> paramClass)
    throws Throwable;
  
  public abstract boolean isComplete();
  
  public abstract boolean isSuccessful();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/tasks/Task.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */