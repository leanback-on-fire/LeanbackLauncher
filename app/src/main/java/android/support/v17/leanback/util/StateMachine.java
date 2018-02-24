package android.support.v17.leanback.util;

import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public final class StateMachine
{
  static boolean DEBUG = false;
  public static final int STATUS_INVOKED = 1;
  public static final int STATUS_ZERO = 0;
  static final String TAG = "StateMachine";
  final ArrayList<State> mFinishedStates = new ArrayList();
  final ArrayList<State> mStates = new ArrayList();
  final ArrayList<State> mUnfinishedStates = new ArrayList();
  
  public void addState(State paramState)
  {
    if (!this.mStates.contains(paramState)) {
      this.mStates.add(paramState);
    }
  }
  
  public void addTransition(State paramState1, State paramState2)
  {
    Transition localTransition = new Transition(paramState1, paramState2);
    paramState2.addIncoming(localTransition);
    paramState1.addOutgoing(localTransition);
  }
  
  public void addTransition(State paramState1, State paramState2, Condition paramCondition)
  {
    paramCondition = new Transition(paramState1, paramState2, paramCondition);
    paramState2.addIncoming(paramCondition);
    paramState1.addOutgoing(paramCondition);
  }
  
  public void addTransition(State paramState1, State paramState2, Event paramEvent)
  {
    paramEvent = new Transition(paramState1, paramState2, paramEvent);
    paramState2.addIncoming(paramEvent);
    paramState1.addOutgoing(paramEvent);
  }
  
  public void fireEvent(Event paramEvent)
  {
    int i = 0;
    if (i < this.mFinishedStates.size())
    {
      State localState = (State)this.mFinishedStates.get(i);
      if ((localState.mOutgoings == null) || ((!localState.mBranchStart) && (localState.mInvokedOutTransitions > 0))) {
        label45:
        break label62;
      }
      for (;;)
      {
        i += 1;
        break;
        Iterator localIterator = localState.mOutgoings.iterator();
        label62:
        if (localIterator.hasNext())
        {
          Transition localTransition = (Transition)localIterator.next();
          if ((localTransition.mState == 1) || (localTransition.mEvent != paramEvent)) {
            break label45;
          }
          if (DEBUG) {
            Log.d("StateMachine", "signal " + localTransition);
          }
          localTransition.mState = 1;
          localState.mInvokedOutTransitions += 1;
          if (localState.mBranchStart) {
            break label45;
          }
        }
      }
    }
    runUnfinishedStates();
  }
  
  public void reset()
  {
    if (DEBUG) {
      Log.d("StateMachine", "reset");
    }
    this.mUnfinishedStates.clear();
    this.mFinishedStates.clear();
    Iterator localIterator = this.mStates.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (State)localIterator.next();
      ((State)localObject).mStatus = 0;
      ((State)localObject).mInvokedOutTransitions = 0;
      if (((State)localObject).mOutgoings != null)
      {
        localObject = ((State)localObject).mOutgoings.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Transition)((Iterator)localObject).next()).mState = 0;
        }
      }
    }
  }
  
  void runUnfinishedStates()
  {
    int j;
    do
    {
      j = 0;
      int i = this.mUnfinishedStates.size() - 1;
      while (i >= 0)
      {
        State localState = (State)this.mUnfinishedStates.get(i);
        if (localState.runIfNeeded())
        {
          this.mUnfinishedStates.remove(i);
          this.mFinishedStates.add(localState);
          j = 1;
        }
        i -= 1;
      }
    } while (j != 0);
  }
  
  public void start()
  {
    if (DEBUG) {
      Log.d("StateMachine", "start");
    }
    this.mUnfinishedStates.addAll(this.mStates);
    runUnfinishedStates();
  }
  
  public static class Condition
  {
    final String mName;
    
    public Condition(String paramString)
    {
      this.mName = paramString;
    }
    
    public boolean canProceed()
    {
      return true;
    }
  }
  
  public static class Event
  {
    final String mName;
    
    public Event(String paramString)
    {
      this.mName = paramString;
    }
  }
  
  public static class State
  {
    final boolean mBranchEnd;
    final boolean mBranchStart;
    ArrayList<StateMachine.Transition> mIncomings;
    int mInvokedOutTransitions = 0;
    final String mName;
    ArrayList<StateMachine.Transition> mOutgoings;
    int mStatus = 0;
    
    public State(String paramString)
    {
      this(paramString, false, true);
    }
    
    public State(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mName = paramString;
      this.mBranchStart = paramBoolean1;
      this.mBranchEnd = paramBoolean2;
    }
    
    void addIncoming(StateMachine.Transition paramTransition)
    {
      if (this.mIncomings == null) {
        this.mIncomings = new ArrayList();
      }
      this.mIncomings.add(paramTransition);
    }
    
    void addOutgoing(StateMachine.Transition paramTransition)
    {
      if (this.mOutgoings == null) {
        this.mOutgoings = new ArrayList();
      }
      this.mOutgoings.add(paramTransition);
    }
    
    final boolean checkPreCondition()
    {
      if (this.mIncomings == null) {}
      do
      {
        while (!localIterator.hasNext())
        {
          return true;
          if (!this.mBranchEnd) {
            break;
          }
          localIterator = this.mIncomings.iterator();
        }
      } while (((StateMachine.Transition)localIterator.next()).mState == 1);
      return false;
      Iterator localIterator = this.mIncomings.iterator();
      while (localIterator.hasNext()) {
        if (((StateMachine.Transition)localIterator.next()).mState == 1) {
          return true;
        }
      }
      return false;
    }
    
    public final int getStatus()
    {
      return this.mStatus;
    }
    
    public void run() {}
    
    final boolean runIfNeeded()
    {
      if ((this.mStatus != 1) && (checkPreCondition()))
      {
        if (StateMachine.DEBUG) {
          Log.d("StateMachine", "execute " + this);
        }
        this.mStatus = 1;
        run();
        signalAutoTransitionsAfterRun();
        return true;
      }
      return false;
    }
    
    final void signalAutoTransitionsAfterRun()
    {
      if (this.mOutgoings != null)
      {
        Iterator localIterator = this.mOutgoings.iterator();
        do
        {
          StateMachine.Transition localTransition;
          do
          {
            if (!localIterator.hasNext()) {
              break;
            }
            localTransition = (StateMachine.Transition)localIterator.next();
          } while ((localTransition.mEvent != null) || ((localTransition.mCondition != null) && (!localTransition.mCondition.canProceed())));
          if (StateMachine.DEBUG) {
            Log.d("StateMachine", "signal " + localTransition);
          }
          this.mInvokedOutTransitions += 1;
          localTransition.mState = 1;
        } while (this.mBranchStart);
      }
    }
    
    public String toString()
    {
      return "[" + this.mName + " " + this.mStatus + "]";
    }
  }
  
  static class Transition
  {
    final StateMachine.Condition mCondition;
    final StateMachine.Event mEvent;
    final StateMachine.State mFromState;
    int mState = 0;
    final StateMachine.State mToState;
    
    Transition(StateMachine.State paramState1, StateMachine.State paramState2)
    {
      this.mFromState = paramState1;
      this.mToState = paramState2;
      this.mEvent = null;
      this.mCondition = null;
    }
    
    Transition(StateMachine.State paramState1, StateMachine.State paramState2, StateMachine.Condition paramCondition)
    {
      if (paramCondition == null) {
        throw new IllegalArgumentException();
      }
      this.mFromState = paramState1;
      this.mToState = paramState2;
      this.mEvent = null;
      this.mCondition = paramCondition;
    }
    
    Transition(StateMachine.State paramState1, StateMachine.State paramState2, StateMachine.Event paramEvent)
    {
      if (paramEvent == null) {
        throw new IllegalArgumentException();
      }
      this.mFromState = paramState1;
      this.mToState = paramState2;
      this.mEvent = paramEvent;
      this.mCondition = null;
    }
    
    public String toString()
    {
      String str;
      if (this.mEvent != null) {
        str = this.mEvent.mName;
      }
      for (;;)
      {
        return "[" + this.mFromState.mName + " -> " + this.mToState.mName + " <" + str + ">]";
        if (this.mCondition != null) {
          str = this.mCondition.mName;
        } else {
          str = "auto";
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/util/StateMachine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */