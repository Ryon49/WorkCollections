package gitlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/**
 * Assorted utilities.
 *
 * @author P. N. Hilfinger
 */
public class Utils {

    /* SHA-1 HASH VALUES. */

    /**
     * The length of a complete SHA-1 UID as a hexadecimal numeral.
     */
    public static final int UID_LENGTH = 40;

    /**
     * Returns the SHA-1 hash of the concatenation of VALS, which may
     * be any mixture of byte arrays and Strings.
     */
    public static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /**
     * @param obj input.
     * @return the SHA-1 hash of the concatenation of obj.
     */
    public static String sha1(Serializable obj) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(serialize(obj));

            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /**
     * Returns the SHA-1 hash of the concatenation of the strings in
     * VALS.
     */
    public static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* READING AND WRITING FILE CONTENTS */

    /**
     * Return the entire contents of FILE as a byte array.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     *
     * @param path target path.
     */
    public static byte[] readContents(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new GitletException("must be a normal file");
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException excp) {
            throw new GitletException(excp.getMessage());
        }
    }

    /**
     * Return the entire contents of FILE as a String.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     *
     * @param path target path.
     */
    public static String readContentsAsString(Path path) {
        return new String(readContents(path), StandardCharsets.UTF_8);
    }

    /**
     * write contents to destination path.
     * @param path dest.
     * @param contents content.
     */
    public static void writeContents(Path path, Object... contents) {
        try {
            if (Files.isDirectory(path)) {
                throw new IllegalArgumentException(
                        "cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(path));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     *
     * read object from input path.
     * @param path target path.
     * @param expectedClass expected class.
     * @param <T> generic type.
     * @return object.
     */
    public static <T extends Serializable> T readObject(
            Path path, Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(Files.newInputStream(path));

            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            return null;
        }
    }

    /**
     * Write OBJ to FILE.
     * @param path target path.
     * @param obj object.
     */
    public static void writeObject(Path path, Serializable obj) {
        writeContents(path, serialize(obj));
    }

    /* DIRECTORIES */

    /**
     * Filter out all but plain files.
     */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    public static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    public static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    public static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    public static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* MESSAGES AND ERROR REPORTING */

    /**
     * Return a GitletException whose message is composed from MSG and ARGS as
     * for the String.format method.
     */
    public static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /**
     * Print a message composed from MSG and ARGS as for the String.format
     * method, followed by a newline.
     */
    public static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

    /** FUNCTIONS */

    /**
     * Represents a function from T1 -> T2.  The apply method contains the
     * code of the function.  The 'foreach' method applies the function to all
     * items of an Iterable.  This is an interim class to allow use of Java 7
     * with Java 8-like constructs.
     */
    public abstract static class Function<T1, T2> {
        /**
         * Returns the value of this function on X.
         */
        abstract T2 apply(T1 x);
    }

    /**
     * Date formatter.
     */
    private static SimpleDateFormat formatter
            = new SimpleDateFormat("EEE MMM d HH:mm:ss YYYY Z");

    /**
     * @param date input.
     * @return String representation of date.
     */
    public static String format(Instant date) {
        return formatter.format(java.util.Date.from(date));
    }

}
