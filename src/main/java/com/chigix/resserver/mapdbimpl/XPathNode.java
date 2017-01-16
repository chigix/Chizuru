package com.chigix.resserver.mapdbimpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class XPathNode {

    private final String localName;

    private XPathNode parent = null;

    HashMap<String, ArrayList<XPathNode>> localNameMapping = new HashMap<>();

    public String getSimplePath() {
        if (parent == null) {
            return "/" + this.localName;
        } else {
            return parent.getSimplePath() + "/" + this.localName;
        }
    }

    public XPathNode(String localName) {
        this.localName = localName;
    }

    public void appendChild(XPathNode node) {
        localNameMapping.putIfAbsent(localName, new ArrayList<>());
        localNameMapping.get(localName).add(node);
        node.parent = this;
    }

    public XPathNode getParent() {
        return parent;
    }

    public String getLocalName() {
        return localName;
    }

}
