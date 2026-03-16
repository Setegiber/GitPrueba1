package es.mjusticia;

public class Datos {
	public String directorioConfiguracion;
	public String paginaInicio="";
	public String navegador="";
	public String driverChrome="";
	public String driverFireFox="";
	public String driverEdge="";
	//propiedades para la ejecucion
	public String nombreFicheroPropiedades="conf.properties";
	public String urlAxeMinJs="axe.min.js";
	public boolean realizarAnalisis=false;
	public String nombreAnalisis="";
	//propiedades para la generacion del informe
	public String directorioInforme="";
	public String plantilla="";
	public String nombreAplicacion="";
	public String versionAplicacion="";
	public String logoEncabezado="";
	public String logoPiePagina="";

	
	//cargar fichero de propiedades
	public void cargarFicheroProperties() {
		Recursos r=new Recursos();
		this.directorioInforme=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "directorioInforme");
		this.paginaInicio=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "paginaInicio");
		this.navegador=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "navegador");
		this.driverChrome=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "driverChrome");
		this.driverFireFox=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "driverFireFox");
		this.driverEdge=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "driverEdge");
		this.plantilla=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "plantilla");
		this.nombreAplicacion=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "nombreAplicacion");
		this.versionAplicacion=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "versionAplicacion");
		this.logoEncabezado=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "logoEncabezado");
		this.logoPiePagina=r.leerProperties(this.directorioConfiguracion+"/"+this.nombreFicheroPropiedades, "logoPiePagina");
	}
	//cargar datos por pantalla y validacion de datos
	public void cargarDatosPorPantalla() throws Exception{
		Recursos r=new Recursos();
//		//cargamos la url de generacion del informe si no lo ha cargado el usaurio
//		if(this.directorioInforme==null||this.directorioInforme.isEmpty()) {
//			//comprobamos si existe el directorio en la ruta de ejcucucion
//			
//			//creamos el directorio informe en la ruta 
//			Path path = Paths.get();
//			Files.createDirectories(this.directorioConfiguracion+"informe");
//		}
//		else {
//			//comprobamos que este vacío el directorio
//		}
		//cargamos el navegador si no lo ha introducido el usuario en la configuracion
		if(this.navegador==null||this.navegador.isEmpty()) {
			this.navegador=r.seleccionarNavegador();
		}
		//comprobamos si el navegador es correcto
		boolean continuar;
		do {
			continuar=false;
			switch (this.navegador) {
			case "chrome":
				//cargamos el driver si no está cargado
				if(this.driverChrome==null||this.driverChrome.isEmpty()) {
					this.driverChrome=r.pregutarAlUsuario("Introduce la url de chrome driver");
				}
				//comprobamos la ruta del driver
				r.validarFichero(this.driverChrome);
				break;
			case "firefox":
				//cargamos el driver si no está cargado
				if(this.driverFireFox==null||this.driverFireFox.isEmpty()) {
					this.driverFireFox=r.pregutarAlUsuario("Introduce la url de firfox driver");
				}
				//comprobamos la ruta del driver
				r.validarFichero(this.driverFireFox);
				break;
			case "edge":
				//cargamos el driver si no está cargado
				if(this.driverEdge==null||this.driverEdge.isEmpty()) {
					this.driverEdge=r.pregutarAlUsuario("Introduce la url de edge driver");
				}
				//comprobamos la ruta del driver
				r.validarFichero(this.driverEdge);
				break;
			default:
				r.imprimirErrorNoPara("Navegador no soportado "+this.navegador);
				this.navegador=r.seleccionarNavegador();
				continuar=true;
				break;
			}
		} while (continuar);
		if(this.paginaInicio==null||this.paginaInicio.isEmpty()) {
			this.paginaInicio=r.pregutarAlUsuario("Introduce la pagina de inicio");
			continuar=true;
		}
		
	}
	//imprimir datosp
	public void imprimirDatos() {
		Recursos r=new Recursos();
		String mensaje=("*****DATOS DE CONFIGURACION*****"+
				"\nurlConfiguracion="+directorioConfiguracion+
				"\nplantilla="+plantilla+
				"\nnombreAplicacion="+nombreAplicacion+
				"\nversionAplicacion="+versionAplicacion+
				"\nlogoEncabezado="+logoEncabezado+
				"\nlogoPiePagina="+logoPiePagina+
				"\npagina="+paginaInicio+
				"\nnavegador="+navegador+
				"\ndriverChrome="+driverChrome+
				"\ndriverFireFox="+driverFireFox+
				"\ndirverEdge="+driverEdge+
				"\ndirectorioInforme="+directorioInforme+
				"\n"+
				"********************");
		r.imprimirMensaje(mensaje);
	}
}
