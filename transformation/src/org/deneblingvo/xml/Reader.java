package org.deneblingvo.xml;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
public class Reader {
	
	Object object;
	
	public Reader (Object object) {
		this.object = object;
	}
	
	private void parseNode(Element element, Object object) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		Class<?> objectClass = object.getClass();
		NamedNodeMap attributes = element.getAttributes();
		int index;
		for (index = 0; index < attributes.getLength(); index++) {
			Node node = attributes.item(index);
			Attr attribute = (Attr)node;
			Field field = objectClass.getField(attribute.getLocalName());
			field.set(object, attribute.getValue());
		}
		NodeList nodes = element.getChildNodes();
		
		for (index = 0; index < nodes.getLength(); index++) {
			Node node = nodes.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element current = (Element)node;
				Field field = objectClass.getField(current.getLocalName());
				Class<?> fieldClass = field.getDeclaringClass();
				Object fieldValue = fieldClass.newInstance();
				field.set(object, fieldValue);
				this.parseNode(current, fieldValue);
			}
		}
	}

	public void parse (Document document) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		this.parseNode(document.getDocumentElement(), this.object);
	}
}