package es.mjusticia.pagojus.frontconsola.web;

import es.mjusticia.milano.security.Principal;
/*-
 * #%L
 * auditoriaweb-webfacade
 * %%
 * Copyright (C) 2023 - 2024 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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
import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class SecurityWebControllerImpl.
 */
@Controller
@RequestMapping("/")
public class SecurityWebController {

  /** The Constant ERROR_AUTORIZACION. */
  private static final String ERROR_AUTORIZACION = "Usuario o contrase\u00F1a incorrectos";

  /** The Constant ERROR_AUTORIZACION_TEST. */
  private static final String ERROR_AUTORIZACION_TEST = "Úsùärïô ö çöǹtŕà&ëñá îńçöŕŕèçẗöś";

  /** The Constant REDIRECT_AD_FORM. */
  private static final String REDIRECT_UNICA_FORM = "redirect:/auth/unica/form";

  /** The Constant LOGGER. */
  private static final Logger LOGGER = Logger.getLogger(SecurityWebController.class);

  /**
   * Logout.
   *
   * @param principal the principal
   * @param request   the request
   * @return the model and view
   */
  @PostMapping(path = "/auth/logout")
  public ModelAndView logout(@AuthenticationPrincipal Principal principal, HttpServletRequest request) {
    request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
    var ret = new ModelAndView();
    ret.setViewName("redirect:/auth/unica/logout");
    return ret;
  }

  /**
   * Show authentication error.
   *
   * @return the model and view
   */
  /*
   * @RequestMapping(path = "/auth/error/authentication-error") public
   * ModelAndView showAuthenticationError() { ModelAndView modelAndView = new
   * ModelAndView(REDIRECT_UNICA_FORM); modelAndView.addObject("error",
   * ERROR_AUTORIZACION); return modelAndView; }
   */

  @RequestMapping(path = "/auth/error/authentication-error")
  public ModelAndView showAuthenticationError(HttpServletResponse response) {
    response.setContentType("text/html;charset=UTF-8");
    ModelAndView modelAndView = new ModelAndView(REDIRECT_UNICA_FORM);
    modelAndView.addObject("error", ERROR_AUTORIZACION);
    return modelAndView;
  }

  /**
   * Show authorization error.
   *
   * @param request  the request
   * @param response the response
   * @return the string
   */
  @GetMapping(path = "/auth/error/authorization-error")
  public String showAuthorizationError(HttpServletRequest request, HttpServletResponse response) {
    return "error/authorization-error.html";
  }

  @GetMapping(path = "/auth/error/noRole-error")
  public String showNoRoleError(HttpServletRequest request, HttpServletResponse response) {
    return "error/noRole-error.html";
  }

  /**
   * Active directory login.
   *
   * @param principal the principal
   * @param error     the error
   * @return the model and view
   */
  @GetMapping(path = "/auth/unica/form")
  public ModelAndView activeDirectoryLogin(@AuthenticationPrincipal Principal principal,
      @RequestParam(value = "error", required = false) String error) {
    ModelAndView model = new ModelAndView("auth/unica/unica-login");
    if (error != null) {
      model.addObject("error", error);
    }
    return model;
  }

  /**
   * Index.
   *
   * @param principal the principal
   * @return the model and view
   */
  // @GetMapping(path = "/")
  // public ModelAndView index(@AuthenticationPrincipal Principal principal) {
  // try{
  // if (principal == null) {
  // return new ModelAndView(REDIRECT_UNICA_FORM);
  // }
  // LOGGER.debug("Usuario logueado:" + principal.getId());
  // ModelAndView modelAndView = new ModelAndView();
  // modelAndView.setViewName("redirect:/auditoria/formulario");
  // modelAndView.addObject("primeraConsulta", true);
  // return modelAndView;
  //
  // }catch (Exception e){
  // LOGGER.error("Error al mostrar el formulario principal", e);
  // return new ModelAndView("error/generic-error.html");
  // }
  // }
}
