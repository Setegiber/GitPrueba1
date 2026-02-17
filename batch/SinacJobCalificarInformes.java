package es.mjusticia.sinac.core.batch;

import java.math.BigInteger;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.sshtools.common.logger.Log;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;

/**
 * Job para solicitar a la Dgp los informes en estado pendiente
 */
@Component
public class SinacJobCalificarInformes extends SinacJob<ExpedienteInformeDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobCalificarInformes.class);

  @Value(value = "${sinac.quartz.sinacJobDgp.solicitar.maxItem}")
  private Integer maxItem;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private UsuariosService usuariosService;

  private String descripcion;

  private String jobName;

  /**
   * Constructor por defecto
   */
  public SinacJobCalificarInformes() {
    super();
  }

  @Override
  public List<ExpedienteInformeDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobCalificarInformes.recuperarItems - Init");
    List<ExpedienteInformeDto> listExpInformes = expedientesFacade
        .getListaExpedientesInformesByCodEstInforme(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO);

    LOG.info("SinacJobCalificarInformes.recuperarItems - End");
    return listExpInformes;
  }

  @Override
  public void procesarItem(ExpedienteInformeDto item, Map<String, Object> contextData) {
    try {
      jobName = (String) contextData.get("jobName");
      descripcion = (String) contextData.get("descripcion");
      BigInteger idExp = item.getExpedienteDto().getIdExp();
      ProcedimientoDto procedimiento = item.getExpedienteDto().getProcedimientoDto();

      contextData.put("idExp", idExp);
      contextData.put("idPro", procedimiento.getIdPro());

      String codInf = item.getLdvMaestraDtoByIdInfLdv().getCodLdvMae();

      if ("TINF-DGP".equals(codInf)) {
        procesarDgp(item, procedimiento, contextData);
      } else if ("TINF-MJU".equals(item.getLdvMaestraDtoByIdInfLdv().getCodLdvMae())) {
        procesarMju(item, procedimiento, contextData, idExp);
      } else if ("TINF-CNI".equals(item.getLdvMaestraDtoByIdInfLdv().getCodLdvMae())) {
        procesarCni(item, procedimiento, contextData, idExp);
      } else if ("TINF-MDE".equals(item.getLdvMaestraDtoByIdInfLdv().getCodLdvMae())) {
        procesarMde(item, procedimiento, contextData, idExp);
      }

      LOG.info("SinacJobCalificarInformes.procesarItem - End");
    } catch (SinacException e) {
      try {
        addError();
        guardaErrorJob(e, descripcion, jobName);

      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }

  }

  private void procesarDgp(ExpedienteInformeDto item, ProcedimientoDto procedimiento, Map<String, Object> contextData)
      throws SinacException {
    try {
      contextData.put("idExpInf", item.getIdExpInf());
      Long idProFaseTraOpeAcc;
     
      if (item.getExpedienteInformeDgpDto() != null && item.getExpedienteInformeDgpDto().getAntecedentes()!=null) {
    	  
    	  if("NO CONSTAN ANTECEDENTES".equals(item.getExpedienteInformeDgpDto().getAntecedentes())) {
	        idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
	            procedimiento.getCodPro(), "INF", "IDGP", "DGPF");
	      }else {
	          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
	                  procedimiento.getCodPro(), "INF", "IDGP", "DGPD");
	      }
	
	      expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);
	   }
    } catch (Exception e) {
      Log.error("Se ha producido un error procesando el informe de DGP {} del expediente {}. Error {}",
          item.getIdExpInf(), item.getExpedienteDto().getIdExp(), e.getMessage());
    }
  }

  private void procesarMju(ExpedienteInformeDto item, ProcedimientoDto procedimiento, Map<String, Object> contextData,
      BigInteger idExp) throws SinacException {
    try {
      contextData.put("idExpInfMju", item.getIdExpInf());

      if (item.getExpedienteInformesMjuFicherosDatosDtos() != null
          && !item.getExpedienteInformesMjuFicherosDatosDtos().isEmpty()) {
        String codRespuesta = item.getExpedienteInformesMjuFicherosDatosDtos().get(0).getCodRespuesta();
        Long idProFaseTraOpeAcc;
if(!"R".equals(codRespuesta)) {
        if ("N".equals(codRespuesta)) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "IMJU", "MJUF");
        } else if ("A".equals(codRespuesta)) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "IMJU", "MJUD");
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_1);
        }

        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);
      }
      }
    } catch (Exception e) {
      Log.error("Se ha producido un error solicitando el informe de MJU {} del expediente {}. Error {}",
          item.getIdExpInf(), item.getExpedienteDto().getIdExp(), e.getMessage());
    }
  }

  private void procesarCni(ExpedienteInformeDto item, ProcedimientoDto procedimiento, Map<String, Object> contextData,
      BigInteger idExp) throws SinacException {
    try {
      contextData.put("idExpInfCni", item.getIdExpInf());

      if (item.getLdvMaestraDtoByIdSentidoInfLdv() != null) {
        String codLdvMae = item.getLdvMaestraDtoByIdSentidoInfLdv().getCodLdvMae();
        Long idProFaseTraOpeAcc;

        if ("CNI-APT".equals(codLdvMae) || "CNI-RES3".equals(codLdvMae) || "CNI-RES6".equals(codLdvMae)) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "ICNI", "CNIF");
        } else if (codLdvMae != null) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "ICNI", "CNID");
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_2);
        }

        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);
      }
    } catch (Exception e) {
      Log.error("Se ha producido un error solicitando el informe de CNI {} del expediente {}. Error {}",
          item.getIdExpInf(), item.getExpedienteDto().getIdExp(), e.getMessage());
    }
  }

  private void procesarMde(ExpedienteInformeDto item, ProcedimientoDto procedimiento, Map<String, Object> contextData,
      BigInteger idExp) throws SinacException {
    try {
      contextData.put("idExpInfMde", item.getIdExpInf());

      if (item.getLdvMaestraDtoByIdSentidoInfLdv() != null) {
        String codLdvMae = item.getLdvMaestraDtoByIdSentidoInfLdv().getCodLdvMae();
        Long idProFaseTraOpeAcc;

        if ("MDE-AP".equals(codLdvMae)) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "IMDE", "MDEF");
        } else if (codLdvMae != null) {
          idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
              procedimiento.getCodPro(), "INF", "IMDE", "MDED");
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_3);
        }

        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, contextData);
      }
    } catch (Exception e) {
      Log.error("Se ha producido un error solicitando el informe de MDE {} del expediente {}. Error {}",
          item.getIdExpInf(), item.getExpedienteDto().getIdExp(), e.getMessage());
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_CAL_INFORMES";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(ExpedientesFacadeImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }
}
