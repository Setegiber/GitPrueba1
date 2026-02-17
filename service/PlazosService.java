package es.mjusticia.sinac.core.business.service;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2023 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.ExpedientesPlazosDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PlazoDto;
import es.mjusticia.sinac.core.model.dto.PlazosProcedimientosFasesTramitesOperacionesAccionesDto;

/**
 * Componente de Negocio para la Interfaz del Servicio de Plazos.
 *
 * @author NTT Data.
 */
public interface PlazosService {

  /**
   * Obtiene la configuración que determina si el Tipo de Acción tiene que crear,
   * suspender, reanudar o finalizar algún Plazo del Expediente.
   *
   * @param idProFaseTraOpeAcc Identificador del Tipo de Acción.
   * @return Configuración que determina si el Tipo de Acción tiene que crear,
   *         suspender, reanudar o finalizar algún Plazo del Expediente.
   * @throws SinacException Si se produce un error al obtener la configuración que
   *                        determina si el Tipo de Acción tiene que crear,
   *                        suspender, reanudar o finalizar algún Plazo del
   *                        Expediente.
   */
  List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> getConfiguracionAccionPlazoByIdProFaseTraOpeAcc(
      long idProFaseTraOpeAcc) throws SinacException;

  /**
   * Obtiene la configuración que determina si el Tipo de Acción tiene que crear,
   * suspender o reanudar algún Plazo del Expediente.
   *
   * @param idProFaseTraOpeAcc Identificador del Tipo de Acción.
   * @param estado             Estado del Plazo tras llevar a cabo la Operación
   *                           asociada al Tipo de Acción.
   * @return Configuración que determina si el Tipo de Acción tiene que crear,
   *         suspender o reanudar algún Plazo del Expediente.
   * @throws SinacException Si se produce un error al obtener la configuración que
   *                        determina si el Tipo de Acción tiene que crear,
   *                        suspender o reanudar algún Plazo del Expediente.
   */
  List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> getConfiguracionAccionPlazoByIdProFaseTraOpeAccAndEstado(
      long idProFaseTraOpeAcc, LdvMaestraDto estado) throws SinacException;

  /**
   * Obtiene el Plazo Vigente asociado al Identificador de Expediente y al
   * Identificador de Plazo establecidos como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @param idPlazo      Identificador del Plazo.
   * @return DTO con la Información del Plazo Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo Vigente
   *                        asociado al Identificador de Expediente y al
   *                        Identificador de Plazo establecidos como parámetro.
   */
  ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazo(BigInteger idExpediente, short idPlazo)
      throws SinacException;

  /**
   * Obtiene el Plazo Vigente en el Estado especificado asociado al Identificador
   * de Expediente y al Identificador de Plazo establecidos como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @param idPlazo      Identificador del Plazo.
   * @param estado       Estado del Plazo.
   * @return DTO con la Información del Plazo Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo Vigente en
   *                        el Estado especificado asociado al Identificador de
   *                        Expediente y al Identificador de Plazo establecidos
   *                        como parámetro.
   */
  ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado(BigInteger idExpediente, short idPlazo,
      String estado) throws SinacException;

  /**
   * Obtiene el Plazo Vigente asociado al Identificador de Expediente, al
   * Identificador de Plazo y al Identificador de Requerimiento establecidos como
   * parámetros.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @return DTO con la Información del Plazo Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo Vigente
   *                        asociado al Identificador de Expediente, al
   *                        Identificador de Plazo y al Identificador de
   *                        Requerimiento establecidos como parámetros.
   */
  ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento(BigInteger idExpediente, short idPlazo,
      BigInteger idRequerimiento) throws SinacException;

  /**
   * Obtiene el Plazo Vigente asociado al Identificador de Expediente, al
   * Identificador de Plazo y al Identificador de Requerimiento establecidos como
   * parámetros.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Plazo.
   * @param codTipoPlazo    Código del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @return DTO con la Información del Plazo Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo Vigente
   *                        asociado al Identificador de Expediente, al
   *                        Identificador de Plazo y al Identificador de
   *                        Requerimiento establecidos como parámetros.
   */
  ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndCodTipoPlazoAndIdRequerimiento(BigInteger idExpediente,
      short idPlazo, String codTipoPlazo, BigInteger idRequerimiento) throws SinacException;

  /**
   * Obtiene el Plazo Vigente en el Estado especificado asociado al Identificador
   * de Expediente, al Identificador de Plazo y al Identificador de Requerimiento
   * establecidos como parámetros.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @param estado          Estado del Plazo.
   * @return DTO con la Información del Plazo Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo Vigente en
   *                        el Estado especificado asociado al Identificador de
   *                        Expediente, al Identificador de Plazo y al
   *                        Identificador de Requerimiento establecidos como
   *                        parámetros.
   */
  ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado(BigInteger idExpediente,
      short idPlazo, BigInteger idRequerimiento, String estado) throws SinacException;

  /**
   * Obtiene los Plazos del Expediente asociados al Identificador de Expediente
   * establecido como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @return Plazos del Expediente asociados al Identificador de Expediente
   *         establecido como parámetro.
   * @throws SinacException Si se produce un error al obtener los Plazos del
   *                        Expediente asociados al Identificador de Expediente
   *                        establecido como parámetro.
   */
  List<ExpedientesPlazosDto> getPlazosExpedienteByIdExpediente(BigInteger idExpediente) throws SinacException;

  /**
   * Comprueba si existen Plazos del Expediente en curso sin tener en cuenta los
   * Plazos de Caducidad de Informes.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @param idExpediente    Identificador del Expediente.
   * @return true, si existen Plazos del Expediente en curso sin tener en cuenta
   *         los Plazos de Caducidad de Informes. false, en caso contrario.
   * @throws SinacException Si se produce un error al comprobar si existen Plazos
   *                        del Expediente en curso sin tener en cuenta los Plazos
   *                        de Caducidad de Informes.
   */
  boolean existsPlazosExpedienteEnCursoForPlazoResolucion(Short idProcedimiento, BigInteger idExpediente)
      throws SinacException;

  /**
   * Obtiene el Histórico del Plazo del Expediente asociado al Identificador de
   * Expediente y al Identificador de Plazo establecidos como parámetros.
   *
   * @param idExpediente Identificador del Expediente.
   * @param idPlazo      Identificador del Tipo de Plazo.
   * @return Histórico del Plazo del Expediente.
   * @throws SinacException Si se produce un error al obtener el Histórico del
   *                        Plazo del Expediente asociado al Identificador de
   *                        Expediente y al Identificador de Plazo establecidos
   *                        como parámetros.
   */
  List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo(BigInteger idExpediente, short idPlazo)
      throws SinacException;

  /**
   * Obtiene el Histórico del Plazo del Expediente asociado al Identificador de
   * Expediente, al Identificador de Plazo y al Identificador de Requerimiento
   * establecidos como parámetros.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @return Histórico del Plazo del Expediente.
   * @throws SinacException Si se produce un error al obtener el Histórico del
   *                        Plazo del Expediente asociado al Identificador de
   *                        Expediente, al Identificador de Plazo y al
   *                        Identificador de Requerimiento establecidos como
   *                        parámetros.
   */
  List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(
      BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento) throws SinacException;

  /**
   * Actualiza en la Tabla "PLA_M_PFTOA" a no vigente el Plazo del Expediente
   * asociado al Identificador establecido como parámetro.
   *
   * @param idExpMPla Identificador del Plazo del Expediente.
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "PLA_M_PFTOA" el Plazo del Expediente a no vigente.
   */
  void updatePlazoExpedienteNoVigente(BigInteger idExpMPla) throws SinacException;

  /**
   * Crea el Plazo del Expediente.
   *
   * @param expedientesPlazosDto Plazo del Expediente a crear.
   * @return DTO con la Información del Plazo del Expediente creado.
   * @throws SinacException Si se produce un error al crear el Plazo del
   *                        Expediente.
   */
  ExpedientesPlazosDto crearPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto) throws SinacException;

  /**
   * Obtiene la siguiente Fecha a una Fecha dada.
   *
   * @param fecha Fecha.
   * @return Fecha siguiente a la Fecha dada.
   */
  Date getNextDate(Date fecha);

  /**
   * Obtiene la siguiente Fecha a una Fecha dada.
   *
   * @param fecha Fecha.
   * @return Fecha siguiente a la Fecha dada.
   */
  Date getNextDate(Calendar fecha);

  /**
   * Obtiene una Fecha posterior a una Fecha dada en base al número de días
   * hábiles a agregar.
   *
   * @param fecha Fecha.
   * @param days  Número de días hábiles a agregar.
   * @param args  Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *              Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *              "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *              Comunidad Autónoma de "Cataluña".
   * @return Fecha posterior a una Fecha dada en base al número de días hábiles a
   *         agregar.
   */
  Date getDateByDaysToBeAdded(Date fecha, int days, String... args);

  /**
   * Obtiene una Fecha posterior a una Fecha dada en base al número de meses a
   * agregar.
   *
   * @param fecha  Fecha.
   * @param months Número de meses a agregar.
   * @param args   Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *               Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *               "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *               Comunidad Autónoma de "Cataluña".
   * @return Fecha posterior a una Fecha dada en base al número de meses a
   *         agregar.
   */
  Date getDateByMonthsToBeAdded(Date fecha, int months, String... args);

  /**
   * Obtiene una Fecha posterior a una Fecha dada en base al número de años a
   * agregar.
   *
   * @param fecha Fecha.
   * @param years Número de años a agregar.
   * @param args  Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *              Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *              "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *              Comunidad Autónoma de "Cataluña".
   * @return Fecha posterior a una Fecha dada en base al número de años a agregar.
   */
  Date getDateByYearsToBeAdded(Date fecha, int years, String... args);

  /**
   * Comprueba si una Fecha dada es Sábado o Domingo.
   *
   * @param fecha Fecha.
   * @return true, si la Fecha dada es Sábado o Domingo. false, en caso contrario.
   */
  boolean isWeekend(Date fecha);

  /**
   * Comprueba si una Fecha dada es Sábado o Domingo.
   *
   * @param fecha Fecha.
   * @return true, si la Fecha dada es Sábado o Domingo. false, en caso contrario.
   */
  boolean isWeekend(Calendar fecha);

  /**
   * Comprueba si una Fecha dada es un Festivo.
   *
   * @param fecha Fecha.
   * @param args  Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *              Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *              "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *              Comunidad Autónoma de "Cataluña".
   * @return true, si la Fecha dada es un Festivo. false, en caso contrario.
   * @throws SinacException Si se produce un error al comprobar si la Fecha dada
   *                        es un Festivo.
   */
  boolean isHoliday(Date fecha, String... args) throws SinacException;

  /**
   * Comprueba si una Fecha dada es un día hábil.
   *
   * @param fecha Fecha.
   * @param args  Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *              Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *              "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *              Comunidad Autónoma de "Cataluña".
   * @return true, si la Fecha dada es un día hábil. false, en caso contrario.
   * @throws SinacException Si se produce un error al comprobar si la Fecha dada
   *                        es un día hábil.
   */
  boolean isBusinessDay(Date fecha, String... args) throws SinacException;

  /**
   * Obtiene la siguiente Fecha hábil a una Fecha dada.
   *
   * @param fecha Fecha.
   * @param args  Jerarquía para obtener los días Festivos, args = {"ct"} -> Días
   *              Festivos de la Comunidad Autónoma de "Cataluña", args = {"ct",
   *              "bcn"} -> Días Festivos de la Localidad de "Barcelona" en la
   *              Comunidad Autónoma de "Cataluña".
   * @return Siguiente Fecha hábil a una Fecha dada.
   */
  Date getNextBusinessDay(Date fecha, String... args);

  /**
   * Obtiene los días transcurridos desde la Fecha Inicial hasta la Fecha Final.
   *
   * @param fechaInicial Fecha Inicial.
   * @param fechaFinal   Fecha Final.
   * @return Días transcurridos desde la Fecha Inicial hasta la Fecha Final.
   */
  int getElapsedDays(Date fechaInicial, Date fechaFinal);

  /**
   * Obtiene la Fecha de Fin a partir de una Fecha dada añadiendo la cantidad de
   * días, meses o años configurada.
   *
   * @param fecha              Fecha.
   * @param codTipoPlazoInTime Código del Tipo de Plazo en días, meses y años.
   * @param cantidad           Cantidad de días, meses o años configurada.
   * @param args               Jerarquía para obtener los días Festivos, args =
   *                           {"ct"} -> Días Festivos de la Comunidad Autónoma de
   *                           "Cataluña", args = {"ct", "bcn"} -> Días Festivos
   *                           de la Localidad de "Barcelona" en la Comunidad
   *                           Autónoma de "Cataluña".
   * @return Fecha de Fin.
   */
  Date getDateByTimeToBeAdded(Date fecha, String codTipoPlazoInTime, short cantidad, String... args);

  /**
   * Obtiene el Código del Tipo de Informe asociado al Código del Plazo de
   * Respuesta a Informe establecido como parámetro.
   *
   * @param codPlazoRespuestaInforme Código del Plazo de Respuesta a Informe.
   * @return Código del Tipo de Informe asociado al Código del Plazo de Respuesta
   *         a Informe establecido como parámetro.
   */
  String getCodTipoInformeByCodPlazoRespuestaInforme(String codPlazoRespuestaInforme);

  /**
   * Obtiene el Código del Tipo de Informe asociado al Código del Plazo de
   * Caducidad de Informe establecido como parámetro.
   *
   * @param codPlazoCaducidadInforme Código del Plazo de Caducidad de Informe.
   * @return Código del Tipo de Informe asociado al Código del Plazo de Caducidad
   *         de Informe establecido como parámetro.
   */
  String getCodTipoInformeByCodPlazoCaducidadInforme(String codPlazoCaducidadInforme);

  /**
   * Obtiene el Código del Plazo de Caducidad de Informe asociado al Código de
   * Tipo de Informe establecido como parámetro.
   *
   * @param codTipoInforme Código del Tipo de Informe.
   * @return Código del Plazo de Caducidad de Informe asociado al Código de Tipo
   *         de Informe establecido como parámetro.
   */
  String getCodPlazoCaducidadInformeByCodTipoInforme(String codTipoInforme);

  /**
   * Obtiene el Plazo asociado al Identificador de Procedimiento y al Código de
   * Tipo de Plazo establecidos como parámetro.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @param codTipoPlazo    Código del Tipo de Plazo.
   * @return Plazo asociado al Identificador de Procedimiento y al Código de Tipo
   *         de Plazo establecidos como parámetro.
   * @throws SinacException Si se produce un error al obtener el Plazo asociado al
   *                        Identificador de Procedimiento y al Código de Tipo de
   *                        Plazo establecidos como parámetro.
   */
  PlazoDto getPlazoByIdProcedimientoAndCodTipoPlazo(short idProcedimiento, String codTipoPlazo) throws SinacException;

  /**
   * Comprueba si el Tipo de Plazo es un Plazo de Caducidad de Informe.
   *
   * @param codTipoPlazo Código del Tipo de Plazo.
   * @return true, si el Tipo de Plazo es un Plazo de Caducidad de Informe. false,
   *         en caso contrario.
   */
  boolean isPlazoCaducidadInforme(String codTipoPlazo);

  /**
   * Comprueba si existe un Informe en Estado "Solicitado" para el Plazo de
   * Caducidad de Informe.
   *
   * @param idExpediente Identificador del Expediente.
   * @param codTipoPlazo Código del Tipo de Plazo.
   * @return true, si existe un Informe en Estado "Solicitado" para el Plazo de
   *         Caducidad de Informe. false, en caso contrario.
   * @throws SinacException Si se produce un error al comprobar si existe un
   *                        Informe en Estado "Solicitado" para el Plazo de
   *                        Caducidad de Informe.
   */
  boolean existsInformeRecibidoForPlazoCaducidadInforme(BigInteger idExpediente, String codTipoPlazo)
      throws SinacException;

  /**
   * Obtiene los Plazos Vigentes Vencidos en el Estado especificado.
   *
   * @param estado Estado.
   * @return Plazos Vigentes Vencidos en el Estado especificado.
   * @throws SinacException Si se produce un error al obtener los Plazos Vigentes
   *                        Vencidos en el Estado especificado.
   */
  List<ExpedientesPlazosDto> getPlazosVigentesVencidosByEstado(String estado) throws SinacException;

  /**
   * Obtiene el Plazo de Resolución Vigente asociado al Identificador de
   * Expediente establecido como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @return DTO con la Información del Plazo de Resolución Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo de
   *                        Resolución Vigente asociado al Identificador de
   *                        Expediente establecido como parámetro.
   */
  ExpedientesPlazosDto getPlazoResolucionVigenteByIdExpediente(BigInteger idExpediente) throws SinacException;

  Date getPreviousDate(Date fecha);

  Date getDateByDaysToTakeOff(Date fecha, int days, String... args);

}
