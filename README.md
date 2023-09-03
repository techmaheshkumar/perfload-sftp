# perfload-sftp
This project is created in Java Spring boot

The following features in this SFTP Perf tool:

* File generation based on the file size range via command line args,
* SFTP transfer with N number of users(Threads)

# Steps to Run this jar in command:

* Build the project in IDE (Intellij), then the executable jar will be generated in /build/libs/ folder.

# Run the executable jar using this command:
nohup java -jar sftp-perf-load-1.0.jar 1-10 perf-10k- sftp-user-1,sftp-user-2,sftp-user-3 100 &

**nohup** - used to run the command in background
**1-10** - file size generation range in MB
**perf-10k-** - Filename prefix
**sftp-user-1** -  SFTP usernames
**100** - files count.
