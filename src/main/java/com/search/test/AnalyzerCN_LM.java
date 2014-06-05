package com.search.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 中文切词
 * 
 * @author lm
 */
public class AnalyzerCN_LM {
	// 分析器类型 基本介绍
	// WhitespaceAnalyzer 以空格作为切词标准，不对语汇单元进行其他规范化处理
	// SimpleAnalyzer 以非字母符来分割文本信息，并将语汇单元统一为小写形式，并去掉数字类型的字符
	// StopAnalyzer 该分析器会去除一些常有a,the,an等等，也可以自定义禁用词
	// StandardAnalyzer Lucene内置的标准分析器，会将语汇单元转成小写形式，并去除停用词及标点符号
	// CJKAnalyzer 能对中，日，韩语言进行分析的分词器，对中文支持效果一般。
	// SmartChineseAnalyzer 对中文支持稍好，但扩展性差

	/**
	 * 中文切词
	 */
	@SuppressWarnings("unchecked")
	public List<String> analyzerCnStr(String str) {
		List<String> result = new ArrayList();
		Analyzer analyzer =new IKAnalyzer();// new SmartChineseAnalyzer(Version.LUCENE_48, true);
		try {
			TokenStream tokenStream = analyzer.tokenStream("field", str);
			CharTermAttribute term = tokenStream
					.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(term.toString());
			}
			tokenStream.end();
			tokenStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	// 测试切词
	public static void main(String[] args) {
		AnalyzerCN_LM analyzer = new AnalyzerCN_LM();
		List<String> l = analyzer
				.analyzerCnStr("对于Lucene4.3，在Lucene的下载包里同步了SmartCN的分词器针对中文发行的，每一次Lucene有新的版本发行，这个包同时更新。");
		System.out.println(l);
	}
}