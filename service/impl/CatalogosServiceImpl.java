package es.mjusticia.sinac.core.business.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.model.dto.LdvEntidadesMaestrasDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.persistence.LdvEntidadesMaestrasDao;
import es.mjusticia.sinac.core.persistence.LdvMaestraDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class CatalogosServiceImpl implements CatalogosService {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogosServiceImpl.class);

  @Autowired
  LdvMaestraDao ldvMaestraDao;

  @Autowired
  LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  LdvEntidadesMaestrasDao ldvEntidadesMaestrasDao;

  @Override
  public LdvMaestraDto getCatalogoById(int id) throws SinacException {
    LOG.debug("CatalogosServiceImpl.getCatalogoById - Init");
    try {
      LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Ldv maestra no encontrada"));
      LOG.debug("CatalogosServiceImpl.getCatalogoById - End");
      return ldvMaestraMapper.toDto(ldvMaestraEntity);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_112).logMessageParams(id).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public LdvMaestraDto getCatalogoByCod(String cod) throws SinacException {
    LOG.debug("CatalogosServiceImpl.getCatalogoByCod - Init");
    try {
      LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findByCodigo(cod);
      LOG.debug("CatalogosServiceImpl.getCatalogoByCod - End");
      return ldvMaestraMapper.toDto(ldvMaestraEntity);
    } catch (Exception ex) {

      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_112).logMessageParams(cod).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<LdvMaestraDto> getComboLdvMaestraByLdvEntidadMaestraCod(String cod) throws SinacException {
    LOG.debug("CatalogosServiceImpl.getComboLdvMaestraByLdvEntidadMaestraCod - Init");
    try {
      List<LdvMaestraEntity> ldvMaestraEntities = ldvMaestraDao.getComboLdvMaestraByLdvEntidadMaestraCod(cod);
      LOG.debug("CatalogosServiceImpl.getComboLdvMaestraByLdvEntidadMaestraCod - End");
      return ldvMaestraMapper.toDto(ldvMaestraEntities);
    } catch (Exception ex) {

      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_112).logMessageParams(cod).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<LdvMaestraDto> getAllLdvMaestraDto() {
    LOG.debug("CatalogosServiceImpl.getAllLdvMaestraDto - Init");
    List<LdvMaestraDto> ldvMaestraDtosLista = new ArrayList<>();
    ldvMaestraDao.findAll().forEach(ldvMaestraEntity -> {
      LdvMaestraDto ldvMaestraDto = new LdvMaestraDto();
      ldvMaestraDto.setCodLdvMae(ldvMaestraEntity.getCodLdvMae());
      ldvMaestraDto.setDesLdvMae(ldvMaestraEntity.getDesLdvMae());
      ldvMaestraDto.setIdLdvMae(ldvMaestraEntity.getIdLdvMae());
      ldvMaestraDto.setNomLdvMae(ldvMaestraEntity.getNomLdvMae());
      LdvEntidadesMaestrasDto ldvEntidadesMaestrasDto = new LdvEntidadesMaestrasDto();
      ldvEntidadesMaestrasDto.setCodLdvEntMae(ldvMaestraEntity.getLdvEntidadesMaestrasEntity().getCodLdvEntMae());
      ldvEntidadesMaestrasDto.setDesLdvEntMae(ldvMaestraEntity.getLdvEntidadesMaestrasEntity().getDesLdvEntMae());
      ldvEntidadesMaestrasDto.setIdLdvEntMae(ldvMaestraEntity.getLdvEntidadesMaestrasEntity().getIdLdvEntMae());
      ldvEntidadesMaestrasDto.setNomLdvEntMae(ldvMaestraEntity.getLdvEntidadesMaestrasEntity().getNomLdvEntMae());
      ldvMaestraDto.setLdvEntidadesMaestrasDto(ldvEntidadesMaestrasDto);
      ldvMaestraDtosLista.add(ldvMaestraDto);
    });
    LOG.debug("CatalogosServiceImpl.getAllLdvMaestraDto - End");
    return ldvMaestraDtosLista;
  }

  @Override
  public LdvMaestraDto getCatalogoByNomAndLdvEntidadMaestraCod(String nomLdv, String codLdvEntMae)
      throws SinacException {
    LOG.debug("CatalogosServiceImpl.getCatalogoByNom - Init");
    try {
      LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findByNombreAndLdvEntidadMaestraCod(nomLdv, codLdvEntMae);
      LOG.debug("CatalogosServiceImpl.getCatalogoByNom - End");
      return ldvMaestraMapper.toDto(ldvMaestraEntity);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_113).logMessageParams(nomLdv, codLdvEntMae)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public String getCodLdvEntMaeByCodLdvMae(String codVal) throws SinacException {
    return ldvEntidadesMaestrasDao.getCodLdvEntMaeByCodLdvMae(codVal);
  }

  @Override
  public Integer getIdLdvByCodLdvMae(String codLdv) throws SinacException {
    return ldvMaestraDao.getIdLdvByCodLdvMae(codLdv);
  }

}
