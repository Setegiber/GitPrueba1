package es.mjusticia.corium.listeners;

import org.testng.IExecutionListener;

public class ListenerIExecution extends ListenerMethods implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        SELENIUM_METHODS.loggerSlf4jOnlyMessageAsInfo("Execution will start");
    }

    @Override
    public void onExecutionFinish() {
    	SELENIUM_METHODS.loggerSlf4jOnlyMessageAsInfo("Execution will finish");
    }
}
