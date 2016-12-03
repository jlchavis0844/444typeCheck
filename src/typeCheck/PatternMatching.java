package typeCheck;

import java.util.regex.Pattern;

public class PatternMatching {
	// a single char lower or upper case
	private static final String c = "[a-zA-z]";
	// a number between 0-9
	private static final String n = "[0-9]";

	// operator +/-/*/ /
	private static final Pattern patternOperator = Pattern.compile("[+|-|*|/]");

	// true false boolean
	private static final Pattern patternBoolean = Pattern.compile("true|false");

	// left parathesis (
	private static final Pattern patternLParen = Pattern.compile("[(]");
	// right parathesis )
	private static final Pattern patternRParen = Pattern.compile("[)]");
	// comparasion
	private static final Pattern patternComparasion = Pattern.compile("<|>|<=|>=|==");
	// assignment
	private static final Pattern patternAssignment = Pattern.compile("=");
	// and or not
	private static final Pattern patternChoice = Pattern.compile("&&| || |!");
	// conditional
	private static final Pattern patternCondition = Pattern.compile("if|else if|while|do|for");

	private static final Pattern patternElse = Pattern.compile("else");

	private static final Pattern patternBreak = Pattern.compile("break");
	
	private static final Pattern patternCont = Pattern.compile("continue");

	// end of file
	private static final Pattern patternEOF = Pattern.compile("EOF");

	// private static final String patternPrimitive =
	// ("int|double|char|float|long|short|bool");

	private static final Pattern primitive = Pattern
			.compile("(^)int($)|(^)double($)|(^)char($)|(^)float($)|(^)long($)|(^)short($)|(^)bool($)");

	private static final String patternPrimitiveArray = ("(^)int[]($)|(^)double[]($)|(^)char[]($)|(^)float[]($)|"
			+ "(^)long[]($)|(^)short[]($)");

	private static final Pattern Returntype = Pattern
			.compile("(^)int($)|(^)double($)|(^)char($)|(^)float($)|(^)long($)|(^)short($)|(^)bool($)|(^)void($)");

	private static final String patternmain = "int main()";

	//---------------------------------If statment checks--------------------------------------------------------
	private static final String ifStatementVar = "^?(if)\\((\\w+)\\){?$?";
	private static final String ifStatementFunc = "^?(if)\\({1}\\w+\\(\\w*?\\)\\){1}\\{?$?";
	private static final String ifStatementOps = "(if)\\({1}(\\w+\\(\\w*?\\))?\\s?(==|>|>=|<|<=|!=)\\s?(\\w+)\\((\\w+\\(\\w*?\\))?\\){1}\\{?$?";
	
	private static final Pattern ifStatVarCheck = Pattern.compile(ifStatementVar);
	private static final Pattern ifStatFuncCheck = Pattern.compile(ifStatementFunc);
	private static final Pattern ifStatOpCheck = Pattern.compile(ifStatementOps);
	//--------------------------------- end If statment checks--------------------------------------------------------
	
	//---------------------------------while statmentment checks--------------------------------------------------
	private static final String whileStatementVar = "^?(while)\\((\\w+)\\){?$?";
	private static final String whileStatementFunc = "^?(while)\\({1}\\w+\\(\\w*?\\)\\){1}\\}?$?";
	private static final String whileStatementOps = "(while)\\({1}(\\w+\\(\\w*?\\))?\\s?(==|>|>=|<|<=|!=)\\s?(\\w+)\\((\\w+\\(\\w*?\\))?\\){1}\\{?$?";
	
	private static final Pattern whileStatVarCheck = Pattern.compile(whileStatementVar);
	private static final Pattern whileStatFuncCheck = Pattern.compile(whileStatementFunc);
	private static final Pattern whileStatOpCheck = Pattern.compile(whileStatementOps);
	//----------------------------------end while statement setup----------------------------------------------------

	//--------------------------------String = char array index------------------------------------------------------
	private static final String arrIndex = "^?(\\w+)\\[{1}\\d+\\]{1}";
	private static final Pattern arrIndexCall = Pattern.compile(arrIndex);	
	//--------------------------------String = char array index------------------------------------------------------
	
	
	//---------------------------------#14 assignment check -----------------------------------------------------------
	private static final String assignStatement = "^((int|double|char|float|long|short|bool|void)\\s+)?(\\S+)\\s*={1}\\s*(\\w+)\\(?\\)?\\s*;\\s*$";
	private static final Pattern assignCheck = Pattern.compile(assignStatement);
	//----------------------------------------------------- -----------------------------------------------------------
	
	
	private static final Pattern main = Pattern.compile(patternmain);

	private static final String patternfuncname = c + "[a-zA-z0-9]{1,14}";

	private static final String patternvariablename = "^[a-zA-z][a-zA-z0-9]{1,14}$";
	
	private static final Pattern variable = Pattern.compile(patternvariablename);

	private static final String patternparameter = "[int|double|char|float|long|short|bool]\\s[a-zA-z][a-zA-z0-9]{1,14}"
			+ "[)]{1,14}";
	private static final Pattern parameter = Pattern.compile(patternparameter);

	private static final String patternfuncreturntype = ("[(^)int|(^)double|(^)char|(^)float|(^)long|(^)short|(^)bool|"
			+ "(^)void]");
	
	private static final String patternfuncheader = "^"
			+ "[int|double|char|float|long|short|bool|void]\\s[a-zA-z][a-zA-z0-9]{1,14}\\s[(]\\s[int|double|char|float|"
			+ "long|short|bool]\\s[a-zA-z][a-zA-z0-9]{1,14}[)]{1,14}+(,\\s[[int|double|char|float|long|short|bool]\\s"
			+ "[a-zA-z][a-zA-z0-9]{1,14}){1,14}]+)*\\s[)]$";
	
	// String regex = "^[a-zA-Z ]+(,[a-zA-Z ]+)*$";
	
	// System.out.println("abc,xyz,pqr".matches(regex)); // true
	// System.out.println("text1,text2,".matches(regex)); // false

	private static final Pattern funcheader = Pattern.compile(patternfuncheader);
	
	private static final Pattern funcCallTwoArgument = Pattern
			.compile("^" + patternfuncname + "[(]" + patternvariablename + ",\\s" + patternvariablename + "[)][;]$");

	private static final Pattern patternReturn = Pattern.compile("(^)return\\s[a-zA-z][a-zA-z0-9]{1,14}[;]$");

	private static final Pattern variableAssignFunction = Pattern.compile(
			"^[a-zA-z][a-zA-z0-9]{1,14}\\s[=]\\s[a-zA-z][a-zA-z0-9]{1,14}[(][a-zA-z][a-zA-z0-9]{1,14}[,]\\s[a-zA-z]"
			+ "[a-zA-z0-9]{1,14}[)][;]$");

	// NOTE we won't have " int a = 1+2 " or "int a = a+b" a+# or #+b or a+b+c
	// or etc...
	// WE WILL ONLY HAVE THE BASE CASE int a; or int a = #;
	private static final Pattern varNumberVer1 = Pattern
			.compile("^" + "[int|double|float|long|short]\\s[a-zA-z][a-zA-z0-9]{0,14}" + "[;]$");
	private static final Pattern varnumberVer2 = Pattern
			.compile("^" + "[int|double|float|long|short]\\s[a-zA-z][a-zA-z0-9]{0,14}\\s" + "=\\s[0-9]{1,14}" + "[;]$");
	private static final Pattern varCharVer1 = Pattern.compile("^" + "char\\s[a-zA-z][a-zA-z0-9]{0,14}" + "[;]$");
	private static final Pattern varCharVer2 = Pattern
			.compile("^" + "char\\s[a-zA-z][a-zA-z0-9]{0,14}\\s" + "=\\s" + "'" + "[a-zA-z]" + "'" + "[;]$");
	private static final Pattern varBoolVer1 = Pattern.compile("^" + "bool\\s[a-zA-z][a-zA-z0-9]{0,14}" + "[;]$");
	private static final Pattern varBoolVer2 = Pattern
			.compile("^" + "bool\\s[a-zA-z][a-zA-z0-9]{0,14}\\s" + "=\\s[true|false]" + "[;]$");

	// private static final Pattern variableDeclation =
	// Pattern.compile("[var1|var2]"));
	public static boolean visitVariableDeclaration(String target) {
		return (varNumberVer1.matcher(target).find() || varnumberVer2.matcher(target).find()
				|| varCharVer1.matcher(target).find() || varCharVer2.matcher(target).find()
				|| varBoolVer1.matcher(target).find() || varBoolVer2.matcher(target).find());
	}

	public static boolean visitReturn(String target) {
		return patternReturn.matcher(target).find();
	}

	public static boolean visitVarAssignFunc(String target) {
		return variableAssignFunction.matcher(target).find();
	}

	public static boolean visitFunctionCall(String target) {
		return funcCallTwoArgument.matcher(target).find();
	}

	public static boolean visitVariable(String target) {
		return variable.matcher(target).find();
	}

	public static boolean visitParam(String target) {
		return parameter.matcher(target).find();
	}

	public static boolean visitPrimitive(String target) {
		return primitive.matcher(target).find();
	}

	public static boolean visitReturnType(String target) {
		return Returntype.matcher(target).find();
	}

	public static boolean visitmain(String target) {
		return main.matcher(target).find();
	}

	public static boolean visitfunction(String target) {
		return funcheader.matcher(target).find();
	}

	// remove all comma, semi colon
	public String RemoveAllCommaNSemicolon(String token) {
		token = token.replace(",", "");
		token = token.replace(";", "");
		return token;
	}
//********************************start if statements****************************************
	public static boolean visitIfStatOper(String target){
		return ifStatOpCheck.matcher(target).find();
	}
	
	public static boolean visitIfStatFunc(String target){
		return ifStatFuncCheck.matcher(target).find();
	}
	
	public static boolean visitIfStatVar(String target){
		return ifStatVarCheck.matcher(target).find();
	}
	//********************************end if statements****************************************
	
	//********************************start while loops****************************************
	public static boolean visitWhileStatOper(String target){
		return whileStatOpCheck.matcher(target).find();
	}
	
	public static boolean visitWhileStatFunc(String target){
		return whileStatFuncCheck.matcher(target).find();
	}
	
	public static boolean visitWhileStatVar(String target){
		return whileStatVarCheck.matcher(target).find();
	}
	//********************************end while loops****************************************
	
	//check for char array index is an int
	public static boolean visitCharArray(String target){
		return arrIndexCall.matcher(target).find();
	}
	
	//***********************************14 - assignment check *******************************
	public static boolean visitAssignmentCheck(String target){
		return assignCheck.matcher(target).find();
	}
}