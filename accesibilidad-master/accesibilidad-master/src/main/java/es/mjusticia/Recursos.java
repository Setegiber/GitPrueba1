package es.mjusticia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Recursos {
	public void imprimirMensaje(String mensaje) {
		System.out.println(mensaje);
		JOptionPane.showMessageDialog(null, mensaje);
	}
	public void imprimirErrorNoPara(String error) {
		System.err.println(error);
		JOptionPane.showMessageDialog(null, error, "ERROR", JOptionPane.ERROR_MESSAGE);
	}
	public void imprimirErrorPara(String error) throws Exception {
		System.err.println(error);
		JOptionPane.showMessageDialog(null, error, "ERROR", JOptionPane.ERROR_MESSAGE);
		throw new Exception();
	}
	public String pregutarAlUsuario(String pregunta) {
		System.out.println(pregunta);
		String resultado="";
		resultado=JOptionPane.showInputDialog (pregunta);
		//Si se tacha el mensaje
		if(resultado==null) {
			resultado="";
		}
		else {
			resultado=resultado.trim();
			System.out.println(resultado);
		}
		return resultado;	
	}
	public boolean preguntarConfirmacionAlUsuairo(String pregunta) {
		boolean confirmacion=false;
		int resp=JOptionPane.showConfirmDialog(null,pregunta);
	      if (JOptionPane.OK_OPTION == resp){
	    	  confirmacion=true;
	      }
	      else{
	    	confirmacion=false;
	   }
	      return confirmacion;
	}
	public String seleccionarNavegador() {
		String resultado="";
		Object color = JOptionPane.showInputDialog(null,"Seleccione Un Navegador",
				   "NAVEGADORES", JOptionPane.QUESTION_MESSAGE, null,
				  new Object[] { "chrome","firefox", "edge"},"Seleccione");
		resultado=color.toString();
		return resultado;
	}
	//validar directorio
	public void validarDirectorio(String ruta) throws Exception {
		File archivo = new File(ruta);
		if (!archivo.exists()) {
			this.imprimirErrorPara("Error en el directorio "+ruta);
		}
		if (!archivo.isDirectory()){
			this.imprimirErrorPara("Error en el directorio "+ruta);
		}
	}
	//validar fichero
	public boolean validarFichero(String rutaFichero) throws Exception {
		boolean resultado=true;
		try {
			File archivo = new File(rutaFichero);
			if (!archivo.exists()) {
				resultado=false;
			}
			if (!archivo.isFile()){
				resultado=false;
			}
		}
		catch(Exception ex) {
			return false;
		}

		return resultado;
	}
	//validar fichero
		public boolean validarReporte(String rutaFichero){
			boolean resultado=false;
			File archivo = new File(rutaFichero);
			if (archivo.exists()) {
				resultado=true;
			}
			return resultado;
		}
	//lanzar analisis
	//leer fichero de propiedades
	public String leerProperties(String rutaFichero, String propiedad) {
		rutaFichero=rutaFichero.trim();
		propiedad=propiedad.trim();
		String resultado="";
		Properties propiedades = new Properties();
		try {
			propiedades.load(new FileReader(rutaFichero));
			resultado=propiedades.getProperty(propiedad);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultado="Error "+e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultado="no cargado"+e;
		}
		return resultado;
	}
}
