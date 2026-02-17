package es.mjusticia.sinac.core.business.service;

import java.util.List;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;

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

public interface CatalogosService {

  LdvMaestraDto getCatalogoById(int id) throws SinacException;

  LdvMaestraDto getCatalogoByCod(String cod) throws SinacException;

  List<LdvMaestraDto> getComboLdvMaestraByLdvEntidadMaestraCod(String cod) throws SinacException;

  List<LdvMaestraDto> getAllLdvMaestraDto();

  /**
   * Recoge un objeto LdvMaestraDto a partir del nombre y el codLdvEntMae
   * 
   * @param nomLdv
   * @param codLdvEntMae
   * @return
   * @throws SinacException
   */
  LdvMaestraDto getCatalogoByNomAndLdvEntidadMaestraCod(String nomLdv, String codLdvEntMae) throws SinacException;

  /**
   * Obtiene el codigo de LdvEntidadesMaestras a partir de codLdvMae
   * 
   * @param codVal
   * @throws SinacException
   */
  String getCodLdvEntMaeByCodLdvMae(String codVal) throws SinacException;
  
  Integer getIdLdvByCodLdvMae (String codLdv) throws SinacException;

}
