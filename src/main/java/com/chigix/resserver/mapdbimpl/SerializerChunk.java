package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class SerializerChunk extends GroupSerializerObjectArray<Chunk> {

    private static final Logger LOG = LoggerFactory.getLogger(SerializerChunk.class.getName());

    public static final Serializer<Chunk> DEFAULT = new SerializerChunk();

    @Override
    public void serialize(DataOutput2 out, Chunk value) throws IOException {
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Chunk");
            writer.writeStartElement("ContentHash");
            writer.writeCharacters(value.getContentHash());
            writer.writeEndElement(); // Chunk.ContentHash
            writer.writeStartElement("Size");
            writer.writeCharacters(value.getSize() + "");
            writer.writeEndElement(); // Chunk.Size
            writer.writeEndElement();// Chunk
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new IOException(ex);
        }
    }

    @Override
    public Chunk deserialize(DataInput2 input, int available) throws IOException {
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
                        //readTag(ctx, node, reader);
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
                            return new Chunk(ctx.getContentHash(), ctx.getChunkSize(), ctx.getLocationId());
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new IOException(ex);
        }
    }

    private void readTag(Context ctx, XPathNode xpath, XMLStreamReader reader) {
        switch (xpath.getSimplePath()) {
            case "/Chunk/ContentHash":
                break;
        }
    }

    private void readCharacters(Context ctx, XPathNode xpath, XMLStreamReader reader) {
        switch (xpath.getSimplePath()) {
            case "/Chunk/ContentHash":
                ctx.setContentHash(reader.getText());
                break;
            case "/Chunk/Size":
                ctx.setChunkSize(Integer.valueOf(reader.getText()));
                break;
            case "/Chunk/LocationId":
                ctx.setLocationId(reader.getText());
                break;
        }
    }

    private class Context {

        private String contentHash;
        private int chunkSize;

        private String locationId;

        public String getContentHash() {
            return contentHash;
        }

        public void setContentHash(String contentHash) {
            this.contentHash = contentHash;
        }

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

    }

}
