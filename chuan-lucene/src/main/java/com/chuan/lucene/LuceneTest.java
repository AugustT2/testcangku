package com.chuan.lucene;

import java.io.File;
import java.io.IOException;








import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {

    /**
     * 测试将数据写入到索引库
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws IOException {
        
        // 构建文档数据
        // 构建商品数据
        Document document = new Document();
        document.add(new LongField("id", 15L, Store.YES));
        document.add(new TextField("title", "小米6X 全网通 4GB+64GB 曜石黑 移动联通电信4G手机 双卡双待 智能手机 拍照手机", Store.YES));
        document.add(new StringField("sell_point", "前置2000万“治愈系”自拍，后置200",Store.YES ));
        document.add(new LongField("price", 1699L, Store.YES));
        document.add(new StringField("image", "http://image.taotao.com/images/2018/11/16/2018111609374513107813.jpg", Store.YES));
        
        // 将数据写入到索引中

        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));
        
        // 定义分词器，标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        
        // 写入索引的配置信息，指定Lucene的版本和分词器
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        
        //写入模式，CREATE：先将索引库清空再写入，APPEND：向索引添加数据（默认）
        config.setOpenMode(OpenMode.CREATE);
        
        // 定义索引写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        // 写入数据
        indexWriter.addDocument(document);
        
        // 提交、关闭
        indexWriter.commit();
        indexWriter.close();
    }
    
    /**
     * 测试从索引库中搜索数据
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        //定义搜索搜索对象

        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        //构造查询条件对象
        Query query = new TermQuery(new Term("title","手"));
        //执行搜索，并且指定top n
        TopDocs topDocs = indexSearcher.search(query, 10);
        
        System.out.println("命中数据条数:" + topDocs.totalHits);
        
        //获取得分文档
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("得分:" + scoreDoc.score);
            
            //通过文档id查询文档数据
            Document document = indexSearcher.doc(scoreDoc.doc);
            
            System.out.println("id : " + document.get("id"));
            System.out.println("title : " + document.get("title"));
            System.out.println("image : " + document.get("image"));
        }
    }
    
    /**
     * 测试分词搜索
     * @throws IOException 
     * @throws ParseException 
     * @throws Exception 
     */
    @Test
    public void test3() throws IOException, ParseException {
        //定义搜索搜索对象

        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));
        IndexReader indexReader = DirectoryReader.open(directory );
        IndexSearcher indexSearcher = new IndexSearcher(indexReader );
        
        //定义分词器
        Analyzer analyzer = new StandardAnalyzer();
        //定义查询解析器
        QueryParser queryParser = new QueryParser("title", analyzer);
        //构造查询条件对象
        Query query = queryParser.parse("手机");
        //执行搜索，并且指定top n
        TopDocs topDocs = indexSearcher.search(query, 10);
        
        System.out.println("命中数据条数:" + topDocs.totalHits);
        
        //获取得分文档
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("得分： "+ scoreDoc.score);
            //通过文档id查询文档数据
            Document document = indexSearcher.doc(scoreDoc.doc);
            
            System.out.println("id : " + document.get("id"));
            System.out.println("title : " + document.get("title"));
            System.out.println("image : " + document.get("image"));
        }
    }
    
    /**
     * 测试IK分词器将数据写入到索引库
     * 
     * @throws Exception
     */
    @Test
    public void test4() throws IOException {
        
        // 构建文档数据
        // 构建商品数据
        Document document = new Document();
        document.add(new LongField("id", 15L, Store.YES));
        document.add(new TextField("title", "传智播客  黑马程序员小米6X 全网通 4GB+64GB 曜石黑 移动联通电信4G手机 双卡双待 智能手机 拍照手机", Store.YES));
        document.add(new StringField("sell_point", "前置2000万“治愈系”自拍，后置200",Store.YES ));
        document.add(new LongField("price", 1699L, Store.NO));
        document.add(new StringField("image", "http://image.taotao.com/images/2018/11/16/2018111609374513107813.jpg", Store.YES));
        
        // 将数据写入到索引中

        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));
        
        // 定义分词器，IK分词器
        Analyzer analyzer = new IKAnalyzer();
        
        // 写入索引的配置信息，指定Lucene的版本和分词器
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        
        //写入模式，CREATE：先将索引库清空再写入，APPEND：向索引添加数据（默认）
        config.setOpenMode(OpenMode.CREATE);
        
        // 定义索引写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        // 写入数据
        indexWriter.addDocument(document);
        
        // 提交、关闭
        indexWriter.commit();
        indexWriter.close();
    }
    
    /**
     * 批量插入doc数据
     * 
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        List<Document> documents = new ArrayList<Document>();
        
        for (int i = 0; i < 100; i++) {
            // 构建文档数据
            // 构建商品数据
            Document document = new Document();
            document.add(new LongField("id", i, Store.YES));
            document.add(new TextField("title", "apple 锤子 T"+i+" (SM705) "+i+"GB 白色 移动联通双4G手机", Store.YES));
            document.add(new StringField("sell_point", "预定订单预计"+i+"月26日各仓开始陆续到货！iF设计金奖手机，简单易用人性化，我们是好手机的搬运工！",
                    Store.YES));
            document.add(new LongField("price", 100 * (i + 1), Store.YES));
            document.add(new StringField("image",
                    "http://image.taotao.com/images/2015/06/24/2015062409343867601188.jpg", Store.YES));
            documents.add(document);
        }

       

        // 将数据写入到索引中

        // 定义索引的位置，在工程的相对目录下的index中
        Directory directory = FSDirectory.open(new File("index"));

        // 定义分词器，标准分词器
        Analyzer analyzer = new IKAnalyzer();

        // 写入索引的配置信息，指定Lucene的版本和分词器
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        
        //写入模式，CREATE：先将索引库清空再写入，APPEND：向索引添加数据（默认）
        config.setOpenMode(OpenMode.CREATE);

        // 定义索引写入对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        // 写入数据
        indexWriter.addDocuments(documents);

        // 提交、关闭
        indexWriter.commit();
        indexWriter.close();

    }
    
}
