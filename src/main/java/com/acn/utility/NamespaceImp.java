package com.acn.utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

/**
 * NamespaceContextImp is a custom implementation of {@link NamespaceContext}
 * used to resolve XML namespaces in XPath expressions.
 * <p>
 * It allows mapping of prefixes to namespace URIs and supports reverse lookup.
 * This is particularly useful when working with XML documents that use namespaces.
 */
class NamespaceContextImp implements NamespaceContext {

    private final Map<String, String> namespaces;

    /**
     * Constructs a NamespaceContextImp with a map of prefix-to-namespace URI mappings.
     *
     * @param namespaces a map where keys are prefixes and values are namespace URIs
     */
    public NamespaceContextImp(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Returns the namespace URI for a given prefix.
     *
     * @param prefix the namespace prefix
     * @return the corresponding namespace URI, or null if not found
     */
    @Override
    public String getNamespaceURI(String prefix) {
        return namespaces.get(prefix);
    }

    /**
     * Returns the first prefix associated with the given namespace URI.
     *
     * @param namespaceURI the namespace URI
     * @return the corresponding prefix, or an empty string if not found
     */
    @Override
    public String getPrefix(String namespaceURI) {
        for (Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Returns all prefixes associated with the given namespace URI.
     *
     * @param namespaceURI the namespace URI
     * @return an iterator over all matching prefixes
     */
    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        HashSet<String> matchingPrefixes = new HashSet<>();
        namespaces.forEach((prefix, uri) -> {
            if (uri.equals(namespaceURI)) {
                matchingPrefixes.add(prefix);
            }
        });
        return matchingPrefixes.iterator();
    }
}