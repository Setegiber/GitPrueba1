package es.mjusticia.sinac.core.batch;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
/*-
 * #%L
 * sinac-core
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
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import com.sshtools.common.logger.Log;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.InformesDgpRecibidosDto;
import es.mjusticia.sinac.core.model.enums.ProcedimientoDgpEnum;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.dgp.dto.RespuestaListadoDgpDto;
import es.mjusticia.sinac.dgp.dto.TitularDto;
import es.mjusticia.sinac.dgp.exception.SinacDgpException;
import es.mjusticia.sinac.dgp.service.InformeDgpService;
import es.mjusticia.sinac.dgp.service.impl.InformeDgpServiceImpl;
import ws_nac_envio_informe_naciona.com.adexttra.Respuesta;
import ws_nac_envio_informe_naciona.com.adexttra.Titular;
import ws_nac_listado_informes_naciona.com.adexttra.Aplicacion;
import ws_nac_listado_informes_naciona.com.adexttra.Organizacion;
import ws_nac_listado_informes_naciona.com.adexttra.Peticion;
import ws_nac_listado_informes_naciona.com.adexttra.SolicitanteDatos;

/**
 * Job para solicitar a la Dgp los informes en estado pendiente
 */
@Component
public class SinacJobDgpRecibir extends SinacJob<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobDgpRecibir.class);

  private static final int POSICION_DIEZ = 10;

  @Value(value = "${sinac.quartz.sinacJobDgp.codOrganizacion}")
  private String codOrganizacion;

  @Value(value = "${es.mjusticia.sinac.nombreOrganizacion}")
  private String nombreOrganizacion;

  @Value(value = "${sinac.quartz.sinacJobDgp.codAplicacion}")
  private String codAplicacion;

  @Value(value = "${sinac.quartz.sinacJobDgp.nombreAplicacion}")
  private String nombreAplicacion;

  @Value(value = "${sinac.quartz.sinacJobDgp.idFuncionario}")
  private String idFuncionario;

  @Value(value = "${sinac.quartz.sinacJobDgp.numFuncionario}")
  private String numFuncionario;

  @Value(value = "${sinac.quartz.sinacJobDgp.ape1Funcionario}")
  private String ape1Funcionario;

  @Value(value = "${sinac.quartz.sinacJobDgp.ape2Funcionario}")
  private String ape2Funcionario;

  @Value(value = "${sinac.quartz.sinacJobDgp.codigoCertificado}")
  private String codigoCertificado;

  @Value(value = "${sinac.quartz.sinacJobDgp.recibir.finalidad.listado}")
  private String finalidadListado;

  @Value(value = "${sinac.quartz.sinacJobDgp.recibir.finalidad.remision}")
  private String finalidadRemision;

  @Value(value = "${sinac.quartz.sinacJobDgp.tipo}")
  private String tipo;

  @Value(value = "${sinac.quartz.sinacJobDgp.telFuncionario}")
  private String telFuncionario;

  @Value(value = "${sinac.quartz.sinacJobDgp.nombreFuncionario}")
  private String nombreFuncionario;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private InformeDgpService informeDgpService;

  @Autowired
  private UsuariosService usuariosService;

  private String descripcion;

  private String jobName;

  /**
   * Constructor por defecto
   */
  public SinacJobDgpRecibir() {
    super();
  }

  @Override
  public List<Object> recuperarItems(Map<String, Object> contextData) {
    RespuestaListadoDgpDto respuestaListadoDgpDto;
    LOG.info("SinacJobDgpRecibir.recuperarItems - Init");
    final int POSICION_OCHO = 8;
    final int POSICION_DOCE = 12;
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<Object> titularesDto = getTitularesNoPro(contextData);
    contextData.put("stopNoPro", true);
    if (!titularesDto.isEmpty()) {
      return titularesDto;
    } else {
      Peticion peticion = new Peticion();
      cargarInfoPeticion(peticion);
      titularesDto = new ArrayList<>();
      List<InformesDgpRecibidosDto> informesNoProcesados = expedientesFacade.getAllInformesDgpRecibidosNoProcesados();
      for (InformesDgpRecibidosDto informe : informesNoProcesados) {
        TitularDto dto = getTitular(informe.getNumExp(), informe.getTipoPeticion(), informe.getFechaAlta());
        titularesDto.add(dto);
      }
      try {
        respuestaListadoDgpDto = informeDgpService.peticionListadoDgp(peticion);
        if (respuestaListadoDgpDto != null && respuestaListadoDgpDto.getEstado().getCodigoEstado().equals("00")) {
          int totalExpedientes = respuestaListadoDgpDto.getTitular().getNumExpediente().length() / POSICION_DOCE;
          int elementoExpediente = 1;
          int caracteresNumExpediente = POSICION_DOCE;
          int caracteresTipoPeticion = 1;
          int caracteresFechaAlta = POSICION_OCHO;
          while (elementoExpediente <= totalExpedientes) {

            String numExp = getCaracteresElemento(respuestaListadoDgpDto.getTitular().getNumExpediente(),
                elementoExpediente, caracteresNumExpediente);
            String tipoPeticion = getCaracteresElemento(respuestaListadoDgpDto.getTitular().getTipoPeticion(),
                elementoExpediente, caracteresTipoPeticion);
            String fechaAlta = getCaracteresElemento(respuestaListadoDgpDto.getTitular().getFechaAlta(),
                elementoExpediente, caracteresFechaAlta);
            elementoExpediente++;
            try {
              expedientesFacade.saveInformeDgpRecibido(numExp, tipoPeticion, fechaAlta, "EST-NO-PROCESS");
              titularesDto.add(getTitular(numExp, tipoPeticion, fechaAlta));
            } catch (SinacException e) {
              LOG.info("Se ha recibido un informe que ya existía en bd con número de expediente {} y fecha de alta {}.",
                  numExp, fechaAlta);
            }
          }
        } else if (respuestaListadoDgpDto != null && respuestaListadoDgpDto.getEstado().getCodigoEstadoSec() == null
            && respuestaListadoDgpDto.getEstado().getLiteralError().equals("No figuran expedientes")) {
          return titularesDto;
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_103).logMessageParams(respuestaListadoDgpDto);
        }
      } catch (SinacException | SinacDgpException | IOException e) {
        contextData.put("errorJob", e.getMessage());
        guardaErrorJob(e, descripcion, jobName);
      }
      LOG.info("SinacJobDgpRecibir.recuperarItems - End");
      return titularesDto;
    }
  }

  /**
   * Devuelve una lista de titulares en funcion del los datos de contexto Se usa
   * solo para lanzar el job desde la pantalla de procesos porque en entornos que
   * no sean PRO el servicio de DGP no devuelve datos
   * 
   * @param contextData
   * @return
   */
  private List<Object> getTitularesNoPro(Map<String, Object> contextData) {
    String codExp = (String) contextData.get("codExp");
    String tipoRespDgp = (String) contextData.get("tiporespdgp");
    if (planificacionParametrizada(codExp, tipoRespDgp) && contextData.get("stopNoPro") == null) {
      TitularDto titularDto = null;
      switch (tipoRespDgp) {
      case "1": {
        titularDto = getTitular("202400014201", "N", "20241120");
        break;
      }
      case "2": {
        titularDto = getTitular("202400004421", "D", "20241120");
        break;
      }
      case "3": {
        titularDto = getTitular("202400012901", "N", "20241120");
        break;
      }
      case "4": {
        titularDto = getTitular("202300050110", "R", "20230405");
        break;
      }
      case "5": {
        titularDto = getTitular("202300050110", "R", "20230405");
        break;
      }

      default:
        return Collections.emptyList();
      }
      List<Object> titularesDto = new ArrayList<>();
      titularesDto.add(titularDto);
      return titularesDto;
    }
    return Collections.emptyList();
  }

  private boolean planificacionParametrizada(String codExp, String tipoRespDgp) {
    return codExp != null && !codExp.isBlank() && tipoRespDgp != null && !tipoRespDgp.isBlank();
  }

  private TitularDto getTitular(String numExpediente, String tipoPeticion, String fechaAlta) {
    TitularDto titularDto = new TitularDto();
    titularDto.setNumExpediente(numExpediente);
    titularDto.setTipoPeticion(tipoPeticion);
    titularDto.setFechaAlta(fechaAlta);
    return titularDto;
  }

  private String getCaracteresElemento(String elemento, int elementoExpediente, int caracteresNumExpediente) {
    return elemento.substring(caracteresNumExpediente * elementoExpediente - caracteresNumExpediente,
        caracteresNumExpediente * elementoExpediente);
  }

  private void cargarInfoPeticion(Peticion peticion) {
    peticion.setCodigoCertificado(codigoCertificado);
    peticion.setCodigoPeticion(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    peticion.setFinalidad(finalidadListado);
    SolicitanteDatos solicitanteDatos = new SolicitanteDatos();
    solicitanteDatos.setApe1Funcionario(ape1Funcionario);
    solicitanteDatos.setApe2Funcionario(ape2Funcionario);
    Aplicacion aplicacion = new Aplicacion();
    aplicacion.setCodAplicacion(codAplicacion);
    aplicacion.setNombreAplicacion(nombreAplicacion);
    solicitanteDatos.setAplicacion(aplicacion);
    solicitanteDatos.setIdFuncionario(idFuncionario);
    solicitanteDatos.setNombreFuncionario(nombreFuncionario);
    Organizacion organizacion = new Organizacion();
    organizacion.setCodOrganizacion(codOrganizacion);
    organizacion.setNombreOrganizacion(nombreOrganizacion);
    solicitanteDatos.setOrganizacion(organizacion);
    solicitanteDatos.setTelFuncionario(telFuncionario);
    solicitanteDatos.setTipo(tipo);
    peticion.setSolicitanteDatos(solicitanteDatos);
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_DGP_REC";
  }

  @Override
  public void procesarItem(Object item, Map<String, Object> contextData) {
    LOG.info("SinacJobDgpRecibir.procesarItem - Init");
    LOG.info("Item: {}", item);
    final int POSICION_CUATRO = 4;

    Map<String, Object> valores = new HashMap<>();
    InformesDgpRecibidosDto informeDto = null;
    Boolean planificacionParametrizada = false;
    String codExpedienteSinac = "";
    try {
      TitularDto titular = (TitularDto) item;
      String anio = titular.getNumExpediente().substring(0, POSICION_CUATRO);
      String nExpediente = titular.getNumExpediente().substring(POSICION_CUATRO, POSICION_DIEZ);
      String codProcedimiento = "";
      if (titular.getTipoPeticion() != null) {
        codProcedimiento = ProcedimientoDgpEnum.valueOf(titular.getTipoPeticion()).getCodProSinac();
      }
      String codExp = (String) contextData.get("codExp");
      String tipoRespDgp = (String) contextData.get("tiporespdgp");
      planificacionParametrizada = planificacionParametrizada(codExp, tipoRespDgp);
      try {
        informeDto = expedientesFacade.findByNumExpAndFechaAlta(titular.getNumExpediente(), titular.getFechaAlta());
      } catch (SinacException se) {
        if (Boolean.FALSE.equals(planificacionParametrizada))
          throw se;
      }
      if (Boolean.TRUE.equals(planificacionParametrizada)) {
        codExpedienteSinac = codExp;
      } else {
        codExpedienteSinac = codProcedimiento + nExpediente + "/" + anio;
      }
      valores.putAll(expedientesFacade.getIdExpCodProceByCodExpediente(codExpedienteSinac,
          Constantes.TiposInforme.TIPO_INFORME_DGP));
      valores.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());
      valores.put("flgProceso", true);
      var peticionRemision = new ws_nac_envio_informe_naciona.com.adexttra.Peticion();
      cargarInfoPeticionRemision(titular, peticionRemision);
      Long idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
          valores.get("codProcedimiento").toString(), "INF", "IDGP", "RDGP");
      Respuesta respuestaRemision = informeDgpService.peticionRemisionDgp(peticionRemision);
      try {
        if (Boolean.TRUE.equals(planificacionParametrizada) && tipoRespDgp.equals("5")) {
          respuestaRemision.setCodigoPeticionRespuesta("99");
          respuestaRemision.getEstado().setCodigoEstado("99");
        }
      } catch (Exception e) {
        Log.error("Se ha producido un error seteando el código de la petición para el caso {}", tipoRespDgp);
      }

      valores.put("respuestaRemision", respuestaRemision);
      if ((informeDto != null && !informeDto.getEstadoDto().getCodLdvMae().equals("EST-PROCESS"))
          || Boolean.TRUE.equals(planificacionParametrizada)) {
        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, valores);
        if ((Boolean.TRUE.equals(planificacionParametrizada) && informeDto != null)
        		|| Boolean.FALSE.equals(planificacionParametrizada))
          expedientesFacade.updateInformeDgpRecibidoEntity(informeDto, "EST-PROCESS");
      }

    } catch (SinacException | SQLException | ParserConfigurationException | SAXException | IOException
        | SinacDgpException e) {
      try {
        if ((Boolean.TRUE.equals(planificacionParametrizada) && informeDto != null)
            || Boolean.FALSE.equals(planificacionParametrizada))
          expedientesFacade.updateInformeDgpRecibidoEntity(informeDto, "EST-ERR");
        else
          LOG.warn(
              "No se puede actualizar el control de la tabla EXP_INF_DGP_RECIBIDOS para el informe DGP del expediente {} porque no hay registro en la tabla. Venimos de mock: {}",
              codExpedienteSinac, planificacionParametrizada);
        addError();
        contextData.put("errorJob", e.getMessage());
        guardaErrorJob(e, descripcion, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }
    LOG.info("SinacJobDgpRecibir.procesarItem - End");
  }

  private void cargarInfoPeticionRemision(TitularDto titular,
      ws_nac_envio_informe_naciona.com.adexttra.Peticion peticionRemision) {
    final int POSICION_CINCO = 5;
    Titular titularRemision = new Titular();
    titularRemision.setFechaAltaPeti(titular.getFechaAlta());
    titularRemision.setNumeroExpedientePeti(titular.getNumExpediente());
    titularRemision.setTipoPeticionPeti(titular.getTipoPeticion());
    peticionRemision.setTitular(titularRemision);
    peticionRemision.setCodigoCertificado(codigoCertificado);
    peticionRemision.setCodigoPeticion(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
        + titular.getNumExpediente().substring(POSICION_CINCO, POSICION_DIEZ));
    peticionRemision.setFinalidad(finalidadRemision);
    var solicitanteDatos = new ws_nac_envio_informe_naciona.com.adexttra.SolicitanteDatos();
    solicitanteDatos.setApe1Funcionario(ape1Funcionario);
    solicitanteDatos.setApe2Funcionario(ape2Funcionario);
    var aplicacion = new ws_nac_envio_informe_naciona.com.adexttra.Aplicacion();
    aplicacion.setCodAplicacion(codAplicacion);
    aplicacion.setNombreAplicacion(nombreAplicacion);
    solicitanteDatos.setAplicacion(aplicacion);
    solicitanteDatos.setIdFuncionario(idFuncionario);
    solicitanteDatos.setNombreFuncionario(nombreFuncionario);
    solicitanteDatos.setNumFuncionario(numFuncionario);
    var organizacion = new ws_nac_envio_informe_naciona.com.adexttra.Organizacion();
    organizacion.setCodOrganizacion(codOrganizacion);
    organizacion.setNombreOrganizacion(nombreOrganizacion);
    solicitanteDatos.setOrganizacion(organizacion);
    solicitanteDatos.setTelFuncionario(telFuncionario);
    solicitanteDatos.setTipo(tipo);
    peticionRemision.setSolicitanteDatos(solicitanteDatos);
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(InformeDgpServiceImpl.class.getName());
    listaNombresClaseGuardarError.add(ExpedientesFacadeImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }
}
