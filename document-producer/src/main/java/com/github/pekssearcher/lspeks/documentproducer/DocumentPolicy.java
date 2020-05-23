package com.github.pekssearcher.lspeks.documentproducer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;

@Data
public class DocumentPolicy {
    String id;
    List<ObjectNode> encryptedNumbers;
    byte[] encryptedDoc;
}
