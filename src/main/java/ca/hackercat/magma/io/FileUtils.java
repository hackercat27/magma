package ca.hackercat.magma.io;

import ca.hackercat.magma.core.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ca.hackercat.logging.Logger.LOGGER;


public class FileUtils {

    public static String getContents(String path) {
        InputStream is = getInputStream(path);
        if (is == null) {
            LOGGER.warn("Attempted to read contents of '" + path + "', but file doesn't exist!");
            return "";
        }
        Scanner scan = new Scanner(is);
        StringBuilder b = new StringBuilder();
        while (scan.hasNextLine()) {
            b.append(scan.nextLine()).append("\n");
        }
        return b.toString();
    }

    private static boolean isResource(String path) {
        return path.charAt(0) == '/';
    }

    public static InputStream getInputStream(String path) {
        if (isResource(path)) {
            return FileUtils.class.getResourceAsStream(path);
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Mesh loadWavefrontOBJ(String path, String texturePath) {
        Mesh mesh = loadWavefrontOBJ(path);
        mesh.setTexture(new Texture(texturePath));
        return mesh;
    }
    public static Mesh loadWavefrontOBJ(String path) {
        String contents = getContents(path);

        String[] lines = contents.split("\n");

        List<Vector3f> vertices = new ArrayList<>(0);
        List<Vector2f> textures = new ArrayList<>(0);
        List<Vector3f> normals = new ArrayList<>(0);
        List<Integer> indices = new ArrayList<>(0);

        Vector3f[] verticesArray;
        Vector3f[] normalsArray;
        Vector2f[] textureArray;
        int[] indicesArray;

        // TODO: this is kindof a braindead way of parsing the file. I mean, it works, but please fix it oh god
        for (String line : lines) {
            String[] splitLine = line.split(" ");

            if (line.startsWith("v ")) {
                Vector3f vertex = new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
                vertices.add(vertex);
            }
            else if (line.startsWith("vt ")) {
                Vector2f texture = new Vector2f(Float.parseFloat(splitLine[1]), 1 - Float.parseFloat(splitLine[2]));
                textures.add(texture);
            }
            else if (line.startsWith("vn ")) {
                Vector3f normal = new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
                normals.add(normal);
            }
            else if (line.startsWith("f ")) {
                break;
            }

        }

        textureArray = new Vector2f[vertices.size()];
        normalsArray = new Vector3f[vertices.size()];


        for (String line : lines) {
            if (!line.startsWith("f ")) {
                continue;
            }

            String[] splitLine = line.split(" ");
            String[] v1 = splitLine[1].split("/");
            String[] v2 = splitLine[2].split("/");
            String[] v3 = splitLine[3].split("/");

            processVertex(v1, indices, textures, normals, textureArray, normalsArray);
            processVertex(v2, indices, textures, normals, textureArray, normalsArray);
            processVertex(v3, indices, textures, normals, textureArray, normalsArray);

        }

        verticesArray = new Vector3f[vertices.size()];
        indicesArray = new int[indices.size()];

        for (int i = 0; i < verticesArray.length; i++) {
            verticesArray[i] = vertices.get(i);
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return new Mesh(verticesArray, textureArray, normalsArray, indicesArray);
    }
    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                       List<Vector3f> normals, Vector2f[] textureArray, Vector3f[] normalsArray) {

        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        textureArray[currentVertexPointer] = textures.get(Integer.parseInt(vertexData[1]) - 1);
        normalsArray[currentVertexPointer] = normals.get(Integer.parseInt(vertexData[2]) - 1);

    }
}
