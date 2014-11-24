package com.snowtide.pdf.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Implementation of {@link LucenePDFDocumentFactory.LuceneInterface} corresponding to Lucene v2.x. (Basically: {@link
 * com.snowtide.pdf.lucene.LuceneInterface3} will be selected over this for Lucene versions >= 2.4.0 due to the
 * introduction of Field.Index.ANALYZED, etc.)
 *
 * @version ©2004-2014 Snowtide, http://snowtide.com, licensed under MIT. See LICENSE in the top level of the
 * <a href="https://github.com/snowtide/lucene-pdf">lucene-pdf</a> project directory.
 */
public class LuceneInterface2 extends LucenePDFDocumentFactory.LuceneInterface {

    public void addField (Document doc, String name, String value, boolean store, boolean index, boolean tokenize) {
        doc.add(new Field(name, value, store ? Field.Store.YES : Field.Store.NO,
                index ? (tokenize ? Field.Index.TOKENIZED : Field.Index.UN_TOKENIZED) : Field.Index.NO));
    }

    public int version () {
        return 2;
    }
}
