package kokonguyen191;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class SVGenerate {

	protected int m_rate = -1;
	protected String FILENAME = null;
	protected String NEWFILE = null;
	protected OsuBeatmapParser m_pb = null;
	protected ArrayList<Integer> m_beats = null;
	protected ArrayList<Double> m_offsets = null;
	protected ArrayList<Double> m_msPerBeat = null;
	protected ArrayList<Boolean> m_inheritance = null;
	protected ArrayList<Integer> m_sounds = null;
	protected ArrayList<Integer> m_volume = null;
	protected ArrayList<Integer> m_kiai = null;
	protected ArrayList<String> m_svs = null;
	public double mainMsPerBeat = -1;

	protected double firstSV = -1.0;
	protected double secondSV = -1.0;
	protected double firstSVText = -1.0;
	protected double secondSVText = -1.0;

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
	SVGenerate(String fileName, int rate, double BPM) throws Exception {
		System.out.println("Reading file...");
		if (rate <= 1) {
			throw new IllegalArgumentException("Rate lower than or equal to 1.");
		} else {

			// Parameter initialization
			m_rate = rate;
			FILENAME = fileName;

			OsuBeatmapParser m_pb = new OsuBeatmapParser(fileName);
			m_beats = m_pb.getBeats();
			m_offsets = m_pb.getOffsets();
			m_msPerBeat = m_pb.getMsPerBeat();
			m_inheritance = m_pb.getInheritance();
			m_sounds = m_pb.getSounds();
			m_volume = m_pb.getVolume();
			m_kiai = m_pb.getKiai();

			m_svs = new ArrayList<String>();
			NEWFILE = m_pb.m_newFile;

			// Calculate SV
			firstSV = 0.01;
			secondSV = rate - 0.01 * (rate - 1);
			firstSVText = -100 / firstSV;
			secondSVText = -100 / secondSV;

			// Get main tempo
			mainMsPerBeat = 60000 / BPM;

			System.out.println("File read.");
		}
	}

	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            osu beatmap input
	 * @param rate
	 *            sv rate / interval
	 * @throws Exception
	 */
	SVGenerate(String fileName, int rate) throws Exception {
		this(fileName, rate, 0);

		// Get main tempo
		double noOfBeats = m_offsets.get(0) / m_msPerBeat.get(0);
		mainMsPerBeat = m_msPerBeat.get(0);
		for (int i = 0; i < m_msPerBeat.size() - 1; i++) {
			if (m_offsets.get(i + 1) * m_msPerBeat.get(i + 1) - m_offsets.get(i) / m_msPerBeat.get(i) > noOfBeats) {
				mainMsPerBeat = m_msPerBeat.get(i);
				noOfBeats = m_offsets.get(i + 1) / m_msPerBeat.get(i + 1) - m_offsets.get(i) / m_msPerBeat.get(i);
			}
		}

		System.out.println("=============================================================");
		System.out.println("Main tempo is " + (int) Math.floor(60000 / mainMsPerBeat) + " BPM.");
		System.out.println("If the main tempo is wrong, please use the other constructor.");
		System.out.println("=============================================================");

	}

	/**
	 * Generate SVs with only inherited timing points
	 */
	protected void generate() {
		m_svs.clear();
		System.out.println("Commence to generate SVs...");
		int count = 0;
		double currentMsPB = m_msPerBeat.get(count);
		double svMultiplier = mainMsPerBeat / (60000 / currentMsPB);
		double nextOffset = m_offsets.get(count + 1);

		String firstSVTextText = Double.toString(firstSVText * svMultiplier);
		String secondSVTextText = Double.toString(secondSVText * svMultiplier);

		String timingPointData = ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",0," + m_kiai.get(count);

		for (int i = 0; i < m_beats.size() - 1; i++) {
			double firstOffset = m_beats.get(i);

			// Update if next offset is reached
			if (firstOffset >= nextOffset) {
				count++;
				nextOffset = m_offsets.get(count + 1);
				currentMsPB = m_msPerBeat.get(count);
				svMultiplier = mainMsPerBeat / (60000 / currentMsPB);
				firstSVTextText = Double.toString(firstSVText / svMultiplier);
				secondSVTextText = Double.toString(secondSVText / svMultiplier);
				timingPointData = ",4,2," + m_sounds.get(count) + "," + m_volume.get(count) + ",0," + m_kiai.get(count);
			}

			int interval = m_beats.get(i + 1) - m_beats.get(i);
			double secondOffset = firstOffset + interval * (m_rate - 1) / m_rate;

			String firstSVToAdd = Double.toString(firstOffset) + "," + firstSVTextText + timingPointData;
			String secondSVToAdd = Double.toString(secondOffset) + "," + secondSVTextText + timingPointData;

			m_svs.add(firstSVToAdd);
			m_svs.add(secondSVToAdd);
		}
		System.out.println("SVs successfully generated...");
	}

	/**
	 * Write SV to file
	 */
	public void writeSV() {
		try {

			// Duplicate metadata and timings
			File inputFile = new File(NEWFILE);
			File tempFile = new File("temp");

			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

			String strLine = br.readLine();
			bw.write(strLine + System.getProperty("line.separator"));

			// Skip to timing points
			while (!strLine.equals("[TimingPoints]")) {
				strLine = br.readLine();
				bw.write(strLine + System.getProperty("line.separator"));
			}

			bw.write("-999," + m_msPerBeat.get(0) + ",4,2,1,0,1,0\n");
			for (String sv : m_svs) {
				bw.write(sv + System.getProperty("line.separator"));
			}

			while ((strLine = br.readLine()) != null) {
				bw.write(strLine + System.getProperty("line.separator"));
			}

			br.close();
			bw.close();

			inputFile.delete();
			if (!tempFile.renameTo(new File(NEWFILE))) {
				System.out.println("Could not rename file");
			}

			System.out.println("SVs successfully written!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			StopWatch sw1 = new StopWatch();
			sw1.reset();
			sw1.start();

			// Put your map here
			SVGenerate svg = new SVGenerate(
					"", 16);
			svg.generate();
			svg.writeSV();

			sw1.stop();
			System.out.println("SVs generated in " + sw1.getTime() + "s.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
