
package com.nutrimax.mscompras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class MsComprasApplication {

	public static void main(String[] args) {
		cargarEnv();
		SpringApplication.run(MsComprasApplication.class, args);
	}

	private static void cargarEnv() {
		Path envPath = Path.of(".env").toAbsolutePath();
		if (!Files.exists(envPath)) {
			return;
		}
		try {
			List<String> lineas = Files.readAllLines(envPath);
			for (String linea : lineas) {
				linea = linea.trim();
				if (linea.isEmpty() || linea.startsWith("#")) {
					continue;
				}
				int idx = linea.indexOf('=');
				if (idx > 0) {
					String clave = linea.substring(0, idx).trim();
					String valor = linea.substring(idx + 1).trim();
					System.setProperty(clave, valor);
				}
			}
		} catch (IOException e) {
			System.err.println("No se pudo leer el archivo .env: " + e.getMessage());
		}
	}
}