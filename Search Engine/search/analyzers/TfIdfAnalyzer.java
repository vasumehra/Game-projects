package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    private IDictionary<URI, Double> vecToNorms;
    private IDictionary<URI, Double> tfIdf;
    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        tfIdf = new ChainedHashDictionary<URI, Double>();
        idfScores = new ChainedHashDictionary<String, Double>();
        documentTfIdfVectors = new ChainedHashDictionary<URI, IDictionary<String, Double>>();
        documentTfIdfVectors = computeAllDocumentTfIdfVectors(webpages);
        vecToNorms = normVectors(documentTfIdfVectors);
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Double> docCounts = new ChainedHashDictionary<String, Double>();
        IDictionary<String, Double> newdocCounts = new ChainedHashDictionary<String, Double>();
        ISet<String> set;
        
        for (Webpage page : pages) {
            set = new ChainedHashSet<String>();
            
            for (String word: page.getWords()) {
                if (!set.contains(word)) {
                    set.add(word);
                }
            }

            for (String s : set) {
                
                if (docCounts.containsKey(s)) {
                    docCounts.put(s, docCounts.get(s) + 1);
                } else {
                    docCounts.put(s, 1.0);
                }
            }
        }        
    
        for (KVPair<String, Double> pair : docCounts)  {   
            double idf =  Math.log(pages.size()/pair.getValue());
            newdocCounts.put(pair.getKey(), idf);
        }
        return newdocCounts;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<>();
        IDictionary<String, Double> newtfScores = new ChainedHashDictionary<>();
        
        for (String s : words) {
            if (!tfScores.containsKey(s)) {
                tfScores.put(s, 0.0);
            }
            tfScores.put(s, tfScores.get(s) + 1);            
        }
        for (KVPair<String, Double> pair : tfScores) {
          
            double tf = pair.getValue()/words.size();
            newtfScores.put(pair.getKey(), tf);
        } 
        return newtfScores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
        
        idfScores = computeIdfScores(pages);
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<String, Double>();        
    
        for (Webpage page : pages) {
            IDictionary<String, Double> computeIdfTf = new ChainedHashDictionary<String, Double>();
            tfScores = computeTfScores(page.getWords());           
        
            for (KVPair<String, Double> pair : tfScores) {
                String s = pair.getKey();         
            
                if (idfScores.containsKey(s)) {
                    double compute = idfScores.get(s) * pair.getValue();
                    computeIdfTf.put(s, compute);           
                }         
            }   

            documentTfIdfVectors.put(page.getUri(), computeIdfTf);
            
    }
    return documentTfIdfVectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
        IDictionary<String, Double> docVec = this.documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<String, Double>();
        IDictionary<String, Double> queryVec = new ChainedHashDictionary<String, Double>();
        tfScores = computeTfScores(query);
        double docWordScore;
        //double queryWordScore;
        double numerator = 0.0;
        for (String s : query) { 
         
            double compute = 0;
            if (tfScores.containsKey(s) && idfScores.containsKey(s)) {
                compute = tfScores.get(s) * idfScores.get(s); 
            } 
            queryVec.put(s, compute);
            
            if (docVec.containsKey(s)) {
                docWordScore = docVec.get(s);
                numerator = numerator + (docWordScore * compute);
            } else {
                docWordScore = 0.0;
            }            
        }
        double denominator = vecToNorms.get(pageUri) * computeNorm(queryVec);
        
        if (denominator != 0.0) {
            return numerator / denominator;
        }        
        return 0.0;        
    }
    
    private IDictionary<URI, Double> normVectors(IDictionary<URI, IDictionary<String, Double>> vectors) {
        IDictionary<URI, Double> res = new ChainedHashDictionary<>();
        for (KVPair<URI, IDictionary<String, Double>> pair : vectors) {
            IDictionary<String, Double> vec = pair.getValue();
            double output = 0.0;
            for (KVPair<String, Double> pair2 : vec) {
                double score = pair2.getValue();
                output =  output + (score * score);
            }
            res.put(pair.getKey(), Math.sqrt(output));
        }
        return res;
    }
    
    private double computeNorm(IDictionary<String, Double> queryDict) {
        double res = 0.0;
        for (KVPair<String, Double> pair : queryDict) {
            double score = pair.getValue();
            res += score * score;
            
        }
        return Math.sqrt(res);
    }
}
