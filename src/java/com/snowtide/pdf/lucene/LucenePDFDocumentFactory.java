package com.snowtide.pdf.lucene;

import com.snowtide.pdf.OutputTarget;
import com.snowtide.pdf.PDFDateParser;
import com.snowtide.util.logging.Log;
import com.snowtide.util.logging.LoggingRegistry;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * <p>
 * This class enables easy Lucene indexing of PDF text and metadata via integration with PDFxStream. A supported
 * lucene-core library jar must be on any application's classpath that uses this class.
 * </p>
 * <p>
 * Typical usage would be to create a new {@link LucenePDFConfiguration} object, configure it as desired, and pass that object into
 * {@link #buildPDFDocument(com.snowtide.pdf.Document, LucenePDFConfiguration)}
 * along with an open {@link com.snowtide.pdf.Document}. A Lucene {@link org.apache.lucene.document.Document} will be
 * returned containing {@link org.apache.lucene.document.Field}s corresponding to the source PDF document's text and
 * metadata, as dictated by the provided configuration object.
 * </p>
 * <p>
 * {@link #buildPDFDocument(com.snowtide.pdf.Document)} is also provided; this does not require a configuration
 * object, but results in Lucene {@link org.apache.lucene.document.Document}s that contain a direct dump of the PDF's
 * text content and metadata attributes according to a {@link
 * LucenePDFConfiguration#LucenePDFConfiguration() default configuration}.
 * This makes little sense in most environments, where the default names of PDF
 * metadata attributes are unlikely to match the names of the corresponding Lucene Fields for those
 * metadata attributes.  See {@link LucenePDFConfiguration} for details of
 * the default configuration of instances of that class.
 * </p>
 * 
 * @see <a href="http://www.snowtide.com/help/indexing-pdf-documents-with-lucene-and-pdfxstream">Indexing PDF
 * Documents with Lucene and PDFxStream</a> for usage details
 * @version ©2004-2014 Snowtide, http://snowtide.com, licensed under MIT. See LICENSE in the top level of the
 * <a href="https://github.com/snowtide/lucene-pdf">lucene-pdf</a> project directory.
 */
public class LucenePDFDocumentFactory {
    private static final Log log = LoggingRegistry.getLog(LucenePDFDocumentFactory.class);
    private static final boolean LOG_DEBUG = log.isDebugEnabled();

    private static final LucenePDFConfiguration DEFAULT_CONFIG = new LucenePDFConfiguration();
    
    static LuceneInterface LUCENE_INTERFACE;
    static {
        try {
            Class c = Class.forName("org.apache.lucene.document.FieldType");
            LUCENE_INTERFACE = (LuceneInterface) Class.forName("com.snowtide.pdf.lucene.LuceneInterface4").newInstance();
            log.info("Recognized Lucene v4.0.0 or greater.");
        } catch (Throwable t3) {
            try {
                Class c = Class.forName("org.apache.lucene.document.Field$Index");
                if (c.getField("ANALYZED") != null) {
                    LUCENE_INTERFACE = (LuceneInterface) Class.forName("com.snowtide.pdf.lucene.LuceneInterface3").newInstance();
                    log.info("Recognized Lucene v2.4 or greater.");
                } else {
                    throw new IllegalStateException();
                }
            } catch (Throwable t1) {
                try {
                    Class.forName("org.apache.lucene.document.Fieldable");
                    LUCENE_INTERFACE = (LuceneInterface) Class.forName("com.snowtide.pdf.lucene.LuceneInterface2").newInstance();
                    log.info("Recognized Lucene v2.1 or greater.");
                } catch (Throwable t) {
                    try {
                        Class.forName("org.apache.lucene.document.Field$Store");
                        LUCENE_INTERFACE = (LuceneInterface) Class.forName("com.snowtide.pdf.lucene.LuceneInterface1").newInstance();
                        log.info("Recognized Lucene v1.9 or greater.");
                    } catch (Throwable t2) {
                        log.error("Could not recognize Lucene library version, PDFxStream Lucene integration will fail.");
                    }
                }
            }
        }
    }


    /**
     * Creates a new Lucene Document instance using the PDF text and metadata provided by the PDFxStream
     * Document using a default {@link LucenePDFConfiguration#LucenePDFConfiguration()} to control Lucene field names,
     * etc.
     */
    public static Document buildPDFDocument (com.snowtide.pdf.Document pdf) throws IOException {
        return buildPDFDocument(pdf, DEFAULT_CONFIG);
    }

    /**
     * Creates a new Lucene Document instance using the PDF text and metadata provided by the PDFxStream
     * Document using the provided {@link LucenePDFConfiguration} to control Lucene field
     * names, etc.
     */
    public static Document buildPDFDocument (com.snowtide.pdf.Document pdf, LucenePDFConfiguration config) throws
            IOException {
        StringWriter sb = new StringWriter();
        pdf.pipe(new OutputTarget(sb));

        Document doc = new Document();

        LUCENE_INTERFACE.addField(doc, config.getBodyTextFieldName(), sb.toString(),
                config.storeBodyText(), config.indexBodyText(), config.tokenizeBodyText());

        for (Map.Entry<String, Object> metadataEntry : pdf.getAttributeMap().entrySet()) {
            String docPropName = metadataEntry.getKey();
            String fieldName = config.getMetadataFieldMapping(docPropName);
            if (fieldName == null) {
                if (config.copyAllPDFMetadata()) {
                    fieldName = docPropName;
                } else {
                    continue;
                }
            }

            Object value = metadataEntry.getValue();
            String valueStr;

            if (value == null) {
                if (LOG_DEBUG) log.debug("Null document property value found for name ["+docPropName+"] ("+pdf.getName()+')');
                continue;
            } else if (value instanceof String) {
                if (docPropName.equals(com.snowtide.pdf.Document.ATTR_MOD_DATE) ||
                        docPropName.equals(com.snowtide.pdf.Document.ATTR_CREATION_DATE)) {
                    try {
                        valueStr = DateTools.dateToString(PDFDateParser.parseDateString((String)value),
                                DateTools.Resolution.MILLISECOND);
                    } catch (Exception e) {
                        log.warn("PDF date string could not be parsed into a java.util.Date instance ["+value+"] ("+pdf.getName()+')', e);
                        valueStr = (String)value;
                    }
                } else {
                    valueStr = (String)value;
                }
            } else if (value instanceof Number) {
                valueStr = value.toString();
            } else {
                if (LOG_DEBUG) log.debug("Unexpected document property value type: "+value.getClass().getName()+
                        ", for name ("+docPropName+") ("+pdf.getName()+')');
                continue;
            }

            LUCENE_INTERFACE.addField(doc, fieldName, valueStr,
                    config.storeMetadata(), config.indexMetadata(), config.tokenizeMetadata());
        }

        return doc;
    }

    /**
     * Very thin interface implemented by shim classes to allow
     * {@link LucenePDFDocumentFactory} to be used with
     * any version of Lucene without separate per-version implementation dependencies.
     */
    static abstract class LuceneInterface {
        public abstract void addField (Document doc, String name, String value, boolean store, boolean index, boolean tokenize);

        public abstract int version ();
    }
}
