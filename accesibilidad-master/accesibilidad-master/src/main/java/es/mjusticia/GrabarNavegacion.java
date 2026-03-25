
package es.mjusticia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Objects;

/** Pantalla de grabación/navegación y análisis con aXe. */
public class GrabarNavegacion extends JPanel {
    private final ConfiguracionDatos datos;
    private final Runnable onExitToMain;

    private final JLabel lblTitulo = new JLabel("", SwingConstants.LEFT);
    // Campos
    private final JTextField tfNombre = new JTextField(24);
    private final JTextArea taObservaciones = new JTextArea(6, 24);
    private final JLabel lblMensaje = new JLabel(" ");
    // Botones
    private final JButton btnComenzar = new JButton("Comenzar grabación");
    private final JButton btnAnalizar = new JButton("Analizar página");
    private final JButton btnTerminar = new JButton("Terminar grabación");

    private final Selenium selenium = new Selenium();
    private File dirProyecto;

    public GrabarNavegacion(ConfiguracionDatos datos, Runnable onExitToMain) {
        this.datos = Objects.requireNonNull(datos);
        this.onExitToMain = Objects.requireNonNull(onExitToMain);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        add(lblTitulo, BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        setEstadoInicial();
    }

    public void prepararAntesDeMostrar() {
        lblTitulo.setText("Proyecto: " + datos.getProyecto());
        dirProyecto = new File(System.getProperty("user.dir"), datos.getProyecto());
        if (!dirProyecto.exists()) {
            boolean ok = dirProyecto.mkdirs();
            if (!ok) {
                mostrarMensaje("No se pudo crear el directorio del proyecto: " + dirProyecto.getAbsolutePath(), true);
            } else {
                limpiarMensaje();
            }
        } else {
            limpiarMensaje();
        }
        setEstadoInicial();
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre análisis
        gbc.gridx = 0; gbc.gridy = 0;
        centro.add(new JLabel("Nombre de análisis:"), gbc);
        gbc.gridx = 1;
        centro.add(tfNombre, gbc);

        // Observaciones
        gbc.gridx = 0; gbc.gridy = 1;
        centro.add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1;
        JScrollPane scroll = new JScrollPane(taObservaciones);
        taObservaciones.setLineWrap(true);
        taObservaciones.setWrapStyleWord(true);
        centro.add(scroll, gbc);

        // Mensaje
        lblMensaje.setText(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        centro.add(lblMensaje, gbc);

        return centro;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(btnComenzar);
        pie.add(btnAnalizar);
        pie.add(btnTerminar);

        btnComenzar.addActionListener(e -> comenzarGrabacion());
        btnAnalizar.addActionListener(e -> analizarPagina());
        btnTerminar.addActionListener(e -> terminarGrabacion());

        return pie;
    }

    private void setEstadoInicial() {
        tfNombre.setText("");
        taObservaciones.setText("");
        btnComenzar.setEnabled(true);
        btnAnalizar.setEnabled(false);
        btnTerminar.setEnabled(false);
    }

    private void comenzarGrabacion() {
        selenium.cerrarNavegador();
        try {
            selenium.iniciarNavegador(datos.getNavegador(), datos.getDriver(), datos.getPaginaInicio());
            btnComenzar.setEnabled(false);
            btnAnalizar.setEnabled(true);
            btnTerminar.setEnabled(true);
            mostrarMensaje("Navegador iniciado. Introduce el nombre y pulsa 'Analizar página'.", false);
        } catch (RuntimeException ex) {
            mostrarMensaje("Error al iniciar el navegador: " + ex.getMessage(), true);
            selenium.cerrarNavegador();
        }
    }

    /** Analiza la página con aXe, guarda PNG/JSON/OBS y crea también el fichero *_revisiones.txt por índice (1..N=Automática). */
    private void analizarPagina() {
        if (selenium.getDriver() == null) {
            mostrarMensaje("El navegador no está iniciado.", true);
            return;
        }
        String nombre = tfNombre.getText().trim().replaceAll("\\s+", "_");
        if (nombre.isEmpty()) {
            mostrarMensaje("El nombre es obligatorio.", true);
            return;
        }

        File png = new File(dirProyecto, nombre + ".png");
        File json = new File(dirProyecto, nombre + ".json");
        File txt  = new File(dirProyecto, nombre + ".txt");
        File rev  = ObservacionesRevisionIO.archivoRevisiones(dirProyecto, nombre);

        if (png.exists() || json.exists() || txt.exists() || rev.exists()) {
            mostrarMensaje("Ese nombre ya existe. Elige otro distinto.", true);
            return;
        }

        try {
            // Captura PNG
            selenium.capturarPantalla(png);

            // Resolver Axe Script (ruta relativa a user.dir o absoluta)
            File axeFile = new File(datos.getAxeScript());
            if (!axeFile.isAbsolute()) {
                axeFile = new File(System.getProperty("user.dir"), datos.getAxeScript());
            }
            if (!axeFile.exists()) {
                throw new RuntimeException("No se encontró 'Axe Script': " + axeFile.getAbsolutePath());
            }
            URL axeUrl = axeFile.toURI().toURL();

            // Ejecutar AXE y guardar JSON
            WebDriver driver = selenium.getDriver();
            JSONObject resultados = new AXE.Builder(driver, axeUrl).analyze();
            AXE.writeResults(new File(dirProyecto, nombre).getAbsolutePath(), resultados);

            // Crear fichero de revisiones por índice: 1..N = Automática
            int filas = contarFilas(resultados);
            ObservacionesRevisionIO.generarPorDefecto(rev, Math.max(filas, 0));

            // Guardar observaciones si hay texto
            String observaciones = taObservaciones.getText().trim();
            if (!observaciones.isEmpty()) {
                try (FileWriter fw = new FileWriter(txt)) {
                    fw.write(observaciones);
                }
            }

            // Mensaje verde con todos los ficheros generados
            mostrarMensaje(
                "Análisis guardado: " + png.getName() + ", " + json.getName()
                + (observaciones.isEmpty() ? "" : ", " + txt.getName())
                + ", " + rev.getName(),
                false
            );

            // Limpiar campos
            tfNombre.setText("");
            taObservaciones.setText("");

        } catch (Exception ex) {
            mostrarMensaje("Error durante el análisis: " + ex.getMessage(), true);
        }
    }

    /**
     * Cuenta filas = suma de 'nodes' en todas las 'violations' del resultado de aXe.
     * Soporta tanto resultados planos como con objeto 'results' anidado.
     */
    private int contarFilas(JSONObject resultados) {
        JSONObject root = resultados;
        if (root.has("results")) {
            root = root.getJSONObject("results");
        }
        if (!root.has("violations")) return 0;
        JSONArray violations = root.getJSONArray("violations");
        int total = 0;
        for (int i = 0; i < violations.length(); i++) {
            JSONObject viol = violations.getJSONObject(i);
            JSONArray nodes = viol.optJSONArray("nodes");
            total += (nodes != null) ? nodes.length() : 0;
        }
        return total;
    }

    private void terminarGrabacion() {
        selenium.cerrarNavegador();
        onExitToMain.run();
    }

    private void mostrarMensaje(String msg, boolean error) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(error ? Color.RED : new Color(0, 128, 0));
    }

    private void limpiarMensaje() {
               lblMensaje.setText(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);
    }
}