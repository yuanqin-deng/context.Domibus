package eu.domibus.common.services.impl;

import eu.domibus.common.ErrorCode;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.common.exception.CompressionException;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.services.MessagingService;
import eu.domibus.configuration.Storage;
import eu.domibus.ebms3.common.model.CompressionService;
import eu.domibus.ebms3.common.model.Messaging;
import eu.domibus.ebms3.common.model.PartInfo;
import eu.domibus.ebms3.common.model.Property;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * Created by idragusa on 10/26/16.
 */
@Service
public class MessagingServiceImpl implements MessagingService {

    private static final Log LOG = LogFactory.getLog(MessagingServiceImpl.class);

    @Autowired
    MessagingDao messagingDao;

    @Override
    public void storeMessage(Messaging messaging) throws CompressionException{
        if (messaging == null || messaging.getUserMessage() == null)
            return;

        if (messaging.getUserMessage().getPayloadInfo() != null && messaging.getUserMessage().getPayloadInfo().getPartInfo() != null) {
            for (PartInfo partInfo : messaging.getUserMessage().getPayloadInfo().getPartInfo()) {
                try {
                    storeBinary(partInfo);
                } catch (IOException exc) {
                    CompressionException ex = new CompressionException("Could not store binary data for message " + exc.getMessage(), exc);
                    throw ex;
                }
            }
        }

        messagingDao.create(messaging);
    }

    protected void storeBinary(PartInfo partInfo) throws IOException {
        partInfo.setMime(partInfo.getPayloadDatahandler().getContentType());
        if (partInfo.getMime() == null) {
            partInfo.setMime("application/unknown");
        }
        InputStream is = partInfo.getPayloadDatahandler().getInputStream();
        if (Storage.storageDirectory == null) {
            byte[] binaryData = getBinaryData(is, isCompressed(partInfo));
            partInfo.setBinaryData(binaryData);
            partInfo.setFileName(null);

        } else {
            final File attachmentStore = new File(Storage.storageDirectory, UUID.randomUUID().toString() + ".payload");
            partInfo.setFileName(attachmentStore.getAbsolutePath());
            saveFileToDisk(attachmentStore, is, isCompressed(partInfo));
        }
    }

    protected byte[] getBinaryData(InputStream is, boolean isCompressed) throws IOException{
        byte[] binaryData  = IOUtils.toByteArray(is);
        if (isCompressed) {
            binaryData = compress(binaryData);
        }
        return binaryData;
    }

    protected void saveFileToDisk(File file, InputStream is, boolean isCompressed) throws IOException{
        OutputStream fileOutputStream = new FileOutputStream(file);
        if (isCompressed) {
            fileOutputStream = new GZIPOutputStream(fileOutputStream);
        }
        IOUtils.copy(is, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    protected byte[] compress(byte[] binaryData) throws IOException{
        final byte[] buffer = new byte[1024];
        InputStream sourceStream = new ByteArrayInputStream(binaryData);
        ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();
        GZIPOutputStream targetStream = new GZIPOutputStream(compressedContent);
        int i;
        while ((i = sourceStream.read(buffer)) > 0) {
            targetStream.write(buffer, 0, i);
        }
        sourceStream.close();
        targetStream.finish();
        targetStream.close();

        return compressedContent.toByteArray();
    }

    protected boolean isCompressed(PartInfo partInfo) {
        for (final Property property : partInfo.getPartProperties().getProperties()) {
            if (property.getName().equals(CompressionService.COMPRESSION_PROPERTY_KEY) && property.getValue().equals(CompressionService.COMPRESSION_PROPERTY_VALUE)) {
                return true;
            }
        }
        return false;
    }
}