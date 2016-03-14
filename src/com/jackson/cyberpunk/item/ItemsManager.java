package com.jackson.cyberpunk.item;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.jackson.cyberpunk.health.Arm;
import com.jackson.cyberpunk.health.DualPart;
import com.jackson.cyberpunk.health.Injury;
import com.jackson.cyberpunk.health.Part.Type;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;
import com.jackson.myengine.Log;

public class ItemsManager {
	private ItemsManager() {
	}

	private static Map<String, Item> data;
	private static Map<String, Injury> injuries;
	private static Map<String, Map<String, String>> defaults;
	private static Map<String, Map<String, Map<String, String>>> defaultsWithType;

	public static void init() {
		data = new HashMap<>();
		defaults = new HashMap<>();
		defaultsWithType = new HashMap<>();
		injuries = new HashMap<>();

		try {
			// parsing defaults
			Files.walkFileTree(Paths.get("res/items"), new MyFileVisitor("defaults"));
			// parsing injuries
			Files.walkFileTree(Paths.get("res/items"), new MyFileVisitor("injuries"));
			// parsing items
			Files.walkFileTree(Paths.get("res/items"), new MyFileVisitor("description"));
		} catch (IOException e) {
			Log.e("ItemsManager.java:init(): " + e);
		}
	}

	public static Item getItem(String name) {
		return data.get(name).copy();
	}

	private static class MyFileVisitor extends SimpleFileVisitor<Path> {
		private ItemParser xmlParser;
		private String target;

		public MyFileVisitor(String target) {
			xmlParser = new ItemParser();
			this.target = target + ".xml";
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
			String name = file.getFileName().toString();
			if (name.equals(target)) {
				xmlParser.parse(file, data);
			}
			return FileVisitResult.CONTINUE;
		}
	}

	private static class ItemParser {
		private DocumentBuilder db;

		public ItemParser() {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				Log.e("ItemsManager.java:ItemParser:ItemParser(): " + e);
			}
		}

		private void parse(Path file, Map<String, Item> data) {
			try {
				Log.d("file: " + file);
				Document doc = db.parse(file.toFile());

				Element root = doc.getDocumentElement();
				Node node = root.getFirstChild();
				while (node != null && (node.getNodeName().equals("#text") || node
						.getNodeName().equals("#comment"))) {
					node = node.getNextSibling();
				}
				switch (root.getNodeName()) {
				case "items":
					while (node != null) {
						Item item = null;
						Element e = (Element) node;
						switch (node.getNodeName()) {
						case "knapsack":
							item = parseKnapsack(e);
							break;
						case "weapon":
							item = parseWeapon(e);
							break;
						case "part":
							item = parsePart(e);
							break;
						default:
							throw new RuntimeException("Unknown inner xml tag");
						}
						item.pictureName = file.getParent().toString() + "\\"
								+ item.pictureName;
						data.put(e.getAttribute("name"), item);
						do {
							node = node.getNextSibling();
						} while (node != null && (node.getNodeName().equals("#text")
								|| node.getNodeName().equals("#comment")));
						;
					}
					break;
				case "injuries":
					Log.d("Parsing injuries...");
					while (node != null) {
						Log.d("nodename: " + node.getNodeName());
						Element e = (Element) node;
						injuries.put(e.getAttribute("name"), parseInjury(e));
						do {
							node = node.getNextSibling();
						} while (node != null && (node.getNodeName().equals("#text")
								|| node.getNodeName().equals("#comment")));
					}
					Log.d("Finish parsing injuries");
					break;
				case "defaults":
					Log.d("Parsing defaults...");
					while (node != null) {
						Element e = (Element) node;
						String tagName = e.getTagName();
						Map<String, String> map = new HashMap<>();
						NamedNodeMap nnm = e.getAttributes();
						for (int i = 0; i < nnm.getLength(); i++) {
							Node attr = nnm.item(i);
							if (attr.getNodeName().equals("type")) {
								continue;
							}
							map.put(attr.getNodeName(), attr.getNodeValue());
						}

						if (e.hasAttribute("type")) {
							String type = e.getAttribute("type");
							if (defaultsWithType.containsKey(tagName)) {
								if (defaultsWithType.get(tagName).containsKey(type)) {
									defaultsWithType.get(tagName).get(type).putAll(map);
								} else {
									defaultsWithType.get(tagName).put(type, map);
								}
							} else {
								Map<String, Map<String, String>> temp = new HashMap<>();
								temp.put(type, map);
								defaultsWithType.put(tagName, temp);
							}
						} else {
							if (defaults.containsKey(tagName)) {
								defaults.get(tagName).putAll(map);
							} else {
								defaults.put(tagName, map);
							}
						}
						do {
							node = node.getNextSibling();
						} while (node != null && (node.getNodeName().equals("#text")
								|| node.getNodeName().equals("#comment")));
					}
					Log.d("Finish parsing defaults");
					break;
				default:
					throw new RuntimeException("Unknown outer xml tag");
				}
			} catch (Exception e) {
				Log.e("ItemsManager.java:ItemParser:parse(): " + e);
				e.printStackTrace();
			}
		}

		String getAttribute(Element e, String name) {
			String tagName = e.getTagName();
			if (e.hasAttribute("type")) {
				String type = e.getAttribute("type");
				if (defaultsWithType.containsKey(tagName) && defaultsWithType.get(
						tagName).containsKey(type)) {
					Map<String, String> map = defaultsWithType.get(tagName).get(type);
					if (map.containsKey(name)) {
						return map.get(name);
					}
				}
			} else {
				if (defaults.containsKey(tagName)) {
					return defaults.get(tagName).get(name);
				}
			}
			return e.getAttribute(name);
		}

		Item parseKnapsack(Element e) {
			String name = getAttribute(e, "name");
			String desc = getAttribute(e, "desc");
			String picture = getAttribute(e, "picture");
			int sizeI = Integer.parseInt(getAttribute(e, "sizeI"));
			int sizeJ = Integer.parseInt(getAttribute(e, "sizeJ"));
			int cost = Integer.parseInt(getAttribute(e, "cost"));
			int capacity = Integer.parseInt(getAttribute(e, "capacity"));
			return new Knapsack(name, desc, picture, sizeI, sizeJ, cost, capacity);
		}

		Item parseWeapon(Element e) {
			boolean melee = getAttribute(e, "type").equals("melee");
			String name = getAttribute(e, "name");
			String desc = getAttribute(e, "desc");
			String picture = getAttribute(e, "picture");
			int sizeI = Integer.parseInt(getAttribute(e, "sizeI"));
			int sizeJ = Integer.parseInt(getAttribute(e, "sizeJ"));
			int cost = Integer.parseInt(getAttribute(e, "cost"));
			float attackAP = Float.parseFloat(getAttribute(e, "AP"));
			int maxAmmo = -1;
			boolean twoHanded = Boolean.parseBoolean(getAttribute(e, "twoHanded"));

			if (!melee) {
				maxAmmo = Integer.parseInt(getAttribute(e, "maxAmmo"));
			}

			InjuryHelper helper = null;
			Node node = e.getFirstChild();
			while (node != null && (node.getNodeName().equals("#text") || node
					.getNodeName().equals("#comment"))) {
				node = node.getNextSibling();
			}
			while (node != null) {
				if (node.getNodeName().equals("injuryTypes")) {
					helper = parseInjuryHelper((Element) node);
				} else {
					throw new RuntimeException("Unknown inner xml tag");
				}
				do {
					node = node.getNextSibling();
				} while (node != null && (node.getNodeName().equals("#text") || node
						.getNodeName().equals("#comment")));
			}
			if (melee) {
				return new MeleeWeapon(name, desc, picture, sizeI, sizeJ, cost,
						twoHanded, attackAP, helper);
			} else {
				return new RangedWeapon(name, desc, picture, maxAmmo, sizeI, sizeJ, cost,
						twoHanded, attackAP, helper);
			}
		}

		Injury parseInjury(Element e) {
			String name = e.getAttribute("name");
			String desc = e.getAttribute("desc");
			String treated = e.getAttribute("treated");
			float pain = Float.parseFloat(e.getAttribute("pain"));
			float bleeding = Float.parseFloat(e.getAttribute("bleeding"));
			int treatTime = Integer.parseInt(e.getAttribute("treatTime"));
			boolean combines = Boolean.getBoolean(e.getAttribute("combines"));
			String fatal = e.getAttribute("fatal");
			return new Injury(name, desc, treated, pain, bleeding, treatTime, combines,
					fatal);
		}

		InjuryHelper parseInjuryHelper(Element e) {
			InjuryHelper res = new InjuryHelper();
			Node node = e.getFirstChild();
			while (node != null && (node.getNodeName().equals("#text") || node
					.getNodeName().equals("#comment"))) {
				node = node.getNextSibling();
			}
			while (node != null) {
				String nodeName = node.getNodeName();
				Element el = (Element) node;
				if (injuries.containsKey(nodeName)) {
					res.add(injuries.get(nodeName), Float.parseFloat(el.getAttribute(
							"prob")));
				} else {
					throw new RuntimeException("Unknown injury type: " + nodeName);
				}
				do {
					node = node.getNextSibling();
				} while (node != null && (node.getNodeName().equals("#text") || node
						.getNodeName().equals("#comment")));
			}
			return res;
		}

		Item parsePart(Element e) {
			String typeName = getAttribute(e, "type");
			Log.d("Parsing part " + typeName);
			String name = getAttribute(e, "name");
			String desc = getAttribute(e, "desc");
			String picture = getAttribute(e, "picture");
			int cost = Integer.parseInt(getAttribute(e, "cost"));
			float strength = Float.parseFloat(getAttribute(e, "strength"));
			boolean organic = Boolean.parseBoolean(getAttribute(e, "organic"));
			int sizeI = Integer.parseInt(getAttribute(e, "sizeI"));
			int sizeJ = Integer.parseInt(getAttribute(e, "sizeJ"));

			Type type = (typeName.equals("arm") ? Type.ARM
					: typeName.equals("leg") ? Type.LEG : Type.EYE);

			InjuryHelper helper = null;
			ArrayList<PartProfit> profits = null;

			Node node = e.getFirstChild();

			while (node != null && (node.getNodeName().equals("#text") || node
					.getNodeName().equals("#comment"))) {
				node = node.getNextSibling();
			}

			while (node != null) {
				String nodeName = node.getNodeName();
				switch (nodeName) {
				case "injuryTypes":
					helper = parseInjuryHelper((Element) node);
					break;
				case "profits":
					profits = parsePartProfits((Element) node);
					break;
				default:
					throw new RuntimeException("Unknown inner xml tag: " + nodeName);
				}
				do {
					node = node.getNextSibling();
				} while (node != null && (node.getNodeName().equals("#text") || node
						.getNodeName().equals("#comment")));
			}

			if (profits == null) {
				profits = new ArrayList<>();
			}
			if (type == Type.ARM) {
				Log.d("Finish parsing part");
				float attackAP = Float.parseFloat(getAttribute(e, "AP"));
				return new Arm(name, desc, picture, sizeI, sizeJ, strength, cost,
						attackAP, helper, organic, profits);
			}
			if (helper != null) {
				throw new RuntimeException(
						"You should use injuryTypes only for arm part");
			}
			Log.d("Finish parsing part");
			return new DualPart(type, name, desc, picture, sizeI, sizeJ, strength, cost,
					organic, profits);
		}

		ArrayList<PartProfit> parsePartProfits(Node node) {
			ArrayList<PartProfit> res = new ArrayList<>();

			node = node.getFirstChild();

			while (node != null && (node.getNodeName().equals("#text") || node
					.getNodeName().equals("#comment"))) {
				node = node.getNextSibling();
			}

			while (node != null) {
				PartProfit.Type type = PartProfit.Type.valueOf(node.getNodeName()
						.toUpperCase());
				String value = ((Element) node).getAttribute("value");
				res.add(new PartProfit(type, value));

				do {
					node = node.getNextSibling();
				} while (node != null && (node.getNodeName().equals("#text") || node
						.getNodeName().equals("#comment")));
			}

			return res;
		}
	}
}