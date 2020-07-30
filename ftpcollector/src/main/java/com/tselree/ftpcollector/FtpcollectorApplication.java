package com.tselree.ftpcollector;

import java.io.File;

import org.apache.commons.net.ftp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.*;
import org.springframework.integration.core.*;
import org.springframework.integration.file.filters.*;
import org.springframework.integration.file.remote.session.*;
import org.springframework.integration.ftp.filters.*;
import org.springframework.integration.ftp.inbound.*;
import org.springframework.integration.ftp.session.*;
import org.springframework.messaging.*;

import com.tselree.ftpcollector.DAO.OmniformDAO;

@SpringBootApplication
public class FtpcollectorApplication {
	@Autowired
	private OmniformDAO omniformDAO;
	
	public static void main(String[] args) {
        new SpringApplicationBuilder(FtpcollectorApplication.class).run(args);
    }

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
        sf.setHost("ftp.parakoder.com");
        sf.setPort(21);
        sf.setUsername("springftp@parakoder.com");
        sf.setPassword("springftp");
        //sf.setTestSession(true);
        return new CachingSessionFactory<FTPFile>(sf);
    }

    @Bean
    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory("public_ftp/springftp");
        fileSynchronizer.setFilter(new FtpSimplePatternFileListFilter("*.xml"));
        return fileSynchronizer;
    }

    @Bean
    @InboundChannelAdapter(channel = "ftpChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> ftpMessageSource() {
        FtpInboundFileSynchronizingMessageSource source =
                new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
        source.setLocalDirectory(new File("src/main/resources/omniform"));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<File>());
        //source.setMaxFetchSize(1);
        //System.out.println(source.getComponentName());
        return source;
    }

    @Bean
    @ServiceActivator(inputChannel = "ftpChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println(message.getPayload());
                omniformDAO.addOmniform(message.getPayload().toString());
            }

        };
    }
	
}
