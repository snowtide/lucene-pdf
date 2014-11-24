(defproject com.snowtide/lucene-pdf "3.0.0-SNAPSHOT"
  :description "A library enabling easy Lucene indexing of PDF text and metadata via integration with PDFxStream"
  :url "http://github.com/snowtide/lucene-pdf"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"

  :dependencies [[com.snowtide/pdfxstream "3.1.1"]
                 [org.apache.lucene/lucene-core "4.10.2"]]

  :plugins [[lein-javadoc "0.1.1"]]
  :javadoc-opts {:package-names "com.snowtide.pdf.lucene"
                 :output-dir "target/javadoc"
                 :additional-args ["-Xdoclint:-missing" "-version" "-charset" "UTF-8"
                                   "-docencoding" "UTF-8" "-encoding" "UTF-8"]}

  :repositories {"snowtide-releases" {:url "http://maven.snowtide.com/releases"}}

  :profiles {:lucene-1 [:dev :base {:dependencies [[org.apache.lucene/lucene-core "1.9.1"]]
                                    :java-source-paths ["src/lucene-1"]}]
             :lucene-2 [:dev :base {:dependencies [[org.apache.lucene/lucene-core "2.9.4"]]
                                    :java-source-paths ["src/lucene-2"]}]
             :lucene-3 [:dev :base {:dependencies [[org.apache.lucene/lucene-core "3.6.2"]]
                                    :java-source-paths ["src/lucene-3"]}]
             :lucene-4 [:dev :base {:dependencies [[org.apache.lucene/lucene-core "4.10.2"]
                                                   [org.apache.lucene/lucene-analyzers-common "4.10.2"]]
                                    :java-source-paths ["src/lucene-4"]}]

             :dev {:dependencies [[org.clojure/clojure "1.6.0"]]
                   :plugins []}}

  :javac-options ["-target" "1.5" "-source" "1.5"]
  :java-source-paths ["src/java"]
  :test-paths ["test"]

  :aliases  {"compile+" ["with-profile" "lucene-1:lucene-2:lucene-3:lucene-4" "do" "javac," "test"]
             "release" ["do" "clean," "compile+," "release"]}

  :deploy-repositories {"releases" {:url "s3p://maven.snowtide.com/releases" :creds :gpg}}
  #_#_
  :deploy-repositories {"releases" {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/" :creds :gpg}
                        "snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots/" :creds :gpg}}

  ;;maven central requirements
  :scm {:url "git@github.com:snowtide/lucene-pdf.git"}
  :pom-addition [:developers [:developer
                              [:name "Chas Emerick"]
                              [:url "http://cemerick.com"]
                              [:email "chas@cemerick.com"]
                              [:timezone "-5"]]]  )
