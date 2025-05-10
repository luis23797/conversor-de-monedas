package com.aluracursos.conversor.modelos;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Reporte {
    private String fecha;
    private HashMap<String,String> claves;
    private double cantidad;
    private double resultado;

    public Reporte(String claveOrigen, String claveDestino,double cantidad, double resultado){
        LocalDateTime momentoActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        fecha = momentoActual.format(formato);
        claves = new HashMap<>();
        claves.put("claveOrigen",claveOrigen);
        claves.put("claveDestino",claveDestino);
        this.cantidad = cantidad;
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "Conversion realizada el " + fecha
                + "\n" + "Se convirtio la cantidad de:" + cantidad + " " + claves.get("claveOrigen") + " a " + claves.get("claveDestino")
                + "\n" + "Con un resultado de:" + resultado + " " + claves.get("claveDestino");
    }

    public String getFecha() {
        return fecha;
    }

    public HashMap<String, String> getClaves() {
        return claves;
    }

    public double getCantidad() {
        return cantidad;
    }

    public double getResultado() {
        return resultado;
    }
}
