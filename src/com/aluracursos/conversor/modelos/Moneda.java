package com.aluracursos.conversor.modelos;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;

public class Moneda {
    private String nombre;
    private HashMap<String,Double> cambios;

    public Moneda(String nombre,String json,ArrayList<String> claves){
        this.nombre = nombre;
        cambios = new HashMap<>();
//        cambios.put("ARS",rates.conversion_rates().ARS());
//        cambios.put("BOB",rates.conversion_rates().BOB());
//        cambios.put("BRL",rates.conversion_rates().BRL());
//        cambios.put("CLP",rates.conversion_rates().CLP());
//        cambios.put("COP",rates.conversion_rates().COP());
//        cambios.put("USD",rates.conversion_rates().USD());
        try{
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject().get("conversion_rates").getAsJsonObject();
            for (String clave:claves){
                cambios.put(clave,obj.get(clave).getAsDouble());
            }

        }catch (NullPointerException | JsonSyntaxException e){
            System.out.println("La clave:"+nombre + " es invalida");
        }

    }

    public double obtenerCambio(String codigo, double cantidad){
        return cambios.get(codigo) * cantidad;
    }

    public String getNombre() {
        return nombre;
    }
    public int getHashSize(){
        return  this.cambios.size();
    }
    public void actualizarHash(Moneda moneda){
        cambios.put(moneda.getNombre().toUpperCase(),moneda.obtenerCambio(nombre.toUpperCase(),1));
    }
    public void actualizarHash(String clave, double valor){
        cambios.put(clave.toUpperCase(),valor);
    }

}
