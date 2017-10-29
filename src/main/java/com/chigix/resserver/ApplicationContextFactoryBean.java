package com.chigix.resserver;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.mybatis.ChunkDaoImpl;
import com.chigix.resserver.mybatis.dao.ChizuruMapper;
import com.chigix.resserver.mybatis.record.Chizuru;
import com.chigix.resserver.mybatis.record.ChizuruExample;
import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.joda.time.DateTime;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.dao.DaoFactory;

/**
 * BeanFactory that enables injection of configured {@link ApplicationContext}.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContextFactoryBean implements FactoryBean<ApplicationContext> {

    private final File dataDir = new File("./data");

    private final File chunksDir = new File(dataDir, "/chunks");

    @Autowired
    private ChizuruMapper chizuruDao;

    @Autowired
    private DaoFactory entityManager;

    public ApplicationContextFactoryBean() {
        // intentionally empty
    }

    private Configuration initConfiguration() {
        List<Chizuru> records = chizuruDao.selectByExample(new ChizuruExample());
        final Map<String, String> mapping_persisted = new HashMap<>();
        records.forEach((record) -> {
            mapping_persisted.put(record.getKey(), record.getValue());
        });
        Configuration conf = new Configuration();
        String value;
        if ((value = mapping_persisted.get("NODE_ID")) == null) {
            Utils.insertChizuru("NODE_ID", conf.getCurrentNodeId(), chizuruDao);
        } else {
            conf.setCurrentNodeId(value);
        }
        if ((value = mapping_persisted.get("CREATION_DATE")) == null) {
            Utils.insertChizuru("CREATION_DATE", conf.getCreationDate().toString(), chizuruDao);
        } else {
            conf.setCreationDate(DateTime.parse(value));
        }
        if ((value = mapping_persisted.get("MAX_CHUNKSIZE")) == null) {
            Utils.insertChizuru("MAX_CHUNKSIZE", conf.getMaxChunkSize() + "", chizuruDao);
        } else {
            conf.setMaxChunkSize(Integer.valueOf(value));
        }
        if ((value = mapping_persisted.get("TRANSFER_BUFFERSIZE")) == null) {
            Utils.insertChizuru("TRANSFER_BUFFERSIZE", conf.getTransferBufferSize() + "", chizuruDao);
        } else {
            conf.setTransferBufferSize(Integer.valueOf(value));
        }
        conf.setChunksDir(chunksDir);
        return conf;
    }

    @Override
    public ApplicationContext getObject() throws Exception {
        Utils.clearUploadingTempDb(dataDir);
        final Configuration conf = initConfiguration();
        Utils.checkChunksDir(chunksDir);
        final String request_id_header_name = Long.toHexString(Double.doubleToLongBits(Math.random()));
        ((ChunkDaoImpl) entityManager.getChunkDao()).setAspectForNewChunk(new ChunkDaoImpl(null) {
            @Override
            public Chunk newChunk(String contentHash, int chunk_size) {
                return new Chunk(contentHash, chunk_size, conf.getCurrentNodeId()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new GZIPInputStream(
                                new FileInputStream(
                                        new File(chunksDir, this.getContentHash())
                                )
                        );
                    }

                };
            }

        });
        final Map<String, String> nodes = conf.getNodesMapping();
        nodes.put(conf.getCurrentNodeId(), "127.0.0.1");
        return new ApplicationContext() {
            @Override
            public String getChizuruVersion() {
                String version = Application.class.getPackage().getImplementationVersion();
                if (version != null) {
                    return version;
                }
                return "master";
            }

            @Override
            public File getChunksDir() {
                return chunksDir;
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
                return entityManager;
            }

            @Override
            public void addNode(String nodeId, String nodeIPAddress) {
                nodes.put(nodeId, nodeIPAddress);
            }

            @Override
            public void finishRequest(HttpRouted routed_info) {
                entityManager.close();
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

    private static final class Utils {

        public static void clearUploadingTempDb(File dataDir) {
            File uploadDbFile_1 = new File(dataDir, "Uploading.mv.db");
            File uploadDbFile_2 = new File(dataDir, "Uploading.trace.db");
            if (uploadDbFile_1.exists()) {
                uploadDbFile_1.delete();
            }
            if (uploadDbFile_2.exists()) {
                uploadDbFile_2.delete();
            }
        }

        public static void checkChunksDir(File chunksDir) {
            if (!chunksDir.exists()) {
                chunksDir.mkdir();
            }
            if (!chunksDir.isDirectory()) {
                throw new RuntimeException("Unable to have chunks directory.");
            }
        }

        public static void insertChizuru(String key, String value, ChizuruMapper dao) {
            Chizuru record = new Chizuru();
            record.setKey(key);
            record.setValue(value);
            dao.insert(record);
        }

        public static void updateChizuru(String key, String value, ChizuruMapper dao) {
            Chizuru record = new Chizuru();
            record.setValue(value);
            ChizuruExample example = new ChizuruExample();
            example.createCriteria().andKeyEqualTo(key);
            dao.updateByExampleSelective(record, example);
        }
    }

}
