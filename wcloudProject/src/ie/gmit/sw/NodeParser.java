package ie.gmit.sw;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NodeParser {

	private static final int MAX = 100;
	private static final int TITLE_WEIGHT = 50;
	private static final int HEADING1_WEIGHT = 20;
	private static final int PARAGRAGH_WEIGHT = 1;
	
	private Map<String, Integer> map = new ConcurrentHashMap<>();

	private String term;

	private Set<String> closed = new ConcurrentSkipListSet<>();
	private Queue<DocumentNode> queue = new PriorityQueue<>(Comparator.comparing(DocumentNode::getScore));

	public NodeParser(String url, String searchTerm) throws Exception {
		this.term = searchTerm;
		Document doc = Jsoup.connect(url).get();
		int score = getHeuristicScore(doc);
		closed.add(url);
		queue.offer(new DocumentNode(doc, score));
	}

	public void process() {
		// TODO Auto-generated method stub
		while(!queue.isEmpty() && closed.size() <= MAX) {
			DocumentNode node = queue.poll();
			Document doc = node.getDocument();
			
			Elements edges = doc.select("a[href]");
			for (Element e : edges) {
				String link = e.absUrl("href");
				
				if(link !=null && closed.size() <= MAX && !closed.contains(link)) {
					try {
						closed.add(link);
						Document child = Jsoup.connect(link).get();
						int score = getHeuristicScore(child);
						queue.offer(new DocumentNode(child, score));
					} catch (IOException ex) {
					}
				}
			}
		}
	}
	
	private int getHeuristicScore(Document doc) {
		int score = 0;
		
		String title = doc.title();
		int titleScore = getFrequency(title) * TITLE_WEIGHT;
		System.out.println(closed.size() + "-->" + title);
		
		int headingScore = 0;
		Elements headings = doc.select("h1");
		for (Element heading : headings) {
			String h1 = heading.text();
			score += getFrequency(title) * HEADING1_WEIGHT;
			//System.out.println("\t" + h1);
		}
		
//		String body = doc.body().text();
//		score = getFrequency(title) * PARAGRAGH_WEIGHT;
		
		score = getFuzzyHeuristic(titleScore, headingScore, 0);
		
//		if (score > 100) index(title, headings);
//		
		
		return score;
	}

	
	private int getFrequency(String s) {
		// check for term in *s*
		return 1;
	}
	
	private int getFuzzyHeuristic(int title, int headings, int body) {
		/*
		FIS fis = FIS.load("./myfcl....",true);
		

        // Set inputs
        fis.setVariable("title", title);
        fis.setVariable("headings", headings);
        fis.setVariable("body", body);
        
        // if title is significant and headings relevant and body is frequent then score is high

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = functionBlock.getVariable("score");
        
        
        if (fuzzy score is high then call index on the title, headings and body...
        
        
		 */
		
		
		return 1;
	}
	
	
	private void index(String... text) {
		// TODO Auto-generated method stub
		for (String s : text) {
			// extract each word & add to map after filtering with ignore words ...
		}
	}
	
	
	// inner class
	private class DocumentNode {
		private Document d;
		private int score;

		public DocumentNode(Document d, int score) {
			super();
			this.d = d;
			this.score = score;
		}

		public Document getDocument() {
			return d;
		}


		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new NodeParser("https://jsoup.org/cookbook/input/parse-document-from-string", "Java");
	}

}
