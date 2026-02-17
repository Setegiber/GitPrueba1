package es.mjusticia.sinac.core.business.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.ProcedimientosAvisosService;
import es.mjusticia.sinac.core.model.entity.ProcedimientosAvisosEntity;
import es.mjusticia.sinac.core.persistence.ProcedimientosAvisosDao;
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

/**
 * Implementacion de los servicios para Procedimientos avisos
 *
 * @author Nttdata
 */
@Component
public class ProcedimientosAvisosServiceImpl implements ProcedimientosAvisosService {

  private static final Logger LOG = LoggerFactory.getLogger(DocumentosServiceImpl.class);

  @Autowired
  private ProcedimientosAvisosDao procedimientosAvisosDao;

  @Override
  public void updateHabilitado(Long idProAviso, Boolean habilitado) {
    LOG.info("Iniciando actualización del campo habilitado para el Procedimiento Aviso con ID: {}", idProAviso);
    try {
      // Buscar el Procedimiento Aviso por su ID
      LOG.debug("Buscando Procedimiento Aviso en la base de datos.");
      Optional<ProcedimientosAvisosEntity> procedimientoAvisoOpt = procedimientosAvisosDao.findById(idProAviso);
      if (procedimientoAvisoOpt.isPresent()) {
        ProcedimientosAvisosEntity procedimientoAviso = procedimientoAvisoOpt.get();
        LOG.debug("Procedimiento Aviso encontrado. Estado actual de habilitado: {}",
            procedimientoAviso.isFlgHabilitado());
        // Actualizar el campo flgHabilitado
        procedimientoAviso.setFlgHabilitado(habilitado);
        procedimientosAvisosDao.save(procedimientoAviso);
        LOG.info("Se actualizó el estado habilitado del Procedimiento Aviso con ID: {} a: {}", idProAviso,
            habilitado);
      } else {
        LOG.error("No se encontró el Procedimiento Aviso con ID: {}", idProAviso);
        throw new SinacException(SinacExceptionMessageType.MESSAGE_59)
            .type(SinacExceptionType.DATA);
      }
    } catch (NullPointerException e) {
      LOG.error("Se produjo un error debido a un valor nulo inesperado al actualizar el estado habilitado.", e);
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_60)
          .type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      LOG.error("Ocurrió un error inesperado al actualizar el estado habilitado del Procedimiento Aviso con ID: {}",
          idProAviso, exception);
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_61)
          .type(SinacExceptionType.DATA);
    }
  }
}
