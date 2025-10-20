package com.rafael0117.producto_service.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageStorage {
    @Value("${app.upload-dir:uploads}") private String uploadDir;

    public String save(MultipartFile file) {
        if (file==null || file.isEmpty()) return null;
        validar(file);
        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')+1) : "bin";
        String name = UUID.randomUUID()+"."+ext.toLowerCase();

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) { throw new RuntimeException("No se pudo guardar imagen", e); }
    }

    public void deleteSilently(String name) {
        if (name==null) return;
        try { Files.deleteIfExists(Paths.get(uploadDir).toAbsolutePath().normalize().resolve(name)); }
        catch (Exception ignored) {}
    }

    private void validar(MultipartFile f) {
        if (f.getSize() > 5*1024*1024) throw new IllegalArgumentException("Imagen > 5MB");
        String ct = f.getContentType();
        if (ct==null || !(ct.equals("image/jpeg")||ct.equals("image/png")||ct.equals("image/webp")))
            throw new IllegalArgumentException("Formato no soportado (jpg, png, webp)");
    }
}
