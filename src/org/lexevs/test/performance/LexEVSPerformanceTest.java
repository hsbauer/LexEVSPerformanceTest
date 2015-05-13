/**
 * 
 */
package org.lexevs.test.performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.LBConstants;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

/**
 * @author m029206
 *
 */
public class LexEVSPerformanceTest {
LexBIGService lbs;
static final String SNOMED_NAME = "SNOMED Clinical Terms US Edition";
static final String SNOMED_VERSION = "version";
static final String THES_NAME = "NCI Thesaurus";
static final String THES_VERSION = "version";
static final String NCIM_NAME = "NCI Metathesaurus";
static final String NCIM_VERSION = "version";
static final String BLOOD = "blood";
static final String MUD = "mud";
static final String ARTICLE = "the";
static final String SINGLE_CHAR = "a";
static final String PHRASE_ONE = "Lung Cancer";
static final String PHRASE_TWO = "liver carcinoma";
public File file;

	/**
	 * 
	 */
	public LexEVSPerformanceTest() {
		lbs = LexBIGServiceImpl.defaultInstance();	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new LexEVSPerformanceTest().run("output.csv");
		} catch (LBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public StringBuilder buildString(String codingScheme, String matchAlgorithm, String textMatch) throws LBException{
		StringBuilder builder = new StringBuilder();
		ThreadMXBean threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
		long benchstart = 0;
		long benchtime = 0;
		long cpustart = 0;
		long cputime = 0;
		CodedNodeSet cns = lbs.getCodingSchemeConcepts(codingScheme, null);
		cns.restrictToMatchingDesignations(textMatch, null, matchAlgorithm, null);
		cpustart = threadBean.getCurrentThreadCpuTime();
		benchstart = System.currentTimeMillis();
		ResolvedConceptReferenceList list = cns.resolveToList(null, null, null, -1);
		benchtime = System.currentTimeMillis() - benchstart;
		cputime = TimeUnit.NANOSECONDS.toMillis(threadBean.getCurrentThreadCpuTime() - cpustart);
		int count = list.getResolvedConceptReferenceCount();
		
		builder.append(codingScheme + "," + textMatch + "," + matchAlgorithm  + 
				"," + benchtime + "," + cputime + "," + count + "\n");
		return builder;
	}
	
	public StringBuilder buildStringForSimpleSearch(MatchAlgorithm matchAlgorithm, String textMatch) throws LBException{
		StringBuilder builder = new StringBuilder();
		ThreadMXBean threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
		long benchstart = 0;
		long benchtime = 0;
		long cpustart = 0;
		long cputime = 0;
		SearchExtension search = (SearchExtension)lbs.getGenericExtension("SearchExtension");
		cpustart = threadBean.getCurrentThreadCpuTime();
		benchstart = System.currentTimeMillis();
		ResolvedConceptReferencesIterator itr = search.search(textMatch, matchAlgorithm);
		benchtime = System.currentTimeMillis() - benchstart;
		cputime = TimeUnit.NANOSECONDS.toMillis(threadBean.getCurrentThreadCpuTime() - cpustart);
		int count = itr.numberRemaining();
		
		builder.append("All Coding Schemes"+ "," + textMatch + "," + matchAlgorithm  + 
				"," + benchtime + "," + cputime + "," + count + "\n");
		return builder;
	}
	
	public StringBuilder runThesaurusLuceneMatch() throws LBException{

		buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), MUD).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), ARTICLE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), SINGLE_CHAR).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_ONE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runThesaurusExactMatch() throws LBException{

		buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), MUD).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), ARTICLE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), SINGLE_CHAR).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_ONE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runThesaurusContainsMatch() throws LBException{

		buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.contains.name(), MUD).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.contains.name(), ARTICLE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.contains.name(), SINGLE_CHAR).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_ONE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runThesaurusStartsWithMatch() throws LBException{

		buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(THES_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.startsWith.name(), MUD).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.startsWith.name(), ARTICLE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.startsWith.name(), SINGLE_CHAR).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_ONE).toString());
		builder.append(buildString(THES_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_TWO).toString());

		return builder;
	}
	public StringBuilder runSnomedLuceneMatch() throws LBException{

		buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), MUD).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), ARTICLE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), SINGLE_CHAR).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_ONE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSnomedExactMatch() throws LBException{

		buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), MUD).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), ARTICLE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), SINGLE_CHAR).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_ONE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_TWO).toString());

		return builder;
	}
	public StringBuilder runSnomedContainsMatch() throws LBException{

		buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.contains.name(), MUD).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.contains.name(), ARTICLE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.contains.name(), SINGLE_CHAR).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_ONE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSnomedStartsWithMatch() throws LBException{

		buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.startsWith.name(), MUD).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.startsWith.name(), ARTICLE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.startsWith.name(), SINGLE_CHAR).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_ONE).toString());
		builder.append(buildString(SNOMED_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runNCIMLuceneMatch() throws LBException{

		buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), MUD).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), ARTICLE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), SINGLE_CHAR).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_ONE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runNCIMExactMatch() throws LBException{

		buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), MUD).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), ARTICLE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), SINGLE_CHAR).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_ONE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.exactMatch.name(), PHRASE_TWO).toString());

		return builder;
	}
	public StringBuilder runNCIMContainsMatch() throws LBException{

		buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.contains.name(), MUD).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.contains.name(), ARTICLE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.contains.name(), SINGLE_CHAR).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_ONE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.contains.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runNCIMStartsWithMatch() throws LBException{

		buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD); //warm up the lbs
		StringBuilder builder = buildString(NCIM_NAME,LBConstants.MatchAlgorithms.LuceneQuery.name(), BLOOD);
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.startsWith.name(), MUD).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.startsWith.name(), ARTICLE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.startsWith.name(), SINGLE_CHAR).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_ONE).toString());
		builder.append(buildString(NCIM_NAME,LBConstants.MatchAlgorithms.startsWith.name(), PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSimpleSearchLuceneMatch() throws LBException{

		buildStringForSimpleSearch(MatchAlgorithm.LUCENE, BLOOD); //warm up the lbs
		StringBuilder builder = buildStringForSimpleSearch(MatchAlgorithm.LUCENE, BLOOD);
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.LUCENE, MUD).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.LUCENE, ARTICLE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.LUCENE, SINGLE_CHAR).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.LUCENE, PHRASE_ONE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.LUCENE, PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSimpleSearchExactMatch() throws LBException{

		buildStringForSimpleSearch(MatchAlgorithm.LUCENE, BLOOD); //warm up the lbs
		StringBuilder builder = buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, BLOOD);
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, MUD).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, ARTICLE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, SINGLE_CHAR).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, PHRASE_ONE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_EXACT, PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSimpleSearchContainsMatch() throws LBException{

		StringBuilder builder = buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, BLOOD);
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, MUD).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, ARTICLE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, SINGLE_CHAR).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, PHRASE_ONE).toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.PRESENTATION_CONTAINS, PHRASE_TWO).toString());

		return builder;
	}
	
	public StringBuilder runSimpleSearchCodeExactMatch() throws LBException{

		StringBuilder builder = buildStringForSimpleSearch(MatchAlgorithm.CODE_EXACT, "C1243");
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.CODE_EXACT, "10024003").toString());
		builder.append(buildStringForSimpleSearch(MatchAlgorithm.CODE_EXACT, "8.61").toString());

		return builder;
	}
	public StringBuilder collectBuilders() throws LBException{
		StringBuilder builder = new StringBuilder();
		builder.append("CodingScheme(s)" + "," + "Text" + "," + "Match Algorithm" + "," + "Stop Watch Time" + "," +"CPU time" + "," + "result count" + "\n");
		builder.append(runThesaurusLuceneMatch().toString());
		builder.append(runThesaurusExactMatch().toString());
		builder.append(runThesaurusContainsMatch().toString());
		builder.append(runThesaurusStartsWithMatch().toString());
		builder.append(runSnomedLuceneMatch().toString());
		builder.append(runSnomedExactMatch().toString());
		builder.append(runSnomedContainsMatch().toString());
		builder.append(runSnomedStartsWithMatch().toString());
		builder.append(runNCIMLuceneMatch().toString());
		builder.append(runNCIMExactMatch().toString());
		builder.append(runNCIMContainsMatch().toString());
		builder.append(runNCIMStartsWithMatch().toString());
		builder.append(runSimpleSearchLuceneMatch().toString());
		builder.append(runSimpleSearchExactMatch().toString());
		builder.append(runSimpleSearchContainsMatch().toString());
		builder.append(runSimpleSearchCodeExactMatch().toString());
		return builder;
	}
	
	public void write(StringBuilder builder, String name){
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(name), "utf-8"));
			   writer.write(builder.toString());
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void run(String name) throws LBException{
		StringBuilder builder = collectBuilders();
		write(builder, name);
	}

}
