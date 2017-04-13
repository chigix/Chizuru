package com.chigix.resserver;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.dao.ChunkDao;
import com.chigix.resserver.domain.dao.DaoFactory;
import com.chigix.resserver.mybatis.ChunkDaoImpl;
import com.chigix.resserver.mybatis.DaoFactoryImpl;
import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContextBuilder {

    public ApplicationContext build(final Configuration conf) {
        final String request_id_header_name = Long.toHexString(Double.doubleToLongBits(Math.random()));
        final DaoFactoryImpl dao_factory = createDaoFactory(
                conf.getCurrentNodeId(), conf.getChunksDir(),
                conf.getMainSession(), conf.getUploadSession());
        final Map<String, String> nodes = conf.getNodesMapping();
        nodes.put(conf.getCurrentNodeId(), "127.0.0.1");

        return new ApplicationContext() {
            @Override
            public File getChunksDir() {
                return conf.getChunksDir();
            }

            @Override
            public DateTime getCreationDate() {
                return conf.getCreationDate();
            }

            @Override
            public String getCurrentNodeId() {
                return conf.getCurrentNodeId();
            }

            @Override
            public int getMaxChunkSize() {
                return conf.getMaxChunkSize();
            }

            @Override
            public int getTransferBufferSize() {
                return conf.getTransferBufferSize();
            }

            @Override
            public String getRequestIdHeaderName() {
                return request_id_header_name;
            }

            @Override
            public DaoFactory getDaoFactory() {
                return dao_factory;
            }

            @Override
            public void addNode(String nodeId, String nodeIPAddress) {
                nodes.put(nodeId, nodeIPAddress);
            }

            @Override
            public void finishRequest(HttpRouted routed_info) {
                dao_factory.closeSessions();
            }

            @Override
            public String getChizuruVersion() {
                String version = Application.class.getPackage().getImplementationVersion();
                if (version != null) {
                    return version;
                }
                return "master";
            }

        };
    }

    private DaoFactoryImpl createDaoFactory(String currentNodeId, File chunksDir, SqlSessionFactory mainSession, SqlSessionFactory uploadSession) {
        final ThreadLocal<ChunkDao> weavedChunkDao = new ThreadLocal<>();
        return new DaoFactoryImpl(mainSession, uploadSession) {
            @Override
            public ChunkDao getChunkDao() {
                if (weavedChunkDao.get() == null) {
                    final ChunkDaoImpl dao = (ChunkDaoImpl) super.getChunkDao();
                    weavedChunkDao.set((ChunkDao) Proxy.newProxyInstance(dao.getClass().getClassLoader(), dao.getClass().getInterfaces(),
                            (Object proxy, Method method, Object[] args) -> {
                                // TODO: check if it possible:  ChunkDao instance = (ChunkDao) proxy;
                                ChunkDao instance = dao;
                                if (!method.getName().equals("newChunk")) {
                                    return method.invoke(instance, args);
                                }

                                return new Chunk((String) args[0], (int) args[1], currentNodeId) {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return new GZIPInputStream(new FileInputStream(new File(chunksDir, this.getContentHash())));
                            }

                        };

                            })
                    );
                    dao.setAspectForNewChunk(weavedChunkDao.get());
                }
                return weavedChunkDao.get();
            }

        };
    }

}
