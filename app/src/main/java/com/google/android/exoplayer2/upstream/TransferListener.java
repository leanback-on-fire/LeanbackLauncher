package com.google.android.exoplayer2.upstream;

public abstract interface TransferListener<S>
{
  public abstract void onBytesTransferred(S paramS, int paramInt);
  
  public abstract void onTransferEnd(S paramS);
  
  public abstract void onTransferStart(S paramS, DataSpec paramDataSpec);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/TransferListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */