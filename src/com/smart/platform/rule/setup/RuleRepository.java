package com.smart.platform.rule.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.rule.define.AddbuttonRule;
import com.smart.platform.rule.define.CComboboxDropdownRule;
import com.smart.platform.rule.define.CComboboxDyndropdownRule;
import com.smart.platform.rule.define.CComboboxSqlddlRule;
import com.smart.platform.rule.define.CComboboxSysddlRule;
import com.smart.platform.rule.define.CalccolumnRule;
import com.smart.platform.rule.define.CompsizeRule;
import com.smart.platform.rule.define.CondforbidmodifyRule;
import com.smart.platform.rule.define.CrossRule;
import com.smart.platform.rule.define.DetailforbiddeleteRule;
import com.smart.platform.rule.define.DetailforbidinsertRule;
import com.smart.platform.rule.define.DetailforbidmodifyRule;
import com.smart.platform.rule.define.DetailsumRule;
import com.smart.platform.rule.define.ExprRule;
import com.smart.platform.rule.define.ForbidExitRule;
import com.smart.platform.rule.define.ForbidSelectopRule;
import com.smart.platform.rule.define.ForbiddeleteRule;
import com.smart.platform.rule.define.ForbidmodifyRule;
import com.smart.platform.rule.define.ForbidnewRule;
import com.smart.platform.rule.define.ForbidprintRule;
import com.smart.platform.rule.define.ForbidqueryRule;
import com.smart.platform.rule.define.ForbidsaveRule;
import com.smart.platform.rule.define.ForbidundoRule;
import com.smart.platform.rule.define.GroupRule;
import com.smart.platform.rule.define.Initrule;
import com.smart.platform.rule.define.OtherwheresRule;
import com.smart.platform.rule.define.PrequeryStoreprocRule;
import com.smart.platform.rule.define.QuerylinkRule;
import com.smart.platform.rule.define.RowcheckRule;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.define.SortRule;
import com.smart.platform.rule.define.StoreprocRule;
import com.smart.platform.rule.define.TablecaneditRule;
import com.smart.platform.rule.define.TablefgcolorRule;
import com.smart.platform.rule.define.TablemultiselectRule;
import com.smart.platform.rule.enginee.Ruleenginee;

/**
 * 规则的存储.存在文件中
 * 
 * @author Administrator
 * 
 */
public class RuleRepository {
	/**
	 * 所有rule保存到文件
	 * 
	 * @param outf
	 * @param rules
	 * @throws Exception
	 */
	public static void saveRule(File outf, Vector<Rulebase> rules)
			throws Exception {
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new FileWriter(outf));
			Enumeration<Rulebase> en = rules.elements();
			while (en.hasMoreElements()) {
				Rulebase rule = en.nextElement();
				saveRule(rule, printer);
			}
		} finally {
			if (printer != null) {
				printer.close();
			}
		}
	}

	static void saveRule(Rulebase rule, PrintWriter printer) {
		printer.println("<rule>");
		printer.println(rule.getRuletype());
		printer.println(rule.isUse() ? "true" : "false");
		printer.println(rule.getExpr());
		printer.println("</rule>");
	}

	/**
	 * 从文件读取
	 */
	public static Vector<Rulebase> loadRules(File f) throws Exception {
		Vector<Rulebase> rules = new Vector<Rulebase>();
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new FileReader(f));
			return loadRules(rd);
		} finally {
			if (rd != null)
				rd.close();
		}
	}

	public static Vector<Rulebase> loadRules(BufferedReader rd) throws Exception {
		Vector<Rulebase> rules = new Vector<Rulebase>();
			String line;
			while ((line = rd.readLine()) != null) {
				if(line.startsWith("<rule>")){
					Rulebase rule=readRule(rd);
					rules.add(rule);
				}
			}
		return rules;
	}

	/**
	 * 第0行 类型
	 * 1行 是否启用
	 * 2行起 expr
	 * </rule>结束
	 * @param rd
	 * @return
	 */
	static Rulebase readRule(BufferedReader rd) throws Exception{
		Rulebase rule=null;
		String ruletype=rd.readLine();
		rule=createRule(ruletype);
		if(rule==null){
			throw new Exception("不能创建ruletype="+ruletype);
		}
		rule.setRuletype(ruletype);
		
		String struse=rd.readLine();
		rule.setUse(struse!=null && struse.equals("true"));
		
		String line;
		StringBuffer sb=new StringBuffer();
		while ((line = rd.readLine()) != null) {
			if(line.startsWith("</rule>")){
				rule.setExpr(sb.toString());
				return rule;
			}
			if(sb.length()>0)sb.append("\r\n");
			sb.append(line);
		}
		return rule;
	}
	
	public static Rulebase createRule(String ruletype){
		if(CComboboxDropdownRule.canProcessruletype(ruletype)){
			return new CComboboxDropdownRule();
		}else if(CComboboxSqlddlRule.canProcessruletype(ruletype)){
			return new CComboboxSqlddlRule();
		}else if(CComboboxSysddlRule.canProcessruletype(ruletype)){
			return new CComboboxSysddlRule();
		}else if(CComboboxDyndropdownRule.canProcessruletype(ruletype)){
			return new CComboboxDyndropdownRule();
		}else if(ForbiddeleteRule.canProcessruletype(ruletype)){
			return new ForbiddeleteRule();
		}else if(ForbidmodifyRule.canProcessruletype(ruletype)){
			return new ForbidmodifyRule();
		}else if(ForbidnewRule.canProcessruletype(ruletype)){
			return new ForbidnewRule();
		}else if(ForbidqueryRule.canProcessruletype(ruletype)){
			return new ForbidqueryRule();
		}else if(ForbidsaveRule.canProcessruletype(ruletype)){
			return new ForbidsaveRule();
		}else if(Initrule.canProcessruletype(ruletype)){
			return new Initrule();
		}else if(TablecaneditRule.canProcessruletype(ruletype)){
			return new TablecaneditRule();
		}else if(TablemultiselectRule.canProcessruletype(ruletype)){
			return new TablemultiselectRule();
		}else if(AddbuttonRule.canProcessruletype(ruletype)){
			return new AddbuttonRule();
		}else if(OtherwheresRule.canProcessruletype(ruletype)){
			return new OtherwheresRule();
		}else if(ExprRule.canProcessruletype(ruletype)){
			return new ExprRule();
		}else if(CondforbidmodifyRule.canProcessruletype(ruletype)){
			return new CondforbidmodifyRule();
		}else if(SortRule.canProcessruletype(ruletype)){
			return new SortRule();
		}else if(TablefgcolorRule.canProcessruletype(ruletype)){
			return new TablefgcolorRule();
		}else if(CompsizeRule.canProcessruletype(ruletype)){
			return new CompsizeRule();
		}else if(ForbidundoRule.canProcessruletype(ruletype)){
			return new ForbidundoRule();
		}else if(GroupRule.canProcessruletype(ruletype)){
			return new GroupRule();
		}else if(StoreprocRule.canProcessruletype(ruletype)){
			return new StoreprocRule();
		}else if(CrossRule.canProcessruletype(ruletype)){
			return new CrossRule();
		}else if(PrequeryStoreprocRule.canProcessruletype(ruletype)){
			return new PrequeryStoreprocRule();
		}else if(QuerylinkRule.canProcessruletype(ruletype)){
			return new QuerylinkRule();
		}else if(DetailforbidinsertRule.canProcessruletype(ruletype)){
			return new DetailforbidinsertRule();
		}else if(DetailforbidmodifyRule.canProcessruletype(ruletype)){
			return new DetailforbidmodifyRule();
		}else if(DetailforbiddeleteRule.canProcessruletype(ruletype)){
			return new DetailforbiddeleteRule();
		}else if(DetailsumRule.canProcessruletype(ruletype)){
			return new DetailsumRule();
		}else if(CalccolumnRule.canProcessruletype(ruletype)){
			return new CalccolumnRule();
		}else if(ForbidprintRule.canProcessruletype(ruletype)){
			return new ForbidprintRule();
		}else if(ForbidSelectopRule.canProcessruletype(ruletype)){
			return new ForbidSelectopRule();
		}else if(ForbidExitRule.canProcessruletype(ruletype)){
			return new ForbidExitRule();
		}else if(RowcheckRule.canProcessruletype(ruletype)){
			return new RowcheckRule();
		}	
		
		return null;
	}
	
	public static String[] getRuletypes(boolean withdetail){
		ArrayList<String> ar=new ArrayList<String>();
		addRuletypes(ar,CComboboxDropdownRule.getRuleypes(),withdetail);
		addRuletypes(ar,CComboboxSqlddlRule.getRuleypes(),withdetail);
		addRuletypes(ar,CComboboxSysddlRule.getRuleypes(),withdetail);
		addRuletypes(ar,CComboboxDyndropdownRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbiddeleteRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidmodifyRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidnewRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidqueryRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidsaveRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidprintRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidSelectopRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidExitRule.getRuleypes(),withdetail);
		addRuletypes(ar,Initrule.getRuleypes(),withdetail);
		addRuletypes(ar,TablecaneditRule.getRuleypes(),withdetail);
		addRuletypes(ar,TablemultiselectRule.getRuleypes(),withdetail);
		addRuletypes(ar,AddbuttonRule.getRuleypes(),withdetail);
		addRuletypes(ar,OtherwheresRule.getRuleypes(),withdetail);
		addRuletypes(ar,ExprRule.getRuleypes(),withdetail);
		addRuletypes(ar,CondforbidmodifyRule.getRuleypes(),withdetail);
		addRuletypes(ar,SortRule.getRuleypes(),withdetail);
		addRuletypes(ar,TablefgcolorRule.getRuleypes(),withdetail);
		addRuletypes(ar,CompsizeRule.getRuleypes(),withdetail);
		addRuletypes(ar,ForbidundoRule.getRuleypes(),withdetail);
		addRuletypes(ar,GroupRule.getRuleypes(),withdetail);
		addRuletypes(ar,StoreprocRule.getRuleypes(),withdetail);
		addRuletypes(ar,CrossRule.getRuleypes(),withdetail);
		addRuletypes(ar,PrequeryStoreprocRule.getRuleypes(),withdetail);
		addRuletypes(ar,QuerylinkRule.getRuleypes(),withdetail);
		addRuletypes(ar,DetailforbidinsertRule.getRuleypes(),withdetail);
		addRuletypes(ar,DetailforbidmodifyRule.getRuleypes(),withdetail);
		addRuletypes(ar,DetailforbiddeleteRule.getRuleypes(),withdetail);
		addRuletypes(ar,DetailsumRule.getRuleypes(),withdetail);
		addRuletypes(ar,CalccolumnRule.getRuleypes(),withdetail);
		addRuletypes(ar,RowcheckRule.getRuleypes(),withdetail);
		
		String s[]=new String[ar.size()];
		ar.toArray(s);
		return s;
	}

	public static String[] getReportRuletypes(boolean withdetail){
		ArrayList<String> ar=new ArrayList<String>();
		addRuletypes(ar,CComboboxDropdownRule.getRuleypes(),withdetail);
		addRuletypes(ar,CComboboxSqlddlRule.getRuleypes(),withdetail);
		addRuletypes(ar,CComboboxSysddlRule.getRuleypes(),withdetail);
		addRuletypes(ar,TablemultiselectRule.getRuleypes(),withdetail);
		addRuletypes(ar,OtherwheresRule.getRuleypes(),withdetail);
		addRuletypes(ar,ExprRule.getRuleypes(),withdetail);
		addRuletypes(ar,SortRule.getRuleypes(),withdetail);
		addRuletypes(ar,TablefgcolorRule.getRuleypes(),withdetail);
		addRuletypes(ar,CompsizeRule.getRuleypes(),withdetail);
		addRuletypes(ar,GroupRule.getRuleypes(),withdetail);
		addRuletypes(ar,CrossRule.getRuleypes(),withdetail);
		addRuletypes(ar,PrequeryStoreprocRule.getRuleypes(),withdetail);
		addRuletypes(ar,QuerylinkRule.getRuleypes(),withdetail);
		
		String s[]=new String[ar.size()];
		ar.toArray(s);
		return s;
	}
	
	static void addRuletypes(ArrayList<String> ar,String[] ruletypes,boolean withdetail){
		for(int i=0;i<ruletypes.length;i++){
			if(!withdetail){
				if(ruletypes[i].startsWith("细单"))continue;
			}
			ar.add(ruletypes[i]);
		}
	}
	
	
	public static void main(String argv[]){
		Ruleenginee ruleeng=new Ruleenginee();
		File outf=new File("rule.txt");
		try {
			//RuleRepository.saveRule(outf, ruleeng.getRuletable());
			RuleRepository.loadRules(outf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
