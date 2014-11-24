package com.snowtide.pdf.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Implementation of {@link LucenePDFDocumentFactory.LuceneInterface} corresponding to Lucene v3.x. (Basically:
 * this will be selected over {@link
 * com.snowtide.pdf.lucene.LuceneInterface2} for Lucene versions >= 2.4.0 due to the
 * introduction of Field.Index.ANALYZED, etc.)
 *
 * @version ©2004-2014 Snowtide, http://snowtide.com, licensed under MIT. See LICENSE in the top level of the
 * <a href="https://github.com/snowtide/lucene-pdf">lucene-pdf</a> project directory.
 */
public class LuceneInterface3 extends LucenePDFDocumentFactory.LuceneInterface {

    public void addField (Document doc, String name, String value, boolean store, boolean index, boolean tokenize) {
        doc.add(new Field(name, value, store ? Field.Store.YES : Field.Store.NO,
                index ? (tokenize ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED) : Field.Index.NO));
    }

    public int version () {
        return 3;
    }
}
