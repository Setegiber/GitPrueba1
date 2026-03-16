package es.mjusticia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HTML {
	
	
	public void generarInformeHTML(Datos datos,ArrayList<AnalizarJSON>objetos) {
		String titulo="@nombreProyecto@";
		String version="@version@";
		String fecha="@fecha@";
		String imagenEncabezado="@imagenPrincipal@";
		String imagenPiePagina="@imagenPiePagina@";
		int totalInfringidas = 0;
		int totalIncompletas=0;
		int totalPasadas=0;
		String divisionElementos="@@@";
		String divisionCampos="@@";	
		String paginas="";
		String principios="";
		String nodos="";
		//titulo
		titulo=datos.nombreAplicacion;
		//version
		version=datos.versionAplicacion;
		//fecha
		Date fechaActual = new Date();
		SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es_ES"));
		String fechaEsp=formateador.format(fechaActual);
		fecha=fechaEsp;
		//imagenPrincipal
		imagenEncabezado=datos.logoEncabezado;
		//imagenPiePagina
		imagenPiePagina=datos.logoPiePagina;
		//***********************
		String nombrePagina=objetos.get(0).nombreJson;
		int paginaId=0;
		int iParcialElementos=0;
		int iParcialInfringidos=0;
		int iParcialPasados=0;
		int iParcialIncumplidos=0;
		//principios
		String nombrePrincipio=objetos.get(0).principio;
		int principiosId=0;
		int nodosParciales=0;
		int nodosTotal=0;
		int saltoLineaNodos=0;
		int saltoLineaPrincipios=0;	
		for(int o=0;o<objetos.size();o++) {
			//nueva pagina
			if(!nombrePagina.equals(objetos.get(o).nombreJson)) {
				paginas+="\"+\n\"";
				paginaId++;
				paginas+=divisionElementos+(paginaId)+divisionCampos+
						nombrePagina+divisionCampos+
						iParcialElementos+divisionCampos+
						iParcialInfringidos+divisionCampos+
						iParcialIncumplidos+divisionCampos+
						iParcialPasados+divisionCampos+
						objetos.get(o-1).urlAnalisis+
						"";
				//cargo los principios
				principiosId++;
				saltoLineaPrincipios++;
				if(saltoLineaPrincipios==1) {
					saltoLineaPrincipios=0;
					principios+="\"+\n\"";
				}
				principios+=divisionElementos+principiosId+divisionCampos+
						(paginaId)+divisionCampos+
						objetos.get(o-1).categoria+divisionCampos+
						objetos.get(o-1).principio+divisionCampos+
						objetos.get(o-1).pauta+divisionCampos+
						objetos.get(o-1).impact+divisionCampos+
						objetos.get(o-1).id+divisionCampos+
						objetos.get(o-1).nivelAccesibilidad+divisionCampos+
						nodosParciales+divisionCampos+
						objetos.get(o-1).description+divisionCampos+
						objetos.get(o-1).helpUrl+divisionCampos+
						objetos.get(o-1).help+
						"";
				nombrePrincipio=objetos.get(o).principio;
				nodosParciales=0;
				//reinicio
				iParcialElementos=0;
				iParcialInfringidos=0;
				iParcialPasados=0;
				iParcialIncumplidos=0;
				nombrePagina=objetos.get(o).nombreJson;
			}
			//principios
			if(!nombrePrincipio.equals(objetos.get(o).principio)) {
				principiosId++;
				saltoLineaPrincipios++;
				if(saltoLineaPrincipios==1) {
					saltoLineaPrincipios=0;
					principios+="\"+\n\"";
				}
				principios+=divisionElementos+principiosId+divisionCampos+
						(paginaId+1)+divisionCampos+
						objetos.get(o-1).categoria+divisionCampos+
						objetos.get(o-1).principio+divisionCampos+
						objetos.get(o-1).pauta+divisionCampos+
						objetos.get(o-1).impact+divisionCampos+
						objetos.get(o-1).id+divisionCampos+
						objetos.get(o-1).nivelAccesibilidad+divisionCampos+
						nodosParciales+divisionCampos+
						objetos.get(o-1).description+divisionCampos+
						objetos.get(o-1).helpUrl+divisionCampos+
						objetos.get(o-1).help+
						"";
				nombrePrincipio=objetos.get(o).principio;
				nodosParciales=0;
			}
			nodosTotal++;
			nodosParciales++;
	    	  //total elementos parciales
	    	  iParcialElementos++;
	    	  //total infringidos
	    	  if(objetos.get(o).categoria.equals("violations")) {
	    		  totalInfringidas++;
	    		  iParcialInfringidos++;
	    	  }
	    	  //total pasados
			  if(objetos.get(o).categoria.equals("passes")) {
				  totalPasadas++;
				  iParcialPasados++;  		  
			  }
			  //total incompletos
			  if(objetos.get(o).categoria.equals("incomplete")) {
				  totalIncompletas++;
				  iParcialIncumplidos++;  
			  }

			//ultimo objeto
			if(o+1==objetos.size()) {
				paginas+="\"+\n\"";
				paginaId++;
				paginas+=divisionElementos+paginaId+divisionCampos+
						nombrePagina+divisionCampos+
						iParcialElementos+divisionCampos+
						iParcialInfringidos+divisionCampos+
						iParcialIncumplidos+divisionCampos+
						iParcialPasados+divisionCampos+
						objetos.get(o-1).urlAnalisis+
						"";
				//principios
				principiosId++;
				saltoLineaPrincipios++;
				if(saltoLineaPrincipios==1) {
					saltoLineaPrincipios=0;
					principios+="\"+\n\"";
				}
				principios+=divisionElementos+principiosId+divisionCampos+
						paginaId+divisionCampos+
						objetos.get(o).categoria+divisionCampos+
						objetos.get(o).principio+divisionCampos+
						objetos.get(o).pauta+divisionCampos+
						objetos.get(o).impact+divisionCampos+
						objetos.get(o).id+divisionCampos+
						objetos.get(o).nivelAccesibilidad+divisionCampos+
						nodosParciales+divisionCampos+
						objetos.get(o).description+divisionCampos+
						objetos.get(o).helpUrl+divisionCampos+
						objetos.get(o).help+
						"";
				nombrePrincipio=objetos.get(o).principio;
				nodosParciales=0;
			}
			//nodos
			
			nodos+=divisionElementos+nodosTotal+divisionCampos+
					(principiosId+1)+divisionCampos+
					objetos.get(o).target+divisionCampos+
//					objetos.get(o).html+divisionCampos+
//					objetos.get(o).help+divisionCampos+
					objetos.get(o).failureSummary+
					"";
			//salto linea
			saltoLineaNodos++;
			if(saltoLineaNodos==10) {
				saltoLineaNodos=0;
				nodos+="\"+\n\"";
			}
			
		}
		paginas=paginas.replaceFirst(divisionElementos, "");
		principios=principios.replaceFirst(divisionElementos, "");
		nodos=nodos.replaceFirst(divisionElementos, "");	

      
		
		
		String nombreInforme=datos.directorioInforme+"/Informe_Accesibilidad_"+datos.nombreAplicacion+".html";
		
		//compiamos la plantilla
		this.copiarFichero(nombreInforme, datos.plantilla);
		this.sustituirValorEnfichero(nombreInforme, "@nombreProyecto@", titulo);
		this.sustituirValorEnfichero(nombreInforme, "@version@",version);
		this.sustituirValorEnfichero(nombreInforme, "@fecha@", fecha);
		this.sustituirValorEnfichero(nombreInforme, "@imagenPrincipal@", imagenEncabezado);
		this.sustituirValorEnfichero(nombreInforme, "@imagenPiePagina@", imagenPiePagina);
		this.sustituirValorEnfichero(nombreInforme, "@numeroPaginasAnalizadas@", paginaId+"");
		this.sustituirValorEnfichero(nombreInforme, "@totalElementos@", nodosTotal+"");
		this.sustituirValorEnfichero(nombreInforme, "@totalInfringidas@", totalInfringidas+"");
		this.sustituirValorEnfichero(nombreInforme, "@totalIncompletas@", totalIncompletas+"");
		this.sustituirValorEnfichero(nombreInforme, "@totalPasadas@", totalPasadas+"");
		this.sustituirValorEnfichero(nombreInforme, "@divisionElementos@", divisionElementos);
		this.sustituirValorEnfichero(nombreInforme, "@divisionCampos@", divisionCampos);
		
		this.sustituirValorEnfichero(nombreInforme, "@paginas@", paginas);
		this.sustituirValorEnfichero(nombreInforme, "@principios@", principios);
		this.sustituirValorEnfichero(nombreInforme, "@nodos@", nodos);
	}
	
	public void sustituirValorEnfichero(String fichero, String valorInicial, String nuevoValor)
    {

        File fileToBeModified = new File(fichero);
         
        String oldContent = "";
         
        BufferedReader reader = null;
         
        FileWriter writer = null;
         
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
             
            //Reading all the lines of input text file into oldContent
             
            String line = reader.readLine();
             
            while (line != null) 
            {
                oldContent = oldContent + line + System.lineSeparator();
                 
                line = reader.readLine();
            }
             
            //Replacing oldString with newString in the oldContent
             
            String newContent = oldContent.replaceAll(valorInicial, nuevoValor);
             
            //Rewriting the input text file with newContent
             
            writer = new FileWriter(fileToBeModified);
             
            writer.write(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                //Closing the resources
                 
                reader.close();
                 
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
	
	public void copiarFichero(String nuevo,String original){	 
	    File fichero = new File(nuevo);
	    try (
	      InputStream in = new BufferedInputStream(
	        new FileInputStream(original));
	      OutputStream out = new BufferedOutputStream(
	        new FileOutputStream(fichero))) {
	 
	        byte[] buffer = new byte[1024];
	        int lengthRead;
	        while ((lengthRead = in.read(buffer)) > 0) {
	            out.write(buffer, 0, lengthRead);
	            out.flush();
	        }
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
