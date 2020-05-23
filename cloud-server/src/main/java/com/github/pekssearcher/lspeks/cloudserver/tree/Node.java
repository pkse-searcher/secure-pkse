package com.github.pekssearcher.lspeks.cloudserver.tree;

import com.n1analytics.paillier.EncryptedNumber;

public class Node {
    public String id;
    public EncryptedNumber[] data;
    public Node left, right, parent, next;
}

