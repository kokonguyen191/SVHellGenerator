package kokonguyen191;

import java.io.File;

public class Generator {

	/*
	 * ███████╗██╗██╗     ███████╗    ██████╗ ███████╗ █████╗ ██████╗ ███████╗██████╗ 
	 * ██╔════╝██║██║     ██╔════╝    ██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗
	 * █████╗  ██║██║     █████╗      ██████╔╝█████╗  ███████║██║  ██║█████╗  ██████╔╝
	 * ██╔══╝  ██║██║     ██╔══╝      ██╔══██╗██╔══╝  ██╔══██║██║  ██║██╔══╝  ██╔══██╗
	 * ██║     ██║███████╗███████╗    ██║  ██║███████╗██║  ██║██████╔╝███████╗██║  ██║
	 * ╚═╝     ╚═╝╚══════╝╚══════╝    ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═════╝ ╚══════╝╚═╝  ╚═╝
	 */

	public static void readFile(File file, SVGenerate svg, double snap, int rate, double BPM) {
		try {
			if (svg.getClass() == SVGenerate.class) {
				svg = new SVGenerate(file.getAbsolutePath(), rate, BPM);
			} else if (svg.getClass() == SVGenerateWithUninheritedPoints.class) {
				svg = new SVGenerateWithUninheritedPoints(file.getAbsolutePath(), snap, rate, BPM);
			}
			svg.generate();
			svg.writeSV();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readFile(File file, SVGenerate svg, double snap, int rate) {
		try {
			if (svg.getClass() == SVGenerate.class) {
				svg = new SVGenerate(file.getAbsolutePath(), rate);
			} else if (svg.getClass() == SVGenerateWithUninheritedPoints.class) {
				svg = new SVGenerateWithUninheritedPoints(file.getAbsolutePath(), snap, rate);
			}
			svg.generate();
			svg.writeSV();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ███████╗ ██████╗ ██╗     ██████╗ ███████╗██████╗     ██████╗ ███████╗ █████╗ ██████╗ ███████╗██████╗ 
	 * ██╔════╝██╔═══██╗██║     ██╔══██╗██╔════╝██╔══██╗    ██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔══██╗
	 * █████╗  ██║   ██║██║     ██║  ██║█████╗  ██████╔╝    ██████╔╝█████╗  ███████║██║  ██║█████╗  ██████╔╝
	 * ██╔══╝  ██║   ██║██║     ██║  ██║██╔══╝  ██╔══██╗    ██╔══██╗██╔══╝  ██╔══██║██║  ██║██╔══╝  ██╔══██╗
	 * ██║     ╚██████╔╝███████╗██████╔╝███████╗██║  ██║    ██║  ██║███████╗██║  ██║██████╔╝███████╗██║  ██║
	 * ╚═╝      ╚═════╝ ╚══════╝╚═════╝ ╚══════╝╚═╝  ╚═╝    ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═════╝ ╚══════╝╚═╝  ╚═╝                                                                                               
	 */

	@SuppressWarnings("finally")
	public static void readFolder(File directory, SVGenerate svg, double snap, int rate) {

		File[] files = directory.listFiles();
		for (File file : files) {
			try {
				if (file.isDirectory()) {
					readFolder(file, svg, snap, rate);
				} else if (file.isFile() && file.getAbsolutePath().endsWith("osu")) {
					readFile(file, svg, snap, rate);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				continue;
			}
		}
	}
}
