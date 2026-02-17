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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.service.AccionesService;
import es.mjusticia.sinac.core.model.dto.AccionDto;
import es.mjusticia.sinac.core.model.entity.AccionEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosFasesTramitesOperacionesAccionesEntity;
import es.mjusticia.sinac.core.model.entity.RolAccionRestringidaEntity;
import es.mjusticia.sinac.core.model.mapper.AccionMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesTramitesOperacionesAccionesMapper;
import es.mjusticia.sinac.core.persistence.AccionDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesAccionesDao;
import es.mjusticia.sinac.core.persistence.RolAccionRestringidaDao;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class AccionesServiceImpl implements AccionesService {

  private static final String PRED_ID_PRO = "idPro";

  private static final String PRED_PRO_FASES = "proFases";

  private static final String PRED_PRO_FASES_TRA = "proFasesTra";

  private static final String PRED_ID_OPE = "idOpe";

  private static final String PRED_ID_ACCION = "idAccion";

  private static final String PRED_OPERACION_ENTITY = "operacionEntity";

  private static final String PRED_PRO_FASES_TRA_OPE = "proFasesTraOpe";

  private static final String PRED_ACCION_ENTITY = "accionEntity";

  private static final String PRED_ID_ROL = "idRol";

  private static final String PRED_ACCION = "accion";

  private static final String PRED_OPERACION = "operacion";

  private static final String PRED_PROCEDIMIENTO = "procedimiento";

  private static final String PRED_ROL = "rol";

  private static final String FLG_ACTIVO = "flgActivo";

  private static final String PRED_PROCEDIMIENTO_ENTITY = "procedimientoEntity";

  @Autowired
  private AccionDao accionDao;

  @Autowired
  private AccionMapper accionMapper;

  @Autowired
  private RolAccionRestringidaDao rolAccionRestringidaDao;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesDao pftoaDao;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesMapper pftoaMapper;

  @Override
  public List<AccionDto> getAllAcciones() throws SinacException {
    List<AccionDto> resultado = new ArrayList<>();
    List<AccionEntity> lista = Lists.newArrayList(accionDao.findAll());
    lista.forEach(accion -> resultado.add(accionMapper.toDto(accion)));
    return resultado;
  }

  @Override
  public List<Long> getCodigosAccionesPermitidas(long rolId) throws SinacException {

    Specification<RolAccionRestringidaEntity> specRar = new Specification<>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<RolAccionRestringidaEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder criteriaBuilder) {
        root.fetch(PRED_ROL, JoinType.INNER);
        root.fetch(PRED_PROCEDIMIENTO, JoinType.LEFT);
        root.fetch(PRED_OPERACION, JoinType.LEFT);
        root.fetch(PRED_ACCION, JoinType.LEFT);
        Predicate predicate = criteriaBuilder.equal(root.get(FLG_ACTIVO), true);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PRED_ROL).get(PRED_ID_ROL), rolId));
//        predicate = criteriaBuilder.and(predicate,
//            criteriaBuilder.equal(root.get(PRED_PROCEDIMIENTO).get(FLG_ACTIVO), true));
//        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PRED_OPERACION).get(FLG_ACTIVO), true));
//        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PRED_ACCION).get(FLG_ACTIVO), true));
        return predicate;
      }
    };

    List<RolAccionRestringidaEntity> accionesRestringidas = rolAccionRestringidaDao.findAll(specRar);
    Set<ProcedimientosFasesTramitesOperacionesAccionesEntity> pftoaSet = new HashSet<>();
    for (RolAccionRestringidaEntity rolAccionRestringida : accionesRestringidas) {
      if (rolAccionRestringida.getProcedimiento() == null && rolAccionRestringida.getOperacion() == null
          && rolAccionRestringida.getAccion() == null) {
        continue;
      }
      Specification<ProcedimientosFasesTramitesOperacionesAccionesEntity> specPftoa = new Specification<>() {
        private static final long serialVersionUID = 1L;

        @Override
        public Predicate toPredicate(Root<ProcedimientosFasesTramitesOperacionesAccionesEntity> root,
            CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
          Predicate predicate = criteriaBuilder.equal(root.get(FLG_ACTIVO), true);
          if (rolAccionRestringida.getAccion() != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(root.get(PRED_ACCION_ENTITY).get(FLG_ACTIVO), true));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                root.get(PRED_ACCION_ENTITY).get(PRED_ID_ACCION), rolAccionRestringida.getAccion().getIdAccion()));
          }
          if (rolAccionRestringida.getOperacion() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder
                .equal(root.get(PRED_PRO_FASES_TRA_OPE).get(PRED_OPERACION_ENTITY).get(FLG_ACTIVO), true));
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(root.get(PRED_PRO_FASES_TRA_OPE).get(PRED_OPERACION_ENTITY).get(PRED_ID_OPE),
                    rolAccionRestringida.getOperacion().getIdOpe()));
          }
          if (rolAccionRestringida.getProcedimiento() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(PRED_PRO_FASES_TRA_OPE)
                .get(PRED_PRO_FASES_TRA).get(PRED_PRO_FASES).get(PRED_PROCEDIMIENTO_ENTITY).get(FLG_ACTIVO), true));
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(
                    root.get(PRED_PRO_FASES_TRA_OPE).get(PRED_PRO_FASES_TRA).get(PRED_PRO_FASES)
                        .get(PRED_PROCEDIMIENTO_ENTITY).get(PRED_ID_PRO),
                    rolAccionRestringida.getProcedimiento().getIdPro()));
          }
          return predicate;
        }
      };
      List<ProcedimientosFasesTramitesOperacionesAccionesEntity> pftoas = pftoaDao.findAll(specPftoa);
      pftoaSet.addAll(pftoas);
    }

    return pftoaSet.stream().map(pftoa -> pftoaMapper.toDto(pftoa).getIdProFaseTraOpeAcc()).toList();
  }

}
