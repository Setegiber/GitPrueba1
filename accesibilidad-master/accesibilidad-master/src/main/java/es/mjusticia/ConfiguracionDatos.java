
package es.mjusticia;

import java.io.*;
import java.util.Properties;

/** Datos de configuración en memoria con carga/guardado .properties */
public class ConfiguracionDatos {
    private String nombreAplicacion = "";
    private String paginaInicio = "";
    private String navegador = "";
    private String driver = "";
    private String proyecto = "";
    private String traducciones = "";
    private String axeScript = "";
    private String axeVersion = "";
    private String imagenHeader = ""; // NUEVO

    public String getNombreAplicacion() { return nombreAplicacion; }
    public void setNombreAplicacion(String nombreAplicacion) { this.nombreAplicacion = nombreAplicacion; }

    public String getPaginaInicio() { return paginaInicio; }
    public void setPaginaInicio(String paginaInicio) { this.paginaInicio = paginaInicio; }

    public String getNavegador() { return navegador; }
    public void setNavegador(String navegador) { this.navegador = navegador; }

    public String getDriver() { return driver; }
    public void setDriver(String driver) { this.driver = driver; }

    public String getProyecto() { return proyecto; }
    public void setProyecto(String proyecto) { this.proyecto = proyecto; }

    public String getTraducciones() { return traducciones; }
    public void setTraducciones(String traducciones) { this.traducciones = traducciones; }

    public String getAxeScript() { return axeScript; }
    public void setAxeScript(String axeScript) { this.axeScript = axeScript; }

    public String getAxeVersion() { return axeVersion; }
    public void setAxeVersion(String axeVersion) { this.axeVersion = axeVersion; }

    public String getImagenHeader() { return imagenHeader; }
    public void setImagenHeader(String imagenHeader) { this.imagenHeader = imagenHeader; }

    public void cargar(File fichero) {
        if (fichero.exists() && fichero.isFile()) {
            Properties props = new Properties();
            try (InputStream in = new FileInputStream(fichero)) {
                props.load(in);
                nombreAplicacion = props.getProperty("nombreAplicacion", "");
                paginaInicio     = props.getProperty("paginaInicio", "");
                navegador        = props.getProperty("navegador", "");
                driver           = props.getProperty("driver", "");
                proyecto         = props.getProperty("proyecto", "");
                traducciones     = props.getProperty("traducciones", "");
                axeScript        = props.getProperty("axeScript", "");
                axeVersion       = props.getProperty("axeVersion", "");
                imagenHeader     = props.getProperty("imagenHeader", ""); // NUEVO
            } catch (IOException e) {
                System.err.println("Error al cargar configuración: " + e.getMessage());
            }
        }
    }

    public void guardar(File fichero) {
        Properties props = new Properties();
        props.setProperty("nombreAplicacion", nombreAplicacion);
        props.setProperty("paginaInicio", paginaInicio);
        props.setProperty("navegador", navegador);
        props.setProperty("driver", driver);
        props.setProperty("proyecto", proyecto);
        props.setProperty("traducciones", traducciones);
        props.setProperty("axeScript", axeScript);
        props.setProperty("axeVersion", axeVersion);
        props.setProperty("imagenHeader", imagenHeader); // NUEVO
        try (OutputStream out = new FileOutputStream(fichero)) {
            props.store(out, "Configuración de la aplicación");
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
    }
}