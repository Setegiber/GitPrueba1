package es.mjusticia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;
public class AnalizarJSON {
	String nombreJson;
	String urlAnalisis;
	String categoria;
	String help;
	String impact;
	String description;
	String helpUrl;
	String id;
	String tags;
	//nodes
	String html;
	String data;
	String message;
	String failureSummary;
	String target;
	//otros
	String nivelAccesibilidad="";
	String principio="";
	String pauta="";
	public ArrayList<AnalizarJSON> realizarAnalisis(String directorioJSON,ArrayList<AnalizarJSON>objetos) {
		File directorio = new File(directorioJSON);
		File[] ficheros = directorio.listFiles();
		for (int i = 0; i < ficheros.length; i++) {	
			if(ficheros[i].getName().contains(".json")) {
				String json=leerFichero(directorioJSON+"\\"+ficheros[i].getName());
				objetos=leerJson(ficheros[i].getName(),json, objetos);
			}
		}
		//ordenamos el array por principio
		 Collections.sort(objetos,new Comparator<AnalizarJSON>() {
				public int compare(AnalizarJSON o1, AnalizarJSON o2) {
					// TODO Auto-generated method stub
					return o1.principio.compareTo(o2.principio);
				}
	        	
			});
		//ordenamos el array por paginas
		 Collections.sort(objetos,new Comparator<AnalizarJSON>() {
				public int compare(AnalizarJSON o1, AnalizarJSON o2) {
					// TODO Auto-generated method stub
					return o1.nombreJson.compareTo(o2.nombreJson);
				}
	        	
			});
		
		return objetos;
	}
	private AnalizarJSON analizarTags(AnalizarJSON objeto) {
		String []partes=objeto.tags.split(",");
		for(String p:partes) {
			p=p.replace("\"", "");
			p=p.replace("[", "");
			p=p.replace("]", "");
			//nivelAccesibilidad
			if(p.contains("wcag2a")||p.contains("wcag21a")) {
				objeto.nivelAccesibilidad=p.replace("wcag2", "");
				objeto.nivelAccesibilidad=objeto.nivelAccesibilidad.replace("a", "A");
//				objeto.nivelAccesibilidad=objeto.nivelAccesibilidad.toUpperCase();
			}
			//nivel de accesibilidad best-practice
			else if(p.contains("best-practice")) {
				objeto.nivelAccesibilidad="Mejores practicas";
				objeto.principio="Mejores practicas";
				objeto.pauta="Mejores practicas";
			}
			//pauta y principio
			else if(p.contains("wcag")) {
				
				p=p.replace("wcag","");
				String tipoPrincipio=p.substring(0,1);
//				objeto.principio+=":"+tipoPrincipio;
				switch (tipoPrincipio) {
				case "1":
					objeto.principio+="Principio 1: Perceptible. ";
					break;
				case "2":
					objeto.principio+="Principio 2: Operable. ";
					break;
				case "3":
					objeto.principio+="Principio 3: Comprensible. ";
					break;
				case "4":
					objeto.principio+="Principio 4: Robustez. ";
					break;
				default:
					objeto.principio+="PRINCIPIO NO DEFINIDO. ";
					break;
				}
				
				String tipoPauta=p.substring(0,2);
//				objeto.pauta+=":"+tipoPauta;
				switch (tipoPauta) {
				case "11":
					objeto.pauta+="Pauta 1.1 Alternativas textuales: Proporcionar alternativas textuales para todo contenido no textual de modo que se pueda convertir a otros formatos que las personas necesiten, tales como textos ampliados, braille, voz, símbolos o en un lenguaje más simple.";
					break;
				case "12":
					objeto.pauta+="Pauta 1.2 Medios tempodependientes: Proporcionar alternativas para los medios tempodependientes.";
					break;
				case "13":
					objeto.pauta+="Pauta 1.3 Adaptable: Crear contenido que pueda presentarse de diferentes formas (por ejemplo, con una disposición más simple) sin perder información o estructura.";
					break;
				case "14":
					objeto.pauta+="Pauta 1.4 Distinguible: Facilitar a los usuarios ver y oír el contenido, incluyendo la separación entre el primer plano y el fondo.";
					break;
				case "21":
					objeto.pauta+="Pauta 2.1 Accesible por teclado: Proporcionar acceso a toda la funcionalidad mediante el teclado.";
					break;
				case "22":
					objeto.pauta+="Pauta 2.2 Tiempo suficiente: Proporcionar a los usuarios el tiempo suficiente para leer y usar el contenido.";
					break;
				case "23":
					objeto.pauta+="Pauta 2.3 Convulsiones: No diseñar contenido de un modo que se sepa podría provocar ataques, espasmos o convulsiones.";
					break;
				case "24":
					objeto.pauta+="Pauta 2.4 Navegable: Proporcionar medios para ayudar a los usuarios a navegar, encontrar contenido y determinar dónde se encuentran.";
					break;
				case "31":
					objeto.pauta+="Pauta 3.1 Legible: Hacer que los contenidos textuales resulten legibles y comprensibles.";
					break;
				case "32":
					objeto.pauta+="Pauta 3.2 Predecible: Hacer que las páginas web aparezcan y operen de manera predecible.";
					break;
				case "33":
					objeto.pauta+="Pauta 3.3 Entrada de datos asistida: Ayudar a los usuarios a evitar y corregir los errores.";
					break;
				case "41":
					objeto.pauta+="Pauta 4.1 Compatible: Maximizar la compatibilidad con las aplicaciones de usuario actuales y futuras, incluyendo las ayudas técnicas.";
					break;
				default:
					objeto.pauta+="PAUTA NO DEFINDA";
					break;
				}
			}
		}
//		if(objeto.principio.contains(":")) {
//			objeto.principio=objeto.principio.substring(1,objeto.principio.length());
//		}
//		if(objeto.pauta.contains(":")) {
//			objeto.pauta=objeto.pauta.substring(1,objeto.pauta.length());
//		}
		return objeto;
	}
	private String leerFichero(String fichero){
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader (fichero));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        try {
				while((line = reader.readLine()) != null) {
				    stringBuilder.append(line);
				    stringBuilder.append(ls);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        return stringBuilder.toString();
	    } finally {
	        try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	private String formatear(String texto) {
		texto=texto.replace("\"", "");
		texto=texto.replace("\n", ". ");
		texto=texto.replace("$", " ");
		texto=texto.replace("<", "&lt;");
		texto=texto.replace(">", "&gt;");
		
		return texto;
	}
	private ArrayList<AnalizarJSON> leerJson(String nombrefichero,String fichero,ArrayList<AnalizarJSON>objetos) {
		
        JSONObject json = new JSONObject(fichero);
        String urlanalizar=json.getString("url");
        String[]nodos= {"incomplete","passes","violations"};
//        String[]nodos= {"incomplete"};
        for(String nodo:nodos) {
            JSONArray nodo1=(json.getJSONArray(nodo));
            for(int i=0;i<nodo1.length();i++) {
            	JSONArray nodo2=nodo1.getJSONObject(i).getJSONArray("nodes");
            	for (int e=0;e<nodo2.length();e++) {
            			AnalizarJSON objeto=new AnalizarJSON();
                		objeto.categoria=nodo;
                		objeto.urlAnalisis=urlanalizar;
                		try {
	                		objeto.help=nodo1.getJSONObject(i).getString("help");
	                		objeto.help=formatear(objeto.help);
                		}
                		catch(Exception ex) {
                			objeto.help="NO";
                		}
                		objeto.nombreJson=nombrefichero.replace(".json", "");
                		try {
                			objeto.impact=nodo1.getJSONObject(i).getString("impact");
                			objeto.impact=formatear(objeto.impact);
                		}
                		catch(Exception ex) {
                			objeto.impact="NO";
                		}
                		try {
                			objeto.description=nodo1.getJSONObject(i).getString("description");
                			objeto.description=formatear(objeto.description);
                		}
                		catch(Exception ex) {
                			objeto.description="NO";
                		}
                		try {
                			objeto.helpUrl=nodo1.getJSONObject(i).getString("helpUrl");
                			objeto.helpUrl=formatear(objeto.helpUrl);
                		}
                		catch(Exception ex) {
                			objeto.helpUrl="NO";
                		}
                		try {
                			objeto.id=nodo1.getJSONObject(i).getString("id");
                			objeto.id=formatear(objeto.id);
                		}
                		catch(Exception ex) {
                			objeto.id="NO";
                		}
                		try {
                			objeto.tags=nodo1.getJSONObject(i).getJSONArray("tags").toString();
                			objeto.tags=formatear(objeto.tags);
                			objeto=analizarTags(objeto);
                		}
                		catch(Exception ex) {
                			objeto.tags="NO";
                		}
                		//nodes
                		try {
                			objeto.failureSummary=nodo1.getJSONObject(i).getJSONArray("nodes").getJSONObject(e).getString("failureSummary");
                			objeto.failureSummary=formatear(objeto.failureSummary);
                			
                		}
                		catch(Exception ex) {	
                			objeto.failureSummary="NO";
                		}
                		try {
                			objeto.target=nodo1.getJSONObject(i).getJSONArray("nodes").getJSONObject(e).getJSONArray("target").toString();
                			objeto.target=formatear(objeto.target);
                			
                		}
                		catch(Exception ex) {	
                			objeto.target="NO";
                		}
                		try {
                			objeto.html=nodo1.getJSONObject(i).getJSONArray("nodes").getJSONObject(e).getString("html");
                			objeto.html=formatear(objeto.html);
                			

                		}
                		catch(Exception ex) {
                			objeto.html="NO";
                		}
                		try {
                			objeto.data=nodo1.getJSONObject(i).getJSONArray("nodes").getJSONObject(e).getJSONArray("any").getJSONObject(0).toString();
                			objeto.data=formatear(objeto.data);
                		}
                		catch(Exception ex) {
                			objeto.data="NO";
                		}
                		try {
                			objeto.message=nodo1.getJSONObject(i).getJSONArray("nodes").getJSONObject(e).getJSONArray("any").getJSONObject(0).getString("message");
                			objeto.message=formatear(objeto.message);
                		}
                		catch(Exception ex) {
                			objeto.message="NO";
                		}
                		objetos.add(objeto);
            	}
            }
        }
        return objetos;
	}
}
