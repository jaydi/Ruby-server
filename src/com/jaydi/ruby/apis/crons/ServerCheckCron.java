package com.jaydi.ruby.apis.crons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerCheckCron extends HttpServlet {
	private static final long serialVersionUID = 3416343198792266957L;
	private static final String CHECK_URL = "https://ruby-mine.appspot.com/_ah/api/rubymine/v1/rubyzones";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			URL url = new URL(CHECK_URL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			StringBuilder sb = new StringBuilder();

			while ((line = reader.readLine()) != null)
				sb.append(line);
			reader.close();

		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
	}
}
