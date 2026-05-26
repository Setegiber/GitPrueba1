package es.mjusticia.corium.listeners;


import es.mjusticia.corium.SeleniumMethods;
import es.mjusticia.corium.utils.FrameworkAI;

public class ListenerMethods {

    public static final SeleniumMethods SELENIUM_METHODS = new SeleniumMethods();
    private static FrameworkAI frameworkAI = new FrameworkAI();
	public static FrameworkAI getFrameworkAI() {
		return frameworkAI;
	}
	
}
