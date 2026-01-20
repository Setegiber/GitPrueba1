package es.mjusticia.pagojus.frontconsola.enums;

/*-
 * #%L
 * pagojus-frontconsola
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import es.mjusticia.pagojus.frontconsola.enums.EstadosPermitidos;
import es.mjusticia.pagojus.frontconsola.utils.Utils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RolesConsolaTest {

    @Test
    public void testGetRol() {
        // Verificar que el método getRol devuelve el valor correcto para cada rol
        assertEquals("VENTANILLA", RolesConsola.ROL_VENTANILLA.getRol());
        assertEquals("ADMIN", RolesConsola.ROL_ADMINISTRADOR.getRol());
    }

    @Test
    public void testIsRolConsola_ValidRoles() {
        // Verificar que isRolConsola devuelve true para roles válidos
        assertTrue(RolesConsola.isRolConsola("VENTANILLA"));
        assertTrue(RolesConsola.isRolConsola("ADMIN"));
    }

    @Test
    public void testIsRolConsola_ValidRolesCaseInsensitive() {
        // Verificar que isRolConsola es insensible a mayúsculas y minúsculas
        assertTrue(RolesConsola.isRolConsola("ventanilla"));
        assertTrue(RolesConsola.isRolConsola("admin"));
    }

    @Test
    public void testIsRolConsola_InvalidRole() {
        // Verificar que isRolConsola devuelve false para un rol inválido
        assertFalse(RolesConsola.isRolConsola("USUARIO"));
    }

    @Test
    public void testIsRolConsola_EmptyString() {
        // Verificar que isRolConsola devuelve false para una cadena vacía
        assertFalse(RolesConsola.isRolConsola(""));
    }
}