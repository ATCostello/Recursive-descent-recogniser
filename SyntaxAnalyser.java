
/**
 *
 * Syntax analyser for exercise course 312.
 *
 * @Author: Alfred Costello
 *
 *
 **/

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

	String filename;
	List<String> declVars = new ArrayList<String>();

	SyntaxAnalyser(String filename) {
		this.filename = filename;
		try {
			lex = new LexicalAnalyser(filename);
		} catch (Exception e) {

		}
		;

	}

	/** Begin processing the first (top level) token. */
	@Override
	public void _statementPart_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("StatementPart");

		// begin
		if (nextToken.symbol == Token.beginSymbol) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			// <statement list>
			_StatementList_();
		} else {
			myGenerate.reportError(nextToken, "File does not start with 'begin' symbol");
			throw new CompilationException(filename, 0);
		}

		//Insert "end" terminal
		if(nextToken.symbol == Token.endSymbol){
			myGenerate.insertTerminal(nextToken);
		} else {
			myGenerate.reportError(nextToken, "File does not end with 'end' symbol");
			throw new CompilationException(filename, 0);
		}
		
		// Finish processing the non terminal
		myGenerate.finishNonterminal("StatementPart");

		// Insert EOF token
		gotoNextToken();
		myGenerate.insertTerminal(nextToken);
	}

	public void _StatementList_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("StatementList");

		// <statement>
		_Statement_();

		// <statement list> ; <statement?
		// if semi colon symbol
		if (nextToken.symbol == Token.semicolonSymbol) {
			// insert semi colon symbol
			// myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_StatementList_();

		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("StatementList");

	}

	public void _Statement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("Statement");

		if (nextToken.symbol == Token.callSymbol) {
			_ProcedureStatement_();
		} else {
			switch (nextToken.symbol) {
				// <assignment statement>
				case (Token.identifier):
					_AssignmentStatement_();
					break;
				// <if statement>
				case (Token.ifSymbol):
					_IfStatement_();
					break;
				// <while statement>
				case (Token.whileSymbol):
					_WhileStatment_();
					break;
				// <procedure statement>
				case (Token.procedureSymbol):
					_ProcedureStatement_();
					break;
				// <until statement>
				case (Token.untilSymbol):
					_UntilStatement_();
					break;
				// <for statement>
				case (Token.forSymbol):
					_ForStatement_();
					break;
				default:
					myGenerate.reportError(nextToken, "Token does not match expected tokens for <statement>");
					throw new CompilationException(filename, 0);
			}
		}
		// Finish processing the non terminal
		myGenerate.finishNonterminal("Statement");

		if (nextToken.symbol != Token.semicolonSymbol) {
			if (!nextToken.text.equals("end")) {
				gotoNextToken();
				myGenerate.insertTerminal(nextToken);
			}

		} else {
			// myGenerate.insertTerminal(nextToken);
			myGenerate.insertTerminal(nextToken);
		}
	}

	public void _AssignmentStatement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("AssignmentStatement");

		// insert identifier token
		String ident = nextToken.text;
		myGenerate.insertTerminal(nextToken);

		// insert becomes symbol
		gotoNextToken();
		myGenerate.insertTerminal(nextToken);

		gotoNextToken();
		// stringConstant
		if (nextToken.symbol == Token.stringConstant) {
			myGenerate.insertTerminal(nextToken);
		}

		// <expression>
		else {
			_Expression_();
		}

		if (!declVars.contains(ident)) {
			declVars.add(ident);
			if (ident.equals("text")) {
				System.out.println("rggDECL Variable: " + ident + " <Text>");
			} else
				System.out.println("rggDECL Variable: " + ident + " <Number>");
		}
		;

		// Finish processing the non terminal
		myGenerate.finishNonterminal("AssignmentStatement");
	}

	public void _Expression_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("Expression");

		// <term>
		_Term_();

		// <expression> + <term> | <expression> - <term>
		// if plus symbol
		if (nextToken.symbol == Token.plusSymbol) {
			// insert plus symbol
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_Expression_();
		}

		// if minus symbol
		else if (nextToken.symbol == Token.minusSymbol) {
			// insert minus symbol
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_Expression_();
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("Expression");

	}

	public void _Term_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("Term");

		// <factor>
		if (nextToken.symbol == Token.identifier || nextToken.symbol == Token.numberConstant
				|| nextToken.symbol == Token.leftParenthesis) {
			_Factor_();
		}

		// <term> * <factor>
		if (nextToken.symbol == Token.timesSymbol) {
			// insert multiply symbol
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_Term_();
		}

		// <term> / <factor>
		if (nextToken.symbol == Token.divideSymbol) {
			// insert divide symbol
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_Term_();
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("Term");

	}

	public void _Factor_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("Factor");

		// identifier
		if (nextToken.symbol == Token.identifier) {
			// insert identifier
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}

		// numberConstant
		else if (nextToken.symbol == Token.numberConstant) {
			// insert numberConstant
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}

		// ( <expression> )
		else if (nextToken.symbol == Token.leftParenthesis) {
			// insert left paranthesis
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
			_Expression_();
			// insert right parenthesis
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}

		else {
			myGenerate.reportError(nextToken, "Token does not relate to expected tokens for <factor>");
			throw new CompilationException(filename, 0);
		}
		// Finish processing the non terminal
		myGenerate.finishNonterminal("Factor");

	}

	public void _IfStatement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("IfStatement");

		// insert if token
		if (nextToken.text.equals("if")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'if'");
			throw new CompilationException(filename, 0);
		}

		// <condition>
		_Condition_();

		// insert then token
		if (nextToken.text.equals("then")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'then'");
			throw new CompilationException(filename, 0);
		}

		// <statement list>
		_StatementList_();

		switch (nextToken.text) {
			case ("end"):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();

				// if
				if (nextToken.text.equals("if")) {
					myGenerate.insertTerminal(nextToken);
					gotoNextToken();
				} else {
					myGenerate.reportError(nextToken, "Expected 'if'");
					throw new CompilationException(filename, 0);
				}

				break;
			case ("else"):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();

				// <statement list>
				_StatementList_();

				// end
				if (nextToken.text.equals("end")) {
					myGenerate.insertTerminal(nextToken);
					gotoNextToken();
				} else {
					myGenerate.reportError(nextToken, "Expected 'end'");
					throw new CompilationException(filename, 0);
				}

				// if
				if (nextToken.text.equals("if")) {
					myGenerate.insertTerminal(nextToken);
					gotoNextToken();
				} else {
					myGenerate.reportError(nextToken, "Expected 'if'");
					throw new CompilationException(filename, 0);
				}

				break;
			default:
				myGenerate.reportError(nextToken, "Expected 'end' or 'else'");
				throw new CompilationException(filename, 0);

		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("IfStatement");
	}

	public void _WhileStatment_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("WhileStatement");

		// insert "while" terminal
		if (nextToken.text.equals("while")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'while'");
			throw new CompilationException(filename, 0);
		}

		// goto <condition>
		_Condition_();

		// insert "loop" terminal
		if (nextToken.text.equals("loop")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'loop'");
			throw new CompilationException(filename, 0);
		}

		// go to <statement list>
		_StatementList_();

		// insert end
		if (nextToken.text.equals("end")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'end'");
			throw new CompilationException(filename, 0);
		}

		// insert loop
		if (nextToken.text.equals("loop")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'loop'");
			throw new CompilationException(filename, 0);
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("WhileStatement");
	}

	public void _ProcedureStatement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("ProcedureStatement");

		// insert "call" terminal
		if (nextToken.symbol == Token.callSymbol) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Token does not match 'case' as expected for <procedure statement>");
			throw new CompilationException(filename, 0);
		}

		// insert identifier
		if (nextToken.symbol == Token.identifier) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken,
					"Token does not match 'identifier' as expected for <procedure statement>");
			throw new CompilationException(filename, 0);
		}

		// insert left parenthesis
		if (nextToken.symbol == Token.leftParenthesis) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken,
					"Token does not match 'left parenthesis' as expected for <procedure statement>");
			throw new CompilationException(filename, 0);
		}

		// argument list
		_ArgumentList_();

		// insert right parenthesis
		if (nextToken.symbol == Token.rightParenthesis) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken,
					"Token does not match 'right parenthesis' as expected for <procedure statement>");
			throw new CompilationException(filename, 0);
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("ProcedureStatement");
	}

	public void _ArgumentList_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("ArgumentList");

		// insert identifier
		myGenerate.insertTerminal(nextToken);
		gotoNextToken();

		// if next token is a comma
		if (nextToken.symbol == Token.commaSymbol) {
			// insert comma token
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();

			// insert identifier token
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("ArgumentList");
	}

	public void _UntilStatement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("UntilStatement");

		// do
		if (nextToken.text.equals("do")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'do'");
			throw new CompilationException(filename, 0);
		}

		// <statment list>
		_StatementList_();

		// until
		if (nextToken.text.equals("until")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'until'");
			throw new CompilationException(filename, 0);
		}

		// condition
		_Condition_();

		// Finish processing the non terminal
		myGenerate.finishNonterminal("UntilStatement");
	}

	public void _ForStatement_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("ForStatement");

		// for
		if (nextToken.text.equals("for")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'for'");
			throw new CompilationException(filename, 0);
		}

		// left parenthesis
		if (nextToken.symbol == Token.leftParenthesis) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'leftParenthesis'");
			throw new CompilationException(filename, 0);
		}

		// <assignment statement>
		_AssignmentStatement_();

		// semi colon
		if (nextToken.symbol == Token.semicolonSymbol) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'semiColon'");
			throw new CompilationException(filename, 0);
		}

		// <condition>
		_Condition_();

		// semi colon
		if (nextToken.symbol == Token.semicolonSymbol) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'semiColon'");
			throw new CompilationException(filename, 0);
		}

		// <assignment statement>
		_AssignmentStatement_();

		// right parenthesis
		if (nextToken.symbol == Token.rightParenthesis) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'RightParenthesis'");
			throw new CompilationException(filename, 0);
		}

		// do
		if (nextToken.text.equals("do")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'do'");
			throw new CompilationException(filename, 0);
		}

		// <statement list>
		_StatementList_();

		// end
		if (nextToken.text.equals("end")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'end'");
			throw new CompilationException(filename, 0);
		}

		// loop
		if (nextToken.text.equals("loop")) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'loop'");
			throw new CompilationException(filename, 0);
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("ForStatement");
	}

	public void _Condition_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("Condition");

		// insert identifier
		if (nextToken.symbol == Token.identifier) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		} else {
			myGenerate.reportError(nextToken, "Expected 'identifier'");
			throw new CompilationException(filename, 0);
		}

		// goto conditional operator
		_ConditionalOperator_();

		// check for EITHER identifier, numberConstant or stringConstant
		// identifier
		if (nextToken.symbol == Token.identifier) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}
		// numberConstant
		else if (nextToken.symbol == Token.numberConstant) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}
		// stringConstant
		else if (nextToken.symbol == Token.stringConstant) {
			myGenerate.insertTerminal(nextToken);
			gotoNextToken();
		}
		// print error message
		else {
			myGenerate.reportError(nextToken, "Expected 'identifier', 'numberConstant' or 'stringConstant");
			throw new CompilationException(filename, 0);
		}

		// Finish processing the non terminal
		myGenerate.finishNonterminal("Condition");
	}

	public void _ConditionalOperator_() throws IOException, CompilationException {
		// Begin processing non terminal
		myGenerate.commenceNonterminal("ConditionalOperator");

		switch (nextToken.text) {
			case (">"):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			case (">="):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			case ("="):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			case ("/="):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			case ("<"):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			case ("<="):
				myGenerate.insertTerminal(nextToken);
				gotoNextToken();
				break;
			default:
				myGenerate.reportError(nextToken, "conditional operator not found");
				throw new CompilationException(filename, 0);

		}
		// Finish processing the non terminal
		myGenerate.finishNonterminal("ConditionalOperator");
	}

	/** Accept a token based on context. */
	@Override
	public void acceptTerminal(int symbol) throws IOException, CompilationException {
		// TODO Auto-generated method stub
		// System.out.println("Accepted Terminal " + symbol);
	}

	public void gotoNextToken() throws IOException, CompilationException {
		nextToken = lex.getNextToken();
	}

} // end of class SyntaxAnalyser
