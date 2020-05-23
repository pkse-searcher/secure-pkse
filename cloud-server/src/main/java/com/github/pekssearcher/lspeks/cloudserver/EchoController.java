package com.github.pekssearcher.lspeks.cloudserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pekssearcher.lspeks.cloudserver.tree.Tree;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import com.n1analytics.paillier.cli.SerialisationUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class EchoController {
  private final Tree tree;
  PaillierPublicKey pub;

    public EchoController() throws IOException {
        this.tree= new Tree();
        String publicKeyString = IOUtils.resourceToString("publicKey.pub", Charset.defaultCharset(), CloudServerApplication.class.getClassLoader());
        // System.out.println(publicKeyString);
        final ObjectMapper mapper = new ObjectMapper();
        final var publicKey = mapper.readValue(publicKeyString, Map.class);
        pub = SerialisationUtil.unserialise_public(publicKey);
    }

    @GetMapping(path= "test")
    public String test(){
        return "hello document producer/user";
    }

    @GetMapping(path= "count")
    public int count(){
        return tree.countNode();
    }


    @PostMapping(path = "receive")
    public String receive(@RequestBody DocumentPolicy received) throws IOException {

        EncryptedNumber[] data =new EncryptedNumber[received.encryptedNumbers.size()];
        String id= received.id;
        int i=0;

        for(Map e: received.encryptedNumbers){
            data[i]= SerialisationUtil.unserialise_encrypted(e, pub);
            i++;
        }
        tree.insert(data, id);
        return "done";

    }

    @PostMapping(path = "subtracted")
    public EncNumber value(@RequestBody String root) throws IOException {

        String direction= root;

        if(direction.equals("initial"))
            tree.currentNode=tree.root;
        if(direction.equals("root-left"))
            tree.currentNode=tree.currentNode.left;
        if(direction.equals("root-right"))
            tree.currentNode=tree.currentNode.right;

        EncNumber response= new EncNumber();
        response.encryptedNumbers= new ArrayList<>();
        EncryptedNumber[] data;
        data= tree.currentNode(tree.currentNode);
        if(data==null) {
            response.isLeaf = true;
            response.id = tree.currentNode.id;
        }
        else {
            for (int i = 0; i < data.length; i++) {
                response.encryptedNumbers.add(SerialisationUtil.serialise_encrypted(data[i]));
            }
        }
        return response;
    }
}
