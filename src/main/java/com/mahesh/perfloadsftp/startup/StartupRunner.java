package com.mahesh.perfloadsftp.startup;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.mahesh.perfloadsftp.service.SftpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    private int fileCount = 5;
    //File size in MB
    private Integer minSize = 1;
    private Integer maxSize = 10;
    private String fileName = "vm1";
    private String[] userNameArr = new String[]{"perf-usr-1"};
    @Autowired
    private SftpService sftpService;

    @Override
    public void run(String... args) {

        try {
            //File Size Range
            String[] fileSizeArr = args[0].split("-");
            minSize = Integer.parseInt(fileSizeArr[0]);
            maxSize = Integer.parseInt(fileSizeArr[1]);

            // FileName starts with.
            fileName = args[1];

            //Sftp usernames
            userNameArr = args[2].split(",");

            // Files count
            fileCount = Integer.parseInt(args[3]);
            startup();
        } catch (Exception ex) {
            log.error("Exception in Input " + ex.getMessage());
        }
    }

    public void startup() {
        int userFilesCount = fileCount / userNameArr.length;
        Arrays.stream(userNameArr).forEach(user -> {

                    //Username null/empty check
                    if (Strings.isNotEmpty(user) && !Strings.isBlank(user)) {
                        ChannelSftp channelSftp = sftpService.getSftpConnection(user);
                        try {
                            sftpService.upload(minSize, maxSize, fileName, userFilesCount, channelSftp);
                        } catch (JSchException e) {
                            log.error("Exception in startup () " + e.getMessage() + " username : " + user);
                            e.printStackTrace();
                        }
                    }
                }
        );
        log.debug("File Upload loop Completed...");
    }
}
