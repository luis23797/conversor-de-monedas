package com.aluracursos.conversor.auxiliares;

import com.aluracursos.conversor.modelos.Moneda;
import com.aluracursos.conversor.modelos.Reporte;

import java.util.ArrayList;

public class Conversor {
    private ArrayList<Moneda> monedas;
    private ArrayList<String> claves;
    private ManejadorJson manejadorJson;
    private ApiConsumidor consumidor;
    public Conversor(){
        //Instancias de la clase que consulta la API y la que escribe archivos y hace serializacion
        consumidor = new ApiConsumidor(Constantes.ApiKey);
        manejadorJson = new ManejadorJson(Constantes.DirectorioArchivoJson);
        claves = new ArrayList<>();
        monedas = new ArrayList<>();
        inicializarClaves();
        inicializarMonedas();
    }
    public void imprimirMenu(int opc,String clave){
        ArrayList<String> clavesJson = manejadorJson.obtenerClaves();
        int index = 0;
        if(opc==1) {
            System.out.print(""" 
                    ===================Bienvenido al conversor de monedas=========================
                    Elige una de las siguientes opciones de conversion
                    1)ARS - Conversion de Peso argentino
                    2)BOB - Conversion de Boliviano boliviano
                    3)BRL - Conversion de Real brasileño
                    4)CLP - Conversion de peso chileno
                    5)COP - Conversion de peso colombiano
                    6)USD - Conversion de dolar estadounidense
                    """);
            index = Constantes.monedasIniciales.length+1;
            for (String claveJson:clavesJson){
                System.out.println(claveJson.indexOf(claveJson) + index + ")Conversion de " + " - " +claveJson.toUpperCase());
                index++;
            }
            System.out.println("m) Mostrar reportes");
            System.out.println("a) Agregar moneda");
            System.out.println("s) Salir");
        }
        if(opc==2){
            System.out.printf("""
                    1)Conversion de %s a Peso argentino
                    2)Conversion de %s a Boliviano boliviano
                    3)Conversion de %s a Real brasileño
                    4)Conversion de %s a peso chileno
                    5)Conversion de %s a peso colombiano
                    6)Conversion de %s a dolar estadounidense
                    """, clave,clave,clave,clave,clave,clave);
            index = Constantes.monedasIniciales.length+1;
            for (String claveJson:clavesJson){
                System.out.println(claveJson.indexOf(claveJson) + index + ")Conversion de " + clave + " a " + claveJson.toUpperCase());
                index++;
            }
        }
    }

    private void inicializarClaves(){
        // Se introducen todas las claves de moneda, tanto las estaticas como las del archivo de claves
        for(String clave:Constantes.monedasIniciales){
            claves.add(clave.toUpperCase());
        }

        ArrayList<String> clavesJson = manejadorJson.obtenerClaves();
        for(String clave:clavesJson){
            claves.add(clave.toUpperCase());
        }
    }

    private Moneda obtenerMoneda(String clave){
        // Consulta la clave mandada como parametro a la APi
        String json = consumidor.consultarMoneda(clave);
        return new Moneda(clave.toUpperCase(),json,claves);
    }
    private void inicializarMonedas(){
        // Crea monedas por cada una de las claves existentes en el arraylist de claves y las agrega a un arraylist
        Moneda moneda = null;
        for (String clave : claves) {
            moneda = obtenerMoneda(clave);
            if(moneda.getHashSize()>0) monedas.add(moneda); //En caso de que la moneda no tenga claves se omite
        }
    }
    public Moneda buscarMoneda(String clave){
        // Busca una moneda especifica almacenada en el arraylist
        Moneda resultado = null;
        for(Moneda moneda:monedas){
            if(moneda.getNombre().equals(clave)){
                resultado = moneda;
                break;
            }
        }
        return resultado;
    }
    public String ObtenerClave(String opc){
        // Busca una clave de moneda dentro de la lista de claves
        String clave = "";
        try {
            // Verifica si el numero proporcionado corresponde a la posicion de una clave disponible
            int aux = Integer.parseInt(opc) - 1;
            if(aux<=claves.size()-1 && aux>=0){
                clave = claves.get(aux);
            }
        } catch (NumberFormatException e) {
            System.out.println("La opcion introducida es invalida");
        }
        return clave;
    }
    public void agregarMoneda(String clave){
        clave = clave.toLowerCase();
        Moneda moneda = obtenerMoneda(clave);
        // Verifica si la clave proporcionada tiene una respuesta valida de la API
        if(moneda.getHashSize()<=0){
            System.out.println("La clave ingresada no es valida");
            return;
        }
        //Agrega la clave proporcionada a la misma moneda y despues se agrega a las demas monedas existentes
        clave = clave.toUpperCase();
        moneda.actualizarHash(clave,1);
        for (Moneda actual:monedas){
            actual.actualizarHash(moneda);
        }
        // Se agrega la nueva clave y se agrega la moneda a la coleccion de monedas y se escribe la moneda en el archivo
        claves.add(clave);
        monedas.add(moneda);
        manejadorJson.escribirJson(clave);

    }
    public void escribirReporte(String claveOrigen, String claveDestino, double cantidad, double resultado){
        //Crea un reporte que sera escrito en el archivo utilizando los datos de la conversion
        Reporte reporte = new Reporte(claveOrigen,claveDestino,cantidad,resultado);
        manejadorJson.escribirReporte(reporte);
    }
    public void mostrarReportes(){
        // Obtiene los reportes existentes en el archivo de reportes y los imprime
        ArrayList<Reporte> reportes = manejadorJson.obtenerReportes();
        if(reportes.isEmpty()){
            System.out.println("No hay reportes");
            return;
        }
        for (Reporte reporte:reportes){
            System.out.println(reporte + "\n");
        }
    }
}
