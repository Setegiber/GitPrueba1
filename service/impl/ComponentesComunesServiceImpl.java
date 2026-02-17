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

import java.math.BigInteger;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.service.AccionesService;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.ComponentesComunesService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.FormularioCamposValidaService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.business.service.RequerimientosAndAudienciasService;
import es.mjusticia.sinac.core.model.dto.AccionDto;
import es.mjusticia.sinac.core.model.dto.AdjuntarDocumentoComboDto;
import es.mjusticia.sinac.core.model.dto.ComboDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteEstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.FaseDto;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.dto.InicioDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.TramiteDto;
import es.mjusticia.sinac.core.model.ldv.TipoDatoMaestroEnum;
import es.mjusticia.sinac.core.security.SinacSessionService;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.MensajeUtils;

@Service("componentesComunesService")
@Transactional(readOnly = true)
public class ComponentesComunesServiceImpl implements ComponentesComunesService {

  @Autowired
  private ProcedimientosService procedimientosService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private DocumentosService documentosService;

  @Autowired
  private AccionesService accionesService;

  @Autowired
  private FormularioCamposValidaService formularioCamposValidaService;

  @Autowired
  private RequerimientosAndAudienciasService requerimientosAndAudienciasService;

  @Autowired
  protected MensajeUtils mensajeUtils;

  @Autowired
  private SinacSessionService sinacSession;

  @Autowired
  private ComponentesComunesService componentesComunesService;

  private static final String LDV_TIPO_COMUNICACION_EXTERNA = "TCOM_EXT";
  private static final String LDV_MOTIVO_COMUNICACION_EXTERNA = "COM_EXT";
  private static final String LDV_RESULTADO_ACUERDO_RESOLUCION = "SEN_ACM";
  private static final String LDV_ORG_DESTINO = "ORG_DEST";
  private static final String ROL = "rol";
  private static final String COD_SITUACIONES = "SIT_";
  private static final String ID_PRO = "idPro";
  private static final String INICIO = "inicio";
  private static final String PROCEDIMIENTO_ROL = "procedimientoRol";
  private static final String NOMBRE_USUARIO = "nombreUsuario";
  private static final String LDV_SENTIDO_INFORME_CNI = "SINF_CNI";

  protected ComponentesComunesServiceImpl() {
    super();
  }

  @Override
  public int calcularEdadEnFechaRegistro(Date fechaNacimiento, Date fechaRegistro) {
    if (fechaNacimiento == null || fechaRegistro == null) {
      return 0;
    }
    Calendar nacimiento = Calendar.getInstance();
    nacimiento.setTime(fechaNacimiento);
    Calendar registro = Calendar.getInstance();
    registro.setTime(fechaRegistro);

    int edad = registro.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

    if (registro.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
      edad--;
    }

    return edad;
  }

  @Override
  public int calcularEdadActual(Date fechaNacimiento) {
    if (fechaNacimiento == null) {
      return 0;
    }
    Calendar nacimiento = Calendar.getInstance();
    nacimiento.setTime(fechaNacimiento);
    Calendar hoy = Calendar.getInstance();

    int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

    if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
      edad--;
    }

    return edad;
  }

  @Override
  public boolean informeDisponible(List<ExpedienteInformeDto> listaExpedienteInformeDto) {
    List<String> listaEstadosInformes = Arrays.asList("EINF-SSO", "EINF-RCH");
    return listaExpedienteInformeDto.stream()
        .anyMatch(informe -> listaEstadosInformes.contains(informe.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae()));
  }

  @Override
  public boolean informeDisponibleEstado(List<ExpedienteEstadoDto> listaExpedientesEstadosDtos) {
    List<String> listaEstados = Arrays.asList("INFP", "DGPR", "MJUR");
    return listaExpedientesEstadosDtos.stream()
        .anyMatch(estado -> listaEstados.contains(estado.getEstado().getEstadoFin().getCodEstado()));
  }

  @Override
  @Cacheable(value = "comboPrefijos", sync = true)
  public List<ComboDto> getComboPrefijos() throws SinacException {
    List<ComboDto> comboPrefijos = expedientesService.getPaisesPrefijo().stream()
        .map(p -> new ComboDto(p.getIdPais(), p.getPrefijoTelefono())).collect(Collectors.toList());
    comboPrefijos.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboPrefijos;
  }

  @Override
  public LocalidadesDto getLocalidadById(Short idLocalidad) throws SinacException {
    List<LocalidadesDto> resultado = componentesComunesService.getListaLocalidades().stream()
        .filter(loc -> loc.getIdMunicipio().equals(idLocalidad)).toList();
    if (!resultado.isEmpty()) {
      return resultado.get(0);
    }
    return null;
  }

  @Override
  public ProvinciasDto getProvinciaById(Short idProvincia) throws SinacException {
    List<ProvinciasDto> resultado = componentesComunesService.getListaProvinicias().stream()
        .filter(pro -> pro.getIdProvincia().equals(idProvincia)).toList();
    if (!resultado.isEmpty()) {
      return resultado.get(0);
    }
    return null;
  }

  @Override
  @Cacheable(value = "comboProcedimientos", sync = true)
  public List<ComboDto> getComboProcedimientos() throws SinacException {
    List<ComboDto> comboProcedimientos = procedimientosService.getProcedimientos().stream()
        .map(p -> new ComboDto(p.getIdPro(), p.getNomPro())).collect(Collectors.toList());
    comboProcedimientos.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboProcedimientos;
  }

  @Override
  @Cacheable(value = "comboLdvMaestraTiposIdentificacion", sync = true)
  public List<LdvMaestraDto> getListaComboLdvMaestraTiposIdentificacionDto() throws SinacException {
    List<LdvMaestraDto> listaComboLdvMaestraTiposIdentificacionDto = new ArrayList<>();
    listaComboLdvMaestraTiposIdentificacionDto.addAll(expedientesService.getTiposIdentificacionDetalleExp());
    // Mover el elemento con codLdvMae igual a DID-NIE al inicio
    LdvMaestraDto didNIEDto = null;
    for (LdvMaestraDto dto : listaComboLdvMaestraTiposIdentificacionDto) {
      if ("DID-NIE".equals(dto.getCodLdvMae())) {
        didNIEDto = dto; // Encontramos el elemento
        break;
      }
    }
    // Si encontramos el elemento, lo movemos al inicio
    if (didNIEDto != null) {
      listaComboLdvMaestraTiposIdentificacionDto.remove(didNIEDto);
      listaComboLdvMaestraTiposIdentificacionDto.add(0, didNIEDto);
    }
    return listaComboLdvMaestraTiposIdentificacionDto;
  }

  @Override
  @Cacheable(value = "comboPaises", sync = true)
  public List<ComboDto> getComboPaises() throws SinacException {
    return expedientesService.getPaises().stream().map(p -> new ComboDto(p.getIdPais(), p.getNomPais())).toList();
  }

  @Override
  @Cacheable(value = "comboNacionalides", sync = true)
  public List<ComboDto> getComboNacionalides() throws SinacException {
    return expedientesService.getPaises().stream()
        .filter(p -> p.getNacionalidad() != null && !p.getCodPais().equals("724"))
        .map(p -> new ComboDto(p.getIdPais(), p.getNacionalidad())).toList();
  }

  @Override
  @Cacheable(value = "comboProvincias", sync = true)
  public List<ComboDto> getComboProvincias() throws SinacException {
    return componentesComunesService.getListaProvinicias().stream()
        .map(p -> new ComboDto(p.getIdProvincia(), p.getNomProvincia())).toList();
  }

  @Override
  @Cacheable(value = "listaProvinicias", sync = true)
  public List<ProvinciasDto> getListaProvinicias() throws SinacException {
    return expedientesService.getProvincias();
  }

  @Override
  @Cacheable(value = "comboLocalidades", sync = true)
  public List<ComboDto> getComboLocalidades() throws SinacException {
    return expedientesService.getLocalidades().stream().map(l -> new ComboDto(l.getIdMunicipio(), l.getNomMunicipio()))
        .toList();
  }

  @Override
  @Cacheable(value = "listaLocalidades", sync = true)
  public List<LocalidadesDto> getListaLocalidades() throws SinacException {
    return expedientesService.getLocalidades();
  }

  @Override
  public List<ComboDto> getComboLocalidadesPorProvincia(String codProvincia) throws SinacException {
    return componentesComunesService.getListaLocalidades().stream()
        .filter(entry -> entry.getCodProvincia().toUpperCase().contains(codProvincia))
        .map(l -> new ComboDto(l.getIdMunicipio(), l.getNomMunicipio())).toList();
  }

  @Override
  public Short getIdProvinciaByIdLocalidad(Short idLocalidad) throws SinacException {
    LocalidadesDto localidadDto = getLocalidadById(idLocalidad);
    if (localidadDto.getCodMunicipio() != null && !localidadDto.getCodMunicipio().isBlank()) {
      ProvinciasDto provinciaEncontrada = componentesComunesService.getListaProvinicias().stream()
          .filter(provincia -> provincia.getCodProvincia().equals(localidadDto.getCodProvincia())).findFirst()
          .orElse(null);
      if (provinciaEncontrada != null) {
        return provinciaEncontrada.getIdProvincia();
      }
    }
    return null;
  }

  @Override
  @Cacheable(value = "comboSexo", sync = true)
  public List<ComboDto> getComboSexo() throws SinacException {
    List<ComboDto> comboSexo = expedientesService.getSexoDetalleExp().stream()
        .map(s -> new ComboDto(s.getIdLdvMae(), s.getNomLdvMae())).collect(Collectors.toList());
    comboSexo.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboSexo;
  }

  @Override
  @Cacheable(value = "comboEstadoCivil", sync = true)
  public List<ComboDto> getComboEstadoCivil() throws SinacException {
    List<ComboDto> comboEstadoCivil = expedientesService.getEstCivilDetalleExp().stream()
        .map(c -> new ComboDto(c.getIdLdvMae(), c.getDesLdvMae())).collect(Collectors.toList());
    comboEstadoCivil.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboEstadoCivil;
  }

  @Override
  @Cacheable(value = "comboMotivoSolicitud", sync = true)
  public List<ComboDto> getComboMotivoSolicitud(String codProCorto) throws SinacException {
    List<ComboDto> comboMotivoSolicitud = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(COD_SITUACIONES + codProCorto).stream()
        .map(c -> new ComboDto(c.getIdLdvMae(), c.getNomLdvMae())).collect(Collectors.toList());
    comboMotivoSolicitud.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboMotivoSolicitud;
  }

  @Override
  @Cacheable(value = "comboOrganismoDestino", sync = true)
  public List<ComboDto> getComboOrganismoDestino() throws SinacException {
    List<ComboDto> comboOrigenSolicitud = catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod(LDV_ORG_DESTINO)
        .stream().map(c -> {
          // Obtener los valores de nomLdvMae y desLdvMae
          String nomLdvMae = c.getNomLdvMae();
          String desLdvMae = c.getDesLdvMae();
          // Construir el valor del campo desplegable según la condición
          String valorCampoDesplegable;
          if (desLdvMae == null || desLdvMae.isEmpty()) {
            valorCampoDesplegable = nomLdvMae;
          } else {
            valorCampoDesplegable = nomLdvMae + " - " + desLdvMae;
          }
          // Retornar el nuevo objeto ComboDto
          return new ComboDto(c.getIdLdvMae(), valorCampoDesplegable);
        }).collect(Collectors.toList());
    comboOrigenSolicitud.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboOrigenSolicitud;
  }

  @Override
  @Cacheable(value = "comboDocumentoTipo", sync = true)
  public AdjuntarDocumentoComboDto getComboDocumentoTipo() throws SinacException {
    AdjuntarDocumentoComboDto comboDocumentoTipo = new AdjuntarDocumentoComboDto();
    comboDocumentoTipo.setListaDocumentoTipoDto(componentesComunesService.getListaDocumentoTipoDto(null));
    comboDocumentoTipo.setListaComboLdvMaestraEstadosElaboracionDto(
        componentesComunesService.getListaComboLdvMaestraEstadosElaboracionDto());
    comboDocumentoTipo.setListaComboLdvOrigenDto(componentesComunesService.getListaComboLdvOrigenDto());
    comboDocumentoTipo.setListaComboLdvOrganoDto(componentesComunesService.getListaComboLdvOrganoDto());
    return comboDocumentoTipo;
  }

  @Override
  @Cacheable(value = "listaDocumentoPorProcedimiento", sync = true)
  public List<DocumentoTipoDto> getListaDocumentoTipoDto(Short procedimiento) {
    List<DocumentoTipoDto> listaTiposDocumentos = new ArrayList<>();
    listaTiposDocumentos.addAll(documentosService.getComboDocumentoTipo(procedimiento));
    listaTiposDocumentos.sort((a, b) -> a.getNomTipo().compareTo(b.getNomTipo()));
    return listaTiposDocumentos;
  }

  @Override
  public List<DocumentoTipoDto> getListaDocumentoTipoDtoSinEntrada(Short procedimiento) {
    List<DocumentoTipoDto> listaTiposDocumentosAux = new ArrayList<>();
    List<DocumentoTipoDto> listaTiposDocumentos = new ArrayList<>();
    listaTiposDocumentosAux.addAll(documentosService.getComboDocumentoTipo(procedimiento));
    listaTiposDocumentos.addAll(listaTiposDocumentosAux.stream()
        .filter(c -> c.getCodTipo() != null && !c.getCodTipo().startsWith("STN")).toList());
    listaTiposDocumentos.sort((a, b) -> a.getNomTipo().compareTo(b.getNomTipo()));
    return listaTiposDocumentos;
  }

  @Override
  public List<DocumentoTipoDto> getListaDocumentoTipoDtoSinFiltro(Short procedimiento) {
    List<DocumentoTipoDto> listaTiposDocumentos = new ArrayList<>();
    listaTiposDocumentos.addAll(documentosService.getComboDocumentoTipo(procedimiento));
    return listaTiposDocumentos;
  }

  @Override
  public List<DocumentoTipoDto> getListaDocumentoTipoDtoByProcId(Short id) {
    // TODO revisar con Víctor - resolucion expedientes
    return getListaDocumentoTipoDtoSinFiltro(null).stream().distinct().toList();
  }

  @Override
  @Cacheable(value = "comboLdvMaestraEstadosElaboracion", sync = true)
  public List<LdvMaestraDto> getListaComboLdvMaestraEstadosElaboracionDto() throws SinacException {
    List<LdvMaestraDto> listaComboLdvMaestraEstadosElaboracionDto = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(TipoDatoMaestroEnum.ESTADO_ELABORACION.getCod());
    listaComboLdvMaestraEstadosElaboracionDto.sort((a, b) -> a.getNomLdvMae().compareTo(b.getNomLdvMae()));
    return listaComboLdvMaestraEstadosElaboracionDto;
  }

  @Override
  @Cacheable(value = "comboLdvOrigen", sync = true)
  public List<LdvMaestraDto> getListaComboLdvOrigenDto() throws SinacException {
    List<LdvMaestraDto> listaComboLdvOrigenDto = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(TipoDatoMaestroEnum.ORIGEN_DOCUMENTOS.getCod());
    listaComboLdvOrigenDto.sort((a, b) -> a.getNomLdvMae().compareTo(b.getNomLdvMae()));
    return listaComboLdvOrigenDto;
  }

  @Override
  @Cacheable(value = "comboMotivosRepre", sync = true)
  public List<ComboDto> getComboMotivosRepre() throws SinacException {
    List<LdvMaestraDto> listaMotivosRepre = catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod("PER");
    listaMotivosRepre.removeIf(x -> x.getCodLdvMae().equals(Constantes.Personas.TIPO_INTERESADO)
        || x.getCodLdvMae().equals(Constantes.Personas.TIPO_REPRESENTANTE_MANDATO));
    List<ComboDto> comboMotivosRepre = listaMotivosRepre.stream()
        .map(t -> new ComboDto(t.getIdLdvMae(), t.getNomLdvMae())).collect(Collectors.toList());
    comboMotivosRepre.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboMotivosRepre;
  }

  @Override
  @Cacheable(value = "comboTiposRc", sync = true)
  public List<ComboDto> getComboTiposRc() throws SinacException {
    List<ComboDto> comboTiposRc = catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod("PER_RC").stream()
        .map(t -> new ComboDto(t.getIdLdvMae(), t.getNomLdvMae())).collect(Collectors.toList());
    if (!comboTiposRc.isEmpty()) {
      comboTiposRc.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    }
    return comboTiposRc;
  }

  @Override
  @Cacheable(value = "comboTiposVia", sync = true)
  public List<ComboDto> getComboTiposVia() throws SinacException {
    List<ComboDto> comboTiposVia = expedientesService.getTiposVia().stream()
        .map(l -> new ComboDto(l.getIdTipoVia(), l.getNomTipoVia())).collect(Collectors.toList());
    comboTiposVia.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboTiposVia;
  }

  @Override
  @Cacheable(value = "comboMotivoComunicacion", sync = true)
  public List<ComboDto> getComboMotivoComunicacion() throws SinacException {
    List<ComboDto> comboMotivoComunicacion = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(LDV_MOTIVO_COMUNICACION_EXTERNA).stream()
        .map(p -> new ComboDto(p.getIdLdvMae(), p.getNomLdvMae())).collect(Collectors.toList());
    comboMotivoComunicacion.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboMotivoComunicacion;
  }

  @Override
  @Cacheable(value = "comboTipoComunicacion", sync = true)
  public List<ComboDto> getComboTipoComunicacion() throws SinacException {
    List<ComboDto> comboTipoComunicacion = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(LDV_TIPO_COMUNICACION_EXTERNA).stream()
        .map(p -> new ComboDto(p.getIdLdvMae(), p.getNomLdvMae())).collect(Collectors.toList());
    comboTipoComunicacion.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboTipoComunicacion;
  }

  @Override
  @Cacheable(value = "comboLdvOrgano", sync = true)
  public List<LdvMaestraDto> getListaComboLdvOrganoDto() {
    List<LdvMaestraDto> listaComboLdvOrganoDto = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(TipoDatoMaestroEnum.ORGANO_DOCUMENTO.getCod());
    listaComboLdvOrganoDto.sort((a, b) -> a.getNomLdvMae().compareTo(b.getNomLdvMae()));
    return listaComboLdvOrganoDto;
  }

  @Override
  public List<FormularioCamposValidaDto> getFormularioCamposValidaListaSinExpedienteByIdProcedimiento(Short idPro)
      throws SinacException {
    return componentesComunesService.getListaFormularioCamposValidaDtos().stream()
        .filter(f -> f.getProcedimiento() != null).filter(f -> f.getProcedimiento().getIdPro() != null)
        .filter(f -> f.getProcedimiento().getIdPro().equals(idPro)).filter(f -> !f.isFlgExpediente()).toList();
  }

  @Override
  @Cacheable(value = "listaFormularioCamposValida", sync = true)
  public List<FormularioCamposValidaDto> getListaFormularioCamposValidaDtos() {
    return formularioCamposValidaService.findAll();
  }

  @Override
  public List<FormularioCamposValidaDto> getListaFormularioCamposValidaByIdProcedimientoFlgExpediente(Short idPro,
      Boolean flgExpediente) throws SinacException {
    List<FormularioCamposValidaDto> lista = new ArrayList<>();
    if (flgExpediente == null) {
      lista.addAll(componentesComunesService.getListaFormularioCamposValidaDtos().stream().filter(f -> {
        return (f.getProcedimiento().getIdPro().equals(idPro));
      }).toList());
    } else {
      lista.addAll(componentesComunesService.getListaFormularioCamposValidaDtos().stream().filter(f -> {
        return (f.getProcedimiento().getIdPro().equals(idPro) && f.isFlgExpediente() == flgExpediente);
      }).toList());
    }
    return lista;
  }

  @Override
  public List<ComboDto> getComboDtoByIdLdvEntidadMaestra(Short id) {
    List<ComboDto> lista = new ArrayList<>();
    lista.addAll(componentesComunesService.getListaLdvMaestra().stream()
        .filter(ldv -> ldv.getLdvEntidadesMaestrasDto().getIdLdvEntMae().shortValue() == id).map(p -> {
          // Obtener los valores de nomLdvMae y desLdvMae
          String nomLdvMae = p.getNomLdvMae();
          String desLdvMae = p.getDesLdvMae();

          // Construir el valor del campo desplegable según la condición
          String valorCampoDesplegable;
          if (desLdvMae == null || desLdvMae.isEmpty()) {
            valorCampoDesplegable = nomLdvMae;
          } else {
            valorCampoDesplegable = nomLdvMae + " - " + desLdvMae;
          }

          // Retornar el nuevo objeto ComboDto
          return new ComboDto(p.getIdLdvMae(), valorCampoDesplegable);
        }).collect(Collectors.toList()));
    lista.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return lista;
  }

  @Override
  public List<ComboDto> getComboDtoByCodLdvEntidadMaestra(String codigoEntidadMaestra) {
    List<ComboDto> lista = new ArrayList<>();
    lista.addAll(componentesComunesService.getListaLdvMaestra().stream()
        .filter(ldv -> ldv.getLdvEntidadesMaestrasDto().getCodLdvEntMae().equals(codigoEntidadMaestra))
        .map(p -> new ComboDto(p.getIdLdvMae(), p.getNomLdvMae())).collect(Collectors.toList()));
    lista.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return lista;
  }

  @Override
  @Cacheable(value = "listaLdvMaestra", sync = true)
  public List<LdvMaestraDto> getListaLdvMaestra() {
    return catalogosService.getAllLdvMaestraDto();
  }

  @Override
  public List<FaseDto> getListaFasesDtoByProcId(Short id) {
    return componentesComunesService.getListaProcedimientoFases().stream()
        .filter(pro -> pro.getProcedimientoDto().getIdPro().equals(id)).map(ProcedimientosFasesDto::getFaseDto)
        .toList();
  }

  @Override
  @Cacheable(value = "listaProcedimientoFases", sync = true)
  public List<ProcedimientosFasesDto> getListaProcedimientoFases() {
    return procedimientosService.getListaProcedimientoFases();
  }

  @Override
  public List<TramiteDto> getListaTramiteDtoByProcId(Short id, Short faseId) {
    List<TramiteDto> lista = new ArrayList<>();
    lista.addAll(procedimientosService.getListaTramiteByIdProIdFase(id, faseId));
    lista.sort((a, b) -> a.getNomTramite().compareTo(b.getNomTramite()));
    return lista;
  }

  @Override
  public List<EstadoDto> getListaMaquinaEstadosDtoByProcIdFaseIdTramiteId(Short procId, Short faseId, Short tramiteId) {
    List<EstadoDto> lista = new ArrayList<>();
    lista.addAll(procedimientosService.getEstadosByidProcedimientoidTramiteIdFase(procId, faseId, tramiteId));
    lista.sort((a, b) -> a.getNomEstado().compareTo(b.getNomEstado()));
    return lista;
  }

  @Override
  public List<AccionDto> obtenerAccionesByMapaAccionesCodAccion(Map<String, Map<String, Long>> mapaAcciones,
      String codAccion) {
    List<AccionDto> resultado = new ArrayList<>();
    mapaAcciones.forEach((key, value) -> {
      if (value.containsKey(codAccion)) {
        resultado.add(componentesComunesService.getListaAcciones().stream()
            .filter(accion -> accion.getCodAccion().equals(key)).toList().get(0));
      }
    });
    return resultado;
  }

  @Override
  @Cacheable(value = "listaAcciones", sync = true)
  public List<AccionDto> getListaAcciones() {
    return accionesService.getAllAcciones();
  }

  @Override
  @Cacheable(value = "comboSentidoAcuerdo", sync = true)
  public List<ComboDto> getComboSentidoAcuerdo() {
    List<ComboDto> comboSentidoAcuerdo = catalogosService
        .getComboLdvMaestraByLdvEntidadMaestraCod(LDV_RESULTADO_ACUERDO_RESOLUCION).stream()
        .map(r -> new ComboDto(r.getIdLdvMae(), r.getNomLdvMae())).collect(Collectors.toList());
    comboSentidoAcuerdo.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboSentidoAcuerdo;
  }

  @Override
  @Cacheable(value = "comboSentidoCniDtoByCodLdvEntidadMaestra", sync = true)
  public List<LdvMaestraDto> getComboSentidoCniDtoByCodLdvEntidadMaestra() throws SinacException {
    return catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod(LDV_SENTIDO_INFORME_CNI);
  }

  @Override
  public void cargarDatosUsuarioSesion(ModelAndView modelAndView) {
    String nombreUsuario = sinacSession.getFullName();
    modelAndView.addObject(NOMBRE_USUARIO, nombreUsuario);
    Long rolUsuario = 0l;
    String procedimientoRol = "";
    Short idPro = 0;
    InicioDto inicio = new InicioDto();
    if (sinacSession.getRolUsuarioSeleccionado() != null) {
      rolUsuario = sinacSession.getRolUsuarioSeleccionado().getIdRolUsu();
      procedimientoRol = sinacSession.getRolUsuarioSeleccionado().getProcedimientoDto().getNomPro();
      idPro = sinacSession.getRolUsuarioSeleccionado().getProcedimientoDto().getIdPro();
      inicio.setProcedimiento(sinacSession.getRolUsuarioSeleccionado().getProcedimientoDto().getCodCorto());
      inicio.setRol(sinacSession.getRolUsuarioSeleccionado().getRol().getNomRol());
    }
    modelAndView.addObject(INICIO, inicio);
    modelAndView.addObject(ROL, rolUsuario);
    modelAndView.addObject(PROCEDIMIENTO_ROL, procedimientoRol);
    modelAndView.addObject(ID_PRO, idPro);
  }

  @Override
  public Integer cargarIdUsuarioSesion() {
    return sinacSession.getUsuario().getIdUsu();
  }

  /**
   * Obtiene el Identificador de la Plantilla asociado al Identificador de
   * Expediente y al Identificador de Requerimiento establecidos como parámetros.
   *
   * @param idExpediente Identificador del Expediente.
   * @param idExpReq     Identificador del Requerimiento.
   * @return Identificador de la Plantilla.
   * @throws SinacException Si se produce un error al obtener el Identificador de
   *                        la Plantilla.
   */
  @Override
  public short getIdPlantillaByIdExpedienteAndIdExpReq(BigInteger idExpediente, BigInteger idExpReq)
      throws SinacException {
    return requerimientosAndAudienciasService.getIdPlantillaByIdExpedienteAndIdExpReq(idExpediente, idExpReq);
  }

  @Override
  @Cacheable(value = "comboTipoRelacionesExpedientes", sync = true)
  public List<ComboDto> getComboTipoRelacionesExpedientes() throws SinacException {
    List<ComboDto> comboTipoRelacionesExpedientes = expedientesService.getTipoRelacionesExpediente().stream()
        .map(s -> new ComboDto(s.getIdLdvMae(), s.getNomLdvMae())).collect(Collectors.toList());
    comboTipoRelacionesExpedientes.sort((a, b) -> a.getValorCampoDesplegable().compareTo(b.getValorCampoDesplegable()));
    return comboTipoRelacionesExpedientes;
  }

  /**
   * Compara strings ignorando espacios en blanco, mayúsculas, minúsculas y signos
   * de acentuación
   * 
   * @param s1 String
   * @param s2 String
   * @return Si coinciden devuelve true
   */
  @Override
  public boolean compararStringsSinAcentosEspaciosMayusculas(String s1, String s2) {
    if (StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) {
      return true;
    } else if ((StringUtils.isEmpty(s1) && !StringUtils.isEmpty(s2))
        || (!StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2))) {
      return false;
    } else {
      // Normalizar cadenas para eliminar acentos y convertirlas a mayúsculas
      s1 = Normalizer.normalize(s1, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase().trim();
      s2 = Normalizer.normalize(s2, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase().trim();

      // Comparar cadenas ignorando espacios
      return s1.replaceAll("\\s", "").equals(s2.replaceAll("\\s", ""));
    }
  }

  /**
   * Compara 2 fechas teniendo en cuenta este formato: "dd/MM/yyyy"
   * 
   * @param fecha1 Date
   * @param fecha2 Date
   * @return Si coinciden devuelve true
   */
  @Override
  public boolean compararFechas(Date fecha1, Date fecha2) {
    // Definimos el formato de la fecha que queremos: dd/mm/yyyy
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    // Convertimos las fechas al formato String
    String fecha1Str = "";
    if (fecha1 != null) {
      fecha1Str = formatter.format(fecha1);
    }
    String fecha2Str = "";
    if (fecha2 != null) {
      fecha2Str = formatter.format(fecha2);
    }
    // Comparamos los strings
    return fecha1Str.equals(fecha2Str);

  }

  @Override
  public List<LdvMaestraDto> getComboEstadoDocumentoValidacion(String codLdvEntMae) {
    List<LdvMaestraDto> listaEstadosDocValidacion = new ArrayList<>();
    listaEstadosDocValidacion.addAll(catalogosService.getComboLdvMaestraByLdvEntidadMaestraCod(codLdvEntMae));
    listaEstadosDocValidacion.sort((a, b) -> a.getNomLdvMae().compareTo(b.getNomLdvMae()));

    return listaEstadosDocValidacion;
  }

}
