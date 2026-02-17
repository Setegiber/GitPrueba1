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

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.business.service.impl.CatalogosServiceImpl;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPlazosDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.core.web.tramites.ListadoValidacionesCodEntMaeEnum;

/**
 * Job para vencer aquellos Plazos que estén "En Curso" y su Fecha de
 * Finalización sea anterior a la Fecha actual.
 *
 * @author NTT Data.
 */
@Component
public class SinacVencimientoPlazosJob extends SinacJob<ExpedientesPlazosDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacVencimientoPlazosJob.class);

  private String jobName;

  private String jobDescription;

  private static final int CINCO = 5;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private PlazosService plazosService;

  public SinacVencimientoPlazosJob() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_PLAZOS_VENCIMIENTO";
  }

  @Override
  public List<ExpedientesPlazosDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacVencimientoPlazosJob.recuperarItems - Init");

    jobName = (String) contextData.get("jobName");
    jobDescription = (String) contextData.get("descripcion");

    LdvMaestraDto enCurso = catalogosService.getCatalogoByCod(Constantes.Plazo.Estado.EN_CURSO);

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = expedientesFacade
        .getPlazosVigentesVencidosByEstado(enCurso.getCodLdvMae());

    if (CollectionUtils.isEmpty(expedientesPlazosDtoList)) {
      LOG.info(
          "SinacVencimientoPlazosJob.recuperarItems - No se han encontrado Plazos vigentes vencidos en estado \"en Curso\".");
    }

    LOG.info("SinacVencimientoPlazosJob.recuperarItems - End");

    return expedientesPlazosDtoList;
  }

  @Override
  public void procesarItem(ExpedientesPlazosDto item, Map<String, Object> contextData) {
    LOG.info("SinacVencimientoPlazosJob.procesarItem {} en el expediente {}",
        item.getPlazoDto().getLdvMaestra().getCodLdvMae(), item.getExpedienteDto().getIdExp());
    String codTipoPlazo = item.getPlazoDto().getLdvMaestra().getCodLdvMae();
    List<String> listaPlazosEjecutanAccion = Arrays.asList("TPLA-RES", "TPLA-SUB", "TPLA-ICNI", "TPLA-CDGP",
        "TPLA-CMJU", "TPLA-ARCH");

    try {
      if (listaPlazosEjecutanAccion.contains(codTipoPlazo)) {
        String codTramite;
        String codOperacion;
        String codAccion;
        Map<String, Object> valores = new HashMap<>();

        switch (codTipoPlazo) {
        case "TPLA-RES":
          codTramite = "REV";
          codOperacion = "REV";
          codAccion = "CAEX";
          break;
        case "TPLA-SUB":
          codTramite = "REV";
          codOperacion = "SUB";
          codAccion = "CREQ";
          valores.put("idExpReq", getIdRequerimiento(item));
          break;
        case "TPLA-ICNI":
          codTramite = "INF";
          codOperacion = "ICNI";
          codAccion = "RCNI";
          valores.put("sentidoCni", "CNI-APT");
          valores.put("observaciones", "Sin respuesta 3 meses");
          break;
        case "TPLA-CDGP":
          codTramite = "INF";
          codOperacion = "CINF";
          codAccion = "CADI";
          valores.put("codTipoInforme", "TINF-DGP");
          break;
        case "TPLA-CMJU":
          codTramite = "INF";
          codOperacion = "CINF";
          codAccion = "CADI";
          valores.put("codTipoInforme", "TINF-MJU");
          break;
        case "TPLA-ARCH":
          codTramite = "ARC";
          codOperacion = "ARC";
          codAccion = "ARCE";
          break;
        default:
          codTramite = null;
          codOperacion = null;
          codAccion = null;
        }

        Long idProFaseTraOpeAcc = expedientesFacade
            .getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(
                item.getExpedienteDto().getProcedimientoDto().getCodPro(), codTramite, codOperacion, codAccion);

        valores.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());
        valores.put("idExp", item.getExpedienteDto().getIdExp());
        valores.put("idPro", item.getExpedienteDto().getProcedimientoDto().getIdPro());
        valores.put("idProFasTraOpeAcc", idProFaseTraOpeAcc);
        valores.put("flgProceso", true);

        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, valores);
      } else if ("TPLA-IDGP".equals(codTipoPlazo) || "TPLA-IMJU".equals(codTipoPlazo)) {
        String codTipoInforme = plazosService.getCodTipoInformeByCodPlazoRespuestaInforme(codTipoPlazo);

        ExpedienteInformeDto expedienteInformeDto = expedientesFacade
            .getInformesByIdExpediente(item.getExpedienteDto().getIdExp()).get(codTipoInforme);

        LdvMaestraDto noRespondido = catalogosService.getCatalogoByCod("EINF-NRE");

        expedientesFacade.updateEstadoInforme(expedienteInformeDto.getIdExpInf(), noRespondido);
        try {
          expedientesFacade.updateValidacionSemaforo(item.getExpedienteDto().getIdExp(),
              "VAL_".concat(codTipoInforme.substring(CINCO)), "EINF-NRE");
          expedientesFacade.recalcularValidadionesSemaforo(item.getExpedienteDto().getIdExp(),
              ListadoValidacionesCodEntMaeEnum.LISTADO_INTEGRACIONES.getListaValidaciones(),
              ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

        } catch (SinacException ex) {
          throw new SinacException(ex,
              SinacExceptionMessageType.MESSAGE_104).logMessageParams(codTipoInforme, item.getExpedienteDto().getIdExp())
              .type(SinacExceptionType.DATA);
        }
      }

      expedientesFacade.vencerPlazoExpediente(item);

      if (!"TPLA-RES".equals(codTipoPlazo)) {
        ExpedientesPlazosDto plazoResolucion = expedientesFacade
            .getPlazoResolucionVigenteByIdExpediente(item.getExpedienteDto().getIdExp());

        if (plazoResolucion != null) {
          expedientesFacade.reanudarPlazoExpediente(plazoResolucion, false);
        }
      }
    } catch (SinacException | SQLException | ParserConfigurationException | SAXException | IOException exception) {
      LOG.error("SinacVencimientoPlazosJob.procesarItem - Error: ", exception);
      try {
        addError();
        guardaErrorJob(exception, jobDescription, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }

    LOG.info("SinacVencimientoPlazosJob.procesarItem {} en el expediente {}",
        item.getPlazoDto().getLdvMaestra().getCodLdvMae(), item.getExpedienteDto().getIdExp());
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    LOG.info("SinacVencimientoPlazosJob.guardaErrorJob - Init");

    List<String> list = List.of(this.getClass().getName(), ExpedientesFacadeImpl.class.getName(),
        CatalogosServiceImpl.class.getName());

    UtilError.guardaErrorJob(e, list, jobFacade, jobDescripcion, this.getClass().getName(), jobName);

    LOG.info("SinacVencimientoPlazosJob.guardaErrorJob - End");
  }

  /**
   * Obtiene el Identificador del Requerimiento asociado al Plazo de Expediente
   * establecido como parámetro.
   *
   * @param expedientesPlazosDto Plazo del Expediente.
   * @return Identificador del Requerimiento asociado al Plazo de Expediente
   *         establecido como parámetro.
   */
  private BigInteger getIdRequerimiento(ExpedientesPlazosDto expedientesPlazosDto) {
    return expedientesPlazosDto.getExpedienteRequerimientoDto() != null
        ? expedientesPlazosDto.getExpedienteRequerimientoDto().getIdExpReq()
        : null;
  }

}
