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
        for(String clave:Constantes.monedasIniciales){
            claves.add(clave.toUpperCase());
        }

        ArrayList<String> clavesJson = manejadorJson.obtenerClaves();
        for(String clave:clavesJson){
            claves.add(clave.toUpperCase());
        }
    }

    private Moneda obtenerMoneda(String clave){
        String json = consumidor.consultarMoneda(clave);
        return new Moneda(clave.toUpperCase(),json,claves);
    }
    private void inicializarMonedas(){
        Moneda moneda = null;
        for (String clave : claves) {
            moneda = obtenerMoneda(clave);
            if(moneda.getHashSize()>0) monedas.add(moneda);
        }
    }
    public Moneda buscarMoneda(String clave){
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
        String clave = "";
        try {
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
        if(moneda.getHashSize()<=0){
            System.out.println("La clave ingresada no es valida");
            return;
        }
        clave = clave.toUpperCase();
        moneda.actualizarHash(clave,1);
        for (Moneda actual:monedas){
            actual.actualizarHash(moneda);
        }
        claves.add(clave);
        monedas.add(moneda);
        manejadorJson.escribirJson(clave);

    }
    public void escribirReporte(String claveOrigen, String claveDestino, double cantidad, double resultado){
        Reporte reporte = new Reporte(claveOrigen,claveDestino,cantidad,resultado);
        manejadorJson.escribirReporte(reporte);
    }
    public void mostrarReportes(){
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
