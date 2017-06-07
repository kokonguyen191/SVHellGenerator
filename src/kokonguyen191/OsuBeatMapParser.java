package kokonguyen191;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class OsuBeatmapParser {

	private static String FILENAME = null;
	private Set<Integer> m_beats = null;
	private ArrayList<Double> m_offsets = null;
	private ArrayList<Double> m_msPerBeat = null;
	private ArrayList<Boolean> m_inheritance = null;
	private ArrayList<Integer> m_sounds = null;
	private ArrayList<Integer> m_volume = null;
	private ArrayList<Integer> m_kiai = null;
	public String m_newFile = null;

	OsuBeatmapParser(String fileName) throws Exception {
		if (!fileName.substring(fileName.length() - 3, fileName.length()).equals("osu")) {
			throw new IllegalArgumentException("Not osu beatmap.");
		} else {

			// Initialization
			FILENAME = fileName;
			m_newFile = FILENAME.substring(0, FILENAME.length() - 5) + " SV Hell].osu";
			m_beats = new LinkedHashSet<Integer>();
			m_offsets = new ArrayList<Double>();
			m_msPerBeat = new ArrayList<Double>();
			m_inheritance = new ArrayList<Boolean>();
			m_sounds = new ArrayList<Integer>();
			m_volume = new ArrayList<Integer>();
			m_kiai = new ArrayList<Integer>();

			// Do the thing
			if (maniaCheck()) {
				parseBeats();
			} else {
				throw new IllegalArgumentException("Not mania map or already is SV Hell.");
			}
		}
	}

	public ArrayList<Integer> getBeats() {
		ArrayList<Integer> al = new ArrayList<Integer>(m_beats);
		al.sort(null);
		return al;
	}

	public ArrayList<Double> getOffsets() {
		return m_offsets;
	}

	public ArrayList<Double> getMsPerBeat() {
		return m_msPerBeat;
	}

	public ArrayList<Boolean> getInheritance() {
		return m_inheritance;
	}

	public ArrayList<Integer> getSounds() {
		return m_sounds;
	}

	public ArrayList<Integer> getVolume() {
		return m_volume;
	}

	public ArrayList<Integer> getKiai() {
		return m_kiai;
	}

	private boolean maniaCheck() throws Exception {

		File inputFile = new File(FILENAME);

		BufferedReader br = new BufferedReader(new FileReader(inputFile));

		String strLine;

		while ((strLine = br.readLine()) != null) {
			if (strLine.startsWith("Mode")) {
				// If not mania map
				if (!strLine.endsWith("3")) {
					br.close();
					return false;
				}
				break;
			}
		}
		
		while ((strLine = br.readLine()) != null) {
			if (strLine.startsWith("Version")) {
				// If not mania map
				if (strLine.endsWith("SV Hell")) {
					br.close();
					return false;
				} else {
					br.close();
					System.out.println("Beatmap: " + inputFile.getAbsolutePath());
					return true;
				}
			}
		}
		br.close();
		return true;
	}

	private void parseBeats() {
		try {

			// Clear beats
			m_beats.clear();

			// Duplicate metadata and timings
			File inputFile = new File(FILENAME);
			File tempFile = new File(m_newFile);

			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

			String strLine = br.readLine();
			bw.write(strLine + System.getProperty("line.separator"));

			// Before timing points
			while (!strLine.equals("[TimingPoints]")) {
				strLine = br.readLine();

				if (strLine.startsWith("Version")) {
					bw.write(strLine + " SV Hell" + System.getProperty("line.separator"));
				} else {
					bw.write(strLine + System.getProperty("line.separator"));
				}
			}

			// Before hit objects
			String lastOffset = "";
			String lastMspb = "";
			while (!strLine.equals("[HitObjects]")) {
				strLine = br.readLine();
				String[] parts = strLine.split(",");
				// If it is not inheritted
				if (parts.length == 8 && parts[6].equals("1")) {
					m_offsets.add(Double.parseDouble(parts[0]));
					m_msPerBeat.add(Double.parseDouble(parts[1]));
					m_inheritance.add(true);
					m_sounds.add(Integer.parseInt(parts[4]));
					m_volume.add(Integer.parseInt(parts[5]));
					m_kiai.add(Integer.parseInt(parts[7]));
					lastOffset = parts[0];
					lastMspb = parts[1];
					bw.write(strLine + System.getProperty("line.separator"));
				} else if (parts.length == 8 && parts[6].equals("0")) {
					if (!lastOffset.equals(parts[0])) {
						m_offsets.add(Double.parseDouble(parts[0]));
						m_msPerBeat.add(Double.parseDouble(lastMspb));
						m_inheritance.add(false);
						m_sounds.add(Integer.parseInt(parts[4]));
						m_volume.add(Integer.parseInt(parts[5]));
						m_kiai.add(Integer.parseInt(parts[7]));
						lastOffset = parts[0];
					}
				}
			}

			bw.write("[HitObjects]\n");

			int lastBeat = -1;
			// Iterate to the end
			while ((strLine = br.readLine()) != null) {
				// Timing points block
				// Split timing point
				String[] parts = strLine.split(",");
				// Parse beat
				if (parts.length == 6) {
					// Get this beat
					int thisBeat = Integer.parseInt(parts[2]);
					// Add beat
					m_beats.add(thisBeat);
					// If is LN
					if (!parts[5].startsWith("0")) {
						thisBeat = Integer.parseInt(parts[5].split(":")[0]);
						m_beats.add(thisBeat);
					}
					// Then update last beat if last beat is before this beat
					if (lastBeat < thisBeat) {
						lastBeat = thisBeat;
					}
				}
				bw.write(strLine + System.getProperty("line.separator"));
			}

			if ((double) lastBeat > Double.parseDouble(lastOffset)) {
				m_offsets.add((double) lastBeat + 1);
				m_msPerBeat.add(Double.parseDouble(lastMspb));
				m_inheritance.add(true);
				m_sounds.add(0);
				m_volume.add(0);
				m_kiai.add(0);
			}

			br.close();
			bw.close();

			System.out.println("Parsed beats and removed SVs successfully...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			OsuBeatmapParser pb = new OsuBeatmapParser("");
			ArrayList<Double> pbm = pb.getMsPerBeat();
			ArrayList<Double> pbo = pb.getOffsets();
			ArrayList<Boolean> pbi = pb.getInheritance();
			ArrayList<Integer> pbs = pb.getSounds();
			ArrayList<Integer> pbv = pb.getVolume();
			ArrayList<Integer> pbk = pb.getKiai();

			for (int i = 0; i < pbm.size(); i++) {
				System.out.println(pbo.get(i) + "       \t" + pbm.get(i) + "    \t" + pbi.get(i) + "\t" + pbs.get(i)
						+ " " + pbv.get(i) + " " + pbk.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}