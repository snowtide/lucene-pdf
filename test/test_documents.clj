(ns test-documents
  (:import com.snowtide.PDF
           (com.snowtide.pdf PDFDateParser Document PDFDateParser)
           (com.snowtide.pdf.lucene LucenePDFDocumentFactory LucenePDFConfiguration)
           (org.apache.lucene.document Field DateTools)
           java.io.File
           java.util.Date)
  (:use clojure.test))

(def test-pdf "test/DocumentSerialization.pdf")

(try
  (Class/forName "org.apache.lucene.document.FieldType")
  (defn assert-field-params [^Field f store index tokenize]
    (let [ft (.fieldType f)]
      (is (= store (.stored ft)))
      (is (= index (.indexed ft)))
      (is (= tokenize (.tokenized ft))))) 
  (catch Throwable t
    (defn assert-field-params [^Field f store index tokenize]
      (is (= store (.isStored f)))
      (is (= index (.isIndexed f)))
      (is (= tokenize (.isTokenized f))))))

(def attr-names [Document/ATTR_AUTHOR Document/ATTR_PRODUCER Document/ATTR_TITLE
                  Document/ATTR_CREATION_DATE Document/ATTR_CREATOR])

(defn verify-lucene-fields
  [pdf lucene-doc text-field text-field-params metadata-fields metadata-field-params]
  (apply assert-field-params (.getField lucene-doc text-field) text-field-params)
  (is (> (-> (.getField lucene-doc text-field) .stringValue count) 9500)
      "PDF text content did not get into lucene document")

  (doseq [[pdf-attr lucene-field-name] metadata-fields
          :let [v (.getAttribute pdf pdf-attr)
                  field (.getField lucene-doc lucene-field-name)]]
    (apply assert-field-params field metadata-field-params)
    (if (= pdf-attr Document/ATTR_CREATION_DATE)
      (= (PDFDateParser/parseDateString v) (DateTools/stringToDate (.stringValue field)))
      (= v (.stringValue field)))))

(deftest default-lucene-document-creation
  (with-open [pdf (PDF/open test-pdf)]
    (let [lucene-doc (LucenePDFDocumentFactory/buildPDFDocument pdf)]
      (verify-lucene-fields pdf lucene-doc
        LucenePDFConfiguration/DEFAULT_MAIN_TEXT_FIELD_NAME [false true true]
        (zipmap attr-names attr-names) [true true true]))))

(deftest custom-lucene-document-creation
  (with-open [pdf (PDF/open test-pdf)]
    (let [text-field-name "l_text"
          attr-names (remove #{Document/ATTR_PRODUCER} attr-names)
         metadata-field-params (zipmap attr-names (map #(str "l_" %) attr-names))
         config (doto (LucenePDFConfiguration. text-field-name)
                  (.setCopyAllPDFMetadata false)
                  (.setMetadataSettings false true false)
                  (.setBodyTextSettings true true false))
         _ (doseq [[pdf-attr lucene-field-name] metadata-field-params]
             (.setMetadataFieldMapping config pdf-attr lucene-field-name))
         lucene-doc (LucenePDFDocumentFactory/buildPDFDocument pdf config)]
     (verify-lucene-fields pdf lucene-doc
       text-field-name [true true false]
       metadata-field-params [false true false])

     ;; ensure producer attr wasn't copied -- it wasn't mapped above, and 'copyAllPDFMetadata' is false in config
     (is (nil? (.getField lucene-doc Document/ATTR_PRODUCER)))
     (is (nil? (.getField lucene-doc (str "l_" Document/ATTR_PRODUCER)))))))
