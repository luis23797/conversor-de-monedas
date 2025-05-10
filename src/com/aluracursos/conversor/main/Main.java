package com.aluracursos.conversor.main;

import com.aluracursos.conversor.auxiliares.Conversor;
import com.aluracursos.conversor.modelos.Moneda;
import com.aluracursos.conversor.modelos.Reporte;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Conversor conversor = new Conversor();
        Scanner teclado = new Scanner(System.in);
        double resultado = 0, cantidad = 0;
        String claveOrigen = "", claveDestino = "";
        Moneda moneda = null;
        String opc= "";
        while (!opc.equals("s")){
            conversor.imprimirMenu(1,"");
            opc = teclado.nextLine();
            switch (opc) {
                case "s" -> {
                    continue;
                }
                case "a" -> {
                    System.out.println("Introduce una clave valida de moneda (Consulte las claves en ExchangeRate)");
                    conversor.agregarMoneda(teclado.nextLine());
                    continue;
                }
                case "m" -> {
                    conversor.mostrarReportes();
                    continue;
                }
            }
            claveOrigen = conversor.ObtenerClave(opc);
            if(claveOrigen.isEmpty()) continue;
            conversor.imprimirMenu(2,claveOrigen);
            claveDestino = conversor.ObtenerClave(teclado.nextLine());
            if(claveDestino.isEmpty()) continue;
            moneda = conversor.buscarMoneda(claveOrigen);
            System.out.println("Introduce la cantidad a convertir");
            try {
                cantidad = Double.parseDouble(teclado.nextLine());
                resultado = moneda.obtenerCambio(claveDestino,cantidad);
                System.out.println("El resultado de la conversion de:"+ cantidad + " " + claveOrigen + " a " + claveDestino + " es de:" + String.format("%.4f",resultado));
                conversor.escribirReporte(claveOrigen,claveDestino,cantidad,resultado);
            } catch (NumberFormatException e) {
                System.out.println("Cantidad introducida invalida");
            }
            }
        }
}
