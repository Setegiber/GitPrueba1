/**
 * Copyright (C) 2015 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

package es.mjusticia;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
public class Main {
	public static void main(String[] args) {
		Datos datos=new Datos();
		Recursos r=new Recursos();
		ArrayList<AnalizarJSON>objetos=new ArrayList<>();
		AnalizarJSON analizar=new AnalizarJSON();
		URL scriptUrl = Main.class.getResource(datos.urlAxeMinJs);
		WebDriver driver=null;
		try {
			String currentPath = new java.io.File(".").getCanonicalPath();
			datos.directorioConfiguracion=currentPath+"\\";
			//cargar urlConfiguracion
			System.out.println("Cargar configuracion");
			//comprobamos si se encuentra el fichero de propiedades en la ruta donde se ejecuta el programa y sino preguntamos al usuario.
			if(!r.validarFichero(datos.nombreFicheroPropiedades)){
				datos.directorioConfiguracion=r.pregutarAlUsuario("No se encuentra "+datos.directorioConfiguracion+datos.nombreFicheroPropiedades+"\nIntroduce la url del fichero "+datos.nombreFicheroPropiedades);
				if(!r.validarFichero(datos.directorioConfiguracion)) {
					r.imprimirErrorPara("ERROR no se encuntra el fichero "+datos.nombreFicheroPropiedades);
				}
				
			}
		datos.cargarFicheroProperties();
		//cargar datos por pantalla
		datos.cargarDatosPorPantalla();
		datos.imprimirDatos();
		switch (datos.navegador) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver", datos.driverChrome);
			driver = new ChromeDriver();
			break;
		case "firefox":
			System.setProperty("webdriver.gecko.driver", datos.driverFireFox);
			driver = new FirefoxDriver();
			break;
		case "edge":
			System.setProperty("webdriver.edge.driver", datos.driverEdge);
			driver = new EdgeDriver();
			break;
		default:
			r.imprimirErrorPara("ERROR Navegador no soportado "+datos.navegador);
			break;
		}
		driver.get(datos.paginaInicio);
		driver.manage().window().maximize();
		boolean ejecutarProceso=true;
		while(ejecutarProceso) {
			datos.nombreAnalisis=r.pregutarAlUsuario("Introduce el nombre del informe y pulsa continuar para realizar el analisis de la pagina actual");
			datos.nombreAnalisis=datos.nombreAnalisis.trim();
			switch (datos.nombreAnalisis) {
			//el usuario ha tachado o cancelado
			case "":
				if(r.preguntarConfirmacionAlUsuairo("¿Parar la grabacion?")) {
					ejecutarProceso=false;
				}
				break;

			default:
				//validar nombre del informe
				String regex = "^[a-zA-Z0-9]+$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(datos.nombreAnalisis);
				if(!matcher.matches()) {
					r.imprimirErrorNoPara("Formato incorrecto: "+datos.nombreAnalisis);
				}
				//validar si existe el nombre del informe
				else if(r.validarReporte(datos.directorioInforme+"/"+datos.nombreAnalisis+".json")) {
					r.imprimirErrorNoPara("Ya existe un informe con el nombre "+datos.nombreAnalisis);
				}
				else {
					//tomar captura de pantalla
					File imagen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		            //Mueve el archivo a la carga especificada con el respectivo nombre
		            FileUtils.copyFile(imagen, new File(datos.directorioInforme+"/"+datos.nombreAnalisis+".png"));
		            
					JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).analyze();
					AXE.writeResults(datos.directorioInforme+"/"+datos.nombreAnalisis, responseJSON);
					r.imprimirMensaje("Se ha realizado el analisis "+datos.nombreAnalisis);
					if(!r.validarReporte(datos.directorioInforme+"/"+datos.nombreAnalisis+".json")) {
						r.imprimirErrorNoPara("ERROR en la generacion del informe de la pagina actual. Vuelva a intentarlo");
					}
					
					
					
				}
			}
		}
		if(r.preguntarConfirmacionAlUsuairo("¿Generar Informe?")) {
			objetos=analizar.realizarAnalisis(datos.directorioInforme, objetos);
			HTML html=new HTML();
			html.generarInformeHTML(datos, objetos);
		}
		}
		catch(Exception ex) {
			r.imprimirErrorNoPara(ex.toString());
		}
		finally {
			try {
			driver.quit();
			}
			catch(Exception ex) {
			}
		}
		
		
	}
}
