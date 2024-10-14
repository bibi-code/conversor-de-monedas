import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Configuración del cliente HTTP
        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        try {
            // 2. Hacer la solicitud a la API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://open.er-api.com/v6/latest/USD")) // Cambia a tu endpoint de API
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            // 3. Obtener la respuesta de la API
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Verificar el código de estado de la respuesta
            if (response.statusCode() != 200) {
                System.out.println("Error: No se pudo obtener la respuesta de la API. Código de estado: " + response.statusCode());
                return;
            }

            // Imprimir la respuesta completa para ver la estructura
            System.out.println("Respuesta JSON: " + response.body());

            // Parsear la respuesta JSON
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!jsonObject.has("rates")) {
                System.out.println("Error: No se encontró la clave 'rates' en la respuesta JSON.");
                return;
            }

            JsonObject rates = jsonObject.getAsJsonObject("rates");

            // 5. Mostrar las opciones de conversión
            System.out.println("***************************");
            System.out.println("Bienvenido alChallenger: ¡Conversor de Monedas!");
            System.out.println("***************************");
            System.out.println("Opciones de conversión:");

            // Listar las tasas disponibles
            for (String currency : rates.keySet()) {
                System.out.println(currency + ": " + rates.get(currency).getAsDouble());
            }

            // Iniciar un bucle para las conversiones
            while (true) {
                System.out.println("****************************");
                System.out.println("\nElige una opción:");
                System.out.println("1. Realizar una conversión");
                System.out.println("2. Salir");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                if (option == 2) {
                    System.out.println("Saliendo del programa. ¡Hasta luego, te esperamos pronto!");
                    break; // Salir del bucle
                } else if (option == 1) {
                    System.out.println("Elige una moneda para convertir (o escribe 'salir' para terminar): ");
                    String fromCurrency = scanner.nextLine().toUpperCase();

                    if (fromCurrency.equals("SALIR")) {
                        break; // Salir del bucle
                    }

                    System.out.println("Ingresa la cantidad a convertir, valor numérico: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // Consumir el salto de línea

                    System.out.println("A qué moneda deseas convertir? ");
                    String toCurrency = scanner.nextLine().toUpperCase();

                    // 6. Realizar la conversión
                    if (rates.has(fromCurrency) && rates.has(toCurrency)) {
                        double fromRate = rates.get(fromCurrency).getAsDouble();
                        double toRate = rates.get(toCurrency).getAsDouble();
                        double convertedAmount = (amount / fromRate) * toRate;

                        System.out.println("El resultado es:");
                        System.out.printf("%.2f %s son %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
                    } else {
                        System.out.println("Error: Una de las monedas no está disponible.");
                    }
                } else {
                    System.out.println("Opción no válida. Intenta de nuevo.");
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close(); // Cerrar el escáner
        }
    }
}
