package kokonguyen191;

import java.io.File;

public class BulkGenerate {

	private String FOLDER;

	BulkGenerate(String folder) {
		FOLDER = folder;
		readFolder(FOLDER);
	}
	
	public void readFolder(String directoryName) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		
		for (File file : fList) {
			// If is osu beatmap
			if (file.isFile() && file.getAbsolutePath().endsWith("osu")) {
				try {
					SVGenerateWithUninheritedPoints svgwup = new SVGenerateWithUninheritedPoints(file.getAbsolutePath(), 16, 1);
					svgwup.generate();
					svgwup.writeSV();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					continue;
				}
			} else if (file.isDirectory()) {
				try {
					readFolder(file.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					continue;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		BulkGenerate bg = new BulkGenerate("songs");
	}
}
