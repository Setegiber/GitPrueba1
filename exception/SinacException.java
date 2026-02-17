package es.mjusticia.sinac.core.business.exception;

import java.text.MessageFormat;

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
/**
 * Excepciones controladas por SINAC-CORE
 * 
 * @author NTTData
 */
public class SinacException extends RuntimeException {

  /**
   * Id de serializacion
   */
  private static final long serialVersionUID = 1L;

  /**
   * Datos de la excepcion
   */
  private final SinacExceptionDto sinacExceptionDto = new SinacExceptionDto();

  public SinacException(SinacExceptionMessageType messageType) {
    super(messageType.getLogMessage());
    sinacExceptionDto.setLogMessage(messageType.getLogMessage());
    sinacExceptionDto.setUserMessage(messageType.getUserMessage());
  }

  public SinacException(Throwable t, SinacExceptionMessageType messageType) {
    super(messageType.getLogMessage(), t);
    sinacExceptionDto.setLogMessage(messageType.getLogMessage());
    sinacExceptionDto.setUserMessage(messageType.getUserMessage());
  }

  public SinacExceptionDto getSinacExceptionDto() {
    return sinacExceptionDto;
  }

  public SinacException type(SinacExceptionType type) {
    sinacExceptionDto.setType(type);
    return this;
  }

  public SinacException logMessageParams(Object... params) {
    sinacExceptionDto.setLogMessageParams(params);
    return this;
  }

  public SinacException userMessageParams(Object... params) {
    sinacExceptionDto.setUserMessageParams(params);
    return this;
  }

  public SinacException destPage(String destPage) {
    sinacExceptionDto.setDestPage(destPage);
    return this;
  }

  public SinacException redirect(boolean redirect) {
    sinacExceptionDto.setRedirect(redirect);
    return this;
  }

  public SinacException level(String level) {
    sinacExceptionDto.setLevel(level);
    return this;
  }

  @Override
  public String getMessage() {
    if (sinacExceptionDto != null && sinacExceptionDto.getLogMessageParams() != null && sinacExceptionDto.getLogMessageParams().length > 0) {
      return format(super.getMessage(), sinacExceptionDto.getLogMessageParams());
    }
    return super.getMessage();
  }
  
  public String getUserMessage() {
    if(sinacExceptionDto.getUserMessage() == null) {
      return getMessage();
    }else if(sinacExceptionDto.getUserMessageParams() != null && sinacExceptionDto.getUserMessageParams().length > 0){
      return format(sinacExceptionDto.getUserMessage(), sinacExceptionDto.getUserMessageParams());
    }else {
      return sinacExceptionDto.getUserMessage();
    }
  }

  private String format(String message, Object[] messageParams) {
    try {
      return MessageFormat.format(message, messageParams);
    } catch (IllegalArgumentException | NullPointerException e) {
      return message;
    }
  }

}
