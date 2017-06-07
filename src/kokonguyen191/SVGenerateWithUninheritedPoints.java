package kokonguyen191;

import java.io.File;

public class SVGenerateWithUninheritedPoints extends SVGenerate {

	protected double m_snap = -1;

	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            osu beatmap input
	 * @param rate
	 *            sv rate / interval
	 * @throws Exception
	 */
	SVGenerateWithUninheritedPoints(String fileName, int rate) throws Exception {
		super(fileName, rate);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param fileName
	 *            osu beatmap input
	 * @param rate
	 *            sv rate / interval
	 * @param BPM
	 *            main bpm
	 * @throws Exception
	 */
	SVGenerateWithUninheritedPoints(String fileName, int rate, double BPM) throws Exception {
		super(fileName, rate, BPM);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            osu beatmap input
	 * @param rate
	 *            sv rate / interval
	 * @param snap
	 *            snap divisor to put uninherited points
	 * @throws Exception
	 */
	SVGenerateWithUninheritedPoints(String fileName, int rate, int snap) throws Exception {
		super(fileName, rate);
		m_snap = snap;
	}

	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            osu beatmap input
	 * @param rate
	 *            sv rate / interval
	 * @param BPM
	 *            main bpm
	 * @param snap
	 *            snap divisor to put uninherited points
	 * @throws Exception
	 */
	SVGenerateWithUninheritedPoints(String fileName, int rate, double BPM, int snap) throws Exception {
		super(fileName, rate, BPM);
		m_snap = snap;
	}

	@Override
	protected void generate() {
		System.out.println("Commence to generate SVs...");
		int count = 0;
		double currentMsPB = m_msPerBeat.get(count);
		double svMultiplier = mainMsPerBeat / (60000 / currentMsPB);
		double lastOffset = m_offsets.get(count);
		double nextOffset = m_offsets.get(count + 1);

		String firstSVTextText = Double.toString(firstSVText * svMultiplier);
		String secondSVTextText = Double.toString(secondSVText * svMultiplier);

		String timingPointData = ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",0," + m_kiai.get(count);
		String fun = "," + currentMsPB + ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",1,"
				+ m_kiai.get(count);

		for (int i = 0; i < m_beats.size() - 1; i++) {
			double firstOffset = m_beats.get(i);

			// Update if next offset is reached
			if (firstOffset >= nextOffset) {
				count++;
				lastOffset = nextOffset;
				nextOffset = m_offsets.get(count + 1);
				currentMsPB = m_msPerBeat.get(count);
				svMultiplier = mainMsPerBeat / (60000 / currentMsPB);
				firstSVTextText = Double.toString(firstSVText / svMultiplier);
				secondSVTextText = Double.toString(secondSVText / svMultiplier);
				timingPointData = ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",0," + m_kiai.get(count);
				fun = "," + currentMsPB + ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",1,"
						+ m_kiai.get(count);
			}

			int interval = m_beats.get(i + 1) - m_beats.get(i);
			double secondOffset = firstOffset + interval * (m_rate - 1) / m_rate;

			String firstSVToAdd = Double.toString(firstOffset) + "," + firstSVTextText + timingPointData;
			String secondSVToAdd = Double.toString(secondOffset) + "," + secondSVTextText + timingPointData;

			if (m_snap == -1 || Math.abs((m_snap * ((firstOffset - lastOffset) / currentMsPB)
					- Math.round(m_snap * ((firstOffset - lastOffset) / currentMsPB)))) < 0.02) {
				m_svs.add(Double.toString(firstOffset) + fun);
			} else {
				m_svs.add(Double.toString(firstOffset));
			}
			m_svs.add(firstSVToAdd);
			m_svs.add(secondSVToAdd);
		}
		System.out.println("SVs successfully generated...");
	}
	
	public static void runFile(File file, int rate, double BPM) {
		try {
			StopWatch sw = new StopWatch();
			sw.reset();
			sw.start();
			SVGenerateWithUninheritedPoints svg = new SVGenerateWithUninheritedPoints(
					file.getCanonicalPath(),
					rate, BPM);
			svg.generate();
			svg.writeSV();
			sw.stop();
			System.out.println("SVs generated in " + sw.getTime() + "s.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runFolder(File folder, int rate, double BPM) {
		try {
			File [] list = folder.listFiles();
			for(File stuff : list) {
				if(stuff.isDirectory()) {
					runFolder(stuff, rate, BPM);
				}
				else if(stuff.getCanonicalPath().substring(
						stuff.getCanonicalPath().length() - 3, stuff.getCanonicalPath().length()).equals("osu")) {
					runFile(stuff, rate, BPM);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			StopWatch sw = new StopWatch();
			sw.reset();
			sw.start();
			// Put your map here
			SVGenerateWithUninheritedPoints svg = new SVGenerateWithUninheritedPoints(
					"",
					16, 4);
			svg.generate();
			svg.writeSV();
			sw.stop();
			System.out.println("SVs generated in " + sw.getTime() + "s.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
