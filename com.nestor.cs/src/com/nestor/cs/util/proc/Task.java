package com.nestor.cs.util.proc;

import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.event.StepListener;

public interface Task extends Initiable {

    public static final int NONE = 0;

    // stany oznaczj�ce zaj�to��
    // (state & RUNNING != 0)
    public static final int RUNNING = 1;

    public static final int INITING = 2 | RUNNING;

    public static final int STOPPING = 4 | RUNNING;

    public static final int PAUSING = 8 | RUNNING;

    public static final int USER1_RUN = 256 | RUNNING;

    public static final int USER2_RUN = 512 | RUNNING;

    // stany oznaczj�ce bezczynno��
    public static final int STOPPED = 16;

    public static final int PAUSED = 32 | STOPPED;

    public static final int INITIALIZED = PAUSED;

    public static final int NOTINITED = 64 | STOPPED;

    public static final int ERROR = 128 | STOPPED;

    public void addStateListener(StateListener l);

    public void addStepListener(StepListener l);

    public int getCurrentStep();

    public int getProcessState();

    // zwraca prawce jesli pauza powiod�a sie
    public boolean pauseProcess();

    public void removeStateListener(StateListener l);

    public void removeStepListener(StepListener l);

    public void setCurrentStep(int step);

    public void setProcessState(int state);

    // zwraca prawce s� jeszcze kroki do wykonania
    public boolean stepProcess() throws ProcessingException;

    // zwraca prawce jesli stop powi�d� sie
    public boolean stopProcess();

}