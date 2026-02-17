package es.mjusticia.sinac.core.business.service;

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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.servlet.ModelAndView;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.AccionDto;
import es.mjusticia.sinac.core.model.dto.AdjuntarDocumentoComboDto;
import es.mjusticia.sinac.core.model.dto.ComboDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteEstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.FaseDto;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.TramiteDto;

public interface ComponentesComunesService {

  void cargarDatosUsuarioSesion(ModelAndView modelAndView);

  AdjuntarDocumentoComboDto getComboDocumentoTipo();

  Integer cargarIdUsuarioSesion();

  List<ComboDto> getComboPaises();

  List<ComboDto> getComboProvincias();

  List<ComboDto> getComboLocalidades();

  List<ComboDto> getComboLocalidadesPorProvincia(String codProvincia);

  List<LdvMaestraDto> getListaComboLdvOrganoDto();

  Short getIdProvinciaByIdLocalidad(Short parseShort);

  LocalidadesDto getLocalidadById(Short idMunicipio);

  ProvinciasDto getProvinciaById(Short idProvincia);

  List<LdvMaestraDto> getComboSentidoCniDtoByCodLdvEntidadMaestra() throws SinacException;

  boolean informeDisponibleEstado(List<ExpedienteEstadoDto> listaExpedientesEstadosDtos);

  List<AccionDto> obtenerAccionesByMapaAccionesCodAccion(Map<String, Map<String, Long>> mapaAcciones, String codAccion);

  int calcularEdadActual(Date fechaNacimiento);

  int calcularEdadEnFechaRegistro(Date fechaNacimiento, Date fechaRegistro);

  boolean informeDisponible(List<ExpedienteInformeDto> listaExpedienteInformeDto);

  List<ComboDto> getComboSentidoAcuerdo();

  List<ComboDto> getComboDtoByCodLdvEntidadMaestra(String codigoEntidadMaestra);

  List<LdvMaestraDto> getListaComboLdvOrigenDto() throws SinacException;

  List<DocumentoTipoDto> getListaDocumentoTipoDtoSinEntrada(Short procedimiento);

  List<DocumentoTipoDto> getListaDocumentoTipoDtoSinFiltro(Short procedimiento);

  List<LdvMaestraDto> getListaComboLdvMaestraEstadosElaboracionDto() throws SinacException;

  List<FormularioCamposValidaDto> getFormularioCamposValidaListaSinExpedienteByIdProcedimiento(Short idPro)
      throws SinacException;

  List<FaseDto> getListaFasesDtoByProcId(Short id);

  List<TramiteDto> getListaTramiteDtoByProcId(Short id, Short faseId);

  List<EstadoDto> getListaMaquinaEstadosDtoByProcIdFaseIdTramiteId(Short procId, Short faseId, Short tramiteId);

  List<ComboDto> getComboMotivoSolicitud(String codProCorto) throws SinacException;

  List<FormularioCamposValidaDto> getListaFormularioCamposValidaByIdProcedimientoFlgExpediente(Short idPro,
      Boolean flgExpediente) throws SinacException;

  List<DocumentoTipoDto> getListaDocumentoTipoDtoByProcId(Short id);

  List<ComboDto> getComboTiposVia() throws SinacException;

  List<ComboDto> getComboProcedimientos() throws SinacException;

  List<LdvMaestraDto> getListaComboLdvMaestraTiposIdentificacionDto() throws SinacException;

  List<ComboDto> getComboNacionalides() throws SinacException;

  List<ComboDto> getComboSexo() throws SinacException;

  List<ComboDto> getComboEstadoCivil() throws SinacException;

  List<ComboDto> getComboMotivosRepre() throws SinacException;

  List<ComboDto> getComboDtoByIdLdvEntidadMaestra(Short id);

  List<ComboDto> getComboMotivoComunicacion() throws SinacException;

  List<ComboDto> getComboPrefijos() throws SinacException;

  short getIdPlantillaByIdExpedienteAndIdExpReq(BigInteger idExpediente, BigInteger idExpReq) throws SinacException;

  List<ComboDto> getComboOrganismoDestino() throws SinacException;

  List<ComboDto> getComboTiposRc() throws SinacException;

  List<ComboDto> getComboTipoRelacionesExpedientes() throws SinacException;

  List<DocumentoTipoDto> getListaDocumentoTipoDto(Short procedimiento);

  List<ComboDto> getComboTipoComunicacion() throws SinacException;

  List<ProvinciasDto> getListaProvinicias() throws SinacException;

  List<LocalidadesDto> getListaLocalidades() throws SinacException;

  List<LdvMaestraDto> getListaLdvMaestra();

  List<ProcedimientosFasesDto> getListaProcedimientoFases();

  List<FormularioCamposValidaDto> getListaFormularioCamposValidaDtos();

  List<AccionDto> getListaAcciones();

  /**
   * Compara 2 fechas teniendo en cuenta este formato: "dd/MM/yyyy"
   * 
   * @param fecha1 Date
   * @param fecha2 Date
   * @return Si coinciden devuelve true
   */
  boolean compararFechas(Date fecha1, Date fecha2);

  /**
   * Compara strings ignorando espacios en blanco, mayúsculas, minúsculas y signos
   * de acentuación
   * 
   * @param s1 String
   * @param s2 String
   * @return Si coinciden devuelve true
   */
  boolean compararStringsSinAcentosEspaciosMayusculas(String s1, String s2);

  /**
   * Devuelve la lista de estados de documentos del semaforo de las validaciones,
   * dependiendo del codLdvEntMae que se le pase.
   * 
   * @param codLdvEntMae
   * @return
   */
  List<LdvMaestraDto> getComboEstadoDocumentoValidacion(String codLdvEntMae);

}
