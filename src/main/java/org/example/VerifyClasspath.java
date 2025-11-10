package org.example;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class VerifyClasspath {

        public static void main(String[] args) {
            System.out.println("=== VERIFICACIÓN DE CLASSPATH ===\n");

            // 1. Verificar si persistence.xml está accesible
            System.out.println("1. Buscando persistence.xml...");
            URL persistenceUrl = VerifyClasspath.class
                    .getClassLoader()
                    .getResource("META-INF/persistence.xml");

            if (persistenceUrl != null) {
                System.out.println("   ENCONTRADO en: " + persistenceUrl);
            } else {
                System.out.println("   NO ENCONTRADO");
                System.out.println("   El archivo debe estar en: src/main/resources/META-INF/persistence.xml");
            }

            // 2. Listar todos los archivos persistence.xml en el classpath
            System.out.println("\n2. Todos los persistence.xml en classpath:");
            try {
                Enumeration<URL> resources = VerifyClasspath.class
                        .getClassLoader()
                        .getResources("META-INF/persistence.xml");

                int count = 0;
                while (resources.hasMoreElements()) {
                    count++;
                    URL url = resources.nextElement();
                    System.out.println("   - " + url);
                }

                if (count == 0) {
                    System.out.println("   No se encontró ningún persistence.xml");
                } else {
                    System.out.println("   Se encontraron " + count + " archivo(s)");
                }
            } catch (IOException e) {
                System.out.println("  Error al buscar: " + e.getMessage());
            }

            // 3. Verificar directorio de trabajo
            System.out.println("\n3. Directorio de trabajo actual:");
            System.out.println("   " + System.getProperty("user.dir"));

            // 4. Verificar classpath completo
            System.out.println("\n4. Classpath completo:");
            String classpath = System.getProperty("java.class.path");
            String[] entries = classpath.split(System.getProperty("path.separator"));
            for (String entry : entries) {
                System.out.println("   - " + entry);
            }

            System.out.println("\n=== FIN DE VERIFICACIÓN ===");
        }
}
