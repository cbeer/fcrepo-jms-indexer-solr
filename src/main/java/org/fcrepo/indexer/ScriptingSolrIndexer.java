package org.fcrepo.indexer;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;
import java.io.StringReader;

public class ScriptingSolrIndexer implements MessageListener {
    @Inject
    private SolrServer solrServer;


    private Parser abderaParser = new Abdera().getParser();

    public void onMessage(Message message) {

        try {
            if (message instanceof TextMessage) {
                final String xml = ((TextMessage) message).getText();

                Document<Entry> doc = abderaParser.parse(new StringReader(xml));

                Entry entry = doc.getRoot();
                final String pid = entry.getCategories("xsd:string").get(0).getTerm();

                if ("purgeObject".equals(entry.getTitle())) {
                    solrServer.deleteById(pid);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SolrServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
