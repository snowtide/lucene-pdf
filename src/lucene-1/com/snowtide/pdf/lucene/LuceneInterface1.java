package com.snowtide.pdf.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Implementation of {@link LucenePDFDocumentFactory.LuceneInterface} corresponding to Lucene v1.x.
 *
 * @version ©2004-2014 Snowtide, http://snowtide.com, licensed under MIT. See LICENSE in the top level of the
 * <a href="https://github.com/snowtide/lucene-pdf">lucene-pdf</a> project directory.
 */
public class LuceneInterface1 extends LucenePDFDocumentFactory.LuceneInterface {

    public void addField (Document doc, String name, String value, boolean store, boolean index, boolean tokenize) {
        doc.add(new Field(name, value, store, index, tokenize));
    }

    public int version () {
        return 1;
    }
}
