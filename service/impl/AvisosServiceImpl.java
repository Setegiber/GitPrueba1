package es.mjusticia.sinac.core.business.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.AvisosService;
import es.mjusticia.sinac.core.model.dto.AvisoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteAvisoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosAvisosDto;
import es.mjusticia.sinac.core.model.entity.AvisoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteAvisoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteEntity;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientoEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosAvisosEntity;
import es.mjusticia.sinac.core.model.mapper.AvisoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteAvisoMapper;
import es.mjusticia.sinac.core.persistence.AvisoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteAvisoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDao;
import es.mjusticia.sinac.core.persistence.LdvMaestraDao;
import es.mjusticia.sinac.core.persistence.ProcedimientoDao;
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

@Component
public class AvisosServiceImpl implements AvisosService {

  @Autowired
  private ProcedimientosAvisosDao procedimientosAvisosDao;
  @Autowired
  private ProcedimientoDao procedimientosDao;
  @Autowired
  private LdvMaestraDao ldvMaestraDao;
  @Autowired
  private AvisoDao avisoDao;
  @Autowired
  private ExpedienteDao expedienteDao;
  @Autowired
  private ExpedienteAvisoDao expedienteAvisoDao;
  @Autowired
  private AvisoMapper avisoMapper;
  @Autowired
  private ExpedienteAvisoMapper expedienteAvisoMapper;
  private static final Logger LOG = LoggerFactory.getLogger(AvisosServiceImpl.class);

  @Override
  public Map<List<String>, LdvMaestraDto> obtenerProcedimientosPorLdvMaestra() throws SinacException {
    try {
      // Usamos LinkedHashMap para preservar el orden al insertar los elementos ya
      // ordenados
      Map<List<String>, LdvMaestraDto> ldvMaestraMap = new LinkedHashMap<>();
      // Obtenemos la lista de categorías
      List<LdvMaestraEntity> ldvMaestraList = ldvMaestraDao.getLdvCategorias();
      // Ordenar la lista por idLdvMae antes de procesarla
      ldvMaestraList.sort(Comparator.comparing(LdvMaestraEntity::getIdLdvMae));
      // Recorremos la lista de ldvMaestraList ya ordenada
      for (LdvMaestraEntity maestra : ldvMaestraList) {
        // Crear el DTO para la categoría
        LdvMaestraDto ldvMaestraDTO = new LdvMaestraDto();
        ldvMaestraDTO.setIdLdvMae(maestra.getIdLdvMae());
        ldvMaestraDTO.setNomLdvMae(maestra.getNomLdvMae());

        Integer idLdvMae = maestra.getIdLdvMae();
        // Obtener los procedimientos activos asociados a esta categoría
        List<ProcedimientoEntity> procedimientos = procedimientosDao.getProcedimientosValidosGestAvisos(idLdvMae);
        // Lista de DTOs de procedimientos
        List<ProcedimientoDto> procedimientoDTOs = new ArrayList<>();
        Set<String> avisosUnicos = new HashSet<>(); // Para almacenar los avisos sin duplicados
        // Rellenar los DTOs de procedimientos
        for (ProcedimientoEntity procedimiento : procedimientos) {
          ProcedimientoDto procedimientoDTO = new ProcedimientoDto();
          procedimientoDTO.setNomPro(procedimiento.getNomPro());
          // Obtener los ProcedimientosAvisos para este procedimiento
          List<ProcedimientosAvisosEntity> procedimientosAvi = procedimientosAvisosDao
              .findByProcedimientoId(procedimiento.getIdPro());
          List<ProcedimientosAvisosDto> procedimientoAvisosDTOs = new ArrayList<>();
          for (ProcedimientosAvisosEntity procedimientoAviso : procedimientosAvi) {
            AvisoEntity aviso = procedimientoAviso.getAviso();
            AvisoDto avisoDto = avisoMapper.toDto(aviso);
            // Añadir el aviso al conjunto de avisos únicos
            avisosUnicos.add(avisoDto.getNomAviso());
            // Crear DTO del procedimientoAviso
            ProcedimientosAvisosDto procedimientoAvisoDTO = new ProcedimientosAvisosDto();
            procedimientoAvisoDTO.setIdProAvisos(procedimientoAviso.getIdProAvisos());
            procedimientoAvisoDTO.setFlgHabilitado(procedimientoAviso.isFlgHabilitado());
            procedimientoAvisoDTO.setFlgActivo(procedimientoAviso.isFlgActivo());
            procedimientoAvisoDTO.setAviso(avisoDto);
            procedimientoAvisosDTOs.add(procedimientoAvisoDTO);
          }
          // Asignar los procedimientosAvisos al procedimiento
          procedimientoDTO.setProcedimientosAvisosDtos(procedimientoAvisosDTOs);
          procedimientoDTOs.add(procedimientoDTO);
        }
        // Solo añadir al Map si hay procedimientos en el DTO
        if (!procedimientoDTOs.isEmpty()) {
          ldvMaestraDTO.setProcedimientoEntitiesLdv(procedimientoDTOs);
          ldvMaestraMap.put(new ArrayList<>(avisosUnicos), ldvMaestraDTO);
        }
      }
      return ldvMaestraMap;
    } catch (NullPointerException e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_13)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_13)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void registrarExpedienteAviso(BigInteger idExpediente, String codAviso) throws SinacException {
    try {
      // Obtener el aviso usando el código
      Optional<AvisoEntity> avisoOpt = avisoDao.findAvisoByCodAviso(codAviso);

      // Obtener el expediente al que se registrará el aviso
      Optional<ExpedienteEntity> expedienteOpt = expedienteDao.findById(idExpediente);

      // Verificar que ambos objetos existan
      if (avisoOpt.isPresent() && expedienteOpt.isPresent()) {
        AvisoEntity aviso = avisoOpt.get();
        ExpedienteEntity expediente = expedienteOpt.get();
        // Crear una nueva instancia de ExpedienteAvisoEntity
        ExpedienteAvisoEntity expedienteAviso = new ExpedienteAvisoEntity();
        expedienteAviso.setAviso(aviso);
        expedienteAviso.setExpediente(expediente);
        // Guardar la nueva entidad en la base de datos
        expedienteAvisoDao.save(expedienteAviso);
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_14)
            .type(SinacExceptionType.DATA);
      }
    } catch (NullPointerException e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_15).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_15).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<String> getAvisosExpediente(BigInteger idExpediente, ProcedimientoDto proDto, Integer idUsuario,
      Boolean isAdmin) throws SinacException {
    LOG.info("Obteniendo avisos para el expediente con ID: {}", idExpediente);
    try {
      // Obtener el expediente usando el idExp
      LOG.debug("Buscando expediente con ID: {}", idExpediente);
      Optional<ExpedienteEntity> expedienteOpt = expedienteDao.findById(idExpediente);
      if (expedienteOpt.isPresent()) {
        ExpedienteEntity expediente = expedienteOpt.get();
        LOG.info("Expediente encontrado. Verificando usuario asignado...");
        List<String> listAvisos = new ArrayList<>();
        Integer userIdFound = expedienteDao.getUsuarioAsignadoExpediente(idExpediente);
        if (userIdFound != null && userIdFound.equals(idUsuario)) {
          LOG.debug("Usuario asignado validado correctamente. Obteniendo avisos del expediente...");
          // Obtener los expAvisos del expediente
          List<ExpedienteAvisoEntity> expedientesAvisosEntities = expedienteAvisoDao
              .getListAvisos(expediente.getIdExp()).stream().filter(ExpedienteAvisoEntity::isFlgActivo).toList();
          if (Boolean.TRUE.equals(isAdmin)) {
            LOG.debug("El usuario es administrador. Filtrando avisos basados en procedimientos habilitados...");
            List<ProcedimientosAvisosEntity> procedimientosAvisos = procedimientosAvisosDao.findAllHabilitadosPA();
            // Crear un conjunto de combinaciones válidas de idPro e idAvi
            Set<Pair<Short, Short>> allowedProcedimientoAvisoPairs = procedimientosAvisos.stream()
                .map(pa -> Pair.of(pa.getProcedimiento().getIdPro(), pa.getAviso().getIdAviso()))
                .collect(Collectors.toSet());
            // Filtrar los expedientesAvisos basándose en las combinaciones permitidas
            List<ExpedienteAvisoEntity> filteredExpedientesAvisos = expedientesAvisosEntities.stream()
                .filter(expAvi -> allowedProcedimientoAvisoPairs.contains(Pair
                    .of(expAvi.getExpediente().getProcedimientoEntity().getIdPro(), expAvi.getAviso().getIdAviso())))
                .toList();
            // Obtener los ids de los avisos y filtrarlos por habilitados
            List<Short> avisosIds = filteredExpedientesAvisos.stream().map(expEnt -> expEnt.getAviso().getIdAviso())
                .toList();
            List<ProcedimientosAvisosEntity> procedimientosAvisosEntities = procedimientosAvisosDao
                .findByProcedimientoAndAvisos(proDto.getIdPro(), avisosIds);
            Set<Short> habilitadosAvisos = procedimientosAvisosEntities.stream()
                .filter(ProcedimientosAvisosEntity::isFlgHabilitado).map(procAviso -> procAviso.getAviso().getIdAviso())
                .collect(Collectors.toSet());
            // Añadir a la lista solo los avisos habilitados
            for (ExpedienteAvisoEntity expEnt : filteredExpedientesAvisos) {
              if (habilitadosAvisos.contains(expEnt.getAviso().getIdAviso())) {
                listAvisos.add(expEnt.getAviso().getNomAviso());
              }
            }
          } else {
            // Para usuarios no administradores, añadir todos los avisos activos
            expedientesAvisosEntities.forEach(expEnt -> listAvisos.add(expEnt.getAviso().getNomAviso()));
          }
          LOG.info("Obtención de avisos completada para el expediente con ID: {}", idExpediente);
          return listAvisos;
        } else {
          LOG.warn("El usuario no está asignado al expediente o no se encontró el usuario.");
          return listAvisos;
        }
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_16).logMessageParams(idExpediente)
            .type(SinacExceptionType.DATA);
      }
    } catch (NullPointerException e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_17)
          .type(SinacExceptionType.DATA);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_18).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExpediente, Boolean isAdmin)
      throws SinacException {
    LOG.info("Iniciando la obtención de avisos para el expediente.");
    try {
      // Obtener los avisos relacionados al expediente
      LOG.debug("Buscando los avisos del expediente con ID proporcionado.");
      List<ExpedienteAvisoEntity> expedienteAvisos = expedienteAvisoDao.findAllByIdExp(idExpediente);
      if (expedienteAvisos.isEmpty()) {
        LOG.warn("No se encontraron avisos para el expediente solicitado.");
      } else {
        LOG.info("Se encontraron {} avisos para el expediente.", expedienteAvisos.size());
      }
      List<ExpedienteAvisoDto> expedienteAvisoDtos;
      if (Boolean.TRUE.equals(isAdmin)) {
        LOG.debug("El usuario tiene permisos de administrador. Aplicando filtros adicionales...");
        List<ProcedimientosAvisosEntity> procedimientosAvisos = procedimientosAvisosDao.findAllHabilitadosPA();
        if (procedimientosAvisos.isEmpty()) {
          LOG.warn("No se encontraron procedimientos habilitados para asociar con los avisos.");
        }
        // Crear un conjunto de combinaciones válidas de idPro e idAvi
        Set<Pair<Short, Short>> allowedProcedimientoAvisoPairs = procedimientosAvisos.stream()
            .map(pa -> Pair.of(pa.getProcedimiento().getIdPro(), pa.getAviso().getIdAviso()))
            .collect(Collectors.toSet());
        // Filtrar los expedientesAvisos basándose en las combinaciones permitidas
        List<ExpedienteAvisoEntity> filteredExpedientesAvisos = expedienteAvisos.stream()
            .filter(expAvi -> allowedProcedimientoAvisoPairs.contains(
                Pair.of(expAvi.getExpediente().getProcedimientoEntity().getIdPro(), expAvi.getAviso().getIdAviso())))
            .toList();
        LOG.debug("De los {} avisos encontrados, {} cumplen con los criterios de administración.",
            expedienteAvisos.size(), filteredExpedientesAvisos.size());
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(filteredExpedientesAvisos);
      } else {
        LOG.debug("El usuario no tiene permisos de administrador. Retornando todos los avisos del expediente.");
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(expedienteAvisos);
      }
      LOG.info("Se completó la obtención de los avisos para el expediente.");
      return expedienteAvisoDtos;
    } catch (NullPointerException e) {
      throw new SinacException(e,
          SinacExceptionMessageType.MESSAGE_19)
          .type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception,
          SinacExceptionMessageType.MESSAGE_20)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void cambiarEstadoAvisoExp(BigInteger idExpAvisos) {
    LOG.info("Cambiando estado del aviso expediente con ID: {}", idExpAvisos);
    try {
      // Busca el expediente por el id expedienteAviso
      LOG.debug("Buscando el expediente aviso con ID: {}", idExpAvisos);
      ExpedienteAvisoEntity expAvi = expedienteAvisoDao.findById(idExpAvisos).orElseThrow(() -> {
        return new SinacException(SinacExceptionMessageType.MESSAGE_111).logMessageParams(idExpAvisos);
      });
      // Cambia el estado de flgActivo
      boolean estadoActual = expAvi.isFlgActivo();
      boolean nuevoEstado = !estadoActual;
      LOG.debug("Cambiando el estado del campo flgActivo de {} a {} para el expediente aviso con ID: {}", estadoActual,
          nuevoEstado, idExpAvisos);
      expAvi.setFlgActivo(nuevoEstado);
      // Guarda el expediente actualizado
      expedienteAvisoDao.save(expAvi);
      LOG.info("Estado del aviso expediente con ID: {} actualizado exitosamente a {}", idExpAvisos, nuevoEstado);
    } catch (NullPointerException e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_21).logMessageParams(idExpAvisos)
          .type(SinacExceptionType.DATA);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_22).logMessageParams(idExpAvisos)
          .type(SinacExceptionType.DATA);
    }
  }

}
