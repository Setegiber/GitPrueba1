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
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.service.FormularioCamposValidaService;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.entity.FormularioCamposValidaEntity;
import es.mjusticia.sinac.core.model.mapper.FormularioCamposValidaMapper;
import es.mjusticia.sinac.core.model.mapper.LdvEntidadesMaestrasMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.persistence.FormularioCamposValidaDao;
import jakarta.transaction.Transactional;

@Component
public class FormularioCamposValidaServiceImpl implements FormularioCamposValidaService {

  @Autowired
  FormularioCamposValidaDao formularioCamposValidaDao;

  @Autowired
  FormularioCamposValidaMapper formularioCamposValidaMapper;

  @Autowired
  LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  LdvEntidadesMaestrasMapper ldvEntidadesMaestrasMapper;

  @Override
  public List<FormularioCamposValidaDto> getFormularioCamposValidaListaByIdProcedimiento(short idProcedimiento) {
    Set<FormularioCamposValidaEntity> formularioCamposValidaEntityLista = formularioCamposValidaDao
        .findFormularioCamposValidaListaByIdProcedimiento(idProcedimiento);
    List<FormularioCamposValidaDto> formularioCamposValidaDtoLista = new ArrayList<>();
    for (FormularioCamposValidaEntity entity : formularioCamposValidaEntityLista) {
      formularioCamposValidaDtoLista.add(formularioCamposValidaMapper.toDto(entity));
    }
    return formularioCamposValidaDtoLista;
  }

  @Override
  public List<FormularioCamposValidaDto> getFormularioCamposValidaListaSinExpedienteByIdProcedimiento(
      short idProcedimiento) {
    Set<FormularioCamposValidaEntity> formularioCamposValidaEntityLista = formularioCamposValidaDao
        .findFormularioCamposValidaListaSinExpedienteByIdProcedimiento(idProcedimiento);
    List<FormularioCamposValidaDto> formularioCamposValidaDtoLista = new ArrayList<>();
    for (FormularioCamposValidaEntity entity : formularioCamposValidaEntityLista) {
      formularioCamposValidaDtoLista.add(formularioCamposValidaMapper.toDto(entity));
    }
    return formularioCamposValidaDtoLista;
  }

  @Override
  @Transactional
  public List<FormularioCamposValidaDto> findAll() {
    List<FormularioCamposValidaEntity> formularioCamposValidaEntityLista = new ArrayList<>();
    formularioCamposValidaDao.findAll().forEach(formularioCamposValidaEntityLista::add);
    List<FormularioCamposValidaDto> formularioCamposValidaDtoLista = new ArrayList<>();
    for (FormularioCamposValidaEntity entity : formularioCamposValidaEntityLista) {
      FormularioCamposValidaDto formularioCamposValidaDto = formularioCamposValidaMapper.toDto(entity);
      ProcedimientoDto procedimientoDto = new ProcedimientoDto();
      procedimientoDto.setIdPro(entity.getProcedimiento().getIdPro());
      procedimientoDto.setCodCorto(entity.getProcedimiento().getCodCorto());
      procedimientoDto.setCodPro(entity.getProcedimiento().getCodPro());
      procedimientoDto.setNomPro(entity.getProcedimiento().getNomPro());
      formularioCamposValidaDto.setProcedimiento(procedimientoDto);
      formularioCamposValidaDto.setLdvMaestra(ldvMaestraMapper.toDto(entity.getLdvMaestra()));
      if (entity.getLdvEntidadMaestra() != null) {
        formularioCamposValidaDto.setLdvEntidadMaestra(ldvEntidadesMaestrasMapper.toDto(entity.getLdvEntidadMaestra()));
      }
      formularioCamposValidaDtoLista.add(formularioCamposValidaDto);
    }
    return formularioCamposValidaDtoLista;
  }

  @Override
  public FormularioCamposValidaEntity findFormularioCamposValidaEntityByCodigo(String codigo) {
    return formularioCamposValidaDao.findFormularioCamposValidaEntityByCodigo(codigo);
  }

}
