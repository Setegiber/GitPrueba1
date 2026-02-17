package es.mjusticia.sinac.core.business.plantillas;

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

@Component
public class EvaluadorClasificacionFactory {

  private static final Logger LOG = LoggerFactory.getLogger(EvaluadorClasificacionFactory.class);

  private static final String EDAD_ANALFABETISMO_MAYOR = "EDAD_ANALFABETISMO_MAYOR";
  private static final String EDAD_ANALFABETISMO_MENOR = "EDAD_ANALFABETISMO_MENOR";

  // Motivo Resolucion CN
  private static final String MOT_SOL_BRIGADISTAS = "BRIGADISTAS";
  private static final String MOT_SOL_CN = "MOTIVO_CN";

  // Motivo Resolucion DIC
  private static final String MOT_SOL_ANALFABETISMO = "ANALFABETISMO";
  private static final String MOT_SOL_DIF_APRENDIZAJE = "DIFICULTADES_APRENDIZAJE";
  private static final String MOT_SOL_ESO = "ESO";

  // Verificar que acción cerrar expediente este ejecutada.
  private static final String ACC_CERRAR_EXP_EJECUTADA = "ACC_CERRAR_EXP_EJECUTADA";

  // Verificar que la acción GEND despues de cerrar expediente no este ejecutada
  private static final String ACC_GEND_EXPC_NO_EJECUTADA = "ACC_GEND_EXPC_NO_EJECUTADA";

  private static final String RESOLUCION_RPOS = "RESOLUCION_RPOS";
  private static final String RESOLUCION_RNDE = "RESOLUCION_RNDE";
  private static final String RESOLUCION_RNED = "RESOLUCION_RNED";
  private static final String RESOLUCION_RNEP = "RESOLUCION_RNEP";
  private static final String RESOLUCION_RNEG = "RESOLUCION_RNEG";
  private static final String RESOLUCION_RPDO = "RESOLUCION_RPDO";
  private static final String RESOLUCION_RPAI = "RESOLUCION_RPAI";
  private static final String RESOLUCION_RPAD = "RESOLUCION_RPAD";
  private static final String RESOLUCION_RADE = "RESOLUCION_RADE";
  private static final String RESOLUCION_RADP = "RESOLUCION_RADP";
  private static final String RESOLUCION_REST = "RESOLUCION_REST";
  private static final String RESOLUCION_SINR = "RESOLUCION_SINR";
  private static final String RESOLUCION_ESOP = "RESOLUCION_ESOP";
  // Resolucion Error Material
  private static final String RESOLUCION_RERR = "RESOLUCION_RERR";
  private static final String RESOLUCION_MSER = "RESOLUCION_MSER";
  // Resolucion Ineficacia
  private static final String RESOLUCION_RABC = "RESOLUCION_RABC";
  private static final String RESOLUCION_RINE = "RESOLUCION_RINE";
  private static final String RESOLUCION_RACP = "RESOLUCION_RACP";
  // Resolucion Revocacion
  private static final String RESOLUCION_RRVC = "RESOLUCION_RRVC";
  private static final String RESOLUCION_RRRA = "RESOLUCION_RRRA";
  // Interposicion Resolucion Error Material
  private static final String INTERPOSICION_DIC = "INTERPOSICION_DIC";
  private static final String INTERPOSICION_RES = "INTERPOSICION_RES";
  private static final String INTERPOSICION_SEF = "INTERPOSICION_SEF";

  // Estado del expediente
  private static final String RESOLUCION_NOTIFICADA = "RESOLUCION_NOT";

  // CARTA NATURALEZA
  private static final String RESOLUCION_REXP_OCRD_CN = "RESOLUCION_REXP_OCRD_CN";

  // RECUROS REPOSICION DR
  private static final String RESOLUCION_INFC_RES_CON = "RESOLUCION_INFC_RES_CON";
  private static final String RESOLUCION_INFC_RES_DEN = "RESOLUCION_INFC_RES_DEN";
  private static final String RESOLUCION_PROPUESTA_CON = "RESOLUCION_PROPUESTA_CON";
  private static final String RESOLUCION_PROPUESTA_DEN = "RESOLUCION_PROPUESTA_DEN";
  private static final String NUNCA_CUMPLE = "NUNCA_CUMPLE";
  private static final String EXISTE_SUSPENSION_INFORMES = "EXISTE_SUSPENSION_INFORMES";

  // RESOLUCIONES GENERICAS
  private static final String RESOLUCION_ESTIMATORIA_GEN = "RESOLUCION_ESTIMATORIA_GEN";
  private static final String RESOLUCION_DESESTIMATORIA_GEN = "RESOLUCION_DESESTIMATORIA_GEN";
  private static final String RESOLUCION_PRUEBA_ADAP_GEN = "RESOLUCION_PRUEBA_ADAP_GEN";
  private static final String RESOLUCION_DESES_PRESUN_GEN = "RESOLUCION_DESES_PRESUN_GEN";

  @Autowired
  private EdadMayorClasificacion edadMayorClasificacion;
  @Autowired
  private EdadMenorClasificacion edadMenorClasificacion;
  @Autowired
  private MotivoSolBrigadistasClasificacion motivoSolBrigadistasClasificacion;
  @Autowired
  private MotivoSolGenericoCnClasificacion motivoSolGenericoCnClasificacion;
  @Autowired
  private MotivoSolAnalfabetismoClasificacion motivoSolAnalfabetismoClasificacion;
  @Autowired
  private MotivoSolDificultadesAprendizajeClasificacion motivoSolDificultadesAprendizajeClasificacion;
  @Autowired
  private MotivoSolEsoClasificacion motivoSolEsoClasificacion;
  @Autowired
  private ResolucionDesaparicionObjetoClasificacion resolucionDesaparicionObjetoClasificacion;
  @Autowired
  private ResolucionDesistimientoExpresoClasificacion resolucionDesistimientoExpresoClasificacion;
  @Autowired
  private ResolucionDesistimientoPresuntoClasificacion resolucionDesistimientoPresuntoClasificacion;
  @Autowired
  private ResolucionInadmisionClasificacion resolucionInadmisionClasificacion;
  @Autowired
  private ResolucionNegativaClasificacion resolucionNegativaClasificacion;
  @Autowired
  private ResolucionPositivaClasificacion resolucionPositivaClasificacion;
  @Autowired
  private ResolucionPruebaAdaptadaClasificacion resolucionPruebaAdaptadaClasificacion;
  @Autowired
  private ResolucionEstimatoriaParcialClasificacion resolucionEstimatoriaParcialClasificacion;
  @Autowired
  private ResolucionEstimatoriaClasificacion resolucionEstimatoriaClasificacion;
  @Autowired
  private ResolucionDesestimatoriaClasificacion resolucionDesestimatoriaClasificacion;
  @Autowired
  private ResolucionEstimatoriaPorDesistimientoDesaparicionClasificacion resolucionEstimatoriaPorDesistimientoDesaparicionClasificacion;
  @Autowired
  private ResolucionConErrorClasificacion resolucionConErrorClasificacion;
  @Autowired
  private EscritoRespuestaSinErrorClasificacion escritoRespuestaSinErrorClasificacion;
  @Autowired
  private ResolucionSinMalaConductaClasificacion resolucionSinMalaConductaClasificacion;
  @Autowired
  private ResolucionRevocacionConcesionClasificacion resolucionRevocacionConcesionClasificacion;
  @Autowired
  private ResolucionRevocacionRetroactuacionClasificacion resolucionRevocacionRetroactuacionClasificacion;
  @Autowired
  private ResolucionIneficaciaClasificacion resolucionIneficaciaClasificacion;
  @Autowired
  private ResolucionCaducidadPlazosClasificacion resolucionCaducidadPlazosClasificacion;
  @Autowired
  private InterposicionDicClasificacion interposicionDicClasificacion;
  @Autowired
  private InterposicionResidenciaClasificacion interposicionResidenciaClasificacion;
  @Autowired
  private InterposicionSefClasificacion interposicionSefClasificacion;
  @Autowired
  private SinResolucionClasificacion sinResolucionClasificacion;
  @Autowired
  private ResolucionEstimatoriaOEstimatoriaParicalClasificacion resolucionEstimatoriaOEstimatoriaParicalClasificacion;
  @Autowired
  private ResolucionNotificadaClasificacion resolucionNotificadaClasificacion;
  @Autowired
  private ResolucionAcuerdoConsejoMinistrosClasificacion resolucionAcuerdoConsejoMinistrosClasificacion;
  @Autowired
  private ResolucionInformeClasAndResolucionConClasificacion resolucionInformeClasAndResolucionConClasificacion;
  @Autowired
  private ResolucionInformeClasAndResolucionDenClasificacion resolucionInformeClasAndResolucionDenClasificacion;
  @Autowired
  private ResolucionPropuestaResConcesionClasificacion resolucionPropuestaResConcesionClasificacion;
  @Autowired
  private ResolucionPropuestaResDenegacionClasificacion resolucionPropuestaResDenegacionClasificacion;
  @Autowired
  private AccionCerrarExpedienteEjecutadaClasificacion accionCerrarExpedienteEjecutadaClasificacion;
  @Autowired
  private AccionExpCerradoDocGeneradoClasificacion accionExpCerradoDocGeneradoClasificacion;
  @Autowired
  private NuncaCumpleClasificacion nuncaCumpleClasificacion;
  @Autowired
  private ExisteSuspensionInformesClasificacion existeSuspensionInformesClasificacion;
  @Autowired
  private ResolucionEstimatoriaGenericaClasificacion resolucionEstimatoriaGenericaClasificacion;
  @Autowired
  private ResolucionDesestimatoriaGenericaClasificacion resolucionDesestimatoriaGenericaClasificacion;
  @Autowired
  private ResolucionPruebaAdaptadaGenericaClasificacion resolucionPruebaAdaptadaGenericaClasificacion;
  @Autowired
  private ResolucionDesestimatoriaPresuntoGenericaClasificacion resolucionDesestimatoriaPresuntoGenericaClasificacion;

  private EvaluadorClasificacionFactory() {
    super();
  }

  public EvaluadorClasificacion getEvaluador(String tipoClasificacion) {
    switch (tipoClasificacion) {
    case EDAD_ANALFABETISMO_MAYOR:
      return edadMayorClasificacion;
    case EDAD_ANALFABETISMO_MENOR:
      return edadMenorClasificacion;
    case MOT_SOL_BRIGADISTAS:
      return motivoSolBrigadistasClasificacion;
    case MOT_SOL_CN:
      return motivoSolGenericoCnClasificacion;
    case MOT_SOL_ANALFABETISMO:
      return motivoSolAnalfabetismoClasificacion;
    case MOT_SOL_DIF_APRENDIZAJE:
      return motivoSolDificultadesAprendizajeClasificacion;
    case MOT_SOL_ESO:
      return motivoSolEsoClasificacion;
    case RESOLUCION_RPOS:
      return resolucionPositivaClasificacion;
    case RESOLUCION_RNDE:
      return resolucionDesestimatoriaClasificacion;
    case RESOLUCION_RNED:
      return resolucionEstimatoriaParcialClasificacion;
    case RESOLUCION_RNEP:
      return resolucionEstimatoriaPorDesistimientoDesaparicionClasificacion;
    case RESOLUCION_RNEG:
      return resolucionNegativaClasificacion;
    case RESOLUCION_RPDO:
      return resolucionDesaparicionObjetoClasificacion;
    case RESOLUCION_RPAI:
      return resolucionInadmisionClasificacion;
    case RESOLUCION_RPAD:
      return resolucionPruebaAdaptadaClasificacion;
    case RESOLUCION_RADE:
      return resolucionDesistimientoExpresoClasificacion;
    case RESOLUCION_RADP:
      return resolucionDesistimientoPresuntoClasificacion;
    case RESOLUCION_RERR:
      return resolucionConErrorClasificacion;
    case RESOLUCION_MSER:
      return escritoRespuestaSinErrorClasificacion;
    case RESOLUCION_RABC:
      return resolucionSinMalaConductaClasificacion;
    case RESOLUCION_RINE:
      return resolucionIneficaciaClasificacion;
    case RESOLUCION_RACP:
      return resolucionCaducidadPlazosClasificacion;
    case RESOLUCION_RRVC:
      return resolucionRevocacionConcesionClasificacion;
    case RESOLUCION_RRRA:
      return resolucionRevocacionRetroactuacionClasificacion;
    case INTERPOSICION_DIC:
      return interposicionDicClasificacion;
    case INTERPOSICION_RES:
      return interposicionResidenciaClasificacion;
    case INTERPOSICION_SEF:
      return interposicionSefClasificacion;
    case RESOLUCION_REST:
      return resolucionEstimatoriaClasificacion;
    case RESOLUCION_SINR:
      return sinResolucionClasificacion;
    case RESOLUCION_ESOP:
      return resolucionEstimatoriaOEstimatoriaParicalClasificacion;
    case RESOLUCION_NOTIFICADA:
      return resolucionNotificadaClasificacion;
    case RESOLUCION_REXP_OCRD_CN:
      return resolucionAcuerdoConsejoMinistrosClasificacion;
    case RESOLUCION_INFC_RES_CON:
      return resolucionInformeClasAndResolucionConClasificacion;
    case RESOLUCION_INFC_RES_DEN:
      return resolucionInformeClasAndResolucionDenClasificacion;
    case RESOLUCION_PROPUESTA_CON:
      return resolucionPropuestaResConcesionClasificacion;
    case RESOLUCION_PROPUESTA_DEN:
      return resolucionPropuestaResDenegacionClasificacion;
    case ACC_CERRAR_EXP_EJECUTADA:
      return accionCerrarExpedienteEjecutadaClasificacion;
    case ACC_GEND_EXPC_NO_EJECUTADA:
      return accionExpCerradoDocGeneradoClasificacion;
    case NUNCA_CUMPLE:
      return nuncaCumpleClasificacion;
    case EXISTE_SUSPENSION_INFORMES:
      return existeSuspensionInformesClasificacion;
    case RESOLUCION_ESTIMATORIA_GEN:
      return resolucionEstimatoriaGenericaClasificacion;
    case RESOLUCION_DESESTIMATORIA_GEN:
      return resolucionDesestimatoriaGenericaClasificacion;
    case RESOLUCION_PRUEBA_ADAP_GEN:
      return resolucionPruebaAdaptadaGenericaClasificacion;
    case RESOLUCION_DESES_PRESUN_GEN:
      return resolucionDesestimatoriaPresuntoGenericaClasificacion;

    default:
      LOG.error("El tipo de clasificación: {} no se encuentra entre las opciones configuradas en código",
          tipoClasificacion);
      return null;
    }
  }

}
