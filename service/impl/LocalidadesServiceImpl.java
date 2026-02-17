package es.mjusticia.sinac.core.business.service.impl;

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

import es.mjusticia.sinac.core.business.service.LocalidadesService;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.mapper.LocalidadesMapper;
import es.mjusticia.sinac.core.persistence.LocalidadesDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalidadesServiceImpl implements LocalidadesService {

  @Autowired
  LocalidadesDao localidadesDao;
  
  @Autowired
  LocalidadesMapper localidadesMapper;
  
  @Override
  public LocalidadesDto getLocalidadByCodProvAndCodMun(String codProv, String codMun) {
    return localidadesMapper.toDto(localidadesDao.getLocalidadByCodProvAndCodMun(codProv, codMun));
  }

  
  
}
