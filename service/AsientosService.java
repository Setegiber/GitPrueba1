package es.mjusticia.sinac.core.business.service;

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

import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.AsientoErrorDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;

import java.math.BigInteger;
import java.util.List;

public interface AsientosService {

  void saveAsiento(AsientoDto asientoDto, UsuarioDto usuarioDto);

  void setExpDocJusticante(AsientoDto asientoDto, ExpedienteDocumentoDto expDocJusticante, UsuarioDto usuarioDto);

  void saveAsientoError(AsientoErrorDto asientoErrorDto);

  List<AsientoDto> getAsientosEnCurso();

  AsientoDto getAsientoConJustificante(BigInteger idAsiento);

  BigInteger findIdExpFromAsiento(AsientoDto asientoDto);

  List<AsientoDto> findByIdExpDoc(BigInteger idExpDoc);

}
