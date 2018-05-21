import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public enum URLGetter {

	INSTANCE;

	private ArrayList<String> urls = new ArrayList<>();

	public String[] getURL(String version, boolean snapshots) {
		String content = getContentofURL(snapshots ? "http://export.mcpbot.bspk.rs/snapshot/" + version + "/?page=1"
				: "http://export.mcpbot.bspk.rs/stable/" + version + "/?page=1");
		Scanner scanner = new Scanner(content);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if ((snapshots ? line.contains("http://export.mcpbot.bspk.rs/mcp_snapshot/")
					: line.contains("http://export.mcpbot.bspk.rs/mcp_stable/")) && line.contains("Download Zip")) {
				String replaced = line.replace("<li><a href=\"", "").replace(
						"\"><span class=\"glyphicon glyphicon-download-alt\"></span>&nbsp;&nbsp;Download Zip</a></li>",
						"");
				
				urls.add(replaced);
			}
		}
		scanner.close();

		String content2 = getContentofURL("http://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp/" + version
				+ "/mcp-" + version + "-srg.zip");
		String other = null;
		if (content2 == null) {
			other = "http://mcpbot.bspk.rs/mcp/" + version + "/mcp-" + version + "-srg.zip";
		} else {
			other = "http://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp/" + version + "/mcp-" + version
					+ "-srg.zip";
		}

		return new String[] { urls.get(0), other };
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
