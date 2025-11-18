package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private static final String DEFAULT_STATIC_DIR = "static";
    private static final int PORT = 8080;
    private static final String DEFAULT_FILE = "index.html";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CHARSET_UTF8 = "charset=UTF-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public static void main(String[] args) throws IOException {
        final String staticDir = args.length > 0 ? args[0] : DEFAULT_STATIC_DIR;
        final Path staticPath = findStaticDirectory(staticDir);

        if (staticPath == null) {
            System.err.println("Error: Directory '" + staticDir + "' does not exist or is not a directory");
            System.exit(1);
        }

        System.out.println("Server started at http://localhost:" + PORT);
        System.out.println("Serving files from: " + staticPath);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (!serverSocket.isClosed()) {
                        serverSocket.close();
                        System.out.println("Server stopped");
                    }
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
            }));

            while (true) {
                final Socket clientSocket = serverSocket.accept();
                try {
                    handleRequest(clientSocket, staticPath);
                } catch (Exception e) {
                    System.err.println("Error handling request: " + e.getMessage());
                } finally {
                    clientSocket.close();
                }
            }
        }
    }

    private static void handleRequest(Socket clientSocket, Path staticPath) throws IOException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            final String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("Request: " + requestLine);

            final String[] requestParts = requestLine.split("\\s+", 3);
            if (requestParts.length < 2) {
                sendErrorResponse(out, 400, "Bad Request");
                return;
            }

            final String urlPath = requestParts[1];
            skipHeaders(in);

            String fileName = extractFileName(urlPath);
            if (fileName == null || fileName.isEmpty()) {
                fileName = DEFAULT_FILE;
            }

            final Path filePath = staticPath.resolve(fileName).normalize();

            if (!filePath.startsWith(staticPath)) {
                System.out.println("Security check failed: Attempted path traversal");
                sendErrorResponse(out, 403, "Forbidden");
                return;
            }

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                System.out.println("File not found: " + filePath);
                sendErrorResponse(out, 404, "Not Found");
                return;
            }

            final byte[] fileContent = Files.readAllBytes(filePath);
            final String contentType = getContentType(fileName);

            sendSuccessResponse(out, contentType, fileContent.length);

            final OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(fileContent);
            outputStream.flush();

            System.out.println("Served file: " + fileName + " (" + fileContent.length + " bytes)");
        }
    }

    private static void skipHeaders(BufferedReader in) throws IOException {
        String line = in.readLine();
        while (line != null && !line.isEmpty()) {
            line = in.readLine();
        }
    }

    private static String extractFileName(String urlPath) {
        final int queryIndex = urlPath.indexOf('?');
        if (queryIndex != -1) {
            urlPath = urlPath.substring(0, queryIndex);
        }

        final int anchorIndex = urlPath.indexOf('#');
        if (anchorIndex != -1) {
            urlPath = urlPath.substring(0, anchorIndex);
        }

        if ("/".equals(urlPath) || urlPath.isEmpty()) {
            return null;
        }

        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }

        final int lastSlash = urlPath.lastIndexOf('/');
        if (lastSlash != -1) {
            return urlPath.substring(lastSlash + 1);
        }
        return urlPath;
    }

    private static String getContentType(String fileName) {
        final String lowerFileName = fileName.toLowerCase();
        final int lastDot = lowerFileName.lastIndexOf('.');
        if (lastDot == -1) {
            return DEFAULT_CONTENT_TYPE;
        }

        final String extension = lowerFileName.substring(lastDot);

        return switch (extension) {
            case ".html", ".htm" -> "text/html; " + CHARSET_UTF8;
            case ".css" -> "text/css; " + CHARSET_UTF8;
            case ".js" -> "application/javascript; " + CHARSET_UTF8;
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".gif" -> "image/gif";
            case ".svg" -> "image/svg+xml";
            case ".json" -> "application/json; " + CHARSET_UTF8;
            case ".xml" -> "application/xml; " + CHARSET_UTF8;
            case ".txt" -> "text/plain; " + CHARSET_UTF8;
            default -> DEFAULT_CONTENT_TYPE;
        };
    }

    private static void sendSuccessResponse(BufferedWriter out, String contentType, long contentLength) throws IOException {
        out.write(HTTP_VERSION + " 200 OK" + CRLF);
        out.write("Content-Type: " + contentType + CRLF);
        out.write("Content-Length: " + contentLength + CRLF);
        out.write(CRLF);
        out.flush();
    }

    private static void sendErrorResponse(BufferedWriter out, int statusCode, String statusMessage) throws IOException {
        final String body = "<html><body><h1>" + statusCode + " " + statusMessage + "</h1></body></html>";
        out.write(HTTP_VERSION + " " + statusCode + " " + statusMessage + CRLF);
        out.write("Content-Type: text/html; " + CHARSET_UTF8 + CRLF);
        out.write("Content-Length: " + body.length() + CRLF);
        out.write(CRLF);
        out.write(body);
        out.flush();
    }

    private static Path findStaticDirectory(String staticDir) {
        final Path projectRoot = findProjectRoot();
        if (projectRoot != null) {
            final Path candidate = projectRoot.resolve(staticDir).normalize();
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath();
            }
        }

        final Path workingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        final int maxLevels = 10;
        Path searchPath = workingDir;

        for (int i = 0; i < maxLevels && searchPath != null; i++) {
            final Path candidate = searchPath.resolve(staticDir).normalize();
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath();
            }
            searchPath = searchPath.getParent();
        }

        final Path absolutePath = Paths.get(staticDir).toAbsolutePath().normalize();
        if (Files.exists(absolutePath) && Files.isDirectory(absolutePath)) {
            return absolutePath;
        }

        return null;
    }

    private static Path findProjectRoot() {
        try {
            final String classPath = HttpServer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            final Path classLocation = Paths.get(classPath).toAbsolutePath().normalize();

            if (classLocation.toString().contains("target/classes")) {
                final Path targetDir = classLocation.getParent();
                if (targetDir != null) {
                    return targetDir.getParent();
                }
            }

            Path current = classLocation;
            final int maxLevels = 10;
            for (int i = 0; i < maxLevels && current != null; i++) {
                final Path pomFile = current.resolve("pom.xml");
                if (Files.exists(pomFile)) {
                    return current;
                }
                current = current.getParent();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}