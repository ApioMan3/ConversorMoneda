import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    private static final String API = "e60a8c2b672f78aa5b60c32a";
    private static final String[] MONEDAS = {"ARS", "USD", "EUR"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while(continuar){
            System.out.println("=== Conversor de Monedas ===");

            System.out.println("Monedas disponibles:");
            System.out.println("1 - ARS");
            System.out.println("2 - USD");
            System.out.println("3 - EUR");


            int opcionDesde = seleccionarMoneda(scanner, "Seleccione moneda origen");
            int opcionHacia = seleccionarMoneda(scanner, "Seleccione moneda destino");

            if (opcionDesde == opcionHacia) {
                System.out.println("La moneda origen y destino no pueden ser iguales.");
            } else{
                System.out.print("Cantidad a convertir: ");
                double cantidad = scanner.nextDouble();

                String monedaOrigen = MONEDAS[opcionDesde - 1];
                String monedaDestino = MONEDAS[opcionHacia - 1];

                double resultado = convertir(monedaOrigen, monedaDestino, cantidad);
                if (resultado >= 0) {
                    System.out.printf("%.2f %s = %.2f %s\n", cantidad, monedaOrigen, resultado, monedaDestino);
                } else {
                    System.out.println("No se pudo realizar la conversión.");
                }
                continuar = false;
                scanner.close();
            }
        }
    }

    private static int seleccionarMoneda(Scanner scanner, String mensaje) {
        int opcion;
        do {
            System.out.print(mensaje + " (1-3): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ingrese un número válido: ");
                scanner.next();
            }
            opcion = scanner.nextInt();
        } while (opcion < 1 || opcion > 3);
        return opcion;
    }

    public static double convertir(String desde, String hacia, double cantidad) {
        try {
            String urlStr = "https://v6.exchangerate-api.com/v6/" + API + "/latest/" + desde;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonElement json = JsonParser.parseReader(reader);
            reader.close();

            JsonObject obj = json.getAsJsonObject();
            String resultado = obj.get("result").getAsString();

            if (!resultado.equals("success")) {
                System.out.println("Error en la API: " + obj.get("error-type").getAsString());
                return -1;
            }

            JsonObject rates = obj.getAsJsonObject("conversion_rates");
            if (!rates.has(hacia)) {
                System.out.println("Moneda de destino no encontrada.");
                return -1;
            }

            double tasa = rates.get(hacia).getAsDouble();
            return cantidad * tasa;

        } catch (Exception e) {
            System.out.println("Error al conectar con la API: " + e.getMessage());
            return -1;
        }
    }
}
