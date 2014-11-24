package com.snowtide.pdf.lucene;

import java.util.HashMap;
import java.util.Map;

/**
 * Instances of this class are used to control the creation of Lucene Documents from PDF content
 * through the {@link LucenePDFDocumentFactory} class.
 *
 * @see <a href="http://www.snowtide.com/help/indexing-pdf-documents-with-lucene-and-pdfxstream">Indexing PDF
 * Documents with Lucene and PDFxStream</a> for usage details
 *
 * @version ©2004-2014 Snowtide, http://snowtide.com, licensed under MIT. See LICENSE in the top level of the
 * <a href="https://github.com/snowtide/lucene-pdf">lucene-pdf</a> project directory.
 */
public class LucenePDFConfiguration {

    /**
     * The default name assigned to the Lucene Field containing the main body of text extracted from a PDF file:
     * <code>"text"</code>.
     */
    public static final String DEFAULT_MAIN_TEXT_FIELD_NAME = "text";

    /**
     * Mapping from PDF metadata keys to Lucene document field names.
     */
    private final HashMap<String, String> metadataFieldMapping = new HashMap<String,String>();
    private boolean copyAllPDFMetadata = true;
    private boolean indexBodyText = true;
    private boolean storeBodyText = false;
    private boolean tokenizeBodyText = true;
    private boolean indexMetadata = true;
    private boolean storeMetadata = true;
    private boolean tokenizeMetadata = true;
    private String bodyTextFieldName = DEFAULT_MAIN_TEXT_FIELD_NAME;

    /**
     * Creates a new config object.  The resulting object retains the default configuration
     * except for the name assigned to the Lucene Field that contains the main PDF text content.
     *
     * @param mainTextFieldName - the name that should be assigned to Fields containing
     * the main PDF text content.
     */
    public LucenePDFConfiguration (String mainTextFieldName) {
        setBodyTextFieldName(mainTextFieldName);
    }

    /**
     * Creates a new config object.  Fields containing the main text content of
     * {@link com.snowtide.pdf.Document PDF documents} converted into
     * Lucene Documents will be assigned a {@link LucenePDFConfiguration#DEFAULT_MAIN_TEXT_FIELD_NAME
     * default name}.  Other configuration defaults are as follows:
     * <ul>
     * <li>All PDF metadata attributes are copied to the resulting Lucene documents</li>
     * <li>The main text content is tokenized and indexed, but not stored</li>
     * <li>The PDF metadata attributes are tokenized, stored, and indexed.</li>
     * </ul>
     */
    public LucenePDFConfiguration () {
        this(DEFAULT_MAIN_TEXT_FIELD_NAME);
    }

    /**
     * Sets the name that will be assigned to Lucene Fields containing PDF body text content.
     */
    public void setBodyTextFieldName (String bodyTextFieldName) {
        this.bodyTextFieldName = bodyTextFieldName;
    }

    /**
     * Returns the name that will be assigned to Lucene Fields containing PDF body text content.
     */
    public String getBodyTextFieldName () {
        return bodyTextFieldName;
    }

    /**
     * Returns a copy of the mapping between PDF metadata attributes and the names given to Lucene fields created for
     * them.
     */
    public Map<String,String> getMetadataFieldMapping () {
        return new HashMap<String,String>(metadataFieldMapping);
    }

    /**
     * Returns the name that should be given to Lucene Fields created from the value of the named PDF metadata
     * attribute.
     */
    public String getMetadataFieldMapping (String pdfMetadataAttr) {
        return metadataFieldMapping.get(pdfMetadataAttr);
    }

    /**
     * Sets the name that will be assigned to Lucene Fields corresponding to the provided PDF metadata attribute
     * name (e.g. {@link com.snowtide.pdf.Document#ATTR_AUTHOR}, etc).
     */
    public void setMetadataFieldMapping (String pdfMetadataAttr, String fieldName) {
        metadataFieldMapping.put(pdfMetadataAttr, fieldName);
    }

    /**
     * Returns true if any PDF metadata attributes not explicitly {@link #getMetadataFieldMapping() mapped} will
     * be added to generated Lucene Documents using their names as specified in the source PDFs.
     */
    public boolean copyAllPDFMetadata() {
        return copyAllPDFMetadata;
    }

    /**
     * @see LucenePDFConfiguration#copyAllPDFMetadata()
     */
    public void setCopyAllPDFMetadata(boolean b) {
        copyAllPDFMetadata = b;
    }

    /**
     * Sets Field attributes that will be used when creating the Field object for the main text content of
     * a PDF document.  These attributes correspond to the <code>store</code>,
     * <code>index</code>, and <code>token</code> parameters of the {@link org.apache.lucene.document.Field}
     * constructor before Lucene v4.x and the same-named attributes of {@link org.apache.lucene.document.FieldType}
     * afterwards.
     */
    public void setBodyTextSettings (boolean store, boolean index, boolean token) {
        indexBodyText = index;
        storeBodyText = store;
        tokenizeBodyText = token;
    }

    /**
     * Sets Field attributes that will be used when creating Field objects for the document attributes found in
     * a PDF document.  These attributes correspond to the <code>store</code>,
     * <code>index</code>, and <code>token</code> parameters of the {@link org.apache.lucene.document.Field}
     * constructor before Lucene v4.x and the same-named attributes of {@link org.apache.lucene.document.FieldType}
     * afterwards.
     */
    public void setMetadataSettings (boolean store, boolean index, boolean token) {
        indexMetadata = index;
        storeMetadata = store;
        tokenizeMetadata = token;
    }

    /**
     * Returns true if the main body text of PDFs added to Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be indexed.
     */
    public boolean indexBodyText () {
        return indexBodyText;
    }

    /**
     * Returns true if the main body text of PDFs added to Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be stored.
     */
    public boolean storeBodyText () {
        return storeBodyText;
    }

    /**
     * Returns true if the main body text of PDFs added to Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be tokenized.
     */
    public boolean tokenizeBodyText () {
        return tokenizeBodyText;
    }

    /**
     * Returns true if the PDF metadata attributes added Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be indexed.
     */
    public boolean indexMetadata () {
        return indexMetadata;
    }

    /**
     * Returns true if the PDF metadata attributes added Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be stored.
     */
    public boolean storeMetadata () {
        return storeMetadata;
    }

    /**
     * Returns true if the PDF metadata attributes added Lucene Documents created through
     * {@link LucenePDFDocumentFactory} using this config object will be tokenized.
     */
    public boolean tokenizeMetadata () {
        return tokenizeMetadata;
    }
}
