package gitlet.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    static final Path WORK_DIR = Paths.get("").toAbsolutePath();
    private static final Path SRC = WORK_DIR.resolve("testing").resolve("src");

    public static void delete(Path path) {
        try {
            if (Files.exists(path) && Files.isDirectory(path)) {
                for (Path subPath : Files.newDirectoryStream(path)) {
                    delete(subPath);
                }
            }
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void copyFile(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(SRC.resolve(fileName));
            Files.write(Paths.get("")
                    .toAbsolutePath().resolve(fileName), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void copyFile(String target, String src) {
        try {
            byte[] bytes = Files.readAllBytes(SRC.resolve(src));
            Files.write(Paths.get("").toAbsolutePath().resolve(target), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void rewrite(Path target, Path src) {
        try {
            byte[] contents = Files.readAllBytes(src);
            Files.write(target, contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void predelete() {
        delete(Paths.get("").toAbsolutePath().resolve(".gitlet"));
        delete(Paths.get("").toAbsolutePath().resolve("f.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("g.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("k.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("h.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("wug.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("wug1.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("wug2.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("wug3.txt"));
        delete(Paths.get("").toAbsolutePath().resolve("notuwg.txt"));
    }

    static boolean compareBytes(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    static boolean compareFiles(String work, String src) {
        try {
            return compareBytes(
                    Files.readAllBytes(
                            WORK_DIR.resolve(work)),
                    Files.readAllBytes(
                            WORK_DIR.resolve("testing/src").resolve(src)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static boolean exists(String fileName) {
        return Files.exists(WORK_DIR.resolve(fileName));
    }

}
