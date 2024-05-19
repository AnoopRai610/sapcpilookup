package com.acn.utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

class NamespaceContextImp implements NamespaceContext {

	private Map<String,String> namespaces;

	public NamespaceContextImp(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return namespaces.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		for(Entry<String,String> e:namespaces.entrySet())
			if(e.getValue().equals(namespaceURI))
				return e.getKey();
		
		return "";
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		HashSet<String> itr = new HashSet<String>();
		namespaces.forEach((K,V)->{
			if(V.equals(namespaceURI))
				itr.add(K);
		});
		return itr.iterator();
	}
}
