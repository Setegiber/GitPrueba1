package es.mjusticia.sinac.core.business.service;

/*-
 * #%L
 * sinac-core
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

import java.util.List;

import es.mjusticia.sinac.core.model.dto.InformesDgpRecibidosDto;

public interface InformesService {

    void saveInformeDgpRecibidoEntity(String numExp, String tipoPeticion, String fechaAlta, String codEstado);
    public List<InformesDgpRecibidosDto> findAllInformesDgpRecibidosEntityNoProcesados();
    public void updateEstadoInformeDgpRecibido(InformesDgpRecibidosDto dtoToUpdate, String codEstado);
    public InformesDgpRecibidosDto findByNumExpAndFechaAlta(String numpExp, String fechaAlta);

}
