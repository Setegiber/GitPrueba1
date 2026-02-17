package es.mjusticia.sinac.core.business.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ServletRequestPathUtils;

import es.mjusticia.sinac.core.utils.Constantes;

/**
 * Controlador de errores de la Aplicación Sinac-Core.
 *
 * @author NTT Data.
 */
@ControllerAdvice
public class SinacExceptionHandler {

  private static final String ERROR_MESSAGE_NAME = "errorMessage";

  private static final String ERROR_NAME = "error";

  private static final String ERROR_GENERICO_MSG = "Se ha producido un error";

  private static final String FORBIDDEN_NAME = "sin_acceso";

  /**
   * Contexto Wopi
   */
  private static final String WOPI_CONTEXT = "WOPI";

  /**
   * LOG
   */
  private static final Logger LOG = LoggerFactory.getLogger(SinacExceptionHandler.class);

  /**
   * Nombre de cabecera de peticiones tipo AJAX
   */
  private static final String AJAX_HEADER = "X-Requested-With";

  /**
   * Valor de cabecera tipo AJAX
   */
  private static final String AJAX_VALUE = "XMLHttpRequest";

  /**
   * Gestion de errores controlados de SINAC
   */
  @ExceptionHandler(SinacException.class)
  public ModelAndView handleSinacException(SinacException ex, Model model, WebRequest request) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModel().putAll(model.asMap());
    String message = getMessage(ex);
    if (AJAX_VALUE.equals(request.getHeader(AJAX_HEADER))) {
      modelAndView.addObject(Constantes.Mensajes.MESSAGE_SINAC_ACTIVE_KEY, true);
      modelAndView.addObject(Constantes.Mensajes.MESSAGE_SINAC_LEVEL_KEY, ex.getSinacExceptionDto().getLevel());
      modelAndView.addObject(Constantes.Mensajes.MESSAGE_SINAC_KEY, message);
      modelAndView.setViewName("/layout/mensajes");
    } else {
      modelAndView.addObject(ERROR_NAME, true);
      modelAndView.addObject(ERROR_MESSAGE_NAME, message);
      ex.getSinacExceptionDto().setRedirect(true);
      modelAndView.setViewName(getViewName(ex.getSinacExceptionDto()));
    }
    if (LOG.isErrorEnabled()) {
      LOG.error(ex.getMessage(), ex);
    }
    modelAndView.setStatus(getStatus(ex.getSinacExceptionDto().getType()));
    return modelAndView;
  }

  private HttpStatus getStatus(SinacExceptionType type) {
    switch (type) {
    case VALIDATION:
      return HttpStatus.BAD_REQUEST;
    case BUSINESS:
      return HttpStatus.INTERNAL_SERVER_ERROR;
    case MOTOR:
      return HttpStatus.INTERNAL_SERVER_ERROR;
    case SECURITY:
      return HttpStatus.FORBIDDEN;
    case DATA:
      return HttpStatus.INTERNAL_SERVER_ERROR;
    default:
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  /**
   * Gestión de Errores SINAC WOPI
   * 
   * @param ex
   * @param model
   * @param request
   * @return
   */
  @ExceptionHandler(SinacWopiException.class)
  public ResponseEntity<Map<String, Object>> handleSinacWopiException(SinacWopiException ex) {
    LOG.error(ex.getMessage(), ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({ AccessDeniedException.class })
  public ModelAndView handleAccessDeniedException(AccessDeniedException ex, Model model, WebRequest request) {
    LOG.error("Acceso denegado a SINAC", ex);
    return new ModelAndView(FORBIDDEN_NAME);
  }

  /**
   * Gestion de errores no controlados
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Object handleException(Exception ex, Model model, WebRequest request) {
    String path = String.valueOf(request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE, 0));
    if (path != null && path.toUpperCase().contains(WOPI_CONTEXT)) {
      return handleSinacWopiException(new SinacWopiException(ex, ERROR_GENERICO_MSG));
    }
    ModelAndView modelAndView = new ModelAndView(ERROR_NAME);
    modelAndView.getModel().putAll(model.asMap());
    modelAndView.addObject(ERROR_NAME, true);
    String errorMessage = ERROR_GENERICO_MSG;
    modelAndView.addObject(ERROR_MESSAGE_NAME, errorMessage);
    LOG.error(errorMessage, ex);
    return modelAndView;
  }

  /**
   * Recupera el mensaje de error a mostrar por pantalla
   */
  private String getMessage(SinacException ex) {
    String message;
    SinacExceptionDto sed = ex.getSinacExceptionDto();
    if (SinacExceptionType.SECURITY.equals(sed.getType())) {
      if (sed.getUserMessage() == null) {
        sed.setUserMessage("El usuario no tiene permisos para realizar la acción");
      }
    } 
    message = ex.getUserMessage();
    if (message == null) {
      message = ERROR_GENERICO_MSG;
    }
    return message;
  }

  /**
   * Recupera el nombre de la vista a devolver
   */
  private String getViewName(SinacExceptionDto exDto) {
    if (exDto.isRedirect()) {
      return "redirect:/".concat(exDto.getDestPage());
    }
    return exDto.getDestPage();
  }

  /**
   * Validaciones automáticas
   */

  @ExceptionHandler({ MethodArgumentNotValidException.class })
  public final ResponseEntity<Object> handleException(MethodArgumentNotValidException ex) {
    return handleMethodArgumentNotValid(ex, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpStatus status) {
    Map<String, Object> fieldError = new HashMap<>();
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    fieldErrors.stream().forEach((FieldError error) -> {
      if (error.getField().equals("mapaFormularioCamposValida")) {
        String errorCode = error.getCode().replace(Constantes.Validaciones.ERROR_PREFIX_KEY, "");
        errorCode = errorCode.replace("mapaFormularioCamposValida", "formularioCamposValida");
        fieldError.put(errorCode, error.getDefaultMessage());
      } else {
        fieldError.put(error.getField(), error.getDefaultMessage());
      }

    });
    Map<String, Object> response = new HashMap<>();
    response.put("isSuccess", true);
    response.put("data", null);
    response.put("status", status);
    response.put("fieldError", fieldError);
    return new ResponseEntity<>(response, status);
  }

}
