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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sshtools.common.logger.Log;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteEstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;

/**
 * Job para solicitar todos los informes de expedientes que tengan el estado
 * informes pendientes de solicitar.
 */
@Component
public class SinacJobSolicitarTodosInformes extends SinacJob<ExpedienteDto> {

  private static final Logger logger = LoggerFactory.getLogger(SinacJobSolicitarTodosInformes.class);

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ProcedimientosService procedimientosService;

  int flgInforme;

  boolean flgEntrada;

  public SinacJobSolicitarTodosInformes() {
    super();
  }

  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {
    flgInforme = 0;
    List<ExpedienteDto> listaExpedientes = new ArrayList<>();
    List<String> listaEstado = Arrays.asList("INFP");
    List<String> listaEstadosIn = Arrays.asList("DCPV");
    List<String> listaEstadosInforme = Arrays.asList("99");

    listaExpedientes = expedientesService.listaExpedientesPorEstado(listaEstado);
    listaExpedientes.addAll(expedientesService.listaExpedientesDocPendienteValidar(listaEstadosIn, listaEstado));
    listaExpedientes.addAll(expedientesService.listaExpedientesDgpRechazo(listaEstadosInforme));
    Log.error("Se van a procesar {} items", listaExpedientes.size());
    return listaExpedientes;
  }

  @Override
  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {

    Map<String, String> listaEstados = new HashMap<String, String>();
    if (flgInforme < 2) { // flgInforme = 0
      for (ExpedienteEstadoDto expEst : item.getExpedientesEstadosDtos()) {
        listaEstados.put(expEst.getEstado().getEstadoFin().getCodEstado(),
            expEst.getEstado().getEstadoFin().getCodEstado());

      }
      if (flgInforme == 0 && listaEstados.get("INFP") == null) {
        flgInforme = 1;
      }
      if (flgInforme == 1 && listaEstados.get("DCPV") == null) {
        flgInforme = 2;
      }
    }

    contextData.put("idPro", item.getProcedimientoDto().getIdPro());
    if (flgInforme == 0) {
      expedientesFacade.solicitarInformesDisponibles(item.getIdExp(), contextData);
    } else if (flgInforme == 1) {
      contextData.put("idExp", item.getIdExp());
      contextData.put("tipoInformeCni", "TINF-CNI");

      try {
        expedientesFacade
            .ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                item.getProcedimientoDto().getCodPro(), "INS", "INF", "ICNI", "SCNI"), contextData);
      } catch (Exception e) {
        Log.error("Se ha producido un error solicitando el informe de CNI ", e.getMessage());
      }
    } else if (flgInforme == 2) {
      for (ExpedienteInformeDto expInf : item.getExpedienteInformeDtos()) {
        if ("TINF-DGP".equals(expInf.getLdvMaestraDtoByIdInfLdv().getCodLdvMae())) {
          expedientesService.informeSolicitado(item.getIdExp(), "TINF-DGP", expInf.getIdExpInf());
        }

      }

    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_SOL_INFORMES";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

}
