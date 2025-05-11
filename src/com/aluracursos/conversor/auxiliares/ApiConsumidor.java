package com.aluracursos.conversor.auxiliares;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiConsumidor {
    private HttpClient client;
    private HttpResponse<String> response;
    private HttpRequest request;
    private String url;

    public ApiConsumidor(String apiKey){
        url = "https://v6.exchangerate-api.com/v6/"+apiKey+"/";
    }
    public String consultarMoneda(String moneda){
        // Se crea la url de consulta a partir de la url de la API y la APIkey y se manda la peticion
        String consulta =url + "latest/" + moneda;

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create(consulta))
                .build();
        try {
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

}
