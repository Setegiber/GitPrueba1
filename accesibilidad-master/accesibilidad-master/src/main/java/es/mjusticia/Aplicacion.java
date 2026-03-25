
package es.mjusticia;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;

public class Aplicacion {
    public static final String ficheroConfiguracion = "config.properties";

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel root;

    private final ConfiguracionDatos datosConfig = new ConfiguracionDatos();
    private GrabarNavegacion panelGrabar;
    private GenerarInforme panelInforme;
    private PrepararInforme panelPreparar;
    private JLabel lblMsgPrincipal = new JLabel(" ");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Aplicacion().initUI());
    }

    private void initUI() {
        frame = new JFrame("Accesibilidad - Informes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(760, 520);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);

        File configFile = new File(System.getProperty("user.dir"), ficheroConfiguracion);
        datosConfig.cargar(configFile);

        JPanel principal = crearPanelPrincipal();
        Configuracion panelConfig = new Configuracion(configFile, datosConfig, () -> {
            limpiarMensajePrincipal();
            cardLayout.show(root, "principal");
        });

        panelGrabar = new GrabarNavegacion(datosConfig, () -> {
            limpiarMensajePrincipal();
            cardLayout.show(root, "principal");
        });

        panelInforme = new GenerarInforme(datosConfig, () -> {
            limpiarMensajePrincipal();
            cardLayout.show(root, "principal");
        }, seleccionados -> {
            String error = validarConfiguracion(datosConfig);
            if (error != null) {
                mostrarMensajePrincipal(error, true);
                return;
            }
            panelPreparar = new PrepararInforme(seleccionados, datosConfig.getProyecto(), datosConfig, () -> {
                cardLayout.show(root, "informe");
            });
            root.add(panelPreparar, "preparar");
            cardLayout.show(root, "preparar");
        });

        root.add(principal, "principal");
        root.add(panelConfig, "config");
        root.add(panelGrabar, "grabar");
        root.add(panelInforme, "informe");

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        JLabel titulo = new JLabel("Aplicación de Informes de Accesibilidad", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(4, 1, 8, 8));
        JButton btnConfig = new JButton("Configuración");
        JButton btnGrabar = new JButton("Grabar navegación");
        JButton btnInforme = new JButton("Generar informe");
        JButton btnCerrar = new JButton("Cerrar");

        btnConfig.addActionListener(e -> {
            limpiarMensajePrincipal();
            cardLayout.show(root, "config");
        });

        btnGrabar.addActionListener(e -> {
            String error = validarConfiguracion(datosConfig);
            if (error != null) {
                mostrarMensajePrincipal("No se puede comenzar la grabación: " + error, true);
                return;
            }
            panelGrabar.prepararAntesDeMostrar();
            limpiarMensajePrincipal();
            cardLayout.show(root, "grabar");
        });

        btnInforme.addActionListener(e -> {
            String error = validarConfiguracion(datosConfig);
            if (error != null) {
                mostrarMensajePrincipal(error, true);
                return;
            }
            panelInforme.prepararAntesDeMostrar();
            limpiarMensajePrincipal();
            cardLayout.show(root, "informe");
        });

        btnCerrar.addActionListener(e -> System.exit(0));

        centro.add(btnConfig);
        centro.add(btnGrabar);
        centro.add(btnInforme);
        centro.add(btnCerrar);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 64, 12, 64));
        panel.add(centro, BorderLayout.CENTER);

        lblMsgPrincipal.setHorizontalAlignment(SwingConstants.CENTER);
        lblMsgPrincipal.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        panel.add(lblMsgPrincipal, BorderLayout.SOUTH);

        return panel;
    }

    /** Validaciones: driver, proyecto, traducciones (si indicó), axeScript (obligatorio), imagenHeader (si indicó, debe existir) */
    private String validarConfiguracion(ConfiguracionDatos d) {
        if (estaVacio(d.getNombreAplicacion())) return "Falta 'Nombre Aplicación'.";
        if (estaVacio(d.getPaginaInicio())) return "Falta 'Página Inicio'.";
        if (estaVacio(d.getNavegador())) return "Falta seleccionar 'Navegador'.";
        if (estaVacio(d.getDriver())) return "Falta 'Driver'.";

        File driverFile = new File(d.getDriver());
        if (!driverFile.exists()) return "El 'Driver' no existe: " + driverFile.getAbsolutePath();

        if (estaVacio(d.getProyecto())) return "Falta 'Proyecto'.";
        File dirProyecto = new File(System.getProperty("user.dir"), d.getProyecto());
        if (dirProyecto.exists() && !dirProyecto.isDirectory()) return "'Proyecto' no es un directorio.";

        // Traducciones (opcional): si hay ruta, debe existir
        if (!estaVacio(d.getTraducciones())) {
            File dict = resolverEnUserDirSiRelativo(d.getTraducciones());
            if (!dict.exists()) return "El fichero de traducciones no existe: " + dict.getAbsolutePath();
        }

        // Axe Script (OBLIGATORIO)
        if (estaVacio(d.getAxeScript())) return "Falta 'Axe Script'.";
        File axeFile = resolverEnUserDirSiRelativo(d.getAxeScript());
        if (!axeFile.exists()) return "El 'Axe Script' no existe: " + axeFile.getAbsolutePath();

        // Imagen encabezado (opcional): si se informó, debe existir
        if (!estaVacio(d.getImagenHeader())) {
            File img = resolverEnUserDirSiRelativo(d.getImagenHeader());
            if (!img.exists()) return "La 'Imagen encabezado' no existe: " + img.getAbsolutePath();
        }

        return null;
    }

    private static File resolverEnUserDirSiRelativo(String ruta) {
        File f = new File(ruta);
        if (!f.isAbsolute()) {
            f = new File(System.getProperty("user.dir"), ruta);
        }
        return f;
    }

    private boolean estaVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void mostrarMensajePrincipal(String msg, boolean error) {
               lblMsgPrincipal.setText(msg);
        lblMsgPrincipal.setForeground(error ? Color.RED : new Color(0, 128, 0));
    }

    private void limpiarMensajePrincipal() {
        lblMsgPrincipal.setText(" ");
        lblMsgPrincipal.setForeground(Color.DARK_GRAY);
    }
}