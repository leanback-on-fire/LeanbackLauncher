package android.support.v17.leanback.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v17.leanback.util.StateMachine;
import android.support.v17.leanback.util.StateMachine.Condition;
import android.support.v17.leanback.util.StateMachine.Event;
import android.support.v17.leanback.util.StateMachine.State;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class BaseSupportFragment
  extends BrandedSupportFragment
{
  final StateMachine.Condition COND_TRANSITION_NOT_SUPPORTED = new StateMachine.Condition("EntranceTransitionNotSupport")
  {
    public boolean canProceed()
    {
      return !TransitionHelper.systemSupportsEntranceTransitions();
    }
  };
  final StateMachine.Event EVT_ENTRANCE_END = new StateMachine.Event("onEntranceTransitionEnd");
  final StateMachine.Event EVT_ON_CREATE = new StateMachine.Event("onCreate");
  final StateMachine.Event EVT_ON_CREATEVIEW = new StateMachine.Event("onCreateView");
  final StateMachine.Event EVT_PREPARE_ENTRANCE = new StateMachine.Event("prepareEntranceTransition");
  final StateMachine.Event EVT_START_ENTRANCE = new StateMachine.Event("startEntranceTransition");
  final StateMachine.State STATE_ENTRANCE_COMPLETE = new StateMachine.State("ENTRANCE_COMPLETE", true, false);
  final StateMachine.State STATE_ENTRANCE_INIT = new StateMachine.State("ENTRANCE_INIT");
  final StateMachine.State STATE_ENTRANCE_ON_ENDED = new StateMachine.State("ENTRANCE_ON_ENDED")
  {
    public void run()
    {
      BaseSupportFragment.this.onEntranceTransitionEnd();
    }
  };
  final StateMachine.State STATE_ENTRANCE_ON_PREPARED = new StateMachine.State("ENTRANCE_ON_PREPARED", true, false)
  {
    public void run()
    {
      BaseSupportFragment.this.mProgressBarManager.show();
    }
  };
  final StateMachine.State STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW = new StateMachine.State("ENTRANCE_ON_PREPARED_ON_CREATEVIEW")
  {
    public void run()
    {
      BaseSupportFragment.this.onEntranceTransitionPrepare();
    }
  };
  final StateMachine.State STATE_ENTRANCE_PERFORM = new StateMachine.State("STATE_ENTRANCE_PERFORM")
  {
    public void run()
    {
      BaseSupportFragment.this.mProgressBarManager.hide();
      BaseSupportFragment.this.onExecuteEntranceTransition();
    }
  };
  final StateMachine.State STATE_START = new StateMachine.State("START", true, false);
  Object mEntranceTransition;
  final ProgressBarManager mProgressBarManager = new ProgressBarManager();
  final StateMachine mStateMachine = new StateMachine();
  
  protected Object createEntranceTransition()
  {
    return null;
  }
  
  void createStateMachineStates()
  {
    this.mStateMachine.addState(this.STATE_START);
    this.mStateMachine.addState(this.STATE_ENTRANCE_INIT);
    this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED);
    this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW);
    this.mStateMachine.addState(this.STATE_ENTRANCE_PERFORM);
    this.mStateMachine.addState(this.STATE_ENTRANCE_ON_ENDED);
    this.mStateMachine.addState(this.STATE_ENTRANCE_COMPLETE);
  }
  
  void createStateMachineTransitions()
  {
    this.mStateMachine.addTransition(this.STATE_START, this.STATE_ENTRANCE_INIT, this.EVT_ON_CREATE);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.COND_TRANSITION_NOT_SUPPORTED);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.EVT_ON_CREATEVIEW);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_ON_PREPARED, this.EVT_PREPARE_ENTRANCE);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.EVT_ON_CREATEVIEW);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_PERFORM, this.EVT_START_ENTRANCE);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.STATE_ENTRANCE_PERFORM);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_PERFORM, this.STATE_ENTRANCE_ON_ENDED, this.EVT_ENTRANCE_END);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_ENDED, this.STATE_ENTRANCE_COMPLETE);
  }
  
  public final ProgressBarManager getProgressBarManager()
  {
    return this.mProgressBarManager;
  }
  
  void internalCreateEntranceTransition()
  {
    this.mEntranceTransition = createEntranceTransition();
    if (this.mEntranceTransition == null) {
      return;
    }
    TransitionHelper.addTransitionListener(this.mEntranceTransition, new TransitionListener()
    {
      public void onTransitionEnd(Object paramAnonymousObject)
      {
        BaseSupportFragment.this.mEntranceTransition = null;
        BaseSupportFragment.this.mStateMachine.fireEvent(BaseSupportFragment.this.EVT_ENTRANCE_END);
      }
    });
  }
  
  public void onCreate(Bundle paramBundle)
  {
    createStateMachineStates();
    createStateMachineTransitions();
    this.mStateMachine.start();
    super.onCreate(paramBundle);
    this.mStateMachine.fireEvent(this.EVT_ON_CREATE);
  }
  
  protected void onEntranceTransitionEnd() {}
  
  protected void onEntranceTransitionPrepare() {}
  
  protected void onEntranceTransitionStart() {}
  
  void onExecuteEntranceTransition()
  {
    final View localView = getView();
    localView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        localView.getViewTreeObserver().removeOnPreDrawListener(this);
        if ((BaseSupportFragment.this.getContext() == null) || (BaseSupportFragment.this.getView() == null)) {
          return true;
        }
        BaseSupportFragment.this.internalCreateEntranceTransition();
        BaseSupportFragment.this.onEntranceTransitionStart();
        if (BaseSupportFragment.this.mEntranceTransition != null) {
          BaseSupportFragment.this.runEntranceTransition(BaseSupportFragment.this.mEntranceTransition);
        }
        for (;;)
        {
          return false;
          BaseSupportFragment.this.mStateMachine.fireEvent(BaseSupportFragment.this.EVT_ENTRANCE_END);
        }
      }
    });
    localView.invalidate();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mStateMachine.fireEvent(this.EVT_ON_CREATEVIEW);
  }
  
  public void prepareEntranceTransition()
  {
    this.mStateMachine.fireEvent(this.EVT_PREPARE_ENTRANCE);
  }
  
  protected void runEntranceTransition(Object paramObject) {}
  
  public void startEntranceTransition()
  {
    this.mStateMachine.fireEvent(this.EVT_START_ENTRANCE);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BaseSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */