package es.mjusticia.sinac.core.business.service.impl;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2022 - 2023 Ministerio de Justicia
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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.service.PaisesService;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.entity.PaisesEntity;
import es.mjusticia.sinac.core.model.mapper.PaisesMapper;
import es.mjusticia.sinac.core.persistence.PaisesDao;

@Component
public class PaisesServiceImpl implements PaisesService {
  @Autowired
  PaisesDao paisesDao;
  @Autowired
  private PaisesMapper paisesMapper;

  private static final Logger log = LoggerFactory.getLogger(PaisesServiceImpl.class);

  @Override
  public List<PaisesDto> getListaPaises() {
    Iterable<PaisesEntity> paises = paisesDao.findAll();

    List<PaisesEntity> listaPaises = new ArrayList<>();
    paises.forEach(listaPaises::add);

    List<PaisesDto> listaPaisesDto = listaPaises.stream().map(p -> paisesMapper.toDto(p)).toList();

    if (log.isDebugEnabled()) {
      log.debug("get paises: {}", String.format("%s", listaPaisesDto));
    }
    return listaPaisesDto;
  }

  @Override
  public PaisesDto getPaisPorCodigo(String codPais) {
    return paisesMapper.toDto(paisesDao.getPaisPorCodigo(codPais));
  }

  @Override
  public PaisesDto getPaisPorCodigoDgp(String codPais) {
    return  paisesMapper.toDto(paisesDao.getPaisPorCodigoDgp(codPais));
  }
  
  @Override
  public PaisesDto getPaisPorNomPaisMju(String nomPais) {
    return  paisesMapper.toDto(paisesDao.getPaisPorNomPaisMju(nomPais));
  }

  @Override
  public PaisesDto getPaisPorCodTresIso(String codTresIso) {
    return paisesMapper.toDto(paisesDao.getPaisporCodTresIso(codTresIso));
  }
  
  @Override
  public PaisesDto getPaisPorPrefijo(String prefijo) {
    return paisesMapper.toDto(paisesDao.getPaisPorPrefijo(prefijo));
  }

}
