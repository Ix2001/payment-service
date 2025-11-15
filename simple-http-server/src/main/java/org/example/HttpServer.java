package org.example;

import java.io.*;
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
        String staticDir = args.length > 0 ? args[0] : DEFAULT_STATIC_DIR;
        Path staticPath = findStaticDirectory(staticDir);
        
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
                Socket clientSocket = serverSocket.accept();
                try {
                    handleRequest(clientSocket, staticPath);
                } catch (Exception e) {
                    System.err.println("Error handling request: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    clientSocket.close();
                }
            }
        }
    }
    
    private static void handleRequest(Socket clientSocket, Path staticPath) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            
            System.out.println("Request: " + requestLine);
            
            String[] requestParts = requestLine.split("\\s+", 3);
            if (requestParts.length < 2) {
                sendErrorResponse(out, 400, "Bad Request");
                return;
            }
            
            String urlPath = requestParts[1];
            skipHeaders(in);
            
            String fileName = extractFileName(urlPath);
            if (fileName == null || fileName.isEmpty()) {
                fileName = DEFAULT_FILE;
            }
            
            Path filePath = staticPath.resolve(fileName).normalize();
            
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
            
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = getContentType(fileName);
            
            sendSuccessResponse(out, contentType, fileContent.length);
            
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(fileContent);
            outputStream.flush();
            
            System.out.println("Served file: " + fileName + " (" + fileContent.length + " bytes)");
        }
    }
    
    private static void skipHeaders(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
        }
    }
    
    private static String extractFileName(String urlPath) {
        int queryIndex = urlPath.indexOf('?');
        if (queryIndex != -1) {
            urlPath = urlPath.substring(0, queryIndex);
        }
        
        int anchorIndex = urlPath.indexOf('#');
        if (anchorIndex != -1) {
            urlPath = urlPath.substring(0, anchorIndex);
        }
        
        if (urlPath.equals("/") || urlPath.isEmpty()) {
            return null;
        }
        
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }
        
        int lastSlash = urlPath.lastIndexOf('/');
        return lastSlash != -1 ? urlPath.substring(lastSlash + 1) : urlPath;
    }
    
    private static String getContentType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        int lastDot = lowerFileName.lastIndexOf('.');
        if (lastDot == -1) {
            return DEFAULT_CONTENT_TYPE;
        }
        
        String extension = lowerFileName.substring(lastDot);
        
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
        String html = String.format("<!DOCTYPE html><html><head><title>%d %s</title></head><body><h1>%d %s</h1></body></html>",
                statusCode, statusMessage, statusCode, statusMessage);
        
        out.write(HTTP_VERSION + " " + statusCode + " " + statusMessage + CRLF);
        out.write("Content-Type: text/html; " + CHARSET_UTF8 + CRLF);
        out.write("Content-Length: " + html.length() + CRLF);
        out.write(CRLF);
        out.write(html);
        out.flush();
    }
    
    private static Path findStaticDirectory(String staticDir) {
        Path projectRoot = findProjectRoot();
        if (projectRoot != null) {
            Path  candidate = projectRoot.resolve(staticDir).normalize();
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath();
            }
        }
        
        Path workingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        int maxLevels = 10;
        Path searchPath = workingDir;
        
        for (int i = 0; i < maxLevels && searchPath != null; i++) {
            Path candidate = searchPath.resolve(staticDir).normalize();
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath();
            }
            searchPath = searchPath.getParent();
        }
        
        Path absolutePath = Paths.get(staticDir).toAbsolutePath().normalize();
        if (Files.exists(absolutePath) && Files.isDirectory(absolutePath)) {
            return absolutePath;
        }
        
        return null;
    }
    
    private static Path findProjectRoot() {
        try {
            String classPath = HttpServer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            Path classLocation = Paths.get(java.net.URLDecoder.decode(classPath, "UTF-8")).toAbsolutePath().normalize();
            
            if (classLocation.toString().contains("target/classes")) {
                Path targetDir = classLocation;
                while (targetDir != null && !targetDir.getFileName().toString().equals("target")) {
                    targetDir = targetDir.getParent();
                }
                if (targetDir != null) {
                    return targetDir.getParent();
                }
            }
            
            Path current = classLocation;
            int maxLevels = 10;
            for (int i = 0; i < maxLevels && current != null; i++) {
                Path pomFile = current.resolve("pom.xml");
                if (Files.exists(pomFile)) {
                    return current;
                }
                current = current.getParent();
            }
        } catch (Exception e) {
        }
        
        return null;
    }
}
