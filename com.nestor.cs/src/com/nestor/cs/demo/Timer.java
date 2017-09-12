//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// Timer.java (com.nestor.cs.demo)
// Utworzono 2005-09-10 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.demo;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nestor.cs.tasks.impl.visual.AbstractDiagramComponent;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.event.UpdateEvent;
import com.nestor.cs.util.event.UpdateListener;
import com.nestor.cs.util.proc.ProcessingException;
import com.nestor.cs.util.proc.Task;
import com.nestor.cs.util.util.param.Argument;
import com.nestor.cs.util.util.param.Parameter;

public class Timer extends AbstractDiagramComponent implements UpdateListener,
        StateListener {

    public final Parameter argEndingTask = new Parameter();

    public final Argument argNote = new Argument("");

    public final Parameter argStartingTask = new Parameter();

    private long start = 0, end = 0;

    public Timer() {
        super();

        this.argStartingTask.setName("Zadanie rozpoczynające");
        this.argStartingTask.setValueType(Task.class);
        this.argStartingTask.addUpdateListener(this);
        getConfigurationArguments().addArgument(this.argStartingTask);

        this.argEndingTask.setName("Zadanie rozpoczynające");
        this.argEndingTask.setRequired(false);
        this.argEndingTask.setValueType(Task.class);
        this.argEndingTask.addUpdateListener(this);
        getConfigurationArguments().addArgument(this.argEndingTask);

        this.argNote.setName("Podpis");
        getConfigurationArguments().addArgument(this.argNote);
    }

    public Task getArgEndingTask() {
        Task t = (Task) this.argStartingTask.getValue();
        if (t == null)
            return getArgStartingTask();
        return t;
    }

    public Task getArgStartingTask() {
        return (Task) this.argStartingTask.getValue();
    }

    public String getName() {
        String s = this.argNote.getValue().toString();
        if (s == null)
            s = "";
        if (s.equals("")) {
            Task t1 = getArgStartingTask();
            Task t2 = getArgEndingTask();
            if (t1 != null) {
                s = t1.getClass().getSimpleName();
            }
            if (t2 != null) {
                s += " - " + t2.getClass().getSimpleName();
            }
        }
        return s;
    }

    public void initProcess() throws ProcessingException {

        long time = this.end - this.start;
        long sec = time / 1000;
        long dsec = (time % 1000) / 100;
        String s = this.argNote.getValue().toString();
        if (s == null)
            s = "";
        if (s.equals("")) {
            s = getArgStartingTask().getClass().getSimpleName() + " - "
                    + getArgEndingTask().getClass().getSimpleName();
        }
        JLabel jl1 = new JLabel("Zakończono pomiar czasu dla: " + s);
        JLabel jl2 = new JLabel("Czas wykonania [s]: " + sec + "." + dsec);
        JPanel jp = new JPanel(new GridLayout(2, 1));
        jp.add(jl1);
        jp.add(jl2);
        setPreparedComponent(jp);
    }

    public void stateChanged(StateEvent e) {
        if ((e.getNewState() == Task.RUNNING)
                && (e.getIterativeProcess() == getArgStartingTask())) {
            this.start = System.currentTimeMillis();
        }
        if ((e.getOldState() == Task.RUNNING)
                && (e.getIterativeProcess() == getArgEndingTask())) {
            this.end = System.currentTimeMillis();
        }
    }

    public void update(UpdateEvent e) {
        Task o = (Task) e.getOldValue();
        Task n = (Task) e.getNewValue();

        if (o != null) {
            o.removeStateListener(this);
        }
        if (n != null) {
            n.addStateListener(this);
        }
    }
}