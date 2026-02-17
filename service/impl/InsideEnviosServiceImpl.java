package es.mjusticia.sinac.core.business.service.impl;

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

import es.mjusticia.sinac.core.business.service.InsideEstadosService;
import es.mjusticia.sinac.core.model.dto.InsideEstadoDto;
import es.mjusticia.sinac.core.model.enums.InsideEnviosEstadosEnum;
import es.mjusticia.sinac.core.model.mapper.ExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.InsideEstadoMapper;
import es.mjusticia.sinac.core.model.mapper.UsuarioMapper;
import es.mjusticia.sinac.core.persistence.InsideEstadosDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class InsideEnviosServiceImpl implements InsideEstadosService {

  @Autowired
  private InsideEstadosDao insideEstadosDao;

  @Autowired
  private InsideEstadoMapper insideEstadoMapper;

  @Override
  public void saveEstado(InsideEstadoDto insideEstadoDto) {
    insideEstadosDao.save(insideEstadoMapper.toEntity(insideEstadoDto));
  }

  @Override
  public List<InsideEstadoDto> getEnviosPendientesAlta() {
    List<String> estados = Arrays.asList(
            InsideEnviosEstadosEnum.PENDIENTE_ALTA_EXPEDIENTE.getCodigo(),
            InsideEnviosEstadosEnum.ERROR_ALTA_EXPEDIENTE.getCodigo(),
            InsideEnviosEstadosEnum.ERROR_REMISION_JUSTICIA.getCodigo()
    );
    return insideEstadoMapper.toDtos(insideEstadosDao.getEnviosEnEstado(estados));
  }

  @Override
  public List<InsideEstadoDto> getEnviosRemitidosNoFinalizados() {
    List<String> estados = Arrays.asList(
            InsideEnviosEstadosEnum.REMITIDO_JUSTICIA.getCodigo()
    );
    return insideEstadoMapper.toDtos(insideEstadosDao.getEnviosEnEstado(estados));
  }
}
