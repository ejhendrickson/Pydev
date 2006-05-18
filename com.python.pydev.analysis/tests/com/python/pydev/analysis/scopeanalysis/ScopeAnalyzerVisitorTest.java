/*
 * Created on May 16, 2006
 */
package com.python.pydev.analysis.scopeanalysis;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.python.pydev.core.IToken;
import org.python.pydev.core.docutils.PySelection;
import org.python.pydev.editor.codecompletion.revisited.modules.AbstractModule;
import org.python.pydev.editor.codecompletion.revisited.modules.SourceModule;

import com.python.pydev.analysis.AnalysisTestsBase;
import com.python.pydev.analysis.messages.AbstractMessage;

public class ScopeAnalyzerVisitorTest extends AnalysisTestsBase {

    public static void main(String[] args) {
    	try {
			ScopeAnalyzerVisitorTest test = new ScopeAnalyzerVisitorTest();
			test.setUp();
			test.testIt3();
			test.tearDown();
			junit.textui.TestRunner.run(ScopeAnalyzerVisitorTest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private Document doc;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testIt2() throws Exception {
    	doc = new Document(
    			"import os\n"+
    			"print os\n"+
    			"\n"
    	);
    	int line=0;
    	int col=8;
    	List<IToken> tokenOccurrences = getTokenOccurrences(line, col);
    	assertEquals(2, tokenOccurrences.size());
    	
    	assertEquals(0, AbstractMessage.getStartLine(tokenOccurrences.get(0), doc)-1);
    	assertEquals(7, AbstractMessage.getStartCol(tokenOccurrences.get(0), doc)-1);
    	
    	assertEquals(1, tokenOccurrences.get(1).getLineDefinition()-1);
    	assertEquals(6, tokenOccurrences.get(1).getColDefinition()-1);
    }    	
    
    public void testIt4() throws Exception {
    	doc = new Document(
    			"print os\n"+
    			"\n"
    	);
    	int line=0;
    	int col=7;
    	//if we don't have the definition, we don't have any references...
    	List<IToken> tokenOccurrences = getTokenOccurrences(line, col);
    	assertEquals(0, tokenOccurrences.size());
    }    	
    
    public void testIt5() throws Exception {
    	doc = new Document(
    			"foo = 10\n" +
    			"print foo\n" +
    			"foo = 20\n"+
    			"print foo\n" +
    			"\n"
    	);
    	int line=0;
    	int col=0;
    	//if we don't have the definition, we don't have any references...
    	List<IToken> tokenOccurrences = getTokenOccurrences(line, col);
    	assertEquals(4, tokenOccurrences.size());
    }    	
    
    
    public void testIt6() throws Exception {
    	doc = new Document(
    			"foo = 10\n" +
    			"foo.a = 10\n" +
    			"print foo.a\n" +
    			"foo.a = 20\n"+
    			"print foo.a\n" +
    			"\n"
    	);
    	//if we don't have the definition, we don't have any references...
    	assertEquals(0, getTokenOccurrences(1, 4).size());
    	assertEquals(5, getTokenOccurrences(1, 0).size());
    }    	
    
    
    public void testIt3() throws Exception {
    	doc = new Document(
    			"import os.path.os\n"+
    			"print os.path.os\n"+
    			"\n"
    	);
    	checkTest3Results(0, 10, "path");
    	checkTest3Results(1, 10, "path");
    	
    	checkTest3Results(0, 7, "os");
    	checkTest3Results(1, 7, "os");
    	
    	checkTest3Results(0, 15, "os");
    	checkTest3Results(1, 15, "os"); //let's see if it checks the relative position correctly
    }
    

	private void checkTest3Results(int line, int col, String lookFor) throws Exception {
		List<IToken> tokenOccurrences = getTokenOccurrences(line, col);
    	assertEquals(2, tokenOccurrences.size());
    	
    	IToken tok0 = tokenOccurrences.get(0);
    	assertEquals(lookFor, tok0.getRepresentation());
		assertEquals(0, AbstractMessage.getStartLine(tok0, doc)-1);
    	assertEquals(col, AbstractMessage.getStartCol(tok0, doc)-1);
    	
    	IToken tok1 = tokenOccurrences.get(1);
    	assertEquals(lookFor, tok1.getRepresentation());
		assertEquals(1, AbstractMessage.getStartLine(tok1, doc)-1);
    	assertEquals(col-1, AbstractMessage.getStartCol(tok1, doc)-1);
	}    	
    
    
	
	public void testIt() throws Exception {
        doc = new Document(
                "foo = 20\n"+
                "print foo\n"+
                "\n"
        );
        
    	List<IToken> toks = getTokenOccurrences(0, 1);
    	assertEquals(2, toks.size());
    	assertEquals(0, toks.get(0).getLineDefinition()-1);
    	assertEquals(0, toks.get(0).getColDefinition()-1);
    	
    	assertEquals(1, toks.get(1).getLineDefinition()-1);
    	assertEquals(6, toks.get(1).getColDefinition()-1);
	}
	
	private List<IToken> getTokenOccurrences(int line, int col) throws Exception {
		ScopeAnalyzerVisitor visitor = doVisit(line, col);
		return visitor.getTokenOccurrences();
	}


	private ScopeAnalyzerVisitor doVisit(int line, int col) throws Exception {
		SourceModule mod = (SourceModule) AbstractModule.createModuleFromDoc(null, null, doc, nature, 0);
		PySelection ps = new PySelection(doc, line, col);
        ScopeAnalyzerVisitor visitor = new ScopeAnalyzerVisitor(nature, "mod1", mod, doc, new NullProgressMonitor(), ps);
        mod.getAst().accept(visitor);
		return visitor;
	}

}
