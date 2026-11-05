package com.trullo.frontend;

import com.trullo.core.ErrorReporter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carga el logo PNG de Trullo y lo entrega escalado para la barra lateral, la bienvenida, los
 * estados vacíos y el ícono de la ventana.
 */
public final class BrandIcon {

    // Ruta del PNG dentro del classpath y rutas de respaldo cuando se ejecuta desde las fuentes.
    private static final String RESOURCE_PATH = "/com/trullo/recursos/icono.png";
    private static final Path[] SOURCE_PATHS = {
            Path.of("src", "main", "java", "com", "trullo", "recursos", "icono.png"),
            Path.of("Trullo", "src", "main", "java", "com", "trullo", "recursos", "icono.png")
    };

    // El PNG original se lee una sola vez y cada tamaño se guarda ya escalado.
    private static final Map<Integer, ImageIcon> ICON_CACHE = new ConcurrentHashMap<>();
    private static BufferedImage sourceImage;
    private static boolean sourceLoaded;

    /**
     * Clase de utilidades: no se instancia.
     */
    private BrandIcon() {
    }

    /**
     * Devuelve el logo escalado al tamaño pedido, listo para mostrar en la interfaz.
     */
    public static ImageIcon at(int size) {
        return ICON_CACHE.computeIfAbsent(size, BrandIcon::scale);
    }

    /**
     * Devuelve el logo como imagen escalada, por ejemplo para el ícono de la ventana.
     */
    public static BufferedImage image(int size) {
        return scaleImage(size);
    }

    /**
     * Escala el PNG original con interpolación suave para que no se vea pixelado.
     */
    private static ImageIcon scale(int size) {
        return new ImageIcon(scaleImage(size));
    }

    /**
     * Genera el logo ya reescalado al tamaño pedido.
     */
    private static BufferedImage scaleImage(int size) {
        BufferedImage source = loadSource();
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        graphics2D.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.drawImage(source, 0, 0, size, size, null);
        graphics2D.dispose();

        return scaled;
    }

    /**
     * Busca el PNG primero en el classpath y después en la carpeta de fuentes del proyecto.
     */
    private static BufferedImage loadSource() {
        synchronized (BrandIcon.class) {
            if (sourceLoaded) {
                return sourceImage;
            }
            sourceLoaded = true;

            sourceImage = readFromClasspath();
            if (sourceImage == null) {
                sourceImage = readFromSourcePaths();
            }
            if (sourceImage == null) {
                // Deja una marca sencilla para que la app pueda abrir aunque falte el PNG.
                System.err.println("No se encontró icono.png; se usa una marca de reemplazo.");
                sourceImage = createFallbackImage();
            }
            return sourceImage;
        }
    }

    /**
     * Dibuja un cuadrado redondeado con el color de acento como respaldo del logo.
     */
    private static BufferedImage createFallbackImage() {
        int size = 128;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setColor(Theme.accent());
        graphics2D.fillRoundRect(0, 0, size, size, 36, 36);
        graphics2D.dispose();

        return image;
    }

    /**
     * Intenta leer el PNG desde los recursos empaquetados de la aplicación.
     */
    private static BufferedImage readFromClasspath() {
        try (InputStream stream = BrandIcon.class.getResourceAsStream(RESOURCE_PATH)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (IOException exception) {
            ErrorReporter.report("No se pudo leer el icono de Trullo", exception);
            return null;
        }
    }

    /**
     * Intenta leer el PNG directamente desde el árbol de fuentes mientras se ejecuta en el IDE.
     */
    private static BufferedImage readFromSourcePaths() {
        for (Path path : SOURCE_PATHS) {
            BufferedImage image = readFromFile(path);
            if (image != null) {
                return image;
            }
        }
        return readFromParentDirectories();
    }

    /**
     * Lee el PNG si existe en la ruta recibida.
     */
    private static BufferedImage readFromFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (InputStream stream = Files.newInputStream(path)) {
            return ImageIO.read(stream);
        } catch (IOException exception) {
            ErrorReporter.report("No se pudo leer el icono de Trullo", exception);
            return null;
        }
    }

    /**
     * Sube desde el directorio de trabajo hasta la raíz buscando el PNG, por si la app se lanza
     * desde una subcarpeta.
     */
    private static BufferedImage readFromParentDirectories() {
        Path directory = Path.of("").toAbsolutePath();
        Path nested = Path.of("src", "main", "java", "com", "trullo", "recursos", "icono.png");

        while (directory != null) {
            BufferedImage image = readFromFile(directory.resolve(nested));
            if (image != null) {
                return image;
            }
            image = readFromFile(directory.resolve("Trullo").resolve(nested));
            if (image != null) {
                return image;
            }
            directory = directory.getParent();
        }
        return null;
    }
}
