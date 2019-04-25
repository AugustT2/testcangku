package com.chuan.lucene;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class TestQuery {
    
    @Test
    public void testTermQuery() throws Exception {
        // 构造查询条件对象
        Query query = new TermQuery(new Term("title", "手机"));
        query(query);
    }
    
    /**
     * 范围搜索
     * 
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery() throws Exception {
        // 设置查询字段、最小值、最大值、最小值是否包含边界，最大值是否包含边界
        Query query = NumericRangeQuery.newLongRange("id", 20L, 50L, false, true);
        query(query);
    }
    
    /**
     * 匹配全部
     * 
     * @throws Exception
     */
    @Test
    public void testMatchAllDocsQuery() throws Exception {
        Query query = new MatchAllDocsQuery();
        query(query);
    }
    
    /**
     * 模糊搜索
     * 
     * @throws Exception
     */
    @Test
    public void testWildcardQuery() throws Exception {
        Query query = new WildcardQuery(new Term("title", "2*"));
        query(query);
    }
    
    /**
     * 相似度搜索
     * 
     * @throws Exception
     */
    @Test
    public void testFuzzyQuery() throws Exception {
        Query query = new FuzzyQuery(new Term("title", "apolz"));
        query(query);
    }
    
    /**
     * 组合搜索
     * 
     * @throws Exception
     */
    @Test
    public void testBooleanQuery() throws Exception {
        BooleanQuery booleanQuery = new BooleanQuery();
        
        //或
        booleanQuery.add(new TermQuery(new Term("title", "14gb")), BooleanClause.Occur.SHOULD);
        
        //或
        booleanQuery.add(new WildcardQuery(new Term("title", "*2*")),BooleanClause.Occur.SHOULD);
        
        query(booleanQuery);
    }
    
    public void query(Query query) throws Exception {
        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 执行搜索，并且指定top n
        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("命中数据条数:" + topDocs.totalHits);

        // 获取得分分得
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("得分：" + scoreDoc.score);

            // 通过文档id查询文档数据
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println("id : " + document.get("id"));
            System.out.println("title : " + document.get("title"));
            System.out.println("image : " + document.get("image"));
        }
    }
}
