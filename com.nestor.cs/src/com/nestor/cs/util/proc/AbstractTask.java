package com.nestor.cs.util.proc;

import com.nestor.cs.util.event.EventDispatcher;
import com.nestor.cs.util.event.SimpleEventDispatcher;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.event.StepEvent;
import com.nestor.cs.util.event.StepListener;

public abstract class AbstractTask implements Task {

    public static String getProcStateName(int state) {
        switch (state) {
        case RUNNING:
            return "RUNNING"; //$NON-NLS-1$
        case INITING:
            return "INITING"; //$NON-NLS-1$
        case STOPPING:
            return "STOPPING"; //$NON-NLS-1$
        case PAUSING:
            return "PAUSING"; //$NON-NLS-1$
        case USER1_RUN:
            return "USER1_RUN"; //$NON-NLS-1$
        case USER2_RUN:
            return "USER2_RUN"; //$NON-NLS-1$
        case STOPPED:
            return "STOPPED"; //$NON-NLS-1$
        case PAUSED:
            return "PAUSED"; //$NON-NLS-1$
        case NOTINITED:
            return "NOTINITED"; //$NON-NLS-1$
        case ERROR:
            return "ERROR"; //$NON-NLS-1$
        }
        return "UNKNOWN"; //$NON-NLS-1$
    }

    private int currentState = Task.NOTINITED;

    private int currentStep = 0;

    private EventDispatcher eventDispatcher = null;

    protected Object lock = new Object();

    public void addStateListener(StateListener l) {
        getEventDispatcher().addListener(StateListener.class, l);
    }

    public void addStepListener(StepListener l) {
        getEventDispatcher().addListener(StepListener.class, l);
    }

    public int getCurrentStep() {
        return this.currentStep;
    }

    private EventDispatcher getEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new SimpleEventDispatcher();
        }
        return this.eventDispatcher;
    }

    public int getProcessState() {
        return this.currentState;
    }

    public boolean pauseProcess() {
        int oldState = getProcessState();
        if ((oldState & Task.RUNNING) == 0) {
            setProcessState(PAUSED);
            return true;
        }
        setProcessState(PAUSING);
        if (waitForStepCompletion()) {
            setProcessState(PAUSED);
            return true;
        } else {
            setProcessState(oldState);
            return false;
        }
    }

    public void removeStateListener(StateListener l) {
        getEventDispatcher().removeListener(StateListener.class, l);
    }

    public void removeStepListener(StepListener l) {
        getEventDispatcher().removeListener(StepListener.class, l);
    }

    public void setCurrentStep(int step) {
        if (this.currentStep != step) {
            this.currentStep = step;
            getEventDispatcher().notifyListeners(StepListener.class,
                    new StepEvent(this, this.currentStep));
        }
    }

    public void setProcessState(int state) {
        if (state != this.currentState) {
            int oldState = this.currentState;
            this.currentState = state;
            getEventDispatcher().notifyListeners(StateListener.class,
                    new StateEvent(this, state, oldState));
        }
    }

    protected final void stepComplete() {
        synchronized (this.lock) {
            this.lock.notify();
        }
    }

    public boolean stopProcess() {
        int oldState = getProcessState();
        if ((oldState & Task.RUNNING) == 0) {
            setProcessState(STOPPED);
            return true;
        }
        setProcessState(STOPPING);
        if (waitForStepCompletion()) {
            setProcessState(STOPPED);
            return true;
        } else {
            setProcessState(oldState);
            return false;
        }
    }

    protected boolean waitForStepCompletion() {
        synchronized (this.lock) {
            try {
                this.lock.wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }
}