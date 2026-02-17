package es.mjusticia.sinac.core.batch;

import java.io.IOException;
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
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.boe.dto.RespuestaBoeDto;
import es.mjusticia.sinac.core.eis.boe.dto.RespuestaBoeDto.Anuncio;
import es.mjusticia.sinac.core.eis.boe.dto.RespuestaBoeDto.CausaDevolucion;
import es.mjusticia.sinac.core.eis.connector.BOEConnector;
import es.mjusticia.sinac.core.eis.connector.impl.BOEConnectorImpl;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosListasDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteBoeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.utils.UtilError;
import io.jsonwebtoken.lang.Collections;

/**
 * Job para solicitar al boe consultas sobre los envíos
 */
@Component
public class SinacJobConsultaBoe extends SinacJob<String> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobConsultaBoe.class);

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private BOEConnector boeConnector;
  
  @Autowired
  private CatalogosService catalogosService;

  public SinacJobConsultaBoe() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<String> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobBoe.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<String> lista = expedientesFacade.getIdsEnvioJobBoe();
    LOG.info("Tamaño lista: {}", lista.size());
    LOG.info("SinacJobBoe.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(String item, Map<String, Object> contextData) {

    LOG.info("SinacJobBoe.procesarItem - Init");
    LOG.info("Item: {}", item);
    Map<String, Object> valores = new HashMap<>();
    RespuestaBoeDto respuesta = boeConnector.consultaEnvio(item);
    ExpedienteBoeDto expedienteBoeDto = expedientesFacade.getExpedienteBoeByIdEnvio(item);
    ExpedienteDocumentoDto expedienteDocumentoDto = expedientesFacade
        .getExpedienteDocumentoByIdDocumento(expedienteBoeDto.getExpedienteDocumentoDto().getIdExpDoc());
    valores.put("idExpDoc", expedienteDocumentoDto.getIdExpDoc());
    List<BoeAnunciosDto> anunciosBoeDtos = expedientesFacade.getBoeAnunciosByIdExpBoe(expedienteBoeDto.getIdExpBoe());
    if (respuesta != null && respuesta.getAnuncios() != null) {
      modificarAnuncios(respuesta, anunciosBoeDtos);
    }
    if(contextData.get("idEnvio") != null && expedienteBoeDto.getIdEnvio().equals(contextData.get("idEnvio").toString()) && 
    		contextData.get("estadoBoe") != null) {
    	anunciosBoeDtos.get(0).setEstadoBoe(contextData.get("estadoBoe").toString());
    }
    expedienteBoeDto.setAnunciosBoeDtos(anunciosBoeDtos);
    expedientesFacade.saveDatosBoe(expedienteBoeDto);
    valores.put("idExp", expedienteDocumentoDto.getExpedienteDto().getIdExp());
    valores.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());
    valores.put("flgProceso", true);
    valores.put("idPro", expedienteDocumentoDto.getExpedienteDto().getProcedimientoDto().getIdPro());
    Long idProFaseTraOpeAcc = null;
    if (!Collections.isEmpty(anunciosBoeDtos)) {
      String codTramite = "";
      String codOperacion = "";
      if (expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo().equals("OFREQ")) {
        codTramite = "REV";
        codOperacion = "NREQ";
      } else {
        codTramite = "RES";
        codOperacion = "NDR";
      }
      if (anunciosBoeDtos.get(0).getEstadoBoe().equals("PUBLICADO")) {
        idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
            expedienteDocumentoDto.getExpedienteDto().getProcedimientoDto().getCodPro(), codTramite, codOperacion,
            "PBOE");

      } else if (anunciosBoeDtos.get(0).getEstadoBoe().equals("ANULADO")
          || anunciosBoeDtos.get(0).getEstadoBoe().equals("DEVUELTO") || anunciosBoeDtos.get(0).getEstadoBoe().equals("CADUCADO")) {
        idProFaseTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
            expedienteDocumentoDto.getExpedienteDto().getProcedimientoDto().getCodPro(), codTramite, codOperacion,
            "DBOE");
      }
    }
    if (idProFaseTraOpeAcc != null) {
      try {
        expedientesFacade.ejecutarAccion(idProFaseTraOpeAcc, valores);
      } catch (SinacException | SQLException | ParserConfigurationException | SAXException | IOException e) {
        LOG.error("Se ha producido un error ",e);
        addError();
        guardaErrorJob(e, descripcion, jobName);
      }
    }

    LOG.info("SinacJobBoe.procesarItem - End");

  }

  private void modificarAnuncios(RespuestaBoeDto respuesta, List<BoeAnunciosDto> anunciosBoeDtos) {
    for (Anuncio anuncio : respuesta.getAnuncios().getAnuncios()) {
      for (BoeAnunciosDto boeAnunciosDto : anunciosBoeDtos) {
        if (boeAnunciosDto.getIdBoe().equals(anuncio.getIdBoe())) {
          cargarCausaDevolucionAnuncio(anuncio, boeAnunciosDto);
          boeAnunciosDto.setEstadoBoe(anuncio.getEstadoBoe());
          break;
        }
      }
    }
  }

  private void cargarCausaDevolucionAnuncio(Anuncio anuncio, BoeAnunciosDto boeAnunciosDto) {
    List<BoeAnunciosListasDto> causasDevolucionDto = new ArrayList<>();
    if (anuncio.getCausasDevolucion() != null) {
      for (CausaDevolucion causaDevolucion : anuncio.getCausasDevolucion().getCausas()) {
        BoeAnunciosListasDto causaDevolucionAnuncioDto = new BoeAnunciosListasDto();
        causaDevolucionAnuncioDto.setBoeAnunciosDto(boeAnunciosDto);
        causaDevolucionAnuncioDto.setDescripcion(causaDevolucion.getDescripcion());
        causaDevolucionAnuncioDto.setObservacion(causaDevolucion.getObservaciones());
        LdvMaestraDto ldvCausaDevolucion = catalogosService.getCatalogoByCod("BOE_ANU_CAU");
        causaDevolucionAnuncioDto.setLdvMaestraDtoByTipoLdv(ldvCausaDevolucion);
        causasDevolucionDto.add(causaDevolucionAnuncioDto);
      }
      boeAnunciosDto.setBoeAnunciosListasDtos(causasDevolucionDto);
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_BOE";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(BOEConnectorImpl.class.getName());
    listaNombresClaseGuardarError.add(ExpedientesFacadeImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
