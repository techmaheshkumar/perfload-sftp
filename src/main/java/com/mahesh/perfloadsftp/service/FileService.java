package com.mahesh.perfloadsftp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
public class FileService {

    public static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_n");
    @Value("${file.path}")
    public String filePath;

    public String generateFile(int minSize, int maxSize, String fileName) throws IOException {
        Random random = new Random();
        final ByteBuffer buf = ByteBuffer.allocate(4).putInt(2);
        buf.rewind();
        final OpenOption[] options = {StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SPARSE};
        String fileNameFmt = fileName + fmt.format(LocalDateTime.now()) + ".txt";
        final Path file = Paths.get(filePath + fileNameFmt);
        try (final SeekableByteChannel channel = Files.newByteChannel(file, options);) {
            int size = random.nextInt(maxSize - minSize) + minSize;
            channel.position((1024L * 1024) * size);
            channel.write(buf);
        }
        return fileNameFmt;
    }
}
