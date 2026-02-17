package es.mjusticia.sinac.core.business.service;

import java.util.List;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.entity.FormularioCamposValidaEntity;

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

public interface FormularioCamposValidaService {

  List<FormularioCamposValidaDto> getFormularioCamposValidaListaByIdProcedimiento(short id) throws SinacException;

  List<FormularioCamposValidaDto> getFormularioCamposValidaListaSinExpedienteByIdProcedimiento(short idProcedimiento)
      throws SinacException;;

  List<FormularioCamposValidaDto> findAll() throws SinacException;

  FormularioCamposValidaEntity findFormularioCamposValidaEntityByCodigo(String codigo);

}
