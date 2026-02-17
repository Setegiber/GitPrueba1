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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sshtools.common.logger.Log;

import es.mjusticia.milano.persistence.bo.ContentRepositoryException;
import es.mjusticia.sinac.ccse.dele.dto.RespuestaCalificacionesDto;
import es.mjusticia.sinac.ccse.dele.dto.RetornoCalificacionesDto;
import es.mjusticia.sinac.ccse.dele.exception.SinacCalificacionesException;
import es.mjusticia.sinac.ccse.dele.service.CalificacionesService;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.InsideEstadosService;
import es.mjusticia.sinac.core.business.service.ObservacionesService;
import es.mjusticia.sinac.core.business.service.PaisesService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.eis.connector.EmailConnector;
import es.mjusticia.sinac.core.model.dto.AccionDto;
import es.mjusticia.sinac.core.model.dto.ArchivoFtpDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosListasDto;
import es.mjusticia.sinac.core.model.dto.BusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.BusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.ComboDto;
import es.mjusticia.sinac.core.model.dto.ComunicacionesExternasDto;
import es.mjusticia.sinac.core.model.dto.DatosSolicitudInformeMjuDto;
import es.mjusticia.sinac.core.model.dto.DescargaDeDocumentoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosAdjuntosEmailDto;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.EnviarEmailDto;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
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
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeHistoricoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInsideDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteNotificacionesDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteSecuenciasDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPersonasDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesVinculadosDto;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.dto.InsideEstadoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.MaquinaEstadosDto;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PerCertificacionesDto;
import es.mjusticia.sinac.core.model.dto.PerFilNiesDto;
import es.mjusticia.sinac.core.model.dto.PerFiliacionesDto;
import es.mjusticia.sinac.core.model.dto.PerPadronDto;
import es.mjusticia.sinac.core.model.dto.PersonaContactoElectronicoDto;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.model.dto.PersonaRcDto;
import es.mjusticia.sinac.core.model.dto.PersonaTitulacionDto;
import es.mjusticia.sinac.core.model.dto.PersonasContactosElectronicosDto;
import es.mjusticia.sinac.core.model.dto.PersonasDomiciliosDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlazoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosPlantillasCriteriosDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.RenovacionDniDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.RolesUsuariosDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudFormularioValDto;
import es.mjusticia.sinac.core.model.dto.SolicitudesPersonasDto;
import es.mjusticia.sinac.core.model.dto.TipoDocumentoEstadoDocumentoDto;
import es.mjusticia.sinac.core.model.dto.TiposViaDto;
import es.mjusticia.sinac.core.model.dto.TitulosDto;
import es.mjusticia.sinac.core.model.dto.TramiteDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.dto.ValidacionSemaforoDto;
import es.mjusticia.sinac.core.model.entity.BoeAnunciosEntity;
import es.mjusticia.sinac.core.model.entity.BoeAnunciosListasEntity;
import es.mjusticia.sinac.core.model.entity.DocumentoTipoEntity;
import es.mjusticia.sinac.core.model.entity.EstadoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteAvisoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteBoeEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteComunicacionesExternasEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteDocumentoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteEstadoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteFirmaEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteFormularioValEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeDgpEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeDgpTramiteEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeMdeEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformesMjuFicherosDatosEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformesMjuFicherosEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteInsideEntity;
import es.mjusticia.sinac.core.model.entity.ExpedienteNotificacionesEntity;
import es.mjusticia.sinac.core.model.entity.ExpedientesPersonasEntity;
import es.mjusticia.sinac.core.model.entity.ExpedientesValidacionesSemaforoEntity;
import es.mjusticia.sinac.core.model.entity.ExpedientesVinculadosEntity;
import es.mjusticia.sinac.core.model.entity.FormularioCamposValidaEntity;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.entity.LocalidadesEntity;
import es.mjusticia.sinac.core.model.entity.MaquinaEstadosEntity;
import es.mjusticia.sinac.core.model.entity.PaisesEntity;
import es.mjusticia.sinac.core.model.entity.PerCertificacionesEntity;
import es.mjusticia.sinac.core.model.entity.PerFilNiesEntity;
import es.mjusticia.sinac.core.model.entity.PerFiliacionesEntity;
import es.mjusticia.sinac.core.model.entity.PerPadronEntity;
import es.mjusticia.sinac.core.model.entity.PersonaDomicilioEntity;
import es.mjusticia.sinac.core.model.entity.PersonaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaFamEntity;
import es.mjusticia.sinac.core.model.entity.PersonaIdentificaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaRcEntity;
import es.mjusticia.sinac.core.model.entity.PersonaTitulacionesEntity;
import es.mjusticia.sinac.core.model.entity.PersonasContactosElectronicosEntity;
import es.mjusticia.sinac.core.model.entity.PersonasDomiciliosEntity;
import es.mjusticia.sinac.core.model.entity.PeticionesPidEntity;
import es.mjusticia.sinac.core.model.entity.PlazoEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientoEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosAvisosEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosDocumentosTipoEntity;
import es.mjusticia.sinac.core.model.entity.ProvinciasEntity;
import es.mjusticia.sinac.core.model.entity.RegistroEntity;
import es.mjusticia.sinac.core.model.entity.RenovacionDniEntity;
import es.mjusticia.sinac.core.model.entity.SolicitudesPersonasEntity;
import es.mjusticia.sinac.core.model.entity.TiposViaEntity;
import es.mjusticia.sinac.core.model.entity.UsuarioEntity;
import es.mjusticia.sinac.core.model.entity.ValidacionSemaforoEntity;
import es.mjusticia.sinac.core.model.enums.CodRespuestaMjuEnum;
import es.mjusticia.sinac.core.model.enums.InsideEnviosEstadosEnum;
import es.mjusticia.sinac.core.model.enums.TipoRegistroRegageEnum;
import es.mjusticia.sinac.core.model.mapper.BoeAnunciosListasMapper;
import es.mjusticia.sinac.core.model.mapper.BoeAnunciosMapper;
import es.mjusticia.sinac.core.model.mapper.DocumentoTipoMapper;
import es.mjusticia.sinac.core.model.mapper.EstadoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteAvisoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteBoeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteComunicacionesExternasMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteDocumentoWithExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteEstadoMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteFirmaMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteFormularioValMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeDgpMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeDgpTramitesMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMapperAux;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMdeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformesMjuFicherosDatosMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInsideMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteNotificacionMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteSecuenciasMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedientesPersonasMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedientesVinculadosWithExpedientesMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.LocalidadesMapper;
import es.mjusticia.sinac.core.model.mapper.PaisesMapper;
import es.mjusticia.sinac.core.model.mapper.ParametrizacionMapper;
import es.mjusticia.sinac.core.model.mapper.PerCertificacionesMapper;
import es.mjusticia.sinac.core.model.mapper.PerFilNiesWithPersonaMapper;
import es.mjusticia.sinac.core.model.mapper.PerFiliacionesWithPersonaMapper;
import es.mjusticia.sinac.core.model.mapper.PerPadronMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaContactoElectronicoMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaDomicilioMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaMapperAux;
import es.mjusticia.sinac.core.model.mapper.PersonaRcMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaTitulacionesMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientoMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesTramitesOperacionesAccionesMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesTramitesOperacionesMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosPlantillasCriteriosMapper;
import es.mjusticia.sinac.core.model.mapper.ProvinciasMapper;
import es.mjusticia.sinac.core.model.mapper.RenovacionDniMapper;
import es.mjusticia.sinac.core.model.mapper.TiposViaMapper;
import es.mjusticia.sinac.core.model.mapper.ValidacionSemaforoMapper;
import es.mjusticia.sinac.core.persistence.BoeAnunciosDao;
import es.mjusticia.sinac.core.persistence.BoeAnunciosListasDao;
import es.mjusticia.sinac.core.persistence.DocumentoTipoDao;
import es.mjusticia.sinac.core.persistence.EstadoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteAvisoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteBoeDao;
import es.mjusticia.sinac.core.persistence.ExpedienteComunicacionesExternasDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDao;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteEstadoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteFormularioValDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDgpDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDgpTramitesDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeMdeDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformesMjuFicherosDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformesMjuFicherosDatosDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInsideDao;
import es.mjusticia.sinac.core.persistence.ExpedienteNotificacionesDao;
import es.mjusticia.sinac.core.persistence.ExpedienteSecuenciasDao;
import es.mjusticia.sinac.core.persistence.ExpedientesPersonasDao;
import es.mjusticia.sinac.core.persistence.ExpedientesValidacionesSemaforoDao;
import es.mjusticia.sinac.core.persistence.ExpedientesVinculadosDao;
import es.mjusticia.sinac.core.persistence.FormularioCamposValidaDao;
import es.mjusticia.sinac.core.persistence.LdvMaestraDao;
import es.mjusticia.sinac.core.persistence.LocalidadesDao;
import es.mjusticia.sinac.core.persistence.MotivoSolPeriodoExigDao;
import es.mjusticia.sinac.core.persistence.PaisesDao;
import es.mjusticia.sinac.core.persistence.ParametrizacionDao;
import es.mjusticia.sinac.core.persistence.PerCertificacionesDao;
import es.mjusticia.sinac.core.persistence.PerFilNiesDao;
import es.mjusticia.sinac.core.persistence.PerFiliacionesDao;
import es.mjusticia.sinac.core.persistence.PerPadronDao;
import es.mjusticia.sinac.core.persistence.PersonaContactoElectronicoDao;
import es.mjusticia.sinac.core.persistence.PersonaDao;
import es.mjusticia.sinac.core.persistence.PersonaDomicilioDao;
import es.mjusticia.sinac.core.persistence.PersonaIdentificaDao;
import es.mjusticia.sinac.core.persistence.PersonaRcDao;
import es.mjusticia.sinac.core.persistence.PersonaTitulacionesDao;
import es.mjusticia.sinac.core.persistence.PeticionesPidDao;
import es.mjusticia.sinac.core.persistence.PlazoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosAvisosDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosDocumentosTipoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesAccionesDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesDao;
import es.mjusticia.sinac.core.persistence.ProvinciasDao;
import es.mjusticia.sinac.core.persistence.RegistroDao;
import es.mjusticia.sinac.core.persistence.RenovacionDniDao;
import es.mjusticia.sinac.core.persistence.SolicitudesPersonasDao;
import es.mjusticia.sinac.core.persistence.TiposViaDao;
import es.mjusticia.sinac.core.persistence.UsuarioDao;
import es.mjusticia.sinac.core.persistence.ValidacionSemaforoDao;
import es.mjusticia.sinac.core.security.SinacSessionService;
import es.mjusticia.sinac.core.utils.CalculaNif;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.Constantes.Literal;
import es.mjusticia.sinac.core.utils.NFSManager;
import es.mjusticia.sinac.core.utils.UtilFiltro;
import es.mjusticia.sinac.core.utils.Utilidades;
import es.mjusticia.sinac.core.web.tramites.ListadoValidacionesCodEntMaeEnum;
import es.mjusticia.sinac.dgp.dto.ConyugeDto;
import es.mjusticia.sinac.dgp.dto.DomicilioDatosNacionalizacionDto;
import es.mjusticia.sinac.dgp.dto.DomicilioNnormDto;
import es.mjusticia.sinac.dgp.dto.TitularDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaAltaFiliacionDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaConsultaNieFiliacionDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaConsultaReferenciaFiliacionDto;
import es.mjusticia.sinac.filiaciones.exception.SinacFiliacionesException;
import es.mjusticia.sinac.filiaciones.service.FiliacionesService;
import es.mjusticia.sinac.inside.model.dto.InsideAltaExpedienteXmlEniDto;
import es.mjusticia.sinac.inside.model.dto.InsideConsultaRemisionJusticiaResultadoDto;
import es.mjusticia.sinac.inside.model.dto.InsideRemisionJusticiaDto;
import es.mjusticia.sinac.inside.service.InsideService;
import es.mjusticia.sinac.padron.dto.RespuestaPadronDto;
import es.mjusticia.sinac.padron.dto.RetornoPadronDto;
import es.mjusticia.sinac.padron.dto.TransmisionDatosPadronDto;
import es.mjusticia.sinac.padron.dto.TransmisionesPadronDto;
import es.mjusticia.sinac.padron.exception.SinacPadronException;
import es.mjusticia.sinac.padron.service.PadronService;
import es.mjusticia.sinac.pid.service.TitulacionesService;
import es.mjusticia.sinac.pid.service.impl.TitulacionesServiceImpl;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.DatosCentro;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.DatosTitulacion;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.DatosTitulo;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.ccsedele.ResultadoCalificaciones;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.padron.Direccion;
import es.redsara.intermediacion.scsp.esquemas.datosespecificos.padron.Domicilio;
import es.redsara.intermediacion.scsp.esquemas.v3.peticion.Titular;
import es.redsara.intermediacion.scsp.esquemas.v3.respuesta.Respuesta;
import es.redsara.intermediacion.scsp.esquemas.v3.respuesta.TransmisionDatos;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

/**
 * Clase de Implementación de {@link ExpedientesService}.
 *
 * @author NTT Data.
 */
@Component
public class ExpedientesServiceImpl implements ExpedientesService {

  private static final String VAL_POL = "VAL_POL";
  private static final String MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA = "Guardada la fecha más antigua initerrumpida por el momento {}";
  private static final String LARGA_DURACI = "LARGA DURACI";
  private static final String PERMA = "PERMA";
  private static final int PERIODO_TRAMITES_DEFAULT = 3;
  private static final int PERIODO_TRAMITES_PERMANENTE = 60;
  private static final String COD_ENT_MAE_VAL_CCI = "VAL_CCI";
  private static final String COD_ENT_MAE_VAL_MDE = "VAL_MDE";
  private static final String COD_ENT_MAE_VAL_CNI = "VAL_CNI";
  private static final String COD_ENT_MAE_VAL_MJU = "VAL_MJU";
  private static final String COD_ENT_MAE_VAL_DGP = "VAL_DGP";
  private static final String EVAL_PEN = "EVAL-PEN";
  private static final String COD_ENT_MAE_VAL_CIN = "VAL_CIN";
  private static final String COD_ENT_MAE_VAL_CCSE = "VAL_CCSE";
  private static final String EVAL_KO = "EVAL-KO";
  private static final String COD_ENT_MAE_VAL_DELE = "VAL_DELE";
  private static final String COD_ENT_MAE_VAL_DCO = "VAL_DCO";
  private static final String COD_ENT_MAE_VAL_JES = "VAL_JES";
  private static final String EVAL_OK = "EVAL-OK";
  private static final Logger LOG = LoggerFactory.getLogger(ExpedientesServiceImpl.class);
  private static final int DOS = 2;
  private static final int TRES = 3;
  private static final int CUATRO = 4;
  private static final int CINCO = 5;
  private static final int TREINTAYCINCO = 35;
  private static final int CUARENTA = 40;
  private static final int OCHENTA = 80;
  private static final int SESENTA = 60;

  private static final String COD_LDV_TIPO_INFORME_MJU = "TINF-MJU";

  private static final String TIPO_DOC_CARTA_APOYO = "CARAP";

  private static final String ANIOS = "Anios";

  private static final String ANYOS = " años";

  private static final String TPLA_ARCH = "TPLA-ARCH";
  private static final String SEX_HOM = "SEX-HOM";

  private static final String FORMAT_DATE = "yyyy-MM-dd";

  private static final String COD_PER_MANDATO = "PER-MAN";
  private static final String COD_PER_REPRE1 = "PER-R1";
  private static final String COD_PER_REPRE2 = "PER-R2";
  private static final String COD_PER_INTE = "PER-INT";

  private static final String CNIR = "CNIR";
  private static final String CNIS = "CNIS";

  private static final String ERROR_RECUPERANDO_LDV = "Error recuperando ldv maestra con id ";
  private static final String ESPANIA_COD_PAIS = "724";

  @Value("${es.mjusticia.sinac.sinacinside.unidadOrganicaActiva}")
  private String insideUnidadOrganicaActiva;

  @Value("${es.mjusticia.sinac.sinacinside.orgOrigen}")
  private String insideOrgOrigen;

  @Value(value = "${es.mjusticia.filiaciones.codOrganismo}")
  private String codOrganismoFiliaciones;

  @Value(value = "${es.mjusticia.filiaciones.nombreOrganismo}")
  private String nombreOrganismoFiliaciones;

  @Value(value = "${es.mjusticia.filiaciones.identificacionPuesto}")
  private String identificacionPuestoFiliaciones;

  @Value(value = "${es.mjusticia.filiaciones.equipo}")
  private String equipo;

  @Value(value = "${es.mjusticia.filiaciones.usuario}")
  private String usuarioFiliaciones;

  @Value("${sinac.quartz.sinacJobAltaExpedientesSede.nfs.documentosSede}")
  private String nfsDocsSede;

  @Value("${sinac.configpath}")
  private String nfsEnvironmentPath;

  @Autowired
  private SinacSessionService sinacSession;

  @Autowired
  private PerCertificacionesMapper perCertificacionesMapper;

  @Autowired
  private PerCertificacionesDao perCertificacionesDao;

  @Autowired
  private PerPadronMapper perPadronMapper;

  @Autowired
  private PerPadronDao perPadronDao;

  @Autowired
  private MotivoSolPeriodoExigDao motivoSolPeriodoExigDao;

  @Autowired
  private PadronService padronService;

  @Autowired
  private CalificacionesService calificacionesService;

  @Autowired
  private TitulacionesService titulacionesService;

  @Autowired
  private DocumentosService documentosService;

  @Autowired
  private DocumentoTipoMapper documentoTipoMapper;

  @Autowired
  private ExpedienteDao expedienteDao;

  @Autowired
  private ObservacionesService observacionesService;

  @Autowired
  private PersonaRcDao personaRcDao;

  @Autowired
  private ExpedienteMapper expedienteMapper;

  @Autowired
  private ProcedimientoMapper procedimientoMapper;

  @Autowired
  private FormularioCamposValidaDao formularioCamposValidaDao;

  @Autowired
  private PersonaMapper personaMapper;

  @Autowired
  private ExpedienteComunicacionesExternasDao expedienteComunicacionesExternasDao;

  @Autowired
  private ExpedienteComunicacionesExternasMapper expedienteComunicacionesExternasMapper;

  @Autowired
  private ExpedienteFormularioValMapper expedienteFormularioValMapper;

  @Autowired
  private ExpedienteFormularioValDao expedienteFormularioValDao;

  @Autowired
  private ExpedienteDocumentoMapper expedienteDocumentoMapper;

  @Autowired
  private ExpedienteFirmaMapper expedienteFirmaMapper;

  @Autowired
  private ExpedienteDocumentoDao expedienteDocumentoDao;

  @Autowired
  private ExpedientesPersonasMapper expedientesPersonasMapper;

  @Autowired
  private ExpedientesPersonasDao expedientesPersonasDao;

  @Autowired
  private SolicitudesPersonasDao solicitudesPersonasDao;

  @Autowired
  private ExpedientesVinculadosDao expedientesVinDao;

  @Autowired
  private CatalogosService ldvMaestraService;

  @Autowired
  private ExpedienteEstadoDao expedienteEstadoDao;

  @Autowired
  private PersonasService personaService;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  private LocalidadesMapper localidadesMapper;

  @Autowired
  private ProvinciasMapper provinciasMapper;

  @Autowired
  private PaisesMapper paisesMapper;

  @Autowired
  private PersonaRcMapper personaRcMapper;

  @Autowired
  private LocalidadesDao localidadesDao;

  @Autowired
  private ProvinciasDao provinciasDao;

  @Autowired
  private PaisesDao paisesDao;

  @Autowired
  private LdvMaestraDao ldvMaestraDao;

  @Autowired
  private ExpedienteNotificacionesDao expedienteNotificacionesDao;

  @Autowired
  private ExpedienteNotificacionMapper expedienteNotificacionesMapper;

  @Autowired
  private ExpedienteInformeDao expedienteInformeDao;

  @Autowired
  private ExpedienteInformeMapper expedienteInformeMapper;

  @Autowired
  private ExpedienteInformeMapperAux expedienteInformeMapperAux;

  @Autowired
  private ExpedienteDocumentoWithExpedienteMapper expedienteDocumentoWithExpedienteMapper;

  @Autowired
  private ExpedienteAvisoDao expedienteAvisoDao;

  @Autowired
  private ProcedimientosAvisosDao procedimientosAvisosDao;

  @Autowired
  private ExpedienteAvisoMapper expedienteAvisoMapper;

  @Autowired
  private ExpedienteInformesMjuFicherosDatosMapper expedienteInformesMjuFicherosDatosMapper;

  @Autowired
  private ExpedienteInformesMjuFicherosDao expedienteInformesMjuFicherosDao;

  @Autowired
  private ExpedienteInformesMjuFicherosDatosDao expedienteInformesMjuFicherosDatosDao;

  @Autowired
  private TiposViaMapper tiposViaMapper;

  @Autowired
  private ExpedienteSecuenciasMapper expSecMapper;

  @Autowired
  private TiposViaDao tiposViaDao;

  @Autowired
  private PeticionesPidDao peticionesPidDao;

  @Autowired
  private PersonaTitulacionesDao personaTitulacionesDao;

  @Autowired
  private PersonaTitulacionesMapper personaTitulacionesMapper;

  @Autowired
  private EmailConnector emailConnector;

  @Autowired
  private NFSManager nfsManager;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private PersonaMapperAux personaMapperAux;

  @PersistenceContext
  private EntityManager entityManager;

  @Value("${email.from}")
  private String emailFrom;

  @Value("${email.destinatario}")
  private String emailDestinatario;

  @Value("${email.correoEnCopia}")
  private String emailCopia;

  @Value("${email.asunto}")
  private String emailAsunto;

  @Value("${email.cuerpoMensaje1}")
  private String cuerpoMensaje1;

  @Value("${email.cuerpoMensaje2}")
  private String cuerpoMensaje2;

  @Value("${email.mensajeBoe}")
  private String mensajeBoe;

  @Autowired
  private PlantillasService plantillasService;

  @Autowired
  private PlazoDao plazoDao;

  @Autowired
  private ExpedienteInformeDgpDao expedienteInformeDgpDao;

  @Autowired
  private ExpedienteInformeDgpMapper expedienteInformeDgpMapper;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesDao procedimientosFasesTramitesOperacionesAccionesDao;

  @Autowired
  private ProcedimientoDao procedimientoDao;

  @Autowired
  private ExpedienteEstadoMapper expedienteEstadoMapper;

  @Autowired
  private ExpedienteSecuenciasDao expSecDao;

  @Autowired
  private UsuarioDao usuarioDao;

  @Autowired
  private RegistroDao registroDao;

  @Autowired
  private EstadoDao estadoDao;

  @Autowired
  private DocumentoTipoDao documentoTipoDao;

  @Autowired
  private ExpedienteInformeDgpTramitesDao expedienteInformeDgpTramitesDao;

  @Autowired
  private RenovacionDniDao renovacionDniDao;

  @Autowired
  private ExpedienteInformeDgpTramitesMapper expedienteInformeDgpTramitesMapper;

  @Autowired
  private RenovacionDniMapper renovacionDniMapper;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesMapper procedimientosFasesTramitesOperacionesAccionesMapper;

  @Autowired
  private PersonaDao personaDao;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesDao procedimientosFasesTramitesOperacionesDao;

  @Autowired
  private ProcedimientosFasesTramitesOperacionesMapper procedimientosFasesTramitesOperacionesMapper;

  @Autowired
  private PersonaDomicilioMapper personaDomicilioMapper;

  @Autowired
  private PersonaDomicilioDao personaDomicilioDao;

  @Autowired
  private PersonaContactoElectronicoDao personaContactoElectronicoDao;

  @Autowired
  private PersonaContactoElectronicoMapper personaContactoElectronicoMapper;

  @Autowired
  private ExpedientesVinculadosWithExpedientesMapper expedientesVinculadosWithExpedientesMapper;

  @Autowired
  private PaisesService paisesService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private ProcedimientosDocumentosTipoDao procedimientosDocumentosTipoDao;

  @Autowired
  private PlazosService plazosService;

  @Autowired
  private SolicitudesService solicitudesService;

  @Autowired
  private EstadoMapper estadoMapper;

  @Autowired
  private ExpedienteBoeDao expedienteBoeDao;

  @Autowired
  private BoeAnunciosDao boeAnunciosDao;

  @Autowired
  private BoeAnunciosListasDao boeAnunciosListasDao;

  @Autowired
  private ExpedienteBoeMapper expedienteBoeMapper;

  @Autowired
  private BoeAnunciosMapper boeAnunciosMapper;

  @Autowired
  private BoeAnunciosListasMapper boeAnunciosListasMapper;

  @Autowired
  private ExpedienteFormularioValDao expForValDao;

  @Autowired
  private ExpedienteInformeMdeDao expedienteInformeMdeDao;

  @Autowired
  private ExpedienteInformeMdeMapper expedienteInformeMdeMapper;

  @Autowired
  private InsideService insideService;

  @Autowired
  private InsideEstadosService insideEstadosService;

  @Autowired
  private ExpedienteInsideDao expedienteInsideDao;

  @Autowired
  private ExpedienteInsideMapper expedienteInsideMapper;

  @Autowired
  private PerFilNiesWithPersonaMapper perFilNiesWithPersonaMapper;

  @Autowired
  private PerFilNiesDao perFilNiesDao;

  @Autowired
  private PerFiliacionesDao perFiliacionesDao;

  @Autowired
  private PerFiliacionesWithPersonaMapper perFiliacionesWithPersonaMapper;

  @Autowired
  private FiliacionesService filiacionesService;

  @Autowired
  private PersonaIdentificaDao personaIdentificaDao;

  @Autowired
  private ValidacionSemaforoMapper validacionSemaforoMapper;

  @Autowired
  private ValidacionSemaforoDao validacionSemaforoDao;

  @Autowired
  private ExpedientesValidacionesSemaforoDao expedientesValSemaforoDao;

  @Autowired
  private ParametrizacionDao parametrizacionDao;

  @Autowired
  private ParametrizacionMapper parametrizacionMapper;

  @Autowired
  private ProcedimientosPlantillasCriteriosMapper procedimientosPlantillasCriteriosMapper;

  @Override
  public ExpedienteDto saveExpediente(final SolicitudDto solicitudDto, final String identificadorExpedienteGD,
      final String codigoExpediente) throws SinacException {
    LOG.info("ExpedientesServiceImpl.saveExpediente - Init");
    ExpedienteDto expedienteDto = new ExpedienteDto();
    ExpedienteEntity expedienteEntity = new ExpedienteEntity();

    try {
      cargarExpediente(expedienteDto, solicitudDto, identificadorExpedienteGD, codigoExpediente);
      ExpedienteEntity entity = expedienteMapper.toEntity(expedienteDto);
      expedienteEntity = expedienteDao.save(entity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_1)
          .logMessageParams(solicitudDto.getIdSol()).type(SinacExceptionType.DATA);
    }
    expedienteDto = expedienteMapper.toDto(expedienteEntity);
    LOG.info("ExpedientesServiceImpl.saveExpediente - End");

    return expedienteDto;
  }

  @Override
  public void saveCamposFormularioExpediente(final ExpedienteDto expedienteDto, SolicitudDto solicitud)
      throws SinacException {
    LOG.debug("Init - ExpedientesServiceImpl.saveCamposFormularioExpediente del expediente {}",
        expedienteDto.getCodExp());
    if (solicitud.getProcedimientoDto().getFormularioCamposValidaDtos() != null) {
      for (FormularioCamposValidaDto formCamp : solicitud.getProcedimientoDto().getFormularioCamposValidaDtos()) {
        for (SolicitudFormularioValDto solForVal : formCamp.getSolicitudFormularioValDtos()) {
          solForVal.setFormularioCamposValidaDto(formCamp);
          ExpedienteFormularioValDto expedienteFormularioVal = new ExpedienteFormularioValDto();
          cargarValorCampoFormularioExpediente(expedienteDto, expedienteFormularioVal, solForVal);
          try {
            expedienteFormularioValDao.save(expedienteFormularioValMapper.toEntity(expedienteFormularioVal));
          } catch (final Exception exception) {
            throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_2)
                .logMessageParams(expedienteDto.getCodExp()).type(SinacExceptionType.DATA);
          }
        }
      }
    }
    LOG.debug("End - ExpedientesServiceImpl.saveCamposFormularioExpediente del expediente {}",
        expedienteDto.getCodExp());
  }

  @Override
  public void saveExpedientePersonas(List<SolicitudesPersonasDto> solicitudesPersonasDto,
      final ExpedienteDto expedienteDto) throws SinacException {
    LOG.debug("Init - SolicitudesServiceImpl.saveExpedientePersonas del expediente {}", expedienteDto.getCodExp());
    try {
      for (ExpedientesPersonasDto expedientesPersonasDto : cargarExpedientePersona(solicitudesPersonasDto,
          expedienteDto)) {
        expedientesPersonasDao.save(expedientesPersonasMapper.toEntity(expedientesPersonasDto));
      }
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_3)
          .logMessageParams(expedienteDto.getIdExp()).type(SinacExceptionType.DATA);
    }
    LOG.debug("End - SolicitudesServiceImpl.saveExpedientePersonas del expediente {}", expedienteDto.getCodExp());
  }

  @Override
  public ExpedienteDto getExpedienteByIdExpediente(final BigInteger idExpediente) throws SinacException {
    LOG.info("ExpedientesServiceImpl.getExpedienteByIdExpediente - Init");
    ExpedienteDto expedienteDto = null;
    try {
      expedienteDto = expedienteMapper.toDto(expedienteDao.getExpedienteByIdExpediente(idExpediente).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_4)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_5).logMessageParams(idExpediente)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.getExpedienteByIdExpediente - End");
    return expedienteDto;
  }

  @Override
  public ExpedienteDto getExpedienteInteresadoByIdExpediente(final BigInteger idExpediente) throws SinacException {
    LOG.info("ExpedientesServiceImpl.getExpedienteInteresadoByIdExpediente - Init");
    ExpedienteDto expedienteDto = null;
    try {
      ExpedienteEntity expEnt = expedienteDao.getExpedienteInteresadoByIdExpediente(idExpediente);
      ProcedimientoEntity proEnt = procedimientoDao.getProcedimientoByIdExp(idExpediente);
      Set<ExpedientesPersonasEntity> expPerEntSet = expEnt.getExpedientesPersonasEntities();

      List<ExpedientesPersonasEntity> expPerEntListOk = new ArrayList<>();
      for (ExpedientesPersonasEntity expPerEnti : expPerEntSet) {
        ExpedientesPersonasEntity expPerEntCompl = expedientesPersonasDao
            .getExpedientesPersonasConPerById(expPerEnti.getIdExpPer());
        expPerEntListOk.add(expPerEntCompl);
      }

      Set<ExpedienteDocumentoEntity> expDocEnt = expedienteDocumentoDao.getExpedienteDocumentosByIdExp(idExpediente);

      expedienteDto = expedienteMapper.toDto(expEnt);

      ProcedimientoDto proDto = procedimientoMapper.toDto(proEnt);
      expedienteDto.setProcedimientoDto(proDto);

      List<ExpedientesPersonasDto> expPerDtoListOk = new ArrayList<>();
      for (ExpedientesPersonasEntity expPerEnti : expPerEntListOk) {
        ExpedientesPersonasDto expDto = expedientesPersonasMapper.toDto(expPerEnti);
        expPerDtoListOk.add(expDto);
      }
      expedienteDto.setExpedientesPersonasDtos(expPerDtoListOk);

      List<ExpedienteDocumentoDto> expDocList = new ArrayList<>();
      for (ExpedienteDocumentoEntity expDocEnti : expDocEnt) {
        ExpedienteDocumentoDto expDocDto = expedienteDocumentoMapper.toDto(expDocEnti);
        expDocList.add(expDocDto);
      }
      expedienteDto.setExpedienteDocumentoDtos(expDocList);

    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_6)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_7).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.getExpedienteInteresadoByIdExpediente - End");
    return expedienteDto;
  }

  @Override
  public ExpedienteDto getExpedienteSimpleByCodExpediente(String codExpediente) throws SinacException {
    LOG.info("ExpedientesServiceImpl.getExpedienteSimpleByCodExpediente - Init");
    ExpedienteDto expedienteDto = null;
    try {
      expedienteDto = expedienteMapper
          .toDto(expedienteDao.getExpedienteSimpleByCodExpediente(codExpediente).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_8)
          .logMessageParams(codExpediente).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_9).logMessageParams(codExpediente)
          .type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.getExpedienteSimpleByCodExpediente - End");
    return expedienteDto;
  }

  @Override
  public ExpedienteDto getExpedienteByCodExpediente(String codExpediente, String numeroIdentificacion)
      throws SinacException {
    LOG.info("ExpedientesServiceImpl.getExpedienteSimpleByCodExpediente - Init");
    ExpedienteDto expedienteDto = null;
    try {
      expedienteDto = expedienteMapper
          .toDto(expedienteDao.getExpedienteWithLdvByCodExpediente(codExpediente, numeroIdentificacion).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_10)
          .logMessageParams(codExpediente).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_11)
          .logMessageParams(codExpediente).type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.getExpedienteSimpleByCodExpediente - End");
    return expedienteDto;
  }

  @Override
  public void setUsuarioToExpediente(BigInteger idExp) throws SinacException {
    Integer idUsu = sinacSession.getUsuario().getIdUsu();
    LOG.debug("Asignando usuario con id:{} al expediente con id: {}", idUsu, idExp);
    try {
      Optional<ExpedienteEntity> expediente = expedienteDao.findById(idExp);
      if (!expediente.isEmpty() && expediente.get().getUsuarioAsig() == null) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsu(idUsu);
        expediente.get().setUsuarioAsig(usuario);
        expedienteDao.save(expediente.get());
        LOG.info("Se ha asignado correctamente el usuario {} al expediente {} con codExp: {}", idUsu, idExp,
            expediente.get().getCodExp());
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_12).logMessageParams(idExp, idUsu);
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_13).logMessageParams(idExp, idUsu)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void unsetUsuarioToExpediente(BigInteger idExp, Integer idUsu) throws SinacException {
    LOG.debug("Desasignando usuario con id:{} al expediente con id: {}", idUsu, idExp);
    try {
      Optional<ExpedienteEntity> expediente = expedienteDao.findById(idExp);
      if (!expediente.isEmpty() && expediente.get().getUsuarioAsig() != null
          && expediente.get().getUsuarioAsig().getIdUsu() == idUsu) {
        expediente.get().setUsuarioAsig(null);
        expedienteDao.save(expediente.get());
        LOG.info("Se ha desasignado correctamente el usuario {} del expediente {} con codExp: {}", idUsu, idExp,
            expediente.get().getCodExp());
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_14).logMessageParams(idExp, idUsu);
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_15).logMessageParams(idExp, idUsu)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ExpedienteDto getExpedientebyId(BigInteger idExp) throws SinacException {
    try {
      ExpedienteEntity expedienteEntity = getExpedienteFormado(idExp);
      ExpedienteDto expedienteDto = expedienteMapper.toDto(expedienteEntity);

      List<ExpedienteDocumentoDto> listaExpedienteDocumentoDtos = expedienteDto.getExpedienteDocumentoDtos();
      for (ExpedienteDocumentoDto expDoc : listaExpedienteDocumentoDtos) {
        List<ExpedienteNotificacionesDto> notificaciones = expDoc.getExpedienteNotificacionesDtos();
        if (notificaciones != null && !notificaciones.isEmpty()) {
          notificaciones.sort(Comparator.comparing(ExpedienteNotificacionesDto::getFechaNotificacion,
              Comparator.nullsLast(Comparator.reverseOrder())));
        }
        List<ExpedienteFirmaDto> firmas = expDoc.getExpedienteFirmaDtos();
        if (firmas != null && !firmas.isEmpty()) {
          firmas.sort(Comparator.comparing(ExpedienteFirmaDto::getFechaRecepcion,
              Comparator.nullsLast(Comparator.reverseOrder())));
        }
      }
      expedienteDto.setExpedienteDocumentoDtos(listaExpedienteDocumentoDtos);

      List<PersonasDomiciliosDto> personasDomiciliosDtos = new ArrayList<>();
      PersonasDomiciliosDto personasDomiciliosDto = new PersonasDomiciliosDto();
      personasDomiciliosDto.setPersonaDomicilioDto(expedienteDto.getPersonaDomicilioDtoNotificacion());
      personasDomiciliosDtos.add(personasDomiciliosDto);
      setTipoDomicilio(personasDomiciliosDtos);
      expedienteDto.setPersonaDomicilioDtoNotificacion(personasDomiciliosDtos.get(0).getPersonaDomicilioDto());
      setSentidoResolucion(expedienteEntity, expedienteDto);
      setInteresado(expedienteDto);
      setRepresentantes(expedienteDto);
      setPersonasContactosElectronicos(expedienteDto);
      LOG.info("Se ha recuperado la información del expediente {} con codExp: {}", idExp, expedienteDto.getCodExp());
      return expedienteDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_16).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  /**
   * Metodo para setear el sentido de la resolución.
   *
   * @param expedienteEntity Entity expedienteEntity
   * @param expedienteDto    DTO ExpedienteDto.
   */
  private void setSentidoResolucion(ExpedienteEntity expedienteEntity, ExpedienteDto expedienteDto) {
    if (expedienteEntity.getLdvMaestraEntityByIdSentidoResolucionLdv() != null) {
      expedienteDto
          .setSentidoResolucion(ldvMaestraMapper.toDto(expedienteEntity.getLdvMaestraEntityByIdSentidoResolucionLdv()));
    }
  }

  /**
   * Metodo para setear al interesado.
   *
   * @param expedienteEntity Entity expedienteEntity
   * @param expedienteDto    DTO ExpedienteDto.
   */
  private void setInteresado(ExpedienteDto expedienteDto) {
    for (ExpedientesPersonasDto ep : expedienteDto.getExpedientesPersonasDtos()) {
      if (Boolean.TRUE.equals(ep.getFlgNotificar())) {
        expedienteDto.setFlgPersonaConsiente(ep.getFlgConsiente());
        expedienteDto.setIdPersonaNotificar(ep.getPersonaDto().getIdPer());
      }

      if (ep.getLdvMaestraDto().getCodLdvMae().equals(Constantes.Personas.TIPO_INTERESADO)) {
        expedienteDto.setInteresado(ep.getPersonaDto());
        expedienteDto.getInteresado().setFlgNotificar(ep.getFlgNotificar());
        expedienteDto.getInteresado().setTipoPersona(ep.getLdvMaestraDto().getNomLdvMae());

        if (expedienteDto.getInteresado().getPersonaRcDtos() == null
            || expedienteDto.getInteresado().getPersonaRcDtos().isEmpty()) {
          expedienteDto.getInteresado().setPersonaRcDtos(new ArrayList<>());
          PersonaRcDto personaRc = new PersonaRcDto();
          personaRc.setNomRc("");
          personaRc.setApellido1("");
          personaRc.setApellido2("");
          personaRc.setPerRc(new LdvMaestraDto());
          expedienteDto.getInteresado().getPersonaRcDtos().add(personaRc);
        }
        List<PersonasDomiciliosDto> personasDomiciliosDto = expedienteDto.getInteresado().getPersonasDomiciliosDto();
        if (personasDomiciliosDto != null && !personasDomiciliosDto.isEmpty()) {
          setTipoDomicilio(personasDomiciliosDto);
        }

        if (!CollectionUtils.isEmpty(expedienteDto.getInteresado().getPersonaFamDtos())) {
          setPersonasFamDetalleExpediente(expedienteDto);
        }
      }
    }
  }

  private void setPersonasFamDetalleExpediente(ExpedienteDto expedienteDto) {
    for (PersonaFamDto personaFam : expedienteDto.getInteresado().getPersonaFamDtos()) {
      if (personaFam.getLdvMaestraDto().getCodLdvMae().equals("CON-CON")
          && personaFam.getPersonaFamMatrimonioDto() != null) {
        expedienteDto.getInteresado().setPersonaFamMatrimonioDto(personaFam.getPersonaFamMatrimonioDto());
        expedienteDto.getInteresado().getPersonaFamMatrimonioDto().setPersonaFamDto(personaFam);
        expedienteDto.getInteresado().getPersonaFamDtos().remove(personaFam);
        break;
      }
    }
  }

  /**
   * Metodo para setear a los representantes.
   *
   * @param expedienteEntity Entity expedienteEntity
   * @param expedienteDto    DTO ExpedienteDto.
   */
  private void setRepresentantes(ExpedienteDto expedienteDto) {
    for (ExpedientesPersonasDto ep : expedienteDto.getExpedientesPersonasDtos()) {
      if (ep.getLdvMaestraDto().getCodLdvMae().equals("PER-RL14")
          || ep.getLdvMaestraDto().getCodLdvMae().equals("PER-RL18")
          || ep.getLdvMaestraDto().getCodLdvMae().equals("PER-RLJ")
          || ep.getLdvMaestraDto().getCodLdvMae().equals("PER-RLJL")) {
        if (expedienteDto.getRepresentante1() == null) {
          expedienteDto.setRepresentante1(ep.getPersonaDto());
          expedienteDto.setMotivoRepresentacion(ep.getLdvMaestraDto());
          expedienteDto.getRepresentante1().setFlgNotificar(ep.getFlgNotificar());
          expedienteDto.getRepresentante1().setTipoPersona(ep.getLdvMaestraDto().getNomLdvMae());

        } else {
          expedienteDto.setRepresentante2(ep.getPersonaDto());
          expedienteDto.getRepresentante2().setFlgNotificar(ep.getFlgNotificar());
          expedienteDto.getRepresentante2().setTipoPersona(ep.getLdvMaestraDto().getNomLdvMae());
        }
      } else if (ep.getLdvMaestraDto().getCodLdvMae().equals(Constantes.Personas.TIPO_REPRESENTANTE_MANDATO)) {
        expedienteDto.setRepresentanteMandato(ep.getPersonaDto());
        expedienteDto.getRepresentanteMandato().setFlgNotificar(ep.getFlgNotificar());
        expedienteDto.getRepresentanteMandato().setTipoPersona(ep.getLdvMaestraDto().getNomLdvMae());
      }
      if (ep.getPersonaDto().getPersonasDomiciliosDto() != null
          && !ep.getPersonaDto().getPersonasDomiciliosDto().isEmpty()) {
        setTipoDomicilio(ep.getPersonaDto().getPersonasDomiciliosDto());
      }
    }
  }

  @Override
  public Map<Integer, List<ResultadoBusquedaExpedientesDto>> getExpedientesPaginated(
      BusquedaExpedientesDto busquedaExpDto, Pageable pageable) throws SinacException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Get expedientes: {}", String.format("%s", busquedaExpDto));
    }
    List<ExpedienteEntity> listaExpediente = expedienteDao.filtroExpedientes(busquedaExpDto, entityManager, pageable,
        sinacSession.getUsuario().getIdUsu());
    Map<Integer, List<ExpedienteEntity>> mapa = new HashMap<>();
    List<ExpedienteEntity> resultado = new ArrayList<>();
    int pagina = pageable.getPageNumber();
    int total = ((pageable.getPageNumber() + 1) * (pageable.getPageSize()));
    resultado.addAll(listaExpediente.subList(((pagina + (pageable.getPageSize() - pagina)) * pagina),
        (total > listaExpediente.size() ? listaExpediente.size() : total)));
    mapa.put(listaExpediente.size(), resultado);

    List<ExpedienteEntity> listaExpedientes = mapa.values().stream().toList().get(0);
    List<ResultadoBusquedaExpedientesDto> listaBusquedaExpedientesDtos = new ArrayList<>();
    if (!listaExpedientes.isEmpty()) {
      listaBusquedaExpedientesDtos.addAll(expedienteToResultadoBusqueda(listaExpedientes));
    }
    LOG.debug("Lista expedientes: {}", listaBusquedaExpedientesDtos);
    Map<Integer, List<ResultadoBusquedaExpedientesDto>> resultadoMapa = new HashMap<>();
    resultadoMapa.put(mapa.keySet().stream().toList().get(0), listaBusquedaExpedientesDtos);
    return resultadoMapa;
  }

  @Override
  public List<ResultadoBusquedaExpedientesDto> getExpedientesFiltrados(BusquedaExpedientesDto busquedaExpDto,
      Pageable pageable) throws SinacException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Get expedientes: {}", String.format("%s", busquedaExpDto));
    }
    List<ExpedienteEntity> listaExpedientes = expedienteDao.filtroExpedientes(busquedaExpDto, entityManager, pageable,
        sinacSession.getUsuario().getIdUsu());

    List<ResultadoBusquedaExpedientesDto> listaBusquedaExpedientesDtos = new ArrayList<>();
    if (!listaExpedientes.isEmpty()) {
      listaBusquedaExpedientesDtos.addAll(expedienteToResultadoBusqueda(listaExpedientes));
    }
    LOG.debug("Lista expedientes: {}", listaBusquedaExpedientesDtos);
    return listaBusquedaExpedientesDtos;
  }

  @Override
  public Map<Integer, List<ResultadoBusquedaAvisosExpDto>> getAvisosExpPaginated(
      BusquedaAvisosExpDto busquedaAvisosExpDto, Pageable pageable, Boolean isAdmin) throws SinacException {
    LOG.info("Iniciando la búsqueda paginada de avisos de expedientes.");
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Parámetros de búsqueda recibidos: {}", busquedaAvisosExpDto);
      }
      // Obtener procedimientos habilitados
      LOG.debug("Obteniendo procedimientos habilitados para avisos.");
      List<ProcedimientosAvisosEntity> procedimientosAvisos = procedimientosAvisosDao.findAllHabilitadosPA();
      // Filtrar avisos de expediente según los parámetros de búsqueda
      LOG.debug("Ejecutando filtro de avisos de expedientes con paginación.");
      Map<Integer, List<ExpedienteAvisoEntity>> mapa = expedienteAvisoDao.filtroAvisosExpediente(busquedaAvisosExpDto,
          entityManager, pageable, procedimientosAvisos, isAdmin);
      if (mapa.isEmpty() || mapa.values().stream().toList().isEmpty()) {
        LOG.warn("No se encontraron expedientes que cumplan con los criterios de búsqueda.");
        return Collections.emptyMap(); // Retornar un mapa vacío en lugar de null
      }
      // Extraer la lista de expedientes del mapa
      List<ExpedienteAvisoEntity> listaExpedientes = mapa.values().stream().toList().get(0);
      LOG.info("Se encontraron {} expedientes que cumplen con los criterios de búsqueda.", listaExpedientes.size());
      // Crear el mapa de resultados
      Map<Integer, List<ResultadoBusquedaAvisosExpDto>> resultadoMapa = new HashMap<>();
      List<ResultadoBusquedaAvisosExpDto> listaBusquedaAvisosExpDtos = new ArrayList<>();
      if (!listaExpedientes.isEmpty()) {
        LOG.debug("Transformando los expedientes avisos encontrados a DTOs de resultados.");
        listaBusquedaAvisosExpDtos.addAll(avisosExpToResultadoBusqueda(listaExpedientes));
      }
      Integer key = mapa.keySet().stream().findFirst().orElse(null);
      if (key == null) {
        LOG.warn("El mapa de resultados no contiene claves válidas.");
        return Collections.emptyMap(); // Retornar un mapa vacío si no hay claves
      }
      resultadoMapa.put(key, listaBusquedaAvisosExpDtos);
      LOG.info("Búsqueda de avisos de expedientes completada con éxito.");
      return resultadoMapa;
    } catch (NullPointerException e) {
      LOG.error("Se produjo un error debido a un valor nulo inesperado al procesar los avisos de expedientes.", e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_17).type(SinacExceptionType.DATA);
    } catch (Exception e) {
      LOG.error("Ocurrió un error inesperado al realizar la búsqueda paginada de avisos de expedientes.", e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_18).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void updateDetalleExpediente(ExpedienteDto detalleExpedienteDto, String segmentoActualizar)
      throws SinacException {
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Update detalleExpediente: {}", String.format("%s", detalleExpedienteDto));
      }
      // Este metodo se encarga de setear a null los campos no obligatorios que vengan
      // sin rellenar, es posible que a futuro se deban de añadir mas.
      limpiarCamposCombos(detalleExpedienteDto);
      // Comprobamos que segmento del expediente es el que queremos actualizar, si los
      // intervinientes, la notificacion o los datos del expediente.
      if (segmentoActualizar.equals("interesado") || segmentoActualizar.equals("rep1")
          || segmentoActualizar.equals("rep2") || segmentoActualizar.equals("repMan")) {
        actualizarPersonaDetalle(detalleExpedienteDto, segmentoActualizar);
      } else if (segmentoActualizar.equals("actualizarNotificacion")) {
        actualizarDatosNotificacion(detalleExpedienteDto);
      } else if (segmentoActualizar.equals("actualizarDatosExpediente")) {
        actualizarDatosExpediente(detalleExpedienteDto, segmentoActualizar);
      } else if (segmentoActualizar.equals("borrarRepDos")) {
        desactivarRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentante2());
      } else if (segmentoActualizar.equals("borrarRepUno")) {
        desactivarRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentante1());
      } else if (segmentoActualizar.equals("borrarRepMan")) {
        desactivarRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentanteMandato());
      }

    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_19)
          .logMessageParams(detalleExpedienteDto.getIdExp()).type(SinacExceptionType.DATA);
    }
  }

  public void desactivarRepresentanteExpediente(ExpedienteDto expediente, PersonaDto persona) throws SinacException {
    ExpedientesPersonasEntity expedientesPersonasActualizar = expedientesPersonasDao
        .recuperarExpedientesPersonasId(expediente.getIdExp(), persona.getIdPer());
    expedientesPersonasActualizar.setFlgActivo(false);
    try {
      expedientesPersonasDao.save(expedientesPersonasActualizar);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_20)
          .logMessageParams(expedientesPersonasActualizar.getExpedienteEntity().getIdExp())
          .type(SinacExceptionType.DATA);
    }
  }

  private void actualizarDatosExpediente(ExpedienteDto detalleExpedienteDto, String segmentoActualizar)
      throws SinacException {
    try {

      if (detalleExpedienteDto.getProcedimientoDto().getCodCorto().equals("R")) {
        actualizarPeriodo(detalleExpedienteDto, segmentoActualizar);

      }
      ExpedienteDto expedienteR = saveExpediente(detalleExpedienteDto);
      detalleExpedienteDto.setIdExp(expedienteR.getIdExp());
      actualizarRegistroCivil(detalleExpedienteDto.getInteresado());
      desactivarExpedienteFormularioValAnterioresByIdExp(detalleExpedienteDto.getIdExp());
      saveCamposDinamicosByProcedimiento(detalleExpedienteDto);

    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_21)
          .logMessageParams(detalleExpedienteDto.getIdExp()).type(SinacExceptionType.DATA);
    }

  }

  private void actualizarRegistroCivil(PersonaDto interesado) {
    PersonaRcEntity personaRcEntity = personaRcDao.findByIdPersona(interesado.getIdPer());
    if (personaRcEntity != null) {
      PersonaRcDto personaRcAnteriorDto = personaRcMapper.toDto(personaRcEntity);
      if (interesado.getPersonaRcDtos().get(0).getFlgInvOrdApellidos() == null) {
        interesado.getPersonaRcDtos().get(0).setFlgInvOrdApellidos(false);
      }
      if (!interesado.getPersonaRcDtos().get(0).compareExistentValues(personaRcAnteriorDto)) {
        personaRcEntity.setFlgActivo(false);
        personaRcDao.save(personaRcEntity);
      } else {
        interesado.getPersonaRcDtos().clear();
      }
      PersonaEntity personaEntity = personaMapper.toEntity(interesado);
      for (PersonaRcDto personaRcDtoAlta : interesado.getPersonaRcDtos()) {
        PersonaRcEntity personaRcAltaEntity = personaRcMapper.toEntity(personaRcDtoAlta);
        personaRcAltaEntity.setPersonaEntity(personaEntity);
        personaRcDao.save(personaRcAltaEntity);
      }
    }
  }

  @Override
  public void desactivarExpedienteFormularioValAnterioresByIdExp(BigInteger idExp) throws SinacException {
    try {
      for (ExpedienteFormularioValEntity expedienteFormularioValEntity : expedienteFormularioValDao
          .getExpFormPorIdExp(idExp)) {
        expedienteFormularioValEntity.setFlgActivo(false);
        expedienteFormularioValDao.save(expedienteFormularioValEntity);
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_22).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  private void saveCamposDinamicosByProcedimiento(ExpedienteDto expedienteDto) {
    for (FormularioCamposValidaDto formularioCampos : expedienteDto.getProcedimientoDto()
        .getFormularioCamposValidaDtos()) {
      if (formularioCampos.getExpedienteFormularioValDtos() != null
          && !formularioCampos.getExpedienteFormularioValDtos().isEmpty()
          && !formularioCampos.getExpedienteFormularioValDtos().get(0).getValor().isEmpty()) {
        saveFormularioCamposValida(expedienteDto, formularioCampos,
            formularioCampos.getExpedienteFormularioValDtos().get(0));
      }
    }
  }

  private void saveFormularioCamposValida(ExpedienteDto expediente, FormularioCamposValidaDto formularioCampos,
      ExpedienteFormularioValDto formularioValor) {

    ExpedienteFormularioValDto expedienteFormularioValDto = new ExpedienteFormularioValDto();
    if (formularioValor != null && !formularioValor.getValor().isEmpty()) {
      expedienteFormularioValDto.setFormularioCamposValidaDto(formularioCampos);
      expedienteFormularioValDto.setExpedienteDto(expediente);
      if (formularioValor.getValor().equalsIgnoreCase("null")) {
        expedienteFormularioValDto.setValor("No");
      } else {
        expedienteFormularioValDto.setValor(formularioValor.getValor());
      }
      saveExpedienteFormularioVal(expedienteFormularioValDto);
      // tener en cuenta la ldv para que solo afecte a los check
    }
  }

  @Override
  public void saveExpedienteFormularioVal(ExpedienteFormularioValDto expedienteFormularioValDto) throws SinacException {
    try {
      ExpedienteFormularioValEntity expedienteFormularioValEntity = expedienteFormularioValMapper
          .toEntity(expedienteFormularioValDto);
      expedienteFormularioValEntity.setFlgActivo(true);
      expedienteFormularioValDao.save(expedienteFormularioValEntity);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_23)
          .logMessageParams(expedienteFormularioValDto.getExpedienteDto().getIdExp()).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ExpedienteDto saveExpediente(ExpedienteDto expedienteDto) throws SinacException {
    // TODO JULIAN: CAMBIAR ESTO
    try {
      ExpedienteEntity expedienteEntity = expedienteMapper.toEntity(expedienteDto);
      if (expedienteDto.getIdExp() != null) {
        ExpedienteEntity expedientePrevioEntity = expedienteDao.getDetalleExpedientePorId(expedienteDto.getIdExp());
        updateValores(expedientePrevioEntity, expedienteEntity);
      }

      expedienteEntity = expedienteDao.save(expedienteEntity);
      expedienteDto = expedienteMapper.toDto(expedienteEntity);
      return expedienteDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_24).type(SinacExceptionType.DATA);
    }
  }

  // Metodo que actualiza los datos de notificacion
  private void actualizarDatosNotificacion(ExpedienteDto detalleExpedienteDto) throws SinacException {
    LdvMaestraDto ldvMotivoRepre = detalleExpedienteDto.getMotivoRepresentacion();
    String codLdvMotivoRepre = "";
    if (ldvMotivoRepre != null) {
      codLdvMotivoRepre = ldvMotivoRepre.getCodLdvMae();
    }

    if (detalleExpedienteDto.getPersonaDomicilioDtoNotificacion().getTipoDomicilio().equals("1")) {
      detalleExpedienteDto.getPersonaDomicilioDtoNotificacion()
          .setPaisDto(paisesMapper.toDto(paisesDao.getPaisPorCodigo("724")));
    }
    // Comprobamos cual es la persona del expediente que tenga el flg notifica y
    // guardamos la tabla expedientes personas, a diferencia de el actualizarPersona
    // aqui solo guardamos la tabla EXP_M_PER

    if (detalleExpedienteDto.getInteresado() != null && detalleExpedienteDto.getInteresado().getIdPer() != null) {
      detalleExpedienteDto.getInteresado()
          .setFlgNotificar(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_INTERESADO));
      detalleExpedienteDto.getInteresado()
          .setFlgConsiente(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_INTERESADO)
              && detalleExpedienteDto.getFlgPersonaConsiente() != null
              && detalleExpedienteDto.getFlgPersonaConsiente() == true);
      saveExpedientesPersonas(detalleExpedienteDto, detalleExpedienteDto.getInteresado(),
          ldvMaestraService.getCatalogoByCod(Constantes.Personas.TIPO_INTERESADO), true);
    }

    if (detalleExpedienteDto.getRepresentante1() != null) {
      detalleExpedienteDto.getRepresentante1()
          .setFlgNotificar(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_REPRE1));
      detalleExpedienteDto.getRepresentante1()
          .setFlgConsiente(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_REPRE1)
              && detalleExpedienteDto.getFlgPersonaConsiente() != null
              && detalleExpedienteDto.getFlgPersonaConsiente() == true);
      saveExpedientesPersonas(detalleExpedienteDto, detalleExpedienteDto.getRepresentante1(),
          ldvMaestraService.getCatalogoByCod(codLdvMotivoRepre), true);
    }

    if (detalleExpedienteDto.getRepresentante2() != null) {
      detalleExpedienteDto.getRepresentante2()
          .setFlgNotificar(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_REPRE2));
      detalleExpedienteDto.getRepresentante2()
          .setFlgConsiente(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_REPRE2)
              && detalleExpedienteDto.getFlgPersonaConsiente() != null
              && detalleExpedienteDto.getFlgPersonaConsiente() == true);
      saveExpedientesPersonas(detalleExpedienteDto, detalleExpedienteDto.getRepresentante2(),
          ldvMaestraService.getCatalogoByCod(codLdvMotivoRepre), true);
    }

    if (detalleExpedienteDto.getRepresentanteMandato() != null) {
      detalleExpedienteDto.getRepresentanteMandato()
          .setFlgNotificar(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_MANDATO));
      detalleExpedienteDto.getRepresentanteMandato()
          .setFlgConsiente(!StringUtils.isEmpty(detalleExpedienteDto.getTipoPersonaNotificar())
              && detalleExpedienteDto.getTipoPersonaNotificar().equals(COD_PER_MANDATO)
              && detalleExpedienteDto.getFlgPersonaConsiente() != null
              && detalleExpedienteDto.getFlgPersonaConsiente() == true);
      saveExpedientesPersonas(detalleExpedienteDto, detalleExpedienteDto.getRepresentanteMandato(),
          ldvMaestraService.getCatalogoByCod(COD_PER_MANDATO), true);
    }

    // Se guarda la relación de personaCorreoElec y personaDomicilo con el
    // expediente
    saveExpedientesCorDom(detalleExpedienteDto);
    saveExpediente(detalleExpedienteDto);
  }

  // Metodo que actualiza la persona del detalle en funcion del segmento que
  // queramos actualizar, guardamos todas las tablas relacionadas con persona.
  private void actualizarPersonaDetalle(ExpedienteDto detalleExpedienteDto, String segmentoActualizar)
      throws SinacException {
    LdvMaestraDto ldvMotivoRepre = detalleExpedienteDto.getMotivoRepresentacion();
    String codLdvMotivoRepre = "";
    if (ldvMotivoRepre != null) {
      codLdvMotivoRepre = ldvMotivoRepre.getCodLdvMae();
    }
    if (detalleExpedienteDto.getInteresado() != null && segmentoActualizar.equals("interesado")) {

      if (!detalleExpedienteDto.getInteresado().getPersonasDomiciliosDto().isEmpty()
          && detalleExpedienteDto.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto() != null
          && detalleExpedienteDto.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
              .getTipoDomicilio() != null
          && detalleExpedienteDto.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
              .getTipoDomicilio().equals("1")) {
        detalleExpedienteDto.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
            .setPaisDto(paisesService.getPaisPorCodigo(ESPANIA_COD_PAIS));
      }
      if (detalleExpedienteDto.getProcedimientoDto().getCodCorto().equals("R")) {
        actualizarPeriodo(detalleExpedienteDto, segmentoActualizar);

      }
      detalleExpedienteDto.setInteresado(personaService.savePersona(detalleExpedienteDto.getInteresado()));

      saveExpedientesPersonas(detalleExpedienteDto, detalleExpedienteDto.getInteresado(),
          ldvMaestraService.getCatalogoByCod(Constantes.Personas.TIPO_INTERESADO), false);
    }
    if (detalleExpedienteDto.getRepresentante1() != null && segmentoActualizar.equals("rep1")) {
      saveRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentante1(), codLdvMotivoRepre,
          segmentoActualizar);
    }
    if (detalleExpedienteDto.getRepresentante2() != null && segmentoActualizar.equals("rep2")) {
      saveRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentante2(), codLdvMotivoRepre,
          segmentoActualizar);
    }
    if (detalleExpedienteDto.getRepresentanteMandato() != null && segmentoActualizar.equals("repMan")) {
      codLdvMotivoRepre = COD_PER_MANDATO;
      saveRepresentanteExpediente(detalleExpedienteDto, detalleExpedienteDto.getRepresentanteMandato(),
          codLdvMotivoRepre, segmentoActualizar);
    }
  }

  private void saveRepresentanteExpediente(ExpedienteDto detalleExpedienteDto, PersonaDto personaDto, String codMotivo,
      String segmentoActualizar) {
    if (personaDto != null) {
      PersonaDto representante = personaService.savePersona(personaDto);
      representante.setFlgNotificar(personaDto.getFlgNotificar());
      representante.setFlgConsiente(personaDto.isFlgConsiente());
      LdvMaestraDto ldvMotivoRepresentacion = ldvMaestraService.getCatalogoByCod(codMotivo);
      if (ldvMotivoRepresentacion.getCodLdvMae().equals("PER-INT")
          || ldvMotivoRepresentacion.getCodLdvMae().equals("PER-MAN")) {
        saveExpedientesPersonas(detalleExpedienteDto, representante, ldvMotivoRepresentacion, false);
      } else {
        saveExpedientesPersonasRepresentantes(detalleExpedienteDto, representante, ldvMotivoRepresentacion,
            segmentoActualizar);
      }
    }
  }

  private void saveExpedientesPersonasRepresentantes(ExpedienteDto expediente, PersonaDto persona, LdvMaestraDto tipo,
      String segmentoActualizar) throws SinacException {
    ExpedientesPersonasEntity expedientePersonaActualizar = expedientesPersonasDao
        .recuperarExpedientesPersonasId(expediente.getIdExp(), persona.getIdPer());
    ExpedientesPersonasEntity expedientesPersonasActualizar = null;
    if (expedientePersonaActualizar != null && expedientePersonaActualizar.getIdExpPer() != null) {
      expedientesPersonasActualizar = expedientePersonaActualizar;
    } else {
      expedientesPersonasActualizar = new ExpedientesPersonasEntity();
    }
    expedientesPersonasActualizar.setFlgConsiente(persona.isFlgConsiente());
    expedientesPersonasActualizar.setFlgNotificar(persona.getFlgNotificar());

    expedientesPersonasActualizar.setPersonaEntity(personaMapper.toEntity(persona));
    expedientesPersonasActualizar.setExpedienteEntity(expedienteMapper.toEntity(expediente));
    expedientesPersonasActualizar.setLdvMaestraEntity(ldvMaestraMapper.toEntity(tipo));
    try {
      expedientesPersonasDao.save(expedientesPersonasActualizar);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_25)
          .logMessageParams(expedientesPersonasActualizar.getExpedienteEntity().getIdExp())
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void saveExpedientesPersonas(ExpedienteDto expediente, PersonaDto persona, LdvMaestraDto tipo,
      Boolean isNotificar) throws SinacException {
    ExpedientesPersonasEntity expedientesPersonasActualizar = expedientesPersonasDao
        .recuperarExpedientesPersonasByIdExpTipoPersona(expediente.getIdExp(), tipo.getCodLdvMae());
    if (expedientesPersonasActualizar == null) {
      expedientesPersonasActualizar = new ExpedientesPersonasEntity();
    }
    if (isNotificar == true) {
      expedientesPersonasActualizar.setFlgConsiente(persona.isFlgConsiente());
      expedientesPersonasActualizar.setFlgNotificar(persona.getFlgNotificar());
    }
    expedientesPersonasActualizar.setPersonaEntity(personaMapper.toEntity(persona));
    expedientesPersonasActualizar.setExpedienteEntity(expedienteMapper.toEntity(expediente));
    expedientesPersonasActualizar.setLdvMaestraEntity(ldvMaestraMapper.toEntity(tipo));
    try {
      expedientesPersonasDao.save(expedientesPersonasActualizar);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_26)
          .logMessageParams(expedientesPersonasActualizar.getExpedienteEntity().getIdExp())
          .type(SinacExceptionType.DATA);
    }
  }

  private void limpiarCamposCombos(ExpedienteDto expedienteDto) {
    setNullPersonaDatosIfEmpty(expedienteDto.getInteresado());
    setNullPersonaDatosIfEmpty(expedienteDto.getRepresentante1());
    setNullPersonaDatosIfEmpty(expedienteDto.getRepresentante2());
    setNullPersonaDatosIfEmpty(expedienteDto.getRepresentanteMandato());

  }

  private void setNullPersonaDatosIfEmpty(PersonaDto personaDto) {
    if (personaDto != null) {
      LocalidadesDto localidadDto = (LocalidadesDto) UtilFiltro.setNullOnEmpty(personaDto.getLocalNac());
      personaDto.setLocalNac(localidadDto);
      PaisesDto paisDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getPaisNacimiento());
      personaDto.setPaisNacimiento(paisDto);
      PaisesDto paisNacionalidadDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getNacionalidad());
      personaDto.setNacionalidad(paisNacionalidadDto);
      PaisesDto paisSegundaNacionalidadDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getSegundaNacionalidad());
      personaDto.setSegundaNacionalidad(paisSegundaNacionalidadDto);
      ProvinciasDto provinciasDto = (ProvinciasDto) UtilFiltro.setNullOnEmpty(personaDto.getProvNac());
      personaDto.setProvNac(provinciasDto);
      LdvMaestraDto ldvEstadoCivilDto = (LdvMaestraDto) UtilFiltro.setNullOnEmpty(personaDto.getEstadoCivil());
      personaDto.setEstadoCivil(ldvEstadoCivilDto);
      LdvMaestraDto ldvSexoDto = (LdvMaestraDto) UtilFiltro.setNullOnEmpty(personaDto.getSexo());
      personaDto.setSexo(ldvSexoDto);
      PaisesDto paisPrefijoDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getPersonasContactosElectronicosDtos()
          .get(0).getPersonaContactoElectronicoDto().getPrefijoTelefono());
      personaDto.getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto()
          .setPrefijoTelefono(paisPrefijoDto);
      if (!personaDto.getPersonasDomiciliosDto().isEmpty()
          && personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto() != null) {
        setNullDatosDomicilioIfEmpty(personaDto);
      }
      if (!personaDto.getPersonaFamDtos().isEmpty()) {
        for (PersonaFamDto personaFamDto : personaDto.getPersonaFamDtos()) {
          PaisesDto paisHijoDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaFamDto.getPaisNacimiento());
          personaFamDto.setPaisNacimiento(paisHijoDto);
        }
      }
    }
  }

  private void setNullDatosDomicilioIfEmpty(PersonaDto personaDto) {
    PaisesDto paisDomicilioDto = (PaisesDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getPaisDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setPaisDto(paisDomicilioDto);
    ProvinciasDto provinciasDto = (ProvinciasDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getProvinciaDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setProvinciaDto(provinciasDto);
    LocalidadesDto localidadDto = (LocalidadesDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getLocalidadDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setLocalidadDto(localidadDto);
    TiposViaDto tipoViaDto = (TiposViaDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getTipoVia());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setTipoVia(tipoViaDto);
  }

  @Override
  public List<PaisesDto> getPaises() throws SinacException {

    List<PaisesEntity> paises = paisesDao.findAllByNombre();

    List<PaisesEntity> listaPaises = new ArrayList<>();
    paises.forEach(listaPaises::add);

    List<PaisesDto> listaPaisesDto = listaPaises.stream().map(pa -> paisesMapper.toDto(pa)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get paises: {}", String.format("%s", listaPaisesDto));
    }
    return listaPaisesDto;
  }

  public List<PaisesDto> getPaisesPrefijo() throws SinacException {
    List<PaisesEntity> paises = paisesDao.getPaisesPrefijo();
    List<PaisesDto> listaPaisesDto = paises.stream().map(pa -> paisesMapper.toDto(pa)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get paises: {}", String.format("%s", listaPaisesDto));
    }
    return listaPaisesDto;
  }

  @Override
  public List<ProvinciasDto> getProvincias() throws SinacException {
    Iterable<ProvinciasEntity> provincias = provinciasDao.findAllOrderByNombre();

    List<ProvinciasEntity> listaProvincias = new ArrayList<>();
    provincias.forEach(listaProvincias::add);

    List<ProvinciasDto> listaProvinciasDto = listaProvincias.stream().map(pr -> provinciasMapper.toDto(pr)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get provincias: {}", String.format("%s", listaProvinciasDto));
    }
    return listaProvinciasDto;
  }

  @Override
  public List<LocalidadesDto> getLocalidades() throws SinacException {
    Iterable<LocalidadesEntity> localidades = localidadesDao.findAllOrderByNombre();

    List<LocalidadesEntity> listaLocalidades = new ArrayList<>();
    localidades.forEach(listaLocalidades::add);

    List<LocalidadesDto> listaLocalidadesDto = listaLocalidades.stream().map(l -> localidadesMapper.toDto(l)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get localidades: {}", String.format("%s", listaLocalidadesDto));
    }
    return listaLocalidadesDto;
  }

  @Override
  public List<LdvMaestraDto> getTiposIdentificacionDetalleExp() throws SinacException {
    Iterable<LdvMaestraEntity> tipoIdentidades = ldvMaestraDao.getComboLdvMaestraByLdvEntidadMaestraCod("DOC_ID");
    List<LdvMaestraEntity> listaTipoId = new ArrayList<>();
    tipoIdentidades.forEach(listaTipoId::add);

    List<LdvMaestraDto> listaTipoIdDto = listaTipoId.stream().map(id -> ldvMaestraMapper.toDto(id)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get TipoId: {}", String.format("%s", listaTipoIdDto));
    }
    return listaTipoIdDto;
  }

  @Override
  public List<LdvMaestraDto> getSexoDetalleExp() throws SinacException {
    Iterable<LdvMaestraEntity> sexos = ldvMaestraDao.getComboLdvMaestraByLdvEntidadMaestraCod("SEXO");
    List<LdvMaestraEntity> listaSexos = new ArrayList<>();
    sexos.forEach(listaSexos::add);

    List<LdvMaestraDto> listaSexosDto = listaSexos.stream().map(s -> ldvMaestraMapper.toDto(s)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get Sexo: {}", String.format("%s", listaSexosDto));
    }
    return listaSexosDto;
  }

  @Override
  public List<LdvMaestraDto> getTipoRelacionesExpediente() throws SinacException {
    Iterable<LdvMaestraEntity> tiposRelaciones = ldvMaestraDao.getComboLdvMaestraByLdvEntidadMaestraCod("REL_EXP");
    List<LdvMaestraEntity> listaTiposRelaciones = new ArrayList<>();
    tiposRelaciones.forEach(listaTiposRelaciones::add);

    List<LdvMaestraDto> listaTiposRelacionesDto = listaTiposRelaciones.stream().map(s -> ldvMaestraMapper.toDto(s))
        .toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get Tipo de relaciones de expedientes: {}", String.format("%s", listaTiposRelacionesDto));
    }
    return listaTiposRelacionesDto;
  }

  @Override
  public List<LdvMaestraDto> getEstCivilDetalleExp() throws SinacException {
    Iterable<LdvMaestraEntity> estadosCiviles = ldvMaestraDao.getComboLdvMaestraByLdvEntidadMaestraCod("ESTADO_CIVIL");
    List<LdvMaestraEntity> listaEstadoCivil = new ArrayList<>();
    estadosCiviles.forEach(listaEstadoCivil::add);

    List<LdvMaestraDto> listaEstCivilDto = listaEstadoCivil.stream().map(ec -> ldvMaestraMapper.toDto(ec)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get EstadoCivil: {}", String.format("%s", listaEstCivilDto));
    }
    return listaEstCivilDto;
  }

  @Override
  public void saveComunicacionExterna(BigInteger idExp, ComunicacionesExternasDto comunicacionesExternasDto,
      Map<String, Object> valores) throws SinacException {
    try {

      ExpedienteDto expedienteDto = new ExpedienteDto();
      expedienteDto.setIdExp(idExp);
      ExpedienteComunicacionesExternasDto expedienteComunicacionesExternasDto = transformComunicacionExterna(
          comunicacionesExternasDto);
      expedienteComunicacionesExternasDto.setExpedienteDto(expedienteDto);

      LdvMaestraEntity ldvMaestraEntityMotivo = ldvMaestraDao
          .findById(expedienteComunicacionesExternasDto.getLdvMaestraDtoByIdMotivoComLdv().getIdLdvMae())
          .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_123)
              .logMessageParams(expedienteComunicacionesExternasDto.getLdvMaestraDtoByIdMotivoComLdv().getIdLdvMae()));

      expedienteComunicacionesExternasDto
          .setLdvMaestraDtoByIdMotivoComLdv(ldvMaestraMapper.toDto(ldvMaestraEntityMotivo));

      crearInformePorTipo(valores, idExp, expedienteComunicacionesExternasDto.getFechaCom());

      expedienteComunicacionesExternasDao
          .save(expedienteComunicacionesExternasMapper.toEntity(expedienteComunicacionesExternasDto));

    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_27).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ProcedimientoEntity recuperarIdPro(BigInteger idExp) {
    return expedienteDao.recuperarIdPro(idExp);
  }

  /**
   * Metodo que transforma los datos del formulario de comunicaciones externas a
   * expedienteComunicacionesExternasDto
   *
   * @param comunicacionesExternasDto Objeto ComunicacionesExternasDto
   */
  private ExpedienteComunicacionesExternasDto transformComunicacionExterna(
      ComunicacionesExternasDto comunicacionesExternasDto) throws ParseException {
    LdvMaestraDto ldvMaestraMotivoDto = new LdvMaestraDto();
    LdvMaestraDto ldvMaestraTipoDto = new LdvMaestraDto();
    ExpedienteComunicacionesExternasDto expedienteComunicacionesExternasDto = new ExpedienteComunicacionesExternasDto();
    SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);

    expedienteComunicacionesExternasDto.setDestinatario(comunicacionesExternasDto.getDestinatario());
    expedienteComunicacionesExternasDto.setComDesc(comunicacionesExternasDto.getDescripcion());

    ldvMaestraMotivoDto.setIdLdvMae(Integer.parseInt(comunicacionesExternasDto.getIdMotivo()));
    expedienteComunicacionesExternasDto.setLdvMaestraDtoByIdMotivoComLdv(ldvMaestraMotivoDto);

    ldvMaestraTipoDto.setIdLdvMae(Integer.parseInt(comunicacionesExternasDto.getIdTipoCom()));
    expedienteComunicacionesExternasDto.setLdvMaestraDtoByIdTipoComLdv(ldvMaestraTipoDto);

    expedienteComunicacionesExternasDto.setOrgano(comunicacionesExternasDto.getOrgano());

    expedienteComunicacionesExternasDto.setFechaCom(formatter.parse(comunicacionesExternasDto.getFechaCom()));

    return expedienteComunicacionesExternasDto;
  }

  private void cargarValorCampoFormularioExpediente(final ExpedienteDto expediente,
      ExpedienteFormularioValDto expedienteFormularioValDto, SolicitudFormularioValDto solicitudFormularioValDto) {
    if (solicitudFormularioValDto.getFormularioCamposValidaDto() != null) {
      expedienteFormularioValDto.setFormularioCamposValidaDto(solicitudFormularioValDto.getFormularioCamposValidaDto());
    }
    expedienteFormularioValDto.setExpedienteDto(expediente);
    expedienteFormularioValDto.setValor(solicitudFormularioValDto.getValor());
  }

  private void cargarExpediente(ExpedienteDto expedienteDto, final SolicitudDto solicitudDto,
      final String identificadorExpedienteGD, String codigoExpediente) throws SinacException {

    expedienteDto.setFechaEfectos(solicitudDto.getFechaEfectos());
    expedienteDto.setCodExp(codigoExpediente);
    expedienteDto.setIdExpGd(identificadorExpedienteGD);
    expedienteDto.setUsuarioAsig(null);
    expedienteDto.setFechaAlta(new Date());
    expedienteDto.setFlgObedienciaLeyes(solicitudDto.getFlgObedienciaLeyes());
    expedienteDto.setFlgOposicion(solicitudDto.getFlgOposicion());
    expedienteDto.setFlgRenunNacAnte(solicitudDto.getFlgRenunNacAnte());
    expedienteDto.setMotivoOposicion(solicitudDto.getMotivoOposicion());
    expedienteDto.setOrigenSolicitud(solicitudDto.getLdvMaestraDtoByIdOriSolLdv());
    expedienteDto.setMotivoSolicitud(solicitudDto.getLdvMaestraDtoByIdMotivoSolLdv());
    if (solicitudDto.getCaja() != null && !solicitudDto.getCaja().isBlank()) {
      expedienteDto.setCaja(solicitudDto.getCaja());
    }
    expedienteDto.setProcedimientoDto(solicitudDto.getProcedimientoDto());
    expedienteDto
        .setPersonaContactoElectronicoDtoNotificacion(solicitudDto.getPersonaContactoElectronicoDtoNotificacion());
    expedienteDto.setPersonaDomicilioDtoNotificacion(solicitudDto.getPersonaDomicilioDtoNotificacion());
    expedienteDto.setSolicitudDto(solicitudDto);
    expedienteDto.setVersion("1");
    if (solicitudDto.getIdSolVea() != null) {
      expedienteDto.setIdSolVea(solicitudDto.getIdSolVea());
    }
  }

  private List<ExpedientesPersonasDto> cargarExpedientePersona(List<SolicitudesPersonasDto> solicitudesPersonasDto,
      final ExpedienteDto expedienteDto) {
    List<ExpedientesPersonasDto> listaExpedientesPersonasDto = new ArrayList<>();

    if (solicitudesPersonasDto != null && !solicitudesPersonasDto.isEmpty()) {

      for (SolicitudesPersonasDto solicitudesPerDto : solicitudesPersonasDto) {
        ExpedientesPersonasDto expedientesPersonasDto = new ExpedientesPersonasDto();
        expedientesPersonasDto.setExpedienteDto(expedienteDto);
        expedientesPersonasDto.setPersonaDto(solicitudesPerDto.getPersonaDto());
        expedientesPersonasDto.setLdvMaestraDto(solicitudesPerDto.getLdvMaestraDto());
        expedientesPersonasDto.setFlgNotificar(solicitudesPerDto.getFlgNotificar());
        expedientesPersonasDto.setFlgConsiente(solicitudesPerDto.getFlgConsiente());
        listaExpedientesPersonasDto.add(expedientesPersonasDto);
      }
    }
    return listaExpedientesPersonasDto;
  }

  private ExpedienteEntity getExpedienteFormado(BigInteger idExp) {
    ExpedienteEntity expedienteEntity = expedienteDao.getDetalleExpedientePorId(idExp);
    Set<ExpedientesPersonasEntity> expedientePer = expedientesPersonasDao.getExpPersonasPorIdExp(idExp);
    Set<ExpedientesVinculadosEntity> expedienteVin = expedientesVinDao.getExpVinPorIdExp(idExp);
    // TODO revisar inicialización estados de los expedientes asociados.
    if (expedienteVin != null && !expedienteVin.isEmpty()) {
      for (ExpedientesVinculadosEntity expVin : expedienteVin) {
        for (ExpedienteEstadoEntity expEstados : expVin.getExpedienteEntityByIdExp2().getExpedientesEstadosEntities()) {
          Hibernate.initialize(expEstados.getEstado().getEstadoFin());
        }
      }
    }
    Set<ExpedienteFormularioValEntity> expedienteForm = expedienteFormularioValDao.getExpFormPorIdExp(idExp);
    Set<ExpedienteDocumentoEntity> expedienteDoc = expedienteDocumentoDao.getExpedienteDocumentosByIdExp(idExp);
    Optional.ofNullable(expedienteDoc).filter(doc -> !doc.isEmpty()).ifPresent(doc -> {
      for (ExpedienteDocumentoEntity expDocEntity : doc) {
        if (expDocEntity.getExpedienteNotificacionesEntities() != null
            && !expDocEntity.getExpedienteNotificacionesEntities().isEmpty()) {
          for (ExpedienteNotificacionesEntity expNotificaciones : expDocEntity.getExpedienteNotificacionesEntities()) {
            if (expNotificaciones.getExpedienteDocumentoAcuseEntity() != null) {
              Hibernate.initialize(expNotificaciones.getExpedienteDocumentoAcuseEntity());
            }
          }
        }
      }
    });

    Set<ExpedienteEstadoEntity> expedienteEstado = expedienteEstadoDao.getEstadosExpedienteByIdExp(idExp);

    // TODO revisar inicialización de Hibernate relacionada con Registros.
    if (expedienteVin != null && !expedienteVin.isEmpty()) {
      for (ExpedientesVinculadosEntity expVin : expedienteVin) {
        if (expVin.getExpedienteEntityByIdExp2() != null) {
          expVin.getExpedienteEntityByIdExp2().getExpedientesEstadosEntities().removeIf(ep -> !ep.isFlgActivo());
        }
      }
    }
    for (ExpedientesPersonasEntity expPer : expedientePer) {
      expPer.getPersonaEntity().getPersonasDomiciliosEntity().removeIf(p -> !p.isFlgActivo());
      expPer.getPersonaEntity().getPersonasContactosElectronicosEntity().removeIf(p -> !p.isFlgActivo());
      expPer.getPersonaEntity().getPersonasIdentificaEntities().removeIf(p -> !p.isFlgActivo());
      expPer.getPersonaEntity().getPersonaRcEntities().removeIf(p -> !p.isFlgActivo());
      expPer.getPersonaEntity().getPersonaFamEntities().removeIf(p -> !p.isFlgActivo());
      for (PersonaFamEntity perFam : expPer.getPersonaEntity().getPersonaFamEntities()) {
        Hibernate.initialize(perFam.getPaisNacimiento());
      }
      Hibernate.initialize(expPer.getPersonaEntity().getNacionalidad());
      Hibernate.initialize(expPer.getPersonaEntity().getSegundaNacionalidad());
      Hibernate.initialize(expPer.getPersonaEntity().getPersonasDomiciliosEntity());
      List<PersonasDomiciliosEntity> domiciliosList = new ArrayList<>(
          expPer.getPersonaEntity().getPersonasDomiciliosEntity());

      if (!domiciliosList.isEmpty()) {
        // Acceder al primer elemento
        PersonasDomiciliosEntity primerDomicilio = domiciliosList.get(0);

        // Inicializar personaDomicilioEntity dentro del primer elemento
        Hibernate.initialize(primerDomicilio.getPersonaDomicilioEntity());

        // Acceder y inicializar tipoViaEntity
        TiposViaEntity tipoVia = primerDomicilio.getPersonaDomicilioEntity().getTipoVia();
        Hibernate.initialize(tipoVia);
      }
      if (expedienteEntity.getPersonaDomicilioEntityNotificacion() != null) {
        Hibernate.initialize(expedienteEntity.getPersonaDomicilioEntityNotificacion().getTipoVia());
        Hibernate.initialize(expedienteEntity.getPersonaDomicilioEntityNotificacion().getProvincias());
        Hibernate.initialize(expedienteEntity.getPersonaDomicilioEntityNotificacion().getMunicipios());
      }
      if (expedienteEntity.getPersonaContactoElectronicoEntityNotificacion() != null) {
        Hibernate.initialize(expedienteEntity.getPersonaContactoElectronicoEntityNotificacion().getEmail());
        Hibernate.initialize(expedienteEntity.getPersonaContactoElectronicoEntityNotificacion().getTelFijo());
        Hibernate.initialize(expedienteEntity.getPersonaContactoElectronicoEntityNotificacion().getTelMovil());
        Hibernate.initialize(expedienteEntity.getPersonaContactoElectronicoEntityNotificacion().getPrefijoTelefono());
      }
    }

    if (expedienteEntity.getProcedimientoEntity().getFormularioCamposValidaEntities() != null) {
      for (FormularioCamposValidaEntity formCampo : expedienteEntity.getProcedimientoEntity()
          .getFormularioCamposValidaEntities()) {
        formCampo.getExpedienteFormularioValEntities().removeIf(val -> !val.isFlgActivo());
        formCampo.getExpedienteFormularioValEntities()
            .removeIf(val -> (val.getExpedienteEntity().getIdExp() != expedienteEntity.getIdExp()));
      }
    }

    for (ExpedienteDocumentoEntity expDoc : expedienteDoc) {
      expDoc.getRegistroEntities().removeIf(reg -> !reg.isFlgActivo());
    }

    expedienteEntity.setExpedientesPersonasEntities(expedientePer);
    expedienteEntity.setExpedientesVinculadosEntitiesForIdExp1(expedienteVin);
    expedienteEntity.setExpedienteFormularioValEntities(expedienteForm);
    expedienteEntity.setExpedienteDocumentoEntities(expedienteDoc);
    expedienteEntity.setExpedientesEstadosEntities(expedienteEstado);

    return expedienteEntity;
  }

  public void informeFavorable(BigInteger idExp, BigInteger idInforme) throws SinacException {
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao.findById(idInforme).orElseThrow(
        () -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_124).logMessageParams(idInforme));
    LdvMaestraEntity ldvMaestraEntity = null;
    ldvMaestraEntity = setLdvInfEstPorCod("EINF-FAV", expedienteInformeEntity,
        "No se puede cambiar el estado a favorable de un informe que ya tiene ese estado");

    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntity);
    expedienteInformeDao.save(expedienteInformeEntity);
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_28).type(SinacExceptionType.DATA);
    }

  }

  public void informeDesfavorable(BigInteger idExp, BigInteger idInforme) throws SinacException {
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao.findById(idInforme).orElseThrow(
        () -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_124).logMessageParams(idInforme));
    LdvMaestraEntity ldvMaestraEntity = null;
    ldvMaestraEntity = setLdvInfEstPorCod("EINF-DES", expedienteInformeEntity,
        "No se puede cambiar el estado a desfavorable de un informe que ya tiene ese estado");
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntity);
    expedienteInformeDao.save(expedienteInformeEntity);
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_29).type(SinacExceptionType.DATA);
    }

  }

  private LdvMaestraEntity setLdvInfEstPorCod(String codLdv, ExpedienteInformeEntity expedienteInformeEntity,
      String mensajeError) throws SinacException {

    if (StringUtils.isNotEmpty(expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae())
        && StringUtils.equals(codLdv, expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae())) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_30).logMessageParams(mensajeError);
    }
    try {
      return ldvMaestraDao.findByCodigo(codLdv);
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_31).logMessageParams(codLdv);
    }
  }

  @Override
  public Map<String, ExpedienteInformeDto> getListaExpedienteInformeByExpId(BigInteger idExp) throws SinacException {
    List<ExpedienteInformeEntity> listaExpedienteInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpediente(idExp);
    List<ExpedienteInformeEntity> listaExpedienteDgpInformeEntity = listaExpedienteInformeEntity.stream()
        .filter(expinf -> expinf.getLdvMaestraEntityByIdInfLdv().getCodLdvMae()
            .equals(Constantes.TiposInforme.TIPO_INFORME_DGP))
        .toList();
    List<ExpedienteInformeEntity> listaExpedienteMjuInformeEntity = listaExpedienteInformeEntity.stream()
        .filter(expinf -> expinf.getLdvMaestraEntityByIdInfLdv().getCodLdvMae()
            .equals(Constantes.TiposInforme.TIPO_INFORME_MJU))
        .toList();
    List<ExpedienteInformeEntity> listaExpedienteCniInformeEntity = listaExpedienteInformeEntity.stream()
        .filter(expinf -> expinf.getLdvMaestraEntityByIdInfLdv().getCodLdvMae()
            .equals(Constantes.TiposInforme.TIPO_INFORME_CNI))
        .toList();
    List<ExpedienteInformeEntity> listaExpedienteMdeInformeEntity = listaExpedienteInformeEntity.stream()
        .filter(expinf -> expinf.getLdvMaestraEntityByIdInfLdv().getCodLdvMae()
            .equals(Constantes.TiposInforme.TIPO_INFORME_DEF))
        .toList();
    Map<String, ExpedienteInformeDto> listaExpedienteInformeDto = new HashMap<>();
    ExpedienteEntity expediente = expedienteDao.getDetalleExpedientePorId(idExp);
    PersonaEntity interesado = expediente.getExpedientesPersonasEntities().stream().filter(p -> p.isFlgActivo())
        .filter(p -> p.getLdvMaestraEntity().getCodLdvMae().equals(COD_PER_INTE))
        .map(ExpedientesPersonasEntity::getPersonaEntity).toList().get(0);
    int anios = Utilidades.obtenerAniosEntreFechas(interesado.getFechaNacimiento(), new Date());
    boolean mostrarCni = interesado.getNacionalidad().isFlgCni();

    // Plazos.
    PlazoDto plazoRespuestaInformeDGP = plazosService
        .getPlazoByIdProcedimientoAndCodTipoPlazo(expediente.getProcedimientoEntity().getIdPro(), "TPLA-IDGP");
    PlazoDto plazoRespuestaInformeMJU = plazosService
        .getPlazoByIdProcedimientoAndCodTipoPlazo(expediente.getProcedimientoEntity().getIdPro(), "TPLA-IMJU");
    PlazoDto plazoRespuestaInformeCNI = plazosService
        .getPlazoByIdProcedimientoAndCodTipoPlazo(expediente.getProcedimientoEntity().getIdPro(), "TPLA-ICNI");

    if (anios >= 18 && mostrarCni) {
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_MJU,
          rellenaExpedienteInformeDto(listaExpedienteMjuInformeEntity, Constantes.TiposInforme.TIPO_INFORME_MJU,
              plazoRespuestaInformeMJU.getNumPlazo(),
              plazoRespuestaInformeMJU.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(), anios));
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_CNI,
          rellenaExpedienteInformeDto(listaExpedienteCniInformeEntity, Constantes.TiposInforme.TIPO_INFORME_CNI,
              plazoRespuestaInformeCNI.getNumPlazo(),
              plazoRespuestaInformeCNI.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(), anios));
    } else if (anios >= 18 && !mostrarCni) {
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_MJU,
          rellenaExpedienteInformeDto(listaExpedienteMjuInformeEntity, Constantes.TiposInforme.TIPO_INFORME_MJU,
              plazoRespuestaInformeMJU.getNumPlazo(),
              plazoRespuestaInformeMJU.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(), anios));
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_CNI, null);
    } else {
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_DGP, null);
      listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_CNI, null);
    }
    listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_DGP,
        rellenaExpedienteInformeDto(listaExpedienteDgpInformeEntity, Constantes.TiposInforme.TIPO_INFORME_DGP,
            plazoRespuestaInformeDGP.getNumPlazo(),
            plazoRespuestaInformeDGP.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(), anios));

    informesResidencia(listaExpedienteMdeInformeEntity, listaExpedienteInformeDto, expediente, anios);
    return listaExpedienteInformeDto;
  }

  private void informesResidencia(List<ExpedienteInformeEntity> listaExpedienteMdeInformeEntity,
      Map<String, ExpedienteInformeDto> listaExpedienteInformeDto, ExpedienteEntity expediente, int anios) {
    if (!StringUtils.isEmpty(expediente.getProcedimientoEntity().getCodCorto())
        && expediente.getProcedimientoEntity().getCodCorto().equals("R")) {
      boolean mostrarMde = false;
      for (ExpedienteFormularioValEntity entity : expediente.getExpedienteFormularioValEntities()) {
        if (!StringUtils.isEmpty(entity.getValor())
            && entity.getFormularioCamposValidaEntity().getCodCampo().equals("EJERC") && entity.isFlgActivo()) {
          mostrarMde = true;
          break;
        }
      }
      if (mostrarMde) {
        listaExpedienteInformeDto.put(Constantes.TiposInforme.TIPO_INFORME_DEF, rellenaExpedienteInformeDto(
            listaExpedienteMdeInformeEntity, Constantes.TiposInforme.TIPO_INFORME_DEF, null, null, anios));
      }
    }
  }

  private ExpedienteInformeDto rellenaExpedienteInformeDto(List<ExpedienteInformeEntity> listaExpedienteInformeEntity,
      String codTipoInforme, Short numPlazo, String unidadTiempo, int edad) {
    if (listaExpedienteInformeEntity.isEmpty()) {
      return addInformeInexistente(codTipoInforme, edad);
    } else {
      return rellenaDatosInformeDto(listaExpedienteInformeEntity, codTipoInforme, numPlazo, unidadTiempo, edad);
    }
  }

  private ExpedienteInformeDto rellenaDatosInformeDto(List<ExpedienteInformeEntity> listaExpedienteInformeEntity,
      String codTipoInforme, Short numPlazo, String unidadTiempo, int edad) {
    ExpedienteInformeEntity informeActivo = listaExpedienteInformeEntity.stream().filter(ei -> ei.isFlgActivo())
        .filter(ei -> ei.getLdvMaestraEntityByIdInfLdv().getCodLdvMae().equals(codTipoInforme)).toList().get(0);
    ExpedienteInformeDto expedienteInformeDto = expedienteInformeMapper.toDto(informeActivo);
    expedienteInformeDto.setEdad(edad);
    ExpedienteDocumentoEntity expedienteDocumentoEntity = informeActivo.getExpedienteDocumentoEntity();
    Date plazoMaxSolicitud = null;
    if (expedienteDocumentoEntity != null) {
      expedienteInformeDto.setExpedienteDocumentoDto(mapearDocumentoConLdvs(expedienteDocumentoEntity));
    }
    if (expedienteInformeDto.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()
        .equals(Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO)) {
      try {
        if (numPlazo == null) {
          plazoMaxSolicitud = null;
        } else {
          plazoMaxSolicitud = Utilidades.sumarUnidadTiempoAFecha(unidadTiempo, informeActivo.getFechaSolicitud(),
              numPlazo);
        }
      } catch (ParseException e) {
        LOG.error("Error rellenaInformeDto sumarUnidadTiempoAFecha ExpInf id: {} NumPlazo: {} UnidadTiempo: {}",
            informeActivo.getIdExpInf(), numPlazo, unidadTiempo);
      }
      expedienteInformeDto.setFechaLimitePlazo(plazoMaxSolicitud);
    }
    if (codTipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_DGP)
        && informeActivo.getExpedienteInformeDgpEntity() != null) {
      ExpedienteInformeDgpDto expedienteInformeDgpDto = expedienteInformeDgpMapper
          .toDto(informeActivo.getExpedienteInformeDgpEntity());
      expedienteInformeDgpDto.setExpedienteInformeDto(null);

      if (expedienteInformeDgpDto.getCodEstadoAlta() != null && !expedienteInformeDgpDto.getCodEstadoAlta().isBlank()) {
        if (expedienteInformeDgpDto.getCodEstadoAlta().equals("00")) {
          expedienteInformeDgpDto.setCodEstadoAlta(expedienteInformeDgpDto.getCodEstadoAlta() + "-Sin problemas");
        } else {
          expedienteInformeDgpDto.setCodEstadoAlta(expedienteInformeDgpDto.getCodEstadoAlta() + "-Error");
        }
      }

      if (expedienteInformeDgpDto.getCodEstadoAltaSec() != null
          && !expedienteInformeDgpDto.getCodEstadoAltaSec().isBlank()) {
        if (expedienteInformeDgpDto.getCodEstadoAltaSec().equals("00")) {
          expedienteInformeDgpDto.setCodEstadoAltaSec(expedienteInformeDgpDto.getCodEstadoAltaSec() + "-Sin problemas");
        } else {
          expedienteInformeDgpDto.setCodEstadoAltaSec(expedienteInformeDgpDto.getCodEstadoAltaSec() + "-Error");
        }
      }

      if (expedienteInformeDgpDto.getCodEstRes() != null && !expedienteInformeDgpDto.getCodEstRes().isBlank()) {
        if (expedienteInformeDgpDto.getCodEstRes().equals("00")) {
          expedienteInformeDgpDto.setCodEstRes(expedienteInformeDgpDto.getCodEstRes() + "-Sin problemas");
        } else {
          expedienteInformeDgpDto.setCodEstRes(expedienteInformeDgpDto.getCodEstRes() + "-Error");
        }
      }

      if (expedienteInformeDgpDto.getCodEstadoSec() != null && !expedienteInformeDgpDto.getCodEstadoSec().isBlank()) {
        if (expedienteInformeDgpDto.getCodEstadoSec().equals("00")) {
          expedienteInformeDgpDto.setCodEstadoSec(expedienteInformeDgpDto.getCodEstadoSec() + "-Sin problemas");
        } else {
          expedienteInformeDgpDto.setCodEstadoSec(expedienteInformeDgpDto.getCodEstadoSec() + "-Error");
        }
      }
      List<ExpedienteInformeDgpTramiteDto> listaTramitesInformeDgp = getListaExpedienteInformeDgpTramiteDtos(
          informeActivo);

      List<ExpedienteInformeDgpTramiteDto> listaAux = listaTramitesInformeDgp.stream().filter(Objects::nonNull)
          .sorted(Comparator.comparing(ExpedienteInformeDgpTramiteDto::getFechaValidez,
              Comparator.nullsFirst(Comparator.reverseOrder())))
          .toList();

      expedienteInformeDto.setExpedienteInformeDgpDto(expedienteInformeDgpDto);
      expedienteInformeDto.setListaExpedienteInformeDgpTramiteDtos(listaAux);
      expedienteInformeDto.setHistoricoDgp(getHistoricoInformesExpedienteDgp(listaExpedienteInformeEntity));
    } else if (codTipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_MJU)) {
      expedienteInformeDto.setHistoricoMju(getHistoricoInformesExpedienteMju(listaExpedienteInformeEntity));
      Optional<ExpedienteInformesMjuFicherosDatosEntity> result = informeActivo
          .getExpedienteInformesMjuFicherosDatosEntity().stream().filter(e -> e.isFlgActivo()).findFirst();
      if (result.isPresent()) {
        expedienteInformeDto
            .setExpedienteInformesMjuFicherosDatosDto(expedienteInformesMjuFicherosDatosMapper.toDto(result.get()));
        ExpedienteInformesMjuFicherosEntity informeSolicitud = result.get()
            .getExpedienteInformeMjuFicheroSolicitudEntity();
        if (informeSolicitud != null) {
          expedienteInformeDto.setNombreFicheroSolicitud(informeSolicitud.getNomFichero());
          expedienteInformeDto.setEstadoFicheroSolicitud(ldvMaestraMapper.toDto(informeSolicitud.getIdEstFichLdv()));
        }
      }
    } else if (codTipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_DEF)) {
      ExpedienteInformeMdeDto expedienteInformeMdeDto = getExpedienteInformeMdeByIdExpedienteInforme(
          expedienteInformeDto.getIdExpInf());
      expedienteInformeDto.getListaExpedienteInformeMdeDtos().add(expedienteInformeMdeDto);
      List<ExpedienteDocumentoDto> expedienteDocumentoDtos = documentosService
          .getExpedientesDocumentosMdeByIdInforme(expedienteInformeDto.getIdExpInf());
      List<ExpedienteDocumentoInformeMdeDto> expedienteDocumentoInformeMdeDtos = new ArrayList<>();
      for (ExpedienteDocumentoDto expedienteDocumentoDto : expedienteDocumentoDtos) {
        ExpedienteDocumentoInformeMdeDto expedienteDocumentoInformeMdeDto = new ExpedienteDocumentoInformeMdeDto();
        expedienteDocumentoInformeMdeDto.setExpedienteDocumentoDto(expedienteDocumentoDto);
        expedienteDocumentoInformeMdeDto.setExpedienteInformeDto(expedienteInformeDto);
        expedienteDocumentoInformeMdeDtos.add(expedienteDocumentoInformeMdeDto);
      }
      expedienteInformeDto.setExpedienteDocumentoInformeMdeDtos(expedienteDocumentoInformeMdeDtos);
    }
    return expedienteInformeDto;
  }

  private List<ExpedienteInformeDgpTramiteDto> getListaExpedienteInformeDgpTramiteDtos(
      ExpedienteInformeEntity informeActivo) {
    List<ExpedienteInformeDgpTramiteDto> listado = new ArrayList<>();
    ExpedienteInformeDgpEntity expedienteInformeDgpEntity = informeActivo.getExpedienteInformeDgpEntity();
    if (expedienteInformeDgpEntity != null) {
      for (ExpedienteInformeDgpTramiteEntity tramiteInforme : expedienteInformeDgpEntity
          .getExpedienteInformeDgpTramiteEntities()) {
        listado.add(expedienteInformeDgpTramitesMapper.toDto(tramiteInforme));
      }
    }
    return listado;
  }

  private List<ExpedienteInformeHistoricoDto> getHistoricoInformesExpedienteDgp(
      List<ExpedienteInformeEntity> listaExpedienteInformeEntity) {
    List<ExpedienteInformeHistoricoDto> resultado = new ArrayList<>();
    for (ExpedienteInformeEntity informe : listaExpedienteInformeEntity) {
      ExpedienteInformeHistoricoDto expedienteInformeHistoricoDto = new ExpedienteInformeHistoricoDto();
      expedienteInformeHistoricoDto.setIdExpInf(informe.getIdExpInf());
      expedienteInformeHistoricoDto.setIdSolicitudInforme(informe.getIdSolicitudInforme());
      expedienteInformeHistoricoDto
          .setLdvMaestraDtoByIdEstInfLdv(ldvMaestraMapper.toDto(informe.getLdvMaestraEntityByIdEstInfLdv()));
      expedienteInformeHistoricoDto.setFechaCaducidad(informe.getFechaCaducidad());
      expedienteInformeHistoricoDto.setFechaEmisionInf(informe.getFechaEmisionInf());
      expedienteInformeHistoricoDto.setFechaRecepcion(informe.getFechaRecepcion());
      expedienteInformeHistoricoDto.setFechaSolicitud(informe.getFechaSolicitud());
      if (expedienteInformeHistoricoDto.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()
          .equals(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO)
          || expedienteInformeHistoricoDto.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()
              .equals(Constantes.EstadosInforme.ESTADO_INFORME_DESFAVORABLE)
          || expedienteInformeHistoricoDto.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()
              .equals(Constantes.EstadosInforme.ESTADO_INFORME_FAVORABLE)
          || expedienteInformeHistoricoDto.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()
              .equals(Constantes.EstadosInforme.ESTADO_INFORME_CADUCADO)) {
        // Puede ser favorable/desfavorable y que esté rechazado sin documento
        setExpedienteDocumentoInformeHistorico(informe, expedienteInformeHistoricoDto);
      }
      resultado.add(expedienteInformeHistoricoDto);
    }
    return resultado;
  }

  private void setExpedienteDocumentoInformeHistorico(ExpedienteInformeEntity informe,
      ExpedienteInformeHistoricoDto expedienteInformeHistoricoDto) {
    if (informe.getExpedienteDocumentoEntity() != null) {
      expedienteInformeHistoricoDto
          .setExpedienteDocumentoDto(mapearDocumentoConLdvs(informe.getExpedienteDocumentoEntity()));
    }
  }

  private List<ExpedienteInformeHistoricoDto> getHistoricoInformesExpedienteMju(
      List<ExpedienteInformeEntity> listaExpedienteInformeEntity) {
    List<ExpedienteInformeHistoricoDto> resultado = new ArrayList<>();
    for (ExpedienteInformeEntity informe : listaExpedienteInformeEntity) {
      for (ExpedienteInformesMjuFicherosDatosEntity datosFichero : informe.getExpedienteInformesMjuFicherosDatosEntity()
          .stream().sorted((o1, o2) -> o1.getIdExpInfMjuFichDat().compareTo(o2.getIdExpInfMjuFichDat())).toList()) {
        ExpedienteInformeHistoricoDto expedienteInformeHistoricoDto = new ExpedienteInformeHistoricoDto();
        expedienteInformeHistoricoDto.setIdExpInf(informe.getIdExpInf());
        expedienteInformeHistoricoDto.setIdSolicitudInforme(informe.getIdSolicitudInforme());
        expedienteInformeHistoricoDto
            .setLdvMaestraDtoByIdEstInfLdv(ldvMaestraMapper.toDto(informe.getLdvMaestraEntityByIdEstInfLdv()));
        expedienteInformeHistoricoDto.setFechaCaducidad(informe.getFechaCaducidad());
        expedienteInformeHistoricoDto.setFechaEmisionInf(informe.getFechaEmisionInf());
        expedienteInformeHistoricoDto.setFechaRecepcion(informe.getFechaRecepcion());
        expedienteInformeHistoricoDto.setFechaSolicitud(informe.getFechaSolicitud());
        if (datosFichero.getCodRespuesta() != null && !datosFichero.getCodRespuesta().isBlank()) {
          expedienteInformeHistoricoDto
              .setCodRespuestaMju(CodRespuestaMjuEnum.getMensajeByCodigo(datosFichero.getCodRespuesta()));
        }
        expedienteInformeHistoricoDto.setEspMotRechazoMju(datosFichero.getEspMotRechazo());
        if (datosFichero.getEspRes() != null && !datosFichero.getEspRes().isBlank()) {
          expedienteInformeHistoricoDto.setEspResMju(CodRespuestaMjuEnum.getMensajeByCodigo(datosFichero.getEspRes()));
        }
        expedienteInformeHistoricoDto.setEuMotRechazoMju(datosFichero.getEuMotRechazo());
        if (datosFichero.getEuRes() != null && !datosFichero.getEuRes().isBlank()) {
          expedienteInformeHistoricoDto.setEuResMju(CodRespuestaMjuEnum.getMensajeByCodigo(datosFichero.getEuRes()));
        }
        if (datosFichero.getExpedienteInformeMjuFicheroRespuestaEntity() != null) {
          expedienteInformeHistoricoDto
              .setNomFichMju(datosFichero.getExpedienteInformeMjuFicheroRespuestaEntity().getNomFichero());
        }
        expedienteInformeHistoricoDto.setNomFichRespuestaMju(datosFichero.getNomFichRespuesta());
        expedienteInformeHistoricoDto.setFechaCreacion(datosFichero.getFechaCreacion());
        expedienteInformeHistoricoDto.setFechaCreacionMju(datosFichero.getFechaCreacionMju());
        ExpedienteDocumentoEntity expedienteDocumentoEntity = informe.getExpedienteDocumentoEntity();
        if (expedienteInformeHistoricoDto.getCodRespuestaMju() != null && expedienteDocumentoEntity != null
            && (expedienteInformeHistoricoDto.getCodRespuestaMju().equals("A")
                || expedienteInformeHistoricoDto.getCodRespuestaMju().equals("R"))) {
          expedienteInformeHistoricoDto.setExpedienteDocumentoDto(mapearDocumentoConLdvs(expedienteDocumentoEntity));
        }
        resultado.add(expedienteInformeHistoricoDto);
      }
    }
    return resultado;
  }

  private ExpedienteDocumentoDto mapearDocumentoConLdvs(ExpedienteDocumentoEntity expedienteDocumentoEntity) {
    ExpedienteDocumentoDto expedienteDocumentoDto = expedienteDocumentoMapper.toDto(expedienteDocumentoEntity);
    expedienteDocumentoDto
        .setDocumentoTipoDto(documentoTipoMapper.toDto(expedienteDocumentoEntity.getDocumentoTipoEntity()));
    expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(
        ldvMaestraMapper.toDto(expedienteDocumentoEntity.getLdvMaestraEntityByIdEstDocLdv()));
    expedienteDocumentoDto.setLdvMaestraDtoByIdEstElaLdv(
        ldvMaestraMapper.toDto(expedienteDocumentoEntity.getLdvMaestraEntityByIdEstElaLdv()));
    expedienteDocumentoDto
        .setLdvMaestraDtoByIdOrgLdv(ldvMaestraMapper.toDto(expedienteDocumentoEntity.getLdvMaestraEntityByIdOrgLdv()));
    expedienteDocumentoDto.setLdvMaestraDtoByIdOriDocLdv(
        ldvMaestraMapper.toDto(expedienteDocumentoEntity.getLdvMaestraEntityByIdOriDocLdv()));
    return expedienteDocumentoDto;
  }

  /**
   * Añade un informe que no esta en BBDD según su codLdvMae
   *
   * @param codLdvMae
   * @param expediente
   * @return ExpedienteInformeDto
   * @throws SinacException
   */
  private ExpedienteInformeDto addInformeInexistente(String codLdvMae, int edad) throws SinacException {
    ExpedienteInformeDto expedienteInformeDto = new ExpedienteInformeDto();
    LdvMaestraDto ldvMaestraDtoEstado = ldvMaestraService.getCatalogoByCod("EINF-SSO");
    LdvMaestraDto ldvMaestraDtoCod = new LdvMaestraDto();
    ldvMaestraDtoCod.setCodLdvMae(codLdvMae);
    ldvMaestraDtoCod.setDesLdvMae(getLdvByCod(codLdvMae).getDesLdvMae());
    expedienteInformeDto.setLdvMaestraDtoByIdEstInfLdv(ldvMaestraDtoEstado);
    expedienteInformeDto.setLdvMaestraDtoByIdInfLdv(ldvMaestraDtoCod);
    expedienteInformeDto.setEdad(edad);
    return expedienteInformeDto;
  }

  @Override
  public List<ExpedienteComunicacionesExternasDto> getListaExpedienteComunicacionesExternasByExpId(BigInteger idExp)
      throws SinacException {
    List<String> listaMotivos = List.of("MCOM-RECD", "MCOM-RECM", "MCOM-RECC");
    List<ExpedienteComunicacionesExternasEntity> listaExpedienteComunicacionesExternasEntity = expedienteComunicacionesExternasDao
        .getExpedienteComunicacionesExternasByIdExpediente(idExp, listaMotivos);

    List<ExpedienteComunicacionesExternasDto> listaExpedienteComunicacionesExternasDto = new ArrayList<>();

    for (ExpedienteComunicacionesExternasEntity expedienteComunicacionesExternasEntity : listaExpedienteComunicacionesExternasEntity) {

      ExpedienteComunicacionesExternasDto expedienteComunicacionesExternasDto = expedienteComunicacionesExternasMapper
          .toDto(expedienteComunicacionesExternasEntity);
      listaExpedienteComunicacionesExternasDto.add(expedienteComunicacionesExternasDto);
    }

    return listaExpedienteComunicacionesExternasDto;
  }

  @Override
  public List<TiposViaDto> getTiposVia() throws SinacException {
    Iterable<TiposViaEntity> vias = tiposViaDao.findByFlgActivoTrue();

    List<TiposViaEntity> listaVias = new ArrayList<>();
    vias.forEach(listaVias::add);

    List<TiposViaDto> listaViasDto = listaVias.stream().map(l -> tiposViaMapper.toDto(l)).toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get tipos via: {}", String.format("%s", listaViasDto));
    }
    return listaViasDto;
  }

  @Override
  public ExpedienteNotificacionesDto saveExpedienteNotificaciones(
      ExpedienteNotificacionesDto expedienteNotificacionesDto, ExpedienteDocumentoDto expedienteDocumentoDto)
      throws SinacException {
    ExpedienteNotificacionesEntity expedienteNotificacionesEntity = expedienteNotificacionesMapper
        .toEntity(expedienteNotificacionesDto);
    try {
      if (expedienteNotificacionesEntity.getIdExpNoti() != null) {
        // TODO: CONFIRMAR QUE ESTO ESTÉ BIEN
        expedienteNotificacionesEntity.setIdExpNoti(null);
        ExpedienteNotificacionesEntity expedienteNotificacionesEntityEditar = expedienteNotificacionesDao
            .getExpedienteNotificacionesById(expedienteNotificacionesDto.getIdExpNoti());
        expedienteNotificacionesEntityEditar.setFlgActivo(false);
        expedienteNotificacionesDao.save(expedienteNotificacionesEntityEditar);
      }
      expedienteNotificacionesEntity
          .setExpedienteDocumentoEntity(expedienteDocumentoMapper.toEntity(expedienteDocumentoDto));
      if (expedienteNotificacionesDto != null && expedienteNotificacionesDto.getExpedienteDocumentoAcuseDto() != null) {
        expedienteNotificacionesEntity.setExpedienteDocumentoAcuseEntity(
            expedienteDocumentoMapper.toEntity(expedienteNotificacionesDto.getExpedienteDocumentoAcuseDto()));
      }
      expedienteNotificacionesEntity = expedienteNotificacionesDao.save(expedienteNotificacionesEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_32)
          .logMessageParams(expedienteDocumentoDto.getIdExpDoc()).type(SinacExceptionType.DATA);
    }
    return expedienteNotificacionesMapper.toDto(expedienteNotificacionesEntity);
  }

  @Override
  public ExpedienteNotificacionesDto getExpedienteNotificacionesbyIdSolSun(String idSolSun) throws SinacException {
    try {
      if (idSolSun != null) {
        ExpedienteNotificacionesEntity expedienteNotificacionesEntity = expedienteNotificacionesDao
            .getExpedienteNotificacionesByIdSolSun(idSolSun);
        if (expedienteNotificacionesEntity != null) {
          return expedienteNotificacionesMapper.toDto(expedienteNotificacionesEntity);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_33).logMessageParams(idSolSun)
              .type(SinacExceptionType.DATA);
        }
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_34).type(SinacExceptionType.DATA);
      }
    } catch (SinacException ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_35).logMessageParams(ex.getMessage())
          .type(SinacExceptionType.DATA);
    }
  }

  @Transactional(readOnly = false)
  @Override
  public void sendEmail(BigInteger idExpediente, EnviarEmailDto enviarEmailDto) throws SinacException {
    LOG.info("Init - ExpedientesServiceImpl.sendEmail para el expediente {}", idExpediente);
    if (enviarEmailDto.getDocumentosAdjuntos() != null) {
      for (DocumentosAdjuntosEmailDto documento : enviarEmailDto.getDocumentosAdjuntos()) {
        LOG.info("Se va a obtener la descarga del documento {}", documento.getNombreDoc());
        DescargaDeDocumentoDto descarga;
        ExpedienteDocumentoDto expedienteDocumentoDto = obtenerExpedienteDocumentoDto(documento.getIdExpDoc());
        if (expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo().equals("RDFCB")
            || expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo().equals("RDFHB")) {
          cambiarFormatoDocumento(expedienteDocumentoDto);
          descarga = documentosService.getArchivoByIdDocExp(expedienteDocumentoDto.getIdExpDoc());
          if (descarga != null) {
            LOG.info("Se ha obtenido correctamente la descarga del documento {}", expedienteDocumentoDto.getNomDoc());
            documento.setContenidoDoc(descarga.getFile());
            documento.setNombreDoc(expedienteDocumentoDto.getNomDoc());
          }
        } else {
          descarga = obtenerDescargaDeDocumento(expedienteDocumentoDto, idExpediente);
          if (descarga != null) {
            LOG.info("Se ha obtenido correctamente la descarga del documento {}", descarga.getNombreArchivo());
            documento.setContenidoDoc(descarga.getFile());
            documento.setNombreDoc(descarga.getNombreArchivo());
          }
        }
      }
      Optional<ExpedienteEntity> expedienteEntity = expedienteDao.findById(idExpediente);
      if (expedienteEntity.isPresent()) {
        ExpedienteDto expedienteDto = expedienteMapper.toDto(expedienteEntity.get());
        for (DocumentosAdjuntosEmailDto documento : enviarEmailDto.getDocumentosAdjuntos()) {
          Optional<ExpedienteDocumentoEntity> expedienteDocumentoEntity = obtenerExpedienteDocumentoEntity(
              documento.getIdExpDoc());
          if (expedienteDocumentoEntity.isPresent()) {
            ExpedienteDocumentoDto expedienteDocumentoDto = expedienteDocumentoMapper
                .toDto(expedienteDocumentoEntity.get());
            expedienteDocumentoDto.setLdvMaestraDtoByIdEstDocLdv(ldvMaestraService.getCatalogoByCod("EDOC-COM"));
            LOG.info("Se ha cambiado el estado del documento {} del expediente {} a comunicado (EDOC-COM)",
                expedienteDocumentoDto.getNomDoc(), expedienteDto.getCodExp());
            documentosService.saveExpedienteDocumento(expedienteDocumentoDto, expedienteDto);
          } else {
            throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_36)
                .logMessageParams(documento.getIdExpDoc()).type(SinacExceptionType.DATA);
          }
        }
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_37).logMessageParams(idExpediente)
            .type(SinacExceptionType.DATA);
      }
    }
    emailConnector.sendEmail(enviarEmailDto);
    LOG.info("End - ExpedientesServiceImpl.sendEmail para el expediente {}", idExpediente);
  }

  /**
   * Metodo para obtener documentos del expediente.
   *
   * @param String id documento.
   * @return Documentos del expediente.
   */
  private ExpedienteDocumentoDto obtenerExpedienteDocumentoDto(String idExpDoc) throws SinacException {
    return expedienteDocumentoMapper
        .toDto(expedienteDocumentoDao.getDocumentosTipoByIdDocumentoExpediente(new BigInteger(idExpDoc)));
  }

  /**
   * Metodo para cambiar el formato del documento.
   *
   * @param expedienteDocumentoDto documentos del expediente.
   */
  private void cambiarFormatoDocumento(ExpedienteDocumentoDto expedienteDocumentoDto) {
    documentosService.cambiarFormatoDocumento(expedienteDocumentoDto, "odt", "docx");
    LOG.info("Se ha cambiado correctamente el formato del documento {} a docx", expedienteDocumentoDto.getNomDoc());
  }

  /**
   * Metodo para descargar un documento a partir de un id.
   *
   * @param expedienteDocumentoDto documentos del expediente.
   * @return Documento descargado
   */
  private DescargaDeDocumentoDto obtenerDescargaDeDocumento(ExpedienteDocumentoDto expedienteDocumentoDto,
      BigInteger idExp) {
    DescargaDeDocumentoDto descargaDocumentoDto;
    if (expedienteDocumentoDto.getCodGd() != null && !expedienteDocumentoDto.getCodGd().isEmpty()) {
      descargaDocumentoDto = documentosService.descargarDocumentoCopiaAutentica(expedienteDocumentoDto.getIdExpDoc());
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_38)
          .logMessageParams(expedienteDocumentoDto.getIdExpDoc(), idExp)
          .userMessageParams(expedienteDocumentoDto.getNomDoc()).type(SinacExceptionType.BUSINESS);
    }

    return descargaDocumentoDto;
  }

  private Optional<ExpedienteDocumentoEntity> obtenerExpedienteDocumentoEntity(String idExpDoc) {
    return Optional.ofNullable(expedienteDocumentoDao.findExpedienteDocumentoById(new BigInteger(idExpDoc)));
  }

  @Override
  public ExpedientesPersonasDto getExpPersonasPorNifPerIdExp(String nif, BigInteger idExp) throws SinacException {
    try {
      if (nif != null && !nif.isEmpty() && idExp != null) {
        ExpedientesPersonasEntity expedientePer = expedientesPersonasDao.getExpPersonasPorNifPerIdExp(nif, idExp);
        if (expedientePer != null) {
          return expedientesPersonasMapper.toDto(expedientePer);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_39).logMessageParams(nif, idExp)
              .type(SinacExceptionType.DATA);
        }
      } else {
        if (nif == null || nif.isEmpty()) {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_40).type(SinacExceptionType.DATA);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_41).type(SinacExceptionType.DATA);
        }
      }
    } catch (SinacException ex) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_42).logMessageParams(ex.getMessage())
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void informeSolicitado(BigInteger idExp, String tipoInforme, BigInteger idExpInf) throws SinacException {
    ExpedienteEntity expedienteEntity = expedienteDao.findById(idExp)
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_125).logMessageParams(idExp));
    String codigoEstado;

    ExpedienteInformeEntity expedienteInformeEntity = new ExpedienteInformeEntity();
    expedienteInformeEntity.setExpedienteEntity(expedienteEntity);
    if (idExpInf != null) {
      expedienteInformeEntity = expedienteInformeDao.getExpedienteInformeByIdExpedienteInforme(idExpInf);
    }
    if (tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_CNI)) {
      codigoEstado = Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO;
      expedienteInformeEntity.setFechaSolicitud(new Date());
    } else {
      codigoEstado = "EINF-PEN";
    }
    LdvMaestraEntity ldvMaestraEntityByIdInfLdv = ldvMaestraDao.findByCodigo(tipoInforme);
    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao.findByCodigo(codigoEstado);
    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(ldvMaestraEntityByIdInfLdv);
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);
    expedienteInformeDao.save(expedienteInformeEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)), codigoEstado);
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_43).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void informeRecibido(BigInteger idExp, BigInteger idExpInforme, Date fechaEmision, Date fechaRecepcion,
      String sentido, BigInteger idExpedienteDocumento) throws SinacException {
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao.findById(idExpInforme).orElseThrow(
        () -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_124).logMessageParams(idExpInforme));
    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao
        .findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO);
    // Plazos.
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    String codPlazoCaducidadInforme = plazosService.getCodPlazoCaducidadInformeByCodTipoInforme(tipoInforme);
    PlazoDto plazoDto = plazosService.getPlazoByIdProcedimientoAndCodTipoPlazo(recuperarIdPro(idExp).getIdPro(),
        codPlazoCaducidadInforme);
    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv());
    expedienteInformeEntity.setFechaSolicitud(expedienteInformeEntity.getFechaSolicitud());
    try {
      expedienteInformeEntity.setFechaEmisionInf(fechaEmision);
      expedienteInformeEntity.setFechaRecepcion(fechaRecepcion);
      if (plazoDto != null) {
        expedienteInformeEntity.setFechaCaducidad(
            Utilidades.sumarUnidadTiempoAFecha(plazoDto.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(),
                expedienteInformeEntity.getFechaRecepcion(), plazoDto.getNumPlazo()));
      }
    } catch (ParseException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_44).type(SinacExceptionType.DATA);
    }
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);

    if (tipoInforme.equals(COD_LDV_TIPO_INFORME_MJU)) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(ldvMaestraDao.findByCodigo(sentido));
    }
    if (tipoInforme.equals("TINF-CNI")) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(ldvMaestraDao.findByCodigo(sentido));
    }
    if (idExpedienteDocumento != null) {
      expedienteInformeEntity.setExpedienteDocumentoEntity(expedienteDocumentoDao.findById(idExpedienteDocumento)
          .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_126)
              .logMessageParams(idExpedienteDocumento)));
    }
    expedienteInformeEntity.setFlgActivo(true);
    expedienteInformeDao.save(expedienteInformeEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_45).type(SinacExceptionType.DATA);
    }

  }

  @Override
  public void informeRecibido(BigInteger idExp, ExpedienteInformeDto expedienteInformeDto) throws SinacException {
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao.findById(expedienteInformeDto.getIdExpInf())
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_125).logMessageParams(idExp));
    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao
        .findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO);
    // Plazos.
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    String codPlazoCaducidadInforme = plazosService.getCodPlazoCaducidadInformeByCodTipoInforme(tipoInforme);
    PlazoDto plazoDto = plazosService.getPlazoByIdProcedimientoAndCodTipoPlazo(recuperarIdPro(idExp).getIdPro(),
        codPlazoCaducidadInforme);
    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv());
    expedienteInformeEntity.setFechaSolicitud(expedienteInformeEntity.getFechaSolicitud());
    try {
      expedienteInformeEntity.setFechaEmisionInf(expedienteInformeDto.getFechaEmisionInf());
      expedienteInformeEntity.setFechaRecepcion(expedienteInformeDto.getFechaRecepcion());
      if (plazoDto != null) {
        expedienteInformeEntity.setFechaCaducidad(
            Utilidades.sumarUnidadTiempoAFecha(plazoDto.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(),
                expedienteInformeEntity.getFechaRecepcion(), plazoDto.getNumPlazo()));
      }
    } catch (ParseException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_46).type(SinacExceptionType.DATA);
    }
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);
    if (tipoInforme.equals(COD_LDV_TIPO_INFORME_MJU)
        && expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv() != null) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(
          ldvMaestraDao.findByCodigo(expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv().getCodLdvMae()));
    }

    if (tipoInforme.equals("TINF-CNI") && expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv() != null) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(
          ldvMaestraDao.findByCodigo(expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv().getCodLdvMae()));
    }
    if ((tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_CNI)
        || tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_DEF))
        && expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv() != null) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(
          ldvMaestraDao.findByCodigo(expedienteInformeDto.getLdvMaestraDtoByIdSentidoInfLdv().getCodLdvMae()));

    }
    if (expedienteInformeDto.getExpedienteDocumentoDto() != null
        && expedienteInformeDto.getExpedienteDocumentoDto().getIdExpDoc() != null) {
      expedienteInformeEntity.setExpedienteDocumentoEntity(
          expedienteDocumentoDao.findById(expedienteInformeDto.getExpedienteDocumentoDto().getIdExpDoc())
              .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_125)
                  .logMessageParams(expedienteInformeDto.getExpedienteDocumentoDto().getIdExpDoc())));
    }
    expedienteInformeEntity.setFlgActivo(true);
    if (!StringUtils.isEmpty(expedienteInformeDto.getInfo())) {
      expedienteInformeEntity.setInfoInformes(expedienteInformeDto.getInfo());
    }
    expedienteInformeDao.save(expedienteInformeEntity);
    if (tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_DEF)) {
      for (ExpedienteInformeMdeDto expedienteInformeMdeDto : expedienteInformeDto.getListaExpedienteInformeMdeDtos()) {
        ExpedienteInformeMdeEntity expedienteInformeMdeEntity = expedienteInformeMdeDao
            .getExpedienteInformeMdeByIdExpedienteInforme(expedienteInformeEntity.getIdExpInf());
        expedienteInformeMdeEntity.setImportante(expedienteInformeMdeDto.isImportante());
        expedienteInformeMdeDao.save(expedienteInformeMdeEntity);
      }
    }
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_47).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public LdvMaestraDto getLdvByCod(String codLdv) {
    return ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo(codLdv));
  }

  /**
   * valida todos los informes de un expediente
   *
   * @param idExp
   * @return ModelAndView
   * @throws SinacException
   */
  @Override
  public void validarTodosInformes(BigInteger idExp) throws SinacException {
    List<ExpedienteInformeEntity> listaExpedienteInformes = expedienteInformeDao
        .getExpedienteInformesByIdExpediente(idExp);
    LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findByCodigo("EINF-VAL");
    for (ExpedienteInformeEntity expedienteInformeEntity : listaExpedienteInformes) {
      expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntity);
      expedienteInformeDao.save(expedienteInformeEntity);
    }

  }

  /**
   * Método que establece los campos predefinidos de la pantalla del envio del
   * email del Acuerdo del Consejo de Ministros
   *
   * @param enviarEmailDto el objeto EnviarEmailDto al que se le establecerán los
   *                       campos predefinidos
   * @param interesado     el nombre del interesado
   * @return el objeto EnviarEmailDto con los campos predefinidos establecidos
   * @throws SinacException si ocurre un error al establecer los campos
   *                        predefinidos
   */

  @Override
  public EnviarEmailDto setCamposPredefEmailAcuerdoConMin(EnviarEmailDto enviarEmailDto, String interesado,
      ExpedienteDto expediente) throws SinacException {
    List<DocumentosAdjuntosEmailDto> documentosAdjuntos = enviarEmailDto.getDocumentosAdjuntos();
    StringBuilder documentosAdjuntosCuerpoMensaje = new StringBuilder();
    if (documentosAdjuntos != null && !documentosAdjuntos.isEmpty()) {
      for (int i = 0; i < documentosAdjuntos.size(); i++) {
        DocumentosAdjuntosEmailDto documento = documentosAdjuntos.get(i);
        String nombreDocumento = documento.getNombreDoc();
        documentosAdjuntosCuerpoMensaje.append("      " + (i + 1)).append(". ").append(nombreDocumento).append("\n");
      }
    }

    enviarEmailDto.setCorreoFrom(emailFrom);
    enviarEmailDto.setEmailDestinatario(emailDestinatario);
    enviarEmailDto.setCorreosDestinatarios(Arrays.asList(emailDestinatario.trim().split(",")));
    enviarEmailDto.setCorreoEnCopia(emailCopia);
    enviarEmailDto.setCorreosEnCopia(Arrays.asList(emailCopia.trim().split(",")));
    enviarEmailDto.setAsunto(emailAsunto.concat(interesado));
    StringBuilder mensajeEmail = new StringBuilder();

    mensajeEmail.append(cuerpoMensaje1).append(interesado).append(" :\n").append(documentosAdjuntosCuerpoMensaje);

    // Condicional para el checkbox de "No publicar en BOE" y que añada el mensaje
    if (!expediente.getProcedimientoDto().getFormularioCamposValidaDtos().isEmpty()) {
      for (FormularioCamposValidaDto form : expediente.getProcedimientoDto().getFormularioCamposValidaDtos()) {
        if (form.getCodCampo().toString().equals("PUBOE")
            && form.getExpedienteFormularioValDtos().get(0).getValor().equals("Sí")) {
          mensajeEmail.append(mensajeBoe).append("\n");
        }
      }
    }
    mensajeEmail.append(cuerpoMensaje2).append(emailFrom);

    enviarEmailDto.setMensajeEmail(mensajeEmail.toString());

    return enviarEmailDto;
  }

  /**
   * Obtiene la lista de documentos del Acuerdo del Consejo de Ministros para un
   * expediente dado.
   *
   * @param idExp el ID del expediente
   * @return la lista de documentos del Consejo de Ministros
   * @throws SinacException si ocurre un error durante la obtención de los
   *                        documentos
   */

  @Override
  public List<DocumentosTramiteDto> getDocumentosConsejoMinistros(BigInteger idExp) throws SinacException {
    List<PlantillaDto> listaPlantillasDto = plantillasService.getListaPlantillas(idExp, "PRE", "GELE", "GEND");

    List<ExpedienteDocumentoEntity> listaExpedienteDocEntity = expedienteDocumentoDao
        .getExpedienteDocumentosConFirmasByIdExp(idExp);
    List<DocumentosTramiteDto> listaAcuerdoConMin = new ArrayList<>();
    for (PlantillaDto doc : listaPlantillasDto) {
      DocumentosTramiteDto docAcuerdoConMin = new DocumentosTramiteDto();
      docAcuerdoConMin.setNomDocumento(doc.getProDocTipo().getDocumentosTipo().getNomTipo());
      docAcuerdoConMin.setIdPla(doc.getIdPla());
      docAcuerdoConMin.setCodPlantilla(doc.getCodPlantilla());
      docAcuerdoConMin.setCodTFirmaLdv(doc.getProDocTipo().getIdDocTfirLdv().getCodLdvMae());
      boolean documentoEncontrado = false;
      ListIterator<ExpedienteDocumentoEntity> iterator = listaExpedienteDocEntity.stream().toList().listIterator();
      while (!documentoEncontrado && iterator.hasNext()) {
        ExpedienteDocumentoEntity expDoc = iterator.next();
        if (expDoc.getDocumentoTipoEntity().getIdDocTipo() == doc.getProDocTipo().getDocumentosTipo().getIdDocTipo()) {
          documentoEncontrado = true;
          docAcuerdoConMin.setIdExpDoc(expDoc.getIdExpDoc());
          docAcuerdoConMin.setEstadoDocumento(ldvMaestraMapper.toDto(expDoc.getLdvMaestraEntityByIdEstDocLdv()));
          docAcuerdoConMin.setFechaCreacionDocumento(expDoc.getFechaCreacion());
          docAcuerdoConMin.setExpedienteDocumentoDto(expedienteDocumentoMapper.toDto(expDoc));
          docAcuerdoConMin.setCodDocTipo(expDoc.getDocumentoTipoEntity().getCodTipo());

          for (ExpedienteFirmaEntity firmaDoc : expDoc.getExpedienteFirmaEntities()) {
            if (firmaDoc.isFlgActivo()) {
              docAcuerdoConMin.setFechaEnvioFirmaDocumento(firmaDoc.getFechaSolicitud());
              docAcuerdoConMin.setFechaFirmaDocumento(firmaDoc.getFechaRecepcion());
            }
          }
          // TODO docAcuerdoConMin.setFechaEnvioCmDocumento(null);
        }

      }
      listaAcuerdoConMin.add(docAcuerdoConMin);
    }
    // Generacion de documento Carta de apoyo
    try {
      ExpedienteDocumentoEntity expedienteDoc = expedienteDocumentoDao
          .getExpedienteDocumentosByCodTipoDocumentoIdExpediente(idExp, TIPO_DOC_CARTA_APOYO);

      DocumentosTramiteDto docCartaApoyo = new DocumentosTramiteDto();
      docCartaApoyo.setIdPla(null);
      if (expedienteDoc != null) {
        docCartaApoyo.setNomDocumento(expedienteDoc.getDocumentoTipoEntity().getNomTipo());
        docCartaApoyo.setIdExpDoc(expedienteDoc.getIdExpDoc());
        docCartaApoyo.setEstadoDocumento(ldvMaestraMapper.toDto(expedienteDoc.getLdvMaestraEntityByIdEstDocLdv()));
        if (expedienteDoc.getFechaCreacion() != null) {
          docCartaApoyo.setFechaCreacionDocumento(expedienteDoc.getFechaCreacion());
        }
        docCartaApoyo.setCodDocTipo(expedienteDoc.getDocumentoTipoEntity().getCodTipo());
        for (ProcedimientosDocumentosTipoEntity proDocTipo : expedienteDoc.getDocumentoTipoEntity()
            .getProcedimientosDocumentosTipoEntities()) {
          docCartaApoyo.setCodTFirmaLdv(proDocTipo.getLdvMaestraEntityByIdDocTfirLdv().getCodLdvMae());
        }
      } else {
        DocumentoTipoDto docTipoCarta = documentoTipoMapper
            .toDto(documentoTipoDao.recuperarTipoDocPorCod(TIPO_DOC_CARTA_APOYO));
        docCartaApoyo.setNomDocumento(docTipoCarta.getNomTipo());
        docCartaApoyo.setCodDocTipo(docTipoCarta.getCodTipo());
        docCartaApoyo.setEstadoDocumento(ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-NRE")));
      }
      listaAcuerdoConMin.add(docCartaApoyo);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_48).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
    return listaAcuerdoConMin;
  }

  @Override
  public List<ResultadoBusquedaExpedientesDto> getExpedientesAsignadosPublicacionBoe(BigInteger idExp)
      throws SinacException {
    Integer idUsu = sinacSession.getUsuario().getIdUsu();
    List<ExpedienteEntity> listaExpedientes = expedienteDao.getExpedientesAsignadosPublicacionBoe(idExp, "ELE", idUsu);
    return expedienteToResultadoBusqueda(listaExpedientes);
  }

  private List<ResultadoBusquedaExpedientesDto> expedienteToResultadoBusqueda(List<ExpedienteEntity> listaExpedientes) {
    List<ResultadoBusquedaExpedientesDto> listaResultadoBusquedaExpedientes = new ArrayList<>();
    ResultadoBusquedaExpedientesDto resultadoBusquedaExpedientesDto;
    for (ExpedienteEntity expedienteEntity : listaExpedientes) {
      resultadoBusquedaExpedientesDto = new ResultadoBusquedaExpedientesDto();
      resultadoBusquedaExpedientesDto.setIdExp(expedienteEntity.getIdExp());
      resultadoBusquedaExpedientesDto.setCodExp(expedienteEntity.getCodExp());
      setDatosInformesIntoResultadoBusquedaExpedientes(resultadoBusquedaExpedientesDto, expedienteEntity);
      resultadoBusquedaExpedientesDto.setFechaEfectos(expedienteEntity.getFechaEfectos());
      PersonaEntity interesado = expedienteEntity.getExpedientesPersonasEntities().stream().filter(p -> p.isFlgActivo())
          .filter(p -> p.getLdvMaestraEntity().getCodLdvMae().equals(Constantes.Personas.TIPO_INTERESADO))
          .map(ExpedientesPersonasEntity::getPersonaEntity).toList().get(0);
      if (interesado.getIdPer() == null) {
        continue;
      }
      resultadoBusquedaExpedientesDto.setNombreInteresado(interesado.getNombre());
      resultadoBusquedaExpedientesDto.setApellido1Interesado(interesado.getApellido1());
      resultadoBusquedaExpedientesDto.setApellido2Interesado(interesado.getApellido2());
      resultadoBusquedaExpedientesDto.setIdPer(interesado.getIdPer());
      if (interesado.getNacionalidad() != null) {
        resultadoBusquedaExpedientesDto.setNacionalidadInteresado(interesado.getNacionalidad().getNacionalidad());
      }
      if (interesado.getSegundaNacionalidad() != null) {
        resultadoBusquedaExpedientesDto
            .setSegundaNacionalidadInteresado(interesado.getSegundaNacionalidad().getNacionalidad());
      }
      resultadoBusquedaExpedientesDto.setTipoProcedimiento(expedienteEntity.getProcedimientoEntity().getNomPro());
      PersonaIdentificaEntity identificacionInteresado = interesado.getPersonasIdentificaEntities().stream()
          .filter(pIden -> pIden.isFlgActivo() && pIden.isFlgPrincipal()).toList().get(0);
      resultadoBusquedaExpedientesDto.setNumAcreditacionInteresado(identificacionInteresado.getNumAcreditacion());
      List<String> lista = expedienteEntity.getExpedientesEstadosEntities().stream().filter(e -> e.isFlgActivo())
          .map(ExpedienteEstadoEntity::getEstado).filter(e -> e.isFlgActivo()).map(MaquinaEstadosEntity::getEstadoFin)
          .filter(e -> e.isFlgActivo()).sorted((e1, e2) -> e2.getFechaCreacion().compareTo(e1.getFechaCreacion()))
          .map(EstadoEntity::getNomEstado).toList();
      if (!lista.isEmpty()) {
        String estado = lista.get(0);
        resultadoBusquedaExpedientesDto.setEstadoExpediente(estado);
        if (lista.size() > 1) {
          for (int i = 1; i < lista.size(); i++) {
            resultadoBusquedaExpedientesDto.getListaEstados().add(lista.get(i));
          }
        }
      }
      listaResultadoBusquedaExpedientes.add(resultadoBusquedaExpedientesDto);
    }
    return listaResultadoBusquedaExpedientes;
  }

  private void setDatosInformesIntoResultadoBusquedaExpedientes(
      ResultadoBusquedaExpedientesDto resultadoBusquedaExpedientesDto, ExpedienteEntity expedienteEntity) {
    for (ExpedienteInformeEntity informeEntity : expedienteEntity.getExpedienteInformeEntities()) {
      if (informeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae()
          .equals(Constantes.TiposInforme.TIPO_INFORME_DEF)) {
        if (informeEntity.getLdvMaestraEntityByIdSentidoInfLdv() != null) {
          resultadoBusquedaExpedientesDto
              .setRespuestaInforme(informeEntity.getLdvMaestraEntityByIdSentidoInfLdv().getNomLdvMae());
        } else {
          resultadoBusquedaExpedientesDto.setRespuestaInforme("-");
        }
        for (ExpedienteInformeMdeEntity expedienteInformeMdeEntity : informeEntity.getExpedienteInformesMdeEntity()) {
          if (expedienteInformeMdeEntity.isFlgActivo()) {
            resultadoBusquedaExpedientesDto.setImportante(expedienteInformeMdeEntity.isImportante());
          }
        }
      }
    }
  }

  private List<ResultadoBusquedaAvisosExpDto> avisosExpToResultadoBusqueda(
      List<ExpedienteAvisoEntity> listaExpedientesAvisos) {
    List<ResultadoBusquedaAvisosExpDto> listaResultadoBusquedaExpedientes = new ArrayList<>();
    ResultadoBusquedaAvisosExpDto resultadoBusquedaAvisosExpDto;
    for (ExpedienteAvisoEntity expedienteAvisoEntity : listaExpedientesAvisos) {
      resultadoBusquedaAvisosExpDto = new ResultadoBusquedaAvisosExpDto();
      resultadoBusquedaAvisosExpDto.setIdExpAvisos(expedienteAvisoEntity.getIdExpAvisos());
      resultadoBusquedaAvisosExpDto.setCodExp(expedienteAvisoEntity.getExpediente().getCodExp());
      resultadoBusquedaAvisosExpDto.setNomAviso(expedienteAvisoEntity.getAviso().getNomAviso());
      resultadoBusquedaAvisosExpDto.setFechaCreacion(expedienteAvisoEntity.getFechaCreacion());
      resultadoBusquedaAvisosExpDto.setFlgActivo(expedienteAvisoEntity.isFlgActivo());

      listaResultadoBusquedaExpedientes.add(resultadoBusquedaAvisosExpDto);
    }
    return listaResultadoBusquedaExpedientes;
  }

  private Boolean isAdminRol(RolesUsuariosDto rolUsuarioSeleccionado) {
    Boolean isAdminRol = false;
    switch (rolUsuarioSeleccionado.getRol().getNomRol()) {
    case Constantes.Roles.TRAMITADOR, Constantes.Roles.TRAMITADOR_EXTENSO, Constantes.Roles.GRABADOR_EXTERNO:
      isAdminRol = false;
      break;
    case Constantes.Roles.ADMINISTRADOR:
      isAdminRol = true;
      break;
    case Constantes.Roles.CNI:
      isAdminRol = false;
      break;
    default:
      isAdminRol = false;
    }
    return isAdminRol;
  }

  @Override
  public Map<String, Object> getResumenExpediente(BigInteger idExpediente) throws SinacException {
    LOG.debug("Init - expedientesServiceImpl.getResumenExpediente del exp {}", idExpediente);
    try {
      List<Object[]> resumenExpediente = expedienteDao.recuperarResumenExpediente(idExpediente);
      Set<Object[]> estados = expedienteEstadoDao.getEstadosExpedienteOrdenado(idExpediente);

      String nomRol = sinacSession.getRolUsuarioSeleccionado().getRol().getNomRol();

      Optional<ExpedienteEntity> expOpt = expedienteDao.findById(idExpediente);
      Boolean permModAviso = false;
      if (expOpt.isPresent()) {
        ExpedienteEntity expEnt = expOpt.get();
        Integer userLogId = (sinacSession.getUsuario() != null) ? sinacSession.getUsuario().getIdUsu() : null;
        Integer userAsigId = (expEnt.getUsuarioAsig() != null) ? expEnt.getUsuarioAsig().getIdUsu() : null;
        if (userLogId != null && userAsigId != null && Objects.equals(userLogId, userAsigId)) {
          permModAviso = true;
        }
      }

      Boolean isAllowed;
      if (!Constantes.Roles.CONSULTA.equals(nomRol) && !Constantes.Roles.GRABADOR_EXTERNO.equals(nomRol)
          && !Constantes.Roles.CNI.equals(nomRol)) {
        isAllowed = true;
      } else {
        isAllowed = false;
      }

      List<ExpedienteAvisoDto> expAviDtos = this.getAvisosExpedienteByIdExp(idExpediente);
      Map<String, Object> result = new HashMap<>();
      if (resumenExpediente.isEmpty()) {
        LOG.info("No se han obtenido datos resumidos del expediente {}", idExpediente);
        return result;
      }
      Object[] resumenExpedienteItem = resumenExpediente.get(0);
      result.put("idExpediente", idExpediente);
      result.put("codExp", resumenExpedienteItem[0]);
      result.put("fechaAlta", resumenExpedienteItem[1]);
      if (resumenExpedienteItem[DOS] != null) {
        BigDecimal usuarioAsig = BigDecimal.valueOf(Long.parseLong((String) resumenExpedienteItem[DOS]));
        result.put("usuarioAsig", usuarioAsig);
        int id = usuarioAsig.intValue();
        UsuarioEntity usuarioEntity = usuarioDao.findById(id).orElseThrow();
        result.put("usuarioLogado", usuarioEntity.getNombre() + " " + usuarioEntity.getApellidos());
      } else {
        result.put("usuarioAsig", resumenExpedienteItem[DOS]);
      }
      result.put("idPro", resumenExpedienteItem[3]);
      result.put("nomPro", resumenExpedienteItem[CUATRO]);
      result.put("motSol", resumenExpedienteItem[5]);
      Set<Object> fases = new HashSet<>();
      List<Object> estadosConvert = new ArrayList<>();
      for (Object[] resumenExpedienteRegistro : resumenExpediente) {
        fases.add(resumenExpedienteRegistro[6]);
//        estados.add(resumenExpedienteRegistro[7]);
      }
      for (Object[] resumenExpedienteRegistro : estados) {
//        fases.add(resumenExpedienteRegistro[6]);
        estadosConvert.add(resumenExpedienteRegistro[0]);
      }
      result.put("fases", fases);
      result.put("estados", estadosConvert);
      result.put("intNombre", resumenExpedienteItem[8]);
      result.put("intApellido1", resumenExpedienteItem[9]);
      result.put("intApellido2", resumenExpedienteItem[10]);
      result.put("fechaNacimiento", resumenExpedienteItem[11]);
      result.put("nacionalidad", resumenExpedienteItem[12]);
      result.put("estadoCivil", resumenExpedienteItem[13]);
      result.put("docIdentificacion", resumenExpedienteItem[14]);
      result.put("tipoDocumento", resumenExpedienteItem[15]);
      result.put("idExpGd", resumenExpedienteItem[16]);
      result.put("oriSol", resumenExpedienteItem[17]);

      List<Object[]> tramitesExpediente = procedimientoDao
          .recuperarTramitesProcedimiento(((Number) resumenExpedienteItem[3]).shortValue());
      List<TramiteDto> tramites = new ArrayList<>();
      for (Object[] tramiteExpediente : tramitesExpediente) {
        TramiteDto tramite = new TramiteDto();
        tramite.setIdTramite((Short.valueOf((String) tramiteExpediente[0])));
        tramite.setCodTramite((String) tramiteExpediente[1]);
        tramite.setNomTramite((String) tramiteExpediente[DOS]);
        tramites.add(tramite);
      }
      result.put("listaTramites", tramites);
      result.put("historicoAcciones", this.getHistoricoAccionesEjecutadas(idExpediente));
      result.put("listaAvisosExp", expAviDtos);
      result.put("isAllowed", isAllowed);
      result.put("permModAviso", permModAviso);

      // Observaciones
      result.put("observaciones", observacionesService.getObservacionesExpediente(idExpediente));

      // Plazos.
      result.put("plazosExpediente", plazosService.getPlazosExpedienteByIdExpediente(idExpediente));
      LOG.info("Se han recuperado correctamente los datos resumidos del expediente {} con codExp: {}", idExpediente,
          result.get("codExp"));
      LOG.debug("Fin - expedientesServiceImpl.getResumenExpediente del exp {}", idExpediente);
      return result;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_49).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<ExpedienteDocumentoDto> obtenerDocumentosExpedientesPorCodigos(List<String> codigos,
      BigInteger idExpediente) throws SinacException {
    List<ExpedienteDocumentoEntity> listaDocumentoEntities = expedienteDao
        .obtenerDocumentosExpedientesPorCodigos(codigos, idExpediente);
    List<ExpedienteDocumentoDto> listaExpedienteDocumentoDtos = new ArrayList<>();
    List<ExpedienteNotificacionesDto> listaExpedienteDocumentoNotificacionesDto = new ArrayList<>();
    for (ExpedienteDocumentoEntity doc : listaDocumentoEntities) {
      ExpedienteDocumentoDto expedienteDocumentoDto = expedienteDocumentoMapper.toDto(doc);
      expedienteDocumentoDto
          .setLdvMaestraDtoByIdEstDocLdv(ldvMaestraMapper.toDto(doc.getLdvMaestraEntityByIdEstDocLdv()));
      expedienteDocumentoDto.setDocumentoTipoDto(documentoTipoMapper.toDto(doc.getDocumentoTipoEntity()));
      for (ExpedienteFirmaEntity firma : doc.getExpedienteFirmaEntities().stream().filter(fir -> fir.isFlgActivo())
          .toList()) {
        expedienteDocumentoDto.getExpedienteFirmaDtos().add(expedienteFirmaMapper.toDto(firma));
      }
      for (ExpedienteNotificacionesEntity notificacion : doc.getExpedienteNotificacionesEntities().stream()
          .filter(expnot -> expnot.isFlgActivo()).toList()) {
        listaExpedienteDocumentoNotificacionesDto.add(expedienteNotificacionesMapper.toDto(notificacion));
      }
      Collections.reverse(listaExpedienteDocumentoNotificacionesDto);
      expedienteDocumentoDto.getExpedienteNotificacionesDtos().addAll(listaExpedienteDocumentoNotificacionesDto);

      listaExpedienteDocumentoDtos.add(expedienteDocumentoDto);
    }
    return listaExpedienteDocumentoDtos;
  }

  @Override
  public List<ExpedienteEstadoDto> getExpedienteEstadoByIdExp(BigInteger idExp) throws SinacException {
    List<ExpedienteEstadoEntity> listaExpedienteEstadosEntity = expedienteEstadoDao
        .getEstadosExpedienteSinFlgActivo(idExp);
    return listaExpedienteEstadosEntity.stream().map(ee -> expedienteEstadoMapper.toDto(ee)).toList();
  }

//TODO
  @Override
  public List<ExpedienteEstadoDto> getHistoricoAccionesEjecutadas(final BigInteger idExp) throws SinacException {
    List<Object[]> listaObjectHistoricoAccionesEjecutadas = expedienteEstadoDao.getHistoricoAccionesEjecutadas(idExp);

    List<ExpedienteEstadoDto> listaHistoricoAccionesEjecutadas = new ArrayList<>();

    for (Object[] datosHistorico : listaObjectHistoricoAccionesEjecutadas) {
      Date fechaCreacion = (Date) datosHistorico[0];
      String nomAccion = (String) datosHistorico[1];
      String usuarioJus = (String) datosHistorico[DOS];
      ExpedienteEstadoDto expedienteEstadoDto = new ExpedienteEstadoDto();

      expedienteEstadoDto.setFechaCreacion(fechaCreacion);

      AccionDto accionDto = new AccionDto();
      accionDto.setNomAccion(nomAccion);
      ProcedimientosFasesTramitesOperacionesAccionesDto pftoaDto = new ProcedimientosFasesTramitesOperacionesAccionesDto();
      pftoaDto.setAccionDto(accionDto);
      MaquinaEstadosDto maquinaEstadosDto = new MaquinaEstadosDto();
      maquinaEstadosDto.setAccion(pftoaDto);
      expedienteEstadoDto.setEstado(maquinaEstadosDto);

      UsuarioDto usuario = new UsuarioDto();
      usuario.setUsuarioJus(usuarioJus);
      expedienteEstadoDto.setCreadoPor(usuario);

      listaHistoricoAccionesEjecutadas.add(expedienteEstadoDto);
    }
    return listaHistoricoAccionesEjecutadas;
  }

  @Override
  public String getPlazoArchivoElectronico(BigInteger idExp) throws SinacException {
    StringBuilder plazoExpedienteCerrado = new StringBuilder();

    ProcedimientoEntity procedimiento = recuperarIdPro(idExp);

    PlazoEntity plazoExpediente = plazoDao.getPlazoArchivoElectronico(procedimiento.getIdPro(), TPLA_ARCH);
    String tipoPlazo = plazoExpediente.getLdvMaestraEntityByIdPlazoTieLdv().getNomLdvMae();
    if (tipoPlazo.equalsIgnoreCase(ANIOS)) {
      plazoExpedienteCerrado.append(plazoExpediente.getNumPlazo()).append(ANYOS);
    } else {
      plazoExpedienteCerrado.append(plazoExpediente.getNumPlazo()).append(" ").append(tipoPlazo.toLowerCase());
    }

    return plazoExpedienteCerrado.toString();
  }

  @Override
  public void saveExpedienteFechaCierreArchivo(BigInteger idExp, Date fechaCierre, Date fechaArchivo)
      throws SinacException {
    LOG.info("ExpedientesServiceImpl.saveExpedienteFechaCierreArchivo - Init");

    try {
      if (idExp == null || fechaCierre == null || fechaArchivo == null) {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_50).type(SinacExceptionType.DATA);
      }

      Optional<ExpedienteEntity> expedienteEntity = expedienteDao.findById(idExp);
      if (expedienteEntity.isPresent()) {
        expedienteEntity.get().setFechaCierre(fechaCierre);
        expedienteEntity.get().setFechaArchivo(fechaArchivo);
        expedienteDao.save(expedienteEntity.get());
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_51).logMessageParams(idExp)
            .type(SinacExceptionType.DATA);
      }
    } catch (IllegalArgumentException e) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_52).logMessageParams(e.getMessage())
          .type(SinacExceptionType.DATA);
    } catch (DataAccessException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_53).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }

    LOG.info("ExpedientesServiceImpl.saveExpedienteFechaCierreArchivo - End");

  }

  @Override
  public ExpedienteDto getExpedientebyIdExp(BigInteger idExp) throws SinacException {
    Optional<ExpedienteEntity> expedienteEntity = expedienteDao.findById(idExp);
    if (expedienteEntity.isPresent()) {
      return expedienteMapper.toDto(expedienteEntity.get());
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_54).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  private void setTipoDomicilio(List<PersonasDomiciliosDto> personasDomiciliosDto) {
    for (PersonasDomiciliosDto personaDomicilio : personasDomiciliosDto) {
      setTipoDomicilio(personaDomicilio);
    }
  }

  private void setTipoDomicilio(PersonasDomiciliosDto personaDomicilio) {
    if (personaDomicilio.getPersonaDomicilioDto() != null) {
      if (personaDomicilio.getPersonaDomicilioDto().getPaisDto() == null) {
        personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("0");
      } else if (personaDomicilio.getPersonaDomicilioDto().getPaisDto().getCodPais().equals("724")) {
        personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("1");
      } else {
        personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("2");
      }
    }
  }

  private void setPersonasContactosElectronicos(ExpedienteDto expedienteDto) {
    if (expedienteDto.getInteresado() != null) {
      if (expedienteDto.getInteresado().getPersonasContactosElectronicosDtos() == null
          || expedienteDto.getInteresado().getPersonasContactosElectronicosDtos().isEmpty()) {
        setPersonasContactosDomiciliodDto(expedienteDto.getInteresado());
      }
    }
    if (expedienteDto.getRepresentante1() != null) {
      if (expedienteDto.getRepresentante1().getPersonasContactosElectronicosDtos() == null
          || expedienteDto.getRepresentante1().getPersonasContactosElectronicosDtos().isEmpty()) {
        setPersonasContactosDomiciliodDto(expedienteDto.getRepresentante1());
      }
    }
    if (expedienteDto.getRepresentante2() != null) {
      if (expedienteDto.getRepresentante2().getPersonasContactosElectronicosDtos() == null
          || expedienteDto.getRepresentante2().getPersonasContactosElectronicosDtos().isEmpty()) {
        setPersonasContactosDomiciliodDto(expedienteDto.getRepresentante2());
      }
    }
    if (expedienteDto.getRepresentanteMandato() != null) {
      if (expedienteDto.getRepresentanteMandato().getPersonasContactosElectronicosDtos() == null
          || expedienteDto.getRepresentanteMandato().getPersonasContactosElectronicosDtos().isEmpty()) {
        setPersonasContactosDomiciliodDto(expedienteDto.getRepresentanteMandato());
      }
    }
  }

  private void setPersonasContactosDomiciliodDto(PersonaDto personaDto) {
    // TODO NACHO - Mirar la utilidad de este método.
    List<PersonasContactosElectronicosDto> personasContactosElectronicosDto = new ArrayList<>();
    if (personasContactosElectronicosDto == null || personasContactosElectronicosDto.isEmpty()) {
      personasContactosElectronicosDto = new ArrayList<>();
      personasContactosElectronicosDto.add(new PersonasContactosElectronicosDto());
    }
    if (personasContactosElectronicosDto.get(0).getPersonaContactoElectronicoDto() == null) {
      personasContactosElectronicosDto.get(0).setPersonaContactoElectronicoDto(new PersonaContactoElectronicoDto());
    }
    personaDto.setPersonasContactosElectronicosDtos(personasContactosElectronicosDto);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public int getSecuenciaExpediente(final short idProcedimiento, final short anio) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.getSecuenciaExpediente - Init");

    try {
      expSecDao.setSecuenciaPorId(idProcedimiento, anio);
      return expSecDao.getSecuenciaPorId(idProcedimiento, anio).getNumero();
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_55)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_56)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    } finally {
      LOG.debug("ExpedientesServiceImpl.getSecuenciaExpediente - End");
    }
  }

  @Override
  public final List<String> validateErroresPlantillas(final PlantillaDto plantillaDto) throws SinacException {
    final List<String> errores = new ArrayList<>();
    final DataSource dataSource = nfsManager.getDataSource(plantillaDto.getNomPlantilla() + ".odt",
        plantillaDto.getNfsRuta());
    if (dataSource == null) {
      errores.add("No se ha encontrado la plantilla seleccionada. ");
    }

    return errores;
  }

  // TODO reintento gestor documental
  public DocumentoToSaveDto getDocumentoToSaveDtoReintentoGD(ExpedienteDocumentoDto expedienteDocumentoDto,
      Boolean recuperarContenido) throws SinacException {
    DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
    documentoToSaveDto.setNombre(expedienteDocumentoDto.getNomDoc());
    documentoToSaveDto.setRutaNFS(expedienteDocumentoDto.getNfsRuta());
    // registroEntradaSalida
    documentoToSaveDto.setTipoDocumento(expedienteDocumentoDto.getDocumentoTipoDto().getIdDocTipo());
    documentoToSaveDto.setOrigen(expedienteDocumentoDto.getLdvMaestraDtoByIdOriDocLdv().getIdLdvMae());
    if (expedienteDocumentoDto.getCodGd() != null)
      documentoToSaveDto.setEstadoDocLdvMae(catalogosService.getCatalogoByCod("EDOC-FIR"));
    else
      documentoToSaveDto.setEstadoDocLdvMae(expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv());
    documentoToSaveDto.setEstadoElaboracion(expedienteDocumentoDto.getLdvMaestraDtoByIdEstElaLdv().getIdLdvMae());
    documentoToSaveDto.setOrgano(expedienteDocumentoDto.getLdvMaestraDtoByIdOrgLdv().getIdLdvMae());
    documentoToSaveDto.setIdentificadorGD(expedienteDocumentoDto.getCodGd());
    // error
    if (recuperarContenido) {
      InputStream inputStream = null;
      try {
        inputStream = nfsManager.getDataSource(expedienteDocumentoDto.getNomDoc(), expedienteDocumentoDto.getNfsRuta())
            .getInputStream();
        documentoToSaveDto.setContenido(inputStream.readAllBytes());
      } catch (final IOException | BeansException | ContentRepositoryException exception) {
        LOG.error(String.format("ExpedientesServiceImpl.getDocumentoToSaveDtoReintentoGD - Error: %s",
            Literal.EL_DOCUMENTO + expedienteDocumentoDto.getNomDoc()), exception);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (final IOException exception) {
            LOG.error(String.format("ExpedientesServiceImpl.getDocumentoToSaveDtoReintentoGD - Error: %s",
                Literal.EL_DOCUMENTO + expedienteDocumentoDto.getNomDoc()), exception);
          }
        }
      }
    }
    documentoToSaveDto.setContenidoFirmado(documentosService.getContenido(expedienteDocumentoDto));// contenidoBase64
    documentoToSaveDto.setIdExpDoc(expedienteDocumentoDto.getIdExpDoc());
    documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidado(true);
    documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidoAntivirus(true);
    documentoToSaveDto.getDocumentoFlagsToSaveDto().setCopiadoNFS(true);
    if (documentoToSaveDto.getEstadoDocLdvMae() != null
        && "EDOC-ERE".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())) {
      documentoToSaveDto.getDocumentoFlagsToSaveDto().setFirmado(true);
    } else if (documentoToSaveDto.getEstadoDocLdvMae() != null
        && "EDOC-EGD".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())) {
      documentoToSaveDto.getDocumentoFlagsToSaveDto().setFirmado(true);
      documentoToSaveDto.getDocumentoFlagsToSaveDto().setRegistrado(true);
    }
    documentoToSaveDto.setGenerarRegistro(true);
    documentoToSaveDto.setCreadoPor(expedienteDocumentoDto.getCreadoPor());
    // documentoToSaveDto.setRegistroDtos(expedienteDocumentoDto.getRegistroDtos());
    return documentoToSaveDto;
  }

  @Override
  public DocumentoToSaveDto reintentoSubidaGestorDocumental(DocumentoToSaveDto documentoToSaveDto,
      final ExpedienteDto expedienteDto, final ExpedienteDocumentoDto expedienteDocumentoDto,
      final String nfsPathDocumentosSolicitudes, final Boolean esEntrada, final LdvMaestraDto estadoDoc) {
    LOG.debug("Init - ExpedientesServiceImpl.reintentoSubidaGestorDocumental del documento {} del expediente {}",
        documentoToSaveDto.getIdExpDoc(), expedienteDto.getIdExp());
    boolean isCreateExpediente = (StringUtils.isNotEmpty(documentoToSaveDto.getRutaNFS())
        && documentoToSaveDto.getRutaNFS().contains(nfsPathDocumentosSolicitudes));
    if (documentoToSaveDto.getEstadoDocLdvMae() != null) {
      String codLdvTfir = procedimientosDocumentosTipoDao.findCodLdvTfirByIdExpDoc(expedienteDocumentoDto.getIdExpDoc(),
          expedienteDto.getProcedimientoDto().getIdPro());

      if (esEntrada || (!esEntrada && "TFIR-SEO".equals(codLdvTfir))) {
        if ("EDOC-EFI".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())
            || (!"EDOC-EFI".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())
                && !"EDOC-ERE".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())
                && !"EDOC-EGD".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae()))) {
          LOG.debug("Se va a firmar con sello el documento {} del expediente {}", expedienteDocumentoDto.getIdExpDoc(),
              expedienteDto.getIdExp());
          documentoToSaveDto = documentosService.signDocumento(documentoToSaveDto);
          if (documentoToSaveDto.getContenidoFirmado() != null) {
            documentosService.copyDocumentoNFS(expedienteDocumentoDto, documentoToSaveDto.getContenidoFirmado());
          }
        }
        if ((documentoToSaveDto.getDocumentoFlagsToSaveDto().getFirmado()
            && !documentoToSaveDto.getDocumentoFlagsToSaveDto().getRegistrado())
            || "EDOC-ERE".equals(documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae())) {
          LOG.debug("Se va a generar el registro del documento {} del expediente {}",
              expedienteDocumentoDto.getIdExpDoc(), expedienteDto.getIdExp());
          documentoToSaveDto = documentosService.generateRegistroDocumentoV2(
              esEntrada ? TipoRegistroRegageEnum.ENTRADA : TipoRegistroRegageEnum.SALIDA, documentoToSaveDto);
        }
        LOG.debug("Se va a guardar el documento {} en Gestor Documental para el expediente {}",
            documentoToSaveDto.getIdExpDoc(), expedienteDto.getIdExp());
        documentoToSaveDto = documentosService.saveDocumentoGestorDocumental(
            esEntrada ? TipoRegistroRegageEnum.ENTRADA : TipoRegistroRegageEnum.SALIDA, expedienteDto.getIdExpGd(),
            expedienteDto.getProcedimientoDto().getIdPro(), documentoToSaveDto);
        if (documentoToSaveDto.getDocumentoFlagsToSaveDto().getGuardadoGestorDocumental()) {
          LdvMaestraDto ldvMaestraDto = null;
          if (esEntrada && documentoToSaveDto.getEstadoDocLdvMae() != null
              && !documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae().equals("EDOC-PVA")
              && !documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae().equals("EDOC-VAL")
              && !documentoToSaveDto.getEstadoDocLdvMae().getCodLdvMae().equals("EDOC-INC")) {
            ldvMaestraDto = ldvMaestraMapper.toDto(ldvMaestraDao.findByCodigo("EDOC-PVA"));
            documentoToSaveDto.setEstadoDocLdvMae(ldvMaestraDto);
          } else {
            documentoToSaveDto.setEstadoDocLdvMae(estadoDoc);
          }

        }
      } else if (!esEntrada && "TFIR-NFI".equals(codLdvTfir)) {
        LOG.debug("Se va a generar el registro del documento {} del expediente {}",
            expedienteDocumentoDto.getIdExpDoc(), expedienteDto.getIdExp());
        documentoToSaveDto = documentosService.generateRegistroDocumentoV2(TipoRegistroRegageEnum.SALIDA,
            documentoToSaveDto);
      } else {
        DataHandler contenido = documentosService.getContenido(expedienteDocumentoDto);
        ExpedienteDocumentoDto expedienteDocumentoSalidaDto = saveDocumentoSalida(expedienteDocumentoDto, contenido);
        documentoToSaveDto = this.getDocumentoToSaveDtoReintentoGD(expedienteDocumentoSalidaDto, false);
      }
    }
    documentoToSaveDto = documentosService.saveDocumentoV2(expedienteDto.getIdExp(), documentoToSaveDto,
        isCreateExpediente);

    LOG.debug("End - ExpedientesServiceImpl.reintentoSubidaGestorDocumental del documento {} del expediente {}",
        documentoToSaveDto.getIdExpDoc(), expedienteDto.getIdExp());
    return documentoToSaveDto;
  }

  @Override
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public ExpedienteDocumentoDto saveDocumentoSalida(ExpedienteDocumentoDto expedienteDocumentoDto,
      DataHandler contenido) throws SinacException {
    LOG.info("ExpedientesFacadeImpl.saveDocumentoSalida - Init");

    expedienteDocumentoDto = documentosService
        .getInfoToSaveDocumentoSalidaByIdDocumentoExpediente(expedienteDocumentoDto.getIdExpDoc());

    final ExpedienteDto expedienteDto = expedienteDocumentoDto.getExpedienteDto();
    RegistroDto registroDto = null;
    if (expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv() != null
        && !"EDOC-EGD".equals(expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae())) {

      try {
        registroDto = documentosService.generateRegistroDocumento(TipoRegistroRegageEnum.SALIDA, expedienteDocumentoDto,
            contenido);

      } catch (SinacException sinacException) {
        expedientesFacade.updateEstadoDocumento(expedienteDocumentoDto.getIdExpDoc(),
            ldvMaestraService.getCatalogoByCod("EDOC-ERE"));
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_57)
            .logMessageParams(expedienteDocumentoDto.getNomDoc()).type(SinacExceptionType.DATA);
      }

      if (registroDto != null) {
        expedientesFacade.saveRegistroAux(registroDto);
      }
    }

    // TODO: COMPROBAR QUE EL CONTENIDO NO SEA NULO
    if (expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv() != null
        && expedienteDocumentoDto.getRegistroDtos() != null && !expedienteDocumentoDto.getRegistroDtos().isEmpty()) {
      try {
        solicitudesService.setEstadoSolicitud("Guardando documento salida " + expedienteDocumentoDto.getNomDoc());
        documentosService.saveDocumentoGestorDocumental(TipoRegistroRegageEnum.SALIDA, expedienteDto.getIdExpGd(),
            expedienteDto.getProcedimientoDto().getIdPro(), expedienteDocumentoDto, contenido);

      } catch (SinacException e) {
        expedientesFacade.updateEstadoDocumento(expedienteDocumentoDto.getIdExpDoc(),
            ldvMaestraService.getCatalogoByCod("EDOC-EGD"));
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_58)
            .logMessageParams(expedienteDocumentoDto.getNomDoc()).type(SinacExceptionType.DATA);
      }
      documentosService.deleteDocumentoNFS(expedienteDocumentoDto);
      LOG.info("Se ha guardado el documento {} en gestor documental con el código {}",
          expedienteDocumentoDto.getIdExpDoc(), expedienteDocumentoDto.getCodGd());
      documentosService.updateCodGdAndSetNfsRutaToNullForDocumento(expedienteDocumentoDto.getIdExpDoc(),
          expedienteDocumentoDto.getCodGd());
    }

    LOG.info("ExpedientesFacadeImpl.saveDocumentoSalida - End");

    return expedienteDocumentoDto;
  }

//FUTURE: Método "gesdocObtenerExpedienteEni" a usar cuando se tenga disponible en el conector de milano
//  @Override
//  public DescargaDeDocumentoDto getExpedienteENI(BigInteger idExp) throws SinacException {
//    ExpedienteEntity expedienteEntity = expedienteDao.findById(idExp)
//        .orElseThrow(() -> new SinacException("No se ha podido recuperar el expediente con ID: " + idExp));
//    try {
//      DescargaDeDocumentoDto descargaDeDocumentoDto = null;
//      if (StringUtils.isNotEmpty(expedienteEntity.getIdExpGd())) {
//        // Recuperar el documento del Gestor Documental
//        InputStream is = gestorDocumentalConnector.obtenerExpedienteENI(expedienteEntity.getIdExpGd());
//        if (is != null) {
//          byte[] file = is.readAllBytes();
//          LOG.info("Tamaño del archivo descargado: {} bytes", file.length);
//          descargaDeDocumentoDto = new DescargaDeDocumentoDto(file, expedienteEntity.getCodExp() + "_EXP_ENI");
//        } else {
//          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_59);
//        }
//      }
//      return descargaDeDocumentoDto;
//    } catch (Exception exception) {
//      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_60);
//    }
//  }

  @Override
  public void saveDatosResolucion(BigInteger idExpediente, Date fechaCertificacion, Date fechaPublicacionBoe,
      Date fechaRecepcionAcuerdo, Integer resultadoAcuerdo, LdvMaestraEntity ldvMaestraEntity, String estadoOrigen)
      throws SinacException {
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    if (fechaPublicacionBoe != null) {
      guardaFormularioCamposValida(idExpediente, "FPBOE", df.format(fechaPublicacionBoe));
    }
    if (fechaRecepcionAcuerdo != null) {
      guardaFormularioCamposValida(idExpediente, "FRSGT", df.format(fechaRecepcionAcuerdo));
    }
    if (estadoOrigen != null && !estadoOrigen.isEmpty()) {
      guardaFormularioCamposValida(idExpediente, "ESRTA", estadoOrigen);

    }
    if (resultadoAcuerdo != null) {
      Optional<LdvMaestraEntity> resultadoAcuerdoLdv = ldvMaestraDao.findById(resultadoAcuerdo);
      if (!resultadoAcuerdoLdv.isEmpty()) {
        guardaFormularioCamposValida(idExpediente, "SACCM", resultadoAcuerdoLdv.get().getNomLdvMae());
      }
    }

    ExpedienteEntity expedienteEntity = expedienteDao.findById(idExpediente).get();

    expedienteEntity.setLdvMaestraEntityByIdSentidoResolucionLdv(ldvMaestraEntity);

    expedienteEntity.setFechaResolucion(fechaCertificacion);
    expedienteEntity.setFechaUltMod(new Date());
    expedienteDao.save(expedienteEntity);
  }

  private void guardaFormularioCamposValida(BigInteger idExpediente, String codigo, String valor) {
    FormularioCamposValidaEntity formularioCamposValidaEntity = formularioCamposValidaDao
        .findFormularioCamposValidaEntityByCodigo(codigo);
    ExpedienteFormularioValEntity expedienteFormularioValEntity = new ExpedienteFormularioValEntity();
    expedienteFormularioValEntity.setExpedienteEntity(expedienteDao.findById(idExpediente).get());
    expedienteFormularioValEntity.setFormularioCamposValidaEntity(formularioCamposValidaEntity);
    expedienteFormularioValEntity.setValor(valor);
    expedienteFormularioValEntity.setFechaCreacion(new Date());
    expedienteFormularioValEntity.setFechaIniVig(new Date());
    expedienteFormularioValEntity.setFlgActivo(true);
    expedienteFormularioValDao.save(expedienteFormularioValEntity);
  }

  @Override
  public RegistroEntity getRegistroExpediente(final BigInteger idExp) throws SinacException {
    LOG.debug("ExpedientesServiceImplHtRegistroExpediente - Init");
    RegistroEntity registro = null;

    try {
      registro = registroDao.getRegistroExpediente(idExp);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_61)
          .logMessageParams(idExp).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_62).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("SolicitudesServiceImpl.getFechaEfectosRegistro - End");

    return registro;
  }

  @Override
  public void updateFechaEfectosExp(final Date fechaEfectos, final BigInteger idExp) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.updateFechaEfectosExp - Init");
    try {
      expedienteDao.updateFechaEfectosExp(fechaEfectos, idExp);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_63).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
    LOG.debug("SolicitudesServiceImpl.updateFechaEfectosExp - End");
  }

  /**
   * Resuelve el filtro de las tarjetas de inicio de la pantalla de Busqueda de
   * Expediente. Dependiendo del id de la tarjeta, realizará un filtro u otro.
   *
   *
   * @param busquedaDto
   * @return BusquedaExpedientesDto
   * @throws SinacException
   */
  @Override
  public BusquedaExpedientesDto resolverFiltroTarjetaInicio(BusquedaExpedientesDto busquedaDto) throws SinacException {
    LOG.debug("Init - ExpedientesServiceImpl.resolverFiltroTarjetaInicio del expediente {}", busquedaDto.getCodExp());
    if (busquedaDto.getIdTarjetaInicio() != null) {
      Date fechaActual = new Date();
      switch (busquedaDto.getIdTarjetaInicio()) {
      // Motivo deportistas y razones humanitarias
      case 1:
        LOG.info("La tarjeta de inicio es: Motivo deportistas y razones humanitarias");
        LdvMaestraEntity ldvMaestraEntityMotDepor = ldvMaestraDao.findByCodigo("CN-DEP");
        LdvMaestraEntity ldvMaestraEntityMotHuma = ldvMaestraDao.findByCodigo("CN-HUM");

        List<Integer> listaIdMotivosSolicitud = new ArrayList<>();
        listaIdMotivosSolicitud.add(ldvMaestraEntityMotDepor.getIdLdvMae());
        listaIdMotivosSolicitud.add(ldvMaestraEntityMotHuma.getIdLdvMae());

        busquedaDto.setFiltroDatosSolicitud(true);
        busquedaDto.setListaMotivosSolicitud(listaIdMotivosSolicitud);
        break;
      // Expedientes con Informes Respondidos
      case 2:
        LOG.info("La tarjeta de inicio es: Expedientes con Informes Respondidos");
        busquedaDto.setFiltroSituacionExpediente(true);
        Short idEstadoInfRespondidos = estadoDao.recuperarIdEstadoPorCod("INFC");
        busquedaDto.setIdEstado(idEstadoInfRespondidos);

        break;
      // Expedientes con Carta de Apoyo
      case 3:

        LOG.info("La tarjeta de inicio es: Expedientes con Carta de Apoyo");
        ComboDto comboDto = new ComboDto();
        LdvMaestraEntity ldvMaestraEntityCartaApoyo = ldvMaestraDao.findByCodigo("EDOC-VAL");
        comboDto.setIdCampoDesplegable(ldvMaestraEntityCartaApoyo.getIdLdvMae());
        comboDto.setValorCampoDesplegable(ldvMaestraEntityCartaApoyo.getNomLdvMae());

        TipoDocumentoEstadoDocumentoDto tipoDocumentoEstadoDocumentoDto = new TipoDocumentoEstadoDocumentoDto();
        DocumentoTipoEntity docTipoEntity = documentoTipoDao.recuperarTipoDocPorCod(TIPO_DOC_CARTA_APOYO);
        tipoDocumentoEstadoDocumentoDto.setTipoDocumento(documentoTipoMapper.toDto(docTipoEntity));
        tipoDocumentoEstadoDocumentoDto.setEstadoDocumento(comboDto);

        busquedaDto.setFiltroDatosDocumentacion(true);
        busquedaDto.getListaTipoDocumentoEstadoDocumento().add(tipoDocumentoEstadoDocumentoDto);
        break;
      // Elevados al Consejo de Ministros
      case 4:
        LOG.info("La tarjeta de inicio es: Elevados al Consejo de Ministros");

        Short idEstadoPropuestaElevacion = estadoDao.recuperarIdEstadoPorCod("CMPE");
        busquedaDto.setFiltroSituacionExpediente(true);
        busquedaDto.setIdEstado(idEstadoPropuestaElevacion);
        break;
      // Concedidos
      case 5:
        LOG.info("La tarjeta de inicio es: Concedidos");
        ComboDto comboResolucionDto = new ComboDto();

        LdvMaestraEntity ldvMaestraEntityResolucionCM = ldvMaestraDao.findByCodigo("RES-ACM");
        comboResolucionDto.setIdCampoDesplegable(ldvMaestraEntityResolucionCM.getIdLdvMae());
        comboResolucionDto.setValorCampoDesplegable(ldvMaestraEntityResolucionCM.getNomLdvMae());

        busquedaDto.setFiltroSituacionExpediente(true);
        busquedaDto.setSentidoResolucion(comboResolucionDto);
        break;
      // En Requerimiento.
      case 6:
        LOG.info("La tarjeta de inicio es: En Requerimiento");
        busquedaDto.setFiltroSituacionExpediente(true);
        busquedaDto.setRequerimientoFlgActivo("true");
        break;

      // Informe Cni Pendiente
      case 7:
        LOG.info("La tarjeta de inicio es: Informe Cni Pendiente");
        Short idEstadoInformeCniPendiente = estadoDao.recuperarIdEstadoPorCod(CNIS);
        busquedaDto.setIdEstado(idEstadoInformeCniPendiente);
        break;

      // Informe Cni Recibido
      case 8:
        LOG.info("La tarjeta de inicio es: Informe Cni Recibido");
        Short idEstadoInformeCniRecibido = estadoDao.recuperarIdEstadoPorCod(CNIR);
        busquedaDto.setIdEstado(idEstadoInformeCniRecibido);
        break;

      // Motivo Analfabetismo y mayores de 40 (No incluye el 40)
      case 9:
        LOG.info("La tarjeta de inicio es: Motivo Analfabetismo y mayores de 40");
        LdvMaestraEntity ldvMaestraEntityMotAnalfaMay40 = ldvMaestraDao.findByCodigo("DIC-AN");

        List<Integer> listaIdMotivosSolicitudDicMay40 = new ArrayList<>();
        listaIdMotivosSolicitudDicMay40.add(ldvMaestraEntityMotAnalfaMay40.getIdLdvMae());

        busquedaDto.setListaMotivosSolicitud(listaIdMotivosSolicitudDicMay40);

        Calendar fechaNacimientoMay40 = Calendar.getInstance();
        fechaNacimientoMay40.clear();
        fechaNacimientoMay40.setTime(fechaActual);
        fechaNacimientoMay40.add(Calendar.YEAR, -41);
        Date fechaNacimientoHastaMay40 = fechaNacimientoMay40.getTime();
        busquedaDto.getInteresado().setFechaNacimientoHasta(fechaNacimientoHastaMay40);

        busquedaDto.setFiltroInteresado(true);
        busquedaDto.setFiltroDatosSolicitud(true);
        break;

      // Motivo Analfabetismo y 40 o menos años
      case 10:
        LOG.info("La tarjeta de inicio es: Motivo Analfabetismo y 40 o menos años");
        LdvMaestraEntity ldvMaestraEntityMotAnalfaMenIgu40 = ldvMaestraDao.findByCodigo("DIC-AN");

        List<Integer> listaIdMotivosSolicitudDicMen40 = new ArrayList<>();
        listaIdMotivosSolicitudDicMen40.add(ldvMaestraEntityMotAnalfaMenIgu40.getIdLdvMae());

        busquedaDto.setListaMotivosSolicitud(listaIdMotivosSolicitudDicMen40);

        Calendar fechaNacimientoMen40 = Calendar.getInstance();
        fechaNacimientoMen40.setTime(fechaActual);
        fechaNacimientoMen40.add(Calendar.YEAR, -41);
        Date fechaNacimientoDesdeMen40 = fechaNacimientoMen40.getTime();
        busquedaDto.getInteresado().setFechaNacimientoDesde(fechaNacimientoDesdeMen40);

        busquedaDto.getInteresado().setFechaNacimientoHasta(Calendar.getInstance().getTime());

        busquedaDto.setFiltroInteresado(true);
        busquedaDto.setFiltroDatosSolicitud(true);
        break;

      // Motivo Dificultades de Aprendizaje
      case 11:
        LOG.info("La tarjeta de inicio es: Motivo Dificultades de Aprendizaje");
        LdvMaestraEntity ldvMaestraEntityMotDificultades = ldvMaestraDao.findByCodigo("DIC-DA");
        List<Integer> listaIdMotivosSolicitudDicDific = new ArrayList<>();
        listaIdMotivosSolicitudDicDific.add(ldvMaestraEntityMotDificultades.getIdLdvMae());

        busquedaDto.setListaMotivosSolicitud(listaIdMotivosSolicitudDicDific);

        busquedaDto.setFiltroDatosSolicitud(true);
        busquedaDto.getMotivoSolicitud().setIdLdvMae(ldvMaestraEntityMotDificultades.getIdLdvMae());
        break;

      // Con autorizacion de pruebas adaptadas
      case 12:
        LOG.info("La tarjeta de inicio es: Con autorización de pruebas adaptadas");
        // Pruebas adaptadas
        ComboDto comboDtoPruebasAdap = new ComboDto();
        LdvMaestraEntity ldvMaestraEntityPruebasAdap = ldvMaestraDao.findByCodigo("EDOC-VAL");
        comboDtoPruebasAdap.setIdCampoDesplegable(ldvMaestraEntityPruebasAdap.getIdLdvMae());
        comboDtoPruebasAdap.setValorCampoDesplegable(ldvMaestraEntityPruebasAdap.getNomLdvMae());

        TipoDocumentoEstadoDocumentoDto tipoDocumentoEstadoDocumentoPruebaAdap = new TipoDocumentoEstadoDocumentoDto();
        DocumentoTipoEntity docTipoEntityPruebaAdap = documentoTipoDao.recuperarTipoDocPorCod("RPADA");
        tipoDocumentoEstadoDocumentoPruebaAdap.setTipoDocumento(documentoTipoMapper.toDto(docTipoEntityPruebaAdap));
        tipoDocumentoEstadoDocumentoPruebaAdap.setEstadoDocumento(comboDtoPruebasAdap);
        busquedaDto.getListaTipoDocumentoEstadoDocumento().add(tipoDocumentoEstadoDocumentoPruebaAdap);

        // Concesion pruebas adaptadas
        ComboDto comboDtoConPrueAdap = new ComboDto();
        LdvMaestraEntity ldvMaestraEntityConPrueAdap = ldvMaestraDao.findByCodigo("EDOC-VAL");
        comboDtoConPrueAdap.setIdCampoDesplegable(ldvMaestraEntityConPrueAdap.getIdLdvMae());
        comboDtoConPrueAdap.setValorCampoDesplegable(ldvMaestraEntityConPrueAdap.getNomLdvMae());

        TipoDocumentoEstadoDocumentoDto tipoDocumentoEstadoDocumentoConPrueAdap = new TipoDocumentoEstadoDocumentoDto();
        DocumentoTipoEntity docTipoEntityConPrueAdap = documentoTipoDao.recuperarTipoDocPorCod("RPADE");
        tipoDocumentoEstadoDocumentoConPrueAdap.setTipoDocumento(documentoTipoMapper.toDto(docTipoEntityConPrueAdap));
        tipoDocumentoEstadoDocumentoConPrueAdap.setEstadoDocumento(comboDtoConPrueAdap);
        busquedaDto.getListaTipoDocumentoEstadoDocumento().add(tipoDocumentoEstadoDocumentoConPrueAdap);

        busquedaDto.setFiltroDatosDocumentacion(true);

        break;
      // Más de 6 meses desde la fecha de solicitud
      case 13:
        LOG.info("La tarjeta de inicio es: Más de 6 meses desde la fecha de solicitud");
        Calendar fechaSolicitudSeisMeses = Calendar.getInstance();
        fechaSolicitudSeisMeses.setTime(fechaActual);
        fechaSolicitudSeisMeses.add(Calendar.MONTH, -6);
        busquedaDto.setFechaEfectosHasta(fechaSolicitudSeisMeses.getTime());
        busquedaDto.setFechaEfectosDesde(null);

        busquedaDto.setFiltroDatosSolicitud(true);
        break;

      // Dispensas resueltas último año
      case 14:
        LOG.info("La tarjeta de inicio es: Dispensas resueltas último año");
        Calendar fechaResolucionUltAnio = Calendar.getInstance();
        fechaResolucionUltAnio.setTime(fechaActual);
        fechaResolucionUltAnio.add(Calendar.YEAR, -1);
        busquedaDto.setFechaResolucionDesde(fechaResolucionUltAnio.getTime());
        busquedaDto.setFiltroDatosGenerales(true);
        break;

      // Dispensas Pendientes de resolver
      case 15:
        LOG.info("La tarjeta de inicio es: Dispensas Pendientes de resolver");
        Short idEstadoInfCalificados = estadoDao.recuperarIdEstadoPorCod("INFC");
        busquedaDto.setIdEstado(idEstadoInfCalificados);
        busquedaDto.setFiltroSituacionExpediente(true);
        break;

      // Diferentes tipos de tarjetas de inicio de Recurso Reposicion
      case 16, 17, 18, 19, 20, 21, 22, 23, 24:
        LOG.info("Tarjetas de inicio de RR");
        break;

      // Informe Mde Pendiente
      case 25:
        LOG.info("La tarjeta de inicio es: Informe Mde Pendiente");
        Short idEstadoInformeMdePendiente = estadoDao.recuperarIdEstadoPorCod("MDES");
        busquedaDto.setIdEstado(idEstadoInformeMdePendiente);
        break;

      // Informe Mde Recibido
      case 26:
        LOG.info("La tarjeta de inicio es: Informe Mde Recibido");
        Short idEstadoInformeMdeRecibido = estadoDao.recuperarIdEstadoPorCod("MDER");
        busquedaDto.setIdEstado(idEstadoInformeMdeRecibido);
        break;

      default:
        break;

      }

    }

    LOG.debug("Fin - ExpedientesServiceImpl.resolverFiltroTarjetaInicio del expediente {}", busquedaDto.getCodExp());
    return busquedaDto;
  }

  @Override
  public Map<String, Object> getIdExpCodProceByCodExpediente(String codExpediente, String tipoInforme)
      throws SinacException {
    LOG.debug("Init - ExpedientesServiceImpl.getIdExpCodProceByCodExpediente para el expediente {}", codExpediente);
    ExpedienteEntity expedienteEntity = expedienteDao.getExpedienteByCodExpediente(codExpediente);
    if (expedienteEntity != null) {
      List<ExpedienteInformeEntity> lista = expedienteEntity.getExpedienteInformeEntities().stream()
          .filter(expinf -> expinf.isFlgActivo())
          .filter(expinf -> expinf.getLdvMaestraEntityByIdInfLdv().getCodLdvMae().equals(tipoInforme)).toList();
      if (!lista.isEmpty()) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("idExp", expedienteEntity.getIdExp());
        ExpedienteInformeEntity expedienteInformeEntity = lista.get(0);
        if (tipoInforme.equals(COD_LDV_TIPO_INFORME_MJU)) {
          mapa.put("idExpInfMju", expedienteInformeEntity.getIdExpInf());
        } else if (tipoInforme.equals("TINF-DGP")) {
          mapa.put("idExpInfDgp", expedienteInformeEntity.getIdExpInf());
        }
        mapa.put("codProcedimiento", expedienteEntity.getProcedimientoEntity().getCodPro());
        mapa.put("idPro", expedienteEntity.getProcedimientoEntity().getIdPro());
        LOG.debug("End - ExpedientesServiceImpl.getIdExpCodProceByCodExpediente - mapa: {}", mapa);
        return mapa;
      }
    }
    throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_64)
        .logMessageParams(codExpediente, tipoInforme.substring(CINCO)).type(SinacExceptionType.DATA);
  }

  @Override
  public DatosSolicitudInformeMjuDto obtenerDatosSolicitudInformeMju(final BigInteger idExpediente)
      throws SinacException {
    LOG.debug("ExpedientesServiceImpl.obtenerDatosSolicitudInformeMju - Init");
    DatosSolicitudInformeMjuDto datosSolicitudInformeMjuDto = new DatosSolicitudInformeMjuDto();
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpCodTipoInformeActivo(idExpediente, COD_LDV_TIPO_INFORME_MJU);
    datosSolicitudInformeMjuDto
        .setCodigoProcedimiento(expedienteInformeEntity.getExpedienteEntity().getProcedimientoEntity().getCodPro());
    datosSolicitudInformeMjuDto.setReferencia(expedienteInformeEntity.getExpedienteEntity().getCodExp());
    PersonaEntity interesado = expedienteInformeEntity.getExpedienteEntity().getExpedientesPersonasEntities().stream()
        .filter(p -> p.isFlgActivo()).filter(p -> p.getLdvMaestraEntity().getCodLdvMae().equals(COD_PER_INTE))
        .map(ExpedientesPersonasEntity::getPersonaEntity).toList().get(0);
    checkDatosSolicitudInformeMjuDtoInteresado(datosSolicitudInformeMjuDto, interesado);
    PersonaIdentificaEntity identificacionInteresado = interesado.getPersonasIdentificaEntities().stream()
        .filter(PersonaIdentificaEntity::isFlgActivo).toList().get(0);
    PersonaDomicilioEntity personaDomicilio = interesado.getPersonasDomiciliosEntity().stream()
        .filter(PersonasDomiciliosEntity::isFlgActivo).map(PersonasDomiciliosEntity::getPersonaDomicilioEntity).toList()
        .get(0);
    checkDatosSolicitudInformeMjuDtoIdentificacionDomicilio(datosSolicitudInformeMjuDto, identificacionInteresado,
        personaDomicilio);
    LOG.debug("SolicitudesServiceImpl.obtenerDatosSolicitudInformeMju - End");
    return datosSolicitudInformeMjuDto;
  }

  private static void checkDatosSolicitudInformeMjuDtoInteresado(
      DatosSolicitudInformeMjuDto datosSolicitudInformeMjuDto, PersonaEntity interesado) {
    datosSolicitudInformeMjuDto.setPrimerApellido(interesado.getApellido1());
    datosSolicitudInformeMjuDto.setSegundoApellido(interesado.getApellido2());
    datosSolicitudInformeMjuDto.setNombre(interesado.getNombre());
    String pattern = "dd/MM/yyyy";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    String fechaNacimiento = null;
    if (interesado.getFechaNacimiento() != null) {
      fechaNacimiento = df.format(interesado.getFechaNacimiento());
    }
    datosSolicitudInformeMjuDto.setFechaNacimiento(fechaNacimiento);
    if (interesado.getProgenitor1() != null && interesado.getProgenitor1().length() > 50) {
      datosSolicitudInformeMjuDto.setNombrePadre(interesado.getProgenitor1().trim().substring(0, 49));
    } else if (interesado.getProgenitor1() != null && !interesado.getProgenitor1().trim().isEmpty()) {
      datosSolicitudInformeMjuDto.setNombrePadre(interesado.getProgenitor1());
    }
    if (interesado.getProgenitor2() != null && interesado.getProgenitor2().length() > 50) {
      datosSolicitudInformeMjuDto.setNombreMadre(interesado.getProgenitor1().trim().substring(0, 49));
    } else if (interesado.getProgenitor2() != null && !interesado.getProgenitor2().trim().isEmpty()) {
      datosSolicitudInformeMjuDto.setNombreMadre(interesado.getProgenitor2());
    }
    String paisNacimiento = null;
    if (interesado.getPaisNacimiento() != null) {
      paisNacimiento = interesado.getPaisNacimiento().getNomPaisMju();
    }
    String provinciaNacimiento = null;
    if (interesado.getProvNac() != null) {
      provinciaNacimiento = interesado.getProvNac().getCodProvincia();
    }
    datosSolicitudInformeMjuDto.setProvinciaNacimiento(provinciaNacimiento);
    String localidadNacimiento = null;
    if (interesado.getLocalNac() != null) {
      localidadNacimiento = interesado.getLocalNac().getNomMunicipio();
    }
    datosSolicitudInformeMjuDto.setLocalidadNacimiento(localidadNacimiento);
    String paisNacionalidad = null;
    if (interesado.getNacionalidad() != null) {
      paisNacionalidad = interesado.getNacionalidad().getNomPaisMju();
    }
    datosSolicitudInformeMjuDto.setPaisNacionalidad(paisNacionalidad);
    datosSolicitudInformeMjuDto.setPaisNacimiento(paisNacimiento);
  }

  private static void checkDatosSolicitudInformeMjuDtoIdentificacionDomicilio(
      DatosSolicitudInformeMjuDto datosSolicitudInformeMjuDto, PersonaIdentificaEntity identificacionInteresado,
      PersonaDomicilioEntity personaDomicilio) {
    if (identificacionInteresado.getLdvMaestraEntity().getCodLdvMae().equals("DID-DNI")
        || identificacionInteresado.getLdvMaestraEntity().getCodLdvMae().equals("DID-PAS")) {
      datosSolicitudInformeMjuDto.setDniPasaporte(identificacionInteresado.getNumAcreditacion());
    } else if (identificacionInteresado.getLdvMaestraEntity().getCodLdvMae().equals("DID-NIE")) {
      datosSolicitudInformeMjuDto.setNie(identificacionInteresado.getNumAcreditacion());
    }
    String domicilio = "";
    if (personaDomicilio.getTipoVia() != null) {
      domicilio += personaDomicilio.getTipoVia().getNomTipoVia() + " ";
    }
    if (personaDomicilio.getNomVia() != null) {
      domicilio += personaDomicilio.getNomVia();
    }
    if (personaDomicilio.getMunicipios() != null) {
      if (!domicilio.isEmpty()) {
        domicilio += " - ";
      }
      domicilio += personaDomicilio.getMunicipios().getNomMunicipio();
    }
    if (domicilio.isEmpty() && personaDomicilio.getLugarResidencia() != null) {
      domicilio = personaDomicilio.getLugarResidencia();
    }
    datosSolicitudInformeMjuDto.setDomicilio(domicilio);
  }

  @Override
  public void cambiarEstadoInformesAsolicitado(String nombreArchivo, String codigoEstado, String tipoInforme,
      BigInteger idExp) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.cambiarEstadoInformesAsolicitado - Init");
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, tipoInforme);
    LdvMaestraEntity estadoInforme = ldvMaestraDao.findByCodigo(codigoEstado);
    expedienteInformeEntity.setIdSolicitudInforme(nombreArchivo);
    expedienteInformeEntity.setFechaSolicitud(new Date());
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(estadoInforme);
    expedienteInformeDao.save(expedienteInformeEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)), codigoEstado);
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_65).type(SinacExceptionType.DATA);
    }
    LOG.debug("ExpedientesServiceImpl.cambiarEstadoInformesAsolicitado - End");
  }

  @Override
  public boolean existeExpedienteInformesMjuFichero(String nombreArchivo) throws SinacException {
    return !expedienteInformesMjuFicherosDao.getExpedienteInformesMjuFicheroByNomFichero(nombreArchivo).isEmpty();
  }

  @Override
  public ExpedienteInformesMjuFicherosEntity guardaExpedienteInformesMjuFicheros(String nombreArchivo,
      String codigoEstado) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.guardaExpedienteInformesMjuFicheros - Init");
    List<ExpedienteInformeEntity> listaExpedienteInforme = expedienteInformeDao
        .getExpedienteInformesByIdSolicitudInforme(nombreArchivo);
    LdvMaestraEntity estadoFichero = ldvMaestraDao.findByCodigo(codigoEstado);
    ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFicherosEntity = new ExpedienteInformesMjuFicherosEntity();
    expedienteInformesMjuFicherosEntity.setFechaSolicitud(new Date());
    expedienteInformesMjuFicherosEntity.setNomFichero(nombreArchivo);
    expedienteInformesMjuFicherosEntity.setIdEstFichLdv(estadoFichero);
    expedienteInformesMjuFicherosEntity = expedienteInformesMjuFicherosDao.save(expedienteInformesMjuFicherosEntity);
    for (ExpedienteInformeEntity expedienteInformeEntity : listaExpedienteInforme) {
      ExpedienteInformesMjuFicherosDatosEntity expedienteInformesMjuFicherosDatosEntity = new ExpedienteInformesMjuFicherosDatosEntity();
      expedienteInformesMjuFicherosDatosEntity.setExpedienteInforme(expedienteInformeEntity);
      expedienteInformesMjuFicherosDatosEntity
          .setExpedienteInformeMjuFicheroSolicitudEntity(expedienteInformesMjuFicherosEntity);
      expedienteInformesMjuFicherosDatosDao.save(expedienteInformesMjuFicherosDatosEntity);
    }
    LOG.debug("ExpedientesServiceImpl.guardaExpedienteInformesMjuFicheros - End");
    return expedienteInformesMjuFicherosEntity;
  }

  @Override
  public void guardaRespuestaInformeMjuPenados(ArchivoFtpDto archivoFtpDto) {
    LOG.debug("ExpedientesServiceImpl.guardaRespuestaInformeMjuPenados - Init");
    LOG.debug("NombreArchivo: {}", archivoFtpDto.getNomFichZip());
    ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFicheroRespuesta;
    List<ExpedienteInformesMjuFicherosEntity> expedienteInformesMjuFicherosList = expedienteInformesMjuFicherosDao
        .getExpedienteInformesMjuFicheroByNomFichero(archivoFtpDto.getNomFichZip());
    // Obtenemos la ldv del sentido informe
    LdvMaestraEntity sentidoInforme = ldvMaestraDao.findByCodigo(archivoFtpDto.getSentidoInforme());
    LOG.error("guardaRespuestaInformeMjuPenados archivoFtpDto: {}", archivoFtpDto);
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpInf(archivoFtpDto.getIdExpInfMju());
    LOG.error("guardaRespuestaInformeMjuPenados expedienteInformeEntity: {}", expedienteInformeEntity);
    expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(sentidoInforme);
    expedienteInformeEntity.setFechaRecepcion(new Date());
    // vinculamos el expediente documento con el informe
    LOG.error("guardaRespuestaInformeMjuPenados expedienteDocumentoEntity: {}", archivoFtpDto.getIdExpDoc());
    if (archivoFtpDto.getIdExpDoc() != null) {
      BigInteger idExpDoc = archivoFtpDto.getIdExpDoc();
      ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoDao
          .findExpedienteDocumentoById(idExpDoc);
      LOG.error("guardaRespuestaInformeMjuPenados expedienteDocumentoEntity: {}", expedienteDocumentoEntity);
      expedienteInformeEntity.setExpedienteDocumentoEntity(expedienteDocumentoEntity);
      expedienteInformeEntity = expedienteInformeDao.save(expedienteInformeEntity);

    }
    // actualizamos el estado del informe
    if (sentidoInforme.getCodLdvMae().equals(Constantes.SentidoInformeMju.SENTIDO_INF_RECHAZADO)) {
      LdvMaestraEntity estadoInforme = ldvMaestraDao.findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECHAZADO);
      expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(estadoInforme);
    } else if (sentidoInforme.getCodLdvMae().equals(Constantes.SentidoInformeMju.SENTIDO_INF_ANTECEDENTES)
        || sentidoInforme.getCodLdvMae().equals(Constantes.SentidoInformeMju.SENTIDO_INF_NO_ANTECEDENTES)) {
      LdvMaestraEntity estadoInforme = ldvMaestraDao.findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO);
      expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(estadoInforme);
    }
    // Plazos.
    String codPlazoCaducidadInforme = plazosService.getCodPlazoCaducidadInformeByCodTipoInforme(
        expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae());
    PlazoDto plazoDto = plazosService.getPlazoByIdProcedimientoAndCodTipoPlazo(
        expedienteInformeEntity.getExpedienteEntity().getProcedimientoEntity().getIdPro(), codPlazoCaducidadInforme);
    if (archivoFtpDto.getFechaCreacionPdfMju() != null) {
      expedienteInformeEntity.setFechaEmisionInf(archivoFtpDto.getFechaCreacionPdfMju());
    } else if (archivoFtpDto.getFechaCreacionRprMju() != null) {
      expedienteInformeEntity.setFechaEmisionInf(archivoFtpDto.getFechaCreacionRprMju());
    } else {
      expedienteInformeEntity.setFechaEmisionInf(new Date());
    }
    expedienteInformeEntity.setFechaRecepcion(new Date());
    try {
      if (plazoDto != null) {
        expedienteInformeEntity.setFechaCaducidad(
            Utilidades.sumarUnidadTiempoAFecha(plazoDto.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(),
                expedienteInformeEntity.getFechaEmisionInf(), plazoDto.getNumPlazo()));
      }
    } catch (ParseException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_66).type(SinacExceptionType.DATA);
    }
    LOG.error("guardaRespuestaInformeMjuPenados expedienteInformeEntity: {}", expedienteInformeEntity);
    expedienteInformeEntity = expedienteInformeDao.save(expedienteInformeEntity);
    // Si el registro del fichero no existe lo creamos
    if (expedienteInformesMjuFicherosList.isEmpty()) {
      expedienteInformesMjuFicheroRespuesta = guardaExpedienteInformesMjuFicheros(archivoFtpDto.getNomFichZip(),
          "EFICH-ENT");
    } else {
      expedienteInformesMjuFicheroRespuesta = expedienteInformesMjuFicherosList.get(0);
    }
    // Obtenemos el registro del fichero de solicitud
    ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFicheroSolicitud = expedienteInformeEntity
        .getExpedienteInformesMjuFicherosDatosEntity().stream()
        .map(datos -> datos.getExpedienteInformeMjuFicheroSolicitudEntity()).filter(fichero -> fichero.isFlgActivo())
        .distinct().toList().get(0);
    // registramos los datos
    crearInformeMjuFicheroDatos(expedienteInformeEntity, archivoFtpDto, expedienteInformesMjuFicheroSolicitud,
        expedienteInformesMjuFicheroRespuesta);

    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    BigInteger idExp = expedienteInformeEntity.getExpedienteEntity().getIdExp();
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_67).type(SinacExceptionType.DATA);
    }

    LOG.debug("ExpedientesServiceImpl.guardaRespuestaInformeMjuPenados - End");
  }

  private ExpedienteInformesMjuFicherosDatosEntity crearInformeMjuFicheroDatos(
      ExpedienteInformeEntity expedienteInformeRespuesta, ArchivoFtpDto archivoFtpDto,
      ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFicheroSolicitud,
      ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFicheroRespuesta) {
    for (ExpedienteInformesMjuFicherosDatosEntity item : expedienteInformeRespuesta
        .getExpedienteInformesMjuFicherosDatosEntity().stream().filter(datos -> datos.isFlgActivo()).toList()) {
      item.setFlgActivo(false);
      expedienteInformesMjuFicherosDatosDao.save(item);
    }
    ExpedienteInformesMjuFicherosDatosEntity expedienteInformesMjuFicherosDatosNuevaLinea = new ExpedienteInformesMjuFicherosDatosEntity();
    expedienteInformesMjuFicherosDatosNuevaLinea.setCodRespuesta(archivoFtpDto.getCodRespuesta());
    expedienteInformesMjuFicherosDatosNuevaLinea.setEspMotRechazo(archivoFtpDto.getEspMotRechazo());
    expedienteInformesMjuFicherosDatosNuevaLinea.setEspRes(archivoFtpDto.getEspRes());
    expedienteInformesMjuFicherosDatosNuevaLinea.setEuMotRechazo(archivoFtpDto.getEuMotRechazo());
    expedienteInformesMjuFicherosDatosNuevaLinea.setEuRes(archivoFtpDto.getEuRes());
    expedienteInformesMjuFicherosDatosNuevaLinea.setNomFichRespuesta(archivoFtpDto.getNomFichRespuesta());
    expedienteInformesMjuFicherosDatosNuevaLinea.setExpedienteInforme(expedienteInformeRespuesta);
    expedienteInformesMjuFicherosDatosNuevaLinea
        .setExpedienteInformeMjuFicheroSolicitudEntity(expedienteInformesMjuFicheroSolicitud);
    expedienteInformesMjuFicherosDatosNuevaLinea
        .setExpedienteInformeMjuFicheroRespuestaEntity(expedienteInformesMjuFicheroRespuesta);
    expedienteInformesMjuFicherosDatosNuevaLinea.setNomFichRespuesta(archivoFtpDto.getNomFichRespuesta());
    if (archivoFtpDto.getFechaCreacionPdfMju() != null) {
      expedienteInformesMjuFicherosDatosNuevaLinea.setFechaCreacionMju(archivoFtpDto.getFechaCreacionPdfMju());
    } else {
      expedienteInformesMjuFicherosDatosNuevaLinea.setFechaCreacionMju(archivoFtpDto.getFechaCreacionRprMju());
    }
    return expedienteInformesMjuFicherosDatosDao.save(expedienteInformesMjuFicherosDatosNuevaLinea);
  }

  @Override
  public BigInteger getIdExpedienteByCodExpediente(String codigoExpediente) {
    LOG.debug("ExpedientesServiceImpl.getIdExpedienteByCodExpediente - Init");
    ExpedienteEntity expedienteEntity = expedienteDao.getExpedienteByCodExpediente(codigoExpediente);
    LOG.debug("ExpedientesServiceImpl.getIdExpedienteByCodExpediente - End");
    if (expedienteEntity != null) {
      return expedienteEntity.getIdExp();
    } else {
      return null;
    }
  }

  @Override
  public void actualizarEstadoArchivoFtp(String nombreArchivo, String codigoEstado) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.actualizarEstadoArchivoFtp - Init");
    LdvMaestraEntity estadoFichero = ldvMaestraDao.findByCodigo(codigoEstado);
    List<ExpedienteInformesMjuFicherosEntity> expedienteInformesMjuFicherosList = expedienteInformesMjuFicherosDao
        .getExpedienteInformesMjuFicheroByNomFichero(nombreArchivo);
    if (expedienteInformesMjuFicherosList.isEmpty()) {
      LOG.debug("creandoEstadoArchivoFtp - expedienteInformesMjuFicherosList {}", expedienteInformesMjuFicherosList);
      ExpedienteInformesMjuFicherosEntity nuevoExpedienteInformeMjuFichero = new ExpedienteInformesMjuFicherosEntity();
      nuevoExpedienteInformeMjuFichero.setIdEstFichLdv(estadoFichero);
      nuevoExpedienteInformeMjuFichero.setNomFichero(nombreArchivo);
      nuevoExpedienteInformeMjuFichero.setFechaSolicitud(new Date());
      expedienteInformesMjuFicherosDao.save(nuevoExpedienteInformeMjuFichero);
    } else {
      LOG.debug("actualizarEstadoArchivoFtp - expedienteInformesMjuFicherosList {}", expedienteInformesMjuFicherosList);
      for (ExpedienteInformesMjuFicherosEntity expedienteInformesMjuFichero : expedienteInformesMjuFicherosList) {
        expedienteInformesMjuFichero.setIdEstFichLdv(estadoFichero);
        expedienteInformesMjuFicherosDao.save(expedienteInformesMjuFichero);
      }
    }
    LOG.debug("ExpedientesServiceImpl.actualizarEstadoArchivoFtp - End");
  }

  @Override
  public List<BigInteger> getIdsExpedienteByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme,
      Integer maxItem) throws SinacException {
    PageRequest pageRequest = null;
    if (maxItem != null) {
      pageRequest = PageRequest.of(0, maxItem);
    }
    return expedienteInformeDao.getIdsExpedienteByCodEstadoCodTipoInforme(codEstado, codTipoInforme, pageRequest);
  }

  @Override
  public boolean actualizaInformeDgpRechazado(BigInteger idExp, String codigoEstado, String codigoEstadoSec,
      String literalError, String codigoPeticionRespuesta, boolean alta) throws SinacException {
    boolean rechazado = false;
    LOG.debug("ExpedientesServiceImpl.actualizaInformeDgpError - Init");
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, Constantes.TiposInforme.TIPO_INFORME_DGP);

    LdvMaestraEntity estadoInforme = ldvMaestraDao.findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECHAZADO);
    ExpedienteInformeDgpEntity expedienteInformeDgpEntity = expedienteInformeDgpDao
        .getExpedienteInformeDgpByIdExpedienteInforme(expedienteInformeEntity.getIdExpInf());
    if (expedienteInformeDgpEntity == null) {
      expedienteInformeDgpEntity = new ExpedienteInformeDgpEntity();
      expedienteInformeEntity.setFechaSolicitud(new Date());
      expedienteInformeEntity.setIdSolicitudInforme(codigoPeticionRespuesta);
      expedienteInformeDgpEntity.setCodEstadoAlta(codigoEstado);
      expedienteInformeDgpEntity.setLiteralErrAlta(literalError);
      expedienteInformeDgpEntity.setCodEstadoAltaSec(codigoEstadoSec);
    } else {
      expedienteInformeEntity.setFechaRecepcion(new Date());
      expedienteInformeDgpEntity.setCodEstRes(codigoEstado);
      expedienteInformeDgpEntity.setLiteralErrRes(literalError);
      expedienteInformeDgpEntity.setCodEstadoSec(codigoEstadoSec);
      if ("EINF-PEN".equals(expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae())) {
        rechazado = true;
      }

    }
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(estadoInforme);
    expedienteInformeDgpEntity.setExpedienteInformeEntity(expedienteInformeEntity);
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    expedienteInformeDao.save(expedienteInformeEntity);
    expedienteInformeDgpDao.save(expedienteInformeDgpEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)), estadoInforme.getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_68).type(SinacExceptionType.DATA);
    }

    LOG.debug("ExpedientesServiceImpl.actualizaInformeDgpError - Fin");
    return rechazado;
  }

  @Override
  public ExpedienteInformeDgpDto saveExpedienteInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgp)
      throws SinacException {
    try {
      ExpedienteInformeDgpEntity expedienteInformeDgpEntityAnterior = expedienteInformeDgpDao
          .getExpedienteInformeDgpByIdExpedienteInforme(expedienteInformeDgp.getExpedienteInformeDto().getIdExpInf());
      ExpedienteInformeDgpEntity expedienteInformeDgpEntity = expedienteInformeDgpMapper.toEntity(expedienteInformeDgp);
      if (expedienteInformeDgpEntityAnterior != null) {
        expedienteInformeDgpEntity.setFlgActivo(true);
        expedienteInformeDgpEntity.setFechaIniVig(expedienteInformeDgpEntityAnterior.getFechaIniVig());
        expedienteInformeDgpEntity.setCreadoPor(expedienteInformeDgpEntityAnterior.getCreadoPor());
        expedienteInformeDgpEntity.setFechaCreacion(expedienteInformeDgpEntityAnterior.getFechaCreacion());
        expedienteInformeDgpEntity.setCodEstadoAlta(expedienteInformeDgpEntityAnterior.getCodEstadoAlta());
        expedienteInformeDgpEntity.setCodEstadoAltaSec(expedienteInformeDgpEntityAnterior.getCodEstadoAltaSec());
        expedienteInformeDgpEntity.setLiteralErrAlta(expedienteInformeDgpEntityAnterior.getLiteralErrAlta());
      }
      expedienteInformeDgpEntity = expedienteInformeDgpDao.save(expedienteInformeDgpEntity);
      return expedienteInformeDgpMapper.toDto(expedienteInformeDgpEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_69).type(SinacExceptionType.DATA);
    }

  }

  @Override
  public void saveRenovacionDni(RenovacionDniDto renovacionDniDto) throws SinacException {
    try {
      RenovacionDniEntity renovacionDniEntity = renovacionDniMapper.toEntity(renovacionDniDto);
      renovacionDniEntity.setExpedienteInformeDgpEntity(
          expedienteInformeDgpMapper.toEntity(renovacionDniDto.getExpedienteInformeDgpDto()));
      renovacionDniDao.save(renovacionDniEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_70).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void saveExpedienteInfomeDgpTramite(ExpedienteInformeDgpTramiteDto expedienteInformeDgpTramite)
      throws SinacException {
    try {
      expedienteInformeDgpTramitesDao.save(expedienteInformeDgpTramitesMapper.toEntity(expedienteInformeDgpTramite));
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_71).type(SinacExceptionType.DATA);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
  public void informeRecibido(BigInteger idExp, BigInteger idExpInforme,
      ExpedienteInformeDgpDto expedienteInformeDgpDto, Date fechaEmision, Date fechaRecepcion, String sentido,
      BigInteger idExpedienteDocumento) throws SinacException {

    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeDao.findById(idExpInforme).orElseThrow(
        () -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_124).logMessageParams(idExpInforme));

    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao
        .findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO);

    // Plazos.
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    String codPlazoCaducidadInforme = plazosService.getCodPlazoCaducidadInformeByCodTipoInforme(tipoInforme);
    PlazoDto plazoDto = plazosService.getPlazoByIdProcedimientoAndCodTipoPlazo(recuperarIdPro(idExp).getIdPro(),
        codPlazoCaducidadInforme);

    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv());
    expedienteInformeEntity.setFechaSolicitud(expedienteInformeEntity.getFechaSolicitud());
    try {

      expedienteInformeEntity.setFechaEmisionInf(fechaEmision);
      expedienteInformeEntity.setFechaRecepcion(fechaRecepcion);
      if (plazoDto != null) {
        expedienteInformeEntity.setFechaCaducidad(
            Utilidades.sumarUnidadTiempoAFecha(plazoDto.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(),
                expedienteInformeEntity.getFechaRecepcion(), plazoDto.getNumPlazo()));
      }
    } catch (ParseException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_72).type(SinacExceptionType.DATA);
    }
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);
    if (tipoInforme.equals("TINF-CNI")) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(ldvMaestraDao.findByCodigo(sentido));
    }
    if (tipoInforme.equals(COD_LDV_TIPO_INFORME_MJU)) {
      expedienteInformeEntity.setLdvMaestraEntityByIdSentidoInfLdv(ldvMaestraDao.findByCodigo(sentido));
    }

    if (idExpedienteDocumento != null) {
      expedienteInformeEntity.setExpedienteDocumentoEntity(expedienteDocumentoDao.findById(idExpedienteDocumento)
          .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_126)
              .logMessageParams(idExpedienteDocumento)));
    }
    expedienteInformeEntity.setFlgActivo(true);
    expedienteInformeDao.save(expedienteInformeEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_73).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void desactivarInformesActivosError(BigInteger idExp, String tipoInforme) throws SinacException {
    ExpedienteInformeEntity expedienteInformeActivoEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, tipoInforme);
    LdvMaestraEntity estado = null;
    if (expedienteInformeActivoEntity != null) {
      estado = expedienteInformeActivoEntity.getLdvMaestraEntityByIdEstInfLdv();
    }
    if (estado != null && (estado.getCodLdvMae().equals(Constantes.EstadosInforme.ESTADO_INFORME_RECHAZADO)
        || estado.getCodLdvMae().equals(Constantes.EstadosInforme.ESTADO_INFORME_CADUCADO))) {
      expedienteInformeActivoEntity.setFlgActivo(false);
      expedienteInformeActivoEntity.setFechaFinVig(new Date());
      expedienteInformeDao.save(expedienteInformeActivoEntity);
    } else if (estado != null
        && (tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_DGP)
            || tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_MJU))
        && estado.getCodLdvMae().equals(Constantes.EstadosInforme.ESTADO_INFORME_PENDIENTE)) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_74);
    } else if (estado != null && tipoInforme.equals(Constantes.TiposInforme.TIPO_INFORME_CNI)
        && estado.getCodLdvMae().equals(Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO)) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_75);
    }
  }

  @Override
  public List<BigInteger> getIdsExpedienteInformesByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme)
      throws SinacException {
    return expedienteInformeDao.getIdsExpedienteInformesByCodEstadoCodTipoInforme(codEstado, codTipoInforme);
  }

  @Override
  public TitularDto getDatosSolicitudInformeDgp(final BigInteger idExpInf) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.getDatosSolicitudInformeDgp - Init");
    TitularDto titularDto = new TitularDto();
    try {
      ExpedienteEntity expediente = expedienteDao.getExpedienteByIdExpedienteInforme(idExpInf);
      titularDto.setIdExpediente(expediente.getIdExp());
      titularDto.setCodigoProcedimiento(expediente.getProcedimientoEntity().getCodPro());
      titularDto.setIdProcedimiento(expediente.getProcedimientoEntity().getIdPro());
      // Obtener interesado
      PersonaEntity interesadoEntity = expediente.getExpedientesPersonasEntities().stream().filter(p -> p.isFlgActivo())
          .filter(p -> p.getLdvMaestraEntity().getCodLdvMae().equals(COD_PER_INTE))
          .map(ExpedientesPersonasEntity::getPersonaEntity).toList().get(0);
      ExpedienteDto expedienteDto = expedienteMapper.toDto(expediente);
      setInfoTitular(titularDto, expedienteDto, interesadoEntity);
      // Obtener contacto electronico
      List<PersonasContactosElectronicosEntity> personasContactosElectronicos = interesadoEntity
          .getPersonasContactosElectronicosEntity().stream().filter(p -> p.isFlgActivo()).toList();
      if (!personasContactosElectronicos.isEmpty()) {
        titularDto.setTelefonoFijo(getStringCheckSize(
            personasContactosElectronicos.get(0).getPersonaContactoElectronicoEntity().getTelFijo(), 20));
        titularDto.setTelefonoMovil(getStringCheckSize(
            personasContactosElectronicos.get(0).getPersonaContactoElectronicoEntity().getTelMovil(), 20));
      }
      // Obtener identificación
      PersonaIdentificaEntity personaIdentifica = interesadoEntity.getPersonasIdentificaEntities().stream()
          .filter(p -> p.isFlgActivo() && p.isFlgPrincipal()).toList().get(0);
      titularDto.setIdentificador("X" + personaIdentifica.getNumAcreditacion());
      // Obtener datos domicilio
      PersonaDomicilioEntity personaDomicilioEntity = interesadoEntity.getPersonasDomiciliosEntity().stream()
          .filter(p -> p.isFlgActivo()).toList().get(0).getPersonaDomicilioEntity();
      PaisesEntity paisesEntity = interesadoEntity.getPersonasDomiciliosEntity().stream().filter(p -> p.isFlgActivo())
          .toList().get(0).getPersonaDomicilioEntity().getPaises();
      LocalidadesEntity localidadesEntity = interesadoEntity.getPersonasDomiciliosEntity().stream()
          .filter(p -> p.isFlgActivo()).toList().get(0).getPersonaDomicilioEntity().getMunicipios();
      ProvinciasEntity provinciasEntity = interesadoEntity.getPersonasDomiciliosEntity().stream()
          .filter(p -> p.isFlgActivo()).toList().get(0).getPersonaDomicilioEntity().getProvincias();
      TiposViaEntity tipoViaEntity = interesadoEntity.getPersonasDomiciliosEntity().stream()
          .filter(p -> p.isFlgActivo()).toList().get(0).getPersonaDomicilioEntity().getTipoVia();
      setDomicilioTitular(titularDto, personaDomicilioEntity, paisesEntity, localidadesEntity, provinciasEntity,
          tipoViaEntity);
      setTipoPeticionTitular(titularDto, expedienteDto);
      String pattern = "yyyyMMdd";
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      titularDto.setFechaSalida(df.format(new Date()));
      PersonaFamDto conyugeFam = personaService.getConyugeByIdPersona(interesadoEntity.getIdPer());
      if (conyugeFam != null) {
        ConyugeDto conyugeDto = new ConyugeDto();
        conyugeDto.setNombreConyuge(checkSoloLetras(conyugeFam.getNombre()));
        conyugeDto.setApellido1Conyuge(checkSoloLetras(conyugeFam.getApellido1()));
        conyugeDto.setApellido2Conyuge(checkSoloLetras(conyugeFam.getApellido2()));
        if (conyugeFam.getPaisNacimiento() != null) {
          conyugeDto.setNacionalidadConyuge(conyugeFam.getNacionalidad().getCodDgp());
        }
      }
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_76).logMessageParams(idExpInf)
          .type(SinacExceptionType.BUSINESS);
    }
    LOG.debug("SolicitudesServiceImpl.getDatosSolicitudInformeDgp - End");

    return titularDto;
  }

  private void setInfoTitular(TitularDto titularDto, ExpedienteDto expediente, PersonaEntity interesadoEntity) {
    if (interesadoEntity.getApellido1() != null) {
      titularDto.setApellido1(checkSoloLetras(getStringCheckSize(interesadoEntity.getApellido1(), CUARENTA)));
    }
    if (interesadoEntity.getApellido2() != null) {
      titularDto.setApellido2(checkSoloLetras(getStringCheckSize(interesadoEntity.getApellido2(), CUARENTA)));
    }
    if (interesadoEntity.getPaisNacimiento() != null) {
      titularDto.setCodNacimiento(interesadoEntity.getPaisNacimiento().getCodDgp());
    }
    if (interesadoEntity.getNacionalidad() != null) {
      titularDto.setCodNacionalidad(interesadoEntity.getNacionalidad().getCodDgp());
    }
    if (interesadoEntity.getEstadoCivil() != null) {
      titularDto.setEstadoCivil(interesadoEntity.getEstadoCivil().getNomLdvMae());
    } else {
      titularDto.setEstadoCivil("I");
    }
    if (interesadoEntity.getFechaNacimiento() != null) {
      String pattern = "yyyyMMdd";
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      titularDto.setFechaNacimiento(df.format(interesadoEntity.getFechaNacimiento()));
    }
    titularDto.setLugarNacimiento(checkSoloLetras(getStringCheckSize(interesadoEntity.getLugarNacimiento(), SESENTA)));
    if (interesadoEntity.getNombre() != null) {
      titularDto.setNombre(checkSoloLetras(getStringCheckSize(interesadoEntity.getNombre(), CUARENTA)));
    }
    if (interesadoEntity.getProgenitor2() != null) {
      titularDto.setNombreMadre(checkSoloLetras(getStringCheckSize(interesadoEntity.getProgenitor2(), CUARENTA)));
    }
    if (interesadoEntity.getProgenitor1() != null) {
      titularDto.setNombrePadre(checkSoloLetras(getStringCheckSize(interesadoEntity.getProgenitor1(), CUARENTA)));
    }

    titularDto.setNumExpediente(formatCodExpSinacToDgp(expediente.getCodExp(), expediente.getProcedimientoDto()));
    if (interesadoEntity.getSexo() != null) {
      if (interesadoEntity.getSexo().getCodLdvMae().equals(SEX_HOM)) {
        titularDto.setSexo("M");
      } else {
        titularDto.setSexo("F");
      }
    } else {
      titularDto.setSexo("");
    }
  }

  private String checkSoloLetras(String valor) {
    if (valor == null) {
      return "";
    }
    valor = StringUtils.stripAccents(valor);
    StringBuilder resultado = new StringBuilder();
    for (Character caracter : valor.toCharArray()) {
      if (Character.isLetter(caracter) || caracter.equals(' ')) {
        resultado.append(caracter);
      }
    }
    return resultado.toString();
  }

  private String formatCodExpSinacToDgp(String codExpSinac, ProcedimientoDto procedimientoDto) {
    StringBuilder codExpDgp = new StringBuilder();
    String[] codExpedienteArray = codExpSinac.split("/");
    String anio = codExpedienteArray[1];
    codExpDgp.append(anio);
    String codigo = codExpedienteArray[0];
    for (int i = 0; i < codigo.length(); i++) {
      char caracter = codigo.charAt(i);
      if (Character.isDigit(caracter)) {
        codExpDgp.append(caracter);
      }
    }
    codExpDgp.append(procedimientoDto.getNumPro());
    return codExpDgp.toString();
  }

  private String getStringCheckSize(String valor, int size) {
    if (valor != null && !valor.isEmpty()) {
      return valor.length() > size ? valor.substring(0, size - 1) : valor;
    } else {
      return null;
    }
  }

  private void setTipoPeticionTitular(TitularDto titularDto, ExpedienteDto expediente) {
    String tipoPeticion = "";
    ProcedimientoDto pro = expediente.getProcedimientoDto();
    if (pro != null) {
      if ("RR".equals(pro.getCodCorto()) || "RA".equals(pro.getCodCorto())) {
        ExpedienteFormularioValEntity expedienteFormularioValEntity = expForValDao
            .getExpFormByIdExpCodCampo(expediente.getIdExp(), "PEORI");
        if (expedienteFormularioValEntity != null) {
          pro.setCodCorto(expedienteFormularioValEntity.getValor());
        }
      }
      switch (pro.getCodCorto()) {
      case "CN":
        tipoPeticion = "N";
        break;
      case "R":
        tipoPeticion = "R";
        break;
      case "DR":
        tipoPeticion = "D";
        break;
      default:
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_77)
            .logMessageParams(expediente.getCodExp());
      }
      titularDto.setTipoPeticion(tipoPeticion);
    }
  }

  private void setDomicilioTitular(TitularDto titularDto, PersonaDomicilioEntity personaDomicilioEntity,
      PaisesEntity paisesEntity, LocalidadesEntity localidadesEntity, ProvinciasEntity provinciasEntity,
      TiposViaEntity tiposViaEntity) {
    if (paisesEntity != null) {
      if (paisesEntity.getCodPais().equals("724")) {
        DomicilioDatosNacionalizacionDto domicilioDatosNacionalizacionDto = new DomicilioDatosNacionalizacionDto();
        domicilioDatosNacionalizacionDto.setBloque(getStringCheckSize(personaDomicilioEntity.getBloque(), DOS));
        domicilioDatosNacionalizacionDto
            .setCodPostal(getStringCheckSize(personaDomicilioEntity.getCodigoPostal(), CINCO));
        if (provinciasEntity != null) {
          domicilioDatosNacionalizacionDto.setCodProvReside(provinciasEntity.getCodProvincia());
        }
        domicilioDatosNacionalizacionDto.setEscalera(getStringCheckSize(personaDomicilioEntity.getEscalera(), DOS));
        domicilioDatosNacionalizacionDto.setKilometro(getStringCheckSize(personaDomicilioEntity.getKm(), TRES));
        if (localidadesEntity != null) {
          domicilioDatosNacionalizacionDto
              .setLocalidad(getStringCheckSize(localidadesEntity.getNomMunicipio(), CUARENTA));
        }
        domicilioDatosNacionalizacionDto.setNombreVia(getStringCheckSize(personaDomicilioEntity.getNomVia(), OCHENTA));
        domicilioDatosNacionalizacionDto.setNumero(getStringCheckSize(personaDomicilioEntity.getNumVia(), CINCO));
        domicilioDatosNacionalizacionDto.setPlanta(getStringCheckSize(personaDomicilioEntity.getPiso(), TRES));
        domicilioDatosNacionalizacionDto.setPortal(getStringCheckSize(personaDomicilioEntity.getPortal(), DOS));
        domicilioDatosNacionalizacionDto.setPuerta(getStringCheckSize(personaDomicilioEntity.getLetra(), CUATRO));
        if (tiposViaEntity != null) {
          domicilioDatosNacionalizacionDto.setTipoVia(getStringCheckSize(tiposViaEntity.getNumDgp(), TRES));
        }
        titularDto.setDomicilio(domicilioDatosNacionalizacionDto);
      } else {
        DomicilioNnormDto domicilioNnormDto = new DomicilioNnormDto();
        domicilioNnormDto.setCodigoNnorm(paisesEntity.getCodDgp());
        StringBuilder direccionNnorm = new StringBuilder();
        setDireccionNnorm(personaDomicilioEntity, direccionNnorm);
        domicilioNnormDto.setDireccionNnorm(getStringCheckSize(direccionNnorm.toString(), OCHENTA));
        domicilioNnormDto.setPoblacionNnorm(getStringCheckSize(personaDomicilioEntity.getPoblacion(), OCHENTA));
        titularDto.setDomicilioNnorm(domicilioNnormDto);
      }
    }
  }

  private void setDireccionNnorm(PersonaDomicilioEntity personaDomicilioEntity, StringBuilder direccionNnorm) {
    if (personaDomicilioEntity.getNomVia() != null && !personaDomicilioEntity.getNomVia().isEmpty()) {
      direccionNnorm.append(personaDomicilioEntity.getNomVia());
      addComa(direccionNnorm);
    }
    if (personaDomicilioEntity.getCodigoPostal() != null && !personaDomicilioEntity.getCodigoPostal().isEmpty()) {
      direccionNnorm.append(personaDomicilioEntity.getCodigoPostal());
      addComa(direccionNnorm);
    }
    if (personaDomicilioEntity.getLugarResidencia() != null && !personaDomicilioEntity.getLugarResidencia().isEmpty()) {
      direccionNnorm.append(personaDomicilioEntity.getLugarResidencia());
    }
  }

  private void addComa(StringBuilder direccionNnorm) {
    if (!direccionNnorm.toString().isEmpty()) {
      direccionNnorm.append(", ");
    }
  }

  @Override
  public void informeSolicitadoDgp(BigInteger idExp, String tipoInforme, Date fechaSolicitud, String idSolicitudInforme,
      BigInteger idExpInf) throws SinacException {
    ExpedienteEntity expedienteEntity = expedienteDao.findById(idExp)
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_125).logMessageParams(idExp));
    LdvMaestraEntity ldvMaestraEntityByIdInfLdv = ldvMaestraDao.findByCodigo(tipoInforme);
    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao
        .findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
    ExpedienteInformeEntity expedienteInformeEntity = new ExpedienteInformeEntity();
    if (idExpInf != null) {
      expedienteInformeEntity = expedienteInformeDao.getExpedienteInformeByIdExpedienteInforme(idExpInf);
    }
    expedienteInformeEntity.setExpedienteEntity(expedienteEntity);
    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(ldvMaestraEntityByIdInfLdv);
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);
    expedienteInformeEntity.setIdSolicitudInforme(idSolicitudInforme);
    expedienteInformeEntity.setFechaSolicitud(fechaSolicitud);
    expedienteInformeDao.save(expedienteInformeEntity);
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_78).type(SinacExceptionType.DATA);
    }

  }

  @Override
  public boolean checkUsarioAsignadoExpediente(BigInteger idExp, Integer idUsuario) {
    Integer usuario = expedienteDao.getUsuarioAsignadoExpediente(idExp);
    return usuario != null && usuario.equals(idUsuario);
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderCniByidExp(BigInteger idExp) {
    return procedimientosFasesTramitesOperacionesAccionesMapper
        .toDto(procedimientosFasesTramitesOperacionesAccionesDao.getPftoaResponderCniByidExp(idExp));
  }

  @Override
  public void saveExpedienteInforme(ExpedienteInformeDto expedienteInformeDto) throws SinacException {
    ExpedienteInformeEntity expedienteInformeEntity = expedienteInformeMapper.toEntity(expedienteInformeDto);
    expedienteInformeEntity.setExpedienteEntity(expedienteMapper.toEntity(expedienteInformeDto.getExpedienteDto()));
    expedienteInformeDao.save(expedienteInformeEntity);
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    BigInteger idExp = expedienteInformeEntity.getExpedienteEntity().getIdExp();
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_79).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesDto getPftobyCod(BigInteger idExpediente, String codFase,
      String codTramite, String codOpe) {

    return procedimientosFasesTramitesOperacionesMapper.toDto(procedimientosFasesTramitesOperacionesDao
        .getProcedimientosFasesTramitesOperacionesByCod(idExpediente, codFase, codTramite, codOpe));
  }

  @Override
  public TitulosDto consultarTitulosEducacion(ExpedienteDto expedienteDto) {
    Titular titular = new Titular();
    PersonaEntity personaEntity = personaDao.findByIdPersona(expedienteDto.getInteresado().getIdPer());
    PersonaIdentificaEntity personaIdentificaEntity = personaEntity.getPersonasIdentificaEntities().stream()
        .filter(perIden -> perIden.isFlgActivo() && perIden.isFlgPrincipal()).toList().get(0);
    String tipoDocumento = personaIdentificaEntity.getLdvMaestraEntity().getNomLdvMae();
    if (tipoDocumento.equals("NIE")) {
      titular.setTipoDocumentacion(tipoDocumento);
    } else {
      titular.setTipoDocumentacion("NIF");
    }
    titular.setDocumentacion(personaIdentificaEntity.getNumAcreditacion());
    titular.setNombre(personaEntity.getNombre());
    titular.setApellido1(personaEntity.getApellido1());
    titular.setApellido2(personaEntity.getApellido2());
    PeticionesPidEntity peticionesPidEntity = new PeticionesPidEntity();
    List<PersonaTitulacionesEntity> listadoTitulos = new ArrayList<>();
    List<PersonaTitulacionDto> listadoTitulosUni = new ArrayList<>();
    List<PersonaTitulacionDto> listadoTitulosNoUni = new ArrayList<>();
    peticionesPidEntity.setFechaPeticion(new Date());
    peticionesPidEntity.setCodExpediente(expedienteDto.getCodExp());
    peticionesPidEntity.setPersonaEntity(personaEntity);
    peticionesPidEntity.setFechaIniVig(new Date());
    peticionesPidEntity.setFlgActivo(true);
    obtenerRespuestaNoUni(titular, expedienteDto, peticionesPidEntity, listadoTitulos, listadoTitulosNoUni,
        titulacionesService, personaTitulacionesMapper);
    obtenerRespuestaUni(titular, expedienteDto, peticionesPidEntity, listadoTitulos, listadoTitulosUni,
        titulacionesService, personaTitulacionesMapper);
    for (PeticionesPidEntity peticion : personaEntity.getPeticionesPidEntities()) {
      if (peticion.isFlgActivo()) {
        peticion.setFlgActivo(false);
        peticion.setFechaFinVig(new Date());
        peticion = peticionesPidDao.save(peticion);
        for (PersonaTitulacionesEntity titulo : peticion.getPersonaTitulacionesEntities()) {
          titulo.setFlgActivo(false);
          titulo.setFechaFinVig(new Date());
          personaTitulacionesDao.save(titulo);
        }
      }
    }
    peticionesPidEntity = peticionesPidDao.save(peticionesPidEntity);
    for (PersonaTitulacionesEntity titulo : listadoTitulos) {
      titulo.setPeticionPidEntity(peticionesPidEntity);
      personaTitulacionesDao.save(titulo);
    }
    TitulosDto titulosDto = new TitulosDto();
    titulosDto.setFechaPeticion(new Date());
    titulosDto.setIdPetPid(expedienteDto.getInteresado().getIdPer());
    titulosDto.setFechaPeticion(peticionesPidEntity.getFechaPeticion());
    titulosDto.setCodEstRespNoUni(peticionesPidEntity.getCodEstRespNoUni());
    titulosDto.setCodEstRespUni(peticionesPidEntity.getCodEstRespUni());
    titulosDto.setCodEstSecRespNoUni(peticionesPidEntity.getCodEstSecRespNoUni());
    titulosDto.setCodEstSecRespUni(peticionesPidEntity.getCodEstSecRespUni());
    titulosDto.setLitErrorNoUni(peticionesPidEntity.getLitErrorNoUni());
    titulosDto.setLitErrorUni(peticionesPidEntity.getLitErrorUni());
    titulosDto.setListadoTitulosNOUniversitarios(listadoTitulosNoUni);
    titulosDto.setListadoTitulosUniversitarios(listadoTitulosUni);
    return titulosDto;
  }

  private static void obtenerRespuestaNoUni(Titular titular, ExpedienteDto expedienteDto,
      PeticionesPidEntity peticionesPidEntity, List<PersonaTitulacionesEntity> listadoTitulos,
      List<PersonaTitulacionDto> listadoTitulosNoUni, TitulacionesService titulacionesService,
      PersonaTitulacionesMapper personaTitulacionesMapper) {
    try {
      ProcedimientoDto procedimientoDto = expedienteDto.getProcedimientoDto();
      Respuesta respuestaNoUni = titulacionesService.consultaTitulosNOUniversitarios(titular, expedienteDto.getCodExp(),
          procedimientoDto.getCodSia(), procedimientoDto.getNomPro());
      peticionesPidEntity.setCodEstRespNoUni(respuestaNoUni.getAtributos().getEstado().getCodigoEstado());
      peticionesPidEntity.setCodEstSecRespNoUni(respuestaNoUni.getAtributos().getEstado().getCodigoEstadoSecundario());
      peticionesPidEntity.setLitErrorNoUni(respuestaNoUni.getAtributos().getEstado().getLiteralError());
      peticionesPidEntity.setIdPeticionNoUni(respuestaNoUni.getAtributos().getIdPeticion());
      if (respuestaNoUni.getTransmisiones() != null
          && respuestaNoUni.getTransmisiones().getTransmisionDatos() != null) {
        for (TransmisionDatos transmisionDatos : respuestaNoUni.getTransmisiones().getTransmisionDatos()) {
          if (transmisionDatos.getDatosEspecificos() != null
              && transmisionDatos.getDatosEspecificos().getRetorno() != null
              && transmisionDatos.getDatosEspecificos().getRetorno().getClass().getMethod("getListaTitulos") != null
              && transmisionDatos.getDatosEspecificos().getRetorno().getListaTitulos() != null) {
            for (DatosTitulacion datosTitulacion : transmisionDatos.getDatosEspecificos().getRetorno().getListaTitulos()
                .getDatosTitulacion()) {
              PersonaTitulacionesEntity personaTitulacionesEntity = datosTitulacionToPersonaTitulaciones(
                  datosTitulacion);
              listadoTitulos.add(personaTitulacionesEntity);
              listadoTitulosNoUni.add(personaTitulacionesMapper.toDto(personaTitulacionesEntity));
            }
          }
        }
      }
      LOG.info("respuestaNouni: {}", respuestaNoUni);
    } catch (Exception e) {
      if (e.getCause() != null) {
        LOG.error("Mensaje error respuestaNouni: {} ", e.getCause().toString());
      }
      LOG.error("Mensaje error respuestaNouni: {} ", e.getMessage());
      peticionesPidEntity.setCodEstRespNoUni("9999");
      peticionesPidEntity.setCodEstSecRespNoUni("9999");
      peticionesPidEntity.setLitErrorUni("Error inesperado: " + e.getMessage());
      peticionesPidEntity.setIdPeticionNoUni(TitulacionesServiceImpl.idPeticion("NO_UNI"));
    }
  }

  private static void obtenerRespuestaUni(Titular titular, ExpedienteDto expedienteDto,
      PeticionesPidEntity peticionesPidEntity, List<PersonaTitulacionesEntity> listadoTitulos,
      List<PersonaTitulacionDto> listadoTitulosUni, TitulacionesService titulacionesService,
      PersonaTitulacionesMapper personaTitulacionesMapper) {
    try {
      ProcedimientoDto procedimientoDto = expedienteDto.getProcedimientoDto();
      Respuesta respuestaUni = titulacionesService.consultaTitulosUniversitarios(titular, expedienteDto.getCodExp(),
          procedimientoDto.getCodSia(), procedimientoDto.getNomPro());
      peticionesPidEntity.setCodEstRespUni(respuestaUni.getAtributos().getEstado().getCodigoEstado());
      peticionesPidEntity.setCodEstSecRespUni(respuestaUni.getAtributos().getEstado().getCodigoEstadoSecundario());
      peticionesPidEntity.setLitErrorUni(respuestaUni.getAtributos().getEstado().getLiteralError());
      peticionesPidEntity.setIdPeticionUni(respuestaUni.getAtributos().getIdPeticion());
      if (respuestaUni.getTransmisiones() != null && respuestaUni.getTransmisiones().getTransmisionDatos() != null) {
        for (TransmisionDatos transmisionDatos : respuestaUni.getTransmisiones().getTransmisionDatos()) {
          if (transmisionDatos.getDatosEspecificos() != null
              && transmisionDatos.getDatosEspecificos().getRetorno() != null
              && transmisionDatos.getDatosEspecificos().getRetorno().getClass().getMethod("getListaTitulos") != null
              && transmisionDatos.getDatosEspecificos().getRetorno().getListaTitulos() != null) {
            for (DatosTitulacion datosTitulacion : transmisionDatos.getDatosEspecificos().getRetorno().getListaTitulos()
                .getDatosTitulacion()) {
              PersonaTitulacionesEntity personaTitulacionesEntity = datosTitulacionToPersonaTitulaciones(
                  datosTitulacion);
              listadoTitulos.add(personaTitulacionesEntity);
              listadoTitulosUni.add(personaTitulacionesMapper.toDto(personaTitulacionesEntity));
            }
          }
        }
      }
      LOG.info("respuestaUni: {}", respuestaUni);
    } catch (Exception e) {
      if (e.getCause() != null) {
        LOG.error("Mensaje error respuestaUni: {} ", e.getCause().toString());
      }
      LOG.error("Mensaje error respuestaUni: {} ", e.getMessage());
      peticionesPidEntity.setCodEstRespUni("9999");
      peticionesPidEntity.setCodEstSecRespUni("9999");
      peticionesPidEntity.setLitErrorUni("Error inesperado: " + e.getMessage());
      peticionesPidEntity.setIdPeticionUni(TitulacionesServiceImpl.idPeticion("UNI"));
    }
  }

  private static PersonaTitulacionesEntity datosTitulacionToPersonaTitulaciones(DatosTitulacion datosTitulacion) {
    DatosCentro datosCentro = datosTitulacion.getDatosCentro();
    DatosTitulo datosTitulo = datosTitulacion.getDatosTitulo();
    PersonaTitulacionesEntity personaTitulacionesEntity = new PersonaTitulacionesEntity();
    personaTitulacionesEntity.setCentro(datosCentro.getCentro());
    personaTitulacionesEntity.setProvincia(datosCentro.getProvincia());
    personaTitulacionesEntity.setUniversidad(datosCentro.getUniversidad());
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");// Vendrá en formato DD/MM/AAAA
    try {
      personaTitulacionesEntity.setFechaExpedicion(sdf.parse(datosTitulo.getFechaExpedicion()));
      personaTitulacionesEntity.setFechaFinalizacion(sdf.parse(datosTitulo.getFechaFinalizacion()));
    } catch (ParseException e) {
      LOG.error("Error al formatear fecha: {} ", e.getCause().toString());
    }
    personaTitulacionesEntity.setFlgActivo(true);
    personaTitulacionesEntity.setNivel(datosTitulo.getNivel());
    personaTitulacionesEntity.setNumeroTitulo(datosTitulo.getNumeroTitulo());
    if (datosTitulo.getRegistro() != null) {
      personaTitulacionesEntity.setNumFolio(datosTitulo.getRegistro().getNumeroFolio());
      personaTitulacionesEntity.setNumLibro(datosTitulo.getRegistro().getNumeroLibro());
      personaTitulacionesEntity.setNumOrdenLibro(datosTitulo.getRegistro().getNumeroOrdenLibro());
    }
    personaTitulacionesEntity.setRegistroUniversitario(datosTitulo.getRegistroUniversitario());
    personaTitulacionesEntity.setTipoEstudio(datosTitulo.getTipoEstudio());
    personaTitulacionesEntity.setTipoTitulo(datosTitulo.getTipoTitulo());
    personaTitulacionesEntity.setTitulacion(datosTitulo.getTitulacion());
    return personaTitulacionesEntity;
  }

  @Override
  public TitulosDto obtenerTitulosEducacion(BigInteger idPersona) throws SinacException {
    TitulosDto titulosDto = new TitulosDto();
    PeticionesPidEntity peticionesPidEntity = peticionesPidDao.findPeticionActivaByIdPersona(idPersona);
    if (peticionesPidEntity == null) {
      return null;
    }
    titulosDto.setFechaPeticion(peticionesPidEntity.getFechaPeticion());
    titulosDto.setCodEstRespNoUni(peticionesPidEntity.getCodEstRespNoUni());
    titulosDto.setCodEstRespUni(peticionesPidEntity.getCodEstRespUni());
    titulosDto.setCodEstSecRespNoUni(peticionesPidEntity.getCodEstSecRespNoUni());
    titulosDto.setCodEstSecRespUni(peticionesPidEntity.getCodEstSecRespUni());
    titulosDto.setLitErrorNoUni(peticionesPidEntity.getLitErrorNoUni());
    titulosDto.setLitErrorUni(peticionesPidEntity.getLitErrorUni());
    for (PersonaTitulacionesEntity titulo : peticionesPidEntity.getPersonaTitulacionesEntities()) {
      if (titulo.getUniversidad() != null || titulo.getNumeroTitulo() != null) {
        titulosDto.getListadoTitulosUniversitarios().add(personaTitulacionesMapper.toDto(titulo));
      } else {
        titulosDto.getListadoTitulosNOUniversitarios().add(personaTitulacionesMapper.toDto(titulo));
      }
    }
    return titulosDto;
  }

  public LdvMaestraDto getLdvById(Integer idLdvMae) throws SinacException {
    LOG.debug("ExpedienteServiceImpl.getLdvById - Init");
    try {
      LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findById(idLdvMae)
          .orElseThrow(() -> new EntityNotFoundException("Ldv maestra no encontrada"));
      LOG.debug("ExpedienteServiceImpl.getLdvById - End");
      return ldvMaestraMapper.toDto(ldvMaestraEntity);
    } catch (Exception ex) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_80)
          .logMessageParams(ERROR_RECUPERANDO_LDV, idLdvMae).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void desactivarRepresentante(BigInteger idExp, BigInteger idPersona) throws SinacException {
    ExpedientesPersonasEntity expedientesPersonasEntity = expedientesPersonasDao.recuperarExpedientesPersonasId(idExp,
        idPersona);
    expedientesPersonasEntity.setFlgActivo(false);
    expedientesPersonasEntity.setFechaFinVig(new Date());
    expedientesPersonasDao.save(expedientesPersonasEntity);
  }

  @Override
  public ExpedienteDto saveExpedientesCorDom(ExpedienteDto expediente) {
    if (expediente.getTipoPersonaNotificar() != null && !expediente.getTipoPersonaNotificar().isBlank()) {
      if (expediente.getTipoPersonaNotificar().equals("PER-INT")) {
        // contacto

        if (Utilidades.isObjetosIguales(expediente.getPersonaContactoElectronicoDtoNotificacion(), expediente
            .getInteresado().getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto())) {
          expediente.getPersonaContactoElectronicoDtoNotificacion().setIdPerConEle(expediente.getInteresado()
              .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getIdPerConEle());
        } else {
          expediente.getPersonaContactoElectronicoDtoNotificacion().setIdPerConEle(null);
          expediente.setPersonaContactoElectronicoDtoNotificacion(
              personaContactoElectronicoMapper.toDto(personaContactoElectronicoDao.save(personaContactoElectronicoMapper
                  .toEntity(expediente.getPersonaContactoElectronicoDtoNotificacion()))));
        }

        // domicilio

        if (Utilidades.isObjetosIguales(expediente.getPersonaDomicilioDtoNotificacion(),
            expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto())) {
          expediente.getPersonaDomicilioDtoNotificacion().setIdPerDom(
              expediente.getInteresado().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());
        } else {

          PersonaDomicilioEntity i = personaDomicilioMapper.toEntity(expediente.getPersonaDomicilioDtoNotificacion());
          PersonaDomicilioDto e = personaDomicilioMapper.toDto(personaDomicilioDao.save(i));
          expediente.setPersonaDomicilioDtoNotificacion(e);
        }

      } else if (expediente.getTipoPersonaNotificar().equals("PER-R1")) {
        // contacto

        if (Utilidades.isObjetosIguales(expediente.getPersonaContactoElectronicoDtoNotificacion(), expediente
            .getRepresentante1().getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto())) {
          expediente.getPersonaContactoElectronicoDtoNotificacion().setIdPerConEle(expediente.getInteresado()
              .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getIdPerConEle());
        } else {
          expediente.setPersonaContactoElectronicoDtoNotificacion(
              personaContactoElectronicoMapper.toDto(personaContactoElectronicoDao.save(personaContactoElectronicoMapper
                  .toEntity(expediente.getPersonaContactoElectronicoDtoNotificacion()))));
        }

        // domicilio

        if (Utilidades.isObjetosIguales(expediente.getPersonaDomicilioDtoNotificacion(),
            expediente.getRepresentante1().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto())) {
          expediente.getPersonaDomicilioDtoNotificacion().setIdPerDom(
              expediente.getRepresentante1().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());
        } else {
          expediente.setPersonaDomicilioDtoNotificacion(personaDomicilioMapper.toDto(personaDomicilioDao
              .save(personaDomicilioMapper.toEntity(expediente.getPersonaDomicilioDtoNotificacion()))));
        }

      } else if (expediente.getTipoPersonaNotificar().equals("PER-R2")) {
        // contacto

        if (Utilidades.isObjetosIguales(expediente.getPersonaContactoElectronicoDtoNotificacion(), expediente
            .getRepresentante2().getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto())) {
          expediente.getPersonaContactoElectronicoDtoNotificacion().setIdPerConEle(expediente.getRepresentante2()
              .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getIdPerConEle());
        } else {
          expediente.setPersonaContactoElectronicoDtoNotificacion(
              personaContactoElectronicoMapper.toDto(personaContactoElectronicoDao.save(personaContactoElectronicoMapper
                  .toEntity(expediente.getPersonaContactoElectronicoDtoNotificacion()))));
        }

        // domicilio

        if (Utilidades.isObjetosIguales(expediente.getPersonaDomicilioDtoNotificacion(),
            expediente.getRepresentante2().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto())) {
          expediente.getPersonaDomicilioDtoNotificacion().setIdPerDom(
              expediente.getRepresentante2().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());
        } else {
          expediente.setPersonaDomicilioDtoNotificacion(personaDomicilioMapper.toDto(personaDomicilioDao
              .save(personaDomicilioMapper.toEntity(expediente.getPersonaDomicilioDtoNotificacion()))));
        }

      } else if (expediente.getTipoPersonaNotificar().equals("PER-MAN")) {
        // contacto

        if (Utilidades.isObjetosIguales(expediente.getPersonaContactoElectronicoDtoNotificacion(),
            expediente.getRepresentanteMandato().getPersonasContactosElectronicosDtos().get(0)
                .getPersonaContactoElectronicoDto())) {
          expediente.getPersonaContactoElectronicoDtoNotificacion().setIdPerConEle(expediente.getRepresentanteMandato()
              .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getIdPerConEle());
        } else {
          expediente.setPersonaContactoElectronicoDtoNotificacion(
              personaContactoElectronicoMapper.toDto(personaContactoElectronicoDao.save(personaContactoElectronicoMapper
                  .toEntity(expediente.getPersonaContactoElectronicoDtoNotificacion()))));
        }

        // domicilio

        if (Utilidades.isObjetosIguales(expediente.getPersonaDomicilioDtoNotificacion(),
            expediente.getRepresentanteMandato().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto())) {
          expediente.getPersonaDomicilioDtoNotificacion().setIdPerDom(expediente.getRepresentanteMandato()
              .getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());
        } else {
          expediente.setPersonaDomicilioDtoNotificacion(personaDomicilioMapper.toDto(personaDomicilioDao
              .save(personaDomicilioMapper.toEntity(expediente.getPersonaDomicilioDtoNotificacion()))));
        }

      }
    } else {
      expediente.setPersonaContactoElectronicoDtoNotificacion(null);
      expediente.setPersonaDomicilioDtoNotificacion(null);
    }
    return expediente;
  }

  @Override
  public List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoPro(BigInteger idPer, String codCortoPro)
      throws SinacException {
    List<ExpedienteEntity> expedientesEntity = expedienteDao.getExpedientesByIdPerInteresadoCodCortoPro(idPer,
        codCortoPro);
    List<ExpedienteDto> expedientesDto = new ArrayList<>();
    for (ExpedienteEntity expedienteEntity : expedientesEntity) {
      expedientesDto.add(expedienteMapper.toDto(expedienteEntity));
    }
    return expedientesDto;
  }

  @Override
  public List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoProDistinto(BigInteger idPer, String codCortoPro)
      throws SinacException {
    List<ExpedienteEntity> expedientesEntity = expedienteDao.getExpedientesByIdPerInteresadoCodCortoProDistinto(idPer,
        codCortoPro);
    List<ExpedienteDto> expedientesDto = new ArrayList<>();
    for (ExpedienteEntity expedienteEntity : expedientesEntity) {
      expedientesDto.add(expedienteMapper.toDto(expedienteEntity));
    }
    return expedientesDto;
  }

  @Override
  public LinkedList<DocumentoToSaveDto> acumularExpediente(ExpedienteDto expediente) throws SinacException {
    LinkedList<DocumentoToSaveDto> documentosAcumulados = new LinkedList<>();
    if (!expediente.getOpcionAcumular().equals("2")) {
      // Llamar a acumular personas
      acumularPersonas(expediente);
    } else if (!expediente.getOpcionAcumular().equals("1")) {
      // Llamar a acumular documentos
      Set<ExpedienteDocumentoEntity> expedienteDocumentoEntities = expedienteDocumentoDao
          .getExpedienteDocumentosByCodExp(expediente.getCodExpOrigenAcumular());
      cargarExpedienteDocumentosAcumulados(documentosAcumulados, expedienteDocumentoEntities);
    }
    return documentosAcumulados;
  }

  private LinkedList<DocumentoToSaveDto> cargarExpedienteDocumentosAcumulados(
      LinkedList<DocumentoToSaveDto> listaDocumentoToSaveDto, Set<ExpedienteDocumentoEntity> expedienteDocumentoDto)
      throws SinacException {
    for (ExpedienteDocumentoEntity expDocEntity : expedienteDocumentoDto) {
      DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
      // Se indica la ruta en la que estaba el documento previamente guardado para
      // recuperar su contenido, en copyDocumentoNFS se setea la ruta real final
      documentoToSaveDto.setRutaNFS(expDocEntity.getNfsRuta());
      documentoToSaveDto.setNombre(expDocEntity.getNomDoc());
      documentoToSaveDto.setTipoDocumento(expDocEntity.getDocumentoTipoEntity().getIdDocTipo());
      documentoToSaveDto.setOrigen(expDocEntity.getLdvMaestraEntityByIdOriDocLdv().getIdLdvMae());
      documentoToSaveDto.setOrgano(expDocEntity.getLdvMaestraEntityByIdOrgLdv().getIdLdvMae());
      documentoToSaveDto.setGenerarRegistro(true);
      documentoToSaveDto.setRegistroEntradaSalida(TipoRegistroRegageEnum.SALIDA.getValor());
      documentoToSaveDto.setEstadoElaboracion(ldvMaestraService.getCatalogoByCod("EE01").getIdLdvMae());
      documentoToSaveDto
          .setContenido(nfsManager.getDocumentContent(documentoToSaveDto.getNombre(), documentoToSaveDto.getRutaNFS()));

      listaDocumentoToSaveDto.add(documentoToSaveDto);
    }
    return listaDocumentoToSaveDto;
  }

  private void acumularPersonas(ExpedienteDto expediente) {
    // Primero se buscan las personas anteriores y se desactivan
    Set<ExpedientesPersonasEntity> expedientesPersonasEntities = expedientesPersonasDao
        .getExpPersonasPorCodExp(expediente.getCodExpDestinoAcumular());
    ExpedienteEntity expedienteEntityDestino = null;
    for (ExpedientesPersonasEntity expedientesPersonasEntity : expedientesPersonasEntities) {
      expedientesPersonasEntity.setFlgActivo(false);
      expedienteEntityDestino = expedientesPersonasEntity.getExpedienteEntity();
      expedientesPersonasDao.save(expedientesPersonasEntity);
    }
    // Ahora se buscan las personas que se quieren acumular y se insertan en el
    // expediente destino
    Set<ExpedientesPersonasEntity> expedientesPersonasEntitiesFinal = expedientesPersonasDao
        .getExpPersonasPorCodExp(expediente.getCodExpOrigenAcumular());
    for (ExpedientesPersonasEntity expedientesPersonasEntity : expedientesPersonasEntitiesFinal) {
      if (expedienteEntityDestino != null) {
        ExpedientesPersonasEntity expedientesPersonasEntityDestino = new ExpedientesPersonasEntity();
        expedientesPersonasEntityDestino.setExpedienteEntity(expedienteEntityDestino);
        expedientesPersonasEntityDestino.setPersonaEntity(expedientesPersonasEntity.getPersonaEntity());
        expedientesPersonasEntityDestino.setLdvMaestraEntity(expedientesPersonasEntity.getLdvMaestraEntity());
        expedientesPersonasEntityDestino.setFlgConsiente(expedientesPersonasEntity.isFlgConsiente());
        expedientesPersonasEntityDestino.setFlgNotificar(expedientesPersonasEntity.isFlgNotificar());
        expedientesPersonasDao.save(expedientesPersonasEntityDestino);
      }
    }
  }

  public void acumularPersonasFromSolicitudToExpediente(ExpedienteDto expediente, SolicitudDto solicitud) {
    // Primero se buscan las personas anteriores y se desactivan
    Set<ExpedientesPersonasEntity> expedientesPersonasEntities = expedientesPersonasDao
        .getExpPersonasPorCodExp(expediente.getCodExp());
    ExpedienteEntity expedienteEntityDestino = null;
    for (ExpedientesPersonasEntity expedientesPersonasEntity : expedientesPersonasEntities) {
      if (!expedientesPersonasEntity.getLdvMaestraEntity().getCodLdvMae().equals("PER-INT")) {
        expedientesPersonasEntity.setFlgActivo(false);
        expedienteEntityDestino = expedientesPersonasEntity.getExpedienteEntity();
        expedientesPersonasDao.save(expedientesPersonasEntity);
      }
    }
    // Ahora se buscan las personas que se quieren acumular y se insertan en el
    // expediente destino
    Set<SolicitudesPersonasEntity> solicitudesPersonaEntity = solicitudesPersonasDao
        .getSolPersonasPorIdSol(solicitud.getIdSol());
    for (SolicitudesPersonasEntity solicitudPersona : solicitudesPersonaEntity) {
      if (expedienteEntityDestino != null) {
        if (!solicitudPersona.getLdvMaestraEntity().getCodLdvMae().equals("PER-INT")) {
          ExpedientesPersonasEntity expedientesPersonasEntityDestino = new ExpedientesPersonasEntity();
          expedientesPersonasEntityDestino.setExpedienteEntity(expedienteEntityDestino);
          expedientesPersonasEntityDestino.setPersonaEntity(solicitudPersona.getPersonaEntity());
          expedientesPersonasEntityDestino.setLdvMaestraEntity(solicitudPersona.getLdvMaestraEntity());
          expedientesPersonasEntityDestino.setFlgConsiente(solicitudPersona.isFlgConsiente());
          expedientesPersonasEntityDestino.setFlgNotificar(solicitudPersona.isFlgNotificar());
          expedientesPersonasDao.save(expedientesPersonasEntityDestino);
        }
      }
    }
  }

  @Override
  public void saveExpedientesViculados(ExpedientesVinculadosDto expedientesVinculadosDto) {
    try {
      ExpedientesVinculadosEntity expedientesVinculadosEntity = expedientesVinculadosWithExpedientesMapper
          .toEntity(expedientesVinculadosDto);
      expedientesVinDao.save(expedientesVinculadosEntity);
    } catch (Exception e) {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_81);
    }
  }

  public void crearInformePorTipo(Map<String, Object> valores, BigInteger idExp, Date fechaCom) {
    try {
      String tipoInforme = valores.get("operacion").toString();
      ExpedienteInformeDto expedienteInformeDto = new ExpedienteInformeDto();

      if (tipoInforme.equals("IDGP")) {// DGP
        expedienteInformeDto.setLdvMaestraDtoByIdInfLdv(getLdvByCod(Constantes.TiposInforme.TIPO_INFORME_DGP));
      } else if (tipoInforme.equals("IMJU")) {// MJU
        expedienteInformeDto.setLdvMaestraDtoByIdInfLdv(getLdvByCod(Constantes.TiposInforme.TIPO_INFORME_MJU));
      } else if (tipoInforme.equals("ICNI")) {// CNI
        expedienteInformeDto.setLdvMaestraDtoByIdInfLdv(getLdvByCod(Constantes.TiposInforme.TIPO_INFORME_CNI));
      }
      expedienteInformeDto
          .setLdvMaestraDtoByIdEstInfLdv(getLdvByCod(Constantes.EstadosInforme.ESTADO_INFORME_RECIBIDO));
      expedienteInformeDto.setFechaRecepcion(new Date());
      expedienteInformeDto.setExpedienteDto(getExpedientebyId(idExp));
      // Plazos.
      String codPlazoCaducidadInforme = plazosService.getCodPlazoCaducidadInformeByCodTipoInforme(
          expedienteInformeDto.getLdvMaestraDtoByIdInfLdv().getCodLdvMae());
      PlazoDto plazoDto = plazosService.getPlazoByIdProcedimientoAndCodTipoPlazo(
          expedienteInformeDto.getExpedienteDto().getProcedimientoDto().getIdPro(), codPlazoCaducidadInforme);
      expedienteInformeDto.setFechaEmisionInf(fechaCom);
      if (plazoDto != null) {
        expedienteInformeDto.setFechaCaducidad(
            Utilidades.sumarUnidadTiempoAFecha(plazoDto.getLdvMaestraDtoByIdPlazoTieLdv().getNomLdvMae(),
                expedienteInformeDto.getFechaRecepcion(), plazoDto.getNumPlazo()));
      }
      saveExpedienteInforme(expedienteInformeDto);
    } catch (SinacException | ParseException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_82).logMessageParams(e.getMessage());
    }

  }

  @Override
  public void saveExpedienteSecuencias(ExpedienteSecuenciasDto expSecDto) {
    LOG.debug("Save Expediente Secuencias - Init");
    try {
      expSecDao.save(expSecMapper.toEntity(expSecDto));
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_83).type(SinacExceptionType.DATA);
    }
    LOG.debug("Save Expediente Secuencias - End");

  }

  private void updateValores(Object objetoPrevio, Object objetoUpdate) {
    objetoPrevio = obtenerValorReal(objetoPrevio);
    if (!objetoPrevio.getClass().equals(objetoUpdate.getClass())) {
      throw new IllegalArgumentException("Las entidades deben ser del mismo tipo.");
    }
    Field[] arrayFields = objetoPrevio.getClass().getDeclaredFields();
    for (Field field : arrayFields) {
      field.setAccessible(true); // Para acceder a los campos privados

      Object valorOriginal;
      Object valorNuevo;
      try {
        valorOriginal = field.get(objetoPrevio);
        valorNuevo = field.get(objetoUpdate);
        if ((valorNuevo == null && valorOriginal != null) || ("flgActivo".equals(field.getName()))) {
          // Si los valores son diferentes, actualizar el valor del campo original
          field.set(objetoUpdate, valorOriginal);
        }
      } catch (IllegalArgumentException | IllegalAccessException exception) {
        LOG.error(String.format("ExpedienteServiceImpl.updateValores - Error: %s", exception.getMessage()), exception);

      }

    }
  }

  private Object obtenerValorReal(Object valor) {
    if (valor instanceof HibernateProxy) {
      return ((HibernateProxy) valor).getHibernateLazyInitializer().getImplementation();
    }
    return valor;
  }

  @Override
  public List<ExpedienteAvisoDto> getUltimosAvisosAsignados(Integer idUsuario, Short idPro, Boolean isAdmin)
      throws SinacException {
    try {
      LOG.info(
          "Iniciando la obtención de los ultimos avisos para mostrar al usuario con ID: {}, procedimiento ID: {}, isAdmin: {}",
          idUsuario, idPro, isAdmin);
      // Obtiene los expedientes avisos ya filtrados por usuario asignado y
      // procedimiento actual
      List<ExpedienteAvisoEntity> expedientesAvisos = expedienteAvisoDao
          .findHabilitadosByUsuarioAndProcedimiento(idUsuario, idPro);
      LOG.info("Se han recuperado {} expedientes avisos del DAO para el usuario con ID: {}", expedientesAvisos.size(),
          idUsuario);
      List<ExpedienteAvisoDto> expedienteAvisoDtos;
      if (Boolean.TRUE.equals(isAdmin)) {
        // Si eres administrador se filtrará por sus ajustes (procedimientosAvisos)
        List<ProcedimientosAvisosEntity> procedimientosAvisos = procedimientosAvisosDao.findAllHabilitadosPA();
        LOG.info("Se han recuperado {} procedimientosAvisos habilitados del DAO procedimientosAvisos.",
            procedimientosAvisos.size());
        Set<Pair<Short, Short>> allowedProcedimientoAvisoPairs = procedimientosAvisos.stream()
            .map(pa -> Pair.of(pa.getProcedimiento().getIdPro(), pa.getAviso().getIdAviso()))
            .collect(Collectors.toSet());
        List<ExpedienteAvisoEntity> filteredExpedientesAvisos = expedientesAvisos.stream()
            .filter(expAvi -> allowedProcedimientoAvisoPairs.contains(
                Pair.of(expAvi.getExpediente().getProcedimientoEntity().getIdPro(), expAvi.getAviso().getIdAviso())))
            .toList();
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(filteredExpedientesAvisos);
        LOG.info("Mapeo a DTO realizado con éxito para los expedientes avisos filtrados.");
      } else {
        LOG.info("El usuario no es administrador. Devolviendo la lista completa de expedientesAvisos.");
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(expedientesAvisos);
        LOG.info("Mapeo a DTO realizado con éxito para los expedientesAvisos.");
      }
      return expedienteAvisoDtos;
    } catch (NullPointerException e) {
      LOG.error(
          "Error de NullPointerException al recuperar los expedientesAvisos para el usuario con ID: {}, procedimiento ID: {}, isAdmin: {}",
          idUsuario, idPro, isAdmin, e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_84).type(SinacExceptionType.DATA);
    } catch (IllegalArgumentException e) {
      LOG.error(
          "Error de IllegalArgumentException al procesar los expedientesAvisos: usuario ID: {}, procedimiento ID: {}, isAdmin: {}",
          idUsuario, idPro, isAdmin, e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_85).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      LOG.error(
          "Error inesperado al recuperar los expedientesAvisos para el usuario con ID: {}, procedimiento ID: {}, isAdmin: {}",
          idUsuario, idPro, isAdmin, exception);
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_86).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void consultarCertificaciones(ExpedienteDto expedienteDto) {
    PersonaDto interesado = expedienteDto.getInteresado();
    interesado = personaService.getPersonaByIdPer(interesado.getIdPer());
    es.redsara.intermediacion.scsp.esquemas.peticion.ccsedele.Titular titular = new es.redsara.intermediacion.scsp.esquemas.peticion.ccsedele.Titular();
    titular.setNombre(interesado.getNombre());
    titular.setApellido1(interesado.getApellido1());
    titular.setApellido2(interesado.getApellido2());
    String nombreCompleto = interesado.getNombre() + " " + interesado.getApellido1();
    if (!StringUtils.isEmpty(interesado.getApellido2())) {
      nombreCompleto += " " + interesado.getApellido2();
    }
    titular.setNombreCompleto(nombreCompleto);
    for (PersonaIdentificaDto perIden : interesado.getPersonasIdentificaDtos()) {
      if (perIden.getFlgPrincipal()) {
        titular.setDocumentacion(perIden.getNumAcreditacion());
        if (perIden.getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")) {
          titular.setTipoDocumentacion("NIE");
        } else if (perIden.getLdvMaestraDto().getCodLdvMae().equals("DID-NIF")) {
          titular.setTipoDocumentacion("NumeroIdentificacion");
        } else if (perIden.getLdvMaestraDto().getCodLdvMae().equals("DID-PAS")) {
          titular.setTipoDocumentacion("Pasaporte");
        }
      }
    }
    try {
      ProcedimientoDto procedimentoDto = expedienteDto.getProcedimientoDto();
      RespuestaCalificacionesDto respuestaCalificaciones = calificacionesService.consultaCalificaciones(titular,
          expedienteDto.getCodExp(), procedimentoDto.getCodSia(), procedimentoDto.getNomPro());
      saveRespuestaCertificaciones(interesado, respuestaCalificaciones);
    } catch (SinacCalificacionesException | IOException e) {
      LOG.info("Se ha producido un error ", e);
    }
  }

  private void saveRespuestaCertificaciones(PersonaDto interesado, RespuestaCalificacionesDto respuestaCalificaciones) {
    List<PerCertificacionesEntity> certificacionesAnteriores = perCertificacionesDao
        .getPerCertificacionesByIdPer(interesado.getIdPer());
    if (respuestaCalificaciones.getTransmisiones() == null
        || CollectionUtils.isEmpty(respuestaCalificaciones.getTransmisiones().getTransmisionDatos())) {
      saveErrorSCPCertificaciones(interesado, respuestaCalificaciones, certificacionesAnteriores);
    } else {
      RetornoCalificacionesDto retornoCalificacionesDto = respuestaCalificaciones.getTransmisiones()
          .getTransmisionDatos().get(0).getDatosEspecificos().getRetorno();
      if (retornoCalificacionesDto.getEstado() != null
          && !retornoCalificacionesDto.getEstado().getCodigoEstado().equals("0")) {
        for (PerCertificacionesEntity perCertificacionesEntityAnterior : certificacionesAnteriores) {
          // Si la petición devuelve error distinto de 1, los certificados anteriores no
          // se desactivan, solo se desactivan errores anteriores
          // Si la petición devuelve error igual a 1, se desactivan todos los anteriores
          if (retornoCalificacionesDto.getEstado().getCodigoEstado().equals("1")
              || !perCertificacionesEntityAnterior.getCodEst().equals("0")) {
            perCertificacionesEntityAnterior.setFlgActivo(false);
            perCertificacionesDao.save(perCertificacionesEntityAnterior);
          }
        }

        PerCertificacionesEntity perCertificacionesEntity = new PerCertificacionesEntity();
        perCertificacionesEntity.setPersonaEntity(personaMapper.toEntity(interesado));
        perCertificacionesEntity.setLitError(retornoCalificacionesDto.getEstado().getLiteralError());
        perCertificacionesEntity.setCodEst(retornoCalificacionesDto.getEstado().getCodigoEstado());
        perCertificacionesEntity.setIdPeticion(respuestaCalificaciones.getAtributos().getIdPeticion());
        perCertificacionesDao.save(perCertificacionesEntity);
      } else {
        saveCertificacionesCorrectas(interesado, respuestaCalificaciones, certificacionesAnteriores);
      }
    }
  }

  private void saveErrorSCPCertificaciones(PersonaDto interesado, RespuestaCalificacionesDto respuestaCalificaciones,
      List<PerCertificacionesEntity> certificacionesAnteriores) {
    if (respuestaCalificaciones.getFaultSCSP() != null) {
      for (PerCertificacionesEntity perCertificacionesEntityAnterior : certificacionesAnteriores) {
        // Si la petición devuelve error scp, los certificados anteriores no
        // se desactivan, solo se desactivan errores anteriores
        if (!perCertificacionesEntityAnterior.getCodEst().equals("0")) {
          perCertificacionesEntityAnterior.setFlgActivo(false);
          perCertificacionesDao.save(perCertificacionesEntityAnterior);
        }
      }
      PerCertificacionesEntity perCertificacionesEntity = new PerCertificacionesEntity();
      perCertificacionesEntity.setPersonaEntity(personaMapper.toEntity(interesado));
      perCertificacionesEntity
          .setLitError(respuestaCalificaciones.getFaultSCSP().getFaultInfo().getEstado().getLiteralError());
      perCertificacionesEntity
          .setCodEst(respuestaCalificaciones.getFaultSCSP().getFaultInfo().getEstado().getCodigoEstado());
      perCertificacionesEntity.setIdPeticion(respuestaCalificaciones.getFaultSCSP().getFaultInfo().getIdPeticion());
      perCertificacionesDao.save(perCertificacionesEntity);
    }
  }

  private void saveCertificacionesCorrectas(PersonaDto interesado, RespuestaCalificacionesDto respuestaCalificaciones,
      List<PerCertificacionesEntity> certificacionesAnteriores) {
    RetornoCalificacionesDto retornoCalificacionesDto = respuestaCalificaciones.getTransmisiones().getTransmisionDatos()
        .get(0).getDatosEspecificos().getRetorno();
    // Si obtenemos certificados, desactivamos los anteriores antes de guardar los
    // nuevos
    for (PerCertificacionesEntity perCertificacionesEntityAnterior : certificacionesAnteriores) {
      perCertificacionesEntityAnterior.setFlgActivo(false);
      perCertificacionesDao.save(perCertificacionesEntityAnterior);
    }
    for (ResultadoCalificaciones resultadoCalificaciones : retornoCalificacionesDto.getResultadosCalificaciones()
        .getResultadoCalificaciones()) {
      PerCertificacionesEntity perCertificacionesEntity = new PerCertificacionesEntity();
      perCertificacionesEntity.setCalificacion(resultadoCalificaciones.getCalificacionObtenida());
      perCertificacionesEntity.setCodigoInscripcion(resultadoCalificaciones.getCodigoInscripcion());
      if (resultadoCalificaciones.getFechaActaCalificacion() != null) {
        perCertificacionesEntity.setFechaActaCalificacion(
            resultadoCalificaciones.getFechaActaCalificacion().toGregorianCalendar().getTime());
      }
      if (resultadoCalificaciones.getFechaExamen() != null) {
        perCertificacionesEntity
            .setFechaExamen(resultadoCalificaciones.getFechaExamen().toGregorianCalendar().getTime());
      }
      perCertificacionesEntity.setNivelObtenido(resultadoCalificaciones.getNivelObtenido());
      perCertificacionesEntity.setTipoCertificacion(resultadoCalificaciones.getTipoCertificacion());
      if (resultadoCalificaciones.getFechaValidez() != null) {
        perCertificacionesEntity
            .setFechaValidez(resultadoCalificaciones.getFechaValidez().toGregorianCalendar().getTime());
      }
      perCertificacionesEntity.setPersonaEntity(personaMapper.toEntity(interesado));
      perCertificacionesEntity.setLitError(retornoCalificacionesDto.getEstado().getLiteralError());
      perCertificacionesEntity.setCodEst(retornoCalificacionesDto.getEstado().getCodigoEstado());
      perCertificacionesEntity.setIdPeticion(respuestaCalificaciones.getAtributos().getIdPeticion());
      perCertificacionesDao.save(perCertificacionesEntity);
    }
  }

  @Override
  public List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExpediente) throws SinacException {
    LOG.debug("Init - expedientesServiceImpl.getAvisosExpedienteByIdExp del expediente {}", idExpediente);
    try {
      Boolean isAdmin = isAdminRol(sinacSession.getRolUsuarioSeleccionado());
      List<ExpedienteAvisoEntity> expedienteAvisos = expedienteAvisoDao.findAllByIdExp(idExpediente);
      List<ExpedienteAvisoDto> expedienteAvisoDtos;

      if (isAdmin) {
        List<ProcedimientosAvisosEntity> procedimientosAvisos = procedimientosAvisosDao.findAllHabilitadosPA();
        // Crear un conjunto de combinaciones válidas de idPro e idAvi (ambos de tipo
        // Short)
        Set<Pair<Short, Short>> allowedProcedimientoAvisoPairs = procedimientosAvisos.stream()
            .map(pa -> Pair.of(pa.getProcedimiento().getIdPro(), pa.getAviso().getIdAviso()))
            .collect(Collectors.toSet());
        // Filtrar los expedientesAvisos basándose en las combinaciones permitidas de
        // idPro e idAvi
        List<ExpedienteAvisoEntity> filteredExpedientesAvisos = expedienteAvisos.stream()
            .filter(expAvi -> allowedProcedimientoAvisoPairs.contains(
                Pair.of(expAvi.getExpediente().getProcedimientoEntity().getIdPro(), expAvi.getAviso().getIdAviso())))
            .collect(Collectors.toList());
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(filteredExpedientesAvisos);
        LOG.info("Se han recuperado correctamente los avisos del expediente {}.", idExpediente);
      } else {
        expedienteAvisoDtos = expedienteAvisoMapper.toDto(expedienteAvisos);
        LOG.info("No se han recuperado los avisos del expediente {} porque no tiene acceso como {}.", idExpediente,
            sinacSession.getRolUsuarioSeleccionado().getRol().getNomRol());
      }
      return expedienteAvisoDtos;
    } catch (NullPointerException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_87).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_88).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPerTipoCertificacion(BigInteger idPersona,
      String tipoCertificacion) throws SinacException {
    List<PerCertificacionesDto> perCertificacionesDtos = new ArrayList<>();
    List<PerCertificacionesEntity> perCertificacionesEntities = perCertificacionesDao
        .getPerCertificacionesByIdPerTipoCertificacion(idPersona, tipoCertificacion);
    if (perCertificacionesEntities != null) {
      for (PerCertificacionesEntity perCertificacionesEntity : perCertificacionesEntities) {
        perCertificacionesDtos.add(perCertificacionesMapper.toDto(perCertificacionesEntity));
      }
    }

    return perCertificacionesDtos;
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPer(BigInteger idPersona) throws SinacException {
    List<PerCertificacionesDto> perCertificacionesDtos = new ArrayList<>();
    List<PerCertificacionesEntity> perCertificacionesEntities = perCertificacionesDao
        .getPerCertificacionesByIdPer(idPersona);
    if (perCertificacionesEntities != null) {
      for (PerCertificacionesEntity perCertificacionesEntity : perCertificacionesEntities) {
        perCertificacionesDtos.add(perCertificacionesMapper.toDto(perCertificacionesEntity));
      }
    }

    return perCertificacionesDtos;
  }

  @Override
  public void consultarPadron(ExpedienteDto expedienteDto) {
    PersonaDto interesado = expedienteDto.getInteresado();
    PerPadronEntity padronAnterior = perPadronDao.getPerPadronByIdPer(interesado.getIdPer());
    if (padronAnterior != null) {
      padronAnterior.setFlgActivo(false);
      perPadronDao.save(padronAnterior);
    }
    interesado = personaService.getPersonaByIdPer(interesado.getIdPer());
    es.redsara.intermediacion.scsp.esquemas.peticion.padron.Titular titular = new es.redsara.intermediacion.scsp.esquemas.peticion.padron.Titular();
    titular.setNombre(interesado.getNombre());
    titular.setApellido1(interesado.getApellido1());
    titular.setApellido2(interesado.getApellido2());
    String nombreCompleto = interesado.getNombre() + " " + interesado.getApellido1();
    if (!StringUtils.isEmpty(interesado.getApellido2())) {
      nombreCompleto += " " + interesado.getApellido2();
    }
    titular.setNombreCompleto(nombreCompleto);
    titular.setDocumentacion(interesado.getPersonasIdentificaDtos().get(0).getNumAcreditacion());
    if (interesado.getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")) {
      titular.setTipoDocumentacion("NIE");
    } else if (interesado.getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getCodLdvMae().equals("DID-NIF")) {
      titular.setTipoDocumentacion("NIF");
    } else if (interesado.getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getCodLdvMae().equals("DID-PAS")) {
      titular.setTipoDocumentacion("Pasaporte");
    }
    try {
      ProcedimientoDto procedimiento = expedienteDto.getProcedimientoDto();
      RespuestaPadronDto respuestaPadron = padronService.consultaPadron(titular, expedienteDto.getCodExp(),
          procedimiento.getCodSia(), procedimiento.getNomPro());
      TransmisionesPadronDto transmisionesPadronDto = respuestaPadron.getTransmisiones();
      saveRespuestaPadron(interesado, respuestaPadron, transmisionesPadronDto);
    } catch (SinacPadronException e) {
      LOG.info("Se ha producido un error ", e);
    }
  }

  private void saveRespuestaPadron(PersonaDto interesado, RespuestaPadronDto respuestaPadron,
      TransmisionesPadronDto transmisionesPadronDto) {
    PerPadronEntity perPadronEntity = new PerPadronEntity();
    if (transmisionesPadronDto != null && !CollectionUtils.isEmpty(transmisionesPadronDto.getTransmisionDatos())) {
      TransmisionDatosPadronDto transmisionDatos = transmisionesPadronDto.getTransmisionDatos().get(0);
      if (transmisionDatos != null) {
        if (transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTitular() != null) {
          perPadronEntity.setNombre(transmisionDatos.getDatosGenericos().getTitular().getNombre());
          perPadronEntity.setApellido1(transmisionDatos.getDatosGenericos().getTitular().getApellido1());
          perPadronEntity.setApellido2(transmisionDatos.getDatosGenericos().getTitular().getApellido2());
          perPadronEntity.setDocumentacion(transmisionDatos.getDatosGenericos().getTitular().getDocumentacion());
          perPadronEntity
              .setTipoDocumentacion(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion());
        }
        if (transmisionDatos.getDatosEspecificos() != null) {
          setDatosEspecificos(transmisionDatos, perPadronEntity);
        }
      }
    }
    setEstadoPadron(respuestaPadron, perPadronEntity);
    perPadronEntity.setPersonaEntity(personaMapper.toEntity(interesado));
    perPadronEntity.setIdPeticion(respuestaPadron.getAtributos().getIdPeticion());
    perPadronDao.save(perPadronEntity);
  }

  private void setEstadoPadron(RespuestaPadronDto respuestaPadron, PerPadronEntity perPadronEntity) {
    if (respuestaPadron.getFaultSCSPMessage() == null) {
      if (respuestaPadron.getTransmisiones() == null) {
        es.redsara.intermediacion.scsp.esquemas.respuesta.padron.Atributos atributos = respuestaPadron.getAtributos();
        perPadronEntity.setCodEstado(atributos.getEstado().getCodigoEstado());
        perPadronEntity.setCodEstadoSec(atributos.getEstado().getCodigoEstadoSecundario());
        perPadronEntity.setLitError(atributos.getEstado().getLiteralError());
        perPadronEntity.setIdPeticion(atributos.getIdPeticion());
      } else {
        es.redsara.intermediacion.scsp.esquemas.datosespecificos.padron.Estado estadoRespuesta = respuestaPadron
            .getTransmisiones().getTransmisionDatos().get(0).getDatosEspecificos().getRetorno().getEstado();
        perPadronEntity.setCodEstado(estadoRespuesta.getCodigoEstado());
        perPadronEntity.setCodEstadoSec(estadoRespuesta.getCodigoEstadoSecundario());
        perPadronEntity.setLitError(estadoRespuesta.getLiteralError());
        perPadronEntity.setIdPeticion(
            respuestaPadron.getTransmisiones().getTransmisionDatos().get(0).getDatosEspecificos().getId());
      }
    } else {
      perPadronEntity.setCodEstado(respuestaPadron.getFaultSCSPMessage().getFaultInfo().getEstado().getCodigoEstado());
      perPadronEntity.setCodEstadoSec(
          respuestaPadron.getFaultSCSPMessage().getFaultInfo().getEstado().getCodigoEstadoSecundario());
      perPadronEntity.setLitError(respuestaPadron.getFaultSCSPMessage().getFaultInfo().getEstado().getLiteralError());
      perPadronEntity.setIdPeticion(respuestaPadron.getFaultSCSPMessage().getFaultInfo().getIdPeticion());
    }
  }

  private void setDatosEspecificos(TransmisionDatosPadronDto transmisionDatos, PerPadronEntity perPadronEntity) {
    SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
    RetornoPadronDto retorno = transmisionDatos.getDatosEspecificos().getRetorno();
    if (retorno != null) {
      setDomicilioPadron(perPadronEntity, format, retorno);
    }
  }

  private void setDomicilioPadron(PerPadronEntity perPadronEntity, SimpleDateFormat format, RetornoPadronDto retorno) {
    Domicilio domicilio = retorno.getDomicilio();
    if (domicilio != null) {
      perPadronEntity.setNombreProvincia(domicilio.getProvinciaRespuesta().getNombre());
      perPadronEntity.setCodProvincia(domicilio.getProvinciaRespuesta().getCodigo());
      perPadronEntity.setNombreMunicipio(domicilio.getMunicipioRespuesta().getNombre());
      perPadronEntity.setCodMunicipio(domicilio.getMunicipioRespuesta().getCodigo());
      perPadronEntity.setNombreEntidadColectiva(domicilio.getEntColectiva().getNombre());
      perPadronEntity.setNombreEntidadSingular(domicilio.getEntSingular().getNombre());
      perPadronEntity.setNombreNucleo(domicilio.getNucleo().getNombre());
      perPadronEntity.setDesVariacion(domicilio.getUltimaVariacion().getCodigo());
      String fechaVariacionString = domicilio.getUltimaVariacion().getFecha();
      if (!StringUtils.isEmpty(fechaVariacionString)) {
        try {
          perPadronEntity.setFechaVariacion(format.parse(fechaVariacionString));
        } catch (ParseException e) {
          Log.info("Se ha producido un error al formatear fecha de variación", e);
        }
      }
      Direccion direccion = domicilio.getDireccion();
      if (direccion != null) {
        perPadronEntity.setNombreVia(direccion.getVia().getNombre());
        perPadronEntity.setNumeroValor(direccion.getNumero().getValor());
        perPadronEntity.setNumeroSuperiorValor(direccion.getNumeroSuperior().getValor());
        perPadronEntity.setKilometro(direccion.getKmt());
        perPadronEntity.setBloque(direccion.getBloque());
        perPadronEntity.setPortal(direccion.getPortal());
        perPadronEntity.setEscalera(direccion.getEscalera());
        perPadronEntity.setPlanta(direccion.getPlanta());
        perPadronEntity.setPuerta(direccion.getPuerta());
      }
    }
  }

  @Override
  public PerPadronDto getPerPadronByIdPer(BigInteger idPersona) throws SinacException {
    PerPadronDto perPadronDto = new PerPadronDto();
    PerPadronEntity perPadronEntity = perPadronDao.getPerPadronByIdPer(idPersona);
    if (perPadronEntity != null) {
      perPadronDto = perPadronMapper.toDto(perPadronEntity);
    }
    return perPadronDto;
  }

  @Override
  public void validarDocumentosEntradaByIdExp(BigInteger idExp) throws SinacException {
    try {
      Set<ExpedienteDocumentoEntity> listaExpedienteDocumentos = expedienteDocumentoDao
          .getExpedienteDocumentosByIdExp(idExp);
      List<ExpedienteDocumentoEntity> listaConvertida = new ArrayList<>(listaExpedienteDocumentos);
      LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findByCodigo("EDOC-VAL");
      for (ExpedienteDocumentoEntity expDoc : listaConvertida) {
        if (expDoc.isFlgActivo() && expDoc.getDocumentoTipoEntity().getProcedimientosDocumentosTipoEntities().stream()
            .toList().get(0).isFlgDocEnt()) {
          expDoc.setLdvMaestraEntityByIdEstDocLdv(ldvMaestraEntity);
          expedienteDocumentoDao.save(expDoc);
        }
      }
    } catch (Exception e) {
      LOG.info("Se ha producido un error al validar los documentos de entrada del expediente ".concat(e.getMessage()));
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_89).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void updateInactivaDocSalida(BigInteger expediente, String codLdvMae) {
    expedienteDocumentoDao.updateInhabilitarDoc(expediente, ldvMaestraDao.findByCodigo(codLdvMae));

  }

  @Override
  public void updateBorrarResolucionExp(BigInteger expediente) {
    expedienteDao.updateResolucionExp(expediente);

  }

  @Override
  public Boolean isEstadoRetroaccion(BigInteger expediente) {
    return expedienteEstadoDao.isEstadoRetroaccion(expediente);
  }

  @Override
  public ExpedienteFormularioValDto getExpedienteFormularioCampo(BigInteger idExp, String codForm) {
    return expedienteFormularioValMapper.toDto(expedienteFormularioValDao.getExpFormPorIdExpYCodForm(idExp, codForm));
  }

  @Override
  public List<EstadoDto> getEstadosByidProcedimientoidTramiteFaseCodAccion(Short idPro, String codAccion)
      throws SinacException {
    LOG.debug("ExpedientesServiceImpl.getEstadosByidProcedimientoidTramiteFaseCodAccion - Init");
    try {
      List<EstadoEntity> estados = estadoDao.getEstadosByidProcedimientoidTramiteFaseCodAccion(idPro, codAccion);
      LOG.debug("ExpedientesServiceImpl.getEstadosByidProcedimientoidTramiteFaseCodAccion - End");
      return estadoMapper.toDtoList(estados);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_90).logMessageParams(codAccion)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public short getProcedimientoOrigenExp(BigInteger idExp) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.getProcedimientoOrigenExp - Init");
    try {
      short idPor = procedimientoDao.getIdProcedimientoOrigen(idExp);
      LOG.debug("ExpedientesServiceImpl.getProcedimientoOrigenExp - End");
      return idPor;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_91).logMessageParams(idExp)
          .type(SinacExceptionType.DATA);
    }
  }

  public Boolean existeDocumentoExpediente(String tipoDoc, BigInteger idExp) throws SinacException {
    return expedienteDocumentoDao.existeDocumentoExpediente(tipoDoc, idExp);
  }

  public void saveDatosBoe(ExpedienteBoeDto expedienteBoeDto) throws SinacException {
    List<BoeAnunciosDto> anunciosBoeDtos = expedienteBoeDto.getBoeAnunciosDtos();
    ExpedienteBoeEntity expedienteBoeEntityAnterior = expedienteBoeDao
        .getExpedienteBoeByIdEnvio(expedienteBoeDto.getIdEnvio());
    ExpedienteBoeEntity expedienteBoeEntity = expedienteBoeMapper.toEntity(expedienteBoeDto);
    if (expedienteBoeEntityAnterior != null) {
      expedienteBoeEntity.setFlgActivo(true);
      expedienteBoeEntity.setFechaIniVig(expedienteBoeEntityAnterior.getFechaIniVig());
      expedienteBoeEntity.setCreadoPor(expedienteBoeEntityAnterior.getCreadoPor());
      expedienteBoeEntity.setFechaCreacion(expedienteBoeEntityAnterior.getFechaCreacion());
    }
    expedienteBoeDao.save(expedienteBoeEntity);
    for (BoeAnunciosDto anuncioBoeDto : anunciosBoeDtos) {
      List<BoeAnunciosListasDto> avisosAnuncioDtos = anuncioBoeDto.getBoeAnunciosListasDtos();
      BoeAnunciosEntity anuncioBoeEntityAnterior = boeAnunciosDao.getAnuncioBoeById(anuncioBoeDto.getIdBoeAnuncios());
      BoeAnunciosEntity anuncioBoeEntity = boeAnunciosMapper.toEntity(anuncioBoeDto);
      if (expedienteBoeEntityAnterior != null) {
        anuncioBoeEntity.setFlgActivo(true);
        anuncioBoeEntity.setFechaIniVig(anuncioBoeEntityAnterior.getFechaIniVig());
        anuncioBoeEntity.setCreadoPor(anuncioBoeEntityAnterior.getCreadoPor());
        anuncioBoeEntity.setFechaCreacion(anuncioBoeEntityAnterior.getFechaCreacion());
      }
      anuncioBoeEntity.setExpedienteBoeEntity(expedienteBoeEntity);
      boeAnunciosDao.save(anuncioBoeEntity);
      for (BoeAnunciosListasDto boeAnunciosListasDto : avisosAnuncioDtos) {
        BoeAnunciosListasEntity avisosAnuncioEntity = boeAnunciosListasMapper.toEntity(boeAnunciosListasDto);
        avisosAnuncioEntity.setBoeAnunciosEntity(anuncioBoeEntity);
        boeAnunciosListasDao.save(avisosAnuncioEntity);
      }
    }

  }

  @Override
  public List<String> getIdsEnvioJobBoe() throws SinacException {
    return expedienteBoeDao.getIdsEnvioJobBoe();
  }

  @Override
  public ExpedienteBoeDto getExpedienteBoeByIdEnvio(String idEnvio) throws SinacException {
    return expedienteBoeMapper.toDto(expedienteBoeDao.getExpedienteBoeByIdEnvio(idEnvio));
  }

  @Override
  public List<BoeAnunciosDto> getBoeAnunciosByIdExpBoe(BigInteger idExpBoe) throws SinacException {
    List<BoeAnunciosEntity> anunciosBoeEntities = boeAnunciosDao.getAnunciosBoeByIdExpBoe(idExpBoe);
    List<BoeAnunciosDto> anunciosBoeDtos = new ArrayList<>();
    for (BoeAnunciosEntity entity : anunciosBoeEntities) {
      anunciosBoeDtos.add(boeAnunciosMapper.toDto(entity));
    }
    return anunciosBoeDtos;
  }

  @Override
  public PersonaDto getInteresadoByIdExp(BigInteger idExpediente) throws SinacException {
    try {
      Set<ExpedientesPersonasEntity> expPers = expedientesPersonasDao.getExpPersonasBasicoPorIdExp(idExpediente);
      List<ExpedientesPersonasEntity> expPersLista = expPers.stream()
          .filter(persona -> persona.getLdvMaestraEntity().getCodLdvMae().equals("PER-INT"))
          .collect(Collectors.toList());
      Hibernate.initialize(expPersLista.get(0).getPersonaEntity().getPersonasIdentificaEntities());
      PersonaDto interesado = personaMapper.toDto(expPersLista.get(0).getPersonaEntity());
      return interesado;
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_92).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void saveDocumentoInterno(ExpedienteDocumentoDto expedienteDocumentoDto, ExpedienteDto expedienteDto,
      BigInteger idExpInf) throws SinacException {
    LOG.info("ExpedientesFacadeImpl.saveDocumentoInterno - Init");
    DataHandler contenido = null;
    if (expedienteDocumentoDto.getIdExpDoc() != null) {
      expedienteDocumentoDto = documentosService
          .getExpedienteDocumentoByIdDocumento(expedienteDocumentoDto.getIdExpDoc());
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_93)
          .logMessageParams(expedienteDto.getIdExp());
    }
    LOG.info("ExpedientesFacadeImpl.saveDocumentoInterno - estado del documento {} con el expediente: {}",
        expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv().getNomLdvMae(), expedienteDto.getIdExp());
    if (expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv() != null
        && !"EDOC-EGD".equals(expedienteDocumentoDto.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae())) {
      try {
        contenido = documentosService.signDocumento(expedienteDocumentoDto);
        if (contenido != null) {
          documentosService.copyDocumentoNFS(expedienteDocumentoDto, contenido);
        } else {
          throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_94);
        }
      } catch (SinacException e) {
        if ("INDGP".equals(expedienteDocumentoDto.getDocumentoTipoDto().getCodTipo())) {
          saveExpedienteInformeDgp(idExpInf, expedienteDocumentoDto);
        }
        LOG.error("ClienteFirmaServidorConnectorImpl.signDocumento - Error: No ha  sido posible firmar el documento "
            + "porque ha habido un error durante el proceso de firma del documento. Error: " + e.getMessage());
      }
    } else {
      contenido = documentosService.getContenido(expedienteDocumentoDto);
    }
    LOG.info("ExpedientesFacadeImpl.saveDocumentoInterno - End");
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void saveExpedienteInformeDgp(BigInteger idExpInf, ExpedienteDocumentoDto expedienteDocumentoDto) {
    ExpedienteDocumentoEntity expedienteDocumentoEntity = expedienteDocumentoMapper.toEntity(expedienteDocumentoDto);
    ExpedienteInformeEntity expedienteInformeEntity = new ExpedienteInformeEntity();
    expedienteInformeEntity = expedienteInformeDao.findById(idExpInf).orElseThrow(
        () -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_124).logMessageParams(idExpInf));
    expedienteInformeEntity.setExpedienteDocumentoEntity(expedienteDocumentoEntity);
    expedienteInformeDao.save(expedienteInformeEntity);
    LOG.info("Se ha guardado correctamente el documento {} en el infExp {}", expedienteDocumentoDto.getNomDoc(),
        idExpInf);
    String tipoInforme = expedienteInformeEntity.getLdvMaestraEntityByIdInfLdv().getCodLdvMae();
    BigInteger idExp = expedienteInformeEntity.getExpedienteEntity().getIdExp();
    try {
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          expedienteInformeEntity.getLdvMaestraEntityByIdEstInfLdv().getCodLdvMae());
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());

    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_95).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public BigInteger saveDocumentoExpedienteDgp(ExpedienteDocumentoDto expedienteDocumento, ExpedienteDto expedienteDto,
      BigInteger idExpInf) {
    BigInteger idExpedienteDocumentoFinal = null;
    String nombreDocumento = expedienteDocumento.getNomDoc().replaceAll(".odt", ".pdf");
    expedienteDocumento.setNomDoc(nombreDocumento);
    if (expedienteDocumento.getContenido() != null) {
      DataSource dataSource = new ByteArrayDataSource(expedienteDocumento.getContenido(),
          "application/vnd.oasis.opendocument.text");
      DataHandler dataHandler = new DataHandler(dataSource);
      documentosService.copyDocumentoNFS(expedienteDocumento, dataHandler);
      saveDocumentoInterno(expedienteDocumento, expedienteDto, idExpInf);
      idExpedienteDocumentoFinal = expedienteDocumento.getIdExpDoc();
    } else {
      throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_96).type(SinacExceptionType.VALIDATION);
    }
    return idExpedienteDocumentoFinal;
  }

  @Override
  public List<ExpedienteInformeDto> getExpedienteInformesByIdExp(BigInteger idExp) throws SinacException {
    List<ExpedienteInformeDto> informesDto = null;
    try {
      List<ExpedienteInformeEntity> informesEntity = expedienteInformeDao.getExpedienteInformesByIdExpediente(idExp);
      informesDto = expedienteInformeMapper.toDto(informesEntity);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_97).type(SinacExceptionType.DATA);
    }
    return informesDto;
  }

  @Override
  public void informeSolicitadoMde(BigInteger idExp, String tipoInforme, Date fechaSolicitud, BigInteger idExpInf,
      String codLdvEjercito) throws SinacException {
    ExpedienteEntity expedienteEntity = expedienteDao.findById(idExp)
        .orElseThrow(() -> new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_125).logMessageParams(idExp));
    LdvMaestraEntity ldvMaestraEntityByIdInfLdv = ldvMaestraDao.findByCodigo(tipoInforme);
    LdvMaestraEntity ldvMaestraEntityByIdEstInfLdv = ldvMaestraDao
        .findByCodigo(Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
    ExpedienteInformeEntity expedienteInformeEntity = new ExpedienteInformeEntity();
    if (idExpInf != null) {
      expedienteInformeEntity = expedienteInformeDao.getExpedienteInformeByIdExpedienteInforme(idExpInf);
    }
    expedienteInformeEntity.setExpedienteEntity(expedienteEntity);
    expedienteInformeEntity.setLdvMaestraEntityByIdInfLdv(ldvMaestraEntityByIdInfLdv);
    expedienteInformeEntity.setLdvMaestraEntityByIdEstInfLdv(ldvMaestraEntityByIdEstInfLdv);
    expedienteInformeEntity.setFechaSolicitud(fechaSolicitud);
    try {
      expedienteInformeDao.save(expedienteInformeEntity);
      updateValidacionSemaforo(idExp, "VAL_".concat(tipoInforme.substring(CINCO)),
          Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
      recalcularValidadionesConducta(idExp,
          ListadoValidacionesCodEntMaeEnum.LISTADO_CONDUCTA_CIVICA.getListaValidaciones());
    } catch (PersistenceException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_98);
    } catch (SinacException ex) {
      LOG.info("Se ha producido un error intentar actualizar las validacion del Informe ".concat(tipoInforme)
          .concat(" del semaforo, para el expediente ").concat(idExp.toString()).concat(ex.getMessage()));
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_99).type(SinacExceptionType.DATA);
    }
    ExpedienteInformeMdeEntity expedienteInformeMdeEntity = new ExpedienteInformeMdeEntity();
    expedienteInformeMdeEntity.setExpedienteInformeEntity(expedienteInformeEntity);
    expedienteInformeMdeEntity.setEjercitoLdv(ldvMaestraDao.findByCodigo(codLdvEjercito));
    expedienteInformeMdeDao.save(expedienteInformeMdeEntity);

  }

  @Override
  public void updateInformeDgpDocumento(BigInteger idExpInf, BigInteger idExpedienteDocumento) throws SinacException {
    try {
      Optional<ExpedienteInformeEntity> informeOpt = expedienteInformeDao.findById(idExpInf);

      if (informeOpt.isPresent()) {
        expedienteInformeDao.updateIdDocumentoInforme(idExpInf, idExpedienteDocumento);
      } else {
        throw new SinacException(SinacExceptionMessageType.SINAC_EXPEDIENTES_100);
      }
    } catch (final Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_101).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public ExpedienteDto getExpedienteByIdExpedienteInforme(BigInteger idExpedienteInforme) throws SinacException {
    LOG.info("ExpedientesServiceImpl.getExpedienteByIdExpediente - Init");
    ExpedienteDto expedienteDto = null;
    try {
      expedienteDto = expedienteMapper.toDto(expedienteDao.getExpedienteByIdExpedienteInforme(idExpedienteInforme));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_102)
          .logMessageParams(idExpedienteInforme).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_103)
          .logMessageParams(idExpedienteInforme).type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.getExpedienteByIdExpediente - End");
    return expedienteDto;
  }

  @Override
  public Map<String, ExpedienteInformeDto> getInformesByIdExpediente(BigInteger idExpediente) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.getInformesByIdExpediente - Init");

    Map<String, ExpedienteInformeDto> expedienteInformeDtoMap = null;

    try {
      List<ExpedienteInformeDto> expedienteInformeDtoList = expedienteInformeMapper.toDto(expedienteInformeDao
          .getInformesByIdExpediente(idExpediente).filter(list -> !CollectionUtils.isEmpty(list)).orElseThrow());

      expedienteInformeDtoMap = expedienteInformeDtoList.stream().collect(
          Collectors.toMap(expedienteInformeDto -> expedienteInformeDto.getLdvMaestraDtoByIdInfLdv().getCodLdvMae(),
              Function.identity(), (existing, replacement) -> existing));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_104)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_105)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    }

    LOG.debug("ExpedientesServiceImpl.getInformesByIdExpediente - End");

    return expedienteInformeDtoMap;
  }

  @Override
  public void updateEstadoInforme(BigInteger idInforme, LdvMaestraDto ldvMaestraDto) throws SinacException {
    LOG.debug("ExpedientesServiceImpl.updateEstadoInforme - Init");

    try {
      expedienteInformeDao.updateEstadoInforme(idInforme, ldvMaestraMapper.toEntity(ldvMaestraDto));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_106)
          .logMessageParams(idInforme).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_EXPEDIENTES_107).logMessageParams(idInforme)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("ExpedientesServiceImpl.updateEstadoInforme - End");

  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderMdeByIdExp(BigInteger idExp) {
    return procedimientosFasesTramitesOperacionesAccionesMapper
        .toDto(procedimientosFasesTramitesOperacionesAccionesDao.getPftoaResponderMdeByIdExp(idExp));
  }

  @Override
  public ExpedienteInformeDto getExpedienteInformesByIdExpCodTipoInformeActivo(BigInteger idExp,
      String codTipoInformeLdv) {
    return expedienteInformeMapper
        .toDto(expedienteInformeDao.getExpedienteInformesByIdExpCodTipoInformeActivo(idExp, codTipoInformeLdv));
  }

  @Override
  public void updateExpedienteSentResolByIdDoc(LdvMaestraEntity idSentidoResolucionLdv, BigInteger idExp)
      throws SinacException {
    LOG.debug("ExpedientesServiceImpl.updateExpedienteSentResolByIdDoc - Init");

    try {
      expedienteDao.updateExpedienteSentResolByIdDoc(idSentidoResolucionLdv, idExp);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_EXPEDIENTES_108)
          .logMessageParams(idSentidoResolucionLdv).type(SinacExceptionType.DATA);
    }

    LOG.debug("ExpedientesServiceImpl.updateExpedienteSentResolByIdDoc - End");
  }

  @Override
  public ExpedienteInformeMdeDto getExpedienteInformeMdeByIdExpedienteInforme(BigInteger idExpedienteInforme) {
    return expedienteInformeMdeMapper
        .toDto(expedienteInformeMdeDao.getExpedienteInformeMdeByIdExpedienteInforme(idExpedienteInforme));
  }

  // mcarbayo: TODO Comentado hasta que podamos remitir a Justicia
  @Override
  public InsideEstadoDto enviarExpedienteInside(InsideEstadoDto insideEstadoDto,
      ProcedimientosFasesTramitesOperacionesDto pfto, UsuarioDto usuario) throws SinacException {

    InsideAltaExpedienteXmlEniDto peticion = new InsideAltaExpedienteXmlEniDto();
    peticion.setUnidadOrganicaActiva(insideEstadoDto.getUnidadOrganica());

    return null; // TODO quitar este return

//      DescargaDeDocumentoDto expedienteEni = this.getExpedienteENI(insideEstadoDto.getExpediente().getIdExp());
//
//      try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(expedienteEni.getFile()))){
//        ZipEntry entry;
//        while ((entry = zipInputStream.getNextEntry()) != null) {
//          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//          byte[] buffer = new byte[1024];
//          int bytesRead;
//
//          while ((bytesRead = zipInputStream.read(buffer)) != -1) {
//            byteArrayOutputStream.write(buffer, 0, bytesRead);
//          }
//
//          byte[] fileContent = byteArrayOutputStream.toByteArray();
//          if (entry.getName().contains("EXP")) {
//            peticion.setExpediente(fileContent);
//          } else {
//            peticion.getDocumentos().add(fileContent);
//          }
//          zipInputStream.closeEntry();
//        }
//
//        InsideAltaExpedienteEniXmlResultadoDto resultado = insideService.altaExpedienteEniXml(peticion);
//
//        InsideEstadoDto nextInsideEstadoDto = new InsideEstadoDto();
//        nextInsideEstadoDto.setEstado(InsideEnviosEstadosEnum.ALTA_EXPEDIENTE);
//        nextInsideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.ALTA_EXPEDIENTE.getDescripcionCodigo());
//        nextInsideEstadoDto.setFechaEstado(new Date());
//        nextInsideEstadoDto.setIdentificador(resultado.getIdentificador());
//        nextInsideEstadoDto.setUnidadOrganica(insideEstadoDto.getUnidadOrganica());
//        nextInsideEstadoDto.setDir3Juzgado(insideEstadoDto.getDir3Juzgado());
//        nextInsideEstadoDto.setRemisionNig(insideEstadoDto.getRemisionNig());
//        nextInsideEstadoDto.setNumProcedimiento(insideEstadoDto.getNumProcedimiento());
//        nextInsideEstadoDto.setClaseProcedimiento(insideEstadoDto.getClaseProcedimiento());
//        nextInsideEstadoDto.setAnhoProcedimiento(insideEstadoDto.getAnhoProcedimiento());
//        nextInsideEstadoDto.setDescripcion(insideEstadoDto.getDescripcion());
//        nextInsideEstadoDto.setExpediente(insideEstadoDto.getExpediente());
//        nextInsideEstadoDto.setProcedimientosFasesTramitesOperaciones(pfto);
//
//        insideEstadosService.saveEstado(nextInsideEstadoDto);
//
//        return nextInsideEstadoDto;
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      } catch (Exception e) {
//        InsideEstadoDto nextInsideEstadoDto = new InsideEstadoDto();
//        if (insideEstadoDto.getEstado() == InsideEnviosEstadosEnum.ERROR_ALTA_EXPEDIENTE) {
//          nextInsideEstadoDto = insideEstadoDto; // Hacemos que se actualice el ultimo para evitar llenar de errores
//        }
//        nextInsideEstadoDto.setEstado(InsideEnviosEstadosEnum.ERROR_ALTA_EXPEDIENTE);
//        nextInsideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.ERROR_ALTA_EXPEDIENTE.getDescripcionCodigo());
//        nextInsideEstadoDto.setFechaEstado(new Date());
//        nextInsideEstadoDto.setUnidadOrganica(insideEstadoDto.getUnidadOrganica());
//        nextInsideEstadoDto.setDir3Juzgado(insideEstadoDto.getDir3Juzgado());
//        nextInsideEstadoDto.setRemisionNig(insideEstadoDto.getRemisionNig());
//        nextInsideEstadoDto.setNumProcedimiento(insideEstadoDto.getNumProcedimiento());
//        nextInsideEstadoDto.setClaseProcedimiento(insideEstadoDto.getClaseProcedimiento());
//        nextInsideEstadoDto.setAnhoProcedimiento(insideEstadoDto.getAnhoProcedimiento());
//        nextInsideEstadoDto.setDescripcion(insideEstadoDto.getDescripcion());
//        nextInsideEstadoDto.setExpediente(insideEstadoDto.getExpediente());
//        nextInsideEstadoDto.setProcedimientosFasesTramitesOperaciones(pfto);
//        nextInsideEstadoDto.setNotas(StringUtils.truncate(e.getMessage(), 490));
//
//        insideEstadosService.saveEstado(nextInsideEstadoDto);
//
//        return null;
//      }
  }

  @Override
  public void remitiarAJusticia(InsideEstadoDto insideEstadoDto, UsuarioDto usuario) throws SinacException {
    InsideRemisionJusticiaDto peticion = new InsideRemisionJusticiaDto();
    peticion.setIdExpEni(insideEstadoDto.getIdentificador());
    peticion.setRemisionDir3Remitente(insideOrgOrigen);
    peticion.setDir3Juzgado(insideEstadoDto.getDir3Juzgado());
    peticion.setRemisionNig(insideEstadoDto.getRemisionNig());
    peticion.setRemisionNumProcedimiento(insideEstadoDto.getNumProcedimiento());
    peticion.setRemisionClaseProcedimiento(insideEstadoDto.getClaseProcedimiento());
    peticion.setRemisionAnyoProcedimiento(insideEstadoDto.getAnhoProcedimiento().toString());
    peticion.setRemisionDescipcion(insideEstadoDto.getDescripcion());
    try {
      insideService.remitirAJusticia(peticion);

      InsideEstadoDto nextInsideEstadoDto = new InsideEstadoDto();

      nextInsideEstadoDto.setEstado(InsideEnviosEstadosEnum.REMITIDO_JUSTICIA);
      nextInsideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.REMITIDO_JUSTICIA.getDescripcionCodigo());
      nextInsideEstadoDto.setFechaEstado(new Date());
      nextInsideEstadoDto.setIdentificador(insideEstadoDto.getIdentificador());
      nextInsideEstadoDto.setUnidadOrganica(insideEstadoDto.getUnidadOrganica());
      nextInsideEstadoDto.setDir3Juzgado(insideEstadoDto.getDir3Juzgado());
      nextInsideEstadoDto.setRemisionNig(insideEstadoDto.getRemisionNig());
      nextInsideEstadoDto.setNumProcedimiento(insideEstadoDto.getNumProcedimiento());
      nextInsideEstadoDto.setClaseProcedimiento(insideEstadoDto.getClaseProcedimiento());
      nextInsideEstadoDto.setAnhoProcedimiento(insideEstadoDto.getAnhoProcedimiento());
      nextInsideEstadoDto.setDescripcion(insideEstadoDto.getDescripcion());
      nextInsideEstadoDto.setExpediente(insideEstadoDto.getExpediente());
      nextInsideEstadoDto
          .setProcedimientosFasesTramitesOperaciones(insideEstadoDto.getProcedimientosFasesTramitesOperaciones());

      insideEstadosService.saveEstado(nextInsideEstadoDto);
    } catch (Exception e) {
      InsideEstadoDto nextInsideEstadoDto = new InsideEstadoDto();
      if (insideEstadoDto.getEstado() == InsideEnviosEstadosEnum.ERROR_REMISION_JUSTICIA) {
        nextInsideEstadoDto = insideEstadoDto; // Hacemos que se actualice el ultimo para evitar llenar de errores
      }
      nextInsideEstadoDto.setEstado(InsideEnviosEstadosEnum.ERROR_REMISION_JUSTICIA);
      nextInsideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.ERROR_REMISION_JUSTICIA.getDescripcionCodigo());
      nextInsideEstadoDto.setFechaEstado(new Date());
      nextInsideEstadoDto.setIdentificador(insideEstadoDto.getIdentificador());
      nextInsideEstadoDto.setUnidadOrganica(insideEstadoDto.getUnidadOrganica());
      nextInsideEstadoDto.setDir3Juzgado(insideEstadoDto.getDir3Juzgado());
      nextInsideEstadoDto.setRemisionNig(insideEstadoDto.getRemisionNig());
      nextInsideEstadoDto.setNumProcedimiento(insideEstadoDto.getNumProcedimiento());
      nextInsideEstadoDto.setClaseProcedimiento(insideEstadoDto.getClaseProcedimiento());
      nextInsideEstadoDto.setAnhoProcedimiento(insideEstadoDto.getAnhoProcedimiento());
      nextInsideEstadoDto.setDescripcion(insideEstadoDto.getDescripcion());
      nextInsideEstadoDto.setExpediente(insideEstadoDto.getExpediente());
      nextInsideEstadoDto
          .setProcedimientosFasesTramitesOperaciones(insideEstadoDto.getProcedimientosFasesTramitesOperaciones());
      insideEstadoDto.setNotas(StringUtils.truncate(e.getMessage(), 490));

      insideEstadosService.saveEstado(nextInsideEstadoDto);
//      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_109).type(SinacExceptionType.BUSINESS);
    }
  }

  @Override
  public void consultarEstadoRemisionAJusticia(InsideEstadoDto insideEstadoDto, UsuarioDto usuario)
      throws SinacException {
    try {
      InsideConsultaRemisionJusticiaResultadoDto resultado = insideService
          .consultarEstadoRemitirAJusticia(insideEstadoDto.getIdentificador());

      // TODO revisar estados de consulta en inside
      if (!resultado.getIdentificadorEstado().equals(insideEstadoDto.getEstado().getCodigo())) {
        insideEstadoDto.setFlgActivo(false);
        insideEstadosService.saveEstado(insideEstadoDto);

        InsideEnviosEstadosEnum ultimoEstado = InsideEnviosEstadosEnum.getByCodigo(resultado.getIdentificadorEstado());

        // TODO Revisar datos de documento de entrada
        DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
        documentoToSaveDto.setContenido(resultado.getResguardo());
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        documentoToSaveDto
            .setNombre("RESGUARDO_INSIDE_" + insideEstadoDto.getIdentificador() + "_" + timeStamp + ".pdf");
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
        documentoToSaveDto.setCreadoPor(insideEstadoDto.getCreadoPor());
        LinkedList<DocumentoToSaveDto> documentosToSaveDto = new LinkedList<>();
        documentosToSaveDto.add(documentoToSaveDto);
        List<DocumentoToSaveDto> documentosGuardados = expedientesFacade
            .saveDocumentosEntradaExpediente(insideEstadoDto.getExpediente().getIdExp(), documentosToSaveDto);

        Date fechaActual = new Date();
        InsideEstadoDto insideEstadoDtoConsulta = new InsideEstadoDto();
        insideEstadoDtoConsulta.setEstado(ultimoEstado);
        insideEstadoDtoConsulta.setDescripcionEstado(ultimoEstado.getDescripcionCodigo());
        insideEstadoDtoConsulta.setFechaEstado(fechaActual);
        insideEstadoDtoConsulta.setIdentificador(insideEstadoDto.getIdentificador());
        insideEstadoDtoConsulta.setUnidadOrganica(insideEstadoDto.getUnidadOrganica());
        insideEstadoDtoConsulta.setExpediente(insideEstadoDto.getExpediente());
        insideEstadoDtoConsulta.setExpedienteDocumento(expedienteDocumentoMapper
            .toDto(expedienteDocumentoDao.findExpedienteDocumentoById(documentosGuardados.get(0).getIdExpDoc())));
        insideEstadoDtoConsulta
            .setProcedimientosFasesTramitesOperaciones(insideEstadoDto.getProcedimientosFasesTramitesOperaciones());

        insideEstadosService.saveEstado(insideEstadoDtoConsulta);
      }
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_110).type(SinacExceptionType.BUSINESS);
    }
  }

  @Override
  public void saveInsideConfig(BigInteger idExp, List<ExpedienteInsideDto> expedienteInsideDtos) {
    List<ExpedienteInsideEntity> expInsideAntiguos = expedienteInsideDao.findByExpedienteIdExpAndFlgActivo(idExp, true);
    List<ExpedienteInsideEntity> expInsideNuevos = expedienteInsideMapper.toEntities(expedienteInsideDtos);

    for (ExpedienteInsideEntity expInsideAntiguo : expInsideAntiguos) {
      boolean sigueExistiendo = false;
      for (ExpedienteInsideEntity expInsideNuevo : expInsideNuevos) {
        if (expInsideAntiguo.getDir3Juzgado().equals(expInsideNuevo.getDir3Juzgado())) {
          // Ya existe, se actualizan los datos secundarios si es necesario
          sigueExistiendo = true;
          if (!StringUtils.equals(expInsideAntiguo.getRemisionNig(), expInsideNuevo.getRemisionNig())
              || !StringUtils.equals(expInsideAntiguo.getNumProcedimiento(), expInsideNuevo.getNumProcedimiento())
              || !StringUtils.equals(expInsideAntiguo.getClaseProcedimiento(), expInsideNuevo.getClaseProcedimiento())
              || !Objects.equals(expInsideAntiguo.getAnhoProcedimiento(), expInsideNuevo.getAnhoProcedimiento())
              || !StringUtils.equals(expInsideAntiguo.getDescripcion(), expInsideNuevo.getDescripcion())) {
            expInsideAntiguo.setRemisionNig(expInsideNuevo.getRemisionNig());
            expInsideAntiguo.setNumProcedimiento(expInsideNuevo.getNumProcedimiento());
            expInsideAntiguo.setClaseProcedimiento(expInsideNuevo.getClaseProcedimiento());
            expInsideAntiguo.setAnhoProcedimiento(expInsideNuevo.getAnhoProcedimiento());
            expInsideAntiguo.setDescripcion(expInsideNuevo.getDescripcion());
            expedienteInsideDao.save(expInsideAntiguo);
          }
          break;
        }
      }
      if (!sigueExistiendo) {
        // Ahora ya no existe, se desactiva
        expedienteInsideDao.desactivarPorId(expInsideAntiguo.getIdExpInside());

        InsideEstadoDto insideEstadoDto = new InsideEstadoDto();
        insideEstadoDto.setEstado(InsideEnviosEstadosEnum.CANCELADO);
        insideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.CANCELADO.getDescripcionCodigo());
        insideEstadoDto.setFechaEstado(new Date());
        insideEstadoDto.setDir3Juzgado(expInsideAntiguo.getDir3Juzgado());
        insideEstadoDto.setExpediente(expedienteMapper.toDto(expInsideAntiguo.getExpediente()));
        insideEstadosService.saveEstado(insideEstadoDto);
      }
    }
    for (ExpedienteInsideEntity expInsideNuevo : expInsideNuevos) {
      boolean yaExistia = false;
      for (ExpedienteInsideEntity expInsideAntiguo : expInsideAntiguos) {
        if (expInsideAntiguo.getDir3Juzgado().equals(expInsideNuevo.getDir3Juzgado())) {
          // Ya existe, no se hace nada
          yaExistia = true;
          break;
        }
      }
      if (!yaExistia) {
        expedienteInsideDao.save(expInsideNuevo);

        InsideEstadoDto insideEstadoDto = new InsideEstadoDto();
        insideEstadoDto.setEstado(InsideEnviosEstadosEnum.PENDIENTE_ALTA_EXPEDIENTE);
        insideEstadoDto.setDescripcionEstado(InsideEnviosEstadosEnum.PENDIENTE_ALTA_EXPEDIENTE.getDescripcionCodigo());
        insideEstadoDto.setFechaEstado(new Date());
        insideEstadoDto.setUnidadOrganica(insideUnidadOrganicaActiva);
        insideEstadoDto.setDir3Juzgado(expInsideNuevo.getDir3Juzgado());
        insideEstadoDto.setRemisionNig(expInsideNuevo.getRemisionNig());
        insideEstadoDto.setNumProcedimiento(expInsideNuevo.getNumProcedimiento());
        insideEstadoDto.setClaseProcedimiento(expInsideNuevo.getClaseProcedimiento());
        insideEstadoDto.setAnhoProcedimiento(expInsideNuevo.getAnhoProcedimiento());
        insideEstadoDto.setDescripcion(expInsideNuevo.getDescripcion());
        insideEstadoDto.setExpediente(expedienteMapper.toDto(expInsideNuevo.getExpediente()));
        insideEstadosService.saveEstado(insideEstadoDto);
      }
    }
  }

  @Override
  public void saveAltaFiliaciones(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto, PersonaDto personaDto,
      ExpedienteDto expedienteDto) {
    List<PerFilNiesDto> perFilNiesDtos = new ArrayList<>();
    PerFiliacionesDto perFiliacionesDto = new PerFiliacionesDto();
    perFiliacionesDto.setPersonaDto(personaDto);
    perFiliacionesDto.setIdPeticion(respuestaAltaFiliacionDto.getCodigoPeticionRespuesta());
    perFiliacionesDto.setCodEstado(respuestaAltaFiliacionDto.getEstado().getCodigoEstado());
    perFiliacionesDto.setLiteralError(respuestaAltaFiliacionDto.getEstado().getLiteralError());
    if (respuestaAltaFiliacionDto.getDatosRespuesta().getNiesRespuesta() != null
        && !CollectionUtils.isEmpty(respuestaAltaFiliacionDto.getDatosRespuesta().getNiesRespuesta().getString())) {
      if (respuestaAltaFiliacionDto.getDatosRespuesta().getNiesRespuesta().getString().size() == 1) {
        saveNiesExistentesEnDgpEnInteresado(respuestaAltaFiliacionDto, personaDto, perFiliacionesDto);

      } else {
        perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(ldvMaestraService.getCatalogoByCod("VAL-DGP-AVN"));
      }
      for (String nie : respuestaAltaFiliacionDto.getDatosRespuesta().getNiesRespuesta().getString()) {
        PerFilNiesDto perFilNiesDto = new PerFilNiesDto();
        perFilNiesDto.setCodEstado(respuestaAltaFiliacionDto.getEstado().getCodigoEstado());
        perFilNiesDto.setLiteralError(respuestaAltaFiliacionDto.getEstado().getLiteralError());
        String nieSinac = "";
        if (nie.startsWith("0")) {
          nieSinac = "X" + nie.substring(1);
        } else if (nie.startsWith("1")) {
          nieSinac = "Y" + nie.substring(1);
        } else if (nie.startsWith("2")) {
          nieSinac = "Z" + nie.substring(1);
        }
        perFilNiesDto.setNie(CalculaNif.calcular(nieSinac));
        perFilNiesDto.setNieDgp(nie);
        perFilNiesDto.setIdPeticion(respuestaAltaFiliacionDto.getCodigoPeticionRespuesta());
        perFilNiesDto.setPersonaDto(personaDto);
        perFilNiesDtos.add(perFilNiesDto);
      }
    } else {
      saveNiesNuevosEnInteresado(respuestaAltaFiliacionDto, personaDto, perFilNiesDtos, perFiliacionesDto);

    }
    for (PerFilNiesDto perFilNieDto : perFilNiesDtos) {
      perFilNiesDao.save(perFilNiesWithPersonaMapper.toEntity(perFilNieDto));
    }
    desactivarFiliacionesByIdPersona(personaDto.getIdPer());
    perFiliacionesDao.save(perFiliacionesWithPersonaMapper.toEntity(perFiliacionesDto));

    try {
      if ("R".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
        LOG.info("Se actualiza la validación de Policía/Filiaciones del semáforo. (Alta)");
        updateValidacionSemaforo(expedienteDto.getIdExp(), VAL_POL,
            perFiliacionesDto.getLdvMaestraEstadoFiliacionDto().getCodLdvMae());
      }
    } catch (SinacException e) {
      LOG.info("Se ha producido un error intentar actualizar la validación de Policía/Filiaciones del semaforo. "
          .concat(e.getMessage()));
    }
  }

  private void saveNiesNuevosEnInteresado(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto, PersonaDto personaDto,
      List<PerFilNiesDto> perFilNiesDtos, PerFiliacionesDto perFiliacionesDto) {
    PerFilNiesDto perFilNiesDto = new PerFilNiesDto();
    perFilNiesDto.setCodEstado(respuestaAltaFiliacionDto.getEstado().getCodigoEstado());
    perFilNiesDto.setLiteralError(respuestaAltaFiliacionDto.getEstado().getLiteralError());
    String nieSinac = "";
    String nie = respuestaAltaFiliacionDto.getDatosRespuesta().getNieRespuesta();
    if (nie.startsWith("0")) {
      nieSinac = "X" + nie.substring(1);
    } else if (nie.startsWith("1")) {
      nieSinac = "Y" + nie.substring(1);
    } else if (nie.startsWith("2")) {
      nieSinac = "Z" + nie.substring(1);
    }
    perFilNiesDto.setNie(CalculaNif.calcular(nieSinac));
    perFilNiesDto.setNieDgp(respuestaAltaFiliacionDto.getDatosRespuesta().getNieRespuesta());
    perFilNiesDto.setIdPeticion(respuestaAltaFiliacionDto.getCodigoPeticionRespuesta());
    perFilNiesDto.setPersonaDto(personaDto);
    perFilNiesDtos.add(perFilNiesDto);
    perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(ldvMaestraService.getCatalogoByCod("VAL-DGP-SIN"));
    perFiliacionesDto.setReferencia(respuestaAltaFiliacionDto.getDatosRespuesta().getReferenciaRespuesta());
    // Se quitan los flag principal del resto de documentos
    PersonaEntity personaEntity = personaDao.getPersonaByIdPer(personaDto.getIdPer());
    for (PersonaIdentificaEntity personaIdentificaEntityAnterior : personaEntity.getPersonasIdentificaEntities()) {
      if (personaIdentificaEntityAnterior.isFlgPrincipal()) {
        personaIdentificaEntityAnterior.setFlgPrincipal(false);
        personaIdentificaDao.save(personaIdentificaEntityAnterior);
      }
    }
    // Se guarda el documento nie como principal
    PersonaIdentificaEntity personaIdentificaEntity = new PersonaIdentificaEntity();
    personaIdentificaEntity.setNumAcreditacion(CalculaNif.calcular(nieSinac));
    personaIdentificaEntity.setFlgPrincipal(true);
    personaIdentificaEntity.setLdvMaestraEntity(ldvMaestraDao.findByCodigo("DID-NIE"));
    personaIdentificaEntity.setPersonaEntity(personaEntity);
    personaIdentificaDao.save(personaIdentificaEntity);
  }

  private void saveNiesExistentesEnDgpEnInteresado(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto,
      PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto) {
    perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(ldvMaestraService.getCatalogoByCod("VAL-DGP-SIN"));
    LdvMaestraEntity ldvMaestraEntity = ldvMaestraDao.findByCodigo("DID-NIE");
    // Se quitan los flag principal del resto de documentos
    PersonaEntity personaEntity = personaDao.getPersonaByIdPer(personaDto.getIdPer());
    for (PersonaIdentificaEntity personaIdentificaEntityAnterior : personaEntity.getPersonasIdentificaEntities()) {
      if (personaIdentificaEntityAnterior.isFlgPrincipal()) {
        personaIdentificaEntityAnterior.setFlgPrincipal(false);
        personaIdentificaDao.save(personaIdentificaEntityAnterior);
      }
      if (personaIdentificaEntityAnterior.getLdvMaestraEntity().getCodLdvMae().equals("DID-NIE")) {
        personaIdentificaEntityAnterior.setFlgActivo(false);
        personaIdentificaDao.save(personaIdentificaEntityAnterior);
      }
    }
    // Se guarda el documento nie como principal
    PersonaIdentificaEntity personaIdentificaEntity = new PersonaIdentificaEntity();
    String nieSinac = "";
    String nieDgp = respuestaAltaFiliacionDto.getDatosRespuesta().getNiesRespuesta().getString().get(0);
    if (nieDgp.startsWith("0")) {
      nieSinac = "X" + nieDgp.substring(1);
    } else if (nieDgp.startsWith("1")) {
      nieSinac = "Y" + nieDgp.substring(1);
    } else if (nieDgp.startsWith("2")) {
      nieSinac = "Z" + nieDgp.substring(1);
    }
    personaIdentificaEntity.setNumAcreditacion(CalculaNif.calcular(nieSinac));
    personaIdentificaEntity.setFlgPrincipal(true);
    personaIdentificaEntity.setLdvMaestraEntity(ldvMaestraEntity);
    personaIdentificaEntity.setPersonaEntity(personaEntity);
    personaIdentificaDao.save(personaIdentificaEntity);
  }

  @Override
  public void savePermitirAltaFiliaciones(PersonaDto personaDto, ExpedienteDto expedienteDto) {
    PerFiliacionesEntity perFiliacionesEntity = new PerFiliacionesEntity();
    perFiliacionesEntity.setPersonaEntity(personaMapper.toEntity(personaDto));
    perFiliacionesEntity.setLdvMaestraEstadoFiliacionEntity(ldvMaestraDao.findByCodigo("VAL-DGP-PEN"));
    perFiliacionesDao.save(perFiliacionesEntity);
    if ("R".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
      LOG.info("Se actualiza la validación de Policía/Filiaciones del semáforo. (Alta)");
      updateValidacionSemaforo(expedienteDto.getIdExp(), VAL_POL,
          perFiliacionesEntity.getLdvMaestraEstadoFiliacionEntity().getCodLdvMae());
    }

  }

  @Override
  public void desactivarFiliacionesByIdPersona(BigInteger idPersona) {
    List<PerFiliacionesEntity> perFiliacionesEntities = perFiliacionesDao.getPerFiliacionesByIdPer(idPersona);
    if (!CollectionUtils.isEmpty(perFiliacionesEntities)) {
      for (PerFiliacionesEntity perFiliacionesEntity : perFiliacionesEntities) {
        perFiliacionesEntity.setFlgActivo(false);
        perFiliacionesEntity.setFechaFinVig(new Date());
        perFiliacionesDao.save(perFiliacionesEntity);
      }
    }

  }

  @Override
  public List<PerFiliacionesDto> getPerFiliacionesByIdPer(BigInteger idPersona) {
    List<PerFiliacionesEntity> perFiliacionesEntities = perFiliacionesDao.getPerFiliacionesByIdPer(idPersona);
    List<PerFiliacionesDto> perFiliacionesDtos = new ArrayList<>();
    for (PerFiliacionesEntity perFiliacionesEntity : perFiliacionesEntities) {
      perFiliacionesDtos.add(perFiliacionesWithPersonaMapper.toDto(perFiliacionesEntity));
    }
    return perFiliacionesDtos;
  }

  @Override
  public List<PerFilNiesDto> getPerFilNiesByIdPer(BigInteger idPersona) {
    List<PerFilNiesEntity> perFiliacionesEntities = perFilNiesDao.getPerFilNiesByIdPer(idPersona);
    List<PerFilNiesDto> perFiliacionesDtos = new ArrayList<>();
    for (PerFilNiesEntity perFiliacionesEntity : perFiliacionesEntities) {
      perFiliacionesDtos.add(perFilNiesWithPersonaMapper.toDto(perFiliacionesEntity));
    }
    return perFiliacionesDtos;
  }

  @Override
  public void desactivarPerFilNiesByIdPersonaMenosNie(BigInteger idPersona, String nie) {
    List<PerFilNiesEntity> perFilNiesEntities = perFilNiesDao.getPerFilNiesByIdPer(idPersona);
    for (PerFilNiesEntity perFilNiesEntity : perFilNiesEntities) {
      if (!nie.equals(perFilNiesEntity.getNie())) {
        perFilNiesEntity.setFlgActivo(false);
        perFilNiesEntity.setFechaFinVig(new Date());
        perFilNiesDao.save(perFilNiesEntity);
      }
    }
    desactivarFiliacionesByIdPersona(idPersona);
    PersonaEntity personaEntity = personaDao.getPersonaByIdPer(idPersona);
    for (PersonaIdentificaEntity personaIdentificaEntityAnterior : personaEntity.getPersonasIdentificaEntities()) {
      personaIdentificaEntityAnterior.setFlgPrincipal(false);
      personaIdentificaDao.save(personaIdentificaEntityAnterior);
    }
    PersonaIdentificaEntity personaIdentificaEntity = new PersonaIdentificaEntity();
    personaIdentificaEntity.setNumAcreditacion(nie);
    personaIdentificaEntity.setFlgPrincipal(true);
    personaIdentificaEntity.setLdvMaestraEntity(ldvMaestraDao.findByCodigo("DID-NIE"));
    personaIdentificaEntity.setPersonaEntity(personaEntity);
    personaIdentificaDao.save(personaIdentificaEntity);
    PerFiliacionesEntity perFiliacionesEntity = new PerFiliacionesEntity();
    perFiliacionesEntity.setPersonaEntity(personaEntity);
    perFiliacionesEntity.setLdvMaestraEstadoFiliacionEntity(ldvMaestraDao.findByCodigo("VAL-DGP-SIN"));
    perFiliacionesDao.save(perFiliacionesEntity);

  }

  @Override
  public void peticionConsultaNieFiliacion(String nie, PersonaDto personaDto) {
    ws_cons_fili_domic_nie.com.adexttra.Peticion peticion = new ws_cons_fili_domic_nie.com.adexttra.Peticion();
    ws_cons_fili_domic_nie.com.adexttra.Datos datos = new ws_cons_fili_domic_nie.com.adexttra.Datos();
    ws_cons_fili_domic_nie.com.adexttra.Titular titular = new ws_cons_fili_domic_nie.com.adexttra.Titular();
    titular.setNie(nie);
    datos.setTitular(titular);
    datos.setEquipo(equipo);
    peticion.setDatos(datos);
    ws_cons_fili_domic_nie.com.adexttra.Solicitante solicitante = new ws_cons_fili_domic_nie.com.adexttra.Solicitante();
    solicitante.setCodigoPeticion(
        "nac" + new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(java.util.Calendar.getInstance().getTime()));
    solicitante.setIdentificacionPuesto(identificacionPuestoFiliaciones);
    ws_cons_fili_domic_nie.com.adexttra.Organismo organismo = new ws_cons_fili_domic_nie.com.adexttra.Organismo();
    organismo.setCodigoOrganismo(codOrganismoFiliaciones);
    organismo.setNombreOrganismo(nombreOrganismoFiliaciones);
    solicitante.setOrganismo(organismo);
    solicitante.setUsuario(usuarioFiliaciones);
    peticion.setSolicitante(solicitante);
    RespuestaConsultaNieFiliacionDto respuestaConsultaNieFiliacionesDto;
    try {
      respuestaConsultaNieFiliacionesDto = filiacionesService.peticionConsultaNieFiliacion(peticion);
    } catch (SinacFiliacionesException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_111).userMessageParams(e.getMessage());
    }

    saveReferenciasFiliaciones(personaDto, respuestaConsultaNieFiliacionesDto);

  }

  private void saveReferenciasFiliaciones(PersonaDto personaDto,
      RespuestaConsultaNieFiliacionDto respuestaConsultaNieFiliacionesDto) {
    if (respuestaConsultaNieFiliacionesDto.getDatosRespuesta() != null) {
      int numeroReferencias = 1;
      if (respuestaConsultaNieFiliacionesDto.getDatosRespuesta().getTitularRespuesta()
          .getReferenciasRespuesta() != null) {
        numeroReferencias = respuestaConsultaNieFiliacionesDto.getDatosRespuesta().getTitularRespuesta()
            .getReferenciasRespuesta().getString().size();
      }
      List<PerFiliacionesDto> perFiliacionesDtos = new ArrayList<>();
      if (numeroReferencias == 1) {
        cargarReferenciaToList(personaDto, perFiliacionesDtos,
            respuestaConsultaNieFiliacionesDto.getDatosRespuesta().getTitularRespuesta().getReferenciaRespuesta(), "",
            respuestaConsultaNieFiliacionesDto);
      } else if (numeroReferencias > 1) {
        for (String referencia : respuestaConsultaNieFiliacionesDto.getDatosRespuesta().getTitularRespuesta()
            .getReferenciasRespuesta().getString()) {
          cargarReferenciaToList(personaDto, perFiliacionesDtos, referencia, "VAL-DGP-CVR",
              respuestaConsultaNieFiliacionesDto);
        }
      }
      desactivarFiliacionesByIdPersona(personaDto.getIdPer());
      for (PerFiliacionesDto perFiliacionesDto : perFiliacionesDtos) {
        perFiliacionesDao.save(perFiliacionesWithPersonaMapper.toEntity(perFiliacionesDto));
      }
    } else {
      LdvMaestraDto ldvMaestraDto = catalogosService.getCatalogoByCod("VAL-DGP-SIN");
      desactivarFiliacionesByIdPersona(personaDto.getIdPer());
      PerFiliacionesDto perFiliacionesDto = new PerFiliacionesDto();
      perFiliacionesDto.setCodEstado(respuestaConsultaNieFiliacionesDto.getEstado().getCodigoEstado());
      perFiliacionesDto.setLiteralError(respuestaConsultaNieFiliacionesDto.getEstado().getLiteralError());
      perFiliacionesDto.setIdPeticion(respuestaConsultaNieFiliacionesDto.getCodigoPeticionRespuesta());
      perFiliacionesDto.setPersonaDto(personaDto);
      perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(ldvMaestraDto);
      perFiliacionesDao.save(perFiliacionesWithPersonaMapper.toEntity(perFiliacionesDto));

    }
  }

  private void cargarReferenciaToList(PersonaDto personaDto, List<PerFiliacionesDto> perFiliacionesDtos,
      String referencia, String codigoEstadoPolicia,
      RespuestaConsultaNieFiliacionDto respuestaConsultaNieFiliacionesDto) {

    PerFiliacionesDto perFiliacionesEntity = cargarRespuestaConsultaNieToFiliacionEntity(personaDto, referencia,
        codigoEstadoPolicia, respuestaConsultaNieFiliacionesDto);
    perFiliacionesDtos.add(perFiliacionesEntity);
  }

  private PerFiliacionesDto cargarRespuestaConsultaNieToFiliacionEntity(PersonaDto personaDto, String referencia,
      String codigoEstadoPolicia, RespuestaConsultaNieFiliacionDto respuestaConsultaNieFiliacionesDto) {
    ws_cons_fili_domic_nie.com.adexttra.TitularRespuesta titularRespuesta = respuestaConsultaNieFiliacionesDto
        .getDatosRespuesta().getTitularRespuesta();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    PerFiliacionesDto perFiliacionesDto = new PerFiliacionesDto();
    perFiliacionesDto.setPersonaDto(personaDto);
    perFiliacionesDto.setNombre(titularRespuesta.getNombre());
    perFiliacionesDto.setApellido1(titularRespuesta.getApellido1());
    perFiliacionesDto.setApellido2(titularRespuesta.getApellido2());
    perFiliacionesDto.setCodEstado(respuestaConsultaNieFiliacionesDto.getEstado().getCodigoEstado());
    perFiliacionesDto.setIdPeticion(respuestaConsultaNieFiliacionesDto.getCodigoPeticionRespuesta());
    perFiliacionesDto.setLiteralError(respuestaConsultaNieFiliacionesDto.getEstado().getLiteralError());
    if (!StringUtils.isEmpty(titularRespuesta.getFechaMod())) {
      try {
        Date date = formatter.parse(titularRespuesta.getFechaMod());
        perFiliacionesDto.setFechaModificacion(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_112);
      }
    } else {
      try {
        Date date = formatter.parse(referencia.substring(2, 9));
        perFiliacionesDto.setFechaModificacion(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_113);
      }
    }
    perFiliacionesDto.setReferencia(referencia);
    perFiliacionesDto.setPadre(titularRespuesta.getPadre());
    perFiliacionesDto.setMadre(titularRespuesta.getMadre());
    perFiliacionesDto.setNacionalidad(titularRespuesta.getNacionalidad());
    if (!StringUtils.isEmpty(titularRespuesta.getFechaNacimiento())) {
      try {
        Date date = formatter.parse(titularRespuesta.getFechaNacimiento());
        perFiliacionesDto.setFechaNacimiento(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_114);
      }
    }
    if (codigoEstadoPolicia.isEmpty()) {
      codigoEstadoPolicia = obtenerCodigoEstadoPolicia(personaDto, perFiliacionesDto);
    }
    perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(catalogosService.getCatalogoByCod(codigoEstadoPolicia));
    return perFiliacionesDto;
  }

  @Override
  public void peticionConsultaReferenciaFiliacion(String referencia, PersonaDto personaDto,
      ExpedienteDto expedienteDto) {
    ws_cons_fili_domic_ref.com.adexttra.Peticion peticion = new ws_cons_fili_domic_ref.com.adexttra.Peticion();
    ws_cons_fili_domic_ref.com.adexttra.Datos datos = new ws_cons_fili_domic_ref.com.adexttra.Datos();
    ws_cons_fili_domic_ref.com.adexttra.Titular titular = new ws_cons_fili_domic_ref.com.adexttra.Titular();
    titular.setReferencia(referencia);
    datos.setTitular(titular);
    datos.setEquipo(equipo);
    peticion.setDatos(datos);
    ws_cons_fili_domic_ref.com.adexttra.Solicitante solicitante = new ws_cons_fili_domic_ref.com.adexttra.Solicitante();
    solicitante.setCodigoPeticion(
        "nac" + new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(java.util.Calendar.getInstance().getTime()));
    solicitante.setIdentificacionPuesto(identificacionPuestoFiliaciones);
    ws_cons_fili_domic_ref.com.adexttra.Organismo organismo = new ws_cons_fili_domic_ref.com.adexttra.Organismo();
    organismo.setCodigoOrganismo(codOrganismoFiliaciones);
    organismo.setNombreOrganismo(nombreOrganismoFiliaciones);
    solicitante.setOrganismo(organismo);
    solicitante.setUsuario(usuarioFiliaciones);
    peticion.setSolicitante(solicitante);
    RespuestaConsultaReferenciaFiliacionDto respuestaConsultaReferenciaFiliacionesDto;
    try {
      respuestaConsultaReferenciaFiliacionesDto = filiacionesService.peticionConsultaReferenciaFiliacion(peticion);
    } catch (SinacFiliacionesException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_115);
    }
    List<PerFiliacionesDto> perFiliacionesDtos = getPerFiliacionesByIdPer(personaDto.getIdPer());
    for (PerFiliacionesDto perFiliacionesDto : perFiliacionesDtos) {
      if (perFiliacionesDto.getReferencia().equals(referencia)) {
        cargarRespuestaConsultaReferenciaToFiliacionDto(personaDto, referencia,
            respuestaConsultaReferenciaFiliacionesDto, perFiliacionesDto);
        perFiliacionesDao.save(perFiliacionesWithPersonaMapper.toEntity(perFiliacionesDto));
      } else {
        if (Boolean.TRUE.equals(perFiliacionesDto.getFlgReferenciaConsultada())) {
          perFiliacionesDto.setFlgReferenciaConsultada(false);
          perFiliacionesDao.save(perFiliacionesWithPersonaMapper.toEntity(perFiliacionesDto));
        }
      }
      try {
        if ("R".equals(expedienteDto.getProcedimientoDto().getCodCorto())) {
          LOG.info("Se actualiza la validación de Policía/Filiaciones del semáforo. (Consulta)");
          updateValidacionSemaforo(expedienteDto.getIdExp(), VAL_POL,
              perFiliacionesDto.getLdvMaestraEstadoFiliacionDto().getCodLdvMae());
        }
      } catch (SinacException e) {
        LOG.info("Se ha producido un error intentar actualizar la validación de Policía/Filiaciones del semaforo. "
            .concat(e.getMessage()));
      }
    }

  }

  private PerFiliacionesDto cargarRespuestaConsultaReferenciaToFiliacionDto(PersonaDto personaDto, String referencia,
      RespuestaConsultaReferenciaFiliacionDto respuestaConsultaReferenciaFiliacionesDto,
      PerFiliacionesDto perFiliacionesDto) {
    ws_cons_fili_domic_ref.com.adexttra.TitularRespuesta titularRespuesta = respuestaConsultaReferenciaFiliacionesDto
        .getDatosRespuesta().getTitularRespuesta();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    perFiliacionesDto.setPersonaDto(personaDto);
    perFiliacionesDto.setNombre(titularRespuesta.getNombre());
    perFiliacionesDto.setApellido1(titularRespuesta.getApellido1());
    perFiliacionesDto.setApellido2(titularRespuesta.getApellido2());
    perFiliacionesDto.setCodEstado(respuestaConsultaReferenciaFiliacionesDto.getEstado().getCodigoEstado());
    perFiliacionesDto.setIdPeticion(respuestaConsultaReferenciaFiliacionesDto.getCodigoPeticionRespuesta());
    perFiliacionesDto.setLiteralError(respuestaConsultaReferenciaFiliacionesDto.getEstado().getLiteralError());
    if (!StringUtils.isEmpty(titularRespuesta.getFechaMod())) {
      try {
        Date date = formatter.parse(titularRespuesta.getFechaMod());
        perFiliacionesDto.setFechaModificacion(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_116);
      }
    } else {
      try {
        Date date = formatter.parse(referencia.substring(2, 9));
        perFiliacionesDto.setFechaModificacion(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_117);
      }
    }
    perFiliacionesDto.setReferencia(referencia);
    perFiliacionesDto.setPadre(titularRespuesta.getPadre());
    perFiliacionesDto.setMadre(titularRespuesta.getMadre());
    perFiliacionesDto.setNacionalidad(titularRespuesta.getNacionalidad());
    if (!StringUtils.isEmpty(titularRespuesta.getFechaNacimiento())) {
      try {
        Date date = formatter.parse(titularRespuesta.getFechaNacimiento());
        perFiliacionesDto.setFechaNacimiento(date);
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_118);
      }
    }
    String codigoEstadoPolicia = obtenerCodigoEstadoPolicia(personaDto, perFiliacionesDto);
    perFiliacionesDto.setLdvMaestraEstadoFiliacionDto(catalogosService.getCatalogoByCod(codigoEstadoPolicia));
    perFiliacionesDto.setFlgReferenciaConsultada(true);
    return perFiliacionesDto;
  }

  private String obtenerCodigoEstadoPolicia(PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto) {
    String codigoEstadoPolicia = "VAL-DGP-CEI";
    personaDto = personaService.getPersonaByIdPer(personaDto.getIdPer());
    // Lista de pares de cadenas para comparar
    List<String[]> comparaciones = Arrays.asList(new String[] { personaDto.getNombre(), perFiliacionesDto.getNombre() },
        new String[] { personaDto.getApellido1(), perFiliacionesDto.getApellido1() },
        new String[] { personaDto.getApellido2(), perFiliacionesDto.getApellido2() },
        new String[] { personaDto.getNacionalidad().getNomPais(), perFiliacionesDto.getNacionalidad() });

    // Verificar condiciones de comparación
    boolean sonIguales = comparaciones.stream()
        .allMatch(pair -> Utilidades.compararStringsSinAcentosEspaciosMayusculas(pair[0], pair[1]))
        && Utilidades.compararFechas(personaDto.getFechaNacimiento(), perFiliacionesDto.getFechaNacimiento());

    if (sonIguales) {
      codigoEstadoPolicia = "VAL-DGP-COH";
    }

    return codigoEstadoPolicia;
  }

  @Override
  public void saveCopyDatosFiliacionEnPersona(PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto) {

    personaDto.setNombre(perFiliacionesDto.getNombre());
    personaDto.setApellido1(perFiliacionesDto.getApellido1());
    personaDto.setApellido2(perFiliacionesDto.getApellido2());
    if (!StringUtils.isEmpty(perFiliacionesDto.getFechaNacimientoString())) {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(perFiliacionesDto.getFechaNacimientoString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Para que en la revisión no se vaya al día anterior hay que meter 2 horas en
        // la fecha de nacimiento de la persona
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        personaDto.setFechaNacimiento(calendar.getTime());
      } catch (ParseException e) {
        throw new SinacException(e, SinacExceptionMessageType.SINAC_EXPEDIENTES_119);
      }
    }
    personaDto.setNacionalidad(paisesService.getPaisPorNomPaisMju(perFiliacionesDto.getNacionalidad()));
    personaDto.setProgenitor1(perFiliacionesDto.getPadre());
    personaDto.setProgenitor2(perFiliacionesDto.getMadre());
    personaService.saveSoloPersona(personaDto);
    PerFiliacionesEntity perFiliacionesEntity = perFiliacionesDao
        .getPerFiliacionesById(perFiliacionesDto.getIdPerFiliaciones());
    perFiliacionesEntity.setLdvMaestraEstadoFiliacionEntity(ldvMaestraDao.findByCodigo("VAL-DGP-COH"));
    perFiliacionesDao.save(perFiliacionesEntity);
  }

  @Override
  public void saveExpedienteValSemaforo(ExpedienteDto expedienteDto, PersonaDto interesadoDto) throws SinacException {
    try {
      List<ValidacionSemaforoEntity> listaValidaciones = validacionSemaforoDao.getListaValidacionesDefault();
      for (ValidacionSemaforoEntity validacionSemaforoEntity : listaValidaciones) {
        if (validacionSemaforoEntity != null) {
          ExpedientesValidacionesSemaforoEntity expedienteValSemaforoEntity = new ExpedientesValidacionesSemaforoEntity();

          if ("DID-NIE".equals(interesadoDto.getPersonasIdentificaDtos().get(0).getLdvMaestraDto().getCodLdvMae())) {
            if (validacionSemaforoEntity.getIdEstadoValLdv() == null
                || !"VAL-DGP-PEN".equals(validacionSemaforoEntity.getIdEstadoValLdv().getCodLdvMae())) {
              expedienteValSemaforoEntity.setExpedienteEntity(expedienteMapper.toEntity(expedienteDto));
              expedienteValSemaforoEntity.setValSemaforoEntity(validacionSemaforoEntity);
              expedientesValSemaforoDao.save(expedienteValSemaforoEntity);
            }
          } else {
            if (validacionSemaforoEntity.getIdEstadoValLdv() == null
                || !"VAL-DGP-SIN".equals(validacionSemaforoEntity.getIdEstadoValLdv().getCodLdvMae())) {
              expedienteValSemaforoEntity.setExpedienteEntity(expedienteMapper.toEntity(expedienteDto));
              expedienteValSemaforoEntity.setValSemaforoEntity(validacionSemaforoEntity);
              expedientesValSemaforoDao.save(expedienteValSemaforoEntity);
            }
          }

        }
      }
    } catch (SinacException ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_120)
          .logMessageParams(expedienteDto.getIdExp()).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void updateValidacionSemaforo(BigInteger idExp, String codLdvEntMae, String codValSem) throws SinacException {
    LOG.info("ExpedientesServiceImpl.updateValidacionSemaforo - Init");
    try {
      LOG.info("ExpedientesServiceImpl.updateValidacionSemaforo");
      int rows = expedientesValSemaforoDao.updateValidacionSemaforoByCodValSem(idExp, codLdvEntMae, codValSem);

      if (rows == 0) {
        LOG.warn("No se actualizo ninguna fila para idExp={}, codLdvEntMae={}, codValSem={}", idExp, codLdvEntMae,
            codValSem);
      } else {
        LOG.info("Filas actualizadas en updateValidacionSemaforo: {}", rows);
      }
    } catch (SinacException ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_121).type(SinacExceptionType.DATA);
    }
    LOG.info("ExpedientesServiceImpl.updateValidacionSemaforo - End");
  }

  @Override
  public void recalcularValidadionesSemaforo(BigInteger idExp, List<String> listaValidacionesInt,
      List<String> listaValidacionesCon) {
    recalcularValidadionesIntegracion(idExp, listaValidacionesInt);
    recalcularValidadionesConducta(idExp, listaValidacionesInt);
  }

  @Override
  public void recalcularValidadionesIntegracion(BigInteger idExp, List<String> listCodValLdvEntMae) {
    Map<String, String> validacionesEntMaeColor = new HashMap<>();
    for (ValidacionSemaforoDto validacion : getListaValidacionesByIdExp(idExp, listCodValLdvEntMae)) {
      validacionesEntMaeColor.put(validacion.getIdLdvEntMae().getCodLdvEntMae(),
          validacion.getIdColorLdv().getCodLdvMae());
    }

    if (EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_JES))
        || EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DCO))
        || (EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DELE))
            && EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_CCSE)))) {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CIN, EVAL_OK);
    } else if (EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_JES))
        && EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DCO))
        && EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DELE))
        && EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_CCSE))) {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CIN, EVAL_KO);
    } else {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CIN, EVAL_PEN);
    }
  }

  @Override
  public void recalcularValidadionesConducta(BigInteger idExp, List<String> listCodValLdvEntMae) {
    Map<String, String> validacionesEntMaeColor = new HashMap<>();
    for (ValidacionSemaforoDto validacion : getListaValidacionesByIdExp(idExp, listCodValLdvEntMae)) {
      validacionesEntMaeColor.put(validacion.getIdLdvEntMae().getCodLdvEntMae(),
          validacion.getIdColorLdv().getCodLdvMae());
    }

    if (EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DGP))
        && EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_MJU))
        && EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_CNI))
        && EVAL_OK.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_MDE))) {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CCI, EVAL_OK);
    } else if (EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_DGP))
        || EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_MJU))
        || EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_CNI))
        || EVAL_KO.equals(validacionesEntMaeColor.get(COD_ENT_MAE_VAL_MDE))) {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CCI, EVAL_KO);
    } else {
      expedientesValSemaforoDao.updateValidacionSemaforoByCodColorLdv(idExp, COD_ENT_MAE_VAL_CCI, EVAL_PEN);
    }
  }

  @Override
  public List<ValidacionSemaforoDto> getListaValidacionesByIdExp(BigInteger idExp, List<String> listCodValLdvEntMae) {
    StringBuilder sqlWhereList = new StringBuilder();
    if (listCodValLdvEntMae != null && !listCodValLdvEntMae.isEmpty()) {
      sqlWhereList.append("AND lvdEntMae.codLdvEntMae IN (");
      for (int i = 0; i < listCodValLdvEntMae.size(); i++) {
        sqlWhereList.append("'").append(listCodValLdvEntMae.get(i)).append("'");
        if (i < listCodValLdvEntMae.size() - 1) {
          sqlWhereList.append(", ");
        }
      }
      sqlWhereList.append(")");
    }

    Query query = entityManager.createQuery("Select val FROM ValidacionSemaforoEntity val "
        + " INNER JOIN val.expedientesValSemaforoEntities expVal INNER JOIN expVal.expedienteEntity exp "
        + " LEFT JOIN FETCH val.idEstadoValLdv estLdv " + " LEFT JOIN FETCH val.idColorLdv colorLdv "
        + " LEFT JOIN FETCH val.idLdvEntMae lvdEntMae WHERE exp.idExp =:idExp " + sqlWhereList
        + " ORDER BY val.ordenVal ASC", ValidacionSemaforoEntity.class);
    query.setParameter("idExp", idExp);

    List<ValidacionSemaforoEntity> listaValidaciones = new ArrayList<>();
    listaValidaciones.addAll(query.getResultList());

    return validacionSemaforoMapper.toDtoList(listaValidaciones);
  }

  @Override
  public List<ExpedienteDto> listaExpedientesPorEstado(List<String> listaEstados) {
    return expedienteMapper.toDtos(expedienteDao.getListaExpedientesPorEstado(listaEstados));
  }

  @Override
  public List<ExpedienteDto> listaExpedientesDocPendienteValidar(List<String> listaEstadosIn,
      List<String> listaEstadosNotIn) {
    return expedienteMapper
        .toDtos(expedienteDao.getListaExpedientesDocPendienteValidar(listaEstadosIn, listaEstadosNotIn));
  }

  @Override
  public List<ExpedienteDto> listaExpedientesDgpRechazo(List<String> listaEstadosInforme) {
    return expedienteMapper.toDtos(expedienteDao.getListaExpedientesInformeDgpRechazado(listaEstadosInforme));
  }

  @Override
  public List<ExpedienteDto> getListaExpDocumentosNotificar(List<String> listaEstadoDoc, List<String> listaParam,
      short idPro, Date fechaFirma) {
    return expedienteMapper
        .toDtos(expedienteDao.getListaExpedientesDocumentosNotificar(listaEstadoDoc, listaParam, idPro, fechaFirma));
  }

  @Override
  public List<ParametrizacionDto> getParametrizacionByNombre(String string) {
    return parametrizacionMapper.toDto(parametrizacionDao.getParametrizacionByNombre(string));
  }

  @Override
  public ParametrizacionDto getParametrizacionByNombreAndProcedimiento(String nomParam, String codPro) {
    return parametrizacionMapper.toDto(parametrizacionDao.getParametrizacionByNombreAndProcedimiento(nomParam, codPro));
  }

  @Override
  public List<ExpedienteInformeDto> getListaExpedientesInformesByCodEstInforme(String codEstInforme)
      throws SinacException {
    try {
      LOG.info("ExpedientesServiceImpl.getListaExpedientesInformesByCodEstInforme - Init");

      List<ExpedienteInformeEntity> listExpInfo = expedienteInformeDao
          .getListaExpedientesInformesByCodEstInforme(codEstInforme);

      listExpInfo.removeIf(ei -> (ei.getExpedienteInformeDgpEntity() == null
          && (ei.getLdvMaestraEntityByIdSentidoInfLdv() == null
              || ei.getLdvMaestraEntityByIdSentidoInfLdv().getNomLdvMae() == null)
          && (ei.getExpedienteInformesMjuFicherosDatosEntity() == null
              || ei.getExpedienteInformesMjuFicherosDatosEntity().isEmpty())));

      for (ExpedienteInformeEntity ei : listExpInfo) {
        ei.getExpedienteInformesMjuFicherosDatosEntity().removeIf(mju -> !mju.isFlgActivo());
      }
      return expedienteInformeMapperAux.toDto(listExpInfo);
    } catch (SinacException ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_EXPEDIENTES_122).type(SinacExceptionType.DATA);
    }

  }

  @Override
  public List<ExpedienteDto> getListaExpedienteByFase(String codFase) {
    return expedienteMapper.toDtos(expedienteDao.getListaExpedientesByFase(codFase));
  }

  @Override
  public List<ExpedienteDocumentoDto> getListaExpedienteDocumentosCodGDNull() {
    List<ExpedienteDocumentoEntity> expDocumentosEntities = expedienteDocumentoDao.findExpedienteDocumentoByCodGdNull();
    return expedienteDocumentoWithExpedienteMapper.toDtos(expDocumentosEntities);
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesCodGDNull() {
    return expedienteMapper.toDtos(expedienteDao.getListaExpedientesCodGDNull());
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesIncompletos(List<String> estados) {
    return expedienteMapper.toDtos(expedienteDao.getListaExpedientesPorEstado(estados));
  }

  @Override
  public void reintentoDocumentosExpediente(List<ExpedienteDocumentoDto> listaDocsExp, SolicitudDto solicitudDto,
      ExpedienteDto expedienteDto) {

    for (SolicitudDocumentoDto docSol : solicitudDto.getSolicitudDocumentoDtos()) {
//      boolean copiado = nfsManager.copiarDocNfsVeaASinac(docSol.getNfsRuta(), docSol.getNomDoc());
      boolean existe = listaDocsExp.stream().anyMatch(docExp -> docExp.getNomDoc().equals(docSol.getNomDoc()));
      if (!existe) {

        // Leemos el stream UNA sola vez y lo cerramos con try-with-resources para
        // evitar que el fichero
        // quede bloqueado en Windows y provoque FileSystemException al intentar
        // borrarlo.
        try {

          // TODO JULIAN : METODO GLOBAL EXPEDIENTE DOCUMENTO
          LOG.info("Se intenta recuperar el documento {} en la ruta {}", docSol.getNomDoc(),
              docSol.getNfsRuta().concat(solicitudDto.getIdSolVea().toString()).concat("/"));
          DataSource dataSource = nfsManager.getDataSource(docSol.getNomDoc(),
              docSol.getNfsRuta().concat(solicitudDto.getIdSolVea().toString()).concat("/"));
          byte[] contenido = null;
          if (dataSource != null) {
            try (InputStream is = dataSource.getInputStream()) {
              contenido = is.readAllBytes();
            }

          } else if (contenido == null) {
            nfsManager.copiarDocNfsVeaASinac(docSol.getNfsRutaVea(), docSol.getNomDoc());
          }

          ExpedienteDocumentoDto nuevoDoc = documentosService.generarExpedienteDocumento(docSol, contenido);

          // Rellenamos el DTO del documento con los datos leídos

          try {
            // TODO JULIAN: METODO GLOBAL DOCUMENTOTOSAVE
            // Preparamos el DTO para copiar el documento a NFS reutilizando el contenido
            // leído
            DocumentoToSaveDto documentoToSaveDto = documentosService.generarDocumentosExpediente(docSol, contenido);

            // Operaciones de servicio: copiar y persistir. Se lanzan en bloque try/catch
            // independiente
            documentosService.copyDocumentoNFS(expedienteDto.getCodExp(),
                expedienteDto.getProcedimientoDto().getCodPro(), expedienteDto.getFechaEfectos(), documentoToSaveDto);

            nuevoDoc = documentosService.saveExpedienteDocumento(nuevoDoc, expedienteDto);
          } catch (Exception e) {
            // Intentamos borrar el documento en NFS si hubo error al guardar la entidad.
            // Como el InputStream ya se cerró, no debería fallar por bloqueo del fichero.
            try {
              documentosService.deleteDocumentoNFS(nuevoDoc);
            } catch (Exception ex) {
              LOG.error("Error al borrar el documento NFS tras fallo en el guardado", ex);
            }
            LOG.error("Se ha producido un error al guardar el documento", e);
          }
        } catch (Exception e) {
          // Capturamos errores al leer desde NFS o preparar el DTO; evitamos dejar
          // streams abiertos.
          LOG.error("Se ha producido un error al leer el documento desde NFS o al preparar el guardado", e);
        }
      }
    }
  }

  @Override
  public List<PersonaDto> getExpedienteAcumular(String numAcreditacion, List<String> listaEstados, String codPro) {
    return personaMapperAux.toDtos(expedienteDao.getExpedienteAcumular(numAcreditacion, listaEstados, codPro));
  }

  @Override
  public ExpedienteDto guardarEntidadesExpedientes(Map<String, Object> valores, BigInteger idExpOri,
      SolicitudDto solicitudDto, PersonaDto interesadoDto, List solicitudesPersonasDtoList, String idenExpGD,
      String codExp) {
    ExpedienteDto expedienteDto = saveExpediente(solicitudDto, idenExpGD, codExp);

    @SuppressWarnings("unchecked")
    List<ExpedienteDto> expedientesRelacionados = (List<ExpedienteDto>) valores.get("expedientesRelacionados");
    expedientesFacade.saveExpedientesRelacionadosAutomaticamente(expedienteDto, expedientesRelacionados, idExpOri);

    saveCamposFormularioExpediente(expedienteDto, solicitudDto);

    saveExpedientePersonas(solicitudesPersonasDtoList, expedienteDto);

    LOG.info("Se guardan datos en la tabla EXP_M_VAL_SEMAFORO al expediente con código {}", codExp);

    if ("R".equals(solicitudDto.getProcedimientoDto().getCodCorto()))
      saveExpedienteValSemaforo(expedienteDto, interesadoDto);

    solicitudDto.setLdvMaestraDtoByIdEstSolLdv(catalogosService.getCatalogoByCod("SOL-ENV"));
    solicitudesService.saveSolicitud(solicitudDto);
    return expedienteDto;
  }

  @Override
  public void comprobarSolicitudPenCompletada(BigInteger idExpInf) {
    ExpedienteInformeEntity expInfEntity = expedienteInformeDao.getExpedienteInformesByIdExpInf(idExpInf);
    String idSolicitud = expInfEntity.getIdSolicitudInforme();
    if (!expedienteInformeDao.existsExpInfByIdSolicitudLikeAndEstados(idSolicitud)) {
      List<ExpedienteInformesMjuFicherosEntity> expInfMjuEntities = expedienteInformesMjuFicherosDao
          .getExpedienteInformesMjuFicheroByNomFichero(idSolicitud);
      if (!expInfMjuEntities.isEmpty()) {
        LdvMaestraEntity einfRec = ldvMaestraDao.findByCodigo("EINF-REC");
        expInfMjuEntities.forEach(archivo -> archivo.setIdEstFichLdv(einfRec));
        LOG.info("Actualizando el estado del fichero de solicitud {} a estado EINF-REC", idSolicitud);
        expedienteInformesMjuFicherosDao.saveAll(expInfMjuEntities);
      }
    }
  }

  @Override
  public void actualizarExpedienteInformesMjuFicherosDatos(String nombreArchivo) {
    List<ExpedienteInformeEntity> listaExpedienteInforme = expedienteInformeDao
        .getExpedienteInformesByIdSolicitudInforme(nombreArchivo);
    for (ExpedienteInformeEntity expedienteInformeEntity : listaExpedienteInforme) {
      ExpedienteInformesMjuFicherosEntity expedienteInformesFicheros = expedienteInformesMjuFicherosDao
          .getExpedienteInformesMjuFicheroByNomFichero(nombreArchivo).get(0);
      ExpedienteInformesMjuFicherosDatosEntity expedienteInformesMjuFicherosDatosEntity = new ExpedienteInformesMjuFicherosDatosEntity();
      expedienteInformesMjuFicherosDatosEntity.setExpedienteInforme(expedienteInformeEntity);
      expedienteInformesMjuFicherosDatosEntity
          .setExpedienteInformeMjuFicheroSolicitudEntity(expedienteInformesFicheros);
      expedienteInformesMjuFicherosDatosDao.save(expedienteInformesMjuFicherosDatosEntity);
    }
  }

  @Override
  public List<ExpedienteDto> getExpedienteAcumularPorIdPer(BigInteger idPer, String codPro, List<String> listaEstados) {
    return expedienteMapper
        .toDtos(expedienteDao.getExpedientesByIdPerInteresadoCodCortoProAcumular(idPer, codPro, listaEstados));
  }

  public Integer calcularTiempoResidenciaExigido(Integer codMotivoSolicitud, Integer nacionalidad,
      Integer segundaNacionalidad, Integer paisNacimiento) {

    LOG.info("Iniciando cálculo de tiempo de residencia exigido. "
        + "Entrada -> aniosMotivoSolicitud: {}, aniosNacionalidad: {}, aniosSegundaNacionalidad: {}, aniosPaisNacimiento: {}",
        codMotivoSolicitud, nacionalidad, segundaNacionalidad, paisNacimiento);

    Integer aniosPorNacionalidadYPais = obtenerAniosPorNacionalidadYPais(nacionalidad, segundaNacionalidad,
        paisNacimiento);

    LOG.info("Resultado de obtenerAniosPorNacionalidadYPais: {}", aniosPorNacionalidadYPais);

    if (codMotivoSolicitud == null && aniosPorNacionalidadYPais == null) {
      LOG.info("NO se ha encontrado codMotivoSolicitud - {} o aniosPorNacionalidadYpais - {}.", codMotivoSolicitud,
          aniosPorNacionalidadYPais);
      return null;
    } else if (aniosPorNacionalidadYPais == null) {
      LOG.info("No existe aniosPorNacionalidadYPais. Se devuelve codMotivoSolicitud - {}. - End", codMotivoSolicitud);
      return codMotivoSolicitud;
    } else {

      LOG.info("Ambos valores existen. Se devolverá mejor valor de ambos - {}. - End",
          Math.min(codMotivoSolicitud, aniosPorNacionalidadYPais));
      return Math.min(codMotivoSolicitud, aniosPorNacionalidadYPais);
    }
  }

  private Integer obtenerAniosPorNacionalidadYPais(Integer nac1, Integer nac2, Integer paisNacimiento) {

    return Stream.of(nac1, nac2, paisNacimiento).filter(Objects::nonNull).min(Integer::compareTo).orElse(null);
  }

  private void actualizarPeriodo(ExpedienteDto detalleExpedienteDto, String segmentoActualizar) {

    LOG.info("ExpedientesServiceImpl.actualizarPeriodo - Init");
    ExpedienteDto expedienteBD = getExpedientebyId(detalleExpedienteDto.getIdExp());

    PersonaDto interesadoDetalle = detalleExpedienteDto.getInteresado();
    PersonaDto interesadoBD = expedienteBD.getInteresado();

    LOG.info("Obtener el periodo exigido de residencia para el exp {}", detalleExpedienteDto.getCodExp());

    boolean cambioPaisNacimiento = cambioPaisNacimiento(interesadoBD, interesadoDetalle);
    boolean cambioNacionalidad = cambioNacionalidad(interesadoBD, interesadoDetalle);
    boolean cambioSegundaNacionalidad = cambioSegundaNacionalidad(interesadoBD, interesadoDetalle);
    boolean cambioMotivoSolicitud = cambioMotivoSolicitud(expedienteBD, detalleExpedienteDto);
    if (segmentoActualizar.equals("interesado")) {
      if (!cambioPaisNacimiento && !cambioNacionalidad && !cambioSegundaNacionalidad) {

        LOG.info(
            "No hay cambios relevantes en los datos del interesado en  el expediente {}, no se recalcula el periodo. - End",
            detalleExpedienteDto.getCodExp());
        return;
      }

    } else {
      if (!cambioMotivoSolicitud) {
        LOG.info("No hay cambios en el motivo de la solicitud en el expediente {}, no se recalcula el periodo. - End",
            detalleExpedienteDto.getCodExp());
        return;

      }
    }

    try {

      Integer tiempoResidenciaExigido = calcularPeriodoDetalle(detalleExpedienteDto, expedienteBD, segmentoActualizar);

      LOG.info("Tiempo de residencia exigido recalculado: {} para el expediente: {}", tiempoResidenciaExigido,
          detalleExpedienteDto.getCodExp());

      actualizarPeriodoEnBD(detalleExpedienteDto.getIdExp(), tiempoResidenciaExigido);
      LOG.info("ExpedientesServiceImpl.actualizarPeriodo - End");
    } catch (Exception e) {
      LOG.error("Error al calcular el periodo de residencia exigido para el expediente {}",
          detalleExpedienteDto.getCodExp(), e);
    }

  }

  private boolean cambioPaisNacimiento(PersonaDto bd, PersonaDto detalle) {
    if (bd.getPaisNacimiento() == null || detalle.getPaisNacimiento() == null) {
      return false;
    }
    return !bd.getPaisNacimiento().getIdPais().equals(detalle.getPaisNacimiento().getIdPais());
  }

  private boolean cambioNacionalidad(PersonaDto bd, PersonaDto detalle) {
    if (bd.getNacionalidad() == null || detalle.getNacionalidad() == null) {
      return false;
    }
    return !bd.getNacionalidad().getIdPais().equals(detalle.getNacionalidad().getIdPais());
  }

  private boolean cambioSegundaNacionalidad(PersonaDto bd, PersonaDto detalle) {
    if (bd.getSegundaNacionalidad() == null || detalle.getSegundaNacionalidad() == null) {
      return false;
    }
    return !bd.getSegundaNacionalidad().getIdPais().equals(detalle.getSegundaNacionalidad().getIdPais());
  }

  private boolean cambioMotivoSolicitud(ExpedienteDto bd, ExpedienteDto detalle) {
    if (bd.getMotivoSolicitud() == null || detalle.getMotivoSolicitud() == null) {
      return false;
    }
    return !bd.getMotivoSolicitud().getIdLdvMae().equals(detalle.getMotivoSolicitud().getIdLdvMae());
  }

  private Integer calcularPeriodoDetalle(ExpedienteDto detalleExpedienteDto, ExpedienteDto expedienteBD,
      String segmentoActualizar) {
    LOG.info("ExpedientesServiceImpl.calcularPeriodoDetalle - Init");
    PersonaDto interesado = detalleExpedienteDto.getInteresado();
    PersonaDto interesadoBD = expedienteBD.getInteresado();

    Integer aniosMotivoSolicitud = null;
    Integer aniosNacionalidad = 10;
    Integer aniosNacionalidad2 = 10;
    Integer periodoPaisNacimiento = 10;

    // CAMBIO EN LOS DATOS PERSONA
    if (segmentoActualizar.equals("interesado")) {
      LOG.info("Modificados datos de la persona.");
      if (interesado.getNacionalidad() != null) {
        LOG.info("Encontrada modificación en la Nacionalidad del interesado.");
        PaisesEntity paisNac = paisesDao.getPaisPorIdPais(interesado.getNacionalidad().getIdPais());
        aniosNacionalidad = paisNac.getPeriodoExigido();
      }

      if (interesado.getSegundaNacionalidad() != null) {
        LOG.info("Encontrada modificación en la Segunda Nacionalidad del interesado.");
        PaisesEntity paisNac2 = paisesDao.getPaisPorIdPais(interesado.getSegundaNacionalidad().getIdPais());
        aniosNacionalidad2 = paisNac2.getPeriodoExigido();
      }

      if (interesado.getPaisNacimiento() != null) {
        LOG.info("Encontrada modificación en el pais de nacimiento del interesado.");
        PaisesEntity paisNacimientoEntity = paisesDao.getPaisPorIdPais(interesado.getPaisNacimiento().getIdPais());
        periodoPaisNacimiento = paisNacimientoEntity.getPeriodoExigido();
      }

      String codMotivoSolicitud = expedienteBD.getMotivoSolicitud().getCodLdvMae();
      aniosMotivoSolicitud = motivoSolPeriodoExigDao.findPeriodoExigidoByCodLdvMae(codMotivoSolicitud);

      // CAMBIO EN LOS DATOS EXPEDIENTE
    } else {
      LOG.info("Modificados datos del motivo de la solicitud.");
      aniosNacionalidad = interesadoBD.getNacionalidad().getPeriodoExigido();
      if (interesadoBD.getSegundaNacionalidad() != null) {
        aniosNacionalidad2 = interesadoBD.getSegundaNacionalidad().getPeriodoExigido();
      }

      periodoPaisNacimiento = interesadoBD.getPaisNacimiento().getPeriodoExigido();

      LdvMaestraEntity ldv = ldvMaestraDao.findByIdLdvMae(detalleExpedienteDto.getMotivoSolicitud().getIdLdvMae());
      String codMotivoSolicitud = ldv.getCodLdvMae();
      aniosMotivoSolicitud = motivoSolPeriodoExigDao.findPeriodoExigidoByCodLdvMae(codMotivoSolicitud);
    }
    LOG.info("ExpedientesServiceImpl.calcularPeriodoDetalle - End");
    return calcularTiempoResidenciaExigido(aniosMotivoSolicitud, aniosNacionalidad, aniosNacionalidad2,
        periodoPaisNacimiento);
  }

  private void actualizarPeriodoEnBD(BigInteger idExpediente, Integer tiempoResidenciaExigido) {
    LOG.info("ExpedientesServiceImpl.actualizarPeriodoEnBD - Init");
    List<ExpedienteInformeEntity> expedientesInformeEntity = expedienteInformeDao
        .getExpedienteInformesByIdExpediente(idExpediente);

    if (expedientesInformeEntity == null || expedientesInformeEntity.isEmpty()) {
      LOG.warn("No se ha encontrado expedientesInformeEntity");
      return;
    }

    for (ExpedienteInformeEntity expInf : expedientesInformeEntity) {
      if (expInf.getExpedienteInformeDgpEntity() != null) {
        expInf.getExpedienteInformeDgpEntity().setPeriodoExigido(tiempoResidenciaExigido);
        expedienteInformeDgpDao.save(expInf.getExpedienteInformeDgpEntity());
      }
    }
    LOG.info("ExpedientesServiceImpl.actualizarPeriodoEnBD - End");
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesResolver(String codPro, List<String> listaEstadosExp) {

    List<ExpedienteEntity> listaExpedientes = expedienteDao.getExpedientesResolverAuto(listaEstadosExp, codPro);

    return expedienteMapper.toDtos(listaExpedientes);
  }

  @Override
  public ExpedienteDto getExpedientesByIdPerInteresado(BigInteger idPer, List<String> listaEstados) {
    ExpedienteEntity listaExpedientes = expedienteDao.getExpedientesByIdPerInteresado(idPer, listaEstados);

    return expedienteMapper.toDto(listaExpedientes);
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesPropuesta(List<String> listaEstados, String valor,
      List<String> listaNombresNotIn) {
    return expedienteMapper
        .toDtos(expedienteDao.getExpedientesGenerarPropuesta(listaEstados, valor, listaNombresNotIn));
  }

  @Override
  public List<ExpedienteDto> getListaExpedientesConsultaTitulaciones(Date fechaComunicacion, String codPro,
      List<String> listaEstadosNotIn, List<String> listaCodigosEstados) {
    return expedienteMapper.toDtos(expedienteDao.getExpedientesConsultaTitulaciones(fechaComunicacion, codPro,
        listaEstadosNotIn, listaCodigosEstados));
  }

  @Override
  public List<PerCertificacionesDto> getPerCertificacionesByIdPerCodigosEstados(BigInteger idPer,
      List<String> listaCodigosEstados) {
    List<PerCertificacionesDto> perCertificacionesDtos = new ArrayList<>();
    List<PerCertificacionesEntity> perCertificacionesEntities = perCertificacionesDao
        .getPerCertificacionesByIdPerCodigoEstado(idPer, listaCodigosEstados);
    if (perCertificacionesEntities != null) {
      for (PerCertificacionesEntity perCertificacionesEntity : perCertificacionesEntities) {
        perCertificacionesDtos.add(perCertificacionesMapper.toDto(perCertificacionesEntity));
      }
    }

    return perCertificacionesDtos;
  }

  @Override
  public List<ProcedimientosPlantillasCriteriosDto> getListaProcedimientosPlantillasCriteriosByIdPro(Short idPro) {

    return procedimientosPlantillasCriteriosMapper
        .toDtos(procedimientoDao.getProcedimientosPlantillasCriteriosByIdPro(idPro));
  }
}
