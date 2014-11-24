# lucene-pdf [![Travis CI status](https://secure.travis-ci.org/snowtide/lucene-pdf.png?branch=master)](http://travis-ci.org/#!/snowtide/lucene-pdf/builds)

lucene-pdf is a JVM (Java, Scala, Groovy, Clojure, etc) library enabling easy
Lucene indexing of PDF text and metadata via integration with
[PDFxStream](http://snowtide.com).

## "Installation"

lucene-pdf is available in Maven central; add it to your Maven project's `pom.xml`:

```xml
<dependency>
  <groupId>com.snowtide</groupId>
  <artifactId>lucene-pdf</artifactId>
  <version>3.0.0</version>
</dependency>
```

Or, add the above Maven artifact coordinates to your {Gradle, Leiningen, sbt, etc} project file.

lucene-pdf is suitable for use with JDK 1.5+, and is tested against the latest
releases of each major revision of Lucene core (1.x, 2.x, 3.x, an 4.x). See the
project file for the exact versions used under test.

While lucene-pdf is suitable for many typical Lucene PDF indexing jobs, there
may be aspects of your project's requirements that it cannot meet (e.g. taking
advantage of some of the more esoteric document indexing parameters available in more
recent versions of Lucene). In that case, its source can serve as a useful
starting point, exhibiting how PDF data can be extracted using
[PDFxStream](http://snowtide.com) and turned into Lucene Documents; please feel
free to import it into your projects and modify it as needed to suit your needs.

## Documentation

* A detailed tutorial is available:
  [Indexing PDF Documents with Lucene and PDFxStream](http://www.snowtide.com/help/indexing-pdf-documents-with-lucene-and-pdfxstream)
* Javadocs are available at [http://snowtide.github.io/lucene-pdf](http://snowtide.github.io/lucene-pdf)

## Example usage

Given a PDF file stored on disk at `/tmp/foo.pdf`, this Java code will use
lucene-pdf to construct a Lucene `org.apache.lucene.document.Document` populated
with fields corresponding to the PDF's main body text and metadata attributes:

```java
import com.snowtide.PDF;
import com.snowtide.pdf.lucene.LucenePDFDocumentFactory;
import org.apache.lucene.document.Document;

// ....

com.snowtide.pdf.Document pdf = PDF.open(new File("/tmp/foo.pdf"));
Document luceneDocument = LucenePDFDocumentFactory.buildPDFDocument(pdf);
pdf.close();
```

`luceneDocument` can then be added to a Lucene index.

This is the simplest sample possible, but it uses a default configuration to
name the fields in the created Lucene document. You will likely want to provide
your own names for:

* the field containing the source PDF document's main body text
* fields corresponding to various PDF document metadata attributes

This Java code does just that, using a `LucenePDFConfiguration` object to
control the mapping:

```java
import com.snowtide.PDF;
import com.snowtide.pdf.lucene.LucenePDFDocumentFactory;
import com.snowtide.pdf.lucene.LucenePDFConfiguration;
import org.apache.lucene.document.Document;

// ....

File f = new File("/tmp/foo.pdf");

LucenePDFConfiguration config = new LucenePDFConfiguration();
config.setBodyTextFieldName("mainText");
config.setMetadataFieldMapping("Author", "document_author");
config.setMetadataFieldMapping("Title", "document_title");

com.snowtide.pdf.Document pdf = PDF.open(new File("/tmp/foo.pdf"));
Document luceneDocument = LucenePDFDocumentFactory.buildPDFDocument(pdf, config);
pdf.close();
```

`LucenePDFConfiguration` provides a number of additional ways to control how
Lucene fields and documents are created, including setting storage,
tokenization, and indexing/analysis flags. See
[Indexing PDF Documents with Lucene and PDFxStream](http://www.snowtide.com/help/indexing-pdf-documents-with-lucene-and-pdfxstream)
and the [lucene-pdf javadoc](http://snowtide.github.io/lucene-pdf) for details.

## License

Copyright © 2004-2014 [Snowtide](http://snowtide.com)

Distributed under the terms of the [MIT License](http://opensource.org/licenses/MIT).
