package es.mjusticia.sinac.core.batch;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.MotorTramitacionComponent;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.MaquinaEstadosDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosDocumentosTipoDto;

/**
 * Job para solicitar todos los informes de expedientes que tengan el estado
 * informes pendientes de solicitar.
 */
@Component
public class SinacJobNotificarDocumentos extends SinacJob<ExpedienteDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobNotificarDocumentos.class);

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private MotorTramitacionComponent motorTramitacionComponent;

  @Autowired
  private PlazosService plazosService;

  @Autowired
  private DocumentosService documentosService;

  private String descripcion;
  private String jobName;
  
  public SinacJobNotificarDocumentos() {
    super();
  }

  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {

    List<ParametrizacionDto> listaParamDias = expedientesService.getParametrizacionByNombre("DIAS_DESDE_FIRMA");
    List<ExpedienteDto> listaExpedientes = new ArrayList<>();
    List<String> listaEstadoDoc = Arrays.asList("EDOC-FIR");
    List<ParametrizacionDto> listaParam = expedientesService.getParametrizacionByNombre("TRAMITE_NOTIFICACION_AUTO");
    Date fechaActual = new Date();

    for (ParametrizacionDto parametrizacionDiaDto : listaParamDias) {
      Date fechaFirma = plazosService.getDateByDaysToTakeOff(fechaActual,
          Integer.parseInt(parametrizacionDiaDto.getValor()));

      for (ParametrizacionDto parametrizacionDto : listaParam) {
        listaExpedientes.addAll(expedientesService.getListaExpDocumentosNotificar(listaEstadoDoc,
            Arrays.asList(parametrizacionDto.getValor().split(",")), parametrizacionDto.getProcedimiento().getIdPro(),
            fechaFirma));
      }
    }

    LOG.info("Se van a procesar {} items", listaExpedientes.size());
    return listaExpedientes;
  }

  @Override
  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {
    LOG.info("Notificación automática de documentos: se va a procesar el expediente {}", item.getIdExp());
    contextData.put("idPro", item.getProcedimientoDto().getIdPro());
    contextData.put("idExp", item.getIdExp());
    contextData.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());

    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");

    List<MaquinaEstadosDto> listaAccionesDisponibles = motorTramitacionComponent
        .getListaAccionesDisponiblesPorIdExp(item.getIdExp(), null, item.getProcedimientoDto().getIdPro());

    // RECORREMOS PRIMERO LA LISTA DE DOCUMENTOS PARA QUE EN CASO DE QUE UNO SE HAYA
    // GENERADO DESPUES DE LA FIRMA DE UN DOCUMENTO, PONER EL FLG ACTIVO A 0
    Map<String, Map<Long, List<ExpedienteDocumentoDto>>> mapaDocumentosPorTramite = new HashMap<>();
    for (ExpedienteDocumentoDto expDoc : item.getExpedienteDocumentoDtos()) {
      LOG.info("Notificación automática de documentos: revisando documento {}", expDoc.getIdExpDoc());
      String tramite = null;
      Long pftoa = null;
      for (MaquinaEstadosDto maquinaEstadosDto : listaAccionesDisponibles) {
        // TODO: REVISAR PROCEDIMIENTO IGUAL AL PRO EXPEDIENTE
        if ("NDOC".equals(maquinaEstadosDto.getAccion().getAccionDto().getCodAccion())) {
          for (ProcedimientosDocumentosTipoDto proDoc : expDoc.getDocumentoTipoDto()
              .getProcedimientosDocumentosTipoDtos()) {
            for (PlantillaDto plantilla : proDoc.getPlantillaDtos()) {

              if (maquinaEstadosDto.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto() != null
                  && plantilla.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto() != null
                  && maquinaEstadosDto.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto().getCodTramite()
                      .equals(
                          plantilla.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto().getCodTramite())) {
                tramite = maquinaEstadosDto.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto()
                    .getCodTramite();
                pftoa = maquinaEstadosDto.getAccion().getIdProFaseTraOpeAcc();
                LOG.info("Notificación automática de documentos: Documento {} OK, trámite: {} y pftoa {}",
                    expDoc.getIdExpDoc(), tramite, pftoa);
              }
            }
          }
        }
      }
      List<ExpedienteDocumentoDto> listaDocs = new ArrayList<>();

      Map<Long, List<ExpedienteDocumentoDto>> mapaDocumentosPorPftoa = mapaDocumentosPorTramite.get(tramite);

      if (mapaDocumentosPorPftoa != null) {

        listaDocs = mapaDocumentosPorPftoa.get(pftoa);
        if (listaDocs == null)
          listaDocs = new ArrayList<>();
      } else {
        mapaDocumentosPorPftoa = new HashMap<>();
      }
      boolean hayGeneradoPostFirma = false;
      if (tramite != null) {
        if (tramite.equals("RES")) {

          // TODO CONSULTAR SI HAY DOCUMENTOS GENERADOS DESPUES DE LA FECHA DE FIRMA
          hayGeneradoPostFirma = documentosService.getListDocsGeneradosPostFirma(item.getIdExp(),
              item.getProcedimientoDto().getIdPro(), tramite,
              expDoc.getExpedienteFirmaDtos().get(0).getFechaRecepcion());
        }
        if (!hayGeneradoPostFirma) {
          listaDocs.add(expDoc);
          mapaDocumentosPorPftoa.put(pftoa, listaDocs);
          mapaDocumentosPorTramite.put(tramite, mapaDocumentosPorPftoa);
        }
      }
    }
    for (Map<Long, List<ExpedienteDocumentoDto>> mapaDocumentosPorPftoa : mapaDocumentosPorTramite.values()) {
      for (Entry<Long, List<ExpedienteDocumentoDto>> pftoa : mapaDocumentosPorPftoa.entrySet()) {
        for (ExpedienteDocumentoDto expDoc : pftoa.getValue()) {
          try {
            LOG.info("Notificación automática de documentos: se va a ejecutar para el documento {} la acción(pftoa) {}",
                expDoc.getIdExpDoc(), pftoa.getKey());
            if (expDoc.getExpedienteRequerimientoDtos() != null && !expDoc.getExpedienteRequerimientoDtos().isEmpty()) {
              contextData.put("idExpReq", expDoc.getExpedienteRequerimientoDtos().get(0).getIdExpReq());
            }

            contextData.put("idExpDoc", expDoc.getIdExpDoc());
            contextData.put("idProFasTraOpeAcc", pftoa.getKey());

            expedientesFacade.ejecutarAccion(pftoa.getKey(), contextData);
          } catch (Exception e) {
            LOG.error("Se ha producido un error notificando el documento: {}", e.getMessage());
            guardaErrorJob(e, descripcion, jobName);
          }
        }

      }
    }

  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_NOT_DOCUMENTOS";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

}
