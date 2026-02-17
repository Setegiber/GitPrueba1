package es.mjusticia.sinac.core.business.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
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
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.ExpedienteAvisoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;

public interface AvisosService {

  Map<List<String>, LdvMaestraDto> obtenerProcedimientosPorLdvMaestra() throws SinacException;

  List<String> getAvisosExpediente(BigInteger idExpediente, ProcedimientoDto proDto, Integer idUsuario, Boolean isAdmin)
      throws SinacException;

  void cambiarEstadoAvisoExp(BigInteger idExpAvisos) throws SinacException;

  List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExpediente, Boolean isAdmin) throws SinacException;

  void registrarExpedienteAviso(BigInteger idExpediente, String codAviso) throws SinacException;

}
