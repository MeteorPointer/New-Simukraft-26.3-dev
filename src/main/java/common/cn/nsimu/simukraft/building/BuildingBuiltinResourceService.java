package common.cn.nsimu.simukraft.building;

import common.cn.nsimu.simukraft.SimuKraft;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class BuildingBuiltinResourceService {
    /**
     * 内置官方包是资源目录，不是单个 zip。
     * 开发环境和打进 JAR 后都用同一条 classpath 路径。
     */
    private static final String RESOURCE_PATH = "assets/simukraft/building";
    private static final String PACK_MARKER = "pack.json";
    private static final Set<String> COPIED_ROOTS = ConcurrentHashMap.newKeySet();

    private BuildingBuiltinResourceService() {
    }

    /**
     * ensureCopied: 将内置官方建筑目录复制到建筑包目录，已存在时不覆盖。
     */
    public static void ensureCopied(Path rootDirectory) {
        if (rootDirectory == null) {
            return;
        }
        Path normalizedRoot = rootDirectory.toAbsolutePath().normalize();
        String rootKey = normalizedRoot.toString().toLowerCase(Locale.ROOT);
        if (COPIED_ROOTS.contains(rootKey)) {
            return;
        }
        synchronized (BuildingBuiltinResourceService.class) {
            if (COPIED_ROOTS.contains(rootKey)) {
                return;
            }
            copyOfficialPackage(normalizedRoot);
            COPIED_ROOTS.add(rootKey);
        }
    }

    /**
     * forceOverwrite: 强制用 jar 内置目录覆写磁盘上的官方建筑文件夹。
     */
    public static void forceOverwrite(Path rootDirectory) {
        if (rootDirectory == null) {
            return;
        }
        Path normalizedRoot = rootDirectory.toAbsolutePath().normalize();
        String rootKey = normalizedRoot.toString().toLowerCase(Locale.ROOT);
        synchronized (BuildingBuiltinResourceService.class) {
            try {
                Files.createDirectories(normalizedRoot);
            } catch (IOException exception) {
                SimuKraft.LOGGER.error("Simukraft: Failed to create building package directory {}", normalizedRoot, exception);
                return;
            }
            Path targetDirectory = officialTarget(normalizedRoot);
            if (targetDirectory == null) {
                return;
            }
            if (!copyBuiltinTree(targetDirectory, true)) {
                SimuKraft.LOGGER.warn("Simukraft: Missing built-in official building package {}", RESOURCE_PATH);
            } else {
                SimuKraft.LOGGER.info("Simukraft: Force-overwrote official building package at {}", targetDirectory);
            }
            COPIED_ROOTS.remove(rootKey);
        }
    }

    /**
     * clearCache: 清理已复制目录标记，测试和重载时允许重新检查官方包。
     */
    public static void clearCache() {
        COPIED_ROOTS.clear();
    }

    private static void copyOfficialPackage(Path rootDirectory) {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException exception) {
            SimuKraft.LOGGER.error("Simukraft: Failed to create building package directory {}", rootDirectory, exception);
            return;
        }

        Path targetDirectory = officialTarget(rootDirectory);
        if (targetDirectory == null) {
            return;
        }
        if (Files.isDirectory(targetDirectory) && Files.isRegularFile(targetDirectory.resolve(PACK_MARKER))) {
            SimuKraft.LOGGER.info("Simukraft: Kept existing official building package {}", targetDirectory);
            return;
        }
        if (!copyBuiltinTree(targetDirectory, false)) {
            SimuKraft.LOGGER.warn("Simukraft: Missing built-in official building package {}", RESOURCE_PATH);
            return;
        }
        SimuKraft.LOGGER.info("Simukraft: Copied official building package to {}", targetDirectory);
    }

    private static Path officialTarget(Path rootDirectory) {
        Path targetDirectory = rootDirectory.resolve(BuildingPackageCatalog.OFFICIAL_PACKAGE_NAME).normalize();
        if (!targetDirectory.startsWith(rootDirectory)) {
            SimuKraft.LOGGER.error("Simukraft: Refused unsafe official building package path {}", targetDirectory);
            return null;
        }
        return targetDirectory;
    }

    /**
     * copyBuiltinTree: 把 classpath 上的官方建筑目录复制到目标文件夹。
     * 目标已有文件时，只有 {@code replaceExisting} 为真才覆盖。
     */
    private static boolean copyBuiltinTree(Path targetDirectory, boolean replaceExisting) {
        URL resource = locateBuiltinRoot();
        if (resource == null) {
            return false;
        }
        try {
            URI uri = resource.toURI();
            if ("jar".equalsIgnoreCase(uri.getScheme())) {
                return copyFromJar(uri, targetDirectory, replaceExisting);
            }
            return copyTree(Path.of(uri), targetDirectory, replaceExisting);
        } catch (IOException | URISyntaxException exception) {
            SimuKraft.LOGGER.error("Simukraft: Failed to copy official building package to {}", targetDirectory, exception);
            return false;
        }
    }

    /**
     * copyFromJar: 资源在模组 JAR 里时，只打开 JAR 本身，再定位包内目录。
     */
    private static boolean copyFromJar(URI resourceUri, Path targetDirectory, boolean replaceExisting) throws IOException {
        String spec = resourceUri.toString();
        int split = spec.indexOf("!/");
        if (split < 0) {
            return false;
        }
        URI jarUri = URI.create(spec.substring(0, split));
        String inside = spec.substring(split + 1);
        FileSystem fileSystem = null;
        boolean openedHere = false;
        try {
            try {
                fileSystem = FileSystems.getFileSystem(jarUri);
            } catch (RuntimeException ignored) {
                fileSystem = FileSystems.newFileSystem(jarUri, Collections.emptyMap());
                openedHere = true;
            }
            return copyTree(fileSystem.getPath(inside), targetDirectory, replaceExisting);
        } finally {
            if (openedHere) {
                fileSystem.close();
            }
        }
    }

    private static URL locateBuiltinRoot() {
        ClassLoader classLoader = BuildingBuiltinResourceService.class.getClassLoader();
        URL resource = classLoader == null
                ? ClassLoader.getSystemResource(RESOURCE_PATH)
                : classLoader.getResource(RESOURCE_PATH);
        if (resource != null) {
            return resource;
        }
        return classLoader == null
                ? ClassLoader.getSystemResource(RESOURCE_PATH + "/" + PACK_MARKER)
                : classLoader.getResource(RESOURCE_PATH + "/" + PACK_MARKER);
    }

    private static boolean copyTree(Path source, Path targetDirectory, boolean replaceExisting) throws IOException {
        if (source == null || !Files.exists(source)) {
            return false;
        }
        Path sourceRoot = Files.isDirectory(source) ? source : source.getParent();
        if (sourceRoot == null || !Files.isDirectory(sourceRoot) || !Files.isRegularFile(sourceRoot.resolve(PACK_MARKER))) {
            return false;
        }
        try (Stream<Path> walk = Files.walk(sourceRoot)) {
            for (Path file : walk.filter(Files::isRegularFile).toList()) {
                Path relative = sourceRoot.relativize(file);
                if (!isSafeRelative(relative)) {
                    SimuKraft.LOGGER.warn("Simukraft: Ignored unsafe built-in building entry {}", relative);
                    continue;
                }
                Path destination = targetDirectory.resolve(relative.toString()).normalize();
                if (!destination.startsWith(targetDirectory)) {
                    continue;
                }
                if (!replaceExisting && Files.exists(destination)) {
                    continue;
                }
                Files.createDirectories(destination.getParent());
                Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return Files.isRegularFile(targetDirectory.resolve(PACK_MARKER));
    }

    private static boolean isSafeRelative(Path relative) {
        if (relative == null) {
            return false;
        }
        String text = relative.toString().replace('\\', '/');
        return !text.isBlank() && !text.startsWith("/") && !text.contains("..");
    }
}
