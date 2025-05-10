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
    private String directorioReporte;
    public ManejadorJson(String nombreDelArchivo){
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
            if(archivo.exists()){
                    Scanner scanner = new Scanner(archivo);
                    while (scanner.hasNextLine()){
                        aux += scanner.nextLine();
                    }
                    try{
                        clavesDelJson = gson.fromJson(aux,ArrayList.class);
                        List<String> listaClavesIniciales = Arrays.asList(Constantes.monedasIniciales);
                        if(listaClavesIniciales.contains(clave) || clavesDelJson.contains(clave)){
                            System.out.println("Moneda ya existente");
                            return;
                        }
                        clavesDelJson.add(clave);
                    }catch (NullPointerException | JsonSyntaxException e){
                        clavesDelJson = new ArrayList<>();
                        clavesDelJson.add(clave);
                    }
                    FileWriter escrituraNueva = new FileWriter(directorio);
                    escrituraNueva.write(gson.toJson(clavesDelJson));
                    escrituraNueva.close();
                    System.out.println("Moneda agregada Exitosamente");
            }else{
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


