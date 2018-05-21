import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class Main {

	private static File directory = new File(".");

	public static void main(String[] args) {
		try {
			System.out.println("Would you like to use the snapshot mcp versions? [true/false]");
			Scanner scanner2 = new Scanner(System.in);
			boolean line2 = scanner2.nextBoolean();

			System.out.println("Current MCP-Versions: " + VersionGetter.INSTANCE.getVersions(line2));
			System.out.println("Which mcp-version do you want?");
			Scanner scanner = new Scanner(System.in);
			String line1 = scanner.nextLine();

			System.out.println("Getting URLS for version: " + line1 + " ...");
			String url1 = URLGetter.INSTANCE.getURL(line1, line2)[0];
			String url2 = URLGetter.INSTANCE.getURL(line1, line2)[1];
			System.out.println("Founded URLS: " + url1 + " : " + url2);
			scanner.close();
			scanner2.close();
			System.out.println("Deleting old ressource files ...");

			File delete = new File(directory + "/conf/");

			File[] alldelete = delete.listFiles();
			for (File file : alldelete) {
				if (file.isFile()) {
					if (!file.getName().contains("mcp.cfg") && !file.getName().contains("version.cfg")) {
						file.delete();
					}
				}
			}

			FileUtils.deleteDirectory(new File(directory + "/conf/patches"));

			System.out.println("Downloading ressources ...");

			File file1 = new File(directory, line1 + ".zip");
			FileUtils.copyURLToFile(new URL(url1), file1);
			unpackArchive(file1, new File(directory + "/conf"));
			file1.delete();

			File file2 = new File(directory, line1 + "_2" + ".zip");
			FileUtils.copyURLToFile(new URL(url2), file2);
			unpackArchive(file2, new File(directory + "/conf"));
			file2.delete();

			System.out.println("Successful download");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("resource")
	public static File unpackArchive(File theFile, File targetDir) throws IOException {
		if (!theFile.exists()) {
			throw new IOException(theFile.getAbsolutePath() + " does not exist");
		}
		if (!buildDirectory(targetDir)) {
			throw new IOException("Could not create directory: " + targetDir);
		}
		ZipFile zipFile = new ZipFile(theFile);
		for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File file = new File(targetDir, File.separator + entry.getName());
			if (!buildDirectory(file.getParentFile())) {
				throw new IOException("Could not create directory: " + file.getParentFile());
			}
			if (!entry.isDirectory()) {
				copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
			} else {
				if (!buildDirectory(file)) {
					throw new IOException("Could not create directory: " + file);
				}
			}
		}
		zipFile.close();
		return theFile;
	}

	public static boolean buildDirectory(File file) {
		return file.exists() || file.mkdirs();
	}

	public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

}
