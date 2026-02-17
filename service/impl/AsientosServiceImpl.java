package es.mjusticia.sinac.core.business.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
import es.mjusticia.sinac.core.business.service.AsientosService;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.AsientoErrorDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.entity.AsientoEntity;
import es.mjusticia.sinac.core.model.entity.AsientoErrorEntity;
import es.mjusticia.sinac.core.model.mapper.AsientoErrorMapper;
import es.mjusticia.sinac.core.model.mapper.AsientoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoMapper;
import es.mjusticia.sinac.core.persistence.AsientoDao;
import es.mjusticia.sinac.core.persistence.AsientoErrorDao;

@Component
@Transactional
public class AsientosServiceImpl implements AsientosService {

  @Autowired
  private AsientoDao asientoDao;

  @Autowired
  private AsientoErrorDao asientoErrorDao;

  @Autowired
  private AsientoMapper asientoMapper;

  @Autowired
  private AsientoErrorMapper asientoErrorMapper;

  @Autowired
  private ExpedienteDocumentoMapper expedienteDocumentoMapper;

  @Override
  public void saveAsiento(AsientoDto asientoDto, UsuarioDto usuarioDto) {
    AsientoEntity asientoEntity = asientoMapper.toEntity(asientoDto);
    // Tenemos que hacerlo así para evitar un mapeo ciclico
    asientoEntity.setExpedienteDocumento(expedienteDocumentoMapper.toEntity(asientoDto.getExpedienteDocumento()));

    asientoDao.setNoActivoAsientosByExpDoc(asientoDto.getExpedienteDocumento().getIdExpDoc(), usuarioDto.getIdUsu());
    asientoDao.save(asientoEntity);

    asientoDto.setIdAsiento(asientoEntity.getIdAsiento());
  }

  @Override
  public void setExpDocJusticante(AsientoDto asientoDto, ExpedienteDocumentoDto expDocJusticante,
      UsuarioDto usuarioDto) {
    asientoDao.setJustificante(asientoDto.getIdAsiento().shortValue(), expDocJusticante.getIdExpDoc(),
        usuarioDto.getIdUsu());
  }

  @Override
  public void saveAsientoError(AsientoErrorDto asientoErrorDto) {
    AsientoErrorEntity asientoErrorEntity = asientoErrorMapper.toEntity(asientoErrorDto);

    asientoErrorDao.save(asientoErrorEntity);
  }

  @Override
  public List<AsientoDto> getAsientosEnCurso() {
    return asientoMapper.toDto(asientoDao.getEnCurso());
  }

  @Override
  public AsientoDto getAsientoConJustificante(BigInteger idAsiento) {
    return asientoMapper.toDto(asientoDao.getAsientoConJustificante(idAsiento).orElseThrow());
  }

  @Override
  public BigInteger findIdExpFromAsiento(AsientoDto asientoDto) {
    return asientoDao.findIdExpFromAsiento(asientoDto.getIdAsiento());
  }

  @Override
  public List<AsientoDto> findByIdExpDoc(BigInteger idExpDoc) {
    return asientoMapper.toDto(asientoDao.findByIdExpDoc(idExpDoc));
  }
}
