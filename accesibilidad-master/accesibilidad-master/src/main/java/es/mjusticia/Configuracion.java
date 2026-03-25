
package es.mjusticia;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/** Pantalla de configuración con campos editables y guardado en config.properties */
public class Configuracion extends JPanel {
    private final File configFile;
    private final ConfiguracionDatos datos;
    private final Runnable onExitToMain;

    private final JTextField tfNombre = new JTextField(28);
    private final JTextField tfPagina = new JTextField(28);
    private final JComboBox<String> cbNavegador = new JComboBox<>(new String[]{"Chrome", "Firefox"});
    private final JTextField tfDriver = new JTextField(28);
    private final JTextField tfProyecto = new JTextField(28);
    private final JTextField tfTraducciones = new JTextField(28);
    private final JTextField tfAxeScript = new JTextField(28);
    private final JTextField tfAxeVersion = new JTextField(28);
    private final JTextField tfImagenHeader = new JTextField(28); // NUEVO

    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnSalir = new JButton("Salir");
    private final JButton btnSalirSinGuardar = new JButton("Salir sin guardar");

    // Botones "Examinar..."
    private final JButton btnBuscarDriver = new JButton("Examinar…");
    private final JButton btnBuscarTraducciones = new JButton("Examinar…");
    private final JButton btnBuscarAxe = new JButton("Examinar…");
    private final JButton btnBuscarImagenHeader = new JButton("Examinar…"); // NUEVO

    private boolean cambiosPendientes = false;

    public Configuracion(File configFile, ConfiguracionDatos datos, Runnable onExitToMain) {
        this.configFile = configFile;
        this.datos = datos;
        this.onExitToMain = onExitToMain;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titulo = new JLabel("Configuración", SwingConstants.LEFT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        add(titulo, BorderLayout.NORTH);

        add(crearFormulario(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        cargarCamposDesdeObjeto();
        prepararListenersDeCambio();
        prepararBotonesExaminar();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        // Nombre aplicación
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Nombre Aplicación:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(tfNombre, gbc);
        gbc.gridwidth = 1; fila++;

        // Página inicio
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Página Inicio:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(tfPagina, gbc);
        gbc.gridwidth = 1; fila++;

        // Navegador
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Navegador:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(cbNavegador, gbc);
        gbc.gridwidth = 1; fila++;

        // Driver + Examinar
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Driver:"), gbc);
        gbc.gridx = 1; panel.add(tfDriver, gbc);
        gbc.gridx = 2; panel.add(btnBuscarDriver, gbc);
        fila++;

        // Proyecto
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Proyecto:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(tfProyecto, gbc);
        gbc.gridwidth = 1; fila++;

        // Traducciones + Examinar
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Fichero traducciones:"), gbc);
        gbc.gridx = 1; panel.add(tfTraducciones, gbc);
        gbc.gridx = 2; panel.add(btnBuscarTraducciones, gbc);
        fila++;

        // Axe Script + Examinar
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Axe Script (JS):"), gbc);
        gbc.gridx = 1; panel.add(tfAxeScript, gbc);
        gbc.gridx = 2; panel.add(btnBuscarAxe, gbc);
        fila++;

        // Axe Version (libre)
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Versión aXe (texto libre):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(tfAxeVersion, gbc);
        gbc.gridwidth = 1; fila++;

        // Imagen encabezado + Examinar (NUEVO)
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Imagen encabezado (PNG/JPG):"), gbc);
        gbc.gridx = 1; panel.add(tfImagenHeader, gbc);
        gbc.gridx = 2; panel.add(btnBuscarImagenHeader, gbc);

        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(8, 8));
        lblMensaje.setText(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);
        lblMensaje.setHorizontalAlignment(SwingConstants.LEFT);
        pie.add(lblMensaje, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnSalir);
        btnSalirSinGuardar.setVisible(false);
        botones.add(btnSalirSinGuardar);
        pie.add(botones, BorderLayout.EAST);

        btnGuardar.addActionListener(e -> guardar());
        btnSalir.addActionListener(e -> salir());
        btnSalirSinGuardar.addActionListener(e -> onExitToMain.run());

        return pie;
    }

    private void cargarCamposDesdeObjeto() {
        tfNombre.setText(datos.getNombreAplicacion());
        tfPagina.setText(datos.getPaginaInicio());
        tfDriver.setText(datos.getDriver());
        tfProyecto.setText(datos.getProyecto());
        tfTraducciones.setText(datos.getTraducciones());
        tfAxeScript.setText(datos.getAxeScript());
        tfAxeVersion.setText(datos.getAxeVersion());
        tfImagenHeader.setText(datos.getImagenHeader()); // NUEVO

        cbNavegador.setSelectedItem(
                datos.getNavegador() == null || datos.getNavegador().isBlank() ? "Chrome" : datos.getNavegador()
        );
        cambiosPendientes = false;
        limpiarMensaje();
        btnSalirSinGuardar.setVisible(false);
    }

    private void prepararListenersDeCambio() {
        tfNombre.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfPagina.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfDriver.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfProyecto.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfTraducciones.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfAxeScript.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfAxeVersion.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        tfImagenHeader.getDocument().addDocumentListener((SimpleDocumentListener) e -> cambiosPendientes = true);
        cbNavegador.addActionListener(e -> cambiosPendientes = true);
    }

    private void prepararBotonesExaminar() {
        btnBuscarDriver.addActionListener(e ->
                seleccionarArchivo(tfDriver, null, "Seleccionar driver"));

        btnBuscarTraducciones.addActionListener(e ->
                seleccionarArchivo(tfTraducciones,
                        new FileNameExtensionFilter("Propiedades / Texto", "properties", "txt"),
                        "Seleccionar fichero de traducciones"));

        btnBuscarAxe.addActionListener(e ->
                seleccionarArchivo(tfAxeScript,
                        new FileNameExtensionFilter("JavaScript (*.js)", "js"),
                        "Seleccionar fichero axe.min.js"));

        btnBuscarImagenHeader.addActionListener(e ->
                seleccionarArchivo(tfImagenHeader,
                        new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"),
                        "Seleccionar imagen de encabezado"));
    }

    private void seleccionarArchivo(JTextField destino, FileNameExtensionFilter filtro, String titulo) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(titulo);
        if (filtro != null) fc.setFileFilter(filtro);

        // Directorio inicial: valor actual si existe, si no user.dir
        File inicial = null;
        String actual = destino.getText().trim();
        if (!actual.isBlank()) {
            File f = new File(actual);
            if (!f.isAbsolute()) {
                f = new File(System.getProperty("user.dir"), actual);
            }
            inicial = f.exists() ? (f.isDirectory() ? f : f.getParentFile()) : null;
        }
        if (inicial == null) inicial = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(inicial);

        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File elegido = fc.getSelectedFile();
            // Guardamos RELATIVO a user.dir si aplica
            File base = new File(System.getProperty("user.dir"));
            String path = elegido.getAbsolutePath();
            String basePath = base.getAbsolutePath() + File.separator;
            if (path.startsWith(basePath)) {
                path = path.substring(basePath.length()); // relativo
            }
            destino.setText(path);
            cambiosPendientes = true;
        }
    }

    private void guardar() {
        datos.setNombreAplicacion(tfNombre.getText().trim());
        datos.setPaginaInicio(tfPagina.getText().trim());
        datos.setNavegador((String) cbNavegador.getSelectedItem());
        datos.setDriver(tfDriver.getText().trim());
        datos.setProyecto(tfProyecto.getText().trim());
        datos.setTraducciones(tfTraducciones.getText().trim());
        datos.setAxeScript(tfAxeScript.getText().trim());
        datos.setAxeVersion(tfAxeVersion.getText().trim());
        datos.setImagenHeader(tfImagenHeader.getText().trim()); // NUEVO

        datos.guardar(configFile);
        cambiosPendientes = false;
        mostrarMensaje("Configuración guardada correctamente.", false);
        btnSalirSinGuardar.setVisible(false);
    }

    private void salir() {
        if (cambiosPendientes) {
            mostrarMensaje("Hay cambios sin guardar. Pulsa Guardar o 'Salir sin guardar'.", true);
            btnSalirSinGuardar.setVisible(true);
        } else {
            onExitToMain.run();
        }
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