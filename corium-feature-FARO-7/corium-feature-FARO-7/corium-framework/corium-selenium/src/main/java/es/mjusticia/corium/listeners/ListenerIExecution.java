package es.mjusticia.corium.listeners;

import org.testng.IExecutionListener;

public class ListenerIExecution extends ListenerMethods implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        seleniumMethods.loggerSlf4jOnlyMessageAsInfo("Execution will start");
    }

    @Override
    public void onExecutionFinish() {
        seleniumMethods.loggerSlf4jOnlyMessageAsInfo("Execution will finish");
    }
}
