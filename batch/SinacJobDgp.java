package es.mjusticia.sinac.core.batch;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
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

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.dgp.dto.TitularDto;

/**
 * Job para solicitar a la Dgp los informes en estado pendiente
 */
@Component
public class SinacJobDgp extends SinacJob<BigInteger> {

  private static final String CONTADOR_DGP_ALTA = "contadorDgpAlta";

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobDgp.class);

  @Value(value = "${sinac.quartz.sinacJobDgp.solicitar.maxItem}")
  private Integer maxItem;

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  public SinacJobDgp() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<BigInteger> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobDgp.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<BigInteger> lista = expedientesFacade.getIdsExpedienteInformesByCodEstadoCodTipoInforme(
        Constantes.EstadosInforme.ESTADO_INFORME_PENDIENTE, Constantes.TiposInforme.TIPO_INFORME_DGP);
    contextData.put(CONTADOR_DGP_ALTA, 0);
    LOG.info("Tamaño lista: {}", lista.size());
    LOG.info("SinacJobDgp.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(BigInteger item, Map<String, Object> contextData) {
    try {
      LOG.info("SinacJobDgp.procesarItem - Init");
      LOG.info("Item: {}", item);
      Map<String, Object> valores = new HashMap<>();
      TitularDto titular = expedientesFacade.getDatosSolicitudInformeDgp(item);
      valores.put("titular", titular);
      valores.put("idExp", titular.getIdExpediente());
      valores.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());
      valores.put("flgProceso", true);
      valores.put("tipoInformeDgp", Constantes.TiposInforme.TIPO_INFORME_DGP);
      valores.put("idPro", titular.getIdProcedimiento());
      valores.put("idExpedienteInforme", item);

      Integer contadorDgpAlta = (Integer) contextData.get(CONTADOR_DGP_ALTA);
      Long idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
          titular.getCodigoProcedimiento(), "INF", "IDGP", "SDGP");
      expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, valores);
      if (valores.get("respuesta").toString().equals("00")) {
        contadorDgpAlta++;
        contextData.remove(CONTADOR_DGP_ALTA);
        contextData.put(CONTADOR_DGP_ALTA, contadorDgpAlta);
      }
      if (contadorDgpAlta.intValue() == maxItem.intValue()) {
        contextData.put("stopProcesarItem", true);
      }
      LOG.info("SinacJobDgp.procesarItem - End");
    } catch (SinacException | SQLException | ParserConfigurationException | SAXException | IOException e) {
      try {
        addError();
        guardaErrorJob(e, descripcion, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_DGP_ALTA";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(ExpedientesFacadeImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
