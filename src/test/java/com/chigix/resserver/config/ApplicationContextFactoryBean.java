package com.chigix.resserver.config;

import com.chigix.resserver.domain.EntityManager;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContextFactoryBean implements FactoryBean<ApplicationContext>,
        ApplicationContextAware {

    private org.springframework.context.ApplicationContext springContext;

    @Override
    public ApplicationContext getObject() throws Exception {
        final EntityManager em = this.springContext.getBean(EntityManager.class);
        final DateTime creation_datetime = new DateTime(DateTimeZone.forID("GMT"));
        final String node_id = UUID.randomUUID().toString();
        return new ApplicationContext() {
            @Override
            public String getChizuruVersion() {
                return "master";
            }

            @Override
            public File getChunksDir() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public DateTime getCreationDate() {
                return creation_datetime;
            }

            @Override
            public String getCurrentNodeId() {
                return node_id;
            }

            @Override
            public int getMaxChunkSize() {
                return 8 * 1024 * 1024;
            }

            @Override
            public int getTransferBufferSize() {
                return 2 * 1024 * 1024;
            }

            @Override
            public String getRequestIdHeaderName() {
                return Long.toHexString(Double.doubleToLongBits(Math.random()));
            }

            @Override
            public EntityManager getEntityManager() {
                return em;
            }

            @Override
            public void addNode(String nodeId, String nodeIPAddress) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void finishRequest(HttpRouted routed_info) {
                em.close();
            }

            @Override
            public <T extends ChannelHandler> T getSharableHandler(Class<T> handler) {
                return springContext.getBean(handler);
            }
        };
    }

    @Override
    public Class<?> getObjectType() {
        return ApplicationContext.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

}
