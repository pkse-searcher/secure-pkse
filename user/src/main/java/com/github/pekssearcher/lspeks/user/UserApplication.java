package com.github.pekssearcher.lspeks.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class UserApplication implements CommandLineRunner {

	//System.setErr(new PrintStream(OutputStream.nullOutputStream()));
	public static void main(String[] args)  {
		SpringApplication.run(UserApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		Scanner user_input = new Scanner(System.in);
		String searchQuery;
		User user = new User();

		//single keyword search
		System.out.println("Enter a keyword");
		while (user_input.hasNext()) {
			searchQuery = user_input.next();
			if (searchQuery.equals("quit")) {
				break;
			}
			System.out.println("The file name is = " + user.searchFileSingle(searchQuery));
			System.out.println("Enter a keyword");
		}

	}
}
