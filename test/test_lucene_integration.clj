(ns test-lucene-integration
  (:import com.snowtide.PDF
           (com.snowtide.pdf PDFDateParser Document PDFDateParser)
           (com.snowtide.pdf.lucene LucenePDFDocumentFactory LucenePDFConfiguration)
           (org.apache.lucene.document Field DateTools)
           (org.apache.lucene.index IndexWriter IndexReader Term)
           (org.apache.lucene.store Directory FSDirectory)
           (org.apache.lucene.search IndexSearcher TermQuery PhraseQuery)
           org.apache.lucene.analysis.standard.StandardAnalyzer
           java.io.File
           java.util.Date)
  (:use clojure.test))

(def filename-field-name "pdf__filename")

(def ^:dynamic ^:private *idx-reader*)

(def ^:private lucene-interface
  (.get (doto (.getDeclaredField LucenePDFDocumentFactory "LUCENE_INTERFACE")
          (.setAccessible true))
    nil))

(defn- build-lucene-document [pdf-file config]
  (with-open [pdf (PDF/open pdf-file)]
    (let [lucene-doc (LucenePDFDocumentFactory/buildPDFDocument pdf config)]
      (.addField lucene-interface lucene-doc
        filename-field-name (.getName pdf)
        true false false)
      lucene-doc)))

(defn- populate-index [^IndexWriter w]
  (let [config (doto (LucenePDFConfiguration.)
                 (.setCopyAllPDFMetadata true))]
    (.addDocument w (build-lucene-document (File. "test/key_steps.pdf") config))
    (doto config
      (.setBodyTextFieldName "alt_text_field")
      (.setCopyAllPDFMetadata false)
      (.setMetadataFieldMapping "Author" "author_attr_field_name"))
    (.addDocument w (build-lucene-document (File. "test/DocumentSerialization.pdf") config))
    w))

(def fsdirectory
  (try
    (eval '#(org.apache.lucene.store.SimpleFSDirectory. %))
    (catch Throwable t
      (eval '#(FSDirectory/getDirectory % true)))))

(def index-writer
  (case (.version lucene-interface)
    (1 2) (eval '#(IndexWriter. % (org.apache.lucene.analysis.standard.StandardAnalyzer.) true))
    3 (eval '#(IndexWriter. %
                (org.apache.lucene.analysis.standard.StandardAnalyzer.
                     org.apache.lucene.util.Version/LUCENE_CURRENT)
                org.apache.lucene.index.IndexWriter$MaxFieldLength/UNLIMITED))
    4 (eval '#(IndexWriter. %
                (org.apache.lucene.index.IndexWriterConfig.
                  org.apache.lucene.util.Version/LUCENE_CURRENT
                  (org.apache.lucene.analysis.standard.StandardAnalyzer.
                     org.apache.lucene.util.Version/LUCENE_CURRENT))))))

(defn- setup-index [f]
  (let [index-dir (fsdirectory (File. (str "target/test-index" (.version lucene-interface))))]
    (-> (index-writer index-dir)
      populate-index
      .close)
    (with-open [reader (IndexReader/open index-dir)]
     (binding [*idx-reader* reader]
       (f)))))

(use-fixtures :once setup-index)

(deftest key-steps-queries
  (let [searcher (IndexSearcher. *idx-reader*)]
    (let [results (.search searcher
                          (TermQuery. (Term. LucenePDFConfiguration/DEFAULT_MAIN_TEXT_FIELD_NAME "macromedia"))
                          nil 1000)]
      (is (= 1 (.-totalHits results)))
      (let [doc (.doc searcher (-> results .-scoreDocs first .doc))]
        (is (= "key_steps.pdf" (.stringValue (.getField doc filename-field-name))))
        (is (instance? Date (DateTools/stringToDate (-> doc (.getField "CreationDate") .stringValue))))
        (is (= "Adobe InDesign 2.0.1" (-> doc (.getField "Creator") .stringValue)))))))

(deftest document-serialization-queries
  (let [searcher (IndexSearcher. *idx-reader*)]
    (let [results (.search searcher
                          (TermQuery. (Term. "alt_text_field" "jxta"))
                          nil 1000)]
      (is (= 1 (.-totalHits results)))
      (let [doc (.doc searcher (-> results .-scoreDocs first .doc))]
        (is (= "DocumentSerialization.pdf" (.stringValue (.getField doc filename-field-name))))
        (is (= "gseidman" (-> doc (.getField "author_attr_field_name") .stringValue)))))

    (let [results (.search searcher
                    (doto (PhraseQuery.)
                      (.add (Term. "alt_text_field" "tight"))
                      (.add (Term. "alt_text_field" "loops")))
                    nil 1000)]
      (is (= 1 (.-totalHits results)))
      (let [doc (.doc searcher (-> results .-scoreDocs first .doc))]
        (is (= "DocumentSerialization.pdf" (.stringValue (.getField doc filename-field-name))))))))
