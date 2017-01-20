package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.joda.time.DateTime;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mapdb.Serializer;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class SerializerBucket extends GroupSerializerObjectArray<Bucket> {

    private static final Logger LOG = LoggerFactory.getLogger(SerializerBucket.class.getName());

    public static final Serializer<Bucket> DEFAULT = new SerializerBucket();

    @Override
    public void serialize(DataOutput2 out, Bucket value) throws IOException {
        if (value instanceof BucketInStorage) {
            throw new ResourceInStorageNotSupportException(value.getName());
        }
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Bucket");
            writer.writeStartElement("Name");
            writer.writeCharacters(value.getName());
            writer.writeEndElement(); // Bucket.Name
            writer.writeStartElement("CreationTime");
            writer.writeCharacters(value.getCreationTime().toString());
            writer.writeEndElement(); // Bucket.CreationTime
            writer.writeStartElement("ChizuruUUID");
            writer.writeCharacters(java.util.UUID.randomUUID().toString());
            writer.writeEndElement(); // Bucket.ChizuruUUID
            writer.writeEndElement();// Bucket
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new IOException(ex);
        }
    }

    @Override
    public Bucket deserialize(DataInput2 input, int available) throws IOException {
        XPathNode node = null;
        Context ctx = new Context();
        try {
            XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new DataInput2.DataInputToStream(input), "UTF-8");
            while (true) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        XPathNode current_tag = new XPathNode(reader.getLocalName());
                        if (node != null) {
                            node.appendChild(current_tag);
                        }
                        node = current_tag;
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        readCharacters(ctx, node, reader);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (node == null) {
                            throw new RuntimeException("Unexpected: node is null");
                        }
                        node = node.getParent();
                        if (node == null) {
                            BucketInStorage b = new BucketInStorage(ctx.getBucketName(), ctx.getCreation());
                            b.setUUID(ctx.getBucketUUID());
                            return b;
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new IOException(ex);
        }
    }

    private void readCharacters(Context ctx, XPathNode xpath, XMLStreamReader reader) {
        switch (xpath.getSimplePath()) {
            case "/Bucket/Name":
                ctx.setBucketName(reader.getText());
                break;
            case "/Bucket/CreationTime":
                ctx.setCreation(DateTime.parse(reader.getText()));
                break;
            case "/Bucket/ChizuruUUID":
                ctx.setBucketUUID(reader.getText());
                break;
        }
    }

    private class Context {

        private String bucketName;

        private DateTime creation;

        private String bucketUUID;

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public DateTime getCreation() {
            return creation;
        }

        public void setCreation(DateTime creation) {
            this.creation = creation;
        }

        public String getBucketUUID() {
            return bucketUUID;
        }

        public void setBucketUUID(String bucketUUID) {
            this.bucketUUID = bucketUUID;
        }

    }

    public static class ResourceInStorageNotSupportException extends IOException {

        public ResourceInStorageNotSupportException(String bucketname) {
            super("SAVING [" + bucketname + "] ERROR: BucketInStorage is not supported for serialize");
        }

    }

}
