package com.github.pekssearcher.lspeks.cloudserver;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;

@Data
public class EncNumber {
    List<ObjectNode> encryptedNumbers;
    boolean isLeaf;
    String id;
}
