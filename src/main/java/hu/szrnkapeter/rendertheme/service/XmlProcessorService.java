package hu.szrnkapeter.rendertheme.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.szrnkapeter.rendertheme.model.Param;
import hu.szrnkapeter.rendertheme.model.RuleData;

public final class XmlProcessorService {
	
	private static final String AREA = "AREA";
	private static final String CAPTION = "CAPTION";
	private static final String LINE = "LINE";
	static List<RuleData> ruleDataList = new ArrayList<>();
	
	public static List<RuleData> getRules(NodeList nList) {
		getRule(nList, 0);
		return ruleDataList;
	}

	private static void getRule(final NodeList nList, int level) {
		if (nList == null || nList.getLength() == 0) {
			return;
		}

		for (int temp = 0; temp < nList.getLength(); temp++) {
			final Node nNode = nList.item(temp);
			boolean hasData = false;
			final RuleData rule = new RuleData();

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				final Element eElement = (Element) nNode;

				rule.setE(eElement.getAttribute("e"));
				rule.setK(Arrays.asList(eElement.getAttribute("k").split("\\|")));
				rule.setV(Arrays.asList(eElement.getAttribute("v").split("\\|")));

				getAttribute("RULE", hasData, rule, eElement, "zoom-min");

				final NodeList newNl = eElement.getElementsByTagName("rule");
				if (newNl != null && newNl.getLength() > 0) {
					getRule(newNl, level + 1/*, sb*/);
				} else {
					// Areas
					final Element area = (Element) eElement.getElementsByTagName("area").item(0);
					if (area != null) {
						final boolean hasAnyParameter = false;
						getAttribute(AREA, hasAnyParameter, rule, area, "fill");
						getAttribute(AREA, hasAnyParameter, rule, area, "stroke");
						rule.setType(AREA);
						hasData = true;
					}

					// Captions
					final Element caption = (Element) eElement.getElementsByTagName("caption").item(0);
					if (caption != null) {
						final boolean hasAnyParameter = false;
						getAttribute(CAPTION, hasAnyParameter, rule, caption, "fill");
						getAttribute(CAPTION, hasAnyParameter, rule, caption, "stroke");
						getAttribute(CAPTION, hasAnyParameter, rule, caption, "font-size");
						getAttribute(CAPTION, hasAnyParameter, rule, caption, "symbol-id");
						getAttribute(CAPTION, hasAnyParameter, rule, caption, "k");
						rule.setType(CAPTION);

						hasData = true;
					}

					// Lines
					final Element line = (Element) eElement.getElementsByTagName("line").item(0);
					if (line != null) {
						final boolean hasAnyParameter = false;
						getAttribute(LINE, hasAnyParameter, rule, line, "fill");
						getAttribute(LINE, hasAnyParameter, rule, line, "stroke");
						rule.setType(LINE);

						hasData = true;
					}
				}
			}

			if (hasData) {
				ruleDataList.add(rule);
			}
		}
	}
	
	private static void getAttribute(String type, boolean hasData, final RuleData rule, Element element, String elementName) {
		if (element.getAttribute(elementName) == null || element.getAttribute(elementName).isEmpty()) {
			return;
		}

		hasData = true;
		rule.getParams().put(elementName, new Param(type, element.getAttribute(elementName)));
	}
}