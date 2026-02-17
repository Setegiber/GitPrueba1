package es.mjusticia.sinac.core.business.exception;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2022 - 2023 Ministerio de Justicia
 * %%
 * Licencia con arreglo a la EUPL, Versión 1.2 o –en cuanto
 *  sean aprobadas por la Comisión Europea– versiones
 *  posteriores de la EUPL (la «Licencia»)
 *  Solo podrá usarse esta obra si se respeta la Licencia.
 *  Puede obtenerse una copia de la Licencia en:
 * 
 *  https://joinup.ec.europa.eu/software/page/eupl
 * 
 *  Salvo cuando lo exija la legislación aplicable o se acuerde
 *  por escrito, el programa distribuido con arreglo a la
 *  Licencia se distribuye «TAL CUAL»,
 *  SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas
 *  ni implícitas.
 *  Véase la Licencia en el idioma concreto que rige
 *  los permisos y limitaciones que establece la Licencia
 * #L%
 */

import java.io.Serializable;

/**
 * Datos de excepción propia de Sinac-Core
 * 
 * @author NTTData
 */
public class SinacExceptionDto implements Serializable {

	/**
	 * Id de serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Tipo de excepcion
	 */
	private SinacExceptionType type = SinacExceptionType.BUSINESS;
	
	/**
	 * Mensaje a mostrar por log
	 */
	private String logMessage;
	
	/**
	 * Parametros del mensaje a mostrar por log
	 */
	private Object[] logMessageParams;
	
	/**
	 * Mensaje a mostrar por pantalla
	 */
	private String userMessage;
	
	/**
	 * Parametros del mensaje de log a mostrar por pantalla
	 */
	private Object[] userMessageParams;
	
	/**
	 * Vista de destino (por defecto error)
	 */
	private String destPage = "error";
	
	/**
	 * Indica si debemos hacer redireccion de URL
	 */
	private boolean redirect = false;

	/**
	 * Nivel de error
	 */
  private String level = "error";

	/**
	 * Recupera el tipo de la excepcion
	 * @return
	 */
	public SinacExceptionType getType() {
		return type;
	}

	/**
	 * Asigna el tipo de la excepcion
	 * @param type
	 */
	public void setType(SinacExceptionType type) {
		this.type = type;
	}

	/**
	 * Recupera el mensaje a mostrar por pantalla
	 * @return
	 */
	String getUserMessage() {
		return userMessage;
	}

	/**
	 * Asigna el mensaje a mostrar por pantalla
	 * @param message
	 */
	void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 * Recupera los parametros del mensaje a mostrar por pantalla
	 * @return
	 */
	Object[] getUserMessageParams() {
		return userMessageParams;
	}

	/**
	 * Asigna los parametros del mensaje a mostrar por pantalla
	 * @param messageParams
	 */
	void setUserMessageParams(Object[] userMessageParams) {
		this.userMessageParams = userMessageParams;
	}

	/**
	 * Recupera el mensaje de log a mostrar por pantalla
	 * @return
	 */
	String getLogMessage() {
		return logMessage;
	}

	/**
	 * Asigna el mensaje de log a mostrar por pantalla
	 * @param logMessage
	 */
	void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	/**
	 * Recupera los parametros del mensaje a mostrar en log
	 * @return
	 */
	Object[] getLogMessageParams() {
		return logMessageParams;
	}

	/**
	 * Asigna los parametros del mensaje a mostrar en log
	 * @param logMessageParams
	 */
	void setLogMessageParams(Object[] logMessageParams) {
		this.logMessageParams = logMessageParams;
	}

	/**
	 * Recupera la pagina de destino del error
	 * @return
	 */
	public String getDestPage() {
		return destPage;
	}

	/**
	 * Asigna la pagina de destino del error
	 * @return
	 */
	public void setDestPage(String destPage) {
		this.destPage = destPage;
	}

	/**
	 * Indica si debemos hacer redireccion
	 * @return
	 */
	public boolean isRedirect() {
		return redirect;
	}

	/**
	 * Asigna si debemos hacer redireccion
	 * @param redirect
	 */
	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}
	
  /**
  * Recupera el nivel de error
  * @param level
  * @return
  */
  public String getLevel() {
    return level;
  }
	
	 /**
   * Asigna el nivel de error
   * @param redirect
   */
  public void setLevel(String level) {
    this.level = level;
  }

}
