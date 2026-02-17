package es.mjusticia.sinac.core.batch.sede;

/*-
 * #%L
 * sinac-core
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.batch.SinacJob;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.JobFacade;
import es.mjusticia.sinac.core.business.facade.SolicitudesFacade;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudesPersonasDto;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.filiaciones.service.impl.FiliacionesServiceImpl;

/**
 * Job para realizar el Alta de un expediente de SEDE
 */
@Component
public class SinacJobAltaExpedientesSede extends SinacJob<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobAltaExpedientesSede.class);

  @Value(value = "${es.mjusticia.filiaciones.codOrganismo}")
  private String codOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.nombreOrganismo}")
  private String nombreOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.identificacionPuesto}")
  private String identificacionPuesto;

  @Value(value = "${es.mjusticia.filiaciones.usuario}")
  private String usuario;

  @Value("${sinac.quartz.sinacJobAltaExpedientesSede.nfs.documentosSede}")
  private String nfsDocsSede;

  @Value("${sinac.configpath}")
  private String nfsEnvironmentPath;

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private SolicitudesFacade solicitudesFacade;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private CatalogosService catalogoService;

  @Autowired
  private SolicitudesService solicitudesService;

  @Autowired
  private JobFacade jobFacade;

  public SinacJobAltaExpedientesSede() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<Object> recuperarItems(Map<String, Object> contextData) {

    LOG.debug("SinacJobAltaExpedientesSede.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<String> listaEstadoExp = Arrays.asList("ECIN");
    List<SolicitudDto> listaSol = solicitudesFacade.getListaSolicitudesSede();
    List<ExpedienteDto> listaExp = expedientesFacade.getListaExpedientesIncompletos(listaEstadoExp);

    List<Object> listaObjetos = new ArrayList<>();
    listaObjetos.addAll(listaSol);
    listaObjetos.addAll(listaExp);

    LOG.info("SinacJobAltaExpedientesSede.recuperarItems - End");
    return listaObjetos;
  }

  @Override
  public void procesarItem(Object item, Map<String, Object> contextData) throws SinacException {

    if (item instanceof SolicitudDto) {

      ExpedienteDto exp = null;
      List<ExpedienteDto> listaExpAcumular = new ArrayList<>();
      List<String> listaEstados = Arrays.asList("EXPC", "DRNT", "ARCE");
      SolicitudDto solicitudDto = ((SolicitudDto) item);
      LinkedList<DocumentoToSaveDto> documentosToSave = new LinkedList<>();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(solicitudDto.getFechaCreacion());

      documentosToSave = expedientesFacade.obtenerTodosLosDocumentosSede(solicitudDto.getSolicitudDocumentoDtos());

      for (SolicitudesPersonasDto solPerDto : solicitudDto.getSolicitudesPersonasDtos()) {
        for (PersonaIdentificaDto perIden : solPerDto.getPersonaDto().getPersonasIdentificaDtos()) {
          if (Boolean.TRUE.equals(perIden.isFlgActivo()) && perIden.getFlgPrincipal()
              && solPerDto.getLdvMaestraDto().getCodLdvMae().equals("PER-INT")) {
            LOG.info(
                "SinacJobAltaExpedientes Acumulacion: se comprueba si el interesado con NIE {} e idPer {} tiene expedientes ya creados",
                perIden.getNumAcreditacion(), solPerDto.getPersonaDto().getIdPer());
            listaExpAcumular.addAll(expedientesFacade.getExpedienteAcumularPorIdPer(
                solPerDto.getPersonaDto().getIdPer(), solicitudDto.getProcedimientoDto().getCodCorto(), listaEstados));
          }
        }
      }
      LOG.info("SinacJobAltaExpedientes Acumulacion: la lista de expedientes a acumular tiene {} elementos",
          listaExpAcumular != null ? listaExpAcumular.size() : "SIN EXPEDIENTES");
      if (!listaExpAcumular.isEmpty()) {
        if (listaExpAcumular.size() > 1) {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_127)
              .logMessageParams(listaExpAcumular.size()).type(SinacExceptionType.DATA);
        } else {
          expedientesFacade.saveDocumentosEntradaExpediente(listaExpAcumular.get(0).getIdExp(), documentosToSave);
          solicitudDto.setLdvMaestraDtoByIdEstSolLdv(
              catalogoService.getCatalogoByCod(Constantes.EstadosSolicitud.ESTADO_SOLICITUD_ACUMULADO));
          solicitudesService.saveSolicitud(solicitudDto);
        }
      } else {
        if (item instanceof SolicitudDto) {
          try {
            LOG.info("SinacJobAltaExpedientesSede.procesarItemSolicitud - Init");
            contextData.put("idSol", solicitudDto.getIdSol());

            ProcedimientoDto procedimiento = solicitudDto.getProcedimientoDto();
            contextData.put("idPro", procedimiento.getIdPro());
            Long idProFaseTraOpeAcc = expedientesFacade
                .getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(procedimiento.getCodPro(), "INI",
                    "SOL", "CRES");
            contextData.put("pftoa", idProFaseTraOpeAcc);
            expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);

            LOG.info("SinacJobAltaExpedientesSede.procesarItemSolicitud - End");
          } catch (

          Exception e) {
            addError();
            guardaErrorJob(e, descripcion, jobName);
          }
        }
      }
    }

    if (item instanceof ExpedienteDto) {
      ExpedienteDto expediente = ((ExpedienteDto) item);
      try {
        LOG.info("SinacJobAltaExpedientesSede.procesarItemExpediente - Init");
        contextData.put("idExp", expediente.getIdExp());
        contextData.put("expediente", expediente);
        contextData.put("idSol", expediente.getSolicitudDto().getIdSol());
        contextData.put("solicitud", expediente.getSolicitudDto());
        contextData.put("docsExp", expediente.getExpedienteDocumentoDtos());

        ProcedimientoDto procedimiento = expediente.getProcedimientoDto();
        contextData.put("idPro", procedimiento.getIdPro());
        Long idProFaseTraOpeAcc = expedientesFacade
            .getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(procedimiento.getCodPro(), "INI",
                "SOL", "COME");
        contextData.put("pftoa", idProFaseTraOpeAcc);
        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);

        LOG.info("SinacJobAltaExpedientesSede.procesarItemExpediente - End");
      } catch (

      Exception e) {
        addError();
        guardaErrorJob(e, descripcion, jobName);
      }
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_SEDE_ALTA_EXPEDIENTES";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(FiliacionesServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
