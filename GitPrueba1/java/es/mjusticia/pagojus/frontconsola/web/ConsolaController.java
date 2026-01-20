package es.mjusticia.pagojus.frontconsola.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.mjusticia.milano.rest.client.RestClientFactory;
/*-
 * #%L
 * pagojus-frontsede
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
import es.mjusticia.milano.security.Principal;
import es.mjusticia.milano.security.auth.jwt.client.JwtServiceClient;
import es.mjusticia.milano.security.auth.jwt.client.rest.JwtRestClientInterceptor;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.PeticionConsolaDetallePagoTasaEditar;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.PeticionDetalleTasasPagador;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.PeticionDetalleTasasPagadorDetalle;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.RespuestaDetalleTasasPagador;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.ServicesResponse;
import es.mjusticia.pagojus.frontconsola.enums.Estados;
import es.mjusticia.pagojus.frontconsola.model.InformarTasasDto;
import es.mjusticia.pagojus.frontconsola.model.TasasBusquedaConsolaDto;
import es.mjusticia.pagojus.frontconsola.model.TasasDetalleConsolaDto;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaDetalleFormVO;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaEditarFormVO;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaPrincipalFormVO;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping
public class ConsolaController {

  private static final Logger log = LoggerFactory.getLogger(ConsolaController.class);

  private static final String URL_CONSOLA_PRINCIPAL = "/adminPagos/consolaPrincipal";
  private static final String URL_CONSOLA_PRINCIPAL_BUSQUEDA = "/adminPagos/consolaPrincipalBusqueda";
  private static final String URL_CONSOLA_PRINCIPAL_ACTION = "/adminPagos/consolaPrincipal/action";
  private static final String URL_CONSOLA_PRINCIPAL_EXCEL = "/adminPagos/consolaPrincipalBusqueda/excel";
  private static final String URL_CONSOLA_DETALLE_ACTION = "/adminPagos/consolaDetalle/action";
  private static final String PANTALLA_ERROR_GENERICO = "error/generic-error";
  private static final String COD_APLICACION = "codAplicacion";
  private static final String BLANK = "";
  private static final String NO_ROLE_APP = "EL USUARIO INDICADO NO TIENE NINGUN ROL ASIGNADO EN LA APLICACION";
  private static final String ADMIN = "ADMIN";
  private static final String VENTANILLA = "VENTANILLA";
  private static final String EMPLEADO_PUBLICO = "EMPLEADO_PUBLICO";

  @Autowired
  private Environment env;

  @Autowired
  private JwtServiceClient jwtService;

  private RestTemplate restTemp;

  // @Autowired
  // private ConsolaBusiness consolaBusiness;

  @GetMapping(value = "/")
  public ModelAndView defaultIndex() {
    log.info("Accediendo a la ruta raíz, redireccionando a la consola de administracion");
    return new ModelAndView("redirect:/adminPagos");
  }

  @GetMapping(value = "/adminPagos")
  public ModelAndView adminPagos(RedirectAttributes redirectAttributes) {
    log.info("Accediendo a consola de administracion, redireccionando a la pagina principal");

    // redirectAttributes.addFlashAttribute(MSG_FALLO_PAGO, MSG_FALLO_PAGO);
    // redirectAttributes.addFlashAttribute(FALLO_PAGO, "0");
    return new ModelAndView("redirect:/adminPagos/consolaPrincipal");
  }

  /**
   * Método que maneja la solicitud GET a la ruta "/adminPagos/consolaPrincipal"
   * 
   * @return Un objeto ModelAndView que representa la vista y los datos a mostrar
   */
  @GetMapping(value = URL_CONSOLA_PRINCIPAL)
  public ModelAndView adminPagosPrincipal(HttpServletRequest request, Authentication authentication,
      RedirectAttributes redirectAttributes) {
    log.info("Accediendo a la ruta {}", URL_CONSOLA_PRINCIPAL);

    List<String> roles = new ArrayList<>();
    String codApp;

    authentication.getAuthorities().forEach(a -> roles.add(a.getAuthority()));
    codApp = gestionarRoles(roles);

    if (codApp.isEmpty()) {

      log.error("\"El usuario no tiene un rol en la aplicación. Redirigiendo a: 'error/noRole-error'.");
      return new ModelAndView("error/noRole-error");

    }

    // informar tasas, codAplicacion deberá ser ADMIN, VENTANILLA o una aplicacion
    // como CERTAPE
    Map<String, String> peticionInformarTasa = new HashMap<>();
    peticionInformarTasa.put(COD_APLICACION, codApp); // pruebas
    // Generamos el JSON que se mandar en la llamada
    HttpEntity<String> entity = null;
    try {
      entity = createPeticionEntity(peticionInformarTasa);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de informar tasas - error: {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    // Realizamos la llamada al servicioWeb
    ResponseEntity<String> response = null;
    try {
      response = getRestTemp().postForEntity("/consola/detallePagoTasa/informarTasas", entity, String.class);
    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de informar tasas - {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    // Procesamos la respuesta que nos ha devuelto del servicioWeb
    ArrayList<InformarTasasDto> retCampos = null;
    try {
      retCampos = (ArrayList<InformarTasasDto>) createResponse(response, List.class);
    } catch (JsonProcessingException e) {
      log.error("{}: Error leyendo el JSON al RECIBIR la respuesta de informar tasas - {}",
          URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    ModelMap model = new ModelMap("selectorTasa", retCampos);

    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    ArrayList<TasasBusquedaConsolaDto> tasas = new ArrayList<>();

    model.addAttribute("tasas", tasas);
    model.addAttribute("formConsolaPrincipal", form);
    model.addAttribute("numPag", 0);
    model.addAttribute("tamPag", 0);
    model.addAttribute("totalRegistros", 0);

    model.addAttribute("primerResultado", 0);
    model.addAttribute("ultimoResultado", 0);
    Principal p = (Principal) authentication.getPrincipal();
    model.addAttribute("username", p.getNombre() + " " + p.getPrimerApellido());
    model.addAttribute("userrole", "[" + codApp + "]");

    return new ModelAndView(URL_CONSOLA_PRINCIPAL, model);
  }

  /**
   * Método que maneja la solicitud GET a la ruta "/adminPagos/consolaPrincipal"
   * 
   * @return Un objeto ModelAndView que representa la vista y los datos a mostrar
   */
  @GetMapping(value = URL_CONSOLA_PRINCIPAL_BUSQUEDA)
  public ModelAndView adminPagosPrincipalBusqueda(HttpServletRequest request, Authentication authentication,
      @Valid @ModelAttribute("formConsolaPrincipal") ConsolaPrincipalFormVO form,
      // @RequestParam(name = "codApp", defaultValue = "CERTAPE") String codApp,
      @RequestParam(name = "asc", defaultValue = "false") String asc,
      @RequestParam(name = "orderBy", defaultValue = "idPago") String orderBy,
      @RequestParam(name = "tamPag", defaultValue = "10") String tamPag,
      @RequestParam(name = "numPag", defaultValue = "1") String numPag, RedirectAttributes redirectAttributes) {
    log.info("Accediendo a la ruta {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA);

    // ROLE_EMPLEADO_PUBLICO, ROLE_CERTAPE, ROLE_ADMINISTRADOR GESTIONAR
    // authentication.getAuthorities().forEach(a -> log.info("Authority: {}",
    // a.getAuthority()));
    ArrayList<String> roles = new ArrayList<>();
    authentication.getAuthorities().forEach(a -> roles.add(a.getAuthority()));
    String codApp = gestionarRoles(roles);
    if (codApp.isEmpty()) {
      log.error("El usuario no tiene un rol en la aplicación. Redirigiendo a: 'error/noRole-error'.");
      return new ModelAndView("error/noRole-error");
    }
    // informar tasas, codAplicacion deberá ser ADMIN, VENTANILLA o una aplicacion
    // como CERTAPE
    Map<String, String> peticionInformarTasa = new HashMap<>();
    peticionInformarTasa.put(COD_APLICACION, codApp); // pruebas
    // Generamos el JSON que se mandar en la llamada
    HttpEntity<String> entity = null;
    try {
      entity = createPeticionEntity(peticionInformarTasa);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de informar tasas - error: {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    // Realizamos la llamada al servicioWeb
    ResponseEntity<String> response = null;
    try {
      response = getRestTemp().postForEntity("/consola/detallePagoTasa/informarTasas", entity, String.class);
    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de informar tasas - {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    // Procesamos la respuesta que nos ha devuelto del servicioWeb
    ArrayList<InformarTasasDto> retCampos = null;
    try {
      retCampos = (ArrayList<InformarTasasDto>) createResponse(response, List.class);
    } catch (JsonProcessingException e) {
      log.error("{}: Error leyendo el JSON al RECIBIR la respuesta de informar tasas - {}",
          URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    ModelMap model = new ModelMap("selectorTasa", retCampos);

    // buscar tasas
    PeticionDetalleTasasPagador peticion = new PeticionDetalleTasasPagador();
    peticion.setCodAplicacion(codApp); // pruebas

    // parece que no hay más datos obligatorios para la búsqueda inicial, aunque
    // podríamos rellenar
    // el numero de pagina por defecto en 1 y el tamaño de página a 10, así como el
    // orden predefinido
    if (asc.equals("true")) {
      peticion.setOrden("asc");
    } else if (asc.equals("false")) {
      peticion.setOrden("desc");
    } else {
      peticion.setOrden(asc);
    }
    peticion.setColumnaOrden(orderBy);
    if (!tamPag.isEmpty()) {
      peticion.setTamanioPagina(Integer.parseInt(tamPag));
    }
    if (!numPag.isEmpty()) {
      peticion.setNumeroPagina(Integer.parseInt(numPag));
    }

    peticion.setExpediente(StringUtils.trim(form.getFiltroIdTasa()));
    if (form.getFiltroLocalizador() != null && !StringUtils.trim(form.getFiltroLocalizador()).isEmpty()) {
      peticion.setLocalizador(Long.parseLong(form.getFiltroLocalizador()));
    }
    peticion.setTasa(StringUtils.trim(StringUtils.trim(form.getFiltroNombreTasa())));
    peticion.setNrc(StringUtils.trim(form.getFiltroNRC()));
    peticion.setFechaPagoDesde(StringUtils.trim(form.getFechaPagoDesde()));
    peticion.setFechaPagoHasta(StringUtils.trim(form.getFechaPagoHasta()));
    peticion.setNombrePagador(StringUtils.trim(form.getFiltroNombrePagador().toUpperCase()));
    peticion.setApellidoUnoPagador(StringUtils.trim(form.getFiltroApellido1Pagador().toUpperCase()));
    peticion.setApellidoDosPagador(StringUtils.trim(form.getFiltroApellido2Pagador().toUpperCase()));
    peticion.setNombreRepresentante(StringUtils.trim(form.getFiltroNombreEntidad().toUpperCase()));
    peticion.setApellidoUnoRepresentante(StringUtils.trim(form.getFiltroApellido1Entidad().toUpperCase()));
    peticion.setApellidoDosRepresentante(StringUtils.trim(form.getFiltroApellido2Entidad().toUpperCase()));
    peticion.setNifPagador(StringUtils.trim(form.getFiltroDocumentoPagador()));
    peticion.setNifRepresentante(StringUtils.trim(form.getFiltroDocumentoEntidad()));

    peticion.setEstadoTasa(StringUtils.trim(form.getFiltroEstadoTasa()));
    peticion.setRazonSocialPagador(StringUtils.trim(form.getFiltroRazonSocial()));

    // Generamos el JSON que se mandar en la llamada
    entity = null;
    try {
      entity = createPeticionEntity(peticion);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de busqueda de tasas - error: {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // Realizamos la llamada al servicioWeb
    response = null;
    try {
      response = getRestTemp().postForEntity("/consola/detallePagoTasa/busqueda", entity, String.class);
    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de busqueda de tasas - {}", URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // Procesamos la respuesta que nos ha devuelto del servicioWeb
    RespuestaDetalleTasasPagador ret = null;
    try {
      ret = (RespuestaDetalleTasasPagador) createResponse(response, RespuestaDetalleTasasPagador.class);
    } catch (JsonProcessingException e) {
      log.error("{}: Error leyendo el JSON al RECIBIR la respuesta de busqueda de tasas - {}",
          URL_CONSOLA_PRINCIPAL_BUSQUEDA, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    model.addAttribute("tasas", ret.getResultados());
    model.addAttribute("formConsolaPrincipal", form);
    model.addAttribute("numPag", numPag);
    model.addAttribute("tamPag", tamPag);
    model.addAttribute("totalRegistros", ret.getTotalResultados());

    if (numPag.isEmpty()) {
      numPag = "1";
    }
    int primerResultado = ((Integer.parseInt(numPag) - 1) * Integer.parseInt(tamPag)) + 1;
    int ultimoResultado = primerResultado + Integer.parseInt(tamPag) - 1;
    model.addAttribute("primerResultado", primerResultado);
    model.addAttribute("ultimoResultado", ultimoResultado);
    model.addAttribute("codApp", codApp);
    Principal p = (Principal) authentication.getPrincipal();
    model.addAttribute("username", p.getNombre() + " " + p.getPrimerApellido());
    model.addAttribute("userrole", "[" + codApp + "]");

    return new ModelAndView(URL_CONSOLA_PRINCIPAL, model);
  }

  @PostMapping(value = "/adminPagos/consolaPrincipal/action")
  public ModelAndView adminPagosPrincipalAction(Authentication authentication,
      @Valid @ModelAttribute("formConsolaDetalle") ConsolaDetalleFormVO form, BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {

    return this.getDatosDetalle(redirectAttributes, StringUtils.trim(form.getCodApp()),
        Long.parseLong(StringUtils.trim(form.getIdPago())), 0);
    /*
     * PeticionDetalleTasasPagadorDetalle peticion = new
     * PeticionDetalleTasasPagadorDetalle();
     * peticion.setCodAplicacion(StringUtils.trim(form.getCodApp());
     * peticion.setIdPago(Long.parseLong(StringUtils.trim(form.getIdPago())); //
     * Generamos el JSON
     * que se mandar en la llamada HttpEntity<String> entity = null; try { entity =
     * createPeticionEntity(peticion); } catch (JsonProcessingException e) {
     * log.error(" {}: Error al crear peticion de detalle de tasas - error: {}",
     * URL_CONSOLA_PRINCIPAL_ACTION, e); return new
     * ModelAndView(PANTALLA_ERROR_GENERICO); }
     * 
     * // Realizamos la llamada al servicioWeb ResponseEntity<String> response =
     * null; try { response =
     * getRestTemp().postForEntity("/consola/detallePagoTasa/detalle", entity,
     * String.class); } catch (Exception e) {
     * log.error("{}: Error al llamar al servicio web de busqueda de tasas - {}",
     * URL_CONSOLA_PRINCIPAL_ACTION, e); return new
     * ModelAndView(PANTALLA_ERROR_GENERICO); }
     * 
     * TasasDetalleConsolaDto ret = null; try { ret = (TasasDetalleConsolaDto)
     * createResponse(response, TasasDetalleConsolaDto.class); } catch
     * (JsonProcessingException e) { log.
     * error("{}: Error leyendo el JSON al RECIBIR la respuesta de busqueda de tasas - {}"
     * , URL_CONSOLA_PRINCIPAL, e); return new
     * ModelAndView(PANTALLA_ERROR_GENERICO); }
     * 
     * // si todo esto funciona aniadimos los campos del retorno al modelo
     * redirectAttributes.addFlashAttribute("idPago", ret.getIdPago());
     * redirectAttributes.addFlashAttribute("idLocalizador",
     * ret.getIdLocalizador()); redirectAttributes.addFlashAttribute("codTasa",
     * ret.getCodTasa()); redirectAttributes.addFlashAttribute("descTasa",
     * ret.getDescTasa()); redirectAttributes.addFlashAttribute("expediente",
     * ret.getExpediente()); redirectAttributes.addFlashAttribute("importeTasa",
     * ret.getImporteTasa());
     * redirectAttributes.addFlashAttribute("documentoPagador",
     * ret.getDocumentoPagador());
     * redirectAttributes.addFlashAttribute("documentoRepresentante",
     * ret.getDocumentoRepresentante());
     * redirectAttributes.addFlashAttribute("nombreCompletoRepresentante",
     * ret.getNombreCompletoRepresentante());
     * redirectAttributes.addFlashAttribute("nrc", ret.getNrc());
     * redirectAttributes.addFlashAttribute("fhPago", ret.getFhPago());
     * redirectAttributes.addFlashAttribute("estadoTasa", ret.getEstadoTasa());
     * redirectAttributes.addFlashAttribute("nombreCompleto",
     * ret.getNombreCompleto());
     * redirectAttributes.addFlashAttribute(COD_APLICACION,
     * StringUtils.trim(form.getCodApp());
     * redirectAttributes.addFlashAttribute("volver", 0); return new
     * ModelAndView("redirect:/adminPagos/consolaDetalle");
     */
  }

  @GetMapping(value = "/adminPagos/consolaDetalle")
  public ModelAndView adminPagosDetalle(Authentication authentication, HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    log.info("Accediendo a la ruta {}", "consolaDetalle");

    // usaremos el redirectAttributes para tener cargados los datos desde el action
    // en el que llamaremos al servicio web
    ModelMap model = new ModelMap();
    Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
    if (inputFlashMap == null) {
      // ver cuando se llega aqui para controlar errores
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }
    model.addAttribute("idPago", (Long) inputFlashMap.get("idPago"));
    model.addAttribute("idLocalizador", (Long) inputFlashMap.get("idLocalizador"));
    model.addAttribute("codTasa", (String) inputFlashMap.get("codTasa"));
    model.addAttribute("descTasa", (String) inputFlashMap.get("descTasa"));
    model.addAttribute("expediente", (String) inputFlashMap.get("expediente"));
    model.addAttribute("importeTasa", (String) inputFlashMap.get("importeTasa"));
    model.addAttribute("documentoPagador", (String) inputFlashMap.get("documentoPagador"));
    model.addAttribute("documentoRepresentante", (String) inputFlashMap.get("documentoRepresentante"));
    model.addAttribute("nombreCompletoRepresentante", (String) inputFlashMap.get("nombreCompletoRepresentante"));
    model.addAttribute("nrc", (String) inputFlashMap.get("nrc"));
    model.addAttribute("fhPago", (String) inputFlashMap.get("fhPago"));
    model.addAttribute("estadoTasa", (String) inputFlashMap.get("estadoTasa"));
    model.addAttribute("nombreCompleto", (String) inputFlashMap.get("nombreCompleto"));
    model.addAttribute(COD_APLICACION, (String) inputFlashMap.get(COD_APLICACION));

    model.addAttribute("formConsolaEditar", new ConsolaEditarFormVO());
    Principal p = (Principal) authentication.getPrincipal();
    model.addAttribute("username", p.getNombre() + " " + p.getPrimerApellido());
    model.addAttribute("userrole", "[" + (String) inputFlashMap.get(COD_APLICACION) + "]");
    model.addAttribute("volver", (int) inputFlashMap.get("volver") - 1);

    String estadoTasaActual = (String) inputFlashMap.get("estadoTasa");
    ArrayList<String> estadosPosibles = new ArrayList<>();
    if (((String) inputFlashMap.get(COD_APLICACION)).equals("VENTANILLA")) {
      if (estadoTasaActual.equalsIgnoreCase(Estados.PAGADO.name())) {
        estadosPosibles.add(BLANK);
        estadosPosibles.add("UTILIZADO");
      }
    } else {
      if (estadoTasaActual.equalsIgnoreCase(Estados.PAGADO.name())) {
        estadosPosibles.add(BLANK);
        estadosPosibles.add("UTILIZADO");
        estadosPosibles.add("DEVUELTO");
      } else if (estadoTasaActual.equalsIgnoreCase(Estados.UTILIZADO.name())) {
        estadosPosibles.add(BLANK);
        estadosPosibles.add("PAGADO");
        estadosPosibles.add("DEVUELTO");
      }

    }
    if (estadosPosibles.isEmpty()) {
      estadosPosibles.add(BLANK);
    }
    model.addAttribute("estadosPosibles", estadosPosibles);

    return new ModelAndView("/adminPagos/consolaDetalle", model);
  }

  @PostMapping(value = "/adminPagos/consolaDetalle/action")
  public ModelAndView adminPagosDetalleAction(@Valid @ModelAttribute("formConsolaEditar") ConsolaEditarFormVO form,
      BindingResult bindingResult, RedirectAttributes redirectAttributes) {

    PeticionConsolaDetallePagoTasaEditar peticion = new PeticionConsolaDetallePagoTasaEditar();
    peticion.setIdPago(Long.parseLong(StringUtils.trim(form.getIdPago())));
    peticion.setCodAplicacion(StringUtils.trim(form.getCodAplicacion()));
    peticion.setEstado(StringUtils.trim(form.getEstadoTasa()));

    if (!(StringUtils.trim(form.getCodAplicacion())).equals("VENTANILLA")) {
      peticion.setNrc(StringUtils.trim(form.getNrc()));
      peticion.setExpediente(StringUtils.trim(form.getExpediente()));
    }

    // Generamos el JSON que se mandar en la llamada
    HttpEntity<String> entity = null;
    try {
      entity = createPeticionEntity(peticion);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de editar tasas - error: {}", URL_CONSOLA_DETALLE_ACTION, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // Realizamos la llamada al servicioWeb
    ResponseEntity<String> response = null;
    try {

      response = getRestTemp().postForEntity("/consola/detallePagoTasa/editar", entity, String.class);

    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de editar tasas - {}", URL_CONSOLA_DETALLE_ACTION, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // tenemos que volver a añadir los campos al modelo, para ello rellenamos las
    // variables de redireccion

    return this.getDatosDetalle(redirectAttributes, StringUtils.trim(form.getCodAplicacion()),
        Long.parseLong(StringUtils.trim(form.getIdPago())),
        form.getContadorHistorialVolver());
    /*
     * ModelMap model = new ModelMap(); // si todo esto funciona aniadimos los
     * campos del retorno al modelo redirectAttributes.addFlashAttribute("idPago",
     * Long.parseLong(StringUtils.trim(form.getIdPago()));
     * redirectAttributes.addFlashAttribute("idLocalizador",
     * Long.parseLong(StringUtils.trim(form.getIdLocalizador()));
     * redirectAttributes.addFlashAttribute("codTasa",
     * StringUtils.trim(form.getCodTasa());
     * redirectAttributes.addFlashAttribute("descTasa",
     * StringUtils.trim(form.getDescTasa());
     * redirectAttributes.addFlashAttribute("expediente",
     * StringUtils.trim(form.getExpediente());
     * redirectAttributes.addFlashAttribute("importeTasa",
     * StringUtils.trim(form.getImporteTasa());
     * redirectAttributes.addFlashAttribute("documentoPagador",
     * StringUtils.trim(form.getDocumentoPagador());
     * redirectAttributes.addFlashAttribute("documentoRepresentante",
     * StringUtils.trim(form.getDocumentoRepresentante());
     * redirectAttributes.addFlashAttribute("nombreCompletoRepresentante",
     * StringUtils.trim(form.getNombreCompletoRepresentante());
     * redirectAttributes.addFlashAttribute("importeTasa",
     * StringUtils.trim(form.getImporteTasa());
     * redirectAttributes.addFlashAttribute("nrc", StringUtils.trim(form.getNrc());
     * redirectAttributes.addFlashAttribute("fhPago",
     * StringUtils.trim(form.getFhPago());
     * redirectAttributes.addFlashAttribute("estadoTasa",
     * StringUtils.trim(form.getEstadoTasa());
     * redirectAttributes.addFlashAttribute("nombreCompleto",
     * StringUtils.trim(form.getNombreCompleto());
     * redirectAttributes.addFlashAttribute(COD_APLICACION,
     * StringUtils.trim(form.getCodAplicacion());
     * redirectAttributes.addFlashAttribute("volver",
     * StringUtils.trim(form.getContadorHistorialVolver());
     * 
     * return new ModelAndView("redirect:/adminPagos/consolaDetalle", model);
     */
  }

  // SERVICIO EXCEL, DEBERIAMOS REDIRECCIONAR CON UN FLAG DESDE LA PANTALLA
  // PRINCIPAL
  // O PROBAR A APROVECHAR QUE ES GET PARA GENERAR LA PETICION CON LA URL EN
  // JAVASCRIPT
  @GetMapping(value = URL_CONSOLA_PRINCIPAL_EXCEL)
  public ResponseEntity<Resource> adminPagosPrincipalExcel(HttpServletRequest request, Authentication authentication,
      @Valid @ModelAttribute("formConsolaPrincipal") ConsolaPrincipalFormVO form,
      // @RequestParam(name = "codApp", defaultValue = "CERTAPE") String codApp,
      @RequestParam(name = "asc", defaultValue = "false") String asc,
      @RequestParam(name = "orderBy", defaultValue = "idPago") String orderBy
  // @RequestParam(name = "tamPag", defaultValue = "10") String tamPag,
  // @RequestParam(name = "numPag", defaultValue = "1") String numPag
  ) {
    String tamPag = "1000";
    String numPag = "1";
    log.info("Accediendo a la ruta {}", URL_CONSOLA_PRINCIPAL_EXCEL);

    ArrayList<String> roles = new ArrayList<>();
    String codApp;

    authentication.getAuthorities().forEach(a -> roles.add(a.getAuthority()));
    codApp = gestionarRoles(roles);

    if (codApp.isEmpty()) {

      log.error("\"El usuario no tiene un rol en la aplicación.");
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    PeticionDetalleTasasPagador peticion = new PeticionDetalleTasasPagador();
    peticion.setCodAplicacion(codApp); // pruebas
    // parece que no hay más datos obligatorios para la búsqueda inicial, aunque
    // podríamos rellenar
    // el numero de pagina por defecto en 1 y el tamaño de página a 10, así como el
    // orden predefinido
    if (asc.equals("true")) {
      peticion.setOrden("asc");
    } else if (asc.equals("false")) {
      peticion.setOrden("desc");
    } else {
      peticion.setOrden(asc);
    }
    peticion.setColumnaOrden(orderBy);
    if (!tamPag.isEmpty()) {
      peticion.setTamanioPagina(Integer.parseInt(tamPag));
    }
    if (!numPag.isEmpty()) {
      peticion.setNumeroPagina(Integer.parseInt(numPag));
    }

    peticion.setExpediente(StringUtils.trim(form.getFiltroIdTasa()));
    if (form.getFiltroLocalizador() != null && !StringUtils.trim(form.getFiltroLocalizador()).isEmpty()) {
      peticion.setLocalizador(Long.parseLong(form.getFiltroLocalizador()));
    }
    peticion.setTasa(StringUtils.trim(form.getFiltroNombreTasa()));
    peticion.setNrc(StringUtils.trim(form.getFiltroNRC()));
    peticion.setFechaPagoDesde(StringUtils.trim(form.getFechaPagoDesde()));
    peticion.setFechaPagoHasta(StringUtils.trim(form.getFechaPagoHasta()));
    peticion.setNombrePagador(StringUtils.trim(form.getFiltroNombrePagador()));
    if (peticion.getNombrePagador() != null) {
      peticion.setNombrePagador(peticion.getNombrePagador().toUpperCase());
    }
    peticion.setApellidoUnoPagador(StringUtils.trim(form.getFiltroApellido1Pagador()));
    if (peticion.getApellidoUnoPagador() != null) {
      peticion.setApellidoUnoPagador(peticion.getApellidoUnoPagador().toUpperCase());
    }
    peticion.setApellidoDosPagador(StringUtils.trim(form.getFiltroApellido2Pagador()));
    if (peticion.getApellidoDosPagador() != null) {
      peticion.setApellidoDosPagador(peticion.getApellidoDosPagador().toUpperCase());
    }
    peticion.setNombreRepresentante(StringUtils.trim(form.getFiltroNombreEntidad()));
    if (peticion.getNombreRepresentante() != null) {
      peticion.setNombreRepresentante(peticion.getNombreRepresentante().toUpperCase());
    }
    peticion.setApellidoUnoRepresentante(StringUtils.trim(form.getFiltroApellido1Entidad()));
    if (peticion.getApellidoUnoRepresentante() != null) {
      peticion.setApellidoUnoRepresentante(peticion.getApellidoUnoRepresentante().toUpperCase());
    }
    peticion.setApellidoDosRepresentante(StringUtils.trim(form.getFiltroApellido2Entidad()));
    if (peticion.getApellidoDosRepresentante() != null) {
      peticion.setApellidoDosRepresentante(peticion.getApellidoDosRepresentante().toUpperCase());
    }
    peticion.setNifPagador(StringUtils.trim(form.getFiltroDocumentoPagador()));
    peticion.setNifRepresentante(StringUtils.trim(form.getFiltroDocumentoEntidad()));

    peticion.setEstadoTasa(StringUtils.trim(form.getFiltroEstadoTasa()));
    peticion.setRazonSocialPagador(StringUtils.trim(form.getFiltroRazonSocial()));

    // Generamos el JSON que se mandar en la llamada
    HttpEntity<String> entity = null;
    try {
      entity = createPeticionEntity(peticion);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de descarga de excel - error: {}", URL_CONSOLA_PRINCIPAL_EXCEL, e);
      return null;
    }

    // Realizamos la llamada al servicioWeb
    ResponseEntity<String> response = null;
    try {
      response = getRestTemp().postForEntity("/consola/detallePagoTasa/descargarExcel", entity, String.class);
    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de descarga de excel - {}", URL_CONSOLA_PRINCIPAL_EXCEL, e);
      return null;
    }

    String ret = null;
    try {
      ret = (String) createResponse(response, String.class);
    } catch (JsonProcessingException e) {
      log.error("{}: Error leyendo el JSON al RECIBIR la respuesta de descarga de excel - {}",
          URL_CONSOLA_PRINCIPAL_EXCEL, e);
      return null;
    }

    // se supone que llega directamente como un string en base64
    byte[] fileContent = Base64.getDecoder().decode(ret);
    InputStream is = new ByteArrayInputStream(fileContent);
    InputStreamResource resource = new InputStreamResource(is);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Resultados" + ".xlsx");

    log.info("descargaExcel - Fin");
    // new MediaType("application", "vnd.ms-excel");
    // new MediaType("application",
    // "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    return ResponseEntity.ok().headers(headers)
        .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(resource);
  }

  public HttpEntity<String> createPeticionEntity(Object peticion) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();

    String peticionInformarPagadorJson = BLANK;
    peticionInformarPagadorJson = ow.writeValueAsString(peticion);
    return new HttpEntity<>(peticionInformarPagadorJson, headers);
  }

  public Object createResponse(ResponseEntity<String> response, Class clase) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new JavaTimeModule());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      ServicesResponse respuestaClase = om.readValue(response.getBody(), ServicesResponse.class);
      return om.convertValue(respuestaClase.getRespuesta(), clase);
    } else {
      return null;
    }
  }

  public String gestionarRoles(List<String> roles) {
    String head = "ROLE_";
    String tail = BLANK;
    String rolEncontrado = BLANK;

    for (String role : roles) {
      if (role.length() > head.length()) {
        tail = role.substring(head.length()).toUpperCase();
      }

      switch (tail) {
        case ADMIN:
          return ADMIN;
        case "ADMINISTRADOR":
          return ADMIN;
        case VENTANILLA:
          rolEncontrado = VENTANILLA;
          break;
        case NO_ROLE_APP:
          return BLANK;

        case EMPLEADO_PUBLICO:
          break;
        case BLANK:
          break;

        default:
          if (!rolEncontrado.equals(VENTANILLA)) {
            rolEncontrado = tail;
          }
          break;

      }

    }
    return rolEncontrado;
  }

  /*
   * public String gestionarRoles(List<String> rolesMock) { String rol = BLANK;
   * for (String r : rolesMock) {
   * 
   * if (r.equals(NO_ROLE_APP)) { return NO_ROLE; }
   * 
   * if (r.contains("_ADMIN") || r.equals("_ADMIN")) { return "ADMIN"; } else if
   * (r.contains("_VENTANILLA")) { rol = "VENTANILLA"; } else { if
   * (!rol.equals("VENTANILLA") && !r.contains("_EMPLEADO_PUBLICO")) { rol =
   * r.split("_")[1]; } } } return rol; }
   */

  public RestTemplate getRestTemp() {
    if (restTemp == null) {
      JwtRestClientInterceptor jwtInterceptor = new JwtRestClientInterceptor(jwtService);
      RestClientFactory fact = new RestClientFactory(env, jwtInterceptor);
      restTemp = fact.generateClient();
    }

    return restTemp;
  }

  public void setRestTemp(RestTemplate restTemp) {
    this.restTemp = restTemp;
  }

  private ModelAndView getDatosDetalle(RedirectAttributes redirectAttributes, String codApp, Long idPago,
      int contadorHistorialVolver) {
    PeticionDetalleTasasPagadorDetalle peticion = new PeticionDetalleTasasPagadorDetalle();
    peticion.setCodAplicacion(codApp);
    peticion.setIdPago(idPago);
    // Generamos el JSON que se mandar en la llamada
    HttpEntity<String> entity = null;
    try {
      entity = createPeticionEntity(peticion);
    } catch (JsonProcessingException e) {
      log.error(" {}: Error al crear peticion de detalle de tasas - error: {}", URL_CONSOLA_PRINCIPAL_ACTION, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // Realizamos la llamada al servicioWeb
    ResponseEntity<String> response = null;
    try {
      response = getRestTemp().postForEntity("/consola/detallePagoTasa/detalle", entity, String.class);
    } catch (Exception e) {
      log.error("{}: Error al llamar al servicio web de busqueda de tasas - {}", URL_CONSOLA_PRINCIPAL_ACTION, e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    TasasDetalleConsolaDto ret = null;
    try {
      ret = (TasasDetalleConsolaDto) createResponse(response, TasasDetalleConsolaDto.class);
    } catch (JsonProcessingException e) {
      log.error("{}: Error leyendo el JSON al RECIBIR la respuesta de busqueda de tasas - {}", URL_CONSOLA_PRINCIPAL,
          e);
      return new ModelAndView(PANTALLA_ERROR_GENERICO);
    }

    // si todo esto funciona aniadimos los campos del retorno al modelo
    redirectAttributes.addFlashAttribute("idPago", ret.getIdPago());
    redirectAttributes.addFlashAttribute("idLocalizador", ret.getIdLocalizador());
    redirectAttributes.addFlashAttribute("codTasa", ret.getCodTasa());
    redirectAttributes.addFlashAttribute("descTasa", ret.getDescTasa());
    redirectAttributes.addFlashAttribute("expediente", ret.getExpediente());
    redirectAttributes.addFlashAttribute("importeTasa", ret.getImporteTasa());
    redirectAttributes.addFlashAttribute("documentoPagador", ret.getDocumentoPagador());
    redirectAttributes.addFlashAttribute("documentoRepresentante", ret.getDocumentoRepresentante());
    redirectAttributes.addFlashAttribute("nombreCompletoRepresentante", ret.getNombreCompletoRepresentante());
    redirectAttributes.addFlashAttribute("nrc", ret.getNrc());
    redirectAttributes.addFlashAttribute("fhPago", ret.getFhPago());
    redirectAttributes.addFlashAttribute("estadoTasa", ret.getEstadoTasa());
    redirectAttributes.addFlashAttribute("nombreCompleto", ret.getNombreCompleto());
    redirectAttributes.addFlashAttribute(COD_APLICACION, codApp);
    redirectAttributes.addFlashAttribute("volver", contadorHistorialVolver);
    return new ModelAndView("redirect:/adminPagos/consolaDetalle");

  }

}
