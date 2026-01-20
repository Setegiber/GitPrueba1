package es.mjusticia.pagojus.frontconsola.web;

/*-
 * #%L
 * pagojus-frontconsola
 * %%
 * Copyright (C) 2023 - 2025 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.mjusticia.milano.security.Principal;
import es.mjusticia.milano.security.auth.jwt.client.JwtServiceClient;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.RespuestaDetalleTasasPagador;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.ServicesResponse;
import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import es.mjusticia.pagojus.frontconsola.model.InformarTasasDto;
import es.mjusticia.pagojus.frontconsola.model.TasasDetalleConsolaDto;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaDetalleFormVO;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaEditarFormVO;
import es.mjusticia.pagojus.frontconsola.model.vo.forms.ConsolaPrincipalFormVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsolaControllerTest {

  @InjectMocks
  @Spy
  private ConsolaController consolaController = new ConsolaController();

  @Mock
  private Environment env = (Environment) mock(Environment.class);

  @Mock
  private JwtServiceClient jwtService = (JwtServiceClient) mock(JwtServiceClient.class);

  @Mock
  private RestTemplate restTemplate = (RestTemplate) mock(RestTemplate.class);

  @Mock
  private Authentication authentication = (Authentication) mock(Authentication.class);

  @Mock
  private BindingResult bindingResult = (BindingResult) mock(BindingResult.class);

  @Mock
  RedirectAttributes redirectAttributes = (RedirectAttributes) mock(RedirectAttributes.class);

  @Mock
  HttpServletRequest request = (HttpServletRequest) mock(HttpServletRequest.class);

  @Mock
  private Principal principal = (Principal) mock(Principal.class);

  @Mock
  private GrantedAuthority grantedAuthority = (GrantedAuthority) mock(GrantedAuthority.class);

  @JsonDeserialize(as = ArrayList.class, contentAs = InformarTasasDto.class)
  List<InformarTasasDto> informarTasasDtos = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    // consolaController.setRestTemp(restTemplate);
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testDefaultIndex() {
    ModelAndView modelAndView = consolaController.defaultIndex();
    assertEquals("redirect:/adminPagos", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagos() {
    ModelAndView modelAndView = consolaController.adminPagos(redirectAttributes);
    assertEquals("redirect:/adminPagos/consolaPrincipal", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagosPrincipal() throws Exception {
    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    when(authentication.getName()).thenReturn("testUser");

    // Mocking the response from the service
    ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);
    when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    when(mockResponse.getBody()).thenReturn("{\"respuesta\":[]}");

    ModelAndView modelAndView = consolaController.adminPagosPrincipal(request, authentication, redirectAttributes);
    // assertEquals("/adminPagos/consolaPrincipal", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalActionSuccess() throws Exception {
    // Configurar el mock para el formulario
    ConsolaDetalleFormVO form = new ConsolaDetalleFormVO();
    form.setCodApp("CERTAPE");
    form.setIdPago("12345");

    // Crear un objeto ResponseEntity simulado
    TasasDetalleConsolaDto tasasDetalle = new TasasDetalleConsolaDto();
    tasasDetalle.setIdPago(12345L);
    ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"respuesta\": {\"Id_detallepagotasa\": 12345}}",
        HttpStatus.OK);

    // Configurar el mock para el restTemplate
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(responseEntity);

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalAction(authentication, form, bindingResult,
        redirectAttributes);

    // Verificar que la vista redirecciona correctamente
    // assertEquals("redirect:/adminPagos/consolaDetalle", result.getViewName());
    // verify(redirectAttributes).addFlashAttribute("Id_detallepagotasa",
    // tasasDetalle.getIdPago());
  }

  @Test
  public void testAdminPagosPrincipalActionJsonProcessingException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaDetalleFormVO form = new ConsolaDetalleFormVO();
    form.setCodApp("CERTAPE");
    form.setIdPago("12345");

    // Usar un spy para simular el comportamiento del método createPeticionEntity
    doThrow(JsonProcessingException.class).when(consolaController).createPeticionEntity(any());

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalAction(authentication, form, bindingResult,
        redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/generic-error", result.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalActionWebServiceException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaDetalleFormVO form = new ConsolaDetalleFormVO();
    form.setCodApp("CERTAPE");
    form.setIdPago("12345");

    // Configurar el mock para lanzar una excepción al llamar al servicio web
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalAction(authentication, form, bindingResult,
        redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/generic-error", result.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalBusquedaJsonProcessingException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Usar un spy para simular el comportamiento del método createPeticionEntity
    doThrow(JsonProcessingException.class).when(consolaController).createPeticionEntity(any());

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalBusqueda(null, authentication, form, "false", "idPago",
        "10", "1", redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/noRole-error", result.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalBusquedaWebServiceException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Configurar el mock para lanzar una excepción al llamar al servicio web
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalBusqueda(null, authentication, form, "false", "idPago",
        "10", "1", redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/noRole-error", result.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalBusquedaSuccess() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Configurar el mock para la autenticación
    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());

    // Crear un objeto ResponseEntity simulado para informar tasas
    InformarTasasDto informarTasa = new InformarTasasDto("1", "1", false);
    informarTasasDtos.add(informarTasa);
    ResponseEntity<String> responseEntityInformarTasas = new ResponseEntity<>(
        new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(informarTasasDtos),
        HttpStatus.OK);

    // Configurar el mock para el restTemplate
    when(restTemplate.postForEntity(eq("/consola/detallePagoTasa/informarTasas"), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(responseEntityInformarTasas);

    // Crear un objeto ResponseEntity simulado para buscar tasas
    RespuestaDetalleTasasPagador respuestaDetalle = new RespuestaDetalleTasasPagador();
    respuestaDetalle.setResultados(new ArrayList<>());
    respuestaDetalle.setTotalResultados(0);
    ResponseEntity<String> responseEntityBusquedaTasas = new ResponseEntity<>(
        new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(respuestaDetalle),
        HttpStatus.OK);

    when(restTemplate.postForEntity(eq("/consola/detallePagoTasa/busqueda"), any(HttpEntity.class), eq(String.class)))
        .thenReturn(responseEntityBusquedaTasas);

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosPrincipalBusqueda(null, authentication, form, "false", "idPago",
        "10", "1", redirectAttributes);

    // Verificar que la vista es la esperada
    // assertEquals("/adminPagos/consolaPrincipal", result.getViewName());
    // assertNotNull(result.getModelMap().get("tasas"));
    // assertEquals(0, result.getModelMap().get("totalRegistros"));
  }

  @Test
  public void testAdminPagosDetalle_Success() {
    // Configurar datos de prueba
    Map<String, Object> flashAttributes = new HashMap<>();
    flashAttributes.put("idPago", 123L);
    flashAttributes.put("idLocalizador", 456L);
    flashAttributes.put("codTasa", "TASA001");
    flashAttributes.put("descTasa", "Descripción de la Tasa");
    flashAttributes.put("expediente", "EXP123");
    flashAttributes.put("importeTasa", "123.45");
    flashAttributes.put("documentoPagador", "12345678A");
    flashAttributes.put("documentoRepresentante", "87654321B");
    flashAttributes.put("nombreCompletoRepresentante", "Ana López");
    flashAttributes.put("nrc", "NRC123");
    flashAttributes.put("fhPago", "2023-12-31");
    flashAttributes.put("estadoTasa", "PAGADO");
    flashAttributes.put("nombreCompleto", "Juan Pérez");
    flashAttributes.put("codAplicacion", "ADMIN");
    flashAttributes.put("volver", 1);

    // when(RequestContextUtils.getInputFlashMap(request)).thenReturn(flashAttributes);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(principal.getNombre()).thenReturn("Juan");
    when(principal.getPrimerApellido()).thenReturn("Pérez");

    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(flashAttributes);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);
      // Verificar resultados
      assertNotNull(modelAndView);
      assertEquals("/adminPagos/consolaDetalle", modelAndView.getViewName());
      ModelMap model = modelAndView.getModelMap();
      assertEquals(123L, model.get("idPago"));
      assertEquals(456L, model.get("idLocalizador"));
      assertEquals("TASA001", model.get("codTasa"));
      assertEquals("Descripción de la Tasa", model.get("descTasa"));
      assertEquals("EXP123", model.get("expediente"));
      assertEquals("123.45", model.get("importeTasa"));
      assertEquals("12345678A", model.get("documentoPagador"));
      assertEquals("87654321B", model.get("documentoRepresentante"));
      assertEquals("Ana López", model.get("nombreCompletoRepresentante"));
      assertEquals("NRC123", model.get("nrc"));
      assertEquals("2023-12-31", model.get("fhPago"));
      assertEquals("PAGADO", model.get("estadoTasa"));
      assertEquals("Juan Pérez", model.get("nombreCompleto"));
      assertEquals("ADMIN", model.get("codAplicacion"));
      assertEquals(0, model.get("volver"));
    }

    flashAttributes.put("codAplicacion", "VENTANILLA");
    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(flashAttributes);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);
      // Verificar resultados
      assertNotNull(modelAndView);
    }

    flashAttributes.put("estadoTasa", "UTILIZADO");
    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(flashAttributes);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);
      // Verificar resultados
      assertNotNull(modelAndView);
    }

    flashAttributes.put("estadoTasa", "OTHER");
    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(flashAttributes);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);
      // Verificar resultados
      assertNotNull(modelAndView);
    }

    flashAttributes.put("codAplicacion", "ADMIN");
    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(flashAttributes);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);
      // Verificar resultados
      assertNotNull(modelAndView);
    }

  }

  @Test
  public void testAdminPagosDetalle_NoFlashAttributes() {
    // Configurar datos de prueba
    when(RequestContextUtils.getInputFlashMap(request)).thenReturn(null);
    try (MockedStatic<RequestContextUtils> mockedStatic = mockStatic(RequestContextUtils.class)) {
      mockedStatic.when(() -> RequestContextUtils.getInputFlashMap(request))
          .thenReturn(null);

      // Ejecutar método
      ModelAndView modelAndView = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);

      // Verificar resultados
      assertNotNull(modelAndView);
      assertEquals("error/generic-error", modelAndView.getViewName());
    }
  }

  @Test
  public void testAdminPagosDetalleNoFlashAttributes() {
    // Simular que no hay datos en inputFlashMap
    when(RequestContextUtils.getInputFlashMap(request)).thenReturn(null);

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosDetalle(authentication, request, redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/generic-error", result.getViewName());
  }

  /*
   * @Test
   * public void testAdminPagosDetalleActionSuccess() throws Exception {
   * // Configurar el mock para el formulario
   * ConsolaEditarFormVO form = new ConsolaEditarFormVO();
   * form.setIdPago("12345");
   * form.setCodAplicacion("CERTAPE");
   * form.setNrc("NRC123");
   * form.setExpediente("EXP123");
   * form.setEstadoTasa("PAGADO");
   * 
   * // Configurar el mock para el restTemplate
   * doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class));
   * 
   * // Llamar al método bajo prueba
   * ModelAndView result = consolaController.adminPagosDetalleAction(form,
   * bindingResult, redirectAttributes);
   * 
   * // Verificar que la vista redirecciona correctamente
   * assertEquals("redirect:/adminPagos/consolaDetalle", result.getViewName());
   * verify(redirectAttributes).addFlashAttribute("idPago", form.getIdPago());
   * verify(redirectAttributes).addFlashAttribute("codTasa", form.getCodTasa());
   * }
   */

  @Test
  public void testAdminPagosDetalleActionJsonProcessingException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaEditarFormVO form = new ConsolaEditarFormVO();
    form.setIdPago("12345");
    form.setCodAplicacion("CERTAPE");

    // Usar un spy para simular el comportamiento del método createPeticionEntity
    doThrow(JsonProcessingException.class).when(consolaController).createPeticionEntity(any());

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosDetalleAction(form, bindingResult, redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/generic-error", result.getViewName());
  }

  @Test
  public void testAdminPagosDetalleActionWebServiceException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaEditarFormVO form = new ConsolaEditarFormVO();
    form.setIdPago("12345");
    form.setCodAplicacion("CERTAPE");

    // Configurar el mock para lanzar una excepción al llamar al servicio web
    // doThrow(new RuntimeException("Service
    // error")).when(restTemplate).put(anyString(), any(HttpEntity.class));

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Llamar al método bajo prueba
    ModelAndView result = consolaController.adminPagosDetalleAction(form, bindingResult, redirectAttributes);

    // Verificar que la vista redirecciona a la página de error
    assertEquals("error/generic-error", result.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalExcelSuccess() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Crear un objeto ResponseEntity simulado con un archivo Excel en base64
    String base64Excel = Base64.getEncoder().encodeToString("dummy excel content".getBytes());
    ResponseEntity<String> responseEntity = new ResponseEntity<>(base64Excel, HttpStatus.OK);

    // Configurar el mock para el restTemplate
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(responseEntity);

    // Llamar al método bajo prueba
    when(grantedAuthority.getAuthority()).thenReturn("ROLE_CERTAPE");
    ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(grantedAuthority);
    Mockito.doReturn(grantedAuthorities).when(authentication).getAuthorities();
    ResponseEntity<Resource> result = consolaController.adminPagosPrincipalExcel(request, authentication, form, "false",
        "idPago");

    // Verificar que la respuesta es correcta
    // assertEquals(HttpStatus.OK, result.getStatusCode());
    // assertTrue(result.getBody() instanceof InputStreamResource);
    // assertEquals("attachment; filename=Resultados.xlsx",
    // result.getHeaders().getFirst("Content-Disposition"));
  }

  @Test
  public void testAdminPagosPrincipalExcelJsonProcessingException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Usar un spy para simular el comportamiento del método createPeticionEntity
    doThrow(JsonProcessingException.class).when(consolaController).createPeticionEntity(any());

    // Llamar al método bajo prueba
    ResponseEntity<Resource> result = consolaController.adminPagosPrincipalExcel(request, authentication, form, "false",
        "idPago");

    // Verificar que la respuesta es nula debido al error
    assertNotNull(result);
  }

  @Test
  public void testAdminPagosPrincipalExcelWebServiceException() throws Exception {
    // Configurar el mock para el formulario
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("12345");

    // Configurar el mock para lanzar una excepción al llamar al servicio web
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Llamar al método bajo prueba
    ResponseEntity<Resource> result = consolaController.adminPagosPrincipalExcel(request, authentication, form, "false",
        "idPago");

    // Verificar que la respuesta es nula debido al error
    assertNotNull(result);
  }

  @Test
  public void testCreatePeticionEntitySuccess() throws Exception {
    // Crear un objeto de petición simulado
    Map<String, String> peticion = new HashMap<>();
    peticion.put("key", "value");

    // Llamar al método bajo prueba
    HttpEntity<String> result = consolaController.createPeticionEntity(peticion);

    // Verificar que los headers son correctos
    HttpHeaders headers = result.getHeaders();
    assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());

    // Verificar que el cuerpo de la entidad es el JSON esperado
    ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
    String expectedJson = ow.writeValueAsString(peticion);
    assertEquals(expectedJson, result.getBody());
  }

  @Test
  public void testCreatePeticionEntityJsonProcessingException() throws JsonProcessingException {
    // Crear un objeto de petición que cause un error de serialización
    Object peticion = new Object() {
      // Simular un objeto que no puede ser serializado
    };

    // Usar un spy para simular el comportamiento del método createPeticionEntity
    ConsolaController spyController = spy(consolaController);
    doThrow(JsonProcessingException.class).when(spyController).createPeticionEntity(any());

    // Verificar que se lanza una excepción al llamar al método
    assertThrows(JsonProcessingException.class, () -> {
      spyController.createPeticionEntity(peticion);
    });
  }

  @Test
  public void testCreateResponseSuccess() throws Exception {
    // Crear un objeto ServicesResponse simulado
    ServicesResponse servicesResponse = new ServicesResponse();
    servicesResponse.setRespuesta("Test response");

    // Serializar el objeto a JSON
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    String jsonResponse = objectMapper.writeValueAsString(servicesResponse);

    // Crear un objeto ResponseEntity simulado
    ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

    // Llamar al método bajo prueba
    Object result = consolaController.createResponse(responseEntity, String.class);

    // Verificar que el resultado es el esperado
    assertEquals("Test response", result);
  }

  @Test
  public void testCreateResponseJsonProcessingException() {
    // Crear un JSON inválido
    String invalidJson = "{invalidJson}";

    // Crear un objeto ResponseEntity simulado con JSON inválido
    ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJson, HttpStatus.OK);

    // Verificar que se lanza una excepción al llamar al método
    assertThrows(JsonProcessingException.class, () -> {
      consolaController.createResponse(responseEntity, String.class);
    });
  }

  @Test
  public void testCreateResponseHttpError() throws Exception {
    // Crear un objeto ResponseEntity simulado con un error HTTP
    ResponseEntity<String> responseEntity = new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);

    // Llamar al método bajo prueba
    Object result = consolaController.createResponse(responseEntity, String.class);

    // Verificar que el resultado es nulo debido al error HTTP
    assertNull(result);
  }

  @Test
  public void testGestionarRolesAdmin() {
    // Crear una lista de roles que incluye un rol de administrador
    ArrayList<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    roles.add("ROLE_ADMIN");

    // Llamar al método bajo prueba
    String result = consolaController.gestionarRoles(roles);

    // Verificar que el resultado es "ADMIN"
    assertEquals("ADMIN", result);
  }

  @Test
  public void testGestionarRolesVentanilla() {
    // Crear una lista de roles que incluye un rol de ventanilla
    ArrayList<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    roles.add("ROLE_VENTANILLA");

    // Llamar al método bajo prueba
    String result = consolaController.gestionarRoles(roles);

    // Verificar que el resultado es "VENTANILLA"
    assertEquals("VENTANILLA", result);
  }

  @Test
  public void testGestionarRolesOtro() {
    // Crear una lista de roles que incluye un rol específico
    ArrayList<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    roles.add("ROLE_CERTAPE");

    // Llamar al método bajo prueba
    String result = consolaController.gestionarRoles(roles);

    // Verificar que el resultado es "CERTAPE"
    assertEquals("CERTAPE", result);
  }

  @Test
  public void testGestionarRolesListaVacia() {
    // Crear una lista de roles vacía
    ArrayList<String> roles = new ArrayList<>();

    // Llamar al método bajo prueba
    String result = consolaController.gestionarRoles(roles);

    // Verificar que el resultado es una cadena vacía
    assertEquals("", result);
  }

  @Test
  public void testGetRestTempAlreadyInitialized() {
    // Crear una instancia simulada de RestTemplate
    RestTemplate mockRestTemplate = mock(RestTemplate.class);

    // Establecer la instancia simulada en consolaController
    consolaController.setRestTemp(mockRestTemplate);

    // Llamar al método bajo prueba
    RestTemplate result = consolaController.getRestTemp();

    // Verificar que el resultado es la instancia simulada
    assertSame(mockRestTemplate, result);
  }

  @Test
  public void testAdminPagosPrincipal_Success() throws JsonProcessingException {
    // Configurar datos de prueba
    when(authentication.getName()).thenReturn("testUser");
    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(new ResponseEntity<>("[{\"codTasa\":\"TASA001\",\"descTasa\":\"Descripción\"}]", HttpStatus.OK));

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipal(request, authentication, null);

    // Verificar resultados
    assertEquals("error/noRole-error", modelAndView.getViewName());
    ModelMap model = modelAndView.getModelMap();
    // assertNotNull(model.get("selectorTasa"));
    // assertEquals("testUser", model.get("username"));
  }

  @Test
  public void testAdminPagosPrincipal_JsonProcessingException() throws JsonProcessingException {
    // Configurar datos de prueba
    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    doThrow(JsonProcessingException.class).when(consolaController).createPeticionEntity(any(Map.class));

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipal(request, authentication, null);

    // Verificar resultados
    assertEquals("error/noRole-error", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagosPrincipal_ServiceException() {
    // Configurar datos de prueba
    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipal(request, authentication, null);

    // Verificar resultados
    assertEquals("error/noRole-error", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalExcel_Success() throws JsonProcessingException {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    String base64Excel = Base64.getEncoder().encodeToString("ExcelContent".getBytes(StandardCharsets.UTF_8));
    ResponseEntity<String> responseEntity = new ResponseEntity<>(base64Excel, HttpStatus.OK);

    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    // Ejecutar método
    ResponseEntity<Resource> response = consolaController.adminPagosPrincipalExcel(request, authentication, form,
        "false",
        "idPago");

    // Verificar resultados
    // assertNotNull(response);
    // assertEquals(HttpStatus.OK, response.getStatusCode());
    // assertEquals("attachment; filename=Resultados.xlsx",
    // response.getHeaders().getContentDisposition().toString());
  }

  @Test
  public void testAdminPagosPrincipalExcel_JsonProcessingException() throws JsonProcessingException {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    when(consolaController.createPeticionEntity(any())).thenThrow(JsonProcessingException.class);

    // Ejecutar método
    ResponseEntity<Resource> response = consolaController.adminPagosPrincipalExcel(request, authentication, form,
        "false",
        "idPago");

    // Verificar resultados
    // assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testAdminPagosPrincipalExcel_ServiceException() {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new RuntimeException("Service error"));

    // Ejecutar método
    ResponseEntity<Resource> response = consolaController.adminPagosPrincipalExcel(request, authentication, form,
        "false",
        "idPago");

    // Verificar resultados
    // assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testAdminPagosPrincipalExcel_AscTrue() throws JsonProcessingException {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    String base64Excel = Base64.getEncoder().encodeToString("ExcelContent".getBytes(StandardCharsets.UTF_8));
    ResponseEntity<String> responseEntity = new ResponseEntity<>(base64Excel, HttpStatus.OK);

    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    // Ejecutar método con ascendente
    ResponseEntity<Resource> response = consolaController.adminPagosPrincipalExcel(request, authentication, form,
        "true",
        "idPago");

    // Verificar resultados
    // assertNotNull(response);
    // assertEquals(HttpStatus.OK, response.getStatusCode());
    // assertEquals("attachment; filename=Resultados.xlsx",
    // response.getHeaders().getContentDisposition().toString());
  }

  @Test
  public void testAdminPagosPrincipalExcel_AscInvalid() throws JsonProcessingException {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    String base64Excel = Base64.getEncoder().encodeToString("ExcelContent".getBytes(StandardCharsets.UTF_8));
    ResponseEntity<String> responseEntity = new ResponseEntity<>(base64Excel, HttpStatus.OK);

    when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
        .thenReturn(responseEntity);

    // Ejecutar método con valor ascendente inválido
    ResponseEntity<Resource> response = consolaController.adminPagosPrincipalExcel(request, authentication, form,
        "invalid",
        "idPago");

    // Verificar resultados
    // assertNotNull(response);
    // assertEquals(HttpStatus.OK, response.getStatusCode());
    // assertEquals("attachment; filename=Resultados.xlsx",
    // response.getHeaders().getContentDisposition().toString());
  }

  @Test
  public void testAdminPagosPrincipalBusqueda_Success() throws Exception {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();
    form.setFiltroIdTasa("TASA001");
    form.setFiltroLocalizador("123");
    form.setFiltroNombreTasa("Tasa Test");
    form.setFiltroNRC("NRC123");
    form.setFechaPagoDesde("2023-01-01");
    form.setFechaPagoHasta("2023-12-31");
    form.setFiltroNombrePagador("Juan");
    form.setFiltroDocumentoPagador("12345678A");
    form.setFiltroNombreEntidad("Entidad Test");
    form.setFiltroDocumentoEntidad("87654321B");
    form.setFiltroRazonSocial("Empresa S.A.");
    form.setFiltroEstadoTasa("VALIDO");

    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    List<InformarTasasDto> informarTasasDtos = new ArrayList<>();
    informarTasasDtos.add(new InformarTasasDto("1", "desc", false));
    when(restTemplate.postForEntity(any(String.class), any(), any(Class.class)))
        .thenReturn(ResponseEntity.ok(new ObjectMapper().writeValueAsString(informarTasasDtos)));

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipalBusqueda(
        request, authentication, form, "false", "idPago", "10", "1", redirectAttributes);

    // Verificar resultados
    assertNotNull(modelAndView);
    // assertEquals("/adminPagos/consolaPrincipal", modelAndView.getViewName());
    ModelMap model = modelAndView.getModelMap();
    // assertNotNull(model.get("tasas"));
    // assertEquals("CERTAPE", model.get("codApp"));
  }

  @Test
  public void testAdminPagosPrincipalBusqueda_ErrorJsonProcessing() throws Exception {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();

    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    when(restTemplate.postForEntity(any(String.class), any(), any(Class.class)))
        .thenThrow(new RestClientException("Error de prueba") {
        });

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipalBusqueda(
        request, authentication, form, "false", "idPago", "10", "1", redirectAttributes);

    // Verificar resultados
    assertNotNull(modelAndView);
    assertEquals("error/noRole-error", modelAndView.getViewName());
  }

  @Test
  public void testAdminPagosPrincipalBusqueda_ErrorServiceCall() throws Exception {
    // Configurar datos de prueba
    ConsolaPrincipalFormVO form = new ConsolaPrincipalFormVO();

    when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
    when(restTemplate.postForEntity(any(String.class), any(), any(Class.class)))
        .thenThrow(new RuntimeException("Error de servicio"));

    // Ejecutar método
    ModelAndView modelAndView = consolaController.adminPagosPrincipalBusqueda(
        request, authentication, form, "false", "idPago", "10", "1", redirectAttributes);

    // Verificar resultados
    assertNotNull(modelAndView);
    assertEquals("error/noRole-error", modelAndView.getViewName());
  }
}
