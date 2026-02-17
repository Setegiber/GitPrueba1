package es.mjusticia.sinac.core.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.business.service.impl.DocumentosServiceImpl;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.geiser.model.enums.EstadoAsientoEnum;

/**
 * Job para comprobar el último estado de todos los asientos enviados a GEISER
 * que esten EN_CURSO
 */
@Component
public class SinacJobGeiser extends SinacJob<AsientoDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobGeiser.class);

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private DocumentosService documentosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private CatalogosService catalogosService;

  public SinacJobGeiser() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<AsientoDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobGeiser.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<AsientoDto> lista = documentosService.getAsientosEnCurso();
    LOG.info("Tamaño lista: {}", lista.size());
    LOG.info("SinacJobGeiser.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(AsientoDto asiento, Map<String, Object> contextData) {
    try {
      LOG.info("SinacJobGeiser.procesarItem - Init");
      LOG.info("Asiento: {}", asiento);
      UsuarioDto usuario = this.getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia());

      if (asiento.getExpedienteDocumentoJustificante() == null
          || asiento.getExpedienteDocumentoJustificante().getIdExpDoc() == null) {
        expedientesFacade.descargarJustificanteGeiser(asiento, usuario);
      }

      // Solo permitimos comprobar si se ha confirmado si se ha guardado el
      // justificante
      if (asiento.getExpedienteDocumentoJustificante() != null
          && asiento.getExpedienteDocumentoJustificante().getIdExpDoc() != null
          && asiento.getEstado().equals(EstadoAsientoEnum.ENVIADO_PENDIENTE_CONFIRMACION.value())) {
        AsientoDto asientoActualizado = documentosService.consultarEstadoDocumentoEnviadoAGeiser(asiento, usuario);

        if (Arrays.asList(EstadoAsientoEnum.ENVIADO_CONFIRMADO.value(), EstadoAsientoEnum.ENVIADO_RECHAZADO.value())
            .contains(asientoActualizado.getEstado())) {
          boolean isAsientoConfirmado = asientoActualizado.getEstado()
              .equals(EstadoAsientoEnum.ENVIADO_CONFIRMADO.value());

          documentosService.updateEstadoDocumento(asientoActualizado.getExpedienteDocumento().getIdExpDoc(),
              catalogosService.getCatalogoByCod(isAsientoConfirmado ? "EDOC-GEC" : "EDOC-GER"));
        }
      }

      LOG.info("SinacJobGeiser.procesarItem - End");
    } catch (SinacException e) {
      addError();
      guardaErrorJob(e, descripcion, jobName);
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_GEISER_CONSULTA";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(DocumentosServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
