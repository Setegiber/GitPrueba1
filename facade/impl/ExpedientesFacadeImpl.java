package es.mjusticia.sinac.core.business.facade.impl;

import java.io.File;
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
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sshtools.common.logger.Log;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.AsientosService;
import es.mjusticia.sinac.core.business.service.AvisosService;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.InformesService;
import es.mjusticia.sinac.core.business.service.ObservacionesService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.business.service.ProcedimientosAvisosService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.business.service.RequerimientosAndAudienciasService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.MotorTramitacionComponent;
import es.mjusticia.sinac.core.eis.connector.GestorDocumentalConnector;
import es.mjusticia.sinac.core.eis.connector.PortafirmasConnector;
import es.mjusticia.sinac.core.model.dto.AccionDto;
import es.mjusticia.sinac.core.model.dto.ArchivoFtpDto;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosDto;
import es.mjusticia.sinac.core.model.dto.BusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.BusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.DatosSolicitudInformeMjuDto;
import es.mjusticia.sinac.core.model.dto.DatosTramiteDto;
import es.mjusticia.sinac.core.model.dto.DescargaDeDocumentoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToRequerirDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosEntradaDto;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.EnviarEmailDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteAvisoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteBoeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteComunicacionesExternasDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteEstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFirmaDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFormularioValDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInsideDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteNotificacionesDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteRequerimientoDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPlazosDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesVinculadosDto;
import es.mjusticia.sinac.core.model.dto.FirmanteDto;
import es.mjusticia.sinac.core.model.dto.InformesDgpRecibidosDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.MaquinaEstadosDto;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PerCertificacionesDto;
import es.mjusticia.sinac.core.model.dto.PerFilNiesDto;
import es.mjusticia.sinac.core.model.dto.PerFiliacionesDto;
import es.mjusticia.sinac.core.model.dto.PerPadronDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlazoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosDocumentosTipoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.RenovacionDniDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.TipoOficioDto;
import es.mjusticia.sinac.core.model.dto.TiposViaDto;
import es.mjusticia.sinac.core.model.dto.TitulosDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.dto.ValidacionSemaforoDto;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.enums.TipoRegistroRegageEnum;
import es.mjusticia.sinac.core.model.enums.TipoRespuestaEnviarDocumentoPortafirmasEnum;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.RegistroDao;
import es.mjusticia.sinac.core.security.SinacSessionService;
import es.mjusticia.sinac.core.utils.Constantes.Plazo;
import es.mjusticia.sinac.core.utils.NFSManager;
import es.mjusticia.sinac.core.utils.Utilidades;
import es.mjusticia.sinac.core.utils.Validaciones;
import es.mjusticia.sinac.dgp.dto.TitularDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaAltaFiliacionDto;
import es.mjusticia.sinac.geiser.exception.SinacGeiserException;
import es.mjusticia.sinac.geiser.model.dto.DocumentoDto;
import es.mjusticia.sinac.geiser.model.dto.PeticionRegistroEnvioDto;
import es.mjusticia.sinac.geiser.model.dto.ResultadoRegistroEnvioDto;
import es.mjusticia.sinac.geiser.service.GeiserService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.validation.Valid;

/**
 * Clase de Implementación de {@link ExpedientesFacade}.
 *
 * @author NTT Data.
 */
@Service
@Transactional(readOnly = true)

public class ExpedientesFacadeImpl implements ExpedientesFacade {

  private static final String SENTIDO_MDE = "sentidoMde";

  private static final Logger LOG = LoggerFactory.getLogger(ExpedientesFacadeImpl.class);

  @Value(value = "${sgnec.dir3.code}")
  private String organo;

  @Value("${nfs.ruta.solicitudes}")
  private String nfsPathDocumentosSolicitudes;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ObservacionesService observacionesService;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private PlantillasService plantillasService;

  @Autowired
  private PersonasService personasService;

  @Autowired
  private DocumentosService documentosService;

  @Autowired
  private AsientosService asientosService;

  @Autowired
  private NFSManager nfsManager;

  @Autowired
  private ProcedimientosService procedimientosService;

  @Autowired
  private ProcedimientosAvisosService procedimientosAvisosService;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private RequerimientosAndAudienciasService requerimientosAndAudienciasService;

  @Autowired
  private PlazosService plazosService;

  @Autowired
  private SinacSessionService sinacSession;

  @Autowired
  private PortafirmasConnector portafirmasConnector;

  @Autowired
  private MotorTramitacionComponent motorTramitacion;

  @Autowired
  private ExpedienteDocumentoDao expedienteDocumentoDao;

  @Autowired
  private AvisosService avisoService;

  @Autowired
  private GestorDocumentalConnector gestorDocumentalConnector;

  @Autowired
  private SolicitudesService solicitudesService;

  @Autowired
  private InformesService informesService;

  @Autowired
  private RegistroDao registroDao;

  @Autowired
  private GeiserService geiserService;

  @Autowired
  private MotorTramitacionComponent motorTramitacionComponent;

  @Override
  @Transactional(readOnly = false)
  public List<DocumentoToSaveDto> saveDocumentosEntradaExpediente(final BigInteger idExpediente,
      LinkedList<DocumentoToSaveDto> documentoToSaveDtoList) throws SinacException {
    LOG.debug("Init - ExpedientesFacadeImpl.saveDocumentosEntradaExpediente del expediente {}", idExpediente);
    final ExpedienteDto expedienteDto = expedientesService.getExpedienteByIdExpediente(idExpediente);
    final Optional<DocumentoToSaveDto> findFirst = documentoToSaveDtoList.stream().findFirst();
    boolean isCreateExpediente = (findFirst.isPresent() && StringUtils.isNotEmpty(findFirst.get().getRutaNFS())
        && findFirst.get().getRutaNFS().contains(nfsPathDocumentosSolicitudes));
    documentoToSaveDtoList.replaceAll(d -> {
      if (d.getContenido() == null) {
        d.setContenido(nfsManager.getDocumentContent(d.getNombre(), d.getRutaNFS()));
      } else {
        int numOrden = expedienteDocumentoDao.getContadorDocumentosTipo(expedienteDto.getIdExp(), d.getTipoDocumento());
        numOrden += 1;
        d.setNombre(numOrden + "_" + d.getNombre());
      }
      return d;
    });
    Date fEntrada = new Date();
    for (DocumentoToSaveDto doc : documentoToSaveDtoList) {
      if (doc.getFechaEntrada() == null)
        doc.setFechaEntrada(fEntrada);
    }
    LOG.debug("Se va a pasar el antivirus para guardar los documentos en el expediente {}", idExpediente);
    solicitudesService.setEstadoSolicitud("Verificando documentos");
    documentoToSaveDtoList.replaceAll(d -> documentosService.validateDocumentoAntivirus(d));
    LOG.debug("Se va a validar para guardar los documentos en el expediente {}", idExpediente);
    solicitudesService.setEstadoSolicitud("Validando documentos");
    documentoToSaveDtoList.replaceAll(d -> documentosService.validateDocumento(d));
    LOG.debug("Se va a firmar con sello para guardar los documentos en el expediente {}", idExpediente);
    solicitudesService.setEstadoSolicitud("Firmando documentos");
    documentoToSaveDtoList.replaceAll(d -> documentosService.signDocumento(d));
    LOG.debug("Se va a registrar para guardar los documentos en el expediente {}", idExpediente);
    solicitudesService.setEstadoSolicitud("Registrando documentos");
    documentoToSaveDtoList
        .replaceAll(d -> documentosService.generateRegistroDocumentoV2(TipoRegistroRegageEnum.ENTRADA, d));
    LOG.debug("Se va a guardar los documentos en Gestor Documental para el expediente {}", idExpediente);
    solicitudesService.setEstadoSolicitud("Guardando documentos");
    documentoToSaveDtoList
        .replaceAll(d -> documentosService.saveDocumentoGestorDocumental(TipoRegistroRegageEnum.ENTRADA,
            expedienteDto.getIdExpGd(), expedienteDto.getProcedimientoDto().getIdPro(), d));
    solicitudesService.setEstadoSolicitud("Copiando documentos");
    documentoToSaveDtoList.replaceAll(d -> documentosService.copyDocumentoNFS(expedienteDto.getCodExp(),
        expedienteDto.getProcedimientoDto().getCodPro(), expedienteDto.getFechaEfectos(), d));
    documentoToSaveDtoList.replaceAll(d -> documentosService.saveDocumentoV2(idExpediente, d, isCreateExpediente));

    if (!isCreateExpediente && !documentoToSaveDtoList.isEmpty()) {
      // Requerimientos de Subsanación y Trámite de Audiencias.
      // Ejecutar Acción "Recibir Subsanación" (RSUB).

      try {
        solicitudesService.setEstadoSolicitud("Generando requerimientos");

        List<String> listaEstadosReq = List.of("REQ-NOT", "REQ-COM");
        List<ExpedienteRequerimientoDto> expedienteRequerimientoDtoList = requerimientosAndAudienciasService
            .getRequerimientosByIdExpedienteAndEstado(idExpediente, listaEstadosReq);

        if (!CollectionUtils.isEmpty(expedienteRequerimientoDtoList)) {
          Map<String, Object> valores = new HashMap<>();
          valores.put("idExp", idExpediente);
          valores.put("idPro", expedienteDto.getProcedimientoDto().getIdPro());
          valores.put("userId", sinacSession.getUsuario().getIdUsu());
          valores.put("flgNoValidar", 1);
          valores.put("requerimientosNotificados", expedienteRequerimientoDtoList);

          if ("IN".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
            ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                expedienteDto.getProcedimientoDto().getCodPro(), "INS", "AIN", "SAIN", "RSUB"), valores);
          } else {
            boolean ejecucionAud = false;
            boolean ejecucionReq = false;
            for (ExpedienteRequerimientoDto expReq : expedienteRequerimientoDtoList) {
              if (expReq.isAudiencia() && !ejecucionAud) {
                ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                    expedienteDto.getProcedimientoDto().getCodPro(), "INS", "REV", "SUBA", "RSUB"), valores);
                ejecucionAud = true;
              } else if (!ejecucionReq) {
                ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                    expedienteDto.getProcedimientoDto().getCodPro(), "INS", "REV", "SUB", "RSUB"), valores);
                ejecucionReq = true;
              }
              // Si ambas acciones ya se han ejecutado, podemos salir del bucle
              if (ejecucionAud && ejecucionReq) {
                break;
              }
            }
          }
        }
        if ("RCA".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
          boolean existeDocAdjunto = documentoToSaveDtoList.stream()
              .anyMatch(doc -> "STNAB".equals(getExpedienteDocumentoByTipoDocId(doc.getTipoDocumento()).getCodTipo()));
          if (existeDocAdjunto) {
            Map<String, Object> valores = new HashMap<>();
            valores.put("idExp", idExpediente);
            valores.put("idUsu", sinacSession.getUsuario().getIdUsu());
            valores.put("idPro", expedienteDto.getProcedimientoDto().getIdPro());
            ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                expedienteDto.getProcedimientoDto().getCodPro(), "INS", "STN", "SRC", "RSFA"), valores);
          }
        }

        List<MaquinaEstadosDto> listaAccionesDisponibles = motorTramitacion
            .getListaAccionesDisponiblesPorIdExp(idExpediente, null, expedienteDto.getProcedimientoDto().getIdPro());
        for (MaquinaEstadosDto maquinaEstadosDto : listaAccionesDisponibles) {
          if (documentoToSaveDtoList.stream().anyMatch(doc -> {
            AccionDto accionDto = getExpedienteDocumentoByTipoDocId(doc.getTipoDocumento()).getAccionDto();
            return accionDto != null && accionDto.getIdAccion() != 0
                && accionDto.getIdAccion() == maquinaEstadosDto.getAccion().getAccionDto().getIdAccion();
          })) {
            Map<String, Object> valores = new HashMap<>();
            valores.put("idExp", idExpediente);
            valores.put("idUsu", sinacSession.getUsuario().getIdUsu());
            valores.put("idPro", expedienteDto.getProcedimientoDto().getIdPro());
            ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
                expedienteDto.getProcedimientoDto().getCodPro(), "INS",
                maquinaEstadosDto.getAccion().getProFasesTraOpe().getProFasesTra().getTramiteDto().getCodTramite(),
                maquinaEstadosDto.getAccion().getProFasesTraOpe().getOperacionDto().getCodOpe(),
                maquinaEstadosDto.getAccion().getAccionDto().getCodAccion()), valores);
          }
        }

      } catch (Exception e) {
        LOG.error(
            "Error ejecutando acción al adjuntar un documento del expediente con id {} y código de expediente {}: {}",
            expedienteDto.getIdExp(), expedienteDto.getCodExp(), e.getMessage());
      }
    }

    LOG.debug("End - ExpedientesFacadeImpl.saveDocumentosEntradaExpediente");
    return documentoToSaveDtoList;
  }

  @Override
  @Transactional(readOnly = false)
  public DocumentoToSaveDto reintentoSubidaGestorDocumental(final BigInteger idExpediente, final BigInteger idExpDoc)
      throws SinacException {
    LOG.info("ExpedientesFacadeImpl.reintentoSubidaGestorDocumental - Init");
    final ExpedienteDocumentoDto expedienteDocumentoDto = documentosService
        .getExpedienteDocumentoByIdDocumento(idExpDoc);
    DocumentoToSaveDto documentoToSaveDto = expedientesService.getDocumentoToSaveDtoReintentoGD(expedienteDocumentoDto,
        true);
    final ExpedienteDto expedienteDto = expedientesService.getExpedienteByIdExpediente(idExpediente);
    ProcedimientosDocumentosTipoDto proDocTipoDto = expedienteDocumentoDto.getDocumentoTipoDto()
        .getProcedimientosDocumentosTipoDtos().stream()
        .filter(pro -> pro.getProcedimiento().getIdPro().equals(expedienteDto.getProcedimientoDto().getIdPro()))
        .toList().get(0);
    documentoToSaveDto = expedientesService.reintentoSubidaGestorDocumental(documentoToSaveDto, expedienteDto,
        expedienteDocumentoDto, nfsPathDocumentosSolicitudes, proDocTipoDto.isFlgDocEnt(),
        proDocTipoDto.getIdEstDocLdv());
    if ("RCA".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
      if ("STNAB".equals(expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo())) {
        Map<String, Object> valores = new HashMap<>();
        valores.put("idExp", idExpediente);
        valores.put("idPro", expedienteDto.getProcedimientoDto().getIdPro());
        ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
            expedienteDto.getProcedimientoDto().getCodPro(), "INS", "STN", "SRC", "RSFA"), valores);
      }
    }
    LOG.info("ExpedientesFacadeImpl.reintentoSubidaGestorDocumental - End");
    return documentoToSaveDto;
  }

  @Override
  @Transactional(readOnly = false)
  public void setUsuarioToExpediente(BigInteger idExp) throws SinacException {
    expedientesService.setUsuarioToExpediente(idExp);
  }

  @Override
  @Transactional(readOnly = false)
  public void unsetUsuarioToExpediente(BigInteger idExp, Integer idUsu) throws SinacException {
    expedientesService.unsetUsuarioToExpediente(idExp, idUsu);
  }

  @Override
  public ExpedienteDto getDetalleExpediente(BigInteger idExp) throws SinacException {
    return expedientesService.getExpedientebyId(idExp);
  }

  @Override
  public Page<ResultadoBusquedaExpedientesDto> getExpedientesPaginated(BusquedaExpedientesDto busquedaDto,
      Pageable pageable) throws SinacException {
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();

    busquedaDto = expedientesService.resolverFiltroTarjetaInicio(busquedaDto);
    Map<Integer, List<ResultadoBusquedaExpedientesDto>> mapa = expedientesService.getExpedientesPaginated(busquedaDto,
        pageable);
    return new PageImpl<>(mapa.values().stream().toList().get(0), PageRequest.of(currentPage, pageSize),
        mapa.keySet().stream().toList().get(0));
  }

  @Override
  public List<ResultadoBusquedaExpedientesDto> getExpedientesFiltrados(BusquedaExpedientesDto busquedaDto,
      Pageable pageable) throws SinacException {

    busquedaDto = expedientesService.resolverFiltroTarjetaInicio(busquedaDto);
    return expedientesService.getExpedientesFiltrados(busquedaDto, pageable);
  }

  @Transactional(readOnly = false)
  public boolean convertirDocumentoEditableEnPdf(ExpedienteDocumentoDto expDoc) throws SinacException {
    return documentosService.convertirDocumentoEditableEnPdf(expDoc);
  }

  @Override
  public List<PlantillaDto> getPlantillas() {
    return documentosService.getPlantillas();
  }

  @Override
  public PlantillaDto getPlantillaById(short idPlantilla) throws SinacException {
    return documentosService.getPlantillaById(idPlantilla);
  }

  @Override
  public List<String> validateErroresPlantillas(PlantillaDto plantillaDto, ExpedienteDto expedienteDto)
      throws SinacException {
    List<String> errores = new ArrayList<>();
    DataSource dataSource = nfsManager.getDataSource(plantillaDto.getNomPlantilla() + ".odt",
        plantillaDto.getNfsRuta());
    if (dataSource == null) {
      errores.add("No se ha encontrado la plantilla seleccionada. ");
    }

    return errores;
  }

  @Override
  public ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException {
    return documentosService.getExpedienteDocumentoByIdDocumento(idDocumento);
  }

  @Override
  public String getUrlDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException {
    return documentosService.getUrlDocumentoByIdDocumento(idDocumento);
  }

  @Override
  public DescargaDeDocumentoDto getArchivoByIdDocExp(BigInteger idDocExp) throws SinacException {
    return documentosService.getArchivoByIdDocExp(idDocExp);
  }

//FUTURE: Método "gesdocObtenerExpedienteEni" a usar cuando se tenga disponible en el conector de milano
//  @Override
//  public DescargaDeDocumentoDto getArchivoExpedienteENIByIdExp(BigInteger idExp) throws SinacException {
//    return expedientesService.getExpedienteENI(idExp);
//  }

  @Override
  public DescargaDeDocumentoDto descargarDocumentoCopiaAutentica(BigInteger idDocExp) throws SinacException {
    return documentosService.descargarDocumentoCopiaAutentica(idDocExp);
  }

  @Override
  public ExpedienteDocumentoDto generarIndiceElectronico(BigInteger idExp) throws SinacException {
    return documentosService.generarIndiceElectronico(idExp);
  }

  @Override
  public List<DocumentoTipoDto> getComboDocumentoTipo() {

    return documentosService.getComboDocumentoTipo(null);
  }

  @Override
  public List<LdvMaestraDto> getComboLdvMaestraByCodLdvEntidadMaestra(String idLdvEntMae) throws SinacException {

    return catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod(idLdvEntMae);
  }

  @Override
  @Transactional(readOnly = false)
  public void updateDetalleExpediente(ExpedienteDto detalleExpedienteDto, String segmentoActualizar)
      throws SinacException {
    expedientesService.updateDetalleExpediente(detalleExpedienteDto, segmentoActualizar);
  }

  @Override
  @Transactional(readOnly = false)
  public void anadirObservacion(ExpedienteDto expediente, String titulo, String mensaje) throws SinacException {
    observacionesService.saveExpedienteObservacion(expediente, titulo, mensaje);
  }

  @Override
  public List<PaisesDto> getPaises() throws SinacException {
    return expedientesService.getPaises();
  }

  @Override
  public List<ProvinciasDto> getProvincias() throws SinacException {
    return expedientesService.getProvincias();
  }

  @Override
  public List<LocalidadesDto> getLocalidades() throws SinacException {
    return expedientesService.getLocalidades();
  }

  @Override
  public List<LdvMaestraDto> getTiposIdentificacionDetalleExp() throws SinacException {
    return expedientesService.getTiposIdentificacionDetalleExp();
  }

  @Override
  public List<LdvMaestraDto> getSexoDetalleExp() throws SinacException {
    return expedientesService.getSexoDetalleExp();
  }

  @Override
  public List<LdvMaestraDto> getEstCivilDetalleExp() throws SinacException {
    return expedientesService.getEstCivilDetalleExp();
  }

  @Override
  @Transactional(readOnly = false)
  public void validarRechazarDoc(BigInteger idDocExp, Integer operacion) throws SinacException {
    documentosService.validarRechazarDoc(idDocExp, operacion);
  }

  @Override
  public Map<String, ExpedienteInformeDto> getListaExpedienteInformeByExpId(BigInteger idExp) throws SinacException {

    return expedientesService.getListaExpedienteInformeByExpId(idExp);
  }

  @Override
  public List<ExpedienteComunicacionesExternasDto> getListaExpedienteComunicacionesExternasByExpId(BigInteger idExp)
      throws SinacException {

    return expedientesService.getListaExpedienteComunicacionesExternasByExpId(idExp);
  }

  public List<TiposViaDto> getTiposVia() throws SinacException {
    return expedientesService.getTiposVia();
  }

  @Override
  public void sendEmail(BigInteger idExpediente, @Valid EnviarEmailDto enviarEmailDto) throws SinacException {
    expedientesService.sendEmail(idExpediente, enviarEmailDto);

  }

  @Override
  public List<LdvMaestraDto> getComboTipoRc() throws SinacException {
    return catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod("PER_RC");
  }

  @Override
  public void informeSolicitado(BigInteger idExp, String tipoInforme, BigInteger idExpInf) throws SinacException {

    expedientesService.informeSolicitado(idExp, tipoInforme, idExpInf);
  }

  @Override
  @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = false)
  public void informeRecibido(BigInteger idExp, BigInteger idExpInforme,
      ExpedienteInformeDgpDto expedienteInformeDgpDto, Date fechaEmision, Date fechaRecepcion, String sentido,
      BigInteger idExpedienteDocumento) throws SinacException {

    expedientesService.informeRecibido(idExp, idExpInforme, expedienteInformeDgpDto, fechaEmision, fechaRecepcion,
        sentido, idExpedienteDocumento);
  }

  @Override
  @Transactional(readOnly = false)
  public ExpedienteNotificacionesDto sincronizarEnvio(String estado, String identificador, boolean acusePDF,
      byte[] contenidoPDF, String hashPDF, String accion, String identificadorNot, BigInteger modoNot,
      Date fechaCambioEstado) throws SinacException {
    LOG.debug("ExpedientesFacadeImpl.sincronizarEnvio - Init");
    ExpedienteNotificacionesDto expedienteNotificacionesdto = null;
    if (estado != null && !estado.isEmpty()) {
      expedienteNotificacionesdto = expedientesService.getExpedienteNotificacionesbyIdSolSun(identificador);
      if (identificadorNot != null && !identificadorNot.isEmpty()) {
        expedienteNotificacionesdto.setIdSolNot(identificadorNot);
      }
      LOG.info("ExpedientesFacadeImpl.sincronizarEnvio - recepción acuse {}", acusePDF);

      ExpedienteDocumentoDto expedienteDocumentoDto = documentosService
          .getExpedienteDocumentoByIdDocumento(expedienteNotificacionesdto.getExpedienteDocumentoDto().getIdExpDoc());

      LOG.info("ExpedientesFacadeImpl.sincronizarEnvio - validacion estado {} modo notificación {}", estado, modoNot);
      if (Boolean.TRUE
          .equals(expedientesService.isEstadoRetroaccion(expedienteDocumentoDto.getExpedienteDto().getIdExp()))
          && "CDOC".equals(accion)) {
        ExpedienteFormularioValDto expForm = expedientesService
            .getExpedienteFormularioCampo(expedienteDocumentoDto.getExpedienteDto().getIdExp(), "TPRES");
        if (expForm != null && expForm.getValor() != null) {
          Map<String, Object> modelMap = new HashMap<>();
          modelMap.put("idExp", expedienteDocumentoDto.getExpedienteDto().getIdExp());
          // Acción para ejecutar la retroacción del exp origen
          ejecutarAccion(Long.parseLong(expForm.getValor()), modelMap);
        }
      }
      expedienteNotificacionesdto.setEstNoti(estado);
      expedienteNotificacionesdto.setFechaNotificacion(fechaCambioEstado);
      // Si hay acuse, se genera el documento
      expedienteNotificacionesdto = saveDocumentoAcuseYExpedienteNotificacion(estado, acusePDF, contenidoPDF, hashPDF,
          expedienteNotificacionesdto, expedienteDocumentoDto);
      LOG.info("ExpedientesFacadeImpl.sincronizarEnvio - actualizar documento notificado con ID {}",
          expedienteDocumentoDto.getIdExpDoc());
      actualizarDocumentoNotifica(expedienteDocumentoDto, estado);
    } else {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_105).logMessageParams(identificador);
    }
    LOG.debug("ExpedientesFacadeImpl.sincronizarEnvio - End");
    return expedienteNotificacionesdto;
  }

  private ExpedienteNotificacionesDto saveDocumentoAcuseYExpedienteNotificacion(String estado, boolean acusePDF,
      byte[] contenidoPDF, String hashPDF, ExpedienteNotificacionesDto expedienteNotificacionesdto,
      ExpedienteDocumentoDto expedienteDocumentoDto) {
    try {
      if (acusePDF) {
        LOG.info("ExpedientesFacadeImpl.sincronizarEnvio - guardar acuse ");
        LOG.info("HASH DEL PDF: " + hashPDF);
        LOG.info("ARRAY DE BYTES DEL PDF: " + Arrays.toString(contenidoPDF));
        expedienteNotificacionesdto.setFechaRecepcionAcuse(new Date());
        List<DocumentoToSaveDto> acuseDocSaved = saveDocumentoAcuse(contenidoPDF, hashPDF, expedienteDocumentoDto);
        // Recuperamos el ExpedienteDocumento del acuse.
        ExpedienteDocumentoDto acuseDto = documentosService
            .getExpedienteDocumentoByIdDocumento(acuseDocSaved.get(0).getIdExpedienteDocumento());
        expedienteNotificacionesdto.setExpedienteDocumentoAcuseDto(acuseDto);
      }
      expedienteNotificacionesdto = saveExpedienteNotificacionByEstadoReceptorExpedienteDocumento(estado,
          expedienteNotificacionesdto, expedienteDocumentoDto);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_106);
    }
    return expedienteNotificacionesdto;
  }

  private ExpedienteNotificacionesDto saveExpedienteNotificacionByEstadoReceptorExpedienteDocumento(String estado,
      ExpedienteNotificacionesDto expedienteNotificacionesdto, ExpedienteDocumentoDto expedienteDocumentoDto)
      throws SinacException {
    LOG.info("ExpedientesFacadeImpl.saveExpedienteNotificacionByEstadoReceptorExpedienteDocumento - Init ");
    expedienteNotificacionesdto = expedientesService.saveExpedienteNotificaciones(expedienteNotificacionesdto,
        expedienteDocumentoDto);
    LOG.info(
        "ExpedientesFacadeImpl.saveExpedienteNotificacionByEstadoReceptorExpedienteDocumento - guardada notificación con id "
            + expedienteNotificacionesdto.getIdExpNoti());
    LOG.info("ExpedientesFacadeImpl.saveExpedienteNotificacionByEstadoReceptorExpedienteDocumento - Fin ");
    return expedienteNotificacionesdto;
  }

  private void actualizarDocumentoNotifica(ExpedienteDocumentoDto expedienteDocumentoDto, String estado)
      throws SinacException {
    String codigoEstadoLdv = switch (estado.toLowerCase()) {
    case "notificada", "rehusada", "expirada" -> "EDOC-NOT";
    case "error", "extraviada", "sin_informacion" -> "EDOC-NER";
    case "ausente", "desconocido", "direccion_incorrecta" -> "EDOC-NIN";
    case "leida" -> "EDOC-COM";
    default -> null;
    };

    // Se actualiza el estado del documento
    if (codigoEstadoLdv != null) {
      expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(catalogosService.getCatalogoByCod(codigoEstadoLdv));
      documentosService.saveExpedienteDocumento(expedienteDocumentoDto, expedienteDocumentoDto.getExpedienteDto());
    }
  }

  private List<DocumentoToSaveDto> saveDocumentoAcuse(byte[] contenidoAcuse, String hashAcuse,
      ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException {
    LOG.info("ExpedientesFacadeImpl.saveDocumentoAcuse - Init");
    LinkedList<DocumentoToSaveDto> documentosToSaveDto = new LinkedList<>();
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
    if (contenidoAcuse != null) {
      documentoToSaveDto.setContenido(contenidoAcuse);
      if (hashAcuse != null) {
        if (!hashAcuse.equals(Utilidades.getHashSha256ForFile(contenidoAcuse))) {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_4).type(SinacExceptionType.DATA);
        }
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_5).type(SinacExceptionType.DATA);
      }
    } else {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_6).type(SinacExceptionType.DATA);
    }

    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    documentoToSaveDto
        .setNombre("ACUSE_" + expedienteDocumentoDto.getNomDoc().replaceAll(".pdf", "") + "_" + timeStamp + ".pdf");
    documentoToSaveDto.setOrigen(catalogosService.getCatalogoByCod("DOC-ADM").getIdLdvMae());
    documentoToSaveDto.setEstadoElaboracion(catalogosService.getCatalogoByCod("EE99").getIdLdvMae());
    documentoToSaveDto.setGenerarRegistro(true);
    LdvMaestraDto catalogoGd = catalogosService.getCatalogoByCod("TD09");
    LdvMaestraDto catalagoReg = catalogosService.getCatalogoByCod("TREG-DOC-AD");
    documentoToSaveDto.setTipoDocumento(documentosService
        .getTipoDocumentoPorCodGdCodReg(catalogoGd.getIdLdvMae(), catalagoReg.getIdLdvMae()).getIdDocTipo());
    // TODO Revisar organo
    documentoToSaveDto.setOrgano(catalogosService.getCatalogoByCod("ORG-JUS").getIdLdvMae());
    documentoToSaveDto.setFechaRegistro(new Date());
    documentoToSaveDto.setCreadoPor(usuariosService.getUsuarioByUsuarioJusticia("ADVNF"));
    documentosToSaveDto.add(documentoToSaveDto);
    List<DocumentoToSaveDto> documentosSaved = saveDocumentosEntradaExpediente(
        expedienteDocumentoDto.getExpedienteDto().getIdExp(), documentosToSaveDto);
    LOG.info("ExpedientesFacadeImpl.saveDocumentoAcuse - End");
    return documentosSaved;
  }

  @Override
  public LdvMaestraDto getLdvByCod(String codLdv) {
    return expedientesService.getLdvByCod(codLdv);
  }

  @Override
  public void validarTodosInformes(BigInteger idExp) throws SinacException {
    expedientesService.validarTodosInformes(idExp);
  }

  @Transactional(readOnly = false)
  public TipoRespuestaEnviarDocumentoPortafirmasEnum sendDocumentoToPortafirmas(final BigInteger idDocumentoExpediente,
      final long idProFaseTraOpe) throws SinacException {
    final ExpedienteDocumentoDto expedienteDocumentoDto = documentosService
        .getExpedienteDocumentoByIdDocumentoExpediente(idDocumentoExpediente);

    List<FirmanteDto> firmantes = null;
    try {
      firmantes = documentosService.getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento(
          expedienteDocumentoDto.getExpedienteDto().getProcedimientoDto().getIdPro(),
          expedienteDocumentoDto.getDocumentoTipoDto().getIdDocTipo());
    } catch (final SinacException sinacException) {
      if (NoSuchElementException.class.equals(sinacException.getCause().getClass())) {
        return TipoRespuestaEnviarDocumentoPortafirmasEnum.ERROR_FIRMANTES_DOCUMENTO;
      } else {
        throw sinacException;
      }
    }

    if (!Validaciones.validarExtensionDocumento(expedienteDocumentoDto.getNomDoc())) {
      try {
        if (!convertirDocumentoEditableEnPdf(expedienteDocumentoDto)) {
          return TipoRespuestaEnviarDocumentoPortafirmasEnum.ERROR_FORMATO_PDF_DOCUMENTO;
        }
      } catch (final SinacException sinacException) {
        Log.error("Error convirtiendo documento a PDF: {} - {}",
            TipoRespuestaEnviarDocumentoPortafirmasEnum.ERROR_FORMATO_PDF_DOCUMENTO, sinacException.getMessage());

        return TipoRespuestaEnviarDocumentoPortafirmasEnum.ERROR_FORMATO_PDF_DOCUMENTO;
      }
    }

    final DataHandler contenido = new DataHandler(
        nfsManager.getDataSource(expedienteDocumentoDto.getNomDoc(), expedienteDocumentoDto.getNfsRuta()));

    firmantes = firmantes.stream().sorted(Comparator.comparingInt(FirmanteDto::getOrden)).toList();

    String requestId = portafirmasConnector.createRequest(expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo(),
        expedienteDocumentoDto.getExpedienteDto().getCodExp(), expedienteDocumentoDto.getNomDoc(), contenido,
        firmantes);

    requestId = portafirmasConnector.sendRequest(requestId);

    final ProcedimientosFasesTramitesOperacionesDto procedimientosFasesTramitesOperacionesDto = procedimientosService
        .getProcedimientosFasesTramitesOperacionesByIdProFaseTraOpe(idProFaseTraOpe);

    final LdvMaestraDto enviado = catalogosService.getCatalogoByCod("FIR-ENV");

    final ExpedienteFirmaDto expedienteFirmaDto = new ExpedienteFirmaDto(expedienteDocumentoDto,
        procedimientosFasesTramitesOperacionesDto, requestId, enviado, new Date());

    documentosService.saveExpedienteFirma(expedienteFirmaDto);

    final LdvMaestraDto enviadoAFirma = catalogosService.getCatalogoByCod("EDOC-ENF");

    documentosService.updateEstadoDocumento(expedienteDocumentoDto.getIdExpDoc(), enviadoAFirma);

    return TipoRespuestaEnviarDocumentoPortafirmasEnum.OK;
  }

  public List<DocumentosTramiteDto> getDocumentosConsejoMinistros(BigInteger idExp) throws SinacException {
    return expedientesService.getDocumentosConsejoMinistros(idExp);
  }

  @Override
  public EnviarEmailDto setCamposPredefEmailAcuerdoConMin(EnviarEmailDto enviarEmailDto, String interesado,
      ExpedienteDto expediente) throws SinacException {
    return expedientesService.setCamposPredefEmailAcuerdoConMin(enviarEmailDto, interesado, expediente);
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)
  public DatosTramiteDto ejecutarAccion(long idProFasTraOpeAcc, Map<String, Object> valores) throws SinacException {
    DatosTramiteDto datosTramite = null;
    while (idProFasTraOpeAcc > 0) {
      idProFasTraOpeAcc = motorTramitacion.procesarEjecucion(idProFasTraOpeAcc, valores);
    }
    return datosTramite;
  }

  @Override
  public List<ResultadoBusquedaExpedientesDto> getExpedientesAsignadosPublicacionBoe(BigInteger idExp)
      throws SinacException {
    return expedientesService.getExpedientesAsignadosPublicacionBoe(idExp);
  }

  @Override
  public List<ExpedienteEstadoDto> getExpedienteEstadoByIdExp(BigInteger idExp) throws SinacException {
    return expedientesService.getExpedienteEstadoByIdExp(idExp);
  }

  @Override
  public String getPlazoArchivoElectronico(BigInteger idExp) throws SinacException {
    return expedientesService.getPlazoArchivoElectronico(idExp);
  }

  @Override
  public Map<String, Object> getResumenExpediente(BigInteger idExpediente) throws SinacException {
    return expedientesService.getResumenExpediente(idExpediente);
  }

  @Override
  public void verificarExpedienteTramite(BigInteger idExpediente, String codTramite) throws SinacException {
    // TODO implementar
  }

  @Override
  public List<ExpedienteDocumentoDto> obtenerDocumentosExpedientesPorCodigos(List<String> codigos,
      BigInteger idExpediente) throws SinacException {
    return expedientesService.obtenerDocumentosExpedientesPorCodigos(codigos, idExpediente);
  }

  @Override
  public PlantillaDto getPlantillaPorCod(String codPlantilla) {
    return plantillasService.getPlantillaPorCod(codPlantilla);
  }

  @Override
  public ExpedienteDto getExpedienteById(BigInteger idExp) throws SinacException {
    return expedientesService.getExpedientebyIdExp(idExp);
  }

  @Override
  public void saveDatosResolucion(BigInteger idExpediente, Date fechaCertificacion, Date fechaPublicacionBoe,
      Date fechaRecepcionAcuerdo, Integer resultadoAcuerdo, LdvMaestraEntity ldvMaestraEntity, String estadoRetroaccion)
      throws SinacException {
    expedientesService.saveDatosResolucion(idExpediente, fechaCertificacion, fechaPublicacionBoe, fechaRecepcionAcuerdo,
        resultadoAcuerdo, ldvMaestraEntity, estadoRetroaccion);
  }

  @Override
  @Transactional(readOnly = false)
  public ExpedienteDocumentoDto saveDocumentoPlantilla(PlantillaDto plantillaDto, ExpedienteDto expedienteDto)
      throws SinacException {
    return documentosService.createExpedienteDocumentoPlantillaContent(plantillaDto, expedienteDto,
        new ExpedienteDocumentoDto(), null);
  }

  @Override
  public LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSaveExp(DocumentosEntradaDto documentosEntrada) {
    return documentosService.transformMultipartToDocumentoToSaveExp(documentosEntrada);
  }

  public DocumentoTipoDto getExpedienteDocumentoByTipoDocCod(String cod_tipo) throws SinacException {
    return documentosService.getDocumentoTipoEntityByCod(cod_tipo);
  }

  @Override
  public DocumentoTipoDto getExpedienteDocumentoByTipoDocId(short idTipoDoc) throws SinacException {
    return documentosService.getDocumentoTipoByIdDocumentoTipo(idTipoDoc);
  }

  public List<BigInteger> getIdsExpedienteInformesByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme)
      throws SinacException {

    return expedientesService.getIdsExpedienteInformesByCodEstadoCodTipoInforme(codEstado, codTipoInforme);
  }

  @Override
  public BigInteger getIdExpedienteByCodExpediente(String codExp) throws SinacException {
    return expedientesService.getIdExpedienteByCodExpediente(codExp);
  }

  @Override
  public TitularDto getDatosSolicitudInformeDgp(BigInteger idExpInforme) throws SinacException {
    return expedientesService.getDatosSolicitudInformeDgp(idExpInforme);
  }

  @Override
  public Map<String, Object> getIdExpCodProceByCodExpediente(String codExpediente, String tipoInforme)
      throws SinacException {
    return expedientesService.getIdExpCodProceByCodExpediente(codExpediente, tipoInforme);
  }

  @Override
  public Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(String codPro, String codTramite,
      String codOpe, String codAccion) throws SinacException {
    return procedimientosService.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(codPro,
        codTramite, codOpe, codAccion);
  }

  @Override
  public Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(long idProFasTraOpe,
      String codAccion) throws SinacException {
    return procedimientosService.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(idProFasTraOpe,
        codAccion);
  }

  @Override
  public DatosSolicitudInformeMjuDto obtenerDatosSolicitudInformeMju(final BigInteger idExpediente)
      throws SinacException {
    return expedientesService.obtenerDatosSolicitudInformeMju(idExpediente);
  }

  @Override
  @Transactional(readOnly = false)
  public void cambiarEstadoInformesAsolicitado(String nombreArchivo, String codigoEstado, String tipoInforme,
      BigInteger idExp) throws SinacException {
    expedientesService.cambiarEstadoInformesAsolicitado(nombreArchivo, codigoEstado, tipoInforme, idExp);
  }

  @Override
  @Transactional(readOnly = false)
  public void guardaExpedienteInformesMjuFicheros(String nombreArchivo, String codigoEstado) throws SinacException {
    expedientesService.guardaExpedienteInformesMjuFicheros(nombreArchivo, codigoEstado);
  }

  @Override
  public boolean copyArchivoFtpNFS(String nombreArchivo, byte[] contenido, String ruta) throws SinacException {
    return documentosService.copyArchivoFtpNFS(nombreArchivo, contenido, ruta);
  }

  @Override
  public boolean borrarArchivoFtpNFS(String nombreArchivo, String ruta) throws SinacException {
    return documentosService.borrarArchivoFtpNFS(nombreArchivo, ruta);
  }

  @Override
  public boolean existeExpedienteInformesMjuFichero(String nombreArchivo) throws SinacException {
    return expedientesService.existeExpedienteInformesMjuFichero(nombreArchivo);
  }

  @Override
  public DocumentoToSaveDto getDocumentoToSaveDtoMju(ArchivoFtpDto archivoFtpDto) throws SinacException, IOException {
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
    documentoToSaveDto.setTipoDocumento(documentosService.getDocumentoTipoEntityByCod("INMJU").getIdDocTipo());
    documentoToSaveDto.setEstadoElaboracion(catalogosService.getCatalogoByCod("EE01").getIdLdvMae());
    documentoToSaveDto.setOrgano(catalogosService.getCatalogoByCod("ORG-MJU").getIdLdvMae());
    documentoToSaveDto.setOrigen(catalogosService.getCatalogoByCod("DOC-ADM").getIdLdvMae());
    documentoToSaveDto.setNombre(archivoFtpDto.getPdf().getName().replace(".PDF", ".pdf"));
    documentoToSaveDto.setRutaNFS(nfsPathDocumentosSolicitudes);
    documentoToSaveDto.setContenido(FileUtils.readFileToByteArray(archivoFtpDto.getPdf()));
    return documentoToSaveDto;
  }

  @Override
  @Transactional(readOnly = false)
  public void guardaRespuestaInformeMjuPenados(ArchivoFtpDto archivoFtpDto) throws SinacException {
    expedientesService.guardaRespuestaInformeMjuPenados(archivoFtpDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void actualizarEstadoArchivoFtp(String nombreArchivo, String codigoEstado) throws SinacException {
    expedientesService.actualizarEstadoArchivoFtp(nombreArchivo, codigoEstado);
  }

  @Override
  public List<BigInteger> getIdsExpedienteByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme,
      Integer maxItem) throws SinacException {
    return expedientesService.getIdsExpedienteByCodEstadoCodTipoInforme(codEstado, codTipoInforme, maxItem);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public boolean actualizaInformeDgpRechazado(BigInteger idExp, String codigoEstado, String codigoEstadoSec,
      String literalError, String codigoPeticionRespuesta, boolean alta) throws SinacException {
    return expedientesService.actualizaInformeDgpRechazado(idExp, codigoEstado, codigoEstadoSec, literalError,
        codigoPeticionRespuesta, alta);
  }

  @Override
  @Transactional(readOnly = false)
  public void desactivarInformesActivosError(BigInteger idExp, String tipoInforme) throws SinacException {
    expedientesService.desactivarInformesActivosError(idExp, tipoInforme);
  }

  @Override
  @Transactional(readOnly = false)
  public void informeSolicitadoDgp(BigInteger idExp, String tipoInforme, Date date, String codigoPeticionRespuesta,
      BigInteger idExpInforme) throws SinacException {
    expedientesService.informeSolicitadoDgp(idExp, tipoInforme, date, codigoPeticionRespuesta, idExpInforme);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void saveDatosInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgpDto,
      List<ExpedienteInformeDgpTramiteDto> expedienteInformeDgpTramites, List<RenovacionDniDto> renovacionesDniDto)
      throws SinacException {
    expedienteInformeDgpDto = expedientesService.saveExpedienteInformeDgp(expedienteInformeDgpDto);
    if (expedienteInformeDgpTramites != null) {
      for (ExpedienteInformeDgpTramiteDto expedienteInformeDgpTramiteDto : expedienteInformeDgpTramites) {
        expedienteInformeDgpTramiteDto.setExpedienteInformeDgpDto(expedienteInformeDgpDto);
        // Se da de alta ExpedienteInformeDgpTramite
        expedientesService.saveExpedienteInfomeDgpTramite(expedienteInformeDgpTramiteDto);
      }
    }
    if (renovacionesDniDto != null) {
      for (RenovacionDniDto renovacionDniDto : renovacionesDniDto) {
        renovacionDniDto.setExpedienteInformeDgpDto(expedienteInformeDgpDto);
        // Se da de alta RenovaciónDni
        expedientesService.saveRenovacionDni(renovacionDniDto);
      }
    }
  }

  @Override
  public ExpedienteInformeDto getExpedienteInformeById(BigInteger id) throws SinacException {
    return documentosService.getExpedienteInformeById(id);
  }

  @Override
  public boolean checkUsarioAsignadoExpediente(BigInteger idExp, Integer idUsuario) throws SinacException {
    return expedientesService.checkUsarioAsignadoExpediente(idExp, idUsuario);
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderCniByidExp(BigInteger idExp)
      throws SinacException {
    return expedientesService.getPftoaResponderCniByidExp(idExp);
  }

  @Override
  public PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException {
    return personasService.getPersonaByIdPer(idPer);
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesDto getPftobyCod(BigInteger idExpediente, String codFase,
      String codTramite, String codOpe) throws SinacException {

    return expedientesService.getPftobyCod(idExpediente, codFase, codTramite, codOpe);
  }

  @Override
  public List<PlantillaDto> getListaPlantillas(BigInteger idExp, String codTramite, String codOpe, String codAccion)
      throws SinacException {

    return plantillasService.getListaPlantillas(idExp, codTramite, codOpe, codAccion);
  }

  @Override
  public TitulosDto obtenerTitulosEducacion(BigInteger idPersona) throws SinacException {
    return expedientesService.obtenerTitulosEducacion(idPersona);
  }

  @Override
  @Transactional(readOnly = false)
  public TitulosDto consultarTitulosEducacion(ExpedienteDto expedienteDto) throws SinacException {
    return expedientesService.consultarTitulosEducacion(expedienteDto);
  }

  @Override
  public LdvMaestraDto getLdvById(Integer idLdvMae) {
    return expedientesService.getLdvById(idLdvMae);
  }

  @Override
  public List<DataSource> obtenerTodosLosArchivos(String nfsPath) throws SinacException {
    return documentosService.obtenerTodosLosArchivos(nfsPath);
  }

  @Override
  public DataSource obtenerArchivoByNombre(String nombre, String ruta) throws SinacException {
    return documentosService.obtenerArchivoByNombre(nombre, ruta);
  }

  @Override
  @Transactional(readOnly = false)
  public ExpedienteDto desactivarRepresentante(BigInteger idExp, BigInteger idPersona) throws SinacException {
    expedientesService.desactivarRepresentante(idExp, idPersona);
    return expedientesService.getExpedientebyId(idExp);
  }

  @Override
  public List<Map<String, Object>> getAccionesOperacionesPorExpedienteUsuario(BigInteger idExp) throws SinacException {
    return motorTramitacion.getAccionesOperacionesPorExpedienteUsuario(idExp);
  }

  @Override
  public Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String apellido1, String apellido2,
      Date fechaNacimiento, Pageable pageable, String tipoOrdenacion, String columnaOrdenar) throws SinacException {
    return personasService.getPersonasRastreo(identificador, nombre, apellido1, apellido2, fechaNacimiento, pageable,
        tipoOrdenacion, columnaOrdenar);
  }

  @Override
  public Map<String, List<TipoOficioDto>> getTiposOficiosAndDocumentosToRequerirForRequerimientosAndAudiencias(
      BigInteger idExpediente, short idProcedimiento, String codTramite, String codAccion) throws SinacException {
    Map<String, List<TipoOficioDto>> tiposOficiosAndDocumentosToRequerir = new HashMap<>();

    List<TipoOficioDto> tipoOficioDtoList = requerimientosAndAudienciasService
        .getTiposOficiosAndDocumentosToRequerirByIdProcedimiento(idProcedimiento);

    List<PlantillaDto> plantillaDtoListForRequerimientos = getListaPlantillas(idExpediente, codTramite, "GREQ",
        codAccion);
    List<PlantillaDto> plantillaDtoListForAudiencias = getListaPlantillas(idExpediente, codTramite, "GAUD", codAccion);

    tiposOficiosAndDocumentosToRequerir.put("GREQ",
        tipoOficioDtoList.stream().filter(tipoOficioDto -> plantillaDtoListForRequerimientos.stream()
            .anyMatch(plantillaDto -> plantillaDto.getIdPla().equals(tipoOficioDto.getIdPla()))).toList());
    tiposOficiosAndDocumentosToRequerir.put("GAUD",
        tipoOficioDtoList.stream().filter(tipoOficioDto -> plantillaDtoListForAudiencias.stream()
            .anyMatch(plantillaDto -> plantillaDto.getIdPla().equals(tipoOficioDto.getIdPla()))).toList());

    return tiposOficiosAndDocumentosToRequerir;
  }

  @Override
  public List<DocumentoToRequerirDto> getDocumentosToRequerirByIdProcedimiento(short idProcedimiento)
      throws SinacException {
    return requerimientosAndAudienciasService.getDocumentosToRequerirByIdProcedimiento(idProcedimiento);
  }

  @Override
  public List<ExpedienteRequerimientoDto> getRequerimientosByIdExpediente(BigInteger idExpediente)
      throws SinacException {
    return requerimientosAndAudienciasService.getRequerimientosByIdExpediente(idExpediente);
  }

  @Override
  @Transactional(readOnly = false)
  public void acumularExpediente(ExpedienteDto expediente) throws SinacException {
    expedientesService.acumularExpediente(expediente);
    ExpedienteDto expedienteDtoDestino = expedientesService
        .getExpedienteSimpleByCodExpediente(expediente.getCodExpDestinoAcumular());
    // TODO Acumular documentos
    ExpedientesVinculadosDto expedientesVinculadosDto = new ExpedientesVinculadosDto();
    expedientesVinculadosDto.setExpedienteDtoByIdExp1(expediente);
    expedientesVinculadosDto.setExpedienteDtoByIdExp2(expedienteDtoDestino);
    LdvMaestraDto ldvMaestraMotivo = catalogosService.getCatalogoByCod("REL-ACU");
    expedientesVinculadosDto.setTipoRelacion(ldvMaestraMotivo);
    expedientesService.saveExpedientesViculados(expedientesVinculadosDto);
    ExpedientesVinculadosDto expedientesVinculadosDto2 = new ExpedientesVinculadosDto();
    expedientesVinculadosDto2.setExpedienteDtoByIdExp1(expedienteDtoDestino);
    expedientesVinculadosDto2.setExpedienteDtoByIdExp2(expediente);
    expedientesVinculadosDto2.setTipoRelacion(ldvMaestraMotivo);
    expedientesService.saveExpedientesViculados(expedientesVinculadosDto2);
    ExpedienteDocumentoDto expedienteDocumentoAcumulacion = new ExpedienteDocumentoDto();
    ExpedienteDto expedienteDestinoPersonas = expedientesService.getExpedientebyId(expedienteDtoDestino.getIdExp());
    PlantillaDto plantilla = plantillasService.getPlantillaPorCod("OACUCN");
    documentosService.createExpedienteDocumentoPlantillaContent(plantilla, expedienteDestinoPersonas,
        expedienteDocumentoAcumulacion, null);
    List<DocumentoToSaveDto> documentoToSaveDtoList = new ArrayList<>();
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
    documentoToSaveDto.setNombre(expedienteDocumentoAcumulacion.getNomDoc());
    documentoToSaveDto.setContenido(expedienteDocumentoAcumulacion.getContenido());
    documentoToSaveDtoList.add(documentoToSaveDto);
    documentoToSaveDtoList.replaceAll(d -> documentosService.copyDocumentoNFS(expediente.getCodExp(),
        expediente.getProcedimientoDto().getCodPro(), expediente.getFechaEfectos(), d));
    expedienteDocumentoAcumulacion = documentosService.saveExpedienteDocumento(expedienteDocumentoAcumulacion,
        expediente);
    DataHandler contenido = documentosService.signDocumento(expedienteDocumentoAcumulacion);
    expedientesService.saveDocumentoSalida(expedienteDocumentoAcumulacion, contenido);
    documentosService.updateEstadoDocumento(expedienteDocumentoAcumulacion.getIdExpDoc(),
        catalogosService.getCatalogoByCod("EDOC-FIR"));
    // TODO Comunicar documento
  }

  @Override
  @Transactional(readOnly = false)
  public void relacionarExpedientes(ExpedienteDto expediente) throws SinacException {
    // Se relaciona el expediente actual con los seleccionados
    relacionarExpedienteActual(expediente);
    // Se relacionan los expedientes seleccionados entre ellos
    relacionarExpedientesSeleccionados(expediente);

  }

  @Override
  public List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(
      BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento) throws SinacException {
    return plazosService.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(idExpediente, idPlazo,
        idRequerimiento);
  }

  @Override
  public void crearPlazoExpediente(BigInteger idExpediente, BigInteger idRequerimiento, PlazoDto plazoDto)
      throws SinacException {
    ExpedientesPlazosDto expedientesPlazosDto = plazosService
        .getPlazoVigenteByIdExpedienteAndIdPlazoAndCodTipoPlazoAndIdRequerimiento(idExpediente, plazoDto.getIdPlazo(),
            plazoDto.getLdvMaestra().getCodLdvMae(), idRequerimiento);

    if (expedientesPlazosDto == null) {
      if (!plazosService.isPlazoCaducidadInforme(plazoDto.getLdvMaestra().getCodLdvMae()) || plazosService
          .existsInformeRecibidoForPlazoCaducidadInforme(idExpediente, plazoDto.getLdvMaestra().getCodLdvMae())) {
        LdvMaestraDto enCurso = catalogosService.getCatalogoByCod(Plazo.Estado.EN_CURSO);

        ExpedienteDto expedienteDto = expedientesService.getExpedienteByIdExpediente(idExpediente);

        expedientesPlazosDto = new ExpedientesPlazosDto();
        expedientesPlazosDto.setExpedienteDto(expedienteDto);
        expedientesPlazosDto.setPlazoDto(plazoDto);
        expedientesPlazosDto.setLdvMaestraDto(enCurso);

        if (!"TPLA-RES".equals(plazoDto.getLdvMaestra().getCodLdvMae())) {
          expedientesPlazosDto.setFechaInicio(plazosService.getNextBusinessDay(new Date()));

          if (idRequerimiento != null && "TPLA-SUB".equals(plazoDto.getLdvMaestra().getCodLdvMae())) {
            ExpedienteRequerimientoDto expedienteRequerimientoDto = requerimientosAndAudienciasService
                .getRequerimientoByIdRequerimiento(idRequerimiento);

            expedientesPlazosDto.setExpedienteRequerimientoDto(expedienteRequerimientoDto);
          }
        } else {
          expedientesPlazosDto.setFechaInicio(
              plazosService.getNextBusinessDay(expedientesPlazosDto.getExpedienteDto().getFechaEfectos()));
        }

        expedientesPlazosDto.setFechaFinOrig(plazosService.getDateByTimeToBeAdded(expedientesPlazosDto.getFechaInicio(),
            expedientesPlazosDto.getPlazoDto().getLdvMaestraDtoByIdPlazoTieLdv().getCodLdvMae(),
            expedientesPlazosDto.getPlazoDto().getNumPlazo()));
        expedientesPlazosDto.setFechaFin(expedientesPlazosDto.getFechaFinOrig());

        plazosService.crearPlazoExpediente(expedientesPlazosDto);

        LOG.info(
            "ExpedientesFacadeImpl.crearPlazoExpediente - El \"{}\" del Expediente \"{}\" se ha creado correctamente.",
            expedientesPlazosDto.getPlazoDto().getNomPlazo(), expedientesPlazosDto.getExpedienteDto().getIdExp());
      }
    } else if (Plazo.Estado.SUSPENDIDO.equals(expedientesPlazosDto.getLdvMaestraDto().getCodLdvMae())) {
      reanudarPlazoExpediente(expedientesPlazosDto, false);
    }
  }

  @Override
  public void suspenderPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento)
      throws SinacException {
    LdvMaestraDto estadoPlazo = catalogosService.getCatalogoByCod(Plazo.Estado.EN_CURSO);

    ExpedientesPlazosDto expedientesPlazosDto = plazosService
        .getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado(idExpediente, idPlazo, idRequerimiento,
            estadoPlazo.getCodLdvMae());

    if (expedientesPlazosDto != null
        && Plazo.Estado.EN_CURSO.equals(expedientesPlazosDto.getLdvMaestraDto().getCodLdvMae())) {
      ExpedientesPlazosDto expedientesPlazosDtoToCreate = new ExpedientesPlazosDto();

      estadoPlazo = catalogosService.getCatalogoByCod(Plazo.Estado.SUSPENDIDO);

      expedientesPlazosDtoToCreate.setExpedienteDto(expedientesPlazosDto.getExpedienteDto());
      expedientesPlazosDtoToCreate.setPlazoDto(expedientesPlazosDto.getPlazoDto());
      expedientesPlazosDtoToCreate.setExpedienteRequerimientoDto(expedientesPlazosDto.getExpedienteRequerimientoDto());
      expedientesPlazosDtoToCreate.setLdvMaestraDto(estadoPlazo);
      expedientesPlazosDtoToCreate.setFechaInicio(expedientesPlazosDto.getFechaInicio());
      expedientesPlazosDtoToCreate.setFechaFinOrig(expedientesPlazosDto.getFechaFinOrig());
      expedientesPlazosDtoToCreate.setFechaFin(null);
      expedientesPlazosDtoToCreate.setFechaIniSusp(new Date());
      expedientesPlazosDtoToCreate.setFechaFinSusp(null);
      expedientesPlazosDtoToCreate.setSuspensionAcumulada(expedientesPlazosDto.getSuspensionAcumulada());
      expedientesPlazosDtoToCreate.setDiasAmpliados(expedientesPlazosDto.getDiasAmpliados());

      plazosService.updatePlazoExpedienteNoVigente(expedientesPlazosDto.getIdExpMPla());

      plazosService.crearPlazoExpediente(expedientesPlazosDtoToCreate);

      LOG.info(
          "ExpedientesFacadeImpl.suspenderPlazoExpediente - El \"{}\" del Expediente \"{}\" se ha suspendido correctamente.",
          expedientesPlazosDtoToCreate.getPlazoDto().getNomPlazo(),
          expedientesPlazosDtoToCreate.getExpedienteDto().getIdExp());
    }
  }

  @Override
  public void reanudarPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento,
      boolean isManual) throws SinacException {
    LdvMaestraDto suspendido = catalogosService.getCatalogoByCod(Plazo.Estado.SUSPENDIDO);

    ExpedientesPlazosDto expedientesPlazosDto = plazosService
        .getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado(idExpediente, idPlazo, idRequerimiento,
            suspendido.getCodLdvMae());

    if (expedientesPlazosDto != null
        && Plazo.Estado.SUSPENDIDO.equals(expedientesPlazosDto.getLdvMaestraDto().getCodLdvMae())) {
      reanudarPlazoExpediente(expedientesPlazosDto, isManual);
    }
  }

  @Override
  public void reanudarPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto, boolean isManual)
      throws SinacException {
    if (isManual || (!"TPLA-RES".equals(expedientesPlazosDto.getPlazoDto().getLdvMaestra().getCodLdvMae())
        || !plazosService.existsPlazosExpedienteEnCursoForPlazoResolucion(
            expedientesPlazosDto.getExpedienteDto().getProcedimientoDto().getIdPro(),
            expedientesPlazosDto.getExpedienteDto().getIdExp()))) {
      ExpedientesPlazosDto expedientesPlazosDtoToCreate = new ExpedientesPlazosDto();

      LdvMaestraDto enCurso = catalogosService.getCatalogoByCod(Plazo.Estado.EN_CURSO);

      short suspensionAcumulada = (short) ((expedientesPlazosDto.getSuspensionAcumulada() != null
          ? expedientesPlazosDto.getSuspensionAcumulada()
          : 0) + plazosService.getElapsedDays(expedientesPlazosDto.getFechaIniSusp(), new Date()));

      expedientesPlazosDtoToCreate.setExpedienteDto(expedientesPlazosDto.getExpedienteDto());
      expedientesPlazosDtoToCreate.setPlazoDto(expedientesPlazosDto.getPlazoDto());
      expedientesPlazosDtoToCreate.setExpedienteRequerimientoDto(expedientesPlazosDto.getExpedienteRequerimientoDto());
      expedientesPlazosDtoToCreate.setLdvMaestraDto(enCurso);
      expedientesPlazosDtoToCreate.setFechaInicio(expedientesPlazosDto.getFechaInicio());
      expedientesPlazosDtoToCreate.setFechaFinOrig(expedientesPlazosDto.getFechaFinOrig());
      expedientesPlazosDtoToCreate.setFechaFin(
          plazosService.getDateByTimeToBeAdded(expedientesPlazosDto.getFechaFinOrig(), "PLA-DIA", suspensionAcumulada));
      expedientesPlazosDtoToCreate.setFechaIniSusp(expedientesPlazosDto.getFechaIniSusp());
      expedientesPlazosDtoToCreate.setFechaFinSusp(new Date());
      expedientesPlazosDtoToCreate.setSuspensionAcumulada(suspensionAcumulada);
      expedientesPlazosDtoToCreate.setDiasAmpliados(expedientesPlazosDto.getDiasAmpliados());

      plazosService.updatePlazoExpedienteNoVigente(expedientesPlazosDto.getIdExpMPla());

      plazosService.crearPlazoExpediente(expedientesPlazosDtoToCreate);

      LOG.info(
          "ExpedientesFacadeImpl.reanudarPlazoExpediente - El \"{}\" del Expediente \"{}\" se ha reanudado correctamente.",
          expedientesPlazosDtoToCreate.getPlazoDto().getNomPlazo(),
          expedientesPlazosDtoToCreate.getExpedienteDto().getIdExp());
    }
  }

  @Override
  public void finalizarPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento)
      throws SinacException {
    ExpedientesPlazosDto expedientesPlazosDto = plazosService
        .getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento(idExpediente, idPlazo, idRequerimiento);

    if (expedientesPlazosDto != null) {
      String codigoEstadoPlazo = expedientesPlazosDto.getLdvMaestraDto().getCodLdvMae();

      if (Plazo.Estado.EN_CURSO.equals(codigoEstadoPlazo) || Plazo.Estado.SUSPENDIDO.equals(codigoEstadoPlazo)
          || Plazo.Estado.VENCIDO.equals(codigoEstadoPlazo)) {
        LdvMaestraDto estadoPlazo = null;

        if (Plazo.Estado.EN_CURSO.equals(codigoEstadoPlazo) || Plazo.Estado.SUSPENDIDO.equals(codigoEstadoPlazo)) {
          estadoPlazo = catalogosService.getCatalogoByCod(Plazo.Estado.FINALIZADO);
        } else {
          estadoPlazo = catalogosService.getCatalogoByCod(Plazo.Estado.FINALIZADO_POST_VENCIMIENTO);
        }

        ExpedientesPlazosDto expedientesPlazosDtoToCreate = new ExpedientesPlazosDto();
        expedientesPlazosDtoToCreate.setExpedienteDto(expedientesPlazosDto.getExpedienteDto());
        expedientesPlazosDtoToCreate.setPlazoDto(expedientesPlazosDto.getPlazoDto());
        expedientesPlazosDtoToCreate
            .setExpedienteRequerimientoDto(expedientesPlazosDto.getExpedienteRequerimientoDto());
        expedientesPlazosDtoToCreate.setLdvMaestraDto(estadoPlazo);
        expedientesPlazosDtoToCreate.setFechaInicio(expedientesPlazosDto.getFechaInicio());
        expedientesPlazosDtoToCreate.setFechaFinOrig(expedientesPlazosDto.getFechaFinOrig());
        expedientesPlazosDtoToCreate.setFechaFin(new Date());
        expedientesPlazosDtoToCreate.setFechaIniSusp(expedientesPlazosDto.getFechaIniSusp());
        expedientesPlazosDtoToCreate.setFechaFinSusp(expedientesPlazosDto.getFechaFinSusp());
        expedientesPlazosDtoToCreate.setSuspensionAcumulada(expedientesPlazosDto.getSuspensionAcumulada());
        expedientesPlazosDtoToCreate.setDiasAmpliados(expedientesPlazosDto.getDiasAmpliados());

        plazosService.updatePlazoExpedienteNoVigente(expedientesPlazosDto.getIdExpMPla());

        plazosService.crearPlazoExpediente(expedientesPlazosDtoToCreate);

        LOG.info(
            "ExpedientesFacadeImpl.finalizarPlazoExpediente - El \"{}\" del Expediente \"{}\" se ha finalizado correctamente.",
            expedientesPlazosDtoToCreate.getPlazoDto().getNomPlazo(),
            expedientesPlazosDtoToCreate.getExpedienteDto().getIdExp());
      }
    }
  }

  @Override
  public void vencerPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto) throws SinacException {
    LdvMaestraDto vencido = catalogosService.getCatalogoByCod(Plazo.Estado.VENCIDO);

    ExpedientesPlazosDto expedientesPlazosDtoToCreate = new ExpedientesPlazosDto();
    expedientesPlazosDtoToCreate.setExpedienteDto(expedientesPlazosDto.getExpedienteDto());
    expedientesPlazosDtoToCreate.setPlazoDto(expedientesPlazosDto.getPlazoDto());
    expedientesPlazosDtoToCreate.setExpedienteRequerimientoDto(expedientesPlazosDto.getExpedienteRequerimientoDto());
    expedientesPlazosDtoToCreate.setLdvMaestraDto(vencido);
    expedientesPlazosDtoToCreate.setFechaInicio(expedientesPlazosDto.getFechaInicio());
    expedientesPlazosDtoToCreate.setFechaFinOrig(expedientesPlazosDto.getFechaFinOrig());
    expedientesPlazosDtoToCreate.setFechaFin(expedientesPlazosDto.getFechaFin());
    expedientesPlazosDtoToCreate.setFechaIniSusp(expedientesPlazosDto.getFechaIniSusp());
    expedientesPlazosDtoToCreate.setFechaFinSusp(expedientesPlazosDto.getFechaFinSusp());
    expedientesPlazosDtoToCreate.setSuspensionAcumulada(expedientesPlazosDto.getSuspensionAcumulada());
    expedientesPlazosDtoToCreate.setDiasAmpliados(expedientesPlazosDto.getDiasAmpliados());

    plazosService.updatePlazoExpedienteNoVigente(expedientesPlazosDto.getIdExpMPla());

    plazosService.crearPlazoExpediente(expedientesPlazosDtoToCreate);

    LOG.info(
        "ExpedientesFacadeImpl.vencerPlazoExpediente - El \"{}\" del Expediente \"{}\" se ha vencido correctamente.",
        expedientesPlazosDtoToCreate.getPlazoDto().getNomPlazo(),
        expedientesPlazosDtoToCreate.getExpedienteDto().getIdExp());
  }

  private void relacionarExpedientesSeleccionados(ExpedienteDto expediente) {
    int index = 0;
    for (String codExpediente : expediente.getCodExpedientes()) {
      if (index != expediente.getCodExpedientes().size() - 1) {
        ExpedienteDto expedienteDto1 = expedientesService.getExpedienteSimpleByCodExpediente(codExpediente);
        for (String codExpediente2 : expediente.getCodExpedientes()) {
          if (!codExpediente.equals(codExpediente2)) {
            ExpedienteDto expedienteDto2 = expedientesService.getExpedienteSimpleByCodExpediente(codExpediente2);
            ExpedientesVinculadosDto expedientesVinculadosDto = new ExpedientesVinculadosDto();
            expedientesVinculadosDto.setExpedienteDtoByIdExp1(expedienteDto1);
            expedientesVinculadosDto.setExpedienteDtoByIdExp2(expedienteDto2);
            LdvMaestraDto ldvMaestraMotivo = catalogosService.getCatalogoByCod("REL-BAS");
            expedientesVinculadosDto.setTipoRelacion(ldvMaestraMotivo);
            expedientesService.saveExpedientesViculados(expedientesVinculadosDto);
            ExpedientesVinculadosDto expedientesVinculadosDto2 = new ExpedientesVinculadosDto();
            expedientesVinculadosDto2.setExpedienteDtoByIdExp1(expedienteDto2);
            expedientesVinculadosDto2.setExpedienteDtoByIdExp2(expedienteDto1);
            expedientesVinculadosDto2.setTipoRelacion(ldvMaestraMotivo);
            expedientesService.saveExpedientesViculados(expedientesVinculadosDto2);
          }
        }
        index++;
      }
    }
  }

  private void relacionarExpedienteActual(ExpedienteDto expediente) {
    int index = 0;
    ExpedienteDto expedienteDto = expedientesService.getExpedienteSimpleByCodExpediente(expediente.getCodExp());
    for (String codExpedientes : expediente.getCodExpedientes()) {
      ExpedienteDto expedienteDto2 = expedientesService.getExpedienteSimpleByCodExpediente(codExpedientes);
      ExpedientesVinculadosDto expedientesVinculadosDto = new ExpedientesVinculadosDto();
      expedientesVinculadosDto.setExpedienteDtoByIdExp1(expedienteDto);
      expedientesVinculadosDto.setExpedienteDtoByIdExp2(expedienteDto2);
      LdvMaestraDto ldvMaestraMotivo = catalogosService
          .getCatalogoById(Integer.parseInt(expediente.getIdsTipoRelacionExpedientes().get(index)));
      expedientesVinculadosDto.setTipoRelacion(ldvMaestraMotivo);
      expedientesService.saveExpedientesViculados(expedientesVinculadosDto);
      ExpedientesVinculadosDto expedientesVinculadosDto2 = new ExpedientesVinculadosDto();
      expedientesVinculadosDto2.setExpedienteDtoByIdExp1(expedienteDto2);
      expedientesVinculadosDto2.setExpedienteDtoByIdExp2(expedienteDto);
      String codLdvMotivo2 = "";
      if (ldvMaestraMotivo.getCodLdvMae().contains("PAD")) {
        codLdvMotivo2 = "REL-HIJ";
      } else if (ldvMaestraMotivo.getCodLdvMae().contains("HIJ")) {
        codLdvMotivo2 = "REL-PAD";
      } else {
        codLdvMotivo2 = ldvMaestraMotivo.getCodLdvMae();
      }
      LdvMaestraDto ldvMaestraMotivo2 = catalogosService.getCatalogoByCod(codLdvMotivo2);
      expedientesVinculadosDto2.setTipoRelacion(ldvMaestraMotivo2);
      expedientesService.saveExpedientesViculados(expedientesVinculadosDto2);
      index++;
    }
  }

  @Override
  @Transactional(readOnly = false)
  public void consultarCertificaciones(ExpedienteDto expedienteDto) throws SinacException {
    expedientesService.consultarCertificaciones(expedienteDto);
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPerTipoCertificacion(BigInteger idPersona,
      String tipoCertificacion) throws SinacException {
    return expedientesService.getPerCertificacionesByIdPerTipoCertificacion(idPersona, tipoCertificacion);
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPer(BigInteger idPersona) throws SinacException {
    return expedientesService.getPerCertificacionesByIdPer(idPersona);
  }

  @Override
  public List<String> getAvisosExpediente(BigInteger idExpediente, ProcedimientoDto proDto, Integer idUsuario,
      Boolean isAdmin) {
    return avisoService.getAvisosExpediente(idExpediente, proDto, idUsuario, isAdmin);
  }

  @Override
  public List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExpediente, Boolean isAdmin) {
    return avisoService.getAvisosExpedienteByIdExp(idExpediente, isAdmin);
  }

  @Override
  @Transactional(readOnly = false)
  public void cambiarEstadoAvisoExp(BigInteger idExpAvisos) throws SinacException {
    avisoService.cambiarEstadoAvisoExp(idExpAvisos);
  }

  @Override
  public List<ExpedienteAvisoDto> getUltimosAvisosByUserId(Integer idUsuario, Short idPro, Boolean isAdmin)
      throws SinacException {
    return expedientesService.getUltimosAvisosAsignados(idUsuario, idPro, isAdmin);
  }

  @Override
  public Page<ResultadoBusquedaAvisosExpDto> getAvisosExpPaginated(BusquedaAvisosExpDto busquedaDto, Pageable pageable,
      Boolean isAdmin) throws SinacException {
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    Map<Integer, List<ResultadoBusquedaAvisosExpDto>> mapa = expedientesService.getAvisosExpPaginated(busquedaDto,
        pageable, isAdmin);

    return new PageImpl<>(mapa.values().stream().toList().get(0), PageRequest.of(currentPage, pageSize),
        mapa.keySet().stream().toList().get(0));
  }

  @Override
  public Map<List<String>, LdvMaestraDto> getAvisosUnicosLdvMaestra() throws SinacException {
    return avisoService.obtenerProcedimientosPorLdvMaestra();
  }

  @Override
  @Transactional(readOnly = false)
  public void actualizarHabilitadoProAvi(Long idProAvisos, Boolean habilitado) throws SinacException {
    procedimientosAvisosService.updateHabilitado(idProAvisos, habilitado);
  }

  @Override
  @Transactional(readOnly = false)
  public void consultarPadron(ExpedienteDto expedienteDto) throws SinacException {
    expedientesService.consultarPadron(expedienteDto);
  }

  @Override
  public PerPadronDto getPerPadronByIdPer(BigInteger idPersona) throws SinacException {
    return expedientesService.getPerPadronByIdPer(idPersona);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void updateEstadoDocumento(BigInteger idExpDoc, LdvMaestraDto ldvMaestra) throws SinacException {
    documentosService.updateEstadoDocumento(idExpDoc, ldvMaestra);

  }

  /**
   * Este metodo ha sido creado para crear una nueva transaccion cuando se ejecute
   * el metodo de saveDocumentosSalida, para que en caso de error guarde el
   * registro
   */

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void saveRegistroAux(RegistroDto registroDto) throws SinacException {
    documentosService.saveRegistro(registroDto);

  }

  @Override
  @Transactional(readOnly = false)
  public ExpedienteNotificacionesDto getExpedienteNotificacionesbyIdSolSun(String idSolSun) throws SinacException {
    return expedientesService.getExpedienteNotificacionesbyIdSolSun(idSolSun);
  }

  @Override
  @Transactional(readOnly = false)
  public AsientoDto enviarDocumentoAGeiser(BigInteger idExpDoc, String orgDestino, String asunto,
      ProcedimientosFasesTramitesOperacionesDto pfto, UsuarioDto usuario) {

    LOG.debug(
        "Init - DocumentosServiceImpl.enviarDocumentoAGeiser del documento {} con orgDestino={}, asunto={} por el usuario={}",
        idExpDoc, orgDestino, asunto, usuario.getIdUsu());
    // obtener doc de gestor documental
    DescargaDeDocumentoDto documento = this.descargarDocumentoCopiaAutentica(idExpDoc);
    // crear documento de envio
    DocumentoDto documentoDto = new DocumentoDto();
    documentoDto.setNombre(documento.getNombreArchivo());
    try {
      Path path = new File(documento.getNombreArchivo()).toPath();
      documentoDto.setMimeType(Files.probeContentType(path));
    } catch (IOException e) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_4)
          .logMessageParams(documento.getNombreArchivo());
    }
    documentoDto.setContenido(documento.getFile());

    try {
      // enviar registro en geiser
      PeticionRegistroEnvioDto peticion = new PeticionRegistroEnvioDto();
      peticion.setOrgDestino(orgDestino);
      peticion.setDocumento(documentoDto);
      peticion.setAsunto(asunto);
      ResultadoRegistroEnvioDto resultado = geiserService.registrarEnviar(peticion);

      // guardar datos de registro en ASIENTOS
      Date fechaActual = new Date();
      AsientoDto asientoDto = new AsientoDto();
      asientoDto.setNumRegistro(resultado.getNumRegistro());
      asientoDto.setOrgDestino(orgDestino);
      asientoDto.setEstado(resultado.getEstado().value());
      asientoDto.setFechaEstado(fechaActual);
      asientoDto.setNotas("Asunto: " + asunto);
      ExpedienteDocumentoDto expDoc = new ExpedienteDocumentoDto();
      expDoc.setIdExpDoc(idExpDoc);
      asientoDto.setExpedienteDocumento(expDoc);
      asientoDto.setProcedimientosFasesTramitesOperaciones(pfto);
      asientoDto.setFlgActivo(true);
      asientoDto.setFechaIniVig(fechaActual);
      asientoDto.setFechaCreacion(fechaActual);
      asientoDto.setCreadoPor(usuario);

      asientosService.saveAsiento(asientoDto, usuario);

      LOG.debug(
          "End - DocumentosServiceImpl.enviarDocumentoAGeiser del documento {} con orgDestino={}, asunto={} por el usuario={}",
          idExpDoc, orgDestino, asunto, usuario.getIdUsu());
      return asientoDto;

    } catch (SinacGeiserException | IOException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_MESSAGE_5).logMessageParams(idExpDoc, e.getMessage())
          .type(SinacExceptionType.BUSINESS);
    }
  }

  @Override
  public DocumentoDto obtenerJustificanteGeiser(BigInteger idAsiento) {
    AsientoDto asientoDto = asientosService.getAsientoConJustificante(idAsiento);
    return documentosService.obtenerJustificanteGeiser(asientoDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveDatosBoe(ExpedienteBoeDto expedienteBoeDto) throws SinacException {
    expedientesService.saveDatosBoe(expedienteBoeDto);
  }

  @Override
  public List<String> getIdsEnvioJobBoe() throws SinacException {
    return expedientesService.getIdsEnvioJobBoe();
  }

  @Override
  public ExpedienteBoeDto getExpedienteBoeByIdEnvio(String idEnvio) throws SinacException {
    return expedientesService.getExpedienteBoeByIdEnvio(idEnvio);
  }

  @Override
  public List<BoeAnunciosDto> getBoeAnunciosByIdExpBoe(BigInteger idExpBoe) throws SinacException {
    return expedientesService.getBoeAnunciosByIdExpBoe(idExpBoe);
  }

  @Override
  @Transactional(readOnly = false)
  public void abrirExpedienteGd(BigInteger idExpediente) throws SinacException {
    try {
      ExpedienteDto expediente = expedientesService.getExpedienteByIdExpediente(idExpediente);
      PersonaDto interesado = expedientesService.getInteresadoByIdExp(idExpediente);
      String identificadorExpedienteGD = gestorDocumentalConnector.abrirExpediente(
          expediente.getProcedimientoDto().getCodSia(),
          interesado.getPersonasIdentificaDtos().get(0).getNumAcreditacion(), organo, expediente.getCodExp());
      expediente.setIdExpGd(identificadorExpedienteGD);
      expedientesService.saveExpediente(expediente);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_107);
    }
  }

  @Override
  public List<DocumentosTramiteDto> getDocumentosTramite(BigInteger idExp, String codTra, String codOpe, String codAcc)
      throws SinacException {
    return plantillasService.getDocumentosTramite(idExp, codTra, codOpe, codAcc);
  }

  @Override
  public List<DocumentosTramiteDto> getDocumentosTramiteSinOpe(BigInteger idExp, String codTra, String codAcc)
      throws SinacException {
    return plantillasService.getDocumentosTramiteSinOpe(idExp, codTra, codAcc);
  }

  @Override
  public Boolean existeDocumentoExpediente(String tipoDoc, BigInteger idExp) throws SinacException {
    return expedientesService.existeDocumentoExpediente(tipoDoc, idExp);
  }

  @Override
  public PlantillaDto getPlantillaPorTipoDocAndPro(short idPro, String codTipo) {
    return plantillasService.getPlantillaPorTipoDocAndPro(idPro, codTipo);
  }

  @Override
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public BigInteger generarPlantillaDgp(BigInteger idExp, BigInteger idExpInf) {
    ExpedienteDto expedienteDto = getDetalleExpediente(idExp);

    PlantillaDto plantillaDto = getPlantillaPorTipoDocAndPro(expedienteDto.getProcedimientoDto().getIdPro(), "INDGP");
    // Se genera plantilla DGP
    // TODO RAUL - Revisar bien esta parte para meter el contenido firmado

    ExpedienteDocumentoDto expedienteDocumento = saveDocumentoPlantilla(plantillaDto, expedienteDto);
    expedienteDocumento = saveDocPlantillaDgp(expedienteDto, expedienteDocumento);
    return saveDocumentoExpedienteDgp(expedienteDocumento, expedienteDto, idExpInf);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public ExpedienteDocumentoDto saveDocPlantillaDgp(ExpedienteDto expedienteDto, ExpedienteDocumentoDto expDoc)
      throws SinacException {
    ExpedienteDocumentoDto expedienteDoc = documentosService.saveExpedienteDocumento(expDoc, expedienteDto);
    expedienteDoc.setContenido(expDoc.getContenido());
    return expedienteDoc;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public BigInteger saveDocumentoExpedienteDgp(ExpedienteDocumentoDto expedienteDocumento, ExpedienteDto expedienteDto,
      BigInteger idExpInf) {
    return expedientesService.saveDocumentoExpedienteDgp(expedienteDocumento, expedienteDto, idExpInf);
  }

  @Override
  public void getListaAccionesDisponiblesPorUsuario(BigInteger idExp, Integer idUsu, Long idProFasTraOpeAcc,
      short idPro) throws SinacException {
    motorTramitacion.getListaAccionesDisponiblesPorUsuario(idExp, idUsu, idProFasTraOpeAcc, idPro);
  }

  @Override
  public void solicitarInformesDisponibles(BigInteger idExp, Map<String, Object> valores) throws SinacException {
    try {
      List<ExpedienteInformeDto> listaInformes = expedientesService.getExpedienteInformesByIdExp(idExp);
      PersonaDto interesado = expedientesService.getInteresadoByIdExp(idExp);
      int anios = Utilidades.obtenerAniosEntreFechas(interesado.getFechaNacimiento(), new Date());
      ExpedienteDto expediente = expedientesService.getExpedientebyId(idExp);

      ExpedienteInformeDto informeDgp = null;
      ExpedienteInformeDto informeMju = null;
      ExpedienteInformeDto informeCni = null;
      ExpedienteInformeDto informeMde = null;

      for (ExpedienteInformeDto expedienteInforme : listaInformes) {
        if (expedienteInforme.getLdvMaestraDtoByIdInfLdv().getCodLdvMae().equals("TINF-DGP")) {
          informeDgp = expedienteInforme;
        } else if (expedienteInforme.getLdvMaestraDtoByIdInfLdv().getCodLdvMae().equals("TINF-MJU")) {
          informeMju = expedienteInforme;
        } else if (expedienteInforme.getLdvMaestraDtoByIdInfLdv().getCodLdvMae().equals("TINF-CNI")) {
          informeCni = expedienteInforme;
        } else if (expedienteInforme.getLdvMaestraDtoByIdInfLdv().getCodLdvMae().equals("TINF-MDE")) {
          informeMde = expedienteInforme;
        }
      }

      if (informeDgp == null
          || (informeDgp != null && "EINF-RCH".equals(informeDgp.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae())
              && valores.get("manual") != null)
          || (informeDgp != null && informeDgp.getExpedienteInformeDgpDto() != null
              && "99".equals(informeDgp.getExpedienteInformeDgpDto().getCodEstadoAlta()))) {

        if ((informeDgp != null && informeDgp.getExpedienteInformeDgpDto() != null
            && "99".equals(informeDgp.getExpedienteInformeDgpDto().getCodEstadoAlta()))
            || (informeDgp != null && "EINF-RCH".equals(informeDgp.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae())
                && valores.get("manual") != null)) {
          expedientesService.informeSolicitado(idExp, "TINF-DGP", informeDgp.getIdExpInf());
        } else {
          expedientesService.informeSolicitado(idExp, "TINF-DGP", null);
        }

      }
      if (informeMju == null && anios >= 18) {
        expedientesService.informeSolicitado(idExp, "TINF-MJU", null);
      }
      if (informeCni == null && anios >= 18 && interesado.getNacionalidad().isFlgCni()) {
        valores.put("idExp", idExp);
        valores.put("tipoInformeCni", "TINF-CNI");
        ejecutarAccion(procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
            expediente.getProcedimientoDto().getCodPro(), "INS", "INF", "ICNI", "SCNI"), valores);
      }
      if (informeMde == null && expediente.getProcedimientoDto().getCodCorto().equals("R")) {
        ExpedienteFormularioValDto pertenece = getExpedienteFormularioCampo(idExp, "EJERC");
        if (pertenece != null && !StringUtils.isEmpty(pertenece.getValor())) {
          expedientesService.informeSolicitado(idExp, "TINF-MDE", null);
        }
      }
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_108);
    }
  }

  @Override
  public ExpedienteDto getExpedienteByIdExpedienteInforme(BigInteger idExpInf) throws SinacException {
    return expedientesService.getExpedienteByIdExpedienteInforme(idExpInf);
  }

  @Override
  public ExpedienteFormularioValDto getExpedienteFormularioCampo(BigInteger idExp, String codForm) {
    return expedientesService.getExpedienteFormularioCampo(idExp, codForm);
  }

  @Override
  @Transactional(readOnly = false)
  public void informeSolicitadoMde(BigInteger idExp, String tipoInforme, Date date, BigInteger idExpInforme,
      String codLdvEjercito) throws SinacException {
    expedientesService.informeSolicitadoMde(idExp, tipoInforme, date, idExpInforme, codLdvEjercito);
  }

  @Override
  public List<ExpedientesPlazosDto> getPlazosVigentesVencidosByEstado(String estado) throws SinacException {
    return plazosService.getPlazosVigentesVencidosByEstado(estado);
  }

  @Override
  public Long getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(String codProcedimiento,
      String codTramite, String codOperacion, String codAccion) throws SinacException {
    return procedimientosService.getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(
        codProcedimiento, codTramite, codOperacion, codAccion);
  }

  @Override
  public ExpedientesPlazosDto getPlazoResolucionVigenteByIdExpediente(BigInteger idExpediente) throws SinacException {
    return plazosService.getPlazoResolucionVigenteByIdExpediente(idExpediente);
  }

  @Override
  public Map<String, ExpedienteInformeDto> getInformesByIdExpediente(BigInteger idExpediente) throws SinacException {
    return expedientesService.getInformesByIdExpediente(idExpediente);
  }

  @Override
  public void updateEstadoInforme(BigInteger idInforme, LdvMaestraDto ldvMaestraDto) throws SinacException {
    expedientesService.updateEstadoInforme(idInforme, ldvMaestraDto);
  }

  @Override
  public void reintentoGenerarDocDgp(BigInteger idExpediente, BigInteger idExpInf) throws SinacException {
    try {
      BigInteger idExpedienteDocumento = generarPlantillaDgp(idExpediente, idExpInf);
      expedientesService.updateInformeDgpDocumento(idExpInf, idExpedienteDocumento);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_109);
    }

  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderMdeByIdExp(BigInteger idExp)
      throws SinacException {
    return expedientesService.getPftoaResponderMdeByIdExp(idExp);
  }

  @Override
  public ExpedienteInformeDto getExpedienteInformesByIdExpCodTipoInformeActivo(BigInteger idExp,
      String codTipoInformeLdv) {
    return expedientesService.getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, codTipoInformeLdv);
  }

  @Override
  @Transactional(readOnly = false)
  public void informeRecibido(BigInteger idExp, ExpedienteInformeDto expedienteInformeDto) {
    expedientesService.informeRecibido(idExp, expedienteInformeDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveDatosDefensa(Map<String, Object> valores) {
    String[] elementosMap = valores.toString().split(",");
    List<String> nombresDocumentos = new ArrayList<>();
    List<String> idsDocumentos = new ArrayList<>();
    addInfoDocumentos(elementosMap, nombresDocumentos, idsDocumentos);
    final BigInteger idExp = new BigInteger(valores.get("idExp").toString());
    Date fechaEmision = new Date();
    Date fechaRecepcion = new Date();
    ExpedienteInformeDto expedienteInformeDto = getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, "TINF-MDE");
    expedienteInformeDto.setFechaEmisionInf(fechaEmision);
    expedienteInformeDto.setFechaRecepcion(fechaRecepcion);
    if (valores.get(SENTIDO_MDE) != null && !StringUtils.isEmpty(valores.get(SENTIDO_MDE).toString())) {
      String sentido = valores.get(SENTIDO_MDE).toString();
      LdvMaestraDto ldvMaestraSentidoDto = catalogosService.getCatalogoById(Integer.parseInt(sentido));
      expedienteInformeDto.setLdvMaestraDtoByIdSentidoInfLdv(ldvMaestraSentidoDto);
    }
    if (valores.get("observaciones") != null) {
      String observaciones = valores.get("observaciones").toString();
      expedienteInformeDto.setInfo(observaciones);
    }
    ExpedienteInformeMdeDto expedienteInformeMdeDto = new ExpedienteInformeMdeDto();
    expedienteInformeMdeDto.setImportante(false);
    if (!valores.get("importante").toString().equals("null")) {
      expedienteInformeMdeDto.setImportante(true);
    }
    expedienteInformeDto.getListaExpedienteInformeMdeDtos().add(expedienteInformeMdeDto);
    informeRecibido(idExp, expedienteInformeDto);
    List<ExpedienteDocumentoDto> expedientesDocumentos = documentosService
        .getExpedientesDocumentosMdeByIdInforme(expedienteInformeDto.getIdExpInf());
    for (ExpedienteDocumentoDto expedientesDocumento : expedientesDocumentos) {
      if (!idsDocumentos.contains(expedientesDocumento.getIdExpDoc().toString())) {
        documentosService.desactivarExpedienteDocumentosMdeByIdDocumento(expedientesDocumento.getIdExpDoc());
      }
    }
    LinkedList<DocumentoToSaveDto> documentos = new LinkedList<>();
    for (String nombreDocumento : nombresDocumentos) {
      String tipoDocumento = "OTROS";
      if (nombreDocumento.equals("adjunto0")) {
        tipoDocumento = "INPFA";
      } else if (nombreDocumento.equals("adjunto1")) {
        tipoDocumento = "IPMDE";
      }
      addDocumentoMde(valores, documentos, nombreDocumento, tipoDocumento);
    }
    saveDocumentosEntradaExpediente(idExp, documentos);
    for (DocumentoToSaveDto documentoToSaveDto : documentos) {
      if (documentoToSaveDto.getIdExpedienteDocumento() != null) {
        ExpedienteDocumentoDto expedienteDocumentoDto = getExpedienteDocumentoByIdDocumento(
            documentoToSaveDto.getIdExpedienteDocumento());
        ExpedienteDocumentoInformeMdeDto expedienteDocumentoInformeMdeDto = new ExpedienteDocumentoInformeMdeDto();
        expedienteDocumentoInformeMdeDto.setExpedienteDocumentoDto(expedienteDocumentoDto);
        expedienteDocumentoInformeMdeDto.setExpedienteInformeDto(expedienteInformeDto);
        documentosService.saveExpedienteDocumentoInformeMde(expedienteDocumentoInformeMdeDto);
      }
    }
    valores.put("mensajeCorrecto", "El informe ha sido respondido correctamente");
  }

  private void addInfoDocumentos(String[] elementosMap, List<String> nombresDocumentos, List<String> idsDocumentos) {
    for (String elemento : elementosMap) {
      String[] valoresMap = elemento.split("=");
      if (valoresMap[0].contains("adjunto") && !valoresMap[0].contains("Name") && !valoresMap[0].contains("Id")) {
        nombresDocumentos.add(valoresMap[0].trim());
      } else if (valoresMap[0].contains("adjunto") && valoresMap[0].contains("Id") && valoresMap.length > 1) {
        idsDocumentos.add(valoresMap[1].trim());

      }
    }
  }

  private void addDocumentoMde(Map<String, Object> valores, LinkedList<DocumentoToSaveDto> documentos,
      String nameDocumento, String codTipoDocumento) {
    if (valores.get(nameDocumento) != null) {
      MultipartFile file = (MultipartFile) valores.get(nameDocumento);
      DocumentoToSaveDto adjunto = new DocumentoToSaveDto();
      try {
        adjunto.setContenido(file.getBytes());
      } catch (IOException e) {
        throw new SinacException(e, SinacExceptionMessageType.MESSAGE_110);
      }
      adjunto.setNombre(file.getOriginalFilename());
      DocumentoTipoDto documentoTipoDto = documentosService.getDocumentoTipoEntityByCod(codTipoDocumento);
      LdvMaestraDto origenLdv = catalogosService.getCatalogoByCod("DOC-ADM");
      adjunto.setOrigen(origenLdv.getIdLdvMae());
      adjunto.setTipoDocumento(documentoTipoDto.getIdDocTipo());
      String codEstadoElaboracion = "EE99";
      if (codTipoDocumento.equals("IPMDE")) {
        codEstadoElaboracion = "EE01";
      } else if (codTipoDocumento.equals("INPFA")) {
        codEstadoElaboracion = "EE03";
      }
      LdvMaestraDto estadoElaboracionLdv = catalogosService.getCatalogoByCod(codEstadoElaboracion);
      adjunto.setEstadoElaboracion(estadoElaboracionLdv.getIdLdvMae());
      LdvMaestraDto organoLdv = catalogosService.getCatalogoByCod("ORG-MDE");
      adjunto.setOrgano(organoLdv.getIdLdvMae());
      documentos.add(adjunto);
    }
  }

  @Override
  public List<ExpedienteDocumentoDto> getExpedientesDocumentosMdeByIdInforme(BigInteger idInforme) {
    return documentosService.getExpedientesDocumentosMdeByIdInforme(idInforme);
  }

  @Override
  public ExpedienteInformeMdeDto getExpedienteInformeMdeByIdExpedienteInforme(BigInteger idExpInf) {
    return expedientesService.getExpedienteInformeMdeByIdExpedienteInforme(idExpInf);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveInsideConfig(BigInteger idExp, List<ExpedienteInsideDto> expedienteInsideDtos) {
    expedientesService.saveInsideConfig(idExp, expedienteInsideDtos);
  }

  @Override
  public List<BigInteger> getIdsInteresadosAltaFiliaciones() {
    return personasService.getIdsInteresadosAltaFiliaciones();
  }

  @Override
  public List<BigInteger> getIdsInteresadosConsultaFiliaciones(String maxItemConsultaFiliaciones) {
    return personasService.getIdsInteresadosConsultaFiliaciones(maxItemConsultaFiliaciones);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveAltaFiliaciones(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto, PersonaDto personaDto,
      ExpedienteDto expedienteDto) {
    expedientesService.saveAltaFiliaciones(respuestaAltaFiliacionDto, personaDto, expedienteDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void savePermitirAltaFiliaciones(PersonaDto personaDto, ExpedienteDto expedienteDto) {
    expedientesService.savePermitirAltaFiliaciones(personaDto, expedienteDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void desactivarFiliacionesByIdPersona(BigInteger idPersona) {
    expedientesService.desactivarFiliacionesByIdPersona(idPersona);
  }

  @Override
  public List<PerFiliacionesDto> getPerFiliacionesByIdPer(BigInteger idPer) {
    return expedientesService.getPerFiliacionesByIdPer(idPer);
  }

  @Override
  public List<PerFilNiesDto> getPerFilNiesByIdPer(BigInteger idPer) {
    return expedientesService.getPerFilNiesByIdPer(idPer);
  }

  @Override
  @Transactional(readOnly = false)
  public void desactivarPerFilNiesByIdPersonaMenosNie(BigInteger idPersona, String nie) {
    expedientesService.desactivarPerFilNiesByIdPersonaMenosNie(idPersona, nie);
  }

  @Override
  @Transactional(readOnly = false)
  public void peticionConsultaNieFiliacion(String nie, PersonaDto personaDto) {
    expedientesService.peticionConsultaNieFiliacion(nie, personaDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void peticionConsultaReferenciaFiliacion(String referencia, PersonaDto personaDto,
      ExpedienteDto expedienteDto) {
    expedientesService.peticionConsultaReferenciaFiliacion(referencia, personaDto, expedienteDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveCopyDatosFiliacionEnPersona(PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto) {
    expedientesService.saveCopyDatosFiliacionEnPersona(personaDto, perFiliacionesDto);
  }

  @Override
  public List<ValidacionSemaforoDto> getListaValidacionesByIdExp(BigInteger idExp) {
    return expedientesService.getListaValidacionesByIdExp(idExp, null);
  }

  @Override
  public LdvMaestraDto identificarValCertificadoByCalificacion(String calificacion, String codLdvEntMae)
      throws SinacException {
    return catalogosService.getCatalogoByNomAndLdvEntidadMaestraCod(calificacion, codLdvEntMae);
  }

  @Override
  public void updateValidacionSemaforo(BigInteger idExp, String codLdvEntMae, String codValSem) throws SinacException {
    expedientesService.updateValidacionSemaforo(idExp, codLdvEntMae, codValSem);

  }

  @Override
  public void recalcularValidadionesIntegracion(BigInteger idExp, List<String> listCodValLdvEntMae)
      throws SinacException {
    expedientesService.recalcularValidadionesIntegracion(idExp, listCodValLdvEntMae);

  }

  @Override
  public void recalcularValidadionesSemaforo(BigInteger idExp, List<String> listaValidacionesInt,
      List<String> listaValidacionesCon) {
    expedientesService.recalcularValidadionesSemaforo(idExp, listaValidacionesInt, listaValidacionesCon);

  }

  @Override
  public String getCodLdvEntMaeByCodLdvMae(String codVal) throws SinacException {
    return catalogosService.getCodLdvEntMaeByCodLdvMae(codVal);

  }

  @Override
  public List<ExpedienteDto> listaExpedientesPorEstado(List<String> listaEstados) {

    return expedientesService.listaExpedientesPorEstado(listaEstados);
  }

  @Override
  public List<ExpedienteDto> listaExpedientesDocPendienteValidar(List<String> listaEstadosIn,
      List<String> listaEstadosNotIn) {
    return expedientesService.listaExpedientesDocPendienteValidar(listaEstadosIn, listaEstadosNotIn);
  }

  @Override
  public List<ExpedienteInformeDto> getListaExpedientesInformesByCodEstInforme(String codEstInforme)
      throws SinacException {
    return expedientesService.getListaExpedientesInformesByCodEstInforme(codEstInforme);
  }

  @Transactional(readOnly = false)
  @Override
  public void saveInformeDgpRecibido(String numExp, String tipoPeticion, String fechaAlta, String codEstado) {
    informesService.saveInformeDgpRecibidoEntity(numExp, tipoPeticion, fechaAlta, codEstado);
  }

  @Transactional(readOnly = true)
  @Override
  public List<InformesDgpRecibidosDto> getAllInformesDgpRecibidosNoProcesados() {
    return informesService.findAllInformesDgpRecibidosEntityNoProcesados();
  }

  @Transactional(readOnly = true)
  @Override
  public InformesDgpRecibidosDto findByNumExpAndFechaAlta(String numExp, String fechaAlta) {
    return informesService.findByNumExpAndFechaAlta(numExp, fechaAlta);
  }

  @Transactional(readOnly = false)
  @Override
  public void updateInformeDgpRecibidoEntity(InformesDgpRecibidosDto entityToUpdate, String codEstado) {
    informesService.updateEstadoInformeDgpRecibido(entityToUpdate, codEstado);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveExpedientesRelacionadosAutomaticamente(ExpedienteDto expedienteDto,
      List<ExpedienteDto> expedientesRelacionados, BigInteger idExpOrigen) {
    LOG.debug(
        "Info - CrearExpedienteAccion.saveExpedientesRelacionadosAutomaticamente relacionados con el expediente de origen {}",
        idExpOrigen);
    if (expedientesRelacionados != null) {
      LdvMaestraDto ldvMaestraMotivo;
      LdvMaestraDto ldvMaestraMotivo2;
      ExpedientesVinculadosDto expedientesVinculadosDto = new ExpedientesVinculadosDto();
      ExpedientesVinculadosDto expedientesVinculadosDto2 = new ExpedientesVinculadosDto();
      for (ExpedienteDto expedienteRelacionado : expedientesRelacionados) {
        if (expedienteDto.getProcedimientoDto().getCodPro().startsWith("REC")
            && expedienteRelacionado.getIdExp().equals(idExpOrigen)) {
          ldvMaestraMotivo = catalogosService.getCatalogoByCod("REL-ORI");
          ldvMaestraMotivo2 = catalogosService.getCatalogoByCod("REL-REC");
        } else {
          ldvMaestraMotivo = catalogosService.getCatalogoByCod("REL-BAS");
          ldvMaestraMotivo2 = ldvMaestraMotivo;
        }
        expedientesVinculadosDto.setExpedienteDtoByIdExp1(expedienteDto);
        expedientesVinculadosDto.setExpedienteDtoByIdExp2(expedienteRelacionado);
        expedientesVinculadosDto.setTipoRelacion(ldvMaestraMotivo);
        expedientesService.saveExpedientesViculados(expedientesVinculadosDto);
        expedientesVinculadosDto2.setExpedienteDtoByIdExp1(expedienteRelacionado);
        expedientesVinculadosDto2.setExpedienteDtoByIdExp2(expedienteDto);
        expedientesVinculadosDto2.setTipoRelacion(ldvMaestraMotivo2);
        expedientesService.saveExpedientesViculados(expedientesVinculadosDto2);
      }
    }
    LOG.debug(
        "End - CrearExpedienteAccion.saveExpedientesRelacionadosAutomaticamente relacionados con el expediente de origen {}",
        idExpOrigen);
  }

  @Override
  @Transactional(readOnly = false)
  public ExpedienteDto guardarEntidadesExpediente(Map<String, Object> valores, BigInteger idExpOri,
      SolicitudDto solicitudDto, PersonaDto interesadoDto, List solicitudesPersonasDtoList, String idenExpGD,
      String codExp) {

    return expedientesService.guardarEntidadesExpedientes(valores, idExpOri, solicitudDto, interesadoDto,
        solicitudesPersonasDtoList, idenExpGD, codExp);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED)
  public LinkedList<DocumentoToSaveDto> obtenerTodosLosDocumentosSede(List<SolicitudDocumentoDto> listaDocs) {

    return documentosService.obtenerTodosLosDocumentosSede(listaDocs);
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesIncompletos(List<String> estados) {
    return expedientesService.getListaExpedientesIncompletos(estados);
  }

  @Override
  public void reintentoDocumentosExpediente(List<ExpedienteDocumentoDto> listaDocsExp, SolicitudDto solicitudDto,
      ExpedienteDto expedienteDto) {
    expedientesService.reintentoDocumentosExpediente(listaDocsExp, solicitudDto, expedienteDto);

  }

  @Override
  public List<PersonaDto> getExpedienteAcumular(String numAcreditacion, List<String> listaEstados, String codPro) {

    return expedientesService.getExpedienteAcumular(numAcreditacion, listaEstados, codPro);
  }

  @Override
  public ParametrizacionDto getParametrizacionByNombreAndProcedimiento(String nomParam, String codPro) {
    return expedientesService.getParametrizacionByNombreAndProcedimiento(nomParam, codPro);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void descargarJustificanteGeiser(AsientoDto asientoDto, UsuarioDto usuarioDto) {
    documentosService.descargarJustificanteGeiser(asientoDto, usuarioDto);
  }

  @Override
  @Transactional(readOnly = false)
  public void comprobarSolicitudPenCompletada(BigInteger idExpInf) {
    expedientesService.comprobarSolicitudPenCompletada(idExpInf);
  }

  @Override
  @Transactional(readOnly = false)
  public void actualizarExpedienteInformesMjuFicherosDatos(String nombreArchivo) throws SinacException {
    expedientesService.actualizarExpedienteInformesMjuFicherosDatos(nombreArchivo);
  }

  public List<ExpedienteDto> getExpedienteAcumularPorIdPer(BigInteger idPer, String codPro, List<String> listaEstados) {
    return expedientesService.getExpedienteAcumularPorIdPer(idPer, codPro, listaEstados);
  }

  @Override
  public List<PersonaDto> getPersonasRastreo(String numAcreditacion) {
    return personasService.getPersonasRastreoVea(numAcreditacion);
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesResolver(String codPro, List<String> listaEstadosExp) {

    return expedientesService.getListaExpedientesResolver(codPro, listaEstadosExp);
  }

  @Override
  public void generarFirmarAuto(ExpedienteDto item, Map<String, Object> contextData, PlantillaDto plantilla) {
    LOG.info("generarFirmarAuto - Se va a generar la plantilla {} para el expediente {}-{}", plantilla.getIdPla(),
        item.getIdExp(), item.getCodExp());

    Long pftoa = plantilla.getAccion().getIdProFaseTraOpeAcc();
    contextData.put("idPro", item.getProcedimientoDto().getIdPro());
    contextData.put("idExp", item.getIdExp());
    contextData.put("idPlantilla", plantilla.getIdPla());
    ejecutarAccion(pftoa, contextData);

    try {
      if ("DIC".equals(item.getProcedimientoDto().getCodCorto())) {
        pftoa = procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
            item.getProcedimientoDto().getCodPro(), "TER", "RES", "FDR", "EPFI");
      } else if ("R".equals(item.getProcedimientoDto().getCodCorto())) {
        pftoa = procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(
            item.getProcedimientoDto().getCodPro(), "TER", "RES", "FDP", "EPFI");
      }
      LOG.info(
          "generarFirmarAuto - Se ha generado la plantilla {} para el expediente {}-{}, se procede a enviar a firmar",
          plantilla.getIdPla(), item.getIdExp(), item.getCodExp());
      ejecutarAccion(pftoa, contextData);
    } catch (Exception e) {
      Log.error("Se ha producido un error ejecutando la acción ", e.getMessage());
    }

  }

  public boolean cumpleCriterios(List<Long> listaCondiciones, BigInteger idExp, Short idPro) {

    for (Long idCondicion : listaCondiciones) {
      if (!motorTramitacionComponent.checkCondicion(idCondicion, idExp, idPro)) {
        return false;
      }
    }
    return true;

  }

  @Override
  public ExpedienteDto getExpedientesByIdPerInteresado(BigInteger idPer, List<String> listaEstados) {
    return expedientesService.getExpedientesByIdPerInteresado(idPer, listaEstados);
  }

  @Override
  @Transactional(readOnly = false)
  public void saveExpedienteInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgpDto) {
    expedientesService.saveExpedienteInformeDgp(expedienteInformeDgpDto);
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesPropuesta(List<String> listaEstados, String valor,
      List<String> listaNombresNotIn) {
    return expedientesService.getListaExpedientesPropuesta(listaEstados, valor, listaNombresNotIn);
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPerCodigosEstados(BigInteger idPer,
      List<String> listaCodigosEstados) {
    return expedientesService.getPerCertificacionesByIdPerCodigosEstados(idPer, listaCodigosEstados);
  }

}
