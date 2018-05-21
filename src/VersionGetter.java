import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public enum VersionGetter {

	INSTANCE;

	private ArrayList<String> versions = new ArrayList<>();

	public String getVersions(boolean snapshot) {
		String content = getContentofURL(snapshot ? "http://export.mcpbot.bspk.rs/snapshot/?page=1"
				: "http://export.mcpbot.bspk.rs/stable/?page=1");
		Scanner scanner = new Scanner(content);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if ((snapshot ? line.contains("<li><a href=\"/snapshot/") : line.contains("<li><a href=\"/stable/"))
					&& !line.contains("All")) {
				String replaced = (snapshot ? line.replace("<li><a href=\"/snapshot/", "")
						: line.replace("<li><a href=\"/stable/", "")).replace("</a></li>", "");
				int end = replaced.indexOf("/\">");
				if (end != -1) {
					String endstring = replaced.substring(0, end);
					versions.add(endstring);
				}
			}
		}

		scanner.close();
		String all = versions.toString().replace("[", "");

		return all.replace("]", "");
	}

	private String getContentofURL(String nurl) {
		String pageText = null;
		try {
			URL url = new URL(nurl);
			URLConnection conn = url.openConnection();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				pageText = reader.lines().collect(Collectors.joining("\n"));
			}
		} catch (Exception e) {
			pageText = null;
		}

		return pageText;
	}

}
