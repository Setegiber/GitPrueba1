package es.mjusticia.sinac.core.business.plantillas;

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

public enum ClasificacionEnum {

  EDAD_ANALFABETISMO_MAYOR("EDAD_ANALFABETISMO_MAYOR") {
    @Override
    public String getPlantilla() {
      return "revision";
    }
  },
  INFORMES_PRECEPTIVOS("INF") {
    @Override
    public String getPlantilla() {
      return "informes-preceptivos";
    }
  },
  PROPUESTA_ACUERDO_CONSEJO_MINISTROS("PRE") {
    @Override
    public String getPlantilla() {
      return "propuesta-acuerdo-consejo-ministros";
    }
  },
  RESOLUCION("RES") {
    @Override
    public String getPlantilla() {
      return "resolucion";
    }
  },
  COMUNICACION_DE_CONCESION("COM") {
    @Override
    public String getPlantilla() {
      return "comunicacion-de-concesion";
    }
  },
  CIERRE("CIE") {
    @Override
    public String getPlantilla() {
      return "cierre";
    }
  },
  ARCHIVO("ARC") {
    @Override
    public String getPlantilla() {
      return "archivo";
    }
  },
  RECURSO("RRO") {
    @Override
    public String getPlantilla() {
      return "recurso";
    }
  },
  GESTIONES_PREVIAS("GPV") {
    @Override
    public String getPlantilla() {
      return "gestionesPrevias";
    }
  },
  PROPUESTA_REVISION("PRV") {
    @Override
    public String getPlantilla() {
      return "propuestaRevision";
    }
  },
  ACUERDO_INCOACION_LE("ACI") {
    @Override
    public String getPlantilla() {
      return "acuerdoIncoacionLE";
    }
  },
  REMISION_RESOLUCION_CM("RCM") {
    @Override
    public String getPlantilla() {
      return "remisionResolucionCM";
    }
  },
  OFICIO_SENTENCIA("OSE") {
    @Override
    public String getPlantilla() {
      return "oficioSentencia";
    }
  };

  private String codTramite;

  ClasificacionEnum(String codTramite) {
    this.codTramite = codTramite;
  }

  public static ClasificacionEnum fromCodTramite(String codTramite) {
    for (ClasificacionEnum tramiteEnum : ClasificacionEnum.values()) {
      if (tramiteEnum.codTramite.equals(codTramite)) {
        return tramiteEnum;
      }
    }
    return null;
  }

  public String getCodTramite() {
    return codTramite;
  }

  public abstract String getPlantilla();

}
