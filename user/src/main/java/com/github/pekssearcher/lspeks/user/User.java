package com.github.pekssearcher.lspeks.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import com.n1analytics.paillier.cli.SerialisationUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class User {
    PaillierPrivateKey priv;
    PaillierPublicKey pub;
    List<String> words;
    HashMap<String, Integer> dictionary = new HashMap<>();
    int noFile = 0;
    private static String dictionaryFile = "dictionary.txt";
    RestTemplate restTemplate = new RestTemplate();

    public User() throws IOException{

        final ObjectMapper mapper = new ObjectMapper();

        String privateKeyString = IOUtils.resourceToString("privateKey.priv", Charset.defaultCharset(), UserApplication.class.getClassLoader());
        final Map privateKey = mapper.readValue(privateKeyString, Map.class);
        this.priv = SerialisationUtil.unserialise_private(privateKey);

        String publicKeyString = IOUtils.resourceToString("publicKey.pub", Charset.defaultCharset(), UserApplication.class.getClassLoader());
        final Map publicKey = mapper.readValue(publicKeyString, Map.class);
        this.pub = SerialisationUtil.unserialise_public(publicKey);

        words = Arrays.asList(IOUtils.resourceToString(dictionaryFile, Charset.defaultCharset(), UserApplication.class.getClassLoader()).split("\\s+"));
        for (int i = 0; i < words.size(); i++) {
            dictionary.put(words.get(i), i);
        }
    }

    public String searchFileSingle(String searchQuery) throws IOException {

        if (!dictionary.containsKey(searchQuery))
            return "Word is not in dictionary";

        int index = dictionary.get(searchQuery);
            String root= "initial";
           while (true) {
               System.out.println(root);
               EncryptedNumber[] currentValue;
               EncNumber response = restTemplate.postForObject("http://localhost:8080/subtracted", root, EncNumber.class);
               if (response.isLeaf) {
                   return response.id;
               } else {
                   EncryptedNumber[] data = new EncryptedNumber[response.encryptedNumbers.size()];
                   int i = 0;
                   for (Map e : response.encryptedNumbers) {
                       data[i] = SerialisationUtil.unserialise_encrypted(e, pub);
                       i++;
                   }
                   currentValue = data;
                   BigInteger decryptedValue = priv.decrypt(currentValue[index]).decodeBigInteger();
                   if (decryptedValue.signum() >= 0)
                       root = "root-left";
                   else
                       root = "root-right";
               }
           }
    }

}