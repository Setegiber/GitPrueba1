package es.mjusticia.sinac.core.business.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.ObservacionesService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteObservacionesDto;
import es.mjusticia.sinac.core.model.mapper.ExpedienteObservacionesMapper;
import es.mjusticia.sinac.core.persistence.ExpedienteObservacionesDao;
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

@Component
public class ObservacionesServiceImpl implements ObservacionesService {

  @Autowired
  private ExpedienteObservacionesDao expedienteObservacionesDao;

  @Autowired
  private ExpedienteObservacionesMapper expedienteObservacionesMapper;

  private static final Logger LOG = LoggerFactory.getLogger(ObservacionesServiceImpl.class);

  @Override
  public List<ExpedienteObservacionesDto> getObservacionesExpediente(BigInteger idExpediente) throws SinacException {
    LOG.debug("Tratando de devolver lista de observaciones del expediente {}", idExpediente);
    try {
      List<ExpedienteObservacionesDto> observaciones = expedienteObservacionesMapper
          .toListDtos(expedienteObservacionesDao.getListObservaciones(idExpediente));
      return observaciones;
    } catch (SinacException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_OBSERVACIONES_1);
    }
  }

  @Override
  public void saveExpedienteObservacion(ExpedienteDto expediente, String titulo, String mensaje) throws SinacException {
    try {

      ExpedienteObservacionesDto expedienteObservacionesDto = new ExpedienteObservacionesDto();
      expedienteObservacionesDto.setExpedienteDto(expediente);
      expedienteObservacionesDto.setTitulo(titulo);
      expedienteObservacionesDto.setMensaje(mensaje);

      expedienteObservacionesDao.save(expedienteObservacionesMapper.toEntity(expedienteObservacionesDto));

    } catch (NullPointerException e) {
      LOG.error("Se produjo un NullPointerException en la agrupación", e);
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_57).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_57).type(SinacExceptionType.DATA);
    }
  }

}
