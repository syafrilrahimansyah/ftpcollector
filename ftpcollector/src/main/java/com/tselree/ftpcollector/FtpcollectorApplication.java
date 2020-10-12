package com.tselree.ftpcollector;

import java.io.File;

import org.apache.commons.net.ftp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
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
@PropertySource({
    "file:src/main/resources/application.properties" 
})
public class FtpcollectorApplication {
	@Autowired
    Environment env;
	@Autowired
	private OmniformDAO omniformDAO;
	
	public static void main(String[] args) {
        new SpringApplicationBuilder(FtpcollectorApplication.class).run(args);
    }

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
        sf.setHost(env.getProperty("FTP_HOST"));
        sf.setPort(Integer.parseInt(env.getProperty("FTP_PORT")));
        sf.setUsername(env.getProperty("FTP_USER"));
        sf.setPassword(env.getProperty("FTP_PASSWORD"));
        //sf.setTestSession(true);
        return new CachingSessionFactory<FTPFile>(sf);
    }

    @Bean
    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory(env.getProperty("FTP_DIR"));
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
            	Integer existOmniform = omniformDAO.checkExist(message.getPayload().toString());
            	if(existOmniform == null) {
            		omniformDAO.addOmniform(message.getPayload().toString());
            	}else {
            		System.out.println("Omniform already exist!");
            	}
                
            }

        };
    }
	
}
