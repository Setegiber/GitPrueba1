package es.mjusticia.sinac.core.business.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.plantillas.EvaluadorClasificacion;
import es.mjusticia.sinac.core.business.plantillas.EvaluadorClasificacionFactory;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFirmaDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlantillasClasificacionDto;
import es.mjusticia.sinac.core.model.entity.ExpedienteDocumentoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteFirmaEntity;
import es.mjusticia.sinac.core.model.entity.PlantillaEntity;
import es.mjusticia.sinac.core.model.entity.PlantillasClasificacionEntity;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.PlantillaMapper;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.PlantillaDao;

@Component
public class PlantillasServiceImpl implements PlantillasService {

  private static final Logger LOG = LoggerFactory.getLogger(PlantillasServiceImpl.class);

  @Autowired
  private PlantillaDao plantillaDao;

  @Autowired
  private PlantillaMapper plantillaMapper;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  private ExpedienteDocumentoMapper expedienteDocumentoMapper;

  @Autowired
  private EvaluadorClasificacionFactory evaluadorClasificacionFactory;

  @Autowired
  private ExpedienteDocumentoDao expedienteDocumentoDao;

  @Override
  public PlantillaDto getPlantillaPorCod(String codPlantilla) throws SinacException {
    PlantillaDto plantilla;
    try {
      plantilla = plantillaMapper.toDto(plantillaDao.getPlantillaByCod(codPlantilla));
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_58).type(SinacExceptionType.DATA);
    }
    return plantilla;
  }

  @Override
  public List<PlantillaDto> getAllPlantillasActivas() throws SinacException {
    List<PlantillaEntity> listaPlantillaEntities = plantillaDao.getAllPlantillasActivas();
    List<PlantillaDto> listaPlantillaDtos = new ArrayList<>();
    for (PlantillaEntity plantillaEntity : listaPlantillaEntities) {
      listaPlantillaDtos.add(plantillaMapper.toDto(plantillaEntity));
    }
    return listaPlantillaDtos;
  }

  @Override
  public List<PlantillaDto> getListaPlantillas(BigInteger idExp, String codTramite, String codOpe, String codAccion)
      throws SinacException {
    List<PlantillaDto> listaPlantillasFinal = new ArrayList<>();
    List<PlantillaEntity> listaPlantillas = plantillaDao.getPlantillasByIdExp(idExp, codTramite, codOpe, codAccion);
    LOG.info("Se recupera la lista de plantillas para el expediente {} en el trámite {}", idExp, codTramite);
    for (PlantillaEntity plantilla : listaPlantillas) {
      PlantillaDto plantillaDto = plantillaMapper.toDto(plantilla);
      if (!plantillaDto.getPlantillasClasificacionDtos().isEmpty()) {
        if (comprobarClasificacionesPlantilla(idExp, plantillaDto.getPlantillasClasificacionDtos())) {
          listaPlantillasFinal.add(plantillaDto);
        }
      } else {
        listaPlantillasFinal.add(plantillaDto);
      }
    }
    LOG.info("La lista final de plantillas en el expediente {} y el trámite {} tras filtrar las clasificaciones es: {}",
        idExp, codTramite, listaPlantillasFinal);
    return listaPlantillasFinal;
  }

  @Override
  public List<PlantillaDto> getListaPlantillasSinOpe(BigInteger idExp, String codTramite, String codAccion)
      throws SinacException {
    List<PlantillaDto> listaPlantillasFinal = new ArrayList<>();
    List<PlantillaEntity> listaPlantillas = plantillaDao.getPlantillasByIdExpSinOpe(idExp, codTramite, codAccion);
    for (PlantillaEntity plantilla : listaPlantillas) {
      Set<PlantillasClasificacionEntity> activas = plantilla.getPlantillasClasificacionEntities().stream()
          .filter(PlantillasClasificacionEntity::isFlgActivo).collect(java.util.stream.Collectors.toSet());
      plantilla.setPlantillasClasificacionEntities(activas);
      PlantillaDto plantillaDto = plantillaMapper.toDto(plantilla);
      if (!plantillaDto.getPlantillasClasificacionDtos().isEmpty()) {
        if (comprobarClasificacionesPlantilla(idExp, plantillaDto.getPlantillasClasificacionDtos())) {
          listaPlantillasFinal.add(plantillaDto);
        }
      } else {
        listaPlantillasFinal.add(plantillaDto);
      }
    }

    return listaPlantillasFinal;
  }

  @Override
  public boolean comprobarClasificacionesPlantilla(BigInteger idExp,
      List<PlantillasClasificacionDto> plantillasClasificacion) {

    for (PlantillasClasificacionDto plantillasClasificacionDto : plantillasClasificacion) {
      EvaluadorClasificacion evaluador = evaluadorClasificacionFactory
          .getEvaluador(plantillasClasificacionDto.getTipoClasificacion());
      if (evaluador == null || (evaluador != null && !evaluador.cumpleClasificacion(idExp))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<DocumentosTramiteDto> getDocumentosTramite(BigInteger idExp, String codTra, String codOpe, String codAcc)
      throws SinacException {
    List<PlantillaDto> listaPlantillasDto = getListaPlantillas(idExp, codTra, codOpe, codAcc);

    List<ExpedienteDocumentoEntity> listaExpedienteDocEntity = expedienteDocumentoDao
        .getExpedienteDocumentosConFirmasByIdExp(idExp);
    List<DocumentosTramiteDto> listaDocTramite = new ArrayList<>();
    for (PlantillaDto doc : listaPlantillasDto) {
      DocumentosTramiteDto docTramite = new DocumentosTramiteDto();
      docTramite.setNomDocumento(doc.getProDocTipo().getDocumentosTipo().getNomTipo());
      docTramite.setIdPla(doc.getIdPla());
      docTramite.setCodPlantilla(doc.getCodPlantilla());
      docTramite.setCodTFirmaLdv(doc.getProDocTipo().getIdDocTfirLdv().getCodLdvMae());
      boolean documentoEncontrado = false;
      ListIterator<ExpedienteDocumentoEntity> iterator = listaExpedienteDocEntity.stream().toList().listIterator();
      while (!documentoEncontrado && iterator.hasNext()) {
        ExpedienteDocumentoEntity expDoc = iterator.next();
        if (expDoc.getDocumentoTipoEntity().getIdDocTipo() == doc.getProDocTipo().getDocumentosTipo().getIdDocTipo()) {
          documentoEncontrado = true;
          docTramite.setIdExpDoc(expDoc.getIdExpDoc());
          docTramite.setEstadoDocumento(ldvMaestraMapper.toDto(expDoc.getLdvMaestraEntityByIdEstDocLdv()));
          docTramite.setFechaCreacionDocumento(expDoc.getFechaCreacion());
          docTramite.setExpedienteDocumentoDto(expedienteDocumentoMapper.toDto(expDoc));
          docTramite.setCodDocTipo(expDoc.getDocumentoTipoEntity().getCodTipo());

          for (ExpedienteFirmaEntity firmaDoc : expDoc.getExpedienteFirmaEntities()) {
            if (firmaDoc.isFlgActivo()) {
              docTramite.setFechaEnvioFirmaDocumento(firmaDoc.getFechaSolicitud());
              docTramite.setFechaFirmaDocumento(firmaDoc.getFechaRecepcion());
            }
          }
        }

      }
      listaDocTramite.add(docTramite);
    }

//    if (codTipoDoc != null) {
//      try {
//        ExpedienteDocumentoEntity expedienteDoc = expedienteDocumentoDao
//            .getExpedienteDocumentosByCodTipoDocumentoIdExpediente(idExp, codTipoDoc);
//
//        DocumentosTramiteDto docCartaApoyo = new DocumentosTramiteDto();
//        docCartaApoyo.setIdPla(null);
//        if (expedienteDoc != null) {
//          docCartaApoyo.setNomDocumento(expedienteDoc.getDocumentoTipoEntity().getNomTipo());
//          docCartaApoyo.setIdExpDoc(expedienteDoc.getIdExpDoc());
//          docCartaApoyo.setEstadoDocumento(ldvMaestraMapper.toDto(expedienteDoc.getLdvMaestraEntityByIdEstDocLdv()));
//          if (expedienteDoc.getFechaCreacion() != null) {
//            docCartaApoyo.setFechaCreacionDocumento(expedienteDoc.getFechaCreacion());
//          }
//          docCartaApoyo.setCodDocTipo(expedienteDoc.getDocumentoTipoEntity().getCodTipo());
//          for (ProcedimientosDocumentosTipoEntity proDocTipo : expedienteDoc.getDocumentoTipoEntity()
//              .getProcedimientosDocumentosTipoEntities()) {
//            docCartaApoyo.setCodTFirmaLdv(proDocTipo.getLdvMaestraEntityByIdDocTfirLdv().getCodLdvMae());
//          }
//        } else {
//          DocumentoTipoDto docTipoCarta = documentoTipoMapper
//              .toDto(documentoTipoDao.recuperarTipoDocPorCod(codTipoDoc));
//          docCartaApoyo.setNomDocumento(docTipoCarta.getNomTipo());
//          docCartaApoyo.setCodDocTipo(docTipoCarta.getCodTipo());
//          docCartaApoyo.setEstadoDocumento(ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-NRE")));
//        }
//        listaDocTramite.add(docCartaApoyo);
//
//      } catch (Exception ex) {
//        throw new SinacException(ex,
//            "Se ha producido un error al encontrar el documento especificado del expediente " + idExp)
//            .type(SinacExceptionType.DATA);
//      }
//    }

    return listaDocTramite;
  }

  @Override
  public List<DocumentosTramiteDto> getDocumentosTramiteSinOpe(BigInteger idExp, String codTra, String codAcc)
      throws SinacException {
    List<PlantillaDto> listaPlantillasDto = getListaPlantillasSinOpe(idExp, codTra, codAcc);
    // TODO Quitar distict, pero dividir las consultas en dos metodos. Una para
    // obtener los doc y otra para las firmas
    List<ExpedienteDocumentoEntity> listaExpedienteDocEntity = expedienteDocumentoDao
        .getExpDocFirmaSinFlgActivoByIdExp(idExp);
    List<ExpedienteDocumentoDto> listaExpedienteDocDto = new ArrayList<>();
    listaExpedienteDocEntity.forEach(expDoc -> listaExpedienteDocDto.add(expedienteDocumentoMapper.toDto(expDoc)));

    List<DocumentosTramiteDto> listDocumentosFinal = new ArrayList<>();
    for (PlantillaDto plantilla : listaPlantillasDto) {
      DocumentoTipoDto docTipoPlantilla = plantilla.getProDocTipo().getDocumentosTipo();
      List<ExpedienteDocumentoDto> listaDocumetosPorTipos = listaExpedienteDocDto.stream()
          .filter(expDoc -> expDoc.getDocumentoTipoDto().equals(docTipoPlantilla)).toList();

      if (!listaDocumetosPorTipos.isEmpty()) {

        List<ExpedienteDocumentoDto> listaAuxiliarMutable = new ArrayList<>(listaDocumetosPorTipos);
        listaAuxiliarMutable.sort(Comparator.comparing(ExpedienteDocumentoDto::isFlgActivo).reversed());
        if (listaAuxiliarMutable.size() > 1) {
          listaAuxiliarMutable.get(0).setFlgFlecha(true);
        }

        for (ExpedienteDocumentoDto expDoc : listaAuxiliarMutable) {
          DocumentosTramiteDto docTramite = new DocumentosTramiteDto();
          docTramite.setNomDocumento(docTipoPlantilla.getNomTipo());
          docTramite.setCodDocTipo(docTipoPlantilla.getCodTipo());
          docTramite.setIdPla(plantilla.getIdPla());
          docTramite.setCodTFirmaLdv(plantilla.getProDocTipo().getIdDocTfirLdv().getCodLdvMae());
          docTramite.setIdExpDoc(expDoc.getIdExpDoc());
          docTramite.setEstadoDocumento(expDoc.getLdvMaestraDtoByIdEstDocLdv());
          docTramite.setFechaCreacionDocumento(expDoc.getFechaCreacion());

          for (ExpedienteFirmaDto firmaDoc : expDoc.getExpedienteFirmaDtos()) {
            if (firmaDoc.isFlgActivo()) {
              docTramite.setFechaEnvioFirmaDocumento(firmaDoc.getFechaSolicitud());
              docTramite.setFechaFirmaDocumento(firmaDoc.getFechaRecepcion());
            }
          }
          docTramite.setExpedienteDocumentoDto(expDoc);
          listDocumentosFinal.add(docTramite);
        }

      } else {
        DocumentosTramiteDto docTramite = new DocumentosTramiteDto();
        docTramite.setNomDocumento(docTipoPlantilla.getNomTipo());
        docTramite.setCodDocTipo(docTipoPlantilla.getCodTipo());
        docTramite.setIdPla(plantilla.getIdPla());
        docTramite.setCodTFirmaLdv(plantilla.getProDocTipo().getIdDocTfirLdv().getCodLdvMae());
        listDocumentosFinal.add(docTramite);
      }
    }
    return listDocumentosFinal;

  }

  @Override
  public PlantillaDto getPlantillaPorTipoDocAndPro(short idPro, String codTipo) throws SinacException {
    PlantillaDto plantilla;
    try {
      plantilla = plantillaMapper.toDto(plantillaDao.getPlantillaByTipoDocPro(idPro, codTipo));
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_58).type(SinacExceptionType.DATA);
    }
    return plantilla;
  }

  @Override
  public PlantillaDto selectPlantillaByCod(List<PlantillaDto> listaPlantillas, ExpedienteDto expedienteDto) {
    for (PlantillaDto plantilla : listaPlantillas) {
      if (plantilla.getCodPlantilla().startsWith("OREC")) {
        return plantilla;
      }
    }
    return null;
  }
}
