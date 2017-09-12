//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// IterativeProcessController.java (nes.proc)
// Utworzono 2005-07-29 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.proc;

public abstract class Controller {

    public static boolean canInit(Task ip) {
        return ((ip.getProcessState() & Task.RUNNING) == 0);
    }

    public static boolean canPause(Task ip) {
        return (isInState(ip, new int[] { Task.RUNNING }));
    }

    public static boolean canRun(Task ip) {
        return (isInState(ip, new int[] { Task.PAUSED }));
    }

    public static boolean canStep(Task ip) {
        return (isInState(ip, new int[] { Task.PAUSED }));
    }

    public static boolean canStop(Task ip) {
        return (isInState(ip, new int[] { Task.RUNNING,
                Task.ERROR, Task.PAUSED }));
    }

    public static void init(Initiable i) throws ProcessingException {
        if (i instanceof Task) {
            Task ip = (Task) i;
            if (!canInit(ip))
                return;
            ip.setProcessState(Task.INITING);
            try {
                ip.initProcess();
            } catch (ProcessingException e) {
                ip.setProcessState(Task.ERROR);
                throw e;
            } catch (RuntimeException e) {
                ip.setProcessState(Task.ERROR);
                throw new ProcessingException(e);
            }
            ip.setCurrentStep(0);
            if (ip.getProcessState() == Task.INITING)
                ip.setProcessState(Task.INITIALIZED);
        } else
            i.initProcess();
    }

    private static boolean isInState(Task ip, int[] states) {
        for (int i = 0; i < states.length; i++) {
            if (ip.getProcessState() == states[i])
                return true;
        }
        return false;
    }

    public static void pause(Task ip) {
        if (!canPause(ip))
            return;
        ip.pauseProcess();
    }

    public static void run(Task ip) throws ProcessingException {
        if (!canRun(ip))
            return;
        ip.setProcessState(Task.RUNNING);
        boolean stepped;
        do {
            try {
                stepped = ip.stepProcess();
                ip.setCurrentStep(ip.getCurrentStep() + 1);
            } catch (ProcessingException e) {
                ip.setProcessState(Task.ERROR);
                throw e;
            } catch (RuntimeException e) {
                ip.setProcessState(Task.ERROR);
                throw new ProcessingException(e);
            }
        } while (stepped && (ip.getProcessState() == Task.RUNNING));
        if (ip.getProcessState() == Task.RUNNING) {
            ip.setProcessState(Task.STOPPED);
        }
    }

    public static boolean step(Task ip) throws ProcessingException {
        if (!canStep(ip))
            return false;
        ip.setProcessState(Task.RUNNING);
        boolean stepped;
        try {
            stepped = ip.stepProcess();
            ip.setCurrentStep(ip.getCurrentStep() + 1);
        } catch (ProcessingException e) {
            ip.setProcessState(Task.ERROR);
            throw e;
        } catch (RuntimeException e) {
            ip.setProcessState(Task.ERROR);
            throw new ProcessingException(e);
        }
        if (ip.getProcessState() == Task.RUNNING) {
            ip.setProcessState(Task.STOPPED);
        }
        return stepped;
    }

    public static void stop(Task ip) {
        if (!canStop(ip))
            return;
        ip.stopProcess();
    }

}