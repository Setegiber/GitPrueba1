package es.mjusticia.pagojus.frontconsola.enums;

/*-
 * #%L
 * pagojus-restws
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

public enum RolesConsola {
  ROL_VENTANILLA("VENTANILLA"), ROL_ADMINISTRADOR("ADMIN");

  private String rol;

  RolesConsola(String rol) {
    this.rol = rol;
  }

  public String getRol() {
    return rol;
  }

  public static boolean isRolConsola(String test) {
    test = test.toUpperCase();
    for (RolesConsola rol : RolesConsola.values()) {
      if (rol.getRol().equals(test)) {
        return true;
      }
    }
    return false;
  }
}
