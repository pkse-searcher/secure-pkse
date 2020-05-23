package com.github.pekssearcher.lspeks.cloudserver;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DocumentPolicy {
    String id;
    List<Map<String, String>> encryptedNumbers;
    byte[] encryptedDoc;
}
