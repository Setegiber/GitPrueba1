package es.mjusticia.sinac.core.batch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformeEntity;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMapperAux;
import es.mjusticia.sinac.core.persistence.ExpedienteDao;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDao;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.dgp.service.impl.InformeDgpServiceImpl;

/**
 * Job para solicitar a la Dgp los informes en estado pendiente
 */
@Component
public class SinacJobCalculoPeriodoIninterrumpido extends SinacJob<ExpedienteInformeDto> {

  private static final String VAL_RLE = "VAL_RLE";

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobCalculoPeriodoIninterrumpido.class);

  private static final String MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA = "Guardada la fecha más antigua initerrumpida por el momento {}";
  private static final String LARGA_DURACI = "LARGA DURACI";
  private static final String PERMA = "PERMA";

  @Value("${sinac.periodo.tramite.default}")
  private int periodoTramitesDefault;

  @Value("${sinac.periodo.tramite.permanente}")
  private int periodoTramitesPermanente;

  @Autowired
  private ExpedienteDao expedienteDao;

  @Autowired
  private ExpedienteInformeDao expedienteInformeDao;

  @Autowired
  private ExpedienteInformeMapperAux expedienteInformeMapperAux;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private UsuariosService usuariosService;

  /**
   * Constructor por defecto
   */
  public SinacJobCalculoPeriodoIninterrumpido() {
    super();
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(InformeDgpServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_CALCULO_PERIODO_ININTERRUMPIDO";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<ExpedienteInformeDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobCalculoPeriodoIninterrumpido.recuperarItems - Init");

    List<ExpedienteInformeEntity> listaExpedienteInformeEntity = expedienteInformeDao
        .getListaExpedientesInformesWithExpedienteInformeDgpTramite();
    return expedienteInformeMapperAux.toDto(listaExpedienteInformeEntity);
  }

  @Override
  public void procesarItem(ExpedienteInformeDto item, Map<String, Object> contextData) {
    LOG.info("SinacJobCalculoPeriodoIninterrumpido.procesarItem - Init");
    LOG.info("Item: {}", item);
    List<String> estadosValidosInf = Arrays.asList("EINF-DES", "EINF-FAV", "EINF-REC");

    if (estadosValidosInf.contains(item.getLdvMaestraDtoByIdEstInfLdv().getCodLdvMae())
        && item.getExpedienteDto() != null && item.getExpedienteDto().getProcedimientoDto() != null
        && item.getExpedienteDto().getProcedimientoDto().getCodCorto().equals("R")) {
      if (item.getExpedienteInformeDgpDto().getExpedienteInformeDgpTramiteDtos() != null
          && !item.getExpedienteInformeDgpDto().getExpedienteInformeDgpTramiteDtos().isEmpty()) {
        if (item.getExpedienteInformeDgpDto() != null
            && item.getExpedienteInformeDgpDto().getPeriodoIninterrumpido() == null) {
          LOG.info(
              "No se ha calculado nunca el periodoIninterrumpido. Se realiza el cálculo del periodo Ininterrumpido.");
          List<ExpedienteInformeDgpTramiteDto> listaTramitesInformeDgp = getListaExpedienteInformeDgpTramiteDtos(item);

          List<ExpedienteInformeDgpTramiteDto> listaAux = listaTramitesInformeDgp.stream().filter(Objects::nonNull)
              .sorted(Comparator.comparing(ExpedienteInformeDgpTramiteDto::getFechaValidez,
                  Comparator.nullsFirst(Comparator.reverseOrder())))
              .toList();

          item.getExpedienteInformeDgpDto().setPeriodoIninterrumpido(calcularPeriodoIninterrumpido(item, listaAux));
          expedientesFacade.saveExpedienteInformeDgp(item.getExpedienteInformeDgpDto());

        }
      } else if (item.getExpedienteInformeDgpDto() != null
          && item.getExpedienteInformeDgpDto().getPeriodoIninterrumpido() == null) {
        LOG.info("No existen trámites. Valor por defecto: 0 Años 0 Meses 0 Días");
        item.getExpedienteInformeDgpDto().setPeriodoIninterrumpido("0 Años 0 Meses 0 Días");
        expedientesFacade.saveExpedienteInformeDgp(item.getExpedienteInformeDgpDto());
      }
    }
    LOG.info("Se realiza la validación de Residencia Legal del semáforo");
    validacionSemaforoResidenciaLegal(item);
    LOG.info("SinacJobDgpRecibir.procesarItem - End");
  }

  private List<ExpedienteInformeDgpTramiteDto> getListaExpedienteInformeDgpTramiteDtos(
      ExpedienteInformeDto informeActivo) {
    List<ExpedienteInformeDgpTramiteDto> listado = new ArrayList<>();
    if (informeActivo != null) {
      for (ExpedienteInformeDgpTramiteDto tramiteInforme : informeActivo.getExpedienteInformeDgpDto()
          .getExpedienteInformeDgpTramiteDtos()) {
        listado.add(tramiteInforme);
      }
    }
    return listado;
  }

  /**
   * Método que calcula el periodo legal ininterrumpido de Residencia.
   * 
   * @param expedienteInformeDto
   * @param listaTramitesInformeDgp
   * @return
   */
  private String calcularPeriodoIninterrumpido(ExpedienteInformeDto expedienteInformeDto,
      List<ExpedienteInformeDgpTramiteDto> listaTramitesInformeDgp) {
    LOG.debug("Metodo Calculo Periodo Residencia ininterrumpido - Init");
    String periodoIninterrumpido = "0 Años 0 Meses 0 Días";
    boolean periodoIndefinido = false;
    boolean tramiteInterrumpido = false;
    boolean periodoBloqueado = false;
    ExpedienteInformeDgpTramiteDto primerTramiteAnterior = null;
    boolean primerTramiteAnteriorValido = true;
    int indiceEncontrado = 0;
    int periodoEntreTramites = periodoTramitesDefault; // Valor parametrizable expresado en meses
    if (listaTramitesInformeDgp != null && !listaTramitesInformeDgp.isEmpty()) {
      List<Object[]> resumenExpediente = expedienteDao
          .getExpedienteAndFechNacInteresadoByIdExpInforme(expedienteInformeDto.getIdExpInf());
      Date fechaEfectos = (Date) resumenExpediente.get(0)[0];
      Date fechaNacimiento = (Date) resumenExpediente.get(0)[1];
      Date fechaMasAntiguaTraIninterrumpido = null;
      // Verificacion de si existe al menos un tramite en la lista con fecha
      // Denegacion/sin fecha Concesión/Fecha Antigua < Fecha Nacimiento
      int idx = 0;
      for (ExpedienteInformeDgpTramiteDto tramite : listaTramitesInformeDgp) {
        Date fechaMasAntigua = fechaMasAntiguaTramite(tramite.getFechaSolicitud(), tramite.getFechaConcesion());
        if (tramite.getFechaDenegacion() != null || tramite.getFechaConcesion() == null
            || (fechaMasAntigua != null && fechaMasAntigua.before(fechaNacimiento))
            || (tramite.getFechaValidez() != null && tramite.getFechaValidez().before(fechaMasAntigua))) {
          LOG.info(
              "El tramite actual ({}) no cuenta con fechaConcesion {} o cuenta con fechaDenegación {} o la fechaMasAntigua es anterior a la fechaNacimiento {} o la fechaValidez es anterior a cualquier otra fecha {}",
              tramite.getTramite(), tramite.getFechaConcesion(), tramite.getFechaDenegacion(), fechaMasAntigua,
              tramite.getFechaValidez());
          periodoIndefinido = true;
          periodoIninterrumpido = "INDEFINIDO";
          break;
        }

        if (fechaMasAntigua != null && fechaEfectos != null
            && (fechaMasAntigua.before(fechaEfectos) || fechaMasAntigua.equals(fechaEfectos))
            && primerTramiteAnterior == null) {
          primerTramiteAnterior = tramite;
          if (primerTramiteAnterior.getFechaValidez() != null
              && primerTramiteAnterior.getFechaValidez().before(fechaEfectos)) {
            LOG.info(
                "Se verifica que el primer tramite inmediatamente anterior a la fechaEfectos se encuentre a un rango máximo de 3 Meses");
            primerTramiteAnteriorValido = verificarPeriodoEntreTramitesTemporales(fechaEfectos,
                primerTramiteAnterior.getFechaValidez(), null, periodoTramitesDefault);
            LOG.info("La verificacion indica que es {}", primerTramiteAnteriorValido);
          }

          // Si todavía no tenemos primerTramiteAnterior y el tramite actual es un
          // "permanente" (sin fechaValidez), buscamos si hay un trámite (entre los
          // posteriores en la lista - que son más antiguos) dentro de los años
          // anteriores al trámite permanente y que además sea anterior a fechaEfectos.
        } else if (primerTramiteAnterior == null && tramite.getFechaValidez() == null) {
          int encontrado = buscarIndiceTramiteRangoPermanentes(idx, listaTramitesInformeDgp, tramite);
          if (encontrado >= 0 && encontrado < listaTramitesInformeDgp.size()) {
            ExpedienteInformeDgpTramiteDto candidato = listaTramitesInformeDgp.get(encontrado);
            Date fechaMasAntiguaCandidato = fechaMasAntiguaTramite(candidato.getFechaSolicitud(),
                candidato.getFechaConcesion());
            // Aseguramos que el candidato esté antes o igual a la fechaEfectos
            if (fechaMasAntiguaCandidato != null && fechaEfectos != null
                && (fechaMasAntiguaCandidato.before(fechaEfectos) || fechaMasAntiguaCandidato.equals(fechaEfectos))) {
              LOG.info(
                  "Se ha encontrado tramite dentro del rango 5 años (y anterior a fechaEfectos). Fecha mas antigua: {} Tramite: {}",
                  fechaMasAntiguaCandidato, candidato.getTramite());
              primerTramiteAnterior = candidato;
              // Si el candidato tiene fechaValidez y está antes de fechaEfectos, verificamos
              // que cumpla el periodo entre tramites (por defecto 3 meses salvo bloqueo).
              if (primerTramiteAnterior.getFechaValidez() != null
                  && primerTramiteAnterior.getFechaValidez().before(fechaEfectos)) {
                primerTramiteAnteriorValido = true;
              }
            } else {
              LOG.info("No existe ningun tramite en rango 5 años anterior a fechaEfectos para este permanente.");
            }
          }
        }
        periodoBloqueado = comprobacionTramitePermanenteBloqueado(periodoBloqueado, tramite);
        idx++;
      }
      if (!periodoIndefinido && primerTramiteAnteriorValido) {
        ListIterator<ExpedienteInformeDgpTramiteDto> it = listaTramitesInformeDgp.listIterator();
        while (it.hasNext() && !tramiteInterrumpido) {
          periodoEntreTramites = periodoTramitesDefault;
          ExpedienteInformeDgpTramiteDto tramite = it.next();
          LOG.info("Busqueda del tramite más antiguo ininterrumpido. Tramite actual: {}", tramite.getTramite());
          Date fechaMasAntigua = fechaMasAntiguaTramite(tramite.getFechaSolicitud(), tramite.getFechaConcesion());
          if (fechaMasAntigua != null && fechaEfectos != null
              && (fechaMasAntigua.before(fechaEfectos) || fechaMasAntigua.equals(fechaEfectos))) {
            LOG.info("Se ha encontrado tramite con fecha anterior a fechaEfectos -> {}", tramite.getTramite());

            // Si el trámite actual no tiene fecha de validez y cuyo nombre incluye dichas
            // claves, el periodo entre tramites aumenta.
            if (!periodoBloqueado && tramite.getTramite() != null
                && (tramite.getFechaValidez() == null
                    && (tramite.getTramite().contains(PERMA) || tramite.getTramite().contains(LARGA_DURACI))
                    || tramite.getFechaValidez() == null)) {
              periodoEntreTramites = periodoTramitesPermanente;
              LOG.info("Se ha encontrado tramite Permanente {}", tramite.getTramite());
            }

            // Este bloque es necesario cuando el primer tramite es PERMANTENTE
            if (periodoEntreTramites == periodoTramitesPermanente) {
              LOG.info("Busqueda del tramite más antiguo ininterrumpido. Tramite actual: {}", tramite.getTramite());

              indiceEncontrado = buscarIndiceTramiteRangoPermanentes(it.previousIndex(), listaTramitesInformeDgp,
                  tramite);

              if (indiceEncontrado >= 0 && indiceEncontrado < listaTramitesInformeDgp.size()) {
                ExpedienteInformeDgpTramiteDto tramiteEnRango = listaTramitesInformeDgp.get(indiceEncontrado);
                Date fechaMasAntiguaEnRango = fechaMasAntiguaTramite(tramiteEnRango.getFechaSolicitud(),
                    tramiteEnRango.getFechaConcesion());
                if (fechaMasAntiguaEnRango != null) {
                  fechaMasAntiguaTraIninterrumpido = fechaMasAntiguaEnRango;
                  LOG.info(
                      "Se ha encontrado tramite dentro del rango 5 años. Fecha más antigua usada: {} - Tramite: {}",
                      fechaMasAntiguaTraIninterrumpido, tramiteEnRango.getTramite());
                  while (it.nextIndex() <= indiceEncontrado) {
                    it.next();
                  }
                  fechaMasAntigua = fechaMasAntiguaEnRango;
                  periodoEntreTramites = periodoTramitesDefault;
                }
              } else {
                tramiteInterrumpido = true;
              }
            }
            // Comprobamos el siguiente trámite (sin consumirlo) para ver si existe una
            // interrupción.
            if (it.hasNext() && indiceEncontrado >= 0) {

              int nextIndex = it.nextIndex(); // índice del siguiente elemento (no avanza el iterador)
              LOG.info("Busqueda del tramite ininterrumpido. Siguiente indice: {}", nextIndex);

              // Si el siguiente trámite (o varios consecutivos) tienen fechaValidez == null,
              // calculamos el indice dentro de ese bloque donde está la fechaMasAntigua más
              // antigua y usaremos ese trámite (y su fechaMasAntigua) para las comprobaciones
              // posteriores.
              if (listaTramitesInformeDgp.get(nextIndex).getFechaValidez() == null) {
                LOG.info("El siguiente tramite tampoco cuenta con f.Validez. "
                    + "Se procede a averiguar el tramite más antiguo entre los que no cuentan con f.Validez");

                int indiceMasAntiguoSinValidez = encontrarMasAntiguoEntreSinValidez(listaTramitesInformeDgp, nextIndex);

                // Determinar primer índice posterior al bloque de nulls (primer trámite con
                // fechaValidez != null)
                LOG.info("Buscamos primer tramite que cuente con f.Validez");
                int indiceTramiteConValidez = nextIndex;
                while (indiceTramiteConValidez < listaTramitesInformeDgp.size()
                    && listaTramitesInformeDgp.get(indiceTramiteConValidez).getFechaValidez() == null) {
                  indiceTramiteConValidez++;
                }
                LOG.info("Indice encontrado {}.", indiceTramiteConValidez);

                // Si hemos encontrado un trámite con fechaMasAntigua válida dentro del bloque,
                // usamos su fecha como fechaParaComparar; si no, fallback a la fechaMasAntigua
                // del trámite actual.
                Date fechaParaComparar = fechaMasAntigua;
                if (indiceMasAntiguoSinValidez >= 0) {
                  LOG.info("Existe tramite sin validez más antiguo");
                  ExpedienteInformeDgpTramiteDto tramiteMasAntiguo = listaTramitesInformeDgp
                      .get(indiceMasAntiguoSinValidez);
                  Date fechaMasAntiguaCandidato = fechaMasAntiguaTramite(tramiteMasAntiguo.getFechaSolicitud(),
                      tramiteMasAntiguo.getFechaConcesion());
                  if (fechaMasAntiguaCandidato != null) {
                    fechaParaComparar = fechaMasAntiguaCandidato;
                  }
                  // Guardamos la fecha más antigua del tramo ininterrumpido actual
                  fechaMasAntiguaTraIninterrumpido = fechaParaComparar;
                  LOG.info(MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA, fechaMasAntiguaTraIninterrumpido);
                }

                // Avanzamos el iterador hasta indiceTramiteConValidez (primer no-null o fin)
                // para omitir el resto de elementos sin fechaValidez para futuras iteraciones.
                it = avanceInterador(it, indiceTramiteConValidez);

                ExpedienteInformeDgpTramiteDto siguienteTramite = indiceTramiteConValidez < listaTramitesInformeDgp
                    .size() ? listaTramitesInformeDgp.get(indiceTramiteConValidez) : null;

                Date fValidezSigTramite = siguienteTramite == null ? null : siguienteTramite.getFechaValidez();
                Date fMasAntSigTramite = fechaMasAntiguaTramite(siguienteTramite.getFechaConcesion(),
                    siguienteTramite.getFechaSolicitud());
                tramiteInterrumpido = !verificarPeriodoEntreTramitesTemporales(fechaParaComparar, fValidezSigTramite,
                    fMasAntSigTramite, periodoEntreTramites);
                LOG.info("Se verifica si el periodo ha sido interrumpido. Interrupción {}", tramiteInterrumpido);

                LOG.info(MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA, fechaMasAntiguaTraIninterrumpido);

                // Comportamiento original si el siguiente trámite tiene fechaValidez
              } else {

                LOG.info("Encontrado Siguiente indice valido. {} - {} ", nextIndex,
                    listaTramitesInformeDgp.get(nextIndex).getTramite());

                ExpedienteInformeDgpTramiteDto siguienteTramite = listaTramitesInformeDgp.get(nextIndex);
                Date fValidezSigTramite = siguienteTramite == null ? null : siguienteTramite.getFechaValidez();
                Date fMasAntSigTramite = fechaMasAntiguaTramite(siguienteTramite.getFechaConcesion(),
                    siguienteTramite.getFechaSolicitud());
                tramiteInterrumpido = !verificarPeriodoEntreTramitesTemporales(fechaMasAntigua, fValidezSigTramite,
                    fMasAntSigTramite, periodoEntreTramites);
                LOG.info("Se verifica si el periodo ha sido interrumpido. {}", tramiteInterrumpido);

                // Guardamos la fecha más antigua del tramo ininterrumpido actual
                fechaMasAntiguaTraIninterrumpido = fechaMasAntigua;
                LOG.info(MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA, fechaMasAntiguaTraIninterrumpido);
              }
            } else {
              // No hay siguiente trámite; guardamos la fecha más antigua actual del tramo
              fechaMasAntiguaTraIninterrumpido = fechaMasAntigua;
              LOG.info(MESSAGE_FECHA_MAS_ANTIGUA_GUARDADA, fechaMasAntiguaTraIninterrumpido);
            }
          }
        }
      }
      if (fechaMasAntiguaTraIninterrumpido != null) {
        periodoIninterrumpido = calculoPeriodo(fechaMasAntiguaTraIninterrumpido, fechaEfectos);
        LOG.info("Se realiza el cálculo del periodo legal ininterrumpido -> {}", periodoIninterrumpido);
      }
    }
    LOG.debug("Método Cálculo Periodo Residencia ininterrumpido - End");
    return periodoIninterrumpido;
  }

  /**
   * Metodo que comprueba si existe un tramite permanente con fecha de validez.
   * 
   * @param periodoBloqueado
   * @param tramite
   * @return
   */
  private boolean comprobacionTramitePermanenteBloqueado(boolean periodoBloqueado,
      ExpedienteInformeDgpTramiteDto tramite) {
    if ((tramite.getTramite().contains(PERMA) || tramite.getTramite().contains(LARGA_DURACI))
        && tramite.getFechaValidez() != null && !periodoBloqueado) {
      LOG.info(
          "Se ha encontrado un tramite Permanente con fecha validez{}. El periodo entre tramites se queda Bloqueado en {} meses.",
          tramite.getTramite(), periodoTramitesDefault);
      periodoBloqueado = true;
    }
    return periodoBloqueado;
  }

  /**
   * Método que permite avanzar el iterador hasta la posicion que se le pasa.
   * 
   * @param iterador
   * @param posicionAvance
   * @return
   */
  private ListIterator<ExpedienteInformeDgpTramiteDto> avanceInterador(
      ListIterator<ExpedienteInformeDgpTramiteDto> iterador, int posicionAvance) {
    LOG.debug("Avance del iterador a la posicion {} - Init", posicionAvance);
    while (iterador.nextIndex() < posicionAvance) {
      if (iterador.hasNext()) {
        iterador.next();
      } else {
        break;
      }
    }
    LOG.debug("Avance del iterador a la posicion {} - End", posicionAvance);
    return iterador;
  }

  /**
   * Helper que busca, a partir de startIndex, el bloque consecutivo de trámites
   * cuya fechaValidez == null y devuelve la fechaMasAntigua (entre fechaSolicitud
   * y fechaConcesion) más antigua de todo ese bloque. Si no encuentra ninguna
   * fechaMasAntigua válida devuelve null.
   *
   * @param listaAux
   * @param startIndex
   * @return
   */
  private int encontrarMasAntiguoEntreSinValidez(List<ExpedienteInformeDgpTramiteDto> listaAux, int startIndex) {
    LOG.debug("Encontrar indice más antiguo entre los tramites sin f.Validez - Init");
    if (listaAux == null || startIndex < 0 || startIndex >= listaAux.size()) {
      LOG.warn("La listaAux es nula - [{}] o el indice de inicio es inferior a 0 o superior al tamaño de la lista - {}",
          listaAux, startIndex);
      return -1;
    }
    Date earliest = null;
    int earliestIndex = -1;
    int i = startIndex;
    while (i < listaAux.size() && listaAux.get(i).getFechaValidez() == null) {
      ExpedienteInformeDgpTramiteDto candidato = listaAux.get(i);
      LOG.info("Tramite actual -> {}", candidato.getTramite());
      Date fechaMasAntiguaCandidato = fechaMasAntiguaTramite(candidato.getFechaSolicitud(),
          candidato.getFechaConcesion());
      if (fechaMasAntiguaCandidato != null && (earliest == null || fechaMasAntiguaCandidato.before(earliest))) {
        earliest = fechaMasAntiguaCandidato;
        earliestIndex = i;
      }
      i++;
    }
    LOG.info("Encontrado tramite más antiguo sin fecha de validez entre los que estan en la lista - {} posición {}",
        listaAux.get(earliestIndex), earliestIndex);
    LOG.debug("Encontrar indice más antiguo entre los tramites sin f.Validez - End");
    return earliestIndex;
  }

  /**
   * Identifica cual es la fecha más antigua de un trámite.
   * 
   * @param fechaSolicitud
   * @param fechaConcesion
   * @return
   */
  private Date fechaMasAntiguaTramite(Date fechaSolicitud, Date fechaConcesion) {
    LOG.debug("Fecha más antigua de un tramite entre {} y {} - Init", fechaSolicitud, fechaConcesion);

    Date fechaMasAntigua;
    if (fechaConcesion == null) {
      fechaMasAntigua = null;
    } else if (fechaSolicitud == null) {
      fechaMasAntigua = fechaConcesion;
    } else {
      fechaMasAntigua = fechaConcesion.before(fechaSolicitud) ? fechaConcesion : fechaSolicitud;
    }

    LOG.info("Fecha más antigua del tramite actual entre Fecha Solicitud y Fecha Concesion. {}", fechaMasAntigua);
    LOG.debug("Fecha más antigua de un tramite entre {} y {} - End", fechaSolicitud, fechaConcesion);
    return fechaMasAntigua;
  }

  /**
   * Verifica si el periodo entre tramites temporales es el indicado por la
   * variable periodoTramitesDefault
   * 
   * @param fechaMasAntiguaTraActual
   * @param fechaValidezSigTra
   * @param periodoEntreTramites
   * @return
   */
  private boolean verificarPeriodoEntreTramitesTemporales(Date fechaMasAntiguaTraActual, Date fechaValidezSigTra,
      Date fechaMasAntiguaSigTra, int periodoEntreTramites) {
    LOG.debug("VerificarPeriodoEntreTramitesTemporales - Init");
    LOG.info("Verificacion del periodo entre tramites Temporales. Fechas: {} y {}. Periodo exigido: {}",
        fechaMasAntiguaTraActual, fechaValidezSigTra, periodoEntreTramites);

    if (fechaMasAntiguaTraActual == null || fechaValidezSigTra == null) {
      LOG.info("Fechas nulas en verificarPeriodoEntreTramitesTemporales. fechaMasAntigua={} fechaValidez={}",
          fechaMasAntiguaTraActual, fechaValidezSigTra);
      return false;
    }

    if (periodoEntreTramites < 0) {
      LOG.warn("Periodo entre tramites negativo: {}. Se devuelve false.", periodoEntreTramites);
      return false;
    }

    Calendar fechaMasAntiguaActualAux = Calendar.getInstance();
    fechaMasAntiguaActualAux.setTime(fechaMasAntiguaTraActual);
    fechaMasAntiguaActualAux.set(Calendar.HOUR_OF_DAY, 0);
    fechaMasAntiguaActualAux.set(Calendar.MINUTE, 0);
    fechaMasAntiguaActualAux.set(Calendar.SECOND, 0);
    fechaMasAntiguaActualAux.set(Calendar.MILLISECOND, 0);

    Calendar fechaValidezSiguienteAux = Calendar.getInstance();
    fechaValidezSiguienteAux.setTime(fechaValidezSigTra);
    fechaValidezSiguienteAux.set(Calendar.HOUR_OF_DAY, 0);
    fechaValidezSiguienteAux.set(Calendar.MINUTE, 0);
    fechaValidezSiguienteAux.set(Calendar.SECOND, 0);
    fechaValidezSiguienteAux.set(Calendar.MILLISECOND, 0);

    Calendar fechaMasAntiguaSiguienteAux = null;
    if (fechaMasAntiguaSigTra != null) {
      fechaMasAntiguaSiguienteAux = Calendar.getInstance();
      fechaMasAntiguaSiguienteAux.setTime(fechaMasAntiguaSigTra);
      fechaMasAntiguaSiguienteAux.set(Calendar.HOUR_OF_DAY, 0);
      fechaMasAntiguaSiguienteAux.set(Calendar.MINUTE, 0);
      fechaMasAntiguaSiguienteAux.set(Calendar.SECOND, 0);
      fechaMasAntiguaSiguienteAux.set(Calendar.MILLISECOND, 0);
    }

    if (fechaMasAntiguaSigTra != null && fechaMasAntiguaSiguienteAux != null
        && (fechaMasAntiguaActualAux.compareTo(fechaValidezSiguienteAux) <= 0)
        && (fechaMasAntiguaActualAux.compareTo(fechaMasAntiguaSiguienteAux) >= 0)) {
      LOG.info("Tramite solapado");
      return true;
    }

    if (!(fechaValidezSiguienteAux.compareTo(fechaMasAntiguaActualAux) <= 0)) {
      LOG.info("FechaValidez no es anterior o igual a fechaMasAntigua. fechaValidez={} fechaMasAntigua={}",
          fechaValidezSiguienteAux.getTime(), fechaMasAntiguaActualAux.getTime());
      return false;
    }
    Calendar limiteInferior = (Calendar) fechaMasAntiguaActualAux.clone();
    limiteInferior.add(Calendar.MONTH, -periodoEntreTramites);

    boolean resultado = (limiteInferior.compareTo(fechaValidezSiguienteAux) <= 0)
        && (fechaMasAntiguaActualAux.compareTo(fechaValidezSiguienteAux) >= 0);

    LOG.info("Verificacion del periodo entre tramites Temporales. Resultado:  {}", resultado);
    LOG.debug("VerificarPeriodoEntreTramitesTemporales - End");
    return resultado;
  }

  /**
   * Metodo que realiza el calculo del periodo a partir de la fecha de solicitud
   * del expediente y la del trámite más antiguo ininterrumpido que se haya
   * encontrado
   * 
   * @param fechaTramite
   * @param fechaExp
   * @return
   */
  private String calculoPeriodo(Date fechaTramite, Date fechaExp) {
    LOG.debug("calculoPeriodo - Init");

    ZoneId zona = ZoneId.systemDefault();

    LocalDate fechaTramiteAux = Instant.ofEpochMilli(fechaTramite.getTime()).atZone(zona).toLocalDate();
    LocalDate fechaExpAux = Instant.ofEpochMilli(fechaExp.getTime()).atZone(zona).toLocalDate();

    LocalDate inicio = fechaExpAux.isBefore(fechaTramiteAux) ? fechaExpAux : fechaTramiteAux;
    LocalDate fin = fechaExpAux.isBefore(fechaTramiteAux) ? fechaTramiteAux : fechaExpAux;

    Period periodo = Period.between(inicio, fin);

    int anos = Math.abs(periodo.getYears());
    int meses = Math.abs(periodo.getMonths());
    int dias = Math.abs(periodo.getDays());

    LOG.info("CalculoPeriodo = {} Años, {} Meses, {} Dias", anos, meses, dias);
    LOG.debug("calculoPeriodo - End");
    return String.format("%d Años %d Meses %d Dias", anos, meses, dias);
  }

  /**
   * Método utilizado para hallar el tramite más antiguo encontrado en los años
   * estipulados anteriores, saltandose todos los tramites sin fecha de validez.
   * (tiene que pasarse el trámite mas antiguo antes de realizar este método)
   * 
   * @param indiceTramite
   * @param listaAux
   * @param tramitePermanente
   * @return
   */
  private int buscarIndiceTramiteRangoPermanentes(int indiceTramite, List<ExpedienteInformeDgpTramiteDto> listaAux,
      ExpedienteInformeDgpTramiteDto tramitePermanente) {
    LOG.debug("Buscar indice del tramite en un rango de {} años antes del tramite Permanente - Init",
        periodoTramitesPermanente / 12);
    int ultimoIndexEncontrado = -1;
    if (tramitePermanente == null) {
      LOG.warn("Indice {}, tramite permanente {} no encontrado", indiceTramite, tramitePermanente);
      return -1;
    }
    Date fechaAntiguaTramitePermanente = fechaMasAntiguaTramite(tramitePermanente.getFechaSolicitud(),
        tramitePermanente.getFechaConcesion());
    if (fechaAntiguaTramitePermanente != null) {
      Calendar fechaTPCal = Calendar.getInstance();
      fechaTPCal.setTime(fechaAntiguaTramitePermanente);

      Calendar aniosAntesCal = (Calendar) fechaTPCal.clone();
      aniosAntesCal.add(Calendar.YEAR, -(periodoTramitesPermanente / 12));
      Calendar mesesAntesCal = (Calendar) aniosAntesCal.clone();
      mesesAntesCal.add(Calendar.YEAR, -(periodoTramitesPermanente % 12));

      aniosAntesCal.set(Calendar.HOUR_OF_DAY, 0);
      aniosAntesCal.set(Calendar.MINUTE, 0);
      aniosAntesCal.set(Calendar.SECOND, 0);
      aniosAntesCal.set(Calendar.MILLISECOND, 0);

      Date fechaAniosAntes = aniosAntesCal.getTime();

      LOG.info("buscarTramiteRango5Anios - Rango desde {} hasta {}", fechaAniosAntes, fechaAntiguaTramitePermanente);

      LOG.info("buscarTramiteRango5Anios - Rango desde {} hasta {}", fechaAniosAntes, fechaAntiguaTramitePermanente);

      for (int i = indiceTramite; i < listaAux.size(); i++) {
        ExpedienteInformeDgpTramiteDto candidato = listaAux.get(i);
        Date fechaValidezCandidato = candidato.getFechaValidez();

        if (fechaValidezCandidato != null) {
          if (!fechaValidezCandidato.before(fechaAniosAntes)
              && !fechaValidezCandidato.after(fechaAntiguaTramitePermanente)) {
            ultimoIndexEncontrado = i;
            LOG.debug("Nuevo índice encontrado {}: trámite {}", i, candidato.getTramite());
          }
        } else {
          LOG.debug("Índice {}: trámite {} sin fecha de validez, se omite", i, candidato.getTramite());
        }
      }
    }
    if (ultimoIndexEncontrado == -1) {
      LOG.warn("No se ha encontrado tramite dentro del rango");
      return ultimoIndexEncontrado;
    }
    LOG.info("Encontrado ultimo tramite en el rango de {} años {}", periodoTramitesPermanente / 12,
        listaAux.get(ultimoIndexEncontrado).getTramite());
    LOG.debug("Buscar indice del tramite en un rango de {} años antes del tramite Permanente - End",
        periodoTramitesPermanente / 12);
    return ultimoIndexEncontrado;
  }

  // VALIDACION SEMAFORO

  /**
   * Metodo que se encarga de hacer la validación de ResidenciaLegal a partir del
   * periodo ininterrumpido y el exigido
   * 
   * @param item
   */
  private void validacionSemaforoResidenciaLegal(ExpedienteInformeDto item) {
    LOG.info("Validacion Semaforo Residencia Legal - Init");
    Integer periodoExigido = item.getExpedienteInformeDgpDto().getPeriodoExigido();
    String periodoIninterrumpido = item.getExpedienteInformeDgpDto().getPeriodoIninterrumpido();

    if (periodoExigido != null && periodoExigido > 0 && periodoIninterrumpido != null
        && !periodoIninterrumpido.trim().isEmpty()) {
      if (!"INDEFINIDO".equalsIgnoreCase(periodoIninterrumpido.trim())) {

        int aniosIninterrumpidos = parseAniosIninterrumpidos(periodoIninterrumpido);

        LOG.info("Años de Periodo Ininterrumpido parseados: {}", aniosIninterrumpidos);

        if (aniosIninterrumpidos >= periodoExigido) {
          LOG.info("El Periodo Ininterrumpido ({} años) es mayor o igual al Periodo Exigido: {}", aniosIninterrumpidos,
              periodoExigido);
          expedientesFacade.updateValidacionSemaforo(item.getExpedienteDto().getIdExp(), VAL_RLE, "VRLE-CPL");
        } else {
          LOG.info("El Periodo Ininterrumpido ({} años) es menor al Periodo Exigido: {}", aniosIninterrumpidos,
              periodoExigido);
          expedientesFacade.updateValidacionSemaforo(item.getExpedienteDto().getIdExp(), VAL_RLE, "VRLE-NCU");
        }

      } else {
        LOG.info("El Periodo Ininterrumpido es {}", periodoIninterrumpido);
        expedientesFacade.updateValidacionSemaforo(item.getExpedienteDto().getIdExp(), VAL_RLE, "VRLE-NCU");
      }
    } else {
      LOG.info("No se han encontrado datos; Periodo Ininterrumpido: {} - Periodo Exigido: {}", periodoIninterrumpido,
          periodoExigido);
      expedientesFacade.updateValidacionSemaforo(item.getExpedienteDto().getIdExp(), VAL_RLE, "VRLE-SDT");
    }
    LOG.info("Validacion Semaforo Residencia Legal - End");
  }

  private int parseAniosIninterrumpidos(String periodoIninterrumpido) {
    int aniosIninterrumpidos = 0;
    try {
      int posAnios = periodoIninterrumpido.indexOf("Años");
      if (posAnios > 0) {
        String aniosStr = periodoIninterrumpido.substring(0, posAnios).trim();
        aniosIninterrumpidos = Integer.parseInt(aniosStr);
      } else {
        LOG.warn("PeriodoIninterrumpido es INDEFINIDO o no se ha encontrado datos: {}", periodoIninterrumpido);
      }
    } catch (NumberFormatException e) {
      LOG.error("Error parseando años del periodoIninterrumpido: {}", periodoIninterrumpido, e);
    }
    return aniosIninterrumpidos;
  }

}
