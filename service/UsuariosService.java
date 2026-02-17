package es.mjusticia.sinac.core.business.service;

import java.util.List;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;

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

public interface UsuariosService {

  /**
   * Obtiene la Información del Usuario asociado al Usuario de Justicia
   * establecido como parámetro.
   *
   * @param usuarioJusticia Usuario de Justicia.
   * @return DTO con la Información del Usuario.
   * @throws SinacException Si se produce un error al obtener la Información del
   *                        Usuario.
   */
  UsuarioDto getUsuarioByUsuarioJusticia(final String usuarioJusticia) throws SinacException;

  List<UsuarioDto> getAllUsuarios() throws SinacException;


  List<String> getEmailsUsuariosByRol(String rol) throws SinacException;

  UsuarioDto getUsuario(final Integer idUsuario) throws SinacException;


}
