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
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.mjusticia.milano.persistence.bo.ContentRepositoryException;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.AsientosService;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PaisesService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.eis.connector.AntivirusConnector;
import es.mjusticia.sinac.core.eis.connector.ClienteFirmaServidorConnector;
import es.mjusticia.sinac.core.eis.connector.CopiaAutenticaConnector;
import es.mjusticia.sinac.core.eis.connector.GestorDocumentalConnector;
import es.mjusticia.sinac.core.eis.connector.NotificaConnector;
import es.mjusticia.sinac.core.eis.connector.RegageConnector;
import es.mjusticia.sinac.core.eis.copia.autentica.dto.CopiaAutenticaDto;
import es.mjusticia.sinac.core.eis.firma.dto.SignDocumentResponseDto;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto.EnumeracionEstadoElaboracion;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto.NivelDeAcceso;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto.TipoDocumental;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto.TipoFirma;
import es.mjusticia.sinac.core.eis.gd.dto.MetadatosDocumentoGesdocDto.TipoRegistro;
import es.mjusticia.sinac.core.eis.notifica.dto.ResultadoAltaRemesaEnviosDto;
import es.mjusticia.sinac.core.eis.regage.dto.AnexoTypeV3Dto;
import es.mjusticia.sinac.core.eis.regage.dto.ResultadoRegistroTypeV3Dto;
import es.mjusticia.sinac.core.model.dto.ArchivoAdjuntoDto;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.AsientoErrorDto;
import es.mjusticia.sinac.core.model.dto.DescargaDeDocumentoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosEntradaDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFirmaDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeIndiceExpDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteNotificacionesDto;
import es.mjusticia.sinac.core.model.dto.ExpedientePersonaIndiceDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPersonasDto;
import es.mjusticia.sinac.core.model.dto.FirmanteDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlantillasPlantillasCamposDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.entity.DocumentoTipoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteDocumentoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteFirmaEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteFormularioValEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeDgpEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeDgpTramiteEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeEntity;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.entity.PerCertificacionesEntity;
import es.mjusticia.sinac.core.model.entity.PlantillaEntity;
import es.mjusticia.sinac.core.model.entity.PlantillasPlantillasCamposEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosDocumentosTipoEntity;
import es.mjusticia.sinac.core.model.entity.RegistroEntity;
import es.mjusticia.sinac.core.model.entity.TiposViaEntity;
import es.mjusticia.sinac.core.model.enums.OrigenDocumentoGesDocEnum;
import es.mjusticia.sinac.core.model.enums.TipoRegistroRegageEnum;
import es.mjusticia.sinac.core.model.enums.TipoRespuestaRegageEnum;
import es.mjusticia.sinac.core.model.mapper.DocumentoTipoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoInformeMdeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoWithExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteFirmaMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteFirmaMapperWithExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeDgpMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeDgpTramitesMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.FirmanteMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.PlantillaMapper;
import es.mjusticia.sinac.core.model.mapper.PlantillasPlantillasCamposMapper;
import es.mjusticia.sinac.core.model.mapper.RegistroMapper;
import es.mjusticia.sinac.core.model.mapper.RegistroWithDocumentosMapper;
import es.mjusticia.sinac.core.model.mapper.SolicitudDocumentoMapper;
import es.mjusticia.sinac.core.model.mapper.UsuarioMapper;
import es.mjusticia.sinac.core.persistence.DocumentoTipoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoInformeMdeDao;
import es.mjusticia.sinac.core.persistence.ExpedienteFirmaDao;
import es.mjusticia.sinac.core.persistence.ExpedienteFormularioValDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDgpDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDgpTramitesDao;
import es.mjusticia.sinac.core.persistence.FirmanteDao;
import es.mjusticia.sinac.core.persistence.LdvMaestraDao;
import es.mjusticia.sinac.core.persistence.PerCertificacionesDao;
import es.mjusticia.sinac.core.persistence.PlantillaDao;
import es.mjusticia.sinac.core.persistence.PlantillasPlantillasCamposDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosDocumentosTipoDao;
import es.mjusticia.sinac.core.persistence.RegistroDao;
import es.mjusticia.sinac.core.persistence.SolicitudDocumentoDao;
import es.mjusticia.sinac.core.persistence.TiposViaDao;
import es.mjusticia.sinac.core.security.SinacSessionService;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.Constantes.Literal;
import es.mjusticia.sinac.core.utils.Constantes.ValidarDocumentosEntradaExpediente;
import es.mjusticia.sinac.core.utils.FileConverterUtil;
import es.mjusticia.sinac.core.utils.NFSManager;
import es.mjusticia.sinac.core.utils.Utilidades;
import es.mjusticia.sinac.core.utils.Validaciones;
import es.mjusticia.sinac.core.utils.WopiUtils;
import es.mjusticia.sinac.geiser.exception.SinacGeiserException;
import es.mjusticia.sinac.geiser.model.dto.DocumentoDto;
import es.mjusticia.sinac.geiser.model.dto.ResultadoConsultaDto;
import es.mjusticia.sinac.geiser.model.enums.EstadoAsientoEnum;
import es.mjusticia.sinac.geiser.service.GeiserService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.util.ByteArrayDataSource;

/**
 * Clase de Implementación de {@link DocumentosService}.
 *
 * @author NTT Data.
 */
@Component
public class DocumentosServiceImpl implements DocumentosService {

  private static final String STRING_SEPARATOR = "/";

  private static final String DD_MM_YYYY = "dd/MM/yyyy";

  private static final String LITERAL_NO_COPIADO = "\" no ha sido copiado en NFS porque ha habido un error durante el proceso de copia del documento.";

  private static final String TABLE_DGP_OPEN_TEXT_CELL_TRAMITE = "<text:p text:style-name=\"P22\"><text:span text:style-name=\"Fuente_20_de_20_párrafo_20_predeter.\"><text:span text:style-name=\"T8\">";
  private static final String TABLE_DGP_OPEN_TEXT_CELL = "<text:p text:style-name=\"P16\"><text:span text:style-name=\"Fuente_20_de_20_párrafo_20_predeter.\"><text:span text:style-name=\"T6\">";

  private static final String CLOSE_TABLE_CELL_DGP = "</text:span></text:span></text:p></table:table-cell>";

  private static final String OPEN_TABLE_CELL_DGP = "<table:table-cell table:style-name=\"Tabla5.A2\" office:value-type=\"string\">";

  private static final Logger LOG = LoggerFactory.getLogger(DocumentosServiceImpl.class);

  private static final String FORMAT_DATE = "yyyy-MM-dd";

  private static final String NO_CONSTA = "No consta";

  private static final Pattern COD_EXP_PATTERN = Pattern.compile("([A-Za-z]+)(\\d+)(?:/(\\d+))?");

  @Value("${es.mjusticia.sinac.nombreOrganizacion}")
  private String nombreOrganizacion;

  @Value("${nfs.ruta.solicitudes}")
  private String nfsPathDocumentosSolicitudes;

  @Autowired
  private SinacSessionService sinacSession;

  @Autowired
  private NFSManager nfsManager;

  @Autowired
  private AntivirusConnector antivirusConnector;

  @Autowired
  private RegageConnector regageConnector;

  @Autowired
  private ClienteFirmaServidorConnector clienteFirmaServidorConnector;

  @Autowired
  private GestorDocumentalConnector gestorDocumentalConnector;

  @Autowired
  private NotificaConnector notificaConnector;

  @Autowired
  private ExpedienteDocumentoDao expedienteDocumentoDao;

  @Autowired
  private ExpedienteInformeDao expedienteInformeDao;

  @Autowired
  private RegistroDao registroDao;

  @Autowired
  private DocumentoTipoDao documentoTipoDao;

  @Autowired
  private SolicitudDocumentoDao solicitudDocumentoDao;

  @Autowired
  private PlantillaDao plantillaDao;

  @Autowired
  private PlantillasPlantillasCamposDao plantillasPlantillasCamposDao;

  @Autowired
  private ProcedimientosDocumentosTipoDao procedimientosDocumentosTipoDao;

  @Autowired
  private LdvMaestraDao ldvMaestraDao;

  @Autowired
  private FirmanteDao firmanteDao;

  @Autowired
  private ExpedienteFirmaDao expedienteFirmaDao;

  @Autowired
  private ExpedienteDocumentoWithExpedienteMapper expedienteDocumentoWithExpedienteMapper;

  @Autowired
  private RegistroMapper registroMapper;

  @Autowired
  private RegistroWithDocumentosMapper registroWithDocumentosMapper;

  @Autowired
  private DocumentoTipoMapper documentoTipoMapper;

  @Autowired
  private SolicitudDocumentoMapper solicitudDocumentoMapper;

  @Autowired
  private ExpedienteDocumentoMapper expedienteDocumentoMapper;

  @Autowired
  private PlantillaMapper plantillaMapper;

  @Autowired
  private PlantillasPlantillasCamposMapper plantillasPlantillasCamposMapper;

  @Autowired
  private ExpedienteMapper expedienteMapper;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private ExpedienteDocumentoMapper expedienteDocuemntoMapper;

  @Autowired
  WopiUtils wopi;

  @Autowired
  private PersonasService personaService;

  @Autowired
  private SolicitudesService solicitudesService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ExpedienteInformeMapper expedienteInformeMapper;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  private FirmanteMapper firmanteMapper;

  @Autowired
  private ExpedienteFirmaMapper expedienteFirmaMapper;

  @Autowired
  private ExpedienteFirmaMapperWithExpedienteMapper expedienteFirmaMapperWithExpedienteMapper;

  @Autowired
  private UsuarioMapper usuarioMapper;

  @Autowired
  private CopiaAutenticaConnector copiaAutenticaConnector;

  @Autowired
  private ExpedienteFormularioValDao expedienteFormularioValDao;

  @Autowired
  private ExpedienteInformeDgpDao expedienteInformeDgpDao;

  @Autowired
  private ExpedienteInformeDgpMapper expedienteInformeDgpMapper;

  @Autowired
  private ExpedienteInformeDgpTramitesDao expedienteInformeDgpTramitesDao;

  @Autowired
  private ExpedienteDao expedienteDao;

  @Autowired
  private ExpedienteInformeDgpTramitesMapper expedienteInformeDgpTramitesMapper;

  @Autowired
  private FileConverterUtil fcu;

  @Autowired
  private PaisesService paisesService;

  @Autowired
  private TiposViaDao tiposViaDao;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private GeiserService geiserService;

  @Autowired
  private AsientosService asientosService;

  @Autowired
  private ExpedienteDocumentoInformeMdeDao expedienteDocumentoInformeMdeDao;

  @Autowired
  private ExpedienteDocumentoInformeMdeMapper expedienteDocumentoInformeMdeMapper;

  @Autowired
  private PerCertificacionesDao perCertificacionesDao;

  @Override
  public DocumentoTipoDto getDocumentoTipoByIdDocumentoTipo(final Short idDocumentoTipo) throws SinacException {
    LOG.debug("DocumentosServiceImpl.getDocumentoTipoByIdDocumentoTipo - Init");
    DocumentoTipoDto documentoTipoDto = null;
    try {
      documentoTipoDto = documentoTipoMapper.toDto(documentoTipoDao.findById(idDocumentoTipo).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.MESSAGE_114)
          .logMessageParams(idDocumentoTipo).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_115).logMessageParams(idDocumentoTipo)
          .type(SinacExceptionType.DATA);
    }
    LOG.debug("DocumentosServiceImpl.getDocumentoTipoByIdDocumentoTipo - End");
    return documentoTipoDto;
  }

  /**
   * Obtiene la Solicitud del Documento asociada al Identificador de Tipo de
   * Documento establecido como parámetro.
   *
   * @param idDocumentoTipo Identificador del Tipo de Documento.
   * @return DTO con la Información de la Solicitud del Documento.
   * @throws SinacException Si se produce un error al obtener la Solicitud del
   *                        Documento.
   */
  private SolicitudDocumentoDto getSolicitudDocumentoByIdDocumentoTipo(final Short idDocumentoTipo,
      final BigInteger idSol) throws SinacException {
    LOG.debug("DocumentosServiceImpl.getSolicitudDocumentoByIdDocumentoTipo - Init");

    SolicitudDocumentoDto solicitudDocumentoDto = null;

    try {
      solicitudDocumentoDto = solicitudDocumentoMapper
          .toDto(solicitudDocumentoDao.getSolicitudDocumentoByIdDocumentoTipoAndSol(idDocumentoTipo, idSol));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.MESSAGE_116)
          .logMessageParams(idDocumentoTipo).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_117).logMessageParams(idDocumentoTipo)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("DocumentosServiceImpl.getSolicitudDocumentoByIdDocumentoTipo - End");

    return solicitudDocumentoDto;
  }

  /**
   * Obtiene el Tipo del Documento Anexo al Registro asociado al Identificador de
   * Tipo de Documento Sinac establecido como parámetro.
   *
   * @param idDocumentoTipo Identificador del Tipo de Documento Sinac.
   * @return DTO con la Información del Tipo del Documento Anexo al Registro.
   * @throws SinacException Si se produce un error al obtener el Tipo del
   *                        Documento Anexo al Registro.
   */
  private LdvMaestraDto getTipoDocumentoAnexoRegageByIdDocumentoTipo(final Short idDocumentoTipo)
      throws SinacException {
    LOG.debug("DocumentosServiceImpl.getTipoDocumentoAnexoRegageByIdDocumentoTipo - Init");

    DocumentoTipoDto documentoTipoDto = null;

    try {
      documentoTipoDto = documentoTipoMapper
          .toDto(documentoTipoDao.getTipoDocumentoAnexoRegageByIdDocumentoTipo(idDocumentoTipo).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.MESSAGE_114)
          .logMessageParams(idDocumentoTipo).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_115).logMessageParams(idDocumentoTipo)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("DocumentosServiceImpl.getTipoDocumentoAnexoRegageByIdDocumentoTipo - End");

    return documentoTipoDto.getIdCodTipoRegLdv();
  }

  /**
   * Obtiene el Tipo de Documento para el Gestor Documental asociado al
   * Identificador de Tipo de Documento Sinac establecido como parámetro.
   *
   * @param idDocumentoTipo Identificador del Tipo de Documento Sinac.
   * @return DTO con la Información del Tipo de Documento para el Gestor
   *         Documental.
   * @throws SinacException Si se produce un error al obtener el Tipo de Documento
   *                        para el Gestor Documental.
   */
  private LdvMaestraDto getTipoDocumentoGestorDocumentalByIdDocumentoTipo(final Short idDocumentoTipo)
      throws SinacException {
    LOG.debug("DocumentosServiceImpl.getTipoDocumentoGestorDocumentalByIdDocumentoTipo - Init");

    DocumentoTipoDto documentoTipoDto = null;

    try {
      documentoTipoDto = documentoTipoMapper
          .toDto(documentoTipoDao.getTipoDocumentoGestorDocumentalByIdDocumentoTipo(idDocumentoTipo).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.MESSAGE_114)
          .logMessageParams(idDocumentoTipo).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_115).logMessageParams(idDocumentoTipo)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("DocumentosServiceImpl.getTipoDocumentoGestorDocumentalByIdDocumentoTipo - End");

    return documentoTipoDto.getIdCodTipoGdLdv();
  }

  /**
   * Establece el Tipo de Registro de Regage asociado al Documento.
   *
   * @param tipoRegistro       Tipo de Registro en Regage.
   * @param documentoToSaveDto Documento para el que se ha de generar un nuevo
   *                           Registro en Regage.
   * @throws SinacException Si se produce un error al establece el Tipo de
   *                        Registro de Regage asociado al Documento.
   */
  private void setRegistroEntradaSalida(final TipoRegistroRegageEnum tipoRegistro,
      final DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LdvMaestraDto ldvMaestraDto = null;
    if (TipoRegistroRegageEnum.ENTRADA.equals(tipoRegistro)) {
      ldvMaestraDto = catalogosService.getCatalogoByCod("OREG-ENT");
      documentoToSaveDto.setRegistroEntradaSalida(ldvMaestraDto.getIdLdvMae());
    } else if (TipoRegistroRegageEnum.SALIDA.equals(tipoRegistro)) {
      ldvMaestraDto = catalogosService.getCatalogoByCod("OREG-SAL");
      documentoToSaveDto.setRegistroEntradaSalida(ldvMaestraDto.getIdLdvMae());
    }
  }

  /**
   * Establece el Registro Manual y el Registro de Regage asociado al Documento.
   *
   * @param numeroRegistro     Número de Registro.
   * @param fechaHoraRegistro  Fecha de Registro.
   * @param documentoToSaveDto Documento.
   */
  private void setRegistroManualAndRegistroRegage(final String numeroRegistro,
      final XMLGregorianCalendar fechaHoraRegistro, final DocumentoToSaveDto documentoToSaveDto) {
    RegistroDto registroDto = new RegistroDto();
    registroDto.setNumReg(numeroRegistro);
    registroDto.setFechaReg(fechaHoraRegistro.toGregorianCalendar().getTime());
    registroDto.setFlgActivo(false);
    registroDto.setRegGenerado(true);

    documentoToSaveDto.getDocumentoFlagsToSaveDto().setRegistrado(true);
    documentoToSaveDto.getRegistroDtos().add(registroDto);

    if (StringUtils.isNotEmpty(documentoToSaveDto.getNumeroRegistro())
        && documentoToSaveDto.getFechaRegistro() != null) {
      registroDto = new RegistroDto();
      registroDto.setNumReg(documentoToSaveDto.getNumeroRegistro());
      registroDto.setFechaReg(documentoToSaveDto.getFechaRegistro());
      registroDto.setFlgActivo(true);

      documentoToSaveDto.getRegistroDtos().add(registroDto);
    }
  }

  @Override
  public String copyDocumentosSolicitudesNFS(final String codSolicitud, final String codProcedimiento,
      final List<DocumentoToSaveDto> documentoToSaveDtoList) {
    LOG.debug("DocumentosServiceImpl.copyDocumentosSolicitudesNFS - Init");

    documentoToSaveDtoList.forEach(documentoToSaveDto -> {
      documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidado(true);
      if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getValidado())) {
        try {
          final String nfsPathDocument = nfsManager.getNFSPathDocumentosSolicitudesForDocument(codSolicitud,
              codProcedimiento);

          final DataSource dataSource = nfsManager.getDataSource(documentoToSaveDto.getNombre(),
              documentoToSaveDto.getContenido());

          nfsManager.getContentRepository(nfsPathDocument).save(dataSource);

          documentoToSaveDto.getDocumentoFlagsToSaveDto().setCopiadoNFS(true);
        } catch (final ContentRepositoryException contentRepositoryException) {
          LOG.error(
              String.format("DocumentosServiceImpl.copyDocumentosSolicitudesNFS - Error: %s",
                  Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre() + LITERAL_NO_COPIADO),
              contentRepositoryException);
        }
      }
    });

    LOG.debug("DocumentosServiceImpl.copyDocumentosSolicitudesNFS - End");

    return nfsManager.getNFSPathDocumentosSolicitudesForDocument(codSolicitud, codProcedimiento);
  }

  @Override
  public void deleteDocumentosSolicitudesNFS(final List<DocumentoToSaveDto> documentoToSaveDtoList) {
    LOG.debug("DocumentosServiceImpl.deleteDocumentosSolicitudesNFS - Init");

    documentoToSaveDtoList.forEach(documentoToSaveDto -> {
      try {
        final String nfsPathDocument = documentoToSaveDto.getRutaNFS();

        final DataSource dataSource = nfsManager.getDataSource(documentoToSaveDto.getNombre(),
            nfsManager.getNfsEnvironmentPath() + nfsPathDocument + STRING_SEPARATOR);

        if (dataSource != null) {
          nfsManager.getContentRepository(nfsPathDocument + STRING_SEPARATOR).delete(dataSource);
        }

      } catch (final ContentRepositoryException contentRepositoryException) {
        LOG.error(String.format("DocumentosServiceImpl.deleteDocumentosSolicitudesNFS - Error: %s",
            Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre()
                + "\" no ha sido borrado de NFS porque ha habido un error durante el proceso de borrado del "
                + "documento."),
            contentRepositoryException);
      }
    });

    LOG.debug("DocumentosServiceImpl.deleteDocumentosSolicitudesNFS - End");
  }

  /**
   * Establece el Flag Activo a <b>false</b> y guarda un Histórico de los
   * Documentos asociados al Expediente con Tipo de Documento igual al Tipo de
   * Documento establecido como parámetro.
   *
   * @param expedienteDto    Expediente.
   * @param documentoTipoDto Tipo de Documento.
   */
  private void setFlagActivoToFalseAndSaveExpedienteDocumentoHistorico(final ExpedienteDto expedienteDto,
      final DocumentoTipoDto documentoTipoDto) {
    if (documentoTipoDto != null && StringUtils.isNotEmpty(documentoTipoDto.getCodTipo())) {
      Set<ExpedienteDocumentoEntity> expedienteDoc = expedienteDocumentoDao
          .getExpedienteDocumentosByIdExpAndCodTipo(expedienteDto.getIdExp(), documentoTipoDto.getCodTipo());
      if (!CollectionUtils.isEmpty(expedienteDoc)) {
        for (ExpedienteDocumentoEntity expedienteDocumentoEntity : expedienteDoc) {
          ExpedienteDocumentoDto expDoc = expedienteDocuemntoMapper.toDto(expedienteDocumentoEntity);
          expDoc.setFlgActivo(false);
          saveExpedienteDocumentoHistorico(expDoc, expedienteDto);
        }
      }
    }
  }

  /**
   * Guarda los Registros asociados al Documento.
   *
   * @param expedienteDocumentoDto Documento.
   * @param documentoToSaveDto     DTO con la Lista de Registros a guardar
   *                               asociados al Documento.
   * @param isCreateExpediente     Flag que determina si la Operación de origen es
   *                               la creación del expediente.
   */
  private void saveRegistrosOfDocumentoToSaveDtoForExpedienteDocumentoDto(
      final ExpedienteDocumentoDto expedienteDocumentoDto, final DocumentoToSaveDto documentoToSaveDto,
      final boolean isCreateExpediente, final BigInteger idSol) {
    LOG.debug("Init - DocumentosServiceImpl.saveRegistrosOfDocumentoToSaveDtoForExpedienteDocumentoDto el documento {}",
        documentoToSaveDto.getIdExpDoc());
    for (RegistroDto registroDto : documentoToSaveDto.getRegistroDtos()) {
      boolean setearFlagActivoFalse = false;
      if (documentoToSaveDto.getRegistroDtos().size() == 1) {
        registroDto.setFlgActivo(true);
      } else if (documentoToSaveDto.getRegistroDtos().size() > 1 && !registroDto.isFlgActivo()) {
        // esto se hace asi por que en la clase de auditoria se setea el flgActivo a 1
        // aunque este venga a 0
        setearFlagActivoFalse = true;

      }

      registroDto.setExpedienteDocumentoDto(expedienteDocumentoDto);

      if (isCreateExpediente) {
        registroDto.setSolicitudDocumentoDto(
            getSolicitudDocumentoByIdDocumentoTipo(documentoToSaveDto.getTipoDocumento(), idSol));
      }

      registroDto.setLdvMaestraDto(catalogosService.getCatalogoById(documentoToSaveDto.getRegistroEntradaSalida()));
      if (setearFlagActivoFalse) {
        BigInteger id = registroWithDocumentosMapper
            .toDto(registroDao.save(registroWithDocumentosMapper.toEntity(registroDto))).getIdReg();
        registroDto.setFlgActivo(false);
        registroDto.setIdReg(id);
      }
      registroDao.save(registroWithDocumentosMapper.toEntity(registroDto));
    }
    LOG.debug("End - DocumentosServiceImpl.saveRegistrosOfDocumentoToSaveDtoForExpedienteDocumentoDto el documento {}",
        documentoToSaveDto.getIdExpDoc());
  }

  /**
   * Valida el Tamaño del Documento.
   *
   * @param documentoToSaveDto DTO con la Información del Documento a validar el
   *                           Tamaño.
   * @return true, si el Tamaño del Documento se ha validado correctamente. false,
   *         en caso contrario.
   */
  private boolean validarTamanyoDocumento(final DocumentoToSaveDto documentoToSaveDto) {
    LOG.debug("Init - DocumentosServiceImpl.validarTamanyoDocumento del documento: {}", documentoToSaveDto.getNombre());

    final String nombreDocumento = documentoToSaveDto.getNombre();

    if (!Validaciones.validarTamanyoDocumento(nombreDocumento, documentoToSaveDto.getContenido())) {
      documentoToSaveDto.setError(Literal.EL_DOCUMENTO + nombreDocumento
          + "\" no ha sido adjuntado porque el campo \"Extensión\" no cumple la validación.");

      if (LOG.isErrorEnabled()) {
        LOG.error(
            String.format("DocumentosServiceImpl.validarTamanyoDocumento - Error: %s", documentoToSaveDto.getError()));
      }

      return false;
    }
    LOG.info("Validación correcta del expDocumento {} - {}", documentoToSaveDto.getIdExpDoc(), nombreDocumento);
    LOG.debug("End - DocumentosServiceImpl.validarTamanyoDocumento del documento: {}", nombreDocumento);

    return true;
  }

  /**
   * Valida la Extensión del Documento.
   *
   * @param documentoToSaveDto DTO con la Información del Documento a validar la
   *                           Extensión.
   * @return true, si la Extensión del Documento se ha validado correctamente.
   *         false, en caso contrario.
   */
  private boolean validarExtensionDocumento(final DocumentoToSaveDto documentoToSaveDto) {
    LOG.debug("Init - DocumentosServiceImpl.validarExtensionDocumento del documento: {}",
        documentoToSaveDto.getNombre());

    final String nombreDocumento = documentoToSaveDto.getNombre();

    if (!Validaciones.validarExtensionDocumento(nombreDocumento)) {
      documentoToSaveDto.setError(Literal.EL_DOCUMENTO + nombreDocumento
          + "\" no ha sido adjuntado porque el campo \"Tamaño\" no cumple la validación.");

      if (LOG.isErrorEnabled()) {
        LOG.error(String.format("DocumentosServiceImpl.validarExtensionDocumento - Error: %s",
            documentoToSaveDto.getError()));
      }

      return false;
    }
    LOG.info("Validación de extensión correcta del documento: {}", nombreDocumento);
    LOG.debug("End - DocumentosServiceImpl.validarExtensionDocumento del documento: {}", nombreDocumento);

    return true;
  }

  /**
   * Valida los Campos requeridos para el Documento.
   *
   * @param documentoToSaveDto DTO con la Información del Documento a validar.
   * @return true, si el Documento se ha validado correctamente. false, en caso
   *         contrario.
   */
  private boolean validarCamposDocumento(final DocumentoToSaveDto documentoToSaveDto) {
    LOG.debug("Init - DocumentosServiceImpl.validarCamposDocumento del documento: {}", documentoToSaveDto.getNombre());

    for (String campo : ValidarDocumentosEntradaExpediente.getNombreCamposValidarForDocumentosEntradaExpediente()) {
      try {
        final String metodo = StringUtils.stripAccents(StringUtils.replace(campo, " ", ""));
        final Object valor = documentoToSaveDto.getClass().getDeclaredMethod("get" + metodo).invoke(documentoToSaveDto);
        final Class<?> tipoParametro = ValidarDocumentosEntradaExpediente.getTiposParametros().get(campo);

        if (!(boolean) Validaciones.class.getDeclaredMethod("validar" + metodo, tipoParametro)
            .invoke(Validaciones.class, valor)) {
          documentoToSaveDto.setError(Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre()
              + "\" no ha sido adjuntado porque el campo \"" + campo + "\" no cumple la validación.");

          if (LOG.isErrorEnabled()) {
            LOG.error(String.format("DocumentosServiceImpl.validarCamposDocumento - Error: %s",
                documentoToSaveDto.getError()));
          }

          return false;
        }
      } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
        LOG.error(String.format("DocumentosServiceImpl.validarCamposDocumento - Error: %s", exception.getMessage()),
            exception);
      }
    }
    LOG.info("Validación de campos correcta del documento {}", documentoToSaveDto.getNombre());
    LOG.debug("End - DocumentosServiceImpl.validarCamposDocumento");

    return true;
  }

  @Override
  public boolean convertirDocumentoEditableEnPdf(ExpedienteDocumentoDto expDoc) throws SinacException {
    LOG.debug("Init - ExpedienteDocumentoServiceImpl.convertirDocumentoEditableEnPdf del expDoc: {}",
        expDoc.getNomDoc());
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao
        .findExpedienteDocumentoById(expDoc.getIdExpDoc());

    int numReintentos = 1;
    int numMaximoReintentos = 5;
    boolean ficheroVacio = true;
    FileDataSource dataSource = null;
    while (numReintentos <= numMaximoReintentos && ficheroVacio) {
      dataSource = (FileDataSource) nfsManager.getDataSource(expedienteDocumentoEntity.getNomDoc(),
          expedienteDocumentoEntity.getNfsRuta());
      LOG.info("Se recupera documento {} - {} para convertirlo a pdf. Tamaño del fichero {}. Número de intento: {}",
          expDoc.getIdExpDoc(), expedienteDocumentoEntity.getNomDoc(), dataSource.getFile().length(), numReintentos);
      if (dataSource.getFile().length() > 0)
        ficheroVacio = false;
      numReintentos++;
      if (numReintentos > 0 && ficheroVacio)
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          LOG.error(
              "Error de interrupción esperando para reintentar obtener fichero tras encontrarlo vacío en la conversión de formato. idDocumento: {}, Error:{}",
              expDoc.getIdExpDoc(), e.getMessage());
        }
    }

    if (numReintentos > numMaximoReintentos)
      throw new SinacException(SinacExceptionMessageType.MESSAGE_170).logMessageParams(expDoc.getIdExpDoc())
          .type(SinacExceptionType.VALIDATION);

    /*
     * validacion para extension odt
     */
    if (expedienteDocumentoEntity.getNomDoc().contains(".")) {
      String extension = expedienteDocumentoEntity.getNomDoc().substring(
          expedienteDocumentoEntity.getNomDoc().lastIndexOf(".") + 1, expedienteDocumentoEntity.getNomDoc().length());
      if ("odt".equalsIgnoreCase(extension)) {
        expedienteDocumentoEntity.setNomDoc(
            expedienteDocumentoEntity.getNomDoc().substring(0, expedienteDocumentoEntity.getNomDoc().lastIndexOf("."))
                + ".pdf");
        if (dataSource == null) {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_118)
              .logMessageParams(expedienteDocumentoEntity.getNomDoc(), expedienteDocumentoEntity.getNfsRuta())
              .type(SinacExceptionType.DATA);
        }

        fcu.transformarDocumento("odt", "pdf", expedienteDocumentoEntity.getNomDoc(),
            expedienteDocumentoEntity.getNfsRuta(), dataSource);
        expedienteDocumentoEntity = expedienteDocumentoDao.save(expedienteDocumentoEntity);
        expDoc.copy(expedienteDocuemntoMapper.toDto(expedienteDocumentoEntity));
        LOG.debug("End - ExpedienteDocumentoServiceImpl.convertirDocumentoEditableEnPdf del expDoc: {}",
            expDoc.getNomDoc());
        return true;
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_119)
            .logMessageParams(expedienteDocumentoEntity.getNomDoc()).type(SinacExceptionType.VALIDATION);
      }
    } else {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_120)
          .logMessageParams(expedienteDocumentoEntity.getNomDoc()).type(SinacExceptionType.VALIDATION);
    }
  }

  @Override
  public ExpedienteDocumentoDto saveExpedienteDocumento(ExpedienteDocumentoDto expedienteDocumentoDto,
      ExpedienteDto expedienteDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.saveExpedienteDocumento del documento {} del expediente {} con codExp {}",
        expedienteDocumentoDto.getNomDoc(), expedienteDto.getIdExp(), expedienteDto.getCodExp());
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoMapper.toEntity(expedienteDocumentoDto);
    try {
      expedienteDocumentoEntity.setExpedienteEntity(expedienteMapper.toEntity(expedienteDto));
      expedienteDocumentoEntity.setVersion(expedienteDto.getVersion());
      if (expedienteDocumentoEntity.getIdExpDoc() != null) {
        ExpedienteDocumentoEntity expedienteDocumentoEntityAux = expedienteDocumentoDao
            .getExpedienteDocumentoByIdDocumentoIdExpediente(expedienteDocumentoDto.getIdExpDoc(),
                expedienteDto.getIdExp());
        expedienteDocumentoEntity.setFlgActivo(expedienteDocumentoEntityAux.isFlgActivo());
        expedienteDocumentoEntity.setFechaCreacion(expedienteDocumentoEntityAux.getFechaCreacion());
        expedienteDocumentoEntity.setFechaIniVig(expedienteDocumentoEntityAux.getFechaIniVig());
      }
      expedienteDocumentoEntity = expedienteDocumentoDao.save(expedienteDocumentoEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_121)
          .logMessageParams(expedienteDocumentoDto.getIdExpDoc()).type(SinacExceptionType.DATA);
    }
    LOG.info("Se ha guardado correctamente en la Base de Datos el documento {} con idExpDoc: {}",
        expedienteDocumentoDto.getNomDoc(), expedienteDocumentoDto.getIdExpDoc());
    LOG.debug("End - DocumentosServiceImpl.saveExpedienteDocumento del documento {} del expediente {} con codExp {}",
        expedienteDocumentoDto.getNomDoc(), expedienteDto.getIdExp(), expedienteDto.getCodExp());
    return expedienteDocumentoMapper.toDto(expedienteDocumentoEntity);
  }

  @Override
  public ExpedienteDocumentoDto saveExpedienteDocumentoHistorico(ExpedienteDocumentoDto expedienteDocumentoDto,
      ExpedienteDto expedienteDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.saveExpedienteDocumentoHistorico del documento {} del expediente {}",
        expedienteDocumentoDto.getNomDoc(), expedienteDto.getIdExp());
    ExpedienteDocumentoEntity expedienteDocumentoEntityAnterior = expedienteDocumentoDao
        .findExpedienteDocumentoById(expedienteDocumentoDto.getIdExpDoc());
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoMapper.toEntity(expedienteDocumentoDto);
    if (expedienteDocumentoEntityAnterior != null) {
      try {
        expedienteDocumentoEntity.setExpedienteEntity(expedienteMapper.toEntity(expedienteDto));
        expedienteDocumentoEntity.setFechaFinVig(new Date());
        expedienteDocumentoEntity.setCreadoPor(expedienteDocumentoEntityAnterior.getCreadoPor());
        expedienteDocumentoEntity.setFechaIniVig(expedienteDocumentoEntityAnterior.getFechaIniVig());
        expedienteDocumentoEntity.setFechaCreacion(expedienteDocumentoEntityAnterior.getFechaCreacion());
        expedienteDocumentoEntity = expedienteDocumentoDao.save(expedienteDocumentoEntity);
      } catch (final Exception exception) {
        throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_121)
            .logMessageParams(expedienteDocumentoDto.getIdExpDoc()).type(SinacExceptionType.DATA);
      }
    }
    LOG.info("Se ha guardado en la Base de Datos el documento {} correctamente", expedienteDocumentoDto.getIdExpDoc());
    LOG.debug("End - DocumentosServiceImpl.saveExpedienteDocumentoHistorico del documento {} del expediente {}",
        expedienteDocumentoDto.getNomDoc(), expedienteDto.getIdExp());
    return expedienteDocumentoMapper.toDto(expedienteDocumentoEntity);
  }

  @Override
  public List<PlantillaDto> getPlantillas() {
    List<PlantillaEntity> plantillasEntities = plantillaDao.getAllPlantillasActivas();
    return plantillasEntities.stream().map(e -> plantillaMapper.toDto(e)).toList();
  }

  @Override
  public List<ExpedienteInformeDto> getExpedienteInformesByIdExpediente(BigInteger idExpediente, int idLdvCaducado) {
    List<ExpedienteInformeEntity> expedienteInformeEntities = expedienteInformeDao
        .getExpedienteInformesByIdExpediente(idExpediente, idLdvCaducado);
    return expedienteInformeEntities.stream().map(e -> expedienteInformeMapper.toDto(e)).toList();
  }

  @Override
  public RegistroDto getRegistroByIdSolicitudDocumento(BigInteger idSolicitudDocumento) {
    RegistroEntity registroEntities = registroDao.getRegistroByIdSolicitudDocumento(idSolicitudDocumento);
    return registroMapper.toDto(registroEntities);
  }

  @Override
  public PlantillaDto getPlantillaById(short id) throws SinacException {
    PlantillaEntity plantillaEntity = null;
    try {
      plantillaEntity = plantillaDao.getPlantillaByIdJoins(id);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.MESSAGE_122).logMessageParams(id)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_123).logMessageParams(id)
          .type(SinacExceptionType.DATA);
    }
    return plantillaMapper.toDto(plantillaEntity);
  }

  @Override
  public ExpedienteDocumentoDto createExpedienteDocumentoPlantillaContent(PlantillaDto plantillaDto,
      ExpedienteDto expedienteDto, ExpedienteDocumentoDto expedienteDocumentoDto, Map<String, String> valoresPlantillas)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.createExpedienteDocumentoPlantillaContent de la plantilla {}, expedienteDto {} y expedienteDocumentoDto {}",
        plantillaDto.getIdPla(), expedienteDto.getIdExp(), expedienteDocumentoDto.getIdExpDoc());
    byte[] contenido = null;
    String codPlantillaIndice = "INDI" + expedienteDto.getProcedimientoDto().getCodCorto();
    expedienteDocumentoDto.setDocumentoTipoDto(
        getDocumentoTipoByIdDocumentoTipo(plantillaDto.getProDocTipo().getDocumentosTipo().getIdDocTipo()));
    try {
      String nfsPathDocument = nfsManager.getNFSPathForDocument(expedienteDto.getCodExp(),
          expedienteDto.getProcedimientoDto().getCodPro(), expedienteDto.getFechaEfectos());
      List<PlantillasPlantillasCamposDto> plantillasCampos = getPlantillasPlantillasCamposByIdPlantilla(
          plantillaDto.getIdPla());
      LOG.info(
          "createExpedienteDocumentoPlantillaContent - Se han recuperado los campos para la plantilla {}. Los campos son: {}",
          plantillaDto.getNomPlantilla(), plantillasCampos);
      DataSource dataSource = nfsManager.getDataSource(plantillaDto.getNomPlantilla() + ".odt",
          plantillaDto.getNfsRuta());
      if (dataSource != null) {
        manejoHistoricoDocumentos(expedienteDto, expedienteDocumentoDto);
        int numOrden = expedienteDocumentoDao.getContadorDocumentosTipo(expedienteDto.getIdExp(),
            expedienteDocumentoDto.getDocumentoTipoDto().getIdDocTipo());
        numOrden += 1;
        String nombreDocumento = "";
        if (!plantillaDto.getCodPlantilla().equals(codPlantillaIndice)) {
          nombreDocumento = asignarNombreDocumento(expedienteDto, expedienteDocumentoDto, numOrden);
        } else {
          nombreDocumento = asignarNombreDocumentoIndice(expedienteDto, expedienteDocumentoDto, numOrden);
        }

        LOG.info("copyDocumentoPlantillaNFS - El nombre final del documento es: {}", nombreDocumento);
        // En el caso del content.xml tendremos que procesarlo y
        // reemplazar variables
        Properties varValues = new Properties();
        ExpedienteInformeDgpDto expedienteInformeDgp = new ExpedienteInformeDgpDto();
        List<ExpedienteInformeDgpTramiteDto> expedienteInformeDgpTramites = new ArrayList<>();
        if (plantillaDto.getCodPlantilla().equals("IDGPCN") || plantillaDto.getCodPlantilla().equals("IDGPDR")
            || plantillaDto.getCodPlantilla().equals("IDGPR") || plantillaDto.getCodPlantilla().equals("IDGPRR")) {
          expedienteInformeDgp = getExpedienteInformeDgpByCodExpediente(expedienteDto.getCodExp());
          expedienteInformeDgpTramites = getExpedienteInformesDgpTramitesByIdExpedienteInformeDgp(
              expedienteInformeDgp.getIdExpInfDgp());
        }
        if (plantillaDto.getCodPlantilla().equals(codPlantillaIndice)) {
          getValoresPlantillaIndice(plantillaDto, expedienteDto, plantillasCampos, varValues);
        } else {
          getValoresPlantilla(plantillaDto, expedienteDto, plantillasCampos, varValues, expedienteInformeDgp,
              expedienteInformeDgpTramites, valoresPlantillas);
        }
        if (plantillasCampos != null && !plantillasCampos.isEmpty()) {
          LOG.info("copyDocumentoPlantillaNFS - Se va a incluir los valores a los campos");

          contenido = modificarPlantillaConDatos(plantillaDto, varValues, expedienteDto);

        } else {
          LOG.info(
              "createExpedienteDocumentoPlantillaContent - La plantilla '{}' no tiene campos configurados en base de datos. No se settea el contenido.",
              plantillaDto.getNomPlantilla());
        }
        cargarDatosExpedienteDocumentoPlantilla(plantillaDto, expedienteDto, expedienteDocumentoDto,
            nfsPathDocument + STRING_SEPARATOR, nombreDocumento);
        if (Boolean.FALSE.equals(plantillaDto.getEditable())) {
          // Si no es editable se convierte directamente a pdf
          expedienteDocumentoDto.setNomDoc(expedienteDocumentoDto.getNomDoc().replaceAll(".odt", ".pdf"));
          LOG.info(
              "createExpedienteDocumentoPlantillaContent - La plantilla '{}' no es editable, se va a convertir a pdf",
              plantillaDto.getCodPlantilla());
          contenido = cambiarFormatoDocumentoPlantilla(contenido, "odt", "pdf");
        }
        expedienteDocumentoDto.setContenido(contenido);
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_124).logMessageParams(plantillaDto.getNomPlantilla(),
            expedienteDto.getIdExp());
      }
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_125)
          .logMessageParams(expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo(), expedienteDto.getIdExp())
          .userMessageParams(expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo());
    }
    LOG.debug(
        "End - DocumentosServiceImpl.createExpedienteDocumentoPlantillaContent de la plantilla {}, expedienteDto {} y expedienteDocumentoDto {}",
        plantillaDto.getIdPla(), expedienteDto.getIdExp(), expedienteDocumentoDto.getIdExpDoc());
    return expedienteDocumentoDto;
  }

  private Properties getValoresPlantilla(PlantillaDto plantillaDto, ExpedienteDto expedienteDto,
      List<PlantillasPlantillasCamposDto> plantillasCampos, Properties varValues,
      ExpedienteInformeDgpDto expedienteInformeDgp, List<ExpedienteInformeDgpTramiteDto> expedienteInformeDgpTramites,
      Map<String, String> valoresPlantillas) throws NoSuchFieldException, IllegalAccessException {

    LOG.debug("Init - DocumentosServiceImpl.getValoresPlantilla de la plantilla {} del expediente {}",
        plantillaDto.getCodPlantilla(), expedienteDto.getIdExp());

    for (PlantillasPlantillasCamposDto plantillaCampo : plantillasCampos) {
      String nombreCampo = plantillaCampo.getPlanitllaCamposDto().getNomCampo();
      LOG.info("copyDocumentoPlantillaNFS - Se va a procesar el campo: {}", nombreCampo);
      if (plantillaDto.getCodPlantilla().equals("IDGPCN") || plantillaDto.getCodPlantilla().equals("IDGPDR")
          || plantillaDto.getCodPlantilla().equals("IDGPR") || plantillaDto.getCodPlantilla().equals("IDGPRR")) {
        if (!nombreCampo.contains("tramites")) {
          varValues.put(nombreCampo, obtenerValoresInfDgp(nombreCampo, expedienteInformeDgp, expedienteDto));
        } else {
          varValues.put(nombreCampo, obtenerValoresTramites(expedienteInformeDgpTramites));
        }
      } else if (valoresPlantillas == null || !valoresPlantillas.containsKey(nombreCampo)) {
        if (valoresPlantillas != null && nombreCampo.equals("documentosRequeridos")) {
          String listaDocumentosString = valoresPlantillas.get("campo_DocumentosARequerir");
          varValues.put(nombreCampo,
              getDocumentosRequeridosListado(plantillaDto.getCodPlantilla(), listaDocumentosString, new StringBuilder())
                  .toString());
        } else {
          varValues.put(nombreCampo, obtenerValorCampo(expedienteDto, nombreCampo));
        }

      } else {
        varValues.put(nombreCampo, valoresPlantillas.get(nombreCampo));
      }
    }
    LOG.info("varValues = {}", varValues);
    LOG.debug("End - DocumentosServiceImpl.getValoresPlantilla de la plantilla {} del expediente {}",
        plantillaDto.getCodPlantilla(), expedienteDto.getIdExp());
    return varValues;
  }

  private Properties getValoresPlantillaIndice(PlantillaDto plantillaDto, ExpedienteDto expedienteDto,
      List<PlantillasPlantillasCamposDto> plantillasCampos, Properties varValues) {
    LOG.debug("Init - DocumentosServiceImpl.getValoresPlantillaIndice de la plantilla {} del expediente {}",
        plantillaDto.getCodPlantilla(), expedienteDto.getIdExp());
    for (PlantillasPlantillasCamposDto plantillaCampo : plantillasCampos) {
      String nombreCampo = plantillaCampo.getPlanitllaCamposDto().getNomCampo();
      LOG.info("PlantillaENI - Se va a procesar el campo: {}", nombreCampo);

      ExpedienteInformeIndiceExpDto indiceDtoData = getIndiceElecData(expedienteDto);

      // Añadimos los documentos
      List<ExpedienteDocumentoDto> documentosDtoOk = new ArrayList<>();
      List<ExpedienteDocumentoDto> documentosDto = expedienteDto.getExpedienteDocumentoDtos();
      for (ExpedienteDocumentoDto expDocDto : documentosDto) {
        if (expDocDto.getCodGd() != null) {
          documentosDtoOk.add(expDocDto);
        }
      }
      varValues.put("documentos.indiceENI", documentosDtoOk);

      // Añadimos las personas
      List<ExpedientePersonaIndiceDto> personasDtoOk = new ArrayList<>();
      List<ExpedientesPersonasDto> personasDto = expedienteDto.getExpedientesPersonasDtos();
      for (ExpedientesPersonasDto expPerDto : personasDto) {
        if (expPerDto.getPersonaDto() != null) {
          ExpedientePersonaIndiceDto expPersonaDto = new ExpedientePersonaIndiceDto();
          expPersonaDto.setNombre(expPerDto.getPersonaDto().getNombre());
          expPersonaDto.setApellido1(expPerDto.getPersonaDto().getApellido1());
          expPersonaDto.setApellido2(expPerDto.getPersonaDto().getApellido2());
          expPersonaDto
              .setNumAcreditacion(expPerDto.getPersonaDto().getPersonasIdentificaDtos().get(0).getNumAcreditacion());
          expPersonaDto.setTipoAcreditacion(
              expPerDto.getPersonaDto().getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getNomLdvMae());
          expPersonaDto.setTipoPersona(expPerDto.getLdvMaestraDto().getNomLdvMae());
          personasDtoOk.add(expPersonaDto);
        }
      }
      varValues.put("personas.indiceENI", personasDtoOk);

      // Añadimos el resto
      varValues.put(nombreCampo, obtenerValoresIndiceExp(nombreCampo, indiceDtoData));

    }
    LOG.info("varValues = {}", varValues);
    LOG.debug("End - DocumentosServiceImpl.getValoresPlantillaIndice de la plantilla {} del expediente {}",
        plantillaDto.getCodPlantilla(), expedienteDto.getIdExp());
    return varValues;
  }

  private StringBuilder getDocumentosRequeridosListado(String codPlantilla, String textoCompleto,
      StringBuilder valorConcatenado) throws SinacException {
    String[] arrayString = textoCompleto.split("\\\\n");
    int contador = 0;
    for (String texto : arrayString) {
      contador++;
      valorConcatenado.append(texto.replace("• ", ""));
      if (arrayString.length > 1 && contador != arrayString.length) {
        switch (codPlantilla) {
        case "OREQCN":
          valorConcatenado.append(
              "</text:p></text:list-item><text:list-item><text:p text:style-name=\"P9\" loext:marker-style-name=\"T20\">");
          break;
        case "RQADIC":
          valorConcatenado.append("</text:p></text:list-item><text:list-item><text:p text:style-name=\"P18\">");
          break;
        case "RQMESORDIC":
          valorConcatenado.append("</text:p></text:list-item><text:list-item><text:p text:style-name=\"P17\">");
          break;
        case "OSIDR":
          valorConcatenado.append("</text:p></text:list-item><text:list-item><text:p text:style-name=\"P35\">");
          break;
        default:
          valorConcatenado.append(
              "</text:p></text:list-item><text:list-item><text:p text:style-name=\"P9\" loext:marker-style-name=\"T20\">");
        }
      }
    }
    return valorConcatenado;
  }

  /**
   * Método para obtener el string que genera las filas de la tabla de trámites
   * necesarias para el informe DGP
   * 
   * @param varValues
   * @param expedienteInformeDgpTramites
   * @param nombreCampo
   */
  private String obtenerValoresTramites(List<ExpedienteInformeDgpTramiteDto> expedienteInformeDgpTramites) {
    LOG.debug("Init - DocumentosServiceImpl.obtenerValoresTramites");
    StringBuilder filasTabla = new StringBuilder();
    int contador = 0;
    for (ExpedienteInformeDgpTramiteDto expedienteInformeDgpTramiteDto : expedienteInformeDgpTramites) {
      // Cada dto es una fila nueva, si no es el primer elemento se abre nueva
      // etiqueta de row
      if (contador != 0) {
        filasTabla.append(
            "<table:table-row table:style-name=\"Tabla5.2\">" + OPEN_TABLE_CELL_DGP + TABLE_DGP_OPEN_TEXT_CELL_TRAMITE);
      }
      SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
      String fechaConcesion = " ";
      fechaConcesion = formatDate(expedienteInformeDgpTramiteDto.getFechaConcesion(), formatter, fechaConcesion);
      String fechaSolicitud = " ";
      fechaSolicitud = formatDate(expedienteInformeDgpTramiteDto.getFechaSolicitud(), formatter, fechaSolicitud);
      String fechaValidez = " ";
      fechaValidez = formatDate(expedienteInformeDgpTramiteDto.getFechaValidez(), formatter, fechaValidez);
      String fechaDenegacion = " ";
      fechaDenegacion = formatDate(expedienteInformeDgpTramiteDto.getFechaDenegacion(), formatter, fechaDenegacion);
      filasTabla.append(getColumnasTabla(expedienteInformeDgpTramiteDto.getTramite(), fechaSolicitud, fechaConcesion,
          fechaValidez, fechaDenegacion));
      contador++;
    }
    if (filasTabla.toString().isEmpty()) {
      filasTabla.append(getColumnasTabla("", "", "", "", ""));
    }
    LOG.debug("End - DocumentosServiceImpl.obtenerValoresTramites");
    return filasTabla.toString();
  }

  private String formatDate(Date fecha, SimpleDateFormat formatter, String fechaConcesion) {
    if (fecha != null) {
      fechaConcesion = formatter.format(fecha);
    }
    return fechaConcesion;
  }

  private String getColumnasTabla(String tramite, String fechaSolicitud, String fechaConcesion, String fechaValidez,
      String fechaDenegacion) {
    return tramite + CLOSE_TABLE_CELL_DGP + OPEN_TABLE_CELL_DGP + TABLE_DGP_OPEN_TEXT_CELL + fechaSolicitud
        + CLOSE_TABLE_CELL_DGP + OPEN_TABLE_CELL_DGP + TABLE_DGP_OPEN_TEXT_CELL + fechaConcesion + CLOSE_TABLE_CELL_DGP
        + OPEN_TABLE_CELL_DGP + TABLE_DGP_OPEN_TEXT_CELL + fechaValidez + CLOSE_TABLE_CELL_DGP + OPEN_TABLE_CELL_DGP
        + TABLE_DGP_OPEN_TEXT_CELL + fechaDenegacion + CLOSE_TABLE_CELL_DGP + "</table:table-row>\r\n";
  }

  private String obtenerValoresInfDgp(String nombreCampo, ExpedienteInformeDgpDto expedienteInformeDgp,
      ExpedienteDto expedienteDto) {
    String[] campoArray = nombreCampo.split("\\.");
    String valor = "";
    SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
    switch (campoArray[0]) {
    case "nombre":
      valor = expedienteInformeDgp.getNombre();
      break;
    case "identificador":
      valor = expedienteInformeDgp.getIdentificador();
      break;
    case "apellidos":
      valor = obtenerApellidos(expedienteInformeDgp);
      break;
    case "fechaNacimiento":
      if (expedienteInformeDgp.getFechaNacimiento() != null) {
        valor = formatter.format(expedienteInformeDgp.getFechaNacimiento());
      }
      break;
    case "sexo":
      valor = expedienteInformeDgp.getSexo();
      break;
    case "progenitores":
      valor = setProgenitoresDgp(expedienteInformeDgp);
      break;
    case "lugarNacimiento":
      valor = expedienteInformeDgp.getLugarNacimiento();
      break;
    case "nacionalidad":
      valor = setNacionalidadDgp(expedienteInformeDgp);
      break;
    case "estadoCivil":
      valor = setEstadoCivilDgp(expedienteInformeDgp);
      break;
    case "nombreCompletoConyuge":
      valor = setNombreConyugeCompleto(expedienteInformeDgp);
      break;
    case "nacConyuge":
      valor = setNacionalidadConyugeDgp(expedienteInformeDgp);
      break;
    case "lugarResidencia":
      valor = setLugarResidencia(expedienteInformeDgp);
      break;
    case "domicilio":
      valor = setValorDomicilioDgp(expedienteInformeDgp);
      break;
    case "codPostal":
      valor = expedienteInformeDgp.getCodPostal();
      break;
    case "telefonoFijo":
      valor = expedienteInformeDgp.getTelefonoFijo();
      break;
    case "telefonoMovil":
      valor = expedienteInformeDgp.getTelefonoMovil();
      break;
    case "fechaSalida":
      if (expedienteInformeDgp.getFechaSalida() != null) {
        valor = formatter.format(expedienteInformeDgp.getFechaSalida());
      }
      break;
    case "legalIrregular":
      valor = expedienteInformeDgp.getLegalIrregular();
      break;
    case "tipoPermiso":
      valor = expedienteInformeDgp.getTipoPermiso();
      break;
    case "entrevista":
      valor = expedienteInformeDgp.getEntrevista();
      break;
    case "motivoNo":
      valor = expedienteInformeDgp.getMotivoNo();
      break;
    case "nombreCompletoConEsp":
      valor = setNombreCompletoConEsp(expedienteInformeDgp);
      break;
    case "nombreCompletoConDis":
      valor = setNombreCompletoConDis(expedienteInformeDgp);
      break;
    case "codExp":
      valor = expedienteDto.getCodExp();
      break;
    case "fechaSolicitud":
      if (expedienteInformeDgp.getExpedienteInformeDto() != null
          && expedienteInformeDgp.getExpedienteInformeDto().getFechaSolicitud() != null) {
        Date fechaSolicitud = expedienteInformeDgp.getExpedienteInformeDto().getFechaSolicitud();
        valor = new SimpleDateFormat(DD_MM_YYYY).format(fechaSolicitud);
      }
      break;
    case "fechaEntrada":
      valor = new SimpleDateFormat(DD_MM_YYYY).format(new Date());
      break;
    case "antecedentes":
      valor = expedienteInformeDgp.getAntecedentes();
      break;
    case "icgi":
      valor = expedienteInformeDgp.getIcgi();
      break;
    default:
      valor = "";
      break;
    }
    if (valor == null) {
      valor = "";
    }
    return valor;

  }

  @Override
  public ExpedienteDocumentoDto generarIndiceElectronico(BigInteger idExp) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.generarIndiceElectronico del expediente {}", idExp);
    try {
      ExpedienteDto expedienteDto = expedientesService.getExpedienteInteresadoByIdExpediente(idExp);

      String codPlantilla = "INDI" + expedienteDto.getProcedimientoDto().getCodCorto();
      PlantillaEntity plantilla = plantillaDao.getPlantillaByCod(codPlantilla);
      if (plantilla == null) {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_126).logMessageParams(idExp, codPlantilla);
      }
      PlantillaDto plantillaDto = plantillaMapper.toDto(plantilla);

      ExpedienteDocumentoDto expDocDtoEmp = new ExpedienteDocumentoDto();
      Map<String, String> valoresPlantillas = null;
      ExpedienteDocumentoDto expDocDto = createExpedienteDocumentoPlantillaContent(plantillaDto, expedienteDto,
          expDocDtoEmp, valoresPlantillas);
      LOG.info("Firmando documento {}...", expDocDto.getNomDoc());
      DataHandler docFirmado = signDocumentByByte(expDocDto.getContenido(), expDocDto.getNomDoc());
      // Verificamos si la firma del documento falló.
      if (docFirmado == null) {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_23);
      }
      // Convertimos el documento firmado a bytes y lo asignamos al DTO.
      byte[] docContent = obtenerBytesDeDataHandler(docFirmado);
      expDocDto.setContenido(docContent);
      LOG.debug("End - DocumentosServiceImpl.generarIndiceElectronico del expediente {}", idExp);
      return expDocDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_127).logMessageParams(idExp);
    }
  }

  // Método que convierte el DataHandler en un byte[]
  public byte[] obtenerBytesDeDataHandler(DataHandler dataHandler) throws IOException {
    // Obtén el DataSource del DataHandler
    DataSource dataSource = dataHandler.getDataSource();
    // Crea un InputStream desde el DataSource
    InputStream inputStream = dataSource.getInputStream();
    // Usamos un ByteArrayOutputStream para almacenar los bytes leídos
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // Leer los datos del InputStream y escribirlos en el ByteArrayOutputStream
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) != -1) {
      byteArrayOutputStream.write(buffer, 0, length);
    }
    // Devuelve el contenido como byte[]
    return byteArrayOutputStream.toByteArray();
  }

  private ExpedienteInformeIndiceExpDto getIndiceElecData(ExpedienteDto expediente) {
    LOG.debug("Init - DocumentosServiceImpl.getIndiceElecData del expediente {} con codExp {}", expediente.getIdExp(),
        expediente.getCodExp());
    Date today = new Date();
    ExpedienteInformeIndiceExpDto indiceExpDto = new ExpedienteInformeIndiceExpDto();
    indiceExpDto.setProcedimiento(expediente.getProcedimientoDto().getNomPro());
    indiceExpDto.setIdenENI(expediente.getIdExpGd());
    indiceExpDto.setEmitidoPor("Ministerio de la Presidencia, Justicia, y relaciones con las cortes.");
    indiceExpDto.setFecEmision(today);
    indiceExpDto.setFecha(today);
    indiceExpDto.setIdenENI(expediente.getIdExpGd());
    indiceExpDto.setCodigo(expediente.getCodExp());
    indiceExpDto.setCodRes(expediente.getIdExpGd());
    indiceExpDto.setNumExp(expediente.getIdExpGd());
    LOG.info("Índice del expediente {} con codExp {}: {}", expediente.getIdExp(), expediente.getCodExp(), indiceExpDto);
    LOG.debug("End - DocumentosServiceImpl.getIndiceElecData del expediente {} con codExp {}", expediente.getIdExp(),
        expediente.getCodExp());
    return indiceExpDto;
  }

  private String obtenerValoresIndiceExp(String nombreCampo, ExpedienteInformeIndiceExpDto expedienteInformeIndiceExp) {
    String valor = "";
    SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    switch (nombreCampo) {
    case "procedimiento":
      valor = expedienteInformeIndiceExp.getProcedimiento();
      break;
    case "codGD":
      valor = expedienteInformeIndiceExp.getNumExp();
      break;
    case "fecEmision.indiceENI":
      if (expedienteInformeIndiceExp.getFecEmision() != null) {
        valor = formatterDate.format(expedienteInformeIndiceExp.getFecEmision());
      }
      break;
    case "fechaDoc.indiceENI":
      if (expedienteInformeIndiceExp.getFecha() != null) {
        valor = formatter.format(expedienteInformeIndiceExp.getFecha());
      }
      break;
    case "codExp":
      valor = expedienteInformeIndiceExp.getCodigo();
      break;
    case "emitidoPor.indiceENI":
      valor = nombreOrganizacion;
      break;
    case "eniDoc.indiceENI":
      valor = expedienteInformeIndiceExp.getIdenENI();
      break;
    case "tipDoc.indiceENI":
      valor = expedienteInformeIndiceExp.getTipDoc();
      break;
    case "origenDoc.indiceENI":
      valor = expedienteInformeIndiceExp.getOrigen();
      break;
    default:
      valor = "";
      break;
    }
    if (valor == null) {
      valor = "";
    }
    return valor;

  }

  private String setNacionalidadConyugeDgp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    String valor = "";
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNacConyuge())) {
      valor = paisesService.getPaisPorCodigoDgp(expedienteInformeDgp.getNacConyuge()).getNacionalidad();
      if (valor == null) {
        valor = expedienteInformeDgp.getNacConyuge();
      }
    }
    return valor;
  }

  private String setEstadoCivilDgp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    String valor = "";
    if (!StringUtils.isEmpty(expedienteInformeDgp.getEstadoCivil())) {
      LdvMaestraDto ldvMaestraDto = catalogosService.getCatalogoByCod("ESTC-" + expedienteInformeDgp.getEstadoCivil());
      if (ldvMaestraDto != null) {
        valor = ldvMaestraDto.getDesLdvMae();
      } else {
        valor = expedienteInformeDgp.getEstadoCivil();
      }
    }
    return valor;
  }

  private String setNacionalidadDgp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    String valor = "";
    if (!StringUtils.isEmpty(expedienteInformeDgp.getCodNacionalidad())) {
      valor = paisesService.getPaisPorCodigoDgp(expedienteInformeDgp.getCodNacionalidad()).getNacionalidad();
      if (valor == null) {
        valor = expedienteInformeDgp.getCodNacionalidad();
      }
    }
    return valor;
  }

  private String obtenerApellidos(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApellido1())) {
      stringBuilder.append(expedienteInformeDgp.getApellido1());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApellido2())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApellido2());
    }
    return stringBuilder.toString();
  }

  private String setNombreCompletoConDis(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNombreDistinta())) {
      stringBuilder.append(expedienteInformeDgp.getNombreDistinta());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe1Distinta())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe1Distinta());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe2Distinta())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe2Distinta());
    }
    return stringBuilder.toString();
  }

  private String setNombreCompletoConEsp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNomConEsp())) {
      stringBuilder.append(expedienteInformeDgp.getNomConEsp());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe1ConEsp())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe1ConEsp());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe2ConEsp())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe2ConEsp());
    }
    return stringBuilder.toString();
  }

  private String setLugarResidencia(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getPoblacionNNor())) {
      stringBuilder.append(expedienteInformeDgp.getPoblacionNNor());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getDireccionNNor())) {
      stringBuilder.append(" " + expedienteInformeDgp.getDireccionNNor());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getCodigoNNor())) {
      stringBuilder.append(" " + expedienteInformeDgp.getCodigoNNor());
    }
    return stringBuilder.toString();
  }

  private String setProgenitoresDgp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    String valor;
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNombrePadre())
        && !StringUtils.isEmpty(expedienteInformeDgp.getNombreMadre())) {
      valor = expedienteInformeDgp.getNombrePadre() + "," + expedienteInformeDgp.getNombreMadre();
    } else if (!StringUtils.isEmpty(expedienteInformeDgp.getNombrePadre())) {
      valor = expedienteInformeDgp.getNombrePadre();
    } else {
      valor = expedienteInformeDgp.getNombreMadre();
    }
    return valor;
  }

  private String setNombreConyugeCompleto(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNombreConyuge())) {
      stringBuilder.append(expedienteInformeDgp.getNombreConyuge());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe1Conyuge())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe1Conyuge());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getApe2Conyuge())) {
      stringBuilder.append(" " + expedienteInformeDgp.getApe2Conyuge());
    }
    return stringBuilder.toString();
  }

  private String setValorDomicilioDgp(ExpedienteInformeDgpDto expedienteInformeDgp) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    if (!StringUtils.isEmpty(expedienteInformeDgp.getLocalidad())) {
      stringBuilder.append("Localidad: " + expedienteInformeDgp.getLocalidad());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getTipoVia())) {
      TiposViaEntity tiposViaEntity = tiposViaDao.getTipoViaByCodDgp(expedienteInformeDgp.getTipoVia());
      if (tiposViaEntity == null) {
        stringBuilder.append(" Tipo Vía: " + expedienteInformeDgp.getTipoVia());
      } else {
        stringBuilder.append(" Tipo Vía: " + tiposViaEntity.getNomTipoVia());
      }
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNombreVia())) {
      stringBuilder.append(" Nombre vía: " + expedienteInformeDgp.getNombreVia());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getNumero())) {
      stringBuilder.append(" Número: " + expedienteInformeDgp.getNumero());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getPortal())) {
      stringBuilder.append(" Portal: " + expedienteInformeDgp.getPortal());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getEscalera())) {
      stringBuilder.append(" Escalera: " + expedienteInformeDgp.getEscalera());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getBloque())) {
      stringBuilder.append(" Bloque: " + expedienteInformeDgp.getBloque());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getPlanta())) {
      stringBuilder.append(" Planta: " + expedienteInformeDgp.getPlanta());
    }
    if (!StringUtils.isEmpty(expedienteInformeDgp.getPuerta())) {
      stringBuilder.append(" Puerta: " + expedienteInformeDgp.getPuerta());
    }
    return stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  private byte[] modificarPlantillaConDatos(PlantillaDto plantillaDto, Properties varValues,
      ExpedienteDto expedienteDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.modificarPlantillaConDatos de la plantilla {} del exp {} con codExp {}",
        plantillaDto.getCodPlantilla(), expedienteDto.getIdExp(), expedienteDto.getCodExp());
    try {
      File inFile = new File(nfsManager.getNfsEnvironmentPath() + plantillaDto.getNfsRuta() + STRING_SEPARATOR
          + plantillaDto.getNomPlantilla() + ".odt");
      ZipFile inZip = new ZipFile(inFile);
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      ZipOutputStream outZip = new ZipOutputStream(outStream);
      Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) inZip.entries();
      String codPlantillaIndice = "INDI" + expedienteDto.getProcedimientoDto().getCodCorto();

      try {
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();

          InputStream in = new BufferedInputStream(inZip.getInputStream(entry));

          // En el caso del content.xml tendremos que procesarlo y
          // reemplazar variables
          if (entry.getName().equals("content.xml")) {
            if (plantillaDto.getCodPlantilla().equals(codPlantillaIndice)) {
              in = getNewContentInputStreamIndice(in, varValues);
            } else {
              in = getNewContentInputStream(in, varValues);
            }
            entry = new ZipEntry("content.xml");
          } else if (entry.getName().equals("styles.xml")) {
            if (plantillaDto.getCodPlantilla().equals(codPlantillaIndice)) {
              in = getNewStylesInputStream(in, varValues); // Procesar styles.xml
              entry = new ZipEntry("styles.xml");
            }
          }

          outZip.putNextEntry(entry);
          final int bufferSize = 1000;
          byte[] buffer = new byte[bufferSize];
          int readCount = 0;

          while ((readCount = in.read(buffer)) != -1) {
            if (readCount < bufferSize) {
              outZip.write(buffer, 0, readCount);
            } else {
              outZip.write(buffer);
            }
          }
          in.close();
        }
      } catch (Exception e) {
        throw new SinacException(e, SinacExceptionMessageType.MESSAGE_128)
            .logMessageParams(plantillaDto.getCodPlantilla(), expedienteDto.getIdExp());
      } finally {
        inZip.close();
        outZip.close();
      }
      LOG.debug("End - DocumentosServiceImpl.modificarPlantillaConDatos de la plantilla {} del exp {} con codExp {}",
          plantillaDto.getCodPlantilla(), expedienteDto.getIdExp(), expedienteDto.getCodExp());
      return outStream.toByteArray();
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_129).logMessageParams(plantillaDto.getIdPla(),
          expedienteDto.getIdExp());
    }
  }

  /**
   * Metodo encargado de asignar nombre al documento
   * 
   * @param expedienteDto          Expediente
   * @param expedienteDocumentoDto Documento del expediente
   */
  private String asignarNombreDocumento(ExpedienteDto expedienteDto, ExpedienteDocumentoDto expedienteDocumentoDto,
      int numOrden) {
    LOG.debug(
        "Init - DocumentosServiceImpl.asignarNombreDocumento del expedienteDocumento {} con numOrden={} para el expediente {}",
        expedienteDocumentoDto.getIdExpDoc(), numOrden, expedienteDto.getIdExp());
    String nombreDocumento = "";
    boolean plantillaCM = isPlantillaCM(expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo());
    if (!plantillaCM) {
      nombreDocumento = numOrden + "_" + expedienteDto.getCodExp().replace("/", "") + "_"
          + expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo() + ".odt";
    } else {
      String nombreCompleto = expedienteDto.getInteresado().getNombre() + " "
          + expedienteDto.getInteresado().getApellido1();

      if (expedienteDto.getInteresado().getApellido2() != null
          && !expedienteDto.getInteresado().getApellido2().isEmpty()) {
        nombreCompleto += " " + expedienteDto.getInteresado().getApellido2();
      }

      nombreDocumento = numOrden + "_" + expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo() + "_"
          + nombreCompleto + ".odt";

      if (nombreDocumento.length() > 80) {
        int exceso = nombreDocumento.length() - 80;
        nombreCompleto = nombreCompleto.substring(0, nombreCompleto.length() - exceso).trim();
        nombreDocumento = numOrden + "_" + expedienteDocumentoDto.getDocumentoTipoDto().getNomTipo() + "_"
            + nombreCompleto + ".odt";
      }
    }
    LOG.info("El nombre establecido para el expDocumento {} del exp {} es '{}'", expedienteDocumentoDto.getIdExpDoc(),
        expedienteDto.getIdExp(), nombreDocumento);
    LOG.debug(
        "End - DocumentosServiceImpl.asignarNombreDocumento del expedienteDocumento {} con numOrden={} para el expediente {}",
        expedienteDocumentoDto.getIdExpDoc(), numOrden, expedienteDto.getIdExp());
    return nombreDocumento;
  }

  private Boolean isPlantillaCM(String codTipo) {
    return switch (codTipo) {
    case "NINFO", "RDFCB", "EXMOT", "RDFHB", "MEMEX", "OFSCM" -> true;
    default -> false;
    };
  }

  /**
   * Metodo encargado de asignar nombre al documento Indice
   * 
   * @param expedienteDto          Expediente
   * @param expedienteDocumentoDto Documento del expediente
   */
  private String asignarNombreDocumentoIndice(ExpedienteDto expedienteDto,
      ExpedienteDocumentoDto expedienteDocumentoDto, int numOrden) {
    LOG.debug(
        "Init - DocumentosServiceImpl.asignarNombreDocumentoIndice del expedienteDocumento {} con numOrden={} para el expediente {}",
        expedienteDocumentoDto.getIdExpDoc(), numOrden, expedienteDto.getIdExp());
    String nombreDocumento = "IndiceExpediente";
    String codigoExpediente = expedienteDto.getCodExp();
    // Expresión regular para extraer los datos
    Matcher matcher = COD_EXP_PATTERN.matcher(codigoExpediente);
    if (matcher.matches()) {
      String codPro = matcher.group(1); // Las letras iniciales
      String secuencial = matcher.group(2); // Los números antes del '/'
      String anio = matcher.group(3); // Los números después del '/'
      // Imprime los resultados
      nombreDocumento += "_" + codPro + "_" + anio + "_" + secuencial;
    }
    LOG.info("El nombre establecido para el expDocumento {} del exp {} es '{}.odt'",
        expedienteDocumentoDto.getIdExpDoc(), expedienteDto.getIdExp(), nombreDocumento);
    LOG.debug(
        "End - DocumentosServiceImpl.asignarNombreDocumento del expedienteDocumento {} con numOrden={} para el expediente {}",
        expedienteDocumentoDto.getIdExpDoc(), numOrden, expedienteDto.getIdExp());
    return nombreDocumento + ".odt";
  }

  /**
   * Método encargado de manejar el Histórico de los Documentos.
   *
   * @param expedienteDto          Expediente.
   * @param expedienteDocumentoDto Documento del Expediente.
   */
  private void manejoHistoricoDocumentos(ExpedienteDto expedienteDto, ExpedienteDocumentoDto expedienteDocumentoDto) {
    LOG.debug("Init - DocumentosServiceImpl.manejoHistoricoDocumentos del expediente {} y del expedienteDocumento {}",
        expedienteDto.getIdExp(), expedienteDocumentoDto.getIdExpDoc());
    if (!expedienteDto.getExpedienteDocumentoDtos().isEmpty()) {
      List<String> listaTiposDocumento = new ArrayList<>(Arrays.asList("OFREQ", "RQDMF", "RQIFM", "RQDFI", "RQDSU",
          "OIICD", "DOCAM", "AUDIE", "RQA", "RQMEJ", "RQDA", "OSOIN"));
      for (int i = 0; i < expedienteDto.getExpedienteDocumentoDtos().size(); i++) {
        if (expedienteDocumentoDto.getDocumentoTipoDto() != null
            && expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo() != null
            && expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo()
                .equals(expedienteDto.getExpedienteDocumentoDtos().get(i).getDocumentoTipoDto().getCodTipo())) {
          if (!listaTiposDocumento
              .contains(expedienteDto.getExpedienteDocumentoDtos().get(i).getDocumentoTipoDto().getCodTipo())) {
            expedienteDto.getExpedienteDocumentoDtos().get(i).setFlgActivo(false);
          }
          saveExpedienteDocumentoHistorico(expedienteDto.getExpedienteDocumentoDtos().get(i), expedienteDto);
        }
      }
      LOG.debug("End - DocumentosServiceImpl.manejoHistoricoDocumentos del expediente {} y del expedienteDocumento {}",
          expedienteDto.getIdExp(), expedienteDocumentoDto.getIdExpDoc());
    }
  }

  private void cargarDatosExpedienteDocumentoPlantilla(PlantillaDto plantillaDto, ExpedienteDto expedienteDto,
      ExpedienteDocumentoDto expedienteDocumentoDto, String nfsPathDocument, String nombreDocumento)
      throws SinacException {
    expedienteDocumentoDto.setNfsRuta(nfsPathDocument);
    expedienteDocumentoDto.setExpedienteDto(expedienteDto);
    expedienteDocumentoDto.setLdvMaestraDtoByIdOriDocLdv(catalogosService.getCatalogoByCod("DOC-ADM"));
    expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(plantillaDto.getProDocTipo().getIdEstDocLdv());
    // TODO Modificar organo
    expedienteDocumentoDto.setLdvMaestraDtoByIdOrgLdv(catalogosService.getCatalogoByCod("ORG-JUS"));
    expedienteDocumentoDto.setLdvMaestraDtoByIdEstElaLdv(catalogosService.getCatalogoByCod("EE01"));
    expedienteDocumentoDto.setNomDoc(nombreDocumento);
  }

  @Override
  public List<PlantillasPlantillasCamposDto> getPlantillasPlantillasCamposByIdPlantilla(short idPlantilla) {
    LOG.debug("Init - DocumentosServiceImpl.getPlantillasPlantillasCamposByIdPlantilla de la plantilla {}",
        idPlantilla);
    List<PlantillasPlantillasCamposEntity> plantillasPlantillasCamposEntityLista = plantillasPlantillasCamposDao
        .getPlantillasPlantillasCamposByIdPlantilla(idPlantilla);
    List<PlantillasPlantillasCamposDto> plantillasPlantillasCamposDtoLista = new ArrayList<>();
    for (PlantillasPlantillasCamposEntity entity : plantillasPlantillasCamposEntityLista) {
      plantillasPlantillasCamposDtoLista.add(plantillasPlantillasCamposMapper.toDto(entity));
    }
    LOG.debug("End - DocumentosServiceImpl.getPlantillasPlantillasCamposByIdPlantilla de la plantilla {}", idPlantilla);
    return plantillasPlantillasCamposDtoLista;
  }

  /**
   * Sustituir variables
   *
   * @param in the in
   */
  private static InputStream getNewContentInputStream(InputStream in, Properties varsValues) throws IOException {
    LOG.debug("Init - DocumentosServiceImpl.getNewContentInputStream");
    StringBuilder res = new StringBuilder();
    StringTokenizer st = new StringTokenizer(toString(in), "$$");

    // T0do lo que haya antes del primer $$ o directamente el
    // documento entero si es que no tiene variables
    res.append(st.nextToken());

    while (st.hasMoreTokens()) {

      // Identificador de la variable a reemplazar
      String variable = st.nextToken();

      if (variable.contains("<")) {

        int init = variable.indexOf("<");
        int fin = variable.indexOf(">");
        String aux = variable.substring(init, fin + 1);

        variable = variable.replace(aux, "");

        while (variable.contains("<")) {
          init = variable.indexOf("<");
          fin = variable.indexOf(">");
          aux = variable.substring(init, fin + 1);
          if (aux == null || (aux != null && aux.isBlank())) {
            throw new SinacException(SinacExceptionMessageType.MESSAGE_24);
          }
          variable = variable.replace(aux, "");
        }
      }

      // Obtengo el valor a asignar a la variable
      String valor = varsValues.getProperty(variable);

      if (valor == null) {
        valor = varsValues.getProperty(variable.toUpperCase());
        if (valor == null) {
          valor = "";
        }
      }
      LOG.info("Sustución de valores en plantillas: Se sustituye el valor para la variable {}, su valor es {}",
          variable, valor);

      if (valor != null) {
        res.append(new String(valor.getBytes(StandardCharsets.UTF_8)));
      }

      // Añadimos lo que haya hasta la siguiente variable, o ya hasta el
      // final.
      if (st.hasMoreTokens()) {
        res.append(st.nextToken());
      }
    }

    // Se quita para la dgp en la tabla de trámites
    String contenidoFinalString = res.toString().replace(
        "</text:span></text:span></text:p></table:table-cell><table:table-cell table:style-name=\"Tabla5.A2\" office:value-type=\"string\"><text:p text:style-name=\"P7\"><text:span text:style-name=\"Fuente_20_de_20_párrafo_20_predeter.\"><text:span text:style-name=\"T6\"/></text:span></text:p></table:table-cell><table:table-cell table:style-name=\"Tabla5.A2\" office:value-type=\"string\"><text:p text:style-name=\"P8\"><text:span text:style-name=\"Fuente_20_de_20_párrafo_20_predeter.\"><text:span text:style-name=\"T6\"/></text:span></text:p></table:table-cell><table:table-cell table:style-name=\"Tabla5.A2\" office:value-type=\"string\"><text:p text:style-name=\"P8\"><text:span text:style-name=\"Fuente_20_de_20_párrafo_20_predeter.\"><text:span text:style-name=\"T6\"/></text:span></text:p></table:table-cell><table:table-cell table:style-name=\"Tabla5.E2\" office:value-type=\"string\"><text:p text:style-name=\"Normal\"/></table:table-cell></table:table-row>",
        "");
    // Se quita en plantilla de CN
    contenidoFinalString = contenidoFinalString.replace(
        "</text:list><text:list text:continue-numbering=\"true\" text:style-name=\"WWNum23\"><text:list-item><text:p "
            + "text:style-name=\"P13\" loext:marker-style-name=\"T10\"></text:p></text:list-item>",
        "");
    // se quita en plantilla con documentos de dolicitud
    contenidoFinalString = contenidoFinalString.replace(
        "</text:p></text:list-item></text:list><text:list text:continue-numbering=\"true\" "
            + "text:style-name=\"WWNum23\"><text:list-item><text:p text:style-name=\"P13\" loext:marker-style-name=\"T10\">",
        "");
    LOG.debug("End - DocumentosServiceImpl.getNewContentInputStream");
    return new ByteArrayInputStream(contenidoFinalString.getBytes());
  }

  private static InputStream getNewStylesInputStream(InputStream in, Properties varsValues) throws IOException {
    String contenido = toString(in);
    // Buscar la variable específica en el contenido
    String variable = "$$fecEmision.indiceENI$$";
    String valor = varsValues.getProperty("fecEmision.indiceENI", ""); // Obtener el valor del Properties
    if (contenido.contains(variable)) {
      contenido = contenido.replace(variable, valor); // Reemplazar la variable con el valor
    }
    return new ByteArrayInputStream(contenido.getBytes(StandardCharsets.UTF_8));
  }

  private static InputStream getNewContentInputStreamIndice(InputStream in, Properties varsValues) throws IOException {
    LOG.debug("Init - DocumentosServiceImpl.getNewContentInputStreamIndice");
    StringBuilder res = new StringBuilder();
    String contenido = toString(in);
    // --- Procesar documentos en Tabla5.2 ---
    String inicioFilaDocumentos = "<table:table-row table:style-name=\"Tabla5.2\">";
    String finFilaDocumentos = "</table:table-row>";
    int inicioDocumentos = contenido.indexOf(inicioFilaDocumentos);
    int finDocumentos = contenido.indexOf(finFilaDocumentos, inicioDocumentos) + finFilaDocumentos.length();
    if (inicioDocumentos != -1 && finDocumentos != -1) {
      // Si hay filas para procesar en Tabla5.2
      String filaBaseDocumentos = contenido.substring(inicioDocumentos, finDocumentos);
      StringBuilder filasGeneradasDocumentos = new StringBuilder();
      // Obtener la lista de documentos desde varsValues
      List<ExpedienteDocumentoDto> documentos = (List<ExpedienteDocumentoDto>) varsValues.get("documentos.indiceENI");
      SimpleDateFormat formatter = new SimpleDateFormat(DD_MM_YYYY);
      if (documentos != null) {
        for (ExpedienteDocumentoDto doc : documentos) {
          String fila = filaBaseDocumentos;
          // Reemplazar variables específicas de documentos con manejo de null
          fila = fila.replace("$$idenENI.indiceENI$$", doc.getCodGd() != null ? doc.getCodGd() : "");
          fila = fila.replace("$$tipDoc.indiceENI$$",
              doc.getDocumentoTipoDto() != null && doc.getDocumentoTipoDto().getNomTipo() != null
                  ? doc.getDocumentoTipoDto().getNomTipo()
                  : "");
          fila = fila.replace("$$origen.indiceENI$$",
              doc.getLdvMaestraDtoByIdOriDocLdv() != null && doc.getLdvMaestraDtoByIdOriDocLdv().getNomLdvMae() != null
                  ? doc.getLdvMaestraDtoByIdOriDocLdv().getNomLdvMae()
                  : "");
          fila = fila.replace("$$fecha.indiceENI$$",
              doc.getFechaCreacion() != null ? formatter.format(doc.getFechaCreacion()) : "");
          filasGeneradasDocumentos.append(fila);
        }
      }
      // Reemplazar las filas originales de documentos con las filas generadas
      contenido = contenido.substring(0, inicioDocumentos) + filasGeneradasDocumentos.toString()
          + contenido.substring(finDocumentos);
    }
    // --- Procesar personas en Tabla3.2 ---
    String inicioFilaPersonas = "<table:table-row table:style-name=\"Tabla3.2\">";
    String finFilaPersonas = "</table:table-row>";
    int inicioPersonas = contenido.indexOf(inicioFilaPersonas);
    // Buscar la tabla específica que contiene $$razon.indiceENI$$
    while (inicioPersonas != -1) {
      int finPersonas = contenido.indexOf(finFilaPersonas, inicioPersonas) + finFilaPersonas.length();
      if (finPersonas != -1) {
        // Extraer la fila para verificar su contenido
        String filaBasePersonas = contenido.substring(inicioPersonas, finPersonas);
        if (filaBasePersonas.contains("$$razon.indiceENI$$")) {
          // Si encontramos la fila correcta, procesamos las personas
          StringBuilder filasGeneradasPersonas = new StringBuilder();
          // Obtener la lista de personas desde varsValues
          List<ExpedientePersonaIndiceDto> personas = (List<ExpedientePersonaIndiceDto>) varsValues
              .get("personas.indiceENI");
          if (personas != null) {
            for (ExpedientePersonaIndiceDto persona : personas) {
              String fila = filaBasePersonas;
              // Reemplazar variables específicas de personas
              fila = fila.replace("$$razon.indiceENI$$",
                  persona.getTipoPersona() != null ? persona.getTipoPersona() : "");
              fila = fila.replace("$$nombre.indiceENI$$", persona.getNombre() != null ? persona.getNombre() : "");
              fila = fila.replace("$$apellido1.indiceENI$$",
                  persona.getApellido1() != null ? persona.getApellido1() : "");
              fila = fila.replace("$$apellido2.indiceENI$$",
                  persona.getApellido2() != null ? persona.getApellido2() : "");
              fila = fila.replace("$$tipIdent.indiceENI$$",
                  persona.getTipoAcreditacion() != null ? persona.getTipoAcreditacion() : "");
              fila = fila.replace("$$numIdent.indiceENI$$",
                  persona.getNumAcreditacion() != null ? persona.getNumAcreditacion() : "");
              filasGeneradasPersonas.append(fila);
            }
          }
          // Reemplazar las filas originales de personas con las filas generadas
          contenido = contenido.substring(0, inicioPersonas) + filasGeneradasPersonas.toString()
              + contenido.substring(finPersonas);
          break; // Salir del bucle al encontrar y procesar la tabla correcta
        }
      }
      // Buscar la siguiente tabla si esta no contiene la variable $$razon.indiceENI$$
      inicioPersonas = contenido.indexOf(inicioFilaPersonas, finPersonas);
    }
    // --- Procesar el resto del contenido con reemplazo de variables ---
    StringTokenizer st = new StringTokenizer(contenido, "$$");
    res.append(st.nextToken()); // Texto antes de la primera variable
    while (st.hasMoreTokens()) {
      // Identificador de la variable a reemplazar
      String variable = st.nextToken();
      if (variable.contains("<")) {
        int init = variable.indexOf("<");
        int finVar = variable.indexOf(">");
        String aux = variable.substring(init, finVar + 1);
        variable = variable.replace(aux, "");
        while (variable.contains("<")) {
          init = variable.indexOf("<");
          finVar = variable.indexOf(">");
          aux = variable.substring(init, finVar + 1);
          if (aux == null || (aux != null && aux.isBlank())) {
            throw new SinacException(SinacExceptionMessageType.MESSAGE_24);
          }
          variable = variable.replace(aux, "");
        }
      }
      // Obtener el valor a asignar a la variable
      String valor = varsValues.getProperty(variable);
      if (valor == null) {
        valor = varsValues.getProperty(variable.toUpperCase());
        if (valor == null) {
          valor = "";
        }
      }
      LOG.info("Sustitución de valores en plantillas: Se sustituye el valor para la variable {}, su valor es {}",
          variable, valor);
      res.append(valor);
      // Añadir el texto hasta la siguiente variable o hasta el final
      if (st.hasMoreTokens()) {
        res.append(st.nextToken());
      }
    }
    LOG.debug("End - DocumentosServiceImpl.getNewContentInputStreamIndice");
    return new ByteArrayInputStream(res.toString().getBytes(StandardCharsets.UTF_8));
  }

  private String obtenerValorCampo(ExpedienteDto expediente, String campo)
      throws NoSuchFieldException, IllegalAccessException, SinacException {
    LOG.debug("Init - DocumentosServiceImpl.obtenerValorCampo del campo {} para el expediente {} con codExp {}", campo,
        expediente.getIdExpGd(), expediente.getCodExp());
    Object valor = expediente;
    StringBuilder valorConcatenado = new StringBuilder();
    if (campo.equals("interesado.nombreCompleto")) {
      // TODO Revisar segundo apellido
      valorConcatenado.append(getFieldObject(expediente.getInteresado(), "nombre").toString() + " "
          + getFieldObject(expediente.getInteresado(), "apellido1").toString() + " "
          + (getFieldObject(expediente.getInteresado(), "apellido2") == null ? ""
              : getFieldObject(expediente.getInteresado(), "apellido2").toString()));
    } else if (campo.equals("interesado.datosDomicilio")) {
      // TODO arreglar posibles errores en esta línea
      PersonaDomicilioDto pd = personaService.getPersonaDomicilioById(
          expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());
      valorConcatenado.append(getDatosDomicilioConcatenados(pd));
    } else if (campo.contains("fechaInforme")) {
      Pageable pageable = PageRequest.of(0, 1);
      valor = getValorFechaInforme(expediente, campo, pageable);
    } else if (campo.equals("fechaPrimerInforme")) {
      valor = getValorFechaPrimerInforme(expediente);
    } else if (campo.equals("fechaRegistroSolicitud")) {
      if (expediente.getFechaEfectos() != null) {
        Date fecha = expediente.getFechaEfectos();
        valor = parseToDateIfDate(fecha);
      } else {
        valor = "";
      } // getFechaRegistroSolicitud(expediente);
    } else if (campo.equals("documentosSolicitud")) {
      valor = getDocumentosSolicitudListado(expediente, valorConcatenado);
    } else if (campo.equals("circExcep")) {
      valor = "";
      if (expediente.getMotivoSolicitud() != null && expediente.getMotivoSolicitud().getNomLdvMae() != null) {
        valor = expediente.getMotivoSolicitud().getNomLdvMae();
      }
    } else if (campo.equals("vecindadCivil")) {
      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValDao
          .getExpFormByIdExpCodCampo(expediente.getIdExp(), "VCIVI");

      if (expedienteFormularioValEntity != null && expedienteFormularioValEntity.getValor() != null) {
        // TODO: CAMBIAR ESTO POR UN SI ES NUMERICO
        if (expedienteFormularioValEntity.getValor().matches(".*\\d.*")) {
          int valorExpedienteFormVal = Integer.parseInt(expedienteFormularioValEntity.getValor());
          valor = catalogosService.getCatalogoById(valorExpedienteFormVal).getNomLdvMae();
        } else {
          valor = expedienteFormularioValEntity.getValor();
        }
      } else {
        valor = "";
      }

    } else if (campo.equals("SREF")) {
      // TODO
      valor = "";
    } else if (campo.equals("numExpedienteRecurrido")) {
      // TODO
      valor = "";
    } else if (campo.equals("fechaResolucion")) {
      valor = parseToDateIfDate(expediente.getFechaResolucion());
    } else if (campo.equals("procedimiento")) {
      valor = expediente.getProcedimientoDto().getNomPro();
      // TODO Rellenar todo los datos recursos
    } else if (campo.equals("nombreSolReqNot")) {
      valor = "";
    } else if (campo.equals("direccionSolReqNot")) {
      valor = "";
    } else if (campo.equals("artReqInteresado")) {
      valor = "";
    } else if (campo.equals("relacionReqInt")) {
      valor = "";
    } else if (campo.equals("denomActo")) {
      valor = "";
    } else if (campo.equals("parrafoActoRecurrido")) {
      valor = "";
    } else if (campo.equals("nombreCompletoNot")) {
      if (expediente.getExpedientesPersonasDtos() != null && !expediente.getExpedientesPersonasDtos().isEmpty()) {
        List<ExpedientesPersonasDto> expPer = expediente.getExpedientesPersonasDtos().stream()
            .filter(c -> Boolean.TRUE.equals(c.getFlgNotificar())).toList();
        if (expPer != null && !expPer.isEmpty()) {
          valor = expPer.get(0).getPersonaDto().getNombre() + " " + expPer.get(0).getPersonaDto().getApellido1() + " "
              + expPer.get(0).getPersonaDto().getApellido2();
        } else {
          valor = "";
        }
      } else {
        valor = "";
      }
    } else if (campo.equals("parrafoSujetos")) {
      valor = "";
    } else if (campo.equals("fechaSolicitudOrigen")) {
      valor = "";
    } else if (campo.equals("fechaSolicitudOrigenRes")) {
      valor = "";
    } else if (campo.equals("organoGestorRecurrido")) {
      valor = "";
    } else if (campo.equals("tipoActoRecurrido")) {
      valor = "";
    } else if (campo.equals("hechosProbadosRsuspA")) {
      valor = "";
    } else if (campo.equals("fechaInterposicion")) {
      valor = "";
    } else if (campo.equals("artObjetoPlazosG")) {
      valor = "";
    } else if (campo.equals("alegacionesRsuspA")) {
      valor = "";
    } else if (campo.equals("hechosProbadosRsusp")) {
      valor = "";
    } else if (campo.equals("alegacionesRsusp")) {
      valor = "";
    } else if (campo.equals("advertencia")) {
      valor = "";
    } else if (campo.equals("fechaExpOrigen")) {
      valor = "";
    } else if (campo.equals("documentoRecurrente")) {
      valor = "";
    } else if (campo.equals("tercerAntecedente")) {
      valor = "";
    } else if (campo.equals("primerFundamento")) {
      valor = "";
    } else if (campo.equals("segundoNegrita")) {
      valor = "";
    } else if (campo.equals("segundoFundamento")) {
      valor = "";
    } else if (campo.equals("tercerFundamento")) {
      valor = "";
    } else if (campo.equals("parrafoNotificacionNot")) {
      valor = "";
    } else if (campo.equals("parrafoDniNot")) {
      valor = "";
    } else if (campo.equals("fechaNotiResolucion")) {
      valor = "";
    } else if (campo.equals("resolucion")) {
      valor = "";
    } else if (campo.equals("tipoResolucion")) {
      valor = "";
    } else if (campo.equals("interesado.nie")) {
      if (expediente.getInteresado().getPersonasIdentificaDtos() != null
          && !expediente.getInteresado().getPersonasIdentificaDtos().isEmpty() && expediente.getInteresado()
              .getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")) {
        valor = expediente.getInteresado().getPersonasIdentificaDtos().get(0).getNumAcreditacion();
      } else {
        valor = "";
      }

    } else if (campo.equals("fechaResolucionRecurrido")) {
      valor = "";
    } else if (campo.equals("plazo")) {
      valor = "";
    } else if (campo.equals("silencio")) {
      valor = "";
    } else if (campo.equals("documentoNot")) {
      valor = "";
    } else if (campo.equals("correoNot")) {
      valor = "";
    } else if (campo.equals("direccionNot")) {
      if (expediente.getPersonaDomicilioDtoNotificacion() != null) {
        valor = expediente.getPersonaDomicilioDtoNotificacion().getNomVia();
      } else {
        valor = "";
      }
    } else if (campo.equals("fechaNotificacionResol")) {
      valor = "";
    } else if (campo.equals("tituloAlzada")) {
      valor = "";
    } else if (campo.equals("primerAntecendenteAlzada")) {
      valor = "";
    } else if (campo.equals("segundoAntecedenteAlzada")) {
      valor = "";
    } else if (campo.equals("tercerAntecedenteAlzada")) {
      valor = "";
    } else if (campo.equals("primerFundamentoAlzada")) {
      valor = "";
    } else if (campo.equals("segundoFundamentoAlzada")) {
      valor = "";
    } else if (campo.equals("resuelvoAlzada")) {
      valor = "";
    } else if (campo.equals("recursoAlzada")) {
      valor = "";
    } else if (campo.equals("propone")) {
      valor = "";
    } else if (campo.equals("correoRecurrente")) {
      valor = "";
    } else if (campo.equals("interesado.paisNacimiento")) {
      valor = expediente.getInteresado().getPaisNacimiento().getNomPais();
    } else if (campo.equals("interesado.nacionalidad")) {
      valor = expediente.getInteresado().getNacionalidad().getNacionalidad();
    } else if (campo.equals("interesado.segundaNacionalidad")) {
      valor = expediente.getInteresado().getSegundaNacionalidad().getNacionalidad();
    } else if (campo.equals("interesado.paisNacionalidad")) {
      valor = expediente.getInteresado().getNacionalidad().getNomPais();
    } else if (campo.equals("origenSolicitud")) {
      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValDao
          .getExpFormByIdExpCodCampo(expediente.getIdExp(), "ORSOL");
      valor = expedienteFormularioValEntity != null ? expedienteFormularioValEntity.getValor() : null;
    } else if (campo.equals("viaOtros")) {
      valor = "";
    } else if (campo.equals("interesado.codigoPostal")) {
      valor = expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getCodigoPostal();
    } else if (campo.equals("interesado.municipio")) {
      if (expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
          .getLocalidadDto() != null) {
        valor = expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getLocalidadDto()
            .getNomMunicipio();
      } else {
        valor = "";
      }
    } else if (campo.equals("interesado.provincia")) {
      if (expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
          .getProvinciaDto() != null) {
        valor = expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getProvinciaDto()
            .getNomProvincia();
      } else {
        valor = "";
      }
    } else if (campo.equals("interesado.via")) {
      if (expediente.getInteresado().getPersonasDomiciliosDto() != null
          && !expediente.getInteresado().getPersonasDomiciliosDto().isEmpty() && expediente.getInteresado()
              .getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getNomVia() != null) {
        valor = expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getNomVia();
      } else {
        valor = "";
      }
    } else if (campo.equals("representante.nombreCompleto")) {
      if (expediente.getRepresentante1() != null) {
        valorConcatenado.append(getFieldObject(expediente.getRepresentante1(), "nombre").toString() + " "
            + getFieldObject(expediente.getRepresentante1(), "apellido1").toString() + " "
            + (getFieldObject(expediente.getRepresentante1(), "apellido2") == null ? ""
                : getFieldObject(expediente.getRepresentante1(), "apellido2").toString()));
      } else {
        valor = "";
      }
    } else if (campo.equals("representante.via")) {
      if (expediente.getRepresentante1() != null && expediente.getRepresentante1().getPersonasDomiciliosDto() != null
          && !expediente.getRepresentante1().getPersonasDomiciliosDto().isEmpty() && expediente.getRepresentante1()
              .getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getNomVia() != null) {
        valor = expediente.getRepresentante1().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getNomVia();
      } else {
        valor = "";
      }
    } else if (campo.equals("representante.codigoPostal")) {
      if (expediente.getRepresentante1() != null) {
        valor = expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getCodigoPostal();
      } else {
        valor = "";
      }
    } else if (campo.equals("representante.municipio")) {
      if (expediente.getRepresentante1() != null && expediente.getRepresentante1().getPersonasDomiciliosDto().get(0)
          .getPersonaDomicilioDto().getLocalidadDto() != null) {
        valor = expediente.getRepresentante1().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
            .getLocalidadDto().getNomMunicipio();
      } else {
        valor = "";
      }
    } else if (campo.equals("representante.provincia")) {
      if (expediente.getRepresentante1() != null && expediente.getRepresentante1().getPersonasDomiciliosDto().get(0)
          .getPersonaDomicilioDto().getProvinciaDto() != null) {
        valor = expediente.getRepresentante1().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
            .getProvinciaDto().getNomProvincia();
      } else {
        valor = "";
      }
    } else if (campo.equals("fechaPublicacionBoe")) {
      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValDao
          .getExpFormByIdExpCodCampo(expediente.getIdExp(), "FPBOE");
      valor = expedienteFormularioValEntity != null ? expedienteFormularioValEntity.getValor().replace("-", "/") : "";
    } else if (campo.equals("codExpOrigen")) {
      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValDao
          .getExpFormByIdExpCodCampo(expediente.getIdExp(), "CODEX");
      valor = expedienteFormularioValEntity != null ? expedienteFormularioValEntity.getValor() : null;
    } else if (campo.equals("fechaResolucionOrigen")) {

      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValDao
          .getExpFormByIdExpCodCampo(expediente.getIdExp(), "CODEX");
      expedienteFormularioValEntity.getValor();

      ExpedienteEntity expedienteOrigen = expedienteDao
          .getExpedienteByCodExpediente(expedienteFormularioValEntity.getValor());

      valor = expedienteOrigen.getFechaResolucion();

    } else if (campo.equals("interesado.fechaExamenCCSEApto")) {
      valor = this.getValorFechaExamenApto(expediente.getInteresado().getIdPer(), "CCSE");
    } else if (campo.equals("interesado.fechaExamenDELEApto")) {
      valor = this.getValorFechaExamenApto(expediente.getInteresado().getIdPer(), "DELE");
    } else {// se obtienen lo siguientes campos
      // codExp, fechaSolicitud, interesado.progenitor1, interesado.progenitor2
      // interesado.lugarNacimiento, interesado.fechaNacimiento, fechaActual
      valor = obtenerValorDto(campo, valor);
    }
    if (valor == null) {
      valor = "";
    }

    if (!valorConcatenado.isEmpty()) {
      valor = valorConcatenado.toString();
    }
    LOG.info("El valor del campo {} del expediente {} es: {}", campo, expediente.getCodExp(), valor);
    LOG.debug("End - DocumentosServiceImpl.obtenerValorCampo del campo {} para el expediente {} con codExp {}", campo,
        expediente.getIdExpGd(), expediente.getCodExp());
    return valor.toString();
  }

  private StringBuilder getDocumentosSolicitudListado(ExpedienteDto expediente, StringBuilder valorConcatenado)
      throws SinacException {
    ExpedienteDto expedienteDto = expedientesService.getExpedienteByIdExpediente(expediente.getIdExp());
    List<SolicitudDocumentoDto> documentosSolicitud = solicitudesService
        .getDocsSolBySolicitudId(expedienteDto.getSolicitudDto().getIdSol());
    if (!documentosSolicitud.isEmpty()) {
      for (SolicitudDocumentoDto ed : documentosSolicitud) {
        valorConcatenado.append(
            ed.getNomDoc() + "</text:p></text:list-item></text:list><text:list text:continue-numbering=\"true\" "
                + "text:style-name=\"WWNum23\"><text:list-item><text:p text:style-name=\"P13\" "
                + "loext:marker-style-name=\"T10\">");
      }
    }
    return valorConcatenado;
  }

  private String getValorFechaExamenApto(BigInteger idPer, String tipo) {
    List<PerCertificacionesEntity> perCertificacionesEntity = perCertificacionesDao
        .getPerCertificacionesByIdPerTipoCertificacionCalificacion(idPer, tipo, "APTO");
    if (!perCertificacionesEntity.isEmpty()) {
      if (perCertificacionesEntity.get(0) != null) {
        return new SimpleDateFormat(DD_MM_YYYY).format(perCertificacionesEntity.get(0).getFechaExamen());
      }
    }
    return NO_CONSTA;
  }

  @Override
  public DocumentoTipoDto getTipoDocumentoPorCodGdCodReg(int idLdvGd, int idLdvReg) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getDocumentoTipoByIdDocumentoTipo de idLdvGd={} e idLdvReg={}", idLdvGd,
        idLdvReg);
    DocumentoTipoDto documentoTipoDto = null;
    try {
      documentoTipoDto = documentoTipoMapper.toDto(documentoTipoDao.getTipoDocPorCodGdCodReg(idLdvGd, idLdvReg));
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_130).logMessageParams(idLdvGd, idLdvReg)
          .type(SinacExceptionType.DATA);
    }
    LOG.debug("End - DocumentosServiceImpl.getDocumentoTipoByIdDocumentoTipo de idLdvGd={} e idLdvReg={}", idLdvGd,
        idLdvReg);
    return documentoTipoDto;
  }

  private Object getValorFechaInforme(ExpedienteDto expediente, String campo, Pageable pageable) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getValorFechaInforme del campo {} del expediente {} con codExp {}", campo,
        expediente.getIdExp(), expediente.getCodExp());
    Object valor;
    valor = null;
    String codLdv = "";
    if (campo.contains("DGP")) {
      codLdv = "TINF-DGP";
    } else if (campo.contains("MJU")) {
      codLdv = "TINF-MJU";
    } else if (campo.contains("CNI")) {
      codLdv = "TINF-CNI";
    }
    final String COD_CADUCADO = "EINF-CAD";

    List<ExpedienteInformeEntity> expedienteInforme = expedienteInformeDao
        .getExpedientesInformesByExpAndTipo(expediente.getIdExp(), codLdv, COD_CADUCADO, pageable);

    if (expedienteInforme != null && !expedienteInforme.isEmpty()) {
      ExpedienteInformeEntity informeEntity = expedienteInforme.get(0);

      Date fechaInforme = campo.contains("solicita") ? informeEntity.getFechaSolicitud()
          : informeEntity.getFechaRecepcion();
      valor = parseToDateIfDate(fechaInforme);
    } else {
      valor = "";
    }

    LOG.info("El valor del campo {} del expediente {} es: {}", campo, expediente.getCodExp(), valor);
    LOG.debug("End - DocumentosServiceImpl.getValorFechaInforme del campo {} del expediente {} con codExp {}", campo,
        expediente.getIdExp(), expediente.getCodExp());
    return valor;
  }

  private Object getValorFechaPrimerInforme(ExpedienteDto expediente) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getValorFechaPrimerInforme del expediente {} con codExp {}",
        expediente.getIdExp(), expediente.getCodExp());
    Object valor;
    valor = null;
    LdvMaestraDto ldvInformeCaducado = catalogosService.getCatalogoByCod("EINF-CAD");
    List<ExpedienteInformeDto> expedientesInformes = getExpedienteInformesByIdExpediente(expediente.getIdExp(),
        ldvInformeCaducado.getIdLdvMae());
    if (!expedientesInformes.isEmpty()) {
      Date fechaInforme = null;
      fechaInforme = expedientesInformes.get(expedientesInformes.size() - 1).getFechaSolicitud();
      valor = parseToDateIfDate(fechaInforme);
    }
    LOG.info("El valor de la fecha primer informe del expediente {} es: {}", expediente.getCodExp(), valor);
    LOG.debug("End - DocumentosServiceImpl.getValorFechaPrimerInforme del expediente {} con codExp {}",
        expediente.getIdExp(), expediente.getCodExp());
    return valor;
  }

  /**
   * Metodo para obtener del DTO el valor de un dato a partir del nombre del campo
   *
   * @param campo Nombre del campo Ejemplos: interesado.nombre,
   *              expedienteDocumentoDtos.nomDoc
   * @param valor Objeto dto con los valores
   * @return Objeto con el valor del campo que se busca
   * @throws SinacException
   */
  private Object obtenerValorDto(String campo, Object valor) throws SinacException {
    Field field = null;
    StringBuilder valorConcatenado = new StringBuilder();
    String[] camposPlantilla = campo.split("\\.");
    for (String campoPlantilla : camposPlantilla) {
      try {
        if (valor instanceof LinkedHashSet<?> valorSet) {
          for (Object objeto : valorSet.stream().toList()) {
            field = objeto.getClass().getDeclaredField(campoPlantilla);
            field.setAccessible(true);
            valorConcatenado.append(field.get(objeto) + "\n");
          }
        } else {
          if (campoPlantilla.equals("fechaActual")) {
            valor = new SimpleDateFormat(DD_MM_YYYY).format(new Date());
          } else {
            valor = getFieldObject(valor, campoPlantilla);
          }
        }
        valor = parseToDateIfDate(valor);
      } catch (Exception e) {
        throw new SinacException(e, SinacExceptionMessageType.MESSAGE_131).logMessageParams(campo);
      }
    }
    if (!valorConcatenado.isEmpty()) {
      valor = valorConcatenado;
    }
    return valor;
  }

  private String getDatosDomicilioConcatenados(PersonaDomicilioDto pd) {
    // Se crean las variables vacías, en caso de que sea nulo el dato, se devolverán
    // así
    String lugarResidencia = "";
    String nomVia = "";
    String numVia = "";
    String codigoPostal = "";
    String bppelk = "";
    if (pd.getLugarResidencia() != null) {
      lugarResidencia = pd.getLugarResidencia();
    }
    if (pd.getNomVia() != null) {
      nomVia = pd.getNomVia();
    }
    if (pd.getNumVia() != null) {
      numVia = pd.getNumVia();
    }
    if (pd.getCodigoPostal() != null) {
      codigoPostal = pd.getCodigoPostal();
    }

    if (pd.getBloque() != null) {
      bppelk = "Bloque: " + pd.getBloque();
    }

    if (pd.getPiso() != null) {
      bppelk += ", piso: " + pd.getPiso();
    }

    if (pd.getPortal() != null) {
      bppelk += ", portal: " + pd.getPortal();
    }

    if (pd.getEscalera() != null) {
      bppelk += ", escalera: " + pd.getEscalera();
    }

    if (pd.getLetra() != null) {
      bppelk += ", letra: " + pd.getLetra();
    }

    if (pd.getKm() != null) {
      bppelk += ", kilometro: " + pd.getKm();
    }

    return lugarResidencia + " " + nomVia + " " + numVia + " " + codigoPostal + " " + bppelk;
  }

  /***
   * En el valor object se le puede pasar un dto y en el valor campo, el nombre
   * del campo del que se quiere obtener el valor. Esto devuelve el valor del
   * campo en un object
   *
   * @param valor          Ejemplo: PersonaDto
   * @param campoPlantilla Ejemplo: nombre
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   */
  private Object getFieldObject(Object valor, String campoPlantilla)
      throws NoSuchFieldException, IllegalAccessException {
    Field field;
    if (valor != null) {
      field = valor.getClass().getDeclaredField(campoPlantilla);
      field.setAccessible(true);
      valor = field.get(valor);
    } else {
      valor = "";
    }
    return valor;
  }

  private Object parseToDateIfDate(Object valor) throws SinacException {
    if (valor instanceof Date date) {
      LOG.info("Es de tipo fecha el objeto {}", valor);
      SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("es", "ES"));

      SimpleDateFormat outputFormat = new SimpleDateFormat(DD_MM_YYYY);

      String formattedDate = inputFormat.format(date);

      boolean isInputParseable = formattedDate.equals(valor.toString());

      valor = isInputParseable ? formattedDate : outputFormat.format(date);
    }
    return valor;
  }

  /**
   * Obtiene la cadena contenida en un InputStream
   *
   * @param in the in
   * @return cadena
   */
  private static String toString(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();

    byte[] buffer = new byte[2048];
    for (int read = in.read(buffer); read > 0; read = in.read(buffer)) {
      sb.append(new String(buffer, 0, read));
    }

    in.close();
    return sb.toString();
  }

  @Override
  public String getUrlDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException {
    return wopi.getWopiUrl(1, idDocumento);
  }

  @Override
  public ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException {
    try {
      return expedienteDocumentoWithExpedienteMapper
          .toDto(expedienteDocumentoDao.findExpedienteDocumentoById(idDocumento));
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_132).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumentoIdExpediente(BigInteger idDocumento,
      BigInteger idExpediente) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getExpedienteDocumentoByIdDocumentoIdExpediente del documento {} del expediente {}",
        idDocumento, idExpediente);
    try {
      if (idDocumento != null && idExpediente != null) {
        ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao
            .getExpedienteDocumentoByIdDocumentoIdExpediente(idDocumento, idExpediente);
        if (!expedienteDocumentoEntity.isFlgActivo()) {
          ExpedienteDocumentoEntity expedienteDocumentoActivo = expedienteDocumentoDao
              .getExpedienteDocumentoActivoByCodDocTipo(expedienteDocumentoEntity.getDocumentoTipoEntity().getCodTipo(),
                  idExpediente);
          expedienteDocumentoActivo.setFlgActivo(false);
          expedienteDocumentoEntity.setFlgActivo(true);
        }
        LOG.debug(
            "End - DocumentosServiceImpl.getExpedienteDocumentoByIdDocumentoIdExpediente del documento {} del expediente {}",
            idDocumento, idExpediente);
        return expedienteDocumentoWithExpedienteMapper.toDto(expedienteDocumentoEntity);
      } else {
        if (idDocumento == null) {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_25).type(SinacExceptionType.DATA);
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_26).type(SinacExceptionType.DATA);
        }
      }
    } catch (SinacException ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_133).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<DocumentoTipoDto> getComboDocumentoTipo(Short procedimiento) {

    LOG.debug("Init - AdjuntarDocumentoServiceImpl.getComboDocumentoTipo del procedimiento {}", procedimiento);

    List<ProcedimientosDocumentosTipoEntity> listaProcedimientosDocumentosTipoEntity = new ArrayList<>();
    LOG.info("Adjuntar documentos - se recuperan los tipos documentos para el procedimiento {}", procedimiento);
    try {
      listaProcedimientosDocumentosTipoEntity = procedimientosDocumentosTipoDao
          .getTipoDocPorProMDocTipoFlgEntrada(procedimiento);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_134).logMessageParams(procedimiento);
    }
    List<DocumentoTipoEntity> listaDocumentoTipoEntity = new ArrayList<>();
    for (ProcedimientosDocumentosTipoEntity procedimientosDocumentosTipoEntity : listaProcedimientosDocumentosTipoEntity) {
      listaDocumentoTipoEntity.add(procedimientosDocumentosTipoEntity.getDocumentosTipo());
    }

    List<DocumentoTipoDto> listaDocumentoTipoDto = documentoTipoMapper.toDto(listaDocumentoTipoEntity);

    LOG.info("End - AdjuntarDocumentoServiceImpl.getComboDocumentoTipo del procedimiento {}", procedimiento);
    return listaDocumentoTipoDto;
  }

  @Override
  public DescargaDeDocumentoDto getArchivoByIdDocExp(BigInteger idDocExp) throws SinacException {
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao.findById(idDocExp)
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.MESSAGE_135).logMessageParams(idDocExp));
    LOG.debug("Init - documentosServiceImpl.getArchivoByIdDocExp del documento {}", idDocExp);
    try {
      DescargaDeDocumentoDto descargaDeDocumentoDto = null;

      if (StringUtils.isNotEmpty(expedienteDocumentoEntity.getCodGd())) {
        // Recuperar el Documento del Gestor Documental.
        InputStream is = gestorDocumentalConnector.obtenerDocumento(expedienteDocumentoEntity.getCodGd());

        if (is != null) {
          byte[] file = is.readAllBytes();

          descargaDeDocumentoDto = new DescargaDeDocumentoDto(file, expedienteDocumentoEntity.getNomDoc());
        } else {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_136).logMessageParams(idDocExp,
              expedienteDocumentoEntity.getNomDoc());
        }
      } else {
        // Si no se ha podido recuperar el Documento del Gestor Documental, se recupera
        // de la NAS.
        byte[] file = nfsManager.getDocumentContent(expedienteDocumentoEntity.getNomDoc(),
            expedienteDocumentoEntity.getNfsRuta());

        if (file == null) {
          throw new SinacException(SinacExceptionMessageType.MESSAGE_137)
              .logMessageParams(expedienteDocumentoEntity.getNomDoc());
        } else {
          descargaDeDocumentoDto = new DescargaDeDocumentoDto(file, expedienteDocumentoEntity.getNomDoc());
        }
      }
      LOG.info("Se ha recuperado correctamente el documento {} con nombre: {}", idDocExp,
          expedienteDocumentoEntity.getNomDoc());
      LOG.debug("End - documentosServiceImpl.getArchivoByIdDocExp del documento {}", idDocExp);
      return descargaDeDocumentoDto;
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_135).logMessageParams(idDocExp);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public DescargaDeDocumentoDto descargarDocumentoCopiaAutentica(BigInteger idDocExp) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.descargarDocumentoCopiaAutentica para el expDocumento {}", idDocExp);
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao.findById(idDocExp)
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.MESSAGE_138).logMessageParams(idDocExp));
    if (StringUtils.isNotEmpty(expedienteDocumentoEntity.getCodGd())) {
      String rutaFicheroTemp = nfsManager.getTemporalPath("temp.pdf");
      try (InputStream is = gestorDocumentalConnector.obtenerDocumento(expedienteDocumentoEntity.getCodGd());
          OutputStream os = new FileOutputStream(new File(rutaFicheroTemp))) {
        IOUtils.copy(is, os);
        IOUtils.closeQuietly(os);
        IOUtils.closeQuietly(is);
        DataHandler dh = new DataHandler(Files.readAllBytes(Path.of(rutaFicheroTemp)), "application/pdf");
        final CopiaAutenticaDto copiaAutenticaDto = copiaAutenticaConnector.generarCopiaAutentica(dh);
        expedienteDocumentoEntity.setDocCsv(copiaAutenticaDto.getCodigoCSV());
        expedienteDocumentoDao.save(expedienteDocumentoEntity);
        LOG.debug("End - DocumentosServiceImpl.descargarDocumentoCopiaAutentica para el expDocumento {}", idDocExp);
        return new DescargaDeDocumentoDto(copiaAutenticaDto.getDocumento().getInputStream().readAllBytes(),
            expedienteDocumentoEntity.getNomDoc(), copiaAutenticaDto.getCodigoCSV());
      } catch (IOException e) {
        throw new SinacException(e, SinacExceptionMessageType.MESSAGE_139).logMessageParams(idDocExp);
      } catch (Exception e) {
        throw new SinacException(e, SinacExceptionMessageType.MESSAGE_140).logMessageParams(idDocExp);
      } finally {
        try {
          Files.delete(Path.of(rutaFicheroTemp));
        } catch (IOException e) {
          LOG.error("Error cerrando fichero temporal del documento {} para la copia auténtica: {}", idDocExp,
              e.getMessage());
        }
      }
    } else {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_141).logMessageParams(idDocExp);
    }
  }

  @Override
  public ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumentoExpediente(final BigInteger idDocumentoExpediente)
      throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getExpedienteDocumentoByIdDocumentoExpediente del documentoExpediente {}",
        idDocumentoExpediente);

    ExpedienteDocumentoDto expedienteDocumentoDto = null;

    try {
      expedienteDocumentoDto = expedienteDocumentoWithExpedienteMapper.toDto(
          expedienteDocumentoDao.getExpedienteDocumentoByIdDocumentoExpediente(idDocumentoExpediente).orElseThrow());
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_142).logMessageParams(idDocumentoExpediente)
          .type(SinacExceptionType.DATA);
    }
    LOG.info("Se ha recuperado correctamente el documento {}", idDocumentoExpediente);
    LOG.debug("End - DocumentosServiceImpl.getExpedienteDocumentoByIdDocumentoExpediente del documentoExpediente {}",
        idDocumentoExpediente);
    return expedienteDocumentoDto;
  }

  @Override
  public List<FirmanteDto> getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento(final short idProcedimiento,
      final short idTipoDocumento) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento del procedimiento {} y tipoDocumento {}",
        idProcedimiento, idTipoDocumento);

    List<FirmanteDto> firmantes = null;

    try {
      firmantes = firmanteMapper.toDtoList(
          firmanteDao.getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento(idProcedimiento, idTipoDocumento)
              .filter(list -> !list.isEmpty()).orElseThrow());
      if (!CollectionUtils.isEmpty(firmantes)) {
        for (FirmanteDto firmanteDto : firmantes) {
          if (StringUtils.isEmpty(firmanteDto.getDni())) {
            firmanteDto.setDni(sinacSession.getUsuario().getDni());
          }
        }
      }

    } catch (final NoSuchElementException noSuchElementException) {
      LOG.warn("No se han encontrado firmantes para el tipo de documento {}", idTipoDocumento);
      return null;
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_155)
          .logMessageParams(idProcedimiento, idTipoDocumento).type(SinacExceptionType.DATA);
    }
    LOG.info("Firmantes del procediemiento {} del tipo de documento {} son: {}", idProcedimiento, idTipoDocumento,
        firmantes);
    LOG.debug(
        "End - DocumentosServiceImpl.getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento del procedimiento {} y tipoDocumento {}",
        idProcedimiento, idTipoDocumento);

    return firmantes;
  }

  @Override
  public void saveExpedienteFirma(final ExpedienteFirmaDto expedienteFirmaDto) throws SinacException {
    String nombreDocumentoFirmado = expedienteFirmaDto.getExpedienteDocumentoDto().getNomDoc();
    LOG.debug("Init - DocumentosServiceImpl.saveExpedienteFirma del expedienteFirmado {} con nombre del documento: {}",
        expedienteFirmaDto.getIdExpFirma(), nombreDocumentoFirmado);
    ExpedienteFirmaEntity expedienteFirmaEntity = expedienteFirmaMapper.toEntity(expedienteFirmaDto);
    try {
      expedienteFirmaDao.save(expedienteFirmaEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_154)
          .logMessageParams(nombreDocumentoFirmado).type(SinacExceptionType.DATA);
    }
    LOG.info("Se ha guardado correctamente el documento firmado {} con nombre {}", expedienteFirmaDto.getIdExpFirma(),
        nombreDocumentoFirmado);
    LOG.debug("End - DocumentosServiceImpl.saveExpedienteFirma del expedienteFirmado {} con nombre del documento: {}",
        expedienteFirmaDto.getIdExpFirma(), nombreDocumentoFirmado);
  }

  @Override
  public void updateEstadoDocumento(final BigInteger idDocumento, final LdvMaestraDto ldvMaestraDto)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.updateEstadoDocumento el valor de la ldvDto para cambiar el estado es {} al documento {}",
        ldvMaestraDto.getCodLdvMae(), idDocumento);

    try {
      LdvMaestraEntity ldvEntity = ldvMaestraMapper.toEntity(ldvMaestraDto);
      expedienteDocumentoDao.updateEstadoDocumento(idDocumento, ldvEntity);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_21).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_22).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("End - DocumentosServiceImpl.updateEstadoDocumento el estado {} al documento {}",
        ldvMaestraDto.getNomLdvMae(), idDocumento);
  }

  @Override
  public ExpedienteFirmaDto getExpedienteFirmaByIdSolicitudFirma(final String idSolicitudFirma) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getExpedienteFirmaByIdSolicitudFirma de la solicitudFirma {}",
        idSolicitudFirma);

    ExpedienteFirmaDto expedienteFirmaDto = null;

    try {
      expedienteFirmaDto = expedienteFirmaMapperWithExpedienteMapper
          .toDto(expedienteFirmaDao.getExpedienteFirmaByIdSolicitudFirma(idSolicitudFirma).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_19).logMessageParams(idSolicitudFirma)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_20).logMessageParams(idSolicitudFirma)
          .type(SinacExceptionType.DATA);
    }
    LOG.info("El expedienteFirmaDto asociado al identificador de la solicitud de firma {} es: {}", idSolicitudFirma,
        expedienteFirmaDto);
    LOG.debug("End - DocumentosServiceImpl.getExpedienteFirmaByIdSolicitudFirma de la solicitudFirma {}",
        idSolicitudFirma);

    return expedienteFirmaDto;
  }

  @Override
  public void updateExpedienteFirmaNoVigente(final BigInteger idExpedienteFirma, final UsuarioDto modificadoPor)
      throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.updateExpedienteFirma del expedienteFirma {} por el usuario {} con dni: {}",
        idExpedienteFirma, modificadoPor.getIdUsu(), modificadoPor.getDni());

    final Date now = new Date();

    try {
      expedienteFirmaDao.updateExpedienteFirmaNoVigente(idExpedienteFirma, now, usuarioMapper.toEntity(modificadoPor));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_13).logMessageParams(idExpedienteFirma)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_14).logMessageParams(idExpedienteFirma)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("End - DocumentosServiceImpl.updateExpedienteFirma del expedienteFirma {} por el usuario {} con dni: {}",
        idExpedienteFirma, modificadoPor.getIdUsu(), modificadoPor.getDni());

  }

  @Override
  public ExpedienteDocumentoDto getInfoToSaveDocumentoSalidaByIdDocumentoExpediente(
      final BigInteger idDocumentoExpediente) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getInfoToSaveDocumentoSalidaByIdDocumentoExpediente del documentoExpediente {}",
        idDocumentoExpediente);

    ExpedienteDocumentoDto expedienteDocumentoDto = null;

    try {
      final ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao
          .findExpedienteDocumentoById(idDocumentoExpediente);

      if (expedienteDocumentoEntity.getLdvMaestraEntityByIdEstDocLdv() != null) {
        Hibernate.initialize(expedienteDocumentoEntity.getLdvMaestraEntityByIdEstDocLdv());
      }

      expedienteDocumentoDto = expedienteDocumentoWithExpedienteMapper.toDto(expedienteDocumentoEntity);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_MESSAGE_15)
          .logMessageParams(idDocumentoExpediente).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_MESSAGE_16)
          .logMessageParams(idDocumentoExpediente).type(SinacExceptionType.DATA);
    }

    LOG.info("La información del expedienteDocumento con id {} es: {}", idDocumentoExpediente, expedienteDocumentoDto);
    LOG.debug(
        "End - DocumentosServiceImpl.getInfoToSaveDocumentoSalidaByIdDocumentoExpediente del documentoExpediente {}",
        idDocumentoExpediente);

    return expedienteDocumentoDto;
  }

  @Override
  public DataHandler signDocumento(final ExpedienteDocumentoDto expedienteDocumentoDto) {
    LOG.debug("Init - DocumentosServiceImpl.signDocumento el documento {}", expedienteDocumentoDto.getNomDoc());

    DataHandler contenidoFirmado = null;

    try {
      final DataSource dataSource = nfsManager.getDataSource(expedienteDocumentoDto.getNomDoc(),
          expedienteDocumentoDto.getNfsRuta());

      final SignDocumentResponseDto signDocumentResponseDto = clienteFirmaServidorConnector
          .signDocumento(expedienteDocumentoDto.getNomDoc(), dataSource);
      if (signDocumentResponseDto.getSignedData() == null) {
        LOG.info("signDocumento:  Se reenviará el documento original por falta de SignedData.",
            expedienteDocumentoDto.getNomDoc());

        contenidoFirmado = new DataHandler(dataSource);
      } else if (checkSignDocumentResponseDto(signDocumentResponseDto)) {
        contenidoFirmado = signDocumentResponseDto.getSignedData();
        LOG.info("Se ha firmado correctamente el documento {}", expedienteDocumentoDto.getNomDoc());

      }
    } catch (SinacException sinacException) {
      // Llamamos a la fachada porque necesitamos que esta actualización se realice
      // aunque falle
      // El tipo de transacción es REQUIERES_NEW
      expedientesFacade.updateEstadoDocumento(expedienteDocumentoDto.getIdExpDoc(),
          catalogosService.getCatalogoByCod("EDOC-EFI"));
      throw new SinacException(SinacExceptionMessageType.MESSAGE_153)
          .logMessageParams(expedienteDocumentoDto.getNomDoc()).type(SinacExceptionType.DATA);
    }
    LOG.debug("End - DocumentosServiceImpl.signDocumento el documento {}", expedienteDocumentoDto.getNomDoc());

    return contenidoFirmado;
  }

  // Método para firmar el documento
  public DataHandler signDocumentByByte(byte[] contenidoDocumento, String nombreDocumento) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.signDocumentByByte el documento {}", nombreDocumento);
    try {
      DataSource dataSource = new ByteArrayDataSource(contenidoDocumento, "application/vnd.oasis.opendocument.text");
      DataHandler docFirmado = signDocumentoToService(nombreDocumento, dataSource);
      // Verificamos si el resultado es nulo.
      if (docFirmado == null) {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_152).logMessageParams(nombreDocumento);
      }
      LOG.debug("End - DocumentosServiceImpl.signDocumentByByte el documento {}", nombreDocumento);
      return docFirmado;
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_150).logMessageParams(nombreDocumento);
    }
  }

  private DataHandler signDocumentoToService(String nombreDocumento, DataSource dataSource) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.signDocumentToService el documento {}", nombreDocumento);
    try {
      final SignDocumentResponseDto signDocumentResponseDto = clienteFirmaServidorConnector
          .signDocumento(nombreDocumento, dataSource);
      // Validamos la respuesta del servicio de firma.
      if (!checkSignDocumentResponseDto(signDocumentResponseDto)) {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_151).logMessageParams(nombreDocumento);
      }
      LOG.info("Se ha firmado correctamente el documento {}", nombreDocumento);
      LOG.debug("End - DocumentosServiceImpl.signDocumentToService el documento {}", nombreDocumento);
      return signDocumentResponseDto.getSignedData();
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_150).logMessageParams(nombreDocumento);
    }
  }

  @Override
  public RegistroDto generateRegistroDocumento(final TipoRegistroRegageEnum tipoRegistro,
      final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido) {
    LOG.debug("Init - DocumentosServiceImpl.generateRegistroDocumento del documento {} con registro {}",
        expedienteDocumentoDto.getNomDoc(), tipoRegistro);
    RegistroDto registroDto = null;

    final AnexoTypeV3Dto documentoAnexo = new AnexoTypeV3Dto();
    documentoAnexo.setNombre(expedienteDocumentoDto.getNomDoc());

    try {
      documentoAnexo.setHash(Utilidades.getHashForFile(contenido.getInputStream().readAllBytes()));
    } catch (final IOException ioException) {
      throw new SinacException(ioException, SinacExceptionMessageType.CUSTOM_MESSAGE)
          .logMessageParams(ioException.getMessage()).type(SinacExceptionType.DATA);
    }
    documentoAnexo.setTipoDocumento(
        getTipoDocumentoAnexoRegageByIdDocumentoTipo(expedienteDocumentoDto.getDocumentoTipoDto().getIdDocTipo())
            .getNomLdvMae());
    ResultadoRegistroTypeV3Dto resultadoRegistroTypeV3Dto = null;

    try {
      solicitudesService.setEstadoSolicitud("Registrando documento " + expedienteDocumentoDto.getNomDoc());
      resultadoRegistroTypeV3Dto = regageConnector.generateRegistroDocumentos(tipoRegistro, List.of(documentoAnexo));

    } catch (SinacException sinacException) {
      throw new SinacException(sinacException, SinacExceptionMessageType.MESSAGE_149)
          .logMessageParams(expedienteDocumentoDto.getNomDoc()).type(SinacExceptionType.DATA);
    }

    if (resultadoRegistroTypeV3Dto != null
        && TipoRespuestaRegageEnum.OK.equals(resultadoRegistroTypeV3Dto.getTipoRespuesta())) {
      registroDto = new RegistroDto();

      registroDto.setExpedienteDocumentoDto(expedienteDocumentoDto);

      if (tipoRegistro.equals(TipoRegistroRegageEnum.SALIDA)) {
        registroDto.setLdvMaestraDto(catalogosService.getCatalogoByCod("OREG-SAL"));
      } else if (tipoRegistro.equals(TipoRegistroRegageEnum.ENTRADA)) {
        registroDto.setLdvMaestraDto(catalogosService.getCatalogoByCod("OREG-ENT"));
      }

      registroDto.setNumReg(resultadoRegistroTypeV3Dto.getNuRegistro());
      registroDto.setFechaReg(resultadoRegistroTypeV3Dto.getFechaHoraRegistro().toGregorianCalendar().getTime());

      expedienteDocumentoDto.getRegistroDtos().add(registroDto);
    }
    LOG.info("Se ha generado el registro {} correctamente en el documento {}: {}", tipoRegistro,
        expedienteDocumentoDto.getNomDoc(), registroDto);
    LOG.debug("End - DocumentosServiceImpl.generateRegistroDocumento del documento {} con registro {}",
        expedienteDocumentoDto.getNomDoc(), tipoRegistro);
    return registroDto;
  }

  @Override
  public void saveRegistro(final RegistroDto registroDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.saveRegistro del registro {} con número: {}", registroDto.getIdReg(),
        registroDto.getNumReg());

    try {
      RegistroEntity registroEntity = registroWithDocumentosMapper.toEntity(registroDto);
      if (registroDto.getIdReg() != null) {
        RegistroEntity registroEntityPrevio = registroDao.getRegistroById(registroDto.getIdReg());
        registroEntity.setFechaCreacion(registroEntityPrevio.getFechaCreacion());
        registroEntity.setFechaIniVig(registroEntityPrevio.getFechaIniVig());
        registroEntity.setFlgActivo(registroEntityPrevio.isFlgActivo());
        registroEntity.setCreadoPor(registroEntityPrevio.getCreadoPor());
      }
      registroDao.save(registroEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_148)
          .logMessageParams(registroDto.getIdReg()).type(SinacExceptionType.DATA);
    }

    LOG.debug("End - DocumentosServiceImpl.saveRegistro del registro {} con número: {}", registroDto.getIdReg(),
        registroDto.getNumReg());
  }

  @Override
  public void deleteRegistro(final RegistroDto registroDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.deleteRegistro con número {}", registroDto.getNumReg());
    try {
      RegistroEntity registroEntity = registroMapper.toEntity(registroDto);
      registroDao.delete(registroEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.MESSAGE_147)
          .logMessageParams(registroDto.getNumReg());
    }
    LOG.info("Se ha eliminado correctamente el registro con número {}", registroDto.getNumReg());
    LOG.debug("End - DocumentosServiceImpl.deleteRegistro con número {}", registroDto.getNumReg());
  }

  @Override
  public void saveDocumentoGestorDocumental(final TipoRegistroRegageEnum tipoAsientoRegistral,
      final String identificadorExpedienteGD, final short idProcedimiento,
      final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.saveDocumentoGestorDocumental el documento {} con identificadorExpedienteGd={} del procedimiento {}",
        expedienteDocumentoDto.getNomDoc(), identificadorExpedienteGD, idProcedimiento);
    final MetadatosDocumentoGesdocDto metadatosDocumentoGesdocDto = getMetadatosDocumentoGesdocDto(tipoAsientoRegistral,
        idProcedimiento, expedienteDocumentoDto);
    String identificadorDocumentoGD = null;
    try {
      identificadorDocumentoGD = gestorDocumentalConnector.capturarDocumento(contenido, identificadorExpedienteGD,
          metadatosDocumentoGesdocDto);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_146)
          .logMessageParams(expedienteDocumentoDto.getNomDoc());
    }

    if (StringUtils.isNotEmpty(identificadorDocumentoGD)) {
      expedienteDocumentoDto.setCodGd(identificadorDocumentoGD);
    } else {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_145)
          .logMessageParams(expedienteDocumentoDto.getNomDoc());
    }
    LOG.debug(
        "End - DocumentosServiceImpl.saveDocumentoGestorDocumental el documento {} con identificadorExpedienteGd={} del procedimiento {}",
        expedienteDocumentoDto.getNomDoc(), identificadorExpedienteGD, idProcedimiento);
  }

  @Override
  public void copyDocumentoNFS(final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido) {
    LOG.debug("Init - DocumentosServiceImpl.copyDocumentoNFS del documento {}", expedienteDocumentoDto.getNomDoc());

    if (StringUtils.isEmpty(expedienteDocumentoDto.getCodGd())) {
      InputStream inputStream = null;
      try {
        inputStream = contenido.getInputStream();
        final DataSource dataSource = nfsManager.getDataSource(expedienteDocumentoDto.getNomDoc(),
            inputStream.readAllBytes());
        nfsManager.getContentRepository(expedienteDocumentoDto.getNfsRuta()).save(dataSource);
        LOG.info("DocumentosServiceImpl.copyDocumentoNFS - contenido del documento {} guardado en NFS",
            expedienteDocumentoDto.getNomDoc());
      } catch (final IOException | BeansException | ContentRepositoryException exception) {
        LOG.error(String.format("DocumentosServiceImpl.copyDocumentoNFS - Error: %s",
            Literal.EL_DOCUMENTO + expedienteDocumentoDto.getNomDoc() + LITERAL_NO_COPIADO), exception);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (final IOException exception) {
            LOG.error(String.format("DocumentosServieImpl.copyDocumentoNFS - Error al recuperar el Documento \"%s\".",
                expedienteDocumentoDto.getNomDoc()));
          }
        }
      }
    } else {
      expedienteDocumentoDto.setNfsRuta(null);
    }

    LOG.debug("End - DocumentosServiceImpl.copyDocumentoNFS del documento {}", expedienteDocumentoDto.getNomDoc());
  }

  @Override
  public void deleteDocumentoNFS(final ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException {

    final String nombreDocumento = expedienteDocumentoDto.getNomDoc();
    final String nfsPathDocumento = expedienteDocumentoDto.getNfsRuta();
    LOG.debug("Init - DocumentosServiceImpl.deleteDocumentoNFS el documento {} con nfsPath: {}", nombreDocumento,
        nfsPathDocumento);

    try {
      if (nfsManager.exists(nombreDocumento, nfsPathDocumento)) {
        final DataSource dataSource = nfsManager.getDataSource(nombreDocumento, nfsPathDocumento);
        nfsManager.getContentRepository(nfsPathDocumento.concat(STRING_SEPARATOR)).delete(dataSource);
      }
    } catch (final ContentRepositoryException contentRepositoryException) {
      throw new SinacException(contentRepositoryException, SinacExceptionMessageType.MESSAGE_144)
          .logMessageParams(nombreDocumento);
    }

    LOG.debug("End - DocumentosServiceImpl.deleteDocumentoNFS el documento {} con nfsPath: {}", nombreDocumento,
        nfsPathDocumento);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void updateCodGdAndSetNfsRutaToNullForDocumento(final BigInteger idDocumento, final String codGd)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.updateCodGdAndSetNfsRutaToNullForDocumento actualizar el codGd por {} del documento {}",
        codGd, idDocumento);

    try {
      expedienteDocumentoDao.updateCodGdAndSetNfsRutaToNullForDocumento(idDocumento, codGd);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_17).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_18).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug(
        "End - DocumentosServiceImpl.updateCodGdAndSetNfsRutaToNullForDocumento actualizar el codGd por {} del documento {}",
        codGd, idDocumento);
  }

  /**
   * Comprueba DTO SignDocumentResponseDto.
   *
   * @param signDocumentResponseDto DTO SignDocumentResponseDto.
   * @return true, si el DTO SignDocumentResponseDto se ha validado correctamente.
   *         false, en caso contrario.
   */
  private boolean checkSignDocumentResponseDto(final SignDocumentResponseDto signDocumentResponseDto) {
    LOG.debug("Init - DocumentosServiceImpl.checkSignDocumentResponseDto ");
    InputStream input = null;
    Boolean checkFirmado = false;
    try {
      input = signDocumentResponseDto.getSignedData().getInputStream();
      checkFirmado = signDocumentResponseDto.getSignedData() != null && input != null && input.readAllBytes() != null
          && !CollectionUtils.isEmpty(signDocumentResponseDto.getSigners())
          && signDocumentResponseDto.getSigners().stream().findFirst().orElseThrow().getSignDate() != null;
    } catch (final IOException | NoSuchElementException exception) {
      LOG.error("Error firmando el documento: {}", exception.getMessage());
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException exception) {
          LOG.error("DocumentosServieImpl.checkSignDocumentResponseDto - Error al recuperar el Documento.");
        }
      }
    }
    LOG.debug("End - DocumentosServiceImpl.checkSignDocumentResponseDto ");
    return checkFirmado;
  }

  /**
   * Obtiene el DTO con los Metadatos asociados al Documento para el Gestor
   * Documental.
   *
   * @param tipoAsientoRegistral Tipo Asiento Registral.
   * @param idProcedimiento      Identificador del Procedimiento necesario para
   *                             determinar el Tipo de Firma.
   * @param nombreDocumento      Nombre del Documento.
   * @param documentoToSaveDto   DTO con la Información del Documento.
   * @return DTO con los Metadatos asociados al Documento para el Gestor
   *         Documental.
   * @throws SinacException Si se produce un error al obtener los Metadatos
   *                        asociados al Documento para el Gestor Documental.
   */
  private MetadatosDocumentoGesdocDto getMetadatosDocumentoGesdocDto(final TipoRegistroRegageEnum tipoAsientoRegistral,
      final short idProcedimiento, final String nombreDocumento, final DocumentoToSaveDto documentoToSaveDto)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getMetadatosDocumentoGesdocDto del documento {} del procedimiento {} con tipoAsientoRegistral={}",
        nombreDocumento, idProcedimiento, tipoAsientoRegistral);
    final MetadatosDocumentoGesdocDto metadatosDocumentoGesdocDto = new MetadatosDocumentoGesdocDto();

    metadatosDocumentoGesdocDto.setEstadoElaboracion(EnumeracionEstadoElaboracion
        .fromValue(catalogosService.getCatalogoById(documentoToSaveDto.getEstadoElaboracion()).getCodLdvMae()));
    metadatosDocumentoGesdocDto.getOrganos()
        .add(catalogosService.getCatalogoById(documentoToSaveDto.getOrgano()).getCodLdvMae());
    metadatosDocumentoGesdocDto.setOrigen(OrigenDocumentoGesDocEnum
        .fromValue(catalogosService.getCatalogoById(documentoToSaveDto.getOrigen()).getCodLdvMae()).getValue());
    metadatosDocumentoGesdocDto.setTipoDocumental(TipoDocumental.fromValue(
        getTipoDocumentoGestorDocumentalByIdDocumentoTipo(documentoToSaveDto.getTipoDocumento()).getCodLdvMae()));
    /*
     * metadatosDocumentoGesdocDto.setTipoFirma(TipoFirma.fromValue(
     * getTipoFirmaGestorDocumentalByIdProcedimientoAndIdDocumentoTipo(
     * idProcedimiento, documentoToSaveDto.getTipoDocumento()).getCodLdvMae()));
     */
    metadatosDocumentoGesdocDto.setTipoFirma(TipoFirma.TF_06);
    metadatosDocumentoGesdocDto.setNombreNatural(nombreDocumento);
    metadatosDocumentoGesdocDto.setNivelDeAcceso(NivelDeAcceso.E);
    metadatosDocumentoGesdocDto.setTipoRegistro(TipoRegistro.fromTipoAsientoRegistral(tipoAsientoRegistral));
    metadatosDocumentoGesdocDto.setNumeroAsiento(documentoToSaveDto.getNumeroRegistro());
    if (documentoToSaveDto.getFechaRegistro() != null) {
      metadatosDocumentoGesdocDto
          .setFechaAsiento(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(documentoToSaveDto.getFechaRegistro()));
    }
    LOG.info("Metadatos obtenido del documento {} del procedimiento {}: {}", nombreDocumento, idProcedimiento,
        metadatosDocumentoGesdocDto);
    LOG.debug(
        "End - DocumentosServiceImpl.getMetadatosDocumentoGesdocDto del documento {} del procedimiento {} con tipoAsientoRegistral={}",
        nombreDocumento, idProcedimiento, tipoAsientoRegistral);
    return metadatosDocumentoGesdocDto;
  }

  /**
   * Obtiene el DTO con los Metadatos asociados al Documento para el Gestor
   * Documental.
   *
   * @param tipoAsientoRegistral   Tipo Asiento Registral.
   * @param idProcedimiento        Identificador del Procedimiento necesario para
   *                               determinar el Tipo de Firma.
   * @param expedienteDocumentoDto DTO con la Información del Documento.
   * @return DTO con los Metadatos asociados al Documento para el Gestor
   *         Documental.
   * @throws SinacException Si se produce un error al obtener los Metadatos
   *                        asociados al Documento para el Gestor Documental.
   */
  private MetadatosDocumentoGesdocDto getMetadatosDocumentoGesdocDto(final TipoRegistroRegageEnum tipoAsientoRegistral,
      final short idProcedimiento, final ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getMetadatosDocumentoGesdocDto del documento {} del procedimiento {} con tipoAsientoRegistral={}",
        expedienteDocumentoDto.getNomDoc(), idProcedimiento, tipoAsientoRegistral);
    final RegistroDto registroDto = expedienteDocumentoDto.getRegistroDtos().stream().findFirst().orElseThrow();

    final MetadatosDocumentoGesdocDto metadatosDocumentoGesdocDto = new MetadatosDocumentoGesdocDto();

    metadatosDocumentoGesdocDto.setEstadoElaboracion(
        EnumeracionEstadoElaboracion.fromValue(expedienteDocumentoDto.getLdvMaestraDtoByIdEstElaLdv().getCodLdvMae()));
    metadatosDocumentoGesdocDto.getOrganos().add(expedienteDocumentoDto.getLdvMaestraDtoByIdOrgLdv().getCodLdvMae());
    metadatosDocumentoGesdocDto.setOrigen(OrigenDocumentoGesDocEnum
        .fromValue(expedienteDocumentoDto.getLdvMaestraDtoByIdOriDocLdv().getCodLdvMae()).getValue());
    metadatosDocumentoGesdocDto.setTipoDocumental(TipoDocumental.fromValue(
        getTipoDocumentoGestorDocumentalByIdDocumentoTipo(expedienteDocumentoDto.getDocumentoTipoDto().getIdDocTipo())
            .getCodLdvMae()));
    /*
     * metadatosDocumentoGesdocDto.setTipoFirma(TipoFirma.fromValue(
     * getTipoFirmaGestorDocumentalByIdProcedimientoAndIdDocumentoTipo(
     * idProcedimiento, documentoToSaveDto.getTipoDocumento()).getCodLdvMae()));
     */
    metadatosDocumentoGesdocDto.setTipoFirma(TipoFirma.TF_06);
    metadatosDocumentoGesdocDto.setNombreNatural(expedienteDocumentoDto.getNomDoc());
    metadatosDocumentoGesdocDto.setNivelDeAcceso(NivelDeAcceso.E);
    metadatosDocumentoGesdocDto.setTipoRegistro(TipoRegistro.fromTipoAsientoRegistral(tipoAsientoRegistral));
    metadatosDocumentoGesdocDto.setNumeroAsiento(registroDto.getNumReg());
    metadatosDocumentoGesdocDto
        .setFechaAsiento(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(registroDto.getFechaReg()));
    LOG.info("Metadatos obtenido del documento {} del procedimiento {}: {}", expedienteDocumentoDto.getNomDoc(),
        idProcedimiento, metadatosDocumentoGesdocDto);
    LOG.debug(
        "End - DocumentosServiceImpl.getMetadatosDocumentoGesdocDto del documento {} del procedimiento {} con tipoAsientoRegistral={}",
        expedienteDocumentoDto.getNomDoc(), idProcedimiento, tipoAsientoRegistral);
    return metadatosDocumentoGesdocDto;
  }

  @Override
  public void enviarNotificacion(ExpedienteDto expedienteDto, BigInteger idDocumento, String codTipoEnvio,
      ProcedimientosFasesTramitesOperacionesDto pftoDto, Map<String, Object> valores) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.enviarNotificacion del documento {} del expediente {} con envio={}",
        idDocumento, expedienteDto.getCodExp(), codTipoEnvio);
    ExpedienteDocumentoDto expedienteDocumentoDto = null;
    ExpedientesPersonasDto expedientePersonaDestino = null;
    ExpedientesPersonasDto interesado = null;
    expedienteDocumentoDto = getExpedienteDocumentoByIdDocumentoIdExpediente(idDocumento, expedienteDto.getIdExp());
    if (expedienteDocumentoDto != null) {
      // Recorrer personas.
      for (ExpedientesPersonasDto expedientePersona : expedienteDto.getExpedientesPersonasDtos()) {
        // Identificar interesado y destinatario
        if (expedientePersona.getLdvMaestraDto() != null
            && expedientePersona.getLdvMaestraDto().getCodLdvMae().equals("PER-INT")) {
          // PONER VALIDACION POR SI HAY DOS INTERESADOS¿?
          interesado = expedientePersona;
        } else if (Boolean.TRUE.equals(expedientePersona.getFlgNotificar())) {
          // PONER VALIDACION POR SI HAY DOS DESTINATARIOS¿?
          expedientePersonaDestino = expedientePersona;
        }
      }
      // Si hay destinatario es porque no es el titular. Comprobar si tiene DNI o NIE
      if (expedientePersonaDestino != null) {
        for (PersonaIdentificaDto identificacion : expedientePersonaDestino.getPersonaDto()
            .getPersonasIdentificaDtos()) {
          if (Boolean.TRUE.equals(identificacion.getFlgPrincipal())
              && !identificacion.getLdvMaestraDto().getCodLdvMae().equals("DID-DNI")
              && !identificacion.getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")
              && !identificacion.getLdvMaestraDto().getCodLdvMae().equals("DID-CIF")) {
            throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_30)
                .logMessageParams(idDocumento, expedientePersonaDestino.getPersonaDto().getIdPer(),
                    expedienteDto.getIdExp(), expedienteDto.getCodExp())
                .type(SinacExceptionType.DATA);
          }
        }
        LOG.info("Se va a realizar un alta de remesa para el expediente {} - {} a la persona destinataria {}",
            expedienteDto.getIdExp(), expedienteDto.getCodExp(), expedientePersonaDestino.getPersonaDto().getIdPer());
      } else {
        // Si no hay destinatario es el interesado. Comprobar NIE
        for (PersonaIdentificaDto identificacion : interesado.getPersonaDto().getPersonasIdentificaDtos()) {
          if (Boolean.TRUE.equals(identificacion.getFlgPrincipal())
              && !identificacion.getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")) {
            // Si no tiene NIE. Comprobar si consiente notificaciones electrónicas
            if (Boolean.TRUE.equals(interesado.getFlgConsiente())) {
              throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_31)
                  .logMessageParams(idDocumento, expedienteDto.getIdExp(), expedienteDto.getCodExp())
                  .type(SinacExceptionType.DATA);
            }
          }
        }
        LOG.info("Se va a realizar un alta de remesa para el expediente {} - {} al interesado",
            expedienteDto.getIdExp(), expedienteDto.getCodExp());
      }

      DescargaDeDocumentoDto descargaDocumentoDto;
      if (expedienteDocumentoDto.getCodGd() != null && !expedienteDocumentoDto.getCodGd().isEmpty()) {
        descargaDocumentoDto = descargarDocumentoCopiaAutentica(idDocumento);
        if (descargaDocumentoDto.getCsv() != null) {
          expedienteDocumentoDto.setDocCsv(descargaDocumentoDto.getCsv());
        }
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_32)
            .userMessageParams(idDocumento, expedienteDto.getIdExp()).type(SinacExceptionType.BUSINESS);
      }

      LdvMaestraDto ldvTipoEnvio = catalogosService.getCatalogoByCod(codTipoEnvio);
      Date fechaSolicitud = new Date();
      ResultadoAltaRemesaEnviosDto resultadoAltaRemesaDto = notificaConnector.altaRemesaEnvios(expedienteDto,
          descargaDocumentoDto, ldvTipoEnvio, valores, expedientePersonaDestino, interesado, idDocumento);
      // Código a implementar cuando se incluya la solución del código de
      // referenciaEmisor
      /*
       * ExpedienteNotificacionesDto expedienteNotificacionesDto = new
       * ExpedienteNotificacionesDto();
       * expedienteNotificacionesDto.setFechaSolicitud(fechaSolicitud);
       * expedienteNotificacionesDto.setExpedienteDocumentoDto(expedienteDocumentoDto)
       * ; expedienteNotificacionesDto.setLdvMaestraDto(ldvTipoEnvio);
       * expedienteNotificacionesDto.setProFasesTraOpe(pftoDto);
       * expedienteNotificacionesDto.setReferenciaEmisor(
       * valores.get("referenciaEmisor") != null ?
       * valores.get("referenciaEmisor").toString() : null); if
       * (resultadoAltaRemesaDto != null) { if
       * ("000".equals(resultadoAltaRemesaDto.getCodigoRespuesta())) {
       * expedienteNotificacionesDto.setEstNoti("Enviada a SUN"); } else {
       * expedienteNotificacionesDto.setEstNoti("Fallo envío a SUN"); } if
       * (resultadoAltaRemesaDto.getResultadoEnvios() != null &&
       * !resultadoAltaRemesaDto.getResultadoEnvios().isEmpty()) {
       * expedienteNotificacionesDto
       * .setIdSolSun(resultadoAltaRemesaDto.getResultadoEnvios().get(0).
       * getIdentificador()); } } else {
       * expedienteNotificacionesDto.setEstNoti("Fallo envío a SUN"); }
       * expedientesService.saveExpedienteNotificaciones(expedienteNotificacionesDto,
       * expedienteDocumentoDto); LdvMaestraDto ldvEstadoEnvio = new LdvMaestraDto();
       * if (codTipoEnvio.equals(Constantes.Comunicaciones.TIPO_NOTIFICACION)) {
       * ldvEstadoEnvio = catalogosService.getCatalogoByCod("EDOC-ENO"); } else if
       * (codTipoEnvio.equals(Constantes.Comunicaciones.TIPO_COMUNICACION)) {
       * ldvEstadoEnvio = catalogosService.getCatalogoByCod("EDOC-ECO"); }
       * expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(ldvEstadoEnvio);
       * saveExpedienteDocumento(expedienteDocumentoDto, expedienteDto);
       */
      if (resultadoAltaRemesaDto != null) {
        ExpedienteNotificacionesDto expedienteNotificacionesDto = new ExpedienteNotificacionesDto();
        if ("000".equals(resultadoAltaRemesaDto.getCodigoRespuesta())) {
          expedienteNotificacionesDto.setEstNoti("Enviada a SUN");
        } else {
          expedienteNotificacionesDto.setEstNoti("Fallo envío a SUN");
        }
        expedienteNotificacionesDto.setExpedienteDocumentoDto(expedienteDocumentoDto);
        expedienteNotificacionesDto.setFechaSolicitud(fechaSolicitud);
        if (resultadoAltaRemesaDto.getResultadoEnvios() != null
            && !resultadoAltaRemesaDto.getResultadoEnvios().isEmpty()) {
          expedienteNotificacionesDto
              .setIdSolSun(resultadoAltaRemesaDto.getResultadoEnvios().get(0).getIdentificador());
        }
        expedienteNotificacionesDto.setReferenciaEmisor(
            valores.get("referenciaEmisor") != null ? valores.get("referenciaEmisor").toString() : null);
        expedienteNotificacionesDto.setLdvMaestraDto(ldvTipoEnvio);
        expedienteNotificacionesDto.setProFasesTraOpe(pftoDto);
        expedientesService.saveExpedienteNotificaciones(expedienteNotificacionesDto, expedienteDocumentoDto);
        LdvMaestraDto ldvEstadoEnvio = new LdvMaestraDto();
        if (codTipoEnvio.equals(Constantes.Comunicaciones.TIPO_NOTIFICACION)) {
          ldvEstadoEnvio = catalogosService.getCatalogoByCod("EDOC-ENO");
        } else if (codTipoEnvio.equals(Constantes.Comunicaciones.TIPO_COMUNICACION)) {
          ldvEstadoEnvio = catalogosService.getCatalogoByCod("EDOC-ECO");
        }
        expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(ldvEstadoEnvio);
        saveExpedienteDocumento(expedienteDocumentoDto, expedienteDto);
      } else {
        throw new SinacException(SinacExceptionMessageType.MESSAGE_27);
      }
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_29).logMessageParams(idDocumento);
    }

    LOG.debug("End - DocumentosServiceImpl.enviarNotificacion del documento {} del expediente {} con envio={}",
        idDocumento, expedienteDto.getCodExp(), codTipoEnvio);
  }

  public int validarRechazarDoc(BigInteger idDocExp, Integer operacion) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.validarRechazarDoc la operación {} del documento {}", operacion, idDocExp);

    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao.findExpedienteDocumentoById(idDocExp);
    if (expedienteDocumentoEntity == null) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_1).logMessageParams(idDocExp);
    }
    LdvMaestraEntity ldvMaestraEntity = null;
    if (operacion == 1) {
      // validamos
      ldvMaestraEntity = setLdvEstDocPorCod("EDOC-VAL", expedienteDocumentoEntity,
          "No se puede validar un documento validado");

    } else if (operacion == 0) {
      // rechazamos
      ldvMaestraEntity = setLdvEstDocPorCod("EDOC-INC", expedienteDocumentoEntity,
          "No se puede rechazar un documento rechazado");

    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_2).logMessageParams(operacion);
    }

    expedienteDocumentoEntity.setLdvMaestraEntityByIdEstDocLdv(ldvMaestraEntity);
    expedienteDocumentoDao.save(expedienteDocumentoEntity);
    LOG.debug("End - DocumentosServiceImpl.validarRechazarDoc la operación {} del documento {}", operacion, idDocExp);
    return 1;
  }

  private LdvMaestraEntity setLdvEstDocPorCod(String codLdv, ExpedienteDocumentoEntity expedienteDocumentoEntity,
      String mensajeError) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.setLdvEstDocPorCod codLdv={} al documento {}", codLdv,
        expedienteDocumentoEntity.getNomDoc());
    if (StringUtils.isNotEmpty(expedienteDocumentoEntity.getLdvMaestraEntityByIdEstDocLdv().getCodLdvMae())
        && StringUtils.equals(codLdv, expedienteDocumentoEntity.getLdvMaestraEntityByIdEstDocLdv().getCodLdvMae())) {
      throw new SinacException(SinacExceptionMessageType.CUSTOM_MESSAGE).logMessageParams(mensajeError);
    }

    if (ObjectUtils.allNotNull(ldvMaestraDao.findByCodigo(codLdv))) {
      LOG.debug("End - DocumentosServiceImpl.setLdvEstDocPorCod codLdv={} al documento {}", codLdv,
          expedienteDocumentoEntity.getNomDoc());
      return ldvMaestraDao.findByCodigo(codLdv);
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_3).logMessageParams(codLdv);
    }
  }

  @Override
  public boolean cambiarFormatoDocumento(ExpedienteDocumentoDto expDoc, String formatoOriginal, String formatoObjetivo)
      throws SinacException {

    LOG.debug(
        "Init - ExpedienteDocumentoServiceImpl.cambiarFormatoDocumento el documento {} convertir el formado {} a {}",
        expDoc.getNomDoc(), formatoOriginal, formatoObjetivo);

    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao
        .findExpedienteDocumentoById(expDoc.getIdExpDoc());
    DataSource dataSource = nfsManager.getDataSource(expedienteDocumentoEntity.getNomDoc(),
        expedienteDocumentoEntity.getNfsRuta());
    /*
     * validacion para extension odt
     */
    if (expedienteDocumentoEntity.getNomDoc().contains(".")) {
      String extension = expedienteDocumentoEntity.getNomDoc().substring(
          expedienteDocumentoEntity.getNomDoc().lastIndexOf(".") + 1, expedienteDocumentoEntity.getNomDoc().length());
      if (!formatoObjetivo.equals(extension) && formatoOriginal.equalsIgnoreCase(extension)) {

        expedienteDocumentoEntity.setNomDoc(
            expedienteDocumentoEntity.getNomDoc().substring(0, expedienteDocumentoEntity.getNomDoc().lastIndexOf("."))
                + "." + formatoObjetivo);
        if (dataSource == null) {
          throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_10)
              .logMessageParams(expedienteDocumentoEntity.getNomDoc(), expedienteDocumentoEntity.getNfsRuta())
              .type(SinacExceptionType.DATA);
        }
        fcu.transformarDocumento(formatoOriginal, formatoObjetivo, expedienteDocumentoEntity.getNomDoc(),
            expedienteDocumentoEntity.getNfsRuta(), dataSource);
        expedienteDocumentoEntity = expedienteDocumentoDao.save(expedienteDocumentoEntity);
        expDoc.copy(expedienteDocuemntoMapper.toDto(expedienteDocumentoEntity));
        LOG.debug(
            "End - ExpedienteDocumentoServiceImpl.cambiarFormatoDocumento el documento {} convertir el formado {} a {}",
            expDoc.getNomDoc(), formatoOriginal, formatoObjetivo);
        return true;
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_11).logMessageParams(expDoc.getNomDoc())
            .type(SinacExceptionType.VALIDATION);
      }
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_12).logMessageParams(expDoc.getNomDoc())
          .type(SinacExceptionType.VALIDATION);
    }
  }

  @Override
  public DocumentoTipoDto getDocumentoTipoEntityByCod(String cod) {
    return documentoTipoMapper.toDto(documentoTipoDao.recuperarTipoDocPorCod(cod));
  }

  @Override
  public LdvMaestraDto getEstadoDocumentoPorProcedimientoYTipo(Short idProcedimiento, Short idDocumentoTipo)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.getEstadoDocumentoPorProcedimientoYTipo - idProcedimiento={}, idDocumentoTipo={}",
        idProcedimiento, idDocumentoTipo);

    try {
      // Obtener ID del estado desde la configuración procedimiento-documento
      int idEstadoDoc = procedimientosDocumentosTipoDao.getEstadoIniDoc(idProcedimiento, idDocumentoTipo);

      // Obtener entidad del catálogo
      Optional<LdvMaestraEntity> estadoEntity = ldvMaestraDao.findById(idEstadoDoc);

      if (estadoEntity.isEmpty()) {
        LOG.warn("No se encontró estado documento con ID={} para procedimiento={} y tipoDocumento={}", idEstadoDoc,
            idProcedimiento, idDocumentoTipo);
        return null;
      }

      LdvMaestraDto estadoDto = ldvMaestraMapper.toDto(estadoEntity.get());
      LOG.debug("End - DocumentosServiceImpl.getEstadoDocumentoPorProcedimientoYTipo - estado={}",
          estadoDto.getCodLdvMae());
      return estadoDto;

    } catch (Exception e) {
      LOG.error("Error al obtener estado documento para procedimiento={} y tipoDocumento={}: {}", idProcedimiento,
          idDocumentoTipo, e.getMessage(), e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_ERROR_OBTENER_ESTADO_DOCUMENTO)
          .logMessageParams(idProcedimiento, idDocumentoTipo, e.getMessage()).type(SinacExceptionType.DATA);
    }
  }

  /**
   * Transforma una lista de archivos adjuntos en una lista de objetos
   * DocumentoToSaveDto.
   * 
   * @param documentosEntrada los documentos de entrada
   * @return la lista de objetos DocumentoToSaveDto
   */
  @Override
  public LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSave(SolicitudDto solicitud) {
    LOG.debug("Init - ExpedienteDocumentoServiceImpl.transformMultipartToDocumentoToSave ");
    LinkedList<DocumentoToSaveDto> documentoToSaveDtoList = new LinkedList<>();
    DocumentosEntradaDto documentosEntrada = solicitud.getDocumentosEntrada();
    SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
    if (documentosEntrada != null) {
      for (ArchivoAdjuntoDto doc : documentosEntrada.getAdjunto()) {
        DocumentoToSaveDto docSaveDto = new DocumentoToSaveDto();
        String nombreArchivo = getNombreArchivo(doc);
        docSaveDto.setNombre(nombreArchivo);

        byte[] contenido = getContenido(doc);
        docSaveDto.setContenido(contenido);

        short tipoDocumento = getShortValue(doc.getIdDocTipo());
        docSaveDto.setTipoDocumento(tipoDocumento);

        int estadoElaboracion = getIntValue(doc.getIdEstadoElaboracion());
        docSaveDto.setEstadoElaboracion(estadoElaboracion);

        if (documentosEntrada.getOrgano() != null) {
          int organo = getIntValue(documentosEntrada.getOrgano());
          docSaveDto.setOrgano(organo);
        } else {
          int organo = getIntValue(doc.getOrgano());
          docSaveDto.setOrgano(organo);
        }
        if (documentosEntrada.getIdOrigen() != null) {
          int origen = getIntValue(documentosEntrada.getIdOrigen());
          docSaveDto.setOrigen(origen);
        } else {
          int origen = getIntValue(doc.getIdOrigen());
          docSaveDto.setOrigen(origen);
        }

        docSaveDto.setFechaRegistro(solicitud.getRegistroDtos().get(0).getFechaReg());
        docSaveDto.setFechaEntrada(solicitud.getRegistroDtos().get(0).getFechaReg());

        String numeroRegistro = documentosEntrada.getNumeroRegistro();
        docSaveDto.setNumeroRegistro(numeroRegistro);

        docSaveDto.setGenerarRegistro(true);

        documentoToSaveDtoList.add(docSaveDto);
      }
    }
    LOG.debug("End - ExpedienteDocumentoServiceImpl.transformMultipartToDocumentoToSave ");
    return documentoToSaveDtoList;
  }

  @Override
  public LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSaveExp(DocumentosEntradaDto documentosEntrada) {
    LOG.debug("Init - ExpedienteDocumentoServiceImpl.transformMultipartToDocumentoToSave ");
    LinkedList<DocumentoToSaveDto> documentoToSaveDtoList = new LinkedList<>();
    SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
    if (documentosEntrada != null) {
      for (ArchivoAdjuntoDto doc : documentosEntrada.getAdjunto()) {
        DocumentoToSaveDto docSaveDto = new DocumentoToSaveDto();
        String nombreArchivo = getNombreArchivo(doc);
        docSaveDto.setNombre(nombreArchivo);

        byte[] contenido = getContenido(doc);
        docSaveDto.setContenido(contenido);

        short tipoDocumento = getShortValue(doc.getIdDocTipo());
        docSaveDto.setTipoDocumento(tipoDocumento);

        int estadoElaboracion = getIntValue(doc.getIdEstadoElaboracion());
        docSaveDto.setEstadoElaboracion(estadoElaboracion);

        if (documentosEntrada.getOrgano() != null) {
          int organo = getIntValue(documentosEntrada.getOrgano());
          docSaveDto.setOrgano(organo);
        } else {
          int organo = getIntValue(doc.getOrgano());
          docSaveDto.setOrgano(organo);
        }
        if (documentosEntrada.getIdOrigen() != null) {
          int origen = getIntValue(documentosEntrada.getIdOrigen());
          docSaveDto.setOrigen(origen);
        } else {
          int origen = getIntValue(doc.getIdOrigen());
          docSaveDto.setOrigen(origen);
        }

        Date fechaRegistro = getDateValue(documentosEntrada.getFechaRegistro(), formatter);
        docSaveDto.setFechaRegistro(fechaRegistro);
        docSaveDto.setFechaEntrada(fechaRegistro);

        String numeroRegistro = documentosEntrada.getNumeroRegistro();
        docSaveDto.setNumeroRegistro(numeroRegistro);

        docSaveDto.setGenerarRegistro(true);

        documentoToSaveDtoList.add(docSaveDto);
      }
    }
    LOG.debug("End - ExpedienteDocumentoServiceImpl.transformMultipartToDocumentoToSave ");
    return documentoToSaveDtoList;
  }

  /**
   * Valida y obtiene el contenido del archivo adjunto.
   * 
   * @param doc el archivo adjunto
   * @return el contenido del archivo adjunto, o null si no existe
   */
  private byte[] getContenido(ArchivoAdjuntoDto doc) {
    try {
      return doc.getDocAdjunto() != null ? doc.getDocAdjunto().getBytes() : null;
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Valida y obtiene el nombre del archivo adjunto.
   * 
   * @param doc el archivo adjunto
   * @return el nombre del archivo adjunto, o null si no existe
   */
  private String getNombreArchivo(ArchivoAdjuntoDto doc) {
    return doc.getDocAdjunto() != null ? doc.getDocAdjunto().getOriginalFilename() : null;
  }

  /**
   * Valida y obtiene un valor short a partir de una cadena.
   * 
   * @param value la cadena
   * @return el valor short, o 0 si la cadena es nula
   */
  private short getShortValue(String value) {
    return value != null ? Short.parseShort(value) : 0;
  }

  /**
   * Valida y obtiene un valor int a partir de una cadena.
   * 
   * @param value la cadena
   * @return el valor int, o 0 si la cadena es nula
   */
  private int getIntValue(String value) {
    return value != null ? Integer.parseInt(value) : 0;
  }

  /**
   * Valida y obtiene una fecha a partir de una cadena y un formateador.
   * 
   * @param value     la cadena de fecha
   * @param formatter el formateador de fecha
   * @return la fecha, o null si la cadena es nula o no se puede parsear
   */
  private Date getDateValue(String value, SimpleDateFormat formatter) {
    try {
      return value != null ? formatter.parse(value) : null;
    } catch (ParseException e) {
      return null;
    }
  }

  @Override
  public ExpedienteInformeDto getExpedienteInformeById(BigInteger id) throws SinacException {
    LOG.debug("Init - ExpedienteDocumentoServiceImpl.getExpedienteInformeById del expediente {} ", id);
    ExpedienteInformeEntity expedienteInformeEntity = null;
    try {
      expedienteInformeEntity = expedienteInformeDao.getExpedienteInformeByIdExpedienteInforme(id);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_8).logMessageParams(id)
          .type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_9).logMessageParams(id);
    }
    LOG.info("El informe del expediente {} es: {}", id, expedienteInformeEntity);
    LOG.debug("End - ExpedienteDocumentoServiceImpl.getExpedienteInformeById del expediente {} ", id);
    return expedienteInformeMapper.toDto(expedienteInformeEntity);
  }

  @Override
  public byte[] cambiarFormatoDocumentoPlantilla(byte[] contenido, String formatoOriginal, String formatoObjetivo)
      throws SinacException {
    LOG.debug("Init - ExpedienteDocumentoServiceImpl.cambiarFormatoDocumentoPlantilla de formato {} a {}",
        formatoOriginal, formatoObjetivo);
    byte[] contenidoPdf = null;
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    fcu.transformarDocumentoSinCrear(formatoOriginal, formatoObjetivo, contenido, outStream);
    contenidoPdf = outStream.toByteArray();
    LOG.debug("End - ExpedienteDocumentoServiceImpl.cambiarFormatoDocumentoPlantilla de formato {} a {}",
        formatoOriginal, formatoObjetivo);
    return contenidoPdf;
  }

  @Override
  public boolean copyArchivoFtpNFS(String nombreArchivo, byte[] contenido, String ruta) {
    LOG.debug("Init - DocumentosServiceImpl.copyArchivoFtpNFS del archivo {} con rutaNfs = {}", nombreArchivo, ruta);
    try {
      final DataSource dataSource = nfsManager.getDataSource(nombreArchivo, contenido);
      nfsManager.getContentRepository(ruta).save(dataSource);
      LOG.debug("End - DocumentosServiceImpl.copyArchivoFtpNFS del archivo {} con rutaNfs = {}", nombreArchivo, ruta);
      return true;
    } catch (final BeansException | ContentRepositoryException exception) {
      LOG.error(String.format("DocumentosServiceImpl.copyArchivoFtpNFS - Error: %s",
          Literal.EL_DOCUMENTO + nombreArchivo + LITERAL_NO_COPIADO), exception);
    }
    return false;
  }

  @Override
  public boolean borrarArchivoFtpNFS(String nombreArchivo, String ruta) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.borrarArchivoFtpNFS el archivo {} con ruta {}", nombreArchivo, ruta);
    try {
      if (nfsManager.exists(nombreArchivo, ruta)) {
        final DataSource dataSource = nfsManager.getDataSource(nombreArchivo, ruta);
        nfsManager.getContentRepository(ruta).delete(dataSource);
        LOG.debug("End - DocumentosServiceImpl.borrarArchivoFtpNFS el archivo {} con ruta {}", nombreArchivo, ruta);
        return true;
      }
    } catch (final ContentRepositoryException contentRepositoryException) {
      throw new SinacException(contentRepositoryException, SinacExceptionMessageType.MESSAGE_144)
          .logMessageParams(nombreArchivo);
    }
    return false;
  }

  @Override
  public ExpedienteInformeDgpDto getExpedienteInformeDgpByCodExpediente(String codExpediente) {
    LOG.debug("Init - DocumentosServiceImpl.getExpedienteInformeDgpByCodExpediente del codExpediente={}",
        codExpediente);
    ExpedienteInformeDgpEntity expedienteInformeDgpEntity = expedienteInformeDgpDao
        .getExpedienteInformeDgpByCodExpedienteTipoInforme(codExpediente, "TINF-DGP");
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDgpEntity.getExpedienteInformeEntity();
    ExpedienteInformeDto expedienteInformeDto = expedienteInformeMapper.toDto(expedienteInformeEntity);
    ExpedienteInformeDgpDto expedienteInformeDgpDto = expedienteInformeDgpMapper.toDto(expedienteInformeDgpEntity);
    expedienteInformeDgpDto.setExpedienteInformeDto(expedienteInformeDto);
    LOG.info("Informe dgp del expediente con codExp={} --> {}", codExpediente, expedienteInformeDgpDto);
    LOG.debug("End - DocumentosServiceImpl.getExpedienteInformeDgpByCodExpediente del codExpediente={}", codExpediente);
    return expedienteInformeDgpDto;
  }

  @Override
  public List<ExpedienteInformeDgpTramiteDto> getExpedienteInformesDgpTramitesByIdExpedienteInformeDgp(
      BigInteger idExpedienteInformeDgp) {
    LOG.debug(
        "Init - DocumentosServiceImpl.getExpedienteInformesDgpTramitesByExpedienteInformeDgp del idExpInformeDgp={}",
        idExpedienteInformeDgp);
    List<ExpedienteInformeDgpTramiteEntity> expedienteInformeDgpTramiteEntities = expedienteInformeDgpTramitesDao
        .getExpedienteInformesDgpTramitesByIdExpedienteInformeDgp(idExpedienteInformeDgp);
    LOG.info("Informes dgp trámites del idExpedienteInformeDgp={} --> {}", idExpedienteInformeDgp,
        expedienteInformeDgpTramiteEntities);
    LOG.debug(
        "End - DocumentosServiceImpl.getExpedienteInformesDgpTramitesByExpedienteInformeDgp del idExpInformeDgp={}",
        idExpedienteInformeDgp);
    return expedienteInformeDgpTramiteEntities.stream().map(e -> expedienteInformeDgpTramitesMapper.toDto(e)).toList();
  }

  @Override
  public DocumentoToSaveDto validateDocumentoAntivirus(DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.validateDocumentoAntivirus el documento {}",
        documentoToSaveDto.getNombre());
    final String nombreDocumento = documentoToSaveDto.getNombre();
    solicitudesService.setEstadoSolicitud("Verificando documento " + nombreDocumento);
    boolean valido = antivirusConnector.validateDocumentoAntivirus(nombreDocumento, documentoToSaveDto.getContenido());
    documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidoAntivirus(valido);
    if (!valido) {
      documentoToSaveDto.setError(Literal.EL_DOCUMENTO + nombreDocumento
          + "\" no ha sido adjuntado porque el resultado del antivirus indica que el documento no es válido.");
    }
    LOG.debug("End - DocumentosServiceImpl.validateDocumentoAntivirus el documento {}", documentoToSaveDto.getNombre());
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto validateDocumento(DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.validateDocumentos del documento {}", documentoToSaveDto.getNombre());
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getValidoAntivirus())) {
      boolean validado = false;
      solicitudesService.setEstadoSolicitud("Validando documento " + documentoToSaveDto.getNombre());
      if (validarTamanyoDocumento(documentoToSaveDto) && validarExtensionDocumento(documentoToSaveDto)
          && validarNombre(documentoToSaveDto)) {
        validado = validarCamposDocumento(documentoToSaveDto);
      }

      documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidado(validado);
    }
    LOG.debug("End - DocumentosServiceImpl.validateDocumentos del documento {}", documentoToSaveDto.getNombre());
    return documentoToSaveDto;
  }

  private boolean validarNombre(DocumentoToSaveDto documentoToSaveDto) {
    String nombreCompleto = documentoToSaveDto.getNombre();
    if (nombreCompleto != null && nombreCompleto.contains(".")) {
      String nombreSinExtension = nombreCompleto.substring(0, nombreCompleto.lastIndexOf('.'));
      if (nombreSinExtension.length() > 70) {
        // Recortar el nombre a 70 caracteres
        String nombreRecortado = nombreSinExtension.substring(0, 70);
        String extension = nombreCompleto.substring(nombreCompleto.lastIndexOf('.'));
        documentoToSaveDto.setNombre(nombreRecortado + extension);
        LOG.debug("El nombre del archivo excedía los 70 caracteres. Se recortó a: {}", documentoToSaveDto.getNombre());
      }
    }
    return true; // Siempre retorna true porque no es un criterio de validación fallido
  }

  @Override
  public DocumentoToSaveDto generateRegistroDocumentoV2(final TipoRegistroRegageEnum tipoRegistro,
      DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.generateRegistroDocumentoV2 del documento {} para el tipo de registro {}",
        documentoToSaveDto.getNombre(), tipoRegistro);

    final List<DocumentoToSaveDto> documentoToSaveDtoListToGenerateRegistro = new ArrayList<>();
    final List<AnexoTypeV3Dto> documentosAnexos = new ArrayList<>();

    try {
      if (Boolean.TRUE.equals(documentoToSaveDto.getGenerarRegistro())
          && documentoToSaveDto.getDocumentoFlagsToSaveDto().getFirmado()) {
        documentoToSaveDtoListToGenerateRegistro.add(documentoToSaveDto);

        final AnexoTypeV3Dto documentoAnexo = new AnexoTypeV3Dto();
        documentoAnexo.setNombre(documentoToSaveDto.getNombre());
        documentoAnexo.setHash(Utilidades.getHashForFile(documentoToSaveDto.getContenido()));
        documentoAnexo.setTipoDocumento(
            getTipoDocumentoAnexoRegageByIdDocumentoTipo(documentoToSaveDto.getTipoDocumento()).getNomLdvMae());
        documentosAnexos.add(documentoAnexo);
      }

      if (!CollectionUtils.isEmpty(documentosAnexos)) {
        solicitudesService.setEstadoSolicitud("Registrando documento " + documentoToSaveDto.getNombre());
        final ResultadoRegistroTypeV3Dto resultadoRegistroTypeV3Dto = regageConnector
            .generateRegistroDocumentos(tipoRegistro, documentosAnexos);

        if (resultadoRegistroTypeV3Dto != null
            && TipoRespuestaRegageEnum.OK.equals(resultadoRegistroTypeV3Dto.getTipoRespuesta())) {
          setRegistroEntradaSalida(tipoRegistro, documentoToSaveDto);
          setRegistroManualAndRegistroRegage(resultadoRegistroTypeV3Dto.getNuRegistro(),
              resultadoRegistroTypeV3Dto.getFechaHoraRegistro(), documentoToSaveDto);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_26)
              .logMessageParams(documentoToSaveDto.getNombre());
        }
      }
    } catch (Exception e) {
      LOG.error(String.format("DocumentosServiceImpl.generateRegistroDocumentoV2 - Error: %s", e.getMessage()), e);
      documentoToSaveDto.setError(Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre()
          + "\" no ha sido adjuntado porque ha habido un problema en el registro del documento.");
      LdvMaestraDto ldvMaestraDto = ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-ERE"));
      documentoToSaveDto.setEstadoDocLdvMae(ldvMaestraDto);
    }
    LOG.debug("End - DocumentosServiceImpl.generateRegistroDocumentoV2 del documento {} para el tipo de registro {}",
        documentoToSaveDto.getNombre(), tipoRegistro);
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto signDocumento(DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.signDocumentos del documento {}", documentoToSaveDto.getNombre());
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getValidado())
        && !Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getEsDocumentoSede())) {
      final String nombreDocumento = documentoToSaveDto.getNombre();
      solicitudesService.setEstadoSolicitud("Firmando documento " + nombreDocumento);
      try {
        DataSource dataSource = nfsManager.getDataSource(nombreDocumento, documentoToSaveDto.getRutaNFS());
        if (dataSource == null) {
          dataSource = nfsManager.getDataSource(nombreDocumento, documentoToSaveDto.getContenido());
        }
        final SignDocumentResponseDto signDocumentResponseDto = clienteFirmaServidorConnector
            .signDocumento(nombreDocumento, dataSource);
        if (signDocumentResponseDto.getSignedData() == null) {
          LOG.info("signDocumento:  Se reenviará el documento original por falta de SignedData.", nombreDocumento);

          documentoToSaveDto.setContenidoFirmado(new DataHandler(dataSource));
          documentoToSaveDto.getDocumentoFlagsToSaveDto().setFirmado(true);
        } else if (checkSignDocumentResponseDto(signDocumentResponseDto)) {
          documentoToSaveDto.setContenidoFirmado(signDocumentResponseDto.getSignedData());
          documentoToSaveDto.getDocumentoFlagsToSaveDto().setFirmado(true);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_27).logMessageParams(nombreDocumento);
        }
      } catch (Exception e) {
        LOG.error(String.format("DocumentosServiceImpl.signDocumento - Error: %s", e.getMessage()), e);
        documentoToSaveDto.setError(Literal.EL_DOCUMENTO + nombreDocumento
            + "\" no ha sido firmado porque ha habido un problema en la firma del documento.");
        LdvMaestraDto ldvMaestraDto = ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-EFI"));
        documentoToSaveDto.setEstadoDocLdvMae(ldvMaestraDto);
      }

    }
    LOG.debug("End - DocumentosServiceImpl.signDocumentos del documento {}", documentoToSaveDto.getNombre());
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto saveDocumentoGestorDocumental(final TipoRegistroRegageEnum tipoAsientoRegistral,
      final String identificadorExpedienteGD, final short idProcedimiento, DocumentoToSaveDto documentoToSaveDto)
      throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.saveDocumentosGestorDocumental el documento {} del procedimiento {} con identificadorExpedienteGD {} y tipoAsientoRegistral={}",
        documentoToSaveDto.getNombre(), idProcedimiento, identificadorExpedienteGD, tipoAsientoRegistral);
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getFirmado())
        && Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getRegistrado())) {
      String nombreDocumento = null;
      try {
        nombreDocumento = documentoToSaveDto.getNombre();
        MetadatosDocumentoGesdocDto metadatosDocumentoGesdocDto = getMetadatosDocumentoGesdocDto(tipoAsientoRegistral,
            idProcedimiento, nombreDocumento, documentoToSaveDto);
        solicitudesService.setEstadoSolicitud("Guardando documento " + documentoToSaveDto.getNombre());
        final String identificadorDocumentoGD = gestorDocumentalConnector.capturarDocumento(
            documentoToSaveDto.getContenidoFirmado(), identificadorExpedienteGD, metadatosDocumentoGesdocDto);

        if (StringUtils.isNotEmpty(identificadorDocumentoGD)) {
          documentoToSaveDto.setIdentificadorGD(identificadorDocumentoGD);
          documentoToSaveDto.getDocumentoFlagsToSaveDto().setGuardadoGestorDocumental(true);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_MESSAGE_28).logMessageParams(nombreDocumento);
        }
      } catch (Exception e) {
        LOG.error(String.format("DocumentosServiceImpl.saveDocumentoGestorDocumental - Error: %s", e.getMessage()), e);
        documentoToSaveDto.setError(Literal.EL_DOCUMENTO + nombreDocumento
            + "\" no ha sido guardado en el Gestor Documental porque ha habido un error durante el proceso de "
            + "guardado del documento.");
        LdvMaestraDto ldvMaestraDto = ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-EGD"));
        documentoToSaveDto.setEstadoDocLdvMae(ldvMaestraDto);
      }
      LOG.info("El documento {} se ha guardado correctamente en el Gestor Documental", documentoToSaveDto.getNombre());
    } else {
      LOG.info("El documento {} no se puede guardar en el Gestor Documental", documentoToSaveDto.getNombre());
    }
    LOG.debug(
        "End - DocumentosServiceImpl.saveDocumentosGestorDocumental el documento {} del procedimiento {} con identificadorExpedienteGD {} y tipoAsientoRegistral={}",
        documentoToSaveDto.getNombre(), idProcedimiento, identificadorExpedienteGD, tipoAsientoRegistral);
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto copyDocumentoNFS(final String codExpediente, final String codProcedimiento,
      Date fechaEfectos, DocumentoToSaveDto documentoToSaveDto) throws SinacException {
    LOG.debug(
        "Init - DocumentosServiceImpl.copyDocumentosNFS el documento {} del expediente {} con la fechaEfectos={} y procedimiento={}",
        documentoToSaveDto.getNombre(), codExpediente, fechaEfectos, codProcedimiento);
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getValidado())
        && Boolean.FALSE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getGuardadoGestorDocumental())) {
      try {
        solicitudesService.setEstadoSolicitud("Copiando documento " + documentoToSaveDto.getNombre());
        final String nfsPathDocument = nfsManager.getNFSPathForDocument(codExpediente, codProcedimiento, fechaEfectos);
        DataSource dataSource = null;
        if (nfsManager.exists(documentoToSaveDto.getNombre(), documentoToSaveDto.getRutaNFS())) {
          dataSource = nfsManager.getDataSource(documentoToSaveDto.getNombre(), documentoToSaveDto.getRutaNFS());
        } else if (documentoToSaveDto.getContenidoFirmado() != null) {
          dataSource = nfsManager.getDataSource(documentoToSaveDto.getNombre(),
              documentoToSaveDto.getContenidoFirmado().getInputStream().readAllBytes());
        } else {
          dataSource = nfsManager.getDataSource(documentoToSaveDto.getNombre(), documentoToSaveDto.getContenido());
        }
        nfsManager.getContentRepository(nfsPathDocument).save(dataSource);
        documentoToSaveDto.setRutaNFS(nfsPathDocument.concat("\\"));
        documentoToSaveDto.getDocumentoFlagsToSaveDto().setCopiadoNFS(true);
      } catch (final IOException | BeansException | ContentRepositoryException contentRepositoryException) {
        LOG.error(
            String.format("DocumentosServiceImpl.copyDocumentosNFS - Error: %s",
                Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre() + LITERAL_NO_COPIADO),
            contentRepositoryException);
      }
      LOG.info("Se ha guardado en el nfs el documento {}: {}", documentoToSaveDto.getNombre(),
          documentoToSaveDto.getRutaNFS());
    } else {
      documentoToSaveDto.setRutaNFS(null);
      LOG.info("No se ha guardado en el nfs el documento {}", documentoToSaveDto.getNombre());
    }
    LOG.debug(
        "End - DocumentosServiceImpl.copyDocumentosNFS el documento {} del expediente {} con la fechaEfectos={} y procedimiento={}",
        documentoToSaveDto.getNombre(), codExpediente, fechaEfectos, codProcedimiento);
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto saveDocumentoV2(BigInteger idExpediente, DocumentoToSaveDto documentoToSaveDto,
      final boolean isCreateExpediente) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.saveDocumentos el documento {} del expediente {}",
        documentoToSaveDto.getIdExpDoc(), idExpediente);
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getGuardadoGestorDocumental())
        || Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getCopiadoNFS())) {
      try {
        ExpedienteDocumentoEntity expedienteDocumentoEntity = new ExpedienteDocumentoEntity();
        if (documentoToSaveDto.getIdExpDoc() != null) {
          expedienteDocumentoEntity.setIdExpDoc(documentoToSaveDto.getIdExpDoc());
          expedienteDocumentoEntity.setFlgActivo(true);
        }
        ExpedienteEntity expedienteEntity = expedienteDao.findById(idExpediente).orElseThrow();
        expedienteDocumentoEntity.setExpedienteEntity(expedienteEntity);
        expedienteDocumentoEntity.setVersion(expedienteEntity.getVersion());
        expedienteDocumentoEntity
            .setDocumentoTipoEntity(documentoTipoDao.recuperarTipoDocPorId(documentoToSaveDto.getTipoDocumento()));
        expedienteDocumentoEntity
            .setLdvMaestraEntityByIdOrgLdv(ldvMaestraDao.findById(documentoToSaveDto.getOrgano()).orElseThrow());
        LdvMaestraDto ldvMaestraDto = catalogosService.getCatalogoById(procedimientosDocumentosTipoDao.getEstadoIniDoc(
            expedienteEntity.getProcedimientoEntity().getIdPro(), documentoToSaveDto.getTipoDocumento()));
        expedienteDocumentoEntity.setLdvMaestraEntityByIdEstDocLdv(documentoToSaveDto.getEstadoDocLdvMae() != null
            ? ldvMaestraDao.findById(documentoToSaveDto.getEstadoDocLdvMae().getIdLdvMae()).orElseThrow()
            : ldvMaestraDao.findById(ldvMaestraDto.getIdLdvMae()).orElseThrow());
        expedienteDocumentoEntity.setLdvMaestraEntityByIdEstElaLdv(
            ldvMaestraDao.findById(documentoToSaveDto.getEstadoElaboracion()).orElseThrow());
        expedienteDocumentoEntity
            .setLdvMaestraEntityByIdOriDocLdv(ldvMaestraDao.findById(documentoToSaveDto.getOrigen()).orElseThrow());
        expedienteDocumentoEntity.setCodGd(documentoToSaveDto.getIdentificadorGD());
        if (documentoToSaveDto.getIdentificadorGD() == null) {
          expedienteDocumentoEntity.setNfsRuta(documentoToSaveDto.getRutaNFS());
        }
        expedienteDocumentoEntity.setFechaEntrada(documentoToSaveDto.getFechaEntrada());
        expedienteDocumentoEntity.setNomDoc(documentoToSaveDto.getNombre());
        setFlagActivoFalseAndSaveExpedienteDocumentoHistorico(expedienteDocumentoEntity,
            documentoToSaveDto.getTipoDocumento());
        ExpedienteDocumentoDto expedienteDocumentoDto = expedienteDocumentoWithExpedienteMapper
            .toDto(expedienteDocumentoEntity);
        expedienteDocumentoEntity = expedienteDocumentoDao.save(expedienteDocumentoEntity);
        documentoToSaveDto.setIdExpedienteDocumento(expedienteDocumentoEntity.getIdExpDoc());
        expedienteDocumentoDto.setIdExpDoc(expedienteDocumentoEntity.getIdExpDoc());

        saveRegistrosOfDocumentoToSaveDtoForExpedienteDocumentoDto(expedienteDocumentoDto, documentoToSaveDto,
            isCreateExpediente, expedienteEntity.getSolicitudEntity().getIdSol());
        documentoToSaveDto.getDocumentoFlagsToSaveDto().setInsertadoBaseDatos(true);
      } catch (final SinacException sinacException) {
        documentoToSaveDto.setError(Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre()
            + "\" no ha sido insertado en Base de Datos porque ha habido un error durante el proceso de "
            + "inserción del documento.");

        LOG.error(String.format("DocumentosServiceImpl.saveDocumentos - Error: %s", documentoToSaveDto.getError()),
            sinacException);
      }
    }

    LOG.debug("End - DocumentosServiceImpl.saveDocumentos el documento {} del expediente {}",
        documentoToSaveDto.getIdExpDoc(), idExpediente);
    return documentoToSaveDto;
  }

  private void setFlagActivoFalseAndSaveExpedienteDocumentoHistorico(
      ExpedienteDocumentoEntity expedienteDocumentoEntity, Short tipoDocumento) {
    LOG.debug(
        "Init - DocumentosServiceImpl.setFlagActivoFalseAndSaveExpedienteDocumentoHistorico el documento {} con tipoDocumento={}",
        expedienteDocumentoEntity.getNomDoc(), tipoDocumento);
    BigInteger idExpdoc = expedienteDocumentoEntity.getIdExpDoc();
    List<ExpedienteDocumentoEntity> listaExpedienDocDesactivar = expedienteDocumentoEntity.getExpedienteEntity()
        .getExpedienteDocumentoEntities().stream().filter(ExpedienteDocumentoEntity::isFlgActivo)
        .filter(expdoc -> !expdoc.getIdExpDoc().equals(idExpdoc))
        .filter(expdoc -> expdoc.getDocumentoTipoEntity().getIdDocTipo() == tipoDocumento).toList();
    listaExpedienDocDesactivar = listaExpedienDocDesactivar.stream()
        .filter(obj -> !obj.getDocumentoTipoEntity().getCodTipo().equals("OTROS")).collect(Collectors.toList());
    LOG.info("Documentos listaExpedienDocDesactivar  {} ", listaExpedienDocDesactivar);
    for (ExpedienteDocumentoEntity expDocumentoEntity : listaExpedienDocDesactivar) {
      expDocumentoEntity.setFlgActivo(false);
      expedienteDocumentoDao.save(expDocumentoEntity);
    }
    LOG.debug(
        "End - DocumentosServiceImpl.setFlagActivoFalseAndSaveExpedienteDocumentoHistorico el documento {} con tipoDocumento={}",
        expedienteDocumentoEntity.getNomDoc(), tipoDocumento);
  }

  @Override
  public DocumentoToSaveDto saveDocumento(final ExpedienteDto expedienteDto, DocumentoToSaveDto documentoToSaveDto,
      final boolean isCreateExpediente) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.saveDocumentos el documento {} del expediente {}",
        documentoToSaveDto.getNombre(), expedienteDto.getCodExp());
    if (Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getGuardadoGestorDocumental())
        || Boolean.TRUE.equals(documentoToSaveDto.getDocumentoFlagsToSaveDto().getCopiadoNFS())) {
      try {
        ExpedienteDocumentoDto expedienteDocumentoDto = new ExpedienteDocumentoDto();
        if (documentoToSaveDto.getIdExpDoc() != null) {
          expedienteDocumentoDto.setIdExpDoc(documentoToSaveDto.getIdExpDoc());
          expedienteDocumentoDto.setFlgActivo(true);
        }
        expedienteDocumentoDto.setExpedienteDto(expedienteDto);
        expedienteDocumentoDto.setVersion(expedienteDto.getVersion());
        expedienteDocumentoDto
            .setDocumentoTipoDto(getDocumentoTipoByIdDocumentoTipo(documentoToSaveDto.getTipoDocumento()));
        expedienteDocumentoDto
            .setLdvMaestraDtoByIdOriDocLdv(catalogosService.getCatalogoById(documentoToSaveDto.getOrigen()));
        expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(
            catalogosService.getCatalogoById(procedimientosDocumentosTipoDao.getEstadoIniDoc(
                expedienteDto.getProcedimientoDto().getIdPro(), documentoToSaveDto.getTipoDocumento())));
        expedienteDocumentoDto
            .setLdvMaestraDtoByIdEstElaLdv(catalogosService.getCatalogoById(documentoToSaveDto.getEstadoElaboracion()));
        expedienteDocumentoDto
            .setLdvMaestraDtoByIdOrgLdv(catalogosService.getCatalogoById(documentoToSaveDto.getOrgano()));
        expedienteDocumentoDto.setCodGd(documentoToSaveDto.getIdentificadorGD());
        expedienteDocumentoDto.setNfsRuta(documentoToSaveDto.getRutaNFS());
        expedienteDocumentoDto.setCreadoPor(documentoToSaveDto.getCreadoPor());
        expedienteDocumentoDto.setNomDoc(documentoToSaveDto.getNombre());

        setFlagActivoToFalseAndSaveExpedienteDocumentoHistorico(expedienteDto,
            expedienteDocumentoDto.getDocumentoTipoDto());

        expedienteDocumentoDto = expedienteDocumentoWithExpedienteMapper.toDto(
            expedienteDocumentoDao.save(expedienteDocumentoWithExpedienteMapper.toEntity(expedienteDocumentoDto)));

        documentoToSaveDto.setIdExpedienteDocumento(expedienteDocumentoDto.getIdExpDoc());

        final RegistroDto registroDto = new RegistroDto();
        registroDto.setExpedienteDocumentoDto(expedienteDocumentoDto);
        if (isCreateExpediente) {
          registroDto.setSolicitudDocumentoDto(getSolicitudDocumentoByIdDocumentoTipo(
              documentoToSaveDto.getTipoDocumento(), expedienteDto.getSolicitudDto().getIdSol()));
        }
        registroDto.setLdvMaestraDto(catalogosService.getCatalogoById(documentoToSaveDto.getRegistroEntradaSalida()));
        registroDto.setNumReg(documentoToSaveDto.getNumeroRegistro());
        registroDto.setFechaReg(documentoToSaveDto.getFechaRegistro());

        registroDao.save(registroWithDocumentosMapper.toEntity(registroDto));

        documentoToSaveDto.getDocumentoFlagsToSaveDto().setInsertadoBaseDatos(true);
      } catch (final SinacException sinacException) {
        documentoToSaveDto.setError(Literal.EL_DOCUMENTO + documentoToSaveDto.getNombre()
            + "\" no ha sido insertado en Base de Datos porque ha habido un error durante el proceso de "
            + "inserción del documento.");

        LOG.error(String.format("DocumentosServiceImpl.saveDocumentos del documento %s - Error: %s",
            documentoToSaveDto.getNombre(), documentoToSaveDto.getError()), sinacException);
      }
    }
    LOG.debug("End - DocumentosServiceImpl.saveDocumentos el documento {} del expediente {}",
        documentoToSaveDto.getNombre(), expedienteDto.getCodExp());
    return documentoToSaveDto;
  }

  @Override
  public List<DataSource> obtenerTodosLosArchivos(String nfsPath) throws SinacException {
    return nfsManager.findAllFilesInDir(nfsPath);
  }

  @Override
  public DataSource obtenerArchivoByNombre(String nombre, String ruta) throws SinacException {
    return nfsManager.getDataSource(nombre, ruta);
  }

  @Override
  public List<DocumentoTipoDto> getDocumentosSolicitudObligatorios(String codPro) {
    LOG.debug("Init - DocumentosServiceImpl.getDocumentosSolicitudObligatorios del procedimiento {}", codPro);
    if (StringUtils.isNotBlank(codPro)) {
      List<DocumentoTipoEntity> listaDocumentosTipo = documentoTipoDao.getDocumentosTipoByCodProObligatorio(codPro)
          .stream().toList();
      LOG.info("Los documentos obligatorios para el procedimiento {} son: {}", codPro, listaDocumentosTipo);
      LOG.debug("End - DocumentosServiceImpl.getDocumentosSolicitudObligatorios del procedimiento {}", codPro);
      return documentoTipoMapper.toDto(listaDocumentosTipo);
    }
    return null;
  }

  @Override
  public DataHandler getContenido(final ExpedienteDocumentoDto expedienteDocumentoDto) {
    return new DataHandler(new ByteArrayDataSource(
        nfsManager.getDocumentContent(expedienteDocumentoDto.getNomDoc(), expedienteDocumentoDto.getNfsRuta()),
        "application/pdf"));
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void descargarJustificanteGeiser(AsientoDto asientoDto, UsuarioDto usuarioDto) {
    try {
      DocumentoDto justificante = geiserService.obtenerJustificante(asientoDto.getNumRegistro());
      this.guardarJustificanteGeiser(asientoDto, justificante, usuarioDto);
    } catch (SinacGeiserException e) {
      LOG.error("Error al descargar el justificante de GEISER: {}", e.getMessage());
    }
  }

  @Override
  public DocumentoDto obtenerJustificanteGeiser(AsientoDto asientoDto) {
    DescargaDeDocumentoDto documento = this
        .getArchivoByIdDocExp(asientoDto.getExpedienteDocumentoJustificante().getIdExpDoc());
    DocumentoDto doc = new DocumentoDto();
    doc.setNombre(documento.getNombreArchivo());
    doc.setContenido(documento.getFile());
    doc.setMimeType("application/pdf");

    return doc;
  }

  @Override
  public AsientoDto guardarJustificanteGeiser(AsientoDto asientoDto, DocumentoDto justificante, UsuarioDto usuarioDto) {
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
    documentoToSaveDto.setTipoDocumento(this.getDocumentoTipoEntityByCod("JUSGE").getIdDocTipo());
    documentoToSaveDto.setEstadoElaboracion(catalogosService.getCatalogoByCod("EE99").getIdLdvMae());
    documentoToSaveDto.setOrgano(catalogosService.getCatalogoByCod("ORG-MJU").getIdLdvMae());
    documentoToSaveDto.setOrigen(catalogosService.getCatalogoByCod("DOC-ADM").getIdLdvMae());
    documentoToSaveDto.setNombre(justificante.getNombre());
    documentoToSaveDto.setRutaNFS(nfsPathDocumentosSolicitudes);
    documentoToSaveDto.setContenido(justificante.getContenido());

    LinkedList<DocumentoToSaveDto> documentoToSaveDtoList = new LinkedList<>();
    documentoToSaveDtoList.add(documentoToSaveDto);
    List<DocumentoToSaveDto> docs = expedientesFacade
        .saveDocumentosEntradaExpediente(asientosService.findIdExpFromAsiento(asientoDto), documentoToSaveDtoList);

    if (docs.get(0).getIdExpedienteDocumento() != null) {
      ExpedienteDocumentoDto justificanteExpDoc = this
          .getExpedienteDocumentoByIdDocumento(docs.get(0).getIdExpedienteDocumento());
      asientoDto.setExpedienteDocumentoJustificante(justificanteExpDoc);
      asientosService.setExpDocJusticante(asientoDto, justificanteExpDoc, usuarioDto);
    }

    return asientoDto;
  }

  @Override
  public List<AsientoDto> getAsientosEnCurso() {
    return asientosService.getAsientosEnCurso();
  }

  @Override
  public AsientoDto consultarEstadoDocumentoEnviadoAGeiser(AsientoDto asientoExistente, UsuarioDto usuario) {
    LOG.debug(
        "Init - DocumentosServiceImpl.consultarEstadoDocumentoEnviadoAGeiser por el usuario={} el asientoExistente={}",
        usuario.getIdUsu(), asientoExistente.getIdAsiento());
    try {
      ResultadoConsultaDto resultado = geiserService.consultarAsiento(asientoExistente.getNumRegistro());
      if (resultado.getEstado().value().equals(asientoExistente.getEstado())) {
        // El estado en geiser no ha cambiado
        LOG.debug(String.format(
            "DocumentosServiceImpl.consultarEstadoDocumentoEnviadoAGeiser - El estado para idAsiento %d no ha cambiado (%s)",
            asientoExistente.getIdAsiento(), resultado.getEstado().value()));
        return asientoExistente;
      }
      // guardar datos de registro en ASIENTOS
      Date fechaActual = new Date();
      AsientoDto asientoActualizado = new AsientoDto();
      asientoActualizado.setNumRegistro(resultado.getNumRegistro());
      asientoActualizado.setOrgDestino(asientoExistente.getOrgDestino());
      asientoActualizado.setEstado(resultado.getEstado().value());
      asientoActualizado.setFechaEstado(fechaActual);
      if (resultado.getEstado().value().equals(EstadoAsientoEnum.ENVIADO_CONFIRMADO.value())) {
        asientoActualizado.setNotas("Nº reg oficial: " + resultado.getNumRegistroOficial());
      }
      asientoActualizado.setExpedienteDocumento(asientoExistente.getExpedienteDocumento());
      asientoActualizado
          .setProcedimientosFasesTramitesOperaciones(asientoExistente.getProcedimientosFasesTramitesOperaciones());
      asientoActualizado.setFlgActivo(true);
      asientoActualizado.setFechaIniVig(fechaActual);
      asientoActualizado.setFechaCreacion(fechaActual);
      asientoActualizado.setCreadoPor(usuario);

      asientosService.saveAsiento(asientoActualizado, usuario);

      return asientoActualizado;
    } catch (SinacGeiserException e) {
      LOG.error(String.format(
          "DocumentosServiceImpl.consultarEstadoDocumentoEnviadoAGeiser - Error al actualizar el estado para el asiento  %d",
          asientoExistente.getIdAsiento()));

      Date fechaActual = new Date();
      AsientoErrorDto error = new AsientoErrorDto();
      error.setAsiento(asientoExistente);
      error.setMensaje(e.getMessage().substring(0, Math.min(e.getMessage().length(), 500)));
      error.setFlgActivo(true);
      error.setFechaIniVig(fechaActual);
      error.setFechaCreacion(fechaActual);
      error.setCreadoPor(usuario);

      asientosService.saveAsientoError(error);
      LOG.error(
          "DocumentosServiceImpl.consultarEstadoDocumentoEnviadoAGeiser - Guardado mensaje de error en ASIENTOS_ERRORES");
    }
    LOG.debug(
        "End - DocumentosServiceImpl.consultarEstadoDocumentoEnviadoAGeiser por el usuario={} el asientoExistente={}",
        usuario.getIdUsu(), asientoExistente.getIdAsiento());

    return asientoExistente;
  }

  @Override
  public LdvMaestraEntity findIdSentidoResolucionLdvByIdExpDoc(BigInteger idExpDoc) throws SinacException {
    return expedienteDocumentoDao.findIdSentidoResolucionLdvByIdDoc(idExpDoc);
  }

  @Override
  public void saveExpedienteDocumentoInformeMde(ExpedienteDocumentoInformeMdeDto expedienteDocumentoInformeMdeDto) {
    expedienteDocumentoInformeMdeDao
        .save(expedienteDocumentoInformeMdeMapper.toEntity(expedienteDocumentoInformeMdeDto));
  }

  @Override
  public List<ExpedienteDocumentoDto> getExpedientesDocumentosMdeByIdInforme(BigInteger idExpedienteInforme)
      throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.getExpedientesDocumentosMdeByIdInforme del expInforme {}",
        idExpedienteInforme);
    try {
      List<ExpedienteDocumentoEntity> expedientesDocumentosEntities = expedienteDocumentoDao
          .getExpedientesDocumentosMdeByIdInforme(idExpedienteInforme);
      List<ExpedienteDocumentoDto> expedientesDocumentosDtos = new ArrayList<>();
      for (ExpedienteDocumentoEntity solicitudPerEntity : expedientesDocumentosEntities) {
        expedientesDocumentosDtos.add(expedienteDocumentoMapper.toDto(solicitudPerEntity));
      }
      LOG.info("Los documentoMde del informe {} son: {}", idExpedienteInforme, expedientesDocumentosDtos);
      LOG.debug("End - DocumentosServiceImpl.getExpedientesDocumentosMdeByIdInforme del expInforme {}",
          idExpedienteInforme);
      return expedientesDocumentosDtos;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_143).logMessageParams(idExpedienteInforme)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void desactivarExpedienteDocumentosMdeByIdDocumento(BigInteger idDocumento) throws SinacException {
    LOG.debug("Init - DocumentosServiceImpl.desactivarExpedienteDocumentosMdeByIdDocumento del documento {}",
        idDocumento);
    try {
      expedienteDocumentoInformeMdeDao.desactivarExpedienteDocumentosMdeByIdDocumento(idDocumento);
      LOG.info("Se ha desactivado correctamente el documento Mde {}", idDocumento);
      LOG.debug("End - DocumentosServiceImpl.desactivarExpedienteDocumentosMdeByIdDocumento del documento {}",
          idDocumento);

    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_MESSAGE_33).logMessageParams(idDocumento)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public short getIdTipoDocByCodTipo(String codTipo) throws SinacException {
    return documentoTipoDao.getIdTipoDocByCodTipo(codTipo);
  }

  @Override
  public LinkedList<DocumentoToSaveDto> obtenerTodosLosDocumentosSede(List<SolicitudDocumentoDto> listaDocs) {

    LinkedList<DocumentoToSaveDto> listDocToSaveDto = new LinkedList<>();

    if (listaDocs == null || listaDocs.isEmpty()) {
      return listDocToSaveDto;
    }

    for (SolicitudDocumentoDto solDoc : listaDocs) {

      try {
        DataSource ds = nfsManager.getDataSource(solDoc.getNomDoc(), solDoc.getNfsRuta());

        if (ds == null) {
          // Documento no obligatorio: registramos y continuamos
          LOG.warn("Documento '{}' no encontrado en NFS (ruta={}). Se ignora y continúa.", solDoc.getNomDoc(),
              solDoc.getNfsRuta());
          continue;
        }

        byte[] contenido;
        try (InputStream is = ds.getInputStream()) {
          contenido = is.readAllBytes();
        }

        DocumentoToSaveDto documentoToSaveDto = generarDocumentosExpediente(solDoc, contenido);
        listDocToSaveDto.add(documentoToSaveDto);

      } catch (IOException ioe) {
        // Error en lectura I/O: documento no imprescindible => log y continuar
        LOG.error("Error I/O leyendo documento '{}' desde NFS (ruta={}). Se ignora documento.", solDoc.getNomDoc(),
            solDoc.getNfsRuta(), ioe);
        // continue implícito al siguiente iterador
      } catch (Exception ex) {
        // Capturamos cualquier otro error inesperado para evitar romper el flujo
        LOG.error("Error inesperado procesando documento '{}' (ruta={}). Se ignora documento.", solDoc.getNomDoc(),
            solDoc.getNfsRuta(), ex);
      }
    }

    return listDocToSaveDto;
  }

  @Override
  public DocumentoToSaveDto generarDocumentosExpediente(SolicitudDocumentoDto docSol, byte[] contenido) {
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();

    documentoToSaveDto.setContenido(contenido);
    documentoToSaveDto.setNombre(docSol.getNomDoc());
    documentoToSaveDto.setRutaNFS(docSol.getNfsRuta());
    documentoToSaveDto
        .setTipoDocumento(docSol.getDocumentoTipoDto() != null ? docSol.getDocumentoTipoDto().getIdDocTipo() : null);
//    documentoToSaveDto
//        .setEstadoDocLdvMae(docSol.getDocumentoTipoDto().getProcedimientosDocumentosTipoDtos().get(0).getIdEstDocLdv());
    documentoToSaveDto.setEstadoElaboracion(
        docSol.getLdvMaestraDtoByIdEstElaLdv() != null ? docSol.getLdvMaestraDtoByIdEstElaLdv().getIdLdvMae() : null);
    documentoToSaveDto.setOrgano(
        docSol.getLdvMaestraDtoByIdOrgLdv() != null ? docSol.getLdvMaestraDtoByIdOrgLdv().getIdLdvMae() : null);
    documentoToSaveDto.setOrigen(
        docSol.getLdvMaestraDtoByIdOriDocLdv() != null ? docSol.getLdvMaestraDtoByIdOriDocLdv().getIdLdvMae() : null);
    return documentoToSaveDto;
  }

  @Override
  public ExpedienteDocumentoDto generarExpedienteDocumento(SolicitudDocumentoDto docSol, byte[] contenido) {
    DocumentoTipoDto docTipo = new DocumentoTipoDto();
    ExpedienteDocumentoDto nuevoDoc = new ExpedienteDocumentoDto();
    if (docSol.getDocumentoTipoDto() != null) {
      docTipo.setIdDocTipo(docSol.getDocumentoTipoDto().getIdDocTipo());
      nuevoDoc.setDocumentoTipoDto(docTipo);
    }

    nuevoDoc.setNomDoc(docSol.getNomDoc());
    nuevoDoc.setNfsRuta(docSol.getNfsRuta());
    nuevoDoc.setContenido(contenido);
    nuevoDoc.setLdvMaestraDtoByIdEstDocLdv(
        docSol.getDocumentoTipoDto().getProcedimientosDocumentosTipoDtos().get(0).getIdEstDocLdv());
    nuevoDoc.setLdvMaestraDtoByIdEstElaLdv(docSol.getLdvMaestraDtoByIdEstElaLdv());
    nuevoDoc.setLdvMaestraDtoByIdOrgLdv(docSol.getLdvMaestraDtoByIdOrgLdv());
    nuevoDoc.setLdvMaestraDtoByIdOriDocLdv(docSol.getLdvMaestraDtoByIdOriDocLdv());

    return nuevoDoc;
  }

  @Override
  public ExpedienteDocumentoDto getExpedienteDocumentosByAccionOperacionTramiteIdExp(
      List<ExpedienteDocumentoDto> expDocs, String codAccion, String codOpe, String codTramite, BigInteger idExp) {

    List<DocumentoTipoEntity> docTipos = documentoTipoDao.getTiposDocumentosByAccion(codAccion, codOpe, codTramite,
        idExp);

    DocumentoTipoEntity tipo = docTipos.isEmpty() ? null : docTipos.get(0);

    expDocs.sort(Comparator.comparing(ExpedienteDocumentoDto::getFechaCreacion).reversed());

    if (tipo != null) {
      for (ExpedienteDocumentoDto expDoc : expDocs) {
        if (expDoc.getDocumentoTipoDto().getCodTipo().equals(tipo.getCodTipo())) {
          return expDoc;
        }
      }
    }
    return null;

  }

  @Override
  public boolean getListDocsGeneradosPostFirma(BigInteger idExp, Short idPro, String tramite, Date fechaRecepcion) {

    return !expedienteDocumentoDao
        .getListaExpedientesDocumentosGeneradosPostFirma(idExp, tramite, idPro, fechaRecepcion).isEmpty();
  }

}