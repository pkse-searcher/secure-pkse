package com.github.pekssearcher.lspeks.documentproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;
import com.n1analytics.paillier.cli.SerialisationUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class DocumentProducerApplication implements CommandLineRunner {

	RestTemplate restTemplate = new RestTemplate();

	private static String dictionaryFile = "dictionary.txt";
	public static void main(String[] args) {
		SpringApplication.run(DocumentProducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(args.length >= 1) dictionaryFile = args[0];

		//System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        String publicKeyString = IOUtils.resourceToString("publicKey.pub", Charset.defaultCharset(), DocumentProducerApplication.class.getClassLoader());
        // System.out.println(publicKeyString);

        final ObjectMapper mapper = new ObjectMapper();
        final var publicKey = mapper.readValue(publicKeyString, Map.class);
        PaillierPublicKey pub = SerialisationUtil.unserialise_public(publicKey);


		Scanner user_input = new Scanner(System.in);
		Path dir;
		System.out.println("Enter documents path");
		String docPath = user_input.nextLine();
		dir = Paths.get(docPath);

		PaillierContext signedContext = pub.createSignedContext();
		FilePolicyGenerator filePolicyGenerator = new FilePolicyGenerator(dictionaryFile);
		EncryptedNumber[] data;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path entry : stream) {
				DocumentPolicy documentPolicy=new DocumentPolicy();
				data = filePolicyGenerator.filePolicyEncrypt(docPath + entry.getFileName(), signedContext);
				documentPolicy.encryptedNumbers= new ArrayList<ObjectNode>();
				for (int i = 0; i <data.length ; i++) {
					documentPolicy.encryptedNumbers.add(SerialisationUtil.serialise_encrypted(data[i]));
				}
				documentPolicy.id = entry.getFileName().toString();
				String response = restTemplate.postForObject("http://localhost:8080/receive", documentPolicy, String.class);
				System.out.println(response);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
