package com.github.pekssearcher.lspeks.user;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EncNumber {
    List<Map<String, String>> encryptedNumbers;
    boolean isLeaf;
    String id;
}
