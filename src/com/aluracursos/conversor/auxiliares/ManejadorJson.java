package com.aluracursos.conversor.auxiliares;

import com.aluracursos.conversor.modelos.Reporte;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ManejadorJson {
    private Gson gson;
    private String directorio;
    public ManejadorJson(String nombreDelArchivo){
        // Se instancia el gson para la serializacion y deserializacion
        this.directorio = nombreDelArchivo;
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();
        
    }
    public void escribirJson(String clave) {
        clave = clave.toLowerCase();
        ArrayList<String> clavesDelJson;
        File archivo = new File(directorio);
        String aux= "";
        try{
            // Se comprueba la existencia del archivo
            if(archivo.exists()){
                    Scanner scanner = new Scanner(archivo);
                    while (scanner.hasNextLine()){
                        aux += scanner.nextLine();
                    }
                    try{
                        clavesDelJson = gson.fromJson(aux,ArrayList.class);
                        List<String> listaClavesIniciales = Arrays.asList(Constantes.monedasIniciales);
                        // Se verifica que la clave a escribir no este presente en las claves estaticas o en las del archivo
                        // Despues se agrega la nueva clave de moneda en un arraylist que contiene las claves agregadas anteriormente
                        if(listaClavesIniciales.contains(clave) || clavesDelJson.contains(clave)){
                            System.out.println("Moneda ya existente");
                            return;
                        }
                        clavesDelJson.add(clave);
                    }catch (NullPointerException | JsonSyntaxException e){
                        clavesDelJson = new ArrayList<>();
                        clavesDelJson.add(clave);
                    }
                    // Finalmente se serializa el arraylist para escribirlo en el archivo
                    FileWriter escrituraNueva = new FileWriter(directorio);
                    escrituraNueva.write(gson.toJson(clavesDelJson));
                    escrituraNueva.close();
                    System.out.println("Moneda agregada Exitosamente");
            }else{
                // En caso de que no exista el archivo se crea uno y se agrega un arraylist que contiene solamente la clave nueva
                FileWriter escritura = new FileWriter(directorio);
                clavesDelJson = new ArrayList<>();
                clavesDelJson.add(clave);
                escritura.write(gson.toJson(clavesDelJson));
                escritura.close();
                System.out.println("Moneda agregada Exitosamente");
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public ArrayList<String> obtenerClaves(){
        // Se crea un ArrayList para almacenar las claves
        ArrayList<String> clavesDelJson = new ArrayList<>();
        File archivo = new File(directorio);
        String aux= "";
        try{
            if(archivo.exists()){
                Scanner scanner = new Scanner(archivo);
                while (scanner.hasNextLine()){
                    aux += scanner.nextLine();
                }
                try{
                    // Despues de leer el archivo se convierte en un ArrayList de Strings que contenga las claves
                    clavesDelJson = gson.fromJson(aux,ArrayList.class);
                    if (clavesDelJson==null) return new ArrayList<>();
                }catch (NullPointerException | JsonSyntaxException e){
                    clavesDelJson = new ArrayList<>();
                }
            }
            return clavesDelJson;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void escribirReporte(Reporte reporte) {
        //Similar a la escritura de claves solo que se escribe un Reporte
        ArrayList<Reporte> reportes;
        File archivo = new File(Constantes.DirectorioReporteJson);
        String aux= "";
        try{
            if(archivo.exists()){
                Scanner scanner = new Scanner(archivo);
                while (scanner.hasNextLine()){
                    aux += scanner.nextLine();
                }
                try{
                    reportes = gson.fromJson(aux,ArrayList.class);
                    reportes.add(reporte);
                }catch (NullPointerException | JsonSyntaxException e){
                    reportes = new ArrayList<>();
                    reportes.add(reporte);
                }
                FileWriter escrituraNueva = new FileWriter(Constantes.DirectorioReporteJson);
                escrituraNueva.write(gson.toJson(reportes));
                escrituraNueva.close();

            }else{
                FileWriter escritura = new FileWriter(Constantes.DirectorioReporteJson);
                reportes = new ArrayList<>();
                reportes.add(reporte);
                escritura.write(gson.toJson(reportes));
                escritura.close();

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Reporte> obtenerReportes(){
        // Similar a la lectura de las llaves solo que se utilizan los generics para leer un arraylist de Reportes
        ArrayList<Reporte> reportes = new ArrayList<>();
        File archivo = new File(Constantes.DirectorioReporteJson);
        String aux= "";
        try{
            if(archivo.exists()){
                Scanner scanner = new Scanner(archivo);
                while (scanner.hasNextLine()){
                    aux += scanner.nextLine();
                }
                try{
                    Type tipo = new TypeToken<List<Reporte>>() {}.getType();
                    reportes = gson.fromJson(aux,tipo);
                    if (reportes==null) return new ArrayList<>();
                }catch (NullPointerException | JsonSyntaxException e){
                    reportes = new ArrayList<>();
                }
            }
            return reportes;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}


