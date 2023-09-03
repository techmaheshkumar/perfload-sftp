package com.mahesh.perfloadsftp.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SftpService {

    @Autowired
    FileService fileService;
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.port}")
    private int sftpPort;
    @Value("${sftp.pwd}")
    private String sftpPwd;

    @Async("taskExecutor")
    public void upload(int minSize, int maxSize, String ipFileName, int count, ChannelSftp channelSftp) throws JSchException {
        try {
            String userName = channelSftp.getSession().getUserName();
            log.debug("SFTP Upload file Started for User :" + userName + " File count : " + count);
            File file;
            for (int index = 0; index < count; index++) {
                String fileName = fileService.generateFile(minSize, maxSize, ipFileName);
                //log.debug("File Generated : " + fileName);
                file = new File(fileService.filePath + fileName);
                String startTime = fileService.fmt.format(LocalDateTime.now());
                long start = System.nanoTime();
                try {
                    channelSftp.put(fileService.filePath + fileName, "/outbox/" + fileName);
                } catch (Exception ex) {
                    log.error("SFTP exception in file upload() : " + ex.getMessage() + " username : " + userName);
                    ex.printStackTrace();
                    channelSftp = getSftpConnection(userName);
                    index = index - 1;
                    continue;
                } finally {
                    Files.deleteIfExists(file.toPath());
                }
                long end = System.nanoTime();
                long timeTaken = TimeUnit.SECONDS.convert((end - start), TimeUnit.NANOSECONDS);
                String endTime = fileService.fmt.format(LocalDateTime.now());
                log.debug("Completed - User " + channelSftp.getSession().getUserName() + " File count : " + index + " timeTaken: " + timeTaken);
            }
            log.debug("SFTP Upload file Completed for User :" + userName);
        } catch (Exception ex) {
            log.error("Exception in file upload(): " + ex.getMessage() + " username : " + channelSftp.getSession().getUserName());
            ex.printStackTrace();
        } finally {
            channelSftp.exit();
        }
    }

    public ChannelSftp getSftpConnection(String userName) {
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            Session jschSession = jsch.getSession(userName, sftpHost, sftpPort);
            jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.setPassword(sftpPwd);
            jschSession.connect();
            channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();
            Session session = channelSftp.getSession();
            log.debug("SFTP Host: " + session.getHost() + " Port: " + session.getPort() + " User: " + session.getUserName());
        } catch (JSchException e) {
            log.debug("Exception in getSftpConnection() " + e.getMessage() + " Username : " + userName);
            e.printStackTrace();
        }
        return channelSftp;
    }

}
