# Caracteristicas de la aplicacion
El conversor de monedas proporciona diferentes opciones las cuales son:
* Obtener el resultado de una conversion de una moneda a otra
* Agregar una nueva moneda 
* Mostrar reportes de conversiones anteriores

El conversor de monedas cuenta con algunas claves de monedas ya predefinidas, estas estan almacenadas en un arreglo en el archivo de constantes, asi como la ruta deseada donde se escriben las claves que se agregan y la ruta para los reportes.
(Para conocer las claves validas disponibles, consulte la API exchange)

Aunque la API key esta expuesta, en este caso debido a que esta aplicacion es para propositos academicos, se ha decidido exponerla para que sea mas sencillo probar la aplicacion.

La logica de la aplicacion consta de conectar con la API exchange, sin embargo para poder almacenar las monedas obtenidas de la API y realizar operaciones con ellas, se definio un modelo para las monedas.
Este modelo contiene un HashMap con las claves de todas las monedas utilizadas en la aplicacion ( las claves que estan definidas en el archivo de constantes y las agregadas por el usuario)  para asi realizar cambios entre cada una de las monedas disponibles 

La clase Moneda tiene su propio metodo que calcula el cambio entre la moneda deseada

    public class Moneda {
    private String nombre;
    private HashMap<String,Double> cambios;

    public Moneda(String nombre,String json,ArrayList<String> claves){
        this.nombre = nombre;
        cambios = new HashMap<>();
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
    }



Cada que se necesita obtener los valores de las tazas de cambio de una moneda se manda a llamar la funcion consultarMoneda

     public String consultarMoneda(String moneda){
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
Mediante esta funcion se hace una peticion a la API para obtener las tazas de cambio de una clave. 
El json obtenido de la consulta es el que se utiliza para poder instanciar cada moneda.

La gestion de la logica de la aplicacion esta llevada a cabo por la clase Conversor.
Esta clase gestiona una lista que contiene objetos de tipo Moneda, para asi poder realizar operaciones con los datos de cada moneda sin necesidad de estar consultando constantemente la API

Para hacer el llenado de la lista de monedas la clase Conversor manda a llamar varios metodos al momento de instanciarse, el constructor hace procesos en cadena para poder finalmente tener un ArrayList que contenga monedas. 

     public Conversor(){
        consumidor = new ApiConsumidor(Constantes.ApiKey);
        manejadorJson = new ManejadorJson(Constantes.DirectorioArchivoJson);
        claves = new ArrayList<>();
        monedas = new ArrayList<>();
        inicializarClaves();
        inicializarMonedas();
    }

Una de las opciones extra es la de agregar monedas, para ello el Conversor consulta a la API la clave proporcionada, en caso de que sea invalida se descarta, en caso contrario se le agrega su propio codigo a la moneda en su HashMap, despues para no redefinir cada moneda, se actualiza manualmente todas las monedas utilizando los valores de conversion de la nueva moneda y finalmente se escribe en el archivo de claves, para que la siguiente vez que se abra el programa se haga la consulta de la nueva clave de moneda

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
Para la gestion de la lectura y escritura de archivos se utilizo la clase ManejadorJson
Mediante esta clase se escribe en archivos con extension json las claves de monedas agregadas por el usuario, asi como tambien se encarga de la escritura de los reportes creados cada vez que se hace una conversion de moneda

La escritura de las claves en el archivo claves.json se realizo implementando la funcion siguiente

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

Esta funcion recibe una clave, despues verifica si existe el archivo donde debe de escribir para en cuyo caso de no existir, este sea creado. 

En terminos generales esta funcion verifica si la clave recibida como parametro ya esta incluida, en caso de que no sea asi: se crea un ArrayList a partir del contenido existente en el Json y se agrega la nueva clave. Finalmente se convierte el ArrayList en un Json para almacenar las claves.

Otra de las opciones extra es la de escribir reportes de las conversiones realizadas, para ello se crea un Objeto de tipo Reporte, los reportes tienen una estructura sencilla la cual es

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
    }
    

Estos son los atributos de la clase Reporte, para poder instanciar un reporte se utilizan los datos de la conversion y se guarda el momento en el que se consulto la conversion utilizando la libreria Time de Java.
Para la escritura de reportes se hace un proceso muy similar a la escritura de las claves de moneda.  Para ello se utiliza la funcion escribirReporte, sin embargo en este caso se escribe un ArrayList de objetos tipo Reporte
